package agents.biu;

import java.util.ArrayList;
/**
 * @author Mike
 */
public class GuySpeilbergAgent extends OldAgentAdapter{
    int hisLastOffer;
    int[][] myAgreements;
    int lowestBound = 0;
    int highestBoud = 0;
    int numOfThreats = 0;
    AutomatedAgentType myType;
    
    public GuySpeilbergAgent() {
    	super();
    }
    
    /**
     * Constructor
     */
    public GuySpeilbergAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the nagotiation starts.
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
            lowestBound = 0;
            highestBoud = 0;
            numOfThreats = 0;
            myType = agentType;
            calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
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
        hisLastOffer = getIndexByIndi(fixPartAgreement(CurrentAgreementIdx));
        switch (nMessageType) {
            case AutomatedAgentMessages.OFFER:
            case AutomatedAgentMessages.COUNTER_OFFER:
                if ((getValueByIndex(highestBoud) * 0.85) <= getValueByIndex(hisLastOffer)) {
                    agentTools.acceptMessage(sOriginalMessage);
                    //agentTools.setSendOfferFlag(false);
                }
                else if ((getValueByIndex(highestBoud) * 0.5) >= getValueByIndex(hisLastOffer)) {
                    agentTools.rejectMessage(sOriginalMessage);
                    if (Math.random() < 0.3)
                        agentTools.sendThreat("If i do not receive an offer that i like i will have to think through about continuing the negotiation");
                }
                else if (hisLastOffer <= lowestBound) {
                    highestBoud =(hisLastOffer - highestBoud) / 5;
                }
                break;    
            case AutomatedAgentMessages.PROMISE:
            case AutomatedAgentMessages.QUERY:
                if ((getValueByIndex(highestBoud) * 0.9) <= getValueByIndex(hisLastOffer))
                    agentTools.acceptMessage(sOriginalMessage);
                else 
                    agentTools.rejectMessage(sOriginalMessage);
                    break;
        }
    }
        
    /***********************************************
     * @@ Logic for receiving messages
     * Below are messages the opponent sends to the automated agent
     * You can add logic if needed to update your agent per message type
     ***********************************************/
	public void commentReceived(String sComment) {       
    }

	public void threatReceived(String sThreat) {
            if (Math.random() < 0.5 && numOfThreats < 5) {
                decHighestBound(1);
                numOfThreats++;
            }
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
            highestBoud -= highestBoud / 5;
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
            if (Math.random() < 0.5)
                decHighestBound(1);
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
       mySort();
      // At the first turn or last turn offers the best agreement
      if (nCurrentTurn == 1)
          highestBoud = 0;
      else if (nCurrentTurn == agentTools.getTurnsNumber()) {
          decHighestBound(lowestBound / 2);
      }
      else {
          decHighestBound((int)Math.random() * 3 + 1);
      }
      
        if (Math.random() < 0.25)
            highestBoud -= highestBoud / 10;
         
        String sOffer = agentTools.getMessageByIndices(getIndicesByIndex(highestBoud));
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

        myAgreements = new int[totalAgreementsNumber][nIssuesNum];
        
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
            for (int j = 0 ; j < nIssuesNum ; j++)
                myAgreements[i][j] = CurrentAgreementIdx[j];
            
            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
    }
    
    // ---------------------------- My Functions ------------------------------
    private void mySort() {
        int[] curIndices = new int[agentTools.getTotalIssues(myType)];
        int tempIndices;
        double curValue;
        int curIndex = 0;
        
        for (int i = 0 ; i < agentTools.getTotalAgreements(myType) ; i++) {
            curValue = Integer.MIN_VALUE;
            
            for (int j = i ; j < agentTools.getTotalAgreements(myType) ; j++) {
                for (int k = 0 ; k < agentTools.getTotalIssues(myType) ; k++)
                    curIndices[k] = myAgreements[j][k];
                
                if (agentTools.getAgreementValue(curIndices) > curValue) {
                    curValue = agentTools.getAgreementValue(curIndices);
                    curIndex = j;
                }
            }     
            
            for (int k = 0 ; k < agentTools.getTotalIssues(myType) ; k++) {
                tempIndices = myAgreements[i][k];
                myAgreements[i][k] = myAgreements[curIndex][k];
                myAgreements[curIndex][k] = tempIndices;
            }
        }
        upDateLowestBound();
    }
    
    private void upDateLowestBound() {
        int curIdx;
        
        for (curIdx = agentTools.getTotalAgreements(myType) ; curIdx > 0 && (getValueByIndex(curIdx-1) <= agentTools.getSQValue(myType) || agentTools.getAcceptedAgreementsValue() > getValueByIndex(curIdx-1)) ; curIdx--);
        lowestBound = curIdx;
        
        if (highestBoud >= lowestBound)
            highestBoud = lowestBound / 2;
    }
    
    private boolean isFullAgr (int[] indi) {
        boolean retValue = true;
        
        for (int i = 0 ; i < agentTools.getTotalIssues(myType) && retValue; i++)
            if (indi[i] == AutomatedAgentType.NO_VALUE)
                retValue = false;
        
        return retValue;
    }
    
    private int[] getIndicesByIndex(int idx) {
        int[] indi = new int[myAgreements[idx].length];
          
          for (int i = 0 ; i < indi.length ; i++)
              indi[i] = myAgreements[idx][i];
        
        return indi;
    }
    
    private double getValueByIndex(int idx) {
        return agentTools.getAgreementValue(getIndicesByIndex(idx));
    }
    
    private void decHighestBound(int decBy) {
        if (highestBoud+decBy <= lowestBound)
            highestBoud+=decBy;
    }
    
    private int getIndexByIndi(int[] curIndi) {
        int curIdx = agentTools.getTotalAgreements(myType) - 1;
        boolean eqFlag = false;
        
        for (int i = 0 ; i < agentTools.getTotalAgreements(myType) && !eqFlag ; i++) 
           if (getValueByIndex(i) <= agentTools.getAgreementValue(curIndi)) {
            curIdx = i;
            eqFlag = true;
           }
        
        return curIdx;
    }

    private int[] fixPartAgreement(int[] indi) {
        int[] curIndi = new int[agentTools.getTotalIssues(myType)];
        
        if (isFullAgr(indi))
            curIndi = indi;
        else {
            for (int i = 0 ; i < agentTools.getTotalIssues(myType) ; i++) 
                if (indi[i] == AutomatedAgentType.NO_VALUE) {
                    if (agentTools.getAgentSide().equals(AutomatedAgent.SIDE_B_NAME))
                        indi[i] = 0;        
                    else
                        indi[i] = agentTools.getMaxValuePerIssue(myType, i);
                }
        }
        
        return curIndi;
    }
}
