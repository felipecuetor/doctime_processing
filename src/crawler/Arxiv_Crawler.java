package crawler;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
//import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Arxiv_Crawler extends WebCrawler {

	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

	/**
	 * You should implement this function to specify whether the given url should be
	 * crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String current_page = referringPage.getWebURL().getURL();
		if(current_page.equals("https://arxiv.org/")) {
			if (href.startsWith("https://arxiv.org/archive/")) {
				return true;
			}
			else {
				return false;
			}
		}

		if (href.startsWith("https://arxiv.org/year/")) {
			return true;
		}
		if (href.startsWith("https://arxiv.org/list/")) {
			if(href.endsWith("/recent")|href.endsWith("/current")|href.endsWith("/new")) {
				return false;
			}
			else {
				return true;
			}
		}
//		if (href.startsWith("https://arxiv.org/abs/")) {
//			return true;
//		}
		return href.startsWith("https://arxiv.org/archive/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed by
	 * your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		try {
			String path = "../data/arxiv_output.csv";
		    String str = url+"\n";
		    BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
		    writer.append(' ');
		    writer.append(str);
		    writer.close();
		    System.out.println(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("URL: {}", url);
	}
}
