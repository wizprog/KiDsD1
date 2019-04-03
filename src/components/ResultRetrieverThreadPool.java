package components;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import interfaces.ResultInterface;

public class ResultRetrieverThreadPool implements Runnable , ResultInterface{
	
	ExecutorService ex;
	private final BlockingQueue<Future<Map<String,Integer>>> queue;	
	
	public ResultRetrieverThreadPool() {
		super();
		this.queue = new ArrayBlockingQueue<Future<Map<String,Integer>>>(20) ;
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		for (int i=0; i<10; i++) {
			ResultRetriever r = new ResultRetriever(this.queue);
			ex.execute(r);
		}
		System.out.println("Web Scanner Thread Pool started...");
		while(true) {
			try {
				
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void putWaitResult(Future<Map<String,Integer>> wait) {
		try {
			this.queue.put(wait);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		ex.shutdown();
	}

	@Override
	public Map<String, Integer> getResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> queryResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearSummary(String summaryType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<String, Integer>> getSummary(String summaryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCorpusResult(String corpusName, Map<String, Integer> corpusResult) {
		// TODO Auto-generated method stub
		
	}
}
