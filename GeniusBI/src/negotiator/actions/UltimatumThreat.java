/*
 * UltimatumThreat.java
 *
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.AgentID;

/**
 * An Action used to send a textual ultimatum threat by the mediator to other parties in negotiation.
 * The ultimatum threat is chosen from a predefined set of possible ultimatum threats 
 * @author Eran Sadeh-Or
 */

@XmlRootElement
public class UltimatumThreat extends Action {
	@XmlElement
	protected int ultimatumThreatIndex;
	
	@XmlElement
	protected int ultimatumThreatReasonIndex;
	
	@XmlElement
	protected boolean ultimatumThreatActivated;

	@XmlElement
	protected int ultimatumThreatPenalty;
	
	/**
	 * Contains the set of all possible ultimatum threats
	 */
	public static final String[] ultimatumThreatsDescription;
	public static final int[] ultimatumPenalties = {-60, -90, -130};
	static {
		ultimatumThreatsDescription = new String[] {
				new String(
						"I can put you on probation, so that if you cause more problems, you'll be kicked out of the complex. " + 
						"This would embarrass you in front of your friends in the complex, since I would have to let everyone living in the apartments know that you are on probation and that they should report you if you cause any problems. " +
						"Points: " + ultimatumPenalties[0]),
				new String(
			    		"I can inform the Oakland Company that you are creating a problem for the community. " +
			    		"This would embarrass you in front of your subordinates, colleagues, and superiors at work. " +
			    		"It could even damage your reputation as a team player on the job, and possibly put your job in danger. " +
			    		"Points: " + ultimatumPenalties[1]),
				new String(
			    		"I can advise the Oakland Company to evict you. " +
			    		"The rent at Oakland Apartments is significantly lower than it is for other apartments in the area. " +
			    		"If you are evicted, you will either have to find a new apartment, which will likely cost more than you can afford on your current salary, or you will have to leave the area and your job. " +
			    		"Points: " + ultimatumPenalties[2])
				
		};
	}

	 /**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public UltimatumThreat() {}
	
	/**
	 * Creates a new instance of UltimatumThreat
	 * 
	 * @param agent
	 *            - the agent id of the agent creating this ultimatum threat
	 * @param threatIndex
	 *            - the index of the wanted ultimatum threat
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public UltimatumThreat(AgentID agent, int ultimatumThreatIndex) {
		super(agent);
		if (ultimatumThreatIndex < 0 || ultimatumThreatIndex >= ultimatumThreatsDescription.length)
			throw new IllegalArgumentException(
					"Can't create ultimatum threat with index " + ultimatumThreatIndex);
		this.ultimatumThreatIndex = ultimatumThreatIndex;
		this.ultimatumThreatPenalty = ultimatumPenalties[ultimatumThreatIndex];
	}
	/**
	 * Creates a new instance of Ultimatum Threat, that will be send only to specific destination
	 * @param agent
	 *            - the agent id of the agent creating this ultimatum threat
	 * @param ultimatumThreatIndex
	 *            - the index of the wanted threat
	 * @param destination - represent the destination for this action, null mean everybody else.
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public UltimatumThreat(AgentID agent, AgentID destination, int ultimatumThreatIndex, int ultimatumThreatReasonIndex, boolean ultimatumThreatActivated) {
		super(agent,destination);
		if (ultimatumThreatIndex < 0 || ultimatumThreatIndex >= ultimatumThreatsDescription.length)
			throw new IllegalArgumentException(
					"Can't create ultimatum threat with index " + ultimatumThreatIndex);
		this.ultimatumThreatIndex = ultimatumThreatIndex;
		this.ultimatumThreatReasonIndex = ultimatumThreatReasonIndex;
		this.ultimatumThreatActivated = ultimatumThreatActivated;
		this.ultimatumThreatPenalty = ultimatumPenalties[ultimatumThreatIndex];
	}
	/**
	 * @return the threat this action contain as String
	 */
	@XmlElement(name="content")
	public String getContent() {
		return ultimatumThreatsDescription[ultimatumThreatIndex];
	}
	
	/**
	 * @return the index of the threat this action contain from the array of all possible threats
	 */
	public int getIndex() {
		return ultimatumThreatIndex;
	}

	public int getPenalty() {
		return ultimatumThreatPenalty;
	}
	
	public String toString() {
		return "(Ultimatum Threat: " + getContent() + ")";
	}

	/**
	 * @return the number of possible threats
	 */
	public static int getNumberOfPossibleUltimatumThreats() {
		return ultimatumThreatsDescription.length;
	}

	/**
	 * Get a specific possible ultimatum threat as String
	 * 
	 * @param index
	 *            - the index of the wanted ultimatum threat
	 * @return the wanted comment as String
	 */
	public static String getUltimatumThreatDescription(int index) {
		if (index < 0 || index >= ultimatumThreatsDescription.length)
			throw new IllegalArgumentException(
					"Can't return ultimatum threat with index " + index);
		return ultimatumThreatsDescription[index];
	}

	/**
	 * Get a specific ultimatum penalty as int
	 * 
	 * @param index
	 *            - the index of the wanted ultimatum penalty
	 * @return the wanted penalty value as int
	 */
	public static int getUltimatumPenalty(int index) {
		if (index < 0 || index >= ultimatumPenalties.length)
			throw new IllegalArgumentException(
					"Can't return ultimatum penalty with index " + index);
		return ultimatumPenalties[index];
	}
	
	public Object clone() throws CloneNotSupportedException {
		UltimatumThreat result = (UltimatumThreat)super.clone();
		result.ultimatumThreatIndex = ultimatumThreatIndex;
		result.ultimatumThreatPenalty = ultimatumThreatPenalty;
		result.ultimatumThreatReasonIndex = ultimatumThreatReasonIndex;
		result.ultimatumThreatActivated = ultimatumThreatActivated;
		return result;
	}
}
