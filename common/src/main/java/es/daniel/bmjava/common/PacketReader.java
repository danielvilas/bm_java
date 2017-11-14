package es.daniel.bmjava.common;

import es.daniel.bmjava.common.data.LogData;
import es.daniel.bmjava.common.data.LogPacket;
import es.daniel.bmjava.common.iface.DataReaderCallback;
import es.daniel.bmjava.common.iface.PacketReaderCallback;

public class PacketReader implements DataReaderCallback {

    private PacketReaderCallback cb;
    int i=-1;
    LogPacket lp;
    public PacketReader(PacketReaderCallback cb) {
        this.cb = cb;
    }

    public void pushLogDataRead(LogData data) {
        if(lp==null){
            lp = new LogPacket();
            i=0;
        }
        lp.set(data,i++);
        if(i==LogPacket.PACKET_SIZE){
            if(cb!=null)
                cb.pushLogPacket(lp);
            lp=null;
        }
    }
}
