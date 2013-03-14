package negotiator.tournament.VariablesAndValues;

public class TotalSessionNumberValue extends TournamentValue {
	private int value = 1;
	public TotalSessionNumberValue() {
	
	}
	public TotalSessionNumberValue(int value) {
		this.value = value;
	}
	public String toString() {return String.valueOf(value);}
	public int getValue() { return value; }
}
