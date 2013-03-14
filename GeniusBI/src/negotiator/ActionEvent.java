package negotiator;

import negotiator.actions.Action;


/** 
 * This class records details about an action of an agent. 
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
public class ActionEvent
{
	Agent actor;
	Action act;   				// Bid, Accept, etc.
	int round;					// integer 0,1,2,...: round in the overall bidding.
	long elapsedMilliseconds;	// milliseconds since start of nego. Using System.currentTimeMillis();
	double normalizedUtilityA;
	double normalizedUtilityB;
	String errorRemarks;		// errors 
	
	public ActionEvent(Agent actorP,Action actP,int roundP,long elapsed,
			double utilA,double utilB,String remarks)
	{
		actor=actorP;
		act=actP;
		round=roundP;
		elapsedMilliseconds=elapsed;
		normalizedUtilityA=utilA;
		normalizedUtilityB=utilB;
		errorRemarks=remarks;
	}
	
	public String toString()
	{
		return "ActionEvent["+actor+","+act+","+round+","+elapsedMilliseconds+","+
		normalizedUtilityA+","+normalizedUtilityB+","+errorRemarks+"]";
	}
	
	//alina: it would be nice to be able to access the fields and not always the whole string.
	public double getUtilA(){
		return normalizedUtilityA;
	}
	public double getUtilB(){
		return normalizedUtilityB;
	}
	public String getAgentAsString(){
		String a = ""+actor.getClass();
		if(a.substring(0,12).equals("class agents")){
			return a.substring(13);
		}else{
			return a.substring(6);
		}
	}
	public int getRound(){
		return round;
	}
}
