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
	private ValueFactory f;

	public CSV_To_Ontology_Location_Adapter() {
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		f = rep.getValueFactory();
	}

	public IRI writeLocationFile(String location_name,String location_id, int type, IRI country_param) {
		IRI location = f.createIRI(DOCTIME.LOCATION_NAMESPACE, location_id+".rdf");
		try (RepositoryConnection conn = rep.getConnection()) {
			if (type == 1) {
				conn.add(location, RDF.TYPE, DOCTIME.CITY);
				conn.add(location, DOCTIME.LOCATEDINSIDE, country_param);
			}
			else if(type == 2) {
				conn.add(location, RDF.TYPE, DOCTIME.COUNTRY);
			}
			conn.add(location, RDFS.LABEL, f.createLiteral(location_name));
			conn.add(location, DOCTIME.NAME, f.createLiteral(location_name));
			
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
			model.setNamespace("", DOCTIME.NAMESPACE);
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