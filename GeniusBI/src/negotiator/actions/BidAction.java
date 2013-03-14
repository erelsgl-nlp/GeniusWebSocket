/**
 * 
 */
package negotiator.actions;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import negotiator.AgentID;
import negotiator.Bid;
/**
 * Base class for action that contain a real bid such as types Query,Offer,CounterOffer & Promise
 * This class is here to help easier handling. you shouldn't create an instance of this class
 * @author Yinon Oshrat
 *
 */
@XmlTransient
public abstract class BidAction extends Action {
	@XmlElement(name="bid")
    protected Bid bid;
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public BidAction() { /* Shouldn't be used, exist to enable serialization */ }
	
    /** Creates a new instance of BidAction 
     * @param agent - the agent creating this action
     * @param bid the bid this action send to other parties 
     * */
    public BidAction(AgentID agent, Bid bid) {
        super(agent);
        this.bid = bid;
    }
    /**
     * Create a new BidAction
     * @param agent - the agent creating this bid
     * @param bid - the bid this action send to other parties 
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public BidAction(AgentID agent, AgentID destination,Bid bid) {
        super(agent,destination);
        this.bid = bid;
    }
    /**
     * @return the Bid that this action contains
     */
    public Bid getBid() {
        return bid;
    }
    @Override public abstract String toString();
    
    @Override public void setRound(int theRound) {
    	super.setRound(theRound);
    	if (bid!=null)
    		bid.setTime(theRound); 
    }
    
	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		// this should work even for subclasses of BidAction
		if (!this.getClass().isInstance(obj)) {
			return false;
		}
		BidAction other = (BidAction) obj;
		if (bid == null) {
			if (other.bid != null) {
				return false;
			}
		} else if (!bid.equals(other.bid)) {
			return false;
		}
		return true;
	} 
	
	@Override public Object clone() throws CloneNotSupportedException {
		BidAction result = (BidAction)super.clone();
		if (bid != null) {
			result.bid = bid.clone();
		}
		else {
			result.bid = null;
		}
		return result;
	}}
