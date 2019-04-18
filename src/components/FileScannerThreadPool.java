package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;

public class FileScannerThreadPool implements Runnable {

	ExecutorService ex;
	List<Future<Map<String, Map<String, Integer>>>> futureBlock;
	ResultRetrieverThreadPool rrtp;
	Semaphore semaphore;
	Semaphore endSemaphore;
	int taskRunner;

	public FileScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(1);
		this.endSemaphore = new Semaphore(1);
		futureBlock = new ArrayList<Future<Map<String, Map<String, Integer>>>>();
		ex = Executors.newFixedThreadPool(10);
		this.rrtp = rrtp;
		this.taskRunner = 0;
	}

	@Override
	public void run() {
		System.out.println("File Scanner Thread Pool started...");
		Future<Map<String, Map<String, Integer>>> help;
		while (true) {
			try {
				help = null;
				semaphore.acquire();
				if (!futureBlock.isEmpty())
					help = futureBlock.remove(0);
				semaphore.release();
				if (help != null) {
					Map<String, Map<String, Integer>> result = help.get();
					this.putRes(result);
				}
				Thread.sleep(1000); //just not to spam the cpu every moment, testing purposes 
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putRes(Map<String, Map<String, Integer>> result) {
	/*	Iterator it = result.entrySet().iterator();
		while (it.hasNext()) {
			HashMap<String, Map<String, Integer>> itRes = (HashMap<String, Map<String, Integer>>)  it.next();
			String key = itRes.entrySet().iterator().next().getKey();
			rrtp.putResult(itRes.get(key), "WEB", key);
		}  */
		for (Map.Entry<String, Map<String, Integer>> entry : result.entrySet()) {
		    String key = entry.getKey();
		    rrtp.putResult(entry.getValue(), "FILE", key);
		}
		
	}

	public void putTask(Task t) {
		try {
			semaphore.acquire();
			Future<Map<String, Map<String, Integer>>> help = ex.submit(t.getScannerPtr());
			futureBlock.add(help);
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() throws InterruptedException {
		while(this.taskRunner != 0);
		ex.shutdown();	
		System.out.println("File Scanner Thread Pool ended...");
	}
	
	public void taskStarted() {
		try {
			endSemaphore.acquire();
			this.taskRunner++;
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void taskEnded() {
		try {
			endSemaphore.acquire();
			this.taskRunner--;
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
