package negotiator.tournament.VariablesAndValues;

import negotiator.repository.AgentRepItem;
import negotiator.repository.ProtocolRepItem;

public class ProtocolValue extends TournamentValue {
	ProtocolRepItem value;	
	
	public ProtocolValue(ProtocolRepItem val) { value=val; }
	public String toString() { return value.getName(); }
	public ProtocolRepItem getValue() { return value; }
}
