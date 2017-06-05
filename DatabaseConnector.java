import java.sql.*;

public class DatabaseConnector {

	private static Connection conn = null;
	private String url = "jdbc:mysql://cslvm74.csc.calpoly.edu/tjsimko?user=tjsimko&password=dekhtyar";
	
	public DatabaseConnector() {
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
	
	public void doSomething() {
		
	}
	
}
