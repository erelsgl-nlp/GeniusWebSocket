package agents.biu;

/**
 * @author Miky Schreiber
 */ 
public class MikySchreiberAgent extends OldAgentAdapter{
    AutomatedAgentType agentType = null;    
    double bestOfferAgentSeen;
    double bestOfferAgentSent;
    
    public MikySchreiberAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public MikySchreiberAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
        /* Negotiation is about to start
         * You can add logic if needed to update your agent
 		*/
		
        // save the agentType for later use		
		this.agentType = agentType;
		// set the bestOfferAgentSeen to zero (no offer received yet)
		bestOfferAgentSeen = 0;
		// set the bestOfferAgentSent to zero (no offer sent yet)
		bestOfferAgentSent = 0;
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
        // Check the utility value of the opponent's offer
        double suggestedOfferValue = agentTools.getAgreementValue(CurrentAgreementIdx);                      
        
    	switch (nMessageType)
    	{
    	case AutomatedAgentMessages.QUERY:    		            
    		// answer positively to the query only if it 10% more than the best offer it seen
            if (suggestedOfferValue >= 1.1 * bestOfferAgentSeen)
            {
            	agentTools.acceptMessage(sOriginalMessage);
            	agentTools.setSendOfferFlag(false);
            	agentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);
            	agentTools.setBestAgreementValue(agentType, suggestedOfferValue);            	
            }
            
            // if not - reject the query
            agentTools.rejectMessage(sOriginalMessage);            
    		break;
    		
    	case AutomatedAgentMessages.PROMISE:
    		// don't get excited of promises
    		agentTools.rejectMessage(sOriginalMessage);
    		break;
    		
    	default:
    		// OFFER or COUNTER_OFFER
    		// take it if it's 10% better than the previous ones
    		if (suggestedOfferValue >= 1.1 * bestOfferAgentSeen)
            {
            	agentTools.acceptMessage(sOriginalMessage);
            	agentTools.setSendOfferFlag(false);
            	agentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);
            	agentTools.setBestAgreementValue(agentType, suggestedOfferValue);            	
            }
            
            // if not - reject the query
            agentTools.rejectMessage(sOriginalMessage);            
    		break;
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
    }

	/**
	 * called whenever we get a threat from the opponent
     * You can add logic to update your agent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {		
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
        switch (nMessageType)
        {
        case AutomatedAgentMessages.PROMISE:
        	// let's try to make it an offer
        	agentTools.sendOffer(sOriginalMessage);
        	break;
        case AutomatedAgentMessages.QUERY:
        	// let's try to make it an offer
        	agentTools.sendOffer(sOriginalMessage);
        	break;
        default: // offer / counter-offer: update the agent
        	// save the value of the agreement for later use
        	bestOfferAgentSent = agentTools.getAgreementValue(CurrentAgreementIdx);
        }
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
        // try to decrease one of the parameters
		boolean decreased = false;
		int i = 0;
		while (!decreased && i<CurrentAgreementIdx.length)
		{
			if (CurrentAgreementIdx[i] != 0)
			{
				CurrentAgreementIdx[i] = CurrentAgreementIdx[i] - 1;
				decreased = true;
			}
			else
			{
				i++;
			}
		}
		
		// send it
		agentTools.sendQuery(CurrentAgreementIdx);
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
        
    	// get the accepted agreement so far    	
    	int acceptedAgreementeIdx[] = agentTools.getAcceptedAgreementIdx();    	    	
    	
    	// try ten times to make a better agreement
    	// the new agreement have to be better than 10% but less than 50% (compared to the best agreement made at this point)
    	int[] agreement = acceptedAgreementeIdx;
    	double agreementValue = 0;
    	boolean offered = false;
    	
    	for (int i = 0; i < 10; i++) {
			agreement = calcBetterAgreement(agreement);
			
			agreementValue = agentTools.getAgreementValue(agreement);
			if (agreementValue >= 1.1 * bestOfferAgentSent)
			{
				if (agreementValue <= 1.5 * bestOfferAgentSent)
				{
					// offer it
					String offer = agentTools.getMessageByIndices(agreement);
					agentTools.sendOffer(offer);
					offered = true;
				}
			}
		}
    	
    	if (!offered) // offer the agreed offer
    	{
    		String offer = agentTools.getMessageByIndices(acceptedAgreementeIdx);
			agentTools.sendOffer(offer);
    	}
		    
    }
    
    public int[] calcBetterAgreement (int[] agreement)
    {
    	// try to increase one parameter
    	boolean increased = false;
		int i = 0;
		while (!increased && i<agreement.length)
		{
			if (agreement[i]+1 != agentTools.getMaxValuePerIssue(agentType, i))
			{
				agreement[i] = agreement[i] + 1;
				increased = true;
			}
			else
			{
				i++;
			}
		}
		return agreement;
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

        // @@EXAMPLE@@
        // Currently, the method calculates the best agreement, worst agreement
        // and the utility value per agreement
        /********************************
         * Start example code
         ********************************/             
        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        
        //To obtain infromation from the utility you can use getters from the AgentType class
        //@@EXample@@
        //Get the value of the Status Quo and Opting-Out values as time increases
        double dAgreementTimeEffect = agentTools.getAgreementTimeEffect(agentType); 
        double dStatusQuoValue = agentTools.getSQValue(agentType);
        double dOptOutValue = agentTools.getOptOutValue(agentType);
        
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremeneted
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
        /********************************
         * End example code
         ********************************/             
    }
}
