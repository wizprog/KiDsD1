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
import exceptions.CorpusDoesNotExist;
import exceptions.CorpusNotComplete;
import exceptions.NonExistingCommand;
import interfaces.ResultInterface;
import main.CLI;

public class ResultRetrieverThreadPool implements Runnable, ResultInterface {

	ExecutorService ex;
	private Map<String, Map<String, Integer>> webResultData, fileResultData, webResultDataSummary, fileResultDataSummary, webResultDataDomain;
	Semaphore putResultSemaphore, endSemaphore;
	boolean shutdown;
	int taskRunner;

	public ResultRetrieverThreadPool() {
		super();
		this.webResultData = new HashMap<String, Map<String, Integer>>();
		this.fileResultData = new HashMap<String, Map<String, Integer>>();
		this.webResultDataSummary = new HashMap<String, Map<String, Integer>>();
		this.fileResultDataSummary = new HashMap<String, Map<String, Integer>>();
		this.webResultDataDomain = new HashMap<String, Map<String, Integer>>();
		this.putResultSemaphore = new Semaphore(1); 
		this.ex = Executors.newFixedThreadPool(10);
		this.shutdown = false;
		this.taskRunner = 0;
		this.endSemaphore = new Semaphore(1);
	}

	@Override
	public void run() {

		System.out.println("Web Scanner Thread Pool started...");
		while (!this.shutdown) {

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
		while(this.taskRunner != 0);
		ex.shutdown();
		System.out.println("Result Retriever Thread Pool ended...");
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
			if (!this.webResultDataDomain.containsKey(name)) {
				 while(CLI.wstp.checkIfItsWorking(name)) Thread.sleep(500);  //Not a good way to do this but it works
				 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(deepCopy(webResultData), deepCopy(fileResultData), name, ResultRetrieverTaskType.DOMENSCANER, Type.WEB);
			     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
			     Map<String, Map<String, Integer>> result = help.get();
			     
			     putResultSemaphore.acquire();
			     this.webResultDataDomain.put(result.entrySet().iterator().next().getKey(), result.entrySet().iterator().next().getValue());
			     putResultSemaphore.release();
			     
			     return result.get(name);
			}
		   return this.webResultDataDomain.get(name);			
		} else {
			if (!fileResultData.containsKey(name)) {
				if (CLI.fstp.checkIfItsWorking(name)) {
					while(CLI.fstp.checkIfItsWorking(name)) Thread.sleep(500);
					return fileResultData.get(name);
				}
				return null;
			}
		   return fileResultData.get(name);	
		}
	}

	@Override
	public Map<String, Integer> queryResult(String query) throws Exception {
		String name = CLI.parseName(query);
		String type = CLI.parseType(query);
		
		if (type.equals("web")) {
			if (CLI.wstp.checkIfItsWorking(name)) {
				throw new CorpusNotComplete("This task/domain is currently being resolved");
			}else {
				if(!webResultDataDomain.containsKey(name)) {
					throw new CorpusDoesNotExist("There is no info about that web domain");
				}
			}
			return webResultDataDomain.get(name);
		} else {
			if (CLI.fstp.checkIfItsWorking(name)) {
				throw new CorpusNotComplete("That task is now running");
			}else {
				if(fileResultData.isEmpty() || !fileResultData.containsKey(name) ) {
					throw new CorpusDoesNotExist("There is no info about about that directory");
				}
			}			
			Map<String, Integer> result = fileResultData.get(name);
			return result;
		}
	}

	@Override
	public void clearSummary(Type summaryType) {
		try {
			putResultSemaphore.acquire();
			if (summaryType.equals("WEB")) {
				webResultDataSummary.clear();
				System.out.println("Web summary has been cleared");
			} else {
				fileResultData.clear();
				System.out.println("File summary has been cleared");
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
					 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(deepCopy(webResultData), deepCopy(fileResultData), null, ResultRetrieverTaskType.SUMMARY, Type.WEB);
				     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
				     Map<String, Map<String, Integer>> result = help.get();
				     
				     putResultSemaphore.acquire();
				     webResultDataSummary = result;
				     putResultSemaphore.release();
					}
					return webResultDataSummary;
			} else {
					if (fileResultDataSummary.isEmpty()) {
						 Callable<Map<String, Map<String, Integer>>> rr = new ResultRetriever(deepCopy(webResultData), deepCopy(fileResultData), null, ResultRetrieverTaskType.SUMMARY, Type.DIRECTORY);
					     Future<Map<String, Map<String, Integer>>> help = ex.submit(rr);
					     Map<String, Map<String, Integer>> result = help.get();
					     
					     putResultSemaphore.acquire();
					     fileResultDataSummary = result;
					     putResultSemaphore.release();
						}
					return fileResultDataSummary;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) throws Exception{ // obradjuje se samo slucaj kada je
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
				throw new CorpusNotComplete("Some file tasks are running");
			}else {
				if(fileResultData.isEmpty()) {
					throw new CorpusDoesNotExist("There is no info about file summary");
				}
			}			
			return fileResultData;
		}
	}

	public void clearWebResultData() {
		try {
			putResultSemaphore.acquire();
			webResultData.clear();
			webResultDataDomain.clear();
			putResultSemaphore.release();
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

	public Map<String, Map<String, Integer>> deepCopy(Map<String, Map<String, Integer>> original){
		
		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
		for (Map.Entry<String, Map<String, Integer>> entry : original.entrySet()) {
		    String key = entry.getKey();
		    Map<String, Integer> helpMap = entry.getValue();   
		    Map<String, Integer> copyMap = new HashMap<String, Integer>();
		    for (Map.Entry<String, Integer> entry2 : helpMap.entrySet()) {
		    	copyMap.put(entry2.getKey(), entry2.getValue());
		    }
		    result.put(key, copyMap);
		} 
		return result;
	}
}
