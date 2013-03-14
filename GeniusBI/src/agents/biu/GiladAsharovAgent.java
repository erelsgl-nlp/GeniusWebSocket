package agents.biu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;



/**
 * @author Gilad Asharov
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class GiladAsharovAgent extends OldAgentAdapter {
    AgentTools m_agentTools = null;
    AutomatedAgentType m_agentType = null;
    MyOpponent m_opponent;
    MessagesGroup m_allMessages;
    MessagesGroup m_currentAllMessages;
    Message m_lastTurnCreatedMessage;
    Message m_lastFairMessage;
    int m_countOfUnsuccessfulTurns;
    int m_countOfRejected;
        
    public GiladAsharovAgent() {
    	super();
    }
    
    
    private double getTurnTreshold(int turnNumber) {
    	double minValue = m_currentAllMessages.	getWorstMessage().getLastTurnValue();
    	minValue = Math.max(minValue, m_agentTools.getSQValue(m_agentType));
    	double maxValue = m_currentAllMessages.getBestMessage().getValue();
    	double dis = maxValue - minValue;
    	double fact = dis / (m_agentTools.getTurnsNumber() * 1.8);
    	return maxValue - turnNumber * fact;
    }
    /**
     * Constructor
     * Save a pointer to the m_agentTools class
     * @param m_agentTools - pointer to the m_agentTools class
     */
    public GiladAsharovAgent(AgentTools m_agentTools) {
        this.m_agentTools = m_agentTools;
    }
    
	/**
	 * Called before the the negotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
		m_agentTools=agentTools;
		m_agentType = agentType;
		//m_history = new History();
		m_opponent = new MyOpponent(sOpponentType);
		m_allMessages = new MessagesGroup(false);
		m_currentAllMessages = m_allMessages;
		Message bestMessage = m_currentAllMessages.getBestMessage();
		bestMessage.sendAsOffer();
		//bestMessage.sendAllPartialMessages();
		m_lastTurnCreatedMessage = m_allMessages.m_creatorMessage;
		m_countOfRejected = 0;
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
    	//m_history.opponentSentMessage(new Message(CurrentAgreementIdx), nMessageType);
    	
    	//trying to understand the opponent utility function
    	m_opponent.updateProbabilitiesForMessage(CurrentAgreementIdx);
    	Message opponentMessage = new Message(CurrentAgreementIdx);
    	Message fairMessage = m_currentAllMessages.getBestMessageForBothSides();
    	Message bestMessage = m_currentAllMessages.getBestMessage();
    	if (opponentMessage.isCompleteMessage()) {
    		//check the value of the message.
    		//check the value of the mean message
    		//if the message is good - agree
    		double myValue = opponentMessage.getValue();
    		double treshold = getTurnTreshold(m_agentTools.getCurrentTurn());
    		treshold = Math.min(treshold, fairMessage.getLastTurnValue());
    		if (myValue >= treshold) {
    			m_agentTools.acceptMessage(sOriginalMessage);
    			if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER) {
    				//sending it as offer
    				m_agentTools.sendMessage(AutomatedAgentMessages.COUNTER_OFFER, CurrentAgreementIdx);
    			}
    		}
    		else {
    			//reject the message.
    			//find the common issues between the messages
    			m_agentTools.rejectMessage(sOriginalMessage);
    			Message toReturn = m_currentAllMessages.getIntersectionMessage(bestMessage, opponentMessage);
    			if (!toReturn.isEmptyMessage()) {
    				//m_history.sendMessage(toReturn);
    				m_agentTools.sendMessage(AutomatedAgentMessages.COUNTER_OFFER, toReturn.m_indexes);
    			}
    		}
    	} else {
    		Message intersection = m_allMessages.getIntersectionMessage(opponentMessage, bestMessage);
    		if (intersection.equals(opponentMessage)) {
    			m_agentTools.acceptMessage(sOriginalMessage);
    			if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER) {
    				//sending it as offer
    				m_agentTools.sendMessage(AutomatedAgentMessages.COUNTER_OFFER, CurrentAgreementIdx);
    			}
    		} else {
    			m_agentTools.rejectMessage(sOriginalMessage);
    			if (!intersection.isEmptyMessage()) {
    				intersection.sendAsOffer();
    			}
    		}
    	}
////    		MessagesGroup completeOpponentMessageGroup = new MessagesGroup(opponentMessage, true);
////    		if (decideReduceTheCurrentGroup(completeOpponentMessageGroup)) {
////    			m_currentAllMessages = completeOpponentMessageGroup;
////    			m_agentTools.acceptMessage(sOriginalMessage);
////    			
////    			if (nMessageType != AutomatedAgentMessages.OFFER && nMessageType != AutomatedAgentMessages.COUNTER_OFFER) {
////    				//sending it as offer
////    				m_agentTools.sendMessage(AutomatedAgentMessages.COUNTER_OFFER, CurrentAgreementIdx);
////    			}
////    		} else {
////    			m_agentTools.rejectMessage(sOriginalMessage);
////    		}
////    	}
//    	return;
    }
    
    boolean decideReduceTheCurrentGroup(MessagesGroup nominee) {
    	double value = nominee.m_creatorMessage.getValue();
    	double opponentValue = nominee.m_creatorMessage.getOpponentValue(m_opponent);
    	double prevValue = m_currentAllMessages.m_creatorMessage.getValue(); 
    	if (prevValue < 0) {
    		prevValue = AutomatedAgentType.VERY_HIGH_NUMBER;
    	}
    	if (prevValue <= value) {
    		return false;
    	}
    	return false;
//    	int numberOfBetters = m_allMessages.getBestCountUntilTreshold(value, false);
//    	int nomineeNumberOfBetters = m_allMessages.getBestCountUntilTresholdForOpponent(opponentValue, false);
//    	
//    	if (numberOfBetters <= nomineeNumberOfBetters) {
//    		return true;
//    	}
//    	
//    	return false;
    	
//    	if (value > opponentValue) {
//    		return true;
//    	}
//    	//we will agree if the (value - opponentValue) is small
//    	//double diff = Math.abs(value - opponentValue);
//    	double frac = Math.abs(value / opponentValue);
//    	if (frac > 0.75) {
//    		return true;
//    	}
//    	return false;    	
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
		Message m = new Message(CurrentAgreementIdx);
		Message union = m.union(m_currentAllMessages.m_creatorMessage);
		m_currentAllMessages = new MessagesGroup(union, true);
    }
	
	/**
	 * called whenever the opponent rejected one of your massages (promise, query, offer or counter offer)
	 * @param nMessageType - the type of massage the oppnent rejected, can be
     * AutomatedAgentMessages.PROMISE, QUERY, OFFER, COUNTER_OFFER
	 * @param CurrentAgreementIdx - the indices of the agreement the opponent agreed to
     * @param sOriginalMessage - the original message that was rejected
	 */
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		m_countOfRejected++;
		//m_history.rejectMessage(CurrentAgreementIdx);
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
//    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
//    	//if (false) {
//    	if (m_lastTurnCreatedMessage == m_currentAllMessages.m_creatorMessage) {
//    		m_countOfUnsuccessfulTurns++;
//    	} else {
//    		m_countOfUnsuccessfulTurns = 1;
//    	}
//    	if (m_countOfUnsuccessfulTurns > nCurrentTurn / 2) {
//    		//m_agentTools.sendThreat();
//    	}
////    	int temp = Math.max(nCurrentTurn, m_currentAllMessages.m_creatorMessage.getNumberOfValues() + m_currentAllMessages.m_creatorMessage.m_indexes.length / 2);
////    	int numberOfValued = Math.min(temp, m_currentAllMessages.m_creatorMessage.m_indexes.length);
//    	m_currentAllMessages.sortBestMessagesForBothSides(m_countOfUnsuccessfulTurns * 15, m_currentAllMessages.m_creatorMessage.m_indexes.length);
//    	int n = m_countOfUnsuccessfulTurns + 1;
//    	boolean useRandom = true;
//    	if (m_countOfUnsuccessfulTurns * 15 > m_currentAllMessages.m_messages.size()) {
//    		useRandom = false;
//    	}
//    	Random random = new Random();
//    	ArrayList<Message> messagesToSend = new ArrayList<Message>();
//    	for (int i=0, count = m_countOfUnsuccessfulTurns * 15; i<count; i++) {
//    		if (!useRandom || random.nextInt() % n == 0) {
//    			if (i < m_currentAllMessages.m_messages.size()) { 
//    				Message m = m_currentAllMessages.m_messages.get(i);
//    				messagesToSend.add(m);
//    			}
//    		}
//    	} 
//    	double optOutValue = m_agentTools.getOptOutValue(m_agentType);
//    	double sqValue = m_agentTools.getSQValue(m_agentType);
//    	
//    	ArrayComparator comparator = m_currentAllMessages.getBestMessageComp();
//    	for (int i=0, count = messagesToSend.size(); i<count; ++i) {
//    		int index = getIndexOfMax(messagesToSend, i, count, comparator);
//    		Message m = messagesToSend.get(index);
//    		Message mi = messagesToSend.get(i);
//    		messagesToSend.set(i, m);
//    		messagesToSend.set(index, mi);
//    		if (m.isCompleteMessage()) {
//    			if (m.getValue() > 0.75*m.getOpponentValue(m_opponent)) {
//    				if (m.getValue() > optOutValue && m.getValue() > sqValue) {
//    					m.sendAsOffer();
//    				}
//    			}
//    		} //ow - we have very small list
//    	}
//    	
//    	if (m_currentAllMessages.getBestMessage().getValue() < optOutValue) {
//    		m_agentTools.sendMessage(AutomatedAgentMessages.OPT_OUT, "It's my best option...");
//    	}
//    	m_lastTurnCreatedMessage = m_currentAllMessages.m_creatorMessage;
//    	//}
//    }
    	
    public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
    	//if (false) {
//    	if (m_lastTurnCreatedMessage == m_currentAllMessages.m_creatorMessage) {
//    		m_countOfUnsuccessfulTurns++;
//    	} else {
//    		m_countOfUnsuccessfulTurns = 1;
//    	}
//    	if (m_countOfUnsuccessfulTurns > nCurrentTurn / 2) {
//    		//m_agentTools.sendThreat();
//    	}
//    	int temp = Math.max(nCurrentTurn, m_currentAllMessages.m_creatorMessage.getNumberOfValues() + m_currentAllMessages.m_creatorMessage.m_indexes.length / 2);
//    	int numberOfValued = Math.min(temp, m_currentAllMessages.m_creatorMessage.m_indexes.length);
//    	m_currentAllMessages.sortBestMessagesForBothSides(m_countOfUnsuccessfulTurns * 15, m_currentAllMessages.m_creatorMessage.m_indexes.length);
    	
    	
    	double treshold = getTurnTreshold(m_agentTools.getCurrentTurn());
    	System.err.println("the treshodl is: "+ treshold);
    	int numberOfMessages = m_currentAllMessages.sortBestUntilTreshold(treshold);
    	System.err.println("total number of messages in this turn: " + numberOfMessages);
    	
    	int numberOfMessagesToSend = 10;
    	boolean useRandom = true;
    	double randomTreshold = 1;
    	if (numberOfMessagesToSend > numberOfMessages) {
    		useRandom = false;
    	} else {
    		randomTreshold = (double)numberOfMessagesToSend / numberOfMessages;
    	}

    	Random random = new Random();
    	ArrayList<Message> messagesToSend = new ArrayList<Message>();
    	
    	for (int i=0; i<numberOfMessages; i++) {
    		if (!useRandom || random.nextDouble() < randomTreshold) {
    			if (i < m_currentAllMessages.m_messages.size()) { 
    				Message m = m_currentAllMessages.m_messages.get(i);
    				messagesToSend.add(m);
    			}
    		}
    	}

    	
    	double optOutValue = m_agentTools.getOptOutValue(m_agentType);
    	double sqValue = m_agentTools.getSQValue(m_agentType);

    	for (int i=0, count = messagesToSend.size(); i<count; i++) {
    		Message m = messagesToSend.get(i);
    		if (m.isCompleteMessage()) {
    			if (m.getValue() > optOutValue && m.getValue() > sqValue) {
    				m.sendAsOffer();
    			}
    		}
    	}
    	
    	Message m = m_currentAllMessages.getBestMessageForBothSides();
    	m.sendAsOffer();
    	
    	if (m_currentAllMessages.getBestMessage().getValue() < optOutValue) {
    		m_agentTools.optOut();
    	}
    	m_lastTurnCreatedMessage = m_currentAllMessages.m_creatorMessage;

    	return;
    	
//    	int n = m_countOfUnsuccessfulTurns + 1;
//    	boolean useRandom = true;
//    	if (m_countOfUnsuccessfulTurns * 15 > m_currentAllMessages.m_messages.size()) {
//    		useRandom = false;
//    	}
//    	Random random = new Random();
//    	ArrayList<Message> messagesToSend = new ArrayList<Message>();
//    	for (int i=0, count = m_countOfUnsuccessfulTurns * 15; i<count; i++) {
//    		if (!useRandom || random.nextInt() % n == 0) {
//    			if (i < m_currentAllMessages.m_messages.size()) { 
//    				Message m = m_currentAllMessages.m_messages.get(i);
//    				messagesToSend.add(m);
//    			}
//    		}
//    	} 
//    	double optOutValue = m_agentTools.getOptOutValue(m_agentType);
//    	double sqValue = m_agentTools.getSQValue(m_agentType);
//    	
//    	ArrayComparator comparator = m_currentAllMessages.getBestMessageComp();
//    	for (int i=0, count = messagesToSend.size(); i<count; ++i) {
//    		int index = getIndexOfMax(messagesToSend, i, count, comparator);
//    		Message m = messagesToSend.get(index);
//    		Message mi = messagesToSend.get(i);
//    		messagesToSend.set(i, m);
//    		messagesToSend.set(index, mi);
//    		if (m.isCompleteMessage()) {
//    			if (m.getValue() > 0.75*m.getOpponentValue(m_opponent)) {
//    				if (m.getValue() > optOutValue && m.getValue() > sqValue) {
//    					m.sendAsOffer();
//    				}
//    			}
//    		} //ow - we have very small list
//    	}
//    	
//    	if (m_currentAllMessages.getBestMessage().getValue() < optOutValue) {
//    		m_agentTools.sendMessage(AutomatedAgentMessages.OPT_OUT, "It's my best option...");
//    	}
//    	m_lastTurnCreatedMessage = m_currentAllMessages.m_creatorMessage;
//    	//}
    }	    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
//    	System.err.println("calculateValues: ");
    }
    
  
    
/////////////////////////////////////////////////////////////////////////////////////////    
    
    interface GetMessageValue {
    	public double getValue(Message m);
    }
    
/////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////
    interface ArrayComparator {
    	double getValue(Object o);
    }
    
    int getIndexOfMax(ArrayList a, int indexFrom, int indexUntil, ArrayComparator compare) {
    	double bestValue = -1;
    	int bestIndex = -1;
    	for (int i=indexFrom; i<indexUntil; ++i) {
    		double value = compare.getValue(a.get(i));
    		if (value > bestValue) {
    			bestIndex = i;
    			bestValue = value;
    		}
    	}
    	return bestIndex;
    }
    
    int getIndexOfMax(ArrayList a, ArrayComparator compare) {
    	return getIndexOfMax(a, 0, a.size(), compare);
    }
    
/////////////////////////////////////////////////////////////////////////////////////////
    
    class MessagesGroup {
    	public ArrayList<Message> m_messages;
    	public Message m_creatorMessage;
    	
    	//get all the messages that are intersect with m
    	MessagesGroup(Message m, boolean includePartialMessages) {
    		m_messages = new ArrayList<Message>();
    		createAllFromMessage(m, includePartialMessages);
    	}
    	
    	MessagesGroup(boolean includePartialMessages) {
    		m_messages = new ArrayList<Message>();
    		createAllMessages(includePartialMessages);
    	}
    	
    	//warning!! very expensive!!!
    	//insertion sort
    	public void sort(int numberOfMax, ArrayComparator comparator) {
    		int count = Math.min(numberOfMax, m_messages.size());
    		for (int i=0; i<count; ++i) {
    			int messageIndex = getIndexOfMax(m_messages, i, m_messages.size(), comparator);
    			Message max = m_messages.get(messageIndex);
    			Message toReplace = m_messages.get(i);
    			m_messages.set(i, max);
    			m_messages.set(messageIndex, toReplace);
    		}
    	}
    	
    	public int getBestCountUntilTreshold(double treshold, boolean onlyCompleteMessages) {
    		int count = 0;
    		for (int i=0, size = m_messages.size(); i<size; ++i) {
    			Message message = m_messages.get(i);
    			if (!onlyCompleteMessages || message.isCompleteMessage()) {
    				if (message.getValue() >= treshold) {
    					count++;
    				}
    			}
    		}
    		return count;
    	}
    	
    	public int getBestCountUntilTresholdForOpponent(double treshold, boolean onlyCompleteMessages) {
    		int count = 0;
    		for (int i=0, size = m_messages.size(); i<size; ++i) {
    			Message message = m_messages.get(i);
    			if (!onlyCompleteMessages || message.isCompleteMessage()) {
    				if (message.getOpponentValue(m_opponent) >= treshold) {
    					count++;
    				}
    			}
    		}
    		return count;
    	}
    	
    	public int sortBestUntilTreshold(double tresholdValue) {
    		int numberOfMessages = getBestCountUntilTreshold(tresholdValue, true);
    		sortBestMessage(numberOfMessages);
    		return numberOfMessages;
    	}
    	
    	public void sortBestMessage(int numberOfMessages) {
    		sort(numberOfMessages, new GetBestMessageComp());
    	}
    	
    	public void sortBestMessagesForBothSides(int numberOfMessages, int numberOfValued) {
    		GetBestMessageForBothSidesComp comparator = new GetBestMessageForBothSidesComp();
    		comparator.m_numberOfValued = numberOfValued;
    		sort(numberOfMessages, comparator);
    	}
    	
    	private void createAllMessages(boolean includePartialMessages) {
    		int issuesCount = m_agentType.getIssuesNum();
    		int[] indexes = new int[issuesCount];
    		for (int i=0, count = indexes.length; i<count; ++i) {
    			indexes[i] = AutomatedAgentType.NO_VALUE;
    		}
    		Message m = new Message(indexes);
    		createAllFromMessage(m, includePartialMessages);
    	}
    	void createAllFromMessage(Message m, boolean includePartialMessages) {
    		m_creatorMessage = m;
        	int issuesCount = m_agentType.getIssuesNum();
        	createMessage(m.m_indexes, 0, issuesCount, includePartialMessages);
        }
    	
    	private void createMessage(int[] indexes, int currentIssueIndex, int totalIssues, boolean includePartialMessages) {
    		if (currentIssueIndex == totalIssues) {
    			int[] newIndexes = new int[indexes.length];
    			for (int i=0; i<indexes.length; ++i) {
    				newIndexes[i] = indexes[i];
    			}
        		Message m = new Message(newIndexes);
        		m_messages.add(m);
        		return;
        	}
        	if (indexes[currentIssueIndex] != AutomatedAgentType.NO_VALUE) {
        		createMessage(indexes, currentIssueIndex + 1, totalIssues, includePartialMessages);
        	}
        	else {
        		for (int i=0, count = m_agentTools.getMaxValuePerIssue(m_agentType, currentIssueIndex); i<count; ++i) {
        			indexes[currentIssueIndex] = i;
        			createMessage(indexes, currentIssueIndex + 1, totalIssues, includePartialMessages);
        		}
        		indexes[currentIssueIndex] = AutomatedAgentType.NO_VALUE;
        		if (includePartialMessages) {
        			createMessage(indexes, currentIssueIndex + 1, totalIssues, includePartialMessages);
        		}
        	}
        }
    	
    	 
        
        class GetBestMessageComp implements ArrayComparator {
    		public double getValue(Object o) {
    			Message m = (Message)(o);
    			if (!m.isCompleteMessage()) {
    				return 0;
    			}
    			return m.getValue();
    		}    	
        }
        
        public GetBestMessageComp getBestMessageComp() {
        	return new GetBestMessageComp();
        }
        
        class GetWorstMessageComp implements ArrayComparator {
        	public double getValue(Object o) {
        		Message m = (Message)(o);
        		if (!m.isCompleteMessage()) {
        			return 0;
        		}
        		double currentValue = m.getValue();
        		return AutomatedAgentType.VERY_HIGH_NUMBER - currentValue;
        	}
        }
        
        class GetBestOpponentMessageComp implements ArrayComparator {
        	public double getValue(Object o) {
        		Message m = (Message)(o);
        		if (!m.isCompleteMessage()) {
        			return 0;
        		}
        		double currentValue = m.getOpponentValue(m_opponent);
        		return currentValue;
        	}
        }
        
        class GetBestMessageForBothSidesComp implements ArrayComparator {
        	int m_numberOfValued;
        	GetBestMessageForBothSidesComp() {
        		m_numberOfValued = m_creatorMessage.m_indexes.length;
        	}
        	public double getValue(Object o) {
        		Message m = (Message)(o);
    			if (m.getNumberOfValues() != m_numberOfValued) {
    				return 0;
    			}
    			double myCurrentValue = m.getValue();
    			double opponentValue = m.getOpponentValue(m_opponent);
    			if (myCurrentValue < 0.75 * opponentValue) {
    				return 0;
    			}
    			myCurrentValue = Math.max(myCurrentValue, 0);
    			opponentValue = Math.max(opponentValue, 0);
    			return myCurrentValue * opponentValue;
    		}
        }
        
        class GetBestPartialMessageComp implements ArrayComparator {
        	private HashSet<Message> m_ignoreMessage;
        	int m_numberOfValues;
        	public GetBestPartialMessageComp(int numberOfValues) {
        		m_numberOfValues = numberOfValues;
        		m_ignoreMessage = new HashSet<Message>();
			}
        	public void ignoreMessage(Message m) {
        		m_ignoreMessage.add(m);
        	}
        	public void ignoreMessages(List<Message> m) {
        		m_ignoreMessage.addAll(m);
        	}
			public double getValue(Object o) {
				Message m = (Message)(o);
				if (m_ignoreMessage.contains(m)) {
					return 0;
				}
				if (m.getNumberOfValues() != m_numberOfValues) {
					return 0;
				}
				if (m.isCompleteMessage()) {
					return 0;
				}
				return m.getValue();
			}
        	
        }
             
        Message getBestOpponentMessage() {
        	GetBestOpponentMessageComp comp = new GetBestOpponentMessageComp();
        	int messageIndex = getIndexOfMax(m_messages, comp);
        	return m_messages.get(messageIndex);
        }
        Message getBestMessage() {
        	GetBestMessageComp comparator = new GetBestMessageComp();
        	int messageIndex = getIndexOfMax(m_messages, comparator);
        	return m_messages.get(messageIndex);
        }
                
        Message getBestMessageForBothSides() {
        	return getBestMessageForBothSides(m_creatorMessage.m_indexes.length);
       }
        
        Message getBestMessageForBothSides(int numberOfValued) {
        	GetBestMessageForBothSidesComp comparator = new GetBestMessageForBothSidesComp();
        	comparator.m_numberOfValued = numberOfValued;
        	int messageIndex = getIndexOfMax(m_messages, comparator);
        	Message m = m_messages.get(messageIndex);
        	return m;
        }
        
        Message getWorstMessage() {
        	GetWorstMessageComp comparator = new GetWorstMessageComp();
        	int messageIndex = getIndexOfMax(m_messages, comparator);
        	return m_messages.get(messageIndex);
        }
        
        double getMeanValue() {
        	int countOfCompleteMessages = 0;
        	double totalValue = 0;
        	for(int i=0, count = m_messages.size(); i<count; ++i) {
        		Message m = m_messages.get(i);
        		if (m.isCompleteMessage()) {
        			countOfCompleteMessages++;
        			totalValue = m.getValue();
        		}
        	}
        	return totalValue/countOfCompleteMessages;
        }
        
        
        
	//        Message getBestPartialMessage(History history) {
	//        	GetBestPartialMessageComp comp = new GetBestPartialMessageComp(m_creatorMessage.getNumberOfValues() + 1);
	//        	ArrayList toIgnore = new ArrayList<Message>();
	//        	//m_history.getAllSentMessages(toIgnore);
	//        	comp.ignoreMessages(toIgnore);
	//        	int messageIndex = getIndexOfMax(m_messages, comp);
	//        	Message m = m_messages.get(messageIndex);
	//        	return m;
	//        }
	//        
        public Message getIntersectionMessage(Message m1, Message m2) {
        	Message intersection = new Message();
        	for (int i=0; i<intersection.m_indexes.length; ++i) {
        		if (m1.m_indexes[i] == m2.m_indexes[i]) {
        			intersection.m_indexes[i] = m1.m_indexes[i];
        		}
        	}
        	return intersection;
        }
                
        public void sendMinusBetweenMessage(Message m) {
        	Random random = new Random();
        	for (int j=0, count = random.nextInt() % m.m_indexes.length; j<count; ++j) {
        		Message partialMessage = new Message(m_creatorMessage.m_indexes);
        		for (int i=0; i<partialMessage.m_indexes.length; ++i) {
        			if (m_creatorMessage.m_indexes[i] == AutomatedAgentType.NO_VALUE) {
        				if (m.m_indexes[i] != AutomatedAgentType.NO_VALUE) {
        					if (random.nextBoolean()) {
        						partialMessage.m_indexes[i] = m.m_indexes[i];
        					}
        				} 
        			}
        		}
        		Message toSend = new Message(partialMessage.m_indexes);
				toSend.sendAsOffer();
        	}
        }
        
        public void sendBetweenMessages(int strictness) {
        	for (int i=0, count = m_creatorMessage.m_indexes.length; i<count; ++i) {
        		if (m_creatorMessage.m_indexes[i] == AutomatedAgentType.NO_VALUE) {
        			Message m = new Message(m_creatorMessage.m_indexes);
        			m.m_indexes[i] = strictness % m_agentTools.getMaxValuePerIssue(m_agentType,i);
        			m.sendAsOffer();
        			m.m_indexes[i] = AutomatedAgentType.NO_VALUE;
        		}
        	}
        }
    }
    
/////////////////////////////////////////////////////////////////////////////////////////
    
    class MyOpponent {
    	public String m_opponentSide;
    	//public AutomatedAgentType m_opponentType;
        public AutomatedAgentType agentOpponentCompromise = null;
        public AutomatedAgentType agentOpponentLongTerm = null;
        public AutomatedAgentType agentOpponentShortTerm = null;          
    	public double m_pOfLongTerm;
    	public double m_pOfShortTerm;
    	public double m_pOfCompromise;
    	public int m_countForLongTerm;
    	public int m_countForShortTerm;
    	public int m_countOfCompromise;
    	 	
    	MyOpponent(String opponentSide) {
    		m_opponentSide = opponentSide;
    		m_countForLongTerm = 1;
        	m_countForShortTerm = 1;
        	m_countOfCompromise = 1;
        		       	
    		agentOpponentCompromise = m_agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.COMPROMISE_TYPE_IDX);
            agentOpponentLongTerm = m_agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.LONG_TERM_TYPE_IDX);
            agentOpponentShortTerm = m_agentTools.getCurrentTurnSideAgentType(opponentSide, AutomatedAgentsCore.SHORT_TERM_TYPE_IDX);
            
            updateProbabilities();
    	}
    	
    	void updateProbabilities() {
    		int totalCount = m_countForLongTerm + m_countForShortTerm + m_countOfCompromise;
    		m_pOfLongTerm = (double)(m_countForLongTerm) / totalCount;
    		m_pOfShortTerm = (double)(m_countForShortTerm) / totalCount;
    		m_pOfCompromise = (double)(m_countOfCompromise) / totalCount;
    	}
    	
    	public void updateProbabilitiesForMessage(int[] indexes) {
    		int currentTurn = m_agentTools.getCurrentTurn();
    		double longTerm = m_agentTools.getAgreementValue(agentOpponentLongTerm, indexes, currentTurn);
    		double shortTerm = m_agentTools.getAgreementValue(agentOpponentShortTerm, indexes, currentTurn);
    		double compromise = m_agentTools.getAgreementValue(agentOpponentCompromise, indexes, currentTurn);
    		
    		double max = Math.max(longTerm, Math.max(shortTerm, compromise));
    		if (max == longTerm) {
    			m_countForLongTerm++;
    		} else if (max == shortTerm) {
    			m_countForShortTerm++;
    		} else if (max == compromise) {
    			m_countOfCompromise++;
    		}

    		updateProbabilities();
    		System.out.println("opponent value:");
    		System.out.println("opponent longTerm:" + longTerm);
    		System.out.println("opponent shortTerm:" + shortTerm);
    		System.out.println("opponent compromise:" + compromise);
    	}
    }
  
///////////////////////////////////////////////////////////////////////////////////////
        
    class Message {
    	private int[] m_indexes;

    	
    	private void initIndexes() {
    		int numberOfIssues = m_agentType.getIssuesNum();
    		m_indexes = new int[numberOfIssues];
    		for (int i=0; i<numberOfIssues; ++i) {
    			m_indexes[i] = AutomatedAgentType.NO_VALUE;
    		}
    	}
    	
    	Message() {
    		initIndexes();
    	}
    	Message(int[] indexes) {
    		initIndexes();
    		for (int i=0; i<m_indexes.length; ++i) {
    			m_indexes[i] = indexes[i];
    		}
    	}
    	
    	public double getValue() {
    		return m_agentTools.getAgreementValue(m_agentType, m_indexes, m_agentTools.getCurrentTurn());
    	}
    	
    	public double getBestValue() {
    		return m_agentTools.getAgreementValue(m_agentType, m_indexes, 0);
    	}
    	
    	public double getOpponentBestValue(MyOpponent opponent) {
        	return getOpponentValue(opponent, 0);
    	}
    	
    	public double getOpponentValue(MyOpponent opponent) {
    		return getOpponentValue(opponent, m_agentTools.getCurrentTurn());        	
    	}
    	
    	public double getLastTurnValue() {
    		int turns = m_agentTools.getTurnsNumber();
    		return m_agentTools.getAgreementValue(m_agentType, m_indexes, turns);
    	}
    	
    	private double getOpponentValue(MyOpponent opponent, int turn) {
    		double totalValue = opponent.m_pOfCompromise * m_agentTools.getAgreementValue(opponent.agentOpponentCompromise, m_indexes, turn);
        	totalValue += opponent.m_pOfLongTerm * m_agentTools.getAgreementValue(opponent.agentOpponentLongTerm, m_indexes, turn);
        	totalValue += opponent.m_pOfShortTerm * m_agentTools.getAgreementValue(opponent.agentOpponentShortTerm, m_indexes, turn);
    		return totalValue;
    	}
    	
    	public int getNumberOfNoValue() {
    		int numberOfNoValue = 0;
    		int numberOfIssues = m_agentType.getIssuesNum();
    		for (int i=0; i<numberOfIssues; ++i) {
    			if (m_indexes[i] == AutomatedAgentType.NO_VALUE) {
    				numberOfNoValue++;
    			}
    		} 
    		return numberOfNoValue;
    	}
    	public int getNumberOfValues() {
    		int numberOfIssues = m_indexes.length;
    		return numberOfIssues - getNumberOfNoValue();
    	}
    	public boolean isEmptyMessage() {
    		return getNumberOfValues() == 0;
    	}
    	    	
    	public void sendAsOffer() {
    		//m_history.sendMessage(this);
    		m_agentTools.sendMessage(AutomatedAgentMessages.OFFER, m_indexes);
    	}
    	
    	public boolean isCompleteMessage() {
    		return getNumberOfValues() == m_indexes.length;     		
    	}
    	
    	public boolean equals(Object o) {
    		Message m = (Message)(o);
    		for (int i=0; i<m_indexes.length; ++i) {
    			if (m_indexes[i] != m.m_indexes[i]) {
    				return false;
    			}
    		}
    		return true;
    	} 
    	
    	public String toString() {
    		return m_agentType.getAgreementStr(m_indexes);
    	}
    	
    	public void sendAllPartialMessages() {
    		int[] indexes = new int[m_indexes.length];
    		for (int i=0; i<indexes.length; ++i) {
    			indexes[i] = AutomatedAgentType.NO_VALUE;
    		}
    		for (int i=0; i<indexes.length; ++i) {
    			indexes[i] = m_indexes[i];
    			Message m = new Message(indexes);
    			m_agentTools.sendMessage(AutomatedAgentMessages.OFFER, indexes);
    			//m_history.sendMessage(m);
    			indexes[i] = AutomatedAgentType.NO_VALUE;
    		}
    	}
    	
    	public Message union(Message m1) {
    		Message u = new Message(m_indexes);
    		for (int i=0; i<m_indexes.length; ++i) {
    			if (u.m_indexes[i] == AutomatedAgentType.NO_VALUE) {
    				u.m_indexes[i] = m1.m_indexes[i];
    			}
    		}
    		return u;
    	}
    }
    
///////////////////////////////////////////////////////////////////////////////////////
    
}
