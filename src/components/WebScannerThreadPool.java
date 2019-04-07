package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	ResultRetrieverThreadPool rrtp;
	List<Future<Map<String, Map<String, Integer>>>> futureBlock;
	
	Semaphore semaphore;
	
	public WebScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(0);
		this.rrtp = rrtp;
		futureBlock = new ArrayList<Future<Map<String, Map<String, Integer>>>>();
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
		Future<Map<String, Map<String, Integer>>> help;
		System.out.println("Web Scanner Thread Pool started...");
		while(true) {
			try {
				help = null;
				semaphore.acquire();
				if (!futureBlock.isEmpty()) help = futureBlock.remove(0);
				semaphore.release();
				if (help != null){
					Map<String, Map<String, Integer>> result = help.get();
					this.putRes(result);
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void putRes(Map<String, Map<String, Integer>> result){
		String key = result.entrySet().iterator().next().getKey();
		rrtp.putResult(result.get(key), "WEB", key);
	}
	
	public void putTask(Task t) {
		try {
			semaphore.acquire();
			Future<Map<String, Map<String, Integer>>> help = ex.submit(t.getScannerPtr());
			futureBlock.add(help);
			//this.rrtp.putWaitResult(help);
			
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
