package stage3.processing;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;

import java.util.*;
import java.util.stream.Collectors;

//Uses Stanford CoreNLP to analyze the Context of every DBLP conference to get their country and/or city
public class NLP_Analyzer {
	// entity mentions in the second sentence
	
	public static void main(String[] args) {
		NLP_Analyzer controller = new NLP_Analyzer();
		controller.get_country_of_string("Proceedings of the 54th Annual Meeting of the Association for Computational Linguistics ACL 2016 August 7-12 2016 Berlin Germany Volume 2 Short Papers.");
	}
	public String[] get_country_of_string(String context) {
	    // set up pipeline properties
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
	    // set up pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // make an example document
	    CoreDocument doc = new CoreDocument(context);
	    // annotate the document
	    pipeline.annotate(doc);
	    // view results
	    String city = null;
	    String country = null;
	    for (CoreEntityMention em : doc.entityMentions()) {
	    	
	    	if(em.entityType().toString().equals("COUNTRY")){
	    		country =em.text();
	    	}
	    	if(em.entityType().toString().equals("CITY")) {
	    		city = em.text();
	    	}
	    }
	    String[] resp = {city,country};
	    return resp;
	}
}
