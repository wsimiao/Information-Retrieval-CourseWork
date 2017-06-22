package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;
import PreprocessData.processSearchResult;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private double u=2000;//miu value
	//private long c=142065539;//the length of the whole collection
	private long c=0;//the length of the whole collection
	
	public QueryRetrievalModel(MyIndexReader ixreader) throws IOException {
		this.indexReader = ixreader;
		getCollectionLength();
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	//get the total length for whole collection
		public void getCollectionLength() throws IOException{
			processSearchResult process=new processSearchResult();
			indexReader =new MyIndexReader();
			HashMap<String,Object>map=(HashMap<String, Object>) process.processResult();
			Iterator iter = map.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry pair = (Map.Entry)iter.next();
				String id=(String)pair.getKey();
				String content=(String)pair.getValue();
				//String temp=ixreader.getDocno(1);
				//System.out.println(temp);
				
				c+=content.length();
				//System.out.println(c);
			}						
		}
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		
		//get all content of a query 
		String[] tokenList=aQuery.GetQueryContent().split(" ");
		
		//store all token word and their location
		HashMap<String,HashMap> tokenMap=new HashMap<>();
		
		//store all relevant document
		HashSet<Integer> docList=new HashSet<>();
		
		List<Document>result=new ArrayList<>();
		
		//find all document which at least has one token word in query	
		
		for(String token:tokenList){
			HashMap<Integer,Integer> map=new HashMap<>();
			
			int[][]post=indexReader.getPostingList(token);
			//if the token is appeared in the document
			if(post!=null){
				if(post.length!=0){
					for(int i=0;i<post.length;i++){
						int docid=post[i][0];
						int freq=post[i][1];
						map.put(docid, freq);
						docList.add(docid);
						tokenMap.put(token,map);
					}	
				}
							
			}			
		}
		
		Iterator<Integer> it=docList.iterator();
		while(it.hasNext()){
			int docid=(int)it.next();
			int docLen=indexReader.docLength(docid);
			double score=1;
			
			//multiple each score in every relevant document 
			for(String token:tokenList){
				if(tokenMap.containsKey(token)){
					long term=indexReader.CollectionFreq(token);
					HashMap map=tokenMap.get(token);
					if(map.get(docid)!=null){
						
						int freq=(int)map.get(docid);
						double prob=(freq+u*term/c)/(docLen+u);
						score*=prob;
					}
					
				}
			}
			if(score!=0){
				String docno =indexReader.getDocno(docid);
				Document doc=new Document(docid+"",docno,score);
				result.add(doc);
			}
			
		}
		//sort all document by score
		Comparator<Document>comparator=new Comparator<Document>(){
			public int compare(Document d1,Document d2){
				if(d1.score()<d2.score()) return 1;
				if(d1.score()== d2.score()) return 0;
				else return -1;					
			}				
		};			
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(result,comparator);
		if(result.size() < TopN) {
			return result;
		}else {
			return result.subList(0, TopN);
		}
		//return result.subList(0, TopN);
	}
	
}