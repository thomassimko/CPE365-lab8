/*
 * Mitchel Davis
 * Thomas Simko
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseConnector {

	private static Connection conn = null;
	private String url;
	
	public DatabaseConnector(String login, String pass, String db) {
		url = "jdbc:mysql://cslvm74.csc.calpoly.edu/" + db + "?user=" + login + "&password=" + pass;
		if (conn == null)
		{
			establishConnection();
		}
	}

	private void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Error fetching driver");
			ex.printStackTrace();
		};
	
		try {
			conn = DriverManager.getConnection(url);
		} catch (Exception ex) {
			System.out.println("Error establishing connection");
			ex.printStackTrace();
		};

		System.out.println("Connected");
	}
	
	public void closeConnection() {
		try {
            conn.close();
            conn = null;
        }
        catch (Exception ex)
        {
            System.out.println("Unable to close connection");
        };
	}
	
	public Connection getConnection() {
		if (conn == null)
		{
			establishConnection();
		}
		return conn;
	}
	
	public List<HashMap<String, Object>> resultSetToTuples(ResultSet rs)
	{
		List<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();

		try {
			ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
			int col = md.getColumnCount();
	
			while (rs.next()) {
				HashMap<String, Object> row = new HashMap<String, Object>(col);
				for(int i = 1; i <= col; ++i) {
					
					Object obj = rs.getObject(i);
					row.put(md.getColumnName(i), obj);
				}
				result.add(row);
			}
	
			rs.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public List<List<String>> tuplesToList(List<HashMap<String, Object>> tuples, List<String> columns) {
		ArrayList<List<String>> ret = new ArrayList<List<String>>();
		for(HashMap<String,Object> tuple : tuples) {
			
			ret.add(tupleToString(tuple, columns));
		}
		return ret;
	}
	private List<String> tupleToString(HashMap<String,Object> tuple, List<String> columns) {
		ArrayList<String> ret = new ArrayList<String>();
		for(String key: columns) {
			ret.add(tuple.get(key).toString());
		}
		return ret;
	}
	
	public List<String> getColumnNames(ResultSet rs) {
		ArrayList<String> columns = new ArrayList<String>();
		ResultSetMetaData md;
		try {
			md = (ResultSetMetaData) rs.getMetaData();
			int col = md.getColumnCount();
			for(int i = 1; i <= col; ++i) {
				columns.add(md.getColumnName(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columns;
	}
	
}
