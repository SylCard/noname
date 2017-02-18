import java.util.LinkedList;

class ExtractionTester {
	public static void main(String[] args) throws Exception {
		LinkedList<Stock> queue;
		queue = DataExtractor.extract(args);
		if (queue != null) {
			System.out.println(queue.getFirst().getTime());
		}
	}	
}