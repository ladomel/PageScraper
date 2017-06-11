import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PSDB {
	
	class PSDB_RESULT{
		private int id;
		private String query;
		private PageScrapper.DATA_TYPE type;
		private Timestamp date;
		
		public PSDB_RESULT(int id, String query,String type,Timestamp date){
			this.id = id;
			this.date = date;
			this.query = query;
			if (type != null) this.type = PageScrapper.DATA_TYPE.valueOf(type); else this.type = null;
		}
		
		public String PSDB_RESULT_getQuery(){
			return query;
		}
		public Timestamp PSDB_RESULT_getDate(){
			return date;
		}
		public PageScrapper.DATA_TYPE PSDB_RESULT_getType(){
			return type;
		}
		public int PSDB_RESULT_getId(){
			return id;
		}
		
	}
	
	private DB_PREFERENCES db_pref;
	private static final String CONNECTOR = "com.mysql.jdbc.Driver";
	private String QUERY_TABLE = "search_query";
	private String RESULTS_TABLE = "query_results";
	private String error;
	
	public PSDB(DB_PREFERENCES db_pref) {
		this.db_pref = db_pref;
		QUERY_TABLE = "`" + db_pref.DBgetName() + "`." + QUERY_TABLE;
		RESULTS_TABLE = "`" + db_pref.DBgetName() + "`." + RESULTS_TABLE;
	}
	
	public boolean PSDB_AddQuery(String query, ArrayList<PageScrapper.Pair> results){
		Connection con = null;
		if ((con = openConnection()) == null) return false;
		
		try {
			PreparedStatement pst;
			pst = con.prepareStatement("INSERT INTO " + QUERY_TABLE + " ( query, time ) VALUES ( ? , NOW());");
			pst.setString(1, query);
			pst.executeUpdate();
			int id = getIndex(con,query);
			System.out.println(id);
			for (PageScrapper.Pair res : results){
				pst = con.prepareStatement(
						"INSERT INTO " + RESULTS_TABLE + " ( query_id , link , type ) VALUES ( ? , ? , ? );");
				pst.setInt(1, id);
				pst.setString(2, res.PairGetValue());
				pst.setString(3, res.PairGetType().toString());
				pst.executeUpdate();
			}
			closeConnection(con);
		} catch (Exception e){
			closeConnection(con);
		}
			
		return true;
	}
	
	private Connection openConnection(){
		Connection con = null;
		try {
			Class.forName(CONNECTOR);
			con = DriverManager.getConnection(
					"jdbc:mysql://" + db_pref.DBgetURL(), db_pref.DBgetUsername() , db_pref.DBgetPassword());
		} catch (ClassNotFoundException ce){
			error = "Connector Problem!";
		} catch (Exception e) {
			error = "Can't connect to DB!";
		}
		return con;
	}
	
	private boolean closeConnection(Connection con){
		try {
			con.close();
		} catch (SQLException e1) {
			error = "Can't Close Connection!";
			return false;
		}
		return true;
	}
	
	private int getIndex(Connection con, String query){
		try {
			PreparedStatement pst = con.prepareStatement(
					"SELECT COUNT(*) FROM " + QUERY_TABLE + ";");
			ResultSet rs = pst.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public ArrayList<PSDB.PSDB_RESULT> PSDB_GetQueryResults(int id){
		ArrayList<PSDB.PSDB_RESULT> res = new ArrayList<PSDB.PSDB_RESULT>();
		Connection con = null;
		if ((con = openConnection()) == null) return null;
		
		try {
			PreparedStatement pst = con.prepareStatement(
					"SELECT * FROM " + RESULTS_TABLE + " WHERE query_id = ( ? );");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()){
				res.add(new PSDB.PSDB_RESULT(-1,rs.getString(3),rs.getString(4),null));
			}
			
			closeConnection(con);
		} catch (Exception e){
			closeConnection(con);
			System.out.println("asd");
			return null;
		}
		
		return res;
	}
	
	public ArrayList<PSDB.PSDB_RESULT> PSDG_GetQueries(){
		ArrayList<PSDB.PSDB_RESULT> res = new ArrayList<PSDB.PSDB_RESULT>();
		Connection con = null;
		if ((con = openConnection()) == null) return null;
		
		try {
			PreparedStatement pst = con.prepareStatement(
					"SELECT * FROM " + QUERY_TABLE + ";");
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()){
				res.add(new PSDB.PSDB_RESULT(rs.getInt(1),rs.getString(2),null,rs.getTimestamp(3)));
			}
			
			closeConnection(con);
		} catch (Exception e){
			closeConnection(con);
			System.out.println("asdasd");
			return null;
		}
		
		return res;
	}
	
	public String PSDB_GetError(){
		return error;
	}
	
}
