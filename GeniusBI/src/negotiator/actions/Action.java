/*
 * Action.java
 *
 * Created on November 6, 2006, 10:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import negotiator.AgentID;

/**
 * This Class is a base class for all possible actions during negotiations
 * @author Dmytro Tykhonov
 * 
 */
/**
 * @author User
 *
 */
/**
 * @author User
 *
 */
@XmlRootElement
public class Action implements Cloneable {
	/**
	 * 
	 */

	@XmlElement
    protected   AgentID       agent;
    protected   AgentID       destination;
    protected   String        accompanyText;
    protected   String        speechText;
    protected   int           round; // integer 0,1,2,...: round in the overall bidding.
    
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Action() {}
	
    /** 
     * Creates a new instance of Action.
     * The destination for this action are all other parties in the negotiation session.
     * @param agent is the agent performing the action. 
     * Note that by referring to the agent class object we effectively prevent the agent
     * from garbage collection. 
     * */
    public Action(AgentID agent) {
        this.agent = agent;
        this.destination = null;
        this.accompanyText = null;
    }
    
    /**
     * Creates a new instance of Action 
     * @param agent is the agent performing the action. 
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public Action(AgentID agent,AgentID destination) {
        this.agent = agent;
        this.destination = destination;
        this.accompanyText = null;
    }
    
    /**
     * @return the AgentID that created this action
     */
    public AgentID getAgent() {
        return agent;
    }
    @Override public String toString() {
        return "(Unknown action)";
    } 
    /**
     * @return the destination of this action, null mean everybody
     */
    @XmlElement public AgentID getDestination() {
		return destination;
	}
    
	/**
	 * Set the destination for this action
	 * @param destination destination of this action, null mean everybody
	 */
	public void setDestination(AgentID destination) {
		this.destination = destination;
	}

	public String getAccompanyText() {
		return accompanyText;
	}

	public void setAccompanyText(String accompanyText) {
		this.accompanyText = accompanyText;
	}
	
	public String getSpeechText() {
		return speechText;
	}

	public void setSpeechText(String speachText) {
		this.speechText = speachText;
	}
	
	public int getRound() {
		return round;
	}

	public void setRound(int theRound) {
		this.round = theRound;
	}

	@Override public Object clone() throws CloneNotSupportedException {
		Action result = (Action)super.clone();
		result.agent = agent;
		result.destination = destination;
		result.accompanyText = accompanyText;
		return result;
	}
}
