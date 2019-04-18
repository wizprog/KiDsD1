package components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import connections.ResultRetrieverTaskType;
import connections.Type;
import exceptions.NonExistingCommand;
import interfaces.ResultInterface;
import main.CLI;

public class ResultRetrieverThreadPool implements Runnable, ResultInterface {

	ExecutorService ex;
	private Map<String, Map<String, Integer>> webResultData; // cache
	private Map<String, Map<String, Integer>> fileResultData; // cache
	Semaphore putResultSemaphore, summarySemaphore, resultSemaphore, endSemaphore;
	private Map<String, Map<String, Integer>> webResultDataSummary;
	private Map<String, Map<String, Integer>> fileResultDataSummary;
	boolean shutdown;
	int taskRunner;
	

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
		this.taskRunner = 0;
		this.endSemaphore = new Semaphore(1);
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
			if (type.equals(Type.WEB)) {
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
	public Map<String, Integer> getResult(String query) throws Exception {
		
		String name = CLI.parseName(query);
		String type = CLI.parseType(query);
		
		if (type.equals("web")) {
			if (!webResultData.containsKey(name)) {
				 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(webResultData, fileResultData, name, ResultRetrieverTaskType.DOMENSCANER, Type.WEB);
			     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
			     Map<String, Map<String, Integer>> result = help.get();
			     return result.get(name);
			}
		   return webResultData.get(name);			
		} else {
			if (!fileResultData.containsKey(name)) {
				return null;
			}
		   return fileResultData.get(name);	
		}
	}

	@Override
	public Map<String, Integer> queryResult(String query) {
		String name = CLI.parseName(query);
		String type = CLI.parseType(query);
		
		if (type.equals("web")) {
			if (CLI.wstp.checkIfItsWorking(name)) {
				System.out.print("This task is currently being resolved");
				return null;
			}else {
				if(!webResultData.containsKey(name)) {
					System.out.print("There is no info about that web summary");
					return null;
				}
			}
			return webResultData.get(name);
		} else {
			if (CLI.fstp.checkIfItsWorking(name)) {
				System.out.print("This task is currently being resolved");
				return null;
			}else {
				if(fileResultData.isEmpty()) {
					System.out.print("There is no info about that directory");
					return null;
				}
			}			
			return fileResultData.get(name);
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
					if (webResultDataSummary.isEmpty()) {
					 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(webResultData, fileResultData, null, ResultRetrieverTaskType.SUMMARY, Type.WEB);
				     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
				     webResultDataSummary = help.get();
					}
					return webResultDataSummary;
			} else {
					if (webResultDataSummary.isEmpty()) {
						 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(webResultData, fileResultData, null, ResultRetrieverTaskType.SUMMARY, Type.DIRECTORY);
					     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
					     fileResultDataSummary = help.get();
						}
					return fileResultDataSummary;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void releaseSummarySemaphore() {
		summarySemaphore.release();
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
