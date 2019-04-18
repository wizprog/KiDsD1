package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import connections.ResultRetrieverTaskType;

public class ResultRetriever implements Callable {

	Map<String, Map<String, Integer>> webDictResult;
	Map<String, Map<String, Integer>> fileDictResult;
	String domainName;
	ResultRetrieverTaskType type;

	public ResultRetriever(Map<String, Map<String, Integer>> webDictResult,
			Map<String, Map<String, Integer>> fileDictResult, String domainName, ResultRetrieverTaskType type) {
		super();
		this.webDictResult = webDictResult;
		this.fileDictResult = fileDictResult;
		this.domainName = domainName;
		this.type = type;
	}
	
	//calculating summary method
	public Map<String, Map<String, Integer>> summary(){
			
		return null;
	}
	
	public Map<String, Map<String, Integer>> domain(){
		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
		
		for (Map.Entry<String, Map<String, Integer>> entry : result.entrySet()) {
		    String url = entry.getKey();
		    if (url.startsWith(domainName)) result.put(url, entry.getValue());
		} 
		return result;
	}

	@Override
	public Map<String, Map<String, Integer>> call() {
		try {
			Map<String, Map<String, Integer>> result;
		    if (this.type.equals("DOMENSCANER")) {
		    	result =  this.domain();
		    	return result;
		    }
		    else {
		    	Thread.sleep(1000);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
