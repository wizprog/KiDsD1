package components;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScanner implements Callable<Map<String,Integer>> {
	
	int hop_count;

	public WebScanner(int hop_count) {
		super();
		this.hop_count = hop_count;
	}

	@Override
	public Map<String,Integer> call() throws Exception {
		//Scanner
		return null;
	}
	
	private boolean isUrl(String isUrl) {
		Pattern pattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
        Matcher matcher = pattern.matcher("http://naruto.rs");
        if(matcher.find()) return true;
        else return false;	
	}

}
