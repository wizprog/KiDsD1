package components;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	
	
	
	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		
	}
	
	public void stop() {
		ex.shutdown();
	}

}
