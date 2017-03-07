//class to test stocks for Volume related Fat Finger errors
import java.lang.Math;

public class VFatFinger implements ICheck {
    int vma;
    int lastVma;
    float a = 0.18;   //effect of past average on vma
    int n = 2;      //sensitivity of error detection
    int channel;    //historical/live data - whichever the check is for

    public VFatFinger(int channel) {
        vma = 0;
        lastVma = 0;
        this.channel = channel;
    }

    public void update(Stock stock) {
        try {
            lastVma = vma;  //save for check on data before this trade was considered
            vma = ( a*stock.getSize() ) + ( (1-a)*vma );    //calculate new average
        } catch( Exception e) {
            return;
        }
    }

    public Anomaly check(Stock stock, Client client) {
        if( (stock.getSize() > lastVma*(Math.pow(10, n))) || ( (stock.getSize() < lastVma+(pow(10, 0-n))) ) ) {
            //then there has been a price ff error
            //calculate severity
            int severity = (stock.getSize() * 100) / lastVma;
            //send anomaly
            FFAnomaly anomaly = new FFAnomaly(client.getCounter(), channel, stock, severity, lastVma, 0);
            return anomaly;
        } else {
            return null;
        }
    }
}