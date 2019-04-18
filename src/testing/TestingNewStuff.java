package testing;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestingNewStuff {

	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect("https://rti.etf.bg.ac.rs/rti/ir1p1/index.html").get();
			Elements links = doc.getElementsByTag("a");
			for (Element link : links) {
			  String linkHref = link.attr("href");
			  String linkText = link.text();
			  System.out.println(linkHref);
			  System.out.println(linkText);
			}
			
			System.out.println("---------------------------------------------");
			
			Elements allElements = doc.getAllElements();
			for (Element link: allElements) {		
				if (link.hasText()) {
					System.out.println("---------------");
					System.out.println(link.text());
					String[] tokens = link.text().split("\\?|\\.|\\!|\\-|\\,|\\s+");
					for (String x: tokens) if (!x.isEmpty()) System.out.println(x);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
