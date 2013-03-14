package agents.biu;

/**
 * @author raz
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class BenAriAgent extends OldAgentAdapter{
	int m_CurrentAgreementIdx[];
	int m_LastAggrement[];
	private boolean firstTurn = true;
	private boolean firstResponse = true;
	private boolean fixed = false;
	private int rejects = 0;
	private int m_hours = 0;
	private int m_salary = 0;
	private boolean threat = true;
	private String opponet = null;
	private AutomatedAgentType m_at;
    int m_allargs[];
    int MaxIssueValues[];
    int totalAgreementsNumber;

public BenAriAgent() {
	super();
}    

    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public BenAriAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the nagotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
       m_at = agentType;
		opponet = sOpponentType;
       m_CurrentAgreementIdx = new int[6];
       m_LastAggrement = new int[6];
       // calculate Automated Agent first offer
       calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
       
    }
    
    public void calculateResponse(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
    	if(fixed) {
    		agentTools.rejectMessage(sOriginalMessage);
    		return;
    	}
    	
    	// MY SQ	
    	int totalIssuesNum = agentTools.getTotalIssues(m_at);
    	
    	if(totalIssuesNum < 6) {
    		double currVal = agentTools.getAgreementValue(CurrentAgreementIdx);
    		if(currVal <= 0) {
    			agentTools.rejectMessage(sOriginalMessage);
    			return;
    		}
    		
    		if(currVal >= agentTools.getBestAgreementValue(m_at) / 2 ||
    		   agentTools.getCurrentTurn() > 7  && currVal >= agentTools.getBestAgreementValue(m_at) / 3) {
    			agentTools.acceptMessage(sOriginalMessage);
    		}
    		return;
    	}
    	
    	boolean happy = true;
    	for(int p = 0; p < totalIssuesNum && happy; ++p) {
    		if(CurrentAgreementIdx[p] != 1)
    			happy = false;
        }
    	
    	if(happy) {
    		System.err.println(" I AM HAPPY");
    		agentTools.sendComment("I AM HAPPY");
    		
    		//  accept offer
            agentTools.acceptMessage(sOriginalMessage);
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
            return;
    	}
    	
    	happy = true;
    	if(opponet.compareTo("SIDE_A") == 0) {
    		System.err.println("I THINK IM JOB CANDIDAT");
    		
    		// long term employer?
    		if(CurrentAgreementIdx[5] == 0 || CurrentAgreementIdx[5] == 4) { 
    			m_CurrentAgreementIdx[5] = 0;
    		} else if(CurrentAgreementIdx[5] == 2 && 
    				  CurrentAgreementIdx[0] != 2) {
    			happy = false;
    			agentTools.sendComment("FOR 10 HOURS I WANT MONEY - ALOT");
    		}
    		m_CurrentAgreementIdx[5] = 1;
    		
    		if(CurrentAgreementIdx[4] <= 1) {
    			m_CurrentAgreementIdx[4] = CurrentAgreementIdx[4];
    		} else {
    			m_CurrentAgreementIdx[4] = 1;
    		}
    		
    		// future is important
    		if(CurrentAgreementIdx[3] == 0) {
    			happy = false;
    			agentTools.sendComment("PENSION IS IMPORTANT TO ME");
    			m_CurrentAgreementIdx[3] = 2;
    		} else if(CurrentAgreementIdx[3] < 3) {
    			m_CurrentAgreementIdx[3] = CurrentAgreementIdx[3];
    		} else {
    			m_CurrentAgreementIdx[3] = 1;
    		}
    		
    		if(CurrentAgreementIdx[1] == 0 && CurrentAgreementIdx[0] != 2) {
    			happy = false;
    			agentTools.sendComment("QA IS NOT AN OPTION");
    			m_CurrentAgreementIdx[1] = 1;
    		} else if(CurrentAgreementIdx[1] != 4) {
    			m_CurrentAgreementIdx[1] = CurrentAgreementIdx[1];
    		} else {
    			m_CurrentAgreementIdx[1] = 2;
    		}
        		
    		// salary...
    		if(CurrentAgreementIdx[0] == 0 && (CurrentAgreementIdx[5] != 0 || CurrentAgreementIdx[2] != 1 || CurrentAgreementIdx[3] != 2)) {
    			happy = false;
    			agentTools.sendComment("I WONT WORK FOR LESS THEN 12000...");
    			m_CurrentAgreementIdx[0] = 1;
    		} else if(CurrentAgreementIdx[0] != 4) {
    			m_CurrentAgreementIdx[0] = CurrentAgreementIdx[0];
    		} else {
    			m_CurrentAgreementIdx[0] = 1;
    		}
    		
    		// last thing is car
    		if(CurrentAgreementIdx[2] == 0 && !happy) {
    			m_CurrentAgreementIdx[2] = 1;
    		} else if(CurrentAgreementIdx[2] == 2) {
    			m_CurrentAgreementIdx[2] = 1;
    		} else { // if im happy till here, car wont be problem
    			m_CurrentAgreementIdx[2] = CurrentAgreementIdx[2];
    		}
    	} else if(opponet.compareTo("SIDE_B") == 0) {
    		System.err.println("I THINK IM EMPLOYER");
    		
    		// short term job or lazy?
    		if(CurrentAgreementIdx[5] == 0) {
    			if(!firstResponse && m_LastAggrement[5] == 0) {
    				++m_hours;
    			}
    			
    			if(m_hours < 3) {
    				happy = false;
    				m_CurrentAgreementIdx[5] = 1;
    			} else {
    				agentTools.sendComment("I WILL MAKE YOU A FAVOUR IN WORK HOURS, BUT YOU WILL NEED TO ACCEPT MY NEXT OFFER!");
    				happy = false;
    				m_hours = 0;
    				m_CurrentAgreementIdx[5] = 0;
    				m_CurrentAgreementIdx[4] = 1;
    			    m_CurrentAgreementIdx[3] = 2;
    			    m_CurrentAgreementIdx[2] = 0;
    			    m_CurrentAgreementIdx[1] = 1;
    			    m_CurrentAgreementIdx[0] = 1;
    			    agentTools.sendOffer(agentTools.getMessageByIndices(m_CurrentAgreementIdx));
    			    for(int p = 0; p < 6; ++p) {
    		    		m_LastAggrement[p] = CurrentAgreementIdx[p];
    		    	}
    			    return;                                        
    			}
    		} else if(CurrentAgreementIdx[5] == 4) { 
    			m_CurrentAgreementIdx[5] = 1;
    		} else {
    			m_CurrentAgreementIdx[5] = CurrentAgreementIdx[5];
    		}
    		
    		// i dont care about fast promotion - he can have it
    		if(CurrentAgreementIdx[4] <= 1) {
    			m_CurrentAgreementIdx[4] = CurrentAgreementIdx[4];
    		} else {
    			m_CurrentAgreementIdx[4] = 1;
    		}
    		
    		// future is important
    		if(CurrentAgreementIdx[3] == 0) {
    			if(!firstResponse && CurrentAgreementIdx[3] != 0) {
    				agentTools.sendComment("PENSION IS IMPORTANT");
    				happy = false;
    			}
    			m_CurrentAgreementIdx[3] = 1;
    		} else if(CurrentAgreementIdx[3] < 3) {
    			m_CurrentAgreementIdx[3] = CurrentAgreementIdx[3];
    		} else {
    			m_CurrentAgreementIdx[3] = 1;
    		}
    		
    		if(CurrentAgreementIdx[1] == 3) {
    			agentTools.sendComment("YOU CANT BE PROJECT LEADER SO FAST!");
    			happy = false;
    			m_CurrentAgreementIdx[1] = 2;
    		} else if(CurrentAgreementIdx[1] != 4) {
    			m_CurrentAgreementIdx[1] = CurrentAgreementIdx[1];
    		} else {
    			m_CurrentAgreementIdx[1] = 1;
    		}
        		
    		// salary...
    		if(CurrentAgreementIdx[0] == 2 && CurrentAgreementIdx[5] != 2) {
    			if(!firstResponse && m_LastAggrement[0] == 2) {
    				++m_salary;
    			}

    			if(m_salary < 3) {
    				happy = false;
    				m_CurrentAgreementIdx[0] = 1;
    			} else {
    				agentTools.sendComment("I WILL MAKE YOU A FAVOUR IN SALARY, BUT YOU WILL NEED TO ACCEPT MY NEXT OFFER!");
    				happy = false;
    				m_salary = 0;
    				m_CurrentAgreementIdx[5] = 2;
    				m_CurrentAgreementIdx[4] = 1;
    				m_CurrentAgreementIdx[3] = 1;
    				m_CurrentAgreementIdx[2] = 0;
    				m_CurrentAgreementIdx[1] = 2;
    				m_CurrentAgreementIdx[0] = 2;
    				agentTools.sendOffer(agentTools.getMessageByIndices(m_CurrentAgreementIdx));
    				for(int p = 0; p < 6; ++p) {
    					m_LastAggrement[p] = CurrentAgreementIdx[p];
    				}
    				return;                                        
    			}
    		} else if(CurrentAgreementIdx[0] != 3) {
    			m_CurrentAgreementIdx[0] = CurrentAgreementIdx[0];
    		} else {
    			m_CurrentAgreementIdx[0] = 1;
    		}
    		
    		// last thing is car
    		if(CurrentAgreementIdx[2] == 1 && !happy) {
    			m_CurrentAgreementIdx[2] = 0;
    		} else if(CurrentAgreementIdx[2] == 2) {
    			m_CurrentAgreementIdx[2] = 1;
    		} else { // if im happy till here, car wont be problem
    			m_CurrentAgreementIdx[2] = CurrentAgreementIdx[2];
    		}
    	} else {
    		System.err.println("I DO NOT KNOW WHO I AM");
    	}
            
    	if(!happy) {
    		System.err.println("rejecting OFFER...");
    		agentTools.rejectMessage(sOriginalMessage);
    	} else {
    		System.err.println("ACCEPTING OFFER...");
            // accept offer
            agentTools.acceptMessage(sOriginalMessage);
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
    	}
    	
    	for(int p = 0; p < 6; ++p) {
    		m_LastAggrement[p] = CurrentAgreementIdx[p];
    	}
    	firstResponse = false;
    }
        
	public void commentReceived(String sComment) {
		System.err.println("comment: " + sComment);
        /* @@ Received a comment from the opponent
         * You can add logic if needed to update your agent 
         */
    }

	
	public void threatReceived(String sThreat) {
		if(threat) { 
			agentTools.sendThreat("IF YOU SEND ME ONE MORE THREAT I WILL MAKE YOUR LIFE MISERABLE!");
			threat = false;
		}  else {
			fixed = true;
			if(opponet.compareTo("SIDE_A") == 0) {
	    		m_CurrentAgreementIdx[5] = 0; 
	    		m_CurrentAgreementIdx[4] = 0;
	    		m_CurrentAgreementIdx[3] = 2;
	    		m_CurrentAgreementIdx[2] = 1;
	    		m_CurrentAgreementIdx[1] = 2;
	    		m_CurrentAgreementIdx[0] = 2;
			} else {
				m_CurrentAgreementIdx[5] = 2; 
				m_CurrentAgreementIdx[4] = 1;
				m_CurrentAgreementIdx[3] = 0;
				m_CurrentAgreementIdx[2] = 0;
				m_CurrentAgreementIdx[1] = 0;
				m_CurrentAgreementIdx[0] = 0;
			}
		}
    }
	
	public void opponentAgreed(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		System.err.println("AGREED on " + sOriginalMessage);
		agentTools.sendComment("THX");
    }
	
	
	public void opponentRejected(int nMessageType, int CurrentAgreementIdx[], String sOriginalMessage) {
		System.err.println("REJECTED "  + sOriginalMessage);
		/* @@ Received a message: opponent rejected the offer/promise/query/counter offer.
		 * You can add logic if needed to update your agent
		 */
	}

	public void calculateOfferAgainstOpponent(AutomatedAgentType agentType, String sOpponentType, int nCurrentTurn) {
		int totalIssuesNum = agentTools.getTotalIssues(agentType);

		if(firstTurn) {

			if(totalIssuesNum < 6) {
				int nIssuesNum = agentTools.getTotalIssues(agentType);

				int CurrentAgreementIdx[] = new int[nIssuesNum];
				MaxIssueValues = new int[nIssuesNum];

				totalAgreementsNumber = agentTools.getTotalAgreements(agentType);
				m_allargs = new int[totalAgreementsNumber];
				
				for (int i = 0; i < nIssuesNum; ++i)
				{
					CurrentAgreementIdx[i] = 0;
					MaxIssueValues[i] = agentTools.getMaxValuePerIssue(agentType, i);
				}
				
				double dAgreementValue;
				int i;
				for (i = 0; i < totalAgreementsNumber; ++i)
				{
					dAgreementValue = agentTools.getAgreementValue(CurrentAgreementIdx);

					// check for best agreement
					if (dAgreementValue < 0) {
						m_allargs[i] = -1;
					} else if(dAgreementValue < (agentTools.getBestAgreementValue(agentType) / 2)) {
						m_allargs[i] = 0;
					} else {
						m_allargs[i] = 1;
					}

					agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
				}
				
				agentTools.sendOffer(agentTools.getBestAgreementIndices(agentType));
				firstTurn = false;
				return;
			}

			System.err.println("IN FIRST ROUND SENDING ALL 1");
			for(int p = 0; p < totalIssuesNum; ++p) {
				m_CurrentAgreementIdx[p] = 1;
			}
		}

		///////////////////////////////////////////////////////
		if(totalIssuesNum < 6) {
			
			int luck = (int)(Math.random() * totalAgreementsNumber) - 1;
			if(luck < 0) luck =0 ;
			if(luck > totalAgreementsNumber - 1) luck = totalAgreementsNumber;
			
			int counter = 0;
			while(counter < totalAgreementsNumber + 80 && (m_allargs[luck] != 1 || (agentTools.getCurrentTurn() > 7 && m_allargs[luck] == -1))) {
				counter++;
				luck = (int)(Math.random() * totalAgreementsNumber);
				if(counter >= 80) {
					luck = counter - 80;
				}
			}
			
			int nIssuesNum = agentTools.getTotalIssues(agentType);

			int CurrentAgreementIdx[] = new int[nIssuesNum];
			for (int i = 0; i < nIssuesNum; ++i)
			{
				CurrentAgreementIdx[i] = 0;
			}
			
			int i ;
			for (i = 0; i < luck; ++i)
			{
				agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
			}
			
			m_allargs[i] = -1;
			agentTools.sendOffer(agentTools.getMessageByIndices(CurrentAgreementIdx));
			return;
        }
        ///////////////////////////////////////////////////////
        
        System.err.println(nCurrentTurn + "> SENDING OFFER " + agentTools.getMessageByIndices(m_CurrentAgreementIdx));
        agentTools.sendOffer(agentTools.getMessageByIndices(m_CurrentAgreementIdx));
    	firstTurn = false;
    	return;
    }
    
    private String getValFromInt(AutomatedAgentType agentType, int index, int val) {
    	int totalIssuesNum = agentTools.getTotalIssues(agentType);
    	int CurrentAgreementIdx[] = new int[totalIssuesNum];
    	
    	for(int p = 0; p < totalIssuesNum; ++p) {
    		if(p != index)
    			CurrentAgreementIdx[p] = 1;
    		else
    			CurrentAgreementIdx[p] = val;
        }
        
    	String msg = agentTools.getMessageByIndices(CurrentAgreementIdx);
    	
    	//System.err.println("nsg to split: " + msg); 
    	String[] sub = msg.split("[*]");
    	return sub[index * 2];
    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
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
                 
    }
}
