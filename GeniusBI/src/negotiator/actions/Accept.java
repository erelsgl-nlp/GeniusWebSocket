/*
 * AcceptBid.java
 *
 * Created on November 6, 2006, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
/**
 * An Action that used to accept a previous action
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class Accept extends AcceptOrReject {
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Accept() {}
	
    /** 
     * Creates a new instance of Accept, 
     * Used only in synchronous protocols where you don't have to specify the accepted action 
     * The destination for this action are all other parties in the negotiation session.
     * @param agent is the agent performing the action. 
     * */
    public Accept(AgentID agent) {
        super(agent);
    }
    
    /** 
     * Creates a new instance of Accept, 
     * Used only in synchronous protocols where you don't have to specify the accepted action 
     * @param agent is the agent performing the action.
     * @param destination represent the destination for this action, null mean everybody else. 
     * */
    public Accept(AgentID agent, AgentID destination) {
        super(agent,destination);
    }
    /**
     * Used to accept a specific action in async scenario
     * The destination for this action are all other parties in the negotiation session.
     * @param agent - the agent accepting
     * @param acceptedAction - the accepted action
     */
    public Accept(AgentID agent,BidAction acceptedAction) {
        super(agent);
        this.acceptedOrRejectedAction=acceptedAction;
    }
    
    /**
     * Used to accept a specific action in async scenario
     * @param agent - the agent accepting
     * @param acceptedAction - the accepted action
     * @param destination represent the destination for this action, null mean everybody else.
     */
    public Accept(AgentID agent, AgentID destination,BidAction acceptedAction) {
        super(agent,destination);
        this.acceptedOrRejectedAction=acceptedAction;
    }

    @Override public String toString() {
    	if (acceptedOrRejectedAction == null)
    		return "("+this.agent+" accepts at round "+round+")";
    	else
    		return "("+this.agent+" accepts "+acceptedOrRejectedAction.toString()+" at round "+round+")";
    }
}
