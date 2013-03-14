/*
 * MyAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agents;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
// import negotiator.exceptions.*;
import negotiator.utility.UtilitySpace;

/**
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * 
 */
public class SimpleAgentOld extends Agent{
    private Action actionOfPartner;
    private int sessionNumber;
    private int sessionTotalNumber;
    private int[] myPreviousBidIndex;
    private Bid myPreviousBid;
    private static final double BREAK_OFF_POINT = 0.5;
 
    /** Creates a new instance of MyAgent */

    public SimpleAgentOld() {
        super();        
        return;
    }
    
    protected void init(int sessionNumber, int sessionTotalNumber, Integer totalTimeP,Date startTimeP, UtilitySpace us) {
        super.init (sessionNumber, sessionTotalNumber, startTimeP,totalTimeP,us);
        this.sessionNumber = sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
        actionOfPartner = null;
        myPreviousBid = null;
        return;
    }

	public void ReceiveMessage(Action opponentAction) {
        this.actionOfPartner = opponentAction;
        return;
    }
    
    private Bid getNextBid() throws Exception
    {
    	HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs <issuenumber,chosen value string>
    	ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
    	
    	// create a random bid.
        for(Issue lIssue:issues) 
        {
			switch(lIssue.getType()) {
			case INTEGER:
	            int numberOfOptions = 
	            	((IssueInteger)lIssue).getUpperBound()- 
	            	((IssueInteger)lIssue).getLowerBound()+1; //do not include "unspecified"
	            int optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
	            System.out.println(optionIndex);
	            values.put(lIssue.getNumber(), new ValueInteger(((IssueInteger)lIssue).getLowerBound()+optionIndex));
			case REAL: 
				IssueReal lIssueReal =(IssueReal)lIssue;
				double lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lIssueReal.getNumberOfDiscretizationSteps();
	            numberOfOptions =lIssueReal.getNumberOfDiscretizationSteps();
	            optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
	            values.put(lIssue.getNumber(), new ValueReal(lIssueReal.getLowerBound()+lOneStep*optionIndex));
				break;
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
	            numberOfOptions =lIssueDiscrete.getNumberOfValues();
	            optionIndex = Double.valueOf(java.lang.Math.random()*(numberOfOptions)).intValue();
	            if (optionIndex >= numberOfOptions) optionIndex= numberOfOptions-1;
	            values.put(lIssue.getNumber(), lIssueDiscrete.getValue(optionIndex));
				break;
			}
		}
        return new Bid(utilitySpace.getDomain(),values);
    }
    
    private Action chooseNextAction() 
    {
        Bid nextBid=null ;
        try { nextBid = getNextBid(); }
        catch (Exception e) { System.out.println("Problem with received bid:"+e.getMessage()+". cancelling bidding"); }
        if (nextBid == null) return (new EndNegotiation(this));                
        myPreviousBid = nextBid;
        return (new Offer(this, nextBid));
    }
    
	public Action chooseAction()
    {
        Action action = null;
        try { 
            if(actionOfPartner==null) {
                action = chooseNextAction();
            } else {
                if(actionOfPartner instanceof Offer) {
                    Bid partnerBid = ((Offer)actionOfPartner).getBid();
                    if(myPreviousBid!=null)
                        if(utilitySpace.getUtility(partnerBid)==utilitySpace.getUtility(myPreviousBid))
                            action = new Accept(this);
                        else action = chooseNextAction();                   
                    else {
                        action = chooseNextAction();                   
                    }
                }
            }
            Thread.sleep(1000);
        } catch (Exception e) {
        
        }
        return action;
    }
    
 

}
