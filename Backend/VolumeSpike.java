import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;
import java.util.LinkedList;


//TODO FATAL after a fixed period historical data can no longer be inserted into the table and errors will begin to occur, does not work correctly after 2 hours(exactly 3:30am system fails)
//TODO allow system to cope with gaps in time, eg if the periods are 1 min and nothing happens for 1 min errors will occur
//TODO prevent large spikes from continuing to trigger after they've passed. eg 1,2,40,5,6 will trigger on 40 5 and 6 where it should only trigger on 40
class VolumeSpike implements ICheck{
	LinkedList<Integer> volumes;			//TODO decide max length for this
	long period;			//determines current period for grouping, currently minutes
	int k = 10;			//number of stocks to pass to anomaly
	boolean flag; 	//has this time period already been flagged
	int limit; //= 5;		//used to gauge the difference between periods for flagging		TODO make this dynamic to vma, but not overly sensitive at small numbers
	int vma;
	boolean startFlag;

	//for testing purposes
	public static void main(String[] args) throws ParseException{
		Stock testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		VolumeSpike test = new VolumeSpike(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		testStock = new Stock("2017-02-25 13:12:43.460105","m.hen@aspectcapital.com","p.greyling@fenchurchst.com",2019.19,38233,"GBX","DGE.L","Consumer Goods",2017.61,2023.39);
		test.update(testStock);
		test.check(testStock);
		System.out.println(test.volumes.getFirst());
	}

	public VolumeSpike(Stock stock) throws ParseException{
		long time = getTime(stock);
		period = time - (time % 3600000) + 3600000;

		volumes = new LinkedList<Integer>();
		volumes.add(1);

		flag = true;
		startFlag = true;
	}


	public void update(Stock stock){
		try {
			/*if time within period add to current volume list*/
			if (getTime(stock) < period) {
				volumes.set((volumes.size() - 1), (volumes.getLast().intValue() + 1) );
				// System.out.println(volumes.getLast());
			} else {

				if (startFlag) {
					vma = volumes.getLast();
					startFlag = false;
				} else {
					calculateVma();
				}

				//visualisation TODO remove this
				System.out.println("vma = " + vma + ", current volume = " + volumes.getLast() + ", current limit = " + limit);
				String string = stock.getTime();
				DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);
				System.out.println(format.parse(string));
				System.out.println("\n");
				//

				volumes.add(1);
				period += 3600000;
				flag = false;
				if (volumes.size() > k) {
					volumes.removeFirst();
				}
			}

		} catch (Exception e) {
			return;
		}
	}

	private void calculateVma() {
		vma = ((vma * (k-1)) + volumes.getLast()) / k;
		limit = (int) (0.7 * vma);
	}

	public Anomaly check(Stock stock) {
		if (flag) {
			return null;
		} else if ((volumes.get(volumes.size() - 2) - vma) > limit) {
			/*if the current periods volume is greater than the vma + the limit: flag*/
			System.out.println("vma = " + vma + ", current volume = " + volumes.get(volumes.size() - 2));
			System.out.println("Spike of at least " + ((volumes.get(volumes.size() - 2) * 100) / vma) + "% detected!");

			flag = true;
			return null;
		} else {
			flag = true;
			return null;
		}
	}

	private static long getTime(Stock stock) throws ParseException{
		String string = stock.getTime();
		DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);
		Date date = format.parse(string);
		return date.getTime();
	}
}
