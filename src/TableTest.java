import java.util.ArrayList;

import junit.framework.TestCase;


public class TableTest extends TestCase {
	
	public void test()
	{
		
		HtmlWriter html = new HtmlWriter("test", "This is my title");
		
		Table table = new Table();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		list.add("test3");
		
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("data1");
		list2.add("data2");
		list2.add("data3");
		
		table.addColumns(list);
		table.addRow(list2);
		
		html.addHeading("This is my heading");
		html.addText("text1");
		
		html.addTable(table.getTable());
		
		html.addText("text2");
		
		html.addHeading("This is my second heading");
		html.addText("text3");
		html.publishHtml();
	}
}
