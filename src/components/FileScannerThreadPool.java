package components;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;

public class FileScannerThreadPool implements Runnable {

	ExecutorService ex;
	
	Semaphore semaphore;
	
	public FileScannerThreadPool() {
		super();
		this.semaphore = new Semaphore(0);
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		System.out.println("File Scanner Thread Pool started...");
		while(true) {
			try {
				
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public Future<Map<String, Integer>> putTask(Task t) {
		try {
			semaphore.acquire();
			Future<Map<String, Integer>> help = ex.submit(t.getScannerPtr());
			semaphore.release();
			return help;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void stop() {
		ex.shutdown();
	}
}
