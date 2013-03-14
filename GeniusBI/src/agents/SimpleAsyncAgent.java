/**
 * 
 */
package agents;

import java.util.Random;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.actions.*;
import negotiator.actions.UpdateStatusAction.Phase;
import negotiator.utility.UtilitySpace;

/**
 * @author User
 *
 */
public class SimpleAsyncAgent extends Agent {

	private int currentTurn;
	private Bid acceptedBid;
	
	/**
	 * 
	 */
	public SimpleAsyncAgent() {
	}
	
	public static String getVersion() { return "1.0"; }
	
	public void init() {
		// initialize all the variables you need before the beginning of the negotiations
		currentTurn = 0;
		acceptedBid = null;
	}
	
    /**
     * informs you which action the opponent did
     * @param opponentAction
     */
    public void ReceiveMessage(Action opponentAction) {
    	try {
	    	if (opponentAction == null) {
				// negotiation started - in a sec you'll get the first EndTurn message to announce the first turn

	    	} else if (opponentAction instanceof EndTurn) {
	    		// a new turn begun - remember what is the current turn
	    		currentTurn=((EndTurn)opponentAction).getTurn();
	    		
	    		// Update here any local information you need that change with the time
	    		
	    		/// The following code-sample shows how to iterate through all possible bids,
	    		/// and check for each bid its our utility for this turn. 
	    		/// We will offer an agreement that maximizes our utility.
	    		/// Notice: We don't claim this is a good thing to do - it just a good example for creating bids.
	    		BidIterator bidIter = new BidIterator(utilitySpace.getDomain());
	    		double maxUtility = Double.NEGATIVE_INFINITY;
	    		Bid maxBid = null;
	    		while (bidIter.hasNext()) {
	    			Bid bid = bidIter.next();
	    			double currentBidUtility = utilitySpace.getUtilityWithTimeEffect(bid, currentTurn);
	    			if (currentBidUtility > maxUtility) {
	    				maxUtility = currentBidUtility;
	    				maxBid = bid;
	    			}
	    		}
	    		if (maxBid==null)
	    			maxBid = utilitySpace.getDomain().getRandomBid();
	    		Offer ourOffer = new Offer(getAgentID(),maxBid);
	    		sendAction(ourOffer);
	    		
			} else if (opponentAction instanceof EndNegotiation) {
				// our opponent opted out - not much to do, but if it's learning agent you might want to learn from this
				
			} else if (opponentAction instanceof BidAction) {
				// We got a message that include a bid.
				// It may be binding offer from types Offer or CounterOffer
				// or it may be unbinding exploratory message of type Query or Promise
				
				/// The following code sample shows how to check our utility from the bid.
				/// We first combine the offer with any previous partial bid we already agreed on.
				/// We will compare this utility to the opt-out and statuesque values if it is better.
				/// We'll continue by checking if this is a full agreement - if so will accept in 50% of
				/// the cases - if not will accept in 30%.
				/// Notice: We don't claim this is a good thing to do - it just a good example for creating bids
				
				// combining with old accepted bid
				BidAction bidAction = (BidAction)opponentAction;
				Bid currentBid = bidAction.getBid();
				Bid combinedBid;
				if (acceptedBid == null)
					combinedBid = currentBid;
				else
					combinedBid = acceptedBid.combinBid(currentBid);

				// check if our utility is high enough
				Random randomer = new Random();
				double randomThreshld = 0.3;
				double ourUtility = utilitySpace.getUtilityWithTimeEffect(combinedBid ,currentTurn);
				// the status quo is the outcome when the negotiation will timeout so it will have the full effect of time
				double statusQuo = utilitySpace.getReservationValue(); 
				if (ourUtility >= statusQuo && ourUtility >= utilitySpace.getOptOutValue(currentTurn) ) {
					
					// check if this is an offer from the mediator
					if (opponentAction.getAgent().equals(worldInformation.getMediatorID())) {
						randomThreshld = 0.5;
					}
					
					// flip a coin
					if (randomer.nextDouble() < randomThreshld) {
						// accept
						AcceptOrReject answer = new Accept(getAgentID(), bidAction);
						sendAction(answer);
						// if this Action was a binding one, save the combined bid that was accepted so far
						if (bidAction instanceof Offer || bidAction instanceof CounterOffer)
							acceptedBid = combinedBid;

						// if we accepted a full bid, we propose to finish the negotiations
						if (utilitySpace.getDomain().isFullBid(combinedBid)) {
							AgreementReached reachedAction = new AgreementReached(getAgentID());
							sendAction(reachedAction);
						}
						
						return;
					}
				}
				// if we got here we just reject the offer
				Reject answer = new Reject(getAgentID(), bidAction);
				sendAction(answer);
				
			} else if (opponentAction instanceof Reject) {
				// the opponent Rejected one of our previous actions
				Action rejectedAction = ((Reject)opponentAction).getAcceptedOrRejectedAction();
	
			} else if (opponentAction instanceof Accept) {
				// the opponent Accepted one of our previous actions 
				BidAction acceptedAction=((AcceptOrReject)opponentAction).getAcceptedOrRejectedAction();
				
				// Code sample if the type of the accepted action was Query or Promise.
				// Try to send a real offer with the same values.
				// Notice that this agent doesn't really send Queries and Promises, still this code sample is given
				if (acceptedAction instanceof Query || acceptedAction instanceof Promise) {
					Offer ourOffer = new Offer(getAgentID(), acceptedAction.getBid());
					sendAction(ourOffer);
				}
				else {
					Bid currentBid = acceptedAction.getBid();
					if (acceptedBid == null)
						acceptedBid = currentBid;
					else
						acceptedBid = acceptedBid.combinBid(currentBid);
				}
	
			} else if (opponentAction instanceof Threat
					|| opponentAction instanceof Comment) {
				// we got a threat or a comment - as a string - it may change the way we continue our negotiation

			} else if (opponentAction instanceof AgreementReached) {
				// we need to send the agreement we believe was reached.
				// If we don't think an agreement was reached, we can send null.
				
				// Send a ValidateAgreement with the agreement that was stored as accepted
				ValidateAgreement validateAction = new ValidateAgreement(getAgentID(), acceptedBid);
				sendAction(validateAction);
				
			} else if (opponentAction instanceof UpdateStatusAction) {
				// the negotiation phase has changed
				UpdateStatusAction statusAction = (UpdateStatusAction)opponentAction;
				
				switch (statusAction.getPhase()) {
				case ContinueNegotiation:
					// We were unable to reach an agreement with ValidateAgreement
					break;
				case Ended:
					// The negotiation ended
					Double myUtility = statusAction.getYourUtility();
					break;
				}

			} else {
				// unknown action - ignore it
			}
				
    	} catch ( Exception e) {
    		System.err.println("AsyncAgent: Exception in ChooseAction:"+e.getMessage());
    		e.printStackTrace();
    	}
    	
    }
    
    
	/**
     * this function is called after ReceiveMessage, in a synchronous scenario 
     * it has no meaning in asynchronous scenario
     * with an Offer-action.
     * @return (should return) the bid-action the agent wants to make.
     */
    public Action chooseAction() {
        return null;
    }

}
