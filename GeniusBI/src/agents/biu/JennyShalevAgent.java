package agents.biu;

/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 

// ID 310099809
public class JennyShalevAgent extends OldAgentAdapter{

	
	String opponentType = "";
	AutomatedAgentType agentType = null;
	
	int SupposedOpponentType_IDX = -1;   // Uninitialized
    int GuessedOpponentTypesHistory[];	 // TBD

    

    
    
    public JennyShalevAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public JennyShalevAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
        // The array holds the history of agents guesses of the opponent type
//        GuessedOpponentTypesHistory = new int[agentTools.getTurnsNumber()];        
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
         * @@EXAMPLE@@
         * For example: calculate the first offer the 
         * automated agent offers the opponent and send it
         */
    
		opponentType = sOpponentType;
		this.agentType = agentType;
		
		this.CalculateFirstAgreement();
    }
    
    /** 
     * Called when a message of type:
     * QUERY, COUNTER_OFFER, OFFER or PROMISE 
     * is received
     * Note that if you accept a message, the accepted message is saved in the 
     * appropriate structure, so no need to add logic for this.
     * @param nMessagentType - the message type
     * @param CurrentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     */
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {

    	
       	// no comments query/promise
    	if (nMessageType == AutomatedAgentMessages.QUERY
    			|| nMessageType == AutomatedAgentMessages.PROMISE)
    	{
            return;
    	}
    	
    	

        // Handle only the case an offer was recieved 
    	// Try to guess its type
    	this.GuessOpponentType();
    	
    	
        // decide whether to accept the message or reject it:
        double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
        double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;

        // Check the utility value of the opponent's offer
        dOppOfferValueForAgent = agentTools.getAgreementValue(CurrentAgreementIdx); 
            
        // 1. check whether previous accepted agreement is better - if so, reject
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
        {
            // reject offer
            agentTools.rejectMessage(sOriginalMessage);
            // Comment the opponent
            agentTools.sendComment("I don't believe that you are just in your intentions");
            return;
        }
        
        // 2. check the value of the automated agent in the next turn
        agentTools.calculateNextTurnOffer();
        dAutomatedAgentNextOfferValueForAgent = agentTools.getNextTurnOfferValue();

        if (dOppOfferValueForAgent >= dAutomatedAgentNextOfferValueForAgent)
        {
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);

        }
        else
        {
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
            
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
	 * @param nMessagentType - the type of massage the oppnent aggreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessagentType, int CurrentAgreementIdx[], String sOriginalMessage) {
        /* @@ Received a message: opponent accepted the offer/promise/query/counter offer.
        * You can add logic if needed to update your agent
        * For example, if the message was a promise, you can now try and offer it as
        * a formal offer...
        */
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessagentType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessagentType, int CurrentAgreementIdx[], String sOriginalMessage) {
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
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
        // calculate Automated Agent offer
    	// My comment - MaxIssueValues is needed as an iteration stop condition

              
        
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        
        // Get the supposed opponent type
        AutomatedAgentType agentOpponent= null;
        agentOpponent = agentTools.getCurrentTurnSideAgentType(sOpponentType, this.SupposedOpponentType_IDX);
      
        // Now, we go over all the possible agreements,
        // First, we calculate the value of each agreement for the automated agent;
        // Then, we calculate the value of each such agreement for the different possible opponent types
        // We only save the last value calculated.
        // You can change this logic, of course...        

        //In this example, we only calculate for the long term orientation

        int OpponentIdx[] = new int[totalIssuesNum];
        double dOpponentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        double dAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // For now find the best agreement for the opponent among X best aggrements for me
        
        double dMyBestAgreementValue = agentTools.getBestAgreementValue(agentType);
        
        double dMyCompromiseAgreementValue = dMyBestAgreementValue * 0.85;
        
        double dOpponentBestAgreementValue = agentTools.getBestAgreementValue(agentOpponent);
        
        
        // The opponent should also compromise (even more)
        double dOpponentCompromiseAgreementValue = dOpponentBestAgreementValue * 0.7;

        double dNearestAgreementValue = dMyCompromiseAgreementValue; 
        
        // No easy Compromise exists 
        if (dMyCompromiseAgreementValue < dOpponentCompromiseAgreementValue)
        	// offer a high offer
        	dNearestAgreementValue  = dMyBestAgreementValue * 0.9;
        	

        // No easy Compromise exists and those are the last turns - compromise more
        if ((dMyCompromiseAgreementValue < dOpponentCompromiseAgreementValue) && (agentTools.getCurrentTurn() > (agentTools.getTurnsNumber()-3)))
        	// offer a lower offer
        	dNearestAgreementValue  = dMyBestAgreementValue * 0.8;
        
        
        //help var
        double dSelectedAgreementValue = dMyBestAgreementValue;
        
       
        
// My comment - go over all possible aggrements and evaluate it for all the 
        // relevant agents - me, all the possible opponents
        // You can save interesting aggreement details for further usage 
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
        	
            dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
            if ((dAutomatedAgentAgreementValue >= dNearestAgreementValue) && (dAutomatedAgentAgreementValue < dSelectedAgreementValue))
            {
            	dSelectedAgreementValue = dAutomatedAgentAgreementValue;
            	// Save agreement
                for (int j = 0; j < totalIssuesNum; ++j) {
                    OpponentIdx[j] = CurrentAgreementIdx[j];
                  }

            }

            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        
        // If a new aggrement has a better value for the opponent
        if (dSelectedAgreementValue > agentTools.getCurrentTurnAutomatedAgentValue())
        {
            // you can save the values for later reference ($1)
            agentTools.setCurrentTurnAutomatedAgentValue(dSelectedAgreementValue);
            agentTools.setCurrentTurnOpponentSelectedValue(agentOpponent.getAgreementValue(OpponentIdx, nCurrentTurn));
            agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(OpponentIdx));
        }

        // Now, the agent's core holds the new selected agreement
        
 //------------------------------------------------------------------------------------       
        
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
        }
        
        // if decided to send offer - then send the offer
        //Get the offer as string and format it as an offer
        
        else
        {
        	String sOffer = agentTools.getSelectedOffer();
        	agentTools.sendOffer(sOffer);
        }
        /********************************
         * End example code
         ********************************/
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

        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        
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
    
    
    private void CalculateFirstAgreement(){
    	// There is no guess about the opponent type
    	// The naive way is to offer the best aggreement
    	// But it may help the oppenent to guess your type
    	// So offer a partial aggreement with one most important issue
    	
    	// My implementation
    	
    	
    	// If there is a guess for an opponent type - use the general alg
    	// If there is no guess (our offer is first), offer a partial aggreement
    	// optimizing one of the important issues
    	
    	int numIssue = agentTools.getTotalIssues(this.agentType);
    	double importencAgreementsIndices[] = new double[numIssue];
    	// find my importance for each issue
		int nIssueNum = 0;
		for (int i = 0; i < numIssue; ++i)
		{
			int issueID=agentType.us.getDomain().getIssues().get(i).getNumber();
			importencAgreementsIndices[nIssueNum] = agentType.us.getWeight(issueID);		
			nIssueNum++;
		}
		
//		find the most importance issue 
		int maxImportenceIssueIndex = 0;
		for (int i=0 ; i < nIssueNum; i++)
		{ 
			if (importencAgreementsIndices[i] >= importencAgreementsIndices[maxImportenceIssueIndex])
				maxImportenceIssueIndex = i;
		}
		
		//build agreements for finding the best value of the important issue
		int agreement[] = new int[nIssueNum];
		for (int i=0 ; i< nIssueNum; i++)
			agreement[i] = AutomatedAgentType.NO_VALUE;
		
		// This line doesn't work TBD - Debug this
		//agreement[maxImportenceIssueIndex] = agentTools.getMaxValuePerIssue(this.agentType, maxImportenceIssueIndex);

		agentTools.setCurrentTurnAgreementString(this.agentType.getAgreementStr(agreement));
        String sOffer = agentTools.getSelectedOffer();
		agentTools.sendOffer(sOffer);
	}
    
    
    private void GuessOpponentType(){
    	// If an offer recieved, check whose offer it may be
    	
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int nCurrentTurn  = agentTools.getCurrentTurn();
    	
        AutomatedAgentType agentOppCompromise = null;
        AutomatedAgentType agentOppLong = null;
        AutomatedAgentType agentOppShort = null;

        
        // May be its better to check for next turn too
        agentOppCompromise = agentTools.getCurrentTurnSideAgentType(opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOppLong = agentTools.getCurrentTurnSideAgentType(opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOppShort = agentTools.getCurrentTurnSideAgentType(opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
        


       int CurrentAgreementIdx[] = new int[totalIssuesNum];        
       double dOppCompromiseValue = agentTools.getAgreementValue(agentOppCompromise, CurrentAgreementIdx, nCurrentTurn);
       double dOppLongValue = agentTools.getAgreementValue(agentOppLong, CurrentAgreementIdx, nCurrentTurn);       
       double dOppShortValue = agentTools.getAgreementValue(agentOppShort, CurrentAgreementIdx, nCurrentTurn);

       int idx = -1;
       if ((dOppCompromiseValue >= dOppLongValue) && (dOppCompromiseValue >= dOppShortValue))
       		//this.GuessedOpponentTypesHistory[nCurrentTurn] = AutomatedAgentsCore.COMPROMISE_TYPE_IDX;
    	   idx = AutomatedAgentsCore.COMPROMISE_TYPE_IDX;

       else if (dOppLongValue > dOppShortValue)
    	   idx = AutomatedAgentsCore.LONG_TERM_TYPE_IDX;
       
       else idx = AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;   		
       	

       if (this.SupposedOpponentType_IDX == -1)
    	   this.SupposedOpponentType_IDX = idx;
       this.SupposedOpponentType_IDX = idx;
 
    
    }
}
