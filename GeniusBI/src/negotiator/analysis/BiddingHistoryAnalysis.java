package negotiator.analysis;

import java.util.ArrayList;

import negotiator.Agent;
import negotiator.Bid;


public class BiddingHistoryAnalysis {
	public enum STEP_CLASS { UNKNOWN, FORTUNATE, UNFORTUNATE, CONCESSION, NICE, SILENT, SELFISH;}
	public static final double CONSTANT_K = 0.006;
	private static TableFrame fOutputFrame;
	
	
	public static void calculateOppPrefSens(Agent[] pAgents, ArrayList<BidPoint> pBiddingHistory, ArrayList<BidPoint> pPareto,  double[] pSensAgent) {
		double sensAgentA = 0, 		sensAgentB = 0;
		int    nrOfBidsAgentA = 0,  nrOfBidsAgentB = 0;
		for (BidPoint bid : pBiddingHistory) {
			double s = getDistanceToPareto(pAgents, bid, pPareto);
			if(bid.getAgent()==pAgents[0]) { 
				sensAgentA = sensAgentA + s;
				nrOfBidsAgentA++;
			} else { 
				sensAgentB = sensAgentB + s;
				nrOfBidsAgentB++;
			}			
		}
		pSensAgent[0] = sensAgentA / nrOfBidsAgentA;
		pSensAgent[1] =  sensAgentB / nrOfBidsAgentB;
		
	}
	private static double getDistanceToPareto(Agent[] pAgents, BidPoint bid , ArrayList<Bid> pPareto) {
		Agent opponent = null;
		if(bid.getAgent()==pAgents[0]) 
			opponent = pAgents[1];
		else 
			opponent = pAgents[0];		
		for (int i=1;i<pPareto.size();i++) {
			if((bid.getAgent().getUtility(pPareto.get(i-1))>=bid.getAgent().getUtility(bid.getBid()))&&
			   (bid.getAgent().getUtility(pPareto.get(i))<=bid.getAgent().getUtility(bid.getBid()))){
				double u = (bid.getAgent().getUtility(bid.getBid())-bid.getAgent().getUtility(pPareto.get(i-1)))/(bid.getAgent().getUtility(pPareto.get(i))-bid.getAgent().getUtility(pPareto.get(i-1)))*(opponent.getUtility(pPareto.get(i))-opponent.getUtility(pPareto.get(i-1)))+opponent.getUtility(pPareto.get(i-1));
				return u-opponent.getUtility(bid.getBid()); 
			}			  
		}
		return 0;
	}
	public static void sensitivityAnalysis(Agent[] pAgents, ArrayList<ExtendedBid> fBiddingHistory){
		//calculate number of steps in each class
		int[] lFortunateCount = {0, 0};
		int[] lUnfortunateCount = {0, 0};
		int[] lConcessionCount = {0, 0};
		int[] lNiceCount = {0, 0};
		int[] lSilentCount = {0, 0};
		int[] lSelfishCount = {0, 0};
		int[] lUnknownCount = {0, 0};

		int[] lOwnSensitivity = {0,0}, lOppSensitivity = {0, 0};
		double lMyDelta = 0, lMyDeltaPrev= 0;
		double lOpponentDelta = 0, lOpponentDeltaPrev = 0;

		int lOpponentIndex;
		
		for(int i=2;i<fBiddingHistory.size();i++) {
			
			Agent lOpponent = fBiddingHistory.get(i-1).getAgent();
			
			ExtendedBid lMyBidPrev = fBiddingHistory.get(i-2);
			ExtendedBid lOpponentBid = fBiddingHistory.get(i-1);			
			ExtendedBid lMyBid = fBiddingHistory.get(i);

			//my step			
			lMyDelta = lMyBid.getAgent().getUtility(lMyBid.getBid())-
					   lMyBid.getAgent().getUtility(lMyBidPrev.getBid());

			lOpponentDelta = lOpponent.getUtility(lMyBid.getBid())-
							 lOpponent.getUtility(lMyBidPrev.getBid());
			
			
			STEP_CLASS lMyStepType = getMyStepClass(lMyDelta, lOpponentDelta);

			int lAgentIndex;
			//TODO: implement equals method for the agent			
			if(lMyBid.getAgent()==pAgents[0]) 
				lAgentIndex = 0;
			else 
				lAgentIndex = 1;
 
			switch(lMyStepType) {
			case CONCESSION: lConcessionCount[lAgentIndex]++; break;
			case FORTUNATE: lFortunateCount[lAgentIndex]++; break;
			case UNFORTUNATE: lUnfortunateCount[lAgentIndex]++; break;
			case SELFISH: lSelfishCount[lAgentIndex]++; break;
			case NICE: lNiceCount[lAgentIndex]++; break;
			case SILENT: lSilentCount[lAgentIndex]++;break;
			case UNKNOWN: lUnknownCount[lAgentIndex]++;
			}//switch
			//can start calculating the matching only after third bid
			if(i<3) continue;
			ExtendedBid lOpponentBidPrev = fBiddingHistory.get(i-3);

			//opponent's step
			lMyDeltaPrev = lMyBid.getAgent().getUtility(lOpponentBid.getBid())-
						   lMyBid.getAgent().getUtility(lOpponentBidPrev.getBid());

			lOpponentDeltaPrev = lOpponent.getUtility(lOpponentBid.getBid())-
								 lOpponent.getUtility(lOpponentBidPrev.getBid());
						
			STEP_CLASS lOpponentStepType = getOpponentStepClass(lMyDeltaPrev, lOpponentDeltaPrev);
			
			//sensitivity of own preferences
			switch(lOpponentStepType) {
			case FORTUNATE:
			case CONCESSION:
			case NICE: 
				if((lMyStepType == STEP_CLASS.CONCESSION)|| 
				   (lMyStepType == STEP_CLASS.UNFORTUNATE)/*||
				   (lStep == STEP_CLASS.SILENT)*/)
					lOwnSensitivity[lAgentIndex]++;
				break;
			case SELFISH:
			case UNFORTUNATE:					
				if((lMyStepType == STEP_CLASS.FORTUNATE)||
				   (lMyStepType == STEP_CLASS.SELFISH)/*||
				   (lStep == STEP_CLASS.SILENT)*/)
					lOwnSensitivity[lAgentIndex]++;
				break;
/*			case SILENT:
				if((lMyStepType == STEP_CLASS.NICE)||
				   (lMyStepType == STEP_CLASS.SILENT))							
					lOwnSensitivity[lAgentIndex ]++;*/
			}
			//sensitivity for opponent preferences
			switch(lOpponentStepType) {		
			case FORTUNATE:
			case SELFISH:
				if((lMyStepType == STEP_CLASS.SELFISH)||
				   (lMyStepType == STEP_CLASS.UNFORTUNATE))
					lOppSensitivity[lAgentIndex ]++;
				break;
			case CONCESSION:
			case UNFORTUNATE:
					if((lMyStepType == STEP_CLASS.FORTUNATE)||
					   (lMyStepType == STEP_CLASS.CONCESSION)||
					   (lMyStepType == STEP_CLASS.NICE)) {
						lOppSensitivity[lAgentIndex]++;
					}
					break;
/*			case NICE:
			case SILENT:
				if(lMyStepType == STEP_CLASS.SILENT)
					lOppSensitivity[lAgentIndex]++;*/						
			}				
		}//for
		if(fOutputFrame==null) {
			String[] pTitles = new String[19];
			pTitles[0] = "No.";
			pTitles[1] = "Own pref. sens." + pAgents[0].getClass().toString();
			pTitles[2] = "Opp. pref. sens." + pAgents[0].getClass().toString();
			pTitles[3] = "Own pref. sens." + pAgents[1].getClass().toString();
			pTitles[4] = "Opp. pref. sens." + pAgents[1].getClass().toString();
			pTitles[5] = "Fortunate"+ pAgents[0].getClass().toString();
			pTitles[6] = "Selfish";
			pTitles[7] = "Concession";
			pTitles[8] = "Unfortunate";
			pTitles[9] = "Nice";
			pTitles[10] = "Silent";
			pTitles[11] = "Fortunate"+ pAgents[1].getClass().toString();
			pTitles[12] = "Selfish";
			pTitles[13] = "Concession";
			pTitles[14] = "Unfortunate";
			pTitles[15] = "Nice";			
			pTitles[16] = "Silent";
			pTitles[17] = "Util. " + pAgents[0].getClass().toString();
			pTitles[18] = "Util. " + pAgents[1].getClass().toString();
			fOutputFrame = new TableFrame("Negotiation Dynamic", pTitles);
			fOutputFrame.setVisible(true);
		}
		String[] lRow = new String[19];
		lRow[0] = "";
		lRow[1] = String.format("%1.2f", ((double)lOwnSensitivity[0])/(fBiddingHistory.size()-2)*2*100)+" %";
		lRow[2] = String.format("%1.2f", ((double)lOppSensitivity[0])/(fBiddingHistory.size()-2)*2*100)+" %";
		lRow[3] = String.format("%1.2f", ((double)lOwnSensitivity[1])/(fBiddingHistory.size()-2)*2*100)+" %";;
		lRow[4] = String.format("%1.2f", ((double)lOppSensitivity[1])/(fBiddingHistory.size()-2)*2*100)+" %";;		
		lRow[5] = String.valueOf(lFortunateCount[0]);
		lRow[6] = String.valueOf(lSelfishCount[0]);
		lRow[7] = String.valueOf(lConcessionCount[0]);
		lRow[8] = String.valueOf(lUnfortunateCount[0]);
		lRow[9] = String.valueOf(lNiceCount[0]);
		lRow[10] = String.valueOf(lSilentCount[0]);
		lRow[11] = String.valueOf(lFortunateCount[1]);
		lRow[12] = String.valueOf(lSelfishCount[1]);
		lRow[13] = String.valueOf(lConcessionCount[1]);
		lRow[14] = String.valueOf(lUnfortunateCount[1]);
		lRow[15] = String.valueOf(lNiceCount[1]);
		lRow[16] = String.valueOf(lSilentCount[1]);		
		lRow[17] = String.format("%1.2f", pAgents[0].getUtility(fBiddingHistory.get(fBiddingHistory.size()-1).getBid()));
		lRow[18] = String.format("%1.2f", pAgents[1].getUtility(fBiddingHistory.get(fBiddingHistory.size()-1).getBid()));		
		fOutputFrame.addRow(lRow);		
	}
	private static STEP_CLASS getMyStepClass(double lMyDelta, double lOpponentDelta){
		STEP_CLASS lStep = STEP_CLASS.UNKNOWN;
		//check for a Silent step
		if((Math.abs(lMyDelta)<CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
			lStep = STEP_CLASS.SILENT;
		else
			//check for a nice step
			if((Math.abs(lMyDelta)<CONSTANT_K)&&(lOpponentDelta>0))
				lStep = STEP_CLASS.NICE;
			else
				//check for a Fortunate step
				if((lMyDelta>0)&&(lOpponentDelta>0))
					lStep = STEP_CLASS.FORTUNATE;
				else
					//check for an unfortunate step
					if((lMyDelta<CONSTANT_K)&&(lOpponentDelta<0))
						lStep = STEP_CLASS.UNFORTUNATE;
					else
						//check for a selfish step
						if((lMyDelta>0)&&(lOpponentDelta<=0))
							lStep = STEP_CLASS.SELFISH;
						else
							//check for a concession
							if((lMyDelta<0)&&(lOpponentDelta>=0))
								lStep = STEP_CLASS.CONCESSION;
		return lStep;
	}
	private static STEP_CLASS getOpponentStepClass(double lMyDelta, double lOpponentDelta){
		STEP_CLASS lStep = STEP_CLASS.UNKNOWN;
		//check for a Silent step
		if((Math.abs(lMyDelta)<CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
			lStep = STEP_CLASS.SILENT;
		else
			//check for a nice step
			if((lMyDelta>CONSTANT_K)&&(Math.abs(lOpponentDelta)<CONSTANT_K))
				lStep = STEP_CLASS.NICE;
			else
				//check for a Fortunate step
				if((lMyDelta>0)&&(lOpponentDelta>0))
					lStep = STEP_CLASS.FORTUNATE;
				else
					//check for an unfortunate step
					if((lMyDelta<CONSTANT_K)&&(lOpponentDelta<0))
						lStep = STEP_CLASS.UNFORTUNATE;
					else
						//check for a selfish step
						if((lMyDelta>0)&&(lOpponentDelta<=0))
							lStep = STEP_CLASS.CONCESSION;
						else
							//check for a concession
							if((lMyDelta<0)&&(lOpponentDelta>=0))
								lStep = STEP_CLASS.SELFISH;
		return lStep;
	}
	
}
