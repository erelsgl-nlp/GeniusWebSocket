package agents.biu;

import java.util.*;

/**
 * @author Vladimir Sadov 309398212
 * This class should hold all your logic for your automated agent
 * Examples are provided inline and marked as examples
 *
 */ 
public class VladimirSadovAgent extends OldAgentAdapter{
		
	//
	// Here we store all the agreements ordered by their utility functions
	// My assumption is that utility order of agreements does not change in time
	//This is sortable class that allows storage in convinient data structure
		private class Agreement implements Comparable{
		public double Utility;
		public int[] Agreement;
		
		
		Agreement (double u, int[] a)
		{
			Utility = u;
			Agreement =a;
		}
		
		//reverse order (we want the agreements to be sorted from the max utility downwards
		public int compareTo(Object o){
			if (((Agreement)o).Utility <this.Utility )
				return -1;
			else if (((Agreement)o).Utility >this.Utility)
				return 1;
			else 
				return 0;
		}
		
		
		
		
	}
	ArrayList<Agreement> Agrs = new ArrayList<Agreement>();
	int CurrAgreement =0; //agreement numerator
	int MoveFactor=1;
	int[] HisBest25Percent;
	int[] MyBest;
	double AgreementFactor = 0.25;
	AutomatedAgentType myAgentType;

	
	
	
    
    public VladimirSadovAgent() {
    	super();
    }
    
    /**
     * Constructor
     * Save a pointer to the AgentTools class
     * @param agentTools - pointer to the AgentTools class
     */
    public VladimirSadovAgent(AgentTools agentTools) {
        this.agentTools = agentTools;
    }
    
	/**
	 * Called before the the negotiation starts.
	 * Add any logic you need here.
     * For example, calculate the very first offer you'll
     * offer the opponent 
     * @param agentType - the automated agent
	 */
	public void initialize(AutomatedAgentType agentType, String sOpponentType) {
        /* Negotiation is about to start
         * You can add logic if needed to update your agent
         * @@EXAMPLE@@
         * For example: calculate the first offer the 
         * automated agent offers the opponent and send it
         */
        
        /********************************
         * Start example code
         ********************************/
        // calculate Automated Agent first offer
int nIssuesNum = agentTools.getTotalIssues(agentType);
        
        HisBest25Percent= new int[nIssuesNum];
        MyBest = new int[nIssuesNum];
       
        
   
        for (int i = 0; i < nIssuesNum; ++i)
        {
            HisBest25Percent[i] =-1;
        }
        
        calculateOfferAgainstOpponent(agentType, sOpponentType, 1);
        /********************************
         * End example code
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

    	//following code is a bit ugly, sorry, no time
    	//calculate array of top 25% his best offers
    	int nIssuesNum = agentTools.getTotalIssues(myAgentType);
           
        for (int i = 0; i < nIssuesNum; ++i)
        {
            HisBest25Percent[i] = CurrentAgreementIdx[ i];
        }   
        double [] bestIssues = new double[nIssuesNum];
        for (int i = 0; i < nIssuesNum; ++i)
        {
        	int[] tempIdx = new int[nIssuesNum];
        	for (int j = 0; j < nIssuesNum; ++j)
        	{
        		if (i!=j)
        			tempIdx[j]=MyBest[j];
        		else
        			tempIdx[j]=CurrentAgreementIdx[j];
        	}
        	bestIssues[i] = agentTools.getAgreementValue(myAgentType, tempIdx, agentTools.getCurrentTurn());
        }
        
        for (int i = 0; i < nIssuesNum; ++i)
        {
        	int countBetterThen=0;
        	for (int j=0; j < nIssuesNum; ++j)
        	{
        		if (i!=j && bestIssues[j]>bestIssues[i])
        			countBetterThen++;
        	}
        	if (countBetterThen>(AgreementFactor)*nIssuesNum)
        		HisBest25Percent[i]=-1;
        	
        }
        
        
    	//accept if and only if offer is better then already casted
    	Agreement AgrRec = new Agreement(agentTools.getAgreementValue(CurrentAgreementIdx),CurrentAgreementIdx);
    	MoveFactor = (int)(((double)agentTools.getTotalAgreements(myAgentType)-CurrAgreement+1)/(2*(agentTools.getTurnsNumber()- agentTools.getCurrentTurn())+1))+1;
    	int AgrHorizon = Math.max(Agrs.size()-1, CurrAgreement+MoveFactor);
    	if (AgrRec.Utility >=Agrs.get(CurrAgreement).Utility )
    	{
    		System.out.println("Offer Accepted");
    		agentTools.acceptMessage(sOriginalMessage);
            
            //prevent sending future offer in this turn
            agentTools.setSendOfferFlag(false);
            
            return;
    	}
    	else
    	{
    		
    		System.out.println("Offer Rejected");
    		agentTools.rejectMessage(sOriginalMessage);
    		
            return;
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
        /* @@ Received a message: opponent rejected the offer/promise/query/counter offer.
         * You can add logic if needed to update your agent
         */
		
		//Send next order in order
		System.out.println ("I am Sending offer " + CurrAgreement + " " +" With value " +agentTools.getAgreementValue(myAgentType, Agrs.get(CurrAgreement).Agreement , agentTools.getCurrentTurn())  +"/" + Agrs.get(CurrAgreement).Utility +  " " + agentTools.getMessageByIndices(Agrs.get(CurrAgreement).Agreement));
		//String sOffer =	agentTools.getMessageByIndices(Agrs.get(CurrAgreement).Agreement);
		int [] idxOffer = Agrs.get(CurrAgreement).Agreement.clone();
		for (int i =0;i<agentTools.getTotalIssues(myAgentType);i++)
			if (HisBest25Percent[i]!=-1)
				idxOffer[i]=HisBest25Percent[i];
		String sOffer =	agentTools.getMessageByIndices(idxOffer);
		MoveFactor = (int)(((double)agentTools.getTotalAgreements(myAgentType)-CurrAgreement+1)/(3*(agentTools.getTurnsNumber()- agentTools.getCurrentTurn())+1))+1;
		CurrAgreement = CurrAgreement + MoveFactor;
        agentTools.sendOffer(sOffer);
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

    	myAgentType = agentType;     
    	
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

    	
    	
        //rebuild ArrayList each turn
        //this is a bit overhead, but I'm still not so much into this code, so it is safer for me
             
        
        Agrs.clear();
                  
        double dAgreementValue = 0;
        
        
        double previousBest =0;
        // going over all agreements and calculating the best/worst agreement
        for (int i = 0; i < totalAgreementsNumber; ++i)
        {
        	
        	
            //Note: the agreements are saved based on their indices
            //At the end of the loop the indices are incremented
            //dAgreementValue = ;
            
            //add agreement to my collection
            
            
        	Agrs.add(new Agreement(agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn),CurrentAgreementIdx.clone()));

            //  check for best agreement
            if (agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn) > previousBest)
            {
              previousBest = agentTools.getAgreementValue(agentType, CurrentAgreementIdx.clone(), nCurrentTurn) ;

              MyBest =  CurrentAgreementIdx.clone();
          }                       

        	
        	
            agentTools.getNextAgreement(nIssuesNum, CurrentAgreementIdx, MaxIssueValues);// get the next agreement indices
        } // end for - going over all possible agreements
        
        
        
        //order my collection of agreements starting from the best 
        Collections.sort(Agrs);
        
    	
    	
        
        

        //Send next agreement in order fused with opponents best 25% of last offer
		int [] idxOffer = Agrs.get(CurrAgreement).Agreement.clone();
		for (int i =0;i<agentTools.getTotalIssues(myAgentType);i++)
			if (HisBest25Percent[i]!=-1)
				idxOffer[i]=HisBest25Percent[i];
		String sOffer =	agentTools.getMessageByIndices(idxOffer);
		MoveFactor = (int)(((double)agentTools.getTotalAgreements(myAgentType)-CurrAgreement+1)/(3*(agentTools.getTurnsNumber()- agentTools.getCurrentTurn())+1))+1;
		CurrAgreement = CurrAgreement + MoveFactor;
        
		//if it is better to Opt Out - Opt out
		if (agentTools.getAgreementValue(myAgentType, idxOffer, nCurrentTurn)<agentTools.getOptOutValue(agentType))
        {
        	 agentTools.optOut();
        	 
        }
        else
        {
        	agentTools.sendOffer(sOffer);
        }
        /********************************
         * End example code
         ********************************/
    }
    
    /**
     * called to calculate the values of the different possible agreements for the agent
     * @param agentType - the automated agent's type
     * @param nCurrentTurn - the current turn
     */
    public void calculateValues(AutomatedAgentType agentType, int nCurrentTurn) {
        //Calculate agreements values for a given turn
    	myAgentType = agentType;
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

        
        
        
        //random reset CurrAgreement (with probability 0.05, to some random point between 0 and current value)
        Random r = new Random();
        if (r.nextFloat()>=0.95)
        {
        	CurrAgreement = Math.round(r.nextFloat()*CurrAgreement);
        }
        	
        

        
              
    }
}
