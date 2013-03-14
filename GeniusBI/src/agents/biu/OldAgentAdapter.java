/**
 * 
 */
package agents.biu;

import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Global;
import negotiator.WorldInformation;
import negotiator.actions.*;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.Warning;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;



/**
 * This class acts as an interface between the old negotiation environment 
 * and structure and the new GeniusBI environment. It was written so that the KBAgent 
 * will be migrated to the new environment
 * @author Yinon Oshrat (minor updates by Inon Zuckerman)
 *  */
public abstract class OldAgentAdapter extends Agent implements AbstractAutomatedAgent {
	
	int currentTurn;
	AgentTools agentTools;
	public Bid acceptedBid;
	

	public OldAgentAdapter() {}	
	public static String getVersion() { return "2.0"; }
	
	/**
     * init() is called when a next session starts with the same opponent.
     */
    public void init() {
    	if (worldInformation==null)
    		worldInformation = new WorldInformation();
    	try {
			agentTools=new AgentTools(this);
	    	if (getName().equalsIgnoreCase("Agent A")) {
	    		agentTools.sMySide=AutomatedAgent.SIDE_A_NAME;
	    		agentTools.sOpponentType=AutomatedAgent.SIDE_B_NAME;
	    	}
	    	else if (getName().equalsIgnoreCase("Agent B")) {
	    		agentTools.sMySide=AutomatedAgent.SIDE_B_NAME;
	    		agentTools.sOpponentType=AutomatedAgent.SIDE_A_NAME;
	    	}
	    	else 
	    		new Warning("Agent name is not Agent A or Agent B - old agents may not work properly");
		    	// initializes the agent types using the World information files
	    	for (int i=0;i<worldInformation.getNumOfPossibleUtiltySpaces();i++) {
	        	calculateValues(agentTools.agentTypes[i], 1);
	        	calculateValues(agentTools.nextTurnAgentTypes[i], 2);
	        }
	        calculateValues(agentTools.myAgentType, 1);
	        calculateValues(agentTools.myNextTurnAgentType,2);
	    	initialize(agentTools.myAgentType, agentTools.sOpponentType);	
    	} catch (NegotiatorException e) {
			e.printStackTrace();
		}
    }

    /**
     * This method takes an Action option and handles the different cases
     */
	public void ReceiveMessage(Action opponentAction) {
		if (agentTools==null)
			init();
		int messageType = 0;
		Bid bid=null;
		if (opponentAction == null)
			return;
		if (opponentAction instanceof EndTurn) {
    		// a new turn begun - remember what is the current turn
    		currentTurn=((EndTurn)opponentAction).getTurn();
    		if (currentTurn>1) {// for the first turn we did this on initialization
    			for (int i=0;i<worldInformation.getNumOfPossibleUtiltySpaces();i++) {
    	        	calculateValues(agentTools.agentTypes[i], currentTurn);
    	        	calculateValues(agentTools.nextTurnAgentTypes[i], currentTurn +1);
    	        }
    	        calculateValues(agentTools.myAgentType, currentTurn);
    	        calculateValues(agentTools.myNextTurnAgentType, currentTurn +1 );
    		}
    		calculateOfferAgainstOpponent(agentTools.myAgentType, agentTools.sOpponentType, currentTurn);
    		return;
		}
		else if (opponentAction instanceof Accept) {
			messageType=AutomatedAgentMessages.ACCEPT;
			// the opponent Accepted one of our previous actions 
			BidAction acceptedAction=((AcceptOrReject)opponentAction).getAcceptedOrRejectedAction();
			if (acceptedAction instanceof Offer || acceptedAction instanceof CounterOffer) {
				Bid currentBid=acceptedAction.getBid();
				if (acceptedBid==null)
					acceptedBid = currentBid;
				else
					try {
						acceptedBid=acceptedBid.combinBid(currentBid);
					} catch (NegotiatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				bid=acceptedBid;
				opponentAgreed(messageType, agentTools.getIndicesFromBid(bid), opponentAction.toString());
				return;
			}
			else 
				bid=acceptedAction.getBid();
		}
		else if (opponentAction instanceof Reject) {
			messageType=AutomatedAgentMessages.REJECT;
			bid=((Reject)opponentAction).getAcceptedOrRejectedAction().getBid();
			opponentRejected(messageType, agentTools.getIndicesFromBid(bid), opponentAction.toString());
			return;
		}
		if (opponentAction instanceof BidAction) {
			bid=((BidAction)opponentAction).getBid();
			if (opponentAction instanceof Query)
				messageType=AutomatedAgentMessages.QUERY;
			else if (opponentAction instanceof Promise)
				messageType=AutomatedAgentMessages.PROMISE;
			else if (opponentAction instanceof Offer)
				messageType=AutomatedAgentMessages.OFFER;
			else if (opponentAction instanceof CounterOffer)
				messageType=AutomatedAgentMessages.COUNTER_OFFER;
			agentTools.pastActions.put(opponentAction.toString(), opponentAction);
		}
		else if (opponentAction instanceof Comment) {
			commentReceived(((Comment)opponentAction).getContent());
			return;
		}
		else if (opponentAction instanceof Threat) {
			threatReceived(((Threat)opponentAction).getContent());
			return;
		}
		else if (opponentAction instanceof UpdateStatusAction) {
			UpdateStatusAction updateAction = (UpdateStatusAction)opponentAction;
			Global.logStdout("OldAgentAdapter.ReceiveAction", "OK, status was adapted to "+updateAction+" what should I do now?", null);
			return;
		}
		else if (opponentAction instanceof EndNegotiation) {
			Global.logStdout("OldAgentAdapter.ReceiveAction", "OK, Negotiation ended, what do you want me to do now?", null);
			return;
		}
		else if (opponentAction instanceof negotiator.actions.TextMessage) {
			Global.logStdout("OldAgentAdapter.ReceiveAction", "OK, I heard that '"+opponentAction+"'", null);
			return;
		}
		else
		{
			//new Warning("Old agent got a type of action which it doesn't know how to handle"); return;
			throw new RuntimeException("Old agent got a type of action which it doesn't know how to handle: "+opponentAction);
		}
		calculateResponse(messageType, agentTools.getIndicesFromBid(bid), opponentAction.toString());
    }
}

/**
 * @author Yinon
 * This class should serve as an auxiliary class
 * The methods should not be changed/revised
 *
 */ 
class AgentTools {

	public static final String NO_AGREEMENT = "No agreement";
		
	OldAgentAdapter agent = null;
    //private int m_myAgentType;
    private int fNumberOfIssues;
    HashMap<String,Action> pastActions;
    AutomatedAgentType[] agentTypes=new AutomatedAgentType[3];
    AutomatedAgentType[] nextTurnAgentTypes=new AutomatedAgentType[3];
    String sOpponentType; // should hold the name of the opponent
    public String sMySide; // should hold my side
	private HashMap<String, int[]> allBidsIndices;
	private double m_currentTurnSelectedAgreementValue;
	private double m_nextTurnSelectedAgreementValue;
	private double m_dNextTurnAutomatedAgentValue = AutomatedAgentType.VERY_SMALL_NUMBER;
	private double m_currentTurnOpponentSelectedValue;
	private String m_currentTurnAgreementString;
	public AutomatedAgentType myAgentType;
	public AutomatedAgentType myNextTurnAgentType;
	
    /**
     * Constructor - called by OldAgentAdapter.init()  
     * Save a pointer to the AutomatedAgent class
     * @param agent - pointer to the AutomatedAgent class
     * @throws NegotiatorException - if the current domain is not comptible with old system
     */
    public AgentTools(OldAgentAdapter agent) throws NegotiatorException {
        this.agent = agent;
        pastActions=new HashMap<String, Action>();        	
        //create all agent types
        //if (agent.worldInformation.getNumOfPossibleUtiltySpaces()!=3) 
        //	throw new NegotiatorException("Old Agents must have 3 posible utility spaces of their opponent");
        for (int i=0;i<agent.worldInformation.getNumOfPossibleUtiltySpaces();i++) {
        	agentTypes[i]=new AutomatedAgentType(agent.worldInformation.getUtilitySpace(i),this);
        	nextTurnAgentTypes[i]=new AutomatedAgentType(agent.worldInformation.getUtilitySpace(i),this);
        }
        // create all bids and insert to allBidsIndices
        allBidsIndices=new HashMap<String, int[]>();
        myAgentType=new AutomatedAgentType(agent.utilitySpace, this);
        myNextTurnAgentType=new AutomatedAgentType(agent.utilitySpace, this);
        fNumberOfIssues=agent.utilitySpace.getDomain().getIssues().size();
        int[] fValuesIndexes= new int[fNumberOfIssues];
        for(int i=0;i<fNumberOfIssues;i++) 
        	fValuesIndexes [i] =-1;
        boolean more=true;
        while (more)
        {
        	Bid bid=getBidFromIndices(fValuesIndexes);
        	putIndicesForBid(bid, fValuesIndexes.clone());
        	fValuesIndexes=makeNextIndexes(fValuesIndexes);
        	// test if we finished to iterate through all possible bids
        	more=false;
        	for(int i=0;i<fNumberOfIssues;i++)
				if(fValuesIndexes[i]!=-1) {				
					more = true;
					break;
				}
        }
    }
    
    private int[] makeNextIndexes(int []fValuesIndexes) {
		int[] lNewIndexes = new int[fNumberOfIssues];
		for(int i=0;i<fNumberOfIssues;i++) 
			lNewIndexes [i] = fValuesIndexes[i];
		ArrayList<Issue> lIssues = agent.utilitySpace.getDomain().getIssues(); 
		for(int i=0;i<fNumberOfIssues;i++) {
			Issue lIssue = lIssues.get(i);
//			to loop through the Real and Price Issues we use discretization
			int lNumberOfValues=0;
			switch(lIssue.getType()) {
			case INTEGER:
				IssueInteger lIssueInteger =(IssueInteger)lIssue;
				lNumberOfValues = lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound()+1;
				break;
			case REAL: 
				IssueReal lIssueReal =(IssueReal)lIssue;
				lNumberOfValues = lIssueReal.getNumberOfDiscretizationSteps();
				break;
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
				lNumberOfValues = lIssueDiscrete.getNumberOfValues();
				break;
			}// switch
			if(lNewIndexes [i]<lNumberOfValues-1) {
				lNewIndexes [i]++;
				break;
			} else {
				lNewIndexes [i]=AutomatedAgentType.NO_VALUE;  // == -1
			}
			
		}//for
		return lNewIndexes;
	}
    
    public int[] getIndicesFromBid(Bid bid) {
    	int[] indices = allBidsIndices.get(bid.valuesToString());
    	if (indices==null) {
    		//System.out.println("allBidsIndices="+allBidsIndices);
    		throw new NullPointerException("No indices for bid "+bid);
    	}
    	return indices;
    }
    
    public void putIndicesForBid(Bid bid, int[] indices) {
    	allBidsIndices.put(bid.valuesToString(), indices);
    }
  
    /***********************************************
     * @@ Logic for sending messages
     * Below are messages the automated agent sends to the opponent
     * Call them from the AbstractAutomatedAgent class, and also
     * add any logic you need in that class, just before calling them
     * Do not add the logic in this class!
     ***********************************************/  
    
    /**
     * Called when you want to accept a message
     * @param sOriginalMessage - the message to be accepted
     */
	public void acceptMessage(String sOriginalMessage) {
		// get the accepted action from our past Action
		Action acceptedAction=pastActions.get(sOriginalMessage);
		if ( (acceptedAction instanceof Offer) || (acceptedAction instanceof CounterOffer)) {
			Bid currentAcceptedBid=((BidAction)acceptedAction).getBid();
			try {
				if (agent.acceptedBid==null)
					agent.acceptedBid=currentAcceptedBid;
				else
					agent.acceptedBid=agent.acceptedBid.combinBid(currentAcceptedBid);
			} catch (NegotiatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        // create and send an Accept action
		AcceptOrReject accept=new Accept(agent.getAgentID(),(BidAction)acceptedAction);
		agent.sendAction(accept);
    }

    /**
     * Called when you want to reject a message
     * @param sOriginalMessage - the message to be rejected
     */
    public void rejectMessage(String sOriginalMessage) {
    	// get the accepted action from our past Action
		Action rejectedAction=pastActions.get(sOriginalMessage);
        // create and send an Accept action
		Reject reject=new Reject(agent.getAgentID(),(BidAction)rejectedAction);
		agent.sendAction(reject);     
    }

    /**
     * 
     * @return the type of the automated agent
     */
    public AutomatedAgentType getAutomatedAgentType(){
        return myAgentType;
    }

    
    /**
     * Called when you want to send a message of type offer, counter offer, promise or query
     * @param currentAgreementIdx - the indices of the message
     */
    public void sendMessage(int nMessageType, int currentAgreementIdx[]) {
        Bid bid = getBidFromIndices(currentAgreementIdx);
        Action action= createBidAction(nMessageType, bid);
        agent.sendAction(action);  
    }
   

	public void optOut() {
    	EndNegotiation endNegotiation=new EndNegotiation(agent.getAgentID());
    	 agent.sendAction(endNegotiation);
    }
    
    /**
     * Called when you want to send a message of type offer, counter offer, promise or query
     * @param sMessage - the message as a String
     */
   /* 
   public void sendMessage(int nMessageType, String sMessage) {
        sMessage = agent.formatMessage(nMessageType, sMessage);
        
        //create a thread to send delayed message
        // You can choose how much time the agent should wait before sending the message to the opponent.
        // The delay time is defined the AutomatedAgentDelayedMessageThread class.
        AutomatedAgentDelayedMessageThread delayedMessageThread = new AutomatedAgentDelayedMessageThread(agent, sMessage, agent.getCurrentTurn());
        delayedMessageThread.start();
    }
*/
    
    /**
     * Called when you want to send an offer
     * @param sOffer - the offer to be sent
     */
    public void sendOffer(String sOffer) {
    	sendOffer(allBidsIndices.get(sOffer));
    }
    
    /**
     * Called when you want to send an offer
     * @param sOffer - the offer to be sent
     */
    public void sendOffer(int[] indices) {
    	Bid bid = getBidFromIndices(indices);
    	Offer action=new Offer(agent.getAgentID(),bid);
    	agent.sendAction(action);
    }
   
    /**
     * Called when you want to send a query
     * @param currentAgreementIdx - the indices of the query
     */
 	public void sendQuery(int currentAgreementIdx[]) {
 		 Bid bid = getBidFromIndices(currentAgreementIdx);
         Query action= new Query(agent.getAgentID(), bid);
         agent.sendAction(action); 
    }
    
    /**
     * Called when you want to send a promise
     * @param currentAgreementIdx - the indices of the promise
     */
 	public void sendPromise(int currentAgreementIdx[]) {
 		Bid bid = getBidFromIndices(currentAgreementIdx);
        Promise action= new Promise(agent.getAgentID(), bid);
        agent.sendAction(action);        
    }
    
    /**
     * Called when you want to send a counter offer
     * @param currentAgreementIdx - the indices of the counter offer
     */
 	public void sendCounterOffers(int currentAgreementIdx[]) {
 		Bid bid = getBidFromIndices(currentAgreementIdx);
        CounterOffer action= new CounterOffer(agent.getAgentID(), bid);
        agent.sendAction(action);      
    }

	/**
     * Called when you want to send a comment
     * @param sMessage - the comment to be sent
     */
    public void sendComment(String sMessage) {
    	for (int i=1;i<Comment.getNumberOfPossibleComments();i++) {
    		if (Comment.getPossibleComment(i).equals(sMessage)) {
    			Comment comment=new Comment(agent.getAgentID(),i);
    			agent.sendAction(comment);
    			break;
    		}
    	}
    }
    
    /**
     * Called when you want to send a threat
     * @param sMessage - the threat to be sent
     */
 	public void sendThreat(String sMessage) {
 		for (int i=1;i<Threat.getNumberOfPossibleThreats();i++) {
    		if (Threat.getPossibleThreat(i).equals(sMessage)) {
    			Threat threat=new Threat(agent.getAgentID(),i);
    			agent.sendAction(threat);
    			break;
    		}
    	}  
    }
    /***********************************************
     * @@ End of methods for sending message
     ***********************************************/
    
	// helper function
    /**
     * Get the total number of turns in the negotiation
     * @return total number of turns
     */	
    public int getTurnsNumber() {
     return agent.numOfTurns;   
    }
    
    /**
     * Get the current turn number
     * @return the current turn
     */
    public int getCurrentTurn() {
     return agent.currentTurn;   
    }
	
	// utility functions
    /**
     * @param agentType - the agent's type
     * @param CurrentAgreementIdx - the agreement indices
     * @param nCurrentTurn - the current turn for calculations
     * @return the value of a given agreement for the agent at a given turn
     */
    public double getAgreementValue(AutomatedAgentType agentType, int[] currentAgreementIdx, int nCurrentTurn) {
		return agentType.getAgreementValue(currentAgreementIdx, nCurrentTurn);
    }
    
    
    /**
     * Return the best agreement as string
     * @param agentType - the agent's type
     * @return the best agreement as String
     */
    public String getBestAgreementStr(AutomatedAgentType agentType) {
			return getMessageByIndices(agentType.m_BestAgreementIdx);
    }
    
    /**
     * Return the best agreement as string
     * @param agentType - the agent's type
     * @return the best agreement as String
     */
    public int[] getBestAgreementIndices(AutomatedAgentType agentType) {
			return agentType.m_BestAgreementIdx;
    }

    /**
     * Return the best agreement value for a given agent
     * @param agentType - the agent's type
     * @return the best agreement value computed for the current turn
     */
    public double getBestAgreementValue(AutomatedAgentType agentType) {
    	return agentType.getBestAgreementValue();
    }
    

    /**
     * Return the worst agreement as a String
     * @param agentType - the agent's type
     * @return the worst agreement as String
     */
    public String getWorstAgreementStr(AutomatedAgentType agentType) {
        return getMessageByIndices(agentType.m_WorstAgreementIdx);
    }

    /**
     * Return the worst agreement for a given agent
     * @param agentType - the agent's type
     * @return the worst agreement value computed for the current turn
     */
    public double getWorstAgreementValue(AutomatedAgentType agentType) {
        return agentType.getWorstAgreementValue();
    }
    /**
     * Sets the worst agreement value for a given agent
     * @param agentType - the agent's type
     * @param value - the value
     */
    public void setWorstAgreementValue(AutomatedAgentType agentType, double value) {
        agentType.m_dWorstAgreementValue=value;
    }

    /**
     * Sets the worst agreement indices for a given agent
     * @param agentType - the agent's type
     * @param currentAgreementIdx - the agreement indices
     */
    public void setWorstAgreementIndices(AutomatedAgentType agentType, int[] currentAgreementIdx) {
    	int nIssuesNum = agentType.getIssuesNum();       
        for (int k = 0; k < nIssuesNum; ++k) {
        	agentType.m_WorstAgreementIdx[k] = currentAgreementIdx[k];
        }  
    }
    /**
     * Initializes the worst agreement - 
     * inits the indices and sets maximal value
     * @param agentType - the agent's type
     */
    public void initializeWorstAgreement(AutomatedAgentType agentType) {
    	agentType.m_dWorstAgreementValue=AutomatedAgentType.VERY_SMALL_NUMBER;
 		int nIssuesNum = agentType.getIssuesNum(); 
 		agentType.m_WorstAgreementIdx=new int[nIssuesNum];
        for (int i = 0; i < nIssuesNum; ++i) {
         	agentType.m_WorstAgreementIdx[i] = 0;
        }	
    }
    /**
     * Sets the best agreement value for a given agent
     * @param agentType - the agent's type
     * @param value - the value
     */
    public void setBestAgreementValue(AutomatedAgentType agentType, double value) {
        agentType.m_dBestAgreementValue=value;
    }

    /**
     * Sets the best agreement indices for a given agent
     * @param agentType - the agent's type
     * @param currentAgreementIdx - the agreement indices
     */
    public void setBestAgreementIndices(AutomatedAgentType agentType, int[] currentAgreementIdx) {
    	int nIssuesNum = agentType.getIssuesNum();       
        for (int k = 0; k < nIssuesNum; ++k) {
        	agentType.m_BestAgreementIdx[k] = currentAgreementIdx[k];
        }
    }
    
    
    /**
     * Initializes the best agreement - 
     * inits the indices and sets minimal value
     * @param agentType - the agent's type
     */
    public void initializeBestAgreement(AutomatedAgentType agentType) {
        agentType.m_dBestAgreementValue=AutomatedAgentType.VERY_SMALL_NUMBER;
		int nIssuesNum = agentType.getIssuesNum();   
		agentType.m_BestAgreementIdx=new int[nIssuesNum];
        for (int i = 0; i < nIssuesNum; ++i) {
        	agentType.m_BestAgreementIdx[i] = 0;
        }		
    }
    
 

    /**
     * Return the time effect for the entire agreement
     * @param agentType - the agent's type
     * @return the time effect for the entire agreement
     */
   public double getAgreementTimeEffect(AutomatedAgentType agentType) {
        return agentType.us.getTimeEffectValue();
    }

    /**
     * Return the SQ value for a given agent
     * @param agentType - the agent's type
     * @return the status quo value computed for the current turn
     * for a given agent type
     */
    public double getSQValue(AutomatedAgentType agentType) {
        return agentType.us.getReservationValue();
    }

    /**
     * Return the opting out value for a given agent
     * @param agentType - the agent's type
     * @return the opting out value computed for the current turn
     */
    public double getOptOutValue(AutomatedAgentType agentType) {
        return agentType.us.getOptOutValue(0);
    }
    
    /**
     * @return the total number of agreement
     */
    public int getTotalAgreements(AutomatedAgentType agentType) {
        return (int)agentType.us.getDomain().getNumberOfPossibleBids();
    }
 
    
    /**
     * Set the automated agent type
     * Possible types: COMPROMISE_TYPE, SHORT_TERM_TYPE and LONG_TERM_TYPE 
     *
     */
    /*
    public void setAutomatedAgentType(String side) {
        // @@EXAMPLE@@
        // using the short term type for the automated agent, 
        // no matter which side it plays
        int agentType = AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;
        agent.setAgentType(side, agentType);

        m_myAgentType = agentType;
    }
   */
    /**
     * Set the automated agent type for the other automated agent
     * Used only if we run two automated agents
     * Possible types: COMPROMISE_TYPE, SHORT_TERM_TYPE and LONG_TERM_TYPE 
     *
     */
    /*
    public void setAnotherAutomatedAgentType(String side) {
        // @@EXAMPLE@@
        // using the short term type for the automated agent, 
        // no matter which side it plays
        int agentType = AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;
        agent.setAgentType(side, agentType);

        m_myAgentType = agentType;
    }
    */
    public String getAgentSide() {
        return sMySide;
    }
    
    /**
     * 
     * @return the selected offer of the agent at a given turn
     */
    public String getSelectedOffer() {
       return m_currentTurnAgreementString;
    }

    /**
     * 
     * @return the value of the selected offer of the agent at a given turn
     */
    public double getSelectedOfferValue() {
        int []nextAgreementIndices = allBidsIndices.get(m_currentTurnAgreementString);
        double dNextAgreementValue = getAgreementValue(nextAgreementIndices);
        return dNextAgreementValue;
    }
    
    /**
     * 
     * @return the value of previously accepted agreement
     */
    public double getAcceptedAgreementsValue() {
    	Bid bid =agent.acceptedBid;
    	double dAcceptedAgreementValue=0;
    	if (bid!=null) {
			try {
				dAcceptedAgreementValue = agent.utilitySpace.getUtilityWithTimeEffect(bid,agent.currentTurn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return dAcceptedAgreementValue;

    }
    
    public int[] getAcceptedAgreementIdx() {
        // The accepted agreement is saved in agent.acceptedBid
        // Note: agreements can be incremental. The m_PreviosAcceptedOffer saves the whole agreement
    	Bid bid =agent.acceptedBid;
    	if (bid!=null) 
    		return getIndicesFromBid(bid);
    	int[] agreementIdx=new int[agent.utilitySpace.getDomain().getIssues().size()];
    	for (int i=0;i<agreementIdx.length;i++)
    		agreementIdx[i]=AutomatedAgentType.NO_VALUE;
    	return agreementIdx;
        
    }

    /**
     * calculate the selected offer the agent will propose
     * in the following turn 
     *
     */
    public void calculateNextTurnOffer() {
    	agent.calculateOfferAgainstOpponent(myNextTurnAgentType, sOpponentType, agent.currentTurn+1);
    }
      
    /**
     * 
     * @return the value of the selected offer the agent will propose
     * in the following turn 
     */
    public double getNextTurnOfferValue() {
        return m_nextTurnSelectedAgreementValue;
    }
    
    /**
     * Iterator for going over all possible agreements
     * @param totalIssuesNum - the total number of issues in the negotiatoin
     * @param currentAgreementIdx - the current agreement indices
     * @param maxIssueValues - the maximal issue value
     */
    public void getNextAgreement(int totalIssuesNum, int[] currentAgreementIdx, int[] maxIssueValues) {
        //TODO:DEBUG THIS
        // update issue values indices for evaluating the next agreement
        boolean bFinishUpdate = false;
        for (int k = totalIssuesNum-1; k >= 0 && !bFinishUpdate; --k)
        {
            if (currentAgreementIdx[k]+1 >= maxIssueValues[k])
            {
                currentAgreementIdx[k] = 0;
            }
            else
            {
                currentAgreementIdx[k]++;
                bFinishUpdate = true;
            }                                   
        }
    }

    /**
     * Get the opponent's side
     * @param sideName - the type of side (A or B)
     * @param type - the type (compromise, short, long)
     * @return
     */
    public AutomatedAgentType getNextTurnSideAgentType(String sideName, int type) {
    	if (sideName.equalsIgnoreCase(agent.getName()))
            new Warning("Old agent ask for unimplemented method");
        AutomatedAgentType agentType = nextTurnAgentTypes[type];
        return agentType;
    }
    
    /**
     * Get the opponent's side
     * @param sideName - the type of side (A or B)
     * @param type - the type (compromise, short, long)
     * @return
     */
    public AutomatedAgentType getCurrentTurnSideAgentType(String sideName, int type) {
        if (agent.getName().equalsIgnoreCase(sideName))
        	new Warning("Old agent ask for unimplemented method");
        
        return agentTypes[type];
    }

    /**
     * 
     * @return the value of the selected offer for the next turn
     */
    //public double getNextTurnAutomatedAgentValue() {
    //    return m_dNextTurnAutomatedAgentValue ; 
    //}

    /**
     * 
     * @return the value of the selected offer for the current turn
     */
    public double getCurrentTurnAutomatedAgentValue() {
        return m_currentTurnSelectedAgreementValue;
    }

   
    
    /**
     * Sets the value of the selected offer for the current turn
     * @param agreementValue - the agreement's value
     */
    public void setCurrentTurnAutomatedAgentValue(double agreementValue) {
        m_currentTurnSelectedAgreementValue=agreementValue;
    }

    /**
     * Sets the value of the selected offer for the following turn
     * @param agreementValue - the agreement's value
     *//*
    public void setNextTurnAutomatedAgentSelectedValue(double agreementValue) {
        agent.setNextTurnAutomatedAgentSelectedValue(agreementValue);
    }
    
    *//**
     * Sets the value of the selected offer for the following turn
     * for the opponent
     * @param agreementValue - the agreement's value
     *//*
    public void setNextTurnOpponentSelectedValue(double agreementValue) {
        agent.setNextTurnOpponentSelectedValue(agreementValue);
    }
    
    *//**
     * Sets the value of the selected offer for the current turn
     * for the opponent
     * @param agreementValue - the agreement's value
     */			
    public void setCurrentTurnOpponentSelectedValue(double agreementValue) {
    	m_currentTurnOpponentSelectedValue=agreementValue;
    }

    /**
     * Sets the String of the selected offer for the following turn
     * @param agreementStr - the agreement as String
     *//*
    public void setNextTurnAgreementString(String agreementStr) {
        agent.setNextTurnAgreementString(agreementStr);        
    }
    
    *//**
     * Sets the String of the selected offer for the current turn
     * @param agreementStr - the agreement as String
     */
    public void setCurrentTurnAgreementString(String agreementStr) {
        m_currentTurnAgreementString=agreementStr;        
    }

    /**
     * Sets the side of the opponent (Side A or B)
     * @param agreementStr - the agreement as String
     *//*
    public void setNextTurnOpponentType(int type) {
        agent.setNextTurnOpponentType(type);        
    }

    *//**
     * Calculating the response to a given proposal.
     * This method eventually calls AbstractAutomatedAgent.calculateResponse()
     * @see AbstractAutomatedAgent#calculateResponse
     *
     *//*
    public void calculateResponse(int messageType, int[] currentAgreementIdx, String message) {
        agent.calculateResponse(messageType, currentAgreementIdx, message);
    }*/
    
    /**
     * @return string of a given agreement for the current agent
     */
    public String getMessageByIndices(int[] currentAgreementIdx) {
        return getBidFromIndices(currentAgreementIdx).toString();
    }
    
    /**
     * @return indices of a given agreement for the current agent
     */
    public int[] getMessageIndicesByMessage(String currentAgreementStr) {
    	
    	return allBidsIndices.get(currentAgreementStr);
    }

    /**
	 * @param currentAgreementIdx indices to bid values; one index for each issue. 
	 * AutomatedAgentType.NO_VALUE means that this issue was not discussed (it will be null in the bid; no completion is done in this function).
     * @return utility value of the given agreement for the current agent.
	 */
    public double getAgreementValue(int[] currentAgreementIdx) {
		if (currentAgreementIdx==null)
			throw new NullPointerException("currentAgreementIdx is null");
    	Bid bid=getBidFromIndices(currentAgreementIdx);
        double dAgreementValue = 0;
		try {
			dAgreementValue = agent.utilitySpace.getUtilityWithTimeEffect(bid,agent.currentTurn);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return dAgreementValue;
    }
    
    /**
     * 
     * @return the total number of seconds per each turn
     */
    public double getSecondPerTurn() {
        return agent.turnLength;
    }

    /**
     * 
     * @return the total number of issues for negotiation
     */
    public int getTotalIssues(AutomatedAgentType agentType) {
        return agentType.us.getDomain().getIssues().size();
    }

    /**
     * return the maximal value for the agent for issue i
     * @param agentType - the agent's type
     * @param issueNum - the issue number
     * @return the maximal value per that issue
     */
    public int getMaxValuePerIssue(AutomatedAgentType agentType, int issueNum) {
    	Bid bid;
    	double res=0;
		try {
			bid = agentType.us.getMaxUtilityBid();
			IssueDiscrete id=(IssueDiscrete) agentType.us.getDomain().getIssues().get(issueNum);
			res=id.getNumberOfValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return (int)res;
    }
    
  
    
    /**
	 * @param currentAgreementIdx indices to bid values; one index for each issue. 
	 * AutomatedAgentType.NO_VALUE means that this issue was not discussed (it will be null in the bid).
	 * @return a Genius Bid with the corresponding values.
	 */
	Bid getBidFromIndices(int[] currentAgreementIdx) {
		Bid lBid =null;
		if (currentAgreementIdx==null)
			throw new NullPointerException("currentAgreementIdx is null");
		if (agent==null)
			throw new NullPointerException("agent is null");
		if (agent.utilitySpace==null)
			throw new NullPointerException("agent.utilitySpace is null (agent="+agent+")");
		if (agent.utilitySpace.getDomain()==null)
			throw new NullPointerException("agent.utilitySpace.getDomain() is null (agent="+agent+")");
		
		ArrayList<Issue> lIssues = agent.utilitySpace.getDomain().getIssues();
		if (lIssues==null)
			throw new NullPointerException("agent.utilitySpace.getDomain(),getIssues() is null (agent="+agent+")");
		int fNumberOfIssues = lIssues.size();
				// build Hashmap and create the next bid.
		try {
			HashMap<Integer,Value> lValues = new HashMap<Integer,Value>(/*16,(float)0.75*/);
			for(int i=0;i<fNumberOfIssues;i++) {
				if (currentAgreementIdx[i]==AutomatedAgentType.NO_VALUE)
					continue;

				Issue lIssue = lIssues.get(i);
				if (lIssue==null)
					throw new NullPointerException("issue #"+i+" is null");
				double lOneStep;
				switch(lIssue.getType()) {
				case INTEGER:
					IssueInteger lIssueInteger =(IssueInteger)lIssue;
					lValues.put(lIssue.getNumber(),  
							new ValueInteger(lIssueInteger.getLowerBound()+currentAgreementIdx[i]));
					break;
				case REAL: 
					IssueReal lIssueReal =(IssueReal)lIssue;
					lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/(lIssueReal.getNumberOfDiscretizationSteps()-1);
					lValues.put(lIssue.getNumber(),new ValueReal(lIssueReal.getLowerBound()+lOneStep*currentAgreementIdx[i]));
					break;			
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
					lValues.put(lIssue.getNumber(), lIssueDiscrete.getValue(currentAgreementIdx[i]));
					break;
				default:
					throw new IllegalArgumentException("Unknown type of issue #"+i+": "+lIssue);
				}// switch
			}//for		
			
			lBid = new Bid(agent.utilitySpace.getDomain(), lValues);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return lBid;
	}
	
	 
    /**
	 * @param messageType
	 * @param bid
	 * @return
	 * @author
	 */
	private Action createBidAction(int messageType, Bid bid) {
		switch (messageType) {
			case AutomatedAgentMessages.OFFER:
				Offer offer=new Offer(agent.getAgentID(),bid);
				return offer;
			case AutomatedAgentMessages.QUERY:
				Query query=new Query(agent.getAgentID(),bid);
				return query;
			case AutomatedAgentMessages.PROMISE:
				Promise promise=new Promise(agent.getAgentID(),bid);
				return promise;
			case AutomatedAgentMessages.COUNTER_OFFER:
				CounterOffer counteroffer=new CounterOffer(agent.getAgentID(),bid);
				return counteroffer;
			default:
				return null;
		}

	}

	/**
	 * This isn't implemented in the Genius for now.
	 * It should cancel a sen't message if it wasn't passed to the opponent
	 * @param b
	 * @author
	 */
	public void setSendOfferFlag(boolean b) {
		// TODO Auto-generated method stub
		
	}
	
	public String getIssueValueStr(int nIssueNum, int nIssueNumIdx) {
		IssueDiscrete issue=(IssueDiscrete)agent.utilitySpace.getDomain().getIssues().get(nIssueNum);
		return issue.getValue(nIssueNumIdx).getValue();
	}

	/**
	 * find agreement that complete the accepted with more one issues add the
	 * most important issues (for me), with the best value (for me)
	 * 
	 * @param agentType
	 * @param PartialAgreement
	 * @return the PartialAgreement after the addition
	 */
	public int[] findPartialAgreementBestForMe(AutomatedAgentType agentType, int PartialAgreement[])
	{
		int numIssue = getTotalIssues(agentType);
		int nCurrentTurn = getCurrentTurn();
		double importencAgreementsIndices[] = new double[numIssue];

		// find my importance for each issue
		ArrayList<Issue> issues = agentType.us.getDomain().getIssues();
		for (int i = 0; i < numIssue; ++i)	{
			importencAgreementsIndices[i]=agentType.us.getWeight(issues.get(i).getNumber());
		}

		// look for the most importance issue that is not set
		// find the agreed issues - change theirs importance to 0
		for (int i = 0; i < numIssue; i++)
		{
			if (agentType.isIssueValueNotSet(PartialAgreement[i])
				|| agentType.isIssueValueNoAgreement(i, PartialAgreement[i]))
				continue;
			else
				// value set - issue agreed
				importencAgreementsIndices[i] = 0; // irrelevant => 0
		}
		// find the most importance issue
		boolean found = false;
		int maxImportenceIssueIndex = 0;
		for (int i = 0; i < numIssue && !found; i++)
		{
			if (importencAgreementsIndices[i] >= importencAgreementsIndices[maxImportenceIssueIndex])
				maxImportenceIssueIndex = i;
		}

		// build agreements for finding the best value of the important issue
		int numberOfImportenceIssueValues = getMaxValuePerIssue(myAgentType,maxImportenceIssueIndex);
		int bestValueForImportenceIssue = 0;
		double maxAgreementVal = AutomatedAgentType.VERY_SMALL_NUMBER;
		int agreement[] = new int[numIssue];
		for (int i = 0; i < numIssue; i++)
			agreement[i] = PartialAgreement[i];

		for (int i = 0; i < numberOfImportenceIssueValues; i++)
		{
			agreement[maxImportenceIssueIndex] = i;
			double agreementVal = getAgreementValue(agentType, agreement, nCurrentTurn);
			if (agreementVal > maxAgreementVal)
			{
				maxAgreementVal = agreementVal;
				bestValueForImportenceIssue = i;
			}
		}

		agreement[maxImportenceIssueIndex] = bestValueForImportenceIssue;
		return agreement;
	}
}

class AutomatedAgentType {
	public double nextOfferValueForAgent;
	public UtilitySpace us;
	private AgentTools agentTools;
	public int[] m_WorstAgreementIdx;
	public int[] m_BestAgreementIdx;
	public double m_dWorstAgreementValue;
	public double m_dBestAgreementValue;
	public FullUtility m_fullUtility;
	
	
	public static final int NO_VALUE = -1;
	public static final int VERY_SMALL_NUMBER = Integer.MIN_VALUE;
	public static final double VERY_HIGH_NUMBER = Integer.MAX_VALUE;
	public static int MAX_ISSUES = 0;
	
	public AutomatedAgentType(UtilitySpace us,AgentTools agentTools) {
		this.us=us;
		this.agentTools=agentTools;
		// we set a static object - its not nice but all Agent type would have the same domain so it won't matter
		MAX_ISSUES = us.getDomain().getIssues().size();
	}

	public int getIssuesNum() {
		return us.getDomain().getIssues().size();
	}


	/**
	 * @param currentAgreementIdx
	 * @param nCurrentTurn
	 * @return
	 * @author
	 */
	public double getAgreementValue(int[] currentAgreementIdx, int nCurrentTurn) {
		try {
    		Bid bid=agentTools.getBidFromIndices(currentAgreementIdx);
			return us.getUtilityWithTimeEffect(bid, nCurrentTurn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}


	public String getAgreementStr(int[] currentAgreementIdx) {
		return agentTools.getBidFromIndices(currentAgreementIdx).toString();
	}

	public double getWorstAgreementValue() {
		return m_dWorstAgreementValue;
	}

	
	public double getBestAgreementValue() {
		return m_dBestAgreementValue;
	}


	public int getOptOutValue() {
		return us.getOptOutValue(0).intValue();
	}
	public int getSQValue() {
		return us.getReservationValue().intValue();
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 * @author
	 */
	public boolean isIssueValueNoAgreement(int nIssueNum, int nIssueNumIdx) {
		String sIssueValue = agentTools.getIssueValueStr(nIssueNum, nIssueNumIdx);	
		if (sIssueValue.equals(AgentTools.NO_AGREEMENT))
			return true;
		else
			return false;
	}

	public boolean isIssueValueNotSet(int issueValue)
    {
    	if (issueValue==AutomatedAgentType.NO_VALUE)
    		return true;
    	else
    		return false;
    }
	 
	/**
	 * @param string
	 * @return
	 * @author
	 */
	public int[] getAgreementIndices(String string) {
		return agentTools.getMessageIndicesByMessage(string);
	}
	
}

class AutomatedAgentMessages {
	// constants for response of message
	public final static int MESSAGE_RECEIVED = 0;
	public final static int MESSAGE_REJECTED = 1;
    //type of messages
	public final static int REGISTER = 0;
	public final static int THREAT = 1;
	public final static int COMMENT = 2;
	public final static int OFFER = 3;
	public final static int PROMISE = 4;
	public final static int QUERY = 5;
	public final static int ACCEPT = 6;
	public final static int REJECT = 7;
	public final static int OPT_OUT = 8;
	public final static int COUNTER_OFFER = 9;
	
}

class AutomatedAgentsCore {
	public static final int LONG_TERM_TYPE_IDX = 0;
	public static final int SHORT_TERM_TYPE_IDX = 1;
	public static final int COMPROMISE_TYPE_IDX = 2;
	
	public static final int AGENT_TYPES_NUM = 3;
}

class AutomatedAgent {
    final public static String SIDE_A_NAME = "Agent A"; //England/Employer
    final public static String SIDE_B_NAME = "Agent B"; //Zimbabwe/Job Candidate
}


class FullUtility {
	public double dTimeEffect;
	public double dStatusQuoValue;
	public double dOptOutValue;
	public ArrayList lstUtilityDetails; // list of UtilityDetails
	public FullUtility()
	{
		dTimeEffect = 0;
		dStatusQuoValue = 0;
		dOptOutValue = 0;
		lstUtilityDetails = new ArrayList();
	}
}


/*****************************************************************
 * Class name: UtilityDetails
 * Goal: This class holds several issues in an array of issues.
 * All issues are under the same title which is saved in a string.
 ****************************************************************/
class UtilityDetails
{
	public String sTitle;
	public ArrayList lstUtilityIssues; // list of UtilityIssue
	
	//the constructor inits the title to an empty string, and the
	//utility issues array to an empty array
	public UtilityDetails()
	{
		sTitle = "";
		lstUtilityIssues = new ArrayList();
	}
}


/*****************************************************************
 * Class name: UtilityIssue
 * Goal: This class holds several utility values in an array of utility 
 * values. Each utility issue contains: a name, the side of the 
 * negotiation and an explanation - all saved in strings.
 ****************************************************************/

class UtilityIssue
{
	public String sAttributeName;
	public String sSide;
	public double dAttributeWeight;
	public String sExplanation;
	public ArrayList lstUtilityValues; // list of UtilityValue
	
	//the constructor inits the side, attribute name and the explanation
	//to empty strings, and the utility values array to an empty array.
	public UtilityIssue()
	{
		sSide="";
		sAttributeName = "";
		dAttributeWeight = 0;
		sExplanation = "";
		lstUtilityValues = new ArrayList();
	}
}


/*****************************************************************
 * Class name: UtilityValue
 * Goal: This class holds utility values: the value itself (saved in
 * a string), the utility and the effect of time (saved in a double).
 ****************************************************************/
class UtilityValue
{
	public String sValue;
	public double dUtility;
	public double dTimeEffect;
	
	//the constructor inits the value to an empty string, and the
	//utility and effect of time to 0.
	public UtilityValue()
	{
		sValue = "";
		dUtility = 0;
		dTimeEffect = 0;
	}
}

