package connections;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public class JobQueue<T> {	
	
	private final BlockingQueue<T> queue;

	public JobQueue() {
		super();
		this.queue = new ArrayBlockingQueue<T>(100);
	}

	public JobQueue(BlockingQueue<T> queue) {
		super();
		this.queue = queue;
	}
	
	public void put(T t) {
		try {
			this.queue.put(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public T get() {
		try {
			return this.queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
/*	public Future<Map<String, Integer>> get(){
		
	}*/
	
	
}
