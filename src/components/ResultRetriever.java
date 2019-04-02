package components;

import java.util.Map;
import java.util.concurrent.Future;

import interfaces.ResultInterface;

public class ResultRetriever implements Runnable,ResultInterface {

	Map<String, Map<String, Integer>> resDict;
	
	public ResultRetriever() {
		
	}
	
	@Override
	public void run() {
		
	}

	@Override
	public Map<String, Integer> getResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> queryResult(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearSummary(String summaryType) {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String, Map<String, Integer>> getSummary(String summaryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCorpusResult(String corpusName, Future<Map<String, Integer>> corpusResult) {
		// TODO Auto-generated method stub
		
	}
	
	public void parseQuery(String query) {
		
	}

}
