import java.util.*;

public class Analyzer {
	
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
