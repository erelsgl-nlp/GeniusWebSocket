package agents.biu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class should hold all your logic for your automated agent
 */
public class LenaZilbermanAgent extends OldAgentAdapter
{

	/****************************************
	 * Start definition: My global variables
	 ****************************************/
	AutomatedAgentType myAgentType;
	AutomatedAgentType OpponentAgentType;
	int bestCompleteAgreement[];
	int bestJointCompleteAgreement[];
	double bestJointCompleteAgreementValue_me;
	double bestJointCompleteAgreementValue_Opponent;
	double bestCompleteAgreementValue;
	boolean sendBestJointCompleteAgreement;
	double bottomLimitFactor; // in [0,1]
	String sOpponentType;
	double fitOpponentType[] = new double[3];
	boolean lastOfferRejectByOpponent;
	boolean lastOfferWasPartial;

	/****************************************
	 * End definition: My global variables
	 ****************************************/

	protected int numberOfThreatsReceived;

	/** all possible bids, sorted by utility */
	protected List<BidUtilityPair> sortedBids;

	protected static class BidUtilityPair
	{
		public int[] bid;
		public double utility;

		public BidUtilityPair(int[] bid, double utility)
		{
			this.bid = bid;
			this.utility = utility;
		}
	}

	public LenaZilbermanAgent()
	{
		super();
	}

	/**
	 * Constructor Save a pointer to the AgentTools class
	 * 
	 * @param agentTools - pointer to the AgentTools class
	 */
	public LenaZilbermanAgent(AgentTools agentTools)
	{
		this.agentTools = agentTools;
	}

	/********************************************
	 * Called before the the negotiation starts. Initialization my global
	 * variables and call for calculateOfferAgainstOpponent
	 * 
	 * @param agentType - the automated agent
	 ********************************************/
	public void initialize(AutomatedAgentType agentType, String sOpponentType)
	{
		this.myAgentType = agentType;
		this.sendBestJointCompleteAgreement = false;
		this.sOpponentType = sOpponentType;
		int nIssuesNum = agentTools.getTotalIssues(agentType);
		bestCompleteAgreement = new int[nIssuesNum];
		bestJointCompleteAgreement = new int[nIssuesNum];
		bestCompleteAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		bestJointCompleteAgreementValue_Opponent = AutomatedAgentType.VERY_SMALL_NUMBER;
		bestJointCompleteAgreementValue_me = AutomatedAgentType.VERY_SMALL_NUMBER;
		this.bottomLimitFactor = 0.7;
		lastOfferRejectByOpponent = false;
		lastOfferWasPartial = false;

		initPossibleBids();

		// calculate Automated Agent first offer
		calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
	}

	/**
	 * calculateResponse according to the agreement type (full, partial) always
	 * reject query/promise
	 * 
	 * @param nMessageType - the message type
	 * @param currentAgreementIdx - the agreement indices
	 * @param sOriginalMessage - the message itself as string
	 */
	public void calculateResponse(int nMessageType, int currentAgreementIdx[],
		String sOriginalMessage)
	{
		// no comments query/promise
		if (nMessageType == AutomatedAgentMessages.QUERY
			|| nMessageType == AutomatedAgentMessages.PROMISE)
		{
			return;
		}

		sendBestJointCompleteAgreement = false;
		int curTurn = agentTools.getCurrentTurn();
		updateOpponentType(currentAgreementIdx, curTurn);

		// Till now the value of the accepted agreement
		double acceptedAgreementValue = agentTools.getAcceptedAgreementsValue();
		// The the value of the suggested agreement
		double suggestedAgreementValue = agentTools.getAgreementValue(myAgentType,
			currentAgreementIdx, curTurn);
		double bottomLimitValue = calculateBottomLimitValue(myAgentType, curTurn);

		// if is full agreement check whether suggestedAgreement is better then
		// previous accepted agreement
		if (isFullAgreemment(myAgentType, currentAgreementIdx))
		{
			if (suggestedAgreementValue > acceptedAgreementValue)
			{// if is better then x_precent_val from out limit from max-min
				if (suggestedAgreementValue >= bottomLimitValue)
				{
					agentTools.acceptMessage(sOriginalMessage);
					return;
				}
			}
		} else
		{// if is real partial agreement or with NoAgreement value
			// check if after accept we can get good full agreement
			calculateValues_CompletionOfPartialAgreement(myAgentType, currentAgreementIdx, curTurn);
			if (bestCompleteAgreementValue >= bottomLimitValue)
			{
				findBestFitcompletionOfPartialAgreement(myAgentType,
					getMostSeasonableOpponentType(), currentAgreementIdx, curTurn, bottomLimitValue);

				agentTools.acceptMessage(sOriginalMessage);
				this.sendBestJointCompleteAgreement = true;

				// check if its better to send offer now or at next turn
				double AgreementCurrentTurnValue = bestJointCompleteAgreementValue_me;
				double AgreementCurrentNextTurnValue = agentTools.getAgreementValue(myAgentType,
					bestJointCompleteAgreement, curTurn + 1);
				if (AgreementCurrentTurnValue > AgreementCurrentNextTurnValue)
					calculateOfferAgainstOpponent(myAgentType, this.sOpponentType, curTurn); // send
				// offer
				// now
				return;
			}
		}

		// reject offer
		agentTools.rejectMessage(sOriginalMessage);
		return;
	}

	/***********************************************
	 * @@ Start: Logic for receiving messages
	 *    ----------------------------------------- Below are messages the
	 *    opponent sends to the automated agent You can add logic if needed to
	 *    update your agent per message type
	 ***********************************************/


	/**
	 * called whenever we get a comment from the opponent You can add logic to
	 * update your agent
	 * 
	 * @param sComment - the received comment
	 */
	public void commentReceived(String sComment)
	{
		/*
		 * @@ Received a comment from the opponent You can add logic if needed
		 * to update your agent
		 */
	}

	/**
	 * called whenever we get a threat from the opponent You can add logic to
	 * update your agent
	 * 
	 * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat)
	{
		numberOfThreatsReceived++;
	}

	/**
	 * update the OpponentType array
	 * 
	 * @param nMessageType - the type of massage the opponent agreed to, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent
	 *            agreed to
	 * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage)
	{
		int curTurn = agentTools.getCurrentTurn();
		updateOpponentType(CurrentAgreementIdx, curTurn);
		lastOfferRejectByOpponent = false;
	}

	/**
	 * called whenever the opponent rejected one of your massages (promise,
	 * query, offer or counter offer)
	 * 
	 * @param nMessageType - the type of massage the opponent rejected, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent
	 *            agreed to
	 * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[],
		String sOriginalMessage)
	{
		lastOfferRejectByOpponent = true;
	}

	/***********************************************
	 * @@ End: methods for receiving message
	 *    ------------------------------------------
	 ***********************************************/

	/**
	 * called to decide which offer to propose the opponent at a given turn send
	 * response according to incoming offer This method is always called when
	 * beginning a new turn
	 * 
	 * @param agentType - the automated agent's type
	 * @param sOpponentType - the opponent's type
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType,
		int nCurrentTurn)
	{
		// send complete agreement that calculate before
		if (sendBestJointCompleteAgreement)
		{
			agentTools.setCurrentTurnAutomatedAgentValue(bestJointCompleteAgreementValue_me);
			agentTools.setCurrentTurnAgreementString(agentType
				.getAgreementStr(bestJointCompleteAgreement));
			String sOffer = agentTools.getSelectedOffer();
			agentTools.sendOffer(sOffer);
			sendBestJointCompleteAgreement = false;
			lastOfferWasPartial = false;
			return;
		}

		double bottomLimitValue = calculateBottomLimitValue(myAgentType, nCurrentTurn);

		int previousAcceptedAgreementsIndices[] = new int[AutomatedAgentType.MAX_ISSUES];
		previousAcceptedAgreementsIndices = agentTools.getAcceptedAgreementIdx();

		// if last offer was partial and reject do not try partial again
		if (!(lastOfferWasPartial && lastOfferRejectByOpponent))
		{ // find agreement that complete the accepted with more one issues
			// (the most important issues (for me), with the best value for me)
			// and send it
			// get accepted agreement for now
			int NewPartialAgreemeneBestForMe[] = new int[AutomatedAgentType.MAX_ISSUES];
			NewPartialAgreemeneBestForMe = agentTools.findPartialAgreementBestForMe(agentType,
				previousAcceptedAgreementsIndices);
			double newPartialAgreementValueBestForMe = agentType.getAgreementValue(
				NewPartialAgreemeneBestForMe, nCurrentTurn);
			// better than agreed agreement
			if (newPartialAgreementValueBestForMe > agentTools.getAcceptedAgreementsValue())
			{
				agentTools.setCurrentTurnAutomatedAgentValue(newPartialAgreementValueBestForMe);
				agentTools.setCurrentTurnAgreementString(agentType
					.getAgreementStr(NewPartialAgreemeneBestForMe));
				String sOffer = agentTools.getSelectedOffer();
				agentTools.sendOffer(sOffer);
				lastOfferWasPartial = true;
				return;
			}
		} else
		{// lastOfferRejectByOpponent or
			// not lastOfferRejectByOpponent but the
			// NewPartailAgreemeneBestForMe
			// is not good compare to accepted agreement
			// look for CompletionOfPartialAgreement
			lastOfferWasPartial = false;
			calculateValues_CompletionOfPartialAgreement(myAgentType,
				previousAcceptedAgreementsIndices, nCurrentTurn);
			if (bestCompleteAgreementValue >= bottomLimitValue)
			{
				findBestFitcompletionOfPartialAgreement(myAgentType,
					getMostSeasonableOpponentType(), previousAcceptedAgreementsIndices,
					nCurrentTurn, bottomLimitValue);

				agentTools.setCurrentTurnAutomatedAgentValue(bestCompleteAgreementValue);
				agentTools.setCurrentTurnAgreementString(agentType
					.getAgreementStr(bestCompleteAgreement));
				String sOffer = agentTools.getSelectedOffer();
				agentTools.sendOffer(sOffer);
				return;
			} else
			{
				// no good options, start from empty agreement
				int nIssuesNum = agentTools.getTotalIssues(myAgentType);
				int emptyAgreements[] = new int[nIssuesNum];
				for (int i = 0; i < nIssuesNum; i++)
					emptyAgreements[i] = AutomatedAgentType.NO_VALUE;

				findBestFitcompletionOfPartialAgreement(myAgentType,
					getMostSeasonableOpponentType(), emptyAgreements, nCurrentTurn,
					bottomLimitValue);

				agentTools.setCurrentTurnAutomatedAgentValue(bestCompleteAgreementValue);
				agentTools.setCurrentTurnAgreementString(agentType
					.getAgreementStr(bestCompleteAgreement));
				String sOffer = agentTools.getSelectedOffer();
				agentTools.sendOffer(sOffer);
				return;
			}

		}
	}

	/**
	 * calculate and save the values of best and worst agreements for the turn:
	 * agentTools.setWorstAgreementIndices agentTools.setBestAgreementIndices
	 * 
	 * @param agentType - the automated agent's type
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn)
	{
		// Calculate agreements values for a given turn

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
		double dAgreementValue = 0;
		agentTools.initializeBestAgreement(agentType);
		agentTools.initializeWorstAgreement(agentType);
		// end initialization

		// calculates the best agreement, worst agreement and the utility value
		// per agreement
		// going over all agreements and calculating the best/worst agreement
		for (int i = 0; i < totalAgreementsNumber; ++i)
		{
			// Note: the agreements are saved based on their indices
			// At the end of the loop the indices are incremented
			dAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx,
				nCurrentTurn);

			// check for best agreement
			if (dAgreementValue > agentTools.getBestAgreementValue(agentType))
			{// save agreement
				agentTools.setBestAgreementValue(agentType, dAgreementValue);
				agentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);
			}
			// check for worst agreement
			if (dAgreementValue < agentType.getWorstAgreementValue())
			{// save agreement
				agentTools.setWorstAgreementValue(agentType, dAgreementValue);
				agentTools.setWorstAgreementIndices(agentType, CurrentAgreementIdx);
			}

			// get the next agreement indices
			agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);
		}
	}

	/**
	 * Get AgreementIdx and return the AutomatedAgentType that fit the most for
	 * that agreement fit the most = have the biggest value for that agreement
	 * 
	 * @param AgreementIdx - the agreement to check
	 * @param curTurn - the current turn number
	 */
	public AutomatedAgentType findOpponentType(int AgreementIdx[], int curTurn)
	{
		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);

		double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(
			agentOpponentCompromise, AgreementIdx, curTurn);
		double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(
			agentOpponentLongTerm, AgreementIdx, curTurn);
		double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(
			agentOpponentShortTerm, AgreementIdx, curTurn);

		double temp_fitOpponentType[] = new double[3];
		temp_fitOpponentType[AutomatedAgentsCore.LONG_TERM_TYPE_IDX] = dOpponentLongTermAgreementValue;
		temp_fitOpponentType[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX] = dOpponentShortTermAgreementValue;
		temp_fitOpponentType[AutomatedAgentsCore.COMPROMISE_TYPE_IDX] = dOpponentCompromiseAgreementValue;

		double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		int maxValueIndex = 0;
		for (int i = 0; i < fitOpponentType.length; i++)
		{
			if (temp_fitOpponentType[i] > maxValue)
			{
				maxValue = fitOpponentType[i];
				maxValueIndex = i;
			}
		}

		return agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
	}

	/**
	 * Get agreed AgreementIdx and update the fitOpponentType array, check the
	 * value for that Agreement for all AutomatedAgentType and update
	 * fitOpponentType array update OpponentAgentType according to the highest
	 * result
	 * 
	 * @param AgreementIdx - the agreement to check
	 * @param curTurn - the current turn number
	 */
	public void updateOpponentType(int AgreementIdx[], int curTurn)
	{
		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(
			sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);

		double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(
			agentOpponentCompromise, AgreementIdx, curTurn);
		double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(
			agentOpponentLongTerm, AgreementIdx, curTurn);
		double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(
			agentOpponentShortTerm, AgreementIdx, curTurn);

		fitOpponentType[AutomatedAgentsCore.LONG_TERM_TYPE_IDX] += dOpponentLongTermAgreementValue;
		fitOpponentType[AutomatedAgentsCore.SHORT_TERM_TYPE_IDX] += dOpponentShortTermAgreementValue;
		fitOpponentType[AutomatedAgentsCore.COMPROMISE_TYPE_IDX] += dOpponentCompromiseAgreementValue;

		// find the highest value
		double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		int maxValueIndex = 0;
		for (int i = 0; i < fitOpponentType.length; i++)
		{
			if (fitOpponentType[i] > maxValue)
			{
				maxValue = fitOpponentType[i];
				maxValueIndex = i;
			}
		}
		OpponentAgentType = agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
	}

	/**
	 * TO CHECK @return the most Seasonable AutomatedAgentType of the Opponent
	 * according to fitOpponentType array
	 */
	public AutomatedAgentType getMostSeasonableOpponentType()
	{
		double maxValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		int maxValueIndex = 0;
		for (int i = 0; i < fitOpponentType.length; i++)
		{
			if (fitOpponentType[i] > maxValue)
			{
				maxValue = fitOpponentType[i];
				maxValueIndex = i;
			}
		}

		return agentTools.getCurrentTurnSideAgentType(sOpponentType, maxValueIndex);
	}




	/**
	 * Find complete agreement to the partial Agreement that his value is more
	 * than min_value and is best for the given pOpponentAgentType. Save the
	 * results at: [bestFitCompleteAgreement] and his value
	 * [bestFitCompleteAgreementValue]
	 * 
	 * @param myAgentType - my automated agent's type
	 * @param pOpponentAgentType - Opponent automated agent's type (estimated)
	 * @param PartialAgreement - Partial Agreement
	 * @param curTurn - the current turn
	 * @param min_value - miv value for the complete agreement
	 */
	public void findBestFitcompletionOfPartialAgreement(AutomatedAgentType myAgentType,
		AutomatedAgentType pOpponentAgentType, int PartialAgreement[], int curTurn, double min_value)
	{
		// initialization - DO NOT CHANGE
		int nIssuesNum = agentTools.getTotalIssues(myAgentType);
		int CurrentAgreementIdx[] = new int[nIssuesNum];
		int MaxIssueValues[] = new int[nIssuesNum];
		int MinIssueValues[] = new int[nIssuesNum];
		int totalAgreementsNumber = agentTools.getTotalAgreements(myAgentType);
		for (int i = 0; i < nIssuesNum; ++i)
		{
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(myAgentType, i);
			MinIssueValues[i] = 0; // minimum value
			bestJointCompleteAgreement[i] = bestCompleteAgreement[i];
		}
		// best value for now is the value of the bestCompleteAgreement
		// which is best for me, but maybe not for Opponent
		bestJointCompleteAgreementValue_Opponent = agentTools.getAgreementValue(pOpponentAgentType,
			bestCompleteAgreement, curTurn);

		// end initialization

		double AgreementValue_forMe = 0;
		double AgreementValue_forOpponent = 0;

		// going over all agreements,
		// check if agreements complete the given PartialAgreement
		// check if their value is more then min_value
		// for each calculating the value
		for (int i = 0; i < totalAgreementsNumber; ++i)
		{
			// check if the if agreements complete the given PartialAgreement
			if (isCompletionOFPartialAgreement(myAgentType, CurrentAgreementIdx, PartialAgreement))
			{
				AgreementValue_forMe = agentTools.getAgreementValue(myAgentType,
					CurrentAgreementIdx, curTurn);
				AgreementValue_forOpponent = agentTools.getAgreementValue(pOpponentAgentType,
					CurrentAgreementIdx, curTurn);

				// check if the agreements is good for me
				if (AgreementValue_forMe > min_value)
				{
					// check if the current agreements is better for Opponent
					if (AgreementValue_forOpponent > bestJointCompleteAgreementValue_Opponent)
					{
						// save agreement
						for (int k = 0; k < nIssuesNum; ++k)
						{
							bestJointCompleteAgreement[k] = CurrentAgreementIdx[k];
						}
						// save value
						bestJointCompleteAgreementValue_Opponent = AgreementValue_forOpponent;
						bestJointCompleteAgreementValue_me = AgreementValue_forMe;
					}
				}
			}
			// get the next agreement indices
			agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);
		}

	}

	/**
	 * Calculate values of completion agreements of the given Partial Agreement
	 * Save the results at: [bestCompleteAgreement] and his value
	 * [bestCompleteAgreementValue]
	 * 
	 * @param agentType - the automated agent's type
	 * @param PartialAgreement - Partial Agreement
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateValues_CompletionOfPartialAgreement(AutomatedAgentType agentType,
		int PartialAgreement[], int nCurrentTurn)
	{
		// initialization - DO NOT CHANGE
		int nIssuesNum = agentTools.getTotalIssues(agentType);
		int CurrentAgreementIdx[] = new int[nIssuesNum];
		int MaxIssueValues[] = new int[nIssuesNum];
		int MinIssueValues[] = new int[nIssuesNum];
		int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
		for (int i = 0; i < nIssuesNum; ++i)
		{
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
			MinIssueValues[i] = 0; // minimum value
			bestCompleteAgreement[i] = 0;
		}
		bestCompleteAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		// end initialization

		double AgreementValue = 0;

		// going over all agreements, check if agreements complete the given
		// PartialAgreement
		// for each calculating the value
		for (int i = 0; i < totalAgreementsNumber; ++i)
		{
			// if the CurrentAgreementIdx complete the given PartialAgreement
			if (isCompletionOFPartialAgreement(agentType, CurrentAgreementIdx, PartialAgreement))
			{
				AgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx,
					nCurrentTurn);
				// check for best agreement
				if (AgreementValue > bestCompleteAgreementValue)
				{
					// save agreement
					for (int k = 0; k < nIssuesNum; ++k)
					{
						bestCompleteAgreement[k] = CurrentAgreementIdx[k];
					}
					// save value
					bestCompleteAgreementValue = AgreementValue;
				}
			}
			// get the next agreement indices
			agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);
		}
	}

	/**
	 * Calculate the BottomLimitValue for this turn.
	 * 
	 * @param agentType
	 * @param nCurrentTurn
	 */
	public double calculateBottomLimitValue(AutomatedAgentType agentType, int nCurrentTurn)
	{
		// find best and worst values for this turn
		calculateValues(agentType, nCurrentTurn);

		// fraction of time passed
		double timePassed = (double)nCurrentTurn / agentTools.getTurnsNumber();

		// last accepted agreement or zero
		double acceptedAgreementsValue = agentTools.getAcceptedAgreementsValue();
		if (acceptedAgreementsValue < 0.0)
		{
			acceptedAgreementsValue = 0.0;
		}

		// threat factor, trying to lower expectation, but more threats will
		// make things worse
		double threatFactor = (numberOfThreatsReceived == 0) ? 1.0 : 0.85;
		if (numberOfThreatsReceived > 1)
		{
			threatFactor *= Math.pow(1.1, numberOfThreatsReceived - 1);
		}

		// calculate the limit
		double lowLimit = (acceptedAgreementsValue + ((agentTools.getAgreementTimeEffect(agentType) < 0) ? (agentTools
			.getTurnsNumber()
			- nCurrentTurn - 1)
			* agentTools.getAgreementTimeEffect(agentType) / 100.0
			: 0.0));
		double addition = agentTools.getAgreementTimeEffect(agentType)
			* (agentTools.getTurnsNumber() - 1)
			/ ((agentTools.getAgreementTimeEffect(agentType) < 0) ? 100.0 : 300.0);
		double res = 0.8 * ((sortedBids.get(0).utility - lowLimit) * (1 - timePassed)
			* threatFactor + lowLimit + addition);
		System.out.println("Bottom limit " + res);
		return res;
	}

	/**
	 * Get: FullAgreementIdx and PartialAgreement Check if the
	 * CurrentAgreementIdx is completion of the PartialAgreement. Return: True
	 * if yes, False if no
	 * 
	 * @param agentType - the automated agent's type
	 * @param FullAgreementIdx - complete agreement
	 * @param PartialAgreementIdx - Partial Agreement
	 */
	public boolean isCompletionOFPartialAgreement(AutomatedAgentType agentType,
		int FullAgreementIdx[], int PartialAgreementIdx[])
	{
		int nIssuesNum = agentTools.getTotalIssues(agentType);
		for (int i = 0; i < nIssuesNum; ++i)
		{
			// check if value is set (not NotSet and not NoAgreement value)
			if (agentType.isIssueValueNotSet(PartialAgreementIdx[i])
				|| (agentType.isIssueValueNoAgreement(i, PartialAgreementIdx[i])))
				continue;
			else
			// value is set
			{ // check if same in full and partial agreements
				if (FullAgreementIdx[i] != PartialAgreementIdx[i])
					return false; // different values
			}
		}
		return true;
	}

	/**
	 * Check if the agreement is: Partial - return 0 , Include NoAgreement
	 * values - return 1 Full - return 2
	 * 
	 * @param agentType - the automated agent's type
	 * @param AgreementIdx - agreement
	 */
	public int getAgreemmentType(AutomatedAgentType agentType, int AgreementIdx[])
	{
		int nIssuesNum = agentTools.getTotalIssues(agentType);
		for (int i = 0; i < nIssuesNum; ++i)
		{
			if (agentType.isIssueValueNotSet(AgreementIdx[i]))
				return 0; // some value in the agreement not set - partial
			// agreement
			if (agentType.isIssueValueNoAgreement(i, AgreementIdx[i]))
				return 1; // some value in the agreement is NoAgreement
		}
		return 2; // Full agreement
	}


	/**
	 * Check if the agreement is full
	 * 
	 * @param agentType - the automated agent's type
	 * @param AgreementIdx - agreement
	 */
	public boolean isFullAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
	{
		return (getAgreemmentType(agentType, AgreementIdx) == 2);
	}


	/**
	 * Check if the agreement is Semi full (with NoAgreement values)
	 * 
	 * @param agentType - the automated agent's type
	 * @param AgreementIdx - agreement
	 */
	public boolean isSemiFullAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
	{
		return (getAgreemmentType(agentType, AgreementIdx) == 1);
	}


	/**
	 * Check if the agreement is real partial (there are no agreed issues)
	 * 
	 * @param agentType - the automated agent's type
	 * @param AgreementIdx - agreement
	 */
	public boolean isRealPartialAgreemment(AutomatedAgentType agentType, int AgreementIdx[])
	{
		return (getAgreemmentType(agentType, AgreementIdx) == 0);
	}

	
	protected void initPossibleBids()
	{
		if ((sortedBids == null) || (sortedBids.isEmpty()))
		{
			// gather all possible bids
			// ------------------------

			// list of all possible bids
			sortedBids = new ArrayList<BidUtilityPair>();

			// initial bid
			int numOfIssues = agentTools.getTotalIssues(myAgentType);

			int[] currentBid = new int[numOfIssues];
			Arrays.fill(currentBid, 0);

			boolean finished = false;
			while (!finished)
			{
				// save current option (if by mistake it is wrong, ignore it)
				try
				{
					sortedBids.add(new BidUtilityPair(currentBid, myAgentType.getAgreementValue(
						currentBid, 0)));
				} catch (Exception e)
				{
					// should not happen, but if it did - continue to other
					// options
				}
				// move to next option
				boolean bFinishUpdate = false;
		        for (int k = numOfIssues-1; k >= 0 && !bFinishUpdate; --k)
		        {
		            if (currentBid[k]+1 >= agentTools.getMaxValuePerIssue(myAgentType,k))
		            {
		            	currentBid[k] = 0;
		            }
		            else
		            {
		            	currentBid[k]++;
		                bFinishUpdate = true;
		            }                                   
		        }

				// if all values are start value - we've got over all options
				finished = true;
				for (int i=0;i<numOfIssues;i++)
					if (currentBid[i]!=0)
						finished=false;
			}


			// sort all bids by their utility
			// ------------------------------

			Collections.sort(sortedBids, new Comparator<BidUtilityPair>()
			{
				public int compare(BidUtilityPair o1, BidUtilityPair o2)
				{
					return (int)(Math.signum(o2.utility - o1.utility));
				}
			});
		}
	}
}
