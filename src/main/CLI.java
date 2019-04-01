package main;

//Author igor

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import components.WebScannerThreadPool;
import connections.DirectoryCrawler;
import connections.JobDispatcher;
import connections.JobQueue;
import connections.Task;

public class CLI {

	public static void main(String[] args) {
		File file = null;
		Scanner scanner = null;
		Scanner inputScanner = new Scanner(System.in);

		try {
			file = new File("app.properties");
			String key_words[] = null;
			String corpus_prefix = null;
			long crawler_sleep_time = 0;
			long file_size_limit = 0;
			long hop_count = 0;
			long refresh_time = 0;
			
			Map<String, Integer> dictionary = new HashMap<String, Integer>();
			dictionary.put("ad", 0);
			dictionary.put("aw", 1);
			dictionary.put("get", 2);
			dictionary.put("query", 3);
			dictionary.put("cws", 4);
			dictionary.put("cfs", 5);
			dictionary.put("stop", 6);

			scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
//			   System.out.println(scanner.nextLine());
				String tokens[] = scanner.nextLine().split("=");
				switch (tokens[0]) {
				case "keywords":
					key_words = tokens[1].split(",");
					break;
				case "file_corpus_prefix":
					corpus_prefix  = tokens[1];
					break;
				case "dir_crawler_sleep_time":
					crawler_sleep_time  = Integer.parseInt(tokens[1]);;
					break;
				case "file_scanning_size_limit":
					file_size_limit  = Integer.parseInt(tokens[1]);;
					break;
				case "hop_count":
					hop_count  = Integer.parseInt(tokens[1]);;
					break;
				case "url_refresh_time":
					refresh_time  = Integer.parseInt(tokens[1]);;
					break;
				}
			}
			System.out.println("Config file reading finished...");
			
			JobQueue<Task> jbQueue = new JobQueue<Task>(); 
			
			JobDispatcher jd = new JobDispatcher(jbQueue);
			Thread jobDispatcherThread = new Thread(jd);
			
			DirectoryCrawler dcThread = new DirectoryCrawler(crawler_sleep_time, corpus_prefix, jbQueue);
			Thread directoryCrawlerThread = new Thread(dcThread);
			
			//Thread pools
			WebScannerThreadPool wsPool = new WebScannerThreadPool();
			Thread webScannerThreadPoolThread = new Thread(wsPool);
			
			
			Thread fileScannerThreadPoolThread = new Thread();
			
			
			//Starting threads
			jobDispatcherThread.start();
			directoryCrawlerThread.start();
			webScannerThreadPoolThread.start();
			fileScannerThreadPoolThread.start();
			
			while(true) {
				String newTask = inputScanner.nextLine();
				if (dictionary.containsKey(newTask)) {
					switch (dictionary.get(newTask)) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					case 6:
						System.exit(0);
						break;
					default:
						break;
					}
				}
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
