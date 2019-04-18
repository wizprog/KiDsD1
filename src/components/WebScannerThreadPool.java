package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;
import connections.Type;

public class WebScannerThreadPool implements Runnable {
	
	ExecutorService ex;
	ResultRetrieverThreadPool rrtp;
	List<Future<Map<String, Map<String, Integer>>>> futureBlock;
	int taskRunner = 0;
	boolean shutdown;
	Semaphore semaphore;
	Semaphore endSemaphore;
	ArrayList<String> taskArrayNames;
	
	public WebScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(1);
		this.endSemaphore = new Semaphore(1);
		this.rrtp = rrtp;
		futureBlock = new ArrayList<Future<Map<String, Map<String, Integer>>>>();
		ex = Executors.newFixedThreadPool(10);
		this.taskRunner = 0;
		this.taskArrayNames = new ArrayList<String>();
		this.shutdown = false;
	}

	@Override
	public void run() {
		Future<Map<String, Map<String, Integer>>> help;
		System.out.println("Web Scanner Thread Pool started...");
		while(!this.shutdown) {
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
		rrtp.putResult(result.get(key), Type.WEB, key);
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
	
	public void stop() {
		while(this.taskRunner != 0);
		ex.shutdown();
		this.shutdown = true;
		System.out.println("Web Scanner Thread Pool ended...");
	}
	
	public void taskStarted(String taskName) {
		try {
			endSemaphore.acquire();
			this.taskRunner++;
			taskArrayNames.add(taskName);
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void taskEnded(String taskName) {
		try {
			endSemaphore.acquire();
			this.taskRunner--;
			taskArrayNames.remove(taskName);
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean checkIfItsWorking(String taskName) {
		boolean result = false;
		try {
			endSemaphore.acquire();
			result = taskArrayNames.contains(taskName);
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean areSomeTasksRunning() {
		boolean result = false;
		try {
			endSemaphore.acquire();
			result = (this.taskRunner > 0);
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
