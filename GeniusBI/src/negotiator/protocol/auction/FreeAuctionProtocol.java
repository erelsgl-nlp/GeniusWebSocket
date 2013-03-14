package negotiator.protocol.auction;

import java.util.HashMap;
import java.util.LinkedList;

import agents.BayesianAgentForAuction;

import negotiator.AgentParam;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.NegotiationEventListener;
import negotiator.analysis.BidSpace;
import negotiator.repository.AgentRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class FreeAuctionProtocol extends AuctionProtocol {

	public FreeAuctionProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams)
			throws Exception {
		super(agentRepItems, profileRepItems, agentParams);
		// TODO Auto-generated constructor stub
	}
	
	private void runFreeNegotiationSessions(LinkedList<AlternatingOffersNegotiationSession> sessions ) throws Exception {
		LinkedList<AlternatingOffersNegotiationSession> freeSessions = new LinkedList<AlternatingOffersNegotiationSession>();
		
		for (AlternatingOffersNegotiationSession s: sessions) {
			HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
			HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
			paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",-1.,1.)), new AgentParamValue(-0.9));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
			paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",-1.,1.)), new AgentParamValue(-0.9));
			
			AlternatingOffersNegotiationSession freeSession = new AlternatingOffersNegotiationSession(s.agentArep,
					s.agentBrep,
					s.getProfileArep(),
					s.getProfileBrep(),
					s.getAgentAname(),
					s.getAgentBname(),
					paramsA,
					paramsB,
					100,
					1,
					true, 1200, 1200, 1);
			freeSessions.add(freeSession);
			BidSpace bidSpace = tournament.getBidSpace(freeSession.getAgentAUtilitySpace(), freeSession.getAgentBUtilitySpace());
			if(bidSpace!=null) {
				freeSession.setBidSpace(bidSpace);
			} else {
				bidSpace = new BidSpace(freeSession.getAgentAUtilitySpace(),freeSession.getAgentBUtilitySpace());
				tournament.addBidSpaceToCash(freeSession.getAgentAUtilitySpace(), freeSession.getAgentBUtilitySpace(), bidSpace);
				freeSession.setBidSpace(bidSpace);
			}
			//freeSession.setAdditional(theoreticalOutcome);
			for (NegotiationEventListener list: negotiationEventListeners) freeSession.addNegotiationEventListener(list);
			fireNegotiationSessionEvent(freeSession);
			freeSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.			

		}
		//determine winner
		double lMaxUtil= Double.NEGATIVE_INFINITY;
		double lSecondPrice = Double.NEGATIVE_INFINITY;
		Bid lSecondBestBid = null;
		AlternatingOffersNegotiationSession winnerSession = null;

		for (AlternatingOffersNegotiationSession s: freeSessions) {
			if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lMaxUtil) {
				lSecondPrice = lMaxUtil;
				lSecondBestBid = s.getSessionRunner().getNegotiationOutcome().AgentABids.get(s.getSessionRunner().getNegotiationOutcome().AgentABids.size()-1).bid;
				lMaxUtil = s.getSessionRunner().getNegotiationOutcome().agentAutility;
				//secondBestSession = winnerSession;
				winnerSession = s;
			} else if(s.getSessionRunner().getNegotiationOutcome().agentAutility>lSecondPrice) 
				lSecondPrice = s.getSessionRunner().getNegotiationOutcome().agentAutility;
				lSecondBestBid = s.getSessionRunner().getNegotiationOutcome().AgentABids.get(s.getSessionRunner().getNegotiationOutcome().AgentABids.size()-1).bid;
		}
		//calculate the strarting utils
		BayesianAgentForAuction center = (BayesianAgentForAuction)(winnerSession.getSessionRunner().agentA);
		BayesianAgentForAuction seller = (BayesianAgentForAuction)(winnerSession.getSessionRunner().agentB);
		double centerStartingUtil = Double.NEGATIVE_INFINITY;
		try {
			BidIterator iter = new BidIterator(winnerSession.getAgentAUtilitySpace().getDomain());
			double tmp = center.getOpponentUtility(lSecondBestBid);
			while(iter.hasNext()) {
				Bid bid = iter.next();
				double lTmpExpecteUtility = winnerSession.getAgentAUtilitySpace().getUtility(bid);
				if(lTmpExpecteUtility > centerStartingUtil)
					if(Math.abs(center.getOpponentUtility(bid)-tmp)<ALLOWED_UTILITY_DEVIATION) 
						centerStartingUtil = lTmpExpecteUtility ;
													
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		double sellerStartingUtil = Double.NEGATIVE_INFINITY;
		try {
			BidIterator iter = new BidIterator(winnerSession.getAgentBUtilitySpace().getDomain());
			double tmp = seller.getOpponentUtility(lSecondBestBid);
			while(iter.hasNext()) {				
				Bid bid = iter.next();
				double lTmpExpecteUtility = winnerSession.getAgentBUtilitySpace().getUtility(bid);				
				if(lTmpExpecteUtility > sellerStartingUtil ) {
					if(Math.abs(seller.getOpponentUtility(bid)- tmp)<ALLOWED_UTILITY_DEVIATION) {						
						sellerStartingUtil = lTmpExpecteUtility ;
					}
				}									
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Center starting utility = " + String.valueOf(centerStartingUtil));
		System.out.println("Seller starting utility = " + String.valueOf(sellerStartingUtil));
		//NegotiationSession2 winnerSession = freeSessions.get(0);
		HashMap<AgentParameterVariable,AgentParamValue>  paramsA=new HashMap<AgentParameterVariable,AgentParamValue> ();
		HashMap<AgentParameterVariable,AgentParamValue>  paramsB=new HashMap<AgentParameterVariable,AgentParamValue> ();
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(centerStartingUtil));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(2.1));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(sellerStartingUtil));

		AlternatingOffersNegotiationSession secondPhaseSession = new AlternatingOffersNegotiationSession(winnerSession.getAgentA(),
				winnerSession.getAgentB(),
				winnerSession.getProfileArep(),
				winnerSession.getProfileBrep(),
				winnerSession.getAgentAname(),
				winnerSession.getAgentBname(),
				paramsA,
				paramsB,
				100,
				1,
				true, 1200, 1200, 1
		);
		BidSpace bidSpace = tournament.getBidSpace(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace());
		if(bidSpace!=null) {
			secondPhaseSession.setBidSpace(bidSpace);
		} else {
			bidSpace = new BidSpace(secondPhaseSession.getAgentAUtilitySpace(),secondPhaseSession.getAgentBUtilitySpace());
			tournament.addBidSpaceToCash(secondPhaseSession.getAgentAUtilitySpace(), secondPhaseSession.getAgentBUtilitySpace(), bidSpace);
			secondPhaseSession.setBidSpace(bidSpace);
		}
		//secondPhaseSession.setAdditional(theoreticalOutcome);
		for (NegotiationEventListener list: negotiationEventListeners) secondPhaseSession.addNegotiationEventListener(list);
		fireNegotiationSessionEvent(secondPhaseSession);
		secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
		
	}


}
