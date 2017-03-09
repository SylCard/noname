//abstract class for anomalies, ensures they all contain the minimum required data
abstract public class Anomaly {
	int anomalyID;			//used to reference the anomaly server side
	int channel;			//used to differenciate between live and historical data streams
	String symbol;			//used to determine which stock is being manipulated
	String type;			//used to id what type of anomaly is being sent
}