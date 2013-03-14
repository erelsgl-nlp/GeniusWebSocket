package negotiator.issue;

import java.util.*;

import negotiator.xml.SimpleElement;
/**
*
* @author Koen Hindriks
* 
*/

public class IssueDiscrete extends Issue {
		
	/** Wouter: the alternatives (String objects) for the issue.
	"value" is misleading, this is NOT the utility value but the name of the alternative */
	ArrayList<ValueDiscrete> issueValues; 
	Map<String, ValueDiscrete> valuesByName;
	
	 // the descriptions for each value
	private HashMap<ValueDiscrete, String> fDesc=new HashMap<ValueDiscrete, String>();
	private HashMap<ValueDiscrete, Double> fCosts=new HashMap<ValueDiscrete, Double>();

	private int numberOfValues; // Wouter: imho should not be done this way

	public static Random random = new Random();

	/**
	 * Adds a value.
	 * @param valname The name of the value to add.
	 */
	protected void addValue(String valueName, ValueDiscrete value) {
		issueValues.add(value);
		valuesByName.put(valueName, value);
	}
	
	/**
	 * Adds a value.
	 * @param valname The name of the value to add.
	 */
	protected void addValue(String valueName) {
		addValue(valueName, new ValueDiscrete(valueName));
	}
	
	public IssueDiscrete(String name, int issueNumber, String values[]) {
		super(name, issueNumber);		
		numberOfValues = values.length;
	    issueValues = new ArrayList<ValueDiscrete>();
	    valuesByName = new HashMap<String, ValueDiscrete>();
	    for(int i=0; i<numberOfValues;i++) 
	    	addValue(values[i]);
	}	
	
	public IssueDiscrete(String name, int issueNumber, String values[], Objective objParent, Double[] cost) {
		super(name, issueNumber, objParent);		
		numberOfValues = values.length;
		issueValues = new ArrayList<ValueDiscrete>();
	    valuesByName = new HashMap<String, ValueDiscrete>();
	    for(int i=0; i<numberOfValues;i++) {
	    	ValueDiscrete value = new ValueDiscrete(values[i]);
	    	addValue(values[i], value);
	        fCosts.put(value, cost[i]);
	    }
	}
	
	public IssueDiscrete(String name, int issueNumber, String values[], String descriptions[],Objective objParent, Double[] cost) {
		super(name, issueNumber, objParent);		
		numberOfValues = values.length;
		issueValues = new ArrayList<ValueDiscrete>();
	    valuesByName = new HashMap<String, ValueDiscrete>();
	    for(int i=0; i<numberOfValues;i++) {
			ValueDiscrete value=new ValueDiscrete(values[i]);
	    	addValue(values[i], value);
	        fCosts.put(value, cost[i]);
	        if (descriptions!=null && descriptions[i]!=null) fDesc.put(value,descriptions[i]);
	    }
	}
	

	
	@Override public boolean equals(Object o)
	{
		if (!(o instanceof IssueDiscrete)) return false;
		if (!equalContents((Objective)o)) return false; // check the basic things like name
		 // NOTE, we use .equals on issueValues which is an ArrayList.
		 // therefore the ORDER of the issues is critical as well (as it should)
		return issueValues.equals( ((IssueDiscrete)o).getValues());
	}
	public int getNumberOfValues() {
	    return issueValues.size();
	}
		
	public ValueDiscrete getValue(int index) {
		return issueValues.get(index);
	}

	/**
	 * @param valueName
	 * @return the value with this name, or null if none found.
	 */
	public ValueDiscrete valueByName(String valueName) {
		return valuesByName.get(valueName);
		
	}

	public String getStringValue(int index) {
		return issueValues.get(index).getValue();
	}
	
	    
	/** 
	 * @param value that is supposed to be one of the alternatives of this issue.
	 * @return index holding that value, or -1 if value is not one of the alternatives.
	 */
	public int getValueIndex(String value) {
	    for(int i=0;i<numberOfValues;i++)
	        if(issueValues.get(i).toString().equalsIgnoreCase(value)) {
	            return i;
	        }
	    return -1;
	}
	
	/** 
	 * @param value that is supposed to be one of the alternatives of this issue.
	 * @return index holding that value, or -1 if value is not one of the alternatives.
	 */
	public int getValueIndex(ValueDiscrete value) {
	    for(int i=0;i<numberOfValues;i++)
	        if(issueValues.get(i).equals(value)) {
	            return i;
	        }
	    return -1;
	}
	
	/**
	 * Removes all values from this Issue.
	 *
	 */
	public void clear(){
		issueValues.clear();
		valuesByName.clear();
	}
	
	/**
	 * Adds values.
	 * @param valnames Array with names of values to add.
	 */
	public void addValues(String[] valnames){
		for(int ind=0; ind < valnames.length; ind++)
			addValue(valnames[ind]);
	}
	
	
	public boolean checkInRange(ValueDiscrete val) {
			return (getValueIndex(val.getValue())!=-1);
	}
	
	/**
	 * Gives an enumeration over all values in this discrete issue.
	 * @return An enumeration containing <code>valueDiscrete</code>
	 */
	public ArrayList<ValueDiscrete> getValues() {
		return issueValues;
	}
	
	/**
	 * Returns a SimpleElement representation of this issue.
	 * @return The SimpleElement with this issues attributes
	 */
	@Override public SimpleElement toXML(){
		SimpleElement thisIssue = new SimpleElement("issue");
		thisIssue.setAttribute("name", getName());
		thisIssue.setAttribute("index", ""+getNumber());
		thisIssue.setAttribute("etype", "discrete");
		thisIssue.setAttribute("type", "discrete");
		thisIssue.setAttribute("vtype", "discrete");
		//TODO find some way of putting the items in. Probably in much the same way as weights.
		for(int item_ind = 0; item_ind < numberOfValues; item_ind++){
			SimpleElement thisItem = new SimpleElement("item");
			thisItem.setAttribute("index", "" + (item_ind +1)); //One off error?
			thisItem.setAttribute("value", issueValues.get(item_ind).toString());
			String desc=fDesc.get(issueValues.get(item_ind));
			if (desc!=null) thisItem.setAttribute("description", desc);
			thisIssue.addChildElement(thisItem);
 		}
		return thisIssue;
		
	}
	
	// Wouter: fromXML is currently incorporated in Domain.java. 
	// TODO I think it should be here.
    
	/**
	 * Sets the desc for value <code>val</code>. If the value doesn't exist yet in this Evaluator,
	 * add it as well.
	 * @param val The value to have it's desc set/modified
	 * @param desc The new desc of the value.
	 */
	public void setDesc(ValueDiscrete val, String desc)
	{
		fDesc.put(val, desc);
	}
	
	public String getDesc(ValueDiscrete val)
	{ return fDesc.get(val); }
	
	public Double getCost(ValueDiscrete val) {
		return fCosts.get(val);
	}
	
	@Override public Value getRandomValue() {
		return issueValues.get(random.nextInt(issueValues.size()));
	}

	@Override public String getPossibleValues() {
		return issueValues.toString();
	}
}