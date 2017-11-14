package es.daniel.bmjava.common.data;


public class LogPacket {
    public static final int PACKET_SIZE=1024;
    private LogData[] data = new LogData[PACKET_SIZE];

    public LogData get(int i){
        return data[i];
    }
    public void set(LogData ld, int i){
        data[i]=ld;
    }

    public LogData[] get(){
        return data;
    }

}
