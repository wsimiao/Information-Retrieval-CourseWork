package Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private double miu = 2000;   
	private long collectionLength = 142065539;
	
	public QueryRetrievalModel(MyIndexReader ixreader) throws IOException {
		indexReader = ixreader;
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
	public List<Document> retrieveQuery(Query aQuery, int TopN) throws IOException {
		
		String queryContent = aQuery.GetQueryContent();
		List<String> tokenList = tokenize(queryContent);
		//<docId, DocDetail>
		HashMap<Integer, DocDetail> DocList = new HashMap<Integer, DocDetail>();
		//<token, P(token|REF)>
		HashMap<String, Double> tokenRefProb = new HashMap<String, Double>();
		for(int i = 0; i < tokenList.size(); i++){
			//use indexReader.getPostingList(token) to retrieve docs;
			String token = tokenList.get(i);
			int[][] posting = indexReader.getPostingList(token);
			//for each docs, put the detail information into DocList
			if(posting==null || posting.length == 0){
				tokenList.remove(token);
				continue;
			}
			for(int j = 0; j < posting.length; j++){
				int docId = posting[j][0];
				int freq = posting[j][1];
				if(DocList.containsKey(docId)){
					DocList.get(docId).tdFreq.put(token, freq);
//					DocDetail docDetail = DocList.get(docId);
//					docDetail.tdFreq.put(token, freq);
//					DocList.put(docId, docDetail);
				}else{
					DocDetail docDetail = new DocDetail();
					docDetail.tdFreq = new HashMap<String, Integer>();
					docDetail.tdFreq.put(token,freq);
//					HashMap<String, Integer> map = new HashMap<String, Integer>();
//					map.put(token, freq);
//					docDetail.tdFreq = map;
					docDetail.docSize = indexReader.DocFreq(token);
					DocList.put(docId, docDetail);
					//System.out.println(DocList);
				}
			}
			//for each token, calculate P(token|REF) with help of APIs in indexReader, save the info into tokenRefProb
			double ptREF = indexReader.CollectionFreq(token)/collectionLength;
			tokenRefProb.put(token, ptREF);
						
		}
		//construct Result class to save the temp 
		class Result{
			int docId;
			double score;
		}
		
		List<Result> resultList = new ArrayList<Result>();
		for(Map.Entry<Integer, DocDetail> entry : DocList.entrySet()){
			int docId = entry.getKey();
			DocDetail docDetail = entry.getValue();
			HashMap<String, Integer> map = docDetail.tdFreq;
			double score = 1;
			for(String token : tokenList){
				int docLength = docDetail.docSize;
				double para = docLength/(docLength + miu);
				int freq;
				if(map.containsKey(token)){
					freq = map.get(token);
				}else{
					freq = 0;
				}
				score *= para * freq/docLength + (1-para) * tokenRefProb.get(token);
			}
			if(score > 0) {
				Result result = new Result();
				result.docId = docId;
				result.score = score;
				resultList.add(result);
			}
		}
		// 3. sort the results
		Comparator<Result> comparator = new Comparator<Result>() {
			@Override
			public int compare(Result r1, Result r2) {
				if (r1.score < r2.score) 
					return 1;
				if(r1.score == r2.score)
					return 0;
				else
					return -1;
			}
		};
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(resultList, comparator);
		List<Document> resultDocList = new ArrayList<Document>();
		for(int i = 0; i < TopN ; i++){
			int docId = resultList.get(i).docId;
			double score = resultList.get(i).score;
			String docNo = indexReader.getDocno(docId);
			Document document = new Document(docId, docNo, score);
			resultDocList.add(document);
		}
		

		// 4. return sorted ranking list
		return resultDocList;
		
		//for each retrived doc iterate the DocList
//		for (Map.Entry<Integer, DocDetail> entry : DocList.entrySet()) {
//			int docId = entry.getKey();
//			DocDetail docDetail = entry.getValue();
//			HashMap<String, Integer> map = docDetail.tdFreq;
//			double score = 1;
//			for(String token : tokenList){
//				int docLength = docDetail.docSize;
//				double para = docLength/(docLength + miu);
//				int freq;
//				if(map.containsKey(token)){
//					freq = map.get(token);
//				}else{
//					freq = 0;
//				}
//				score *= para * freq/docDetail.docSize + (1-para) * tokenRefProb.get(token);
//			}
//			if(score > 0){
//				String docNo = indexReader.getDocno(docId);
//				Document document = new Document(docId, docNo, score);
//				//System.out.println(document.docno()+" "+document.docid()+" "+ document.score());
//				resultDocList.add(document);
//				
//			}
//			
//		}
		//output of the document
//		for(int i = 0; i<resultDocList.size(); i++){
//			System.out.println(resultDocList.get(i).docno() + " & " + resultDocList.get(i).score());
//		}
		// 3. sort the results
//		Comparator<Document> comparator = new Comparator<Document>() {
//			@Override
//			public int compare(Document d1, Document d2) {
//				if (d1.score() < d2.score()) 
//					return 1;
//				if(d1.score() == d2.score())
//					return 0;
//				else
//					return -1;
//			}
//		};
	}
	
	
	class DocDetail {
		//save <token, c(token, doc)>
		HashMap<String, Integer> tdFreq;
		//save |doc|
		int docSize;
		
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