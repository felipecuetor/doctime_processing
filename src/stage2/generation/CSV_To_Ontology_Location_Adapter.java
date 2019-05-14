package stage2.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
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

public class CSV_To_Ontology_Location_Adapter {

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

	public CSV_To_Ontology_Location_Adapter() {
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		namespace = "http://172.24.101.57:80/";
		String ontology_structure_namespace = namespace+"ontology_description#";
		// namespace = "http://www.grupo6.semanticweb.uniandes.edu.co/curso/doctime/";
		f = rep.getValueFactory();
		journal_map = new HashMap<String, BNode>();
		ontology_iri = f.createIRI(namespace);
		document = f.createIRI(ontology_structure_namespace, "Document");
		author = f.createIRI(ontology_structure_namespace, "Author");
		context = f.createIRI(ontology_structure_namespace, "Context");
		journal = f.createIRI(ontology_structure_namespace, "Journal");
		conference = f.createIRI(ontology_structure_namespace, "Conference");

		location = f.createIRI(ontology_structure_namespace, "Location");
		city = f.createIRI(ontology_structure_namespace, "City");
		country = f.createIRI(ontology_structure_namespace, "Country");

		tookPlaceIn = f.createIRI(ontology_structure_namespace, "tookPlaceIn");
		publishedIn = f.createIRI(ontology_structure_namespace, "publishedIn");
		locatedInside = f.createIRI(ontology_structure_namespace, "locatedInside");

		writtenBy = f.createIRI(ontology_structure_namespace, "writtenBy");
		propogatedBy = f.createIRI(ontology_structure_namespace, "propogatedBy");
		references = f.createIRI(ontology_structure_namespace, "references");
		isPeer = f.createIRI(ontology_structure_namespace, "isPeer");
		titleRefrencesTopic = f.createIRI(ontology_structure_namespace, "titleRefrencesTopic");

		title = f.createIRI(ontology_structure_namespace, "title");
		url = f.createIRI(ontology_structure_namespace, "url");
		doi = f.createIRI(ontology_structure_namespace, "doi");
		publishingDate = f.createIRI(ontology_structure_namespace, "publishingDate");
		name = f.createIRI(ontology_structure_namespace, "name");
		subject = f.createIRI(ontology_structure_namespace, "subject");
		issn = f.createIRI(ontology_structure_namespace, "issn");
		isbn = f.createIRI(ontology_structure_namespace, "isbn");
	}

	public IRI writeLocationFile(String location_name,String location_id, int type, IRI country_param) {
		IRI location = f.createIRI(namespace, "locations/"+location_id+".rdf");
		try (RepositoryConnection conn = rep.getConnection()) {
			if (type == 1) {
				conn.add(location, RDF.TYPE, city);
				conn.add(location, locatedInside, country_param);
			}
			else if(type == 2) {
				conn.add(location, RDF.TYPE, country);
			}
			conn.add(location, RDFS.LABEL, f.createLiteral(location_name));
			conn.add(location, name, f.createLiteral(location_name));
			
			String dbpedia_resource_namespace = "http://dbpedia.org/resource/";
			conn.add(location, OWL.SAMEAS, f.createIRI(dbpedia_resource_namespace, location_name));
		}
		return location;
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
			model.setNamespace("", namespace);
			// File onto_file = new File("../" + document_name);
			File onto_file = new File("../data_web_locations/"+document_name);
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