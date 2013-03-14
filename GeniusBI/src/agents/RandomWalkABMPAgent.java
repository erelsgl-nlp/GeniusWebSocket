/*
 * SimpleAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 */

package agents;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import negotiator.*;
import negotiator.actions.*;

/**
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 */

public class RandomWalkABMPAgent extends Agent {
	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber;
	private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private static final double BREAK_OFF_POINT = 0.5;
	private double[] lIssueWeight;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };

	// Parameter of RANDOMWALK strategy
	private static final int RANDOMWALKLENGTH = 100000;

	// Used in ABMP
	private static final double NEGOTIATIONSPEED = 0.05;
	private static final double CONCESSIONFACTOR = 0.8;
	private static final double CONFTOLERANCE = 0;

	// Class constructor
	public RandomWalkABMPAgent() {
		super();
	}

	protected void init(int sessionNumber, int sessionTotalNumber,NegotiationTemplate nt) {		
		super.init(sessionNumber, sessionTotalNumber, nt);
		myName = super.getName();
		this.sessionNumber = sessionNumber;
		this.sessionTotalNumber = sessionTotalNumber;

		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
	}

	// Class methods
	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() {
		Bid lBid;
		// Return (one of the) possible bid(s) with maximal utility.
		lBid = getMaxUtilityBid();
		myLastBid = lBid;
		return new Offer(getAgentID(), lBid);
	}

	private Action proposeNextBid(Bid lOppntBid) {
		Bid lBid = null;
		double lMyUtility, lOppntUtility, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = utilitySpace.getUtility(myLastBid);
		lOppntUtility = utilitySpace.getUtility(lOppntBid);
		lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
		lBid = getBidRandomWalk(lTargetUtility);
		myLastBid = lBid;
		return new Offer(getAgentID(), lBid);
	}

	public Action chooseAction() {
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;

		lActionType = getActionType(messageOpponent);
		switch (lActionType) {
		case OFFER: // Offer received from opponent
			lOppntBid = ((Offer) messageOpponent).getBid();
			if (myLastAction == null)
				// Other agent started, lets propose my initial bid.
				lAction = proposeInitialBid();
			else if (utilitySpace.getUtility(lOppntBid) >= utilitySpace
					.getUtility(myLastBid))
				// Opponent bids equally, or outbids my previous bid, so lets
				// accept
				lAction = new Accept(getAgentID());
			else
				// Propose counteroffer. Get next bid.
				lAction = proposeNextBid(lOppntBid);
			break;
		case ACCEPT: // Presumably, opponent accepted last bid, but let's
			// check...
			//lOppntBid = ((Accept) messageOpponent).getBid();
			if (lOppntBid.equals(myLastBid))
				lAction = new Accept(getAgentID());
			else
				lAction = new Offer(getAgentID(), myLastBid);
			break;
		case BREAKOFF:
			// nothing left to do. Negotiation ended, which should be checked by
			// Negotiator...
			break;
		default:
			// I am starting, but not sure whether Negotiator checks this, so
			// lets check also myLastAction...
			if (myLastAction == null)
				lAction = proposeInitialBid();
			else
				// simply repeat last action
				lAction = myLastAction;
			break;
		}

		myLastAction = lAction;
		return lAction;
	}

	private ACTIONTYPE getActionType(Action lAction) {
		ACTIONTYPE lActionType = ACTIONTYPE.START;

		if (lAction instanceof Offer)
			lActionType = ACTIONTYPE.OFFER;
		else if (lAction instanceof Accept)
			lActionType = ACTIONTYPE.ACCEPT;
		else if (lAction instanceof EndNegotiation)
			lActionType = ACTIONTYPE.BREAKOFF;
		return lActionType;
	}


	private Bid getBidRandomWalk(double targetUtility) {
		Bid lBid = null, lBestBid = null;

		// Return bid that gets closest to target utility in a "random walk"
		// search.
		lBestBid = getNegotiationTemplate().getDomain().getRandomBid();
		for (int k = 0; k < RANDOMWALKLENGTH; k++) {
			lBid = getNegotiationTemplate().getDomain().getRandomBid();
			if (Math.abs(utilitySpace.getUtility(lBid) - targetUtility) < Math
					.abs(utilitySpace.getUtility(lBestBid) - targetUtility))
				lBestBid = lBid;
		}
		return lBestBid;
	}
	
	private double getTargetUtility(double myUtility, double oppntUtility) {
		return myUtility + getConcessionStep(myUtility, oppntUtility);
	}

	private double getNegotiationSpeed() {
		return NEGOTIATIONSPEED;
	}

	private double getConcessionFactor() {
		// The more the agent is willing to concess on its aspiration value, the
		// higher this factor.
		return CONCESSIONFACTOR;
	}

	private double getConcessionStep(double myUtility, double oppntUtility) {
		double lConcessionStep = 0, lMinUtility = 0, lUtilityGap = 0;

		// FIXME: Always normalize evaluators/weights(already done?)/templates!!!
		// Compute concession step
		lMinUtility = 1 - getConcessionFactor();
		lUtilityGap = (oppntUtility - myUtility);
		lConcessionStep = getNegotiationSpeed() * (1 - lMinUtility / myUtility) * lUtilityGap;
		return lConcessionStep;
	}

}
