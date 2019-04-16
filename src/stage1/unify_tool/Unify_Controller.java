package stage1.unify_tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import stage1.unify_tool.Unify_Adapter;
import stage1.unify_tool.Unify_Controller;

public class Unify_Controller {
	public static void main(String[] args) {
		Unify_Controller cleaning_controller = new Unify_Controller();
		cleaning_controller.clean_data_folder();
	}
	
	public void clean_data_folder() {
		File folder = new File("../data_clean/");
		File[] listOfFiles = folder.listFiles();
		Unify_Adapter cleaning_adapter = new Unify_Adapter();

		HashMap<String, String> current_total = new HashMap<String, String>();
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
		    String current_file = listOfFiles[i].getName();
		    if(current_file.endsWith(".csv")) {
		    	System.out.println(current_file);
		    	current_total = cleaning_adapter.check_n_write(current_file, current_total);
		    }
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
	}
}
