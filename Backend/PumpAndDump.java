import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Arrays;
import java.lang.Math;


class PumpDump implements ICheck{

	LinkedList<Integer> prices;			//TODO decide max length for this
	LinkedList<Integer> pmas;
	long period;			//determines current period for grouping, currently minutes
	int k = 10;			//number of stocks to pass to anomaly
	boolean flag; 	//has this time period already been flagged
	int limit; //= 5;		//used to gauge the difference between periods for flagging		TODO make this dynamic to pma, but not overly sensitive at small numbers
	int pma;
	boolean startFlag;
	long periodLength = 3600000;
	double a = 0.18;			//alpha equal to 2/(1+N) where N is the number of periods in this case 10	TODO choose dynamicaly based on k
	int diff;					//used in the detection of time period gaps
	int channel = 0 ; // default value

	public pumpDump(Stock stock, int channel) throws ParseException{
		this.channel = channel;
		long time = stock.getTime();
		period = time - (time % periodLength) + periodLength;

		prices = new LinkedList<Integer>();
		prices.add(1);

		flag = true;
		startFlag = true;

	}


	public void update(Stock stock


						try {
										/*if time within period add to current price list*/
										if (stock.getTime() < period) {
											prices.set((prices.size() - 1), (prices.getLast() + stock.getPrice()) );
											// System.out.println(prices.getLast());
										} else {

											// section for coping with empty time periods	(works in theory has yet to be tested with gaps)
											diff = (int) ((stock.getTime() - period) / periodLength);
											if (diff >= 1) {
												for (int i = 0; i < diff ; i++) {
													calculatePma();
													prices.add(0);
													period += periodLength;
													if (prices.size() > k) {
														prices.removeFirst();
														pmas.removeFirst();
													}
												}
											}

							       calculatePma();

										 prices.add(1);
										 period += periodLength;
										 flag = false;
										 if (prices.size() > k) {
												prices.removeFirst();
												pmas.removeFirst();
											}
										}

				} catch (Exception e) {
							return;
						}

	}

	public Anomaly check(Stock stock, Client client) {
			if(pmas.size() < 50) {
					return null;
			} else { // there is a sufficient amount of averages to analyse for a pump and dump

				  boolean	dumpingState = false ;
					// check for dumpingState
					// look at latest and previous price averages
					//look at percentage increase/decrease, if there is a large enough decrease then dumping state is true

					double difference = pmas.getLast() -  pmas.get(pmas.size()-2) ;
					double percentage_decrease = (difference/PriceAverage.Get(PreviousPrice)) *100
					if (percentage_decrease < 0 ){ // it is negative
							if(Math.abs(percentage_decrease) >= 30){// if there is a 30% or more percentage decrease flag a dumping state
								dumpingState = true;
							}
					}

					//check for Pumping
					/* t = time of drop
						a = predefined gradient of threshold lines
						d = +- from drop point
						*/
					int t = pma.size()-1 ;
					int a = 1 ; //gradient of line ---- Expiremental value
					int d = (pmas.getLast()/100)*30 ;//this is the height of the threshold lines -- Expiremental value
					int[] Yarray = new int[50]
				  if (dumpingState) {

										for (int i = t ; i >= t - 50 ; i--) {

											if ( (pmas.get(i)) <= pmas.getLast() + d - a * (t-i) ) {
													// within threshold
											} else {
													// not within threshold
													return null;
											}
										}
										// if code reaches here, then pumping state is true
										//return PumpAndDump Anomaly object

										// build array so it contains last 50 recent prices
										long tStart = period - (pmas.size() * periodLength);
										int counter =  0 ;
										for (int x = 0; x< pmas.size() ; ) {
											if (x>=50) {
													Yarray[counter] = pmas.get(x) ;
													counter++;
											}

										}
										// send dat shiz off
										PDAnomaly anomaly = new PDAnomaly(client.getCounter(), channel, stock.getSymbol(), pmasArray, tStart, this.periodLength);

										return anomaly ;
						}
			}
	}

	private void calculatepma() {

		if (startFlag) {
			pma = prices.getLast();
			startFlag = false;
			pmas = new LinkedList<Integer>();
			pmas.add(pma);
		}

		else {
			pma = (int) ((a * prices.getLast()) + ((1 - a) * pma));			//Exponential Moving Average where a is alpha
			limit = (int) (0.7 * pma);
			pmas.add(pma);
		}

	}



}
