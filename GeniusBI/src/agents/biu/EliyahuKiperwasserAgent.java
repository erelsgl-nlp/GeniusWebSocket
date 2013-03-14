package agents.biu;

import java.util.*;

/**
 * @author raz This class should hold all your logic for your automated agent
 *         Examples are provided inline and marked as examples
 * 
 */
public class EliyahuKiperwasserAgent extends OldAgentAdapter{


	ArrayList<int[]> m_RejectedOffers = new ArrayList<int[]>();

	ArrayList<int[]> m_AcceptedOffers = new ArrayList<int[]>();

	String m_SideName, m_OpponentSideName;

	public EliyahuKiperwasserAgent() {
		super();
	}

	/**
	 * Constructor Save a pointer to the AgentTools class
	 * 
	 * @param agentTools -
	 *            pointer to the AgentTools class
	 */
	public EliyahuKiperwasserAgent(AgentTools agentTools) {
		this.agentTools = agentTools;
	}

	/**
	 * Called before the the nagotiation starts. Add any logic you need here.
	 * For example, calculate the very first offer you'll offer the opponent
	 * 
	 * @param agentType -
	 *            the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		/*
		 * Negotiation is about to start You can add logic if needed to update
		 * your agent @@EXAMPLE@@ For example: calculate the first offer the
		 * automated agent offers the opponent and send it
		 */

		/***********************************************************************
		 * Start example code
		 **********************************************************************/

		m_SideName = agentTools.getAgentSide();
		if (m_SideName.equals(AutomatedAgent.SIDE_B_NAME))
			m_OpponentSideName = AutomatedAgent.SIDE_A_NAME;
		else
			m_OpponentSideName = AutomatedAgent.SIDE_B_NAME;

		// calculate Automated Agent first offer
		calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
		// agentTools.sendComment("My Init prop is
		// "+agentTools.getCurrentTurnAutomatedAgentValue());
		/***********************************************************************
		 * End example code
		 **********************************************************************/
	}

	/**
	 * Called when a message of type: QUERY, COUNTER_OFFER, OFFER or PROMISE is
	 * received Note that if you accept a message, the accepted message is saved
	 * in the appropriate structure, so no need to add logic for this.
	 * 
	 * @param nMessageType -
	 *            the message type
	 * @param CurrentAgreementIdx -
	 *            the agreement indices
	 * @param sOriginalMessage -
	 *            the message itself as string
	 */
	public void calculateResponse(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {
		// Calculating the response
		// You can decide on your actions for that turn
		// You can decide on different logic based on the message type
		// In case you accept an offer, you might decide NOT to
		// send an offer you calculated before and just waited for
		// it to be sent. To do so, use the "send flag" as in
		// the example below
		// @@EXAMPLE@@
		// For example:
		// 1 - if the newly offer has lower utility values than already
		// accepted agreement, reject it;
		// 2 - if the automated agent is going to propose an offer with lower
		// utility values to
		// it in the next turn, accept the opponent's offer and
		// don't send any offer of your own
		// 3 - else, always accept

		/***********************************************************************
		 * Start example code
		 **********************************************************************/
		// decide whether to accept the message or reject it:
		double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
		double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;

		// Check the utility value of the opponent's offer
		dOppOfferValueForAgent = agentTools
				.getAgreementValue(CurrentAgreementIdx);

		// 1. check whether previous accepted agreement is better - if so,
		// reject
		double dAcceptedAgreementValue = agentTools
				.getAcceptedAgreementsValue();

		if (dAcceptedAgreementValue >= dOppOfferValueForAgent) {
			// reject offer
			agentTools.rejectMessage(sOriginalMessage);
			return;
		}

		// 2. check the value of the automated agent in the next turn
		agentTools.calculateNextTurnOffer();
		dAutomatedAgentNextOfferValueForAgent = dMaxAutomatedAgentAgreementValue;// agentTools.getNextTurnOfferValue();

		if (dOppOfferValueForAgent >= dAutomatedAgentNextOfferValueForAgent) {
			//System.err.println("I Accepted");
			// accept offer
			agentTools.acceptMessage(sOriginalMessage);

			// prevent sending future offer in this turn
			agentTools.setSendOfferFlag(false);

			/*
			 * @@ You accepted opponent's message The automated agent accepts a
			 * messsage here You can add logic if needed to update your agent
			 * Note: The accepted message is saved in the appropriate structure.
			 * No need to add logic for this
			 */
		} else {
			//System.err.println("I Rejected");
			// accept offer
			agentTools.rejectMessage(sOriginalMessage);

			// prevent sending future offer in this turn
			agentTools.setSendOfferFlag(false);

			/*
			 * @@ You accepted opponent's message The automated agent accepts a
			 * messsage here You can add logic if needed to update your agent
			 * Note: The accepted message is saved in the appropriate structure.
			 * No need to add logic for this
			 */
		}
		/***********************************************************************
		 * End example code
		 **********************************************************************/
	}

	/***************************************************************************
	 * @@ Logic for receiving messages Below are messages the opponent sends to
	 *    the automated agent You can add logic if needed to update your agent
	 *    per message type
	 **************************************************************************/

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
	 * @param sThreat -
	 *            the received threat
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
	 * @param nMessageType -
	 *            the type of massage the oppnent aggreed to, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx -
	 *            the indices of the agreement the opponent agreed to
	 * @param sOriginalMessage -
	 *            the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {
		/*
		 * @@ Received a message: opponent accepted the
		 * offer/promise/query/counter offer. You can add logic if needed to
		 * update your agent For example, if the message was a promise, you can
		 * now try and offer it as a formal offer...
		 */
		int[] temp = new int[6];
		for (int i = 0; i < 6; ++i) {
			temp[i] = CurrentAgreementIdx[i];
		}
		m_AcceptedOffers.add(temp);
		/*
		 * for(int i=0;i<AutomatedAgentsCore.AGENT_TYPES_NUM;i++) {
		 * AutomatedAgentType t =
		 * agentTools.getCurrentTurnSideAgentType(m_OpponentSideName,i); double
		 * temp =
		 * agentTools.getAgreementValue(t,CurrentAgreementIdx,agentTools.getCurrentTurn());
		 * 
		 * m_MinOffersUtils[i] =
		 * (m_MinOffersUtils[i]>temp)?m_MinOffersUtils[i]:temp; }
		 * agentTools.sendComment("He Accepted!!!");
		 */
	}

	/**
	 * called whenever the opponent rejected one of your massages (promise,
	 * query, offer or counter offer)
	 * 
	 * @param nMessageType -
	 *            the type of massage the oppnent rejected, can be
	 *            AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx -
	 *            the indices of the agreement the opponent agreed to
	 * @param sOriginalMessage -
	 *            the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[],
			String sOriginalMessage) {
		/*
		 * @@ Received a message: opponent rejected the
		 * offer/promise/query/counter offer. You can add logic if needed to
		 * update your agent
		 */
		int[] temp = new int[6];
		for (int i = 0; i < 6; ++i) {
			temp[i] = CurrentAgreementIdx[i];
		}
		m_RejectedOffers.add(temp);
		/*
		 * for(int i=0;i<AutomatedAgentsCore.AGENT_TYPES_NUM;i++) {
		 * AutomatedAgentType t =
		 * agentTools.getCurrentTurnSideAgentType(m_OpponentSideName,i); double
		 * temp =
		 * agentTools.getAgreementValue(t,CurrentAgreementIdx,agentTools.getCurrentTurn());
		 * 
		 * m_MaxOffersUtils[i] = (m_MaxOffersUtils[i]<temp)?m_MaxOffersUtils[i]:temp; }
		 */
	}

	/***************************************************************************
	 * @@ End of methods for receiving message
	 **************************************************************************/
	double dMaxAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;

	/**
	 * called to decide which offer to propose the opponent at a given turn This
	 * method is always called when beginning a new turn You can also call it
	 * during the turn if needed
	 * 
	 * @param agentType -
	 *            the automated agent's type
	 * @param sOpponentType -
	 *            the opponent's type
	 * @param nCurrentTurn -
	 *            the current turn
	 */
	public void calculateOfferAgainstOpponent(AutomatedAgentType agentType,
			String sOpponentType, int nCurrentTurn) {
		// @@ Add any logic to calculate offer (or several offers)
		// and decide which to send to the opponent in a given turn

		/***********************************************************************
		 * @@EXAMPLE@@ In the following example, ONE offer is chosen to be send
		 *             to the opponent based on the following logic: It will be
		 *             sent only if it has a value higher than an offer already
		 *             accepted.
		 * 
		 * You can see in the example how to: a) obtain the different possible
		 * types of opponent b) get the total number of issues in the
		 * negotiation c) get the total number of agreements in the negotiation
		 * d) get the maximal value of a certain issue for each agent e) go over
		 * all possible agreements and evaluate them f) compare the agreement to
		 * previously accepted agreement g) save one offer for later references
		 * 
		 * 
		 * /******************************** Start example code
		 **********************************************************************/

		double minOffersUtils[] = new double[AutomatedAgentsCore.AGENT_TYPES_NUM];
		double maxOffersUtils[] = new double[AutomatedAgentsCore.AGENT_TYPES_NUM];
		
		// calculate Automated Agent offer
		double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;

		int totalIssuesNum = agentTools.getTotalIssues(agentType);
		int totalAgreementsNumber = agentTools.getTotalAgreements(agentType);

		int CurrentAgreementIdx[] = new int[totalIssuesNum];
		int CurrentAgreementIdx2[] = new int[totalIssuesNum];
		int CurrentAgreementIdx3[] = new int[totalIssuesNum];
		int MaxIssueValues[] = new int[totalIssuesNum];

		for (int i = 0; i < totalIssuesNum; ++i) {
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
		}

		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = null;
		AutomatedAgentType agentOpponentLongTerm = null;
		AutomatedAgentType agentOpponentShortTerm = null;

		agentOpponentCompromise = agentTools.getNextTurnSideAgentType(
				sOpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		agentOpponentLongTerm = agentTools.getNextTurnSideAgentType(
				sOpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		agentOpponentShortTerm = agentTools.getNextTurnSideAgentType(
				sOpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);


		// Now, we go over all the possible agreements,
		// First, we calculate the value of each agreement for the automated
		// agent;
		// Then, we calculate the value of each such agreement for the different
		// possible opponent types
		// We only save the last value calculated.
		// You can change this logic, of course...

		// In this example, we only calculate for the long term orientation
		int OpponentLongTermIdx[] = new int[totalIssuesNum];
		double dOpponentLongTermAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;

		double dAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		dMaxAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;

		/** *********************************** */

		/** ************************************************************************************************** */
		double dAgreementValue = 0;

		for (int currAgentType = 0; currAgentType < AutomatedAgentsCore.AGENT_TYPES_NUM; currAgentType++) {
			double min = Double.NEGATIVE_INFINITY;
			double max = Double.POSITIVE_INFINITY;

			AutomatedAgentType t = agentTools.getNextTurnSideAgentType(
					sOpponentType, currAgentType);
			dOpponentLongTermAgreementValue = agentTools.getAgreementValue(t,
					CurrentAgreementIdx, nCurrentTurn);

			for (int[] offer : m_RejectedOffers) {
				dAgreementValue = agentTools.getAgreementValue(t, offer,
						nCurrentTurn);
				if (max > dAgreementValue)
					max = dAgreementValue;
			}

			for (int[] offer : m_AcceptedOffers) {
				dAgreementValue = agentTools.getAgreementValue(t, offer,
						nCurrentTurn);
				if (min < dAgreementValue)
					min = dAgreementValue;
			}

			maxOffersUtils[currAgentType] = Double.NEGATIVE_INFINITY;
			minOffersUtils[currAgentType] = Double.POSITIVE_INFINITY;

			// going over all agreements and calculating the best/worst
			// agreement
			for (int i = 0; i < totalAgreementsNumber; ++i) {
				// Note: the agreements are saved based on their indices
				// At the end of the loop the indices are incremeneted
				dAgreementValue = agentTools.getAgreementValue(t,
						CurrentAgreementIdx, nCurrentTurn);

				// check for best agreement
				if ((dAgreementValue > maxOffersUtils[currAgentType])
						&& (dAgreementValue <= max)) {
					maxOffersUtils[currAgentType] = dAgreementValue;
					for (int j = 0; j < totalIssuesNum; ++j) {
						CurrentAgreementIdx2[j] = CurrentAgreementIdx[j];
					}
				}

				// check for worst agreement
				if ((dAgreementValue < minOffersUtils[currAgentType])
						&& (dAgreementValue >= min)) {
					minOffersUtils[currAgentType] = dAgreementValue;
					for (int j = 0; j < totalIssuesNum; ++j) {
						CurrentAgreementIdx3[j] = CurrentAgreementIdx[j];
					}
				}

				agentTools.getNextAgreement(agentTools
						.getTotalIssues(agentType), CurrentAgreementIdx,
						MaxIssueValues);// get the next agreement indices
			}

			System.err.println();
			System.err.println("Max IDX " + currAgentType);
			for (int j = 0; j < totalIssuesNum; ++j) {
				System.err.print(CurrentAgreementIdx2[j] + ", ");
			}
			System.err.println(maxOffersUtils[currAgentType]);
			System.err.println("Min IDX " + currAgentType);
			for (int j = 0; j < totalIssuesNum; ++j) {
				System.err.print(CurrentAgreementIdx3[j] + ", ");
			}
			System.err.println(minOffersUtils[currAgentType]);
		}

		/** ************************************************************************************************** */

		double maxVal = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < totalAgreementsNumber; ++i) {
			boolean skip = !m_RejectedOffers.isEmpty();

			for (int[] offer : m_AcceptedOffers) {
				for (int ii = 0; ii < 6; ii++)
					if (offer[ii] != CurrentAgreementIdx[ii]) {
						skip = false;
						break;
					}
			}

			for (int[] offer : m_RejectedOffers) {
				for (int ii = 0; ii < 6; ii++)
					if (offer[ii] != CurrentAgreementIdx[ii]) {
						skip = false;
						break;
					}
			}
			if (skip) {
				agentTools.getNextAgreement(totalIssuesNum,
						CurrentAgreementIdx, MaxIssueValues);// get the next
																// agreement
																// indices
				continue;
			}
			skip = !m_RejectedOffers.isEmpty();

			double currVal = 1;
			boolean consider = true;
			dAutomatedAgentAgreementValue = agentTools.getAgreementValue(
					agentType, CurrentAgreementIdx, nCurrentTurn);
			int agentCount = 0;
			for (int currAgentType = 0; currAgentType < AutomatedAgentsCore.AGENT_TYPES_NUM; currAgentType++) {
				AutomatedAgentType t = agentTools.getCurrentTurnSideAgentType(
						sOpponentType, currAgentType);
				dOpponentLongTermAgreementValue = agentTools.getAgreementValue(
						t, CurrentAgreementIdx, nCurrentTurn);

				if ((maxOffersUtils[currAgentType] >= minOffersUtils[currAgentType])) 
				{
					if ((dOpponentLongTermAgreementValue < maxOffersUtils[currAgentType])
							&& (dOpponentLongTermAgreementValue > minOffersUtils[currAgentType])) 
					{
						double t1 = ((maxOffersUtils[currAgentType] - dOpponentLongTermAgreementValue + agentTools.getCurrentTurn()) / 
								     (maxOffersUtils[currAgentType] - minOffersUtils[currAgentType] + agentTools.getCurrentTurn()));

						currVal *= t1;
						agentCount++;
					}
				}
			}
			if (agentCount > 0)
				currVal = Math.pow(currVal, 1 / agentCount);

			double myProbAcc = 1 - Math.pow(
							((agentTools.getBestAgreementValue(agentType) - dAutomatedAgentAgreementValue) / 
							 (agentTools.getBestAgreementValue(agentType) - agentTools.getWorstAgreementValue(agentType))),
							1 + 0.5 * ((double) (agentTools.getTurnsNumber() - agentTools.getCurrentTurn()))
									/ ((double) agentTools.getTurnsNumber()));

			currVal *= myProbAcc;

			if ((maxVal < currVal) && (consider) && (agentCount > 0)) {
				for (int j = 0; j < totalIssuesNum; ++j) {
					OpponentLongTermIdx[j] = CurrentAgreementIdx[j];
				}
				maxVal = currVal;
				dMaxAutomatedAgentAgreementValue = dAutomatedAgentAgreementValue;
			}
			agentTools.getNextAgreement(totalIssuesNum, CurrentAgreementIdx,
					MaxIssueValues);// get the next agreement indices
		}
		/** *********************************** */

		// select which offer to propose
		// In this example, selecting the last offer that was calculated
		dOpponentLongTermAgreementValue = maxVal;

		double optOutVal = agentTools.getOptOutValue(agentType);

		if (dMaxAutomatedAgentAgreementValue < optOutVal) {
			agentTools.optOut();
		}
		if (dMaxAutomatedAgentAgreementValue > agentTools.getCurrentTurnAutomatedAgentValue()) 
		{
			// you can save the values for later reference ($1)
			System.err.println("Best value for this turn is "
					+ dMaxAutomatedAgentAgreementValue);
			agentTools
					.setCurrentTurnAutomatedAgentValue(dMaxAutomatedAgentAgreementValue);
			agentTools
					.setCurrentTurnOpponentSelectedValue(
							(agentOpponentLongTerm.getAgreementValue(OpponentLongTermIdx, nCurrentTurn) +
							agentOpponentShortTerm.getAgreementValue(OpponentLongTermIdx, nCurrentTurn) + 
							agentOpponentCompromise.getAgreementValue(OpponentLongTermIdx,nCurrentTurn)) / 3);
			agentTools.setCurrentTurnAgreementString(agentType.getAgreementStr(OpponentLongTermIdx));
		}

		// Now, the agent's core holds the new selected agreement

		// check the value of the offer (the one saved before, see $1...)
		double dNextAgreementValue = agentTools.getSelectedOfferValue();

		// get the value of previously accepted agreement
		double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue();

		// Now, check whether the offer the agent intends to propose in the next
		// turn is better
		// for it than previously accepted agreement

		// if the value of the offer is lower than already accepted offer, don't
		// send it...
		//if (dAcceptedAgreementValue <= dNextAgreementValue) {
			// if decided to send offer - then send the offer
			// Get the offer as string and format it as an offer
			String sOffer = agentTools.getSelectedOffer();
			agentTools.sendOffer(sOffer);
		//}

		
		/***********************************************************************
		 * End example code
		 **********************************************************************/
	}

	/**
	 * called to calculate the values of the different possible agreements for
	 * the agent
	 * 
	 * @param agentType -
	 *            the automated agent's type
	 * @param nCurrentTurn -
	 *            the current turn
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
		/***********************************************************************
		 * Start example code
		 **********************************************************************/
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
		/***********************************************************************
		 * End example code
		 **********************************************************************/
	}
}
