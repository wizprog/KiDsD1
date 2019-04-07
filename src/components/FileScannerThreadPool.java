package components;

import java.util.ArrayList;
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

	public FileScannerThreadPool(ResultRetrieverThreadPool rrtp) {
		super();
		this.semaphore = new Semaphore(0);
		futureBlock = new ArrayList<Future<Map<String, Map<String, Integer>>>>();
		this.rrtp = rrtp;
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);
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
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putRes(Map<String, Map<String, Integer>> result) {
		Iterator it = result.entrySet().iterator();
		while (it.hasNext()) {
			Map<String, Map<String, Integer>> itRes = (Map<String, Map<String, Integer>>)  it.next();
			String key = itRes.entrySet().iterator().next().getKey();
			rrtp.putResult(itRes.get(key), "WEB", key);
		}
	}

	public Future<Map<String, Map<String, Integer>>> putTask(Task t) {
		try {
			semaphore.acquire();
			Future<Map<String, Map<String, Integer>>> help = ex.submit(t.getScannerPtr());
			futureBlock.add(help);
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
