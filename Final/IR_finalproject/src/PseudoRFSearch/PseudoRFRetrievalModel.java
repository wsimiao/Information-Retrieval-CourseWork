package PseudoRFSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import IndexingLucene.PreProcessedCorpusReader;
import PreprocessData.processSearchResult;
import Search.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private double u=2000;//miu value
	private long c=0;//the length of the whole collection
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader) throws IOException
	{
		getCollectionLength();
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 * @throws IOException 
	 */
	
	//get the total length for whole collection
	public void getCollectionLength() throws IOException{
		processSearchResult process=new processSearchResult();
		ixreader=new MyIndexReader();
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
	
	
	
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);
		
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
					
					int[][]post=ixreader.getPostingList(token);
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
					int docLen=ixreader.docLength(docid);
					double fbscore=1;
					
					//multiple each score in every relevant document 
					for(String token:tokenList){
						if(tokenMap.containsKey(token)){
							long term=ixreader.CollectionFreq(token);
							HashMap map=tokenMap.get(token);
							if(map.get(docid)!=null){
								
								int freq=(int)map.get(docid);
								double score=(freq+u*term/c)/(docLen+u);
								double newScore=1;
								if(TokenRFScore.get(token)!=null){
									newScore=alpha * score + (1 - alpha) * TokenRFScore.get(token);
								}
								fbscore*=newScore;
								
							}
							
						}
					}
					if(fbscore!=0){
						String docno =ixreader.getDocno(docid);
						Document doc=new Document(docid+"",docno,fbscore);
						result.add(doc);
					}
					
				}
				//sort all document by score
				Comparator<Document>comparator=new Comparator<Document>(){
					public int compare(Document d1,Document d2){
						if(d1.score()<d2.score()) return 1;
						else return -1;					
					}				
				};			
				Collections.sort(result,comparator);
				
				return result.subList(0, TopN);
		
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
		
		//get all relevance documents		
		QueryRetrievalModel qrm=new QueryRetrievalModel(ixreader);
						
		//store all relevant document
		HashSet<Integer> docList=new HashSet<>();
				
		List<Document> releDocList=qrm.retrieveQuery(aQuery, TopK);
		
		int docLen=0;
		
		for (Document doc:releDocList){
			if(doc.docid()!=null){
				int docid=Integer.parseInt(doc.docid());				
				int len=ixreader.docLength(docid);
				docList.add(docid);
				docLen+=len;
			}			
		}
				
		//get token content 
		String tokenContent=aQuery.GetQueryContent();
		//get the token List
		String[]tokenList=tokenContent.split(" ");
		
		//calculate the frequency of token in the relevant document
		for (String token:tokenList){
		  int freq=0;
		  double score=1;
		  int term=(int) ixreader.CollectionFreq(token);
		  int[][]post=ixreader.getPostingList(token);
			//if the token is appeared in the document
			if(post!=null){
				if(post.length!=0){
					for(int i=0;i<post.length;i++){
						int docid=post[i][0];
						if(docList.contains(docid)){
							int f=post[i][1];
							freq+=f;
						}					
					}	
				}							
			}
			//calculate the score of token---P(token|feedback documents)
			score=(freq+u*term/c)/(docLen+u);			
			if(score!=0){
				TokenRFScore.put(token, score);
			}
			
		}
	
		return TokenRFScore;
	}
	
	
}