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
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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

public class DOCTIME {

	private Repository rep;
	public final static String NAMESPACE;
	public final static String DESCRIPTION_NAMESPACE;
	public final static String LOCATION_NAMESPACE;
	public final static String DOCUMENT_NAMESPACE;
	public final static String CONFERENCE_NAMESPACE;
	public final static String JOURNAL_NAMESPACE;
	public final static IRI ONTOLOGY_IRI;
	private ValueFactory f;
	// Classes
	public final static IRI DOCUMENT;
	public final static IRI AUTHOR;
	public final static IRI CONTEXT;
	public final static IRI JOURNAL;
	public final static IRI CONFERENCE;
	public final static IRI LOCATION;
	public final static IRI CITY;
	public final static IRI COUNTRY;
	// Properties

	public final static IRI TOOKPLACEIN;
	public final static IRI PUBLISHEDIN;
	public final static IRI LOCATEDINSIDE;

	public final static IRI WRITTENBY;
	public final static IRI PROPOGATEDBY;
	public final static IRI REFERENCES;
	public final static IRI ISPEER;
	public final static IRI TITLEREFERENCESTOPIC;

	public final static IRI TITLE;
	public final static IRI URL;
	public final static IRI DOI;
	public final static IRI PUBLISHINGDATE;
	public final static IRI NAME;
	public final static IRI SUBJECT;
	public final static IRI ISSN;
	public final static IRI ISBN;

	public DOCTIME() {
		rep = new SailRepository(new MemoryStore());
		rep.initialize();
		String ontology_description = "ontology_description#";
		// namespace = "http://www.grupo6.semanticweb.uniandes.edu.co/curso/doctime/";
		f = rep.getValueFactory();
	}
	static {
		ValueFactory f = SimpleValueFactory.getInstance();
		NAMESPACE = "http://172.24.101.57/";
		DESCRIPTION_NAMESPACE = NAMESPACE+"ontology_description#";
		LOCATION_NAMESPACE = NAMESPACE+"locations/";
		DOCUMENT_NAMESPACE = NAMESPACE+"documents/";
		
		CONFERENCE_NAMESPACE = NAMESPACE+"all_conference_ontology#";
		JOURNAL_NAMESPACE = NAMESPACE+"all_journal_ontology#";
		
		ONTOLOGY_IRI = f.createIRI(NAMESPACE);
		DOCUMENT = f.createIRI(DESCRIPTION_NAMESPACE, "Document");
		AUTHOR = f.createIRI(DESCRIPTION_NAMESPACE, "Author");
		CONTEXT = f.createIRI(DESCRIPTION_NAMESPACE, "Context");
		JOURNAL = f.createIRI(DESCRIPTION_NAMESPACE, "Journal");
		CONFERENCE = f.createIRI(DESCRIPTION_NAMESPACE, "Conference");

		LOCATION = f.createIRI(DESCRIPTION_NAMESPACE, "Location");
		CITY = f.createIRI(DESCRIPTION_NAMESPACE, "City");
		COUNTRY= f.createIRI(DESCRIPTION_NAMESPACE, "Country");

		TOOKPLACEIN = f.createIRI(DESCRIPTION_NAMESPACE, "tookPlaceIn");
		PUBLISHEDIN = f.createIRI(DESCRIPTION_NAMESPACE, "publishedIn");
		LOCATEDINSIDE= f.createIRI(DESCRIPTION_NAMESPACE, "locatedInside");

		WRITTENBY = f.createIRI(DESCRIPTION_NAMESPACE, "writtenBy");
		PROPOGATEDBY= f.createIRI(DESCRIPTION_NAMESPACE, "propogatedBy");
		REFERENCES= f.createIRI(DESCRIPTION_NAMESPACE, "references");
		ISPEER = f.createIRI(DESCRIPTION_NAMESPACE, "isPeer");
		TITLEREFERENCESTOPIC = f.createIRI(DESCRIPTION_NAMESPACE, "titleReferencesTopic");

		TITLE = f.createIRI(DESCRIPTION_NAMESPACE, "title");
		URL = f.createIRI(DESCRIPTION_NAMESPACE, "url");
		DOI = f.createIRI(DESCRIPTION_NAMESPACE, "doi");
		PUBLISHINGDATE = f.createIRI(DESCRIPTION_NAMESPACE, "publishingDate");
		NAME = f.createIRI(DESCRIPTION_NAMESPACE, "name");
		SUBJECT = f.createIRI(DESCRIPTION_NAMESPACE, "subject");
		ISSN = f.createIRI(DESCRIPTION_NAMESPACE, "issn");
		ISBN= f.createIRI(DESCRIPTION_NAMESPACE, "isbn");
	}

	public void write_ontology_documentation() {

		// Ontology structure
		try (RepositoryConnection conn = rep.getConnection()) {
			conn.setNamespace(NAMESPACE, "doctime");
			conn.add(ONTOLOGY_IRI, RDF.TYPE, OWL.ONTOLOGY);
			conn.add(DOCUMENT, RDF.TYPE, RDFS.CLASS);
			conn.add(AUTHOR, RDF.TYPE, RDFS.CLASS);
			conn.add(CONTEXT, RDF.TYPE, RDFS.CLASS);
			conn.add(JOURNAL, RDF.TYPE, RDFS.CLASS);
			conn.add(CONFERENCE, RDF.TYPE, RDFS.CLASS);
			conn.add(CITY, RDF.TYPE, RDFS.CLASS);
			conn.add(COUNTRY, RDF.TYPE, RDFS.CLASS);
			conn.add(LOCATION, RDF.TYPE, RDFS.CLASS);
			conn.add(JOURNAL, RDFS.SUBCLASSOF, CONTEXT);
			conn.add(CONFERENCE, RDFS.SUBCLASSOF, CONTEXT);

			conn.add(CITY, RDFS.SUBCLASSOF, LOCATION);
			conn.add(COUNTRY, RDFS.SUBCLASSOF, LOCATION);

			// Property, domain, range definition
			conn.add(WRITTENBY, RDF.TYPE, RDF.PROPERTY);
			conn.add(WRITTENBY, RDFS.DOMAIN, DOCUMENT);
			conn.add(WRITTENBY, RDFS.RANGE, AUTHOR);

			conn.add(PUBLISHEDIN, RDF.TYPE, RDF.PROPERTY);
			conn.add(PUBLISHEDIN, RDFS.DOMAIN, JOURNAL);
			conn.add(PUBLISHEDIN, RDFS.RANGE, LOCATION);

			conn.add(TOOKPLACEIN, RDF.TYPE, RDF.PROPERTY);
			conn.add(TOOKPLACEIN, RDFS.DOMAIN, CONFERENCE);
			conn.add(TOOKPLACEIN, RDFS.RANGE, LOCATION);

			conn.add(LOCATEDINSIDE, RDF.TYPE, RDF.PROPERTY);
			conn.add(LOCATEDINSIDE, RDFS.DOMAIN, CITY);
			conn.add(LOCATEDINSIDE, RDFS.RANGE, COUNTRY);

			conn.add(PROPOGATEDBY, RDF.TYPE, RDF.PROPERTY);
			conn.add(PROPOGATEDBY, RDFS.DOMAIN, DOCUMENT);
			conn.add(PROPOGATEDBY, RDFS.RANGE, CONTEXT);

			conn.add(REFERENCES, RDF.TYPE, RDF.PROPERTY);
			conn.add(REFERENCES, RDFS.DOMAIN, DOCUMENT);
			conn.add(REFERENCES, RDFS.RANGE, DOCUMENT);

			conn.add(ISPEER, RDF.TYPE, RDF.PROPERTY);
			conn.add(ISPEER, RDFS.DOMAIN, AUTHOR);
			conn.add(ISPEER, RDFS.RANGE, AUTHOR);

			conn.add(TITLEREFERENCESTOPIC, RDF.TYPE, RDF.PROPERTY);

			conn.add(TITLE, RDF.TYPE, OWL.DATATYPEPROPERTY);

			conn.add(URL, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(DOI, RDF.TYPE, OWL.DATATYPEPROPERTY);

			conn.add(PUBLISHINGDATE, RDF.TYPE, RDF.PROPERTY);

			conn.add(NAME, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(SUBJECT, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(ISSN, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(ISBN, RDF.TYPE, OWL.DATATYPEPROPERTY);

		}

		// Ontology integration
		try (RepositoryConnection conn = rep.getConnection()) {
			// FOAF
			conn.add(AUTHOR, RDFS.SUBCLASSOF, FOAF.PERSON);
			conn.add(DOCUMENT, OWL.EQUIVALENTCLASS, FOAF.DOCUMENT);

			// DBPEDIA
			String dbpedia_namespace = "http://dbpedia.org/ontology/";
			conn.add(CONFERENCE, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "AcademicConference"));
			conn.add(JOURNAL, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "AcademicJournal"));
			conn.add(CONTEXT, RDFS.SUBCLASSOF, f.createIRI(dbpedia_namespace, "SocietalEvent"));
			conn.add(AUTHOR, RDFS.SUBCLASSOF, f.createIRI(dbpedia_namespace, "Person"));
			conn.add(CITY, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "City"));
			conn.add(COUNTRY, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "Country"));
		}

		// Class and subclass definitions
		try (RepositoryConnection conn = rep.getConnection()) {
			conn.add(DOCUMENT, RDFS.COMMENT, f.createLiteral(
					"Documento, es el cuerpo de la investigacion realizada, puede estar enfocado a diversos temas y ramas de la ciencia."));
			conn.add(AUTHOR, RDFS.COMMENT, f.createLiteral(
					"Autor, se considera al creador del contenido del documento, es importante resaltar que cada publicacion puede tener mas de un autor."));
			conn.add(CONTEXT, RDFS.COMMENT, f.createLiteral(
					"Contexto, refiere al entorno de propagacion de un documento. Una publicacion se puede contextualizar en una revista o en una conferencia."));
			conn.add(JOURNAL, RDFS.COMMENT, f.createLiteral(
					"Revista, refiere a las publicaciones academicas publicadas periodicamente por expertos en una materia especifica."));
			conn.add(CONFERENCE, RDFS.COMMENT, f.createLiteral(
					"Conferencia, refiere a las reuniones con el fin de socializar, discutir y presentar diferentes temas en una materia especifica."));
			conn.add(WRITTENBY, RDFS.COMMENT, f.createLiteral(
					"Hace referencia a la propiedad de objeto que identifica que autor escribio que documento, o que documento fue escrito por determinado autor."));
			conn.add(PROPOGATEDBY, RDFS.COMMENT, f.createLiteral(
					"Hace referencia al contexto (Context) que se encarga de propagar determinado documento. Como se menciono en las clases, un contexto puede ser un Journal o un Conference."));
			conn.add(REFERENCES, RDFS.COMMENT, f.createLiteral(
					"Un documento puede referenciar a otro documento, pero no se puede referenciar a si mismo."));
			conn.add(ISPEER, RDFS.COMMENT, f.createLiteral(
					"Hace referencia a que un autor puede ser par de otro autor, pero no puede ser par de si mismo."));
			conn.add(TITLEREFERENCESTOPIC, RDFS.COMMENT, f.createLiteral(
					"Conecta un documento con la entidad correspondiente de Dbpedia del sujeto principal de su titulo."));
			conn.add(URL, RDFS.COMMENT,
					f.createLiteral("Url en la cual se puede encontrar el contexto donde se encuentra el documento. "));
			conn.add(DOI, RDFS.COMMENT, f.createLiteral("Numero unico de identificacion de un documento."));
			conn.add(TITLE, RDFS.COMMENT, f.createLiteral("Titulo del documento."));
			conn.add(PUBLISHINGDATE, RDFS.COMMENT, f.createLiteral("Fecha de publicacion de un documento."));
			conn.add(SUBJECT, RDFS.COMMENT, f.createLiteral(
					"Tema academico del contexto, sobre que trata el journal o la conferencia. Puede ser el nombre del journal o conferencia."));
			conn.add(ISSN, RDFS.COMMENT,
					f.createLiteral("Numero unico de identificacion del contexto (Journal o Conference)."));
			conn.add(ISBN, RDFS.COMMENT,
					f.createLiteral("    Numero unico de identificacion del contexto (Journal o Conference). "));
		
			conn.add(CITY, RDFS.COMMENT,
					f.createLiteral("Una ciudad dentro de un pais en algun lugar del mundo"));
			conn.add(COUNTRY, RDFS.COMMENT,
					f.createLiteral("Es un pais/nacion del mundo. Contiene ciudades dentro de si."));
			conn.add(LOCATION, RDFS.COMMENT,
					f.createLiteral("Represtenta cualquier ubicacion en el mundo. Puede ser una ciudad, un pais, una peninsula, una isla, un continente, etc."));
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
			model.setNamespace("", NAMESPACE);
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