package agents;

import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

import negotiator.*;
import negotiator.actions.*;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;


class BidComparator implements java.util.Comparator<Bid>
{
	UtilitySpace utilspace;
	
	BidComparator(UtilitySpace us)
	{
		if (us==null) throw new NullPointerException("null utility space");
		utilspace=us;
	}
	
	public int compare(Bid b1,Bid b2) throws ClassCastException
	{
		double d1=0 , d2=0;
		try {
			d1 = utilspace.getUtility(b1);
			d2 = utilspace.getUtility(b2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (d1 < d2) return 1; 
		if (d1 > d2) return -1; 
		return 0;
	}
}



/**
 *
 * @author W.Pasman
 * This agent just offers all bids in decreasing utility order to opponent.
 * The acceptance criterion is the same as with the SimpleAgent.
 * 
 */
public class DecUtilAgent extends Agent
{
    private Action actionOfPartner=null;
    private static final double MINIMUM_BID_UTILITY = 0.5;
    
    
    	// just here to suggest possibilities, not used in this agent.
    private int sessionNumber;			
    private int sessionTotalNumber;
    ArrayList<Bid> bids=new ArrayList<Bid>();
    int nextBidIndex=0; // what's the next bid from bids to be done.
 
    
    /**
     * init is called when a next session starts with the same opponent.
     */
    public void init() {

        BidIterator biter=new BidIterator(utilitySpace.getDomain());
        while (biter.hasNext()) bids.add(biter.next());
		Collections.sort(bids, new BidComparator(utilitySpace));

    }

	public void ReceiveMessage(Action opponentAction) {
        actionOfPartner = opponentAction;
    }
    
 
	public Action chooseAction()
    {
        Action action = null;
        try { 
            if(actionOfPartner==null) action = new Offer(getAgentID(),bids.get(nextBidIndex++));
            if(actionOfPartner instanceof Offer)
            {
                Bid partnerBid = ((Offer)actionOfPartner).getBid();
                double offeredutil=utilitySpace.getUtility(partnerBid);
                double time=((new Date()).getTime()-startTime.getTime())/(1000.*totalTime);
                double P=Paccept(offeredutil,time);
                if (.02*P>Math.random()) action = new Accept(getAgentID());
                else action = new Offer(getAgentID(),bids.get(nextBidIndex++));               
            }
            Thread.sleep(300); // 3 bids per second is good enough.
        } catch (Exception e) { 
        	System.out.println("Exception in ChooseAction:"+e.getMessage());
        	action=new Accept(getAgentID()); // best guess if things go wrong. 
        }
        return action;
    }
    

	   
	/**
	 * This function determines the accept probability for an offer.
	 * At t=0 it will prefer high-utility offers.
	 * As t gets closer to 1, it will accept lower utility offers with increasing probability.
	 * it will never accept offers with utility 0.
	 * @param u is the utility 
	 * @param t is the time as fraction of the total available time 
	 * (t=0 at start, and t=1 at end time)
	 * @return the probability of an accept at time t
	 * @throws Exception if you use wrong values for u or t.
	 * 
	 */
	double Paccept(double u, double t1) throws Exception
	{
		double t=t1*t1*t1; // steeper increase when deadline approaches.
		if (u<0 || u>1.05) throw new Exception("utility "+u+" outside [0,1]");
		 // normalization may be slightly off, therefore we have a broad boundary up to 1.05
		if (t<0 || t>1) throw new Exception("time "+t+" outside [0,1]");
		if (u>1.) u=1;
		if (t==0.5) return u;
		return (u - 2.*u*t + 2.*(-1. + t + Math.sqrt(sq(-1. + t) + u*(-1. + 2*t))))/(-1. + 2*t);
	}
	
	double sq(double x) { return x*x; }

}
