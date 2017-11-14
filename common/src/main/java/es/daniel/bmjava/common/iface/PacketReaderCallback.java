package es.daniel.bmjava.common.iface;

import es.daniel.bmjava.common.data.LogPacket;

public interface PacketReaderCallback {
    public void pushLogPacket(LogPacket lp);
}
