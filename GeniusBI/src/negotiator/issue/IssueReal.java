package negotiator.issue;

import negotiator.xml.SimpleElement;

/**
*
* @author Koen Hindriks
* 
*/

public class IssueReal extends Issue {
	
	// Class fields
	// Assumption 1: real-valued issues have a fixed range, with a lower and upper bound.
	// Assumption 2: value ranges for issue are shared between agents.
	RangeReal range;
	//use this value for discrete operations in the analysis
	//TODO make it template parameter
	private int fNumberOfDiscretizationSteps = 21;
	// Constructors
	
	public IssueReal(String name, int issueNumber, double min, double max) {
		super(name, issueNumber);
		range = new RangeReal(min, max);
	}
	
	public IssueReal(String name, int issueNumber, double min, double max, Objective objParent) {
		super(name, issueNumber, objParent);
		range = new RangeReal(min, max);
	}
	
	// Class method
	public boolean checkInRange(ValueReal val) {
			return ( val.getValue() >= range.getLowerBound() && val.getValue() <= range.getUpperBound());
	}
	
	public final double getLowerBound() {
		return range.getLowerBound();
	}
	
	public final double getUpperBound() {
		return range.getUpperBound();
	}
	
	public final boolean setUpperBound(double up){
		if(up > range.lowerBound){
			range.upperBound=up;
			return true;
		}else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
		
	}
	
	public final boolean setLowerBound(double lo){
		if(lo < range.upperBound){
			range.lowerBound=lo;
			return true;
		}else{
			System.out.println("Minimum bound exceeds maximum bound in integer-valued issue!");
			return false;
		}
	}

	public int getNumberOfDiscretizationSteps() {
		return fNumberOfDiscretizationSteps;
	}

	public void setNumberOfDiscretizationSteps(int numberOfDiscretizationSteps) {
		fNumberOfDiscretizationSteps = numberOfDiscretizationSteps;
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
		thisIssue.setAttribute("type", "real");
		thisIssue.setAttribute("etype", "real");
		thisIssue.setAttribute("vtype", "real");
		SimpleElement thisRange = new SimpleElement("range");
		thisRange.setAttribute("lowerbound", ""+getLowerBound());
		thisRange.setAttribute("upperbound", ""+getUpperBound());
		thisIssue.addChildElement(thisRange);
		//todo find way of adding items.
		return thisIssue;
		
	}

	@Override
	public Value getValue(String text) {
		return new ValueReal(Double.parseDouble(text));
	}
	
	@Override public String getPossibleValues() {
		return range.toString();
	}
}
