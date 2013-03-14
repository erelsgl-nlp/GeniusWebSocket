package negotiator.tournament.VariablesAndValues;

import negotiator.repository.MediatorRepItem;

public class MediatorValue extends TournamentValue
{
	MediatorRepItem value;	
	
	public MediatorValue(MediatorRepItem val) { value=val; }
	public String toString() { return (value == null ? "None" : value.getName()); }
	public MediatorRepItem getValue() { return value; }
}
