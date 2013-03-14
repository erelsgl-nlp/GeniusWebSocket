/*
 * CounterOffer.java
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Bid;
/**
 * This class represent an Offer that is sent in response to a previous offer.
 * Other then that it has the same function as regular Offer. If this action will be
 * accepted by other parties it will form a binding agreement 
 * @author Yinon Oshrat
 */

@XmlRootElement
public class CounterOffer extends BidAction {
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public CounterOffer(){}
    
	/** Creates a new instance of CounterOffer
	 * @param agent - the agent creating this action
     * @param bid the bid this action send to other parties 
     * */
    public CounterOffer(AgentID agent, Bid bid) {
        super(agent,bid);

    }
    /**
     * Create a new CounterOffer
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public CounterOffer(AgentID agent, AgentID destination,Bid bid) {
        super(agent,destination,bid);
    }
    
    public String toString() {
        return "(CounterOffer: " + bid.toString() + ")";
    }
    
}