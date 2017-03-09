import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Arrays;

class ExtractionTester {
	static int channel;

	public static void main(String[] args) throws Exception {
		LinkedList<Stock> queue;
		Hashtable<String,ICheck[]> table = new Hashtable<String,ICheck[]>();
		String symbol;

		// Connection conn = establishDatabase();		//get database connection
		Client sender = new Client("46.101.34.184", 6969);

		if (args.length >= 3) {			//if given a third argument sets channel to argument, otherwise channel = 0
			channel = Integer.parseInt(args[2]);
		}

		queue = DataExtractor.extract(args);		//start the extraction system
		Stock stock;
		while (true) {					//TODO find a way to exit loop safley, especialy in the case of historical data
			synchronized (queue) {
				while (queue.size() > 0) {			//when the queue contains transactions pass them to analysis
					stock = queue.removeFirst();	

					symbol = stock.getSymbol();
					if (table.containsKey(symbol)) {				//if symbol already has check object use it

						//for all checks first update then check data
						for ( ICheck check: table.get(symbol)) {
							check.update(stock);
						}
						for ( ICheck check : table.get(symbol)) {
							objectParser(check.check(stock, sender), sender);
						}
					
					} else {										//if symbol has no check object create one
						table.put(symbol, new ICheck[] { new VolumeSpike(stock, channel), new VFatFinger(channel), new PFatFinger(channel), new PumpAndDump(stock, channel)});
						table.get(symbol)[1].update(stock);
						table.get(symbol)[2].update(stock);							
					}
					//TODO make system work for sectors as well as symbols

				}
			}
		}
	}

	//parses anomaly objects to json objects and passes them to the frint end
	//TODO remove print statements, currently for display purposes only
	private static void objectParser(Anomaly anomaly, Client sender) {
		if (anomaly instanceof VSAnomaly) {
			String out = vsParser((VSAnomaly)anomaly);
			// System.out.println(out);
			sender.sendMessage(out);
		} else if (anomaly instanceof FFAnomaly) {
			String out = ffParser((FFAnomaly)anomaly);
			// System.out.println(out);
			sender.sendMessage(out);
		} else if (anomaly instanceof PDAnomaly) {
			String out = pdParser((PDAnomaly)anomaly);
			// System.out.println(out);
			sender.sendMessage(out);
		}
	}

	private static String vsParser(VSAnomaly anomaly) {
		String jsonString = "{";
		jsonString += "\"AnomalyID\":" + anomaly.anomalyID + ",";
		jsonString += "\"mode\":" + anomaly.channel + ",";
		jsonString += "\"type\":\"" + anomaly.type + "\",";
		jsonString += "\"symbol\":\"" + anomaly.symbol + "\",";
		jsonString += "\"yaxis1\":" + Arrays.toString(anomaly.vmas) + ",";
		jsonString += "\"yaxis2\":" + Arrays.toString(anomaly.volumes) + ",";
		jsonString += "\"timeBegin\":" + anomaly.tStart + ",";
		jsonString += "\"periodLen\":" + anomaly.periodLength + ",";
		jsonString += "\"severity\":" + anomaly.severity;
		jsonString += "}";
		return jsonString;
	}

	private static String ffParser(FFAnomaly anomaly) {
		String jsonString = "{";
		jsonString += "\"AnomalyID\":" + anomaly.anomalyID + ",";
		jsonString += "\"mode\":" + anomaly.channel + ",";
		jsonString += "\"type\":\"" + anomaly.type + "\",";
		jsonString += "\"stock\":{";
		jsonString += "\"time\":" + anomaly.stock.getTime() + ",";
		jsonString += "\"buyer\":\"" + anomaly.stock.getBuyer() + "\",";
		jsonString += "\"seller\":\"" + anomaly.stock.getSeller() + "\",";
		jsonString += "\"price\":" + anomaly.stock.getPrice() + ",";
		jsonString += "\"size\":" + anomaly.stock.getSize() + ",";
		jsonString += "\"currency\":\"" + anomaly.stock.getCurrency() + "\",";
		jsonString += "\"symbol\":\"" + anomaly.stock.getSymbol() + "\",";
		jsonString += "\"sector\":\"" + anomaly.stock.getSector() + "\",";
		jsonString += "\"bid\":" + anomaly.stock.getBid() + ",";
		jsonString += "\"ask\":" + anomaly.stock.getAsk();
		jsonString += "},";
		jsonString += "\"error\":" + anomaly.error + ",";
		jsonString += "\"rma\":" + anomaly.rma + ",";
		jsonString += "\"severity\":" + anomaly.severity;
		jsonString += "}";
		return jsonString;
	}

	private static String pdParser(PDAnomaly anomaly) {
		String jsonString = "{";
		jsonString += "\"AnomalyID\":" + anomaly.anomalyID + ",";
		jsonString += "\"mode\":" + anomaly.channel + ",";
		jsonString += "\"type\":\"" + anomaly.type + "\",";
		jsonString += "\"symbol\":\"" + anomaly.symbol + "\",";
		jsonString += "\"pmas\":" + Arrays.toString(anomaly.pmas) + ",";
		jsonString += "\"tStart\":" + anomaly.tStart + ",";
		jsonString += "\"periodLength\":" + anomaly.periodLength;
		jsonString += "}";
		return jsonString;
	}


}