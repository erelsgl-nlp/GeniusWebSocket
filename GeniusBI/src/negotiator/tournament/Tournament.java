package negotiator.tournament;

import java.lang.reflect.Method;
import java.util.ArrayList;

import negotiator.Global;

import negotiator.tournament.VariablesAndValues.*;
import negotiator.protocol.Protocol;
import negotiator.repository.*;


/**
 * This class stores all tournament info.
 * Only ONE ProfileValue is allowed in the variables.
 * Only TWO AgentValues are allowed.
 * 
 * @author wouter
 *
 */
public class Tournament
{
	/** TournamentNumber is used to give a unique reference to this tournament to the user.
	 * So the first tournament the user creates is tournament 1, the second 2, etc.
	 * The number is used in the tabs, eg "tour1" as tab name.
	 */
	public final int TournamentNumber;
	int nrOfRunsPerSession;
	
	/** the time (ms) that GUI and non-GUI agents will get for a nego session 
	 * TODO Wouter: this is quick hack, make sure they get set properly.*/
	 
	
	ArrayList<TournamentVariable> variables=new ArrayList<TournamentVariable>();
		// ASSSUMPTIONS: variable 0 is the ProfileVariable.
		// variable 1 is AgentVariable for agent A.
		// variable 2 is AgentVariable for agent B.
		// variable 3 is number of runs per session
		// rest is AgentParameterVariables.
	public static final int VARIABLE_PROTOCOL = 0;
	public static final int VARIABLE_PROFILE = 1;
	public static final int VARIABLE_AGENT_A = 2;
	public static final int VARIABLE_AGENT_B = 3;
	public static final int VARIABLE_NUMBER_OF_RUNS = 4;
	public static final int VARIABLE_MEDIATOR = 5;
	
	ArrayList<Protocol> sessions=null;
	
	
	
	/** creates empty tournament with the next TournamenNumber */
		static int next_number=1;
	public Tournament()
	{
		TournamentNumber=next_number;
		next_number++;
	}
	
	public ArrayList<Protocol> getSessions() throws Exception {
		ProtocolRepItem protRepItem = getProtocol();
		Class<Protocol> protocol = Global.getProtocolClass(protRepItem);
    	Class[] paramTypes = {
    			Tournament.class
    	};
		Method mthdGetTournamentSessions = protocol.getMethod("getTournamentSessions", paramTypes);
		sessions = (ArrayList<Protocol>)(mthdGetTournamentSessions.invoke(null,this));
		return sessions;
		
	}
	/**
	 * @return the available AgentVariables in the tournament.
	 */
	public ArrayList<AgentVariable> getAgentVars() {
		ArrayList<AgentVariable> agents=new ArrayList<AgentVariable>();
		for (TournamentVariable v: variables) {
			if (v instanceof AgentVariable) agents.add((AgentVariable)v);
		}
		return agents;
	}

	/**
	 * @return the available MediatorVariables in the tournament.
	 */
	public MediatorVariable getMediatorVars() {
		for (TournamentVariable v: variables) {
			if (v instanceof MediatorVariable) {
				return (MediatorVariable)v;
			}
		}
		throw new RuntimeException("tournament does not contain a mediator variable");
	}

	public ProtocolRepItem getProtocol() throws Exception {
		for (TournamentVariable v: variables) {
			if (v instanceof ProtocolVariable) {
				ArrayList<ProtocolRepItem> protocols =new ArrayList<ProtocolRepItem>();
				 for (TournamentValue tv: ((ProtocolVariable)v).getValues()) {
					 protocols.add( ((ProtocolValue)tv).getValue());
				 }
				 return protocols.get(0);
			}
		}
		throw new RuntimeException("tournament does not contain a profile variable");
		
	}
	 /**
	  * Get the profiles that are available.
	  * The TournamentVarsUI will always place them in position 0 of the array but that is not mandatory.
	  */
	public ArrayList<ProfileRepItem> getProfiles() throws Exception { 
		for (TournamentVariable v: variables) {
			if (v instanceof ProfileVariable) { 
				ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>();
				 for (TournamentValue tv: ((ProfileVariable)v).getValues()) {
					 profiles.add( ((ProfileValue)tv).getProfile());
				 }
				 return profiles;
			}
		}
		throw new RuntimeException("tournament does not contain a profile variable");
	}

	/**
	 * @param agent the agent you want the parameters of
	 * @param name the name of this unique instantiation of the agent. Typically AgentA and AgentB.
	 * @return ArrayList of AssignedParameterVariable of given agent that are selected/set in this tournament.
	 */
	public ArrayList<AssignedParameterVariable> getParametersOfAgent(AgentRepItem agent,String name) {
		ArrayList<AssignedParameterVariable> allparameters= new ArrayList<AssignedParameterVariable>();
		for (TournamentVariable v: variables) {
			if (!(v instanceof AgentParameterVariable)) continue;
			AgentParameterVariable agentparam = (AgentParameterVariable)v;
			if (!(agentparam.getAgentParam().agentclass.equals(agent.getClass()))) continue;	
			allparameters.add(new AssignedParameterVariable(agentparam,name));
		}
		return allparameters;
	}
	
	public ArrayList<TournamentVariable> getVariables() { return variables; }
	
}



