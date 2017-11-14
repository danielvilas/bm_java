package es.daniel.bmjava.common.iface;

import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.ParsedPacket;

public interface Client {
    int init(Config cfg);
    int send(ParsedPacket data);
    int close();
}
