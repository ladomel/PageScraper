import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class PageScrapper{
	
	public enum DATA_TYPE{IMAGE,LINK};
	
	public class Pair{
		private String s;
		private DATA_TYPE type;
		public Pair(String s, DATA_TYPE type){
			this.s = s;
			this.type = type;
		}
		public String PairGetValue(){
			return s;
		}
		public DATA_TYPE PairGetType(){
			return this.type;
		}
		@Override
		public boolean equals(Object obj){
			return s.equals(((Pair) obj).PairGetValue());
		}
		
	}
	
	private static final String REGEX_FOR_IMG = "src\\s*=\\s*\"(.*?)\"";
	private static final String REGEX_FOR_A = "href\\s*=\\s*\"(.*?)\"";
	private ArrayList<String> links;
	private ArrayList<Pair> storage;
	private BufferedReader bf;
	private String error;
	
	public PageScrapper(){
		error = "PageScrapper not yet called!";
		bf = null;
		links = new ArrayList<String>();
		storage = new ArrayList<Pair>();
	}
	
	public ArrayList<Pair> PageDownload(String URLstring){
		URL url;
		String page = "";
		error = new String("Success!");
		storage.clear();
		links.clear();
		
		URLstring = checkLink(URLstring,"https://");
		
		try {
			url = new URL(URLstring);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(2000);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
			con.connect();
			
			bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
			String temp;
			while ((temp = bf.readLine()) != null){
				page = page.concat(temp.trim());
			}
			bf.close();
		} catch (MalformedURLException mf){
			error = new String("Invalid URL!");
			return null;
		} catch (IOException ioe){
			//ioe.printStackTrace();
			error = new String("Connection Failed!");
			return null;
		}
		
		return parseList(page,URLstring);
	}
	
	public String PageGetError(){
		return error;
	}
	
	public Image PageGetImage(String Url){
		Image image = null;
		try {
			URL url = new URL(Url);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(5000);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");			
			con.connect();

			image = ImageIO.read(con.getInputStream());
		} catch (Exception e){
			//e.printStackTrace();
		}
	
		return image;
	} 
	
	private ArrayList<Pair> parseList(String page,String URString){
		parseTags(page);
		
		for (String s: links){
			if (s.length() < 5) continue;
			String link = null;
			if (s.substring(0, 3).equals("<a ")){
				Matcher mat = Pattern.compile(REGEX_FOR_A).matcher(s);
				if (mat.find()) link = mat.group(1);
				
				if (link != null) {
					String newS = checkLink(link,URString);
					Pair p = new Pair(newS, DATA_TYPE.LINK);
					if (newS != null && !storage.contains(p)) storage.add(p);
				}
			} else if(s.substring(0, 5).equals("<img ")){
				Matcher mat = Pattern.compile(REGEX_FOR_IMG).matcher(s);
				if (mat.find()) link = mat.group(1);

				if (link != null) {
					String newS = checkLink(link,URString);
					Pair p = new Pair(newS, DATA_TYPE.IMAGE);
					if (newS != null && !storage.contains(p)) storage.add(p);
				}
			}
		}
		
		return storage;
	}
	
	// deals with relative links
	private String checkLink(String link,String URString){
		if (link.startsWith("../")){
			int k = URString.lastIndexOf('/');
			if (k == -1) URString = ""; else URString = URString.substring(0,k);
			while (link.startsWith("../")){
				if (link.length() <= 3) break;
				link = link.substring(3);
				k = URString.lastIndexOf('/');
				if (k == -1) URString = ""; else URString = URString.substring(0,k);
			}
			return validateLink(URString + "/" + link);
		}
		if (link.startsWith("http")) return validateLink(link);
		if (link.startsWith("//")) return validateLink("https:" + link);
		if (link.charAt(0) == '/' || link.charAt(0) == '?') return validateLink(URString + link);
		if (link.startsWith("#") || link.startsWith("javascript")) return null; 
		return validateLink(URString.substring(0,URString.lastIndexOf('/')) + "/" + link);
	}
	
	private String validateLink(String link){
		try {
			new URL(link);
		} catch (MalformedURLException e) {
			return null;
		}
		return link;
	}	
	
	private void parseTags(String page){
		boolean parOpen = false;
		int old_i = 0;
		// separate TAGs
		for (int i=0; i<page.length(); i++){
			if (page.charAt(i) == '"') parOpen = !parOpen;
			if ((page.charAt(i) == '>') && !parOpen){
				links.add(page.substring(old_i, i+1));
				while (i<page.length() && page.charAt(i) != '<')
					i++;
				old_i = i;
			}
		}
	}
}
