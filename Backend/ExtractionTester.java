import java.util.LinkedList;
import java.util.Hashtable;
import java.sql.*;
import java.util.Arrays;

class ExtractionTester {
	static int channel;

	public static void main(String[] args) throws Exception {
		LinkedList<Stock> queue;
		Hashtable<String,ICheck> table = new Hashtable<String,ICheck>();			//TODO in future change ICheck to either LinkedList<ICheck> or ICheck[]
		String symbol;

		Connection conn = establishDatabase();		//get database connection
		Client sender = new Client("46.101.34.184", 6969);

		if (args.length >= 3) {
			channel = Integer.parseInt(args[2]);
		}

		if (args[0].equals("-view")) {				//prints out table contents TODO remove
			showTable(conn);						
			return;
		}

		//either find a method of running the schema in program or run it in the setup script

		queue = DataExtractor.extract(args);		//start the extraction system
		Stock stock;
		VolumeSpike test = null;
		while (true) {					//TODO find a way to exit loop safley, especialy in the case of historical data
			synchronized (queue) {
				while (queue.size() > 0) {			//when the queue contains transactions pass them to analysis
					stock = queue.removeFirst();	

					insertStock(stock, conn);						//inserts the stock into the sql table
					symbol = stock.getSymbol();
					if (table.containsKey(symbol)) {				//if symbol already has check object use it
						table.get(symbol).update(stock);			//TODO fetch object so this is only called once
						objectParser(table.get(symbol).check(stock, sender), sender);
					} else {										//if symbol has no check object create one
						table.put(symbol, new VolumeSpike(stock, channel));								
					}
					//TODO make system work for sectors as well as symbols


					// if (stock.getSymbol().equals("DGE.L")) {		//currently uses fixed stock for testing TODOchange this section to a hash table
					// 	if (test == null) {						//when no check object exists for the stock create one
					// 		test = new VolumeSpike(stock);
					// 	} else {								//update the check objects then run the checks
					// 		test.update(stock);
					// 		test.check(stock);
					// 	}

					// }


					// if (stock.getTime().equals("2017-01-13 23:59:58.857204")) {				//TODO change this to look for the long form
					// 	System.out.println("file end reached");				//informs when file end reached for historic data TODO find a better way to do this
					// }


				}
			}
		}
	}

	//builds database connection
	private static Connection establishDatabase() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();			//load drivers
		} catch (Exception e) {
			System.out.println("Could not load Driver");
		}

		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/FTSE100", "java", "password");	//create connection to sql database
			System.out.println("It works!");
		} catch (SQLException e) {				//if connection fails return error data
			System.out.println("SQLException: " + e.getMessage());
    		System.out.println("SQLState: " + e.getSQLState());
    		System.out.println("VendorError: " + e.getErrorCode());
		}
		return conn;
	}

	//shows table values (currently first five and only time) TODO remove, only exists for display purposes
	private static void showTable(Connection conn) throws SQLException{
		Statement stmt = conn.createStatement();
		ResultSet rset;
		String query = "SELECT * FROM stocks;";
		try {
            rset = stmt.executeQuery(query);            //attempt to execute query
        } catch (SQLException e) {
            System.out.println("Could not execute query");
            stmt.close();
            return;
        }
        int counter = 0;        //counts number of result lines
        while (rset.next() && (counter < 5)) {
            System.out.println(rset.getString(1));      //prints out result lines in order
            counter++;
        }
        if (counter == 0) {
            System.out.println("No matching data");     //if the query returns empty this line is printed
        }
        stmt.close();
	}

	private static void insertStock(Stock stock, Connection conn) throws SQLException {
		try {
        	String query = "INSERT INTO stocks VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";		//builds a query from stock object data
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setLong(1, stock.getTime());
            prepStmt.setString(2, stock.getBuyer());
            prepStmt.setString(3, stock.getSeller());
            prepStmt.setFloat(4, (float) stock.getPrice());
            prepStmt.setInt(5, stock.getSize());
            prepStmt.setString(6, stock.getCurrency());
            prepStmt.setString(7, stock.getSymbol());
            prepStmt.setString(8, stock.getSector());
            prepStmt.setFloat(9, (float) stock.getBid());
            prepStmt.setFloat(10, (float) stock.getAsk());
            prepStmt.execute();
            prepStmt.close();				//find a better method for closing, as currently any exception will cause a mem leak
        } catch (SQLException e) {
        	System.out.println("For stock at time: " +stock.getTime());
            System.out.println("SQLException: " + e.getMessage());
    		System.out.println("SQLState: " + e.getSQLState());
    		System.out.println("VendorError: " + e.getErrorCode());
            return;
        } /*finally {
        	prepStmt.close();
        }*/
	}

	private static void objectParser(Anomaly anomaly, Client sender) {
		if (anomaly instanceof VSAnomaly) {
			String out = vsParser((VSAnomaly)anomaly);
			System.out.println(out);
			sender.sendMessage(out);
		}
	}

	private static String vsParser(VSAnomaly anomaly) {
		String jsonString = "{";
		jsonString += "\"AnomalyId\":" + anomaly.anomalyID + ",";
		jsonString += "\"mode\":" + anomaly.channel + ",";
		jsonString += "\"type\":\"" + anomaly.type + "\",";
		jsonString += "\"symbol\":\"" + anomaly.symbol + "\",";
		jsonString += "\"y-axis1\":" + Arrays.toString(anomaly.vmas) + ",";
		jsonString += "\"y-axis2\":" + Arrays.toString(anomaly.volumes) + ",";
		jsonString += "\"time_begin\":" + anomaly.tStart + ",";
		jsonString += "\"period_len\":" + anomaly.periodLength + ",";
		jsonString += "\"severity\":" + anomaly.severity;
		jsonString += "}";
		return jsonString;
	}


}