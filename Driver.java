
public class Driver {
	public static void main(String[] args) {
		
		Credentials cred = new Credentials();
		DatabaseConnector dc = new DatabaseConnector(cred.getLogin(), cred.getPass(), cred.getDB());
	}
}
