package stage2.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.rdf4j.common.iteration.IterationSpliterator;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class CSV_To_Ontology_Context_Adapter {

	private Repository rep;
	private ValueFactory f;
	private HashMap<String, IRI> location_list;

	private CSV_To_Ontology_Location_Adapter accum_location_adapter;
	
	public void init_location_adapter() {
		accum_location_adapter = new CSV_To_Ontology_Location_Adapter();
	}
	
	public CSV_To_Ontology_Context_Adapter() {
		accum_location_adapter = null;
		location_list = new HashMap<String, IRI>();
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		// namespace = "http://www.grupo6.semanticweb.uniandes.edu.co/curso/doctime/";
		f = rep.getValueFactory();
	}

	public void write_journal_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list,
			Object[] documents) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode journal_BNode = f.createBNode();
			for (String issn_id : issn_list) {
				try {
					conn.add(journal_BNode, DOCTIME.ISSN, f.createLiteral(remove_string_inconsistencies(issn_id)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (String isbn_id : isbn_list) {
				try {
					conn.add(journal_BNode, DOCTIME.ISBN, f.createLiteral(remove_string_inconsistencies((String) isbn_id)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Object current_document : documents) {
				conn.add((IRI) current_document, DOCTIME.PROPOGATEDBY, journal_BNode);
			}
			conn.add(journal_BNode, RDF.TYPE, DOCTIME.JOURNAL);
			conn.add(journal_BNode, DOCTIME.SUBJECT, f.createLiteral(context_subject));
			conn.add(journal_BNode, DOCTIME.URL, f.createLiteral(url_link));
		}
	}

	public String remove_string_inconsistencies(String original) {
		original = Normalizer.normalize(original, Form.NFKC);
		original = original.replaceAll("[^\\p{ASCII}]", "");
		return original;
	}

	public void write_conference_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list,
			Object[] documents, String[] location) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode conference_BNode = f.createBNode();
			try {
				if (location[1] != null) {
					IRI countrySearch = location_list.get(location[1]);
					if (countrySearch != null) {
						conn.add(conference_BNode, DOCTIME.TOOKPLACEIN, countrySearch);
					} else {
						CSV_To_Ontology_Location_Adapter location_adapter = new CSV_To_Ontology_Location_Adapter();
						accum_location_adapter.writeLocationFile(location[1], location[1], 2, null);
						IRI newCountry = location_adapter.writeLocationFile(location[1], location[1], 2, null);
						conn.add(conference_BNode, DOCTIME.TOOKPLACEIN, newCountry);
						location_list.put(location[1], newCountry);
						location_adapter.end_document(location[1]);
						countrySearch = newCountry;
					}

					if (location[0] != null) {
						IRI citySearch = location_list.get(location[0] + "_" + location[1]);
						if (citySearch != null) {
							conn.add(conference_BNode, DOCTIME.TOOKPLACEIN, citySearch);
						} else {
							CSV_To_Ontology_Location_Adapter location_adapter = new CSV_To_Ontology_Location_Adapter();
							accum_location_adapter.writeLocationFile(location[0], location[0] + "_" + location[1], 1,
									countrySearch);
							IRI newCity = location_adapter.writeLocationFile(location[0],
									location[0] + "_" + location[1], 1, countrySearch);
							conn.add(conference_BNode, DOCTIME.TOOKPLACEIN, newCity);
							location_list.put(location[0] + "_" + location[1], newCity);
							location_adapter.end_document(location[0] + "_" + location[1]);
						}
					}
				}

				for (String issn_id : issn_list) {
					conn.add(conference_BNode, DOCTIME.ISSN, f.createLiteral(remove_string_inconsistencies(issn_id)));
				}
				for (String isbn_id : isbn_list) {
					conn.add(conference_BNode, DOCTIME.ISBN, f.createLiteral(remove_string_inconsistencies((String) isbn_id)));
				}
				for (Object current_document : documents) {
					conn.add((IRI) current_document, DOCTIME.PROPOGATEDBY, conference_BNode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.add(conference_BNode, RDF.TYPE, DOCTIME.CONFERENCE);
			conn.add(conference_BNode, DOCTIME.SUBJECT, f.createLiteral(context_subject));
			conn.add(conference_BNode, DOCTIME.URL, f.createLiteral(url_link));

		}

	}
	
	public void end_document(String document_name) {
		
		if (accum_location_adapter != null) {
			accum_location_adapter.end_document("../data_web_few_files/all_locations");
		}
		try (RepositoryConnection conn = rep.getConnection()) {
			RepositoryResult<Statement> statements = conn.getStatements(null, null, null);
			Model model = QueryResults.asModel(statements);
			model.setNamespace("rdf", RDF.NAMESPACE);
			model.setNamespace("rdfs", RDFS.NAMESPACE);
			model.setNamespace("owl", OWL.NAMESPACE);
			model.setNamespace("foaf", FOAF.NAMESPACE);
			String dbpedia_namespace = "http://dbpedia.org/ontology/";
			model.setNamespace("dbo", dbpedia_namespace);
			model.setNamespace("", DOCTIME.NAMESPACE);
			// File onto_file = new File("../" + document_name);
			File onto_file = new File(document_name);
			try {
				onto_file.createNewFile();
				FileWriter writer = new FileWriter(onto_file);
				Rio.write(model, writer, RDFFormat.RDFXML);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}