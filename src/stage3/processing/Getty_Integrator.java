package stage3.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.ntriples.NTriplesWriter;
import org.eclipse.rdf4j.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Getty_Integrator {
	private JSONParser parser;

	public static void main(String[] args) {
		Getty_Integrator integrator = new Getty_Integrator();
		integrator.queryIterator();
	}

	public Getty_Integrator() {
//		parser = new JSONParser();
//		queryIterator();
	}

	public void queryIterator() {
		Repository rep = new SPARQLRepository("http://vocab.getty.edu/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			int currentTriple = 0;
			boolean end_of_triples = false;
			while (!end_of_triples) {
				TupleQueryResult resp = querySPARQL(con, currentTriple);
				
				if(!resp.hasNext()) {
					end_of_triples = true;
				}
				System.out.println(resp.getBindingNames());
				while (resp.hasNext()) {
					BindingSet current_doc = resp.next();
					String country = current_doc.getValue("country").toString();
					String country_name = current_doc.getValue("label").toString();
					System.out.println(country_name);
				}

				currentTriple += 50;
			}
		}
	}
	
	public TupleQueryResult querySPARQL(RepositoryConnection con, int currentTriple) {
		String queryString = "";
		queryString += "PREFIX : <http://vocab.getty.edu/ontology#> ";
		queryString += "SELECT * ";
		queryString += "WHERE{\r\n" + 
				"\r\n" + 
				"  ?country gvp:prefLabelGVP [xl:literalForm ?label];\r\n" + 
				"\r\n" + 
				"     gvp:placeType [skos:prefLabel \"republics\"@en]} ";
		//queryString += "ORDER BY ?title LIMIT 50";
		TupleQuery graphQuery = con.prepareTupleQuery(queryString);
		// con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult resp = graphQuery.evaluate();
		return resp;
	}
	
	
	public void constructSPARQL(RepositoryConnection con,String element_iri, String topic_uri) {
		System.out.println(element_iri);
		System.out.println(topic_uri);
		String updateString = "";
		updateString += "PREFIX : <http://172.24.101.57/ontology_description#>";
		updateString += "INSERT DATA {<"+element_iri+"> :titleReferencesTopic <"+topic_uri+">}";
		Update graphQuery = con.prepareUpdate(updateString);
		graphQuery.execute();
	}

}
