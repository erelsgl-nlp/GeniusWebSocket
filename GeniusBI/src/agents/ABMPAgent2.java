/*
 * SimpleAgent.java
 *
 * Created on November 6, 2006, 9:55 AM
 *
 */

package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;


import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
import negotiator.utility.*;

/**
 * 
 * @author Dmytro Tykhonov & Koen Hindriks
 */

public class ABMPAgent2 extends Agent {

	private String myName;
	private Action messageOpponent;
	private int sessionNumber;
	private int sessionTotalNumber; 
	//private int nrOfIssues;
	private Bid myLastBid = null;
	private Action myLastAction = null;
	private static final double BREAK_OFF_POINT = 0.5;
	//private double[] fIssueWeight;
	private enum ACTIONTYPE { START, OFFER, ACCEPT, BREAKOFF };
	private double fOldTargetUtility; 
	// Paraters used in ABMP strategy
	// TODO: Include these parameters as agent parameters in agent's utility template.
	// QUESTION: How to do that nicely, since these parameters are strategy specific?
	private static final double NEGOTIATIONSPEED = 0.1; // TODO: Probably still somewhere a bug. When you set this too low (e.g. 0.05), no deal is reached and no concession is done!
	private static final double CONCESSIONFACTOR = 1;
	private static final double CONFTOLERANCE = 0;
	private static final double UTIlITYGAPSIZE = 0.02; // Accept when utility gap is <= UTILITYGAPSIZE.
	// CHECK: Utility gap size needed since concession steps get VERY small when opponent's last bid utility is
	// close to own last bid utility.

	// Code is independent from AMPO vs CITY case, but some specifics about
	// using this case as test are specified below.
	// ****************************************************************************************************
	// AMPO VS CITY: Outcome space has size of about 7 milion.
	// ****************************************************************************************************
	// ******************************************************** *******************************************
	// CHECK: ABMP gets stuck on the Car Example with a negotiation speed of less than 0.05!!
	// ABMP "gets stuck" on AMPO vs CITY. The search through the space is not effective in discrete outcome
	// spaces. Even with very high negotiation speed parameters (near 1) no bid can be found with the target utility
	// at a certain point. In a discrete space, the evaluation distance between two different values on an
	// issue need to be taken into account, which may differ from value to value... In such spaces one strategy
	// would be to consider which combination of concessions on a set of issues would provide 
	// ******************************************************** *******************************************

	/** Creates a new instance of MyAgent */

	public ABMPAgent2() {
		super();
	}
	@Override
	public void init() {
		super.init();
		messageOpponent = null;
		myLastBid = null;
		myLastAction = null;
		fOldTargetUtility=1;		

	}

	public void ReceiveMessage(Action opponentAction) {
		messageOpponent = opponentAction;
	}

	private Action proposeInitialBid() throws Exception{
		Bid lBid;
		
		// Return (one of the) possible bid(s) with maximal utility.
		lBid = utilitySpace.getMaxUtilityBid();
		lBid = getBidRandomWalk(utilitySpace.getUtility(lBid)*0.95, utilitySpace.getUtility(lBid));
		myLastBid = lBid;
		return new Offer(this.getAgentID(),lBid);
	}

	private Action proposeNextBid(Bid lOppntBid) {
		Bid lBid = null;
		double lMyUtility, lOppntUtility=1, lTargetUtility;
		// Both parties have made an initial bid. Compute associated utilities from my point of view.
		lMyUtility = fOldTargetUtility;//utilitySpace.getUtility(myLastBid);
		try {
			lOppntUtility = utilitySpace.getUtility(lOppntBid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		lTargetUtility = getTargetUtility(lMyUtility, lOppntUtility);
		try {
			lBid = getBidABMPsimple(lTargetUtility);
		} catch (Exception e) {
			e.printStackTrace();
		}
		myLastBid = lBid;
		fOldTargetUtility = lTargetUtility;
		return new Offer(this.getAgentID(), lBid);
	}

	public Action chooseAction() {
		Action lAction = null;
		ACTIONTYPE lActionType;
		Bid lOppntBid = null;

		lActionType = getActionType(messageOpponent);
		switch (lActionType) {
		case OFFER: // Offer received from opponent
			try {
			lOppntBid = ((Offer) messageOpponent).getBid();
			if (myLastAction == null)
				// Other agent started, lets propose my initial bid.
				lAction = proposeInitialBid();
			//DT Accept if utility gap is smaller than my target utility (instead of actual utility of my previous bid) 
			else if (utilitySpace.getUtility(lOppntBid) >=/* (utilitySpace.getUtility(myLastBid))*/ fOldTargetUtility-UTIlITYGAPSIZE)
				// Opponent bids equally, or outbids my previous bid, so lets accept.
				lAction = new Accept(getAgentID());
			else
				// Propose counteroffer. Get next bid.
				lAction = proposeNextBid(lOppntBid);
			} catch(Exception e) {
				e.printStackTrace();
			}
			break;
		case ACCEPT: // Presumably, opponent accepted last bid, but let's check...
/*			lOppntBid = ((Accept) messageOpponent).getBid();
			if (lOppntBid.equals(myLastBid))
				lAction = new Accept(this, myLastBid);
			else
				lAction = new Offer(this, myLastBid);
			break;*/
		case BREAKOFF:
			// nothing left to do. Negotiation ended, which should be checked by
			// Negotiator...
			break;
		default:
			// I am starting, but not sure whether Negotiator checks this, so
			// lets check also myLastAction...
			if (myLastAction == null)
				try {
					lAction = proposeInitialBid();
				} catch (Exception e) {
					e.printStackTrace();
				}
			else
				// simply repeat last action
				lAction = myLastAction;
			break;
		}

		myLastAction = lAction;
		return lAction;
	}

	private ACTIONTYPE getActionType(Action lAction) {
		ACTIONTYPE lActionType = ACTIONTYPE.START;

		if (lAction instanceof Offer)
			lActionType = ACTIONTYPE.OFFER;
		else if (lAction instanceof Accept)
			lActionType = ACTIONTYPE.ACCEPT;
		else if (lAction instanceof EndNegotiation)
			lActionType = ACTIONTYPE.BREAKOFF;
		return lActionType;
	}

/*	public void loadUtilitySpace(String fileName) {
		
		utilitySpace = new SimpleUtilitySpace(getNegotiationTemplate().getDomain(), fileName);
	
		nrOfIssues = getNegotiationTemplate().getDomain().getNumberOfIssues();
		fIssueWeight = new double[nrOfIssues];
		for (int i=0; i<nrOfIssues; i++) {
			fIssueWeight[i] = this.utilitySpace.getWeight(i);
		}
	}
*/
	// ABMP Specific Code

	private Bid getBidABMPsimple(double targetUtility) throws Exception{
		int nrOfIssues = utilitySpace.getDomain().getIssues().size();
		double[] lIssueWeight = new double[nrOfIssues];
		int i=0;
		for(Issue lIssue : utilitySpace.getDomain().getIssues()) {
			lIssueWeight[i]= utilitySpace.getWeight(lIssue.getNumber());
			i++;
		}
		ArrayList<Issue> lIssues = utilitySpace.getDomain().getIssues();
		Value[] lIssueIndex = new Value[nrOfIssues];
		double[] lIssueAlpha = new double[nrOfIssues];
		double[] lBE = new double[nrOfIssues];
		double[] lBTE = new double[nrOfIssues];
		double[] lTE = new double[nrOfIssues];
		double lUtility = 0, lNF = 0, lAlpha, lUtilityGap, lTotalConcession = 0;

		// ASSUMPTION: Method computes a second bid. Method proposeInitialBid is used to compute first bid.
		lUtilityGap = targetUtility - utilitySpace.getUtility(myLastBid);
		for (i = 0; i < nrOfIssues; i++) {
			lBE[i] = (Double)(utilitySpace.getEvaluator(lIssues.get(i).getNumber()).getEvaluation(utilitySpace, myLastBid, lIssues.get(i).getNumber()));
		}

		// STEP 1: Retrieve issue value for last bid and compute concession on each issue.
		for (i = 0; i < nrOfIssues; i++) {
			lAlpha = (1 - lIssueWeight[i]) * lBE[i]; // CHECK: (1 - lBE[i]); This factor is not right??
			lNF = lNF + lIssueWeight[i] * lAlpha;
			lIssueAlpha[i] = lAlpha;
		}

		// Compute basic target evaluations per issue
		for ( i = 0; i < nrOfIssues; i++) {
			lBTE[i] = lBE[i] + (lIssueAlpha[i] / lNF) * lUtilityGap;
		}

		// STEP 2: Add configuration tolerance for opponent's bid
		for ( i = 0; i < nrOfIssues; i++) {
			lUtility =(Double)(utilitySpace.getEvaluator(lIssues.get(i).getNumber()).getEvaluation(utilitySpace,((Offer) messageOpponent).getBid(), lIssues.get(i).getNumber()));
			lTE[i] = (1 - CONFTOLERANCE) * lBTE[i] + CONFTOLERANCE * lUtility;
		}

		// STEP 3: Find bid in outcome space with issue target utilities corresponding with those computed above.
		// ASSUMPTION: There is always a UNIQUE issue value with utility closest to the target evaluation.
		// First determine new values for discrete-valued issues.
		double lEvalValue;
		int lNrOfRealIssues = 0;
		for (i = 0; i < nrOfIssues; i++) {
			lUtility = 1; // ASSUMPTION: Max utility = 1.
			Issue lIssue = lIssues.get(i);//getNegotiationTemplate().getDomain().getIssue(i);
			if(lIssue.getType() == ISSUETYPE.DISCRETE) {
				IssueDiscrete lIssueDiscrete =(IssueDiscrete)lIssue;
				for (int j = 0; j < lIssueDiscrete.getNumberOfValues(); j++) {
					lEvalValue = ((EvaluatorDiscrete) utilitySpace.getEvaluator(lIssues.get(i).getNumber())).getEvaluation(lIssueDiscrete.getValue(j));
					if (Math.abs(lTE[i] - lEvalValue) < lUtility) {
						lIssueIndex[i] = lIssueDiscrete.getValue(j);
						lUtility = Math.abs(lTE[i]- lEvalValue);
					}//if
				}//for
			lTotalConcession += lIssueWeight[i]*(lBE[i] - ((EvaluatorDiscrete) utilitySpace.getEvaluator(lIssues.get(i).getNumber())).getEvaluation((ValueDiscrete)lIssueIndex[i]));
			} else if (lIssue.getType() == ISSUETYPE.REAL)
				lNrOfRealIssues += 1;
		}
		
		// TODO: Still need to integrate integer-valued issues somewhere here. Low priority.
		
		// STEP 4: RECOMPUTE size of remaining concession step
		// Reason: Issue value may not provide exact match with basic target evaluation value.
		// NOTE: This recomputation also includes any concession due to configuration tolerance parameter...
		// First compute difference between actual concession on issue and target evaluation.
		// TODO: Think about how to (re)distribute remaining concession over MULTIPLE real issues. In car example
		// not important. Low priority.
		double lRestUtitility = lUtilityGap + lTotalConcession;
		// Distribute remaining utility of real and/or price issues. Integers still to be done. See above.
		for (i = 0; i < nrOfIssues; i++) {
			Issue lIssue = lIssues.get(i);//getNegotiationTemplate().getDomain().getIssue(i);
			if(lIssue.getType() == ISSUETYPE.REAL) {
				lTE[i] += lRestUtitility/lNrOfRealIssues;
				switch(utilitySpace.getEvaluator(lIssues.get(i).getNumber()).getType()) {
				case REAL:
					EvaluatorReal lRealEvaluator=(EvaluatorReal) (utilitySpace.getEvaluator(lIssues.get(i).getNumber()));
					double r = lRealEvaluator.getValueByEvaluation(lTE[i]);
					lIssueIndex[i] = new ValueReal(r);
					break;
				case PRICE:
					EvaluatorPrice lPriceEvaluator=(EvaluatorPrice)(utilitySpace.getEvaluator(i));
					lIssueIndex [i] =  new ValueReal(lPriceEvaluator.getLowerBound());
					HashMap<Integer, Value> lValues = new HashMap<Integer, Value>();
					for(i=0;i<nrOfIssues;i++) {
						lValues.put(lIssues.get(i).getNumber(), lIssueIndex[i]);
					}
					
					Bid lTempBid = new Bid(utilitySpace.getDomain(), lValues);
					lIssueIndex[i] =  lPriceEvaluator.getValueByEvaluation(utilitySpace, lTempBid, lTE[i]);
					break;
				}
			}
		}
		HashMap<Integer, Value> lValues = new HashMap<Integer, Value>();
		for(i=0;i<nrOfIssues;i++) {
			lValues.put(lIssues.get(i).getNumber(), lIssueIndex[i]);
		}
		return new Bid(utilitySpace.getDomain(),lValues);
	}

	private double getTargetUtility(double myUtility, double oppntUtility) {
		return myUtility + getConcessionStep(myUtility, oppntUtility);
	}

	private double getNegotiationSpeed() {
		return NEGOTIATIONSPEED;
	}

	private double getConcessionFactor() {
		// The more the agent is willing to concess on its aspiration value, the
		// higher this factor.
		return CONCESSIONFACTOR;
	}

	private double getConcessionStep(double myUtility, double oppntUtility) {
		double lConcessionStep = 0, lMinUtility = 0, lUtilityGap = 0;

		// Compute concession step
		lMinUtility = 1 - getConcessionFactor();
		lUtilityGap = (oppntUtility - myUtility);
		lConcessionStep = getNegotiationSpeed() * (1 - lMinUtility / myUtility) * lUtilityGap;
		System.out.println(lConcessionStep);
		return lConcessionStep;
	}

	
	// Quicksort algorithm that returns a sorted list of issue indices based on weights
	
	private void quickSort(double[] lWeights, int[] lSortedIndex, int left, int right) {
		int pivotIndex;
		
	    // if (right > left)
    	pivotIndex = left;
	    int pivotNewIndex = partition(lWeights, lSortedIndex, left, right, pivotIndex);
	    if (pivotNewIndex > left+1)
	    	quickSort(lWeights, lSortedIndex, left, pivotNewIndex-1);
	    if (pivotNewIndex+1<right)
	    	quickSort(lWeights, lSortedIndex, pivotNewIndex+1, right);
	}
	
	private int partition(double[] lWeights, int[] lSortedIndex, int left, int right, int pivotIndex) {
	    double pivotValue = lWeights[pivotIndex];
	    swap(lWeights, pivotIndex, right); // Move pivot to end
	    swap(lSortedIndex, pivotIndex, right);
	    int storeIndex = left;
        for (int i=left; i<right; i++) {
            if (lWeights[i] <= pivotValue) {
            	swap(lWeights, storeIndex, i);
            	swap(lSortedIndex, storeIndex, i);
            	storeIndex = storeIndex + 1;
            }
        }
        swap(lWeights, right, storeIndex); // Move pivot to its final place
        swap(lSortedIndex, right, storeIndex);
        return storeIndex;
	}
	
	private void swap(double[] values, int fromIndex, int toIndex) {
		double x = values[fromIndex];
		values[fromIndex] = values[toIndex];
		values[toIndex] = x;
	}
	
	private void swap(int[] values, int fromIndex, int toIndex) {
		int x = values[fromIndex];
		values[fromIndex] = values[toIndex];
		values[toIndex] = x;
	}
	private Bid getBidRandomWalk(double lowerBound, double upperBoud)  throws Exception{
		Bid lBid = null, lBestBid = null;

		// Return bid that gets closest to target utility in a "random walk"
		// search.
		lBestBid = utilitySpace.getDomain().getRandomBid();
		while(true) {
			lBid = utilitySpace.getDomain().getRandomBid();
			if ((utilitySpace.getUtility(lBid) > lowerBound)&&
					(utilitySpace.getUtility(lBestBid) < upperBoud)) {
				lBestBid = lBid;
				break;
			}
				
		}
		return lBestBid;
	}

	
}
