package components;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Callable;

import main.CLI;

public class FileScanner implements Callable<Map<String, Map<String, Integer>>> {

	Stack<File> directoryUrls;
	String[] searchWords;

	public FileScanner(Stack<File> directoryUrls) {
		super();
		this.directoryUrls = directoryUrls;
		this.searchWords = CLI.key_words;
	}

	@Override
	public Map<String, Map<String, Integer>> call() throws Exception {

		try {
			CLI.fstp.taskStarted(directoryUrls);
			Map<String, Integer> result = new HashMap<String, Integer>();
			Map<String, Map<String, Integer>> finalResult = new HashMap<String, Map<String, Integer>>();
			
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			};

			for (String word : searchWords) {
				result.put(word, 0);
			}

			File[] dirArr = this.directoryUrls.toArray(new File[this.directoryUrls.size()]);
			for (File directory : dirArr) { // da li moze ovako kroz Stack
												// proveriti
				File[] listOfFiles = directory.listFiles(filter);
				for (int i = 0; i < listOfFiles.length; i++) {
					File file = listOfFiles[i];
					Scanner sc = new Scanner(file);
					while (sc.hasNext()) {
						String link = sc.nextLine();
						String[] tokens = link.split("\\?|\\.|\\!|\\-|\\,|\\s+");
						for (String x : tokens)
							if (!x.isEmpty()) {
								if (result.containsKey(x)) {
									int help_count = result.get(x);
									result.put(x, help_count + 1);
								}
						}
					}
				}
				finalResult.put(directory.getAbsolutePath(), result);
			}
			CLI.fstp.taskEnded(directoryUrls);
			return finalResult;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
