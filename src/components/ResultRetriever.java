package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import connections.ResultRetrieverTaskType;
import connections.Type;

public class ResultRetriever implements Callable<Map<String, Map<String, Integer>>> {

	Map<String, Map<String, Integer>> webDictResult;
	Map<String, Map<String, Integer>> fileDictResult;
	String domainName;
	ResultRetrieverTaskType type;
	Type typeWF;

	public ResultRetriever(Map<String, Map<String, Integer>> webDictResult,
			Map<String, Map<String, Integer>> fileDictResult, String domainName, ResultRetrieverTaskType type, Type typeWF) {
		super();
		this.webDictResult = webDictResult;
		this.fileDictResult = fileDictResult;
		this.domainName = domainName;
		this.type = type;
		this.typeWF = typeWF;
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
		    	result = new HashMap<String, Map<String, Integer>>();
		    	
		    	if (this.typeWF.equals(Type.WEB)) {
		    		for (Map.Entry<String, Map<String, Integer>> entry : this.webDictResult.entrySet()) {
		    		    String key = entry.getKey();
		    		    result.put(key, entry.getValue());
		    		} 
		    	}
		    	else {
		    		for (Map.Entry<String, Map<String, Integer>> entry : this.fileDictResult.entrySet()) {
		    		    String key = entry.getKey();
		    		    result.put(key, entry.getValue());
		    		} 
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
