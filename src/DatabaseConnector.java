import java.sql.*;

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
        }
        catch (Exception ex)
        {
            System.out.println("Unable to close connection");
        };
	}
	
}
