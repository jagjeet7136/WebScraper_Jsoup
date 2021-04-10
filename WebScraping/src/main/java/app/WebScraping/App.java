package app.WebScraping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App 
{
	String diseases[] = {"ADHD","Acne","Alcohol-Addiction","Allergies","Alzheimer","Amoebiasis","Anaemia","Anaesthesia-Local","Anaesthesia-General",
			"Anal-Fissure","Angina","Anti-Scar","Anxiety","Appetite","Arrhythmiasis","Arthritis","Asthma-COPD","Auto-Immune-Disease","Ayurvedic-Medicine",
			"Bacterial-Infections","Bladder-And-Prostate-Disorders","Bleeding-Disorders","Blood-Clot","Bone-Metabolism","Burn","CNS-stimulant",
			"Cancer-Oncology","Cholelithiasis-Gall-Stones","Cleanser","Constipation","Contraception","Cough-And-Cold","Crack",
			"Dandruff","Denture-Adhesive",
			"Depression","Diabetes","Diagnostic","Diarrhoea","Dietary-Management","Digestion","Dry-Eye","Dry-Skin"};

	String finalString;

	public App() {
		String url = "";
		for(String s : diseases) {
			url = "https://www.netmeds.com/prescriptions/" + s.toLowerCase();
			getLink(url, s);
		}
	}

	public void getLink(String url, String category) {

		try {
			Document doc = Jsoup.connect(url).get();

			Elements elements = doc.getElementsByClass("product-item");

			for(Element e : elements) {
				String link = e.select("li.product-item a").attr("href");
				getDetails(link, category);
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void getDetails(String url, String Category) throws IOException {
		Document doc = null;
		String name = "";
		String price = "";
		String salts = "";
		String mfr = "";
		String country = "";
		String category = Category;

		doc = Jsoup.connect(url).get();

		Elements element = doc.getElementsByClass("black-txt");
		name = element.text();


		element = doc.getElementsByClass("drug-manu");
		String extra = element.text();

		try {
			int first = extra.indexOf("*");
			salts = extra.substring(0, first-1);
			int second = extra.lastIndexOf("*");
			mfr = extra.substring(first+7, second-1);


			if(extra.contains("Country of Origin: India") || extra.contains("Country of Origin: INDIA") || extra.contains("Country of Origin: Korea")
					|| extra.contains("Country of Origin: KOREA") || extra.contains("Country of Origin: ITALY") || extra.contains("Country of Origin: Italy") ||
					extra.contains("Country of Origin: iNDIA") || extra.contains("Country of Origin: Spain") || extra.contains("Country of Origin: SPAIN")) {
				country = extra.substring(second+21, second+26);
			}
			else if(extra.contains("Country of Origin: NA")) {
				country = extra.substring(second+21, second+23);
			}
			else if(extra.contains("Country of Origin: GERMANY") || extra.contains("Country of Origin: Germany") || extra.contains("Country of Origin: IRELAND")
					|| extra.contains("Country of Origin: Ireland") || extra.contains("Country of Origin: DENMARK") || extra.contains("Country of Origin: Denmark"))
			{
				country = extra.substring(second+21, second+28);
			}
			else if(extra.contains("Country of Origin: USA")) {
				country = extra.substring(second+21, second+24);
			}
			else if(extra.contains("Country of Origin: Australia") || extra.contains("Country of Origin: Australia")) {
				country = extra.substring(second+21, second+30);
			}
			else if(extra.contains("Country of Origin: FRANCE") || extra.contains("Country of Origin: France") || extra.contains("Country of Origin: CANADA") 
					|| extra.contains("Country of Origin: Canada")) {
				country = extra.substring(second+21, second+27);
			}
			else if(extra.contains("Country of Origin: SWITZERLAND") || extra.contains("Country of Origin: Switzerland")) {
				first = extra.indexOf("Country of Origin: INDIA");
				country = extra.substring(second+21, second+32);
			}
			else {
				country = extra;
			}


			element = doc.getElementsByClass("final-price");
			extra = element.text();
			if(extra.contains("Rs.")) {
				first = extra.indexOf("Rs.");
				price = extra.substring(first+3);
			}
			else  {
				price = extra.substring(14);
			}

			if(price.contains(",")) {
				StringBuffer string = new StringBuffer(price);
				first = price.indexOf(",");
				string.deleteCharAt(first);
				price = "";
				for(int i=0; i<string.length(); i++) {
					price = price + string.charAt(i);
				}
			}

			finalString = finalString + name + "," + category + "," + price + "," + salts + "," + mfr + "," + country +"\n";
		} catch(Exception e) {e.printStackTrace();}



	}

	public static void main( String[] args )
	{
		System.out.println("_-_-_-_-_-_-_-_-_-_-_-_Program has started-_-_-_-_-_-_-_-_-_-_-_-");
		System.out.println(" This program will scrap https://www.netmeds.com/prescriptions/");
		App a = new App();

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("D:\\data2.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder();
		String columnNamesList = "Name,Category,Price,Salts,Manufacturer,Country";
		builder.append(columnNamesList +"\n");
		builder.append(a.finalString);
		pw.write(builder.toString());
		pw.close();
		System.out.println("done!");
	}
}
