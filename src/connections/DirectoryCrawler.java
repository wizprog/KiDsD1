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

	public DirectoryCrawler(long sleep_time, String files, JobQueue<Task> queue) {
		super();
		this.sleep_time = sleep_time;
		this.files = files.split(",");
		this.queue = queue;
	}

	@Override
	public void run() {
		Stack<String> stack = new Stack<String>(); 
		System.out.println("Directory crawler started...");
		while(true) {
			try {
				File dir = new File(files[0]);
				if (dir.exists()) {
					
					for (File f: dir.listFiles()) {
						if(f.getName().contains("")) {
							queue.put(new Task(Type.DIRECTORY, f.getName(), null));
						}else {
							stack.push(f.getName());
						}
					}
					
					while(!stack.empty()) {
						String fileName = stack.pop();
						File help = new File(fileName);
						for (File f: dir.listFiles()) {
							if(f.getName().contains("")) {
								queue.put(new Task(Type.DIRECTORY, f.getName(), null));
							}else {
								stack.push(f.getName());
							}
						}
					}
					
				}
				
				Thread.sleep(this.sleep_time);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
