package agents.biu;

import java.util.*;

/**
 * @author Tomer Zilberstein 066207093
 * my agent negotiation logic
 */ 
public class TomerZilbersteinAgent extends OldAgentAdapter{
	/**
	 * @author Tomer Zilberstein 066207093
	 * class to represent agreement
	 */
	private class Agreement implements Comparable
	{
		int[] agreement;
		double value;
		public Agreement(int[] agreement)
		{
			this.agreement = new int[agreement.length];
			for (int i =0 ; i<agreement.length; i++) 
			{
				this.agreement[i] = agreement[i];
			}
			value = myAgentType.getAgreementValue(agreement, agentTools.getCurrentTurn());
		}
		
		public Agreement(int[] agreement, double value)
		{
			this.agreement = new int[agreement.length];
			for (int i =0 ; i<agreement.length; i++) 
			{
				this.agreement[i] = agreement[i];
			}
			this.value = value;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Agreement))
				return false;
			Agreement ag = (Agreement)obj;
			for(int i=0; i < agreement.length && i < ag.agreement.length; i++)
			{
				if(ag.agreement[i] != agreement[i])
					return false;
			}
			return true;
		}

		public int[] getAgreement()
		{
			return agreement;
		}

		public int compareTo(Object o) {
			Agreement a = (Agreement)o;
			return (int)Math.signum(a.value - this.value);
		}

	}
	//consts to describe opponent types
	private final int LONGTERM = 1;
	private final int SHORTTERM = 2;
	private final int COMPROMISE = 3;
	private final int UNKNOWN = 4;

	//local variables
	private ArrayList<String> comments;
	private ArrayList<String> threats;
	private AutomatedAgentType myAgentType;
	private String opponentSide;
	private ArrayList<Agreement> pastOffers;
	private boolean threatSent = false;
	private int longTermCounter = 0;
	private int shortTermCounter = 0;
	private int compromiseCounter = 0;
	private int opponentGuess;
	private ArrayList<Agreement> Agreements;
	private int offersSent;
	private boolean valuesCalculated = false;
	private int currentTurn = -1;

	public TomerZilbersteinAgent() {
		super();
		comments = new ArrayList<String>();
		comments.add("I feel that we are on the right track for reaching an agreement");
		comments.add("I don't believe that you are just in your intentions");
		comments.add("If you will try to compromise I will also try to go towards you");
		threats = new ArrayList<String>();
		threats.add("If an agreement is not reached by the next round i will toughen my stands");
		threats.add("If an agreement is not reached by the next round i will opt out");
		threats.add("If i do not receive an offer that i like i will have to think through about continuing the negotiation");
		pastOffers = new ArrayList<Agreement>();
		opponentGuess = UNKNOWN;
		Agreements = new ArrayList<Agreement>();
		offersSent = -1;
	}

	/**
	 * Constructor
	 * Save a pointer to the AgentTools class
	 * Initialize some local variables
	 * @param agentTools - pointer to the AgentTools class
	 */
	public TomerZilbersteinAgent(AgentTools agentTools) {
		this.agentTools = agentTools;
		comments = new ArrayList<String>();
		comments.add("I feel that we are on the right track for reaching an agreement");
		comments.add("I don't believe that you are just in your intentions");
		comments.add("If you will try to compromise I will also try to go towards you");
		threats = new ArrayList<String>();
		threats.add("If an agreement is not reached by the next round i will toughen my stands");
		threats.add("If an agreement is not reached by the next round i will opt out");
		threats.add("If i do not receive an offer that i like i will have to think through about continuing the negotiation");
		pastOffers = new ArrayList<Agreement>();
		opponentGuess = UNKNOWN;
		Agreements = new ArrayList<Agreement>();
		offersSent = -1;
	}

	/**
	 * Called before the the nagotiation starts.
	 * save the agent type and the opponent type for future use
	 * calculate the next offer only if the time has negative effect 
	 * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		/* Negotiation is about to start
		 */
		myAgentType = agentType;
		opponentSide = sOpponentType;
		double timeEffect = agentTools.getAgreementTimeEffect(agentType);

		// if the time effect is positive,I would like to wait not to send the first offer right away 
		if(timeEffect < 0)
		{
			//calculate Automated Agent first offer
			calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
		}
	}

	/**
	 * guess the opponent type by the offer it send
	 */
	private void guessOpponentType()
	{
		if(longTermCounter > shortTermCounter)
		{
			if(longTermCounter > compromiseCounter)
			{
				opponentGuess = LONGTERM;
			}
			else
			{
				opponentGuess = COMPROMISE;
			}
		}
		else // short is better than long
		{
			if(shortTermCounter > compromiseCounter)
			{
				opponentGuess = SHORTTERM;
			}
			else
			{
				opponentGuess = COMPROMISE;
			}
		}
	}
	
	/** 
	 * Called when a message of type:
	 * QUERY, COUNTER_OFFER, OFFER or PROMISE 
	 * is received
	 * @param nMessageType - the message type
	 * @param CurrentAgreementIdx - the agreement indices
	 * @param sOriginalMessage - the message itself as string
	 */
	public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		//Calculating the response

		//store the agreement in the past agreements list
		Agreement ag = new Agreement(CurrentAgreementIdx);
		if(!pastOffers.contains(ag))
		{
			pastOffers.add(ag);
		}

		// the different possible agents for the opponent side
		AutomatedAgentType agentOpponentCompromise = agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		AutomatedAgentType agentOpponentLongTerm = agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		AutomatedAgentType agentOpponentShortTerm = agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);

		//the values of the opponents for the given agreement
		double dOpponentLongTermAgreementValue = agentTools.getAgreementValue(agentOpponentLongTerm, CurrentAgreementIdx, agentTools.getCurrentTurn());
		double dOpponentCompromiseAgreementValue = agentTools.getAgreementValue(agentOpponentCompromise, CurrentAgreementIdx, agentTools.getCurrentTurn());
		double dOpponentShortTermAgreementValue = agentTools.getAgreementValue(agentOpponentShortTerm, CurrentAgreementIdx, agentTools.getCurrentTurn());
		//guess the type who most likely to suggest the given offer
		if(dOpponentLongTermAgreementValue > dOpponentShortTermAgreementValue)
		{
			if(dOpponentLongTermAgreementValue > dOpponentCompromiseAgreementValue)
			{
				longTermCounter++;
			}
			else
			{
				compromiseCounter++;
			}
		}
		else // short is better than long
		{
			if(dOpponentShortTermAgreementValue > dOpponentCompromiseAgreementValue)
			{
				shortTermCounter++;
			}
			else
			{
				compromiseCounter++;
			}
		}
		guessOpponentType();
		// Check the utility value of the opponent's offer
		double dOppOfferValueForAgent = agentTools.getAgreementValue(CurrentAgreementIdx); 

		//check whether previous accepted agreement is better - if so, reject
		double dAcceptedAgreementValue = agentTools.getAcceptedAgreementsValue(); 
		//accepted agreement is better than the offer given - so reject
		if (dAcceptedAgreementValue >= dOppOfferValueForAgent)
		{
			// reject offer
			agentTools.rejectMessage(sOriginalMessage);
			return;
		}
		//re-calculate the values if needed 
		calculateValues(myAgentType, agentTools.getCurrentTurn());
		if(offersSent == -1)
			offersSent = Agreements .size() / 10;
		double nextValue = AutomatedAgentType.VERY_HIGH_NUMBER;
		if(offersSent < Agreements.size())
		{
			nextValue = myAgentType.getAgreementValue(Agreements.get(offersSent).getAgreement(), agentTools.getCurrentTurn());
		}
		else
		{
			nextValue = Math.max(agentTools.getSQValue(myAgentType), agentTools.getOptOutValue(myAgentType));
		}
		//the offer is better than what I intended to offer - so accept
		if(dOppOfferValueForAgent >= nextValue)
		{
			// accept offer
			agentTools.acceptMessage(sOriginalMessage);

			//prevent sending future offer in this turn
			agentTools.setSendOfferFlag(false);
			return;
		}
		//reject
		agentTools.rejectMessage(sOriginalMessage);
	}

	/***********************************************
	 * @@ Logic for receiving messages
	 * Below are messages the opponent sends to the automated agent
	 ***********************************************/

	/**
	 * called whenever we get a comment from the opponent
	 * You can add logic to update your agent
	 * @param sComment -the received comment
	 */
	public void commentReceived(String sComment) {
		/* @@ Received a comment from the opponent
		 */
	}

	/**
	 * called whenever we get a threat from the opponent
	 * You can add logic to update your agent
	 * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {
		/* @@ Received a threat from the opponent
		 */
	}

	/**
	 * called whenever the opponent agreed to one of your massages (promise, query, offer or counter offer).
	 * @param nMessageType - the type of massage the oppnent aggreed to, can be
	 * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
	 * @param sOriginalMessage - the original message that was accepted
	 */
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		/* @@ Received a message: opponent accepted the offer/promise/query/counter offer.
		 */

		//store the agreement in the past agreements list
		Agreement ag = new Agreement(CurrentAgreementIdx);
		if(!pastOffers.contains(ag))
		{
			pastOffers.add(ag);
		}

		//if the opponent agreed to a promise or a query, lets make is official offer
		if(nMessageType == AutomatedAgentMessages.PROMISE || nMessageType == AutomatedAgentMessages.QUERY)
		{
			agentTools.sendOffer(sOriginalMessage);
		}
		else //the opponent agreed to an offer or counter offer
		{
			//we have partial agreement, lets try to achieve full agreement
			calculateOfferAgainstOpponent(myAgentType, opponentSide, agentTools.getCurrentTurn());
		}
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
		 */

		//store the agreement in the past agreements list
		Agreement ag = new Agreement(CurrentAgreementIdx);
		if(!pastOffers.contains(ag))
		{
			pastOffers.add(ag);
		}
		//if the time effect is negative, try a new offer
		if(agentTools.getAgreementTimeEffect(myAgentType) < 0)
		{
			calculateOfferAgainstOpponent(myAgentType, opponentSide, agentTools.getCurrentTurn());
		}
	}

	/***********************************************
	 * @@ End of methods for receiving message
	 ***********************************************/

	/**
	 * get guessed opponent type 
	 */
	private AutomatedAgentType getBestOpponentGuess()
	{
		if(opponentGuess == COMPROMISE)
		{
			return agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
		}
		if(opponentGuess == LONGTERM)
		{
			return agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
		}
		if(opponentGuess == SHORTTERM)
		{
			return agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
		}
		return myAgentType;
	}

	/**
	 * called to decide which offer to propose the opponent at a given turn
	 * @param agentType - the automated agent's type
	 * @param sOpponentType - the opponent's type
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
		// calculate Automated Agent offer
		currentTurn = nCurrentTurn;
		calculateValues(agentType, nCurrentTurn);
		//checking my options
		double dStatusQuoValue = agentTools.getSQValue(agentType);
		double dOptOutValue = agentTools.getOptOutValue(agentType);
		//the value of the agreement already achieved - the minimum value for agreements from now on
		double dCurrentAgentAgreementValue = agentTools.getAcceptedAgreementsValue();

		//initialization
		int totalIssuesNum = agentTools.getTotalIssues(agentType);

		int CurrentAgreementIdx[] = new int[totalIssuesNum];
		int MaxIssueValues[] = new int[totalIssuesNum];

		for (int i = 0; i < totalIssuesNum; ++i)
		{
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
		}
		
		if(offersSent == -1)
			offersSent = Agreements.size() / 10;
		AutomatedAgentType opponent = getBestOpponentGuess();
		//get the best offer
		double bestValue = 0;
		Agreement best = null;
		if(offersSent < Agreements.size())
		{
			best = Agreements.get(offersSent);
			bestValue = agentType.getAgreementValue(best.getAgreement(), nCurrentTurn);
		}
		//if the best offer is too low opt out or wait for the status quo
		if(bestValue < dOptOutValue || bestValue < dStatusQuoValue || bestValue < dCurrentAgentAgreementValue
				|| best == null)
		{
			if(dOptOutValue > dStatusQuoValue)
			{
				if(threatSent)
				{
					agentTools.optOut();
				}
				else
				{
					agentTools.sendThreat(threats.get(2));
					threatSent = true;
				}
			}
			//else just wait for the status quo
			return;
		}

		//Get the offer as string and format it as an offer
		String sOffer = agentType.getAgreementStr(best.getAgreement());
		agentTools.sendOffer(sOffer);
		offersSent++;
		pastOffers.add(best);
	}

	/**
	 * called to calculate the values of the different possible agreements for the agent
	 * @param agentType - the automated agent's type
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
		if(valuesCalculated && currentTurn != nCurrentTurn)
			return;
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

		Agreements.clear();

		double dAutomatedAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		//the value of the agreement already achieved - the minimum value for agreements from now on
		double dCurrentAgentAgreementValue = AutomatedAgentType.VERY_SMALL_NUMBER;
		try
		{
			dCurrentAgentAgreementValue = agentTools.getAcceptedAgreementsValue();
		}
		catch (Exception e) 
		{
			//agent framework bug - ignore it
		}
		//checking my options
		double dStatusQuoValue = agentTools.getSQValue(agentType);
		double dOptOutValue = agentTools.getOptOutValue(agentType);

		// going over all agreements and calculating the best/worst agreement
		for (int i = 0; i < totalAgreementsNumber; ++i)
		{
			//the current agreement already been seen (sent or received)
			dAutomatedAgentAgreementValue = agentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);
			Agreement ag = new Agreement(CurrentAgreementIdx, dAutomatedAgentAgreementValue);
			//the current agreed agreement's value is better than the current agreement
			if(dAutomatedAgentAgreementValue < dCurrentAgentAgreementValue)
			{
				agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
				continue;
			}

			//lower than the status quo, nothing is better than this
			if(dAutomatedAgentAgreementValue < dStatusQuoValue)
			{
				agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
				continue;
			}

			//lower than the opt out value, I'd better opt out than taking this agreement
			if(dAutomatedAgentAgreementValue < dOptOutValue)
			{
				agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
				continue;
			}

			Agreements.add(ag);

			agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
		} // end for - going over all possible agreements  
		
		Collections.sort(Agreements);
		if(currentTurn != -1)
			valuesCalculated = true;
	}
}
