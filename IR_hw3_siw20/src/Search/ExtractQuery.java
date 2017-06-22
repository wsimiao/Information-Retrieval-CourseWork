package Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	private HashSet<String> stopWordList= new HashSet<String>();;
	private FileInputStream fis = new FileInputStream(Path.TopicDir);
	private BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	private String line = null;


	public ExtractQuery() throws Exception {
		// load all stopwords into memory
		loadStopList();

		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
	}
	
	public void loadStopList() throws Exception{
		FileInputStream stopfis = new FileInputStream(Path.StopwordDir);
        BufferedReader stopreader = new BufferedReader(new InputStreamReader(stopfis));    
    	    
             String line = stopreader .readLine();
              while(line != null){
                 //store every stop word into hashset
                 stopWordList.add(line);
                 line = stopreader .readLine();           
               }        
              stopreader.close();
              stopfis.close();
	}

	
	public boolean hasNext() throws Exception{
		//System.out.println(line);
		line = reader.readLine();
		if(line != null){
//			while(!line.startsWith("</top>")){
//				line = reader.readLine();
//				System.out.println(line);
//			}
			return true;
		}else{
			reader.close();
			fis.close();
			return false;		
		}
	}
	
	public Query next() throws Exception{
		Query query = new Query();
		//int id = 1;
		while (line != null && !line.equals("")) {
			if (line.startsWith("<title>")) { // find a new topic
				String title = line.substring(7);
				System.out.println(title);
				// 1) tokenized
				List<String> tokenList = tokenize(title);

				ArrayList<String> newTokenList = new ArrayList<String>();
				for (int i = 0; i < tokenList.size(); i++) {
					// 2) to lowercase
					String token = tokenList.get(i).toLowerCase();
					// 3) remove stop words
					if (!stopWordList.contains(token)) {
						// 4) stemming
						Stemmer s = new Stemmer();
						char[] chars = token.toCharArray();
						s.add(chars, chars.length);
						s.stem();
						token = s.toString();

						newTokenList.add(token);
					}
				}
				query.SetQueryContent(newTokenList);
			}
			if(line.startsWith("<num>")){
				String id = line.split(" ")[2];
				query.SetTopicId(id);
			}
			line = reader.readLine();
			//System.out.println(line);
		}

		return query;
	}
	
	public List<String> tokenize(String title){
		ArrayList<String> tokenList = new ArrayList<String>();
		
		int len = title.length();
		int  i = 0;
		while (i < len) {  
			// find the start of a word
			if (Character.isLetter(title.charAt(i))) { 
				StringBuffer sb = new StringBuffer();
				
				// find the end of a word
				while (i < len && (Character.isLetter(title.charAt(i)) || Character
								.isDigit(title.charAt(i)))) {
					sb.append(title.charAt(i));
					i++;
				}

				tokenList.add(sb.toString());
			}
			i++;
		}
		
		return tokenList;
	}
}
