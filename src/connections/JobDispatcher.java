package connections;

public class JobDispatcher implements Runnable {

	JobQueue<Task> jobqueue;
	
	public JobDispatcher(JobQueue<Task> j) {
		jobqueue=j;
	}
	
	@Override
	public void run() {
		System.out.println("Job dispatcher started...");
		while(true) {
			Task t = jobqueue.get();
			if (t.getType().equals("WEB")) {
				System.out.println("Job dispatcher found WEB job...");
				// t se prosledjuje WEB thread poolu
			} else {
				System.out.println("Job dispatcher found FILE job...");
				// t se prosledjuje FILE thread poolu
			}
		}
	}

}
