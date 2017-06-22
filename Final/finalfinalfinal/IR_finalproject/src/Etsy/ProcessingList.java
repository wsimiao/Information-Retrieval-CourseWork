package Etsy;
import Item.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import Classes.Path;

public class ProcessingList {
	private FileInputStream fis = null;
	private BufferedReader reader = null;
	private String path = Path.Top10;
	//private String path = "/Users/simiao/Documents/workspace/IR_finalproject/WebContent/WEB-INF/data/abde.txt";
	
	public ProcessingList() throws IOException {
		this.fis = new FileInputStream(path);
		this.reader = new BufferedReader(new InputStreamReader(fis));
	}
	
	public List<ItemDetail> getList() throws IOException{
		List<ItemDetail> resultList = new ArrayList<ItemDetail>();
		String line= reader.readLine();
		while(line!= null){
			if(line.startsWith("<ID>") && line != null){
				ItemDetail item = new ItemDetail();
				line = reader.readLine();
				item.setId(line);
				System.out.println(item.getId());
				while(line!=null && !line.equals("<ID>")){
					
					if(line.startsWith("<Title>")){
						line = reader.readLine();
						System.out.println(line);
						item.setTitle(line);
					}
					if(line.startsWith("<Price>")){
						line = reader.readLine();
						item.setPrice(line);
					}
					if(line.startsWith("<url>")){
						line = reader.readLine();
						item.setLink(line);
						System.out.println(item.getLink());
					}
					if(line.startsWith("<ImageUrl>") && line != null){
						line = reader.readLine();
						item.setImgLink(line);
						System.out.println(item.getImgLink());
					}
					line = reader.readLine();
					
				}
				resultList.add(item);
			}
			//line= reader.readLine();

		}
		return resultList;
	}
	

}
