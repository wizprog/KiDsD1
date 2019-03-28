package connections;

import java.io.File;
import java.util.Stack;

public class DirectoryCrawler implements Runnable {

	long sleep_time;
	String files[];
	JobQueue<Task> queue;
	
	public DirectoryCrawler(long sleep_time) {
		super();
		this.sleep_time = sleep_time;
	}	

	public DirectoryCrawler(long sleep_time, String[] files) {
		super();
		this.sleep_time = sleep_time;
		this.files = files;
	}



	@Override
	public void run() {
		Stack<Integer> stack = new Stack<Integer>(); 
		while(true) {
			try {
				File dir = new File(files[0]);
				for (File f: dir.listFiles()) {
					if(f.getName().contains("")) {
						
					}
				}
				
				Thread.sleep(this.sleep_time);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
