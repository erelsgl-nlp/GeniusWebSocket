package negotiator.protocol.auction;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import agents.BayesianAgentForAuction;

import negotiator.*;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;

public class MultiPhaseAuctionProtocol extends AuctionProtocol {

	public MultiPhaseAuctionProtocol(AgentRepItem[] agentRepItems,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			ArrayList<ProfileRepItem>[] agentsWorldProfiles)
	throws Exception {
		super(agentRepItems, profileRepItems, agentParams,null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cleanUP() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegotiationOutcome getNegotiationOutcome() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void run() {
		synchronized (tournamentRunner) {
			
		
		try { 
			int numberOfSellers = getNumberOfAgents()-1;
			//run the sessions
			AuctionBilateralAtomicNegoSession[] sessions = new AuctionBilateralAtomicNegoSession[numberOfSellers];
			//Agent center;
			int numberOfOffers = 0;
			java.lang.ClassLoader loaderA = ClassLoader.getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
			Agent agentA = (Agent)(loaderA.loadClass(getAgentRepItem(0).getClassPath()).newInstance());
			agentA.setName("Buyer");

			for (int i=0;i<numberOfSellers;i++) {
				java.lang.ClassLoader loaderB = ClassLoader.getSystemClassLoader();
				Agent agentB = (Agent)(loaderB.loadClass(getAgentRepItem(i+1).getClassPath()).newInstance());
				agentB.setName("Seller");
				
				
				HashMap<AgentParameterVariable,AgentParamValue> centerParams = getAgentParams(0);
				centerParams.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"opponent",-1.,1.)), new AgentParamValue(Double.valueOf(i)));
				
				sessions[i] = 
					runNegotiationSession(
							agentA,
							agentB,
							getAgentRepItem(0),
							getAgentRepItem(i+1),
							"Buyer", "Seller", 
							getProfileRepItems(0),
							getProfileRepItems(i+1),
							getAgentUtilitySpaces(0),
							getAgentUtilitySpaces(i+1),
							centerParams,
							getAgentParams(i+1));
			}
			//determine winner
			double lMaxUtil= Double.NEGATIVE_INFINITY;
			double lSecondPrice = Double.NEGATIVE_INFINITY;
			Bid lSecondBestBid = null;
			AuctionBilateralAtomicNegoSession winnerSession = null;
			//				NegotiationSession2 secondBestSession = null;
			int winnerSessionIndex=0, i=0;
			Bid lBestBid=null;
			for (AuctionBilateralAtomicNegoSession s: sessions) {
				if(s.getNegotiationOutcome().agentAutility>lMaxUtil) {
					lSecondPrice = lMaxUtil;
					lSecondBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
					lBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
					lMaxUtil = s.getNegotiationOutcome().agentAutility;
					//secondBestSession = winnerSession;
					winnerSession = s;
					winnerSessionIndex = i;
				} else if(s.getNegotiationOutcome().agentAutility>lSecondPrice) { 
					lSecondPrice = s.getNegotiationOutcome().agentAutility;
					lSecondBestBid = s.getNegotiationOutcome().AgentABids.get(s.getNegotiationOutcome().AgentABids.size()-1).bid;
				}
				i++;
				numberOfOffers += s.getNegotiationOutcome().AgentABids.size() +s.getNegotiationOutcome().AgentBBids.size();
			}
			boolean bContinue = true;
			int opponentIndex = winnerSessionIndex;
			//if(opponentIndex==0) opponentIndex =1; else opponentIndex = 0;
			int numberOfSession = 2;
			
			while(bContinue) {
				//calculate the strarting utils

				if(opponentIndex==0) opponentIndex =1; else opponentIndex = 0;
				AuctionBilateralAtomicNegoSession nextSession = sessions[opponentIndex];
				//BayesianAgentForAuction center = (BayesianAgentForAuction)(nextSession.getAgentA());
				//BayesianAgentForAuction seller = (BayesianAgentForAuction)(nextSession.getAgentB());
				double centerStartingUtil = getAgentUtilitySpaces(0).getUtility(lBestBid) ;
/*				try {
					BidIterator iter = new BidIterator(nextSession.getAgentAUtilitySpace().getDomain());
					double tmp = center.getOpponentUtility(lBestBid);
					while(iter.hasNext()) {
						Bid bid = iter.next();
						double lTmpExpecteUtility = nextSession.getAgentAUtilitySpace().getUtility(bid);
						if(lTmpExpecteUtility > centerStartingUtil)
							if(Math.abs(center.getOpponentUtility(bid)-tmp)<ALLOWED_UTILITY_DEVIATION) 
								centerStartingUtil = lTmpExpecteUtility;
					}
				}catch (Exception e) {
					e.printStackTrace();
				}*/
				double sellerStartingUtil = getAgentUtilitySpaces(1+opponentIndex).getUtility(lBestBid) ;;
/*				try {
					BidIterator iter = new BidIterator(nextSession.getAgentBUtilitySpace().getDomain());
					double tmp = seller.getOpponentUtility(lBestBid);
					while(iter.hasNext()) {				
						Bid bid = iter.next();
						double lTmpExpecteUtility = nextSession.getAgentBUtilitySpace().getUtility(bid);				
						if(lTmpExpecteUtility > sellerStartingUtil ) {
							if(Math.abs(seller.getOpponentUtility(bid)- tmp)<ALLOWED_UTILITY_DEVIATION) {						
								sellerStartingUtil = lTmpExpecteUtility ;
							}
						}									
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
*/
				HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue> ();
				HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue> ();
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(centerStartingUtil));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(0.9));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(lSecondPrice));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"opponent",-1.,1.)), new AgentParamValue(Double.valueOf(opponentIndex)));			
				//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(0.6));
				paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(-0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(0.9));
				paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"starting_utility",0.,1.)), new AgentParamValue(sellerStartingUtil));

				AuctionBilateralAtomicNegoSession secondPhaseSession = 
					runNegotiationSession(
							nextSession.getAgentA(),
							nextSession.getAgentB(),
							getAgentRepItem(0), 
							getAgentRepItem(1+opponentIndex), 
							"Buyer", "Seller", 
							getProfileRepItems(0),
							getProfileRepItems(1+opponentIndex),
							getAgentUtilitySpaces(0),
							getAgentUtilitySpaces(1+opponentIndex), 
							paramsA, paramsB); 
				//TODO: secondPhaseSession.setAdditional(theoreticalOutcome);
				//secondPhaseSession.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				//secondPhaseSession.getNegotiationOutcome().
				if(secondPhaseSession.getNegotiationOutcome().ErrorRemarks!=null) bContinue = false; 
				lBestBid = secondPhaseSession.getNegotiationOutcome().AgentABids.get(secondPhaseSession.getNegotiationOutcome().AgentABids.size()-1).bid;
				numberOfSession++;
				numberOfOffers += (secondPhaseSession.getNegotiationOutcome().AgentABids.size()+secondPhaseSession.getNegotiationOutcome().AgentBBids.size());
			}
			for (AuctionBilateralAtomicNegoSession s: sessions) 
				s.cleanUp();
			//secondPhaseSession.cleanUp();
			System.out.println("Results: number of sessions:"+String.valueOf(numberOfSession) + "; number of offers:"+String.valueOf(numberOfOffers));
		} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
			tournamentRunner.notify();
		}
	
	}
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception {
		return generateAllSessions(tournament);
	}

	protected static AuctionProtocol createSession(Tournament tournament,ProfileRepItem profileCenter, ProfileRepItem profileSeller1, ProfileRepItem profileSeller2) throws Exception {

		ArrayList<AgentVariable> agentVars=tournament.getAgentVars();
		if (agentVars.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		ArrayList<TournamentValue> agentAvalues=agentVars.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		ArrayList<TournamentValue> agentBvalues=agentVars.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");

		ProfileRepItem[] profiles= new ProfileRepItem[3];//getProfiles();
		profiles[0] = profileCenter;
		profiles[1] = profileSeller1;
		profiles[2] = profileSeller2;
		AgentRepItem[] agents= new AgentRepItem[3];//getProfiles();
		AgentRepItem agentA=((AgentValue)agentAvalues.get(0)).getValue();
		agents[0] = agentA;
		agents[1] = ((AgentValue)agentBvalues.get(0)).getValue();
		agents[2] = agentA;
		//prepare parameters
		HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
		HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(1.1));
		//paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsA.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"role",-1.,3.)), new AgentParamValue(-0.9));
		//paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"reservation",0.,1.)), new AgentParamValue(reservationValue));
		paramsB.put(new AgentParameterVariable(new AgentParam(BayesianAgentForAuction.class.getName(),"phase",0.,1.)), new AgentParamValue(-0.9));
		HashMap<AgentParameterVariable, AgentParamValue>[] params = new HashMap[3]; 
		params[0] = paramsA;
		params[1] = paramsB;
		params[2] = paramsB;
		MultiPhaseAuctionProtocol session = new  MultiPhaseAuctionProtocol(agents,  profiles,	params,null) ;
		return session;

	}
	private static ArrayList<Protocol> generateAllSessions(Tournament tournament) {
		ArrayList<Protocol> allSessions = null;
		try {
			allSessions = new ArrayList<Protocol>();
			//sessionIndex = 0;
			DomainRepItem domain = new DomainRepItem(new URL("file:etc/templates/SON/son_domain.xml"));
			//center profiles
			ProfileRepItem center1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_1.xml"),domain);
			ProfileRepItem center2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_2.xml"),domain);
			ProfileRepItem center3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_3.xml"),domain);
			ProfileRepItem center4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_4.xml"),domain);
			ProfileRepItem center5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_5.xml"),domain);
			ProfileRepItem center6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_6.xml"),domain);
			ProfileRepItem center7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_7.xml"),domain);
			ProfileRepItem center8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_8.xml"),domain);
			ProfileRepItem center9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_9.xml"),domain);
			ProfileRepItem center10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_10.xml"),domain);
			ProfileRepItem center11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_11.xml"),domain);
			ProfileRepItem center12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_center_12.xml"),domain);
			//seller profiles
			ProfileRepItem seller1 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_1.xml"),domain);
			ProfileRepItem seller2 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_2.xml"),domain);
			ProfileRepItem seller3 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_3.xml"),domain);
			ProfileRepItem seller4 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_4.xml"),domain);
			ProfileRepItem seller5 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_5.xml"),domain);
			ProfileRepItem seller6 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_6.xml"),domain);
			ProfileRepItem seller7 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_7.xml"),domain);
			ProfileRepItem seller8 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_8.xml"),domain);
			ProfileRepItem seller9 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_9.xml"),domain);
			ProfileRepItem seller10 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_10.xml"),domain);
			ProfileRepItem seller11 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_11.xml"),domain);
			ProfileRepItem seller12 = new ProfileRepItem(new URL("file:etc/templates/SON/son_seller_12.xml"),domain);

			allSessions.add(createSession(tournament,center5, seller4, seller1));
			allSessions.add(createSession(tournament,center12, seller2, seller10));
			allSessions.add(createSession(tournament,center1, seller2, seller10));
			allSessions.add(createSession(tournament,center10, seller7, seller9));
			allSessions.add(createSession(tournament,center7, seller11, seller9));
			allSessions.add(createSession(tournament,center8, seller11, seller10));
			allSessions.add(createSession(tournament,center10, seller3, seller6));
			allSessions.add(createSession(tournament,center7, seller4, seller7));
			allSessions.add(createSession(tournament,center10, seller11, seller8));
			allSessions.add(createSession(tournament,center11, seller5, seller11));
			allSessions.add(createSession(tournament,center6, seller7, seller3));
			allSessions.add(createSession(tournament,center6, seller10, seller5));
			//allSessions.add(createSession(tournament,center8, seller6, seller3));
			//allSessions.add(createSession(tournament,center2, seller5, seller1));
			//allSessions.add(createSession(tournament,center3, seller5, seller4));
			//allSessions.add(createSession(tournament,center10, seller5, seller2));
			//allSessions.add(createSession(tournament,center1, seller3, seller6));
			//allSessions.add(createSession(tournament,center3, seller5, seller4));
			//allSessions.add(createSession(tournament,center8, seller10, seller6));
			//allSessions.add(createSession(tournament,center4, seller12, seller3));
			//allSessions.add(createSession(tournament,center3, seller2, seller11));
			//allSessions.add(createSession(tournament,center6, seller2, seller5));
			//allSessions.add(createSession(tournament,center10, seller2, seller6));
			//allSessions.add(createSession(tournament,center12, seller8, seller12));
			//allSessions.add(createSession(tournament,center9, seller6, seller2));
			//allSessions.add(createSession(tournament,center7, seller7, seller11));
			//allSessions.add(createSession(tournament,center2, seller1, seller5));
//			allSessions.add(createSession(tournament,center10, seller8, seller10));
//			allSessions.add(createSession(tournament,center11, seller8, seller7));
//			allSessions.add(createSession(tournament,center8, seller7, seller10));
//			allSessions.add(createSession(tournament,center2, seller7, seller12));
//			allSessions.add(createSession(tournament,center10, seller12, seller7));
//			allSessions.add(createSession(tournament,center7, seller7, seller11));
//			allSessions.add(createSession(tournament,center2, seller1, seller5));
//			allSessions.add(createSession(tournament,center10, seller8, seller10));
			//allSessions.add(createSession(center11, seller8, seller7));
			//allSessions.add(createSession(center8, seller7, seller10));
			//allSessions.add(createSession(center2, seller7, seller12));
			//allSessions.add(createSession(center7, seller12, seller11));
			//allSessions.add(createSession(center12, seller8, seller10));
			//allSessions.add(createSession(center2, seller5, seller4));
			//allSessions.add(createSession(center3, seller10, seller12));
			//allSessions.add(createSession(center1, seller5, seller3));
			//allSessions.add(createSession(center11, seller7, seller10));
			//allSessions.add(createSession(center1, seller8, seller12));
			//allSessions.add(createSession(center10, seller4, seller6));
			//allSessions.add(createSession(center7, seller4, seller6));
			//allSessions.add(createSession(center2, seller3, seller6));
			//allSessions.add(createSession(center5, seller10, seller11));
			//allSessions.add(createSession(center12, seller7, seller10));
			//allSessions.add(createSession(center6, seller9, seller11));
			//allSessions.add(createSession(center4, seller10, seller7));
			//allSessions.add(createSession(center3, seller5, seller2));
			//allSessions.add(createSession(center7, seller2, seller4));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allSessions;
	}

}
