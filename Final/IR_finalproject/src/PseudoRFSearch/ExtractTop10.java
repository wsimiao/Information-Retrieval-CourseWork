package PseudoRFSearch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Classes.Document;
import Classes.Path;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import Search.ExtractQuery;

public class ExtractTop10 {
	private FileInputStream fis = null;
    private BufferedReader reader = null;
    private String line=null;
   
   
    
public HashMap<Integer,String> getTop10() throws IOException, Exception{
	// Open index, initialize the pseudo relevance feedback retrieval model,and extract queries
		MyIndexReader ixreader = new MyIndexReader();
		PseudoRFRetrievalModel PRFSearchModel = new PseudoRFRetrievalModel(ixreader);
		ExtractQuery queries = new ExtractQuery();
		HashMap<Integer,String>	res=new HashMap<>();
				// begin search
				
				while (queries.hasNext()) {
					Query aQuery = queries.next();
					List<Document> results = PRFSearchModel.RetrieveQuery(aQuery, 10, 30, 0.4);
					if (results != null) {
						int rank = 1;
						for (Document result : results) {
//							System.out.println(aQuery.GetTopicId() + " Q0 " + result.docno() + " " + rank + " "
//									+ result.score() + " MYRUN");
							res.put(rank,result.docno());
							
							rank++;
						}
					}
				}
							
		ixreader.close();
		return res;
}


public void writeTop10List(HashMap<Integer,String> IdList) throws IOException{
	FileWriter wr=new FileWriter(Path.Top10);
	int rank=1;
	while(rank<=10){
		if(IdList.containsKey(rank)){
			String id=IdList.get(rank);
			this.fis= new FileInputStream(Path.resultPath);
	        this.reader = new BufferedReader(new InputStreamReader(fis));
			  line=reader.readLine();	  
			  while(line!=null){
				  if(line.startsWith("<ID>")){
						 //get the id
						 line=reader.readLine();
						 //write the list according to the id 
						 if(line.equals(id)){
							 wr.append("<ID>"+"\n");
							 wr.append(id+"\n");
							 int count=1;//16
							 while(count<=16){
								 line=reader.readLine();
								 wr.append(line+"\n");
								 count++;								 
							 }							
						 }						
					 }
				  line=reader.readLine();				 		 
		}
		rank++;
	}
	}
		
	wr.close();
	
}
	
}
