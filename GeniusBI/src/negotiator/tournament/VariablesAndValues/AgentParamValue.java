package negotiator.tournament.VariablesAndValues;

/**
 * This class contains a possible parameter value for a nego session 
 * A parameter value is a value that will appear as a start-up argument for the agent,
 * for instance the random-seed value, a tau value or debug options
 * @author wouter
 *
 */
public class AgentParamValue extends TournamentValue
{
	Double value;

	public AgentParamValue(){
		value = 0.0;
	}
	
	public AgentParamValue(Double v) {
		value=v;
	}
	
	public Double getValue() { return value; }
	
	public String toString() { return value.toString(); }
	
}