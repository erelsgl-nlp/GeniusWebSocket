/**
 * 
 */
package negotiator.actions;

import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;

/**
 * An Action that used to reject a previous action
 * @author Yinon Oshrat
 */
@XmlRootElement
public class Reject extends AcceptOrReject {

	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Reject()  { /* Shouldn't be used, exist to enable serialization */ super(); 	}
	
	 
    /**
    * Creates a new instance of Reject, 
    * Used only in synchronous protocols where you don't have to specify the rejected action 
    * The destination for this action are all other parties in the negotiation session.
    * @param agent is the agent performing the action. 
    * */
    public Reject(AgentID agent) {
        super(agent);
    }
    
    /** 
     * Creates a new instance of Reject, 
     * Used only in synchronous protocols where you don't have to specify the accepted action 
     * @param agent is the agent performing the action.
     * @param destination represent the destination for this action, null mean everybody else. 
     * */
    public Reject(AgentID agent,AgentID destination) {
        super(agent,destination);
    }
    /**
     * Used to reject a specific action in async scenario
     * The destination for this action are all other parties in the negotiation session.
     * @param agent - the agent rejecting
     * @param rejectedAction - the rejected action
     */
    public Reject(AgentID agent,BidAction rejectedAction) {
        super(agent);
        this.acceptedOrRejectedAction=rejectedAction;
    }
    
    /**
     * Used to reject a specific action in async scenario
     * @param agent - the agent rejecting
     * @param rejectedAction - the rejected action
     * @param destination represent the destination for this action, null mean everybody else.
     */
    
    public Reject(AgentID agent,AgentID destination,BidAction rejectedAction) {
        super(agent,destination);
        this.acceptedOrRejectedAction=rejectedAction;
    }

    @Override public String toString() {
    	if (acceptedOrRejectedAction == null)
    		return "("+this.agent+" rejects at round "+round+")";
    	else
    		return "("+this.agent+" rejects "+acceptedOrRejectedAction.toString()+" at round "+round+")";
    }    
}
