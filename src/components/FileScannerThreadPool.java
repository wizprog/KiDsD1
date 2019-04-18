package components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.Task;
import connections.Type;

public class FileScannerThreadPool implements Runnable {

	ExecutorService ex;
	List<Future<Map<String, Map<String, Integer>>>> futureBlock;
	ResultRetrieverThreadPool rrtp;
	Semaphore semaphore;
	Semaphore endSemaphore;
	Semaphore taskSemaphore;
	int taskRunner;
	boolean shutdown;
	ArrayList<String> taskArrayNames;

	public FileScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(1);
		this.endSemaphore = new Semaphore(1);
		futureBlock = new ArrayList<Future<Map<String, Map<String, Integer>>>>();
		ex = Executors.newFixedThreadPool(10);
		this.rrtp = rrtp;
		this.taskRunner = 0;
		taskArrayNames = new ArrayList<String>();
		this.shutdown = false;
	}

	@Override
	public void run() {
		System.out.println("File Scanner Thread Pool started...");
		Future<Map<String, Map<String, Integer>>> help;
		while (!this.shutdown) {
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
				//just not to spam the cpu every moment, testing purposes 
				//Thread.sleep(1000); 
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putRes(Map<String, Map<String, Integer>> result) {
		for (Map.Entry<String, Map<String, Integer>> entry : result.entrySet()) {
		    String key = entry.getKey();
		    rrtp.putResult(entry.getValue(), Type.DIRECTORY, key);
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
		this.shutdown = true;
		System.out.println("File Scanner Thread Pool ended...");
	}
	
	public void taskStarted(Stack<File> taskName) {
		try {
			endSemaphore.acquire();
			this.taskRunner++;
			//we need to put all directory paths contained in a specific task
			File[] dirArr = taskName.toArray(new File[taskName.size()]);
			for (File directory : dirArr) taskArrayNames.add(directory.getAbsolutePath());
			endSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void taskEnded(Stack<File> taskName) {
		try {
			endSemaphore.acquire();
			this.taskRunner--;
			//we need to remove all directory paths contained in a specific task
			File[] dirArr = taskName.toArray(new File[taskName.size()]);
			for (File directory : dirArr) taskArrayNames.remove(directory.getAbsolutePath());
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
