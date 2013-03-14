/*
 * SendBid.java
 *
 * Created on November 6, 2006, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Bid;
/**
 * This class represent an Offer of specific bid to other parties in the negotiation.
 * If this action will be accepted by other parties it will form a binding agreement 
 * @author Dmytro Tykhonov
 */

@XmlRootElement
public class Offer extends BidAction {
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Offer() {}
	/** Creates a new instance of Offer 
     * @param agent - the agent creating this action
     * @param bid the bid this action send to other parties 
     * */
    public Offer(AgentID agent, Bid bid) {
        super(agent,bid);
    } 
    
    /**
     * Create a new BidAction
     * @param agent - the agent creating this bid
     * @param bid - the bid this action send to other parties 
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public Offer(AgentID agent, AgentID destination, Bid bid) {
        super(agent,destination,bid);
    }
    
    @Override public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("(").append(this.agent).append(" offers: ");
    	if (bid == null)
    		sb.append("null");
    	else 
    		sb.append(bid.toString());
    	if (accompanyText != null)
    		sb.append(", Text: " + accompanyText);
    	sb.append(")");
    	return sb.toString();
    }
    
}
