class PDAnomaly extends Anomaly {
	int anomalyID;
	int channel;
	String symbol;
	String type;
	int[] pmas;
	long tStart;
	long periodLength;


	public PDAnomaly(int anomalyID, int channel, String symbol, int[] pmas, long tStart, long periodLength, int severity) {
		this.anomalyID = anomalyID;
		this.channel = channel;
		this.symbol = symbol;
		this.pmas = pmas;
		this.tStart = tStart;
		this.periodLength = periodLength;
		this.type = "PumpAndDump";
	}
}
