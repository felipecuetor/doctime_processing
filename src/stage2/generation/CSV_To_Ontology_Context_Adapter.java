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
	private String namespace;
	private IRI ontology_iri;
	private ValueFactory f;
	// Classes
	private IRI document;
	private IRI author;
	private IRI context;
	private IRI journal;
	private IRI conference;
	private IRI location;
	private IRI city;
	private IRI country;
	// Properties

	private IRI tookPlaceIn;
	private IRI publishedIn;
	private IRI locatedInside;

	private IRI writtenBy;
	private IRI propogatedBy;
	private IRI references;
	private IRI isPeer;
	private IRI titleRefrencesTopic;

	private IRI title;
	private IRI url;
	private IRI doi;
	private IRI publishingDate;
	private IRI name;
	private IRI subject;
	private IRI issn;
	private IRI isbn;

	private HashMap<String, BNode> journal_map;
	private HashMap<String, IRI> location_list;

	public CSV_To_Ontology_Context_Adapter() {
		location_list = new HashMap<String, IRI>();
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		namespace = "http://172.24.101.57:80/";
		// namespace = "http://www.grupo6.semanticweb.uniandes.edu.co/curso/doctime/";
		f = rep.getValueFactory();
		journal_map = new HashMap<String, BNode>();
		ontology_iri = f.createIRI(namespace);
		document = f.createIRI(namespace, "Document");
		author = f.createIRI(namespace, "Author");
		context = f.createIRI(namespace, "Context");
		journal = f.createIRI(namespace, "Journal");
		conference = f.createIRI(namespace, "Conference");

		location = f.createIRI(namespace, "Location");
		city = f.createIRI(namespace, "City");
		country = f.createIRI(namespace, "Country");

		tookPlaceIn = f.createIRI(namespace, "tookPlaceIn");
		publishedIn = f.createIRI(namespace, "publishedIn");
		locatedInside = f.createIRI(namespace, "locatedInside");

		writtenBy = f.createIRI(namespace, "writtenBy");
		propogatedBy = f.createIRI(namespace, "propogatedBy");
		references = f.createIRI(namespace, "references");
		isPeer = f.createIRI(namespace, "isPeer");
		titleRefrencesTopic = f.createIRI(namespace, "titleRefrencesTopic");

		title = f.createIRI(namespace, "title");
		url = f.createIRI(namespace, "url");
		doi = f.createIRI(namespace, "doi");
		publishingDate = f.createIRI(namespace, "publishingDate");
		name = f.createIRI(namespace, "name");
		subject = f.createIRI(namespace, "subject");
		issn = f.createIRI(namespace, "issn");
		isbn = f.createIRI(namespace, "isbn");
	}

	public void write_journal_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list,
			Object[] documents) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode journal_BNode = f.createBNode();
			for (String issn_id : issn_list) {
				try {
					conn.add(journal_BNode, issn, f.createLiteral(remove_string_inconsistencies(issn_id)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (String isbn_id : isbn_list) {
				try {
					conn.add(journal_BNode, isbn, f.createLiteral(remove_string_inconsistencies((String) isbn_id)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Object current_document : documents) {
				conn.add((IRI) current_document, propogatedBy, journal_BNode);
			}
			conn.add(journal_BNode, RDF.TYPE, journal);
			conn.add(journal_BNode, subject, f.createLiteral(context_subject));
			conn.add(journal_BNode, url, f.createLiteral(url_link));
		}
	}

	public void write_conference_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list,
			Object[] documents, String[] location) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode conference_BNode = f.createBNode();
			if (location[1] != null) {
				IRI countrySearch = location_list.get(location[1]);
				if (countrySearch != null) {
					conn.add(conference_BNode, tookPlaceIn, countrySearch);
				} else {
					CSV_To_Ontology_Location_Adapter location_adapter = new CSV_To_Ontology_Location_Adapter();
					IRI newCountry = location_adapter.writeLocationFile(location[1], location[1], 2, null);
					conn.add(conference_BNode, tookPlaceIn, newCountry);
					location_list.put(location[1], newCountry);
					location_adapter.end_document(location[1]);
					countrySearch = newCountry;
				}
				
				if (location[0] != null) {
					IRI citySearch = location_list.get(location[0] + "_" + location[1]);
					if (citySearch != null) {
						conn.add(conference_BNode, tookPlaceIn, citySearch);
					} else {
						CSV_To_Ontology_Location_Adapter location_adapter = new CSV_To_Ontology_Location_Adapter();
						IRI newCity = location_adapter.writeLocationFile(location[0], location[0] + "_" + location[1], 1, countrySearch);
						conn.add(conference_BNode, tookPlaceIn, newCity);
						location_list.put(location[0] + "_" + location[1], newCity);
						location_adapter.end_document(location[0] + "_" + location[1]);
					}
				}
			}

			for (String issn_id : issn_list) {
				conn.add(conference_BNode, issn, f.createLiteral(remove_string_inconsistencies(issn_id)));
			}
			for (String isbn_id : isbn_list) {
				conn.add(conference_BNode, isbn, f.createLiteral(remove_string_inconsistencies((String) isbn_id)));
			}
			for (Object current_document : documents) {
				conn.add((IRI) current_document, propogatedBy, conference_BNode);
			}
			conn.add(conference_BNode, RDF.TYPE, conference);
			conn.add(conference_BNode, subject, f.createLiteral(context_subject));
			conn.add(conference_BNode, url, f.createLiteral(url_link));

		}

	}
	
	public String remove_string_inconsistencies(String original) {
		original = Normalizer.normalize(original, Form.NFKC);
		original = original.replaceAll("[^\\p{ASCII}]", "");
		return original;
	}
	
	public void end_document(String document_name) {
		try (RepositoryConnection conn = rep.getConnection()) {
			RepositoryResult<Statement> statements = conn.getStatements(null, null, null);
			Model model = QueryResults.asModel(statements);
			model.setNamespace("rdf", RDF.NAMESPACE);
			model.setNamespace("rdfs", RDFS.NAMESPACE);
			model.setNamespace("owl", OWL.NAMESPACE);
			model.setNamespace("foaf", FOAF.NAMESPACE);
			String dbpedia_namespace = "http://dbpedia.org/ontology/";
			model.setNamespace("dbo", dbpedia_namespace);
			model.setNamespace("", namespace);
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