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
import exceptions.NonExistingCommand;

//postrebase
public class CLI {
	
	public static Map<String, Integer> dictionary;
	public static DirectoryCrawler dcThread;
	public static FileScannerThreadPool fstp;
	public static ResultRetrieverThreadPool rrtp;
	public static WebScannerThreadPool wstp;
	public static String key_words[];
	
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
			key_words = null;
			String corpus_prefix = null;
			long crawler_sleep_time = 0;
			long file_size_limit = 0;
			long hop_count = 0;
			long refresh_time = 0;
			
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
			
			JobQueue<Task> jbQueue = new JobQueue<Task>(); 
			
			JobDispatcher jd = new JobDispatcher(jbQueue);
			Thread jobDispatcherThread = new Thread(jd);
			
			dcThread = new DirectoryCrawler(crawler_sleep_time, corpus_prefix, jbQueue, file_size_limit, key_words);
			Thread directoryCrawlerThread = new Thread(dcThread);
			
			ResultRetrieverThreadPool rrtp = new ResultRetrieverThreadPool();
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
					case 0:  //adding new directory to DirectoryCrawler
						dcThread.putDirectoryCorpusDestination(tokens[1]);
						break;
					case 1:  //add new Web page to WebScannerPool
//						jbQueue.put(new Task(Type.WEB, tokens[1], hop_count, key_words));
						break;
					case 2:  //get result from Result retriever, blocking next request unitl it gets the result
						String name = parseName(tokens[1]);
						String type = parseType(tokens[1]);
						if (name.equals("summary")) {
					//		Map<String, Map<String, Integer>> result = rrtp.getSummary(type); 
							//treba ispisati
						}else {
					//		Map<String, Integer> result = rrtp.getResult(tokens[1]);
							//treba ispisati
						}
						break;
					case 3:
						String name1 = parseName(tokens[1]);
						String type1 = parseType(tokens[1]);
						if (name1.equals("summary")) {
					//		Map<String, Map<String, Integer>> result = rrtp.querySummary(type1); 
							//treba ispisati
						}else {
					//		Map<String, Integer> result = rrtp.queryResult(tokens[1]);
							//treba ispisati
						}						
						//  ,not blocking next request unitl it gets the result
						break;
					case 4:  //cws - clear web summary
						rrtp.clearSummary("WEB");
						break;
					case 5:  //cfs - clear file summary
						rrtp.clearSummary("FILE");
						break;
					case 6:  //stop - stopping the system
						fstp.stop();
						wstp.stop();
						rrtp.stop();
						System.exit(0);
						break;
					default:
						throw new NonExistingCommand("Not Existing Command");
					}
				}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

	}

}
