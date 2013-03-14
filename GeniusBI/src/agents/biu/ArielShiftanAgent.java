package agents.biu;

/**
 * @author Ariel Shiftan
 * 
 */ 
public class ArielShiftanAgent extends OldAgentAdapter{
    
    // My data
    double myPDiff;
    double hisPDiff;
    boolean learnMode;
    int proposeBestFor;
    int agreedBestFor;
    
    boolean[] possOp;
    boolean lastAgree;
    int m_nextPossToCheck;
    int [] lastRejected;
    int [] lastAccepted;
    
    AutomatedAgentType m_agentType;
    String m_sOpponentType;

	private int m_minDiff;
    
    // My Functions

    // usually caled only when preparing first proposal for best lerning after.
	private boolean sendMaxDiffForLearn(int index) {
    	int totalIssuesNum = agentTools.getTotalIssues(m_agentType);
    	int totalAgreementsNumber = agentTools.getTotalAgreements(m_agentType);
        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = AutomatedAgentType.NO_VALUE;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(m_agentType, i);
        }

        String bestOffer = "";
        boolean res = false;
        double bestVal = 0;
        
        double lastMaxDiff = 100000;
        for (; index >= 0; index--)
        {
        	double maxDiff = 0;
	        for (int i = 0; i < totalIssuesNum; ++i)
	        {
	        	for (int j = 0; j < MaxIssueValues[i]; j++)
	        	{
	        		CurrentAgreementIdx[i] = j;
	        		if (false == hasRealOpenIssues(totalIssuesNum, CurrentAgreementIdx, m_agentType)) continue;
	        		double min = 0;
	        		double max = 0;
	                for (int k = 0; k < possOp.length; k++)
	                {
	                	if (possOp[k] == false)
	                	{
	                		continue;
	                	}
	
	                	AutomatedAgentType opAgentType = agentTools.getCurrentTurnSideAgentType(m_sOpponentType, k);
	        			double opValue = getRealAgreementValue(CurrentAgreementIdx, opAgentType);
	                	
	        			min = Math.min(min, opValue);
	        			max = Math.max(max, opValue);
	                }
	                if (max-min < lastMaxDiff) maxDiff = Math.max(maxDiff, max-min);
	                
	                if (max-min == maxDiff)
	                {
	                	bestOffer = m_agentType.getAgreementStr(CurrentAgreementIdx);
	                	bestVal = getRealAgreementValue(CurrentAgreementIdx, m_agentType);
	                	res = true;
	                }
	        	}
	        	CurrentAgreementIdx[i] = AutomatedAgentType.NO_VALUE;
	        }
	        lastMaxDiff = maxDiff;
        }
        if (res == true)
        {
        	agentTools.sendOffer(bestOffer);
        	//System.out.println("***Sending offer2:"+ bestVal);
        }
        
        
        return res;
	}
	
	// is agreement has open issues?
	private boolean hasRealOpenIssues(int totalIssuesNum, int[] CurrentAgreementIdx, AutomatedAgentType agentType) ////////
	{
		int [] realAgg = new int [totalIssuesNum];
		for (int i = 0; i < totalIssuesNum; i++) realAgg[i] = CurrentAgreementIdx[i];
		getRealAgreement(realAgg, agentType);
		boolean noOpenIssues = true;

		for (int t = 0; t < totalIssuesNum; t++)
		{
			if ((realAgg[t] == AutomatedAgentType.NO_VALUE) || (isIssueValueNoAgreement(t, realAgg[t], agentType))) 
			{
					noOpenIssues = false;
			}
		}
		return noOpenIssues == false;
	}
	
	// get next agreement with/without open issues
	private void getNextRealOpenIssuse(int totalIssuesNum, int[] CurrentAgreementIdx, int[] MaxIssueValues, boolean openIssus) {
		int [] realAgg = new int [totalIssuesNum];
		for (int i = 0; i < totalIssuesNum; i++) realAgg[i] = CurrentAgreementIdx[i];
		
		boolean hasOpenIssues = true;
		do
		{
			myGetNextAgreement(totalIssuesNum, realAgg, MaxIssueValues);// get the next agreement indices
			hasOpenIssues = hasRealOpenIssues(totalIssuesNum, realAgg, m_agentType);
		} while(hasOpenIssues != openIssus);

		for (int i = 0; i < totalIssuesNum; i++) CurrentAgreementIdx[i] = realAgg[i];

	}
    
	private double max(double a, double b) {
		return a>b?a:b;
	}

    public boolean isIssueValueNoAgreement(int nIssueNum, int nIssueNumIdx, AutomatedAgentType agentType)
	{
		String sIssueValue = agentTools.getIssueValueStr(nIssueNum, nIssueNumIdx);
		
		if (sIssueValue.equals(AgentTools.NO_AGREEMENT))
			return true;
		else
			return false;		
	}

	
	// my get next agreenent. including N/A values.
	public void myGetNextAgreement(int totalIssuesNum, int[] currentAgreementIdx, int[] maxIssueValues) {
        // update issue values indices for evaluating the next agreement
        boolean bFinishUpdate = false;
        for (int k = totalIssuesNum-1; k >= 0 && !bFinishUpdate; --k)
        {
            if (currentAgreementIdx[k]+1 >= maxIssueValues[k])
            {
                currentAgreementIdx[k] = AutomatedAgentType.NO_VALUE;
            }
            else
            {
                currentAgreementIdx[k]++;
                bFinishUpdate = true;
            }                                   
        }
    }
	
	// increment opp to chek next time
	private void incNextCheck()
	{
		do
		{
			if (lastAgree == true)
			{
				lastAgree = false;
			}
			else
				m_nextPossToCheck = ((m_nextPossToCheck + 1) % AutomatedAgentsCore.AGENT_TYPES_NUM);
		} while (possOp[m_nextPossToCheck] == false);
	}
	
	// save new agreed indxs. Try to learn from it (see attahced documentation)
    public void saveLastAccepted(int CurrentAgreementIdx[], boolean isAccept)
    {
    	// try to learn opp
    	
    	boolean learned = false;
    	int [] AgreementIdx = new int[agentTools.getTotalIssues(m_agentType)];
    	for (int i = 0; i < agentTools.getTotalIssues(m_agentType); i++) 
    		AgreementIdx[i] = CurrentAgreementIdx[i];
    	getRealAgreement(AgreementIdx, m_agentType);
    	
		for (int j = 0; j < possOp.length; j++)
		{
			if (possOp[j] == false)
			{
				continue;
			}
			
			AutomatedAgentType opAgentType = agentTools.getCurrentTurnSideAgentType(m_sOpponentType, j);
			double opValue = getRealAgreementValue(AgreementIdx, opAgentType);
			double opPrevRejValue = getRealAgreementValue(lastRejected, opAgentType);
			
			// if a least 50 less than prev rej -> probably not this agent
			if (opPrevRejValue!= AutomatedAgentType.VERY_SMALL_NUMBER &&  opValue + m_minDiff < opPrevRejValue)
			{
				learned = true;
				possOp[j] = false;
			}
		}
		
		if (false == learned && isAccept == true)
		{
			incNextCheck(); 
		}
		
		checkLearnModeRelevant();
		
		// save it
    	for (int i = 0; i < agentTools.getTotalIssues(m_agentType); i++)
    		lastAccepted[i] = AgreementIdx[i];
    }
    
	// save new agreed indxs. Try to learn from it (see attahced documentation)
    public void saveLastRejected(int CurrentAgreementIdx[])
    {
    	// try to learn opp
    	boolean learned = false;
    	int [] AgreementIdx = new int[agentTools.getTotalIssues(m_agentType)];
    	for (int i = 0; i < agentTools.getTotalIssues(m_agentType); i++) 
    		AgreementIdx[i] = CurrentAgreementIdx[i];
    	getRealAgreement(AgreementIdx, m_agentType);
    	
		for (int j = 0; j < possOp.length; j++)
		{
			if (possOp[j] == false)
			{
				continue;
			}
			
			AutomatedAgentType opAgentType = agentTools.getCurrentTurnSideAgentType(m_sOpponentType, j);
			double opValue = getRealAgreementValue(AgreementIdx, opAgentType);
			double opPrevAccValue = getRealAgreementValue(lastAccepted, opAgentType);
			
			// if a least 50 less than prev rej -> probably not this agent
			if (opPrevAccValue!= AutomatedAgentType.VERY_SMALL_NUMBER && opValue - m_minDiff > opPrevAccValue)
			{
				possOp[j] = false;
				learned = true;
			}
		}
		
		if (false == learned)
		{
			incNextCheck(); 
		}

		
		checkLearnModeRelevant();
		
		// save it
    	for (int i = 0; i < agentTools.getTotalIssues(m_agentType); i++)
    		lastRejected[i] = AgreementIdx[i];    
    }

    // check whether learn mode is not relevant anymore.
	private void checkLearnModeRelevant() 
	{
		int trueCount = 0;
		for (int j = 0; j < possOp.length; j++)
			if (possOp[j] == true) trueCount++;
		if (trueCount == 1) 
		{
			learnMode = false;
			/*for (int j = 0; j < possOp.length; j++)
				if (possOp[j] == true) System.out.println("***Know:" + j);*/
		}
		
		// bad reastart possabilities
		if (trueCount == 0)
		{
			for (int j = 0; j < possOp.length; j++)
				possOp[j] = true;			
		}
		
		if (possOp[m_nextPossToCheck] == false) incNextCheck();
	}
    
	// find best offer for learning other side
    private boolean makeOfferForLearnMode(int minDiff) 
    {
    	// initializtion
    	int totalIssuesNum = agentTools.getTotalIssues(m_agentType);
    	int totalAgreementsNumber = agentTools.getTotalAgreements(m_agentType);
        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = AutomatedAgentType.NO_VALUE;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(m_agentType, i);
        }

        // check for max diff to acc val
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
        	double maxBelow = 0;
        	double maxAbove = 0;
        	
        	for (int j = 0; j < possOp.length; j++)
    		{
    			if (possOp[j] == false)
    			{
    				continue;
    			}
    			
    			AutomatedAgentType opAgentType = agentTools.getCurrentTurnSideAgentType(m_sOpponentType, j);
    			double aValue = getRealAgreementValue(CurrentAgreementIdx, opAgentType);
    			double aRejVal = getRealAgreementValue(lastRejected, opAgentType);
    			double aAccVal = getRealAgreementValue(lastAccepted, opAgentType);
    			
    			if (lastAgree)
    			{
	    			maxAbove = max(maxAbove, aValue - aAccVal);
	    			if (m_nextPossToCheck == j)
	    				maxBelow = max(maxBelow, aAccVal - aValue);
    			}
    			else
    			{
    				if (m_nextPossToCheck == j)
    					maxAbove = max(maxAbove, aValue - aRejVal);
	    			maxBelow = max(maxBelow, aRejVal - aValue);
    				
    			}
    			
    		}
        	
        	if (maxBelow > minDiff && maxAbove > minDiff)
        	{
        		String sOffer = m_agentType.getAgreementStr(CurrentAgreementIdx);
        		//System.out.println("***Sending offer:"+ getRealAgreementValue(CurrentAgreementIdx, m_agentType));
        		agentTools.sendOffer(sOffer);
        		return true;
        	}
            
        	
        	getNextRealOpenIssuse(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues, true);// get the next agreement indices
        	
        } // end for - going over all possible agreements
		return false;
	}
	
    // get real agreement value (after raplacing N/A values with prev agreed ones)
	public double getRealAgreementValue(int CurrentAgreementIdx[], AutomatedAgentType agentType)
	{
		int [] AgreementIdx = new int[agentTools.getTotalIssues(agentType)];
		for (int i = 0; i < AgreementIdx.length; i++) AgreementIdx[i] = CurrentAgreementIdx[i];
    	getRealAgreement(AgreementIdx, agentType);
    	return agentTools.getAgreementValue(agentType, AgreementIdx, agentTools.getCurrentTurn()) * 100;
	}
	
	// raplace N/A values with prev agreed ones
    public void getRealAgreement(int CurrentAgreementIdx[], AutomatedAgentType agentType)
    {
    	int m_PreviosAcceptedOffer[] = agentTools.getAcceptedAgreementIdx();
	    	// if a partial agreement was agreed in the past, 
	    // the current agreement may include only partial
	    // value - merge it with previous accepted agreement
	    for (int i = 0; i < agentTools.getTotalIssues(agentType); ++i)
	    {
	        // if value of current issue is "no agreement" or "no value"
	        if (CurrentAgreementIdx[i] == AutomatedAgentType.NO_VALUE)
	            CurrentAgreementIdx[i] = m_PreviosAcceptedOffer[i];
	        else if (isIssueValueNoAgreement(i, CurrentAgreementIdx[i], agentType))
	        {
	            // if previous accepted agreement has values
	            // for it, copy the value
	            if (m_PreviosAcceptedOffer[i] != AutomatedAgentType.NO_VALUE)
	                CurrentAgreementIdx[i] = m_PreviosAcceptedOffer[i];
	        }
	    }
    }
        
    public double getPrec(double min, double max, double value)
    {
    	return 100 * (value-min) / (max-min);
    }
    
    // get precenteage diff between me and possible opps.
    public double getPrecentDiff(int[] AgreementIdx)
    {
    	int totalIssuesNum = agentTools.getTotalIssues(m_agentType);

    	double precent = getPrec(m_agentType.getWorstAgreementValue(), m_agentType.getBestAgreementValue(), 
    			getRealAgreementValue(AgreementIdx, m_agentType));
    	
        double minPrecentDiff = 0;
        
        int totalPos = 0;
        for (int j = 0; j < possOp.length; j++)
        	if (possOp[j] == true)
        		totalPos +=1;
        
        // TODO: check only possible types
        for (int j = 0; j < possOp.length; j++)
        {
        	if (possOp[j] == false)
        	{
        		continue;
        	}
        	AutomatedAgentType opAgentType = agentTools.getCurrentTurnSideAgentType(m_sOpponentType, j);
        	double opValue = getRealAgreementValue(AgreementIdx, opAgentType);
        	
        	
            //double opPrecent = getPrec(opAgentType.getWorstAgreementValue(), opAgentType.getBestAgreementValue(), 
        	double opPrecent = getPrec(m_agentType.getWorstAgreementValue(), m_agentType.getBestAgreementValue(),
        			opValue);
            
            /*if ((precent - opPrecent) < minPrecentDiff)
            {
                minPrecentDiff = (precent - opPrecent);
            }*/
        	minPrecentDiff += (precent - opPrecent);
        }
        return minPrecentDiff/totalPos;
    }
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public ArielShiftanAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
    public ArielShiftanAgent() {
    	super();
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
		m_agentType = agentType;
		m_sOpponentType = sOpponentType;
		// initial values
		myPDiff = 70;
        hisPDiff = -100;
        
        learnMode = true;
        proposeBestFor = -1;
        agreedBestFor = -1;
        m_minDiff = 50;
        
        m_nextPossToCheck = 0;
        
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        lastRejected = new int[totalIssuesNum];
        lastAccepted = new int[totalIssuesNum];
        for (int i = 0; i < totalIssuesNum; i++)
        {
        	lastRejected[i] = lastAccepted[i] = AutomatedAgentType.NO_VALUE;
        }
        
        
        possOp = new boolean [AutomatedAgentsCore.AGENT_TYPES_NUM];
        lastAgree = true;
        for (int i = 0; i < AutomatedAgentsCore.AGENT_TYPES_NUM; i++)
        {
        	possOp[i] = true;
        }
        	
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
        
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
    	int totalIssuesNum = agentTools.getTotalIssues(m_agentType);
    	
    	boolean hasOpen = hasRealOpenIssues(totalIssuesNum, CurrentAgreementIdx, m_agentType);
    	
    	// if last turn and SQ is much bad - accept
    	if (hasOpen == false && agentTools.getCurrentTurn() == agentTools.getTurnsNumber() && 
    			getRealAgreementValue(CurrentAgreementIdx, m_agentType) > agentTools.getSQValue(m_agentType) * 100 + 100)
    		agentTools.acceptMessage(sOriginalMessage);
    	

    	// save the agrreement as last accepted
    	if (hasOpen == false) saveLastAccepted(CurrentAgreementIdx, false);
    	
    	// learn from offser if full one    	
    	if (nMessageType == AutomatedAgentMessages.OFFER || nMessageType == AutomatedAgentMessages.COUNTER_OFFER)
    	{
    		double hisNewPDiff = getPrecentDiff(CurrentAgreementIdx);
    		// already proposed something better for me - reject and "punish"
    		if (hisPDiff > hisNewPDiff)
    		{
    			if (hasOpen == false && /*minRejectedPrecentDiff != 100 &&*/ (hisPDiff - hisNewPDiff) > 2)
    			{
    				myPDiff += (hisPDiff - hisNewPDiff) + 5; 
    			}    			
    			agentTools.rejectMessage(sOriginalMessage);
    		}
    		// very close to my position - accept
    		else if (hisNewPDiff > myPDiff - 10)
    		{
    			if(hasOpen == false) 
    				{
    				//System.out.println("***Accepting:" + hisNewPDiff + "value:" + getRealAgreementValue(CurrentAgreementIdx, m_agentType) + ":" + agentTools.getAgreementValue(m_agentType, CurrentAgreementIdx, agentTools.getCurrentTurn()) + ":" + agentTools.getSQValue(m_agentType));
    				//System.out.println(agentTools.getCurrentTurn());
    				
    				}
    			/*for(int i = 0; i < CurrentAgreementIdx.length;i++)
    				System.out.print(CurrentAgreementIdx[i] + " ");
    			System.out.println("");
    			getRealAgreement(CurrentAgreementIdx, m_agentType);
    			for(int i = 0; i < CurrentAgreementIdx.length;i++)
    				System.out.print(CurrentAgreementIdx[i] + " ");
    			System.out.println("");*/
    			agentTools.acceptMessage(sOriginalMessage);
    		}
    		else
    		{
    			//ddd
    			myPDiff = Math.abs(hisNewPDiff);
    			//System.out.println("dsf");
    		}
    		if (hasOpen == false)
    			hisPDiff = hisNewPDiff;
			//agentTools.setSendOfferFlag(false);
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
		saveLastAccepted(CurrentAgreementIdx, true);
		if (nMessageType == AutomatedAgentMessages.OFFER || nMessageType == AutomatedAgentMessages.COUNTER_OFFER)
		{
			if (hasRealOpenIssues(agentTools.getTotalIssues(m_agentType), CurrentAgreementIdx, m_agentType) == true)
			{
				double hisNewPDiff = getPrecentDiff(CurrentAgreementIdx);
	    		// already proposed something better for me - reject and "punish"
	    		if (hisPDiff > hisNewPDiff)
	    		{
	    			if (/*minRejectedPrecentDiff != 100 &&*/ (hisPDiff - hisNewPDiff) > 2)
	    			{
	    				myPDiff += (hisPDiff - hisNewPDiff) + 5; 
	    			}    			
	    			agentTools.rejectMessage(sOriginalMessage);
	    		}
	    		// very close to my position - accept
	    		else if (hisNewPDiff > myPDiff - 10)
	    		{
	    			//agentTools.acceptMessage(sOriginalMessage);
	    		}
	    		else
	    		{
	    			//ddd
	    			myPDiff = Math.abs(hisNewPDiff);
	    			//System.out.println("dsf");
	    		}
	    		hisPDiff = hisNewPDiff;
			}
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
        /* @@ Received a message: opponent rejected the offer/promise/query/counter offer.
         * You can add logic if needed to update your agent
         */
		saveLastRejected(CurrentAgreementIdx);
		if (nMessageType == AutomatedAgentMessages.OFFER || nMessageType == AutomatedAgentMessages.COUNTER_OFFER)
		{
			double hisRejPD = getPrecentDiff(CurrentAgreementIdx);
			if (myPDiff >= hisRejPD - 2)
			{
				// get closer to other side
				myPDiff /= 2;
			}
				 
			//calculateOfferAgainstOpponent(m_agentType, m_sOpponentType, agentTools.getCurrentTurn());
		}
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
         * 
         * 
        /********************************
         * Start example code
         ********************************/
        // calculate Automated Agent offer
        double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        int SaveAIdx[] = new int[totalIssuesNum];
        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        
        /*if (nCurrentTurn > 6 && learnMode == true)
        {
        	learnMode = false;
        }*/
        
        if (learnMode == true)
        {
	        boolean offerSent = false;
	    	for (int i = 50; i >= 20; i-=10)
	    	{
	    		if (makeOfferForLearnMode(i))
	    		{
	    			m_minDiff = i;
	    			for (int j = 0; j < 5; j++)
	    			{
	    				incNextCheck();
	    				makeOfferForLearnMode(i);
	    			}
	            	offerSent = true;
	            	break;
	    		}
	    		
	    	}
	    	if (offerSent == false && true == sendMaxDiffForLearn(0))
	    	{
	    		for (int j = 1; j < 5; j++)
	    		{
	    			sendMaxDiffForLearn(j);
	    		}
	    	}
        }

        double maxMinPrecentDiff = -100; 
        
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            
            double minPrecentDiff = getPrecentDiff(CurrentAgreementIdx);
            
            if ((minPrecentDiff > maxMinPrecentDiff) && 
            		(minPrecentDiff <= (myPDiff + 1)))
            {
            	maxMinPrecentDiff = minPrecentDiff;
            //if (dOpponentLongTermAgreementValue > agentTools.getCurrentTurnAutomatedAgentValue())
                // you can save the values for later reference ($1)
            	double value = getRealAgreementValue(CurrentAgreementIdx, agentType);
                agentTools.setCurrentTurnAutomatedAgentValue(value);
                //agentTools.setCurrentTurnOpponentSelectedValue(agentType.getAgreementValue(OpponentLongTermIdx, nCurrentTurn));
                agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(CurrentAgreementIdx));
                for (int j = 0; j < totalIssuesNum; j++) SaveAIdx[j] = CurrentAgreementIdx[j];
            }

            getNextRealOpenIssuse(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues, false);

            
        } // end for - going over all possible agreements
        
        /*System.out.print("---> Current value:" + agentTools.getCurrentTurnAutomatedAgentValue());
        System.out.print(", worst:" + agentTools.getWorstAgreementValue(agentType));
        System.out.print(", best:" + agentTools.getBestAgreementValue(agentType));
        System.out.println(", myPdiff:" + myPDiff + ", hisPdiff" + hisPDiff + ", PDiff" + getPrecentDiff(SaveAIdx));*/

        
        
        // if going to propose something worse than opt out        
        double dOptOutValue = agentTools.getOptOutValue(agentType) * 100;
        if (dOptOutValue > agentTools.getCurrentTurnAutomatedAgentValue())
        {
        	/*System.out.println("Current Turn value:" + agentTools.getCurrentTurnAutomatedAgentValue());
        	System.out.println("OptOut Value:" + dOptOutValue);*/
        	agentTools.optOut();
        }
        
        String sOffer = agentTools.getSelectedOffer();
    	//System.out.println("***Sending offer3:"+ agentTools.getCurrentTurnAutomatedAgentValue());
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

        // worst agreement is the one agreed so far
        /*int previousAcceptedAgreementsIndices[] = new int[AutomatedAgentType.MAX_ISSUES];
        previousAcceptedAgreementsIndices = agentTools.agent.getPreviousAcceptedAgreementsIndices();
        double dAcceptedAgreementValue = agentTools.getAgreementValue(agentType, previousAcceptedAgreementsIndices, nCurrentTurn);
        agentTools.setWorstAgreementValue(agentType, dAcceptedAgreementValue);
        agentTools.setWorstAgreementIndices(agentType, previousAcceptedAgreementsIndices);*/
        
        
        //To obtain infromation from the utility you can use getters from the AgentType class
        //@@EXample@@
        //Get the value of the Status Quo and Opting-Out values as time increases
        double dAgreementTimeEffect = agentTools.getAgreementTimeEffect(agentType); 
        double dStatusQuoValue = agentTools.getSQValue(agentType) * 100;
        double dOptOutValue = agentTools.getOptOutValue(agentType) * 100;
        double max = Math.max(dOptOutValue, dStatusQuoValue);
        double min = Math.min(dOptOutValue, dStatusQuoValue);
        if (max > agentTools.getBestAgreementValue(agentType))
        {
            agentTools.setBestAgreementValue(agentType, max);
        }                       
        if (min < agentType.getWorstAgreementValue())
        {
            agentTools.setWorstAgreementValue(agentType, min);
        }
        
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremeneted
            dAgreementValue = getRealAgreementValue(CurrentAgreementIdx, agentType);
            
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

        	myGetNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        
        /********************************
         * End example code
         ********************************/             
    }
}
