package connections;

public class JobDispatcher implements Runnable {

	JobQueue<Task> jobqueue;
	
	public JobDispatcher(JobQueue<Task> j) {
		jobqueue=j;
	}
	
	@Override
	public void run() {
		while(true) {
			Task t = jobqueue.get();
			if (t.getType().equals("WEB")) {
				// t se prosledjuje WEB thread poolu
			} else {
				// t se prosledjuje FILE thread poolu
			}
		}
	}

}
