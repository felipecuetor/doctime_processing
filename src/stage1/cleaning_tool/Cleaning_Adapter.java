package stage1.cleaning_tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Cleaning_Adapter {
	public String clean(String original_name) {
		String fileDir = "../data/" + original_name;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"))) {
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
			authors = remove_string_inconsistencies(authors).toLowerCase();
			context = context.replace("LCC Subject Category<#><#> ", "");
			context = remove_string_inconsistencies(context);
			external_references = external_references.replace("Journal Title<#><#> ", "journal:");
			//String inconsistencies algorithm does not work in doi, issn, and isbn codes. Capital letters are necesarry
			//external_references = remove_string_inconsistencies(external_references);

			String resp = title + "," + authors + "," + date + "," + context + "," + external_references + "," + url;
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String remove_string_inconsistencies(String original) {
		String s = original;
	    s = s.replaceAll("[èéêë]","e");
	    s = s.replaceAll("[ûùú]","u");
	    s = s.replaceAll("[ïîí]","i");
	    s = s.replaceAll("[àâá]","a");
	    s = s.replaceAll("[Ôó]","o");

	    s = s.replaceAll("[ÈÉÊËÉ]","E");
	    s = s.replaceAll("[ÛÙÚ]","U");
	    s = s.replaceAll("[ÏÎÍ]","I");
	    s = s.replaceAll("[ÀÂÁ]","A");
	    s = s.replaceAll("[ÔÓ]","O");
	    s = s.replaceAll("æ","ae");
	    String resp = s;
		resp = resp.replace(".", "");
		resp = resp.replace("<#><#>", "");
		resp = resp.replace("<$<$<", "");
		resp = resp.replace(">*>*>", "");
		resp = resp.replace("\"", "");
		resp = resp.replace("\\?", "");
		resp = resp.replace("!", "");
		resp = Normalizer.normalize(resp, Form.NFKC);
		resp = resp.replaceAll("[^\\p{ASCII}]", "");

		return resp;

	}
}
