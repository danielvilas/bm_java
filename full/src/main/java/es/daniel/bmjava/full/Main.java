package es.daniel.bmjava.full;

import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.iface.Client;
import es.daniel.bmjava.kafka.KafkaClientImpl;
import es.daniel.bmjava.mqtt.MqttClientImpl;
import es.daniel.bmjava.rest.RestClientImpl;
import es.daniel.bmjava.soap.SoapClientImpl;

public class Main {
    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        cmd.setFull(true);
        Config cfg = cmd.parseConfig();
        Client client = null;
        switch (cfg.getProto()){
            case WS: client = new SoapClientImpl();break;
            case KAFKA: client=new KafkaClientImpl();break;
            case REST: client=new RestClientImpl();break;
            case MQTT: client=new MqttClientImpl();break;
        }

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
