import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//connects to socket. Sends anomaly packet to Node.JS
public class Client {
	private Socket socket = null;
	private String ip;
	private int port;
	private static int counter;

	// public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
	// 	Client client = new Client();

	// 	String ip = "46.101.34.184";
	// 	int port = 6969;
	// 	client.socketConnect(ip, port);

	// 	String message = "message123";

	// 	System.out.println("Sending: " + message);
	// 	String returnStr = client.echo(message);
	// 	System.out.println("Receiving: " + returnStr);
	// }

	public Client(String ip, int port) throws UnknownHostException, IOException, ClassNotFoundException {
		this.ip = ip;
		this.port = port;
		this.socketConnect(ip, port);
		counter = 0;
	}

	public static int getCounter() {
		return counter;
	}

	public void sendMessage(String message) {
		this.echo(message);
		counter++;
	}

	private void socketConnect(String ip, int port) throws UnknownHostException, IOException {
		System.out.println("[Connecting to socket...]");
		this.socket = new Socket(ip, port);
		System.out.println("-");
	}

	public String echo(String message) {
		try {
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));

			out.println(message);
			//String returnStr = in.readLine();
			//return returnStr;
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Socket getSocket() {
		return socket;
	}
}
