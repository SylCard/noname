
class FFAnomaly extends Anomaly {
    int anomalyID;
    int channel;
    String type;
    Stock stock;
    int severity;
    double rma;
    String error;

    public FFAnomaly(int anomalyID, int channel, Stock stock, int severity, double rma, String error) {
        this.anomalyID = anomalyID;
        this.channel = channel;
        this.type = "FatFinger";
        this.stock = stock;
        this.severity = severity;
        this.rma = rma;
        this.error = error;
    }
}
