//class to test stocks for Volume related Fat Finger errors
import java.lang.Math;

public class VFatFinger implements ICheck {
    int vma;
    int lastVma;
    int severity;
    double a = 0.18;   //effect of past average on vma
    double n = 0.5;      //sensitivity of error detection
    int channel;    //historical/live data - whichever the check is for
    boolean flag;

    public VFatFinger(int channel) {
       	flag = true;
        lastVma = 0;
        this.channel = channel;
    }

    public void update(Stock stock) {
        try {
	    if (flag) {
		vma = stock.getSize();
		flag = false;
	    } else {
                lastVma = vma;  //save for check on data before this trade was considered
                vma = (int) Math.ceil(( a*stock.getSize() ) + ( (1-a)*vma ));    //calculate new average
	    }
        } catch( Exception e) {
            return;
        }
    }

    public Anomaly check(Stock stock, Client client) {
        if( ((stock.getSize() > lastVma*(Math.pow(10, n))) ) && (lastVma != 0) ) {
            //then there has been a price ff error
            //calculate severity
            severity = (stock.getSize() * 100) / lastVma;
            //send anomaly
            FFAnomaly anomaly = new FFAnomaly(client.getCounter(), channel, stock, severity, lastVma, "Volume");
            return anomaly;
        } else {
            return null;
        }
    }
}
