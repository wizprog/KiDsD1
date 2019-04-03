package components;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import interfaces.ResultInterface;

public class ResultRetriever implements Runnable {

	Map<String, Map<String, Integer>> resDict;
	BlockingQueue<Future<Map<String,Integer>>> queue;	
	
	public ResultRetriever(BlockingQueue<Future<Map<String,Integer>>> queue) {
		this.queue = queue;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Future<Map<String,Integer>> resultTask = this.queue.take();
				Map<String,Integer> result = resultTask.get();
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
