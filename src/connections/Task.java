package connections;

import interfaces.TaskJob;

public class Task implements TaskJob{
	
	enum Type{
		WEB,
		DIRECTORY
	}
	
	Type myType;

	public Task(Type myType) {
		super();
		this.myType = myType;
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
