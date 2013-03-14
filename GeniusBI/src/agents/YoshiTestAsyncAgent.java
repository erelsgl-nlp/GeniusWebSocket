/**
 * 
 */
package agents;

import java.util.ArrayList;
import java.util.Random;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.actions.*;
import negotiator.utility.UtilitySpace;

/**
 * @author Yoshi
 * 
 */
public class YoshiTestAsyncAgent extends Agent {


	private int currentTurn;
	private Bid acceptedBid;
	private ArrayList<Bid> recievedOffers;
	double statusQuo;
	Random randomer;
	double maxRecievedUtil; 
	double goalReduce;

	/**
	 * returns the version of the agent.
	 * @return a string with the version
	 */
    public static String getVersion() { return "1.0"; }
    
    /**
     * initialization of negotiation session
     */
    public void init() {
        // initialize all the variables you need before the beginning of the negotiations
        currentTurn = 0;
        acceptedBid = null;
        randomer = new Random();
        statusQuo = utilitySpace.getReservationValue() + utilitySpace.getTimeEffectValue() * numOfTurns;
        goalReduce = 0.01;
        maxRecievedUtil = Double.NEGATIVE_INFINITY;
        recievedOffers = new ArrayList<Bid>();

    }
    
    /**
     * informs you which action the opponent did.
     * @param opponentAction
     */
    public void ReceiveMessage(Action opponentAction) {
        try {
            if (opponentAction == null) {
                // negotiation started
            	
            } else if (opponentAction instanceof EndNegotiation) {
                // our opponent opted out
            
            } else if (opponentAction instanceof EndTurn) {
                // a new turn begun - remember what is the current turn
                currentTurn = ((EndTurn)opponentAction).getTurn();
                
                // Decrease our goal utility
                decGoal(1 / numOfTurns);
                
                // Get a bid to offer
                Bid offerBid = getNextBid();
                
                // Offer the bid
                if (offerBid != null) {
                	Offer ourOffer = new Offer(getAgentID(), offerBid);
                	//sendAction(ourOffer);
                }

                
            } else if (opponentAction instanceof BidAction) {
                // We got a message that include a bid
                // It may be binding offer from types Offer or CounterOffer
                // or it may be non-binding exploratory message of type Query or Promise
                BidAction bidAction = (BidAction)opponentAction;
                Bid currentBid = bidAction.getBid();

                boolean recievedNew = false;
                
                if (bidAction instanceof Offer || bidAction instanceof CounterOffer)
                {            
                	recievedNew = true;
                	
                    for (Bid rec : recievedOffers)
                    	if (rec.equals(currentBid))
                    		recievedNew = false;

                 	recievedOffers.add(currentBid);
                }

                
                // combining with old accepted bid
                Bid combinedBid;
                
                if (acceptedBid == null)
                    combinedBid = currentBid;
                else
                    combinedBid = acceptedBid.combinBid(currentBid);

                // Check our utility from the bid
                double ourUtility = utilitySpace.getUtilityWithTimeEffect(combinedBid, currentTurn);
            
                double goal = getGoal();

                // If the utility is greater that out goal, accept it
                if (ourUtility >= goal) {

                    // Accept the bid
                    AcceptOrReject answer = new Accept(getAgentID(), bidAction);
                    sendAction(answer);
                    
                    // If this Action was a binding one, save the combined bid that was accepted do far
                    if (bidAction instanceof Offer || bidAction instanceof CounterOffer)
                        acceptedBid = combinedBid;
                }
                else {
                
                    // Decrease our goal utility
                	if (recievedNew)
                		decGoal(0.03);
                	else
                		decGoal(0.003);
                                    	
                    // Get a bid to offer
                	Bid offerBid = getNextBid();

                    // Offer the bid as a counter offer
                    if (offerBid != null) {
    					CounterOffer ourOffer = new CounterOffer(getAgentID(), offerBid);
                    	//sendAction(ourOffer);
                    }
                }
                
                
                
            } else if (opponentAction instanceof Reject) {
                // the opponent Rejected one of our previous actions
                //Action rejectedAction=((Reject)opponentAction).getRejectedAction();
    
                
                
            } else if (opponentAction instanceof Accept) {
                // the opponent Accepted one of our previous actions 
                BidAction acceptedAction=((AcceptOrReject)opponentAction).getAcceptedOrRejectedAction();

                if (acceptedAction instanceof Offer || acceptedAction instanceof CounterOffer) {
                	
                    Bid currentBid = acceptedAction.getBid();
                    
                    // If this Action was a binding one, save the combined bid that was accepted do far
                    if (acceptedBid == null)
                        acceptedBid = currentBid;
                    else
                        acceptedBid = acceptedBid.combinBid(currentBid);
                }
    

            } else if (opponentAction instanceof Threat
                    || opponentAction instanceof Comment) {
                // we got a threat or a comment - as a string - it may change the way we continue our negotiation
            	
            } else {
                // unknown action - ignore it
            }
                
        } catch (Exception e) {
            System.out.println("Exception in ReceiveMessage: " + e.getMessage());
    
        }
    }
    
    
    /**
     * returns a bid to offer.
     * @return a bid
     * @throws Exception
     */
    Bid getNextBid() throws Exception  {
   
    	// Get our utility goal
        double goal = getGoal();
        
        double worstMyUtility = Double.POSITIVE_INFINITY;
        Bid worstMyBid = null;
        
        double bestOpponentUtility = Double.NEGATIVE_INFINITY;
        Bid bestOpponentBid = null;
        
        // Choose a random opponent (or no opponent)
        int numOpponents = worldInformation.getNumOfPossibleUtiltySpaces();
	    int randomOpponent = randomer.nextInt(numOpponents + 1);
	    
        // get the utility space of the opponent
        UtilitySpace opponentUtilitySpace = null;
        
        if (randomOpponent < numOpponents) {
	        opponentUtilitySpace = worldInformation.getUtilitySpace(randomOpponent);
        }
        
        // Iterate over the possible bids
        BidIterator bidIter = new BidIterator(utilitySpace.getDomain());
        
        while(bidIter.hasNext()) {
            Bid bid = bidIter.next();
            
            // Get our utility
            double myUtil = utilitySpace.getUtilityWithTimeEffect(bid, currentTurn);
       
            // If this bid conforms to our current goal
            if (myUtil > goal) {
                
            	// Store our worst bid that conforms to our current goal
                if (myUtil < worstMyUtility) {
                    worstMyUtility = myUtil;
                    worstMyBid = bid;
                }

                // Store the opponent best bid that conforms to our current goal
                if (opponentUtilitySpace != null) {
	                double opponentBidUtility =
	                	opponentUtilitySpace.getUtilityWithTimeEffect(bid, currentTurn);
	                
	                if (opponentBidUtility > bestOpponentUtility) {
	                    bestOpponentUtility = opponentBidUtility;
	                    bestOpponentBid = bid;
	                }
                }
            }
        }

        // Return the bid that is the opponent best (or our worst) that conforms to our current goal
        Bid offerBid;
        
        if (opponentUtilitySpace != null) {
            offerBid = bestOpponentBid;
        }
        else {
            offerBid = worstMyBid;
        }

        return (offerBid);
    }
    
    
    /**
     * decrease the goal by the factor specified using randomization.
     * @param decFactor
     */
    void decGoal(double decFactor) {
    
        goalReduce = goalReduce + decFactor * randomer.nextDouble();
        
    }
    
    
    /**
     * get the current goal utility.
     * @return the current goal.
     * @throws Exception
     */
    double getGoal() throws Exception {
    	// Get our best possible utility
        Bid myBestBid = utilitySpace.getMaxUtilityBid();
        double myBestUtility = utilitySpace.getUtilityWithTimeEffect(myBestBid, currentTurn);
       
        double diffFromBest = myBestUtility - statusQuo;
       
        // Calculate the goal
        double goal = myBestUtility - diffFromBest * goalReduce;
         
        // Make sure that the goal is not lower the the status quo or the opting out utility
        if (goal < statusQuo)
            goal = statusQuo;
            
        if (goal < utilitySpace.getOptOutValue(currentTurn))
            goal = utilitySpace.getOptOutValue(currentTurn);
        
        return (goal);
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
