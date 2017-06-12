import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Table {
	
	List<String> columnNames;
	FileWriter writer;
	
	String setup = "<!DOCTYPE html>\n<html>\n<head>\n<style>\ntable, th, td "
			+ "{\nborder: 1px solid black;\nborder-collapse: collapse;\n}\n</style>\n</head>\n<body>\n";

	public Table(String ticker) {

		try {
			writer = new FileWriter(new File(ticker + ".html"));
			writer.write(setup);
			writer.write("<table style=\"width:100%\">\n\t<tr>\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void addColumns(List<String> columnNames) {
		this.columnNames = columnNames;
		
		try {
			for (String name : columnNames)
			{
				writer.write("\t\t<th>" + name + "</th>\n");
				writer.flush();
			}
			writer.write("\t</tr>\n\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRow(List<String> values) {
		try {
			for (String name : columnNames)
			{
				writer.write("\t\t<td>" + name + "</td>\n");
				writer.flush();
			}
			writer.write("\t</tr>\n\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void finishTable() {
		try {
			writer.write("</table>\n</body>\n</html>");
			writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
