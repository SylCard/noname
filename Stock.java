public class Stock {
    private String time;
    private String buyer;
    private String seller;
    private double price;
    private int size;
    private String currency;
    private String symbol;
    private String sector;
    private double bid;
    private double ask;

    public Stock(String time, String buyer, String seller, double price, int size, String currency, String symbol, String sector, double bid, double ask) {
        this.time = time;
        this.buyer = buyer;
        this.seller = seller;
        this.price = price;
        this.size = size;
        this.currency = currency;
        this.symbol = symbol;
        this.sector = sector;
        this.bid = bid;
        this.ask = ask;
    }

    public String getTime() {
        return time;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getSeller() {
        return seller;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSector() {
        return sector;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }
}