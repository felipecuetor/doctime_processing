package stage2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;

public class CSV_To_Ontology_Controller {
	private HashMap<String,Object[]> journal_list;
	private HashMap<String,Object[]> conference_list;
	private CSV_To_Ontology_Adapter csv_ontology_adapter;
	
	
	public static void main(String[] args) {
		CSV_To_Ontology_Controller controller = new CSV_To_Ontology_Controller();
		controller.begin_ontology_generation();
	}
	
	public CSV_To_Ontology_Controller() {
		csv_ontology_adapter = new CSV_To_Ontology_Adapter();
		journal_list = new HashMap<String,Object[]>();
		conference_list = new HashMap<String,Object[]>();
	}
	
	public void begin_ontology_generation() {
		System.out.println("Starting csv to ontology converter...");
		try (BufferedReader br = new BufferedReader(new FileReader("../data_final/unified_doaj_arxiv_ceur_dblp.csv"))) {
			String columns = br.readLine();
			String row = "";
			System.out.println("Processing documents...");
			while ((row = br.readLine()) != null) {
				process_document(row);
			}
			System.out.println("Processing Journals and Conferences...");
			process_journals();
			process_conferences();
			System.out.println("Printing Ontology to /data_final/document_ontology.owl...");
			csv_ontology_adapter.end_document();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void process_document(String row) {
		String[] split_columns = row.split(",");
		String title = split_columns[0];
		String[] authors = split_columns[1].split(";");
		String publishing_date = split_columns[2];
		String context = split_columns[3];
		String[] external_connections = split_columns[4].split(";");
		String doi = null;
		ArrayList<String>  issn_list = new ArrayList<String>();
		ArrayList<String> isbn_list = new ArrayList<String>();
		for(String element_org : external_connections) {
			String element = element_org.replace(" ", "");
			if(element.startsWith("issn")) issn_list.add(element);
			if(element.startsWith("isbn") | element.startsWith("idbn")) isbn_list.add(element);
			if(element.startsWith("doi")) doi = element;
		}
		String[] issn_array = Arrays.copyOf(issn_list.toArray(), issn_list.toArray().length, String[].class);
		String[] isbn_array = Arrays.copyOf(issn_list.toArray(), issn_list.toArray().length, String[].class);
		String url = split_columns[5];
		IRI document_iri = csv_ontology_adapter.write_document_rdf(title, authors, publishing_date, doi, url, context);
		
		//TODO
		//issn and isbn analisis
		Object[] element_array = new Object[]{url, context, issn_array, isbn_array, new ArrayList<Object>()};
		((ArrayList<Object>) element_array[4]).add(document_iri);
		if(url.startsWith("https://dblp.org/db/conf/" )|url.startsWith("http://ceur-ws.org/")) {
			Object[] conference_lookup = (Object[]) conference_list.get(url);
			if(conference_lookup == null) conference_list.put(url, element_array);
			else ((ArrayList<Object>) conference_lookup[4]).add(document_iri);
		}
		else {
			Object[] journal_lookup = (Object[]) journal_list.get(url);
			if(journal_lookup == null) journal_list.put(url, element_array);
			else ((ArrayList<Object>) journal_lookup[4]).add(document_iri);
		}
	}
	
	public void process_journals() {
		Set map_set = journal_list.entrySet();
		Iterator map_iterator = map_set.iterator();
		while(map_iterator.hasNext()) {
			Object[] current_journal = (Object[]) (((HashMap.Entry) map_iterator.next()).getValue());
			csv_ontology_adapter.write_journal_rdf((String)current_journal[0],(String)current_journal[1],(String[])current_journal[2],(String[])current_journal[3],(Object[])(((ArrayList) current_journal[4]).toArray()));
		}
	}
	
	public void process_conferences() {
		Set map_set = conference_list.entrySet();
		Iterator map_iterator = map_set.iterator();
		while(map_iterator.hasNext()) {
			Object[] current_conference = (Object[]) (((HashMap.Entry) map_iterator.next()).getValue());
			csv_ontology_adapter.write_journal_rdf((String)current_conference[0],(String)current_conference[1],(String[])current_conference[2],(String[])current_conference[3],(Object[])(((ArrayList) current_conference[4]).toArray()));
		}
	}
}
