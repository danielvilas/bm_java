package es.daniel.bmjava.basic;

import com.googlecode.fannj.Fann;
import es.daniel.bmjava.common.data.LogData;
import es.daniel.bmjava.common.data.LogPacket;
import es.daniel.bmjava.common.data.ParsedPacket;
import es.daniel.bmjava.common.iface.Client;
import es.daniel.bmjava.common.iface.PacketReaderCallback;
import es.daniel.bmjava.common.iface.Processor;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Date;

public class BasicProcess implements Processor {
    Fann net;
    Client client;

    public BasicProcess(){

    }

    public void pushLogPacket(LogPacket lp) {
        Date tmp = lp.get(0).getDate();

        double[] in0=createData(lp, 0);//[0,300)
        double[] in3=createData(lp, 300);//[300,600)
        double[] in6=createData(lp, 600);//[600,900)
        double[] in9=createData(lp, 1023-300);//[724,1024)

        ParsedPacket p=createData(tmp,in0,in3,in6,in9);

        client.send(p);

    }
    private double[] createData(LogPacket lp, int offset) {
        double[] in = getFftData(lp,offset);
        //network is trained to return
        //TV, BluRay, AppleTV, IpTV
        double out[] = execute(in);
        return out;
    }

    private ParsedPacket createData(Date tmp, double[] in0, double[]in3, double[]in6,double[]in9){
        ParsedPacket ret = new ParsedPacket();
        double[] out = new double[in0.length];
        for(int i=0;i<out.length;i++){
            out[i]=(in0[i]+in3[i]+in6[i]+in9[i])/4.0;
        }

        ret.setDate(tmp);
        ret.setTv(out[0]);
        ret.setBluray(out[1]);
        ret.setAppleTv(out[2]);
        ret.setIpTv(out[3]);

        return ret;
    }

    private double[] getFftData(LogPacket lp, int offset) {
        double dData[] = new double[300];
        double average =0.0;
        for (int j = 0; j < 300; j++) {
            LogData lg = lp.get(j+offset);
            double tmp=(lg.getA0() - 512.0) /512.0;
            dData[j]=tmp;
            tmp=Math.abs(tmp);
            average+=tmp;
        }
        average/=dData.length;

        double[] fft = new double[dData.length * 2];
        System.arraycopy(dData, 0, fft, 0, dData.length);
        DoubleFFT_1D fftDo = new DoubleFFT_1D(dData.length);
        fftDo.realForwardFull(fft);
        //BasicMLData data = new BasicMLData(3);
        return new double[]{getMagnitude(fft,50),getMagnitude(fft,150),getMagnitude(fft,250),getMagnitude(fft,350),average};
    }

    public double getMagnitude(double[] fft, int i){
        /**
         * h = P*fs/N
         * P = h/ (FS/N) = h*N/FS
         */
        int pos = i*(fft.length/2)/1000;
        double re = fft[2*pos];
        double im = fft[2*pos+1];
        return  Math.sqrt(re*re+im*im);
    }


    public double[] execute(double[] data) {
        float[] in = new float[5];
        in[0]= (float) data[0];
        in[1]= (float) data[1];
        in[2]= (float) data[2];
        in[3]= (float) data[3];
        in[4]= (float) data[4];
        //in[5]= (float) data[5];
        double ret[]=new double[4];
        if(net!=null) {
            float res[] = net.run(in);
            ret[0] = res[0];
            ret[1] = res[1];
            ret[2] = res[2];
            ret[3] = res[3];
        }
        return ret;
    }



    public void setClient(Client client) {
        this.client = client;
    }

    public void setAnnNetwork(Fann network) {
        this.net=network;
    }
}

