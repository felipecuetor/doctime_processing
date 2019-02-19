package cleaning_tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Cleaning_Adapter {
	public String clean(String original_name) {
		try (BufferedReader br = new BufferedReader(new FileReader("../data/" + original_name))) {
			String path = "../data_clean/" + "clean_" + original_name;

			String line;
			String columns = br.readLine();
			BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(columns+"\n");
			writer.close();
			
			while ((line = br.readLine()) != null) {
				String clean_line = clean_line(line);
				String str = clean_line;
				writer = new BufferedWriter(new FileWriter(path, true));
				writer.append(str+"\n");
				writer.close();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String clean_line(String line) {
		String[] column_split = line.split(",");
		if (column_split.length > 6) {
			return "";
		}
		try {
			String title = column_split[0];
			String authors = column_split[1];
			String date = column_split[2];
			String context = column_split[3];
			String external_references = column_split[4];
			String url = column_split[5];

			title = remove_string_inconsistencies(title);
			authors = remove_string_inconsistencies(authors);
			context = context.replace("LCC Subject Category<#><#> ", "");
			context = remove_string_inconsistencies(context);
			external_references = external_references.replace("Journal Title<#><#> ", "journal:");
			external_references = remove_string_inconsistencies(external_references);

			String resp = title + "," + authors + "," + date + "," + context + "," + external_references + "," + url;
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String remove_string_inconsistencies(String original) {
		String s = original;
	    s = s.replaceAll("[ËÈÍÎ]","e");
	    s = s.replaceAll("[˚˘]","u");
	    s = s.replaceAll("[ÔÓ]","i");
	    s = s.replaceAll("[‡‚]","a");
	    s = s.replaceAll("‘","o");

	    s = s.replaceAll("[»… À]","E");
	    s = s.replaceAll("[€Ÿ]","U");
	    s = s.replaceAll("[œŒ]","I");
	    s = s.replaceAll("[¿¬]","A");
	    s = s.replaceAll("‘","O");
	    String resp = s;
		resp = resp.replace(".", "");
		resp = resp.replace("<#><#>", "");
		resp = resp.replace("<$<$<", "");
		resp = resp.replace(">*>*>", "");
		resp = resp.replace("\"", "");
		resp = resp.replace("\\?", "");
		resp = resp.replace("!", "");
		resp = resp.toLowerCase();



		return resp;

	}
}
