package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;

import negotiator.AgentID;


/**
 * An action that relates to a previous action - either accepts or rejects it.
 * @author erelsgl
 * @since 2012-01-29
 */
public class AcceptOrReject extends Action {
	@XmlElement(name = "acceptedOrRejectedAction")
	protected BidAction acceptedOrRejectedAction = null;

	public AcceptOrReject() {
		super();
	}

	public AcceptOrReject(AgentID agent) {
		super(agent);
	}

	public AcceptOrReject(AgentID agent, AgentID destination) {
		super(agent, destination);
	}

	/**
	 * @return The accepted action, that this accept refer to
	 */
	public BidAction getAcceptedOrRejectedAction() {  
		return acceptedOrRejectedAction; 
	}

	@Override public AcceptOrReject clone() throws CloneNotSupportedException {
		AcceptOrReject result = (AcceptOrReject)super.clone();
		result.acceptedOrRejectedAction = acceptedOrRejectedAction==null? null: (BidAction)acceptedOrRejectedAction.clone();
		return result;
	}

}