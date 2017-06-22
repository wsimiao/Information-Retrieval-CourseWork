package PseudoRFSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import Search.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private long collectionLength = 142065539;
	private double miu = 2000;
	private HashMap<Integer, DocDetail> OriginDocResultList;
	//private QueryRetrievalModel queryRetrievalModel;

	public PseudoRFRetrievalModel(MyIndexReader ixreader) throws IOException {
		this.ixreader = ixreader;
		//this.queryRetrievalModel = new QueryRetrievalModel(ixreader);
	}

	/**
	 * Search for the topic with pseudo relevance feedback. The returned results
	 * (retrieved documents) should be ranked by the score (from the most
	 * relevant to the least).
	 * 
	 * @param aQuery
	 *            The query to be searched for.
	 * @param TopN
	 *            The maximum number of returned document
	 * @param TopK
	 *            The count of feedback documents
	 * @param alpha
	 *            parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery(Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and
		// this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK
		// documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's
		// P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine
		// the each query token's original retrieval score P(token|document)
		// with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get
		// the whole query's new score,
		// P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')

		// 1 obtain feedback documents
		// get the query String
		String queryContent = aQuery.GetQueryContent();
		List<String> tokenList = tokenize(queryContent);
		
//		QueryRetrievalModel originalModel = new QueryRetrievalModel(ixreader);
//		List<Document> originResults = originalModel.retrieveQuery(aQuery, TopK);
		
		//The original HashMap<Integer, DocDetail> returned TopK result;
		OriginDocResultList = retrieveQuery(aQuery, TopK);
		

		// 2 implement GetTokenRFScore to get each query token's
		// get P(token|feedback documents)
		HashMap<String, Double> TokenRFScore = GetTokenRFScore(aQuery, TopK);
		
		// 3 implement the relevance feedback model for each token: combine
		
		class Result{
			double score;
			int docId;
		}
		List<Result> newResultList = new ArrayList<Result>();
		for(Map.Entry<Integer, DocDetail> entry: OriginDocResultList.entrySet()){
			int docId = entry.getKey();
			DocDetail docDetail = entry.getValue();
			HashMap<String, Integer> map = docDetail.tdFreq;
			double originScore = 1;
			double rfScore = 1;
			double newScore = 0;
			for(int i = 0; i < tokenList.size(); i++){
				String token = tokenList.get(i);
				int tokenFrequency = (int) ixreader.CollectionFreq(token);
				if (tokenFrequency==0){
					tokenList.remove(token);
					continue;
				}
				int docLength = docDetail.docSize;
				double para = docLength/(docLength + miu);
				int freq;
				if(map.containsKey(token)){
					freq = map.get(token);
				}else{
					freq = 0;
				}
				originScore *=  para * freq/docLength + (1-para) * ixreader.CollectionFreq(token)/collectionLength;
				rfScore *= para * freq/docLength + (1-para) * TokenRFScore.get(token);
			}
			newScore = alpha*originScore + (1-alpha)*newScore;
			Result result = new Result();
			result.score = newScore;
			result.docId = docId;
			newResultList.add(result);
		}
		
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
		Collections.sort(newResultList, comparator);
		

		// sort all retrieved documents from most relevant to least, and return
		// TopN
		List<Document> results = new ArrayList<Document>();
		for(int i = 0; i < TopN ; i++){
			int docId = newResultList.get(i).docId;
			double score = newResultList.get(i).score;
			String docNo = ixreader.getDocno(docId);
			Document document = new Document(docId, docNo, score);
			results.add(document);
		}
		return results;
	}

	public HashMap<String, Double> GetTokenRFScore(Query aQuery, int TopK) throws Exception {
		// for each token in the query, you should calculate token's score in
		// feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();
		
		//get the origin Doc List ranking TopK
		//
		//HashMap<Integer, DocDetail> docResultList = retrieveQuery(aQuery, TopK);
		HashMap<Integer, DocDetail> docResultList = OriginDocResultList;
		
		// get the query String
		String queryContent = aQuery.GetQueryContent();
		List<String> tokenList = tokenize(queryContent);
		
		for(String token: tokenList){
			int rfLength = 0;
			int tokenTotalFreq = 0;
			for(Map.Entry<Integer, DocDetail> entry : docResultList.entrySet()){
				int docId = entry.getKey();
				DocDetail docDetail = entry.getValue();
				if(docDetail.tdFreq.containsKey(token)){
					tokenTotalFreq += docDetail.tdFreq.get(token);
				}
				rfLength += docDetail.docSize;
			}
			double rfScore = (0.0 + tokenTotalFreq) / rfLength ;
			TokenRFScore.put(token, rfScore);		
		}

		return TokenRFScore;
	}

	public HashMap<Integer, DocDetail> retrieveQuery(Query aQuery, int TopK) throws IOException {

		String queryContent = aQuery.GetQueryContent();
		List<String> tokenList = tokenize(queryContent);
		// <docId, DocDetail>
		HashMap<Integer, DocDetail> DocList = new HashMap<Integer, DocDetail>();
		// <token, P(token|REF)>
		HashMap<String, Double> tokenRefProb = new HashMap<String, Double>();
		for (int i = 0; i < tokenList.size(); i++) {
			// use indexReader.getPostingList(token) to retrieve docs;
			String token = tokenList.get(i);
			int[][] posting = ixreader.getPostingList(token);
			// for each docs, put the detail information into DocList
			if (posting == null || posting.length == 0) {
				tokenList.remove(token);
				continue;
			}
			for (int j = 0; j < posting.length; j++) {
				int docId = posting[j][0];
				int freq = posting[j][1];
				if (DocList.containsKey(docId)) {
					DocList.get(docId).tdFreq.put(token, freq);
				} else {
					DocDetail docDetail = new DocDetail();
					docDetail.tdFreq = new HashMap<String, Integer>();
					docDetail.tdFreq.put(token, freq);
					docDetail.docSize = ixreader.DocFreq(token);
					DocList.put(docId, docDetail);
				}
			}
			// for each token, calculate P(token|REF) with help of APIs in
			// indexReader, save the info into tokenRefProb
			double ptREF = ixreader.CollectionFreq(token) / collectionLength;
			tokenRefProb.put(token, ptREF);

		}
		// construct Result class to save the temp
		class Result {
			int docId;
			double score;
		}

		List<Result> resultList = new ArrayList<Result>();
		for (Map.Entry<Integer, DocDetail> entry : DocList.entrySet()) {
			int docId = entry.getKey();
			DocDetail docDetail = entry.getValue();
			HashMap<String, Integer> map = docDetail.tdFreq;
			double score = 1;
			for (String token : tokenList) {
				int docLength = docDetail.docSize;
				double para = docLength / (docLength + miu);
				int freq;
				if (map.containsKey(token)) {
					freq = map.get(token);
				} else {
					freq = 0;
				}
				score *= para * freq / docLength + (1 - para) * tokenRefProb.get(token);
			}
			if (score > 0) {
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
				if (r1.score == r2.score)
					return 0;
				else
					return -1;
			}
		};
		


		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(resultList, comparator);
		
		HashMap<Integer, DocDetail> DocResultList = new HashMap<Integer, DocDetail>();
		for(int i = 0; i < TopK; i++){
			int docId = resultList.get(i).docId;
			DocDetail docDetail = DocList.get(docId);
			DocResultList.put(docId, docDetail);	
//			DetailResult detailResult = new DetailResult();
//			detailResult.score = resultList.get(i).score;
//			detailResult.docDetail = DocList.get(docId);
				
		}
		
//		//save the <tokne, <docId, Feedback total frequency>>
//		HashMap<String, HashMap<Integer, Integer>> TokenDetail = new HashMap<String, HashMap<Integer, Integer>>();
		
//		List<Document> resultDocList = new ArrayList<Document>();
//		for (int i = 0; i < TopN; i++) {
//			int docId = resultList.get(i).docId;
//			double score = resultList.get(i).score;
//			String docNo = ixreader.getDocno(docId);
//			Document document = new Document(docId, docNo, score);
//			resultDocList.add(document);
//		}

		// 4. return sorted ranking list
		return DocResultList;

	}
	
	public class DetailResult{
		double score;
		DocDetail docDetail;
	}

	public class DocDetail {
		// save <token, c(token, doc)>
		HashMap<String, Integer> tdFreq;
		// save |doc|
		int docSize;
	}

	public List<String> tokenize(String title) {
		ArrayList<String> tokenList = new ArrayList<String>();

		int len = title.length();
		int i = 0;
		while (i < len) {
			// find the start of a word
			if (Character.isLetter(title.charAt(i))) {
				StringBuffer sb = new StringBuffer();

				// find the end of a word
				while (i < len && (Character.isLetter(title.charAt(i)) || Character.isDigit(title.charAt(i)))) {
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