import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/*TODO add data checks to flag corrupted stock values*/


//TODO add proper exception handling
public class DataExtractor {

    //starts threads for extracting data
    public static LinkedList<Stock> extract(String args[]) throws Exception {
        if (args.length == 0) {                 //if no arguments are passed print the results and exit
            instructions();
            return null;
        } else if (args[0].equals("-h") && (args.length >= 2)) {        //if historical is chosen and given a filename start extraction on historical data
            LinkedList<Stock> queue = new LinkedList<Stock>();
            StockBuilderThread historical = new StockBuilderThread("h", queue, args[1]);    //start extraction thread
            return queue;
        } else if (args[0].equals("-l") && (args.length >= 2)) {                              //if live is chosen start extraction on live data TODO allow optional stream address specification
            LinkedList<Stock> queue = new LinkedList<Stock>();
            StockBuilderThread live = new StockBuilderThread("l", queue, args[1]);                   //start extraction thread
            return queue;
        } else {                                                        //if none of the above occur inform user that the arguments were invalid
            System.out.println("Invalid options. Give no arguments to see useage instructions.");
            return null;
        }
    }
        

    //provides instructions for usage
    private static void instructions(){
        System.out.println("DataExtractor usage Guide:");
        System.out.println("DataExtractor -[option] [arguments]...\n");
        System.out.println("Options:");
        System.out.println("-h      Takes two arguments: [FileName] and channel.");
        System.out.println("        Processes stock data from file(assumes first line is context line, does not process first line)");
        System.out.println("-l      Takes two arguments: [address] and channel");
        System.out.println("        Connects to the stream at address on port 80 and processes the live data stream");
        System.out.println("\nExample usage:");
        System.out.println("DataExtractor -h file 0      Will process the stock data in file, and will send all anomalies with mode 0.");
    }

    //extracts live data from the live data stream and places it into the queue in the form of stock object
    public static void liveData(LinkedList<Stock> queue, String server) throws Exception{
        String value;
        Stock stock;

        Socket s = new Socket(server, 80);   //establishes a server socket

        InputStream is = s.getInputStream();
        BufferedReader dis = new BufferedReader(new InputStreamReader(is));         //connects to stream

        /*reads values from server feed and places them in a queue*/
        value = dis.readLine();
        while (true) {
            value = dis.readLine();
            stock = stockBuilder(value);
            if (stock != null) {                //if the stock was of an invalid form then do not add
                synchronized (queue) {          //synchronised ensures thread saftey
                    queue.add(stock);
                }
            }
        }
        //TODO find a way to close in the case of shutdown
        // s.close();
    }

    //extracts historical data from a file and places it into the queue in the form of a stock object
    public static void historicalData(String fileName, LinkedList<Stock> queue) throws Exception{
        String str;
        Stock stock;
        if ((fileName == null) || (fileName.equals(""))) {              //checks for possible empty name insertion
            System.out.println("Error: No file name entered");
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));       //reads lines and adds them to the queue
            in.readLine();
            while ((str = in.readLine()) != null)  {
                stock = stockBuilder(str);
                if (stock != null) {                //if the stock was of an invalid form then do not add
                    synchronized (queue) {      //synchronised ensures thread saftey
                        queue.add(stockBuilder(str));
                    }
                }
            }
            in.close();
            //TODO find a method of exit when file end
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot find file: " + fileName);
            return;
        }
    }

    /*splits the string by commas, then builds and returns a Stock object using the stock values*/
    private static Stock stockBuilder(String stock) throws ParseException{
        String[] str = stock.split(",");
        try {
            return new Stock(parseDate(str[0]), str[1], str[2], Double.parseDouble(str[3]), Integer.parseInt(str[4]), str[5], str[6], str[7], Double.parseDouble(str[8]), Double.parseDouble(str[9]));
        } catch (Exception e) {
            System.out.println("parse error for trade(invalid format or corrupted data):\n" + stock);           //informs of incompatible data form
            return null;
        }
    }

    //parses date from a string format to a long, in the form ms since linux epoch
    private static long parseDate(String date) throws ParseException{
        DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);
        return format.parse(date).getTime();
    }

}