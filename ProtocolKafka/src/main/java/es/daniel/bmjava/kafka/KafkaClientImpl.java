package es.daniel.bmjava.kafka;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.ParsedPacket;
import es.daniel.bmjava.common.iface.Client;
import org.apache.kafka.clients.producer.*;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaClientImpl implements Client {

    private Producer<String, String> producer;
    private ZooKeeper zk;
    private int sentMessages=0;
    private int pendingMessages=0;

    public KafkaClientImpl(){

    }
    public int init(Config cfg) {
        String addr = cfg.getServer();
        int p=addr.indexOf(":");
        if(p==-1){
            addr+=":2181";
        }
        System.out.println("Connecting to: "+addr);
        try {
            String kafka = buildConnectionString(addr);
            producer = createProducer(kafka);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return 0;
    }

    private String buildConnectionString(String server) throws  Exception{
        zk = new ZooKeeper(server, 10000, null);
        List<String> brokerList = new ArrayList<String>();

        List<String> ids = zk.getChildren("/brokers/ids", false);
        for (String id : ids) {
            String brokerInfoString = new String(zk.getData("/brokers/ids/" + id, false, null));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsBroker= objectMapper.readTree(brokerInfoString);
            System.out.println(jsBroker);
            ArrayNode array = (ArrayNode) jsBroker.get("endpoints");
            for(JsonNode n:array){
                brokerList.add(n.textValue());
            }
        }
        return toList(",", brokerList);

    }

    private Producer<String,String> createProducer(String connectionString){
        Properties props = new Properties();
        //props.put("metadata.broker.list", String.join(",", brokerList));
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,connectionString);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<String, String>(props);
    }

    public int send(ParsedPacket p) {
        try {

            ObjectMapper om= new ObjectMapper();
            String data= om.writeValueAsString(p);

            ProducerRecord<String, String> record = new ProducerRecord<String, String>("AppliancesBucket", data);
            synchronized (producer) {
                pendingMessages++;
                sentMessages++;
            }
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //System.out.println("Sent, " + recordMetadata);
                    synchronized (producer) {
                        pendingMessages--;
                        producer.notify();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public String toList(String delim, List<String> all){
        String ret=null;
        for(String s:all){
            if(ret==null){
                ret=s;
            }else{
                ret+=delim+s;
            }
        }
        return ret;
    }


    public int close() {
        boolean wait;
        synchronized (producer) {
            wait = pendingMessages > 0;
            while (wait) {
                System.out.println("Pending Messages: " + this.pendingMessages);
                try {
                    producer.wait(100);
                } catch (InterruptedException e) {
                    wait = false;
                }
                if (wait) {
                    wait = pendingMessages > 0;

                }
            }
        }
        System.out.println("Messages: "+this.sentMessages);
        try {
            zk.close();
            producer.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return 0;
    }

    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        Config cfg = cmd.parseConfig();
        Client client = new KafkaClientImpl();

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
