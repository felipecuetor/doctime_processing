package crawler;

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

public class Dblp_Crawler extends WebCrawler {
	public int pos_current = 0;

	/**
	 * You should implement this function to specify whether the given url should be
	 * crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String current_page = referringPage.getWebURL().getURL();
		if (href.contains("prefix")) {
			return false;
		}
		if (current_page.contentEquals("https://dblp.org/db/conf/")
				| current_page.startsWith("https://dblp.org/db/conf/?pos=")) {
			if (href.startsWith("https://dblp.org/db/conf/") && href.length() > "https://dblp.org/db/conf/".length()) {
				if (href.contains("pos=")) {
					String[] query_split = href.split("\\?");
					String pos_number = query_split[1].replace("pos=", "");
					int pos_value = Integer.parseInt(pos_number);
					if (pos_value > pos_current) {
						pos_current = pos_value;
						return true;
					}
					return false;
				}
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
		if (url.startsWith("https://dblp.org/db/conf/") && url.length() > "https://dblp.org/db/conf/".length()) {
			if (!url.contains("pos=")) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				Map<String, String> metaDataMap = htmlParseData.getMetaTags();
				String pureHTML = htmlParseData.getHtml();
				Document doc = Jsoup.parse(pureHTML);
				Simple_data_csv_adapter adapter = new Simple_data_csv_adapter();
				try {
					String context = doc.select("h1").first().text();
					Elements event_list = doc.select("ul.publ-list");
					Iterator iter_events = event_list.iterator();
					while (iter_events.hasNext()) {
						Elements doc_list = ((Element) iter_events.next()).select("li");
						Iterator doc_iter = doc_list.iterator();
						while (doc_iter.hasNext()) {
							Element current_doc = (Element) doc_iter.next();
							Element data = current_doc.select("div.data").first();
							String title = adapter.clean(data.select("span.title").first().text());
							String date = "";
							try {
								date = adapter
										.clean(data.select("span[itemprop = datePublished]").first().text() + "/01/01");
							} catch (Exception e) {
								e.printStackTrace();
							}
							String external_reference = "idbn:"
									+ adapter.clean(data.select("span[itemprop = isbn]").first().text());
							String authors = "";
							Elements authors_list = data.select("span[itemprop = author]");
							Iterator author_iter = authors_list.iterator();
							while (author_iter.hasNext()) {
								String current_author = adapter.clean(((Element) author_iter.next()).text());
								authors += current_author;
							}
							String path = "../data/dblp_output.csv";
							String str = title + "," + authors + "," + date + "," + context + "," + external_reference
									+ "," + url + "\n";
							BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
							writer.append(str);
							writer.close();
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
