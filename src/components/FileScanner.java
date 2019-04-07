package components;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Callable;

public class FileScanner implements Callable<Map<String, Map<String, Integer>>> {

	Stack<String> directoryUrls;
	String[] searchWords;

	public FileScanner(Stack<String> directoryUrls) {
		super();
		this.directoryUrls = directoryUrls;
	}

	@Override
	public Map<String, Map<String, Integer>> call() throws Exception {

		try {
			Map<String, Integer> result = new HashMap<String, Integer>();
			Map<String, Map<String, Integer>> finalResult = new HashMap<String, Map<String, Integer>>();
			
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			};

			for (String word : searchWords) {
				result.put(word, null);
			}

			String[] dirArr = this.directoryUrls.toArray(new String[this.directoryUrls.size()]);
			for (String directory : dirArr) { // da li moze ovako kroz Stack
												// proveriti
				File dir = new File(directory);
				File[] listOfFiles = dir.listFiles(filter);
				for (int i = 0; i < listOfFiles.length; i++) {
					File file = listOfFiles[i];
					Scanner sc = new Scanner(file);
					while (sc.hasNext()) {
						String link = sc.nextLine();
						String[] tokens = link.split("\\?|\\.|\\!|\\-|\\,|\\s+");
						for (String x : tokens)
							if (!x.isEmpty()) {
								if (result.containsKey(x))
									result.put(x, result.get(x) + 1);
									finalResult.put(directory, result);

							}
					}
				}
			}

			return finalResult;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
