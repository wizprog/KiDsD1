package components;

import java.util.Map;
import java.util.concurrent.Callable;

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

}
