package negotiator.tournament.VariablesAndValues;


/**
 * ProfileVariable is a variable for a tournament,
 * indicating that the profile is to be manipulated.
 * It just is an indicator for the TournamentVariable that its
 * value array contains a ProfileValue.
 * 
 * @author wouter
 *
 */
public class ProfileVariable extends TournamentVariable
{
	public void addValue(TournamentValue v) throws Exception
	{
		if (!(v instanceof ProfileValue))
			throw new IllegalArgumentException("Expected ProfileValue but received "+v);
		values.add(v);
	}
	
	public String varToString() {
		return "Preference profiles";
	}
}