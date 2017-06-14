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
	
	private String q4 = "SELECT stats.*, AVG(oSect.Price) as AvgOpen, AVG(cSect.Price) as AvgClose,
			AVG(cSect.Price-oSect.Price) as AvgAbsChange,
			100*AVG(cSect.Price/oSect.Price) as AvgRelChange
			FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
			LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow, 
		    AVG(ap.high) as MonthlyHigh
		    FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
		    WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
		    FROM Securities s WHERE s.ticker='KLAC') and ap.day<'2017-06-13'
		    GROUP BY s.Sector,year, month) stats 
		    LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
		    FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
		    WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
		    FROM Securities s WHERE s.ticker='KLAC') and ap.day<'2017-06-13') oSect
		    ON oSect.day=stats.FirstDay) LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
		    FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
		    WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
		    FROM Securities s WHERE s.ticker='KLAC') and ap.day<'2017-06-13') cSect
		    ON cSect.day=stats.LastDay)
		    GROUP BY stats.year, stats.month;
	
	private String q5 = "SELECT stats.*, AVG(open.Price) as AvgOpen, AVG(close.Price) as AvgClose, " +
			"AVG(close.Price-open.Price) as AvgAbsChange, " +
			"100*AVG(close.Price/open.Price) as AvgRelChange " +
			"FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as  " +
			"LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow,  " +
		    "AVG(ap.high) as MonthlyHigh FROM AdjustedPrices ap WHERE ap.ticker='?' and ap.day<'?' " +
		    "GROUP BY year, month) stats LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price " +
		    "FROM AdjustedPrices ap WHERE ap.ticker='?'  and ap.day<'?' ) open " +
		    "ON open.day=stats.FirstDay) " +
		    "LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price " +
		    "FROM AdjustedPrices ap  " +
		    "WHERE ap.ticker='?' and ap.day<'?') close " +
		    "ON close.day=stats.LastDay) " +
		    "GROUP BY stats.year, stats.month;";
	
	private String q6;
	
	private String q7;
	
	private String q8;
	
	
	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	private ResultSet r5d1;
	private ResultSet r5d2;
	private ResultSet r5d3;
	private ResultSet r5d4;
	private ResultSet r5d5;
	private ResultSet r5d6;
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
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q5);
			ps.setString(0, ticker);
			ps.setString(1, ticker);
			ps.setString(2, ticker);
			ps.setDate(0, java.sql.Date.valueOf("2015-01-01"));
			ps.setDate(1, java.sql.Date.valueOf("2015-01-01"));
			ps.setDate(2, java.sql.Date.valueOf("2015-01-01"));
			r5d1 = ps.executeQuery();
			
			ps.setDate(0, java.sql.Date.valueOf("2015-06-01"));
			ps.setDate(1, java.sql.Date.valueOf("2015-06-01"));
			ps.setDate(2, java.sql.Date.valueOf("2015-06-01"));
			r5d2 = ps.executeQuery();
			
			ps.setDate(0, java.sql.Date.valueOf("2015-10-01"));
			ps.setDate(1, java.sql.Date.valueOf("2015-10-01"));
			ps.setDate(2, java.sql.Date.valueOf("2015-10-01"));
			r5d3 = ps.executeQuery();
			
			ps.setDate(0, java.sql.Date.valueOf("2016-01-01"));
			ps.setDate(1, java.sql.Date.valueOf("2016-01-01"));
			ps.setDate(2, java.sql.Date.valueOf("2016-01-01"));
			r5d4 = ps.executeQuery();
			
			ps.setDate(0, java.sql.Date.valueOf("2016-05-01"));
			ps.setDate(1, java.sql.Date.valueOf("2016-05-01"));
			ps.setDate(2, java.sql.Date.valueOf("2016-05-01"));
			r5d5 = ps.executeQuery();
			
			ps.setDate(0, java.sql.Date.valueOf("2016-10-01"));
			ps.setDate(1, java.sql.Date.valueOf("2016-10-01"));
			ps.setDate(2, java.sql.Date.valueOf("2016-10-01"));
			r5d6 = ps.executeQuery();
			
			dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q5");
			ex.printStackTrace();
		}
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
	public ResultSet getR5D1() {
		return r5d1;
	}
	public ResultSet getR5D2() {
		return r5d2;
	}
	public ResultSet getR5D3() {
		return r5d3;
	}
	public ResultSet getR5D4() {
		return r5d4;
	}
	public ResultSet getR5D5() {
		return r5d5;
	}
	public ResultSet getR5D6() {
		return r5d6;
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
