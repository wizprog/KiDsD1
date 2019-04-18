package interfaces;

import java.util.Map;
import java.util.concurrent.Future;

import connections.Type;

public interface ResultInterface {
	public Map<String, Integer> getResult(String query) throws Exception;
	public Map<String, Integer> queryResult(String query) throws Exception;
	public void clearSummary(Type summaryType);
	public Map<String, Map<String, Integer>> getSummary(String summaryType);
	public Map<String, Map<String, Integer>> querySummary(String summaryType) throws Exception;
}
