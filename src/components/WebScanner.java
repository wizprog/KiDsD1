package components;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScanner implements Callable<Map<String,Integer>> {
	
	int hop_count;
	String url;

	public WebScanner(int hop_count, String url) {
		super();
		this.hop_count = hop_count;
		this.url = url;
	}

	@Override
	public Map<String,Integer> call() {
		try {
			Document doc = Jsoup.connect(this.url).get();
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
