package agents.biu;

import java.util.*;



/**
 * This class should hold all your logic for your automated agent
 */ 
public class MeytalTraubAgent extends OldAgentAdapter
{
    /****************************************
     * Start definition: My global variables 
     ****************************************/
    AutomatedAgentType myAgentType;
    AutomatedAgentType OpponentAgentType;
    int bestCompleteAgreement_me[];
    int bestCompleteAgreement_Opponent[];
    int bestJointCompleteAgreement[];
    double bestJointCompleteAgreementValue_me;
    double bestJointCompleteAgreementValue_Opponent;
    double bestCompleteAgreementValue;
    boolean sendBestJointCompleteAgreement;
    double AgreementFactor; // in [0,1]
    String sOpponentType;
    double fitOpponentType[] = new double[3];
    boolean lastOfferRejectByOpponent;
    boolean lastOfferWasPartial;
    int CurrAgreement =0; //agreement numerator
	int MoveFactor=1;
    
	ArrayList<Agreement> AllAgreements = new ArrayList<Agreement>();
    /****************************************
     * End definition: My global variables 
     ****************************************/
    
    public MeytalTraubAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public MeytalTraubAgent(AgentTools agentTools) 
    {
        this.agentTools = agentTools;
    }
    
	/********************************************
	 * Called before the the negotiation starts.
	 * Initialization my global variables 
	 * and call for calculateOfferAgainstOpponent
     * @param agentType - the automated agent
	 ********************************************/
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		this.myAgentType = agentType;
        this.sendBestJointCompleteAgreement = false;
        this.sOpponentType = sOpponentType; 
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        bestCompleteAgreement_me = new int[nIssuesNum];
        bestCompleteAgreement_Opponent = new int[nIssuesNum];
        bestJointCompleteAgreement = new int[nIssuesNum];
        bestCompleteAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        bestJointCompleteAgreementValue_Opponent = AutomatedAgentType.VERY_SMALL_NUMBER;
        bestJointCompleteAgreementValue_me = AutomatedAgentType.VERY_SMALL_NUMBER;
        this.AgreementFactor = 0.25;
        lastOfferRejectByOpponent = false;
        lastOfferWasPartial = false;

        
        bestCompleteAgreement_Opponent= new int[nIssuesNum];
   
        for (int i = 0; i < nIssuesNum; ++i)
        {
        	bestCompleteAgreement_Opponent[i] =-1;
        }
        
        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
    }
    
    /** 
     * calculateResponse according to the agreement type (full, partial)
     * always reject query/promise
     * @param nMessageType - the message type
     * @param CurrentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     */
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) 
    {
    	//calculate array of top 25% opponent best offers
    	int nIssuesNum = agentTools.getTotalIssues(myAgentType);
           
        for (int i = 0; i < nIssuesNum; ++i)
        {
            bestCompleteAgreement_Opponent[i] = CurrentAgreementIdx[i];
        }   
        double [] bestIssues = new double[nIssuesNum];
        for (int i = 0; i < nIssuesNum; ++i)
        {
        	int[] tempIdx = new int[nIssuesNum];
        	for (int j = 0; j < nIssuesNum; ++j)
        	{
        		// The issues are not the same so take the best issue indices from the best issue 
        		// Agreement of me
        		if (i!=j)
        			tempIdx[j]=bestCompleteAgreement_me[j];
        		// Otherwise take it from the opponent offer
        		else
        			tempIdx[j]=CurrentAgreementIdx[j];
        	}
        	// Got the Value of the best Agreements with the changes in the agreement the 
        	// opponent offered
        	bestIssues[i] = agentTools.getAgreementValue(myAgentType, tempIdx, agentTools.getCurrentTurn());
        }
        
        for (int i = 0; i < nIssuesNum; ++i)
        {
        	int countBetterThen=0;
        	for (int j=0; j < nIssuesNum; ++j)
        	{
        		// If the the issue in the offer is not the same issue
        		// and it value is better than the best offer I can get in 
        		// this round
        		if (i!=j && bestIssues[j]>bestIssues[i])
        			countBetterThen++;
        	}
        	if (countBetterThen>(AgreementFactor)*nIssuesNum)
        		bestCompleteAgreement_Opponent[i]=-1;
        }
        
    	//accept if and only if the current offer is better then offers that have already given
    	Agreement AgrRec = new Agreement(agentTools.getAgreementValue(CurrentAgreementIdx),CurrentAgreementIdx);
    	MoveFactor = (int)(((double)agentTools.getTotalAgreements(myAgentType)-CurrAgreement+1)/(2*(agentTools.getTurnsNumber()- agentTools.getCurrentTurn())+1))+1;
    	if (AgrRec.Utility >=AllAgreements.get(CurrAgreement).Utility )
    	{
    		// Accept the offer
    		agentTools.acceptMessage(sOriginalMessage);
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
    	}
    	// The offer utility is not good so reject the offer
    	else
    	{
    		agentTools.rejectMessage(sOriginalMessage);
    	}
    }
        
    /***********************************************
     * @@ Start: Logic for receiving messages -----------------------------------------
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
	 * update the OpponentType array
	 * @param nMessageType - the type of massage the opponent agreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) 
	{
    	int curTurn = agentTools.getCurrentTurn();
		updateOpponentType(CurrentAgreementIdx, curTurn);
		lastOfferRejectByOpponent = false;
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the opponent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) 
	{
		lastOfferRejectByOpponent = true;
		
		//Send next agreement in order
		//String sOffer =	agentTools.getMessageByIndices(Agrs.get(CurrAgreement).Agreement);
		int [] idxOffer = AllAgreements.get(CurrAgreement).Agreement.clone();
		for (int i =0;i<agentTools.getTotalIssues(myAgentType);i++){
			if (bestCompleteAgreement_Opponent[i]!=-1){
				idxOffer[i]=bestCompleteAgreement_Opponent[i];
			}
		}
		String sOffer =	agentTools.getMessageByIndices(idxOffer);
		MoveFactor = calculateMoveFactor();
		CurrAgreement = CurrAgreement + MoveFactor;
        agentTools.sendOffer(sOffer);
    }
    
    /***********************************************
     * @@ End: methods for receiving message ------------------------------------------
     ***********************************************/
 
    /**
     * called to decide which offer to propose the opponent at a given turn
     * send response according to incoming offer 
     * This method is always called when beginning a new turn
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) 
    {
    	int nIssuesNum = agentTools.getTotalIssues(agentType);
    	int CurrentAgreementIdx[] = new int[nIssuesNum];
    	int MaxIssueValues[] = new int[nIssuesNum];
    	
        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
    	
    	int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);    	
    	AllAgreements.clear();
        double previousBest =0;
        
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {   
            //add agreement to my collection
        	AllAgreements.add(new Agreement(agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn),CurrentAgreementIdx.clone()));

            //  check for best agreement
            if (agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn) > previousBest)
            {
              previousBest = agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn) ;
              bestCompleteAgreement_me =  CurrentAgreementIdx.clone();
            }                       
            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        }
        
        //Sorting the agreements in descending order 
        Collections.sort(AllAgreements);

        //Send next agreement with opponents best 25% of last offer
		int [] idxOffer = AllAgreements.get(CurrAgreement).Agreement.clone();
		for (int i =0;i<agentTools.getTotalIssues(myAgentType);i++){
			if (bestCompleteAgreement_Opponent[i]!=-1){
				idxOffer[i]=bestCompleteAgreement_Opponent[i];
			}
		}
		String sOffer =	agentTools.getMessageByIndices(idxOffer);
		MoveFactor = calculateMoveFactor();
		CurrAgreement = CurrAgreement + MoveFactor;
        
		//if it is better to Opt Out than Opt out
		if (agentTools.getAgreementValue(myAgentType, idxOffer, nCurrentTurn)<agentTools.getOptOutValue(agentType)){
        	 agentTools.optOut();	 
        }else{
        	agentTools.sendOffer(sOffer);
        }  	
    }
    
    /**
     * calculate and save the values of best and worst agreements for the turn:
     * agentTools.setWorstAgreementIndices
     * agentTools.setBestAgreementIndices
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
        double dAgreementValue = 0;
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        //end initialization

        // calculates the best agreement, worst agreement and the utility value per agreement
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremeneted
            dAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
            
            // check for best agreement
            if (dAgreementValue > agentTools.getBestAgreementValue(agentType))
            {// save agreement
                agentTools.setBestAgreementValue(agentType, dAgreementValue);
                agentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);
            }                              
            // check for worst agreement
            if (dAgreementValue < agentType.getWorstAgreementValue())
            {// save agreement
                agentTools.setWorstAgreementValue(agentType, dAgreementValue);
                agentTools.setWorstAgreementIndices(agentType, CurrentAgreementIdx);
            }                       
            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } 
        
        //random reset CurrAgreement (with probability 0.05, to some random point between 0 and current value)
        Random rnd = new Random();
        if (rnd.nextFloat()>=0.95)
        {
        	CurrAgreement = Math.round(rnd.nextFloat()*CurrAgreement);
        }
    }

    /** 
     * Get AgreementIdx and return the AutomatedAgentType that fit the most for that agreement
     * fit the most = have the biggest value for that agreement
     * @param AgreementIdx - the agreement to check
     * @param curTurn - the current turn number
     */
    public AutomatedAgentType findOpponentType(int AgreementIdx[], int curTurn)
    {
        // the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
        
        double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, AgreementIdx, curTurn);
        double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, AgreementIdx, curTurn);
        double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, AgreementIdx, curTurn);
        
        double temp_fitOpponentType[] = new double[3];
        temp_fitOpponentType[AutomatedAgentsCore.LONG_TERM_TYPE_IDX] = dOpponentLongTermAgreementValue;
        temp_fitOpponentType[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX] = dOpponentShortTermAgreementValue;
        temp_fitOpponentType[AutomatedAgentsCore.COMPROMISE_TYPE_IDX] = dOpponentCompromiseAgreementValue;
        
        double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        int maxValueIndex=0;
        for (int i=0; i<fitOpponentType.length ; i++)
        {
        	if (temp_fitOpponentType[i] > maxValue)
        	{
        		maxValue = fitOpponentType[i];
        		maxValueIndex = i;          
        	}
        }      
        
        return agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
	}
    
    /** 
     * Get agreed AgreementIdx and update the fitOpponentType array, 
     * check the value for that Agreement for all AutomatedAgentType and update fitOpponentType array
     * update OpponentAgentType according to the highest result
     * @param AgreementIdx - the agreement to check
     * @param curTurn - the current turn number
     */
    public void updateOpponentType(int AgreementIdx[], int curTurn)
    {
        // the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
        
        double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, AgreementIdx, curTurn);
        double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, AgreementIdx, curTurn);
        double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, AgreementIdx, curTurn);
        
        fitOpponentType[AutomatedAgentsCore.LONG_TERM_TYPE_IDX]  += dOpponentLongTermAgreementValue;
        fitOpponentType[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX] += dOpponentShortTermAgreementValue;
        fitOpponentType[AutomatedAgentsCore.COMPROMISE_TYPE_IDX] += dOpponentCompromiseAgreementValue;
        
        //find the highest value
        double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        int maxValueIndex=0;
        for (int i=0; i<fitOpponentType.length ; i++)
        {
        	if (fitOpponentType[i] > maxValue)
        	{
        		maxValue = fitOpponentType[i];
        		maxValueIndex = i;          
        	}
        }      
        OpponentAgentType = agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
	}
    
    /**
     * TO CHECK @return the most Seasonable AutomatedAgentType of the Opponent
     * according to fitOpponentType array
     */
    public AutomatedAgentType getMostSeasonableOpponentType()
    {
        double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        int maxValueIndex=0;
        for (int i=0; i<fitOpponentType.length ; i++)
        {
        	if (fitOpponentType[i] > maxValue)
        	{
        		maxValue = fitOpponentType[i];
        		maxValueIndex = i;          
        	}
        }      
        
        return agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
    }

    /**
     * Find complete agreement to the partial Agreement that his value is
     * more than min_value and is best for the given pOpponentAgentType.
     * Save the results at: 
     * [bestFitCompleteAgreement] and his value [bestFitCompleteAgreementValue]
     * @param myAgentType - my automated agent's type
     * @param pOpponentAgentType - Opponent automated agent's type (estimated)
     * @param PartialAgreement - Partial Agreement
     * @param curTurn - the current turn
     * @param min_value - miv value for the complete agreement
     */
    public void FindBestFitcompletionOfPartialAgreement(
    		AutomatedAgentType myAgentType, AutomatedAgentType pOpponentAgentType, 
    		int PartialAgreement[], int curTurn, double min_value)
    {
        // initialization - DO NOT CHANGE
        int nIssuesNum = agentTools.getTotalIssues(myAgentType);
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];
        int MinIssueValues[] = new int[nIssuesNum];
        int totalAgreementsNumber = agentTools.getTotalAgreements(myAgentType);
        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(myAgentType, i);
            MinIssueValues[i] = 0; //minimum value
            bestJointCompleteAgreement[i] = bestCompleteAgreement_me[i];
        }
        //best value for now is the value of the bestCompleteAgreement
        //which is best for me, but maybe not for Opponent
        bestJointCompleteAgreementValue_Opponent = 
        	agentTools.getAgreementValue(pOpponentAgentType, bestCompleteAgreement_me, curTurn);;
        //end initialization

        double AgreementValue_forMe = 0;
        double AgreementValue_forOpponent = 0;

        // going over all agreements, 
        // check if agreements complete the given PartialAgreement 
        // check if their value is more then min_value
        // for each calculating the value
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //check if the if agreements complete the given PartialAgreement
        	if (isCompletionOFPartialAgreement(myAgentType,CurrentAgreementIdx,PartialAgreement))
        	{ 
	        	AgreementValue_forMe = agentTools.getAgreementValue(myAgentType, CurrentAgreementIdx, curTurn);
	        	AgreementValue_forOpponent = agentTools.getAgreementValue(pOpponentAgentType, CurrentAgreementIdx, curTurn);
	            
	        	// check if the agreements is good for me
	            if (AgreementValue_forMe > min_value)
	            {
	                // check if the current agreements is better for Opponent
	            	if (AgreementValue_forOpponent > bestJointCompleteAgreementValue_Opponent)
	            	{
	            		//save agreement
	            		for (int k = 0; k < nIssuesNum; ++k)
		                {
	            			bestJointCompleteAgreement[k] = CurrentAgreementIdx[k];
		                }
		                //save value
	            		bestJointCompleteAgreementValue_Opponent = AgreementValue_forOpponent;
	            		bestJointCompleteAgreementValue_me = AgreementValue_forMe;
	            	}
	            }                       
        	}
        	// get the next agreement indices
        	agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);
        }

	}
    
    /**
     * Calculate values of completion agreements of the given Partial Agreement
     * Save the results at: 
     * [bestCompleteAgreement] and his value [bestCompleteAgreementValue]
     * @param agentType - the automated agent's type
     * @param PartialAgreement - Partial Agreement
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues_CompletionOfPartialAgreement(AutomatedAgentType agentType, int PartialAgreement[], int nCurrentTurn) {
        // initialization - DO NOT CHANGE
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];
        int MinIssueValues[] = new int[nIssuesNum];
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
            MinIssueValues[i] = 0; //minimum value
            bestCompleteAgreement_me[i] = 0;
        }
        bestCompleteAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        //end initialization

        double AgreementValue = 0;

        // going over all agreements, check if agreements complete the given PartialAgreement  
        // for each calculating the value
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //if the CurrentAgreementIdx complete the given PartialAgreement
        	if (isCompletionOFPartialAgreement(agentType,CurrentAgreementIdx,PartialAgreement))
        	{
	            AgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
	            //check for best agreement
	            if (AgreementValue > bestCompleteAgreementValue)
	            {
	            	//save agreement             
	                for (int k = 0; k < nIssuesNum; ++k)
	                {
	                	bestCompleteAgreement_me[k] = CurrentAgreementIdx[k];
	                }
	                //save value
	                bestCompleteAgreementValue = AgreementValue;
	            }                                            
        	}
            // get the next agreement indices
        	agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);
        } 
    }
    
    /**
     * Get: FullAgreementIdx and PartialAgreement
     * Check if the CurrentAgreementIdx is completion of the PartialAgreement.
     * Return: True if yes, False if no
     * @param agentType - the automated agent's type
     * @param FullAgreementIdx - complete agreement
     * @param PartialAgreementIdx - Partial Agreement
     */
    public boolean isCompletionOFPartialAgreement(AutomatedAgentType agentType, int FullAgreementIdx[] ,int PartialAgreementIdx[])
    {
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        for (int i = 0; i < nIssuesNum; ++i)
        {
            //check if value is set (not NotSet and not NoAgreement value)
        	if (agentType.isIssueValueNotSet(PartialAgreementIdx[i]) 
        			|| (agentType.isIssueValueNoAgreement(i, PartialAgreementIdx[i]))) 
        		continue;
        	else //value is set 
        	{ //check if same in full and partial agreements 
        		if (FullAgreementIdx[i]!= PartialAgreementIdx[i])
        		return false; //different values 
        	}
        }
        return true;   
    }
    
    /**
     * Check if the agreement is:  
     * Partial - return 0 ,
     * Include NoAgreement values - return 1
     * Full - return 2  
     * @param agentType - the automated agent's type
     * @param AgreementIdx - agreement
     */
    public int getAgreemmentType(AutomatedAgentType agentType, int AgreementIdx[])
    { 
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        for (int i = 0; i < nIssuesNum; ++i)
        {            
        	if (agentType.isIssueValueNotSet(AgreementIdx[i]))
        		return 0; // some value in the agreement not set - partial agreement
        	if (agentType.isIssueValueNoAgreement(i, AgreementIdx[i]))
        		return 1; // some value in the agreement is NoAgreement
        }
        return 2;  // Full agreement 
    }
    

    /**
     * Check if the agreement is full
     * @param agentType - the automated agent's type
     * @param AgreementIdx - agreement
     */
    public boolean isFullAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
    { 
         if (getAgreemmentType(agentType,AgreementIdx) == 2)
        	 return true;
         else 
        	 return false;
    }
     
    
    /**
     * Check if the agreement is Semi full (with NoAgreement values) 
     * @param agentType - the automated agent's type
     * @param AgreementIdx - agreement
     */  
    public boolean isSemiFullAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
    { 
         if (getAgreemmentType(agentType,AgreementIdx) == 1)
        	 return true;
         else 
        	 return false;
    }
    
      
    /**
     * Check if the agreement is real partial (there are no agreed issues)
     * @param agentType - the automated agent's type
     * @param AgreementIdx - agreement
     */
    public boolean isRealPartialAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
    { 
         if (getAgreemmentType(agentType,AgreementIdx) == 0)
        	 return true;
         else 
        	 return false;
    }
       
        
	private int calculateMoveFactor(){
		return (int)(((double)agentTools.getTotalAgreements(myAgentType)-CurrAgreement+1)/(3*(agentTools.getTurnsNumber()- agentTools.getCurrentTurn())+1))+1;
	}
	
	//
	// This is a class that stores all the agreements ordered by their utility functions
	private class Agreement implements Comparable{
		double Utility;
		int[] Agreement;
		
		Agreement (double u, int[] a)
		{
			Utility = u;
			Agreement =a;
		}
		
		//reverse order (we want the agreements to be sorted from the max utility downwards
		public int compareTo(Object o){
			if (((Agreement)o).Utility <this.Utility )
				return -1;
			else if (((Agreement)o).Utility >this.Utility)
				return 1;
			else 
				return 0;
		}	
	}
	
}


    
    
 
