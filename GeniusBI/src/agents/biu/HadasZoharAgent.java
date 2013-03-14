package agents.biu;


/**
 * @author Hadas
 */ 

public class HadasZoharAgent extends OldAgentAdapter{
    
    public HadasZoharAgent() {
    	super();
    }

    public double dFactor=0.90; //the factor of compromising for the first turn
    public AutomatedAgentType TypeOfAgent;
    public int nIssuesNum;
    public int NoValue;
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public HadasZoharAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the negotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		TypeOfAgent=agentType;
		nIssuesNum = agentTools.getTotalIssues(agentType);
		NoValue = AutomatedAgentType.NO_VALUE;
		calculateValues(TypeOfAgent, 1);
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
    }
	
	
    
    /*
     * this method check if a given agreement is full\complete agreement (not partial)
     */
    public boolean isFullAgreement(int[] agreement)
    {
    	
    	boolean isFull=true;
    	for (int i=0 ; i<nIssuesNum ; ++i )
    		if (agreement[i] == NoValue)
    			isFull=false;
    	return isFull;
    
    }
 	
	
/*
 * this method returns the maximum value of agreement, with the same issues that were given
 * CurrentAgreementIdx - given partial agreement
 * dMaxPartialValue - the value of partial comparable agreement (agreement with same issues, max values)  
 */
	
	
public double MaxPartialValue(int[] CurrentAgreementIdx){
	double dMaxPartialValue=0;
	for(int i=0;i<nIssuesNum ; ++i)
		if (CurrentAgreementIdx[i]!=NoValue)
			dMaxPartialValue=+agentTools.getMaxValuePerIssue(TypeOfAgent, i);
	return dMaxPartialValue;
			
	
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
 
    	//response only for offer or counter offer
    	if ((nMessageType==AutomatedAgentMessages.OFFER)||(nMessageType==AutomatedAgentMessages.COUNTER_OFFER))
    	{

    		double dCurrentAgreementValue = agentTools.getAgreementValue(CurrentAgreementIdx);
    	    		
    	/* decisions */
    		
    		if (isFullAgreement(CurrentAgreementIdx))
    		{		
    			if ((dCurrentAgreementValue <= agentTools.getOptOutValue(TypeOfAgent))
    					||(dCurrentAgreementValue <= agentTools.getSQValue(TypeOfAgent)))
    		
    			{	//reject offer, if its value is lower than opt or sq values
    				agentTools.rejectMessage(sOriginalMessage);
    				return;	
    			}
    			/*	full agreements:
    			* 	accept good agreements(that their value is factor of the best agreement)
    			*	reject the others
    			*/
    			if (dCurrentAgreementValue >= (dFactor*agentTools.getBestAgreementValue(TypeOfAgent)))
    			{
    				agentTools.acceptMessage(sOriginalMessage);
    				agentTools.setSendOfferFlag(false);
    			}
    			else
    			{	//reject offer
    				agentTools.rejectMessage(sOriginalMessage);
    				return;	
    			}
    		}
    		else 
    		{	/*	partial agreements:
    		 	*	accept good agreements (that their value is factor of the best comparable agreement)
    		 	*	reject the others
    		 	*/
    			if (dCurrentAgreementValue >= dFactor*MaxPartialValue(CurrentAgreementIdx))  
    			{
    				agentTools.acceptMessage(sOriginalMessage);
    				agentTools.setSendOfferFlag(false);
    			}
    			else
    			{	//reject offer
    				agentTools.rejectMessage(sOriginalMessage);
    				return;	
    			}
    		}
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
	 * @param nMessageType - the type of massage the opponent agreed to, can be
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
   public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
		
	   /* declarations and initialization */
	   
	    int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
	    
	    int CurrentAgreementIdx[] = new int[nIssuesNum];	//temporary agreement to go over all the agreements
	    int SavedAgreementToOffer[] = new int[nIssuesNum];	//agreement to offer
	    int MaxIssueValues[] = new int[nIssuesNum];			//array with the max value of each issue
	    //initialize
	    for (int i=0 ; i<nIssuesNum ; ++i)
	    {  	CurrentAgreementIdx[i] = 0;
	    	SavedAgreementToOffer[i] = 0;
	    	MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
	    }
	    String sCurrentAgreementString =agentTools.getMessageByIndices(CurrentAgreementIdx);
	    String sSavedAgreementToOfferString =agentTools.getMessageByIndices(SavedAgreementToOffer);
	    
	    double dBestAgreementValue=agentTools.getBestAgreementValue(agentType);	//the value of best agreement
	    String sBestAgreementStr=agentTools.getBestAgreementStr(agentType);		//string of best agreement
	    //initialize
	    double dCurrentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
	    double dSavedAgreementToOfferValue = AutomatedAgentType.VERY_SMALL_NUMBER;
	    double dLastOfferAgentSentValue = AutomatedAgentType.VERY_HIGH_NUMBER;
  

	    /* decisions */
	    
	    //at the first turn, sent the best agreement
	    if (nCurrentTurn==1)
	    {
	    	agentTools.setCurrentTurnAgreementString(sBestAgreementStr);
		    agentTools.setCurrentTurnAutomatedAgentValue(dBestAgreementValue);
			
			String sOffer = agentTools.getSelectedOffer();
			agentTools.sendOffer(sOffer);
	    }    
	    	
	    	dLastOfferAgentSentValue=agentTools.getSelectedOfferValue();//the value of the last offer i sent 

	       	for (int i=0 ; i<totalAgreementsNumber ; ++i)
	    	{	
	       		dCurrentAgreementValue = agentType.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
	    		sCurrentAgreementString = agentType.getAgreementStr(CurrentAgreementIdx);
	    		/*
	    		 * 	save (in dSavedAgreementToOfferValue) the agreement that its value is:  
	    		 * 	1. good (factor of the best value)
	    		 * 	2. less than the one sent before	
	    		 *  3. more than the one save before 
	    		 */
	    		if (dCurrentAgreementValue >= (dFactor*dBestAgreementValue)
	    			&&(dCurrentAgreementValue < dLastOfferAgentSentValue)
	    			&&(dCurrentAgreementValue > dSavedAgreementToOfferValue))
	    		{	
	    			dSavedAgreementToOfferValue = dCurrentAgreementValue;
	    			sSavedAgreementToOfferString = sCurrentAgreementString;
	    		}
	    		agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);//next agreement
	    	}
	       	//save the offer in the core
	    	agentTools.setCurrentTurnAutomatedAgentValue(dSavedAgreementToOfferValue);
	   		agentTools.setCurrentTurnAgreementString(sSavedAgreementToOfferString); 
	   		//sent the offer
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
    	int	nIssuesNum = agentTools.getTotalIssues(agentType);
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];

        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        //end initialization
        
        // calculates the best agreement, worst agreement
        // and the utility value per agreement
                     
        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        
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
        
        
        
        //update the factor of compromising linearly,
        //it goes from the factor that was set at the beginning of the game
        //to the status quo
 
        if ((agentTools.getTurnsNumber()==14)&&(agentType==TypeOfAgent))
        {
        	double SQValue = agentTools.getSQValue(agentType);
        	double dFactorForFirstTurn = 0.90;
        	double dFactorForLastTurn = (double)SQValue/(double)agentTools.getBestAgreementValue(agentType);
        	int nLastTurn = agentTools.getTurnsNumber() ;
        	double delta = (dFactorForFirstTurn-dFactorForLastTurn)/nLastTurn;
        	dFactor = dFactorForFirstTurn-delta*(nCurrentTurn-1);
        }
                
        //opt out if opt out value is higher than status quo value  
        if (agentTools.getCurrentTurn()==agentTools.getTurnsNumber() )
        	if (TypeOfAgent.getSQValue()<TypeOfAgent.getOptOutValue())
        		agentTools.optOut();
           
        						
    }
    
     
}
