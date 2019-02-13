package crawler;

public class Simple_data_csv_adapter {
	public String clean(String input) {
		String output = input;
		output = output.replace(",",">*>*>");
		output = output.replace(";", "<$<$<");
		output = output.replace(":", "<#><#>");
		return output;
	}
	
	public String undoClean(String input) {
		String output = input;
		output = output.replace(">*>*>",",");
		output = output.replace("<$<$<",";");
		output = output.replace("<#><#>",":");
		return output;
	}
}
