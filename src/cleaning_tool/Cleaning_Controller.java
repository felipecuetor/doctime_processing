package cleaning_tool;

import java.io.File;
import java.util.ArrayList;

public class Cleaning_Controller {
	public static void main(String[] args) {
		Cleaning_Controller cleaning_controller = new Cleaning_Controller();
		cleaning_controller.clean_data_folder();
	}
	
	public void clean_data_folder() {
		File folder = new File("../data/");
		File[] listOfFiles = folder.listFiles();
		Cleaning_Adapter cleaning_adapter = new Cleaning_Adapter();

		ArrayList<String> csv_list = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
		    String current_file = listOfFiles[i].getName();
		    if(current_file.endsWith(".csv")) {
		    	File csv_file = new File("../data/"+current_file);
		    	String clean_file = cleaning_adapter.clean("../data/"+current_file);
		    }
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
	}
}
