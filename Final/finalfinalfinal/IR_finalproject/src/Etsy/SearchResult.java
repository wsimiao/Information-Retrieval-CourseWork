package Etsy;

import java.io.File;
import java.io.FileWriter;



import Classes.Path;

public class SearchResult {

	private FileWriter resultWrite = null;
	private int count = 0;
	
	public int getCount(){
		return count;
	}

	public void writeResultfile(String input) throws Exception {
		//this.resultWrite = new FileWriter("/Users/simiao/Documents/workspace/IR_finalproject/WebContent/WEB-INF/data/abde.txt");
		this.resultWrite = new FileWriter(Path.resultPath);

		try {

			// find "listing_id"
			int id = input.indexOf("\"listing_id\"");

			//int count = 0;
			while (id != -1) {
				count++;
				input = input.substring(id + 12);
				String idContent = input.split(",\"")[0];
				idContent = idContent.replaceAll(":", "");
				// System.out.println(idContent);
				resultWrite.write("<ID>" + "\n" + idContent + "\n");

				// find the title content
				int title = input.indexOf("\"title\"");
				input = input.substring(title + 9);
				String titleContent = input.split("\"")[0];
				// titleContent=titleContent.replace("\n","");
				// System.out.println("<Title>"+"\n"+titleContent);
				resultWrite.write("<Title>" + "\n" + titleContent + "\n");

				// find description content
				int des = input.indexOf("\"description\"");
				// System.out.println(des);
				input = input.substring(des + 15);
				String desContent = input.split("\"")[0];
				// desContent=desContent.replace("\n", "");
				// desContent=desContent.replace("\n\n", "");
				// System.out.println("<Description>"+"\n"+desContent);
				resultWrite.write("<Description>" + "\n" + desContent + "\n");

				// find price
				int price = input.indexOf("\"price\"");
				input = input.substring(price + 9);
				String priceContent = input.split("\",\"")[0];
				// System.out.println("<Price>"+"\n"+ priceContent);
				resultWrite.write("<Price>" + "\n" + priceContent + "\n");

				// find currency_code
				int currency = input.indexOf("\"currency_code\"");
				input = input.substring(currency + 17);
				String currencyContent = input.split("\"")[0];
				// System.out.println("<Currency>"+"\n"+currencyContent);
				resultWrite.write("<Currency>" + "\n" + currencyContent + "\n");

				// find url
				int url = input.indexOf("\"url\"");
				input = input.substring(url + 7);
				String urlContent = input.split("\"")[0];
				// System.out.println(urlContent);
				urlContent = urlContent.replace("\\", "");
				// System.out.println("<url>"+"\n"+urlContent);
				resultWrite.write("<url>" + "\n" + urlContent + "\n");

				// find views
				int view = input.indexOf("\"views\"");
				input = input.substring(view + 8);
				String viewContent = input.split(",\"")[0];
				// System.out.println("<Views>"+"\n"+viewContent);
				resultWrite.write("<Views>" + "\n" + viewContent + "\n");

				// find num_favorers
				int favorer = input.indexOf("\"num_favorers\"");
				input = input.substring(favorer + 15);
				String favorerContent = input.split(",\"")[0];
				// System.out.println("<Num_favorers>"+"\n"+favorerContent);
				resultWrite.write("<Num_favorers>" + "\n" + favorerContent + "\n");

				// find image url_170*135
				int imageUrl = input.indexOf("\"url_170x135\"");
				input = input.substring(imageUrl + 15);
				String imageContent = input.split("\"")[0];
				imageContent = imageContent.replace("\\", "");
				// System.out.println("<ImageUrl>"+"\n"+imageContent);
				resultWrite.write("<ImageUrl>" + "\n" + imageContent + "\n");

				id = input.indexOf("listing_id");
			}
			System.out.println("\nNo of listings is : " + count);

		} catch (Exception e) {
			System.out.println("Something went wrong.");
		}
		resultWrite.close();
	}

}
