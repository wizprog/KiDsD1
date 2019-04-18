package components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import connections.Type;
import exceptions.NonExistingCommand;
import interfaces.ResultInterface;
import main.CLI;

public class ResultRetrieverThreadPool implements Runnable, ResultInterface {

	ExecutorService ex;
	private Map<String, Map<String, Integer>> webResultData; // cache
	private Map<String, Map<String, Integer>> fileResultData; // cache
	Semaphore putResultSemaphore, summarySemaphore, resultSemaphore;
	private Map<String, Map<String, Integer>> webResultDataSummary;
	private Map<String, Map<String, Integer>> fileResultDataSummary;
	boolean shutdown;
	

	public ResultRetrieverThreadPool() {
		super();
		webResultData = new HashMap<String, Map<String, Integer>>();
		fileResultData = new HashMap<String, Map<String, Integer>>();
		webResultDataSummary = new HashMap<String, Map<String, Integer>>();
		fileResultDataSummary = new HashMap<String, Map<String, Integer>>();
		putResultSemaphore = new Semaphore(1); // treba samo 1 nit da prolazi
		summarySemaphore = new Semaphore(0); // treba da se odmah zablokira
		resultSemaphore = new Semaphore(0); // treba da se odmah zablokira
		ex = Executors.newFixedThreadPool(10);
		this.shutdown = false;
	}

	@Override
	public void run() {

		System.out.println("Web Scanner Thread Pool started...");
		while (!this.shutdown) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putResult(Map<String, Integer> result, Type type, String key) {
		try {
			putResultSemaphore.acquire();
			if (type.equals("WEB")) {
				webResultData.put(key, result);
			} else {
				fileResultData.put(key, result);
			}
			putResultSemaphore.release();


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stop() {
		ex.shutdown();
	}

	public String parseName(String query) {
		String name = query.substring(query.indexOf('|') + 1, query.length() - 1);
		return name;
	}

	public String parseType(String query) {
		String type = query.substring(0, query.indexOf('|'));
		return type;
	}

	@Override
	public Map<String, Integer> getResult(String query) {

		return null;
	}

	@Override
	public Map<String, Integer> queryResult(String query) throws Exception {
		if (parseType(query).equals("web")) {
			while (!webResultData.containsKey(parseName(query))
					|| webResultData.get(parseName(query)).containsValue(0)) {
				resultSemaphore.acquire();

			}
			return webResultData.get(parseName(query));

		} else if (parseType(query).equals("file")) {
			while (!fileResultData.containsKey(parseName(query))
					|| fileResultData.get(parseName(query)).containsValue(0)) {
				resultSemaphore.acquire();
			}
			return fileResultData.get(parseName(query));
		} else {
			throw new NonExistingCommand("Non existing command");
		}
	}

	@Override
	public void clearSummary(Type summaryType) {
		try {
			putResultSemaphore.acquire();
			if (summaryType.equals("WEB")) {
				webResultData.clear();
			} else {
				fileResultData.clear();
			}
			putResultSemaphore.release();
			System.out.println("Clear summary done");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getSummary(String summaryType) {
		try {
			if (summaryType.equals("web")) {
				if (!isFinished(summaryType)) {
					summarySemaphore.acquire(); // treba da se zablokira jer nije zavrsen summary, cim prodje moze da
												// vrati
					return webResultDataSummary;
				}
			} else {
				if (!isFinished(summaryType)) {

					summarySemaphore.acquire(); // treba da se zablokira jer nije zavrsen summary, cim prodje moze da
												// vrati
					return fileResultDataSummary;
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) { // obradjuje se samo slucaj kada je
																				// val==0
		if (summaryType.equals("web")) {
			if (CLI.wstp.areSomeTasksRunning()) {
				System.out.print("Some web tasks are running");
				return null;
			}else {
				if(webResultDataSummary.isEmpty()) {
					System.out.print("There is no info about web summary");
					return null;
				}
			}
			return webResultDataSummary;
		} else {
			if (CLI.fstp.areSomeTasksRunning()) {
				System.out.print("Some file tasks are running");
				return null;
			}else {
				if(fileResultDataSummary.isEmpty()) {
					System.out.print("There is no info about file summary");
					return null;
				}
			}			
			return fileResultDataSummary;
		}
	}

	@Override
	public void addCorpusResult(String corpusName, Map<String, Integer> corpusResult) {
		// TODO Auto-generated method stub

	}

	public boolean isFinished(String type) {
		if (type.equals("web")) {
			Iterator it = webResultData.entrySet().iterator();
			while (it.hasNext()) {
				Map<String, Integer> val = (Map<String, Integer>) it.next();
				if (val.containsValue(0)) {
					return false;
				}
			}
			return true;
		} else {
			Iterator it = fileResultData.entrySet().iterator();
			while (it.hasNext()) {
				Map<String, Integer> val = (Map<String, Integer>) it.next();
				if (val.containsValue(0)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public void clearWebResultData() {
		try {
			resultSemaphore.acquire();
			webResultData.clear();
			resultSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
