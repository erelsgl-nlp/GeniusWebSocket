/*
 * Bid.java
 *
 * Created on November 6, 2006, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.exceptions.MissingIssueException;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.Warning;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * <p>A bid contains the values of a possible agreement.
 * <p>A bid is a set of <idnumber,value> pairs, where idnumber is the unique number of the issue,
 * and value is the picked alternative.
 * @author Dmytro Tykhonov & Koen Hindriks
 */
@XmlRootElement public class Bid implements XMLable
{

	// Class fields
	Domain fDomain;
	
	@XmlElement(name="values")
	@XmlJavaTypeAdapter(MyMapAdapter.class)
	private HashMap<Integer, Value> fValues; // Wouter: the bid values  for each IssueID
	
	private double fTime = 0; // the time the bid was created

	@XmlElement(name="isoptout")
	private boolean isOptOut=false;
	
	public Bid(){
		fValues = new HashMap<Integer, Value>();
	}
	
	public Bid(Domain domainP) {
		this.fDomain = domainP; // THIS NEEDS A CHECK!
		fValues = new HashMap<Integer, Value>();
	}
	
	public Bid setOptOut() {
		isOptOut = true;
		return this;
	}
	
	public boolean isOptOut() {
		return this.isOptOut; 
	}

	/**
	 * create a new bid in a domain. 
	 * @param domainP the domain in which the bid is done
	 * @param bidsP HashMap, which is a set of pairs <issueID,value>
	 * @throws Exception if the bid is not a legal bid in the domain.
	 */
	public Bid(Domain domainP, HashMap<Integer,Value> bidP) 
	{
		this.fDomain = domainP; // THIS NEEDS A CHECK!

		// Check if indexes are ok
		// Discussion with Dmytro 16oct 1200: it is possible to do only a partial bid, leaving
		// part of the issues un-set. But each issue being bidded on has to exist in the domain,
		// and this is what we check here.
		// Discussion 16oct 16:03: No, ALL values have to be set.
		// Discussion 19oct: probably there only is this particular constructor because
		// that enables to enforce this.
		//ArrayList<Issue> issues=domainP.getIssues();
/*		for (Issue issue:issues)
			if (bidP.get(new Integer(issue.getNumber()))==null)
				throw new BidDoesNotExistInDomainException(
						"bid for issue '"+issue.getName()+"' (issue #"+issue.getNumber()+") lacks");
		*/
		fValues = bidP;
	}

	public Bid clone(){
		Bid result = new Bid();
		result.fValues = (HashMap<Integer,Value>)this.fValues.clone();
		result.fDomain = this.fDomain;
		result.fTime = this.fTime;
		result.isOptOut = this.isOptOut;
		return result;
	}

	/**
	 * create a new bid in a domain. It also let the user set the creation time of the bid 
	 * @param domainP the domain in which the bid is done
	 * @param bidsP HashMap, which is a set of pairs <issueID,value>
	 * @param time the time the bid was created (the units used depend on the protocol usually it turns).
	 * @throws Exception if the bid is not a legal bid in the domain.
	 */
	public Bid(Domain domainP, HashMap<Integer,Value> bidP,double time) throws Exception
	{
		this.fDomain = domainP;
		fValues = bidP;
		if (time>0)
			fTime=time;
		else
			new Warning("Bids can not have negative time");
	}

	/**
	 * @return the picked value for given issue idnumber 
	 */
	public Value getValue(int issueNr) throws MissingIssueException {
		Value v=fValues.get(issueNr);
		if (v==null) 
			throw new MissingIssueException(issueNr);
		return v;
	}
	
	/**
	 * @return true if the bid has a value for the given issue
	 * @author erelsgl
	 */
	public boolean hasValue(int issueNr) {
		return (fValues.get(issueNr)!=null);
	}

	/**
	 * set a value for one issue
	 * @param issueId - the id of the issue (1, 2, 3...)
	 * @param pValue - the value to be set
	 */
	public void setValue(int issueId, Value pValue) {
		if (fValues.get(issueId)==null)
			fValues.put(issueId, pValue);
		if (pValue==null)
			throw new IllegalArgumentException("Null value for issue #"+issueId);
		if (fValues.get(issueId).getType() == pValue.getType())
			fValues.put(issueId, pValue);
		/*
		 * TODO Throw an excpetion. else throw new
		 * BidDoesNotExistInDomainException();
		 */
	}
	

	/**
	 * set a value for one issue
	 * @param issueIndex - the index of the issue (0, 1, 2, ...)
	 * @param value - a string representation of the value.
	 */
	public void setValue (int issueIndex, String value) {
		Issue issue = fDomain.getIssues().get(issueIndex);
		setValue(issue.getNumber(), issue.getValue(value));
	}

	public void setDomain(Domain domain) {
		fDomain = domain;
	}
	
	@Override public String toString() {
        String s = "Bid[";
        for (Entry<Integer, Value> entry: fValues.entrySet()) {
        	int ind = entry.getKey();
        	
        	if (fDomain != null) {
        		Object tmpobj = fDomain.getObjective(ind); //Objective isn't recognized here, GKW. hdv
            	if(tmpobj != null){
            		String nm = fDomain.getObjective(ind).getName();
            		s += nm + ": " +
            			fValues.get(ind) +", ";
            	}else{
            		System.out.println("objective with index " + ind + " does not exist");
            	}
            // Needed for reading the results file where the domain is not initialised	
        	} else {
        		s += fValues.get(ind) +", ";
        	}
        }
        s += " time="+Double.toString(fTime);
        s += ", isoptout="+Boolean.toString(isOptOut);
        s += "]";
        return s;
    }

	
	public String toShortString() {
        String s = "";
        for (Entry<Integer, Value> entry: fValues.entrySet()) {
        	int ind = entry.getKey();
        	if (!s.isEmpty())
        		s += ", ";
        	if (fDomain != null) {
        		Object tmpobj = fDomain.getObjective(ind); //Objective isn't recognized here, GKW. hdv
            	if(tmpobj != null){
            		//String nm = fDomain.getObjective(ind).getName();
            		s += fValues.get(ind);
            	}else{
            		System.out.println("objective with index " + ind + " does not exist");
            	}
            // Needed for reading the results file where the domain is not initialised	
        	} else {
        		s += fValues.get(ind);
        	}
        }
        s.replaceAll(", ([^,])+", "and $1"); // replace last comma with "and"
        return s;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bid other = (Bid) obj;
		if (Double.doubleToLongBits(fTime) != Double
				.doubleToLongBits(other.fTime))
			return false;
		if (fValues == null) {
			if (other.fValues != null)
				return false;
		} else if (!fValues.equals(other.fValues))
			return false;
		return true;
	}

	/**
	 * Helper function to enable the comparison between two Bids.
	 * Wouter: changed to public for convenience.
	 * @return
	 */
	public HashMap<Integer, Value> getValues() {
		 // create a clone, to avoid changing of the values.
		return (HashMap<Integer, Value>)fValues.clone();
	}

	public String valuesToString() {
		return fValues.toString();
	}
	
	/**
	 * combine this bid with newBid, if an issue has value in both bids the value from newBid is preferred.
	 * @param newBid - the bid to combine with 
	 * @return - the combined bid
	 * @throws NegotiatorException - in case the bids belong to different domains
	 */
	public Bid combinBid(Bid newBid) throws NegotiatorException{
		if (!fDomain.equals(newBid.fDomain)) 
			throw new NegotiatorException("Bids of different domain can not be combined");
		try {
			// create new bid containing the old values
			Bid result = new Bid(fDomain,new HashMap<Integer, Value>(fValues));
			// override old values with the new ones 
			for (Map.Entry< Integer, Value> entry: newBid.getValues().entrySet()) {
				if (entry.getValue()!=null && (!entry.getValue().equals(ValueDiscrete.StrNoAgreement) || !result.getValues().containsKey(entry.getKey()))) 
					result.setValue(entry.getKey(), entry.getValue());
			}
			result.setTime(newBid.getTime());
			return result;
		} catch (Exception e) {	e.printStackTrace();}
		return null;
	}

	// DOES NO LONGER APPLY
	// public String indexesToString() {
	// String result ="";
	// for(int i=0;i<fValues.length;i++) {
	// result += String.valueOf(fValues[i])+";";
	// }
	// return result;
	// }
	// TODO re-do the save/load XML for bids
	/**
	 * @deprecated
	 */
	public Bid(Domain pDomain, SimpleElement pXMLBid) throws Exception
	{
		fDomain = pDomain;
		fValues = new HashMap<Integer,Value>();
		// read tie time of the bid from the file
		Object[] lXMLtime = (pXMLBid.getChildByTagName("time"));
		if (lXMLtime.length == 1)
			fTime= Double.parseDouble(((SimpleElement)lXMLtime[0]).getAttribute("value"));
		// fValuesIndexes = new int[pDomain.getNumberOfIssues()];
		Object[] lXMLIssues = (pXMLBid.getChildByTagName("issue"));
		Value lValue = null;
		SimpleElement lXMLItem;
		String lTmp;
		for (int i = 0; i < lXMLIssues.length; i++) {
			String indexStr = ((SimpleElement) lXMLIssues[i])
					.getAttribute("index"); // find the index of this Issue.
			int ind = new Integer(indexStr);
			switch (fDomain.getIssue(ind).getType()) {
			case DISCRETE:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("item"))[0];
				lTmp = lXMLItem.getAttribute("value");
				// fValuesIndexes[Integer.valueOf(((SimpleElement)lXMLIssues[i]).getAttribute("index"))-1]
				// =
				// Integer.valueOf();
				lValue = new ValueDiscrete(lTmp);
				break;
			case INTEGER:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("value"))[0];
				lTmp = lXMLItem.getText();
				lValue = new ValueInteger(Integer.valueOf(lTmp));
				break;

			// case PRICE:
			// lXMLItem =
			// (SimpleElement)(((SimpleElement)lXMLIssues[i]).getChildByTagName("value"))[0];
			// lTmp = lXMLItem.getText();
			// lValue = new ValuePrice(Double.valueOf(lTmp));
			// break;
			case REAL:
				lXMLItem = (SimpleElement) (((SimpleElement) lXMLIssues[i])
						.getChildByTagName("value"))[0];
				lTmp = lXMLItem.getText();
				lValue = new ValueReal(Double.valueOf(lTmp));
				break;
			case OBJECTIVE:
				//TODO something with objectives.
				//for now, do nothing. Objectives do not enter into bids.
				break;
				
			// TODO:COMPLETED: DT implement Bid(Domain, SimpleElement) in Bid
			// for the rest of the issue/value types
			// TODO: DT add bid validation w.r.t. Domain, throw an exception
			// BidDoesNotExist
			}// switch
			fValues.put(ind, lValue);
		}
	}

	public SimpleElement toXML() {

		SimpleElement lXMLBid = new SimpleElement("bid");
		// 5/11/09 Yinon Oshrat Add time of bid
		SimpleElement lXMLtime = new SimpleElement("time");
		lXMLtime.setAttribute("value",Double.toString(fTime));
		lXMLBid.addChildElement(lXMLtime);
		// TODO hdv rewrite this to use the hashmap.

		for(Issue lIssue : fDomain.getIssues()) {

			Value lVal = fValues.get(lIssue.getNumber());
			if (lVal==null)
				continue;
			SimpleElement lXMLIssue = new SimpleElement("issue");
			lXMLIssue.setAttribute("type",
					Issue.convertToString(lIssue.getType()));
			lXMLIssue.setAttribute("index", String.valueOf(lIssue.getNumber()));
			lXMLBid.addChildElement(lXMLIssue); SimpleElement lXMLItem=null;		 
			switch(lVal.getType()) { 
			case DISCRETE: 
				ValueDiscrete lDiscVal = (ValueDiscrete)(lVal); 
				lXMLItem = new SimpleElement("item");		  
				lXMLItem.setAttribute("value", lDiscVal.getValue()); 
				break; 
				//TODO:/COMPLETE DT implement toXML method in Bid for the rest of theissue/value types 
			case INTEGER: 
				ValueInteger lIntVal =(ValueInteger)(lVal); 
				lXMLItem = new SimpleElement("value");
				lXMLItem.setText(String.valueOf(lIntVal.getValue())); 
				break; 
			case REAL: 
				ValueReal lRealVal = (ValueReal)(lVal); 
				lXMLItem = new  SimpleElement("value");
				lXMLItem.setText(String.valueOf(lRealVal.getValue())); 
				break; 
				// case PRICE: 
				// ValueReal lPriceVal = (ValueReal)(fValues[i]); 
				// lXMLValue =  new SimpleElement("value"); 
				// lXMLValue.setText(String.valueOf(lPriceVal.getValue())); 
				// break; 
			}//switch
			lXMLIssue.addChildElement(lXMLItem); 
		}
		return lXMLBid;
	}
	// TODO can we save indexes to Strings?
	/*
	 * public String indexesToString() { String result =""; for(int i=0;i<fValuesIndexes.length;i++) {
	 * result += String.valueOf(fValuesIndexes[i])+";"; } return result; }
	 */


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(fTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((fValues == null) ? 0 : fValues.hashCode());
		return result;
	}
	
	/**
	 * In order to allow time effect on utility the bid's time is saved in the bid, and effect the final utilities
	 * @author Yinon Oshrat
	 */
	public void setTime(double value) {
		if (value<0) {
			new Warning("Bids can not have negative time");
		} else		
			fTime=value;
	}
	
	/**
	 * @return the time in the negotiation the bid was created. if no one set this time it is set to 0  
	 * @author Yinon Oshrat
	 */
	public double getTime() {
		return fTime;
	}
	
	
	public static Bid parseValueList(Domain domain, String valueList) throws Exception {
		String[] values = valueList.split(" ");
		if (values.length > domain.getIssues().size()) {
			throw new Exception("Too many values in value list");
		}
		Bid bid = new Bid(domain);
		for (int i=0; i<values.length; ++i) {
			bid.setValue(i, values[i]);
		}
		return bid;
	}
	
	
	/**
	 * Demo program for testing only
	 * @author Erel Segal
	 * @date 2011-02-09
	 * 
	 * @param args[0] a path to an xml file holding domain info
	 * @param args[1] a path to an xml file holding utility info
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		Domain domain = new Domain(args[0]);
		ArrayList<Issue> issues = domain.getIssues();
		System.out.println("domain issues: "+issues);
		
		Bid emptybid = new Bid(domain);
		System.out.println("Empty bid: "+emptybid);
		System.out.println("Empty bid xml: \n"+emptybid.toXML());
		emptybid.setOptOut();
		System.out.println("OptOut bid: "+emptybid);
		System.out.println("OptOut bid xml: \n"+emptybid.toXML());

		Bid partialbid = new Bid(domain);
		partialbid.setValue(1, new ValueDiscrete("7,000 NIS"));
		partialbid.setValue(2, new ValueDiscrete("Programmer"));
		System.out.println("Partial bid 0-1 1-2: "+partialbid);

		System.out.println("3 random bids for domain "+domain.getName()+": ");
		for (int i=0; i<3; ++i) {
			Bid randombid = domain.getRandomBid();
			System.out.println(randombid);
		}
		System.out.println();
		
	}
}







class MyMapAdapter extends XmlAdapter<Temp,Map<Integer,Value>> {

	@Override
	public Temp marshal(Map<Integer, Value> arg0) throws Exception {
		Temp temp = new Temp();
		for(Entry<Integer, Value> entry : arg0.entrySet()){
			temp.entry.add(new Item(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<Integer, Value> unmarshal(Temp arg0) throws Exception {
		Map<Integer, Value> map = new HashMap<Integer, Value>();
		for(Item item: arg0.entry) {
			map.put(item.key, item.value);
		}
		return map;
	}

}
class Temp {
  @XmlElement(name="issue")  
  public List<Item> entry ;
  
  public Temp(){entry = new ArrayList<Item>();}
   
}
@XmlRootElement
class Item {
	  @XmlAttribute(name="index")
	  public Integer key;
	  	
	  @XmlElement	  
	  public Value value;
	  
	  public Item(){ }
	  public Item(Integer key, Value val) {
		  this.key = key;
		  this.value = val;
	  }
	}


