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

public class Dbpedia_Spotlight_Integrator {
	private JSONParser parser;

	public static void main(String[] args) {
		Dbpedia_Spotlight_Integrator integrator = new Dbpedia_Spotlight_Integrator();
	}

	public Dbpedia_Spotlight_Integrator() {
		parser = new JSONParser();
		queryIterator();
	}

	public void queryIterator() {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			int currentTriple = 0;
			boolean end_of_triples = false;
			while (!end_of_triples) {
				TupleQueryResult resp = querySPARQL(con, currentTriple);
				
				if(!resp.hasNext()) {
					end_of_triples = true;
				}

				while (resp.hasNext()) {
					BindingSet current_doc = resp.next();
					String title = current_doc.getValue("title").toString();
					String iri = current_doc.getValue("iri").toString();
					dbpediaEnrichment(title, iri, con);
				}

				currentTriple += 50;
			}
		}
	}

	public void dbpediaEnrichment(String title, String iri, RepositoryConnection con) {
		URI uri;
		CloseableHttpResponse response = null;
		try {
			uri = new URIBuilder().setScheme("https").setHost("api.dbpedia-spotlight.org").setPath("/en/annotate")
					.setParameter("text", title).setParameter("confidence", "0.45").build();
			HttpGet httpget = new HttpGet(uri);
			httpget.addHeader("accept", "application/json");
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpget);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject jsonObject = (JSONObject) parser.parse(result.toString());

            JSONArray resources = (JSONArray) jsonObject.get("Resources");
            Iterator resources_iterator = resources.iterator();
            while(resources_iterator.hasNext()) {
            	JSONObject detected_attribute = (JSONObject) resources_iterator.next();
            	String topic_uri = (String) detected_attribute.get("@URI");
            	constructSPARQL(con, iri, topic_uri);
            }
			
			response.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				response.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public TupleQueryResult querySPARQL(RepositoryConnection con, int currentTriple) {
		String queryString = "";
		queryString += "PREFIX : <http://172.24.101.57/ontology_description#>";
		queryString += "SELECT ?title ?iri ";
		queryString += "WHERE{ ?iri :title ?title }";
		queryString += "ORDER BY ?title LIMIT 50 OFFSET " + currentTriple;
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
