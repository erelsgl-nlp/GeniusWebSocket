package negotiator.tournament.VariablesAndValues;

import negotiator.repository.AgentRepItem;

public class AgentValue extends TournamentValue
{
	AgentRepItem value;	
	
	public AgentValue(AgentRepItem val) { value=val; }
	public String toString() { return value.getName(); }
	public AgentRepItem getValue() { return value; }
}