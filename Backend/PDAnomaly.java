class PDAnomaly extends Anomaly {
	int anomalyID;
	int channel;
	String symbol;
	String type;
	double[] pmas;
	long tStart;
	long periodLength;


	public PDAnomaly(int anomalyID, int channel, String symbol, double[] pmas, long tStart, long periodLength) {
		this.anomalyID = anomalyID;
		this.channel = channel;
		this.symbol = symbol;
		this.pmas = pmas;
		this.tStart = tStart;
		this.periodLength = periodLength;
		this.type = "PumpAndDump";
	}
}