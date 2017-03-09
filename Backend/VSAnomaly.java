class VSAnomaly extends Anomaly {
	int anomalyID;
	int channel;
	String symbol;
	String type;
	int[] vmas;
	int[] volumes;
	long tStart;
	long periodLength;
	int severity;

	public VSAnomaly(int anomalyID, int channel, String symbol, int[] vmas, int[] volumes, long tStart, long periodLength, int severity) {
		this.anomalyID = anomalyID;
		this.channel = channel;
		this.symbol = symbol;
		this.vmas = vmas;
		this.volumes = volumes;
		this.tStart = tStart;
		this.periodLength = periodLength;
		this.severity = severity;
		this.type = "VolumeSpike";
	}
}
