package negotiator.protocol.auction;

import java.util.HashMap;

import negotiator.Agent;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersBilateralAtomicNegoSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

public class AuctionBilateralAtomicNegoSession extends AlternatingOffersBilateralAtomicNegoSession {

	public AuctionBilateralAtomicNegoSession(Protocol protocol, Agent agentA,
			Agent agentB, String agentAname, String agentBname,
			UtilitySpace spaceA, UtilitySpace spaceB,
			HashMap<AgentParameterVariable, AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable, AgentParamValue> agentBparams,
			String startingAgent, int totalTime) throws Exception {
		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB,
				agentAparams, agentBparams, startingAgent, totalTime);
		// TODO Auto-generated constructor stub
	}

}
