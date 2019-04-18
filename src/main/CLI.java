package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import components.ClearThread;
import components.FileScannerThreadPool;
import components.ResultRetrieverThreadPool;
import components.WebScannerThreadPool;
import connections.DirectoryCrawler;
import connections.JobDispatcher;
import connections.JobQueue;
import connections.Task;
import connections.Type;
import exceptions.NonExistingCommand;

//postrebase
public class CLI {
	
	public static Map<String, Integer> dictionary;
	public static DirectoryCrawler dcThread;
	public static FileScannerThreadPool fstp;
	public static ResultRetrieverThreadPool rrtp;
	public static WebScannerThreadPool wstp;
	public static String key_words[];
	public static JobDispatcher jd;
	public static JobQueue<Task> jbQueue;
	
	public static String corpus_prefix;
	public static long crawler_sleep_time;
	public static long file_size_limit;
	public static Integer hop_count;
	public static long refresh_time;
	
	public static String parseName(String query) {
		String name = query.substring(query.indexOf('|') + 1, query.length());
		return name;
	}
	
	public static String parseType(String query) {
		String type = query.substring(0, query.indexOf('|'));
		return type;
	}
	
	public static void printSummaryMap(Map<String, Map<String, Integer>> result) {
		for (Map.Entry<String, Map<String, Integer>> entry : result.entrySet()) {
		    String key = entry.getKey();
		    Map<String, Integer> helpMap = entry.getValue();
		    System.out.println(key + ":");
		    for (Map.Entry<String, Integer> entry2 : helpMap.entrySet()) {
		    	System.out.println(entry2.getKey() + ": " + entry.getValue().get(entry2.getKey()));
		    }
		} 
	}
	
	public static void printSimpleMap(Map<String, Integer> result) {
		for (Map.Entry<String, Integer> entry : result.entrySet()) {
	    	System.out.println(entry.getKey() + ": " + entry.getValue());
	    }
	}

	public static void main(String[] args) {
		File file = null;
		Scanner scanner = null;
		Scanner inputScanner = new Scanner(System.in);

		try {
			file = new File("app.properties");
			key_words = null;
			corpus_prefix = null;
			crawler_sleep_time = 0;
			file_size_limit = 0;
			hop_count = 0;
			refresh_time = 0;
			
			dictionary = new HashMap<String, Integer>();
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
			
			
			//Thread and thread pool creation
			jbQueue = new JobQueue<Task>(); 
			
			jd = new JobDispatcher(jbQueue);
			Thread jobDispatcherThread = new Thread(jd);
			
			dcThread = new DirectoryCrawler(crawler_sleep_time, corpus_prefix, jbQueue, file_size_limit, key_words);
			Thread directoryCrawlerThread = new Thread(dcThread);
			
			rrtp = new ResultRetrieverThreadPool();
			Thread resultRetrieverThreadPool = new Thread(rrtp);
			
			fstp = new FileScannerThreadPool(rrtp);
			Thread fileScannerThreadPoolThread = new Thread(fstp);
			
			wstp = new WebScannerThreadPool(rrtp);
			Thread webScannerThreadPoolThread = new Thread(wstp);
			
	/*		//Thread pools
								
			ClearThread clThread = new ClearThread(refresh_time, rrtp);
			*/
			
			//Starting threads
			resultRetrieverThreadPool.start();
			directoryCrawlerThread.start();
			jobDispatcherThread.start();
			fileScannerThreadPoolThread.start();
			webScannerThreadPoolThread.start();
			Thread.sleep(500);  // just to be sure
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
			while(true) {
				try {
				System.out.println("Input command: ");
				String newTask = inputScanner.nextLine();
				String tokens[] = newTask.split(" ");
				if (dictionary.containsKey(tokens[0])) {
					switch (dictionary.get(tokens[0])) {
					case 0:  //ad - adding new directory to DirectoryCrawler
						dcThread.putDirectoryCorpusDestination(tokens[1]);
						break;
						
					case 1:  //aw - add new Web page to WebScannerPool
						jbQueue.put(new Task(Type.WEB, tokens[1], hop_count));
						break;
						
					case 2:  //get - get result from Result retriever, blocking next request unitl it gets the result
						String name = parseName(tokens[1]);
						String type = parseType(tokens[1]);
						if (!type.equals("web") && !type.equals("file")) throw new NonExistingCommand("Not Existing Command");
						if (name.equals("summary")) {
							Map<String, Map<String, Integer>> result = rrtp.getSummary(type); 
							printSummaryMap(result);
						}else {
							Map<String, Integer> result = rrtp.getResult(tokens[1]);
							printSimpleMap(result);
						}
						break;
						
					case 3: // query - get result from Result retriever, not blocking next request, just show the info if exists
						String name1 = parseName(tokens[1]);
						String type1 = parseType(tokens[1]);
						if (!type1.equals("web") && !type1.equals("file")) throw new NonExistingCommand("Not Existing Command");
						if (name1.equals("summary")) {
							Map<String, Map<String, Integer>> result = rrtp.querySummary(type1); 
							if (result == null) break;
							printSummaryMap(result);
						}else {
							Map<String, Integer> result = rrtp.queryResult(tokens[1]);
							if (result == null) break;
							System.out.println(type1 + ":");
							printSimpleMap(result);
						}						
						break;
						
					case 4:  //cws - clear web summary
						rrtp.clearSummary(Type.WEB);
						break;
						
					case 5:  //cfs - clear file summary
						rrtp.clearSummary(Type.DIRECTORY);
						break;
						
					case 6:  //stop - stopping the system
						fstp.stop();
						wstp.stop();
						rrtp.stop();
						dcThread.setShutDown(true);
						jd.setShutdown(true);
						System.exit(0);
						break;
					default:
						throw new NonExistingCommand("Not Existing Command");
					}
				}else {
					throw new NonExistingCommand("Not Existing Command");
				}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

	}

}
