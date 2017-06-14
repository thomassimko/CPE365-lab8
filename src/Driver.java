import java.util.List;




//KLAC, DISCK, NWL, SYY


public class Driver {
	public static void main(String[] args) {
		
		System.out.println(args[0]);
		String ticker = args[0];
		
		Credentials cred = new Credentials();
		DatabaseConnector dc = new DatabaseConnector(cred.getLogin(), cred.getPass(), cred.getDB());
		
		
		Individual ind = new Individual(dc, ticker);
		General gen = new General(dc);
		HtmlWriter html = new HtmlWriter(args[0], "KLAC Analysis");
		
		html.addSection("General");
		
		//Query 1
		
		html.addHeading("1. 2016 Security Trading Breakdown");
		Table g1 = new Table();
		List<String> g1Columns = dc.getColumnNames(gen.getR1());
		g1.addColumns(g1Columns);
		List<List<String>> grs1 = dc.tuplesToList(dc.resultSetToTuples(gen.getR1()), g1Columns);
		for (List<String> tuple : grs1) {
			g1.addRow(tuple);
		}
		html.addTable(g1.getTable());
		html.addText("Reports the total number of securities traded at the start of 2016, total number of securities traded at the end of 2016, total number " 
				+ "of securities whose prices saw increase between the end of 2015 and the end of 2016, and the total number of securities whose prices saw "
				+ "decrease between the end of 2015 and the end of 2016.");
		
		
		//Query 2
		
				html.addHeading("2. 10 Most Traded Stocks of 2016");
				Table g2 = new Table();
				List<String> g2Columns = dc.getColumnNames(gen.getR2());
				g2.addColumns(g2Columns);
				List<List<String>> grs2 = dc.tuplesToList(dc.resultSetToTuples(gen.getR2()), g2Columns);
				for (List<String> tuple : grs2) {
					g2.addRow(tuple);
				}
				html.addTable(g2.getTable());
				html.addText("");
		
		
		
		
		
		
		
		html.addSection("Individual");
		//Query 1
		
		html.addHeading("1. " + ticker + " Pricing Date Range");
		Table t1 = new Table();
		List<String> t1Columns = dc.getColumnNames(ind.getR1());
		t1.addColumns(t1Columns);
		List<List<String>> rs1 = dc.tuplesToList(dc.resultSetToTuples(ind.getR1()), t1Columns);
		for (List<String> tuple : rs1) {
			t1.addRow(tuple);
		}
		html.addTable(t1.getTable());
		html.addText("For the given ticker, displays the name of the ticker and the first day of sales and last day of sales in the database.");
		
		//Query 2
		
		html.addHeading("2. " + ticker + " Stock Performance Per Year");
		Table t2 = new Table();
		List<String> t2Columns = dc.getColumnNames(ind.getR2());
		t2.addColumns(t2Columns);
		List<List<String>> rs2 = dc.tuplesToList(dc.resultSetToTuples(ind.getR2()), t2Columns);
		for (List<String> tuple : rs2) {
			t2.addRow(tuple);
		}
		html.addTable(t2.getTable());
		html.addText("Reports the increase/decrease in prices year-over-year, volume of trading, average closing price in a given year, average trade volume per day.");
		
		//Query 3
		
		html.addHeading("3. " + ticker + " 2016 Monthly Breakdown");
		Table t3 = new Table();
		List<String> t3Columns = dc.getColumnNames(ind.getR3());
		t3.addColumns(t3Columns);
		List<List<String>> rs3 = dc.tuplesToList(dc.resultSetToTuples(ind.getR3()), t3Columns);
		for (List<String> tuple : rs3) {
			t3.addRow(tuple);
		}
		html.addTable(t3.getTable());
		html.addText("Reports the average closing price, the highest and the lowest price, the average daily trading volume by month.");
		
		
		//Query 4
		html.addHeading("4. " + ticker + " Best Month Per Year");
		Table t4 = new Table();
		List<String> t4Columns = dc.getColumnNames(ind.getR4());
		t4.addColumns(t4Columns);
		List<List<String>> rs4 = dc.tuplesToList(dc.resultSetToTuples(ind.getR4()), t4Columns);
		for (List<String> tuple : rs4) {
			t4.addRow(tuple);
		}
		html.addTable(t4.getTable());
		html.addText("");
		
		html.publishHtml();
	}
}
