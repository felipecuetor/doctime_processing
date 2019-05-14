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

	private CSV_To_Ontology_Location_Adapter accum_location_adapter;

	public void init_location_adapter() {
		accum_location_adapter = new CSV_To_Ontology_Location_Adapter();
	}

	public CSV_To_Total_Ontology_Adapter() {
		location_list = new HashMap<String, IRI>();
		accum_location_adapter = null;
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

	public void write_ontology_documentation() {

		// Ontology structure
		try (RepositoryConnection conn = rep.getConnection()) {
			conn.setNamespace(namespace, "doctime");
			conn.setNamespace(namespace+"ontology_description#", "doctimedes");
			conn.add(ontology_iri, RDF.TYPE, OWL.ONTOLOGY);
			conn.add(document, RDF.TYPE, RDFS.CLASS);
			conn.add(author, RDF.TYPE, RDFS.CLASS);
			conn.add(context, RDF.TYPE, RDFS.CLASS);
			conn.add(journal, RDF.TYPE, RDFS.CLASS);
			conn.add(conference, RDF.TYPE, RDFS.CLASS);
			conn.add(journal, RDFS.SUBCLASSOF, context);
			conn.add(conference, RDFS.SUBCLASSOF, context);

			conn.add(city, RDFS.SUBCLASSOF, location);
			conn.add(country, RDFS.SUBCLASSOF, location);

			// Property, domain, range definition
			conn.add(writtenBy, RDF.TYPE, RDF.PROPERTY);
			conn.add(writtenBy, RDFS.DOMAIN, document);
			conn.add(writtenBy, RDFS.RANGE, author);

			conn.add(publishedIn, RDF.TYPE, RDF.PROPERTY);
			conn.add(publishedIn, RDFS.DOMAIN, journal);
			conn.add(publishedIn, RDFS.RANGE, location);

			conn.add(tookPlaceIn, RDF.TYPE, RDF.PROPERTY);
			conn.add(tookPlaceIn, RDFS.DOMAIN, conference);
			conn.add(tookPlaceIn, RDFS.RANGE, location);

			conn.add(locatedInside, RDF.TYPE, RDF.PROPERTY);
			conn.add(locatedInside, RDFS.DOMAIN, city);
			conn.add(locatedInside, RDFS.RANGE, country);

			conn.add(propogatedBy, RDF.TYPE, RDF.PROPERTY);
			conn.add(propogatedBy, RDFS.DOMAIN, document);
			conn.add(propogatedBy, RDFS.RANGE, context);

			conn.add(references, RDF.TYPE, RDF.PROPERTY);
			conn.add(references, RDFS.DOMAIN, document);
			conn.add(references, RDFS.RANGE, document);

			conn.add(isPeer, RDF.TYPE, RDF.PROPERTY);
			conn.add(isPeer, RDFS.DOMAIN, author);
			conn.add(isPeer, RDFS.RANGE, author);

			conn.add(titleRefrencesTopic, RDF.TYPE, RDF.PROPERTY);

			conn.add(title, RDF.TYPE, OWL.DATATYPEPROPERTY);

			conn.add(url, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(doi, RDF.TYPE, OWL.DATATYPEPROPERTY);

			conn.add(publishingDate, RDF.TYPE, RDF.PROPERTY);

			conn.add(name, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(subject, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(issn, RDF.TYPE, OWL.DATATYPEPROPERTY);
			conn.add(isbn, RDF.TYPE, OWL.DATATYPEPROPERTY);

		}

		// Ontology integration
		try (RepositoryConnection conn = rep.getConnection()) {
			// FOAF
			conn.add(author, RDFS.SUBCLASSOF, FOAF.PERSON);
			conn.add(document, OWL.EQUIVALENTCLASS, FOAF.DOCUMENT);

			// DBPEDIA
			String dbpedia_namespace = "http://dbpedia.org/ontology/";
			conn.add(conference, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "AcademicConference"));
			conn.add(journal, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "AcademicJournal"));
			conn.add(context, RDFS.SUBCLASSOF, f.createIRI(dbpedia_namespace, "SocietalEvent"));
			conn.add(author, RDFS.SUBCLASSOF, f.createIRI(dbpedia_namespace, "Person"));
			conn.add(city, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "City"));
			conn.add(country, OWL.EQUIVALENTCLASS, f.createIRI(dbpedia_namespace, "Country"));
		}

		// Class and subclass definitions
		try (RepositoryConnection conn = rep.getConnection()) {
			conn.add(document, RDFS.COMMENT, f.createLiteral(
					"Documento, es el cuerpo de la investigacion realizada, puede estar enfocado a diversos temas y ramas de la ciencia."));
			conn.add(author, RDFS.COMMENT, f.createLiteral(
					"Autor, se considera al creador del contenido del documento, es importante resaltar que cada publicacion puede tener mas de un autor."));
			conn.add(context, RDFS.COMMENT, f.createLiteral(
					"Contexto, refiere al entorno de propagacion de un documento. Una publicacion se puede contextualizar en una revista o en una conferencia."));
			conn.add(journal, RDFS.COMMENT, f.createLiteral(
					"Revista, refiere a las publicaciones academicas publicadas periodicamente por expertos en una materia especifica."));
			conn.add(conference, RDFS.COMMENT, f.createLiteral(
					"Conferencia, refiere a las reuniones con el fin de socializar, discutir y presentar diferentes temas en una materia especifica."));
			conn.add(writtenBy, RDFS.COMMENT, f.createLiteral(
					"Hace referencia a la propiedad de objeto que identifica que autor escribio que documento, o que documento fue escrito por determinado autor."));
			conn.add(propogatedBy, RDFS.COMMENT, f.createLiteral(
					"Hace referencia al contexto (Context) que se encarga de propagar determinado documento. Como se menciono en las clases, un contexto puede ser un Journal o un Conference."));
			conn.add(references, RDFS.COMMENT, f.createLiteral(
					"Un documento puede referenciar a otro documento, pero no se puede referenciar a si mismo."));
			conn.add(isPeer, RDFS.COMMENT, f.createLiteral(
					"Hace referencia a que un autor puede ser par de otro autor, pero no puede ser par de si mismo."));
			conn.add(titleRefrencesTopic, RDFS.COMMENT, f.createLiteral(
					"Conecta un documento con la entidad correspondiente de Dbpedia del sujeto principal de su titulo."));
			conn.add(url, RDFS.COMMENT,
					f.createLiteral("Url en la cual se puede encontrar el contexto donde se encuentra el documento. "));
			conn.add(doi, RDFS.COMMENT, f.createLiteral("Numero unico de identificacion de un documento."));
			conn.add(title, RDFS.COMMENT, f.createLiteral("Titulo del documento."));
			conn.add(publishingDate, RDFS.COMMENT, f.createLiteral("Fecha de publicacion de un documento."));
			conn.add(subject, RDFS.COMMENT, f.createLiteral(
					"Tema academico del contexto, sobre que trata el journal o la conferencia. Puede ser el nombre del journal o conferencia."));
			conn.add(issn, RDFS.COMMENT, f.createLiteral(
					"Numero unico de identificacion del contexto asignado por la organizacion isbn(Journal o Conference)."));
			conn.add(isbn, RDFS.COMMENT, f.createLiteral(
					"Numero unico de identificacion del contexto asignado por la organizacion issn(Journal o Conference). "));
		}
	}

	public IRI write_document_rdf(String document_id_hex, String title_param, String[] authors_param,
			String publishing_date_param, String doi_param, String url_param, String context_param) {
		try (RepositoryConnection conn = rep.getConnection()) {
			IRI current_document = f.createIRI(namespace, "documents/" + document_id_hex);

			conn.add(current_document, RDF.TYPE, document);
			conn.add(current_document, RDFS.LABEL, f.createLiteral(title_param));
			conn.add(current_document, title, f.createLiteral(title_param));
			if (doi_param != null)
				conn.add(current_document, doi, f.createLiteral(doi_param));
			conn.add(current_document, url, f.createLiteral(url_param));
			// conn.add(current_document, publishingDate,
			// f.createLiteral(publishing_date_param));
			try {
				Date date = new Date(publishing_date_param);
				conn.add(current_document, publishingDate, f.createLiteral(date));
			} catch (Exception e) {
				conn.add(current_document, publishingDate, f.createLiteral(publishing_date_param));
			}

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
			String dbpedia_resource_namespace = "http://dbpedia.org/resource/";
			model.setNamespace("dbr", dbpedia_resource_namespace);
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
						conn.add(conference_BNode, tookPlaceIn, countrySearch);
					} else {
						CSV_To_Ontology_Location_Adapter location_adapter = new CSV_To_Ontology_Location_Adapter();
						accum_location_adapter.writeLocationFile(location[1], location[1], 2, null);
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
							accum_location_adapter.writeLocationFile(location[0], location[0] + "_" + location[1], 1,
									countrySearch);
							IRI newCity = location_adapter.writeLocationFile(location[0],
									location[0] + "_" + location[1], 1, countrySearch);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.add(conference_BNode, RDF.TYPE, conference);
			conn.add(conference_BNode, subject, f.createLiteral(context_subject));
			conn.add(conference_BNode, url, f.createLiteral(url_link));

		}

	}
}