/*
 * Mitchel Davis
 * Thomas Simko
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class Credentials {
	
	private String db;
	private String login;
	private String pass;
	
	public Credentials() {
		loadCredentials();
	}
	
	private void loadCredentials() {
		Scanner scan;
		try {
			scan = new Scanner(new File("credentials.in"));
			login = scan.nextLine();
			pass = scan.nextLine();
			db = scan.nextLine();
			
		} catch (FileNotFoundException e) {
			System.out.println("unable to load credntials");
			e.printStackTrace();
		}
	}

	public String getDB() {
		return db;
	}
	public String getLogin() {
		return login;
	}
	public String getPass() {
		return pass;
	}
}
