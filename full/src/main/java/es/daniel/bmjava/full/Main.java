package es.daniel.bmjava.full;

import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.iface.Client;
import es.daniel.bmjava.mqtt.MqttClientImpl;

public class Main {
    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        cmd.setFull(true);
        Config cfg = cmd.parseConfig();
        Client client = null;
        switch (cfg.getProto()){
            case WS: System.out.println("WS");break;
            case KAFKA: System.out.println("KAFKA");break;
            case REST: System.out.println("REST");break;
            case MQTT: client=new MqttClientImpl();break;
        }

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
