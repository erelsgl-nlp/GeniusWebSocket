/**
 * 
 */
package negotiator;

import negotiator.actions.Action;

/**
 * @author User
 *
 */
public interface ActionReceiver {
	 public AgentID getAgentID();
	 public String getName();
	 public void ReceiveMessage(Action opponentAction);
	 public String getUserID();
}
