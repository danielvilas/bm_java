package es.daniel.bmjava.common.data;

public class Config {
    String server;
    boolean continous;
    String dataset;
    Protocol proto;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isContinous() {
        return continous;
    }

    public void setContinous(boolean continous) {
        this.continous = continous;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Protocol getProto() {
        return proto;
    }

    public void setProto(Protocol proto) {
        this.proto = proto;
    }


    @Override
    public String toString() {
        if(proto!=null)
        return "Config{" +
                "server='" + server + '\'' +
                ", continous=" + continous +
                ", dataset='" + dataset + '\'' +
                ", proto=" + proto +
                '}';

        return "Config{" +
                "server='" + server + '\'' +
                ", continous=" + continous +
                ", dataset='" + dataset + '\'' +
                '}';
    }
}
