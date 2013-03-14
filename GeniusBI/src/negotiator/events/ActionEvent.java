package negotiator.events;

import negotiator.ActionReceiver;
import negotiator.Agent;
import negotiator.actions.Action;


/** This class records details about an action of an agent. 
 * It is passed as event to interested parties (currently the logger&data display GUI).
 * 
 * 
 * If there is a time-out or other protocol error,
 * an additional EndNegotiation action will be created
 * by the NegotiationManager and sent to listener.
 * 
 * @author wouter
 *
 */
public class ActionEvent extends NegotiationEvent
{
	ActionReceiver actor;
	Action action;   				// Bid, Accept, etc.
	long elapsedMilliseconds;	// milliseconds since start of nego. Using System.currentTimeMillis();
	double normalizedUtilityA;
	double normalizedUtilityB;
	String errorRemarks;		// errors 
	
	public ActionEvent(Object source, ActionReceiver actorP,Action actP,long elapsed,
			double utilA,double utilB,String remarks)
	{
		super(source);
		actor=actorP;
		action=actP;
		elapsedMilliseconds=elapsed;
		normalizedUtilityA=utilA;
		normalizedUtilityB=utilB;
		
		errorRemarks=remarks;
	}
	
	public String toString()
	{
		return "ActionEvent["+actor+","+action+","+elapsedMilliseconds+","+
		normalizedUtilityA+","+normalizedUtilityB+","+errorRemarks+"]";
	}

	public ActionReceiver getActor() {
		return actor;
	}

	public Action getAction() {
		return action;
	}

	public int getRound() {
		return action.getRound();
	}

	public long getElapsedMilliseconds() {
		return elapsedMilliseconds;
	}

	public double getNormalizedUtilityA() {
		return normalizedUtilityA;
	}
	public String getAgentAsString() {
		return actor.getName();
	}

	public double getNormalizedUtilityB() {
		return normalizedUtilityB;
	}

	public String getErrorRemarks() {
		return errorRemarks;
	}
	
	
}
