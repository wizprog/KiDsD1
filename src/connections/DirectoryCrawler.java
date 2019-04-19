package connections;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import exceptions.CorpusDoesNotExist;
import exceptions.NonExistingURLOrDirectory;

public class DirectoryCrawler implements Runnable {

	long sleep_time;
	String file_corpus_prefix;
	JobQueue<Task> queue;
	Stack<String> directoryStack;
	String searchingWords[];
	private final Semaphore semaphore;
	private  Map<String, Long> lastModified;
	private long minimumFileSizeBatch;
	private boolean shutDown;

	public DirectoryCrawler(long sleep_time, String file_corpus_prefix, JobQueue<Task> queue, long minimumFileSizeBatch, String[] searchingWords) {
		super();
		this.sleep_time = sleep_time;
		this.file_corpus_prefix = file_corpus_prefix;
		this.queue = queue;
		this.directoryStack = new Stack<String>();
		semaphore = new Semaphore(1);
		lastModified = new HashMap<String, Long>();
		this.minimumFileSizeBatch = minimumFileSizeBatch;
		this.searchingWords = searchingWords;
		this.shutDown = false;
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
	
	public long directorySize(File directory) {
		 long length = 0;
		    for (File file : directory.listFiles()) {
		        if (file.isFile())
		            length += file.length();
		        else
		            length += directorySize(file);
		    }
		    return length;	
	}

	@Override
	public void run() {
		Stack<File> stack = new Stack<File>();
		Stack<File> taskDocuments = new Stack<File>();
		String directorySearch;
		
		long currentTaskSize = 0;
		System.out.println("Directory crawler started...");
		while(!this.shutDown) {
			try {
				directorySearch = null;
				currentTaskSize = 0;
				
				semaphore.acquire();
				if ( !this.directoryStack.empty() ) {
					directorySearch = this.directoryStack.pop();
				}
				semaphore.release();
				
				if (directorySearch != null) {
					File dir = new File(directorySearch);
					if (dir.exists()) {
						
						for (File f: dir.listFiles()) {
							
							//evaluating last modified
							long lastModNew = f.lastModified();
							long lastModOld = -1;
							if (lastModified.containsKey(f.getAbsolutePath())) lastModOld = lastModified.get(f.getAbsolutePath());
							else lastModified.put(f.getAbsolutePath(), f.lastModified());
							boolean createTask = (lastModOld < lastModNew);
							
							if(f.getName().startsWith(this.file_corpus_prefix) && createTask) {
								
								if (currentTaskSize >= minimumFileSizeBatch && currentTaskSize!=0) {
									queue.put(new Task(Type.DIRECTORY, taskDocuments, null, this.searchingWords));
								}
								else {
									currentTaskSize += directorySize(f);
									taskDocuments.push(f);	
								}
							}else {
								stack.push(f);
							}
						}
						
						//eventualy put everything in one while
						while(!stack.empty()) {
							File help =  stack.pop();
							for (File f: help.listFiles()) {
								//evaluating last modified
								long lastModNew = f.lastModified();
								long lastModOld = -1;
								if (lastModified.containsKey(f.getAbsolutePath())) lastModOld = lastModified.get(f.getAbsolutePath());
								else lastModified.put(f.getAbsolutePath(), f.lastModified());
								boolean createTask = (lastModOld < lastModNew);
								
								if(f.getName().startsWith(this.file_corpus_prefix) && createTask) {
									
									if (currentTaskSize >= minimumFileSizeBatch && currentTaskSize!=0) {
										queue.put(new Task(Type.DIRECTORY, taskDocuments, null, this.searchingWords));
									}
									else {
										currentTaskSize += directorySize(f);
										taskDocuments.push(f);	
									}
								}else {
									stack.push(f);
								}
							}
						}
						
						//if currentTaskSize < minimumFileSizeBatch and not 0
						if (currentTaskSize != 0) {
							queue.put(new Task(Type.DIRECTORY, taskDocuments, null, this.searchingWords));
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
		System.out.println("Directory Crawler ended");
	}

	public boolean isShutDown() {
		return shutDown;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}
	
	

}
