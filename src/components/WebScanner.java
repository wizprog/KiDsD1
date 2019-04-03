package components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import connections.JobQueue;
import connections.Task;

public class WebScanner implements Callable<Map<String,Integer>> {
	
	int hop_count;
	String url;
	JobQueue<Task> queue;
	String searchWords[];

	public WebScanner(int hop_count, String url, String[] searchWords) {
		super();
		this.hop_count = hop_count;
		this.url = url;
		this.searchWords = searchWords;
	}

	@Override
	public Map<String,Integer> call() {
		try {
			Document doc = Jsoup.connect(this.url).get();
			 Map<String,Integer> result = new HashMap<String,Integer>();
			 
			 for (String word: searchWords) {
				 result.put(word, 0);
			 }
			 
			if (this.hop_count > 0) {
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
				  String linkHref = link.attr("href");
	//			  this.queue.put(new Task(Type.WEB, this.hop_count-1, linkHref));
				}
			}
			Elements allElements = doc.getAllElements();
			for (Element link: allElements) {		
				if (link.hasText()) {
					String[] tokens = link.text().split("\\?|\\.|\\!|\\-|\\,|\\s+");
					for (String x: tokens) if (!x.isEmpty()) {
						if (result.containsKey(x)) result.put(x, result.get(x) + 1 );
					}
				}
			}
			
			return result;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean isUrl(String isUrl) {
		Pattern pattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
        Matcher matcher = pattern.matcher("http://naruto.rs");
        if(matcher.find()) return true;
        else return false;	
	}

}
