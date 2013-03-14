package agents.biu;

/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class InbalHalutzAgent extends OldAgentAdapter{
    AutomatedAgentType agentType = null;
    String opp_type = null;
    
    double comp_percent = 1.0/3.0;
    double long_percent = 1.0/3.0;
    double short_percent = 1.0/3.0;
    
public InbalHalutzAgent(){
	super();
}

    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public InbalHalutzAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType1, String sOpponentType) {
        /* Negotiation is about to start
         * You can add logic if needed to update your agent
         * @@EXAMPLE@@
         * For example: calculate the first offer the 
         * automated agent offers the opponent and send it
         */
        
        /********************************
         * Start example code
         ********************************/
        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType1, sOpponentType, 1);
        /********************************
         * End example code
         ********************************/
        agentType = agentType1;
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
        //Calculating the response
        //You can decide on your actions for that turn
        //You can decide on different logic based on the message type
        //In case you accept an offer, you might decide NOT to
        //send an offer you calculated before and just waited for
        //it to be sent. To do so, use the "send flag" as in
        //the example below
        //@@EXAMPLE@@
        //For example: 
        //1 - if the newly offer has lower utility values than already
        //accepted agreement, reject it;
        //2 - if the value of the offerd agreement is bigger than some 
    	//value calculated by the calculating methode than accept the offer
    	//3 - else, reject the offer

    	// update the percentage
    	if (opp_type != null)
    		update_pecent_according_to_offer (opp_type, CurrentAgreementIdx, agentTools.getCurrentTurn());
    	
    	
        // decide whether to accept the message or reject it:
        double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
        //double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;

        // Check the utility value of the opponent's offer
        dOppOfferValueForAgent = agentTools.getAgreementValue(CurrentAgreementIdx); 
            
        // 1. check whether previous accepted agreement is better - if so, reject
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
        {
        	// reject offer
            agentTools.rejectMessage(sOriginalMessage);
            return;
        }
        
        // 2. check the value for the automated agent
        if (dOppOfferValueForAgent >= calculate_minimum_value_to_accept(agentType,agentTools.getTurnsNumber()))
        {
			// accept offer
            agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
        }
        else	//reject
        {
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
    }

	/**
	 * called whenever we get a threat from the opponent
     * You can add logic to update your agent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {
        /* @@ Received a threat from the opponent
         * You can add logic if needed to update your agent 
         */
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
        /* @@ Received a message: opponent accepted the offer/promise/query/counter offer.
        * You can add logic if needed to update your agent
        * For example, if the message was a promise, you can now try and offer it as
        * a formal offer...
        */
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
        /* @@ Received a message: opponent rejected the offer/promise/query/counter offer.
         * You can add logic if needed to update your agent
         */
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
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType1, String sOpponentType, int nCurrentTurn) {
        //@@ Add any logic to calculate offer (or several offers)
        // and decide which to send to the opponent in a given turn
        
        /** @@EXAMPLE@@
         * In the following example, ONE offer is chosen to be
         * send to the opponent based on the following logic:
         * It will be sent only if it has a value higher than
         * an offer already accepted.
         * 
         * You can see in the example how to:
         * a) obtain the different possible types of opponent
         * b) get the total number of issues in the negotiation
         * c) get the total number of agreements in the negotiation
         * d) get the maximal value of a certain issue for each agent
         * e) go over all possible agreements and evaluate them
         * f) compare the agreement to previously accepted agreement
         * g) save one offer for later references
         **/
    	
    	opp_type = sOpponentType;
    	
        // calculate Automated Agent offer
        double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        int totalIssuesNum = agentTools.getTotalIssues(agentType1);
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType1);

        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        // variables to save the best agreement for the opp that has a value more than the minimum_value_to_offer calculated
        double MaxAgreementValueForOpp = 0;
        int MaxAgreementForOpp [] = new int [totalIssuesNum];
        double MinAgreementValue =  calculate_minimum_value_to_offer (agentType1, nCurrentTurn);
        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
        	MaxAgreementForOpp[i] = 0;	//initialize
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType1, i);
        }
        
        // the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
      
        // Now, we go over all the possible agreements,
        // First, we calculate the value of each agreement for the automated agent;
        // Then, we calculate the value of each such agreement for the different possible opponent types
        // We choose the best agreement for the opp that is bigger than the minimum_value_to_offer calculated        

        
        double AgreementValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
        double AvrgAgreementValueForOpp = 0;
        
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
        	AgreementValueForAgent = agentTools.getAgreementValue(agentType1, CurrentAgreementIdx, nCurrentTurn);
        	if (AgreementValueForAgent > MinAgreementValue)	// bigger than the minimum value than the agent is willing to accept on this turn
        	{
        		// get the average agreement value for all the 3 types of opponent
        		AvrgAgreementValueForOpp = (agentOpponentCompromise.getAgreementValue(CurrentAgreementIdx, nCurrentTurn))*comp_percent + 
        								(agentOpponentLongTerm.getAgreementValue(CurrentAgreementIdx, nCurrentTurn))*long_percent +
        								(agentOpponentShortTerm.getAgreementValue(CurrentAgreementIdx, nCurrentTurn))*short_percent;
        		// found a better agreement for the opponent. Save it.
        		if (MaxAgreementValueForOpp < AvrgAgreementValueForOpp)
        		{
        			MaxAgreementValueForOpp = AvrgAgreementValueForOpp;	// copy the better value
        			for (int j=0; j<totalIssuesNum; j++)	// copy the better agreement
        				MaxAgreementForOpp[j] = CurrentAgreementIdx[j];
        		}
        	}
        	

            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        String sOffer=null;
        // check if an agreement was found and send it as an offer
        if (MaxAgreementValueForOpp != 0)
        {
        	//Get the offer as string and format it as an offer
        	sOffer = agentTools.getMessageByIndices(MaxAgreementForOpp);
        }
        else	// no agreement was found
        {
        	sOffer = agentTools.getBestAgreementStr(agentType1);
        }
        
        agentTools.sendOffer(sOffer);
        
        
    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType1, int nCurrentTurn) {
        //Calculate agreements values for a given turn

        // initialization - DO NOT CHANGE
        int nIssuesNum = agentTools.getTotalIssues(agentType1);
        
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];

        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType1);

        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType1, i);
        }
        //end initialization

        // @@EXAMPLE@@
        // Currently, the method calculates the best agreement, worst agreement
        // and the utility value per agreement
                    
        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType1);
        agentTools.initializeWorstAgreement(agentType1);
        
        //To obtain infromation from the utility you can use getters from the AgentType class
        //@@EXample@@
        //Get the value of the Status Quo and Opting-Out values as time increases
        double dAgreementTimeEffect = agentTools.getAgreementTimeEffect(agentType1); 
        double dStatusQuoValue = agentTools.getSQValue(agentType1);
        double dOptOutValue = agentTools.getOptOutValue(agentType1);
        
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremeneted
            dAgreementValue = agentTools.getAgreementValue(agentType1, CurrentAgreementIdx, nCurrentTurn);
            
            // check for best agreement
            if (dAgreementValue > agentTools.getBestAgreementValue(agentType1))
            {
                agentTools.setBestAgreementValue(agentType1, dAgreementValue);

                // save agreement
                agentTools.setBestAgreementIndices(agentType1, CurrentAgreementIdx);
            }                       
                        
            // check for worst agreement
            if (dAgreementValue < agentType1.getWorstAgreementValue())
            {
                agentTools.setWorstAgreementValue(agentType1, dAgreementValue);
                
                // save agreement
                agentTools.setWorstAgreementIndices(agentType1, CurrentAgreementIdx);
            }                       

            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
                    
    }

    /**
     *  calculate the threshold of value of agreement to send 
     **/
    public double calculate_minimum_value_to_offer (AutomatedAgentType agentType1, int nCurrentTurn)
    {
    	// calculate the maximum value between opt-out and status-quo
    	double max_opt_SQ = Math.max(agentTools.getSQValue(agentType1), 
    								agentTools.getOptOutValue(agentType1));
    	// calculate the value of the best agreement
    	double best_offer_value = agentTools.getBestAgreementValue(agentType1);
    	// caculate the diff between the best agreement and the maximum between opt-put and status-quo
    	double diff = best_offer_value -max_opt_SQ;
    	if (diff < 0)
    		diff = max_opt_SQ;
    	//get the total number of turns
    	int max_turns= agentTools.getTurnsNumber();
    	
    	// calculate the return value
    	double value = max_opt_SQ + diff*((max_turns - diff*nCurrentTurn)/max_turns);
    	return value;
    }
    
    /**
     *  calculate the threshold of value of agreement to accept 
     **/
    public double calculate_minimum_value_to_accept (AutomatedAgentType agentType1, int nCurrentTurn)
    {
    	// calculate the maximum value between opt-out and status-quo
    	double max_opt_SQ = Math.max(agentTools.getSQValue(agentType1), 
    								agentTools.getOptOutValue(agentType1));
    	// calculate the value of the best agreement
    	double best_offer_value = agentTools.getBestAgreementValue(agentType1);
    	// caculate the diff between the best agreement and the maximum between opt-put and status-quo
    	double diff = best_offer_value -max_opt_SQ;
    	if (diff < 0)
    		diff = max_opt_SQ;
    	//get the total number of turns
    	int max_turns= agentTools.getTurnsNumber();
    	
    	// calculate the return value
    	double value = max_opt_SQ + 0.5*diff*((max_turns - diff*nCurrentTurn)/max_turns);
    	return value;
    }

    
    public void update_pecent_according_to_offer (String sOpponentType, int CurrentAgreementIdx[], int nCurrentTurn)
    {
    	// the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
      
        double comp_value = agentOpponentCompromise.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
        double long_value = agentOpponentLongTerm.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
		double short_value = agentOpponentShortTerm.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
		
		double SECOND_PLACE_PERCENT = 0.9;
		double THIRD_PLACE_PERCENT = 0.8;
		if ((comp_value < long_value) && (comp_value < short_value)) //comp_value is the minimal
		{
			comp_percent = comp_percent*THIRD_PLACE_PERCENT;
			if (long_value < short_value)	// short_value is the maximum
			{
				long_percent = long_percent*SECOND_PLACE_PERCENT;
				short_percent = 1.0 - (long_percent + comp_percent);
			}
			else	// long_percent is the maximum
			{
				short_percent = short_percent*SECOND_PLACE_PERCENT;
				long_percent = 1.0 - (short_percent + comp_percent);
			}
		}
		
		else if ((long_value < comp_value) && (long_value < short_value))	//long_value is the minimum
		{
			long_percent = long_percent*THIRD_PLACE_PERCENT;
			if (comp_value < short_value)	// short_value is the maximum
			{
				comp_percent = comp_percent*SECOND_PLACE_PERCENT;
				short_percent = 1.0 - (long_percent + comp_percent);
			}
			else	// comp_percent is the maximum
			{
				short_percent = short_percent*SECOND_PLACE_PERCENT;
				comp_percent = 1.0 - (short_percent + long_percent);
			}
		}
		
		else		// short_value is the minimum
		{
			short_percent = short_percent*THIRD_PLACE_PERCENT;
			if (comp_value < long_value)	// long_value is the maximum
			{
				comp_percent = comp_percent*SECOND_PLACE_PERCENT;
				long_percent = 1.0 - (short_percent + comp_percent);
			}
			else	// comp_percent is the maximum
			{
				long_percent = short_percent*SECOND_PLACE_PERCENT;
				comp_percent = 1.0 - (short_percent + long_percent);
			}
		}
    }

}
