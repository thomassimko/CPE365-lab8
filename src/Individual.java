import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Individual {
	
	private DatabaseConnector dc;
	private String ticker;
	
	String q1 = "SELECT s.ticker, s.name, MIN(p.DAY) as FirstDay, MAX(p.Day) as LastDay " +
			"FROM Securities s join Prices p on s.ticker=p.ticker WHERE s.ticker=?";
	
	private String q2 = "SELECT tDays.year, tDays.ticker, s.name, p2.Close-p1.Open as PriceChange, " +
			"tDays.TotalVolume, tDays.AvgClose, tDays.AvgVol " +
			"FROM (Securities s join AdjustedPrices p1 on s.ticker=p1.ticker) join  " +
			"AdjustedPrices p2 on s.ticker=p2.ticker, " +
			"(SELECT s.ticker, YEAR(p.day) as year, MIN(p.DAY) as FirstDay, MAX(p.Day) " +
            "as LastDay, SUM(p.Volume) as TotalVolume, AVG(p.Close) as  " +
             "AvgClose, AVG(p.Volume) as AvgVol " +
             "FROM Securities s join AdjustedPrices p on s.ticker=p.ticker " +
             "WHERE s.ticker=? " +
             "GROUP BY year) tDays " +
             "WHERE s.ticker=? and p1.day=tDays.FirstDay and p2.day=tDays.LastDay";
	
	private String q3 = "SELECT DATE_FORMAT(p.day, '%M') as Month, p.ticker, AVG(p.close) as AvgClose, MAX(p.high) " + 
			"as MonthlyHigh, MIN(p.low) as MonthlyLow, AVG(p.volume) as AvgVol " + 
			"FROM AdjustedPrices p " + 
			"WHERE p.ticker=? and YEAR(p.day)>= ALL(SELECT DISTINCT YEAR(p.day) " + 
			"FROM AdjustedPrices p WHERE p.ticker=?) " + 
			"GROUP BY Month ORDER BY Month(p.Day)";
	
	private String q4 = "SELECT counts.year, counts.month FROM (SELECT stock.year, stock.month, " +
	"stock.AvgRelChange-industry.AvgRelChange as Difference " +
    "FROM (SELECT stats.year, stats.month, 100-100*AVG(close.Price/open.Price) as AvgRelChange " +
    "FROM (((SELECT ap.ticker, Year(ap.day) as year, MONTHNAME(ap.day) as month, MIN(ap.day) " +
    "as FirstDay,MAX(ap.day) as LastDay FROM AdjustedPrices ap WHERE ap.ticker=? " +
    "GROUP BY year, month) stats LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price " +
    "FROM AdjustedPrices ap WHERE ap.ticker=? ) open ON open.day=stats.FirstDay) " +
    "LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price FROM AdjustedPrices ap " +
    "WHERE ap.ticker=?) close ON close.day=stats.LastDay) GROUP BY stats.year, stats.month) stock " +
    "JOIN (SELECT stats.year, stats.month, 100-100*AVG(cSect.Price/oSect.Price) as AvgRelChange " +
    "FROM (((SELECT s.Sector, Year(ap.day) as year, MONTHNAME(ap.day) as month, MIN(ap.day) " +
    "as FirstDay,MAX(ap.day) as LastDay FROM (AdjustedPrices ap JOIN Securities s " +
    "on s.ticker=ap.ticker) WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry " +
    "FROM Securities s WHERE s.ticker=?) GROUP BY s.Sector,year, month) stats " +
    "LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price FROM AdjustedPrices ap " +
    "JOIN Securities s on s.ticker=ap.ticker WHERE (s.Sector, s.Industry) = " +
    "(SELECT s.Sector, s.Industry FROM Securities s WHERE s.ticker=?)) oSect " +
    "ON oSect.day=stats.FirstDay) LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price " +
    "FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker " +
    "WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry FROM Securities s " +
    "WHERE s.ticker=?) ) cSect ON cSect.day=stats.LastDay) " +
    "GROUP BY stats.year, stats.month) industry on (stock.year=industry.year and " +
    "stock.month=industry.month) GROUP BY stock.year, stock.month) counts, " +
    "(SELECT counts.year, MAX(counts.Difference) as max FROM " +
    "(SELECT stock.year, stock.month, stock.AvgRelChange-industry.AvgRelChange as Difference " +
    "FROM (SELECT stats.year, stats.month, 100-100*AVG(close.Price/open.Price) as AvgRelChange " +
    "FROM (((SELECT ap.ticker, Year(ap.day) as year, MONTHNAME(ap.day) as month, MIN(ap.day) as " +
    "FirstDay,MAX(ap.day) as LastDay FROM AdjustedPrices ap WHERE ap.ticker=? " +
    "GROUP BY year, month) stats  LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price " +
    "FROM AdjustedPrices ap WHERE ap.ticker=? ) open ON open.day=stats.FirstDay) " +
    "LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price FROM AdjustedPrices ap " +
    "WHERE ap.ticker=?) close ON close.day=stats.LastDay) GROUP BY " +
    "stats.year, stats.month) stock JOIN  (SELECT stats.year, stats.month, " +
    "100-100*AVG(cSect.Price/oSect.Price) as AvgRelChange FROM (((SELECT s.Sector, Year(ap.day) as " +
    "year, MONTHNAME(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as  LastDay " +
    "FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker) WHERE (s.Sector, s.Industry) " +
    "= (SELECT s.Sector, s.Industry FROM Securities s WHERE s.ticker=?) " +
    "GROUP BY s.Sector,year, month) stats  LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, " +
    "ap.Open as Price FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker " +
    "WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry " +
    "FROM Securities s WHERE s.ticker=?)) oSect ON oSect.day=stats.FirstDay) " +
    "LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price " +
    "FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker " +
    "WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry FROM Securities s " +
    "WHERE s.ticker=?) ) cSect ON cSect.day=stats.LastDay) " +
    "GROUP BY stats.year, stats.month) industry on (stock.year=industry.year and stock.month=industry.month) " +
    "GROUP BY stock.year, stock.month) counts GROUP BY counts.year) max " +
	"where max.max=counts.Difference group by counts.year;";
	
	private String q5 = "SELECT sector.year, sector.month, stock.StockChange, " +
	"sector.SectorChange, " +
    "stock.StockChange-sector.SectorChange as Difference " + 
    "FROM (SELECT stats.year, stats.month, 100-100*AVG(close.Price/open.Price) as StockChange " +
    "FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as " +
    "FirstDay,MAX(ap.day) as LastDay " +
    "FROM AdjustedPrices ap WHERE ap.ticker=? and ap.day<? " + 
    "GROUP BY year, month) stats  LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price " +
    "FROM AdjustedPrices ap WHERE ap.ticker=?  and ap.day<? ) open " +
    "ON open.day=stats.FirstDay) LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price " +
    "FROM AdjustedPrices ap WHERE ap.ticker=? and ap.day<?) close " +
    "ON close.day=stats.LastDay) GROUP BY stats.year, stats.month) stock JOIN " +
    "(SELECT stats.year, stats.month, 100-100*AVG(cSect.Price/oSect.Price) as SectorChange " +
    "FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as " +
    "FirstDay,MAX(ap.day) as LastDay FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker) " +
    "WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry FROM Securities s WHERE " +
    "s.ticker=?) and ap.day<? GROUP BY s.Sector,year, month) stats " +
    "LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price " +
    "FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker WHERE (s.Sector, s.Industry) " +
    "= (SELECT s.Sector, s.Industry FROM Securities s WHERE s.ticker=?) and ap.day<?) " +
    "oSect ON oSect.day=stats.FirstDay) LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price " +
    "FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker WHERE (s.Sector, s.Industry) = " +
    "(SELECT s.Sector, s.Industry FROM Securities s WHERE s.ticker=?) and ap.day<?) " +
    "cSect ON cSect.day=stats.LastDay) GROUP BY stats.year, stats.month) sector on " +
    "(sector.year=stock.year and sector.month=stock.month);";
	
	private String q7;
	
	private String q8;
	
	
	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	
	private HashMap<String,List<HashMap<String,Object>>> r5;
	private HashMap<String,List<HashMap<String,Object>>> r6;

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
			ps.setString(1, ticker);
			r1 = ps.executeQuery();
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q1");
			ex.printStackTrace();
		}
	}
	private void query2() {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q2);
			ps.setString(1, ticker);
			ps.setString(2, ticker);
			r2 = ps.executeQuery();
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q2");
			ex.printStackTrace();
		}
	}
	private void query3() {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q3);
			ps.setString(1, ticker);
			ps.setString(2, ticker);
			r3 = ps.executeQuery();
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q3");
			ex.printStackTrace();
		}
	}
	private void query4() {
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q4);
			ps.setString(1, ticker);
			ps.setString(2, ticker);
			ps.setString(3, ticker);
			ps.setString(4, ticker);
			ps.setString(5, ticker);
			ps.setString(6, ticker);
			ps.setString(7, ticker);
			ps.setString(8, ticker);
			ps.setString(9, ticker);
			ps.setString(10, ticker);
			ps.setString(11, ticker);
			ps.setString(12, ticker);
			r4 = ps.executeQuery();
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q4");
			ex.printStackTrace();
		}
	}
	private void query5() {
		r5 = new HashMap<String,List<HashMap<String,Object>>>();
		String date = "";

		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q5);
			ps.setString(1, ticker);
			ps.setString(3, ticker);
			ps.setString(5, ticker);
			ps.setString(7, ticker);
			ps.setString(9, ticker);
			ps.setString(11, ticker);
			
			date="2015-01-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2015-06-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2015-10-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-01-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-05-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-10-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			r5.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q5");
			ex.printStackTrace();
		}
	}
	private void query6() {
		r6 = new HashMap<String, List<HashMap<String,Object>>>();
		String date = "";
		try {
			PreparedStatement ps = dc.getConnection().prepareStatement(q5);
			ps.setString(1, ticker);
			ps.setString(3, ticker);
			ps.setString(5, ticker);
			ps.setString(7, ticker);
			ps.setString(9, ticker);
			ps.setString(11, ticker);
			
			date="2015-04-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2015-01-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2015-09-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2015-06-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-01-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2015-10-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-04-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2016-01-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2016-08-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2016-05-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			date="2017-01-01";
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(4, java.sql.Date.valueOf(date));
			ps.setDate(6, java.sql.Date.valueOf(date));
			ps.setDate(8, java.sql.Date.valueOf(date));
			ps.setDate(10, java.sql.Date.valueOf(date));
			ps.setDate(12, java.sql.Date.valueOf(date));
			date="2016-10-01";
			r6.put(date,dc.resultSetToTuples(ps.executeQuery()));
			
			
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q6");
			ex.printStackTrace();
		}
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
	public HashMap<String, List<HashMap<String,Object>>> getR5() {
		return r5;
	}
	public HashMap<String, List<HashMap<String,Object>>> getR6() {
		return r6;
	}
	
	public ResultSet getR7() {
		return r7;
	}
	public ResultSet getR8() {
		return r8;
	}
	
}
