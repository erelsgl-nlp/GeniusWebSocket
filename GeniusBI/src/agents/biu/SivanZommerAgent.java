package agents.biu;


import java.util.Arrays;
import java.util.Enumeration;

import java.util.Hashtable;


public class SivanZommerAgent extends OldAgentAdapter
{
    double THRESHOLD = 0.1;
    double COMPROMISE_THRESHOLD = 0.25;
    
    int MESSAGE_FREQUENCY = 11;
    
    //private double opponentValue = 0;
    private AutomatedAgentType _opponentGuessType = null;
    String _opponentType = null;
    
    private double[] _shortTermAgrrementValues;
    private double[] _longTermAgrrementValues;
    private double[] _compromiseAgrrementValues;
    AutomatedAgentType _agentType;
    
    private int _numOfGuessShortTerm = 0;
    private int _numOfGuessLongTerm = 0;
    private int _numOfGuessCompromise = 0;
        
    private int OPPONENT_REJECT = 0;
    private int OPPONENT_ACCEPT = 1;
    private int OPPONENT_SENT_OFFER = 2;
    
    
    Hashtable _acceptedOffersTbl;
    Hashtable _offeredMessagesTbl;
    
    
    public SivanZommerAgent() {
    	super();
    	_acceptedOffersTbl = new Hashtable();
        _offeredMessagesTbl = new Hashtable();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public SivanZommerAgent(AgentTools agentTools)
    {
        this.agentTools = agentTools;
        _acceptedOffersTbl = new Hashtable();
        _offeredMessagesTbl = new Hashtable();
    }
    
	/**
	 * Called before the the negotiation starts.
	 * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType)
	{
		_opponentType = sOpponentType;
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
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage)
    {    
    	int currentTurn = agentTools.getCurrentTurn();
    	// Define the types of opponents
		AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;
        
        if(nMessageType == AutomatedAgentMessages.OFFER || nMessageType == AutomatedAgentMessages.COUNTER_OFFER )
        {
        	// Short term agreement value
	        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
	        double valueShortTerm = agentTools.getAgreementValue(agentOpponentShortTerm,CurrentAgreementIdx,currentTurn);
			
	        // Long term agreement value
	        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
	        double valueLongTerm = agentTools.getAgreementValue(agentOpponentLongTerm,CurrentAgreementIdx,currentTurn);
			
		    // Compromise agreement value
		    agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		    double valueCompromise = agentTools.getAgreementValue(agentOpponentCompromise,CurrentAgreementIdx,currentTurn);
		       
		    discoverOpponentType(OPPONENT_ACCEPT, valueShortTerm, valueLongTerm, valueCompromise);
        
            // Save the offer in hashtable for future use
		    addOfferToTable(sOriginalMessage);
	          
		    // decide whether to accept the message or reject it:
	        double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
	        double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
	
	        // Check the utility value of the opponent's offer
	        dOppOfferValueForAgent = agentTools.getAgreementValue(CurrentAgreementIdx);
	                           
	        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
	        
	        // If the same message was sent more then 11 time, accept it in the last turn if it's the best choice to do
		    if(agentTools.getCurrentTurn() >= agentTools.getTurnsNumber()-1)
		    {
		    	if(_offeredMessagesTbl != null && _offeredMessagesTbl.size() > 0)
		    	{
		    		int messageFreq = getMessageFrequency(sOriginalMessage);
		    		// if the message exists
		    		if(messageFreq != -1)
		    		{
		    			if(messageFreq >= MESSAGE_FREQUENCY)
		    			{
		    				double optOutValue = agentTools.getOptOutValue(_agentType);
		    			   	double sQValue = agentTools.getSQValue(_agentType);
					    	if(dOppOfferValueForAgent >= optOutValue && dOppOfferValueForAgent >= sQValue)
					    	{
					    		// accept offer
					            agentTools.acceptMessage(sOriginalMessage);         
					            //prevent sending future offer in this turn
					            agentTools.setSendOfferFlag(false);
					    	}
		    			}
		    		}
		    	}		    			    	
		    }
	        
	        // If the previous accepted agreement has higher utility - reject the offer in case the difference between the 
	        // offers is more than 10%
	        if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
	        {
	        	double difference = (dAcceptedAgreementValue - dOppOfferValueForAgent)/(dAcceptedAgreementValue + dOppOfferValueForAgent);
	        	if(difference > THRESHOLD || dOppOfferValueForAgent < 0)
	        	{
	        		// reject offer
	                agentTools.rejectMessage(sOriginalMessage);
	                return;
	        	}      	            
	        }
	        
	        // Check the value of the automated agent in the next turn
	        agentTools.calculateNextTurnOffer();
	        dAutomatedAgentNextOfferValueForAgent = agentTools.getNextTurnOfferValue();
	
	        // If the offer is better than the agent offer - accept the higher offer
	        if (dOppOfferValueForAgent >= dAutomatedAgentNextOfferValueForAgent)
	        {        	
	            // accept offer
	            agentTools.acceptMessage(sOriginalMessage);         
	            //prevent sending future offer in this turn
	            agentTools.setSendOfferFlag(false);
	        }
	        
	        // If the oppenent's offer has lower utility, 
	        // check the difference between the offers - if it is more than 10% - reject
	        // otherwise - accept
	        else
	        {
	        	double difference = (dAcceptedAgreementValue - dOppOfferValueForAgent)/(dAcceptedAgreementValue + dOppOfferValueForAgent);
	        	if(difference > THRESHOLD && dOppOfferValueForAgent < 0)
	        	{
	        		// reject offer
	                agentTools.rejectMessage(sOriginalMessage);
	                return;
	        	}
	        	else
	        	{
	        		// accept offer
	                agentTools.acceptMessage(sOriginalMessage);
	                
	                //prevent sending future offer in this turn
	                agentTools.setSendOfferFlag(false);        		
	        	}                                    
	        }
        }
    }
        
    /***********************************************
     * @@ Logic for receiving messages
     * Below are messages the opponent sends to the automated agent
     ***********************************************/
    
    /**
	 * called whenever we get a comment from the opponent
     * @param sComment -the received comment
	 */
	public void commentReceived(String sComment) 
	{
       
    }

	
	/**
	 * called whenever we get a threat from the opponent
     * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) 
	{
		
    }
	
	/**
	 * called whenever the opponent agreed to one of your massages (promise, query, offer or counter offer).
     * NOTE: if an OFFER is accepted, it is saved in the appropriate structure. No need to add logic for this.
	 * @param nMessageType - the type of massage the oppnent aggreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage)
	{
		// If the opponent agreed to query - save the offer for future handling
		if(nMessageType == AutomatedAgentMessages.QUERY)
		{
			if(_acceptedOffersTbl != null)
			{
				if(!_acceptedOffersTbl.containsKey(sOriginalMessage))
				{
					_acceptedOffersTbl.put(sOriginalMessage,agentTools.getMessageByIndices(CurrentAgreementIdx));
				}				
			}
		}
		
		double maximalValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		int currentTurn = agentTools.getCurrentTurn();
				
		// Define the types of opponents
		AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;
		
		// If the opponent agreed to an offer, check the offer value for each type of the opponent.
		// By the maximal value of the offer, guess the opponent type;
		if(nMessageType == AutomatedAgentMessages.OFFER)
		{
			if(_opponentType != null)
	        {  	  
				 // Short term agreement value
	             agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
	             double valueShortTerm = agentTools.getAgreementValue(agentOpponentShortTerm,CurrentAgreementIdx,currentTurn);
	    		
	            // Long term agreement value
	            agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
	            double valueLongTerm = agentTools.getAgreementValue(agentOpponentLongTerm,CurrentAgreementIdx,currentTurn);
	    		
	            // Compromise agreement value
	            agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
	            double valueCompromise = agentTools.getAgreementValue(agentOpponentCompromise,CurrentAgreementIdx,currentTurn);
	            
	            discoverOpponentType(OPPONENT_ACCEPT, valueShortTerm, valueLongTerm, valueCompromise);
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
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage)
	{
		// When the opponent rejects an offer, check the value of this offer for each type of opponent.
		// Check which value is the minimal and according to this information, guess the opponent type.
		
		int currentTurn = agentTools.getCurrentTurn();
				
		// Define the types of opponents
		AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;
              
        
        if(_opponentType != null)
        {  	
        	 // Short term agreement value
            agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
            double valueShortTerm = agentTools.getAgreementValue(agentOpponentShortTerm,CurrentAgreementIdx,currentTurn);
   		
           // Long term agreement value
           agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
           double valueLongTerm = agentTools.getAgreementValue(agentOpponentLongTerm,CurrentAgreementIdx,currentTurn);
   		
           // Compromise agreement value
           agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
           double valueCompromise = agentTools.getAgreementValue(agentOpponentCompromise,CurrentAgreementIdx,currentTurn);
           
           discoverOpponentType(OPPONENT_REJECT, valueShortTerm, valueLongTerm, valueCompromise);
    	}
        
				
		// If the agent get an offer reject - send another offer
		// The offer that will be sent will be chosen from the agreed massages
		if(nMessageType == AutomatedAgentMessages.OFFER)
		{
			if(_acceptedOffersTbl != null)
			{
				String maxMessage = getMaximalAcceptedOffer();
				if(maxMessage != null)
				{
					agentTools.sendOffer(maxMessage);					
				}
			}			
		}        
    }
    
    /***********************************************
     * @@ End of methods for receiving message
     ***********************************************/
 
    
    /**
     * called to decide which offer to propose the opponent at a given turn
     * This method is always called when beginning a new turn
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) 
    {    	
    	// Indicates whether  to compromise according to turn number
    	boolean compromise = false;
    	
    	// calculate Automated Agent offer
        //double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Get the total issues
        int totalIssuesNum = agentTools.getTotalIssues(agentType);
        // Get total agreements
        int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
        
        // Get the value of previously accepted agreement
        double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
        
        // Define array to contain the agreements and maximum issue value 
        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
        }
        
        // the different possible agents for the opponent side
        AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;

        agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
        agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
        agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);                
            
        // Will contain the maximal agreement between all possible opponent types
        double dOpponentMaximalAgreementValue = 0;
        int maximalOpponentIdx[] = new int[totalIssuesNum];
        boolean isMaximal = false;
        
        // Will contain the difference between the maximal opponent agreement value and the agent agreement value
        double dMinDifferenceValue = 9999;
        double dCurrentDiffValue = 0;
        double dChosenAgentValue = 0;
        
        int chosenAgreementIdx[] = new int[totalIssuesNum];
        
        int OpponentLongTermIdx[] = new int[totalIssuesNum];
        double dOpponentLongTermAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        int OpponentShortTermIdx[] = new int[totalIssuesNum];
        double dOpponentShortTermAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        int OpponentCompromiseTermIdx[] = new int[totalIssuesNum];
        double dOpponentCompromiseAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        double dAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Check if the negotiation has reached more than half of the turns
        int numOfTurnsToCompromise = (agentTools.getTurnsNumber()/2)+1;
        
        int currentTurn = agentTools.getCurrentTurn();        
        
        double maximalValueOfAgreemnt = AutomatedAgentType.VERY_SMALL_NUMBER;
        double agentMaximalValueOfAgreement = AutomatedAgentType.VERY_SMALL_NUMBER;
        
        // Check if half of the negotiation passed
        if(agentTools.getCurrentTurn()>= numOfTurnsToCompromise)
        {
        	compromise = true;
        	// Save the indices of the previously accepted agreements 
        	double dPreviousAcceptedValue = agentTools.getAcceptedAgreementsValue();
        	// Go over all possible agreements
            for (int i = 0; i < totalAgreementsNumber; ++i)
            {
            	// Get the agreement value
                dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
             
                // If the agreement value is higher then the previous accepted value
                if(dAutomatedAgentAgreementValue > dPreviousAcceptedValue)
                {
                	double difference = (dAutomatedAgentAgreementValue - dPreviousAcceptedValue)/(dAutomatedAgentAgreementValue + dPreviousAcceptedValue);
                    if((difference <= COMPROMISE_THRESHOLD) && (difference > 0) && dAutomatedAgentAgreementValue > 0)
                    {
                    	agentTools.setCurrentTurnAutomatedAgentValue(dAutomatedAgentAgreementValue);
                        agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(CurrentAgreementIdx)); 
                        String sOffer = agentTools.getSelectedOffer();
        	        	agentTools.sendOffer(sOffer);
        	        	break;
                    }
                	
                }
                agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);            
            }
        }
        
        //Check if the agent guessed his opponent type
        else 
        {   
        	// Guess the opponent type
        	setOpponentTypeGuessed();
        	
        	if(_opponentGuessType != null)
        	{
	        	// Go over all possible agreements for the agent type
	        	for (int i = 0; i < totalAgreementsNumber; ++i)
	            {
	        		// Get the agreement value for the opponent that was guessed
	        		double opponentValue = agentTools.getAgreementValue(_opponentGuessType, CurrentAgreementIdx, nCurrentTurn);
	        		dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
	        		// If the agreement value is maximal
	        		if(opponentValue > maximalValueOfAgreemnt && dAutomatedAgentAgreementValue > agentMaximalValueOfAgreement)
	        		{
	        			maximalValueOfAgreemnt = opponentValue;
	        			agentMaximalValueOfAgreement = dAutomatedAgentAgreementValue;
	        			dOpponentMaximalAgreementValue = opponentValue;
	        			for (int j = 0; j < totalIssuesNum; ++j) 
	     	            {
	        				 chosenAgreementIdx[j] = CurrentAgreementIdx[j];
	     	            }
	        			
	        			// Calculate the difference
	    	            if(dOpponentMaximalAgreementValue >= dAutomatedAgentAgreementValue)
	    	            	dCurrentDiffValue = dOpponentMaximalAgreementValue - dAutomatedAgentAgreementValue;
	    	            else dCurrentDiffValue = dAutomatedAgentAgreementValue - dOpponentMaximalAgreementValue;
	    	            // Now, that I have the difference check if it is the minimal
	    	            if(dCurrentDiffValue < dMinDifferenceValue  && dAutomatedAgentAgreementValue > 0)
	    	            {
	    	            	dChosenAgentValue = dAutomatedAgentAgreementValue;
	    	            	dMinDifferenceValue = dCurrentDiffValue;
	    	            	// Save the indices of the agreement
	    	            	for (int j = 0; j < totalIssuesNum; ++j) 
	    	                {
	    	                   chosenAgreementIdx[j] = CurrentAgreementIdx[j];
	    	                }    	            	
	    	            }
	        		}
	        		agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues); 
	            }
        	}
        
	        // The opponent type was not guessed by the agent
	        // Calculate the next offer using all opponent types
	        else
	        {       	
	        	// Go over all possible agreements
		        for (int i = 0; i < totalAgreementsNumber; ++i)
		        {
		        	// Get the agreement value
		            dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
		                      
		            // Calculate the agreement value for all opponent types
		            
		            // Long term
		            dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, CurrentAgreementIdx, nCurrentTurn);
		            // save the indices of that offer
		            for (int j = 0; j < totalIssuesNum; ++j) 
		            {
		               OpponentLongTermIdx[j] = CurrentAgreementIdx[j];
		            }
		            
		            // Short term
		            dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, CurrentAgreementIdx, nCurrentTurn);
		            // save the indices of that offer
		            for (int j = 0; j < totalIssuesNum; ++j) 
		            {
		              OpponentShortTermIdx[j] = CurrentAgreementIdx[j];
		            }
		
		            // Compromise
		            dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, CurrentAgreementIdx, nCurrentTurn);
		            // save the indices of that offer
		            for (int j = 0; j < totalIssuesNum; ++j) 
		            {
		               OpponentCompromiseTermIdx[j] = CurrentAgreementIdx[j];
		            }
	    	                        
		            // Check which value is maximal            
		            	                               
		            // Long term
		            isMaximal = isAgreementValueMaximal(dOpponentLongTermAgreementValue, dOpponentMaximalAgreementValue);
		            if(isMaximal)
		            {
		            	// Set the maximal agreement value and indices
	            	dOpponentMaximalAgreementValue = dOpponentLongTermAgreementValue;
		            	for (int j = 0; j < totalIssuesNum; j++)
		            	{
							maximalOpponentIdx[j] = OpponentLongTermIdx[j];
						}
		            }
		            
		            // Short term
		            isMaximal = isAgreementValueMaximal(dOpponentShortTermAgreementValue, dOpponentMaximalAgreementValue);
		            if(isMaximal)
		            {
		            	dOpponentMaximalAgreementValue = dOpponentShortTermAgreementValue;
		            	for (int j = 0; j < totalIssuesNum; j++)
		            	{
							maximalOpponentIdx[j] = OpponentShortTermIdx[j];
						}
		            }
		            // Compromise
		            isMaximal = isAgreementValueMaximal(dOpponentCompromiseAgreementValue, dOpponentMaximalAgreementValue);
		            if(isMaximal)
		            {
		            	dOpponentMaximalAgreementValue = dOpponentCompromiseAgreementValue;
		            	for (int j = 0; j < totalIssuesNum; j++)
		            	{
							maximalOpponentIdx[j] = OpponentCompromiseTermIdx[j];
						}
		            	
		            }
		            
		            if(dAutomatedAgentAgreementValue > agentMaximalValueOfAgreement)
		            {
		            	agentMaximalValueOfAgreement = dAutomatedAgentAgreementValue;
		            	// Calculate the difference
			            if(dOpponentMaximalAgreementValue >= dAutomatedAgentAgreementValue)
			            	dCurrentDiffValue = dOpponentMaximalAgreementValue - dAutomatedAgentAgreementValue;
			            else dCurrentDiffValue = dAutomatedAgentAgreementValue - dOpponentMaximalAgreementValue;
			            // Now, that I have the difference check if it is the minimal
			            if(dCurrentDiffValue < dMinDifferenceValue && dAutomatedAgentAgreementValue > 0)
			            {
			            	dChosenAgentValue = dAutomatedAgentAgreementValue;
			            	dMinDifferenceValue = dCurrentDiffValue;
			            	for (int j = 0; j < totalIssuesNum; ++j) 
			                {
			                   chosenAgreementIdx[j] = CurrentAgreementIdx[j];
			                }    	            	
			            }
		            }
		            agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
		        } // end for - going over all possible agreements
	        }
        }
        //select which offer to propose           
       
        // Select the offer with the maximal value from the different types of opponents
        if (dChosenAgentValue > agentTools.getCurrentTurnAutomatedAgentValue() && dChosenAgentValue > 0)
        {
            agentTools.setCurrentTurnAutomatedAgentValue(dChosenAgentValue);
            agentTools.setCurrentTurnOpponentSelectedValue(dOpponentMaximalAgreementValue);
            agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(chosenAgreementIdx));
        }

        
        // Check the value of the offer 
        double dNextAgreementValue = agentTools.getSelectedOfferValue();
        
        
        // If it is the last turn, check which action has the higher value
        if(currentTurn == agentTools.getTurnsNumber())
        {
        	double acceptedAgreement = agentTools.getAcceptedAgreementsValue();
        	double optOutValue = agentTools.getOptOutValue(agentType);
            double sqValue = agentTools.getSQValue(agentType);
            
            // Check if opt out has higher value than the previous accepted offer
            if(optOutValue > sqValue && optOutValue > acceptedAgreement)
          		agentTools.optOut();            	        
         }
        
        
        // Now, check whether the offer the agent intends to propose in the next turn is better
        // than previously accepted agreement
        
        // If the value of the offer is less then 10% from already accepted offer - send it
        
        // Indicates whether to send the offer
        boolean sendOffer = false;
        
        if (dAcceptedAgreementValue >= dNextAgreementValue )
        {
        	double difference = (dAcceptedAgreementValue - dNextAgreementValue)/ (dAcceptedAgreementValue + dNextAgreementValue);
        	if(difference <= THRESHOLD && dNextAgreementValue > 0)
        	{
        		sendOffer = true;
        	}
        	// Otherwise - don't sent the offer because it is insufficient
            // previously accepted offer has better score
   
            // so - don't send the offer
        }
        else sendOffer = true;
	    // if decided to send offer - then send the offer
        //Get the offer as string and format it as an offer
	    
        if(sendOffer)
	    {
	       	String sOffer = agentTools.getSelectedOffer();
	       	agentTools.sendOffer(sOffer);
	    }    	        
             
      // Now, the agent's core holds the new selected agreement
    }
    
    
    
    
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn)
    {
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
        
        //Get the value of the Status Quo and Opting-Out values as time increases
        double dAgreementTimeEffect = agentTools.getAgreementTimeEffect(agentType); 
        double dStatusQuoValue = agentTools.getSQValue(agentType);
        double dOptOutValue = agentTools.getOptOutValue(agentType);
               
               
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
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
    
    
    
    /**
     * Check if the current agreement value is bigger than the maximal value that was calculated before.
     * @param currentAgreement
     * @param maxValue
     * @return
     */
    private boolean isAgreementValueMaximal(double currentAgreement, double maxValue)
    {
    	if(currentAgreement > maxValue)
    		return true;
    	return false;
    }
    
    /**
     * Go over the hashtable of all accepted massages and retrun the massage with the maximal value.
     * @return
     * 
     */
    private String getMaximalAcceptedOffer()
    {
    	double maxValue = 0;
    	String maxOfferKey = null;
    	// enumerate all the contents of the hashtable
    	Enumeration keys = _acceptedOffersTbl.keys();
    	while(keys.hasMoreElements())
    	{    		
    	   String key = (String)keys.nextElement();
    	   String value = (String)_acceptedOffersTbl.get(key);
    	   double currentValue = Double.parseDouble(value);
    	   if(currentValue >= maxValue)
    	   {
    		  currentValue = maxValue;
    		  maxOfferKey = key;
    	   }
    	}
    	return maxOfferKey;
    }  
      
    /**
     * Get a specified message frequency. if the message doen't exist, return -1.
     * @param message
     * @return
     */
    private int getMessageFrequency(String message)
    {
    	if(_offeredMessagesTbl != null && _offeredMessagesTbl.size() > 0)
    	{
    		Enumeration keys = _offeredMessagesTbl.keys();
    		while(keys.hasMoreElements())
        	{    		
        	   String key = (String)keys.nextElement();
        	   if(key.equalsIgnoreCase(message))
        	   {
        		   int value = (Integer)_offeredMessagesTbl.get(key);
    	    	   return value;
        	   }
        	}    
    	}
		return -1;
    	
    }
    
    private void addOfferToTable(String offer)
    {
    	Enumeration keys = _offeredMessagesTbl.keys();
    	if(_offeredMessagesTbl == null)
    		return;
    	if(_offeredMessagesTbl.size() == 0)
    	{
    		// Add a new value
    		_offeredMessagesTbl.put(offer, 0);
    		return;
    	}
    	while(keys.hasMoreElements())
    	{    		
    	   String key = (String)keys.nextElement();
    	   if(key.equalsIgnoreCase(offer))
    	   {
    		   int value = (Integer)_offeredMessagesTbl.get(key);
	    	   int currentOfferAccurance = value;
	    	   currentOfferAccurance ++;
	    	   //value = Integer.toString(currentOfferAccurance);
	    	   // Update the agreement frequency 
	    	   _offeredMessagesTbl.remove(key);
	    	   _offeredMessagesTbl.put(key, currentOfferAccurance);
	    	   return;
    	   }
    	}    	    	
    }

    
    /**
     * Try to discover the opponent type according to:
     * Accepted agreement, rejected agreements, agreement that he sends
     * @param messageType - the type of the opponent message
     * @param agreementValue - the agreement value that was accepted or rejected by the opponent
     */
    private void discoverOpponentType(int messageType, double shortTermValue, double longTermValue, double compromiseValue)
    {
    	rankOpponentAgreements();
    	
    	int compromiseIndex = getCurrentAgreementValueIndexForCompromiseOpponent(compromiseValue);
    	int longTermIndex = getCurrentAgreementValueIndexForLongTermOpponent(longTermValue);
    	int shortTermIndex = getCurrentAgreementValueIndexForShortTermOpponent(shortTermValue);
    	
    	AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
    	AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
    	AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
    	
    	// If the opponent accepted an offer or sent offer, try to find out his type by investigating all types
    	// of agreements of each opponent type and search for the highest value
    	if(messageType == OPPONENT_ACCEPT || messageType == OPPONENT_SENT_OFFER)
    	{
    		if(compromiseIndex > longTermIndex && compromiseIndex > shortTermIndex)
    		{
    			_opponentGuessType = agentOpponentCompromise;
    			_numOfGuessCompromise ++;
    		}
    		
    		else if(longTermIndex > shortTermIndex && longTermIndex > compromiseIndex)
    		{
    			_opponentGuessType = agentOpponentLongTerm;
    			_numOfGuessLongTerm ++;    			
    		}
    		
    		else if(shortTermIndex > longTermIndex && shortTermIndex > compromiseIndex)
    		{
    			_opponentGuessType = agentOpponentShortTerm;
    			_numOfGuessShortTerm ++;
    		}
    		
    		// Cannot select the opponent type
    		else
    		{
    			_opponentGuessType = null;
    		}
    	}
    	
    	// If the opponent rejected an offer - try to find out his type by investigating all types
    	// of agreements of each opponent type and search for the lowest value
    	if(messageType == OPPONENT_REJECT)
    	{
    		if(compromiseIndex < longTermIndex && compromiseIndex < shortTermIndex)
    		{
    			_opponentGuessType = agentOpponentCompromise;
    			_numOfGuessCompromise ++;
    		}
    		
    		else if(longTermIndex < shortTermIndex && longTermIndex < compromiseIndex)
    		{
    			_opponentGuessType = agentOpponentLongTerm;
    			_numOfGuessLongTerm ++;
    		}
    		
    		else if(shortTermIndex < longTermIndex && shortTermIndex < compromiseIndex)
    		{
    			_opponentGuessType = agentOpponentShortTerm;
    			_numOfGuessShortTerm ++;
    		}
    		
    		// Cannot select the opponent type
    		else
    		{
    			_opponentGuessType = null;
    		}
    	}
    }
    
    /**
     * Go over all agreements of each opponent type and rank them from the lowest value to the highest value.
     * The arranged agreements values of each opponent type will be stored in arrays.
     */
    private void rankOpponentAgreements()
    {
    	int currentTurn = agentTools.getCurrentTurn();
    	// Get the total issues
        int totalIssuesNum = agentTools.getTotalIssues(_agentType);
    	// Get total agreements
        int totalAgreementsNumber = agentTools.getTotalAgreements(_agentType);
        int MaxIssueValues[] = new int[totalIssuesNum];
        
        // Define array to contain the agreements and maximum issue value 
        int CurrentAgreementIdx[] = new int[totalIssuesNum];
        
        // initialize the agreement indices
        for (int i = 0; i < totalIssuesNum; ++i)
        {
            CurrentAgreementIdx[i] = 0;
            MaxIssueValues[i] = agentTools.getMaxValuePerIssue(_agentType, i);
        }
        
        // Define the types of opponents
		AutomatedAgentType agentOpponentCompromise = null;
        AutomatedAgentType agentOpponentLongTerm = null;
        AutomatedAgentType agentOpponentShortTerm = null;
        
        // Initialize the arrays
        _shortTermAgrrementValues = new double[totalAgreementsNumber];
        _longTermAgrrementValues = new double[totalAgreementsNumber];
        _compromiseAgrrementValues = new double[totalAgreementsNumber];
        
        // Go over all possible agreements for the agent type
    	for (int i = 0; i < totalAgreementsNumber; ++i)
        {
    		// Get the agreement value for each opponent type and put it in the appropriate array of values
    		 agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
             double valueShortTerm = agentTools.getAgreementValue(agentOpponentShortTerm,CurrentAgreementIdx,currentTurn);
             if(_shortTermAgrrementValues != null && i < _shortTermAgrrementValues.length)
            	 _shortTermAgrrementValues[i] = valueShortTerm;
    		
             agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
             double valueLongTerm = agentTools.getAgreementValue(agentOpponentLongTerm,CurrentAgreementIdx,currentTurn);
             if(_longTermAgrrementValues!= null && i < _longTermAgrrementValues.length)
            	 _longTermAgrrementValues[i] = valueLongTerm;
             
             agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
             double valueCompromise = agentTools.getAgreementValue(agentOpponentCompromise,CurrentAgreementIdx,currentTurn);
             if(_compromiseAgrrementValues != null && i < _compromiseAgrrementValues.length)
            	 _compromiseAgrrementValues[i] = valueCompromise;
    		
            // Go to the next agreement
    		agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx, MaxIssueValues); 
        }  
    	
    	// Sort the arrays
    	Arrays.sort(_shortTermAgrrementValues);
    	Arrays.sort(_longTermAgrrementValues);
    	Arrays.sort(_compromiseAgrrementValues);
    }
    
    
    /**
     * Go over array of agreement values and get the index of the agreement
     * @return
     */
    private int getCurrentAgreementValueIndexForShortTermOpponent(double agreementValue)
    {
    	int indexOfAgreement = 0;
    	if(_shortTermAgrrementValues != null && _shortTermAgrrementValues.length >= 0)
    	{
    		for (int i = 0; i < _shortTermAgrrementValues.length; i++) 
    		{
    			if(_shortTermAgrrementValues[i] == agreementValue)
    				indexOfAgreement = i;
			}
    	}
    	return indexOfAgreement;    	
    }
    
    
    private int getCurrentAgreementValueIndexForLongTermOpponent(double agreementValue)
    {
    	int indexOfAgreement = 0;
    	if(_longTermAgrrementValues != null && _longTermAgrrementValues.length >= 0)
    	{
    		for (int i = 0; i < _longTermAgrrementValues.length; i++) 
    		{
    			if(_longTermAgrrementValues[i] == agreementValue)
    				indexOfAgreement = i;
			}
    	}
    	return indexOfAgreement;    	
    }
    
    
    
    private int getCurrentAgreementValueIndexForCompromiseOpponent(double agreementValue)
    {
    	int indexOfAgreement = 0;
    	if(_compromiseAgrrementValues != null && _compromiseAgrrementValues.length >= 0)
    	{
    		for (int i = 0; i < _compromiseAgrrementValues.length; i++) 
    		{
    			if(_compromiseAgrrementValues[i] == agreementValue)
    				indexOfAgreement = i;
			}
    	}
    	return indexOfAgreement;    	
    }
    
    
    
    
    private void setOpponentTypeGuessed()
    {    	
    	AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
    	AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
    	AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(_opponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
    	
    	if(_numOfGuessCompromise > _numOfGuessLongTerm && _numOfGuessCompromise > _numOfGuessShortTerm)
    		_opponentGuessType = agentOpponentCompromise;
    			
    	else if(_numOfGuessLongTerm > _numOfGuessCompromise && _numOfGuessLongTerm > _numOfGuessShortTerm)
    	   _opponentGuessType = agentOpponentLongTerm;
    	    			
    	else if(_numOfGuessShortTerm > _numOfGuessCompromise && _numOfGuessShortTerm > _numOfGuessLongTerm)
    		_opponentGuessType = agentOpponentShortTerm;
    	
    	else _opponentGuessType = null;
    }
}
