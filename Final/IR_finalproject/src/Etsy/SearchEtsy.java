package Etsy;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import Classes.Path;

public class SearchEtsy {
	private String keyWord;
	public SearchEtsy(String keyWord){
		this.keyWord = keyWord;
	}

	public static String getHTML(String urlToRead) {
		URL url; // The URL to read
		HttpURLConnection conn; // The actual connection to the web page
		BufferedReader rd; // Used to read results from the web page
		String line; // An individual line of the web page HTML
		String result = ""; // A long string containing all the HTML
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
				System.out.println(result);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// Microsoft Translator API
	public static String translate(String source, Language lang) throws Exception {
		Translate.setClientId("etsy_chinese");
		Translate.setClientSecret("gh9NGVw44aoFAQnfFN25zJ78qqNJRSLTOgthJvl3h4Y=");
		// Language.ENGLISH;
		return Translate.execute(source, lang);
	}

	// use query to seach in Etsy and return the result
	public String getSearchResult() throws Exception {
		FileWriter wr=new FileWriter (Path.TopicDir);
		String api_key = "ahct17k8nfjvqs0gonmtxjp4";
		// String terms = "box";

		// translate the search query into English
		String q = translate(keyWord, Language.ENGLISH);
		//record search query
		wr.append(q+"\n");
		System.out.println(q);
		// split the search query and change into lower case
		String[] temp = q.trim().toLowerCase().split(" ");
		StringBuilder terms = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			if (i != temp.length - 1) {
				terms.append(temp[i]);
				terms.append("+");
			} else
				terms.append(temp[i]);
		}
		System.out.println(terms);

		String output = "";

		// access to the etsy and use URL to search
		try {

			output = getHTML("https://openapi.etsy.com/v2/listings/active?keywords=" + terms
					+ "&limit=100&includes=Images:1&api_key=" + api_key);
		} catch (Exception e) {
			System.out.println("Something went wrong.");
		}
		wr.close();
		return output;
	}

}