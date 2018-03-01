package es.daniel.bmjava.common;

import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.LogData;
import es.daniel.bmjava.common.iface.DataReaderCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class DataReader implements Runnable {
    DataReaderCallback cb;
    Config cfg;
    int files;
    int samples;

    public DataReader(DataReaderCallback cb, Config cfg) {
        this.cb = cb;
        this.cfg=cfg;
        this.files=0;
    }

    public void run() {
        File dir = new File("./data/sets/"+cfg.getDataset());
        File[] files = dir.listFiles();
        Arrays.sort(files);
        for(File file:files){
            System.out.println(file.getName());
            try{
                processFile(file);
            }catch (Exception e){
                throw  new RuntimeException(e);
            }
        }
    }

    private void processFile(File f) throws Exception{
        files++;
        FileInputStream fis =new FileInputStream(f);
        InputStreamReader isr =new InputStreamReader(fis,"UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String last;
        LogData lastLog=null;
        while ((last=br.readLine())!=null) {
            if (last.startsWith("#")) continue;
            LogData log = new LogData(last);

            if(lastLog==null){
                lastLog=log;
            }else{
                if(lastLog.getMicros()>log.getMicros() && log.getMicros()!=-1){
                    System.err.println("Dataset not clear (micros), parse in java spliter first");
                }
                if(lastLog.getDate().getTime()>log.getDate().getTime() && log.getMicros()!=-1){
                    System.err.println("Dataset not clear (date), parse in java spliter first");
                }
            }

            if(cb!=null) {
                cb.pushLogDataRead(log);
            }
        }

    }

    public void finish(){
        System.out.println("Files: "+this.files);
    }
}
