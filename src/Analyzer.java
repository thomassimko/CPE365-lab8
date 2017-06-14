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
			else if (r < .1) {
				report = "Slowly growing.";
			}
			else if (r < .15) {
				report = "Doing quite well.";
			}
			else if (r < .2) {
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
