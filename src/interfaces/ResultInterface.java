package interfaces;

import java.util.Map;
import java.util.concurrent.Future;

public interface ResultInterface {
	public Map<String, Integer> getResult(String query);
	public Map<String, Integer> queryResult(String query) throws Exception;
	public void clearSummary(String summaryType);
	public Map<String, Map<String, Integer>> getSummary(String summaryType);
	public Map<String, Map<String, Integer>> querySummary(String summaryType);
	public void addCorpusResult(String corpusName, Map<String,Integer> corpusResult);
}
