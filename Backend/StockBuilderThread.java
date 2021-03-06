import java.lang.Runnable;
import java.util.LinkedList;

class StockBuilderThread implements Runnable {

	Thread runner;
	LinkedList<Stock> queue;
	String type;
	String file;
	public StockBuilderThread() {
	}
	public StockBuilderThread(String threadName) {
		runner = new Thread(this); // (1) Create a new thread.
		runner.start(); // (2) Start the thread.
	}

	//live data
	public StockBuilderThread(String threadName, LinkedList<Stock> queue) {
		runner = new Thread(this);
		this.queue = queue;
		this.type = threadName;
		runner.start(); // (2) Start the thread.
	}

	//historical data
	public StockBuilderThread(String threadName, LinkedList<Stock> queue, String file) {
		runner = new Thread(this);
		this.queue = queue;
		this.type = threadName;
		this.file = file;
		runner.start(); // (2) Start the thread.
	}

	public void run() {
		//starts one of the extraction threads, pushing data onto queues
		try {
			if (type.equals("l")) {
				DataExtractor.liveData(queue, file);
			} else if (type.equals("h")) {
				DataExtractor.historicalData(file, queue);
			}
		} catch (Exception e) {
			System.out.println("Something went wrong.");
		}
		// System.out.println(Thread.currentThread());
	}
}