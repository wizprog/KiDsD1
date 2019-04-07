package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import exceptions.NonExistingCommand;
import interfaces.ResultInterface;

public class ResultRetrieverThreadPool implements Runnable, ResultInterface {

	ExecutorService ex;
	private Map<String, Map<String, Integer>> webResultData;
	private Map<String, Map<String, Integer>> fileResultData;
	Semaphore resultSemaphore;

	public ResultRetrieverThreadPool() {
		super();
		webResultData = new HashMap<String, Map<String, Integer>>();
		fileResultData = new HashMap<String, Map<String, Integer>>();
		resultSemaphore = new Semaphore(0);
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);

		System.out.println("Web Scanner Thread Pool started...");
		while (true) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putResult(Map<String, Integer> result, String type, String key) {
		try {
			resultSemaphore.acquire();

			if (type.equals("WEB")) {
				webResultData.put(key, result);
			} else {
				fileResultData.put(key, result);
			}
			resultSemaphore.release();
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
			if (webResultData.containsKey(parseName(query))) {
				return webResultData.get(parseName(query));
			}
		} else if (parseType(query).equals("file")) {
			if (fileResultData.containsKey(parseName(query))) {
				return fileResultData.get(parseName(query));
			}
		} else {
			throw new NonExistingCommand("Non existing command");
		}
		return null;
	}

	@Override
	public void clearSummary(String summaryType) {
		if (summaryType.equals("WEB")) {
			webResultData.clear();
		} else {
			fileResultData.clear();
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getSummary(String summaryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) {
		if (summaryType.equals("web")) {
			return webResultData;
		}
		return null;
	}

	@Override
	public void addCorpusResult(String corpusName, Map<String, Integer> corpusResult) {
		// TODO Auto-generated method stub

	}
}
