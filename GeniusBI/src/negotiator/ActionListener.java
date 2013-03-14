/**
 * 
 */
package negotiator;

import negotiator.actions.Action;

/**
 * This is event handler interface, used by negotiation sessions to listen to asynchronous action the agent perform 
 * @author Yinon Oshrat
 *
 */
public interface ActionListener {
	public void actionSent(Action a);
}
