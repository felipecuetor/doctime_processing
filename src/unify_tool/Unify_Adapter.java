package unify_tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Unify_Adapter {

	public ArrayList<String> check_n_write(String file_name, ArrayList<String> current_total) {
		String row1;
		try (BufferedReader br = new BufferedReader(new FileReader("../data_clean/" + file_name))) {
			String columns = br.readLine();
			while ((row1 = br.readLine()) != null) {
				Iterator<String> total_iter = current_total.iterator();
				boolean repeat = false;
				while (total_iter.hasNext()) {
					String row2 = (String) total_iter.next();
					if (compare_row_equivalance(row1, row2)) {
						repeat = true;
						System.out.println(row1);
						System.out.println(row2);
						System.out.println("");
					}
				}
				if (!repeat) {
					current_total.add(row1);
					String path = "../data_final/unified_doaj_arxiv_ceur_dblp.csv";
					BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
					writer.append(row1 + "\n");
					writer.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return current_total;
	}

	public boolean compare_row_equivalance(String row1, String row2) {

		try {
			String[] column_split_row1 = row1.split(",");
			String[] column_split_row2 = row2.split(",");
			int title_distance = minDistance(column_split_row1[0], column_split_row2[0]);
			int author_distance = author_equivalence(column_split_row1[1], column_split_row2[1]);
			if (title_distance < 5 && author_distance > 1) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	public int author_equivalence(String row1, String row2) {
		String[] author_split_row1 = row1.split("(;)|( )");
		String[] author_split_row2 = row2.split("(;)|( )");
		ArrayList author_split_row2_array = new ArrayList<String>();
		author_split_row2_array.addAll(Arrays.asList(author_split_row2));
		int count_matches = 0;
		for (int i = 0; i < author_split_row1.length; i++) {
			String current_author = author_split_row1[i];
			if (author_split_row2_array.contains(current_author)) {
				count_matches++;
			}
		}
		return count_matches;
	}

	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}

		// iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);

				// if last two chars equal
				if (c1 == c2) {
					// update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}

		return dp[len1][len2];
	}
}
