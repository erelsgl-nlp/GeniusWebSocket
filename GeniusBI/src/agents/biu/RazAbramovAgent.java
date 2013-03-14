package agents.biu;

/**
 * @author Raz Abramov, ID 038115689
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class RazAbramovAgent extends OldAgentAdapter{
    AutomatedAgentType myType;
    
    int allAgreements[][];
    double allAgreementsValues[];
    
    int currentAgentPriorities[];
    boolean finish_stage = false;
    
    int finishStageAgreement[];
    public RazAbramovAgent() {
    	super();
    }

    int starting_val  = 0;
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public RazAbramovAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) 
	{   
		finishStageAgreement = new int[agentTools.getTotalIssues(agentType)];
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        allAgreements = new int[totalAgreementsNumber][totalIssuesNum];
        allAgreementsValues = new double[totalAgreementsNumber];
        
        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        double dAutomatedAgentAgreementValue;
        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        
        ///calc and store all agreements.
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, 1);

            allAgreementsValues[i] = dAutomatedAgentAgreementValue;
            // save the indices of that offer
            for (int j = 0; j < totalIssuesNum; ++j) {
            	allAgreements[i][j] =  CurrentAgreementIdx[j];	
            }

            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements

        //sort all agreements by their value
        sort_agreements_by_value(agentType);
        
        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
        
        
	}
    
	private void sort_agreements_by_value(AutomatedAgentType agentType) {
	    int n = agentTools.getTotalAgreements(agentType);
	    for (int pass=1; pass < n; pass++) {  // count how many times
	        // This next loop becomes shorter and shorter
	        for (int i=0; i < n-pass; i++) {
	            if (this.allAgreementsValues[i] > this.allAgreementsValues[i+1]) {
	                // exchange elements
	            	double temp1 = allAgreementsValues[i];  
	            	allAgreementsValues[i] = allAgreementsValues[i+1];  
	            	allAgreementsValues[i+1] = temp1;
	                int temp2[] = allAgreements[i];  
	                allAgreements[i] = allAgreements[i+1];  
	                allAgreements[i+1] = temp2;
	                
	            }
	        }
	    }
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
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) 
    {

    	//if last turn, and the offer made is not the worst offer possible, accept.
    	if (agentTools.getCurrentTurn() == agentTools.getTurnsNumber()-1)
    	{
    		if (agentTools.getAgreementValue(CurrentAgreementIdx) > agentTools.getWorstAgreementValue(myType))
    		{
    			agentTools.acceptMessage(sOriginalMessage);
    		}
    	}
    	
    	//basically, accept ONLY if the offer made is better than the one we want to ask... in all other cases acceptance is on the other side.
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
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            complete_agreement();
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
        }
        else
        {
            // reject offer
            agentTools.rejectMessage(sOriginalMessage);
            
          
            agentTools.setSendOfferFlag(true);
            
        }
    }
     
    /* called to complete a partial agreement, if agreed*/
    private void complete_agreement()
    {
    	finish_stage = true;
    	int agr[] = new int[agentTools.getTotalIssues(myType)];
    	agr = agentTools.getAcceptedAgreementIdx();
    	if (agr[0] == 0)
    	{
    		agr[0] = 2;
    	}
    	if (agr[1] == 0)
    	{
    		agr[1] = 2;
    	}
    	if (agr[2] == 0)
    	{
    		agr[2] = 2;
    	}
    	if (agr[3] == 0)
    	{
    		agr[3] = 1;
    	}
    	if (agr[4] == 0)
    	{
    		agr[4] = 2;
    	}
    	if (agr[5] == 0)
    	{
    		agr[5] = 2;
    	}
    	for (int i = 0;i<agentTools.getTotalIssues(myType);i++)
    	{
    		this.finishStageAgreement[i] = agr[i];
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
        /* don't care for comments 
         */
    }

	/**
	 * called whenever we get a threat from the opponent
     * You can add logic to update your agent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {
        /* I will not be intimidated 
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
        /* the only offers I make are complete offers, so I don't worry
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
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) 
    {
    	
    	if (this.finish_stage)
    	{
    		agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(this.finishStageAgreement));
    		String sOffer = agentTools.getSelectedOffer();
            agentTools.sendOffer(sOffer);
            return;
    	}
    	this.starting_val = agentTools.getTotalAgreements(agentType);
    	
    	//if I'm the employer, my utility increases while time advances. meaning I'll start by offering the more attractive proposals, and as
    	//time passes, offer less and less attractive ones (more profit for me)
    	if (agentTools.getAgentSide().contains("SIDE_A"))
    	{
    		
    		try
    		{
    			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(allAgreements[(int)starting_val-(int)(agentTools.getTurnsNumber()*agentTools.getAgreementTimeEffect(agentType))-(int)(nCurrentTurn*agentTools.getAgreementTimeEffect(agentType))]));
    		}
    		catch (Exception ex)
    		{
    			//if reached maximum in array
    			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(allAgreements[agentTools.getTotalAgreements(agentType)-2]));
    		}
    	}
    	//if i'm the employee - opposite.
    	else
    	{
    	
    		try
    		{
    			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(allAgreements[(int)starting_val-(int)(nCurrentTurn*agentTools.getAgreementTimeEffect(agentType))]));
    		}
    		catch (Exception ex)
    		{
    			//if reached maximum in array
    			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(allAgreements[0]));
    		}
    	}
    	
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

    	myType = agentType;
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
