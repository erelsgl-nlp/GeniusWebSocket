package agents.biu;

/**
 * @author Ronen Louvton
 * This class should hold all logic for automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
import java.util.*;

public class RonenLouvtonAgent extends OldAgentAdapter{
	
    // Used for accessing the best agreement value for agent
    double dBestAgreementValue = 0;
    // Used for opponent modeling
    // 0 - Long Term, 1- Short Term, 2 - Compromise
    int opponentType = 0;
    // The reduction factor determines the amount of difference 
    // in the agent offers from turn to turn
    // The factor is set according to the opponent type
    int reductionFactor = 0;
    // Used to count the threats in a game
    int threatsCounter = 0;
    
    public RonenLouvtonAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public RonenLouvtonAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the negotiation starts.
	 * 1. Set the opponent type randomly
	 * 2. Set the reduction factor according to the opponent type 
	 * 3. calculate the very first offer you'll
     * 		offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) 
	{    
		// Choose randomly the opponent type
		Random rand = new Random();
		int opponentType = rand.nextInt(3);
		
		// Set the reduction factor according to the opponent type
		if(opponentType == AutomatedAgentsCore.LONG_TERM_TYPE_IDX)
			reductionFactor = 1;
		else if(opponentType == AutomatedAgentsCore.SHORT_TERM_TYPE_IDX) 
			reductionFactor = 2;
		else
			reductionFactor = 3; // AutomatedAgentsCore.COMPROMISE_TYPE_IDX
		
		threatsCounter = 0;

        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
    }
    
    /** 
     * Called when a message of type:
     * QUERY, COUNTER_OFFER, OFFER or PROMISE 
     * is received
     * Note that if you accept a message, the accepted message is saved in the 
     * appropriate structure, so no need to add logic for this.
     * @param nMessageType - the message type
     * @param CurrentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     */
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {

    	// Init values
        double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
        double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;

        // Check the utility value of the opponent's offer
        dOppOfferValueForAgent = agentTools.getAgreementValue(CurrentAgreementIdx); 

        // Check whether previous accepted agreement is better - if so, reject
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
        {
            // reject offer
            agentTools.rejectMessage(sOriginalMessage);
            return;
        }
        
        // Lower Limit
        // Check if the offer is less than 0.6 from the best offer - reject it
        if(dOppOfferValueForAgent < (0.6*dBestAgreementValue))
        {
        	// reject offer
            agentTools.rejectMessage(sOriginalMessage);
            return;
        }
        
        // Check the value of the automated agent in the next turn
        agentTools.calculateNextTurnOffer();
        dAutomatedAgentNextOfferValueForAgent = agentTools.getNextTurnOfferValue();

        // Check if the opponent offer is better than the agent
        // next offer -> accept the opponent offer
        // Else - reject it.
        if (dOppOfferValueForAgent >= dAutomatedAgentNextOfferValueForAgent)
        {
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);

        }
        else
        {
        	// reject offer
            agentTools.rejectMessage(sOriginalMessage);
            return;
        }
    }
        
    /***********************************************
     * @@ Logic for receiving messages
     * Below are messages the opponent sends to the automated agent
     * You can add logic if needed to update your agent per message type
     ***********************************************/
    
    /**
	 * called whenever we get a comment from the opponent
     * You can add logic to update your agent
     * @param sComment -the received comment
	 */
	public void commentReceived(String sComment) {
        /* @@ Received a comment from the opponent
         * You can add logic if needed to update your agent 
         */
		
		// Do not handle comments since they are not changing
		// the opponent strategy.
    }

	/**
	 * called whenever we get a threat from the opponent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {
        // Handle all threats the same and increase 
		// the reduction factor, so the next offer 
		// will be better for the opponent and we can "save"
		// the negotiation
		if(++threatsCounter <= 3)
			reductionFactor++;
    }
	
	/**
	 * called whenever the opponent agreed to one of your massages (promise, query, offer or counter offer).
     * NOTE: if an OFFER is accepted, it is saved in the appropriate structure. No need to add logic for this.
	 * @param nMessageType - the type of massage the oppnent aggreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
        // Do not handle agreed message.
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
       // Do not handle rejection message
    }
    
    /***********************************************
     * @@ End of methods for receiving message
     ***********************************************/
 
    
    /**
     * called to decide which offer to propose the opponent at a given turn
     * This method is always called when beginning a new turn
     * You can also call it during the turn if needed
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
        //@@ Add any logic to calculate offer (or several offers)
        // and decide which to send to the opponent in a given turn
        
        // calculate Automated Agent offer
        double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Count the total issues and possible agreements
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        // Init values
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        
        // the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                        

        // Init the vectors and values
        int OpponentIdx[] = new int[totalIssuesNum];
        double dOpponentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        double dAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Set lower and upper bounds for your offer according to my turn number
        // and the reduction factor
        double dOfferLower = ((100-nCurrentTurn*reductionFactor)*dBestAgreementValue/100);
        double dOfferUpper = ((100-nCurrentTurn*reductionFactor)*dBestAgreementValue/100) + (0.02*dBestAgreementValue);
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
                    
            // Set the agreement according to the agent type
            switch(opponentType)
            {
            	case AutomatedAgentsCore.LONG_TERM_TYPE_IDX:
	        		dOpponentAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, CurrentAgreementIdx, nCurrentTurn);
	                
	                break;
            	case AutomatedAgentsCore.SHORT_TERM_TYPE_IDX:
	        		dOpponentAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, CurrentAgreementIdx, nCurrentTurn);
	                
	                break;
            	case AutomatedAgentsCore.COMPROMISE_TYPE_IDX:
	        		dOpponentAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, CurrentAgreementIdx, nCurrentTurn);
	                
	                break;
            }
            // save the indices of that offer
            for (int j = 0; j < totalIssuesNum; ++j) {
                OpponentIdx[j] = CurrentAgreementIdx[j];
            }
            
            // Check that I have an offer within the boundaries
            if((dAutomatedAgentAgreementValue >= dOfferLower)&&
            		(dAutomatedAgentAgreementValue <= dOfferUpper))
            {
            	// Stop looking for other offers and send this one
            	break;
            }
            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        //select which offer to propose 
        //In this example, selecting the last offer that was calculated
        if (dOpponentAgreementValue > agentTools.getCurrentTurnAutomatedAgentValue())
        {
            // you can save the values for later reference ($1)
            agentTools.setCurrentTurnAutomatedAgentValue(dOpponentAgreementValue);
            // Switch between the types
            if(opponentType == AutomatedAgentsCore.LONG_TERM_TYPE_IDX)
            	agentTools.setCurrentTurnOpponentSelectedValue(agentOpponentLongTerm.getAgreementValue(OpponentIdx, nCurrentTurn));
            else if(opponentType == AutomatedAgentsCore.SHORT_TERM_TYPE_IDX)
            	agentTools.setCurrentTurnOpponentSelectedValue(agentOpponentShortTerm.getAgreementValue(OpponentIdx, nCurrentTurn));
            else // Compromise
            	agentTools.setCurrentTurnOpponentSelectedValue(agentOpponentCompromise.getAgreementValue(OpponentIdx, nCurrentTurn));
            
            agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(OpponentIdx));
        }

        // Now, the agent's core holds the new selected agreement
        
        // check the value of the offer (the one saved before, see $1...)
        double dNextAgreementValue = agentTools.getSelectedOfferValue();

        // get the value of previously accepted agreement
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        // Now, check whether the offer the agent intends to propose in the next turn is better
        // for it than previously accepted agreement
        
        // if the value of the offer is lower than already accepted offer, don't send it...
        if (dAcceptedAgreementValue >= dNextAgreementValue)
        {
            // default behavior is to send offer
            // however... now we don't want to send the offer
            // previously accepted offer has better score
            
            // so - don't send the offer
        	return;
        }
        
        // if decided to send offer - then send the offer
        //Get the offer as string and format it as an offer
        String sOffer = agentTools.getSelectedOffer();
        agentTools.sendOffer(sOffer);
    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
        //Calculate agreements values for a given turn

        // initialization - DO NOT CHANGE
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];

        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        //end initialization

        // Currently, the method calculates the best agreement, worst agreement
        // and the utility value per agreement            
        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        
        //To obtain infromation from the utility you can use getters from the AgentType class

        //Get the value of the Status Quo and Opting-Out values as time increases
        double dAgreementTimeEffect = agentTools.getAgreementTimeEffect(agentType); 
        double dStatusQuoValue = agentTools.getSQValue(agentType);
        double dOptOutValue = agentTools.getOptOutValue(agentType);
        
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremented
            dAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
            
            // check for best agreement
            if (dAgreementValue > agentTools.getBestAgreementValue(agentType))
            {
                agentTools.setBestAgreementValue(agentType, dAgreementValue);

                // save agreement
                agentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);
            }                       
                        
            // check for worst agreement
            if (dAgreementValue < agentType.getWorstAgreementValue())
            {
                agentTools.setWorstAgreementValue(agentType, dAgreementValue);
                
                // save agreement
                agentTools.setWorstAgreementIndices(agentType, CurrentAgreementIdx);
            }                       

            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        // Set the class variable to hold the best agreement
        dBestAgreementValue = agentTools.getBestAgreementValue(agentType);            
    }
}
