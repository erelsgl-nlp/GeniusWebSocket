package negotiator.issue;

import negotiator.xml.SimpleElement;

/**
*
* @author Koen Hindriks
* 
*/

public class IssueInteger extends Issue {
	
	// Class fields
	// Assumption 1: integer-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	// Assumption 3: step size for integer valued issue is 1.
	RangeInt range;
	
	// Constructors
	public IssueInteger(String name, int issueNumber, int min, int max) {
		super(name, issueNumber);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	public IssueInteger(String name, int issueNumber, int min, int max, Objective objParent) {
		super(name, issueNumber, objParent);
		if (min>max)
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
		range = new RangeInt(min, max);
	}
	
	// Class method
	public boolean checkInRange(ValueInteger val) {
			return ( val.getValue() >= range.getLowerBound() && val.getValue() <= range.getUpperBound());
	}
	
	public final int getLowerBound() {
		return range.getLowerBound();
	}
	
	public final int getUpperBound() {
		return range.getUpperBound();
	}
	
	public final boolean setUpperBound(int up){
		if(up > range.lowerBound){
			range.upperBound=up;
			return true;
		}else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
		
	}
	
	public final boolean setLowerBound(int lo){
		if(lo < range.upperBound){
			range.lowerBound=lo;
			return true;
		}else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
	}
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues attributes
	 */
	@Override
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		thisIssue.setAttribute("type", "integer");
		thisIssue.setAttribute("etype", "integer");
		thisIssue.setAttribute("vtype", "integer");
		//TODO set range, upperBound and lowerBound items.
		SimpleElement thisRange = new SimpleElement("range");
		thisRange.setAttribute("lowerbound", ""+getLowerBound());
		thisRange.setAttribute("upperbound", ""+getUpperBound());
		thisIssue.addChildElement(thisRange);
		return thisIssue;
		
	}

	@Override
	public Value getValue(String text) {
		return new ValueInteger(Integer.parseInt(text));
	}
	
	@Override public String getPossibleValues() {
		return range.toString();
	}
}