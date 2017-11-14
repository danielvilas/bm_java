package es.daniel.bmjava.common.iface;

import com.googlecode.fannj.Fann;

public interface Processor extends PacketReaderCallback {
    public void setAnnNetwork(Fann network);
    public void setClient(Client client);
}
