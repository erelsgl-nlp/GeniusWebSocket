package negotiator;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import negotiator.utility.UtilitySpace;
import negotiator.issue.*;

public class LinearBidIterator extends BidIterator {

	private double maxUtil;
	private double minUtil;
	private UtilitySpace utilSpace;
	private List<Issue> sortedIssues;
	public LinearBidIterator(Domain domain, final UtilitySpace space, double maxUtil, double minUtil) {
		super(domain);
		this.maxUtil = maxUtil;
		this.minUtil = minUtil;
		this.utilSpace = utilSpace;
		sortedIssues = new LinkedList<Issue>();
		//sort issues wrt their weights
		sortedIssues.addAll(space.getDomain().getIssues());
		Collections.sort(sortedIssues, new Comparator<Issue>(){
			public int compare(Issue o1, Issue o2) {
				if(space.getWeight(o1.getNumber())>space.getWeight(o2.getNumber()))
					return 0;
				else return 1;
			} });
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return super.hasNext();
	}
	@Override
	public Bid next() {
		// TODO Auto-generated method stub
		return super.next();
	}
	

}
