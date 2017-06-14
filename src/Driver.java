import java.util.List;




//KLAC, DISCK, NWL, SYY


public class Driver {
	public static void main(String[] args) {
		
		System.out.println(args[0]);
		String ticker = args[0];
		
		Credentials cred = new Credentials();
		DatabaseConnector dc = new DatabaseConnector(cred.getLogin(), cred.getPass(), cred.getDB());
		
		
		Individual ind = new Individual(dc, ticker);
		HtmlWriter html = new HtmlWriter(args[0], "Individual Analysis");
		
		//Query 1
		
		Table t1 = new Table();
		t1.addColumns(dc.getColumnNames(ind.getR1()));
		List<List<String>> rs1 = dc.tuplesToList(dc.resultSetToTuples(ind.getR1()));
		for (List<String> tuple : rs1) {
			t1.addRow(tuple);
		}
		html.addTable(t1.getTable());
		
		//Query 2
		
		Table t2 = new Table();
		t2.addColumns(dc.getColumnNames(ind.getR2()));
		List<List<String>> rs2 = dc.tuplesToList(dc.resultSetToTuples(ind.getR2()));
		for (List<String> tuple : rs2) {
			t2.addRow(tuple);
		}
		html.addTable(t2.getTable());
		
		
		
		
		html.publishHtml();
		
		Table t4 = new Table();
		t4.addColumns(dc.getColumnNames(ind.getR4()));
		List<List<String>> rs4 = dc.tuplesToList(dc.resultSetToTuples(ind.getR4()));
		for (List<String> tuple : rs4) {
			t4.addRow(tuple);
		}
		html.addTable(t4.getTable());
		
		
		
		
		html.publishHtml();
		
	}
}
