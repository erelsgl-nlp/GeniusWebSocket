package negotiator.tournament.VariablesAndValues;

/**
 * MediatorVariable is a variable for a mediator.
 *
 */
public class MediatorVariable extends TournamentVariable
{
	public void addValue(TournamentValue a) throws Exception
	{
		if (!(a instanceof MediatorValue))
			throw new IllegalArgumentException("Expected MediatorVariable but received "+a);
		values.add(a);
	}
	
	public String varToString() {
		String res = "Mediator";
		return res;
	}
		
}