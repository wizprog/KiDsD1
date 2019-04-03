package components;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	ResultRetrieverThreadPool rrtp;
	
	Semaphore semaphore;
	
	public WebScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(0);
		this.rrtp = rrtp;
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		System.out.println("Web Scanner Thread Pool started...");
		while(true) {
			try {
				
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void putTask(Task t) {
		try {
			semaphore.acquire();
			Future<Map<String, Integer>> help = ex.submit(t.getScannerPtr());
			this.rrtp.putWaitResult(help);
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		ex.shutdown();
	}

}
