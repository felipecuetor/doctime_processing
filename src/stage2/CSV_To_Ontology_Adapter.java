package stage2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.rdf4j.common.iteration.IterationSpliterator;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
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

public class CSV_To_Ontology_Adapter {

	private Repository rep;
	private String namespace;
	private ValueFactory f;
	// Classes
	private IRI document;
	private IRI author;
	private IRI context;
	private IRI journal;
	private IRI conference;
	// Properties
	private IRI writtenBy;
	private IRI propogatedBy;

	private IRI title;
	private IRI url;
	private IRI doi;
	private IRI publishingDate;
	private IRI name;
	private IRI subject;
	private IRI issn;
	private IRI isbn;

	private String document_id_hex;
	private HashMap<String, BNode> journal_map;

	public CSV_To_Ontology_Adapter() {
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		namespace = "172.24.101.57:80/";
		f = rep.getValueFactory();

		document_id_hex = "FFFFFFF";
		
		journal_map = new HashMap<String, BNode>();

		document = f.createIRI(namespace, "Document");
		author = f.createIRI(namespace, "Author");
		context = f.createIRI(namespace, "Context");
		journal = f.createIRI(namespace, "Journal");
		conference = f.createIRI(namespace, "Conference");

		writtenBy = f.createIRI(namespace, "writtenBy");
		propogatedBy = f.createIRI(namespace, "propogatedBy");

		title = f.createIRI(namespace, "title");
		url = f.createIRI(namespace, "url");
		doi = f.createIRI(namespace, "doi");
		publishingDate = f.createIRI(namespace, "publishingDate");
		name = f.createIRI(namespace, "name");
		subject = f.createIRI(namespace, "subject");
		issn = f.createIRI(namespace, "issn");
		isbn = f.createIRI(namespace, "isbn");

		try (RepositoryConnection conn = rep.getConnection()) {
			// Class and subclass definitions
			conn.add(document, RDF.TYPE, RDFS.CLASS);
			conn.add(author, RDF.TYPE, RDFS.CLASS);
			conn.add(context, RDF.TYPE, RDFS.CLASS);
			conn.add(journal, RDF.TYPE, RDFS.CLASS);
			conn.add(conference, RDF.TYPE, RDFS.CLASS);
			conn.add(journal, RDFS.SUBCLASSOF, context);
			conn.add(conference, RDFS.SUBCLASSOF, context);

			// Property, domain, range definition
			conn.add(writtenBy, RDF.TYPE, RDF.PROPERTY);
			conn.add(writtenBy, RDFS.DOMAIN, document);
			conn.add(writtenBy, RDFS.RANGE, author);
			conn.add(propogatedBy, RDF.TYPE, RDF.PROPERTY);
			conn.add(propogatedBy, RDFS.DOMAIN, document);
			conn.add(propogatedBy, RDFS.RANGE, context);
			
			conn.add(title, RDF.TYPE, RDF.PROPERTY);
			conn.add(url, RDF.TYPE, RDF.PROPERTY);
			conn.add(doi, RDF.TYPE, RDF.PROPERTY);
			conn.add(publishingDate, RDF.TYPE, RDF.PROPERTY);
			conn.add(name, RDF.TYPE, RDF.PROPERTY);
			conn.add(subject, RDF.TYPE, RDF.PROPERTY);
			conn.add(issn, RDF.TYPE, RDF.PROPERTY);
			conn.add(isbn, RDF.TYPE, RDF.PROPERTY);
		}

	}

	public IRI write_document_rdf(String title_param, String[] authors_param, String publishing_date_param,
			String doi_param, String url_param,String context_param) {
		try (RepositoryConnection conn = rep.getConnection()) {
			IRI current_document = f.createIRI(namespace, document_id_hex);
			int document_id_value = Integer.parseInt(document_id_hex, 16);
			document_id_value--;
			document_id_hex = Integer.toHexString(document_id_value);
			conn.add(current_document, RDF.TYPE, document);
			conn.add(current_document, RDFS.LABEL, f.createLiteral(title_param));
			conn.add(current_document, title, f.createLiteral(title_param));
			if(doi_param!=null)conn.add(current_document, doi, f.createLiteral(doi_param));
			conn.add(current_document, url, f.createLiteral(url_param));
			conn.add(current_document, publishingDate, f.createLiteral(publishing_date_param));

			// authors creation
			for (String current_author : authors_param) {
				BNode author_BNode = f.createBNode();
				conn.add(author_BNode, name, f.createLiteral(current_author));
				conn.add(author_BNode, RDF.TYPE, author);
				conn.add(current_document, writtenBy, author_BNode);
			}
			return current_document;
		}
	}


	public void end_document() {
		try (RepositoryConnection conn = rep.getConnection()) {
			RepositoryResult<Statement> statements = conn.getStatements(null, null, null);
			Model model = QueryResults.asModel(statements);
			model.setNamespace("rdf", RDF.NAMESPACE);
			model.setNamespace("rdfs", RDFS.NAMESPACE);
			model.setNamespace("owl", OWL.NAMESPACE);
			model.setNamespace("", namespace);
			File onto_file = new File("../data_final/document_ontology.owl");
			try {
				onto_file.createNewFile();
				FileWriter writer = new FileWriter(onto_file); 
				Rio.write(model, writer, RDFFormat.RDFXML);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	public void write_journal_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list, Object[] documents) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode journal_BNode = f.createBNode();
			for(String issn_id : issn_list) {
				conn.add(journal_BNode, issn, f.createLiteral(issn_id));
			}
			for(String isbn_id : isbn_list) {
				conn.add(journal_BNode, isbn, f.createLiteral((String)isbn_id));
			}
			for(Object current_document : documents) {
				conn.add((IRI)current_document, propogatedBy, journal_BNode);
			}
			conn.add(journal_BNode, RDF.TYPE, journal);
			conn.add(journal_BNode, subject, f.createLiteral(context_subject));
			conn.add(journal_BNode, url, f.createLiteral(url_link));
		}
	}

	public void write_conference_rdf(String url_link, String context_subject, String[] issn_list, String[] isbn_list, Object[] documents) {
		try (RepositoryConnection conn = rep.getConnection()) {
			BNode journal_BNode = f.createBNode();
			for(String issn_id : issn_list) {
				conn.add(journal_BNode, issn, f.createLiteral(issn_id));
			}
			for(String isbn_id : isbn_list) {
				conn.add(journal_BNode, isbn, f.createLiteral((String)isbn_id));
			}
			for(Object current_document : documents) {
				conn.add((IRI)current_document, propogatedBy, journal_BNode);
			}
			conn.add(journal_BNode, RDF.TYPE, conference);
			conn.add(journal_BNode, subject, f.createLiteral(context_subject));
			conn.add(journal_BNode, url, f.createLiteral(url_link));
		}
		
	}
}