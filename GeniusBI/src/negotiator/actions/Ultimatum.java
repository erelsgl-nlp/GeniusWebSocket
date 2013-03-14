package negotiator.actions;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * Represents an ultimatum action. It is used by the mediator to motivate negotiators into
 * an agreement
 * @author Eran
 *
 */
public class Ultimatum extends BidAction {
	protected int ultimatumSentTurn; // turn in which ultimatum was sent
	protected int ultimatumActivationTurn; // turn in which ultimatum is activated
	protected int ultimatumPenalty; // penalty if ultimatum isn't accepted
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Ultimatum() {	}

	
	/**
	 * Creates a new instance of Ultimatum.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties
     */	
	public Ultimatum(AgentID agent, Bid bid) {
		super(agent, bid);
	}


    /**
	 * Creates a new instance of Ultimatum.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     * @param destination - the destination for this action, null mean everybody else.
     */
	public Ultimatum(AgentID agent, AgentID destination, Bid bid) {
		super(agent, destination, bid);
	}

	public int getUltimatumActivationTurn() {
		return ultimatumActivationTurn;
	}

	public void setUltimatumActivationTurn(int ultimatumActivationTurn) {
		this.ultimatumActivationTurn = ultimatumActivationTurn;
	}

	public int getUltimatumSentTurn() {
		return ultimatumSentTurn;
	}

	public void setUltimatumSentTurn(int ultimatumSentTurn) {
		this.ultimatumSentTurn = ultimatumSentTurn;
	}

	public int getUltimatumPenalty() {
		return ultimatumPenalty;
	}

	public void setUltimatumPenalty(int ultimatumPenalty) {
		this.ultimatumPenalty = ultimatumPenalty;
	}
	
	public String getContent() {
		return (bid != null ? bid.toString() : "") + ", accompanyText: " + this.accompanyText + ", ultimatumSentTurn: " + ultimatumSentTurn + ", ultimatumActivationTurn: " + ultimatumActivationTurn + ", ultimatumPenalty: " + ultimatumPenalty;
	}
	
	@Override
	public String toString() {
        return "(Ultimatum: " + getContent() + ")";
	}

	public Ultimatum clone() throws CloneNotSupportedException {
		Ultimatum result = (Ultimatum)super.clone();
		result.ultimatumSentTurn = ultimatumSentTurn;
		result.ultimatumActivationTurn = ultimatumActivationTurn;
		result.ultimatumPenalty = ultimatumPenalty;
		return result;
	}
}
