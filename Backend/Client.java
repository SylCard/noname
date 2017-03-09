import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket socket = null;
	private String ip;
	private int port;
	private static int counter;			//used to calculate AnomalyIDs for each channel

	//set up connection service
	public Client(String ip, int port) throws UnknownHostException, IOException, ClassNotFoundException {
		this.ip = ip;
		this.port = port;
		this.socketConnect(ip, port);
		counter = 0;
	}

	//used to retrieve AnomalyID
	public static int getCounter() {
		return counter;
	}

	public void sendMessage(String message) {
		this.echo(message);
		counter++;			//ensures the ID is incremented each time an anomaly is sent
	}

	//establishes socket
	private void socketConnect(String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);
	}

	//sends message through socket
	private void echo(String message) {
		try {
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true); //sets of data stream on socket

			out.println(message);		//prints to socket data stream
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	//used to retrive socket
	private Socket getSocket() {
		return socket;
	}
}
