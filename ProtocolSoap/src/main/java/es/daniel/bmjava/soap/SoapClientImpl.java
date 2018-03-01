package es.daniel.bmjava.soap;

import es.daniel.bmjava.basic.BasicProcess;
import es.daniel.bmjava.common.CmdParser;
import es.daniel.bmjava.common.MainApp;
import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.ParsedPacket;
import es.daniel.bmjava.common.iface.Client;
import es.daniel.outputgui.data.AddPacketRequest;
import es.daniel.outputgui.data.AddPacketResponse;
import es.daniel.outputgui.data.Packet;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SoapClientImpl extends WebServiceGatewaySupport implements Client {

    String address;
    int sentMessages=0;

    public SoapClientImpl(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("es.daniel.outputgui.data");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
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
        addr+="/ws";
        System.out.println("Connecting to: "+addr);
        this.address=addr;
        return 0;
    }

    public int send(ParsedPacket data) {
        Packet packet = fromParsedPacket(data);
        AddPacketRequest req = new AddPacketRequest();
        req.setPacket(packet);
        sentMessages++;
        AddPacketResponse res = (AddPacketResponse) getWebServiceTemplate().marshalSendAndReceive(address,                req, new SoapActionCallback(""));
        //client.addBucket(req);
        return 0;
    }

    private Packet fromParsedPacket(ParsedPacket p){
        Packet packet = new Packet();
        packet.setAppleTvSeconds((float)p.getAppleTv());
        packet.setBluraySeconds((float)p.getBluray());
        packet.setDate(getXmlGregorianCalendarFromDate(p.getDate()));
        packet.setIpTvSeconds((float)p.getIpTv());
        packet.setTvSeconds((float)p.getTv());
        return packet;
    }

    private XMLGregorianCalendar getXmlGregorianCalendarFromDate(final Date date)   {
        try {
            GregorianCalendar calendar = new GregorianCalendar();

            calendar.setTime(date);

            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        }catch (DatatypeConfigurationException e){
            throw new RuntimeException(e);
        }
    }

    public int close() {
        System.out.println("Messages: "+this.sentMessages);
        return 0;
    }

    public static void main(String args[]){
        CmdParser cmd = new CmdParser(args);
        Config cfg = cmd.parseConfig();
        Client client = new SoapClientImpl();

        MainApp app = new MainApp(client, cfg, new BasicProcess());
        app.run();
    }
}
