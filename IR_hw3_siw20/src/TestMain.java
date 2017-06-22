import java.util.List;

import Classes.*;
import IndexingLucene.*;
import Search.*;

public class TestMain {
	public static void main(String[] args) throws Exception{
		ExtractQuery query = new ExtractQuery();
		int id =1;
		while(query.hasNext()){
			System.out.println(id);
			Query aquery = query.next();
			List<String> output = aquery.GetQueryContent();
			System.out.println(output);
			System.out.println("yes");
			id++;
		}

	}

}
