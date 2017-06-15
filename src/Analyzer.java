/*
 * Mitchel Davis
 * Thomas Simko
 */
import java.util.*;

public class Analyzer {
	public List<List<String>> table5;
	
	public List<List<String>> gen5(List<HashMap<String, Object>> tuples) {
		
		String report = "";
		
		List<List<String>> ret = new ArrayList<List<String>>();
		for(HashMap<String, Object> tuple : tuples) {
			List<String> newList = new ArrayList<String>();
			
			double ratio = (double) tuple.get("Ratio");
			double totalratio = (double) tuple.get("totalratio");
			double diff = (double) tuple.get("Difference");
			double totaldiff = (double) tuple.get("totaldiff");
			
			double r = ratio - totalratio;
			double d = diff - totaldiff;
			
			
			if(r < -.1) {
				report = "Tanking it.";
			}
			else if(r < -.05) {
				report = "Diminishing returns.";
			}
			else if (r < 0) {
				report = "Trying to hold on.";
			}
			else if (r < .05) {
				report = "Showing resilience.";
			}
			else if (r < .08) {
				report = "Slowly growing.";
			}
			else if (r < .12) {
				report = "Doing quite well.";
			}
			else if (r < .15) {
				report = "Rapid growth.";
			}
			else
				report = "Significantly Outperforming.";
			
			newList.add(tuple.get("Sector").toString());
			newList.add(report);

			ret.add(newList);
		}
		return ret;
	}
	public List<List<String>> ind5(HashMap<String,List<HashMap<String, Object>>> tables) {
		ArrayList<List<String>> returnTable = new ArrayList<List<String>>();
		ArrayList<String> row; 
		for(String table : tables.keySet()) {
			row = new ArrayList<String>();
			
			// add the date for the BHS prediction
			row.add(table);
			
			//Generate the BHS signal for the date and add it to the row
				//Get the corresponding table for the date (table)
			List<HashMap<String, Object>> aTable = tables.get(table);
			
			//Iterate through the last six tuples in the table and 
			// generate a BHS signal for each tuple and add that 
			// value to the aggregate BHS signal
			int aggBHS = 0;
			
			for(int i = aTable.size()-6; i < aTable.size(); i++) {
				HashMap<String, Object> tuple = aTable.get(i);
				
				int year = (int)tuple.get("year");
				int month = (int)tuple.get("month");
				double stockChange = (double)tuple.get("StockChange");
				double diff = (double)tuple.get("Difference");
				
				int stockBHS = getBHSSignalSingle(stockChange);
				int diffBHS = getBHSSignalSingle(diff);
				aggBHS += getBHSSignal(stockBHS, diffBHS);
				
			}
			row.add(getAggBHSSignal(aggBHS));
			returnTable.add(row);
		}
		table5=returnTable;
		return returnTable;
	}
	private int getBHSSignal(int stock, int diff) {
		int sum = stock + diff;
		if(sum > 0)
			return 1;
		else if (sum == 0)
			return 0;
		else
			return -1;
	}
	private int getBHSSignalSingle(double num) {
		if(Math.abs(num)<1.5)
			return 0;
		else 
			return num>0 ? 1 : -1;
	}
	private String getAggBHSSignal(int num) {
		if(num >= 4)
			return "Buy";
		else if(num >= -3)
			return "Hold";
		else
			return "Sell";
	}
	public List<List<String>> ind6(HashMap<String,List<HashMap<String, Object>>> tables) {
		List<List<String>> returnTable= new ArrayList<List<String>>();
		ArrayList<String> row;
		
		List<List<String>> table6 = buildInd6(tables);
		for(int i = 0; i < table6.size(); i++) {
			ArrayList<String> thisRow = new ArrayList<String>();
			List<String> t5Row = table5.get(i);
			List<String> t6Row = table6.get(i);
			
			//Add date
			thisRow.add(t5Row.get(0));
			
			//Add Prediction and actual Performance
			String prediction = t5Row.get(1);
			thisRow.add(prediction);
			
			String actualPerformance =t6Row.get(1);
			thisRow.add(actualPerformance);
			
			//Add "Correctly Predicted" or "Incorrectly Predicted"
			thisRow.add(performanceAnalysis(prediction, actualPerformance));
			
			returnTable.add(thisRow);
		}
		return returnTable;
	}
	private String performanceAnalysis(String prediction, String actual) {
		String result = "";
		if(prediction.equals(actual))
			result += "Correctly Predicted -->   ";
		else
			result += "Incorrectly Predicted -->   ";
		if(prediction.equals("Buy")) {
			if(actual.equals("Buy")) {
				result += "Making money";
			} else if(actual.equals("Hold")) {
				result += "No money lost";
			}
			else {
				result += "Money lost";
			}
		}else if(prediction.equals("Hold")) {
			if(actual.equals("Buy")) {
				result += "Opportunity Lost";
			} else if(actual.equals("Hold")) {
				result += "No money lost";
			}
			else {
				result += "Money lost";
			}
		} else {
			if(actual.equals("Buy")) {
				result += "Opportunity Lost";
			} else if(actual.equals("Hold")) {
				result += "No money lost";
			}
			else {
				result += "No money lost";
			}
		}
		return result;
	}
	public List<List<String>> buildInd6(HashMap<String,List<HashMap<String, Object>>> tables) {
		ArrayList<List<String>> returnTable = new ArrayList<List<String>>();
		ArrayList<String> row; 
		for(String table : tables.keySet()) {
			row = new ArrayList<String>();
			
			// add the date for the BHS prediction
			row.add(table);
			
			//Generate the BHS signal for the date and add it to the row
				//Get the corresponding table for the date (table)
			List<HashMap<String, Object>> aTable = tables.get(table);
			
			//Iterate through the last six tuples in the table and 
			// generate a BHS signal for each tuple and add that 
			// value to the aggregate BHS signal
			int aggBHS = 0;
			
			for(int i = aTable.size()-3; i < aTable.size(); i++) {
				HashMap<String, Object> tuple = aTable.get(i);
				
				int year = (int)tuple.get("year");
				int month = (int)tuple.get("month");
				double stockChange = (double)tuple.get("StockChange");
				double diff = (double)tuple.get("Difference");
				
				int stockBHS = getBHSSignalSingle(stockChange);
				int diffBHS = getBHSSignalSingle(diff);
				aggBHS += getBHSSignal(stockBHS, diffBHS);
			}
			row.add(getAggBHSSignal6(aggBHS));
			returnTable.add(row);
		}
		
		return returnTable;
	}
	private String getAggBHSSignal6(int num) {
		if(num >= 2)
			return "Buy";
		else if(num >= -1)
			return "Hold";
		else
			return "Sell";
	}
	
	public String indiv8(List<HashMap<String, Object>> tuples, String ticker1, String ticker2) {
		HashMap<String, Object> tuple = tuples.get(0);
		
		StringBuilder output = new StringBuilder();
		
		if ((double) tuple.get("mainRelativeGrowth") > (double) tuple.get("otherRelativeGrowth"))
		{
			output.append(ticker1 + " is performing better than " + ticker2 + " according to relative price growth.  ");
		}
		else if ((double) tuple.get("mainRelativeGrowth") < (double) tuple.get("otherRelativeGrowth"))
		{
			output.append(ticker2 + " is performing better than " + ticker1 + " according to relative price growth.  ");
		}
		else {
			output.append("The two stocks have the same relative price growth.  ");
		}
		
		if ((double) tuple.get("mainVolumeTraded") > (double) tuple.get("otherVolumeTraded"))
		{
			output.append(ticker1 + " has sold better than " + ticker2 + " over the year.");
		}
		else if ((double) tuple.get("mainVolumeTraded") < (double) tuple.get("otherVolumeTraded"))
		{
			output.append(ticker2 + " has sold better than " + ticker1 + " over the year.");
		}
		else {
			output.append("The two stocks have the same volume traded.");
		}
		
		return output.toString();
	}
}
