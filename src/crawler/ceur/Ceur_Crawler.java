package crawler.ceur;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import utility.Simple_data_csv_adapter;

public class Ceur_Crawler extends WebCrawler {

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String currentPage = referringPage.getWebURL().getURL();
		if (currentPage.equals("http://ceur-ws.org/")) {
			System.out.println(href);
			if (href.startsWith("http://ceur-ws.org/vol")) {

				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void visit(Page page) {
		Simple_data_csv_adapter adapter = new Simple_data_csv_adapter();
		String url = page.getWebURL().getURL();
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String pureHtml = htmlParseData.getHtml();
		Document doc = Jsoup.parse(pureHtml);

		if (url.startsWith("http://ceur-ws.org/Vol")) {
			String context = doc.select("span.CEURFULLTITLE").first().text();
			Element date = doc.select("span.CEURLOCTIME").first();
			// Elements editor = doc.select("span.CEURVOLEDITOR");
			String urn ="";
			try {
				urn = "urn:" + adapter.clean(doc.select("span.CEURURN").first().text());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Element papers = doc.select("div.CEURTOC").first();
			Elements lists_ol = papers.select("ol");
			Elements lists_ul = papers.select("ul");
			lists_ol.addAll(lists_ul);
			// lists_ol.size();
			String authors = "";
			String splitDate = date.text();
			String[] parts = splitDate.split(" ");
			String year = parts[parts.length - 1].substring(0, 4);
			String day = parts[parts.length - 2].substring(0, 2);
			String month = "01";
			try {
				int day_int = Integer.parseInt(day);
				month = decipher_months(parts[parts.length - 3]);	
			} catch (Exception e) {
				day = "01";
				e.printStackTrace();
			}
			
			String correct_date = year + "/" + month + "/" + day;

			for (int i = 0; i < lists_ol.size(); i++) {
				Element current_list = lists_ol.get(i);
				Elements lists_li = current_list.select("li");
				for (int j = 0; j < lists_li.size(); j++) {
					Element current_doc = lists_li.get(j);
					Element title = current_doc.select("span.CEURTITLE").first();
					Elements authors_list = current_doc.select("span.CEURAUTHOR");
					authors = "";
					for (int k = 0; k < authors_list.size(); k++) {
						authors += adapter.clean(authors_list.get(k).text())+";";
					}
					authors = authors.substring(0, authors.length()-1);

					try {
						String path = "../data/ceur_output.csv";
						// String fullStr = title.text() + "," + authors + "," + correct_date + "," +
						// context.text() + "," + urn.text()
						// + "," + url + "\n";
						String str = adapter.clean(title.text()) + "," + authors + "," + correct_date + "," + adapter.clean(context) + "," + urn + ","
								+ adapter.clean(url) + "\n";
						BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
						writer.append(str);
						writer.close();
//						System.out.println(tester);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	public String decipher_months(String month) throws Exception {

		if (month.equals("January")) {
			String month_number = "01";
			return month_number;
		}
		if (month.equals("February")) {
			String month_number = "02";
			return month_number;
		}
		if (month.equals("March")) {
			String month_number = "03";
			return month_number;
		}
		if (month.equals("April")) {
			String month_number = "04";
			return month_number;
		}
		if (month.equals("May")) {
			String month_number = "05";
			return month_number;
		}
		if (month.equals("June")) {
			String month_number = "06";
			return month_number;
		}
		if (month.equals("July")) {
			String month_number = "07";
			return month_number;
		}
		if (month.equals("August")) {
			String month_number = "08";
			return month_number;
		}
		if (month.equals("September")) {
			String month_number = "09";
			return month_number;
		}
		if (month.equals("October")) {
			String month_number = "10";
			return month_number;
		}
		if (month.equals("November")) {
			String month_number = "11";
			return month_number;
		}
		if (month.equals("December")) {
			String month_number = "12";
			return month_number;
		}
		throw new Exception("Date wrong format");

	}

}
