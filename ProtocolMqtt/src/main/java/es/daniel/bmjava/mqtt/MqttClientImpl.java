package es.daniel.bmjava.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.ParsedPacket;
import es.daniel.bmjava.common.iface.Client;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClientImpl implements Client {
    MqttClient client;


    public MqttClientImpl(){}


    public int init(Config cfg) {
        String addr;
        int p =cfg.getServer().indexOf("://");
        if(p==-1){
            addr="tcp://"+cfg.getServer();
            p =addr.indexOf("://");
        }else{
            addr=cfg.getServer();
        }

        p=addr.indexOf(":",p+1);
        if(p==-1){
            addr+=":1883";
        }
        System.out.println("Connecting to: "+addr);
        try {
            client = new MqttClient(addr, MqttClient.generateClientId());
            client.connect();

        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int send(ParsedPacket data) {
        try {
            ObjectMapper om= new ObjectMapper();
            String str= om.writeValueAsString(data);
            MqttMessage message = new MqttMessage();
            message.setPayload(str.getBytes());
            client.publish("AppliancesBucket", message);
        }catch (Exception e){
            e.printStackTrace();
        }


        return 0;
    }

    public int close() {
        try {
            if(client!=null && client.isConnected()){
                client.disconnect();
                client.close();
            }
        }catch (Exception e){
            throw  new RuntimeException(e);
        }

        return 0;
    }


    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        Config cfg = cmd.parseConfig();
        Client client = new MqttClientImpl();

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
