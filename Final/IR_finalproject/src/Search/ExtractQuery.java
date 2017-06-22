package Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	private FileInputStream fis = null;
    private BufferedReader reader = null;
    private String line=null;
    
    private HashSet<String> stopWordList=new HashSet<>();
    
    
	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		
		
        //load stop list to help remove stop word
        loadStopList();
      //read topic file
       this.fis= new FileInputStream(Path.TopicDir);
       this.reader = new BufferedReader(new InputStreamReader(fis));
       
	}
	
	public boolean hasNext() throws IOException
	{ 	
		this.line = reader.readLine();
		if (line!=null) return true;
		else{//if go to the end of the file, close bufferreader
			this.fis.close();
			this.reader.close();
			return false;	
		}
	}
	
	public Query next() throws IOException
	{
		        
	Query q=new Query();
	//initiate a reading line 
	//line = reader.readLine();
	StringBuilder content=new StringBuilder();
	
	//1) token process
	int count=1;
	while (line!=null){//store the query content until go to the end of a topic 
	q.SetTopicId(count+"");
	content.append(line);
	line=reader.readLine();
	}
	String result=preProcess(content.toString().trim());
	//store query content
	q.SetQueryContent(result);
		
	return q;
	
	}
	
	private String preProcess(String content) {
		//2)to lowercase
		String str=content.toLowerCase();
			
		
		String[] wordList=str.split(" ");
		//3)remove stop word
		StringBuilder sb=new StringBuilder();
		for (String word:wordList){
			word=token(word);
			if(!isStopWord(word)){
				//4)stem
				char[] chars=word.toCharArray();
				Stemmer s=new Stemmer();
				s.add(chars,chars.length);
				s.stem();			
				word=s.toString();
				//store processed word
				sb.append(word+" ");
			}
			
		}
		return sb.toString().trim();
	}

	private String token(String word) {
		char[] texts=word.toCharArray();
		int count=0;
		
		while (count < texts.length) { 
				// find the start of a word,inclue letter and number 
				if(Character.isLetter(texts[count]) || Character
						.isDigit(texts[count])){
					StringBuilder sb = new StringBuilder();
					
				
			while(count < texts.length&&(Character.isLetter(texts[count]) || Character.isDigit(texts[count]) )) { //append the character until the end of a word
					//append the character until the end of word (until meet the space)				
						sb.append(texts[count]);
						count++;
					}
			
			//for the word end with n't,the ' will be filited ,and then delete 'n'
			if (count < texts.length  && count < texts.length - 1&& texts[count + 1] == 't' && count > 0
					&& texts[count-1] == 'n') {
				sb.deleteCharAt(sb.length() - 1);
			}	
				return sb.toString();//return the token word 
				
			}			
			count++;
		}

			return null;
	}


	private void loadStopList() throws IOException{
		  // load and store the stop words from the fileinputstream with appropriate data structure
	      // that you believe is suitable for matching stop words.
	      // address of stopword.txt should be Path.StopwordDir
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
	private boolean isStopWord (String word){
	//check if the input string is include in the stop word list
	//the search time complexity for hastset is O(1) and make sure there is no dupilcate word in the list 
		if(stopWordList.contains(word))return true;
		return false;
		
	}
	
	
}
