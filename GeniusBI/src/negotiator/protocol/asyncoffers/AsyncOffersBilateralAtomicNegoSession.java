package negotiator.protocol.asyncoffers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import negotiator.*;
import negotiator.actions.*;
import negotiator.actions.UpdateStatusAction.Phase;
import negotiator.analysis.BidPoint;
import negotiator.events.NegotiationEndedEvent;
import negotiator.events.NegotiationEndedEvent.AgreementType;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.Warning;
import negotiator.protocol.*;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

/**
 * @author Yinon Oshrat
 *
 */
public class AsyncOffersBilateralAtomicNegoSession extends BilateralAtomicNegotiationSession implements ActionListener {


    /**
     * stopNegotiation indicates that the session has now ended.
     * it is checked after every call to the agent,
     * and if it happens to be true, session is immediately returned without any updates to the results list.
     * This is because killing the thread in many cases will return Agent.getAction() but with
     * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
     */
	private static final int	delayMiliSec=0;//20000; // used if one of the agent is human to add delay to the second one
	private int					actualDelayAgentA=0;
	private int					actualDelayAgentB=0;
	public boolean 				stopNegotiation=false;
	public boolean				isFinished=false;
	public NegotiationOutcome 	no;
    protected String 			startingAgent;
	private boolean 			startingWithA=true;    
	private Date 				startTime; 
	private long 				startTimeMillies; 		//idem.
	private Integer 			totalTime = 180000;
	private int 				numOfTurns; 			// Number of turns in this negotiation
	private int					turnLength;
	private int 				lastReportedRound =0;		// the last turn about which the agent were notified
    private int 				sessionTotalNumber = 1;
    private Protocol 			protocol;
    private Bid 				finalBid=null; 			// used to hold the issues agreed so far. 
    private ActionReceiver		sender;					//used to simplify handling of actions
    private ArrayList<ActionReceiver> receivers;			
	private HashMap<AgentID,ArrayList<Action>>	negotiatorActions;
    private LinkedList<MessageWrapper>		messageQueue = new LinkedList<MessageWrapper>();
    private ArrayList<ReceivedOffer> receivedOffers;
    private UpdateStatusAction.Phase phase;
    private AgreementType result;
    private HashMap<AgentID,Boolean> wasActiveCurrentTurn;
    private boolean inReachedAgreement = false;
    
    private boolean resultIsNotError() {
    	return result!=AgreementType.Error;
    }
	
    /**
     * Determines whether the protocol will end the negotiation if an agreement was reached on
     * all issues.
     * If set to false, the negotiation will end only if the negotiators choose to end it or if
     * it was timed out.
     */
	private boolean isAutoEnd = true;
    
    
    /**
     * Represents a received offer, with information on who accepted or rejected it.
     * 
     */
    private class ReceivedOffer {
    	public BidAction action;
    	public HashMap<AgentID, Boolean> accepted;
    	public HashMap<AgentID, Boolean> rejected;
    	
    	public ReceivedOffer(BidAction b) {
    		try {
    			action = (BidAction)b.clone();
    		}
			catch (CloneNotSupportedException e) {
				new Warning("ReceivedOffer: Exeption during action cloning", e, true, 5);
				action = null;
			}    			
    		accepted = new HashMap<AgentID, Boolean>();
    		rejected = new HashMap<AgentID, Boolean>();
    		accepted.put(agentA.getAgentID(), false);
    		accepted.put(agentB.getAgentID(), false);
    		rejected.put(agentA.getAgentID(), false);
    		rejected.put(agentB.getAgentID(), false);
    		accepted.put(b.getAgent(), true);
    	}
    	
    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if (obj == null) {
    			return false;
    		}
    		if (action == null) {
    			return false;
    		}
    		if (obj instanceof ReceivedOffer) {
    			return action.equals(((ReceivedOffer)obj).action);
    		}
    		if (obj instanceof BidAction) {
    			return action.equals(obj);
    		}
    		return false;
    	}
    }
    
    private class MessageWrapper {
    	/**
		 * @param sendTime
		 * @param action2
		 */
		public MessageWrapper(long sendTime, Action action) {
			this.sendTimeMili=sendTime;
			this.action=action;
		}
		public long  sendTimeMili; // the time to send this message in mili seconds
    	public Action action;
    }
     /** load the runtime objects to start negotiation */
    public AsyncOffersBilateralAtomicNegoSession(Protocol protocol,
    		Agent agentA,
			Agent agentB, 
			Mediator mediator,
			String agentAname, 
			String agentBname,
			UtilitySpace spaceA, 
			UtilitySpace spaceB, 
			HashMap<AgentParameterVariable,AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable,AgentParamValue> agentBparams,
			String startingAgent,
			WorldInformation worldInformationA,
    		WorldInformation worldInformationB,
			int numOfTurns,
			int turnLength) throws Exception {
    	
		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB,
				agentAparams, agentBparams,worldInformationA,worldInformationB,mediator);
		this.protocol = protocol;
		this.startingAgent = startingAgent;
        this.numOfTurns = numOfTurns;
        this.turnLength=turnLength;
        this.totalTime= numOfTurns * turnLength;
        negotiatorActions = new HashMap<AgentID, ArrayList<Action>>();
        negotiatorActions.put(agentA.getAgentID(),new ArrayList<Action>());
        negotiatorActions.put(agentB.getAgentID(),new ArrayList<Action>());
        if (mediator != null)
        	negotiatorActions.put(mediator.getAgentID(),new ArrayList<Action>());
        receivers = new ArrayList<ActionReceiver>();
        receivedOffers = new ArrayList<ReceivedOffer>();
        wasActiveCurrentTurn = new HashMap<AgentID, Boolean>();
	}
    
    /**
     * a parent thread will call this via the Thread.run() function.
     * Then it will start a timer to handle the time-out of the negotiation.
     * At the end of this run, we will notify the parent so that he does not keep waiting for the time-out.
     */
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
	public void run() {
		startTime=new Date(); startTimeMillies=System.currentTimeMillis();
		lastReportedRound=0;
		phase = Phase.Negotiation;
		inReachedAgreement = false;
		result = null;
		
		// Set the active flag to true, so the agents won't get inactive notification
		// on the beginning of the first turn.
		wasActiveCurrentTurn.put(agentA.getAgentID(), true);
		wasActiveCurrentTurn.put(agentB.getAgentID(), true);
		
        try {
            // note, we clone the utility spaces for security reasons, so that the agent
        	 // can not damage them.
        	
        	WorldInformation reducedWorldInfoA =
        		new WorldInformation(worldInformationA.getOpponentID(), worldInformationA.getMediatorID());
        	for (int i=0;i<worldInformationA.getNumOfPossibleUtiltySpaces();i++)
        	{
        		reducedWorldInfoA.addUtilitySpace(worldInformationA.getUtilitySpace(i));
        	}
        	WorldInformation reducedWorldInfoB =
        		new WorldInformation(worldInformationB.getOpponentID(), worldInformationB.getMediatorID());
        	for (int i=0;i<worldInformationB.getNumOfPossibleUtiltySpaces();i++)
        	{
        		reducedWorldInfoB.addUtilitySpace(worldInformationB.getUtilitySpace(i));
        	}
        	
            agentA.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,turnLength,
            		new UtilitySpace(spaceA),agentAparams,reducedWorldInfoA);
            agentA.init();
            agentB.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,turnLength,
            		new UtilitySpace(spaceB),agentBparams,reducedWorldInfoB);
            agentB.init();
            agentA.setActionListener(this);
            agentB.setActionListener(this);
            if (mediator!=null) {
            	HashMap<AgentID,WorldInformation> mediatorWorldInformation=new HashMap<AgentID,WorldInformation>();
            	mediatorWorldInformation.put(agentA.getAgentID(),worldInformationA);
            	mediatorWorldInformation.put(agentB.getAgentID(),worldInformationB);
            	mediator.internalInit(sessionNumber, sessionTotalNumber, startTime, totalTime, turnLength,mediatorWorldInformation,spaceA.getDomain());
            	mediator.init();
            	mediator.setActionListener(this);
            }
            stopNegotiation = false;
            isFinished = false;
            // check if we need to delay one of the agent's messages
            if (agentA.isUIAgent() && !agentB.isUIAgent())
            	actualDelayAgentB=delayMiliSec;
            else if (!agentA.isUIAgent() && agentB.isUIAgent())
            	actualDelayAgentA=delayMiliSec;
 
            // this was moved here from AsyncOffersProtocol::runNegotiationSession so the event will be thrown 
            // only after the agents are initialized. Otherwise the web interface may crash as some information is missing
            AsyncOffersProtocol asyncProtocol=(AsyncOffersProtocol)protocol;
            asyncProtocol.fireBilateralAtomicNegotiationSessionEvent(this, asyncProtocol.getProfileArep(), asyncProtocol.getProfileBrep(),
            											asyncProtocol.getAgentARep(), asyncProtocol.getAgentBRep());
        	fireLogMessage("Nego","Agent " + agentAname + " begins");
        	
        	
            // Inform Both agent of the negotiation beginning
            if (startingAgent.equals(agentAname)) {
            	agentA.ReceiveMessage(null);
            	agentB.ReceiveMessage(null);
            }
           	else {
           		agentB.ReceiveMessage(null);
            	agentA.ReceiveMessage(null);
            }
            
            if (mediator != null) {
            	mediator.ReceiveMessage(null);
            }
           
            // ------------------------------------------------------------------------------------
            // The main loop of the negotiation
            while(!stopNegotiation) {
            	long currentTime=(new Date()).getTime();
                while (!messageQueue.isEmpty() && messageQueue.get(0).sendTimeMili<=currentTime) {
                	ProcessAction(messageQueue.removeFirst().action);
                	if (stopNegotiation)
                		break;
                }
                // announce end turn if needed
                long currentTurn=(currentTime-startTimeMillies)/(turnLength*1000)+1;
                if (isTimeout()) {
                	stopNegotiation=true;
                	break;
                }
                if (currentTurn>lastReportedRound) {
                	changeTurn();
                }
                
            	Thread.sleep(10);
            }
            // ------------------------------------------------------------------------------------
            
            // If a full agreement was reached, and the mediator supports upgrading, enter the
            // upgrading phase
            if (isAutoEnd) {
	            if (no != null && no.typeOfAgreement.equals(AgreementType.Full)) {
	            	if (mediator != null && mediator.supportUpgradeAgreement()) {
	            		upgradeAgreementPhase();
	            	}
	            }
            }
            
            // If we don't have an agreement in this stage, there might have been a timeout
			if(no==null) {
				JudgeTimeout();
			}
			
			// If there was no timeout, than there was an error
			if(no != null) {
				// Notify of the negotiation outcome
	    		phase = Phase.Ended;
				sendUpdateStatus();
			}

        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	// This happens when the thread is brutally killed (using stop).
            	// This indicates that the thread was stuck and didn't attend the interrupts.
            	// If we catch it without re-throwing it, than the thread will not die.
            	System.err.println("Negotiation session was killed by ThreadDeath.");
    		}
            isFinished = true;
            throw(e);
        }
        catch (InterruptedException e) {
        	// This indicates that we were sent an interrupt, that is meant to end the negotiations
			new Warning("Negotiation session had InterruptedException",e,true,3);
        	try {
				StackTraceElement[] stack = e.getStackTrace();
				String additionalError = " Active stack trace: ";
				for (StackTraceElement trace : stack) {
					additionalError += "\n " + trace.toString();
				}
				newOutcome( -1,-1,null,AgreementType.Error, "Error in session. Session was interrupted." + additionalError);
			} catch (Exception e1) {
				new Warning("Negotiation Session have an exception",e1,true,3);
				e1.printStackTrace();
			}
        }
        catch (RuntimeException e) {
        	throw e;
        }
        catch (Exception e) {
			new Warning("Negotiation Session have an exception",e,true,3);
			e.printStackTrace();
		}

        // notify parent that we're finished.
        isFinished = true;
        synchronized (protocol) { protocol.notify(); }
    }

    /**
     * Increments the current turn.
     * The method sends a different action for each agent, to inform it if 
     * it was inactive.
     */
	private void changeTurn() {
		lastReportedRound++;
		
		// Create a different action for each agent, to inform it if it was inactive.
		// The general action is used for the log and for the mediator.
		EndTurn et=new EndTurn((int)lastReportedRound);
		EndTurn etForA=new EndTurn((int)lastReportedRound);
		EndTurn etForB=new EndTurn((int)lastReportedRound);
		
		if (wasActiveCurrentTurn.get(agentA.getAgentID())) {
			etForA.setWasInactive(false);
			wasActiveCurrentTurn.put(agentA.getAgentID(), false);
		}
		else {
			etForA.setWasInactive(true);
		}

		if (wasActiveCurrentTurn.get(agentB.getAgentID())) {
			etForB.setWasInactive(false);
			wasActiveCurrentTurn.put(agentB.getAgentID(), false);
		}
		else {
			etForB.setWasInactive(true);
		}


		// Send the action
		et.setRound(lastReportedRound);
		fireNegotiationActionEvent(/*actor=*/null, et, System.currentTimeMillis()-startTimeMillies,
				0,0,"");
		
		agentA.ReceiveMessage(etForA);
		agentB.ReceiveMessage(etForB);
		if (mediator!=null)
			mediator.ReceiveMessage(et);
	}

	
    /**
     * 
     * 
     */
    private void reachedAgreementPhase() throws Exception {

    	try {
    		inReachedAgreement = true;
	    	
			boolean agentAAnswered = false;
	    	boolean agentBAnswered = false;
	    	
	    	Bid agentABid = null;
	    	Bid agentBBid = null;
	  	
	    	boolean bothAnswered = false;
	    	
			// Wait for the negotiators' responses
	        while(!stopNegotiation && !bothAnswered) {
	        	long currentTime = (new Date()).getTime();
	        	
	            // Check for the end of the negotiation
	            if (isTimeout()) {
	    			new Warning("Negotiation session was timed-out during reached agreement phase",false,10000);
	            	stopNegotiation=true;
	            	break;
	            }
	            
	            // Get the action from the message queue
	            while (!stopNegotiation && !messageQueue.isEmpty() && messageQueue.get(0).sendTimeMili<=currentTime) {
	            	
	            	Action receivedAction = messageQueue.removeFirst().action;
	            	
	            	sender = getAgentById(receivedAction.getAgent());
	            	
	    			if (sender == null)
	    				throw new NegotiatorException("Got an Action with wrong AgentID");
	            	
	    	 		// Notify of the action
	    			receivedAction.setRound(lastReportedRound);
	    	        fireNegotiationActionEvent(sender, receivedAction, System.currentTimeMillis()-startTimeMillies, 
	    	        		0, 0, "");
	
	    			fireLogMessage("Nego", sender.getName() + " sent the following " + 
	    					getActionType(receivedAction) + ":");
		            fireLogMessage("Nego", receivedAction.toString());
	
	    			negotiatorActions.get(sender.getAgentID()).add(receivedAction);
	    			
	    	        // If the action is ValidateAgreement
	            	if (receivedAction instanceof ValidateAgreement) {
	            		
	            		ValidateAgreement validateAction = (ValidateAgreement)receivedAction;
	            		
	            		// Store the answers
	            		if (validateAction.getAgent().equals(agentA.getAgentID())) {
	            			agentAAnswered = true;
	            			agentABid = validateAction.getBid();
	            		}
	            		else if (validateAction.getAgent().equals(agentB.getAgentID())) {
	            			agentBAnswered = true;
	            			agentBBid = validateAction.getBid();
	            		}
	            	}
	            	else {
	    				new Warning("Negotiator sent invalid action during reached-agreement phase");
	            	}
	            	
	            	// If both negotiators answered
	            	if (agentAAnswered && agentBAnswered)
	            		bothAnswered = true;
	            }
	
	            Thread.sleep(10);
	        }
	
	        if (!stopNegotiation) {
		        // If one agent didn't send an agreement (the agent canceled)
		        if (agentABid == null || agentBBid == null) {
					fireLogMessage("Nego", "Not both negotiators agreed.");
		    		String accompanyText = "One of you did not enter an agreement.";
		    		phase = Phase.ContinueNegotiation;
		    		sendUpdateStatus(accompanyText);
		    		phase = Phase.Negotiation;
		    	}
		        // If one agent sent a partial agreement
		        else if (!protocol.getDomain().isFullBid(agentABid) ||
		        		!protocol.getDomain().isFullBid(agentBBid)) {
					fireLogMessage("Nego", "Not both negotiators offered full agreement.");
		    		String accompanyText = "One of you did not enter a full agreement.";
		    		phase = Phase.ContinueNegotiation;
		    		sendUpdateStatus(accompanyText);
		    		phase = Phase.Negotiation;
		        }
		        // If the agreements are different
		        else if (!agentABid.equals(agentBBid)) {
					fireLogMessage("Nego", "Agreements are different.");

					String accompanyText = "There is still a disagreement on these issues: ";
		    		for (Integer issue : agentABid.getValues().keySet()) {
		    			if (!agentABid.getValue(issue).equals(agentBBid.getValue(issue))) {
		    				accompanyText += protocol.getDomain().getObjective(issue).getName() + ", ";
		    			}
		    		}	    		
		    		accompanyText = accompanyText.substring(0, accompanyText.lastIndexOf(","));
		    		accompanyText += ".";
		    		
		    		phase = Phase.ContinueNegotiation;
		    		sendUpdateStatus(accompanyText);
		    		phase = Phase.Negotiation;
		        }
		        // If an agreement was reached
		        else {
		        	stopNegotiation = true;
					finalBid = agentABid.clone();
					finalBid.setTime(lastReportedRound);
		            Global.logStdout("AsyncOffersBilateralAtomicNegoSession", "agentABid.clone finalBid="+finalBid, "");
		
			    	// Get the negotiators utilities
			    	double finalUtilityA = spaceA.getUtility(finalBid);
			 		double finalUtilityB = spaceB.getUtility(finalBid);
			    	
		            // Set the outcome of the negotiations
			        newOutcome(finalUtilityA, finalUtilityB, null, AgreementType.Full, null);
		        }
	        }
        
	        inReachedAgreement = false;
    	}
    	catch (Exception e) {
	        inReachedAgreement = false;
    		throw (e);
    	}
    }

    
    /**
     * Runs the agreement upgrading phase, in which the mediator offers a upgrade to the
     * accepted agreement.
     * If both negotiators accept the upgraded agreement, the upgraded agreement is used as the
     * outcome. Otherwise, the original reached agreement is used.
     */
    private void upgradeAgreementPhase() {
        try {
        	// Notify of the new phase
    		phase = Phase.UpgradeAgreement;
    		sendUpdateStatus();

    		boolean agentAAccept = false;
	    	boolean agentBAccept = false;
	    	boolean agentAReject = false;
	    	boolean agentBReject = false;
	    	
	    	stopNegotiation = false;
	    	
	    	// Get the upgrade from the mediator
	    	Action upgradeAction = null;
	    	
	    	if (mediator != null && resultIsNotError()) {
	    		upgradeAction = mediator.upgradeAgreement(finalBid);
	    	}
	    	
	    	// If has an upgrade
	    	if (upgradeAction == null)
	    		return;
	    	
	    	// Check for validity of upgrade
	    	if (!(upgradeAction instanceof OfferUpgrade)) {
				throw new NegotiatorException("Mediator sent invalid action");
	    	}
	    	
	    	Bid tmpFinalBid = ((OfferUpgrade)upgradeAction).getBid();
	    	tmpFinalBid.setTime(lastReportedRound);
	
	    	// Get the negotiators upgraded utilities
	    	double finalUtilityA = spaceA.getUtility(tmpFinalBid);
	 		double finalUtilityB = spaceB.getUtility(tmpFinalBid);
	
        	double utilA_Penalty = 0;
        	double utilB_Penalty = 0;
        	// If we have a mediator and the agreement is not errornous, we check if an ultimatum was activated
        	if (mediator != null && resultIsNotError()) {
        		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
        		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
        	}
	 		
	 		// Notify of the upgrade action
        	upgradeAction.setRound(lastReportedRound);
	        fireNegotiationActionEvent(mediator, upgradeAction, System.currentTimeMillis()-startTimeMillies, 
	        		finalUtilityA+utilA_Penalty, finalUtilityB+utilB_Penalty, "");
	        
			negotiatorActions.get(mediator.getAgentID()).add(upgradeAction);
			
            fireLogMessage("Nego", "The mediator offered the following upgrade: " + upgradeAction.toString());

            // Send the upgrade offer to both negotiators
	  	   	agentA.ReceiveMessage(upgradeAction);
			agentB.ReceiveMessage(upgradeAction);
			
			// Wait for the negotiators' responses
	        while(!stopNegotiation) {
	        	long currentTime=(new Date()).getTime();
	        	
	        	// Get the action from the message queue
	            while (!messageQueue.isEmpty() && messageQueue.get(0).sendTimeMili<=currentTime) {
	            	
	            	Action recievedAction = messageQueue.removeFirst().action;
	            	
	            	sender = getAgentById(recievedAction.getAgent());
	            	
	    			if (sender == null)
	    				throw new NegotiatorException("Got an Action with wrong AgentID");
	            	
	    	 		// Notify of the action
	    			recievedAction.setRound(lastReportedRound);
	    	        fireNegotiationActionEvent(sender, recievedAction, System.currentTimeMillis()-startTimeMillies, 
	    	        		0, 0, "");
	    	        
	    			negotiatorActions.get(sender.getAgentID()).add(recievedAction);

	    			fireLogMessage("Nego", sender.getName() + " sent the following " + getActionType(recievedAction) + ":");
		            fireLogMessage("Nego", recievedAction.toString());

	    	        // If the action is accept
	            	if (recievedAction instanceof Accept) {
	            		AcceptOrReject acceptAction = (AcceptOrReject)recievedAction;
	            		
	            		// Check the validity of the action
	            		if (acceptAction.getAcceptedOrRejectedAction()==null)
		    				throw new NegotiatorException("Negotiator accepted null action");
	            		if (!acceptAction.getAcceptedOrRejectedAction().equals(upgradeAction)) {
		    				throw new NegotiatorException("Negotiator accepted wrong action");
	            		}
	            		// Store the acceptance
	            		else if (acceptAction.getAgent().equals(agentA.getAgentID())) {
	            			agentAAccept = true;
	            		}
	            		else if (acceptAction.getAgent().equals(agentB.getAgentID())) {
	            			agentBAccept = true;
	            		}
	            	}
	            	// If the action is reject
	            	else if (recievedAction instanceof Reject) {
	            		Reject rejectAction = (Reject)recievedAction;
	            		
	            		// Check the validity of the action
	            		if (rejectAction.getAcceptedOrRejectedAction()==null) 
		    				throw new NegotiatorException("Negotiator rejected null action");
	            		if (!rejectAction.getAcceptedOrRejectedAction().equals(upgradeAction)) 
		    				throw new NegotiatorException("Negotiator rejected wrong action");
	            		// Store the reject
	            		else if (rejectAction.getAgent().equals(agentA.getAgentID())) 
	            			agentAReject = true;
	            		else if (rejectAction.getAgent().equals(agentB.getAgentID())) 
	            			agentBReject = true;
	            	}
	            	else {
	            		throw new NegotiatorException("Negotiator sent invalid action");
	            	}
	            	
	            	// If both negotiators accepted or one rejected, end the round
	            	if ((agentAAccept && agentBAccept) || agentAReject || agentBReject)
	            		stopNegotiation = true;
	            }
	
	            Thread.sleep(10);
	        }

        	// If both negotiators accepted
	        if (agentAAccept && agentBAccept) {
	        	
	            finalBid = tmpFinalBid;
	            Global.logStdout("AsyncOffersBilateralAtomicNegoSession", "upgraded finalBid="+finalBid, "");
	            
	            // Update the outcome of the negotiations
		        newOutcome(finalUtilityA, finalUtilityB, upgradeAction.getAgent(), AgreementType.Full, null);
	        }
        }
        catch (Exception e) {
        	System.err.println("upgradeAgreementPhase: EXCEPTION: " + e);
        	e.printStackTrace(); 
        }
    }
    
    
    public void newOutcome(double utilA, double utilB, AgentID lastActor, NegotiationEndedEvent.AgreementType typeOfAgreement, String message) throws Exception {
        
    	result = typeOfAgreement;
    	
    	double utilA_Penalty = 0;
    	double utilB_Penalty = 0;
    	// If we have a mediator and the agreement is not errornous, we check if an ultimatum was activated
    	if (mediator != null && resultIsNotError()) {
    		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
    		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
    		
            fireLogMessage("Nego","Penalty of " + agentA.getName() +": " + utilA_Penalty);
            fireLogMessage("Nego","Penalty of " + agentB.getName() +": " + utilB_Penalty);
    	}
    	
    	Global.logStdout("AsyncOffersBilateralAtomicNegoSession.newOutcome", "utilA: " + utilA, "");
    	Global.logStdout("AsyncOffersBilateralAtomicNegoSession.newOutcome", "utilB: " + utilB, "");
    	Global.logStdout("AsyncOffersBilateralAtomicNegoSession.newOutcome", "utilA_Penalty: " + utilA_Penalty, "");
    	Global.logStdout("AsyncOffersBilateralAtomicNegoSession.newOutcome", "utilB_Penalty: " + utilB_Penalty, "");
    	Global.logStdout("AsyncOffersBilateralAtomicNegoSession.newOutcome", "finalBid: " + finalBid, "");
    	
    	no=new NegotiationOutcome(sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,
            utilB,
            utilA_Penalty,
            utilB_Penalty,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            startingWithA, 
            spaceA.getFileName(),
            spaceB.getFileName(),
            typeOfAgreement,
            additionalLog
            );
    	
    	fireNegotiationEndedEvent(System.currentTimeMillis()-startTimeMillies, utilA + utilA_Penalty, utilB + utilB_Penalty, lastActor,
    			typeOfAgreement, finalBid, message);  		
    	
    }
    
    /**
     * This is called whenever the protocol is timed-out. 
     */
    public void JudgeTimeout() {
		try {
			// Check if we had a timeout
            if (isTimeout()) {
            	stopNegotiation=true;
	
	            double agentAUtility,agentBUtility;
				NegotiationEndedEvent.AgreementType agreementType;
	
				// Get the timeout utilities
				agentAUtility=spaceA.getReservationValue(numOfTurns);
				agentBUtility=spaceB.getReservationValue(numOfTurns);
		    	//Global.logStdout("AsyncOffersBilateralAtomicNegoSession.JudgeTimeout", "agentAUtility="+agentAUtility+" agentBUtility="+agentBUtility, "");
				
				finalBid = null;
	            Global.logStdout("AsyncOffersBilateralAtomicNegoSession", "sq finalBid="+finalBid, "");
				agreementType = NegotiationEndedEvent.AgreementType.StatusQuo;
	
	    		newOutcome(agentAUtility, agentBUtility, null, agreementType,"negotiation has timed out");
            }
        }
		catch (Exception err) {
			new Warning("error during creation of new outcome:",err,true,2);
			err.printStackTrace();
		}
    }
    public NegotiationOutcome getNegotiationOutcome() {
    	return no;
    }
	@Override
	public String getStartingAgent() {
		return startingAgent;
	}
	public void setStartingWithA(boolean val) { 
		startingWithA = val;
	}
	public void setTotalTime(int val) {
		totalTime = val;
	}
	public void setSessionTotalNumber(int val) {
		sessionTotalNumber = val;
	}

	
	/* (non-Javadoc)
	 * @see negotiator.ActionListener#actionSent(negotiator.actions.Action)
	 */
	@Override
	public void actionSent(Action action) {
			// find the right delay (if any) for this message
			long sendTime=new Date().getTime();
			if (action.getAgent().equals(agentA.getAgentID()))
				sendTime+=actualDelayAgentA;
			else if (action.getAgent().equals(agentB.getAgentID()))
				sendTime+=actualDelayAgentB;
			// find the right place in the processing queue
			int i=0;
			while (i<messageQueue.size()) {
				if (messageQueue.get(i).sendTimeMili>sendTime) 
					break;
				else 
					i++;
			}
			// add this action to the processing queue. with the correct delay
			try {
				messageQueue.add(i,new MessageWrapper(sendTime,(Action)action.clone()));
			}
			catch (CloneNotSupportedException e) {
				new Warning("actionSent: Exeption during action cloning", e, true, 5);
			}
	}
	
	/**
	 * This is where the real processing of each message sent by the agent occur.
	 * @param action
	 * @author Yinon Oshrat
	 * @throws InterruptedException 
	 */
	private void ProcessAction(Action action) throws InterruptedException {
		try {
			receivers.clear();
			sender = getAgentById(action.getAgent());
			
			if (sender == null)
				throw new NegotiatorException("Got an Action with wrong AgentID");
				
			// Store the action
			negotiatorActions.get(sender.getAgentID()).add(action);
			
			// Set the receivers of the action
			if (action.getDestination() == null) {
				if (sender == mediator) {
					receivers.add(agentB);
					receivers.add(agentA);
				}
				else {
					receivers.add(getOtherParty(sender));
					if (mediator != null)
						receivers.add(mediator);
				}
			}
			else {
				receivers.add(getAgentById(action.getDestination()));
			}
			
			// Check the type of action
        	action.setRound(lastReportedRound);
	        if(action instanceof EndNegotiation) {
	        	if (sender==mediator)
	        		return;

	        	// agent optout  
	            stopNegotiation=true;
	            finalBid = new Bid().setOptOut();
	            finalBid.setTime(lastReportedRound);
	            Global.logStdout("AsyncOffersBilateralAtomicNegoSession", "optout finalBid="+finalBid, "");
	            
	            Double utilA=spaceA.getOptOutValue(lastReportedRound);
	            Double utilB=spaceB.getOptOutValue(lastReportedRound);
	            if (utilA == null)
	            	utilA = 0.0;
	            if (utilB == null)
	            	utilB = 0.0;

	            fireLogMessage("Nego","Agent " + sender.getName() + " has opted-out");

	        	double utilA_Penalty = 0;
	        	double utilB_Penalty = 0;
	        	// If we have a mediator and the agreement is not erroneous, we check if an ultimatum was activated
	        	if (mediator != null && resultIsNotError()) {
	        		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
	        		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
	        	}
	            
	            fireNegotiationActionEvent(sender,action, System.currentTimeMillis()-startTimeMillies,
	            		utilA+utilA_Penalty,utilB+utilB_Penalty,sender.getName() + " Opted out");
	            newOutcome(utilA, utilB, sender.getAgentID(),AgreementType.Optout, "Agent "+sender.getName()+" ended the negotiation without agreement");
	            passActionToRecivers(action);
	        }
	        else if (action instanceof AgreementReached) {
	        	fireLogMessage("Nego","Agent " + sender.getName() + " think an agreement was reached");
	            fireNegotiationActionEvent(sender,action, System.currentTimeMillis()-startTimeMillies,
	            		0,0,sender.getName() + " think an agreement was reached");
	        	receivers.add(sender);
	            passActionToRecivers(action);
	        	
	        	// Enter the agreement reached phase, and check if both think they reached the
	        	// same agreement
	        	reachedAgreementPhase();
	        }
	        else if (action instanceof ValidateAgreement) {
	        	// This is check before BidAction, because ValidateAgreement extends BidAction
		        new Warning("Agent sent ValidateAgreement during negotiation", false, 5);
	        }
	        else if (action instanceof Offer || action instanceof CounterOffer || action instanceof Ultimatum) {
	        	String actionType=getActionType(action);
	            fireLogMessage("Nego",sender.getName() + " sent the following " + actionType + ":");
	            fireLogMessage("Nego",action.toString());
	            
	            double utilA = 0;
            	double utilB = 0;
	            lastBid  = ((BidAction)action).getBid();  
	            if (lastBid != null) {
		            //lastBid.setTime(lastReportedRound); // already done by action.setRound
		            utilA = agentA.utilitySpace.getUtility(lastBid);
		            utilB = agentB.utilitySpace.getUtility(lastBid);
		            fireLogMessage("Nego","Utility of " + agentA.getName() +": " + utilA);
		            fireLogMessage("Nego","Utility of " + agentB.getName() +": " + utilB);
		            wasActiveCurrentTurn.put(sender.getAgentID(), true);
		            //save last results 
		            BidPoint p=null;
		    		p=new BidPoint(lastBid,
		    				   spaceA.getUtility(lastBid),
		    				   spaceB.getUtility(lastBid));
	
		    		if (sender.equals(agentA))
		    			fAgentABids.add(p);
		    		else if (sender.equals(agentB))
		    			fAgentBBids.add(p);
		    		
		    		// Store the bidAction
	            	int offerIndex = receivedOffers.indexOf(new ReceivedOffer((BidAction)action));
	            	if (offerIndex == -1) {
	            		receivedOffers.add(new ReceivedOffer((BidAction)action));
	            	} else {
	            		// If the bid was offered before, clear the rejected flags for it
	            		receivedOffers.get(offerIndex).rejected.put(agentA.getAgentID(), false);
	            		receivedOffers.get(offerIndex).rejected.put(agentB.getAgentID(), false);
	            		receivedOffers.get(offerIndex).accepted.put(sender.getAgentID(), true);
	            	}
	            	
	            } else {
	            	if (!(action instanceof Ultimatum)) {
	            		throw new NegotiatorException("BidAction with null bid by agent "+ sender.getName());
	            	}
	            }
	            
	        	double utilA_Penalty = 0;
	        	double utilB_Penalty = 0;
	        	// If we have a mediator and the agreement is not errornous, we check if an ultimatum was activated
	        	if (mediator != null && resultIsNotError()) {
	        		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
	        		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
	        	}
	            fireNegotiationActionEvent(sender,action, System.currentTimeMillis()-startTimeMillies,
	            		utilA+utilA_Penalty,utilB+utilB_Penalty,actionType + " by "+sender.getName());
	            passActionToRecivers(action);
	        }   // end of  if(  action instanceof Offer...)       
	        else if (action instanceof BidAction) {
        		throw new NegotiatorException("unknown BidAction by agent " + sender.getName() + ":" + action.getClass().toString());
	        }
	        else if (action instanceof Reject ) {
	        	fireLogMessage("Nego","Agent " + sender.getName() + " Rejected the following:");
	            fireLogMessage("Nego",action.toString());
	            BidAction rejectedAction = ((Reject)action).getAcceptedOrRejectedAction();
	            if (rejectedAction==null)
	            	throw new NegotiatorException("Agent tried to reject a null offer");

            	// Check that the offer was seen before
            	int offerIndex = receivedOffers.indexOf(new ReceivedOffer(rejectedAction));
            	if (offerIndex == -1)
	            	throw new NegotiatorException("Agent tried to reject a non-existing offer");
            	
            	ReceivedOffer receivedOffer = receivedOffers.get(offerIndex);

            	// Yoshi: for now this check will be performed only in the agents,
            	// because they can send the same offer twice.
            	// Make sure that the offer was not accepted before
            	//if (receivedOffer.accepted.get(sender.getAgentID())) {
	            //	throw new NegotiatorException("Agent tried to reject a previously accepted offer");
            	//}
            	
            	// Mark the offer as rejected
            	receivedOffer.rejected.put(sender.getAgentID(), true);
	        	
            	// Notify of the action
	        	passActionToRecivers(action);
        		fireNegotiationActionEvent(sender,action,System.currentTimeMillis()-startTimeMillies,0,0,"Rejected by "+sender.getName());
	        }
	        else if (action instanceof Accept) {
	        	// The mediator cannot accept offers
	        	if (sender==mediator)
	        		return;
	        	
	        	AcceptOrReject accept = (AcceptOrReject)action;
	            BidAction acceptedAction = accept.getAcceptedOrRejectedAction();
	        	if (acceptedAction == null)
	           		throw new NegotiatorException(sender.getName()+ " accepted a null action");
	            
	        	fireLogMessage("Nego","Agent " + sender.getName() + " Accepted the following:");
	            fireLogMessage("Nego",accept.toString());
	            
            	// Check that the offer was seen before
            	int offerIndex = receivedOffers.indexOf(new ReceivedOffer(acceptedAction));
            	if (offerIndex == -1) {
	            	throw new NegotiatorException("Agent tried to accept a non-existing offer");
            	}
            	
            	ReceivedOffer receivedOffer = receivedOffers.get(offerIndex);
            	
            	// Yoshi: for now this check will be performed only in the agents,
            	// because they can send the same offer twice.
            	// Make sure that the offer was not rejected before
            	//if (receivedOffer.rejected.get(sender.getAgentID())) {
	            //	throw new NegotiatorException("Agent tried to accept a previously rejected offer");
            	//}
            	
            	// Mark the offer as accepted
            	receivedOffer.accepted.put(sender.getAgentID(), true);
            	
	            boolean bothAccepted = false;
	            
            	// Check if both sides accepted the offer
            	// (needed in case the offer was proposed by the mediator)
            	if (receivedOffer.accepted.get(getOtherParty(sender).getAgentID())) {
            		bothAccepted = true;
            	}	
	            
	            // Calculate the combined bid and the utilities
        		Bid tmpFinalBid=finalBid;
        		if (tmpFinalBid==null)
        			tmpFinalBid=acceptedAction.getBid();
            	else
            		tmpFinalBid=tmpFinalBid.combinBid(acceptedAction.getBid());
        		tmpFinalBid.setTime(lastReportedRound);
        		double finalUtilityA=spaceA.getUtility(tmpFinalBid);
         		double finalUtilityB=spaceB.getUtility(tmpFinalBid);
         		
	        	double utilA_Penalty = 0;
	        	double utilB_Penalty = 0;
	        	// If we have a mediator and the agreement is not errornous, we check if an ultimatum was activated
	        	if (mediator != null && resultIsNotError()) {
	        		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
	        		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
	        	}
         		
         		// Notify of the action
	            fireNegotiationActionEvent(sender,action, System.currentTimeMillis()-startTimeMillies,
	            		finalUtilityA+utilA_Penalty,finalUtilityB+utilB_Penalty,"");
	            passActionToRecivers(action);
	            
	            // If both sides accepted a binding offer, update the final bid reached
	            if (bothAccepted) {
		            if (acceptedAction instanceof Offer || acceptedAction instanceof CounterOffer) {
		            	
		            	finalBid=tmpFinalBid;
			            Global.logStdout("AsyncOffersBilateralAtomicNegoSession", "bothAccepted finalBid="+finalBid, "");
		            	
		            	// If we have a full agreement, stop the negotiations
			        	if (isAutoEnd && spaceA.getDomain().isFullBid(finalBid)) {
			        		stopNegotiation = true;   
			        		newOutcome(finalUtilityA, finalUtilityB,sender.getAgentID(),AgreementType.Full, null);
			        	}
			        	else
			        	{
			        		// If the negotiation continues, update of the new partial agreement
			            	sendUpdateStatus();
			        	}
		            }
	            }
	            
	        } 
	        else if (action instanceof UltimatumThreat || action instanceof Threat || action instanceof Comment || action instanceof TextMessage) {
	        	fireLogMessage("Nego","Agent " + sender.getName() + " sent the following " + getActionType(action) + ":");
	            fireLogMessage("Nego",action.toString());
	            fireNegotiationActionEvent(sender,action, System.currentTimeMillis()-startTimeMillies,
	            		0,0,"");
	            passActionToRecivers(action);
	        }
	        else {  // action instanceof unknown action, e.g. null.
	     	   throw new NegotiatorException("unknown action by agent "+ sender.getName());
	        }
		 } catch (InterruptedException e) {
		 	throw (e);
		 } catch(Exception e) { // something wrong happened during action handling - change here if you want the negotiation to continue 
	        stopNegotiation=true;
	        new Warning("Protocol error: ",e,true,10000);
	        try {
	     	   newOutcome(-1,-1,action.getAgent(), AgreementType.Error, "Agent " + sender.getName() +":"+e.getMessage());
	     	   stopNegotiation = true;
	        }
	        catch (Exception err) { 
	        	new Warning("exception raised during exception handling: "+err); 
	        	err.printStackTrace(); 
	        }
	        // don't compute the max utility, we're in exception which is already bad enough.
	     }
	}
	
	private void sendUpdateStatus() {
		sendUpdateStatus(null);
	}

	private void sendUpdateStatus(String accompanyText) {

        //Global.logStdout("AsyncOffersBilateralAtomicNegoSession.sendUpdateStatus", "finalBid="+finalBid, "");
		
		double finalUtilityA = 0.0;
		double finalUtilityB = 0.0;

		// Creating an update action to each agent, so we can send it its utility.
		// The general action is created for the log file and for the mediator.
 		UpdateStatusAction update = new UpdateStatusAction(lastReportedRound, phase, finalBid, result);
 		UpdateStatusAction updateToA = new UpdateStatusAction(lastReportedRound, phase, finalBid, result);
 		UpdateStatusAction updateToB = new UpdateStatusAction(lastReportedRound, phase, finalBid, result);

        //Global.logStdout("AsyncOffersBilateralAtomicNegoSession.sendUpdateStatus", "update="+update, "");
 		
		if (phase.equals(Phase.Ended) && no != null) {
			updateToA.setYourUtility(no.agentAutility + no.agentAutilityPenalty);
			updateToB.setYourUtility(no.agentButility + no.agentButilityPenalty);
			finalUtilityA = no.agentAutility;
			finalUtilityB = no.agentButility;
		}
		
		if (accompanyText != null) {
			update.setAccompanyText(accompanyText);
			updateToA.setAccompanyText(accompanyText);
			updateToB.setAccompanyText(accompanyText);
		}
		

    	fireLogMessage("Nego","Update the negotiation status:");
        fireLogMessage("Nego",update.toString());

    	double utilA_Penalty = 0;
    	double utilB_Penalty = 0;
    	// If we have a mediator and the agreement is not errornous, we check if an ultimatum was activated
    	if (mediator != null && resultIsNotError()) {
    		utilA_Penalty = mediator.getUltimatumActivatedPenalty(agentA.getAgentID(), finalBid, lastReportedRound);
    		utilB_Penalty = mediator.getUltimatumActivatedPenalty(agentB.getAgentID(), finalBid, lastReportedRound);
    	}
        
    	update.setRound(lastReportedRound);

        fireNegotiationActionEvent(/*actor=*/null,update, System.currentTimeMillis()-startTimeMillies,
        		finalUtilityA+utilA_Penalty,finalUtilityB+utilB_Penalty,"");

    	agentA.ReceiveMessage(updateToA);
    	agentB.ReceiveMessage(updateToB);
    	
    	if (mediator!=null)
    		mediator.ReceiveMessage(update);	
	}
	
	/**
	 * @param sender2
	 * @return
	 * @author
	 */
	private ActionReceiver getOtherParty(ActionReceiver a) {
		if (a.getAgentID().equals(agentA.getAgentID()))
			return agentB;
		if (a.getAgentID().equals(agentB.getAgentID()))
			return agentA;
		return null;
	}

	
	private ActionReceiver getAgentById(AgentID id) {
		if (id.equals(agentA.getAgentID()))
			return agentA;
		if (id.equals(agentB.getAgentID()))
			return agentB;
		if (mediator != null && id.equals(mediator.getAgentID()))
			return mediator;
		return null;
	}
	
	/**
	 * Test if the current time exceeds the negotiation time.
	 * 
	 * @return true if there was a timeout
	 */
	public boolean isTimeout() {
    	long currentTime = System.currentTimeMillis();
        long additionalTime = 0;
        
        // Give the sides additional 100 seconds if they are in the reached agreement phase
        if (inReachedAgreement) {
        	additionalTime = 100; // in seconds
        }
        
		// Check if we had a timeout after starting the negotiation
        if (lastReportedRound > 0 && currentTime > startTimeMillies + (totalTime + additionalTime) * 1000) {
        	return true;
        }
        else {
        	return false;
        }
	}
	
	
	/**
	 * @param action
	 * @author
	 */
	private void passActionToRecivers(Action action) {
		try {
			for (ActionReceiver receiver:receivers)
				receiver.ReceiveMessage((Action)action.clone());
		}
		catch (CloneNotSupportedException e) {
			new Warning("passActionToRecivers: Exeption during action clonning", e, true, 5);
		}
	}

	private String getActionType(Action action) {
		if (action instanceof Query)
			return "Query";
		else if (action instanceof Offer)
			return "Offer";
		else if (action instanceof Promise)
			return "Promise";
		else if (action instanceof CounterOffer)
			return "CounterOffer";
		else if (action instanceof Accept)
			return "Accept";
		else if (action instanceof Reject)
			return "Reject";
		else if (action instanceof Threat)
			return "Threat";
		else if (action instanceof OfferUpgrade)
			return "OfferUpgrade";
		else if (action instanceof UltimatumThreat)
			return "UltimatumThreat";
		else if (action instanceof Ultimatum)
			return "Ultimatum";
		else if (action instanceof TextMessage)
			return "TextMessage";
		else
			return null;
	}
}

