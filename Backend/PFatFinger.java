import java.lang.Math;

public class PFatFinger implements ICheck {
    double pma;
    double lastPma;
    int severity;
    double a = 0.18;   //effect of past average on vma
    double n = 1.0;      //sensitivity of error detection
    int channel;    //historical/live data - whichever the check is for
    boolean flag;

    public PFatFinger(int channel) {
        pma = 0;
        lastPma = 0;
        this.channel = channel;
	flag = true;
    }

    public void update(Stock stock) {
        try {
	    if (flag) {
		pma = stock.getPrice();
		flag = false;
	    } else {
                lastPma = pma;  //save for check on data before this trade was considered
                pma = (int) Math.ceil(( a*stock.getPrice() ) + ( (1-a)*pma ));    //calculate new average
	    }
        } catch( Exception e) {
            return;
        }
    }

    public Anomaly check(Stock stock, Client client) {
        if( ((stock.getPrice() > lastPma*(Math.pow(10, n))) || (stock.getPrice() < lastPma*(Math.pow(10, 0-n))))&& (lastPma != 0)) {
            //there has been a price ff error
            //calculate severity
            severity = (int) ((stock.getPrice() * 100) / lastPma);
            //send anomaly
            FFAnomaly anomaly = new FFAnomaly(client.getCounter(), channel, stock, severity, lastPma, "Price");
            return anomaly;
        } else {
            return null;
        }
    }
}
