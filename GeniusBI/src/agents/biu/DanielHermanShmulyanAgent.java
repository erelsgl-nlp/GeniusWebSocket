package agents.biu;

import java.util.ArrayList;

/**
 * @author Daniel Herman Shmulyan, ID 305855348
 * @version 1.16
 */ 
public class DanielHermanShmulyanAgent extends OldAgentAdapter{
    
    private enum NegotiatorType { EMPLOYER, CANDIDATE}; 
    private NegotiatorType whoAmI;
    private String         whoIsAgainstMe;
    private AutomatedAgentType whatIsMyType;
    
    private enum NegotiationState { OPTOUT, STA_QUO, NEGOTIATION, ANGRY, DEFECT};
    private NegotiationState myNegState;
    
    private enum OppUtil { UNKNOWN, PROBABLY_COMPROMISE, PROBABLY_SHORT_TERM, PROBABLY_LONG_TERM,COMPROMISE,SHORT_TERM,LONG_TERM};
    private OppUtil whatIsOppUtil;
    
    // Define the range of the desired utilities in which I negotiate.
    private double myMaxDesiredUtility;
    private double myMinDesiredUtility;
    
    private ArrayList<String> parretoOptimal;
    
    // ---------------------------------------------------------------
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public DanielHermanShmulyanAgent() {
    	super();
    }
    public DanielHermanShmulyanAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
        
    }
    
    // ---------------------------------------------------------------
	/**
	 * Called before the the negotiation starts.
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		
		// Define who am I.
        String side = this.agentTools.getAgentSide();
        if (side.equals(AutomatedAgent.SIDE_B_NAME)){
           this.whoAmI= DanielHermanShmulyanAgent.NegotiatorType.CANDIDATE;           
        }
        else{
           this.whoAmI= DanielHermanShmulyanAgent.NegotiatorType.EMPLOYER; 
        }

        // Initialize other private states
        myNegState          = DanielHermanShmulyanAgent.NegotiationState.NEGOTIATION;
        whatIsOppUtil       = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
        whatIsMyType        = agentType;
        whoIsAgainstMe      = sOpponentType;
        myMaxDesiredUtility = AutomatedAgentType.VERY_SMALL_NUMBER; 
        myMinDesiredUtility = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        //printUtilityStructure(agentType);
        
        // calculate Automated Agent first offer
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);        
    }
    
    // ---------------------------------------------------------------    
    /**
     * Apply built in function used for probability calculations.
     * @param num - double in the range [0..1]
     */
    private double innerProbability(double num){
    	switch(myNegState){
    	case ANGRY:
    		return 0.4 + Math.pow(num,2)*0.6;
    	case DEFECT:
    		return Math.sqrt(num);
    	default:
    		return 0.3 + Math.pow(num,2)*0.7;
    	}    	    
    }
        
    // ---------------------------------------------------------------
    /** 
     * Decides how to respond to an incoming message.
     * @param nMessageType - the message type
     * @param CurrentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     */
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
    	
    	// Check if I am negotiating
    	if (!amI_Negotiating()){
    	   agentTools.rejectMessage(sOriginalMessage);
    	   return;
    	}
    	
    	// Try to learn the utility function of the opponent.
    	updateOppTypeKnowledge(guessWhichTypeSentMeTheAgreement(CurrentAgreementIdx));
    	//this.calculateMyDesiredUtility(me, nCurrentTurn);
    	
    	// decide whether to accept the message or reject it:
        double opOffer_MyVal;
               opOffer_MyVal     = agentTools.getAgreementValue(CurrentAgreementIdx);
               
        // Calculate offers quality. Quality < 0 means bad
        // Quality > 1 means outstanding and [0..1] means in negotiation.
       double offerQuality = (opOffer_MyVal      -myMinDesiredUtility)/
                             (myMaxDesiredUtility-myMinDesiredUtility);

       /*System.err.println("--------------->");
       System.err.println("offerQuality: "+ offerQuality);
       System.err.println("opOffer_MyVal: "+ opOffer_MyVal);
       System.err.println("myMaxDesiredUtility: "+ myMaxDesiredUtility);
       System.err.println("myMinDesiredUtility: "+ myMinDesiredUtility);
       System.err.println("--------------->");*/
       
        if (offerQuality < 0.1){
            agentTools.rejectMessage(sOriginalMessage);
            this.agentTools.sendComment("Dont ever propose me an offer that is so bad!");
            return;
        }     
        
        if (offerQuality >= 1){
           agentTools.acceptMessage(sOriginalMessage);
           this.agentTools.sendComment("Nice, goof offer");
           agentTools.setSendOfferFlag(false);
           return;
        }
        
        // The offer is in the desired range. Accept it randomly.
        // But the higher is the offer the greater is the probability
        // to accept it.
        double randNum = Math.random();
        
        // Resolve the type of message
    	switch(nMessageType){
    	case(AutomatedAgentMessages.QUERY):
    		// If it is query then I am more willing to accept it.
    		break;
    	case(AutomatedAgentMessages.OFFER):
			randNum = innerProbability(randNum);
			break;
    	case(AutomatedAgentMessages.COUNTER_OFFER):
			randNum = innerProbability(randNum);
			break;
    	case(AutomatedAgentMessages.PROMISE):
			randNum = innerProbability(randNum);
			break;
    	}
        /*System.err.println("randNum: "+ randNum);
        System.err.println("offerQuality: "+ offerQuality);*/
      
        if ( randNum < offerQuality){
            agentTools.acceptMessage(sOriginalMessage);      	
        }
        else {
            agentTools.rejectMessage(sOriginalMessage);        	
        }

        /* In future can use the following code:       
        // Find the previous accepted agreement
        double acceptedAgreement_MyVal = agentTools.getAcceptedAgreementsValue(); 
        // Find the offer I have intentions to make
        agentTools.calculateNextTurnOffer();
        myNextOffer_MyVal = agentTools.getNextTurnOfferValue();
        
        if (acceptedAgreement_MyVal >= opOffer_MyVal){
            agentTools.rejectMessage(sOriginalMessage);
            this.agentTools.sendComment("Dont propose me an offer that is worst then you already proposed");
            return;
        }     
        if (opOffer_MyVal >= myNextOffer_MyVal){
           agentTools.acceptMessage(sOriginalMessage);
           this.agentTools.sendComment("Nice, you offered more then I thought to offer: "+Double.toString(myNextOffer_MyVal));
           agentTools.acceptMessage(sOriginalMessage);
           agentTools.setSendOfferFlag(false);
        }
        else{
            this.agentTools.sendComment("Unsatisfying offer. I will now ask more");
//            agentTools.rejectMessage(sOriginalMessage);
            agentTools.acceptMessage(sOriginalMessage);
        }       */
    }
        
    // ---------------------------------------------------------------
    /**
	 * called whenever we get a comment from the opponent
     * @param sComment -the received comment
	 */
	public void commentReceived(String sComment) {
        //this.agentTools.sendComment("I cannot understand text, yet");
    }

    // ---------------------------------------------------------------
	/**
	 * called whenever we get a threat from the opponent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {
        //this.agentTools.sendComment("I dont like threats, Dont use this kind of argument in negotioation");
        this.myNegState = DanielHermanShmulyanAgent.NegotiationState.ANGRY;
    }
	
    // ---------------------------------------------------------------
	/**
	 * called whenever the opponent agreed to one of your massages (promise, query, offer or counter offer).
     * NOTE: if an OFFER is accepted, it is saved in the appropriate structure. No need to add logic for this.
	 * @param nMessageType - the type of massage the opponent agreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		
    	// Check if I am negotiating
    	if (!amI_Negotiating()){
    	   return;
    	}
    	
    	// Try to learn the utility function of the opponent.
    	updateOppTypeKnowledge(guessWhichTypeSentMeTheAgreement(CurrentAgreementIdx));

    	// I send only full agreements so if opponent agreed it must be a query.		
    }
	
    // ---------------------------------------------------------------
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the opponent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {

		// Check if I am negotiating
    	if (!amI_Negotiating()){
    	   return;
    	}
    	 
    	if (myNegState == DanielHermanShmulyanAgent.NegotiationState.ANGRY){
    	   // I was angry and that is not leading us anywhere. Opponent rejects.
    	   // Maybe I should calm down and become more talkative.
    	   this.myNegState = DanielHermanShmulyanAgent.NegotiationState.NEGOTIATION;	
    	}    	
    }   
 
    // ---------------------------------------------------------------   
    /**
     * This method is always called when beginning a new turn
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn){

    	// Check if I am negotiating
    	if (!amI_Negotiating()){
    	   return;
    	}
    	
    	AutomatedAgentType opAgent = null;
    	if (iKnowOpptUtilForSure()){
    		opAgent= agentTools.getCurrentTurnSideAgentType(sOpponentType, OppUtil2AgentCore());
    		if (!sendOfferToOpponent(agentType, opAgent,nCurrentTurn)){
    			// Could not send offer to a known opponent. negotiation cannot
    			// be proceeded.
           	   stopNegotioation(agentType);
               return;
            }
    	}
    	else{
    		// I don't know who is against me. Send a good query to each type of opponent.
    		// Also send a bad agreement to each opponent (it is good for me). 
    		
    		// Send to first type.
    		opAgent = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
    		sendOfferToOpponent(agentType, opAgent,nCurrentTurn);

    		// Send to second type.
    		opAgent = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
    		sendOfferToOpponent(agentType, opAgent,nCurrentTurn);
            
            // Send to third type.
            opAgent = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
            sendOfferToOpponent(agentType, opAgent,nCurrentTurn);
    	}
    		
    	// ToDo: Improve so desired utility is not taken always as opponent
    	// is short term when I don't know who is opponent.
    	this.calculateMyDesiredUtility(agentType, nCurrentTurn);
        // In Future, can use the following commands
        // if ( Val(myBest) > agentTools.getCurrentTurnAutomatedAgentValue())
        // agentTools.setCurrentTurnAutomatedAgentValue(agg_MyVal);
        // agentTools.setCurrentTurnOpponentSelectedValue(agentOpponentCompromise.getAgreementValue(agg_oppCmIdx, nCurrentTurn));
    	// if (agentTools.getSelectedOfferValue() > agentTools.getAcceptedAgreementsValue()){
        //     String sOffer = agentTools.getSelectedOffer();
        //      agentTools.sendOffer(sOffer);
    	// else ??
        // Check this turn offer against already agreed offers.
    }
    // ---------------------------------------------------------------   
    /**
     * This method is always called from calculateOfferAgainstOpponent()
     * @param me - the automated agent's type
     * @param op - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    private boolean sendOfferToOpponent(AutomatedAgentType me, 
    									     AutomatedAgentType op, int nCurrentTurn){
        calculateParetoAgg(me, op,nCurrentTurn);           
        if (!parretoOptimal.isEmpty()){
         	
            // Send a good offer for me.
        	String myGoodAgreement = getPrettyGoodPareto(me,nCurrentTurn);
            agentTools.sendOffer(myGoodAgreement);
            String opBest = getPrettyGoodPareto(op,nCurrentTurn);
            
            // Check if I am sure who is my opponent
        	if (iKnowOpptUtilForSure()){
        	    // Store the current turn offer. 	
        		agentTools.setCurrentTurnAgreementString(myGoodAgreement);
        	}
        	else{
        		// Send a query in order to get a clue about opponents utility
        		// function.            	
                agentTools.sendQuery(me.getAgreementIndices(opBest));        		
        	}
            
//				Debug probabilities using this code. 
/*            int totalIssuesNum = agentTools.getTotalIssues(me);   	
            int    cur_aggIdx[] = new int[totalIssuesNum];
            cur_aggIdx = me.getAgreementIndices(myGoodAgreement);
            double par_MyVal = agentTools.getAgreementValue(me, cur_aggIdx, nCurrentTurn);
            double par_OpVal = agentTools.getAgreementValue(op, cur_aggIdx, nCurrentTurn);
            
            // Print my and opponents utility ranges 
            calculateMyDesiredUtility(me, nCurrentTurn);
            System.err.println("*****Ranges***********");
            System.err.println("myRange:["+myMaxDesiredUtility+","+myMinDesiredUtility+"]");
            calculateMyDesiredUtility(op, nCurrentTurn);
            System.err.println("opRange:["+myMaxDesiredUtility+","+myMinDesiredUtility+"]");
            
            // Print offers utilities
            System.err.println("    Offer");
            System.err.println("par_MyVal: "+par_MyVal+" ^^^"+"  par_OpVal:"+par_OpVal);
            cur_aggIdx = me.getAgreementIndices(opBest);
            par_MyVal = agentTools.getAgreementValue(me, cur_aggIdx, nCurrentTurn);
            par_OpVal = agentTools.getAgreementValue(op, cur_aggIdx, nCurrentTurn);
            //System.err.println("*****Query***********");
            //System.err.println("par_MyVal: "+par_MyVal+" ^^^"+"  par_OpVal:"+par_OpVal);*/
            return true;            
        }
        // parreto optimal is empty - Could not send anything.
        return false;   
    }
    
    // ---------------------------------------------------------------    
    /**
     * Calculate and store all the pareto optimal agreements.
     * A small worst portion of pareto agreements is thrown out.
     * @param me - my automated agent's type
     * @param op - opponents automated agent's type
     * @param nCurrentTurn - the current turn
     */
    private void calculateParetoAgg(AutomatedAgentType me, AutomatedAgentType op, int nCurrentTurn){
    	// Read general information
    	// ---------------------------------    	
    	int totalIssuesNum = agentTools.getTotalIssues(me);   	
        int totalAgreementsNumber = agentTools.getTotalAgreements(me);
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        // Initialize variables for offer and pareto offer
        // ---------------------------------
        double agg_MyVal    = AutomatedAgentType.VERY_SMALL_NUMBER;
        double agg_OpVal    = AutomatedAgentType.VERY_SMALL_NUMBER;
        int    cur_aggIdx[] = new int[totalIssuesNum];
        
        double par_MyVal    = AutomatedAgentType.VERY_SMALL_NUMBER;
        double par_OpVal    = AutomatedAgentType.VERY_SMALL_NUMBER;
        int    par_aggIdx[] = new int[totalIssuesNum];

        
        // Initialize the basic zero agreement and maxValues.
        // ---------------------------------
        for (int i = 0; i < totalIssuesNum; ++i){
        	cur_aggIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(me, i);
        }        
        
        // Iterate over all the possible agreements. totalAgreementsNumber = Pi(MaxIssueValues).
        // Create offer against my opponent and store only the 
        // complete and effective pareto optimal agreements.
        // ---------------------------------
        
        this.parretoOptimal = new ArrayList<String>();       
        
        for (int i = 0; i < totalAgreementsNumber; ++i){
        	
        	if (isCompleteOffer(me, cur_aggIdx,totalIssuesNum)){
            	agg_MyVal = agentTools.getAgreementValue(me, cur_aggIdx, nCurrentTurn);
            	agg_OpVal = agentTools.getAgreementValue(op, cur_aggIdx, nCurrentTurn);
            	
            	
               	if ((agg_MyVal <= me.getOptOutValue())||(agg_MyVal <= me.getSQValue()) 
                  ||(agg_OpVal <= op.getOptOutValue())||(agg_OpVal <= op.getSQValue())) {
               	   // This is a bad agreement, and nobody would acceept it anyway. 
            	   }
               	else {
               	    // Thats might be a good agreement. Check if it is pareto.
               		boolean isCurParreto = true;
                 	for (int j = 0;j < parretoOptimal.size();j++){
               			
                      par_aggIdx = me.getAgreementIndices((String)this.parretoOptimal.get(j));
                      par_MyVal = agentTools.getAgreementValue(me, par_aggIdx, nCurrentTurn);
                      par_OpVal = agentTools.getAgreementValue(op, par_aggIdx, nCurrentTurn);

                      if ((agg_MyVal >= par_MyVal)&&(agg_OpVal >= par_OpVal)){
                    	 if ((agg_MyVal == par_MyVal)&&(agg_OpVal == par_OpVal)){
                             // Current agreement is exactly as previous pareto.
                    		 // Don't change anything.                    	 
                    	 }
                    	 else{
                             // Current agreement is better then a previous pareto.
                             // Delete the pareto, and insert current instead.
                             parretoOptimal.remove(j);
                             j--;
                             isCurParreto = true;
                    	 }
                      }
                      else{
                    	  isCurParreto = isCurParreto&&((agg_MyVal > par_MyVal)||(agg_OpVal > par_OpVal));
                      }
               		}  // For
               		if ((isCurParreto)||parretoOptimal.isEmpty()){
               			// We have found a new pareto.
               			parretoOptimal.add(me.getAgreementStr(cur_aggIdx));
               		}
               	}
               	
            }    // if (isCompleteOffer(cur_aggIdx,totalIssuesNum))   	
            agentTools.getNextAgreement(totalIssuesNum, cur_aggIdx, MaxIssueValues);
        } // for (int i = 0; i < totalAgreementsNumber; ++i)
        
        // ------------------------------
        // Remove 10% Of worst agreements to me and to my opponent, assuming
        // that we will not agree on them anyway.
                
        double myBound;
        double opBound;
        
        // Find the 10% opponent bound.
        this.calculateMyDesiredUtility(op, nCurrentTurn);
        opBound = myMaxDesiredUtility*0.1 + myMinDesiredUtility*0.9;
        
        // Find the 10% my bound.
        this.calculateMyDesiredUtility(me, nCurrentTurn);
        myBound = myMaxDesiredUtility*0.1 + myMinDesiredUtility*0.9;        
        
        
        // Remove the worst agreements.
        for (int j = 0; j < parretoOptimal.size(); ++j) {
            par_aggIdx = me.getAgreementIndices((String)this.parretoOptimal.get(j));
            par_MyVal = agentTools.getAgreementValue(me, par_aggIdx, nCurrentTurn);
            par_OpVal = agentTools.getAgreementValue(op, par_aggIdx, nCurrentTurn);

            if ((par_MyVal < myBound)|| (par_OpVal < opBound)){
               // Bad agreement to one of the sides. Remove it.
                parretoOptimal.remove(j);
                j--;            
            }
        }
        
        // Print the agreements.
        /*for (int j = 0; j < parretoOptimal.size(); ++j) {
            par_aggIdx = me.getAgreementIndices((String)this.parretoOptimal.get(j));
            par_MyVal = agentTools.getAgreementValue(me, par_aggIdx, nCurrentTurn);
            par_OpVal = agentTools.getAgreementValue(op, par_aggIdx, nCurrentTurn);
            System.err.println("par_myval:"+ Math.round(par_MyVal*100)+" par_OpVal:"+Math.round(par_OpVal*100));        
        }
        System.err.println("Par length: "+ parretoOptimal.size());*/
//        System.err.println("opBound: "+ opBound);
  //      System.err.println("myBound: "+ myBound);

    }
    // ---------------------------------------------------------------    
    /**
     * Calculate the range of utility in which I will negotiate.
     * @param me - My automated agent's type
     * @param nCurrentTurn - the current turn
     */
    private void calculateMyDesiredUtility(AutomatedAgentType me, int nCurrentTurn){
    	int totalIssuesNum = agentTools.getTotalIssues(me);   	
        int    par_aggIdx[] = new int[totalIssuesNum];
        
        par_aggIdx =  me.getAgreementIndices(getBestPareto(me, nCurrentTurn));       
        myMaxDesiredUtility = agentTools.getAgreementValue(me, par_aggIdx, nCurrentTurn);
        par_aggIdx = me.getAgreementIndices(getWorstPareto(me, nCurrentTurn));       
        myMinDesiredUtility = agentTools.getAgreementValue(me, par_aggIdx, nCurrentTurn);
        
    }
    // ---------------------------------------------------------------    
    /**
     * Find the best agreement for the agent, from the pareto list.
     * @param agent - The automated agent's type
     * @param nCurrentTurn - the current turn
     */
    private String getBestPareto(AutomatedAgentType agent, int nCurrentTurn){
        double par_MyVal = AutomatedAgentType.VERY_SMALL_NUMBER;
        double max   	 = AutomatedAgentType.VERY_SMALL_NUMBER;
        int    maxJ      = 0;
        int    par_aggIdx[] = new int[agentTools.getTotalIssues(agent)];

        
    	for (int j = 0; j < parretoOptimal.size(); ++j) {
            par_aggIdx = agent.getAgreementIndices((String)this.parretoOptimal.get(j));
            par_MyVal = agentTools.getAgreementValue(agent, par_aggIdx, nCurrentTurn);
            maxJ = (max>par_MyVal)?maxJ:j;
            max  = (max>par_MyVal)?max:par_MyVal;            	
    	}
    	//System.err.println(maxJ);
    	return (String)this.parretoOptimal.get(maxJ);
    }
        
    // ---------------------------------------------------------------    
    /**
     * Find not necessary the best agreement for the agent, but a pretty good one, 
     * from the pareto list.
     * @param agent - The automated agent's type
     * @param nCurrentTurn - the current turn
     */
    private String getPrettyGoodPareto(AutomatedAgentType agent, int nCurrentTurn){
    	
    	// Calculate the desired range of utilities for the agent.
    	calculateMyDesiredUtility(agent, nCurrentTurn);

        int    parSize   = parretoOptimal.size();
        int    par_aggIdx[] = new int[agentTools.getTotalIssues(agent)];
        double par_MyVal = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Define what is a good pareto.
        //double randWeight = Math.sqrt(Math.random());
        double randWeight = innerProbability(Math.random());
        double good       = myMaxDesiredUtility * (randWeight) + 
                            myMinDesiredUtility * (1-randWeight);
        
        // Search a good agreement from random place in order not to choose each
        // time the same agreement.
        if (parSize <= 1){
           return getBestPareto(agent, nCurrentTurn);	
        }
        int j = (int)Math.round(Math.random()*(parSize-1));

    	while (par_MyVal < good){
    		
    		// Advance j by 1.
    		j = (j+1)%parSize;
    		
    		// Extract the agreement.
            par_aggIdx = agent.getAgreementIndices((String)this.parretoOptimal.get(j));
            par_MyVal  = agentTools.getAgreementValue(agent, par_aggIdx, nCurrentTurn);                        
                       	
    	}
    	// Return a good pareto.
    	return (String)this.parretoOptimal.get(j);
    }

    // ---------------------------------------------------------------    
    /**
     * Find the worst agreement for the agent, from the pareto list.
     * @param agent - The automated agent's type
     * @param nCurrentTurn - the current turn
     */
    private String getWorstPareto(AutomatedAgentType agent, int nCurrentTurn){
        double par_MyVal = AutomatedAgentType.VERY_SMALL_NUMBER;
        double min   	 = AutomatedAgentType.VERY_HIGH_NUMBER;
        int    minJ      = 0;
        int    par_aggIdx[] = new int[agentTools.getTotalIssues(agent)];

        
    	for (int j = 0; j < parretoOptimal.size(); ++j) {
            par_aggIdx = agent.getAgreementIndices((String)this.parretoOptimal.get(j));
            par_MyVal = agentTools.getAgreementValue(agent, par_aggIdx, nCurrentTurn);
            minJ = (min<par_MyVal)?minJ:j;
            min  = (min<par_MyVal)?min:par_MyVal;            	
    	}
    	return (String)this.parretoOptimal.get(minJ);
    }
    
    // ---------------------------------------------------------------    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {

        // initialization - DO NOT CHANGE
     	// ---------------------------------
        int nIssuesNum = agentTools.getTotalIssues(agentType);       
        int cur_aggIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
        for (int i = 0; i < nIssuesNum; ++i){
        	cur_aggIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }

        // Get Info
        // ---------------------------------
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);               
        
        
        // Initialize the best and the worst agreements for me.
        // ---------------------------------
        double agg_MyVal = 0;
        for (int i = 0; i < totalAgreementsNumber; ++i){
        	agg_MyVal = agentTools.getAgreementValue(agentType, cur_aggIdx, nCurrentTurn);
            
            // check for best agreement
            if (agg_MyVal > agentTools.getBestAgreementValue(agentType)){
               agentTools.setBestAgreementValue(agentType, agg_MyVal);
               agentTools.setBestAgreementIndices(agentType, cur_aggIdx);
            }                       
                        
            if (agg_MyVal < agentType.getWorstAgreementValue()){
               agentTools.setWorstAgreementValue(agentType, agg_MyVal);
               agentTools.setWorstAgreementIndices(agentType, cur_aggIdx);
            }                       

            agentTools.getNextAgreement(nIssuesNum, cur_aggIdx, MaxIssueValues);// get the next agreement indices
        } 
    }
    
    // ---------------------------------------------------------------    
    /**
     * Decides whether the input offer is complete (all the issues are set).
     * @param me - my agentType 
     * @param aggIdx - agreement indices
     * @param totalIssuesNum - total number of issues
     */
    private boolean isCompleteOffer(AutomatedAgentType me, int aggIdx[], int totalIssuesNum){
        for (int i = 0; i < totalIssuesNum; ++i) {
        	if (aggIdx[i] == AutomatedAgentType.NO_VALUE){
        	   return false;
        	}    
        	if (me.isIssueValueNoAgreement(i, aggIdx[i])){
         	   return false;        		
        	}
        }                
        return true;
    }
    // ---------------------------------------------------------------    
    /**
     * This method is called when agent doesn't want to negotiate anymore.
     * @param agentType - the automated agent's type
     */
    private void stopNegotioation(AutomatedAgentType agentType){
        // No agreement is worthy for keeping negotiation.
        double optOutVal = agentTools.getOptOutValue(agentType);
        double SQVal   	 = agentTools.getSQValue(agentType);

        if (optOutVal >= SQVal){
            this.myNegState = DanielHermanShmulyanAgent.NegotiationState.OPTOUT;
            this.agentTools.sendComment("Could not find a worthy agreement. Quitting");
            this.agentTools.optOut();
        }
        else{
            this.myNegState = DanielHermanShmulyanAgent.NegotiationState.STA_QUO;
            this.agentTools.sendComment("Could not find a worthy agreement. Waiting for status Quo");
        }
    }
    // ---------------------------------------------------------------    
    /**
     * This method checks whether agents want to negotiate.
     */
    private boolean amI_Negotiating(){
    	if (this.myNegState == DanielHermanShmulyanAgent.NegotiationState.OPTOUT){
    		return false;
    	}
    	if (this.myNegState == DanielHermanShmulyanAgent.NegotiationState.STA_QUO){
    		return false;
    	}
    	return true;    		
    }
    
    
    // ---------------------------------------------------------------    
    /**
     * This method checks whether i know the utility function of the opponent.
     */
    private boolean iKnowOpptUtilForSure(){
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.COMPROMISE)
    		return true; 
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.SHORT_TERM)
    		return true; 
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.LONG_TERM)
    		return true;

    	return false;
    }
    // ---------------------------------------------------------------    
    /**
     * This method checks whether have a clue about the utility function of the opponent.
     */
    private boolean iHaveClueForOpptUtil(){
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.PROBABLY_COMPROMISE)
    		return true; 
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.PROBABLY_SHORT_TERM)
    		return true; 
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.PROBABLY_LONG_TERM)
    		return true;

    	return false;
    }
    // ---------------------------------------------------------------    
    /**
     * This method checks whether i don't know the utility function of the opponent.
     */
    private boolean iDontKnowOpptUtilAtAll(){
    	if (whatIsOppUtil == DanielHermanShmulyanAgent.OppUtil.UNKNOWN)
    		return true;
    	
    	return false;
    }
    
    // ---------------------------------------------------------------    
    /**
     * This Converts OppUtill enumerator to AgentsCore constants.
     */
    private int OppUtil2AgentCore(){
    	switch(whatIsOppUtil){
    	case UNKNOWN			:   return -1;
    	case PROBABLY_COMPROMISE:   return AutomatedAgentsCore.COMPROMISE_TYPE_IDX;
    	case PROBABLY_SHORT_TERM:   return AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;
    	case PROBABLY_LONG_TERM	:	return AutomatedAgentsCore.LONG_TERM_TYPE_IDX;
    	case COMPROMISE			:   return AutomatedAgentsCore.COMPROMISE_TYPE_IDX;
    	case SHORT_TERM			:	return AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;
    	case LONG_TERM			:	return AutomatedAgentsCore.LONG_TERM_TYPE_IDX;
    	default					: 	return -2;
    	}
    	
    }
    // ---------------------------------------------------------------
    /**
     * This method tries to guess the type of the opponent using an example
     * of agreement that he sent me.
     * @param aggIdx - agreement indices
     */
    private DanielHermanShmulyanAgent.OppUtil  guessWhichTypeSentMeTheAgreement(int aggIdx[]){
    	
        // Initialize the different possible agents for the opponent side
        // ---------------------------------
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;
        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(this.whoIsAgainstMe, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm   = agentTools.getCurrentTurnSideAgentType(this.whoIsAgainstMe, AutomatedAgentsCore.LONG_TERM_TYPE_IDX );
        agentOpponentShortTerm  = agentTools.getCurrentTurnSideAgentType(this.whoIsAgainstMe, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                

        double agg_oppCmVal   = AutomatedAgentType.VERY_SMALL_NUMBER;
        double agg_oppLtVal = AutomatedAgentType.VERY_SMALL_NUMBER;
        double agg_oppStVal = AutomatedAgentType.VERY_SMALL_NUMBER;

        // Calculate utility of each one from that agreement.
        
        agg_oppCmVal = agentTools.getAgreementValue(agentOpponentCompromise, aggIdx, this.agentTools.getCurrentTurn());
    	agg_oppLtVal = agentTools.getAgreementValue(agentOpponentLongTerm  , aggIdx, this.agentTools.getCurrentTurn());
      	agg_oppStVal = agentTools.getAgreementValue(agentOpponentShortTerm , aggIdx, this.agentTools.getCurrentTurn());
        
    	// Check who is the maximum and return it.
      	if      	(agg_oppCmVal>agg_oppLtVal){
      		if		(agg_oppCmVal>agg_oppStVal){
      			return DanielHermanShmulyanAgent.OppUtil.COMPROMISE;
      		}
      		else if (agg_oppCmVal==agg_oppStVal){
      			return DanielHermanShmulyanAgent.OppUtil.UNKNOWN;      			
      		}
      		else{
      			return DanielHermanShmulyanAgent.OppUtil.SHORT_TERM;
      		}
      	}
      	else if     (agg_oppCmVal==agg_oppLtVal){
      			if	(agg_oppCmVal>=agg_oppStVal){
      				return DanielHermanShmulyanAgent.OppUtil.UNKNOWN;	
      			}
      			else{
      				return DanielHermanShmulyanAgent.OppUtil.SHORT_TERM;
      			}
      	}      			
      	else if		(agg_oppLtVal>agg_oppStVal){
  			return DanielHermanShmulyanAgent.OppUtil.LONG_TERM;
  		}
  		else if     (agg_oppLtVal==agg_oppStVal){
  			return DanielHermanShmulyanAgent.OppUtil.UNKNOWN;      			
  		}
  		else{
  			return DanielHermanShmulyanAgent.OppUtil.SHORT_TERM;
      	}
    }
    // ---------------------------------------------------------------    
    /**
     * Updates the knowledge base about the type of the opponent agent
     * using new guessed fact about him.
     * @param oppType - the approximated type of the opponent agent.
     */
    private void updateOppTypeKnowledge(DanielHermanShmulyanAgent.OppUtil oppTypeEvidence){
    	DanielHermanShmulyanAgent.OppUtil temp = whatIsOppUtil;
    	switch(temp){
    	case UNKNOWN:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_COMPROMISE;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_SHORT_TERM;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_LONG_TERM;
			 				 break;
        	}
        	break;
        	
    	case PROBABLY_COMPROMISE:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.COMPROMISE;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
			 				 break;
        	}
        	break;

    	case PROBABLY_SHORT_TERM:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.SHORT_TERM;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
			 				 break;
        	}
        	break;
        	
    	case PROBABLY_LONG_TERM:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.UNKNOWN;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.LONG_TERM;
			 				 break;
        	}
        	break;
    	
    	case COMPROMISE:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.COMPROMISE;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_COMPROMISE;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_COMPROMISE;
			 				 break;
        	}
        	break;
       	
    	case SHORT_TERM:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_SHORT_TERM;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.SHORT_TERM;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_SHORT_TERM;
			 				 break;
        	}
        	break;

    	case LONG_TERM:
        	switch(oppTypeEvidence){
        	case COMPROMISE:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_LONG_TERM;
        					 break;
        	case SHORT_TERM:whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.PROBABLY_LONG_TERM;
			 				 break;
        	case LONG_TERM :whatIsOppUtil = DanielHermanShmulyanAgent.OppUtil.LONG_TERM;
			 				 break;
        	}
        	break;        	        	
    	}
    	//System.err.println(whatIsOppUtil);
    	//System.err.println("XXXXXXXXXXXXXX");
    }
    // ---------------------------------------------------------------    
    /**
     * Prints the utility structure to stdErr file.
     * @param agentType - the automated agent's type
     */
    /*
    private void printUtilityStructure(AutomatedAgentType agentType){
    	
    	System.err.println("timeEffect:"+agentType.m_fullUtility.dTimeEffect);
    	System.err.println("QuoVal    :"+agentType.m_fullUtility.dTimeEffect);
    	System.err.println("OptVal    :"+agentType.m_fullUtility.dOptOutValue);
    	System.err.println("------------------------");

    	// Iterate over all the issues of negotiation. 
        for (int i = 0; i < agentType.m_fullUtility.lstUtilityDetails.size(); ++i){
    		UtilityDetails utilityDetails = (UtilityDetails)agentType.m_fullUtility.lstUtilityDetails.get(i);
    		System.err.println("sTitle:"+utilityDetails.sTitle);

    		// Iterate over all the sub-issues of the current issue.
    		for (int j = 0; j < utilityDetails.lstUtilityIssues.size(); ++j){
    			UtilityIssue utilityIssue = (UtilityIssue)utilityDetails.lstUtilityIssues.get(j);
    			// System.err.println("  sAttr:"+utilityIssue.sAttributeName);
    			System.err.println("  sSide:"+utilityIssue.sSide);
    			System.err.println("  sWeight:"+utilityIssue.dAttributeWeight);
    			//System.err.println("  sExp:"+utilityIssue.sExplanation);
    			
    			// Iterate over all the possible values of the current sub-issue.
    			for (int k = 0; k < utilityIssue.lstUtilityValues.size(); ++k){
    				UtilityValue utilityValue = (UtilityValue)utilityIssue.lstUtilityValues.get(k);
    	            //System.err.println("    Svalue:"+utilityValue.sValue);
    				double dUtility = utilityValue.dUtility;
    	            System.err.println("    dUtility:"+dUtility);
    	            //System.err.println("    Dtime_eff:"+utilityValue.dTimeEffect);

    			}
    		}   		
    	}
    	
    } */   
    // ---------------------------------------------------------------    
    // EOClass
}
// ---------------------------------------------------------------    
// EOF.
