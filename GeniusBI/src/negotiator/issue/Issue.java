/*
 * Issue.java
 *
 * Created on November 6, 2006, 1:10 PM
 *
 */

package negotiator.issue;

import negotiator.xml.SimpleElement;

/**
 *
 * @author Koen Hindriks
 * @author Richard
 */

public class Issue extends Objective {
  
    // Constructors
	
    public Issue(String name, int issueNumber) {
        super(null, name, issueNumber);
    }
    
    public Issue (String name, int issueNumber, Objective parent) {
    	super(parent, name, issueNumber);
    }
    
    // Class methods
    
    @Override
	public ISSUETYPE getType() {
    	if (this instanceof IssueDiscrete)
    		return ISSUETYPE.DISCRETE;
// TODO: Remove.
//    	else if (this instanceof IssueDiscreteWCost)
//    		return ISSUETYPE.DISCRETEWCOST;
    	else if (this instanceof IssueInteger)
    		return ISSUETYPE.INTEGER;
    	else if (this instanceof IssueReal)
    		return ISSUETYPE.REAL;
// TODO: Remove.
//    	else if (this instanceof IssuePrice)
//    		return ISSUETYPE.PRICE;
    	else return null;
    }

    /**
     * Converts ISSUETYPE enumeration to a string. Reverse functiong for 
     * convertToType method. Used to save issue to XML file.
     * 
     * Remark: Added by Dmytro on 09/05/2007 
     * 
     * @param pType - issue type
     * @return corresponding string representation
     */
    public static String convertToString(ISSUETYPE pType) {
    	//If typeString is null for some reason (i.e. not spceified in the XML template
    	// then we assume that we have DISCRETE type
    	switch(pType) {
    	case DISCRETE:
    		return "discrete";
// TODO: Remove    		
//    	case PRICE:
//    		return "price";		
    	case INTEGER:
    		return "integer";		
    	case REAL:
    		return "real";	
    	default: return "";
    	}
       	// TODO: Define corresponding exception.

    }
    
    public boolean checkInRange(Value val) {
    	return false;
    }
    
    //  Inner range classes for integers and reals
	protected class RangeInt {
		
		// Class fields
		int lowerBound;
		int upperBound;
		
		// Constructor
		public RangeInt(int min, int max) {
			if (min>max)
				System.out.println("Lower bound in real range exceeds upper bound!");
				// TO DO: Define exception.
			if (min==max) // issue warning.
				System.out.println("Lower bound equals upper bound in range.");
			lowerBound = min;
			upperBound = max;
		}
		
		// Class methods
		public int getLowerBound() {
			return lowerBound;
		}
		
		public int getUpperBound() {
			return upperBound;
		}

	}
	
	protected class RangeReal {
		
		// Class fields
		double lowerBound;
		double upperBound;
		
		// Constructor
		protected RangeReal(double min, double max) {
			if (min>max)
				System.out.println("Lower bound in real range exceeds upper bound!");
				// TO DO: Define exception.
			if (min==max) // issue warning.
				System.out.println("Lower bound equals upper bound in range.");
			lowerBound = min;
			upperBound = max;
		}
		
		// Class methods
		protected double getLowerBound() {
			return lowerBound;
		}
		
		protected double getUpperBound() {
			return upperBound;
		}

	}
	
	/**
	 * Overrides addChild from Objective to do nothing, since Issues can't have children. This
	 * method simply returns without doing anything. 
	 * @param newObjective gets negated.
	 */
	@Override
	public void addChild(Objective newObjective) {
	}
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues name and index.
	 */
	@Override
	public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		return thisIssue;
	}

	/**
	 * @param text - a textual representation of a value from the value-set of this issue. 
	 * @return the parsed value.
	 */
	public Value getValue(String text) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a random value from the list of valid values to this issue.
	 */
	public Value getRandomValue() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @return a string describing the possible values for this issue.
	 * @see negotiator.utility.UtilitySpace#toHTML
	 */
	public String getPossibleValues() {
		throw new UnsupportedOperationException();
	}
}