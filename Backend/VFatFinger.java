import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

public class FatFinger implements ICheck {
    int vma;
    int lastVma;
    int a = 0.18;   //effect of past average on vma
    int n = 2;      //sensitivity of error detection
    int channel;    //historical/live data - whichever the check is for

    public FatFinger(int channel) {
        vma = 0;
        lastVma = 0;
        this.channel = channel;
    }

    public void update(Stock stock) {
        vma = ( b*stock.getSize() ) + ( (1-b)*vma );
    }

    public void check(Stock stock) {
        if( (stock.getSize() > vma*(10^n)) || ( (stock.getSize() < vma+(10^-n)) ) ) {
            //then there has been a price ff error
            //calculate severity
            severity = (stock.getSize() * 100) / vma;
            //send anomaly
            FFAnomaly anomaly = new FFAnomaly(client.getCounter(), channel, stock, severity, 0);
        }
    }
}