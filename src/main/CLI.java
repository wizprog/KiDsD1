package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI {

	public static void main(String[] args) {
		File file = null;
		Scanner scanner = null;

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
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
