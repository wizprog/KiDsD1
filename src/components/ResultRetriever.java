package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import connections.ResultRetrieverTaskType;
import connections.Type;
import main.CLI;

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
		
		Map<String, Integer> counter = new HashMap<String, Integer>();

		for (String word : CLI.key_words) {
			counter.put(word, 0);
		}
		
		//adding together all results for a specific domain
		for (Map.Entry<String, Map<String, Integer>> entry : this.webDictResult.entrySet()) {
		    String url = entry.getKey();
		    if (url.startsWith(domainName)) {
		    	Map<String, Integer> object = entry.getValue();
		    	for (Map.Entry<String, Integer> entry2 : object.entrySet()) {
		    		counter.put(entry2.getKey(), counter.get(entry2.getKey()) + entry2.getValue());  
		    	}
		    }
		} 
		
		result.put(domainName, counter);
		return result;
	}

	@Override
	public Map<String, Map<String, Integer>> call() {
		try {
			CLI.rrtp.taskStarted(); //signal to inform that a new task has started
			Map<String, Map<String, Integer>> result;
		    if (this.type.equals(ResultRetrieverTaskType.DOMENSCANER)) {
		    	result =  this.domain();
		    	CLI.rrtp.taskEnded();
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
		    	
		    	CLI.rrtp.taskEnded();
		    	return result;
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
