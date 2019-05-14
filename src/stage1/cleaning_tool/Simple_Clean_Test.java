package stage1.cleaning_tool;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class Simple_Clean_Test {

	public static void main(String[] args) {
		Simple_Clean_Test cleaning_controller = new Simple_Clean_Test();
		System.out.println(cleaning_controller.remove_string_inconsistencies("hartmut könig á´sd´´d´fsd´fé´´óú"));
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
		resp = resp.toLowerCase();
		resp = Normalizer.normalize(resp, Form.NFKC);
		resp = resp.replaceAll("[^\\p{ASCII}]", "");

		return resp;

	}
}
