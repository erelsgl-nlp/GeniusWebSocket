package agents.biu;

/** @author Ron Munitz, ID 037074671
 * 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.EvaluatorDiscrete;


/**
 *   
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class RonMunitzAgent extends OldAgentAdapter {	
    AgentTools m_agentTools = null; // AgentTools - for most of the framework functionalities
    AutomatedAgentType m_myAgentType = null; //AgentType representing my agent's type
    int m_nCurrentTurn;
    //--------------------------------------------------
    // Opponent strategy evaluation members
    //--------------------------------------------------    
    int m_nOppProbShortTerm=0, m_nOppProbLongTerm=0, m_nOppProbCompromise=0; // counters to help calculate probabilities for opponent's estimation.
    double m_dOppProbShortTerm=0, m_dOppProbLongTerm=0, m_dOppProbCompromise=0; // probabilities for opponent's estimation.
    String m_sOpponentType=null;                 
    //  current interaction arrays
    int[] m_myCurrentBestOffer= null ; // Last Offer sent by me at the beginning of a new round
    int[] m_myCurrentCounterOffer= null ; // Last Counter Offer sent by me
    int[] m_yourCurrentOffer= null ; // Last offer sent by opponent    
    // Last observed and estimated utilities
    double m_dYourCurrentUtility = AutomatedAgentType.VERY_SMALL_NUMBER, 
    	   m_dMyUtilityForYourCurrentOffer = AutomatedAgentType.VERY_SMALL_NUMBER; 
    double m_dMyCurrentBestMessageUtility =AutomatedAgentType.VERY_SMALL_NUMBER, 
           m_dMyCurrentCounterOfferUtility = AutomatedAgentType.VERY_SMALL_NUMBER;
    // Thresholds
    double m_dThresholdPercentageForMyCurrentBestOffer; // To select a new message at Beginning of Round
    double m_dThresholdPercentageForYourCounterOfferEstimation; // To select a message as Counter Offer                        
    // misc.
    static final int RESOLVED_ISSUE = 0, UNRESOLVED_ISSUE = 1, NO_VALUE_ISSUE = 2; // for unresolved issues enum
    boolean m_bReachedCompleteAgreement=false; // in order to avoid opting out due to late transmission of ACCEPT full agreement
    
    
    /********************************
     * default ctor - does nothing
     *******************************/
    public RonMunitzAgent() {
    	super();
    }
    
    /****************************************************************
     * Constructor
     * Save a pointer to the m_agentTools class
     * @param m_agentTools - pointer to the m_agentTools class
     ****************************************************************/
    public RonMunitzAgent(AgentTools m_agentTools) {
        this.m_agentTools = m_agentTools;
    }
    
	/*****************************************************************
	 * Called before the the negotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 *******************************************************************/
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
    	m_agentTools=agentTools;
		m_myAgentType = agentType;
		m_sOpponentType = sOpponentType;
		// Prepare my first message - Maximal values for all issues.                
        int totalIssuesNum = m_agentTools.getTotalIssues(m_myAgentType);                               
        // initialize arrays
        m_myCurrentBestOffer = new int[AutomatedAgentType.MAX_ISSUES];// Last Offer sent by me at the beginning of a new round
        m_myCurrentCounterOffer = new int[AutomatedAgentType.MAX_ISSUES];// Last Counter Offer sent by me
        m_yourCurrentOffer = new int[AutomatedAgentType.MAX_ISSUES];// Last offer sent by opponent        
        for (int i = 0; i<AutomatedAgentType.MAX_ISSUES; ++i)
        {
        	m_myCurrentBestOffer[i] = AutomatedAgentType.NO_VALUE;
        	m_myCurrentCounterOffer[i] = AutomatedAgentType.NO_VALUE;
        	m_yourCurrentOffer[i] = AutomatedAgentType.NO_VALUE;
        }        
        // Initialize first message - with the best values for me.
        for (int i = 0; i < totalIssuesNum; ++i)
        {                   
            m_myCurrentBestOffer[i] = getIssueMaxValue(m_myAgentType, i); 
            
        }        
        m_dMyCurrentBestMessageUtility = calculateMyUtility(m_myCurrentBestOffer); // update my last utility
        sendMessage(AutomatedAgentMessages.OFFER, m_myCurrentBestOffer); // send message
        // Initialize Thresholds
        m_dThresholdPercentageForMyCurrentBestOffer = 0.9; // initialize threshold
        m_dThresholdPercentageForYourCounterOfferEstimation = 0.85;        
    } //endof initialize function
    
    	
	
	/*************************************************************************
     * called to decide which offer to propose the opponent at a given turn
     * This method is always called when beginning a new turn
     * You can also call it during the turn if needed
     * @param agentType - the automated agent's type
     * @param sOpponentType - the opponent's type
     * @param nCurrentTurn - the current turn
     **************************************************************************/
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) 
    {    	
    	//===================================================================
    	// Variable decleration and initialization of opponent variables 
    	//====================================================================
    	int totalIssuesNum = m_agentTools.getTotalIssues(m_myAgentType); 
    	double optOutValue = m_agentTools.getOptOutValue(m_myAgentType);
    	double sqValue = m_agentTools.getSQValue(m_myAgentType);    	
    	ArrayList<ArrayWrapper> optionsArray = new ArrayList<ArrayWrapper>(); // Will store the options my agent will consider to send in response.    	    	    	    	    	    	
    	m_nCurrentTurn = m_agentTools.getCurrentTurn(); // update current turn member
    	// Get the utility of my current best offer (sent at beginning of round)
    	m_dMyCurrentBestMessageUtility= m_agentTools.getAgreementValue(m_myAgentType, m_myCurrentBestOffer, m_nCurrentTurn);
    			
    	   							
		int indexIssueIteration; // index to iterate over issues		
		int []nextPossibleOffer = new int[totalIssuesNum]; // next offer to calculate
		int [] unresolvedIssues = new int[totalIssuesNum]; // maintain indices we care about for this offer - for efficiency reasons		
		//==============================================================================
		// 1. initialization of unresolved issues indices - we will deal only with those.
		// 2. Initialize nextPossibleOffer with my current offer indices.
		//==============================================================================
		if (m_yourCurrentOffer != null) // check for null - to avoid problems at first round
		{			
			for (indexIssueIteration =0 ; indexIssueIteration < totalIssuesNum; ++indexIssueIteration)
			{			
				nextPossibleOffer[indexIssueIteration] = m_myCurrentBestOffer[indexIssueIteration];
				// if our offers are different for this issue
				if (m_yourCurrentOffer[indexIssueIteration] != m_myCurrentBestOffer[indexIssueIteration])
				{
					unresolvedIssues[indexIssueIteration] = UNRESOLVED_ISSUE;
				}
				else 
					unresolvedIssues[indexIssueIteration] = RESOLVED_ISSUE;			
			}
		}
		else // null, so everything is UNRESOLVED_ISSUE.
		{
			for (indexIssueIteration =0 ; indexIssueIteration < totalIssuesNum; ++indexIssueIteration)
			{			
				nextPossibleOffer[indexIssueIteration] = m_myCurrentBestOffer[indexIssueIteration];								
				unresolvedIssues[indexIssueIteration] = UNRESOLVED_ISSUE;
			}
		}
						
		//=====================================================================
		// CALCULATE next possible messages, and add them to optionsArray
		// 1. iterate over all issues - maintain RESOLVED_ISSUE indices (i.e. intersection values)
		// 2. Look for a value - and decrease by one the demand 		
		//=====================================================================
		int myCurrentIssueValueIndex = -1; // used for iteration on UtilityIssue.lstUtilityValues							
		// iterate over all issues - maintain intersection values.		
		for (indexIssueIteration =0 ; indexIssueIteration < totalIssuesNum; ++indexIssueIteration)
		{
			switch (unresolvedIssues[indexIssueIteration]) // 1.
			{
				case RESOLVED_ISSUE:			
//					System.err.println("### We agree on issue:" + getUtilityIssue(m_myAgentType, indexIssueIteration).sAttributeName + " ###");
					// DO NOTHING
					break;								
				//========= UNRESOLVED ISSUE ======================
				case UNRESOLVED_ISSUE:					
					myCurrentIssueValueIndex = 
						getIssueNextLowerValueIndex(m_myAgentType,indexIssueIteration, m_myCurrentBestOffer[indexIssueIteration]);					
	        		// suggest the next message - if applicable - 
	        		// applicable means that my current issue value is not the lowest one (i.e. already had the lowest value.)
	        		// because in this phase we decrease my value
	        		if ( // myCurrentIssueValueIndex >=0 && 
        				myCurrentIssueValueIndex != m_myCurrentBestOffer[indexIssueIteration] )	        			
	        		{
	        			nextPossibleOffer[indexIssueIteration] =  myCurrentIssueValueIndex; // the index in a message is a "pointer" to the value transmitted!
	        			optionsArray.add(new ArrayWrapper(nextPossibleOffer.clone())); // add offer
	        			nextPossibleOffer[indexIssueIteration] = m_myCurrentBestOffer[indexIssueIteration]; // restore original value - done here for efficiency reasons, for next iteration.
	        		}				
					break;	
				default:
				{
					break;
				}
			} // end of switch case													
		}//end for - endof CALCULATE next possible messages, and add them to optionsArray
		
					
		double myMaxUtility = AutomatedAgentType.VERY_SMALL_NUMBER;
		int [] myBestOptionIndices = null;
		
		double myCurrentUtility;	
		int myRandomMaxUtilityIndex = -1; // indices in the ArrayList. -1 means we can't use this index.		
															
		//==============================================
		//= CHOOSE best messages from optionsArray
		//= 1. Choose at random, a message which is better than 0.9 of my utility
		//=   1.1 If no such - remove it from the list of messages
		//= 2. Update the best message obtained so far, regardless of the random index 
		//==============================================
		boolean bSentMessage = false;
		while (!bSentMessage && optionsArray.size() > 0 )
		{			
			Random r = new Random();
			int randomIndex = r.nextInt(optionsArray.size() ); // next index to send message!									
			int []currentOption = optionsArray.get(randomIndex).array; // get a random index		
			myCurrentUtility    =  calculateMyUtility(currentOption); //my utility for this message			
//			System.err.println("RANDOM NUMBER:" + randomIndex + "  ; size=" + optionsArray.size() + " RandUtility=" + myCurrentUtility +  " ; " + m_dMyCurrentBestMessageUtility + "*MyBest=" +m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility + "; SQ=" + sqValue + " ; optOutValue=" + optOutValue);			
			// update my best utility indices for this round
			if (myCurrentUtility >= myMaxUtility)
			{
				myBestOptionIndices = currentOption.clone(); // clone
				myMaxUtility = myCurrentUtility;
			}			
			// check if the random index message is good enough. if so - SEND IT AS OFFER 
			if (myCurrentUtility >= m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility) // don't drop my offer by too much
				{									
					myRandomMaxUtilityIndex = randomIndex;
					// send message if it is good enough
					if (myCurrentUtility > sqValue && myCurrentUtility > optOutValue)
					{
						sendMessage(AutomatedAgentMessages.OFFER, currentOption); // send message
						bSentMessage = true;
						// Update my best message member for next turn
						m_myCurrentBestOffer = currentOption.clone();
						m_dMyCurrentBestMessageUtility = calculateMyUtility(m_myCurrentBestOffer);												
						return;
					}
					else // Not GOOD ENOUGH to be sent - remove object from array - for next iteration
					{
						optionsArray.remove(randomIndex); // remove object from array
					}
				}
			else // NOT GOOD ENOUGH to be sent- remove object from array - for next iteration
			{
				optionsArray.remove(randomIndex); // remove object from array
			}								
		}// end while - BY NOW WE EITHER SENT A RANDOM MESSAGE, OR DID NOT FIND ONE.
		
		//===============================================================================
		// couldn't select a good message at random message. 
		// try to send the best message - or opt out.
		//===============================================================================
		if (!bSentMessage)
		{			
			if (myBestOptionIndices == null)
			{
				if (!m_bReachedCompleteAgreement) // Protect from late sending of ACCEPT by the ServerThread
				{
//				System.err.println("$$$ Best Indices was NULL... OPTING OUT");				
					m_agentTools.optOut();
				}
				return;
			}
			double myBestMessageUtility = calculateMyUtility(myBestOptionIndices);
			if (myBestMessageUtility > sqValue && myBestMessageUtility > optOutValue)
			{
				sendMessage(AutomatedAgentMessages.OFFER, myBestOptionIndices); // send message
				// Update my best message member for next turn
				m_myCurrentBestOffer = myBestOptionIndices.clone();
				m_dMyCurrentBestMessageUtility = calculateMyUtility(m_myCurrentBestOffer);								
			}			
			else if (myBestMessageUtility <= optOutValue) // opt out
			{
				if (!m_bReachedCompleteAgreement)
				{
					m_agentTools.optOut();
				}
			}
		}// BY NOW WE SENT A MESSAGE. IF WE DID NOT - THEN WE WILL SEEK SQ OR WE HAVE OPTED OUT. 				  	        	    	    	
    }//endof function calculateOfferAgainstOpponent()
//---------------------------------------------------------------------------------------------------------	
    	
    
    
    /***************************************************************************** 
     * Called when a message of type:
     * QUERY, COUNTER_OFFER, OFFER or PROMISE 
     * is received
     * Note that if you accept a message, the accepted message is saved in the 
     * appropriate structure, so no need to add logic for this.
     * @param nMessageType - the message type
     * @param CurrentAgreementIdx - the agreement indices
     * @param sOriginalMessage - the message itself as string
     * 
     *  
     ******************************************************************************/	
	public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		
		m_bReachedCompleteAgreement = false; // will be changed to true in this function - if we decide to accept a message,
		
		int totalIssuesNum = m_agentTools.getTotalIssues(m_myAgentType);		
    	double optOutValue = m_agentTools.getOptOutValue(m_myAgentType);
    	double sqValue = m_agentTools.getSQValue(m_myAgentType);
		
    	int [] unresolvedIssues = new int[totalIssuesNum]; // maintain indices we care about for this offer - for efficiency reasons
		int indexIssueIteration;
		double dThresholdForCounterOffers; // used for the calculate
		m_nCurrentTurn = m_agentTools.getCurrentTurn(); // update current turn member
				
		m_yourCurrentOffer = CurrentAgreementIdx.clone();
		updateProbabilitiesAccordingToMessage(m_yourCurrentOffer); // update probabilities for opponent estimation.
				
		
    	// utility calculations for the message just received:  
		m_dYourCurrentUtility = estimateOpponentUtility(m_yourCurrentOffer); // oponnent's estimated utility
    	m_dMyUtilityForYourCurrentOffer =  m_agentTools.getAgreementValue(m_myAgentType, m_yourCurrentOffer, m_nCurrentTurn); // my utility of this message.
		
    	boolean bCompleteMessage = true;
		dThresholdForCounterOffers = 0.8 * m_dMyCurrentBestMessageUtility; // Used for the calculations in TWEAK THIS LATER 
		//==============================================================================
		// 1. initialization of unresolved issues indices - we will deal only with those.
		// 2. Initialize nextPossibleOffer with my current offer indices.
		//==============================================================================
		if (m_yourCurrentOffer != null) // check for null - to avoid problems at first round
		{			
			for (indexIssueIteration =0 ; indexIssueIteration < totalIssuesNum; ++indexIssueIteration)
			{							
				// if our offers are different for this issue
				if (m_yourCurrentOffer[indexIssueIteration] != m_myCurrentBestOffer[indexIssueIteration])
				{
					unresolvedIssues[indexIssueIteration] = UNRESOLVED_ISSUE;
				}
				else 
					unresolvedIssues[indexIssueIteration] = RESOLVED_ISSUE;								
				
				if (isNoAgreement(m_myAgentType, indexIssueIteration, m_yourCurrentOffer[indexIssueIteration]) )
				{
					bCompleteMessage = false;
					unresolvedIssues[indexIssueIteration] = NO_VALUE_ISSUE;
				}
			}
		}
		else // null, so everything is UNRESOLVED_ISSUE.
		{
			for (indexIssueIteration =0 ; indexIssueIteration < totalIssuesNum; ++indexIssueIteration)
			{														
				unresolvedIssues[indexIssueIteration] = UNRESOLVED_ISSUE;
			}
		}
		
		ArrayList<ArrayWrapper> optionsArray = new ArrayList<ArrayWrapper>(); // Will store the options my agent will consider to send in response.		
		calculateAllCounterOffers (optionsArray, m_yourCurrentOffer, 0, totalIssuesNum-1, unresolvedIssues, dThresholdForCounterOffers);
		

		
		double yourCurrentUtility, myCurrentUtility;
		double myMaxUtility = AutomatedAgentType.VERY_SMALL_NUMBER, yourMaxUtility = AutomatedAgentType.VERY_SMALL_NUMBER;
		double myMaxUtilityForYourMaxUtilityCalculation = AutomatedAgentType.VERY_SMALL_NUMBER; 
		int myMaxUtilityIndex=-1, yourMaxUtilityIndex=-1; 
		
		//==============================================
		//= CHOOSE best messages from optionsArray		
		//==============================================
		for (int indexOptions = 0 ; indexOptions < optionsArray.size(); ++indexOptions)
		{
			int []currentOption = optionsArray.get(indexOptions).array;
			yourCurrentUtility = estimateOpponentUtility(currentOption); //your utility
			myCurrentUtility    =  calculateMyUtility(currentOption);    //my utility
			
			// update my best utility
			if (myCurrentUtility >= myMaxUtility && 
				myCurrentUtility > optOutValue	)
			{				
				if (myCurrentUtility >= m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility) // don't drop my offer by too much
				{					
					myMaxUtility = myCurrentUtility;
					myMaxUtilityIndex = indexOptions;
				}
			}
			// update our common best utility
			if (yourCurrentUtility >= yourMaxUtility &&
				yourCurrentUtility > optOutValue)
			{
				if (yourCurrentUtility >= m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility  || // compare to utility of my last sent offer
				    yourCurrentUtility >= m_dThresholdPercentageForYourCounterOfferEstimation * myMaxUtility) // utility calculated here for me
				{					
					yourMaxUtility = yourCurrentUtility;
					yourMaxUtilityIndex = indexOptions;
					myMaxUtilityForYourMaxUtilityCalculation = myCurrentUtility;
				}
			}			
		}
		
		//=========================================
		// Consider accepting the message
		//=========================================
		if (m_dMyUtilityForYourCurrentOffer >= myMaxUtility &&
			m_dMyUtilityForYourCurrentOffer >= myMaxUtilityForYourMaxUtilityCalculation)			
		{
			if (bCompleteMessage)
			{
				if (m_dMyUtilityForYourCurrentOffer >= m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility) // Accept message
				{
					m_agentTools.acceptMessage(sOriginalMessage);
					if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER)
					{
						sendMessage(AutomatedAgentMessages.OFFER, m_yourCurrentOffer); // send message
//						System.err.println("DEBUG: sent001 - Counter offer for accepted promise/etc.");
					}					
					m_bReachedCompleteAgreement = true; // To defer OPT_OUT.
					return;
				}
				else
				{					
//					System.err.println("NOT ACCEPTING MESSAGE1 my=" + m_dMyCurrentBestMessageUtility + " yours=" + m_dMyUtilityForYourCurrentOffer);
				}
			}
			
			else // incomplete message
			{
				if (m_dMyUtilityForYourCurrentOffer >= m_dThresholdPercentageForMyCurrentBestOffer * m_dMyCurrentBestMessageUtility) // Accept message
				{
					m_agentTools.acceptMessage(sOriginalMessage);
					if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER)
					{
						sendMessage(AutomatedAgentMessages.OFFER, m_yourCurrentOffer); // send message
							//						System.err.println("DEBUG: sent002 - Counter offer for accepted promise/etc.");
					}
					return;
				}
				else
				{
						//					System.err.println("NOT ACCEPTING MESSAGE2 my=" + m_dMyCurrentBestMessageUtility + " yours=" + m_dMyUtilityForYourCurrentOffer);
				}
			}
		}
		
		//
		//=========================================
		// SEND OFFER SECTION
		//=========================================
		// if found an offer good enough for the opponent
		if (yourMaxUtilityIndex != -1)  
		{						
			m_myCurrentCounterOffer = optionsArray.get(yourMaxUtilityIndex).array.clone();
			m_dMyCurrentCounterOfferUtility = yourMaxUtility;		
			if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER)
			{
				sendMessage(AutomatedAgentMessages.OFFER, m_myCurrentCounterOffer); // send message
//				System.err.println("DEBUG: sent1 - OFFER for yourMaxUtility");
			}
			else
			{
				sendMessage(AutomatedAgentMessages.COUNTER_OFFER, m_myCurrentCounterOffer); // send message
//				System.err.println("DEBUG: sent2 - COUNTER_OFFER for yourMaxUtility");
			}
		}		
		// ELSE found an offer good enough for me, but couldn't find an offer good enough for both of us
		else if (myMaxUtilityIndex != -1) 
		{
			m_myCurrentCounterOffer = optionsArray.get(myMaxUtilityIndex).array.clone();
			m_dMyCurrentCounterOfferUtility = myMaxUtility;
			if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER)
			{
				sendMessage(AutomatedAgentMessages.OFFER, m_myCurrentCounterOffer); // send message
//				System.err.println("DEBUG: sent3 - OFFER for myMaxUtility");
			}
			else
			{
				sendMessage(AutomatedAgentMessages.COUNTER_OFFER, m_myCurrentCounterOffer); // send message
//				System.err.println("DEBUG: sent4 - COUNTER_OFFER for myMaxUtility");
			}
		}
		
		else
		{
//			System.err.println("Did not find a candidate message!");
		}
		
		optionsArray.clear();
	}//endof calculateResponse
	
       
    
	//========== HELPER FUNCTION FOR CALCULATE RESPONSE ==========================
	/**
	 * the function adds to @optionsArray all the offers which are
	 * better than the currently received offer (@indices), and that the utility of this 
	 * function is better than @threshold
	 * 
	 * On return from this recursive function,  optionsArray will contain a list of possible counter offers
	 * so that the user will be able to pick and send one.
	 */
	public void calculateAllCounterOffers (ArrayList<ArrayWrapper> optionsArray, int []indices, int nStartIndex, int nEndIndex, int []unresolvedIssues, double threshold)
    {
    	//ArrayList<ArrayWrapper> optionsArray = new ArrayList<ArrayWrapper>(); // Will store the options my agent will consider to send in response.
    	int totalIssuesNum = m_agentTools.getTotalIssues(m_myAgentType);
    	int indexIssueIteration, issueValueIndex;
    	int []nextPossibleOffer= indices.clone();
    	double dCurrentUtility;    	
    	    	
    	// stop condition
    	if (nStartIndex >= nEndIndex)
    	{
    		//System.err.println("ENDED ITERATION!" + optionsArray.size());
    		return;
    	}
    	
    	for (indexIssueIteration = 0; indexIssueIteration < totalIssuesNum; ++indexIssueIteration) 
    	{
    		// get next possible value for me.
    		issueValueIndex = indices[indexIssueIteration];
    		// In this case we have nothing to add
    		if (unresolvedIssues[indexIssueIteration] == RESOLVED_ISSUE)
    		{
    			// just apply the recursive call...
    			calculateAllCounterOffers (optionsArray,nextPossibleOffer, nStartIndex+1, nEndIndex, unresolvedIssues,threshold);
    		}
    		
    		else
    		{
    			if (unresolvedIssues[indexIssueIteration] == UNRESOLVED_ISSUE) // set to one issue higher
    			{
        			nextPossibleOffer[indexIssueIteration] = getIssueNextHigherValueIndex(m_myAgentType, indexIssueIteration, issueValueIndex);
        			calculateAllCounterOffers (optionsArray,nextPossibleOffer, nStartIndex+1, nEndIndex, unresolvedIssues,threshold); // RECURSIVE CALL
    			}    			
        		else if (unresolvedIssues[indexIssueIteration] == NO_VALUE_ISSUE) // set to min
        		{
        			nextPossibleOffer[indexIssueIteration] = getIssueMaxValue(m_myAgentType, indexIssueIteration); 
        			calculateAllCounterOffers (optionsArray,nextPossibleOffer, nStartIndex+1, nEndIndex, unresolvedIssues,threshold); // RECURSIVE CALL
        		}        		    		        		        		        		
        		dCurrentUtility = calculateMyUtility(nextPossibleOffer);
        		if (dCurrentUtility >= threshold) // This will be the opponent estimated threshold
            		{
            			//nextPossibleOffer[indexIssueIteration] =  myCurrentIssueValueIndex; // the index in a message is a "pointer" to the value transmitted!
            			optionsArray.add(new ArrayWrapper(nextPossibleOffer.clone())); // add offer
            			//nextPossibleOffer[indexIssueIteration] = m_myCurrentBestOffer[indexIssueIteration]; // restore original value - done here for efficiency reasons, for next iteration.
            		}				        		
        		nextPossibleOffer[indexIssueIteration] =issueValueIndex; // RESTORE STATE for next iteration.
        		// Calculate when keeping this issue.
        		calculateAllCounterOffers (optionsArray,nextPossibleOffer, nStartIndex+1, nEndIndex, unresolvedIssues,threshold);
    		}    						
    	}//end for
    }//endof function calculateAllCounterOffers
    
    
    
            
    
    /**
	 * called whenever we get a comment from the opponent
     * You can add logic to update your agent
     * @param sComment -the received comment
	 */
	public void commentReceived(String sComment) {
        /* @@ Received a comment from the opponent
         * You can add logic if needed to update your agent 
         */
		// IGNORE COMMENTS
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
		// IGNORE THREATS.
    }
	
	/**
	 * 
	 * called whenever the opponent agreed to one of your massages (promise, query, offer or counter offer).
     * NOTE: if an OFFER is accepted, it is saved in the appropriate structure. No need to add logic for this.
	 * @param nMessageType - the type of massage the oppnent aggreed to, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was accepted
	 */
	
public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
	return;
	}
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) { 		
    }
    	    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
    }
    
    
   //======================================================================
  //======================================================================
  //======================================================================
   // Helper functions Section
  //======================================================================
  //======================================================================
  //======================================================================
    
    /***********************************************************************
	 * a wrapper class made in order to allow an int[] array's members
	 * to be used as the key to the HashMap. And so that we can compare 
	 * two arrays easily, according to its hashCode method.
	 ***********************************************************************/
	class ArrayWrapper
	{
		private int[] array;

		public ArrayWrapper(int[] arr)
		{
		array = arr;
		}

		public boolean equals(Object o)
		{
		return ( (o instanceof ArrayWrapper) &&
		((ArrayWrapper)o).array == array );
		}

		public int hashCode()
		{
		return Arrays.hashCode(array);
		}
	}//endof class ArrayWrapper

    
    /**	
	 * Indices array according to the opponent message.
	 * 1. Get agreements value according to each possible opponent strategy
	 * 2. Update probabilities.
	 */
	public void updateProbabilitiesAccordingToMessage(int[] indices) {		
		m_nCurrentTurn = m_agentTools.getCurrentTurn(); 
		
		//--- Get Utility for each opponent type
		double shortTermUtility = 
			m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX), 
					indices, m_nCurrentTurn);
		double longTermUtility = 
			m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX), 
					indices, m_nCurrentTurn);		
		double compromiseUtility = m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX), 
				indices, m_nCurrentTurn);						                		
		
		//--- Increase the utility which corresponds to the 
		double maxOpponentUtility = Math.max(shortTermUtility, Math.max(longTermUtility, compromiseUtility));//AutomatedAgentType.VERY_SMALL_NUMBER; 					
		if (maxOpponentUtility == shortTermUtility) 
		{
			++m_nOppProbShortTerm;
		} 
		else if (maxOpponentUtility == longTermUtility) 
		{
			++m_nOppProbLongTerm;
		} 
		else if (maxOpponentUtility == compromiseUtility) 
		{
			++m_nOppProbCompromise;
		}
		
		// update probabilities
		int totalCount = m_nOppProbShortTerm + m_nOppProbLongTerm + m_nOppProbCompromise;
		m_dOppProbShortTerm = (double)(m_nOppProbShortTerm) / totalCount;
		m_dOppProbLongTerm = (double)(m_nOppProbLongTerm) / totalCount;
		m_dOppProbCompromise = (double)(m_nOppProbCompromise) / totalCount;		
	}//endof function
    
	
	/** 
	 * Calculate estimated value for opponent - according to probabilistic expected utility
	 * @param indices - the message to calculate the utility for
	 * @return
	 */
	private double estimateOpponentUtility(int [] indices) {
		//--- Get Utility for each opponent type
		double shortTermUtility = 
			m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX), 
					indices, m_nCurrentTurn);
		double longTermUtility = 
			m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX), 
					indices, m_nCurrentTurn);		
		double compromiseUtility = m_agentTools.getAgreementValue(m_agentTools.getCurrentTurnSideAgentType(m_sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX), 
				indices, m_nCurrentTurn);
				
		double estimatedOpponentUtility = m_dOppProbShortTerm * shortTermUtility +
										m_dOppProbLongTerm    * longTermUtility +
										m_dOppProbCompromise  * compromiseUtility;
		return estimatedOpponentUtility;
	}
	
	/**
	 * calculate my utility for the message
	 * @param indices
	 * @return
	 */
	private double calculateMyUtility(int [] indices) {
		return m_agentTools.getAgreementValue(m_myAgentType, indices, m_agentTools.getCurrentTurn());		
	}

  
    
 
    
    
    /**
     * Returns the INDEX of the maximal value for the issue represented by issueIndex 
     * @param agentType - agent type
     * @param issueIndex - issue's index
     * @return
     */
    public int getIssueMaxValue(AutomatedAgentType agentType, int issueIndex) {
    	double dMaxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
    	int dMaxIndex = -2;
    	
    	try {
    		IssueDiscrete  issue = (IssueDiscrete)agentType.us.getDomain().getIssues().get(issueIndex);
		    EvaluatorDiscrete evaluator=(EvaluatorDiscrete)agentType.us.getEvaluator(issue.getNumber());
		    for (int i = 0; i < issue.getNumberOfValues(); ++i)
    		{
		    	double uv = evaluator.getEvaluationNotNormalized(issue.getValue(i));
    			if ( uv > dMaxValue)
    			{
    				// Added a test to avoid a NO_AGREEMENT issue.
    				if ( !isNoAgreement(agentType, issueIndex, i))  
    					{        					
    					dMaxValue = uv;
    					dMaxIndex = i;
    					}
    			}
    		}
    		return dMaxIndex;
    	} catch(Exception e) {}
    	return -2 ; //index not found.       
    }
    
        
    /**
     * Returns the INDEX of the minimal value for the issue represented by issueIndex 
     * @param agentType - agent type
     * @param issueIndex - issue's index
     * @return
     */
    public int getIssueMinValue(AutomatedAgentType agentType, int issueIndex) {;
    	double dMinValue = AutomatedAgentType.VERY_HIGH_NUMBER;
    	int dMinIndex = -2;
    	
    	try {
    		IssueDiscrete  issue = (IssueDiscrete)agentType.us.getDomain().getIssues().get(issueIndex);
		    EvaluatorDiscrete evaluator=(EvaluatorDiscrete)agentType.us.getEvaluator(issue.getNumber());
		    for (int i = 0; i < issue.getNumberOfValues(); ++i)
    		{
		    	double uv = evaluator.getEvaluationNotNormalized(issue.getValue(i));
    			if ( uv < dMinValue)
    			{
    				// Added a test to avoid a NO_AGREEMENT issue.
    				if ( !isNoAgreement(agentType, issueIndex, i))  
    					{        					
        				dMinValue = uv;
        				dMinIndex = i;
    					}
    			}
    		}
    		return dMinIndex;
    	} catch(Exception e) {}
    	return -2 ; //index not found.
    }

    
    /**
     * Gets the INDEX representing the next lower value for this issue
     * @param agentType
     * @param issueIndex
     * @param issueValueIndex
     * @return
     */
    public int getIssueNextLowerValueIndex(AutomatedAgentType agentType, int issueIndex, int issueValueIndex) 
    {
    	int nextIssueValueIndex = issueValueIndex;
    	try {
    		IssueDiscrete  issue = (IssueDiscrete)agentType.us.getDomain().getIssues().get(issueIndex);
		    EvaluatorDiscrete evaluator=(EvaluatorDiscrete)agentType.us.getEvaluator(issue.getNumber());
		    double dIssueValue = evaluator.getEvaluationNotNormalized(issue.getValue(issueValueIndex));

    
			// traverse the list of indices
			for (int i = 0; i < issue.getNumberOfValues(); ++i)
			{
				double uv = evaluator.getEvaluationNotNormalized(issue.getValue(i));
				if ( uv < dIssueValue && 	// TODO: Note - I skip issues with the same value - MAYBE use history here, but it's complicated
					 (!isNoAgreement(agentType, issueIndex, i))) 
				{
					nextIssueValueIndex = i;
				}		
			}
    	} catch(Exception e) {}
		return nextIssueValueIndex;        	            	
	
    }
    
    /**
     * Gets the INDEX represnting the next higher value for this issue
     * @param agentType
     * @param issueIndex
     * @param issueValueIndex
     * @return
     */
    public int getIssueNextHigherValueIndex(AutomatedAgentType agentType, int issueIndex, int issueValueIndex) 
    { 
    	int nextIssueValueIndex = issueValueIndex;
    	try {
		    IssueDiscrete  issue = (IssueDiscrete)agentType.us.getDomain().getIssues().get(issueIndex);
		    EvaluatorDiscrete evaluator=(EvaluatorDiscrete)agentType.us.getEvaluator(issue.getNumber());
		    double dIssueValue = evaluator.getEvaluationNotNormalized(issue.getValue(issueValueIndex));
		   
		    	
			// traverse the list of indices
			for (int i = 0; i < issue.getNumberOfValues(); ++i)
			{
				double uv = evaluator.getEvaluationNotNormalized(issue.getValue(i));
				if ( uv > dIssueValue) // TODO: Note - I skip issues with the same value - MAYBE use history here, but it's complicated
				{
					nextIssueValueIndex = i;
				}					
			}
		        	            	
    	} catch(Exception e) {}
    	return nextIssueValueIndex;
    }
    
    
    //============ SOME WORKAROUNDS FOR BUGS IN THE FRAMEWORK (i.e. NO_AGREEENT != No Value, etc.)

    /**
     * Check if the current message is a No Agreement. 
     * This is required because the value of No Agreement != NO_VALUE. (bug in the framework?!)
     */
    public boolean isNoAgreement(AutomatedAgentType agentType, int issueIndex, int issueValueIndex)
    {
    	if (issueValueIndex == AutomatedAgentType.NO_VALUE)
    		return true;
    	return agentType.isIssueValueNoAgreement(issueIndex, issueValueIndex);

    }
    
    
    
    /**
     * This is a wrapper function, in order to not forget
     * to setSendOfferFlag because I had problems with it (HOPE IT HELPS!)
     * @param nMessageType
     * @param currentAgreementIdx
     */
    public void sendMessage(int nMessageType, int currentAgreementIdx[]) 
    {
	    m_agentTools.setSendOfferFlag(true);
	    m_agentTools.sendMessage(AutomatedAgentMessages.OFFER, currentAgreementIdx); // send message
    }
    
}
