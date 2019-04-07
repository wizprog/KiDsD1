package connections;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import components.FileScanner;
import components.WebScanner;
import interfaces.TaskJob;

enum Type {
	WEB, DIRECTORY
}

public class Task implements TaskJob {

	Type myType;
	String task_name_destination;
	Callable<Map<String, Map<String, Integer>>> scannerPtr;

	public Task(Type myType, String task_name_destination, Integer hop_count, String[] searchingWords) {
		super();
		this.myType = myType;
		this.task_name_destination = task_name_destination;
		
		//in discussion
		if (this.myType.equals(Type.WEB)) {
			scannerPtr = new WebScanner(hop_count, task_name_destination, searchingWords);
		}
	}
	
	public Task(Type myType, Stack<String> task_name_destination, Integer hop_count, String[] searchingWords) {
		super();
		this.myType = myType;
		//in discussion
		if (this.myType.equals(Type.WEB)) {
			scannerPtr = new WebScanner(hop_count, task_name_destination.pop(),searchingWords);
		}else {
			scannerPtr = new FileScanner(task_name_destination);
		}
	}

	@Override
	public String getType() {
		return myType.toString();
	}

	@Override
	public String getQuery() {
		return task_name_destination;
	}

	@Override
	public void setType(String s) {
		if(s.equals("WEB")) {
			myType=Type.valueOf("WEB".toUpperCase());
		};
		if(s.equals("DIRECTORY")) {
			myType=Type.valueOf("DIRECTORY".toUpperCase());
		};
	}
	
	@Override
	public void setQuery(String q) {
		task_name_destination=q;
		
	}
	
	public Callable<Map<String, Map<String, Integer>>> getScannerPtr(){
		return this.scannerPtr;
	}

}
