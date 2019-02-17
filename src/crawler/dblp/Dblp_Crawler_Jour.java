package crawler.dblp;

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

public class Dblp_Crawler_Jour extends WebCrawler {
	public int pos_current = 3020;

	/**
	 * You should implement this function to specify whether the given url should be
	 * crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String current_page = referringPage.getWebURL().getURL();
		int sub_dir_count = href.length() - href.replace("/", "").length();
		int sub_dir_count_current = current_page.length() - current_page.replace("/", "").length();
		if (href.contains("prefix")|sub_dir_count>6|href.contains("index.html")|href.endsWith("index")|href.endsWith("index/")) {
			return false;
		}
		if (current_page.contentEquals("https://dblp.org/db/journals/")
				| current_page.startsWith("https://dblp.org/db/journals/?pos=")) {
			if (href.startsWith("https://dblp.org/db/journals/")
					&& href.length() > "https://dblp.org/db/journals/".length()) {
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
				if(sub_dir_count==6 && href.endsWith("/")) {
					return true;
				}
				return false;
			}
		}
		if(current_page.endsWith("/")&&sub_dir_count_current==6) {
			if(href.startsWith("https://dblp.org/db/journals/")&&sub_dir_count==6) {
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
		int sub_dir_count = url.length() - url.replace("/", "").length();
		if (url.startsWith("https://dblp.org/db/journals/") && sub_dir_count == 6 && url.endsWith("html")) {
			if (!url.contains("pos=")) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				Map<String, String> metaDataMap = htmlParseData.getMetaTags();
				String pureHTML = htmlParseData.getHtml();
				Document doc = Jsoup.parse(pureHTML);
				Simple_data_csv_adapter adapter = new Simple_data_csv_adapter();
				try {
					String context = adapter.clean(doc.select("h1").first().text());
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
							String external_reference = "";
							try {
								external_reference = "idbn:"
										+ adapter.clean(data.select("span[itemprop = isbn]").first().text());
							} catch (Exception e) {
								e.printStackTrace();
							}
							String authors = "";
							Elements authors_list = data.select("span[itemprop = author]");
							Iterator author_iter = authors_list.iterator();
							while (author_iter.hasNext()) {
								String current_author = adapter.clean(((Element) author_iter.next()).text());
								authors += current_author+";";
							}
							String path = "../data/dblp_output_jour.csv";
							String str = title + "," + authors + "," + date + "," + context + "," + external_reference
									+ "," + adapter.clean(url) + "\n";
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
