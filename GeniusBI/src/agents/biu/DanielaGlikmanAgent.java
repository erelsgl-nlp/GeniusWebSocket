package agents.biu;

import java.util.Random;

import negotiator.issue.IssueDiscrete;
import negotiator.utility.EvaluatorDiscrete;
/**
 * @author Daniela
 * 
 */ 
public class DanielaGlikmanAgent extends OldAgentAdapter{

    double[] issuesIndxWeights;
    int[] maxValIssueIndx;
    int mostImportanatIssue, leastImportanatIssue;
    int totalNumOfIssues;
    protected Random rand;
    
    public DanielaGlikmanAgent() {
    	super();
    	rand = new Random(System.currentTimeMillis());
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public DanielaGlikmanAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
        rand = new Random(System.currentTimeMillis());
    }
    
	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
        // calculate Automated Agent first offer
		totalNumOfIssues = agentTools.getTotalIssues(agentType);
		getWeightOfIssuesMostAndLeastImportant(agentType);
		calculateFirstStepBestAggrement(agentType);
    }
    
	public void getWeightOfIssuesMostAndLeastImportant(AutomatedAgentType agentType){
		issuesIndxWeights = new double[totalNumOfIssues];
		mostImportanatIssue = -1;
		leastImportanatIssue = -1;
		maxValIssueIndx = new int[totalNumOfIssues];
		double maxUtilityValuePerIssue;
		for (int i=0;i<agentTools.getTotalIssues(agentType);i++) {
			IssueDiscrete issue=(IssueDiscrete) agentType.us.getDomain().getIssues().get(i);
			EvaluatorDiscrete evaluator=(EvaluatorDiscrete)agentType.us.getEvaluator(issue.getNumber());
			issuesIndxWeights[i]=evaluator.getWeight();
			if ((mostImportanatIssue == -1) || (issuesIndxWeights[mostImportanatIssue] <= issuesIndxWeights[i])){
				mostImportanatIssue = i;
			}
			if ((leastImportanatIssue == -1) || (issuesIndxWeights[mostImportanatIssue] > issuesIndxWeights[i])){
				leastImportanatIssue = i;
			}
			maxUtilityValuePerIssue = -1;
			for (int k=0;k<issue.getNumberOfValues();k++){
				double dValue = -1;
				try {
					dValue = evaluator.getEvaluationNotNormalized(issue.getValue(k));
				} catch (Exception e) {	e.printStackTrace();}
				if ((maxUtilityValuePerIssue == -1) || (maxUtilityValuePerIssue <= dValue)){
					maxValIssueIndx[i] = k;
					maxUtilityValuePerIssue = dValue;
				}
			}
		}
	}
	
	public void calculateFirstStepBestAggrement(AutomatedAgentType agentType){
		
		int nIssuesNum = agentTools.getTotalIssues(agentType);
		int[] bestAgreementIndx = new int[nIssuesNum];
		for (int i = 0; i < nIssuesNum; ++i)
		{
			if (issuesIndxWeights[i] < 0){
				bestAgreementIndx[i] = AutomatedAgentType.NO_VALUE;
			}else{
				bestAgreementIndx[i] = maxValIssueIndx[i];
			}
		}
		double dAggrementValue = agentType.getAgreementValue(bestAgreementIndx, 1);
		agentTools.setBestAgreementIndices(agentType, bestAgreementIndx);
		agentTools.setBestAgreementValue(agentType, dAggrementValue);
		agentTools.sendOffer(agentTools.getBestAgreementIndices(agentType));
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
    public void calculateResponse(int nMessageType, int []CurrentAgreementIdx, String sOriginalMessage) {
    	
    	if (AutomatedAgentMessages.OFFER == nMessageType || AutomatedAgentMessages.COUNTER_OFFER == nMessageType){
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
    			return;
    		}

    		// 2. check the value of the automated agent in the next turn
    		agentTools.calculateNextTurnOffer();
    		dAutomatedAgentNextOfferValueForAgent = agentTools.getNextTurnOfferValue();
    		
    		if (dOppOfferValueForAgent >= dAutomatedAgentNextOfferValueForAgent)
    		{
    			
    			if (agentTools.getCurrentTurn() >= (agentTools.getTurnsNumber()*0.5)){
    				AutomatedAgentType agentType = agentTools.getAutomatedAgentType();
    				double dOutPutValue = agentTools.getOptOutValue(agentType);
    				if (dOutPutValue > dAutomatedAgentNextOfferValueForAgent){
    					if (rand.nextDouble() < 0.1d){
    						agentTools.optOut();
    						agentTools.setSendOfferFlag(false);
    						return;
    					}
    				}
    				if (agentTools.getCurrentTurn() > (agentTools.getTurnsNumber()*0.8) 
    						&& dOppOfferValueForAgent > agentTools.getSQValue(agentType)){
    					// accept offer
    					agentTools.acceptMessage(sOriginalMessage);

    					//prevent sending future offer in this turn
    					agentTools.setSendOfferFlag(false);
    					return;
    				}
    			}
    			//try to get agreement close to mine
    			AutomatedAgentType agentType = agentTools.getAutomatedAgentType();
    			createNewAgreementBasedOnBestAgreement(CurrentAgreementIdx, false);
    			String selectedOffer = agentTools.getSelectedOffer();
    			int[] bestAgreementIndx = agentType.getAgreementIndices(selectedOffer);
    			agentTools.sendCounterOffers(bestAgreementIndx);
    		}
    	}
    }
    
    //create new agreement based on best one and taking the value of worst agreement or from last agreement 
    //received from client and update it at least important issue.
    private void createNewAgreementBasedOnBestAgreement(int []CurrentAgreementIdx, boolean fromReject){
    	double currentAgreementVal = agentTools.getAgreementValue(CurrentAgreementIdx);
    	AutomatedAgentType agentType = agentTools.getAutomatedAgentType();
    	String bestAgreement = agentTools.getBestAgreementStr(agentType);
    	int[] bestAgreementIndx = agentType.getAgreementIndices(bestAgreement);
    	if (!fromReject){
    		for (int i=0;i<totalNumOfIssues;i++){
    			if (CurrentAgreementIdx[i] == AutomatedAgentType.NO_VALUE 
    					&& bestAgreementIndx[i] != AutomatedAgentType.NO_VALUE){
    				CurrentAgreementIdx[i] = bestAgreementIndx[i];
    			}
    		}
    		CurrentAgreementIdx[mostImportanatIssue] = bestAgreementIndx[mostImportanatIssue];
    		double newCurrentAgreementVal = agentTools.getAgreementValue(CurrentAgreementIdx);
    		bestAgreementIndx[leastImportanatIssue] = CurrentAgreementIdx[leastImportanatIssue];
    		double newBestAgreement = agentTools.getAgreementValue(bestAgreementIndx);
    		if (newCurrentAgreementVal >= currentAgreementVal){
    			if (newCurrentAgreementVal >= newBestAgreement){
    				agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(CurrentAgreementIdx));
    			}
    		}else{
    			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(bestAgreementIndx));
    		}
    	}else{
    		String worstAgreementStr = agentTools.getWorstAgreementStr(agentType);
    		int[] worstAgreementIndx = agentType.getAgreementIndices(worstAgreementStr);
    		bestAgreementIndx[leastImportanatIssue] = worstAgreementIndx[leastImportanatIssue];
    		agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(bestAgreementIndx));
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
		AutomatedAgentType agentType = agentTools.getAutomatedAgentType();
		//add the undecided issues which increase the agreement value to complete agreement
		for (int i=0;i<totalNumOfIssues;i++){
			if (CurrentAgreementIdx[i] == AutomatedAgentType.NO_VALUE){
				if (issuesIndxWeights[i] * agentTools.getMaxValuePerIssue(agentType, i) > 0){
					CurrentAgreementIdx[i] = maxValIssueIndx[i];
				}
			}
		}
		agentTools.sendOffer(agentType.getAgreementStr(CurrentAgreementIdx));
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
		createNewAgreementBasedOnBestAgreement(CurrentAgreementIdx, true);
		
		String selectedOffer = agentTools.getSelectedOffer();
		agentTools.sendOffer(selectedOffer);
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

    	int[] bestAgreementIndx = agentTools.getBestAgreementIndices(agentType);
    	int[] acceptedAgreementIndx = agentTools.getAcceptedAgreementIdx().clone();
    	int[] currentAgreementBasedOnPreviousIndx = acceptedAgreementIndx; 
    	int[] currentAgreementBasedOnBestIndx = bestAgreementIndx;
    	//build new agreement based on previous agreement use the the best one and the 
    	//best values to increase its value 
    	for (int i=0;i<totalNumOfIssues;i++){
    		if (currentAgreementBasedOnPreviousIndx[i] == AutomatedAgentType.NO_VALUE){
    			if (bestAgreementIndx[i] != AutomatedAgentType.NO_VALUE){
    				currentAgreementBasedOnPreviousIndx[i] = bestAgreementIndx[i];
    			}else{
    				currentAgreementBasedOnPreviousIndx[i] = maxValIssueIndx[i];
    			}
    		}
    	}
    	//build new agreement based on best agreement use the the previous one and the 
    	//to increase its value
    	for (int i=0;i<totalNumOfIssues;i++){
    		if ((currentAgreementBasedOnBestIndx[i] == AutomatedAgentType.NO_VALUE) && 
    				(bestAgreementIndx[i] != AutomatedAgentType.NO_VALUE)){

    			currentAgreementBasedOnBestIndx[i] = bestAgreementIndx[i];
    		}
    	}
    	//calculate the value of 2 new agreements
    	agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(currentAgreementBasedOnPreviousIndx));
    	double nextByPrevVal = agentTools.getSelectedOfferValue();
    	agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(currentAgreementBasedOnBestIndx));
    	double nextByBestVal = agentTools.getSelectedOfferValue();
        double dNextAgreementValue;
        //select the better one
        if (nextByPrevVal >= nextByBestVal){
        	dNextAgreementValue = nextByPrevVal;
        	agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(currentAgreementBasedOnPreviousIndx));
        }else{
        	dNextAgreementValue = nextByBestVal;
        }
        
        // get the value of previously accepted agreement
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        // Now, check whether the offer the agent intends to propose in the next turn is better
        // for it than previously accepted agreement
        
        // if the value of the offer is higer than already accepted send it...
        if (dAcceptedAgreementValue < dNextAgreementValue)
        {
        	//if decided to send offer - then send the offer
            //Get the offer as string and format it as an offer
            String sOffer = agentTools.getSelectedOffer();
            agentTools.sendOffer(sOffer);
        }
        
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
    }
}
