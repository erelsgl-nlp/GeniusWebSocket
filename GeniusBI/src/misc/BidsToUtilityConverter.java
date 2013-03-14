/**
 * 
 */
package misc;

import java.io.IOException;
import java.util.ArrayList;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

/**
 * @author ינון
 *
 */
public class BidsToUtilityConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Domain domain = new Domain("C:/Users/ינון/workspace/GeniusBI/etc/templates/JobCandiate/JobCanDomain.xml");
			UtilitySpace empUti = new UtilitySpace(domain, domain.getUtilitySpaceFilename("employer","short-term"));
			UtilitySpace canUti = new UtilitySpace(domain, domain.getUtilitySpaceFilename("candidate","short-term"));
			ArrayList<Issue> issues = domain.getIssues();
			System.out.println("domain issues: "+issues);
			Bid partialbid = new Bid(domain);
		
			partialbid.setValue(1, new ValueDiscrete("20,000 NIS"));
			partialbid.setValue(2, new ValueDiscrete("Programmer"));
			partialbid.setValue(3, new ValueDiscrete("With leased car"));
			partialbid.setValue(4, new ValueDiscrete("10%"));
			partialbid.setValue(5, new ValueDiscrete("Slow promotion track"));
			partialbid.setValue(6, new ValueDiscrete("8 hours"));			
			

			
			System.out.println("Partial bid: "+partialbid);
			System.out.println("emp util: "+empUti.getUtility(partialbid));
			System.out.println("can util: "+canUti.getUtility(partialbid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
