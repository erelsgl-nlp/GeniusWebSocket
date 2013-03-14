/**
 * 
 */
package negotiator;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import negotiator.actions.Action;
import negotiator.actions.EndTurn;
import negotiator.actions.TextMessage;
import negotiator.actions.Ultimatum;
import java.util.ArrayList;
import negotiator.actions.UltimatumThreat;

/**
 * This is a class representing a mediator. The mediator has knowladge about 
 * @author Yinon Oshrat
 *
 */
public class Mediator implements ActionReceiver{
	
	private AgentID 		agentID; // this mediator ID
    private String          fName=null;
    private ActionListener	actionSender=null; // event handler for action performing on asynch protocols
    public HashMap<AgentID,WorldInformation> worldInformation=null; // hold available information about the world
    public	Date			startTime;
    public Integer			totalTime; // total time to complete entire nego, in seconds.
    public Integer			numOfTurns; // number of turns to complete entire nego.
    public Integer			turnLength; // length of turn, in seconds.
    public Integer			sessionNumber;
    public Integer			sessionTotalNumber;
    public Domain 			domain; // the domain of negotiation
    
    protected int currentTurn;
    protected HashMap<AgentID, NonCooperativeTracker> agentNCtracker = null;
    
    public ArrayList<Ultimatum> sentUltimatums;

	private boolean NC_ACTIVATED = false;
	private int NC_TURNS = 2;
	private int NC_REJECT = 2;
	private int NC_OFFER = 2;
	private double NC_RANK = 0.5;
	
    public Mediator() {
    	sentUltimatums = new ArrayList<Ultimatum>();
    }
    
    /**
     * This method is called by the environment (SessionRunner) every time before starting a new 
     * session after the internalInit method is called. User can override this method. 
     */
    public void init() {
    	currentTurn = 0;
    }
    
    public static String getVersion() {return "unknown";};
    
   
    
    /**
     * This method is called by the SessionRunner to initialize the mediator with a new session information.
     * @param sessionNumber number of the session
     * @param sessionTotalNumber total number of sessions
     * @param startTimeP - the time the negotiation started
     * @param totalTimeP - the total time of negotiation (in sec)
     * @param turnLengthP - if time has effect on negotiation, what the length (in sec) of each time step 
     * @param wi Hash table that map AgentID to a WorldInformation object that contain information about that agent

     */
    public final void internalInit(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
    		Integer totalTimeP,Integer turnLengthP,
    		HashMap<AgentID,WorldInformation> wi,Domain domain) {
        startTime=startTimeP;
        totalTime=totalTimeP;
        turnLength=turnLengthP;
        numOfTurns = totalTime/turnLength;
        this.sessionNumber = sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
    	worldInformation=wi;
    	this.domain=domain;
        return;
    }
    
    /**
     * informs the mediator of actions of the negotiating parties
     * @param action
     */
    public void ReceiveMessage(Action action) {
        try {
            if (action instanceof EndTurn) {
                // A new turn begun - remember what is the current turn
                currentTurn = ((EndTurn)action).getTurn();
            }
        } catch (Exception e) {
            System.err.println("MED: Exception in ReceiveMessage: " + e.getMessage());
            e.printStackTrace();
        }
        return;
    }
    
    
    /**
     * this function is called after ReceiveMessage, with an Offer-action.
     * Right now no protocol uses this and it reserved for future protocols
     * @return (should return) the bid-action the agent wants to make.
     */
    public Action chooseAction() {
        return null;
    }
    
    public String getName() {
        return fName;
    }
        
    public final void setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return;
    }
    
    
    /**
     * @author Y. OShrat
     * determine if this mediator is communicating with the user about nego steps.
     * @return true if a human user is directly communicating with the agent in order
     * to steer the nego. This flag is used to determine the timeout for the
     * negotiation (larger with human users).
     */
    public boolean isUIMeidator() { return false; }
    
    /**
     * This function cleans up the remainders of the agent: open windows etc.
     * This function will be called when the agent is killed,
     * typically when it was timed out in a nego session.
     * The agent will not be able to do any negotiation actions here, just clean up.
     * To ensure that the agent can not sabotage the negotiation, 
     * this function will be called from a separate thread.
     * 
     * @author W.Pasman
     */
    public void cleanUp() {  }
    
    public AgentID getAgentID() {
    	return agentID;
    }
    public void setAgentID(AgentID value) {
    	agentID = value;
    }
    
    public String getUserID() {
    	return null;
    }
    /**
     * Used by Negotiation Session to set a listener for the asynchronous actions of this mediator 
     * @param al
     * @author Yinon Oshrat
     */
    public final void setActionListener(ActionListener al) {
    	actionSender=al; 	
    }
    /**
     * Use this function to perform event in asynchronous protocols
     * @param a - the action to perform
     * @author Yinon Oshrat
     */
    public final void sendAction(Action a)
    {
		// test for cheating
    	AgentID id=getAgentID();
    	AgentID actionId=a.getAgent();
    	if (id !=null && id.equals(actionId)) {
    		if (actionSender!=null) {
//    			if (a instanceof Ultimatum) {
//	    			try {
//	    				System.out.println("Adding ultimatum: " + (Ultimatum) a);
//	    				sentUltimatums.add((Ultimatum) a.clone());
//	    			} catch (CloneNotSupportedException e) {
//	    				System.err.println("MED: sendAction had exception in Ultimatum.clone()");
//	    			}
//    			}
    			
    			actionSender.actionSent(a);
    		}
    	}
    }
    
    /**
     * Used to check if this mediator support agreement upgrades after they were accepted.
     * It is checked by the protocol when the negotiation ends with agreement, to know if there
     * is an  upgrading phase.
     * @return true if the mediator supports upgrading agreements
     */
    public boolean supportUpgradeAgreement() {
    	return false;
    }
    
    
    /**
     * Enters the agreement upgrading phase, in which the mediator can offer an upgrade to the
     * accepted agreement.
     * @param currentBid - the current final bid
     * @return an action (the upgrading offer) to be taken. null, if not upgrading.
     */
    public Action upgradeAgreement(Bid currentBid) { 
    	return null;
    }

//  /**
//  * Used to check if an agent has an activated penalty (from an activated ultimatum).
//  * It is checked by the protocol when the negotiation ends with agreement, to determine
//  * the score including the penalty.
//  * @return the activated penalty for the specified agent
//  */
    public double getUltimatumActivatedPenalty(AgentID agentID, Bid finalBid, int currentTurn) {
    	double penalty = 0;
    	if (NC_ACTIVATED) {
    		penalty = agentNCtracker.get(agentID).getCurrentPenalty();
    	}
    	return penalty;
    }
    
//    /**
//     * Used to check if an agent has an activated penalty (from an activated ultimatum).
//     * It is checked by the protocol when the negotiation ends with agreement, to determine
//     * the score including the penalty.
//     * @return the activated penalty for the specified agent
//     */
//    public double getUltimatumActivatedPenalty(AgentID agentID, Bid finalBid, int currentTurn) {
//    	double resultPenalty = 0;
//    	System.out.println("Number of pending ultimatums: " + sentUltimatums.size());
//        for (Iterator<Ultimatum> i = sentUltimatums.iterator(); i.hasNext();) {
//        	Ultimatum currUltimatum = i.next();
//        	System.out.println(currUltimatum);
//        	
//        	if (currUltimatum.getDestination()==null || currUltimatum.getDestination().equals(agentID)) {
//	        	if (currentTurn >= currUltimatum.getUltimatumActivationTurn()) {
//	        		if (currUltimatum.getBid() != null) {
//	        			if (isUltimatumFulfilled(currUltimatum.getBid(), finalBid)) {
//	        				System.out.println("ULTIMATUM IS FULFILLED");
//	        			} else {
//	        				System.out.println("ULTIMATUM IS NOT FULFILLED");
//	        				resultPenalty += currUltimatum.getUltimatumPenalty();
//	        			}
//	        		} else {
//	        			System.out.println("ULTIMATUM BID IS NULL");
//	        		}
//	        	} else {
//	        		System.out.println("ULTIMATUM ACTIVATION TURN DIDN'T PASS");
//	        	}
//        	} else {
//        		System.out.println("ULTIMATUM IRRELEVANT TO THIS AGENT");
//        	}
//        }
//    	return resultPenalty;
//    }
    
//	/**
//	 * Returns true if the Ultimatum bid is fulfilled by the final bid
//	 * 
//	 * @param bidUltimatum - the ultimatum bid
//	 * @param bidFinal - the final bid
//	 * @return whether the ultimatum is fulfilled by the final bid
//	 */
//	private boolean isUltimatumFulfilled(Bid bidUltimatum, Bid bidFinal) {
//		try {
//			Bid combinedBid = bidFinal.combinBid(bidUltimatum);
//			return combinedBid.equals(bidFinal);
//		} catch (Exception e) {
//            System.err.println("MED: Exception in isUltimatumFulfilled: " + e.getMessage());
//            e.printStackTrace();
//        }
//		return false;
//	}

	public void setNC_ACTIVATED(boolean NC_ACTIVATED) {
		this.NC_ACTIVATED = NC_ACTIVATED;
	}
    
	public void setNC_TURNS(int NC_TURNS) {
		this.NC_TURNS = NC_TURNS;
	}

	public void setNC_REJECT(int NC_REJECT) {
		this.NC_REJECT = NC_REJECT;
	}

	public void setNC_OFFER(int NC_OFFER) {
		this.NC_OFFER = NC_OFFER;
	}

	public void setNC_RANK(double NC_RANK) {
		this.NC_RANK = NC_RANK;
	}
    
    /**
     * Tracks a non-cooperative player
     * 
     */
	protected class NonCooperativeTracker {
		public AgentID agentID;
		
		private Locale locale;
		private ResourceBundle resourceBundle;

		// variables to keep track of a non-cooperative player
		private int nc_last_active_turn;
		private int nc_good_offers_rejected;
		private int nc_bad_offers_made;
		private int nc_num_warnings_made;
		private int nc_current_ultimatum_level;
		private double nc_current_penalty;
		
		private double prevMadeOfferRank;

		private final int NC_NUM_OF_WARNINGS = 1;
		private final int NC_NUM_OF_ULTIMATUMS = 3;
		private final double[] NC_ULTIMATUM_PENALTIES = {-60,-90,-130};
		
		private static final int NC_PLAYER_COOPERATES = 0;
		private static final int NC_PLAYER_IDLE = 1;
		private static final int NC_BAD_OFFERS = 2;
		private static final int NC_REJECTED_GOOD = 3;
		
		public NonCooperativeTracker(AgentID agentID, Locale locale) {
			this.agentID = agentID;
			this.locale = locale;
			
			resourceBundle = ResourceBundle.getBundle("mediators.MediatorResources", locale);
			
			nc_last_active_turn = 0;
			nc_good_offers_rejected = 0;
			nc_bad_offers_made = 0;
			nc_num_warnings_made = 0;
			nc_current_ultimatum_level = 0;
			nc_current_penalty = 0;
			
			prevMadeOfferRank = NC_RANK;
		}

		public void turnPassed() {
			if (!NC_ACTIVATED) return;
			
			checkNonCooperative();
		}
		
		public void acceptedOffer() {
			if (!NC_ACTIVATED) return;
			
			resetCounters(true, false);
		}
		
		public void rejectedOffer(double rejectedOfferRank) {
			if (!NC_ACTIVATED) return;
			
			nc_last_active_turn = currentTurn;
            if (rejectedOfferRank >= NC_RANK) {
            	nc_good_offers_rejected++;
            	checkNonCooperative();
            } else {
            	//nc_good_offers_rejected = 0;
            	//resetCounters();
            }
		}

		public void madeOffer(double madeOfferRank) {
			if (!NC_ACTIVATED) return;
			
			nc_last_active_turn = currentTurn;
			if (prevMadeOfferRank >= madeOfferRank)
			{
				if (madeOfferRank < NC_RANK) {
					nc_bad_offers_made++;
					checkNonCooperative();
				}else {
					//nc_bad_offers_made = 0;
					resetCounters(true, false);
				}
			}else {
				//nc_bad_offers_made = 0;
				resetCounters(true, false);
			}
			prevMadeOfferRank = madeOfferRank;
		}
		
		protected double getCurrentPenalty() {
			double penalty = 0;
			if (NC_ACTIVATED) {
				penalty = nc_current_penalty;
			}
			return penalty;
		}

		private void checkNonCooperative() {
			// Send Ultimatum
			int cooperationState = getCooperationStatus();
			if (cooperationState != NC_PLAYER_COOPERATES) {
				// Player already warned, now we activate ultimatum and reset counters
				if (nc_num_warnings_made >= NC_NUM_OF_WARNINGS) {
					if (nc_current_ultimatum_level < NC_NUM_OF_ULTIMATUMS) {
						nc_current_penalty += NC_ULTIMATUM_PENALTIES[nc_current_ultimatum_level];
						//sendNonCooperativeUltimatumActivatedMessage();
						sendNonCooperativeUltimatum(cooperationState, true);
						nc_current_ultimatum_level++;
						resetCounters(true, true);
					}
				} else { // O/W, send the ultimatum warning
					nc_num_warnings_made++;
					sendNonCooperativeUltimatum(cooperationState, false);
					resetCounters(false, false);
				}
			}
		}

		
		private void sendNonCooperativeUltimatum(int cooperationState, boolean isActivated) {
		    System.out.println("MED: sending ultimatum #" + (nc_current_ultimatum_level+1));
		    UltimatumThreat ultimatumThreat = new UltimatumThreat(getAgentID(), agentID, nc_current_ultimatum_level, cooperationState, isActivated);
		    String threatMessage = resourceBundle.getString("MediatorUltimatum.youAreNotCooperativeMessage");
		    String threatSpeech = resourceBundle.getString("MediatorUltimatum.youAreNotCooperativeSpeech");
		    
		    switch (cooperationState) {
			case NC_PLAYER_IDLE:
				threatMessage += resourceBundle.getString("MediatorUltimatum.idletoolongMessage");
				threatSpeech += resourceBundle.getString("MediatorUltimatum.idletoolongSpeech");
				break;
			case NC_BAD_OFFERS:
				threatMessage += resourceBundle.getString("MediatorUltimatum.madebadMessage");
				threatSpeech += resourceBundle.getString("MediatorUltimatum.madebadSpeech");
				break;
			case NC_REJECTED_GOOD:
				threatMessage += resourceBundle.getString("MediatorUltimatum.rejectegoodMessage");
				threatSpeech += resourceBundle.getString("MediatorUltimatum.rejectegoodSpeech");
				break;
			}
		    
		    if (isActivated) {
				threatMessage += resourceBundle.getString("MediatorUltimatum.activatedThreatMessage") + ((int) nc_current_penalty);
				threatSpeech += resourceBundle.getString("MediatorUltimatum.activatedThreatSpeech") + ((int) nc_current_penalty);
		    } else {
		    	threatMessage += UltimatumThreat.getUltimatumThreatDescription(nc_current_ultimatum_level);
		    	threatSpeech += UltimatumThreat.getUltimatumThreatDescription(nc_current_ultimatum_level);
		    }
		    ultimatumThreat.setAccompanyText(threatMessage);
		    ultimatumThreat.setSpeechText(threatSpeech);
			sendAction(ultimatumThreat);
		}

//		private void sendNonCooperativeUltimatumWarning() {
//		    System.out.println("MED: sending ultimatum #" + (nc_current_ultimatum_level+1));
//		    UltimatumThreat ultimatumThreat = new UltimatumThreat(getAgentID(), agentID, nc_current_ultimatum_level);
//		    ultimatumThreat.setAccompanyText(UltimatumThreat.getUltimatumThreatDescription(nc_current_ultimatum_level));
//		    ultimatumThreat.setSpeechText(UltimatumThreat.getUltimatumThreatDescription(nc_current_ultimatum_level));
//			sendAction(ultimatumThreat);
//		}
		
//		private void sendNonCooperativeUltimatumActivatedMessage() {
//		    System.out.println("MED: activated ultimatum #" + (nc_current_ultimatum_level+1) + " penalty incurred is: " + nc_current_penalty);
//		    
//			//String messageA = agentsResources.get(idA).getString("MediatorPerIssue.timeoutMessage");
//		    String message = "Activated ultimatum #" + (nc_current_ultimatum_level+1) + " penalty incurred is: " + nc_current_penalty;
//			TextMessage textUltimatumActivated = new TextMessage(getAgentID(), agentID, message);
//			textUltimatumActivated.setAccompanyText(message);
//			textUltimatumActivated.setSpeechText(message);
//			sendAction(textUltimatumActivated);
//		}
		
		private void resetCounters(boolean resetPendingWarning, boolean threatActivated) {
			System.out.println("MED: Counters are RESET");
			nc_last_active_turn = currentTurn;
			nc_good_offers_rejected = 0;
			nc_bad_offers_made = 0;
			
			if (threatActivated) {
				nc_num_warnings_made = 0;
			}
			
			if (resetPendingWarning) {
				System.out.println("MED: Warning Counter is RESET");
				if (nc_num_warnings_made > 0) {
					System.out.println("MED: clearing ultimatum threat");
					UltimatumThreat ultimatumThreat = new UltimatumThreat(getAgentID(), agentID, nc_current_ultimatum_level, NC_PLAYER_COOPERATES, false);
					ultimatumThreat.setAccompanyText("Threat cleared for agent " + agentID);
					ultimatumThreat.setSpeechText("Threat cleared for agent " + agentID);
					sendAction(ultimatumThreat);
				}
				nc_num_warnings_made = 0;
			}
		}
		
		private int getCooperationStatus() {
		//private boolean isNonCooperative() {
			//boolean isNonCooperative = false;
			int cooperationState = NC_PLAYER_COOPERATES;
			
			if (currentTurn > (nc_last_active_turn + NC_TURNS)) {
				cooperationState = NC_PLAYER_IDLE;
			} else if (nc_bad_offers_made >= NC_OFFER) {
				cooperationState = NC_BAD_OFFERS;
			} else if (nc_good_offers_rejected >= NC_REJECT) {
				cooperationState = NC_REJECTED_GOOD;
			}
			
//			isNonCooperative = ((currentTurn > (nc_last_active_turn + NC_TURNS)) || (nc_bad_offers_made >= NC_OFFER) || (nc_good_offers_rejected >= NC_REJECT));
			
//			if (isNonCooperative) {
			if (cooperationState != NC_PLAYER_COOPERATES) {
				System.out.println("MED: Non-Cooperative player \"" + agentID + "\" detected at turn #" + currentTurn);
				if (currentTurn > (nc_last_active_turn + NC_TURNS)) {
					System.out.println("MED: Player idle for too many turns!");
				}
				
				if (nc_bad_offers_made >= NC_OFFER) {
					System.out.println("MED: Player made too many bad offers!");
				}
					
				if (nc_good_offers_rejected >= NC_REJECT) {
					System.out.println("MED: Player rejected too many good offers!");
				}
				System.out.println("\n");
			}
			return cooperationState;
		}
	}
}