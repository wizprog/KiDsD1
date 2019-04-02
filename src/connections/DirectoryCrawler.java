package connections;

import java.io.File;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import exceptions.CorpusDoesNotExist;

public class DirectoryCrawler implements Runnable {

	long sleep_time;
	String file_corpus_prefix;
	JobQueue<Task> queue;
	Stack<String> directoryStack;
	private final Semaphore semaphore;
	
	public DirectoryCrawler(long sleep_time) {
		super();
		this.sleep_time = sleep_time;
		this.directoryStack = new Stack<String>();
		semaphore = new Semaphore(0);
	}	

	public DirectoryCrawler(long sleep_time, String file_corpus_prefix, JobQueue<Task> queue) {
		super();
		this.sleep_time = sleep_time;
		this.file_corpus_prefix = file_corpus_prefix;
		this.queue = queue;
		this.directoryStack = new Stack<String>();
		semaphore = new Semaphore(0);
	}
	
	public void putDirectoryCorpusDestination(String destination) {
		try {
			semaphore.acquire();
			this.directoryStack.push(destination);
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Stack<String> stack = new Stack<String>();
		String directorySearch;
		System.out.println("Directory crawler started...");
		while(true) {
			try {
				directorySearch = null;
				
				semaphore.acquire();
				if ( !this.directoryStack.empty() ) {
					directorySearch = this.directoryStack.pop();
				}
				semaphore.release();
				
				if (directorySearch != null) {
					File dir = new File(directorySearch);
					if (dir.exists()) {
						
						for (File f: dir.listFiles()) {
							if(f.getName().startsWith(this.file_corpus_prefix)) {
								queue.put(new Task(Type.DIRECTORY, f.getName(), null));
							}else {
								stack.push(f.getName());
							}
						}
						
						while(!stack.empty()) {
							String fileName = stack.pop();
							File help = new File(fileName);
							for (File f: dir.listFiles()) {
								if(f.getName().startsWith(this.file_corpus_prefix)) {
									queue.put(new Task(Type.DIRECTORY, f.getName(), null));
								}else {
									stack.push(f.getName());
								}
							}
						}
					} 
					else {
						throw new CorpusDoesNotExist("Corpus does not exist");
					}
				}				
				
				Thread.sleep(this.sleep_time);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
