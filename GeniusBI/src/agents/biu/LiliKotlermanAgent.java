package agents.biu;

import java.util.LinkedList;
import java.util.Arrays;

/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class LiliKotlermanAgent extends OldAgentAdapter{

/*    public boolean isFullAgreement (int AgreementIdx[], AutomatedAgentType agentType) {
    	boolean result = true;
    	    	
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        
        for (int i = 0; i < totalIssuesNum; i++)
        {
            if((AgreementIdx[i] < 0)||(AgreementIdx[i] >= agentTools.getMaxValuePerIssue(agentType, i))){
            	result=false;
            	break;
            }
        }
    	return result;
    }*/
    
    public LiliKotlermanAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public LiliKotlermanAgent(AgentTools agentTools) {
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
        //Calculating the response
    	double dOfferedAgreementValue = agentTools.getAgreementValue(CurrentAgreementIdx);
    	double dPreviousAgreementValue = agentTools.getAcceptedAgreementsValue();
    	double dMyNextOfferValue = agentTools.getNextTurnOfferValue();
    	// if not a full agreement - reject
    	if(dOfferedAgreementValue > dPreviousAgreementValue){ // if better than previous
    		// accept anything with value >= dMinAcceptableValue
    		if (dOfferedAgreementValue >= dMinAcceptableValue) {
    			agentTools.acceptMessage(sOriginalMessage);
    			// if it was a query or a promise - check whether to send my next turn offer is better
    			if((nMessageType==AutomatedAgentMessages.QUERY)||(nMessageType==AutomatedAgentMessages.PROMISE)){
    				if (dMyNextOfferValue < dOfferedAgreementValue)  { //if my next offer isn't better
    					agentTools.setSendOfferFlag(false); //don't send my offer
    				}
    			}
    		}
    		else agentTools.rejectMessage(sOriginalMessage);
    	}
    	else agentTools.rejectMessage(sOriginalMessage);
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
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
        //@@ Add any logic to calculate offer (or several offers)
        // and decide which to send to the opponent in a given turn
    	
       int totalIssuesNum = agentTools.getTotalIssues(agentType);
       int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
       String theSelectedOffer=agentTools.getBestAgreementStr(agentType);
                 
       int CurrentAgreementIdx[] = new int[totalIssuesNum];
       int MaxIssueValues[] = new int[totalIssuesNum];
       
       for (int i = 0; i < totalIssuesNum; ++i)
       {
           CurrentAgreementIdx[i] = 0;
           MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
       }
       
      
       for (int i = 0; i < totalAgreementsNumber; ++i)
       {
    	   double thisOfferValue = agentTools.getAgreementValue(CurrentAgreementIdx);
    	 if((thisOfferValue > dMinAcceptableValue)&&(i>lastI)&&(thisOfferValue < dMaxOfferedValue)) {
    		 theSelectedOffer = agentTools.getMessageByIndices(CurrentAgreementIdx);
    		 if(!theSelectedOffer.contains("No agreement")){
    			 lastI = i;
    			 break;
    		 }	
    	 }
         agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
           
        } // end for - going over all possible agreements
 

       agentTools.sendOffer(theSelectedOffer);
       
      
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
        dMinAcceptableValue = 0.8*agentTools.getBestAgreementValue(agentType);//(agentTools.getBestAgreementValue(agentType)+agentTools.getSQValue(agentType))/2.0;
        dMaxOfferedValue = 0.85*agentTools.getBestAgreementValue(agentType);
    }
    double []MyOffersList = null;
    double dMinAcceptableValue = 0;
    double dMaxOfferedValue = 0;
    String [] OffersStrings = null;
    int lastI = 0;
}
