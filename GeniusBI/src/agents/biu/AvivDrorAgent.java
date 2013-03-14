package agents.biu;

/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class AvivDrorAgent extends OldAgentAdapter{

    
    public AvivDrorAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public AvivDrorAgent(AgentTools agentTools) {
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
         * @@EXAMPLE@@
         * For example: calculate the first offer the 
         * automated agent offers the opponent and send it
         */
        
        /********************************
         * Start example code
         ********************************/
        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
        
        // initialize rejected
        int numberOfIssues= agentTools.getTotalIssues(agentType);
        RejectedValues = new int[numberOfIssues];
        for (int i =0; i<numberOfIssues; i++)
		{
			RejectedValues[i] = -1;
		}
		
		// initialize compromise
        CompromiseValues = new int[numberOfIssues];
        for (int i =0; i<numberOfIssues; i++)
		{
			CompromiseValues[i] = -1;
		}
        // added by Yinon Oshrat
        maxValues = new int[numberOfIssues];
        for (int i =0; i<numberOfIssues; i++)
		{
        	maxValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
		}
        // calculate maximal range
        int maxRange = 0;
        for (int i=0;i<numberOfIssues;i++)
        {
        	if (maxRange < agentTools.getMaxValuePerIssue(agentType, i))
        	{
        		maxRange = agentTools.getMaxValuePerIssue(agentType, i);
        	}
        }
        // from 0-based to 1-based
        maxRange++;
        
        // init Priorities array
        IssuesByPriority = new int[numberOfIssues][maxRange];

        // calculate issues by priority
        for (int nIssue = 0; nIssue < numberOfIssues; nIssue++)
        {	
        	// define issue score array and fill it
        	double[] IssueScoreArray = new double[maxRange];
        	int maxValue = maxValues[nIssue];// agentTools.getMaxValuePerIssue(agentType, nIssue);
        	//maxValue--;
        	for (int nValueIndex=0; nValueIndex<maxRange; nValueIndex++)
        	{
        		// end of range - assign (-1)
        		if (nValueIndex >= maxValue)
        		{
        			IssueScoreArray[nValueIndex] = -9999;
        			IssuesByPriority[nIssue][nValueIndex] = -1;
        			continue;
        		}
        		
        		// offer array
        		int[] OfferArray = new int[numberOfIssues];
        		for (int j=0; j < numberOfIssues; j++)
        		{
        			OfferArray[j] = -1;
        		}
        		
        		OfferArray[nIssue] = nValueIndex;
        		
        		// calculate score for this value of issue
        		IssueScoreArray[nValueIndex] = agentTools.getAgreementValue(OfferArray);
        	}
        	
        	// now that we have the score, we can rank the array
        	// we will loop over the array, each time find the max and write it
        	for (int rank = 0; rank < maxValue; rank++)
        	{
        		int maxIndex = 0;
        		double maxScore = -9999;
        		for(int j=1;j<IssueScoreArray.length; j++)
        		{
        			if (IssueScoreArray[j] > maxScore)
        			{
        				maxScore = IssueScoreArray[j];
        				maxIndex = j;
        			}
        		}
    			IssueScoreArray[maxIndex] = -9999;
        		IssuesByPriority[nIssue][rank] = maxIndex;
        	}
        }
        
    	// debug - print array
        for (int i = 0; i< numberOfIssues; i++)	
        {
        	String str = "";
        	for (int j=0; j<maxRange; j++)
        	{
        		str = str + IssuesByPriority[i][j];
        	}
        	System.err.println(str);
        }

        /********************************
         * End example code
         ********************************/
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
    	// calculate if there is enough compromise pints;
    	// if there is - accept, if not - reject.
    	
    	int nPoints = GetCompromisePoints(agentTools.getCurrentTurn());
    	for (int i=0;i< CurrentAgreementIdx.length; i++)
    	{
    		if (CurrentAgreementIdx[i] != -1 && CurrentAgreementIdx[i] != AgreementStatus[i])
    		{
    			nPoints -= CalculateValuePriority(i,CurrentAgreementIdx[i]);  		
    		}
    	}
    	// if has enough points, accept; else reject
    	if (nPoints < 0)
            agentTools.rejectMessage(sOriginalMessage);
    	else
    	{
    		agentTools.acceptMessage(sOriginalMessage);
    		// save values if it was an offer or counter offer
    		if (nMessageType == 9 || nMessageType == 3)
    		{
	        	for (int i=0;i< CurrentAgreementIdx.length; i++)
	        	{
	        		if (CurrentAgreementIdx[i] != -1)
	        		{
	        			AgreementStatus[i] = CurrentAgreementIdx[i];		
	        		}
	        	}
    		}
    		
    	}
            return;
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
		
		// (Ignoring comments)
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
		
		// randomize response
		double rand = Math.random();
		if (rand < 0.33)
		{
			globalCompromisePoints++;
		}
		else if (rand < 0.66)
		{
		globalCompromisePoints--;	
		}
		// else - do nothing
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
		for (int i=1; i< CurrentAgreementIdx.length; i++)
		{
			if (CurrentAgreementIdx[i] != -1)
			{
				AgreementStatus[i] = CurrentAgreementIdx[i];
			}
		}
    }
	
	// holds the current status (agreed / not agreed issues)
	int[] AgreementStatus = {-1,-1,-1,-1,-1};
	// will hold last rejected value of issues for opponent
	int[] RejectedValues; 
	
	// values that the agent is willing to compromise to
	int[] CompromiseValues; 
	
	// sorted array of all priorities per issue
	// for example, if leased car is issue 4, and the best value for the agent 
	//is 'with car (0)', the second is 'no agreement (2)' and third is 'without car(1)
	// so IssuesByPriority[4] = {0,2,1}
	int[][] IssuesByPriority; 
	
	// compromise points can be raised or deducted for some reasons throughout the game;
	// this parameter holds some of points from all reductions / additions
	int globalCompromisePoints = 0;
	
	// maximal values for issues
    int[] maxValues = {3,3,2,2,3};
	
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
		int[] RejectedAgreement = agentTools.getMessageIndicesByMessage(sOriginalMessage);
		
		// store in rejected 
		for (int i=0; i<5;i++)
		{
			RejectedValues[i] = RejectedAgreement[i];
		}
		
    }
    
    /***********************************************
     * @@ End of methods for receiving message
     ***********************************************/
 
	///allow more issues in the offers as the game progresses
    private int IssuesPerTurn(int nTurn, AutomatedAgentType agentType)
    {
    	if (nTurn < agentTools.getTurnsNumber() / 2)
    	{
    		return (agentTools.getTotalIssues(agentType) / 2);
    	}
    	return agentTools.getTotalIssues(agentType) / 2;
    }
	
	///how far can we compromise
    private int GetCompromisePoints(int nTurn)
    {
    	// calculate initial points 
    	int nPoints = 0;
    	int nReducePoints = 0;
    	int nRange = 0;
    	int numOfIssues = 5;
    	
    	// calculate total range
    	for (int i=0; i < numOfIssues; i++)
    	{
    		nRange += maxValues[i];
    	}
    	
    	// assign points
    	if (nTurn < agentTools.getTurnsNumber() / 2)
    	{
    		nPoints = nRange / 4;
    	}
    	nPoints = nRange / 2;
    	
    	// deduct points from agreed issues 
    	for (int i=0; i< AgreementStatus.length; i++)
    	{
    		if (AgreementStatus[i] != -1)
    		{
    			nPoints -= CalculateValuePriority(i,AgreementStatus[i]);
    		}
    	}
    	
    	
    	// deduct global points
    	nPoints -= globalCompromisePoints;
    	return nPoints;
    }
    
    // calculate priority of value of issue
    // (highest is 0)
    int CalculateValuePriority(int nIssue, int nValue)
    {
    	for (int i=0; i < IssuesByPriority[nIssue].length; i++)
    	{
    		if (IssuesByPriority[nIssue][i] == nValue)
    			return i;
    	}
    	return -1;
    }
    
    /**
     * called to decide which offer to propose the opponent at a given turn
     * This method is always called when beginning a new turn
     * You can also call it during the turn if needed
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
    	
    	// Trivial Agent
        //String sOffer = agentTools.getMessageByIndices(new int[]{1,1,1,1,1,1});
        //agentTools.sendOffer(sOffer);
    	
        // define parameters
    	// Salary(3), Position(4), car(2), pension(3), promotion(2), hours(3)
        
    	// validate
    	if (IssuesByPriority == null)
    		return;
    	
    	// initialize agreement and calculate best agreement
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int nIssuesPerTurn = IssuesPerTurn(nCurrentTurn,agentType);
    	int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
    	int[] BestAgreementValues = agentTools.getBestAgreementIndices(agentType);
        int nCompromisePoints = GetCompromisePoints(nCurrentTurn);
        
        // initialize to best values
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = IssuesByPriority[i][0];
        }  
        
        // give offer for unaccepted issues
        for (int j=0; j<4; j++)
        {
	        for (int i = 0; i < totalIssuesNum; ++i)
	        {
	        	if (AgreementStatus[i] != -1)
	        	{
	        		CurrentAgreementIdx[i] = AgreementStatus[i];
	        	}
	        	else if (nCompromisePoints > 0)
	            {
	        		// compromise
	        		// get current priority in the offer
	        		int nCurrentPriority = CalculateValuePriority(i, CurrentAgreementIdx[i]);
	        		
	            	// get next value (compromise) (if we are not on minimal value already)
	        		int comp = IssuesByPriority[i][nCurrentPriority+1];
	        		if (comp != -1)
	        		{
	        			CurrentAgreementIdx[i] = comp; 
	        			nCompromisePoints--;
	        		}
	            }
	        }
        }
  
        // save offer
        agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(CurrentAgreementIdx));
        // Now, the agent's core holds the new selected agreement
        
        // check that the agent doesn't offer agreement with lower value than previously accepted
        double dNextAgreementValue = agentTools.getSelectedOfferValue();
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        // if the value of the offer is lower than already accepted offer, don't send it...
        if (dAcceptedAgreementValue >= dNextAgreementValue)
        {
        	// previous value is higher - don't send
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
