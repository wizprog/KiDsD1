package connections;

import java.io.File;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import components.FileScanner;
import components.WebScanner;
import interfaces.TaskJob;
import main.CLI;

public class Task implements TaskJob {

	Type myType;
	File task_name_destination;
	Callable<Map<String, Map<String, Integer>>> scannerPtr;

	public Task(Type myType, String task_name_destination, Integer hop_count) {
		super();
		this.myType = myType;
		if (this.myType.equals(Type.WEB)) {
			scannerPtr = new WebScanner(hop_count, task_name_destination, CLI.key_words);
		}
	}
	
	public Task(Type myType, Stack<File> task_name_destination, Integer hop_count, String[] searchingWords) {
		super();
		this.myType = myType;
		//in discussion
		if (this.myType.equals(Type.WEB)) {
//			scannerPtr = new WebScanner(hop_count, task_name_destination.pop(),searchingWords);
		}else {
			scannerPtr = new FileScanner(task_name_destination);
		}
	}

	@Override
	public String getType() {
		return myType.toString();
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
	
	public Callable<Map<String, Map<String, Integer>>> getScannerPtr(){
		return this.scannerPtr;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setQuery(String q) {
		// TODO Auto-generated method stub
		
	}

}
