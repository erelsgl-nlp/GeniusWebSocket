package negotiator.utility;

import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

public class EvaluatorPrice implements Evaluator {

	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;	
	double lowerBound;
	double upperBound;
	double maxMargin = -1;
	double rationalityfactor=0;
	
	public EvaluatorPrice() {
		fweight = 0;
	}

	// Class methods
	public double getWeight(){
		return fweight;
	}
	
	public void setWeight(double wt){
		fweight = wt;
	}
	

	/**
	 * Locks the weight of this Evaluator.
	 */
	public void lockWeight(){
		fweightLock = true;
	}
	
	/**
	 * Unlock the weight of this evaluator.
	 *
	 */
	public void unlockWeight(){
		fweightLock = false;
	}
	
	/**
	 * 
	 * @return The state of the weightlock.
	 */
	public boolean weightLocked(){
		return fweightLock;
	}
	
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) {

		double price=0, costs = 0, maxCost = 0, profit, utility;
		try	{
			price = ((ValueReal)bid.getValue(index)).getValue();
		} catch (Exception  e) {
			e.printStackTrace();
		}
		
		// Collect costs for discrete-valued associated with other issues in bid.		
		for (Entry<Objective,Evaluator> lEntry : uspace.getEvaluators()) {
			if (lEntry.getValue().getType()==EVALUATORTYPE.DISCRETE) {
				try {
					costs += ((EvaluatorDiscrete)lEntry.getValue()).getCost(bid.getValue(lEntry.getKey().getNumber()));
					maxCost += ((EvaluatorDiscrete)lEntry.getValue()).getMaxCost();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Compute profit.
		// The variable lowerBound represents the basic costs associated with any deal.
		// Selling for a price less than lowerBound would mean making a loss.
		profit = price - costs - lowerBound;
		
		// Compute maximal margin if not known yet.
		if (maxMargin==-1) {
			maxMargin = (upperBound - lowerBound - maxCost)/lowerBound;
		}
		utility = profit/(maxMargin*lowerBound);
		if (utility<0)
			utility = 0;
		else if (utility>1)
			utility = 1;
		return utility;
	}
	public ValueReal getValueByEvaluation(UtilitySpace uspace, Bid bid, double pTargetUtility) {
		double lPrice = 0;
		double costs = 0, maxCost = 0, profit, utility;
		// Collect costs for discrete-valued associated with other issues in bid.
		for (Entry<Objective,Evaluator> lEntry : uspace.getEvaluators()) {
			if (lEntry.getValue().getType()==EVALUATORTYPE.DISCRETE) {
				try {
					costs += ((EvaluatorDiscrete)lEntry.getValue()).getCost(bid.getValue(lEntry.getKey().getNumber()));
					maxCost += ((EvaluatorDiscrete)lEntry.getValue()).getMaxCost();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Compute profit.
		// The variable lowerBound represents the basic costs associated with any deal.
		// Selling for a price less than lowerBound would mean making a loss.

		//ASSUMPTION: maxMargin is computed
		// Compute maximal margin if not known yet.
		if (maxMargin==-1) {
			maxMargin = (upperBound - lowerBound - maxCost)/lowerBound;
		}
//		utility = profit/(maxMargin*lowerBound);
//		profit = price - costs - lowerBound;
		profit = pTargetUtility*maxMargin*lowerBound;
		lPrice = profit+costs+lowerBound; 
		if (lPrice<lowerBound)
			lPrice= lowerBound;
		else if (lPrice>upperBound)
			lPrice= upperBound;
		return new ValueReal(lPrice);
		
	}
	
	public Double getEvaluationNotNormalized(UtilitySpace uspace, Bid bid,int index) throws Exception {
		throw new Exception("getEvaluationNotNormalized not implemented for EvaluatorPrice");	
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.PRICE;
	}
	
	public double getLowerBound() {
		return lowerBound;
	}
	
	public double getUpperBound() {
		return lowerBound; //TODO hdv: check if this is correct.
	}	
	
	/**
	 * Sets the lower bound for this evaluator.
	 * @param lb The new lower bound.
	 */
	public void setLowerBound(double lb){
		lowerBound = lb;
	}
	
	/**
	 * Sets the upper bound for this evaluator.
	 * @param ub The new upper bound.
	 */
	public void setUpperBound(double ub){
		upperBound = ub;
	}
	
	/**
	 * Sets the rationality factor of this evaluator.
	 * @param rf The new rationality factor.
	 */
	public void setRationalityFactor(double rf){
		rationalityfactor = rf;
	}
 	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("upperbound"));
		xml_item = ((SimpleElement)pRoot).getChildByTagName("rationality_factor");
		this.rationalityfactor = Double.valueOf(((SimpleElement)xml_item[0]).getAttribute("value"));
	}
	
	/**
	 * Sets weights and evaluator properties for the object in SimpleElement representation that is passed to it.
	 * @param evalObj The object of which to set the evaluation properties.
	 * @return The modified simpleElement with all evaluator properties set.
	 */
	public SimpleElement setXML(SimpleElement evalObj){	
		return evalObj;
	}
	
	public String isComplete(Objective whichobj )
	{
		return null;
	}
	
	
	public Double getCost(UtilitySpace uspace, Bid bid, int index) throws Exception
	{
		//hrow new Exception("getCost not implemented for EvaluatorPrice");
		return new Double(.0);
	}
	
	public EvaluatorPrice clone() {
		EvaluatorPrice ed=new EvaluatorPrice();
		ed.setWeight(fweight);
		//ed.setMaxCost(getMaxCost());
		ed.setLowerBound(lowerBound);
		ed.setUpperBound(upperBound);
		ed.setMaxMargin(maxMargin);
		ed.setRationalityFactor(rationalityfactor);
		return ed;

	}
	public void setMaxMargin(double pValue) {
		maxMargin = pValue;
	}
	public void showStatistics() {
		// TODO Auto-generated method stub
		
	}
	
}
