import java.sql.*;


public class General {

	private DatabaseConnector dc;

	String q1 = "SELECT Start, End, PriceInc, PriceDec " +
			"FROM " +
			"(SELECT COUNT(DISTINCT Ticker) as Start " +
			"FROM AdjustedPrices p " +
			"WHERE p.Day <= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2016) and " +
			"YEAR(Day) = 2016) q1, " +
			"(SELECT COUNT(DISTINCT Ticker) as End " +
			"FROM AdjustedPrices p " +
			"WHERE p.Day >= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2016) and " +
			"YEAR(Day) = 2016) q2, " +
			"(SELECT COUNT(DISTINCT p1.Ticker) as PriceInc " +
			"FROM AdjustedPrices p1, AdjustedPrices p2 " +
			"WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015  " +
			"and p2.Day >= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close < p2.Close) q3, " +
			"(SELECT COUNT(DISTINCT p1.Ticker) as PriceDec " +
			"FROM AdjustedPrices p1, AdjustedPrices p2 " +
			"WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and "
			+ "p2.Day >= ALL (SELECT Day from AdjustedPrices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close > p2.Close) q4;";

	private String q2 = "SELECT t1.ticker, t1.s as volumeTraded " +
			"FROM " +
			"(SELECT Ticker, SUM(p.volume) as s " +
			"FROM AdjustedPrices p " +
			"WHERE YEAR(p.day) = 2016 " +
			"GROUP BY Ticker " +
			"ORDER BY s DESC) t1, " +
			"(SELECT Ticker, SUM(p.volume) as s " +
			"FROM AdjustedPrices p " +
			"WHERE YEAR(p.day) = 2016 " +
			"GROUP BY Ticker) t2 " +
			"WHERE t1.s <= t2.s " +
			"GROUP BY t1.ticker, t1.s " +
			"HAVING count(*) <= 10 " +
			"ORDER BY volumeTraded DESC; ";

	private String q3 = "SELECT absYears.Year, absYears.Place, absYears.ticker as Absolute,  " +
			"relYears.ticker as Relative " +
			"FROM (SELECT abs1.year, abs1.ticker, abs1.Absolute, count(*) as Place " +
			"FROM (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute " +
			"FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen,  " +
			"max(day) as YearClose " +
			"FROM AdjustedPrices p " +
			"GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1  " +
			"on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker)) " +
			"JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and  " +
			"tDays.YearClose=ap2.day)) " +
			"GROUP BY year, ticker) abs1, " +
			"(SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute " +
			"FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen,  " +
			"max(day) as YearClose " +
			"FROM AdjustedPrices p " +
			"GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1  " +
			"on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker)) " +
			"JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and  " +
			"tDays.YearClose=ap2.day)) " +
			"GROUP BY year, ticker) abs2 " +
			"WHERE abs1.year=abs2.year and abs1.Absolute<=abs2.Absolute " +
			"GROUP BY abs1.year, abs1.ticker " +
			"HAVING Place <= 5 " +
			"ORDER BY abs1.year, abs1.Absolute, Place DESC " +
			") absYears " +
			"JOIN " +
			"(SELECT rel1.year, rel1.ticker, rel1.Relative, count(*) as Place " +
			"FROM (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative " +
			"FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day)  " +
			"as YearClose " +
			"FROM AdjustedPrices p " +
			"GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1  " +
			"on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker)) " +
			"JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and  " +
			"tDays.YearClose=ap2.day)) " +
			"GROUP BY year, ticker) rel1, " +
			"(SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative " +
			"FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day)  " +
			"as YearClose " +
			"FROM AdjustedPrices p " +
			"GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1  " +
			"on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker)) " +
			"JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and  " +
			"tDays.YearClose=ap2.day)) " +
			"GROUP BY year, ticker) rel2 " +
			"WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative " +
			"GROUP BY rel1.year, rel1.ticker " +
			"HAVING Place <= 5 " +
			"ORDER BY rel1.year, Place DESC " +
			")relYears on (absYears.year=relYears.Year and absYears.Place=relYears.Place) " +
			"ORDER BY year, Place;";

	private String q4 = "SELECT r1.ticker, r1.relativeGrowth " +
			"FROM " +
			"(SELECT t1.ticker, t2.close / t1.open as relativeGrowth " +
			"FROM " +
			"(SELECT p.ticker, p.day, p.open " +
			"FROM " +
			"AdjustedPrices p, " +
			"(SELECT ticker, min(day) as start, max(day) as end " +
			"FROM AdjustedPrices " +
			"WHERE YEAR(day) = 2016 " +
			"GROUP BY ticker) days " +
			"WHERE p.ticker = days.ticker and p.day = days.start) t1, " +
			"(SELECT p.ticker, p.day, p.close " +
			"FROM " +
			"AdjustedPrices p, " +
			"(SELECT ticker, min(day) as start, max(day) as end " +
			"FROM AdjustedPrices " +
			"WHERE YEAR(day) = 2016 " +
			"GROUP BY ticker) days " +
			"WHERE p.ticker = days.ticker and p.day = days.end) t2 " +
			"WHERE t1.ticker = t2.ticker " +
			"ORDER BY relativeGrowth DESC) r1, " +
			"(SELECT t1.ticker, t2.close / t1.open as relativeGrowth " +
			"FROM " +
			"(SELECT p.ticker, p.day, p.open " +
			"FROM AdjustedPrices p, " +
			"(SELECT ticker, min(day) as start, max(day) as end " +
			"FROM AdjustedPrices " +
			"WHERE YEAR(day) = 2016 " +
			"GROUP BY ticker) days " +
			"WHERE p.ticker = days.ticker and p.day = days.start) t1, " +
			"(SELECT p.ticker, p.day, p.close " +
			"FROM " +
			" AdjustedPrices p, " +
			"(SELECT ticker, min(day) as start, max(day) as end " +
			"FROM AdjustedPrices " +
			"WHERE YEAR(day) = 2016 " +
			"GROUP BY ticker) days " +
			"WHERE p.ticker = days.ticker and p.day = days.end) t2 " +
			" WHERE t1.ticker = t2.ticker " +
			"ORDER BY relativeGrowth DESC) r2 " +
			"WHERE r1.relativeGrowth <= r2.relativeGrowth " +
			"GROUP BY r1.ticker, r1.relativeGrowth " +
			"HAVING count(*) <= 10 " +
			"ORDER BY r1.relativeGrowth DESC;";

	private String q5; 



	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	private ResultSet r5;



	public General(DatabaseConnector dc) 
	{
		this.dc = dc;

		query1();
		query2();
		query3();
		query4();
		//query5();
	}

	private void query1 () {
		try {
			Statement ps = dc.getConnection().createStatement();
			r1 = ps.executeQuery(q1);
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q1");
			ex.printStackTrace();
		}
	}
	private void query2() {
		try {
			Statement ps = dc.getConnection().createStatement();
			r2 = ps.executeQuery(q2);
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q2");
			ex.printStackTrace();
		}
	}
	private void query3() {
		try {
			Statement ps = dc.getConnection().createStatement();
			r3 = ps.executeQuery(q3);
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q3");
			ex.printStackTrace();
		}
	}
	private void query4() {
		try {
			Statement ps = dc.getConnection().createStatement();
			r4 = ps.executeQuery(q4);
			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q4");
			ex.printStackTrace();
		}
	}
	private void query5() {
		try {
			Statement ps = dc.getConnection().createStatement();
			r5 = ps.executeQuery(q5);

			//dc.closeConnection();
		} catch (Exception ex) {
			System.out.println("Error executing Q5");
			ex.printStackTrace();
		}
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

}
