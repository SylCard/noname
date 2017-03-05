import java.lang.Math;

public class PFatFinger implements ICheck {
    int pma;
    int lastPma;
    int a = 0.18;   //effect of past average on vma
    int n = 2;      //sensitivity of error detection
    int channel;    //historical/live data - whichever the check is for

    public FatFinger(int channel) {
        pma = 0;
        lastPma = 0;
        this.channel = channel;
    }

    public void update(Stock stock) {
        try {
            lastPma = pma;  //save for check on data before this trade was considered
            pma = ( a*stock.getPrice() ) + ( (1-b)*pma );    //calculate new average
        } catch( Exception e) {
            return;
        }
    }

    public Anomaly check(Stock stock, Client client) {
        if( (stock.getPrice() > lastPma*(pow(10, n))) ) {
            //there has been a price ff error
            //calculate severity
            severity = (stockPrice() * 100) / lastPma;
            //send anomaly
            FFAnomaly anomaly = new FFAnomaly(client.getCounter(), channel, stock, severity, lastPma, 1);
            return anomaly;
        } else {
            return null;
        }
    }
}