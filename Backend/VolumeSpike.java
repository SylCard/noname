import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Arrays;


class VolumeSpike implements ICheck{
	LinkedList<Integer> volumes;
	LinkedList<Integer> vmas;
	long period;			//determines current period for grouping, currently minutes
	int k = 10;			//number of stocks to pass to anomaly
	boolean flag; 	//has this time period already been flagged
	int limit;		//used to gauge the difference between periods for flagging
	int vma;
	boolean startFlag;
	long periodLength = 900000;
	double a = 0.18;			//alpha equal to 2/(1+N) where N is the number of periods in this case 10	TODO choose dynamicaly based on k
	int diff;					//used in the detection of time period gaps
	int channel;

	//builds volume spike object
	public VolumeSpike(Stock stock, int channel) throws ParseException{
		this.channel = channel;
		long time = stock.getTime();
		period = time - (time % periodLength) + periodLength;

		volumes = new LinkedList<Integer>();
		volumes.add(1);

		flag = true;
		startFlag = true;
	}


	public void update(Stock stock){
		try {
			/*if time within period add to current volume list*/
			if (stock.getTime() < period) {
				volumes.set((volumes.size() - 1), (volumes.getLast().intValue() + stock.getSize()) );
			} else {

				// section for coping with empty time periods
				diff = (int) ((stock.getTime() - period) / periodLength);
				if (diff >= 1) {
					for (int i = 0; i < diff ; i++) {		//for the number of periods for which the object is not updated add a 0 to the queue
						calculateVma();
						volumes.add(0);
						period += periodLength;
						if (volumes.size() > k) {		//limits list size to k
							volumes.removeFirst();
							vmas.removeFirst();
						}
					}
				}

				calculateVma();

				volumes.add(1);
				period += periodLength;
				flag = false;
				if (volumes.size() > k) {				//limits list size to k
					volumes.removeFirst();
					vmas.removeFirst();
				}
			}

		} catch (Exception e) {
			return;
		}
	}


	private void calculateVma() {

		if (startFlag) {			//if first run set vma to volume of trade
			vma = volumes.getLast();
			startFlag = false;
			vmas = new LinkedList<Integer>();
			vmas.add(vma);
		} else {					//else calculate ema
			vma = (int) Math.ceil((a * volumes.getLast()) + ((1 - a) * vma));			//Exponential Moving Average where a is alpha 
			limit = (int) Math.ceil(0.7 * vma);
			vmas.add(vma);
		}

	}

	public Anomaly check(Stock stock, Client client) {
		if (flag) {
			return null;
		} else if ((volumes.get(volumes.size() - 2) - vma) > limit) {		//if volume is abnormaly large build and send an anomaly object
			int[] volumesArray = buildArray(volumes, 1);
			int[] vmasArray = buildArray(vmas, 0);
			long tStart = period - (vmasArray.length * periodLength);
			int severity = (volumes.get(volumes.size() - 2) * 100) / vma;			//this includes the 100% of expected. eg an increase of 70% will give a result of 170
			VSAnomaly anomaly = new VSAnomaly(client.getCounter(), channel, stock.getSymbol(), volumesArray, vmasArray, tStart, periodLength, severity);

			flag = true;
			return anomaly;
		} else {
			flag = true;
			return null;
		}
	}

	//builds an array for part of a linked list
	private static int[] buildArray(LinkedList<Integer> list, int offset) {
		int[] array = new int[list.size()-offset];
		for (int i  = 0; i < array.length ; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}
