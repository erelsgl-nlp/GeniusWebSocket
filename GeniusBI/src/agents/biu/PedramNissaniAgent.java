package agents.biu;

/**
 * @author raz This class should hold all your logic for your automated agent
 *         Examples are provided inline and marked as examples
 * 
 */
public class PedramNissaniAgent extends OldAgentAdapter{

	/****************************************
	 * Start definition: My global variables
	 ****************************************/
	AutomatedAgentType myAgentType;
	AutomatedAgentType OpponentAgentType;
	double dLastOfferValue=0;
	double[] dOpponentTypeProbability = new double[3];
	int nOffersRevicedForType = 0;
	String sOpponentType;
	boolean lastOfferRejectByOpponent;

	/****************************************
	 * End definition: My global variables
	 ****************************************/

	public PedramNissaniAgent() {
		super();
	}

	/**
	 * Constructor Save a pointer to the AgentTools class
	 * 
	 * @param agentTools
	 *            - pointer to the AgentTools class
	 */
	public PedramNissaniAgent(AgentTools agentTools) {
		this.agentTools = agentTools;
		
	}

	/**
	 * Called before the the nagotiation starts. Add any logic you need here.
	 * For example, calculate the very first offer you'll offer the opponent
	 * 
	 * @param agentType
	 *            - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {

		this.myAgentType = agentType;
		this.sOpponentType = sOpponentType;

	//	int nIssuesNum = agentTools.getTotalIssues(agentType);

		lastOfferRejectByOpponent = false;

		 nOffersRevicedForType = 0;
		for (int i = 0; i < dOpponentTypeProbability.length; i++)
			dOpponentTypeProbability[i] = 0.3333;
		
		
		// calculate Automated Agent first offer
		calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
		

	}

	/**
	 * Called when a message of type: QUERY, COUNTER_OFFER, OFFER or PROMISE is
	 * received Note that if you accept a message, the accepted message is saved
	 * in the appropriate structure, so no need to add logic for this.
	 * 
	 * @param nMessageType
	 *            - the message type
	 * @param CurrentAgreementIdx
	 *            - the agreement indices
	 * @param sOriginalMessage
	 *            - the message itself as string
	 */
	public void calculateResponse(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {

		// no comments query/promise
		if (nMessageType == AutomatedAgentMessages.QUERY
				|| nMessageType == AutomatedAgentMessages.PROMISE) {
			return;
		}

		int curTurn = agentTools.getCurrentTurn();
		updateOpponentTypeProbability(CurrentAgreementIdx, curTurn);
		// Till now the value of the accepted agreement
		double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue();
		// The the value of the suggested agreement
		double dOppOfferValueForAgent = agentTools.getAgreementValue(myAgentType, CurrentAgreementIdx, curTurn);
		// The minimum expected Value we will agree in this turn
		double dExpectedAgreementValue = calculateCurrentTurnValue(myAgentType, curTurn);

		// if is full agreement check whether suggestedAgreement is better then
		// previous accepted agreement
			if (dAcceptedAgreementValue >= dOppOfferValueForAgent) {
				// reject offer
				agentTools.rejectMessage(sOriginalMessage);
				//calculateOfferAgainstOpponent(myAgentType, sOriginalMessage, curTurn);
			} 
			else {
				// will accept it , if it is higher then the value we except to receive
				if (dOppOfferValueForAgent>=dExpectedAgreementValue)
				{
				agentTools.acceptMessage(sOriginalMessage);
				// prevent sending future offer in this turn
				agentTools.setSendOfferFlag(false);
				}
				else
				{
					// reject offer
					agentTools.rejectMessage(sOriginalMessage);
					//calculateOfferAgainstOpponent(myAgentType, sOriginalMessage, curTurn);
				}
			}
	}


	/***********************************************
	 * @@ Logic for receiving messages Below are messages the opponent sends to
	 *    the automated agent You can add logic if needed to update your agent
	 *    per message type
	 ***********************************************/

	/**
	 * called whenever we get a comment from the opponent You can add logic to
	 * update your agent
	 * 
	 * @param sComment
	 *            -the received comment
	 */
	public void commentReceived(String sComment) {
		/*
		 * @@ Received a comment from the opponent You can add logic if needed
		 * to update your agent
		 */
	}

	/**
	 * called whenever we get a threat from the opponent You can add logic to
	 * update your agent
	 * 
	 * @param sThreat
	 *            - the received threat
	 */
	public void threatReceived(String sThreat) {
		/*
		 * @@ Received a threat from the opponent You can add logic if needed to
		 * update your agent
		 */
	}

	/**
	 * called whenever the opponent agreed to one of your massages (promise,
	 * query, offer or counter offer). NOTE: if an OFFER is accepted, it is
	 * saved in the appropriate structure. No need to add logic for this.
	 * 
	 * @param nMessageType
	 *            - the type of massage the oppnent aggreed to, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx
	 *            - the indices of the agreement the opponent agreed to
	 * @param sOriginalMessage
	 *            - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {
		/*
		 * @@ Received a message: opponent accepted the
		 * offer/promise/query/counter offer. You can add logic if needed to
		 * update your agent For example, if the message was a promise, you can
		 * now try and offer it as a formal offer...
		 */
		int curTurn = agentTools.getCurrentTurn();
		updateOpponentTypeProbability(CurrentAgreementIdx, curTurn);
		lastOfferRejectByOpponent = false;
	}

	/**
	 * called whenever the opponent rejected one of your massages (promise,
	 * query, offer or counter offer)
	 * 
	 * @param nMessageType
	 *            - the type of massage the oppnent rejected, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx
	 *            - the indices of the agreement the opponent agreed to
	 * @param sOriginalMessage
	 *            - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {
		lastOfferRejectByOpponent = true;
	}

	/***********************************************
	 * @@ End of methods for receiving message
	 ***********************************************/

	/**
	 * called to decide which offer to propose the opponent at a given turn This
	 * method is always called when beginning a new turn You can also call it
	 * during the turn if needed
	 * 
	 * @param agentType
	 *            - the automated agent's type
	 * @param sOpponentType
	 *            - the opponent's type
	 * @param nCurrentTurn
	 *            - the current turn
	 */
	public void calculateOfferAgainstOpponent(AutomatedAgentType agentType,
			String sOpponentType, int nCurrentTurn) {
		// @@ Add any logic to calculate offer (or several offers)
		// and decide which to send to the opponent in a given turn

		int nTotalTurns = agentTools.getTurnsNumber();
		int totalIssuesNum = agentTools.getTotalIssues(agentType);
		int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
		int CurrentAgreementIdx[] = new int[totalIssuesNum];
		int MaxIssueValues[] = new int[totalIssuesNum];

		for (int i = 0; i < totalIssuesNum; ++i) {
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
		}

		// if our last offer was rejected then we get down in new offer 
		double dFactor = 1;
		
		if (nCurrentTurn<=nTotalTurns*0.3)
		{
			if (lastOfferRejectByOpponent)
				dFactor = 0.99;
		}
		else
		{
			if (nCurrentTurn<=nTotalTurns*0.6)
			{
				if (lastOfferRejectByOpponent)
					dFactor = 0.97;
			}
			else
				if (lastOfferRejectByOpponent)
					dFactor = 0.95;
		}
		
		

		// The value we want to at least achieve in the turn
		double dAutomatedAgentAgreementValue = calculateCurrentTurnValue(agentType, nCurrentTurn);
		int TermIdx[] = new int[totalIssuesNum];
		double dOpponentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;

		// going over all agreements
		for (int i = 0; i < totalAgreementsNumber; ++i) 
		{
			
			
			// Agent current value for the agreement
			double dTempAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx,nCurrentTurn);

			// Opponent Current Value for the agreement
			double dTempOpponentAgreementTurnUtillValue = calculateOpponentTurnUtillValue(nCurrentTurn, CurrentAgreementIdx);

			// check if can improve offer to be accepted by opponent and save it
			if (dAutomatedAgentAgreementValue <= dTempAutomatedAgentAgreementValue) {
				// offer is still good for us
				// if last one was rejected then we want to lower new offer from us in factor value
				if (!(lastOfferRejectByOpponent && dLastOfferValue*dFactor<= dTempAutomatedAgentAgreementValue))
				{
					if (dOpponentAgreementValue < dTempOpponentAgreementTurnUtillValue) {
						///  it is good for our possible opponent also
						for (int j = 0; j < totalIssuesNum; ++j) {
							TermIdx[j] = CurrentAgreementIdx[j];
						}
						dOpponentAgreementValue = dTempOpponentAgreementTurnUtillValue;
						dAutomatedAgentAgreementValue = dTempAutomatedAgentAgreementValue;
					}
				}
			}

			agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx,MaxIssueValues);// get the next agreement indices
		} // end for - going over all possible agreements
		

		
			
		// is the new offer better for us then the last one * Factor (in case last one was rejected we use factor as 0.75)
		if (dAutomatedAgentAgreementValue > agentTools.getCurrentTurnAutomatedAgentValue()* dFactor) {
			// you can save the values for later reference ($1)
			agentTools.setCurrentTurnAutomatedAgentValue(dAutomatedAgentAgreementValue);
			agentTools.setCurrentTurnOpponentSelectedValue(dOpponentAgreementValue);
			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(TermIdx));
		}

		// if the value of the offer is better than already accepted offer
		// send it...
		if (agentTools.getAcceptedAgreementsValue() < dAutomatedAgentAgreementValue) {

			dLastOfferValue = dAutomatedAgentAgreementValue;
			String sOffer = agentTools.getSelectedOffer();
			agentTools.sendOffer(sOffer);
			
		}

		
		
	}

	/**
	 * calculate the utility for the opponent using Opponent Probability  table
	 * @param nCurrentTurn
	 * @param CurrentAgreementIdx
	 * @param agentOpponentCompromise
	 * @param agentOpponentLongTerm
	 * @param agentOpponentShortTerm
	 * @return
	 */
	private double calculateOpponentTurnUtillValue(int nCurrentTurn,
			int[] CurrentAgreementIdx) {
		
		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = null;
		AutomatedAgentType agentOpponentLongTerm = null;
		AutomatedAgentType agentOpponentShortTerm = null;
		
		// set agents
		agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);

	
		
		// find current value for each opponent
		double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(
				agentOpponentLongTerm, CurrentAgreementIdx, nCurrentTurn);
		double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(
				agentOpponentShortTerm, CurrentAgreementIdx, nCurrentTurn);
		double dOpponentCompTermAgreementValue = agentTools.getAgreementValue(
				agentOpponentCompromise, CurrentAgreementIdx, nCurrentTurn);
		
		
	

		// calculate the utility for the opponent using Opponent Probability
		// table
		double dTempOpponentAgreementValue = this.dOpponentTypeProbability[AutomatedAgentsCore.COMPROMISE_TYPE_IDX]
				* dOpponentCompTermAgreementValue;
		dTempOpponentAgreementValue += this.dOpponentTypeProbability[AutomatedAgentsCore.LONG_TERM_TYPE_IDX]
				* dOpponentLongTermAgreementValue;
		dTempOpponentAgreementValue += this.dOpponentTypeProbability[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX]
				* dOpponentShortTermAgreementValue;
		return dTempOpponentAgreementValue;
	}

	/**
	 * called to calculate the values of the different possible agreements for
	 * the agent
	 * 
	 * @param agentType
	 *            - the automated agent's type
	 * @param nCurrentTurn
	 *            - the current turn
	 */
	public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
		// Calculate agreements values for a given turn

		// initialization - DO NOT CHANGE
		int nIssuesNum = agentTools.getTotalIssues(agentType);

		int CurrentAgreementIdx[] = new int[nIssuesNum];
		int MaxIssueValues[] = new int[nIssuesNum];

		int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

		for (int i = 0; i < nIssuesNum; ++i) {
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
		}
		// end initialization

		// @@EXAMPLE@@
		// Currently, the method calculates the best agreement, worst agreement
		// and the utility value per agreement
		/********************************
		 * Start example code
		 ********************************/
		double dAgreementValue = 0;

		agentTools.initializeBestAgreement(agentType);
		agentTools.initializeWorstAgreement(agentType);

		// To obtain infromation from the utility you can use getters from the
		// AgentType class
		// @@EXample@@
		// Get the value of the Status Quo and Opting-Out values as time
		// increases
		double dAgreementTimeEffect = agentTools
				.getAgreementTimeEffect(agentType);
		double dStatusQuoValue = agentTools.getSQValue(agentType);
		double dOptOutValue = agentTools.getOptOutValue(agentType);

		// going over all agreements and calculating the best/worst agreement
		for (int i = 0; i < totalAgreementsNumber; ++i) {
			// Note: the agreements are saved based on their indices
			// At the end of the loop the indices are incremeneted
			dAgreementValue = agentTools.getAgreementValue(agentType,
					CurrentAgreementIdx, nCurrentTurn);

			// check for best agreement
			if (dAgreementValue > agentTools.getBestAgreementValue(agentType)) {
				agentTools.setBestAgreementValue(agentType, dAgreementValue);

				// save agreement
				agentTools.setBestAgreementIndices(agentType,
						CurrentAgreementIdx);
			}

			// check for worst agreement
			if (dAgreementValue < agentType.getWorstAgreementValue()) {
				agentTools.setWorstAgreementValue(agentType, dAgreementValue);

				// save agreement
				agentTools.setWorstAgreementIndices(agentType,
						CurrentAgreementIdx);
			}

			agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx,
					MaxIssueValues);// get the next agreement indices
		} // end for - going over all possible agreements
		/********************************
		 * End example code
		 ********************************/
	}

	/***********************************************
	 * @@ Helper methods
	 ***********************************************/

	/**
	 * Get agreed AgreementIdx and update the dOpponentTypeProbability array,
	 * check the value for that Agreement for all AutomatedAgentType update
	 * dOpponentTypeProbability with agent most likely to give this offer
	 * 
	 * @param AgreementIdx
	 *            - the agreement to check
	 * @param curTurn
	 *            - the current turn number
	 */
	public void updateOpponentTypeProbability(int AgreementIdx[], int curTurn) {
		nOffersRevicedForType++;

		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = agentTools
				.getCurrentTurnSideAgentType(sOpponentType,
						AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		AutomatedAgentType agentOpponentLongTerm = agentTools
				.getCurrentTurnSideAgentType(sOpponentType,
						AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		AutomatedAgentType agentOpponentShortTerm = agentTools
				.getCurrentTurnSideAgentType(sOpponentType,
						AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);

		double[] dOpponentAgreementValue = new double[3];
		dOpponentAgreementValue[AutomatedAgentsCore.COMPROMISE_TYPE_IDX] = agentTools
				.getAgreementValue(agentOpponentCompromise, AgreementIdx,
						curTurn);
		dOpponentAgreementValue[AutomatedAgentsCore.LONG_TERM_TYPE_IDX] = agentTools
				.getAgreementValue(agentOpponentLongTerm, AgreementIdx, curTurn);
		dOpponentAgreementValue[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX] = agentTools
				.getAgreementValue(agentOpponentShortTerm, AgreementIdx,
						curTurn);

		// find the highest value
		double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		int maxValueIndex = 0;
		for (int i = 0; i < dOpponentAgreementValue.length; i++) {
			if (dOpponentAgreementValue[i] > maxValue) {
				maxValue = dOpponentAgreementValue[i];
				maxValueIndex = i;
			}
		}

		for (int i = 0; i < dOpponentTypeProbability.length; i++) {
			if (i == maxValueIndex) {
				dOpponentTypeProbability[i] = (dOpponentTypeProbability[i] + 1)
						/ nOffersRevicedForType;
			} else {
				dOpponentTypeProbability[i] = dOpponentTypeProbability[i]
						/ nOffersRevicedForType;
			}
		}
	}

	/*
	 * calculate the Value we would like to at least achieve in this turn 
	 * 
	 * @param agentType
	 * 
	 * @param nCurrentTurn
	 */
	public double calculateCurrentTurnValue(AutomatedAgentType agentType,
			int nCurrentTurn) {
		// find best and worst values for this turn
		calculateValues(agentType, nCurrentTurn);
		double BestAgreementValue = agentTools.getBestAgreementValue(agentType);
		double WorstAgreementValue = agentTools
				.getWorstAgreementValue(agentType);

		int nTotalTurns = agentTools.getTurnsNumber();
		
		// for the first 1/3 turns we try o get between our best and average 
		if (nCurrentTurn<=nTotalTurns*0.3)
			return BestAgreementValue*0.5+((BestAgreementValue + WorstAgreementValue) / 2) * 0.50;
		
		// for the second 1/3 turns we try to get our average 
		if (nCurrentTurn<=nTotalTurns*0.6)
			return BestAgreementValue*0.25+((BestAgreementValue + WorstAgreementValue) / 2) * 0.50
			+ WorstAgreementValue * 0.25;
		
		// for the last 1/3 except the last turn we are trying to get less then our average
		if (nCurrentTurn<nTotalTurns-1)
			return ((BestAgreementValue + WorstAgreementValue) / 2) * 0.75
				+ WorstAgreementValue * 0.25;
		
		// for last turn we try to compromise less then our average and more then worst
		return ((BestAgreementValue + WorstAgreementValue) / 2) * 0.50
		+ WorstAgreementValue * 0.50;
	}



	
	/***********************************************
	 * @@ End of Helper methods
	 ***********************************************/

}
