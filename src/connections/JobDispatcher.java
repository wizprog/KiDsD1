package connections;

import main.CLI;

public class JobDispatcher implements Runnable {

	JobQueue<Task> jobqueue;
	boolean shutdown;
	
	public JobDispatcher(JobQueue<Task> j) {
		jobqueue=j;
		this.shutdown = false;
	}
	
	@Override
	public void run() {
		System.out.println("Job dispatcher started...");
		while(!this.shutdown) {
			Task t = jobqueue.get();
			if (t.getType().equals("WEB")) {
				CLI.wstp.putTask(t);
			} else {
				CLI.fstp.putTask(t);
			}
		}
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}
	
	

}
