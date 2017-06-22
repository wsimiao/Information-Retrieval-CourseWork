package Etsy;

import java.io.IOException;
import java.util.*;

import Item.*;

public class testMain {
	public static void main(String[] args) throws IOException{
//		SearchEtsy searchEtsy = new SearchEtsy("箱子");
//		try{
//			String output = searchEtsy.getSearchResult();
//			SearchResult searchResult = new SearchResult();
//			searchResult.writeResultfile(output);
//		}catch(Exception e){
//			
//		}
		
		ProcessingList process = new ProcessingList();
		List<ItemDetail> list = new ArrayList<ItemDetail>();
		list = process.getList();
		
	}

}
