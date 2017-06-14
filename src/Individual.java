import java.sql.*;


public class Individual {
	
	private DatabaseConnector dc;
	private String ticker;
	
	String q1 = "SELECT s.ticker, s.name, MIN(p.DAY) as FirstDay, MAX(p.Day) as LastDay " +
			"FROM Securities s join Prices p on s.ticker=p.ticker WHERE s.ticker='?';";
	
	private String q2 = "SELECT tDays.year, tDays.ticker, s.name, p2.Close-p1.Open as PriceChange, " +
			"tDays.TotalVolume, tDays.AvgClose, tDays.AvgVol " +
			"FROM (Securities s join AdjustedPrices p1 on s.ticker=p1.ticker) join  " +
			"AdjustedPrices p2 on s.ticker=p2.ticker, " +
			"(SELECT s.ticker, YEAR(p.day) as year, MIN(p.DAY) as FirstDay, MAX(p.Day) " +
            "as LastDay, SUM(p.Volume) as TotalVolume, AVG(p.Close) as  " +
             "AvgClose, AVG(p.Volume) as AvgVol " +
             "FROM Securities s join AdjustedPrices p on s.ticker=p.ticker " +
             "WHERE s.ticker='?' " +
             "GROUP BY year) tDays " +
             "WHERE s.ticker='?' and p1.day=tDays.FirstDay and p2.day=tDays.LastDay;";
	
	private String q3 = "SELECT DATE_FORMAT(p.day, '%M') as Month, p.ticker, AVG(p.close) as AvgClose, MAX(p.high) " + 
			"as MonthlyHigh, MIN(p.low) as MonthlyLow, AVG(p.volume) as AvgVol " + 
			"FROM AdjustedPrices p " + 
			"WHERE p.ticker='?' and YEAR(p.day)>= ALL(SELECT DISTINCT YEAR(p.day) " + 
			"FROM AdjustedPrices p WHERE p.ticker='?') " + 
			"GROUP BY Month ORDER BY Month(p.Day);";
	
	private String q4;
	
	private String q5;
	
	private String q6;
	
	private String q7;
	
	private String q8;
	
	
	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	private ResultSet r5;
	private ResultSet r6;
	private ResultSet r7;
	private ResultSet r8;
	
	
	
	public Individual(DatabaseConnector dc, String ticker) 
	{
		this.dc = dc;
		this.ticker = ticker;
		
		query1();
		query2();
		query3();
		query4();
		query5();
		query6();
		query7();
		query8();
	}
	
	private void query1 () {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q1);
			ps.setString(0, ticker);
			r1 = ps.executeQuery();
			dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q1");
			ex.printStackTrace();
		}
	}
	private void query2() {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q2);
			ps.setString(0, ticker);
			ps.setString(1, ticker);
			r2 = ps.executeQuery();
			dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q2");
			ex.printStackTrace();
		}
	}
	private void query3() {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q3);
			ps.setString(0, ticker);
			ps.setString(1, ticker);
			r3 = ps.executeQuery();
			dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q3");
			ex.printStackTrace();
		}
	}
	private void query4() {
	
	}
	private void query5() {
	
	}
	private void query6() {
		
	}
	private void query7() {
		
	}
	private void query8() {
		
	}
	
	public ResultSet getR1() {
		return r1;
	}
	public ResultSet getR2() {
		return r2;
	}
	public ResultSet getR3() {
		return r3;
	}
	public ResultSet getR4() {
		return r4;
	}
	public ResultSet getR5() {
		return r5;
	}
	public ResultSet getR6() {
		return r6;
	}
	public ResultSet getR7() {
		return r7;
	}
	public ResultSet getR8() {
		return r8;
	}
	
}
