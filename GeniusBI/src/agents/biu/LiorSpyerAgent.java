package agents.biu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Lior Spyer
 */ 
public class LiorSpyerAgent extends OldAgentAdapter{

	private class Agreement implements Comparable<Agreement> 
	{
		private int m_Agreement[];		
		private double m_Value;

		public Agreement(int agreement[], double value) 
		{
			m_Agreement = new int[agreement.length];
			for (int i=0; i<agreement.length; i++)
			{
				m_Agreement[i] = agreement[i];
			}
			m_Value = value;
		}

		public double GetValue()
		{
			return m_Value;
		}

		public int[] GetAgreement()	
		{
			return m_Agreement;
		}

		public int compareTo(Agreement o) {
			return (int) Math.signum(o.GetValue() - this.m_Value); 
		}
	}

	private class AgreementDS
	{
		ArrayList<Agreement> m_AgreementList;	

		public AgreementDS(Collection<Agreement> collection)
		{
			m_AgreementList = new ArrayList<Agreement>(collection);
			Collections.sort(m_AgreementList);
		}

		public Iterator<Agreement> GetTopPercentAgreements(double percent)
		{
			return m_AgreementList.listIterator((int) (m_AgreementList.size() * percent));			
		}		
		
		public Agreement GetFirstHighestWithHighestValue(Agreement a)
		{
			if (m_AgreementList.size() <= 0)
			{
				return null;
			}
			Agreement currentAgreement;
			for (int i=0; i<m_AgreementList.size(); i++)
			{
				currentAgreement = m_AgreementList.get(i); 
				if (currentAgreement.GetValue() > a.GetValue())
				{
					return currentAgreement;
				}				
			}
			return a;
		}
	}

	private double c_MAX_COMPROMISE = 0.6;

	enum ThreatType { ToughenStands, OptOut, ThinkContinue, Non} 
	enum CommentType { RightTrack, NoBeliefInIntentions, YouScrachMyBackAndIWillScratchYours, Non}    

	AgentTools m_AgentTools = null;
	AutomatedAgentType m_AgentType = null;
	String m_OpponentType;

	AgreementDS m_AgreementDS;
	double m_CompromisePrecent;
	Iterator<Agreement> m_CurrentTurnAgreements;

	ThreatType m_ThreatType = ThreatType.Non;
	CommentType m_CommentType = CommentType.Non;

	public LiorSpyerAgent() {
		super();
	}

	/**
	 * Constructor
	 * Save a pointer to the AgentTools class
	 * @param agentTools - pointer to the AgentTools class
	 */
	public LiorSpyerAgent(AgentTools agentTools) {
		this.m_AgentTools = agentTools;	
	}

	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
	 * For example, calculate the very first offer you'll
	 * offer the opponent 
	 * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		m_AgentTools=agentTools;
		/********************************
		 * Start of Lior Spyer Code
		 ********************************/
		// calculate Automated Agent first offer		
		m_AgentType = agentType;
		m_OpponentType = sOpponentType;

		calculateValues(agentType, 1);
		calculateOfferAgainstOpponent(agentType, sOpponentType, 1);        
		/********************************
		 * End of Lior Spyer Code
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
		/********************************
		 * Start Of Lior Spyer Code
		 ********************************/      	    	        	    	           
		double dOppOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
		double dAutomatedAgentNextOfferValueForAgent = AutomatedAgentType.VERY_SMALL_NUMBER;
		double optoutValue = m_AgentTools.getOptOutValue(m_AgentType);
		double sqValue = m_AgentTools.getSQValue(m_AgentType);

		dOppOfferValueForAgent = m_AgentTools.getAgreementValue(CurrentAgreementIdx); 

		double dAcceptedAgreementValue = m_AgentTools.getAcceptedAgreementsValue();      

		if (dAcceptedAgreementValue >= dOppOfferValueForAgent || dOppOfferValueForAgent <= optoutValue || dOppOfferValueForAgent <= sqValue)
		{
			m_AgentTools.rejectMessage(sOriginalMessage);            			
		}

		m_AgentTools.calculateNextTurnOffer();
		dAutomatedAgentNextOfferValueForAgent = m_AgentTools.getNextTurnOfferValue();				
						
		if (!m_CurrentTurnAgreements.hasNext() && sqValue < optoutValue && dAcceptedAgreementValue <= optoutValue)
		{
			m_AgentTools.optOut();
		}
		
		Agreement nextAgreement = m_CurrentTurnAgreements.next();
		
		if (dAutomatedAgentNextOfferValueForAgent > m_AgentTools.getAgreementValue(nextAgreement.GetAgreement())
			&&
			dAutomatedAgentNextOfferValueForAgent > dOppOfferValueForAgent)
		{
			m_AgentTools.rejectMessage(sOriginalMessage);   
			m_AgentTools.setSendOfferFlag(false);
		}		
		
		else if (m_AgentTools.getAgreementValue(CurrentAgreementIdx) >= optoutValue &&
				m_AgentTools.getAgreementValue(nextAgreement.GetAgreement()) <= m_AgentTools.getAgreementValue(CurrentAgreementIdx)
				&& m_AgentTools.getAgreementValue(nextAgreement.GetAgreement()) > dAutomatedAgentNextOfferValueForAgent)				
		{        							
			m_AgentTools.acceptMessage(sOriginalMessage);
			
			nextAgreement = m_AgreementDS.GetFirstHighestWithHighestValue(new Agreement(CurrentAgreementIdx,m_AgentTools.getAgreementValue(CurrentAgreementIdx)));
			if (nextAgreement != null)
			{
				m_AgentTools.sendOffer(m_AgentTools.getMessageByIndices(nextAgreement.GetAgreement()));
			}
		}
		else
		{			   	
			m_AgentTools.sendCounterOffers(nextAgreement.GetAgreement());    	
		}
		/********************************
		 * End of Lior Spyer Code
		 ********************************/
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

		if(sComment.equals("I feel that we are on the right track for reaching an agreement"))
		{
			m_CommentType = CommentType.RightTrack;
			return;
		}
		if(sComment.equals("I don't believe that you are just in your intentions"))
		{			
			m_CommentType = CommentType.NoBeliefInIntentions;
			return;
		}
		if(sComment.equals("If you will try to compromise I will also try to go towards you"))
		{
			m_CommentType = CommentType.YouScrachMyBackAndIWillScratchYours;
			return;
		}
		m_CommentType = CommentType.Non;			
	}

	/**
	 * called whenever we get a threat from the opponent
	 * You can add logic to update your agent
	 * @param sThreat - the received threat
	 */
	public void threatReceived(String sThreat) {

		if(sThreat.equals("If an agreement is not reached by the next round i will toughen my stands"))
		{
			m_ThreatType = ThreatType.ToughenStands;
			return;
		}
		if(sThreat.equals("If an agreement is not reached by the next round i will opt out"))
		{			
			m_ThreatType = ThreatType.OptOut;
			return;
		}
		if(sThreat.equals("If i do not receive an offer that i like i will have to think through about continuing the negotiation"))
		{
			m_ThreatType = ThreatType.ThinkContinue;
			return;
		}
		m_ThreatType = ThreatType.Non;
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
		 */		 

		switch (nMessageType) 
		{
		case AutomatedAgentMessages.PROMISE:
			m_AgentTools.sendOffer(sOriginalMessage);
			break;
		case AutomatedAgentMessages.QUERY:
			m_AgentTools.sendOffer(sOriginalMessage);
			break;
		case AutomatedAgentMessages.OFFER:
			m_AgentTools.sendComment("I feel that we are on the right track for reaching an agreement");
			Agreement nextAgreement = m_AgreementDS.GetFirstHighestWithHighestValue(new Agreement(CurrentAgreementIdx,m_AgentTools.getAgreementValue(CurrentAgreementIdx)));
			if (nextAgreement != null)
			{
				m_AgentTools.sendOffer(m_AgentTools.getMessageByIndices(nextAgreement.GetAgreement()));
			}
			break;
		default:
			break;
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
		/* @@ Received a message: opponent rejected the offer/promise/query/counter offer.
		 */				
				
		if (!m_CurrentTurnAgreements.hasNext())
		{
			m_AgentTools.optOut();
		}
		
		Agreement nextAgreement = m_CurrentTurnAgreements.next();
		
		double nextAgreementValue = m_AgentTools.getAgreementValue(nextAgreement.GetAgreement());				

		double optOutValue = m_AgentTools.getOptOutValue(m_AgentType);

		m_AgentTools.calculateNextTurnOffer();        

		if (nextAgreementValue < optOutValue ) // Yinon Oshrat: this was  the original version I think it does nothing so I removed it && m_AgentTools.getNextTurnAutomatedAgentValue() < optOutValue)
		{
			m_AgentTools.optOut();
		}
		// Yinon Oshrat: this was  the original version I think it does nothing so I removed it &&
		//if (m_AgentTools.getNextTurnAutomatedAgentValue() > nextAgreementValue)
		//{
		//	m_AgentTools.setSendOfferFlag(false);
		//	return;
		//}
		m_AgentTools.sendOffer(m_AgentTools.getMessageByIndices(nextAgreement.GetAgreement()));
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

		/********************************
		 * Start Lior Spyer Code
		 ********************************/    	
		
		m_CompromisePrecent = (c_MAX_COMPROMISE / (double)m_AgentTools.getTurnsNumber()) * (nCurrentTurn - 1); 
		
		m_CurrentTurnAgreements = m_AgreementDS.GetTopPercentAgreements(m_CompromisePrecent);

		if (!m_CurrentTurnAgreements.hasNext())
		{
			m_AgentTools.optOut();
		}
		Agreement a = m_CurrentTurnAgreements.next();
		m_AgentTools.sendOffer(m_AgentTools.getMessageByIndices(a.GetAgreement()));
		/********************************
		 * End Lior Spyer Code
		 ********************************/
	}

	/**
	 * called to calculate the values of the different possible agreements for the agent
	 * @param agentType - the automated agent's type
	 * @param nCurrentTurn - the current turn
	 */
	public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) 
	{
		// yinon : hack to avoid null pointer
		if (m_AgentTools==null)
			m_AgentTools=agentTools;
		SetBestAndWorstAgreement(agentType, nCurrentTurn);

		double agreementTimeEffect = m_AgentTools.getAgreementTimeEffect(agentType); 
		double statusQuoValue = m_AgentTools.getSQValue(agentType);
		double optOutValue = m_AgentTools.getOptOutValue(agentType);	

		int totalIssuesNum = m_AgentTools.getTotalIssues(agentType);
		int totalAgreementsNumber = m_AgentTools.getTotalAgreements(agentType);

		int iterationIDX[] = new int[totalIssuesNum];
		int MaxIssueValues[] = new int[totalIssuesNum];	

		// the different possible agents for the opponent side
		AutomatedAgentType automatedAgentTypes[] = new AutomatedAgentType[3];

		double averageOpponentOptoutValue  = 0;
		if (m_OpponentType != null)
		{
			automatedAgentTypes[0] = m_AgentTools.getCurrentTurnSideAgentType(m_OpponentType, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
			automatedAgentTypes[1] = m_AgentTools.getCurrentTurnSideAgentType(m_OpponentType, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
			automatedAgentTypes[2] = m_AgentTools.getCurrentTurnSideAgentType(m_OpponentType, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
			for (int i=0; i<automatedAgentTypes.length; i++)
			{
				averageOpponentOptoutValue += automatedAgentTypes[i].getOptOutValue();			
			}
			averageOpponentOptoutValue = averageOpponentOptoutValue / 3.0;
		}
		ArrayList<Agreement> agreementList = new ArrayList<Agreement>();

		for (int i=0; i<totalIssuesNum; i++)
		{
			iterationIDX[i] = 0;
			MaxIssueValues[i] = m_AgentTools.getMaxValuePerIssue(agentType, i);
		}

		for (int i = 0; i < totalAgreementsNumber; ++i)
		{        	
			Agreement a = new Agreement(iterationIDX,m_AgentTools.getAgreementValue(agentType, iterationIDX, nCurrentTurn));
			boolean toInsert = true;
			
			if (m_OpponentType != null)
			{
				for (int j=0; j<automatedAgentTypes.length; j++)
				{
					if (automatedAgentTypes[j].getAgreementValue(a.GetAgreement(), nCurrentTurn) < averageOpponentOptoutValue)
					{
						toInsert = false;
						break;
					}
				}
			}

			if (toInsert && a.GetValue() > optOutValue && a.GetValue() > statusQuoValue)
			{				
				agreementList.add(a);
			}
			m_AgentTools.getNextAgreement(totalIssuesNum, iterationIDX, MaxIssueValues);// get the next agreement indices
		} 

		m_AgreementDS = new AgreementDS(agreementList);
	}

	private void SetBestAndWorstAgreement(AutomatedAgentType agentType, int nCurrentTurn)    
	{
		int nIssuesNum = m_AgentTools.getTotalIssues(agentType);

		int CurrentAgreementIdx[] = new int[nIssuesNum];
		int MaxIssueValues[] = new int[nIssuesNum];

		int totalAgreementsNumber = m_AgentTools.getTotalAgreements(agentType);

		for (int i = 0; i < nIssuesNum; ++i)
		{
			CurrentAgreementIdx[i] = 0;
			MaxIssueValues[i] = m_AgentTools.getMaxValuePerIssue(agentType, i);
		}

		double dAgreementValue = 0;

		m_AgentTools.initializeBestAgreement(agentType);
		m_AgentTools.initializeWorstAgreement(agentType);        

		// going over all agreements and calculating the best/worst agreement
		for (int i = 0; i < totalAgreementsNumber; ++i)
		{
			//Note: the agreements are saved based on their indices
			//At the end of the loop the indices are incremeneted
			dAgreementValue = m_AgentTools.getAgreementValue(agentType, CurrentAgreementIdx, nCurrentTurn);

			// check for best agreement
			if (dAgreementValue > m_AgentTools.getBestAgreementValue(agentType))
			{
				m_AgentTools.setBestAgreementValue(agentType, dAgreementValue);

				// save agreement
				m_AgentTools.setBestAgreementIndices(agentType, CurrentAgreementIdx);                        
			}                       

			// check for worst agreement
			if (dAgreementValue < agentType.getWorstAgreementValue())
			{
				m_AgentTools.setWorstAgreementValue(agentType, dAgreementValue);

				// save agreement
				m_AgentTools.setWorstAgreementIndices(agentType, CurrentAgreementIdx);
			}                       

			m_AgentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
		} // end for - going over all possible agreements
	}    
}
