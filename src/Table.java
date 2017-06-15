/*
 * Mitchel Davis
 * Thomas Simko
 */
import java.util.List;

public class Table {
	
	StringBuilder out;

	public Table() {

		out = new StringBuilder();
		out.append("<table style=\"width:100%\">\n");
	}
	
	public void addColumns(List<String> columnNames) {
		
		for (String name : columnNames) {
			out.append("\t\t<th>" + name + "</th>\n");
		}
		out.append("\t</tr>\n\n");
	}
	
	public void addRow(List<String> values) {
		for (String name : values)
		{
			out.append("\t\t<td>" + name + "</td>\n");
		}
		out.append("\t</tr>\n\n");
	}
	
	
	public String getTable() {
		out.append("</table>\n");
		return out.toString();
	}
	
	
	
}
