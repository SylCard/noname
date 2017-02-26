import java.util.LinkedList;
import java.sql.*;

class ExtractionTester {
	public static void main(String[] args) throws Exception {
		LinkedList<Stock> queue;

		Connection conn = establishDatabase();

		if (args[0].equals("-view")) {
			showTable(conn);
			return;
		}

		//either find a method of running the schema in program or run it in the setup script

		queue = DataExtractor.extract(args);
		Stock stock;
		VolumeSpike test = null;
		while (true) {					//TODO find a way to exit loop safley, especialy in the case of historical data
			synchronized (queue) {
				while (queue.size() > 0) {
					stock = queue.removeFirst();
					// System.out.println(stock.getTime());
					insertStock(stock, conn);						//any sql insertion method must regulary be cleared to prevent memory overflow
					if (stock.getSymbol().equals("DGE.L")) {
						if (test == null) {
							test = new VolumeSpike(stock);
						} else {
							test.update(stock);
							test.check(stock);
						}

					}


					if (stock.getTime().equals("2017-01-13 23:59:58.857204")) {
						System.out.println("file end reached");				//informs when file end reached for historic data TODO find a better way to do this
					}


				}
			}
		}
	}

	//builds database connection
	private static Connection establishDatabase() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Could not load Driver");
		}

		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/FTSE100", "java", "password");
			System.out.println("It works!");
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
    		System.out.println("SQLState: " + e.getSQLState());
    		System.out.println("VendorError: " + e.getErrorCode());
		}
		return conn;
	}

	//shows table values (currently first five and only time)
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
        	String query = "INSERT INTO stocks VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, stock.getTime());
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
            prepStmt.close();
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
}