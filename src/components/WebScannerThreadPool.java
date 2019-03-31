package components;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import connections.Task;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	
	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		while(true) {
			
		}
	}
	
	public void putTask(Task t) {
		ex.submit(t.getScannerPtr());
		
		// create WebScanner...
	}
	
	public void stop() {
		ex.shutdown();
	}

}
