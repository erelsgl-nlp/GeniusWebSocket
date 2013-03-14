/*
 * Threat.java
 *
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.AgentID;

/**
 * An Action used to send a textual threats to other parties in negotiation.
 * Opposed to Comment this class used to send negative impressions and threats
 * The threat is chosen from a predefined set of possible threats 
 * @author Yinon Oshrat
 */

@XmlRootElement
public class Threat extends Action {
	@XmlElement
	protected int threatIndex;

	/**
	 * Contains the set of all possible threats
	 */
	public static final String[] possibleThreats;
	static {
		possibleThreats = new String[] {
				new String(
						"If an agreement is not reached by the next round I will toughen my stands"),
				new String(
						"If an agreement is not reached by the next round I will opt out"),
				new String(
						"If I do not receive an offer that I like I will have to think through about continuing the negotiation") };
	}

	 /**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Threat() {}
	
	/**
	 * Creates a new instance of Threat
	 * 
	 * @param agent
	 *            - the agent id of the agent creating this threat
	 * @param threatIndex
	 *            - the index of the wanted threat from the possible threats
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public Threat(AgentID agent, int threatIndex) {
		super(agent);
		if (threatIndex < 0 || threatIndex >= possibleThreats.length)
			throw new IllegalArgumentException(
					"Can't create threat with index " + threatIndex);
		this.threatIndex = threatIndex;
	}
	/**
	 * Creates a new instance of Threat, that will be send only to specific destination
	 * @param agent
	 *            - the agent id of the agent creating this threat
	 * @param threatIndex
	 *            - the index of the wanted comment from the possible threat.
	 * @param destination - represent the destination for this action, null mean everybody else.
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public Threat(AgentID agent,AgentID destination, int threatIndex) {
		super(agent,destination);
		if (threatIndex < 0 || threatIndex >= possibleThreats.length)
			throw new IllegalArgumentException(
					"Can't create threat with index " + threatIndex);
		this.threatIndex = threatIndex;
	}
	/**
	 * @return the threat this action contain as String
	 */
	@XmlElement(name="content")
	public String getContent() {
		return possibleThreats[threatIndex];
	}
	
	/**
	 * @return the index of the thret this action contain from the array of all possible threats
	 */
	public int getIndex() {
		return threatIndex;
	}
	
	public String toString() {
		return "(Threat: " + getContent() + ")";
	}

	/**
	 * @return the number of possible threats
	 */
	public static int getNumberOfPossibleThreats() {
		return possibleThreats.length;
	}

	/**
	 * Get a specific possible threats as String
	 * 
	 * @param index
	 *            - the index of the wanted threat
	 * @return the wanted comment as String
	 */
	public static String getPossibleThreat(int index) {
		if (index < 0 || index >= possibleThreats.length)
			throw new IllegalArgumentException(
					"Can't return threat with index " + index);
		return possibleThreats[index];
	}

	public Object clone() throws CloneNotSupportedException {
		Threat result = (Threat)super.clone();
		result.threatIndex = threatIndex;
		return result;
	}
}
