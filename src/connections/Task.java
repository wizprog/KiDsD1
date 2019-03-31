package connections;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.ws.handler.MessageContext.Scope;

import interfaces.TaskJob;

enum Type {
	WEB, DIRECTORY
}

public class Task implements TaskJob {

	Type myType;
	String task_name_destination;
	Callable<Map<String,Integer>> res;

	public Task(Type myType, String task_name_destination) {
		super();
		this.myType = myType;
		this.task_name_destination = task_name_destination;
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

}
