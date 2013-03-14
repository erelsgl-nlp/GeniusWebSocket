package negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.exceptions.NegotiatorException;
import negotiator.utility.UtilitySpace;

/**
 * This class represent the knowledge about the world that is available to the agent.
 * For now it contain only list of posible UitlitySpaces that one of them is the real space of the opponent
 * @author Yinon Oshrat
 * 
 */
@XmlRootElement
public class WorldInformation {
	// hold the possible utility spaces of the opponent
	@XmlElementWrapper(name="utilitySpaces")
	@XmlElement(name="utilitySpace")
	protected ArrayList<UtilitySpace> opponentUtiltySpaces=new ArrayList<UtilitySpace>();
	protected final AgentID opponentID; // its final so negotiator won't be able to mess with this
	protected final AgentID mediatorID; // its final so negotiator won't be able to mess with this

	private HashMap<AgentID, Locale> agentsLocale;

	public WorldInformation() {
		this.opponentID=null;
		this.mediatorID=null;
		this.agentsLocale = new HashMap<AgentID, Locale>();
	}
	
	/**
	 * Constructor
	 * @param opponentID - the ID of the opponent
	 * @param mediatorID - the ID of the mediator or null if there is none
	 */
	public WorldInformation(AgentID opponentID,AgentID mediatorID) {
		this.opponentID=opponentID;
		this.mediatorID=mediatorID;
		this.agentsLocale = new HashMap<AgentID, Locale>();
	}
	/**
	 * 
	 * @return -The number of possible utility spaces of the opponent that are held by this object
	 */
	public int getNumOfPossibleUtiltySpaces() { 
		return opponentUtiltySpaces.size(); 
	
	}
	/**
	 * 
	 * @param index - the number of the utility space to return
	 * @return - possible opponent's utility space with the given index 
	 */
	public UtilitySpace getUtilitySpace(int index) { 
		return opponentUtiltySpaces.get(index); 
	}
	/**
	 * insert the given UtilitySpace into this object
	 * @param us
	 * @throws NegotiatorException - if the domain of the given UtiltySpace differ from 
	 * the domain of UtilitySpaces already in this object
	 */
	public void addUtilitySpace(UtilitySpace us) throws NegotiatorException {
		if (opponentUtiltySpaces.size()>0 && !opponentUtiltySpaces.get(0).getDomain().equals(us.getDomain()))
			throw new NegotiatorException("The UtilitySpace has different domain then the ones already in the WorldInformation"); 
		opponentUtiltySpaces.add(us);
	}
	
	/**
	 * @return the ID of the opponent this WorldInformation object describes
	 */
	public AgentID getOpponentID() {
		return opponentID;
	}
	/**
	 * @return the ID of the mediator in this neg session or null if no mediator exist
	 */
	public AgentID getMediatorID() {
		return mediatorID;
	}
	
	/**
	 * @return all possible utilities for the opponent
	 */
	public ArrayList<UtilitySpace> getUtilities() {
		return opponentUtiltySpaces;
	}

	/**
	 * Sets the HashMap for the agents' locale.
	 * 
	 * @param agentsLocale the agentsLocale to set
	 */
	public void setAgentsLocale(HashMap<AgentID, Locale> agentsLocale) {
		this.agentsLocale = agentsLocale;
	}

	/**
	 * Gets the HashMap of the agents' locale.
	 * @return the agentsLocale
	 */
	public HashMap<AgentID, Locale> getAgentsLocale() {
		return agentsLocale;
	}
	
}
