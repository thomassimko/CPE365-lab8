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
			
			
			if(r < -.3) {
				report = "Tanking it.";
			}
			else if(ratio < -.2) {
				report = "Diminishing returns.";
			}
			else if (ratio < -.1) {
				report = "Trying to hold on.";
			}
			else if (ratio < 0) {
				report = "Showing resilience.";
			}
			else if (ratio < .1) {
				report = "Slowly growing.";
			}
			else if (ratio < .2) {
				report = "Doing quite well.";
			}
			else if (ratio < .3) {
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
}
