package components;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import connections.Task;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	
	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		System.out.println("Web Scanner Thread Pool started...");
		while(true) {
			
		}
	}
	
	public Future<Map<String, Integer>> putTask(Task t) {
		return ex.submit(t.getScannerPtr());
	}
	
	public void stop() {
		ex.shutdown();
	}

}
