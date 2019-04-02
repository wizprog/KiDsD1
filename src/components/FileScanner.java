package components;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

public class FileScanner implements Callable<Map<String,Integer>> {
	
	Stack<String> directoryUrls; 

	public FileScanner(Stack<String> directoryUrls) {
		super();
		this.directoryUrls = directoryUrls;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
