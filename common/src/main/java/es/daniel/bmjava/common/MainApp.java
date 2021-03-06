package es.daniel.bmjava.common;

import com.googlecode.fannj.Fann;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.iface.Client;
import es.daniel.bmjava.common.iface.Processor;

import java.util.Date;

public class MainApp implements Runnable {
    Client client;
    Config cfg;
    Processor processor;
    public MainApp(Client client, Config cfg, Processor processor) {
        this.client = client;
        this.cfg = cfg;
        this.processor=processor;
    }

    public void run() {
        long start = System.currentTimeMillis();
        client.init(cfg);

        processor.setAnnNetwork(new Fann("./data/net_16000.net"));
        processor.setClient(client);

        PacketReader packetReader = new PacketReader(processor);
        DataReader dr = new DataReader(packetReader,cfg);
        dr.run();

        System.out.println("I'm done!!");
        dr.finish();
        packetReader.finish();
        client.close();
        long end = System.currentTimeMillis();
        System.out.println("Total time: "+(end-start)+" ms");
    }
}
