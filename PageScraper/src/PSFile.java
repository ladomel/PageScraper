import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PSFile {
	
	private String error;
	
	public PSFile(){
		error = "";
	}
	
	public boolean PSFileSave(ArrayList<PageScrapper.Pair> links, File file, String search){
		PrintWriter pw;
		if (file == null){
			error = "No File Chosen!";
			return false;
		}
		
		try {
			pw = new PrintWriter(file);
			
			pw.println("Scrape Results for \"" + search + "\":");
			pw.println();
			
			if (links != null){
				int i = 1;
				for (PageScrapper.Pair s : links){
					pw.println("" + i + ") [" + s.PairGetType() + "] " + s.PairGetValue());
					i++;
				}
			}
			
			pw.close();
		} catch (FileNotFoundException fe){
			error = "File not found!";
			return false;
		}
		
		error = "Saved!";
		return true;
	}
	
	public String PSFileGetError(){
		return error;
	}
	
}
