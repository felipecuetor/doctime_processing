package crawler;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Ceur_Crawler extends WebCrawler{

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String currentPage = referringPage.getWebURL().getURL();
		if (currentPage.equals("http://ceur-ws.org/")) {
			if (href.startsWith("http://ceur-ws.org/Vol")) {
				return true;
			}
			else {
				return false;
			}
		}
		return href.startsWith("http://ceur-ws.org/Vol");
	}
	
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String pureHtml = htmlParseData.getHtml();
		Document doc = Jsoup.parse(pureHtml);
		Element title = doc.select("span.CEURFULLTITLE").first();
		Element date = doc.select("span.CEURLOCTIME").first();
		Elements editor = doc.select("span.CEURVOLEDITOR");
		Element university = doc.select("h3").get(2);
		Elements university_List = university.getElementsByTag("a");
		/*for (int i = 0; i < university_List; i++) {
			
		}*/
		Element urn = doc.select("span.CEURURN").first();
		
		if (url.startsWith("http://ceur-ws.org/Vol")) {
			try {
				String path = "../data/ceur_output.csv";
				String str = title.text() + "\n";
				BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
				writer.append(' ');
				writer.append(str);
				writer.close();
//				System.out.println(tester);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	/*
	public void visit2(Page page, WebURL url, Page referringPage, String name, TextParseData parseData) {
		this.shouldVisit(referringPage, url);
		String tag = page.getWebURL().getTag();
		
		String attribute = page.getWebURL().getAttribute(name);
		if (attribute == "CEURFULLTITLE") {
			//String anchorTitle = page.getWebURL().getAnchor();
			String textTitle = parseData.getTextContent();
		}
		if (attribute == "CEURURN") {
			String textURN = parseData.getTextContent();			
		}
		if (attribute == "CEURLOCTIME") {
			//String anchorTime = page.getWebURL().getAnchor();
			//Revisar como separar lugar de fecha
			String textTime = parseData.getTextContent();
		}
		if (attribute == "CEURVOLEDITOR") {
			String anchorEditor = page.getWebURL().getAnchor();
			String editor = anchorEditor;
			//Revisar como ponerlo en un ciclo para varios autores
			String textEditor = parseData.getTextContent();
		}
		if (tag == "<a>") {
			String anchorUniversity = page.getWebURL().getAnchor();
			String university = anchorUniversity;
			//Revisar como ponerlo en un ciclo para varias universidades
		}		
		
	}*/

}
