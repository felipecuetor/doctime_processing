package stage2.generation;


public class Print_Ontology_Description {
	public static void main(String[] args) {
		DOCTIME doctime = new DOCTIME();
		doctime.write_ontology_documentation();
		doctime.end_document("../data_web/ontology_description");
	}
}
