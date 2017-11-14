package es.daniel.bmjava.common.iface;

import es.daniel.bmjava.common.data.LogData;

public interface DataReaderCallback {
    public void pushLogDataRead(LogData data);
}
