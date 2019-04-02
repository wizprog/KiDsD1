package components;

import java.util.Map;
import java.util.concurrent.Callable;

public class FileScanner implements Callable<Map<String,Integer>> {
	
	String directory_destination;

	public FileScanner(String directory_destination) {
		super();
		this.directory_destination = directory_destination;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
