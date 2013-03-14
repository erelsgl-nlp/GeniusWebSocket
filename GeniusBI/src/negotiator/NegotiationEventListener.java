package negotiator;


import negotiator.events.ActionEvent;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEndedEvent;
import negotiator.events.NegotiationSessionEvent;


/** 
 * implement this class in order to subscribe with the NegotiationManager
 * to get callback on handleEvent().
 * 
 * @author wouter
 *
 */
public interface NegotiationEventListener 
{
	 /** IMPORTANT:
	  * in handleEvent, don't do more than just storing the event and
	  * notifying your interface that a new event has arrived.
	  * Doing more than this will snoop time from the negotiation,
	  * which will disturb the negotiation.
	  * @param evt
	  */
	public void handleActionEvent(ActionEvent evt);
	
	public void handleNegotiationEndedEvent(NegotiationEndedEvent evt);
	
	public void handleLogMessageEvent(LogMessageEvent evt);
	
	public void handleNegotiationSessionEvent(NegotiationSessionEvent evt);
	
	public void handleBlateralAtomicNegotiationSessionEvent(BilateralAtomicNegotiationSessionEvent evt);
	
}


