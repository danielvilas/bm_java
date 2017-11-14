package es.daniel.bmjava.common.data;

import sun.rmi.runtime.Log;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by daniel on 11/6/17.
 */
public class LogData {
    private long micros;
    private int A0;
    private int A1;
    private Date date=null;
    private long deltaMicros=-1;


    public LogData(String data){
        StringTokenizer st = new StringTokenizer(data,",");
        String stMicros = st.nextToken().trim();
        String stA0 =st.nextToken().trim();
        String stA1=st.nextToken().trim();
        String stDate=st.nextToken().trim();
        String stDelta=st.nextToken().trim();

        if(stMicros.startsWith("-")){
            long tmp = Long.parseLong(stMicros,16);
            long tmp2 = 0x00FFFFFFFFL & tmp;
            this.micros=(tmp==-1)?tmp:tmp2;
        }else{
            this.micros=Long.parseLong(stMicros,16);
        }

        this.A0=Integer.parseInt(stA0);
        this.A1=Integer.parseInt(stA1);
        this.date=new Date();
        this.date.setTime(Long.parseLong(stDate));
        this.deltaMicros=Long.parseLong(stDelta);

    }



    public long getMicros() {
        return micros;
    }

    public int getA0() {
        return A0;
    }

    public int getA1() {
        return A1;
    }

    public Date getDate() {
        return date;
    }

}
