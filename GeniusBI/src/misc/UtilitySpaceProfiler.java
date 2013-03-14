package misc;

import negotiator.*;
import negotiator.utility.UtilitySpace;

public class UtilitySpaceProfiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String domainFileName = "etc//templates//AMPOvsCity//ampo_vs_city_template.xml";
		String utilitySpaceFileName = "etc//templates//AMPOvsCity//ampo_vs_city_city_space.xml"; 
		Domain domain;
		int i = 0;
		try {
			domain = new Domain(domainFileName);
			UtilitySpace us = new UtilitySpace(domain,utilitySpaceFileName);
			BidIterator iter = new BidIterator(domain);
			double maxUtil = Double.NEGATIVE_INFINITY;
			while(iter.hasNext()) {
				Bid bid = iter.next();
				double util = us.getUtility(bid);
				if(util>maxUtil) {
					maxUtil = util;
				}
				i++;
				if(i>10000000) break;
			}
			System.out.println(maxUtil);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
