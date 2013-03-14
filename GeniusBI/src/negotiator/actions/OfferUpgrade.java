package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * Represents an agreement upgrading action. It is used by the mediator to offer an upgrading to
 * an agreement that was reached by the negotiators.
 * @author Yoshi
 *
 */
public class OfferUpgrade extends BidAction {

	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public OfferUpgrade() {	}

	
	/**
	 * Creates a new instance of OfferUpgrade.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties
     */	
	public OfferUpgrade(AgentID agent, Bid bid) {
		super(agent, bid);
	}


    /**
	 * Creates a new instance of OfferUpgrade.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     * @param destination - the destination for this action, null mean everybody else.
     */
	public OfferUpgrade(AgentID agent, AgentID destination, Bid bid) {
		super(agent, destination, bid);
	}

	
	@Override
	public String toString() {
        return "(OfferUpgrade: " + bid.toString() + ")";
	}

}
