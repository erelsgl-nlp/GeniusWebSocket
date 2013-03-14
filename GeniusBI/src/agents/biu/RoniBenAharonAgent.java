package agents.biu;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/////////////////////////////////////////////////////
//  ENGLAND - ZIMBABUE
/////////////////////////////////////////////////////


/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class RoniBenAharonAgent extends OldAgentAdapter{
    
    //RONI: for debug ONLY:
    boolean _debug = true;
    boolean _debugDast = false;
    
    //sOpponentType
    String _sOpponentType;
    
    //AutomatedAgentType
    AutomatedAgentType _agentType;
    
    //this hash table contains all the possible full agreements, and their value
    //for my agent, and for the average opponent. the keys are the utility value, while the value is the agreement
    Hashtable<Double, int[]> _myValueHash=new Hashtable<Double, int[]>();
    Hashtable<Double, int[]> _averageOpponentValueHash =new Hashtable<Double, int[]>();

    //these vectors contain all the possible utility values, sorted. this is an 
    //assisting data structure to sort the _myValueHash hash table.
    Vector<Double> _mySortedValueVector;
    Vector<Double> _averageOpponentSortedValueVector; 

    //opt-out value for each of the four possible agents:
    double _myOptOut;
    double _compromiseOptOut;
    double _longTermOptOut;
    double _shortTermOptOut;
    double _averageOpponentOptOut;
      
    
    //last proposals: the next members represent the last relevant proposals, and 
    //their utilities for me and for the opponent:
    int[] _myLastProposal;
    int[] _oppLastProposal;
	double _lastUtilityForMeThatIProposed = AutomatedAgentType.VERY_HIGH_NUMBER;
	double _bestUtilityForMeThatOppProposed = 0;
	double _bestUtilityForOppThatIProposed = 0;
	double _lastUtilityForOppThatOppProposed = AutomatedAgentType.VERY_HIGH_NUMBER;
	
	
	
    public RoniBenAharonAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public RoniBenAharonAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the negotiation starts.
	 * Add any logic you need here.
     * @param agentType - the automated agent
	 */
    //TODO
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		
		_sOpponentType = sOpponentType;
		_agentType = agentType;
		
        // calculate Automated Agent first offer
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
	
	/**
	 * my algorithm:
	 * --------------
	 * a. ignore query/promise
	 * b. calculate utility for me and for opponent, for the given offer.
	 * c. if it is a partial offer - reject
	 * d. check whether the new proposal of opponent already suggested a better proposal for me. if so - reject.  
	 * e. update _bestUtilityForMeThatOppProposed, and the utility of this proposal 
	 *    for the opponent. If the offer is equal or better than the one my agent offered, accept. 
	 *    else - reject.
	 */

    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {

    	//A. ignore query/promise
    	//---------------------------
    	if (nMessageType == AutomatedAgentMessages.QUERY
    			|| nMessageType == AutomatedAgentMessages.PROMISE)
    	{
            return;
    	}
    	
        // B. Check the utility value of the opponent's offer
    	//---------------------------------------------------
        double dOppOfferValueForMe = agentTools.getAgreementValue(_agentType, CurrentAgreementIdx, agentTools.getCurrentTurn());
        
        double dOppOfferUtilityForMe = dOppOfferValueForMe - _myOptOut;    
        
    	int totalIssuesNum = agentTools.getTotalIssues(_agentType);
        
    	//for debugging
        String indicesInString = indices2String(CurrentAgreementIdx);
    	System.err.println("I was offered (offer): "+indicesInString);
    	System.err.println("I was offered (value): "+dOppOfferValueForMe);
    	System.err.println("I was offered (utility): "+dOppOfferUtilityForMe);

//    	//C. if it is a partial offer - reject:
//    	//-------------------------------------
//    	for (int i=0;i<totalIssuesNum;i++) {
// 
//    		
//    		
//    		// if salary(i=0), job(i=1) or workingHours(i=5) not filled - reject: (not_generic)
//    		if ((i==0 || i==1 || i==5) && (CurrentAgreementIdx[i]==-1)) {
//                agentTools.rejectMessage(sOriginalMessage);
//                System.err.println(">>>PARTIAL, rejecting ");
//                return;
//    		}
//    	}
    	
        // D. check whether the new proposal of opponent already suggested a better proposal for me. if so - reject
        // --------------------------------------------------------------------------------------------------------
        if (_bestUtilityForMeThatOppProposed >= dOppOfferUtilityForMe)
        {
            // reject offer - this offer doesn't mean anything.
            agentTools.rejectMessage(sOriginalMessage);
            System.err.println(">>>LOWER_OFFER_THAN_EXISTING, rejecting ");
            return;
        }
        
        // E. update _bestUtilityForMeThatOppProposed, and the utility of this proposal
        //    for the opponent. If the offer is equal or better than the
        //    one my agent offered. accept. else - reject.
        // --------------------------------------------------------------------------
        _bestUtilityForMeThatOppProposed = dOppOfferUtilityForMe;
        _lastUtilityForOppThatOppProposed =calcApproxUtilityForOpponent(CurrentAgreementIdx);
        _oppLastProposal = CurrentAgreementIdx.clone();
        
        if (_bestUtilityForMeThatOppProposed >= _lastUtilityForMeThatIProposed) {
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            System.err.println(">>>GOOD!, accepting ");
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
            
            return;
        }
        else {
            agentTools.rejectMessage(sOriginalMessage);
            System.err.println(">>>NOT_GOOD_ENOUGH, rejecting ");
        	return;
        }
    }
        
    
	/**
     * called to decide which offer to propose the opponent at a given turn
     * This method is always called when beginning a new turn
     * You can also call it during the turn if needed
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */

	/**
	 * my algorithm:
	 * --------------
	 * 1. go over each of the agreements, take the agreement value for each of
	 *    the four possible agents (me + three possible opponents), and store 
	 *    the values in the correlating hash tables. build correlating sorted vectors, for sorted access
	 *    to the hash tables.
	 * 2. If first round, give best offer for me.
	 * 3. calculate my risk and opponent risk.
	 * 4. if my risk is bigger then oppRisk, give the last offer (and wait for the opponent to concede).
	 *    else, find the offer that will change the risk balance, and offer it.
	 *    
	 */

    
    //TODO
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
       
        //A. get the different environment parameters:
        //--------------------------------------------
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        for (int i = 0; i < totalIssuesNum; ++i) {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
  
        // B. create the three possible agent types for the opponent side:
        //-----------------------------------------------------------------
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
      
        
        //init vars for temporarily storing the values:
        double dMyAgentAgreementValue            = AutomatedAgentType.VERY_SMALL_NUMBER; 
        double dOpponentCompromiseAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        double dOpponentLongTermAgreementValue   = AutomatedAgentType.VERY_SMALL_NUMBER;
        double dOpponentShortTermAgreementValue  = AutomatedAgentType.VERY_SMALL_NUMBER;   
        double dAverageOpponentAgreementValue    = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        //C. calculate opt-out values:
        //----------------------------
        _myOptOut = agentTools.getOptOutValue(agentType);
        _compromiseOptOut = agentTools.getOptOutValue(agentOpponentCompromise);
        _longTermOptOut = agentTools.getOptOutValue(agentOpponentLongTerm);
        _shortTermOptOut = agentTools.getOptOutValue(agentOpponentShortTerm);
        _averageOpponentOptOut = (_compromiseOptOut+_longTermOptOut+_shortTermOptOut)/3;
        
        //D. go over each of the agreements, take the agreement value for each of
        //   the four possible agents (me + three possible opponents), and store
        //   the values in the correlating hash tables:
        //-----------------------------------------------------------------------
        _myValueHash.clear();
        _averageOpponentValueHash.clear();
        
        for (int i = 0; i < totalAgreementsNumber; ++i)
        { 	        	
        	//get the agreement value for my agent, and for the different possible opponents:
            dMyAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);  
            dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, CurrentAgreementIdx, nCurrentTurn);
            dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, CurrentAgreementIdx, nCurrentTurn);
            dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, CurrentAgreementIdx, nCurrentTurn);
            dAverageOpponentAgreementValue = (dOpponentCompromiseAgreementValue+dOpponentLongTermAgreementValue+dOpponentShortTermAgreementValue)/3;
            
            //store the values in the hash tables (agreement as the hash value):
            _myValueHash.put(dMyAgentAgreementValue, CurrentAgreementIdx.clone());
            _averageOpponentValueHash.put(dAverageOpponentAgreementValue, CurrentAgreementIdx.clone());

           //get the next agreement indices
            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);
        } // end for - going over all possible agreements
        
        //E. sort the vectors:
        //----------------------

        _mySortedValueVector = new Vector<Double>(_myValueHash.keySet());
	    Collections.sort(_mySortedValueVector);

	    _averageOpponentSortedValueVector = new Vector<Double>(_averageOpponentValueHash.keySet());
	    Collections.sort(_averageOpponentSortedValueVector);
	    

	    int myOfferIdx[] = new int[totalIssuesNum];

	    //F. if first round, give best offer for me:
	    //------------------------------------------
	    if (nCurrentTurn==1) {
	    	myOfferIdx = _myValueHash.get(_mySortedValueVector.lastElement());
	        
	    	//send offer:
	    	agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(myOfferIdx));
	        String sOffer = agentTools.getSelectedOffer();
	        agentTools.sendOffer(sOffer);
	        
	        //update members:
	        _lastUtilityForMeThatIProposed = agentTools.getAgreementValue(agentType, myOfferIdx, nCurrentTurn) - _myOptOut;
	        _bestUtilityForOppThatIProposed= calcApproxUtilityForOpponent(myOfferIdx);
	        _myLastProposal = myOfferIdx.clone();
	        
	        if (_debug) {
	        	double bestValue = agentTools.getAgreementValue(agentType, myOfferIdx, nCurrentTurn);
	        		System.err.println("BEST_FOR_1ST_ROUND: "+indices2String(myOfferIdx)+", value: "+bestValue);
	        }
    		
	        return;
	    }
	    
	    //else, follow ZEUTHEN protocol...
	    
	    
	    //G. calculate my risk and opponent risk:
	    //---------------------------------------
	    
	    
	    double myRisk = (_lastUtilityForMeThatIProposed - _bestUtilityForMeThatOppProposed)/_lastUtilityForMeThatIProposed;
	    double oppRisk =(_lastUtilityForOppThatOppProposed - _bestUtilityForOppThatIProposed) / _lastUtilityForOppThatOppProposed;
	    
	    System.err.println("_lastUtilityForMeThatIProposed: "+_lastUtilityForMeThatIProposed+", _bestUtilityForMeThatOppProposed:" +_bestUtilityForMeThatOppProposed);
	    System.err.println("_lastUtilityForOppThatOppProposed: "+_lastUtilityForOppThatOppProposed+", _bestUtilityForOppThatIProposed:" +_bestUtilityForOppThatIProposed);
	    System.err.println("My risk: "+myRisk+", opp's risk: "+oppRisk);
	    
	    
	    //H. if my risk is bigger then oppRisk, give the last offer.
	    //   else, find the offer that will change the balance
	    //-----------------------------------------------------------
	    if (myRisk>oppRisk) {
	    	System.err.println("WAIT_FOR_BETTER_OFFER - repeat last offer");
	    	
	    	//send last offer:
	    	agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(_myLastProposal));
	        String sOffer = agentTools.getSelectedOffer();
	        agentTools.sendOffer(sOffer);

	    	return;
	    }
	    
	    //else, find the offer that will change the balance:

		Iterator<Double> it =_averageOpponentSortedValueVector.iterator();
		while (it.hasNext()) {
			Double element =  (Double)it.next();
			System.err.println("_averageOpponentSortedValueVector: "+element + " " + indices2String(_averageOpponentValueHash.get(element)));
		}
	      
	    boolean balanceChanged = false;

	    
//	    int avgVectorIterator = _averageOpponentSortedValueVector.size()-1;
	    int avgVectorIterator = 0;
//	    while (!balanceChanged && avgVectorIterator>0 ) {
	    while (!balanceChanged && avgVectorIterator<_averageOpponentSortedValueVector.size() ) {
	    	double curValueOfOfferToOpp = _averageOpponentSortedValueVector.get(avgVectorIterator);
	    	int[] curOfferToOpp = _averageOpponentValueHash.get(curValueOfOfferToOpp).clone();
	    	double curValueOfOfferToMe = dMyAgentAgreementValue = agentTools.getAgreementValue(agentType, curOfferToOpp, nCurrentTurn);
	    
	    	double curUtilityOfOfferToOpp = curValueOfOfferToOpp -_averageOpponentOptOut;
	    	double curUtilityOfOfferToMe  = curValueOfOfferToMe -_myOptOut;
	    	
	    	myRisk = (curUtilityOfOfferToMe - _bestUtilityForMeThatOppProposed)/curUtilityOfOfferToMe;
		    oppRisk =(_lastUtilityForOppThatOppProposed - curUtilityOfOfferToOpp) / _lastUtilityForOppThatOppProposed;
	    
		    System.err.println("Index: "+avgVectorIterator+"| offer: "+indices2String(curOfferToOpp)+"| curValueOfOfferToOpp:"+curValueOfOfferToOpp+"| curValueOfOfferToMe:"+curValueOfOfferToMe+" |myRisk: "+myRisk+" |oppRisk:"+oppRisk+" |curUtilityOfOfferToMe"+curUtilityOfOfferToMe+" |_bestUtilityForMeThatOppProposed"+_bestUtilityForMeThatOppProposed+" |_lastUtilityForOppThatOppProposed"+_lastUtilityForOppThatOppProposed+" |curUtilityOfOfferToOpp"+curUtilityOfOfferToOpp+" |_averageOpponentOptOut"+_averageOpponentOptOut+" |_shortTermOptOut"+_shortTermOptOut+" |_longTermOptOut"+_longTermOptOut+" |_compromiseOptOut"+_compromiseOptOut+" |_myOptOut"+_myOptOut);

		    double _myOptOut;
		    double _compromiseOptOut;
		    double _longTermOptOut;
		    double _shortTermOptOut;
		    double _averageOpponentOptOut;
		    
		    
		    if (myRisk>oppRisk) { //we found it, myRisk is once again bigger than oppRisk..
		    	balanceChanged = true;
		    	
		    	//send offer:
		        agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(curOfferToOpp));
		        String sOffer = agentTools.getSelectedOffer();
		        agentTools.sendOffer(sOffer);

		    	//update members:
		    	_lastUtilityForMeThatIProposed = curUtilityOfOfferToMe;
		    	_bestUtilityForOppThatIProposed = curUtilityOfOfferToOpp;
		    	_myLastProposal = curOfferToOpp.clone();
		        return;
		    }
		
//		    avgVectorIterator--;
		    avgVectorIterator++;
	    }
	    

    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    
    //TODO
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
        //Calculate agreements values for a given turn

        // Initialisation - DO NOT CHANGE
        int nIssuesNum = agentTools.getTotalIssues(agentType);
        
        int CurrentAgreementIdx[] = new int[nIssuesNum];
        int MaxIssueValues[] = new int[nIssuesNum];

        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

        for (int i = 0; i < nIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        //end initialisation

        // @@EXAMPLE@@
        // Currently, the method calculates the best agreement, worst agreement
        // and the utility value per agreement
        /********************************
         * Start example code
         ********************************/             
        double dAgreementValue = 0;
        
        agentTools.initializeBestAgreement(agentType);
        agentTools.initializeWorstAgreement(agentType);
        
        //To obtain information from the utility you can use getters from the AgentType class
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
    
    
    /**
     * 
     * 
     * 
     * 
     */
    
    
    
    
    
    
    
   //not TODO 
   //not TODO 
   //not TODO 
   //not TODO 
   //not TODO 
   //not TODO 
   //not TODO 
   //not TODO 
    ///////////////////////////////////////////////////////
    //				NON IMPORTANT METHODS:
    ///////////////////////////////////////////////////////
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
	 * @param nMessageType - the type of massage the opponent rejected, can be
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
 
	
    /***********************************************
     * @@ Auxiliary private methods
     ***********************************************/
	
    /**
     * This method calculates the approx. utility of the opponent. it is approximated,
     * because we don't know for sure the type of the opponent.
     * In this method, we just take the average of the three.
     * @param currentAgreementIdx
     * @return
     */
    private double calcApproxUtilityForOpponent(int[] currentAgreementIdx) {

    	//create the three possible agent types for the opponent side:
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                

        
        // calc values:
        double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, currentAgreementIdx, agentTools.getCurrentTurn());
        double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, currentAgreementIdx, agentTools.getCurrentTurn());
        double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, currentAgreementIdx, agentTools.getCurrentTurn());

        //calc utilities:
        double dOpponentCompromiseAgreementUtility = dOpponentCompromiseAgreementValue - _compromiseOptOut;
        double dOpponentLongTermAgreementUtility = dOpponentLongTermAgreementValue - _longTermOptOut;
        double dOpponentShortTermAgreementUtility = dOpponentShortTermAgreementValue - _shortTermOptOut;
        
        //calc average of the three:
        return (dOpponentCompromiseAgreementUtility+dOpponentLongTermAgreementUtility+dOpponentShortTermAgreementUtility)/3;
    }
 
    
    //RONI - rmv
    private String indices2String(int[] offer) {
    	int totalIssuesNum = agentTools.getTotalIssues(_agentType);
    	String ret = "";
    	for (int i=0;i<totalIssuesNum;i++) {
    		ret+= offer[i] + "_";
    	}
    	return ret;
    }
   
    
}



////////////////////////////////GARBAGE///////////////////////////////////////////////
////////////////////////////////GARBAGE///////////////////////////////////////////////
////////////////////////////////GARBAGE///////////////////////////////////////////////
/** comes after "calculateOfferAgainstOpponent(agentType, sOpponentType, 1);":

	Iterator<Double> it =_mySortedValueVector.iterator();
	while (it.hasNext()) {
		Double element =  (Double)it.next();
		System.err.println("vector_automatedAgent: "+element + " " + indices2String(_myValueHash.get(element)));
	}
 **/



