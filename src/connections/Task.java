package connections;

import interfaces.TaskJob;

enum Type{
	WEB,
	DIRECTORY
}

public class Task implements TaskJob{
	
	Type myType;
	String task_name_destination;

	public Task(Type myType, String task_name_destination) {
		super();
		this.myType = myType;
		this.task_name_destination = task_name_destination;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
