package components;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import interfaces.ResultInterface;

public class ResultRetriever implements Runnable {

	Map<String, Map<String, Integer>> webDictResult;
	Map<String, Map<String, Integer>> fileDictResult;
	String domainName;

	public ResultRetriever(Map<String, Map<String, Integer>> webDictResult,
			Map<String, Map<String, Integer>> fileDictResult, String domainName) {
		super();
		this.webDictResult = webDictResult;
		this.fileDictResult = fileDictResult;
		this.domainName = domainName;
	}

	@Override
	public void run() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
