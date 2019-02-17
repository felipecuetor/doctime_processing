package crawler.doaj;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import utility.Simple_data_csv_adapter;

public class Doaj_Crawler extends WebCrawler {
	/**
	 * You should implement this function to specify whether the given url should be
	 * crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {

		String href = url.getURL().toLowerCase();
		String current_page = referringPage.getWebURL().getURL();
		HtmlParseData htmlParseData = (HtmlParseData) referringPage.getParseData();
		Map<String, String> metaDataMap = htmlParseData.getMetaTags();
		String pureHTML = htmlParseData.getHtml();
		Document doc = Jsoup.parse(pureHTML);
		if (current_page.startsWith("https://doaj.org/search")) {
			try {
				String path = "../data/doaj_output.csv";
				String str = pureHTML + "\n";
				BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
				writer.append(str);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (href.startsWith("https://doaj.org/article/")) {

				return true;
			}
		}
		return false;

	}

	/**
	 * This function is called when a page is fetched and ready to be processed by
	 * your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		Map<String, String> metaDataMap = htmlParseData.getMetaTags();
		String pureHTML = htmlParseData.getHtml();
		Document doc = Jsoup.parse(pureHTML);
		Simple_data_csv_adapter adapter = new Simple_data_csv_adapter();
		if (url.startsWith("https://doaj.org/article/")) {
			try {
				String path = "../data/doaj_output.csv";
				String str = url + "\n";
				BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
				writer.append(str);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
