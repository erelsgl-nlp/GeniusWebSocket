package agents.biu;

import java.io.*;
import java.util.*;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Global;
import negotiator.actions.Offer;
import negotiator.actions.UpdateStatusAction;
import negotiator.exceptions.NegotiatorException;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.UtilitySpace;

/**
 * @author Yinon
 * Examples are provided inline and marked as examples
 *
 */ 
public class KBAgent extends OldAgentAdapter {
	public class OpponentData
	{
		String _opponentType = null;
		int _opponentTypeIDX = 0;
		int _lastOfferIndex = 0;
		String _opponentSide = null;
		Bid _agentOffers[] = null;
		double _allOffersUtilitySum = 0;
		double _probability = 0;
	    double _agentAcceptThersholds[] = null;
	    AgreementWrapper _sortedAllAgreements[]=null;
	    Hashtable<String,Double> _defaultValues=null;
	}

	private static final String BASE_FILENAME = "AgentConfig";
	private static final String PROBABILTY_FILE_NAME = Global.pathToLogFiles + File.separator + "Prob_";
	//static int SHORT_TERM_INDEX = AutomatedAgentsCore.SHORT_TERM_TYPE_IDX;
	//static int LONG_TERM_INDEX = AutomatedAgentsCore.LONG_TERM_TYPE_IDX;
	//static int COMPROMISE_INDEX = AutomatedAgentsCore.COMPROMISE_TYPE_IDX;
	private static int AGENT_TYPES_NUM = 3;
	public static final double PRECISION_VALUE = 0.3; // used in order to scale utilities and make them positive
	private static final int TURNS_BETWEEN_OFFERS=2; // HOW LONG DO WE WAIT FOR A RESPONSE AFTER SENING AN OFFER
	
    AutomatedAgentType _agentType = null;
    String _opponentType= null;
    OpponentData _possibleOpponents[] = new OpponentData[3];
    int _currentOpponent =0;
    // this flag causes the agent not to make offers before the first offer from the opponent was given.
    private boolean waitForOpponentFirstOffer = false;
    
    /**
     * true iff the opponent rejected my last offer, OR if the opponent made a counter-offer.
     */
    boolean _opponentRejectedMyLastOfferOrMadeCounterOffer=false;
    
    int _lastTurnOfOffer=-100; // negative so the agent will offer on the first turn
    
    public KBAgent() {
    	Global.logStdout("KBAgent","default constructor", "");
    }
    
    /**
     * Constructor - NOT USED
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     *
    public KBAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    	System.out.println("KBAgent const 1");
    }*/
    
	/**
	 * Called before the the negotiation starts. Add any logic you need here.
     * For example, calculate the very first offer you'll offer the opponent and send it.
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
        // save all paramters
		_agentType = agentType;
		_opponentType = sOpponentType;
		  
		/* read config file of the following form "AgentConfigBCompromise.cfg"/"AgentConfigBLongTerm.cfg"
		 * If we're playing Agent A, we need to read all config file of the 
		 * possible opponents of side B. 
		 */
		AGENT_TYPES_NUM = worldInformation.getNumOfPossibleUtiltySpaces();
		 for (int i=0;i<AGENT_TYPES_NUM;i++) {
			 _possibleOpponents[i]=initializeOpponentData(_opponentType,getTypeNameFromWorldInfo(i),i,agentTools);
		 }
		
		_currentOpponent= (int)(Math.random()*3);
		
		//calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
    }
	
	// returns the string that is associated with the agent type from the utility space
	private String getTypeNameFromWorldInfo(int index)
	{
		String name = worldInformation.getUtilitySpace(index).getName();
		if (name.contains("ShortTerm"))
			return "ShortTerm";
		else if (name.contains("LongTerm"))
			return "LongTerm";
		else if (name.contains("Compromise"))
			return "Compromise";
		else 
			return name;
			
	}
	
	// this class defines a way to compare agreements
	class AgreementWrapper implements Comparable 
	{
		Bid agreement=null;
		double value=0;
		public int compareTo(Object o)
		{
			AgreementWrapper aw=(AgreementWrapper)o;
			if (value<aw.value)
				return -1;
			if (value>aw.value)
				return 1;
			return 0;
		}
	}
	
	// initializes the OpponentData data of a specific type
	public OpponentData initializeOpponentData(String side,String type, int typeIDX,AgentTools agentTools)
	{
		OpponentData od = new OpponentData();
		od._opponentSide=side;
		od._opponentType=type;
		od._opponentTypeIDX=typeIDX;
		od._probability = 1.0/AGENT_TYPES_NUM;
		if (od._opponentSide=="SIDE_A")
			readConfigFile(od,BASE_FILENAME + "A" + type + ".cfg");
		else
			readConfigFile(od,BASE_FILENAME + "B" + type + ".cfg");
				
		calculateTotalAgreementPerTurn(od,1);
        	
        return od;
	}
	
	public void calculateTotalAgreementPerTurn(OpponentData od,int turn)
	{
		try {
			UtilitySpace opponentUtilitySpace = worldInformation.getUtilitySpace(od._opponentTypeIDX);
			int totalAgreements = agentTools.getTotalAgreements(_agentType);
			od._sortedAllAgreements = new AgreementWrapper[totalAgreements];
			od._allOffersUtilitySum = 0;
			BidIterator bidIter = new BidIterator(utilitySpace.getDomain());
			int i = 0;
			while (bidIter.hasNext()) {
				Bid bid = bidIter.next();
				od._sortedAllAgreements[i] = new AgreementWrapper();
				od._sortedAllAgreements[i].agreement = bid;
				od._sortedAllAgreements[i].value = opponentUtilitySpace.getUtilityWithTimeEffect(bid, turn);
				od._allOffersUtilitySum += Math.exp(od._sortedAllAgreements[i].value * PRECISION_VALUE);
				i++;
			}

			Arrays.sort(od._sortedAllAgreements);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readConfigFile(OpponentData od,String filename) {
		try {
			// open file
			BufferedReader bfr = new BufferedReader(new FileReader(utilitySpace.getDomain().getPathToDataFiles()+ File.separator+ filename));
			// read numOfTurns
			String line = bfr.readLine();
			int numOfTurns=Integer.parseInt(line);
			od._agentOffers = new Bid[numOfTurns];
			od._agentAcceptThersholds = new double[numOfTurns];
			// read thresholds
			bfr.readLine(); //read the header
			for (int i=0;i<numOfTurns;i++) {
				od._agentAcceptThersholds[i]=Double.parseDouble(bfr.readLine());
			}
			bfr.readLine();
			// read offers
			bfr.readLine(); //read the header
			for (int i=0;i<numOfTurns;i++) {
				
				od._agentOffers[i]=getBidFromOldFormatString(bfr.readLine());
			}	
			bfr.readLine(); //read the header
			od._defaultValues=new Hashtable<String,Double>();
			while ((line=bfr.readLine())!=null)
				od._defaultValues.put(line.substring(0,line.indexOf('*')).toLowerCase(), Double.parseDouble(line.substring(line.indexOf('*')+1)));
			bfr.close();
		} catch (java.io.FileNotFoundException ex) {
			throw new RuntimeException("Cannot find config file '"+filename+"' in folder '"+new File(".").getAbsolutePath()+"'", ex);
		} catch (Exception ex) {
			throw new RuntimeException("Cannot read config file '"+filename+"' in folder '"+new File(".").getAbsolutePath()+"'", ex);
		}
	}
	
    /**
	 * @param readLine
	 * @return
	 */
	private Bid getBidFromOldFormatString(String str) {
		Bid bid =null;
		try {
			String[] parts = str.split("\\*");
			HashMap<String,String> issueToValue = new HashMap<String,String>();
			int i=0;
			while (i<parts.length-1) {
				issueToValue.put(parts[i+1].toLowerCase(), parts[i].toLowerCase());
				i+=2;
			}
			HashMap<Integer,Value> lValues = new HashMap<Integer,Value>();
			ArrayList<Issue> lIssues = utilitySpace.getDomain().getIssues();
			int fNumberOfIssues = lIssues.size();
			for(i=0;i<fNumberOfIssues;i++) {
				Issue lIssue = lIssues.get(i);	
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
				String currentValueStr = issueToValue.get(lIssue.getName().toLowerCase());
				int lOptionIndex = lIssueDiscrete.getValueIndex(currentValueStr);
				lValues.put(lIssue.getNumber(), lIssueDiscrete.getValue(lOptionIndex));
			}
		
			bid = new Bid(utilitySpace.getDomain(),lValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bid;
	}

	/** 
     * Called when a message of type:
     * QUERY, COUNTER_OFFER, OFFER or PROMISE 
     * is received
     * Note that if you accept a message, the accepted message is saved in the 
     * appropriate structure, so no need to add logic for this.
     * @param nMessageType - the message type
     * @param currentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     */
    public void calculateResponse(int nMessageType, int[] currentAgreementIdx, String sOriginalMessage) {
		if (currentAgreementIdx==null)
			throw new NullPointerException("currentAgreementIdx is null");
		if (waitForOpponentFirstOffer)
			waitForOpponentFirstOffer = false;
        //Calculating the response
        //You can decide on your actions for that turn
        //You can decide on different logic based on the message type
        //In case you accept an offer, you might decide NOT to
        //send an offer you calculated before and just waited for
        //it to be sent. To do so, use the "send flag" as in
        //the example below 
        //1 - if the newly offer has lower utility values than already
        //accepted agreement, reject it;
        //2 - the offer has higher utility then the acceptance threshold for this turn - accpet
        //don't send any offer of your own
        //3 - else, reject
    	_opponentRejectedMyLastOfferOrMadeCounterOffer=true; //it wasn't rejected but we got counter offer
    	// update opponent type probabilty according to this message
    	Global.logStdout("KBAgent.CalculateReponse", "Before updateOpponentProbability", null);
    	updateOpponentProbability(currentAgreementIdx,nMessageType,agentTools.getCurrentTurn(),AutomatedAgentMessages.MESSAGE_RECEIVED);
       
        // decide whether to accept the message or reject it:

        // Check the utility value of the opponent's offer
        // if this isn't full offer - complete the utilities using the average.
        double dOppOfferValueForAgentNotCompleted = agentTools.getAgreementValue(currentAgreementIdx);
        double dOppOfferValueForAgent = getCompletedAgreementValue(currentAgreementIdx ,_possibleOpponents[_currentOpponent]); 
       
        
        // 1. check whether previous accepted agreement is better - if so, reject
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
        {
            // reject offer
        	delayIfNeeded();
            agentTools.rejectMessage(sOriginalMessage);
            logMassage("Offer rejected: turn = " + agentTools.getCurrentTurn() + " offer utility="  + dOppOfferValueForAgentNotCompleted + " completed to value=" + dOppOfferValueForAgent  + " alredy accepted offer with utility =" + dAcceptedAgreementValue);
            // if we didn't sent offer yet send one now
            if (agentTools.getCurrentTurn() - _lastTurnOfOffer>0)
    			calculateOfferAgainstOpponent(_agentType, _opponentType, agentTools.getCurrentTurn());
            return;
        }
        
        // 2. check the acceptance threshold for the current turn
       
        if (_currentOpponent<0)
        	throw new ArrayIndexOutOfBoundsException("_currentOpponent="+_currentOpponent);
        if (agentTools.getCurrentTurn()<=0)
        	throw new ArrayIndexOutOfBoundsException("agentTools.getCurrentTurn()="+agentTools.getCurrentTurn());
        if (dOppOfferValueForAgent >= _possibleOpponents[_currentOpponent]._agentAcceptThersholds[agentTools.getCurrentTurn()-1])
        {
            // accept offer
        	delayIfNeeded();
            agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
            logMassage("Offer accepted: turn = " + agentTools.getCurrentTurn() + " offer utility= "  +  dOppOfferValueForAgentNotCompleted + " completed to value=" + dOppOfferValueForAgent  +  " type of opponent = " + _currentOpponent + " threshold= " + _possibleOpponents[_currentOpponent]._agentAcceptThersholds[agentTools.getCurrentTurn()-1]);

        }
        else
        {
            // reject offer
        	logMassage("Offer rejected: turn = " + agentTools.getCurrentTurn() + " offer utility= "  +  dOppOfferValueForAgentNotCompleted + " completed to value=" + dOppOfferValueForAgent  +  " type of opponent = " + _currentOpponent + " threshold= " + _possibleOpponents[_currentOpponent]._agentAcceptThersholds[agentTools.getCurrentTurn()-1]);
        	delayIfNeeded();
            agentTools.rejectMessage(sOriginalMessage);
            // if we didn't sent offer yet send one now
            if (agentTools.getCurrentTurn() - _lastTurnOfOffer>0)
    			calculateOfferAgainstOpponent(_agentType, _opponentType, agentTools.getCurrentTurn());
            
        }
    }
    
    /**
	 * @param currentAgreementIdx indices to bid values; one index for each issue. 
	 * AutomatedAgentType.NO_VALUE means that this issue was not discussed (it will be null in the bid; utility will be completed using the mean values for each issue).
     * @return utility value of the given agreement for the current agent.
	 */
    double getCompletedAgreementValue(int[] currentAgreementIdx,OpponentData od) {
		
    	double dAgreementValue = agentTools.getAgreementValue(currentAgreementIdx);
				
		try {
			// build Hashmap and create the next bid.
			ArrayList<Issue> lIssues = utilitySpace.getDomain().getIssues();
			int fNumberOfIssues = lIssues.size();
			for(int i=0;i<fNumberOfIssues;i++) {
				Issue lIssue = lIssues.get(i);				
				if (currentAgreementIdx[i]==AutomatedAgentType.NO_VALUE)
				{
					double dUtilityForIssue;
					if (od._defaultValues.containsKey(lIssue.getName().toLowerCase()))
						dUtilityForIssue = od._defaultValues.get(lIssue.getName().toLowerCase()).doubleValue();
					else
						dUtilityForIssue = 0;
					
					
					double dAttributeWeight = utilitySpace.getWeight(i) * utilitySpace.getWeightMultiplyer();
					double dCurrentIssueValue = (dUtilityForIssue * dAttributeWeight);
					dAgreementValue += dCurrentIssueValue;			
				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return dAgreementValue;
	}
        
    private void logMassage(String massage) {
    	 PrintWriter pw = openPrintWriterForAppend(PROBABILTY_FILE_NAME + AutomatedAgent.SIDE_A_NAME + ".txt");
    	 if (pw!=null) {
             pw.println(massage);
             pw.close();
    	 }
	}
    
    private PrintWriter openPrintWriterForAppend(String name) {
    	File file = new File(name);
        try {
            return new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
        	System.out.println("KBAgent Warning: cannot append to "+file.getAbsolutePath());
        	return null;
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
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		updateOpponentProbability(CurrentAgreementIdx,nMessageType,agentTools.getCurrentTurn(),AutomatedAgentMessages.MESSAGE_REJECTED);
		_opponentRejectedMyLastOfferOrMadeCounterOffer=true;
		if (agentTools.getCurrentTurn() - _lastTurnOfOffer>0)
			calculateOfferAgainstOpponent(_agentType, _opponentType, agentTools.getCurrentTurn());
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
        
    	if (waitForOpponentFirstOffer)
    		return;
    	
        // just send the offer from the file if it better then the one
    	// already agreed.
    	// the function ignore agent types as it assumed to be learned from the db
    	// update opponentData for the current turn
    	if (!_opponentRejectedMyLastOfferOrMadeCounterOffer && nCurrentTurn-_lastTurnOfOffer<TURNS_BETWEEN_OFFERS)
    		return;
    	for (int i=0;i<AGENT_TYPES_NUM;i++)
    		calculateTotalAgreementPerTurn(_possibleOpponents[i], nCurrentTurn);
    	int offerIndex=_possibleOpponents[_currentOpponent]._lastOfferIndex++;
    	Bid newOffer=_possibleOpponents[_currentOpponent]._agentOffers[offerIndex];
    	//// next 2 lines make sure that the Capital letters are correct - 
    	//// should contain a change to getMessageIndicesByMessage to compare lower case strings
        ////int currentAgreementIdx[] = agentTools.getMessageIndicesByMessage(newOffer);
        ////newOffer = agentTools.getMessageByIndices(currentAgreementIdx);
    	
        
		try {
			double dCurrentAgreementValue = utilitySpace.getUtilityWithTimeEffect(newOffer, nCurrentTurn);
	        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue();
	        // check if the new offer value is better then the already accepted offer
	        if (dAcceptedAgreementValue < dCurrentAgreementValue)
	        {
	        	_lastTurnOfOffer=nCurrentTurn;
	        	_opponentRejectedMyLastOfferOrMadeCounterOffer=false;
	        	Offer action=new Offer(getAgentID(),newOffer);

	        	delayIfNeeded();
	        	sendAction(action);	        	
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        // if the current offer worse then the accepted offer don't do any thing
    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     * 
     * 
     * PROBABLY DOES NOT DO A THING !!
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

    public void updateOpponentProbability(int CurrentAgreementIdx[], int nCurrentTurn, int nMessageType, int nResponseType)
    {
        if (nResponseType == AutomatedAgentMessages.MESSAGE_RECEIVED)
            updateOpponentProbabilityUponMessageReceived(CurrentAgreementIdx, nCurrentTurn, nMessageType);
        else if (nResponseType == AutomatedAgentMessages.MESSAGE_REJECTED)
            updateOpponentProbabilityUponMessageRejected(CurrentAgreementIdx, nCurrentTurn, nMessageType);
        _currentOpponent=0;
        for (int i=1;i<AGENT_TYPES_NUM;i++)
        {
        	if (_possibleOpponents[i]._probability>_possibleOpponents[_currentOpponent]._probability)
        		_currentOpponent=i;
        }
        	
    }
    
    private void updateOpponentProbabilityUponMessageReceived(int CurrentAgreementIdx[], int nCurrentTurn, int nMessageType)
    {
        AutomatedAgentType agentType = null;
        double dPrevTypeProbability = 0;
        double dPrevOfferValue = 0;
        double dPrevOfferProbability = 0;
	    double dOfferSum = 0;
	    double dUpdatedTypeProbability = 0;
	    
  
        // calculate posteriori proability using Bayes formula:
        // P(type | Ht) = [P(Ht|Type)*P(type)] / P(Ht)
        // where P(Ht) = sigam(i=1 to #types)[P(Ht|Type_i) * P(type_i)]
        // and P(Ht|Type_i) = luce number of Ht (Ht - last agreement)
        // [this is done incrementally after each agreement

        // calculate P(Ht)
        for (int i = 0; i < AGENT_TYPES_NUM; ++i)
        {
            agentType = agentTools.getCurrentTurnSideAgentType(_opponentType, i);
                    
            dPrevTypeProbability = _possibleOpponents[i]._probability;
            dPrevOfferValue = agentType.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
            dPrevOfferValue = Math.exp(dPrevOfferValue * PRECISION_VALUE);
            dPrevOfferProbability = dPrevOfferValue / _possibleOpponents[i]._allOffersUtilitySum;
            
            dOfferSum += (dPrevOfferProbability * dPrevTypeProbability);
        }

        // calculate P(type | Ht) and update P(type)
        for (int i = 0; i < AGENT_TYPES_NUM; ++i)
        {
        	agentType = agentTools.getCurrentTurnSideAgentType(_opponentType, i);
                    
        	dPrevTypeProbability = _possibleOpponents[i]._probability;
            dPrevOfferValue = agentType.getAgreementValue(CurrentAgreementIdx, nCurrentTurn);
            dPrevOfferValue = Math.exp(dPrevOfferValue * PRECISION_VALUE);
            dPrevOfferProbability = dPrevOfferValue / _possibleOpponents[i]._allOffersUtilitySum;
            
            dUpdatedTypeProbability = (dPrevOfferProbability * dPrevTypeProbability) / dOfferSum;
            
            _possibleOpponents[i]._probability = dUpdatedTypeProbability;
        
        }
        Global.logStdout("KBAgent.updateOpponentProbabilityUponMessageReceived", "Before opening the file", null);
   	 	PrintWriter pw = openPrintWriterForAppend(PROBABILTY_FILE_NAME + AutomatedAgent.SIDE_A_NAME + ".txt");
        if (pw!=null) {
            for (int i=0;i<AGENT_TYPES_NUM;i++)
            	pw.print(_possibleOpponents[i]._opponentType + ":" + Double.toString(_possibleOpponents[i]._probability) + ",");
            pw.println();
            pw.close();
        } 
    }
    
    private void updateOpponentProbabilityUponMessageRejected(int CurrentAgreementIdx[], int nCurrentTurn, int nMessageType)
    {
        AutomatedAgentType agentType = null;
        double dPrevTypeProbability = 0;
        double dPrevOfferProbability = 0;
        double dOfferSum = 0;
        double dUpdatedTypeProbability = 0;
        double dAgentOfferSum = 0;
        
        String sRejectedMsg = _agentType.getAgreementStr(CurrentAgreementIdx);
        
   
        // calculate posteriori proability using Bayes formula:
        // P(type | Ht) = [P(Ht|Type)*P(type)] / P(Ht)
        // where P(Ht) = sigma(i=1 to #types)[P(Ht|Type_i) * P(type_i)]
        // and P(Ht|Type_i) = luce number of Ht (Ht - last agreement)
        // [this is done incrementally after each agreement

        // calculate P(Ht)
        for (int i = 0; i < AGENT_TYPES_NUM; ++i)
        {
            agentType = agentTools.getCurrentTurnSideAgentType(_opponentType, i);
                    
            dOfferSum += calcRejectionProbabilities(CurrentAgreementIdx, nCurrentTurn,_possibleOpponents[i]);
        }

        // calculate P(type | Ht) and update P(type)
        for (int i = 0; i < AGENT_TYPES_NUM; ++i)
        {
        	agentType = agentTools.getCurrentTurnSideAgentType(_opponentType, i);

            dPrevTypeProbability = _possibleOpponents[i]._probability;
            
            dAgentOfferSum = calcRejectionProbabilities(CurrentAgreementIdx, nCurrentTurn,_possibleOpponents[i]);

            dUpdatedTypeProbability = (dAgentOfferSum * dPrevTypeProbability) / dOfferSum;
            
            
            _possibleOpponents[i]._probability=dUpdatedTypeProbability;
        
        }
        
        PrintWriter pw = openPrintWriterForAppend(PROBABILTY_FILE_NAME + AutomatedAgent.SIDE_A_NAME + ".txt");
        if (pw!=null) {
            for (int i=0;i<AGENT_TYPES_NUM;i++)
            	pw.print(_possibleOpponents[i]._opponentType + ":" + Double.toString(_possibleOpponents[i]._probability) + ",");
            pw.println();
            pw.close();
        }
    }
                
            
    // calculate probabilities and values upon rejection
    public double calcRejectionProbabilities(int CurrentAgreementIdx[], int nCurrentTurn,OpponentData opponentData)
    {
    	double dMessageValue=agentTools.getAgreementValue(agentTools.getCurrentTurnSideAgentType(_opponentType, opponentData._opponentTypeIDX), CurrentAgreementIdx, nCurrentTurn);
    	Bid sMessege = agentTools.getBidFromIndices(CurrentAgreementIdx);
        //double dMessageValue = agentTools.getAgreementValue(CurrentAgreementIdx);
        double dPrevTypeProbability = opponentData._probability;
        double dOfferValue = 0;
        double dOffersSum = 0;
        double dOfferProbability = 0;
        int totalAgreements=agentTools.getTotalAgreements(_agentType);
        boolean bSameOffer=false;
        for (int i = 0; i < totalAgreements; ++i)
        {
        	if (opponentData._sortedAllAgreements[i].agreement.equals(sMessege)) // wait till we pass the offer rejected
        	{
        		bSameOffer=true;
        		continue;
        	}
        	if (opponentData._sortedAllAgreements[i].value >= dMessageValue && bSameOffer) // sum all offers with better rank
            {
                dOfferValue = Math.exp(opponentData._sortedAllAgreements[i].value * PRECISION_VALUE);
                dOfferProbability = dOfferValue/opponentData._allOffersUtilitySum;
                
                dOffersSum += (dOfferProbability * dPrevTypeProbability);
            }
       
	          
           
        }
        
        return dOffersSum;
    }




	/* Erel: add support for delay */
	private static Random rnd = new Random();
	public static boolean delayResponse = true;
	public static int delayMeanMillis = 5000;
	public static int delayStdMillis = 2000;
	private static void delayIfNeeded() {
		if (delayResponse) {
			int delay = (new Double(delayMeanMillis + rnd.nextGaussian() * delayStdMillis)).intValue();
			Global.logStdout("KBAgent", "delay="+delay, "");
			if (delay > 0) {
    			try {	Thread.sleep(delay);	} 
    			catch (InterruptedException e) {	e.printStackTrace();	}
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		KBAgent agent1 = new KBAgent();
		agent1.init();
	}
}
    
