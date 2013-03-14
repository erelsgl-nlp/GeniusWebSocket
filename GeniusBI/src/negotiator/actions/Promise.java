/*
 * Promise.java
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Bid;
/**
 * When this action is sent it mean that the agent promise to accept the terms contained in it,
 * if it will be offered by the other side.
 * If this Action will be accepted it still isn't binding and has no influence on the results of the
 * negotiation. However Promises let you gently probe for the other negotiators intentions   
 * @author Yinon Oshrat
 */

@XmlRootElement
public class Promise extends BidAction {
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Promise() {}
	/** Creates a new instance of Promise 
     * @param agent - the agent creating this action
     * @param bid the bid this action send to other parties 
     * */
    public Promise(AgentID agent, Bid bid) {
        super(agent,bid);
    }
    /**
     * Create a new Promise
     * @param agent - the agent creating this bid
     * @param bid - the bid this action send to other parties 
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public Promise(AgentID agent, AgentID destination,Bid bid) {
        super(agent,destination,bid);
    }
    
    public String toString() {
        return "(Promise: " + bid.toString() + ")";
    }
    
}