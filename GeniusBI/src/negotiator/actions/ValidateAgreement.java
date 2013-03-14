package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * Represents the agreement the negotiators believe they have reached, in the agreement
 * reached phase.
 * 
 * @author Yoshi
 *
 */
public class ValidateAgreement extends BidAction {

	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public ValidateAgreement() {
	}

    /**
	 * Creates a new instance of ValidateAgreement.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     */	public ValidateAgreement(AgentID agent, Bid bid) {
		super(agent, bid);
	}

    /**
	 * Creates a new instance of ValidateAgreement.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     * @param destination - the destination for this action, null mean everybody else.
     */
	public ValidateAgreement(AgentID agent, AgentID destination, Bid bid) {
		super(agent, destination, bid);
	}

	@Override
	public String toString() {
		if (bid != null) {
			return "(ValidateAgreement: " + bid.toString() + ")";
		}
        else {
			return "(ValidateAgreement: null)";
        }
	}

}
