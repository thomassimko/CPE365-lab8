


//KLAC, DISCK, NWL, SYY


public class Driver {
	public static void main(String[] args) {
		
		System.out.println(args[0]);
		String ticker = "'" + args[0] + "'";
		
		Credentials cred = new Credentials();
		DatabaseConnector dc = new DatabaseConnector(cred.getLogin(), cred.getPass(), cred.getDB());
		
		
		Individual ind = new Individual(dc, ticker);
	}
}
