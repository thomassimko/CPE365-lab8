import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlWriter {
	
	FileWriter writer;
	
	String setup = "<!DOCTYPE html>\n<html>\n<head>\n";
	String setup2 = "\n<style>\ntable, th, td "
			+ "{\nborder: 1px solid black;\nborder-collapse: collapse;\n}\n</style>\n</head>\n<body>\n";

	public HtmlWriter(String ticker, String title) {

		try {
			writer = new FileWriter(new File(ticker + ".html"));
			writer.write(setup);
			
			if (title != null)
				addTitle(title);
			
			writer.write(setup2);
			
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void addTitle(String title) throws IOException {
		writer.write("<title>" + title + "</title>\n");
	}
	
	public void addHeading(String heading) {
		try {
			writer.write("<h3>" + heading + "</h3>\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSection(String heading) {
		try {
			writer.write("<h1>" + heading + "</h1>\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void addText(String text) {
		try {
			writer.write("<p>" + text + "</p>\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTable(String TableText) {
		try {
			writer.write(TableText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void publishHtml() {
		try {
			writer.write("</body>\n</html>");
			writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
