import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB_PREFERENCES {

	// same folder as our project
	private static final String DB_preferences = "DB_preferences.txt";
	private static final String KEY_REGEX = "(.*)=", VALUE_REGEX = "=(.*)";
	private String DB_name,DB_password,DB_link,DB_username,error;
	
	public DB_PREFERENCES() {
		error = null;
		parsePreferences();
	}

	private boolean parsePreferences(){
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DB_preferences));
			
			String tmp;
			while ((tmp = br.readLine()) != null){
				String key_str = null, value_str = null;
				Matcher mat = Pattern.compile(KEY_REGEX).matcher(tmp);
				mat.find();
				key_str = mat.group(1).trim();
				mat = Pattern.compile(VALUE_REGEX).matcher(tmp);
				mat.find();
				value_str = mat.group(1).trim();
				if (key_str.equals("DB_NAME")) DB_name = value_str;
				if (key_str.equals("DB_URL")) DB_link = value_str;
				if (key_str.equals("DB_USERNAME")) DB_username = value_str;
				if (key_str.equals("DB_PASSWORD")) DB_password = value_str;
			}
			
			br.close();
		} catch (FileNotFoundException fe){
			error = "DB Preferences file is missing!";
			return false;
		} catch (IOException ie){
			error = "Problem reading DB Preferences!";
			return false;
		}
		return true;
	}
	
	public String DBgetError(){
		return error;
	}
	public String DBgetName(){
		return DB_name;
	}
	public String DBgetURL(){
		return DB_link;
	}
	public String DBgetUsername(){
		return DB_username;
	}
	public String DBgetPassword(){
		return DB_password;
	}
	
}
