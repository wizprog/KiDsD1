package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import components.ResultRetrieverThreadPool;
import components.WebScannerThreadPool;
import connections.DirectoryCrawler;
import connections.JobDispatcher;
import connections.JobQueue;
import connections.Task;
import exceptions.NonExistingCommand;

//postrebase
public class CLI {
	
	public static String parseName(String query) {
		String name = query.substring(query.indexOf('|') + 1, query.length()-1);
		return name;
	}
	
	public static String parseType(String query) {
		String type = query.substring(0, query.indexOf('|'));
		return type;
	}

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
					file_size_limit  = Long.parseLong(tokens[1]);;
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
			
			DirectoryCrawler dcThread = new DirectoryCrawler(crawler_sleep_time, corpus_prefix, jbQueue, file_size_limit, key_words);
			Thread directoryCrawlerThread = new Thread(dcThread);
			
			//Thread pools
			ResultRetrieverThreadPool rrtp = new ResultRetrieverThreadPool();
			Thread resultRetrieverThreadPool = new Thread(rrtp);
			
			WebScannerThreadPool wsPool = new WebScannerThreadPool(rrtp);
			Thread webScannerThreadPoolThread = new Thread(wsPool);
			
			
			Thread fileScannerThreadPoolThread = new Thread();
			
			
			//Starting threads
			resultRetrieverThreadPool.start();
			directoryCrawlerThread.start();
			jobDispatcherThread.start();
			webScannerThreadPoolThread.start();
			fileScannerThreadPoolThread.start();
			
			while(true) {
				String newTask = inputScanner.nextLine();
				String tokens[] = newTask.split(" ");
				if (dictionary.containsKey(tokens[0])) {
					switch (dictionary.get(newTask)) {
					case 0:  //adding new directory to DirectoryCrawler
						dcThread.putDirectoryCorpusDestination(tokens[1]);
						break;
					case 1:  //add new Web page to WebScannerPool
//						wsPool.putTask(new Task(Type.WEB, tokens[1], hop_count, key_words));
						break;
					case 2:  //get result from Result retriever, blocking next request unitl it gets the result
						String name = parseName(tokens[1]);
						String type = parseType(tokens[1]);
						if (name.equals("summary")) {
							Map<String, Map<String, Integer>> result = rrtp.getSummary(type); 
							//treba ispisati
						}else {
							Map<String, Integer> result = rrtp.getResult(tokens[1]);
							//treba ispisati
						}
						break;
					case 3:
						String name1 = parseName(tokens[1]);
						String type1 = parseType(tokens[1]);
						if (name1.equals("summary")) {
							Map<String, Map<String, Integer>> result = rrtp.querySummary(type1); 
							//treba ispisati
						}else {
							Map<String, Integer> result = rrtp.queryResult(tokens[1]);
							//treba ispisati
						}						
						//  ,not blocking next request unitl it gets the result
						break;
					case 4:  //cws - clear web summary, remove from result retriever web search info
						rrtp.clearSummary("WEB");
						break;
					case 5:  //cfs - clear file summary, remove from result retriever file search info
						rrtp.clearSummary("FILE");
						break;
					case 6:  //stoping the system
						System.exit(0);
						//Should signal all pools to end, wait for them and then finish
						break;
					default:
						throw new NonExistingCommand("Not Existing Command");
					}
				}
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
