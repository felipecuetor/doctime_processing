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

public class CSV_To_Total_Ontology_Adapter {

	private Repository rep;
	private ValueFactory f;

	private HashMap<String, BNode> journal_map;
	private HashMap<String, IRI> location_list;


	public CSV_To_Total_Ontology_Adapter() {
		location_list = new HashMap<String, IRI>();
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		// namespace = "http://www.grupo6.semanticweb.uniandes.edu.co/curso/doctime/";
		f = rep.getValueFactory();
		journal_map = new HashMap<String, BNode>();
	}

	public IRI write_document_rdf(String document_id_hex, String title_param, String[] authors_param,
			String publishing_date_param, String doi_param, String url_param, String context_param) {
		try (RepositoryConnection conn = rep.getConnection()) {
			IRI current_document = f.createIRI(DOCTIME.DOCUMENT_NAMESPACE, document_id_hex+".rdf");

			conn.add(current_document, RDF.TYPE, DOCTIME.DOCUMENT);
			conn.add(current_document, RDFS.LABEL, f.createLiteral(title_param));
			conn.add(current_document, DOCTIME.TITLE, f.createLiteral(title_param));
			if (doi_param != null)
				conn.add(current_document, DOCTIME.DOI, f.createLiteral(doi_param));
			conn.add(current_document, DOCTIME.URL, f.createLiteral(url_param));
			// conn.add(current_document, publishingDate,
			// f.createLiteral(publishing_date_param));
			try {
				Date date = new Date(publishing_date_param);
				conn.add(current_document, DOCTIME.PUBLISHINGDATE, f.createLiteral(date));
			} catch (Exception e) {
				conn.add(current_document, DOCTIME.PUBLISHINGDATE, f.createLiteral(publishing_date_param));
			}

			// authors creation
			for (String current_author : authors_param) {
				BNode author_BNode = f.createBNode();
				conn.add(author_BNode, DOCTIME.NAME, f.createLiteral(current_author));
				conn.add(author_BNode, RDF.TYPE, DOCTIME.AUTHOR);
				conn.add(current_document, DOCTIME.WRITTENBY, author_BNode);
			}
			return current_document;
		}
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
			String dbpedia_resource_namespace = "http://dbpedia.org/resource/";
			model.setNamespace("dbr", dbpedia_resource_namespace);
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