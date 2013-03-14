package negotiator.analysis;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Bid;
import negotiator.xml.SimpleElement;
import negotiator.XMLable;

/**
 * 
 * 
 * @author W.Pasman
 * BidPoint is a point with two utilities for the two agents.
 */

@XmlRootElement
public class BidPoint implements XMLable {
	@XmlElement
	public Bid bid;
	@XmlAttribute(name="utility_agent_a")
	public Double utilityA;
	@XmlAttribute(name="utility_agent_b")
	public Double utilityB;
	
	public BidPoint(){}
	
	public BidPoint(Bid b,Double uA, Double uB)
	{
		bid=b; utilityA=uA; utilityB=uB;
	}
	
	public String toString()
	{
		return "BidPoint ["+bid+" utilA["+utilityA+"],utilB["+utilityB+"]]";
	}
	/*
	boolean equals(BidPoint pt)
	{
		return bid.equals(pt.bid);
	}
	*/
	
	/**
	 * Notice: Only compare the bids (and not the utilities).
	 *         This functionality is used by the mediator. 
	 * @author Yoshi
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BidPoint other = (BidPoint) obj;
		return bid.equals(other.bid);		
	}

	public SimpleElement toXML()
	{
		SimpleElement xml = new SimpleElement("BidPoint");
		xml.addChildElement(bid.toXML());
		xml.setAttribute("utilityA", ""+utilityA);
		xml.setAttribute("utilityB", ""+utilityB);
		return xml;
	}

	
	
}
