package es.daniel.bmjava.rest;

import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.ParsedPacket;
import es.daniel.bmjava.common.iface.Client;
import org.springframework.web.client.RestTemplate;

public class RestClientImpl implements Client {
    String address;
    public RestClientImpl(){

    }

    public int init(Config cfg) {

        String addr;
        int p =cfg.getServer().indexOf("://");
        if(p==-1){
            addr="http://"+cfg.getServer();
            p =addr.indexOf("://");
        }else{
            addr=cfg.getServer();
        }

        p=addr.indexOf(":",p+1);
        if(p==-1){
            addr+=":9090";
        }
        addr+="/api/addPacket";
        System.out.println("Connecting to: "+addr);
        this.address=addr;
        return 0;
    }

    public int send(ParsedPacket p) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(address,fromParsedPacket(p));
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private Packet fromParsedPacket(ParsedPacket p){
        Packet packet = new Packet();
        packet.setAppleTvSeconds((float)p.getAppleTv());
        packet.setBluraySeconds((float)p.getBluray());
        packet.setDate(p.getDate());
        packet.setIpTvSeconds((float)p.getIpTv());
        packet.setTvSeconds((float)p.getTv());
        return packet;
    }

    public int close() {
        return 0;
    }

    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        Config cfg = cmd.parseConfig();
        Client client = new RestClientImpl();

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
