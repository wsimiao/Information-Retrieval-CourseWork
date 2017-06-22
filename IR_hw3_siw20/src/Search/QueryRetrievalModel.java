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
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		
		List<String> tokenList = aQuery.GetQueryContent();   // all the tokens in the query
		HashMap<String, HashMap> tokenDetail = new HashMap<String, HashMap>();   // to store all relevant documents' p(w|d) of each token

		// 1. find all relevant documents that contains at least one token in the query
		HashSet<Integer> docSet = new HashSet<Integer>();
		for (int i = 0; i < tokenList.size(); i++) {  
			String token = tokenList.get(i);
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(); 
			// get posting list
			int[][] posting = indexReader.getPostingList(token);
			if (posting == null || posting.length == 0) { 
				tokenList.remove(token);
				continue;
			}

			// save relevant documents' token frequency
			for (int ix = 0; ix < posting.length; ix++) {
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				map.put(docid, freq); // store docid & token's frequency
				docSet.add(docid); // mark the relevant documents
				tokenDetail.put(token, map);
			}
		}

		List<Document> resultList = new ArrayList<Document>();
		
		// 2. calculate query likelihood of each relevant document
		Iterator<Integer> iterator = docSet.iterator();
		while (iterator.hasNext()) { // for each relevant document
			int docid = (int) iterator.next();
			int docLength = indexReader.docLength(docid);			
			double score = 1;
			
			// to generate score, multiple each token's p(w|d)
			for (String token : tokenList){
				
				long cf = indexReader.CollectionFreq(token);
				HashMap tokenMap = tokenDetail.get(token);   // stored docid & token's frequency
				int freq = tokenMap.containsKey(docid) ? (Integer)tokenMap.get(docid) : 0;

				// calculate score by multiplying each token's p(w|D)
				double probability = (freq + miu * cf / collectionLength) / (docLength + miu);
				score *= probability;
				
			}
			if(score != 0){
				String docno = indexReader.getDocno(docid);
				Document document = new Document(docid, docno, score);
				resultList.add(document);
			}
			
		}
		
		// 3. sort the results
		Comparator<Document> comparator = new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				if (d1.score() <= d2.score()) {
					return 1;
				} else {
					return -1;
				}
			}
		};
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(resultList, comparator);
		
		// 4. return sorted ranking list
		return resultList.subList(0, TopN);
	}

	
}