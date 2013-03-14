package negotiator.utility;

import negotiator.Bid;
import negotiator.issue.*;
import negotiator.xml.SimpleElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class EvaluatorInteger implements Evaluator {
	
	// Class fields
	private double fweight; //the weight of the evaluated Objective or Issue.
	private boolean fweightLock;
	int lowerBound;
	int upperBound;
	EVALFUNCTYPE type;
	HashMap<Integer, Double> fParam;
		
	public EvaluatorInteger() {
		fParam = new HashMap<Integer, Double>();
		
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
		Integer lTmp = null;
		try {
			lTmp = ((ValueInteger)bid.getValue(index)).getValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		switch(this.type) {
		case LINEAR:
			Double d = EVALFUNCTYPE.evalLinear(lTmp, this.fParam.get(1), this.fParam.get(0));
			if (d<0)
				d=0.0;
			else if (d>1)
				d=1.0;
			return d;
		case CONSTANT:
			return new Double(this.fParam.get(0));
		default:
			return -1.;
		}	
	}
	
	public EVALUATORTYPE getType() {
		return EVALUATORTYPE.INTEGER;
	}
	
	public EVALFUNCTYPE getFuncType(){
		return this.type;
	}
	
	public int getLowerBound() {
		return lowerBound;
	}
	
	public int getUpperBound() {
		return lowerBound;   //TODO check if this is ok.
	}	
	
	/**
	 * Sets the lower bound of this evaluator.
	 * @param lb The new lower bound
	 */
	public void setLowerBound(int lb) {
		lowerBound = lb;
	}
	
	/**
	 * Sets the upper bound of this evaluator.
	 * @param ub The new upper bound
	 */
	public void setUpperBound(int ub){
		upperBound = ub;
	}
	
	/**
	 * Sets the ftype of this evaluator
	 * @param ft The ftype, either <code>"linear"</code> or <code>"constant"</code>
	 */
	public void setftype(String ft){
		type = EVALFUNCTYPE.convertToType(ft);
	}
	
	/**
	 * Sets the linear parameter for this evaluator, and changes the ftype to linear.
	 * @param par0 The linear parameter
	 */
	public void setLinearParam(int par0){
		setftype("linear");
		fParam.put(new Integer(1), new Double(par0) );
	}

	/**
	 * 
	 * @return The linear parameter of this Evaluator, or 0 if it doesn't exist.
	 */		
	public double getLinearParam(){
		try{
			return fParam.get(new Integer(1));
		}catch(Exception e){
			//do nothing
		}
		return 0;
	}
	/**
	 * Sets the constant parameter for this evaluetor, and changes the ftype to constant.
	 * @param par1 The constant parameter.
	 */
	public void setConstantParam(double par1){
		setftype("constant");
		fParam.put(new Integer(0), new Double(par1));
	}

	/**
	 * 
	 * @return The constant parameter of this Evaluator, or 0 if it doesn't exist.
	 */	
	public double getConstantParam(){
		try{
			return fParam.get(new Integer(0));
		}catch(Exception e){
			//do nothing.
		}
		return 0;
	}
	
	public void loadFromXML(SimpleElement pRoot) {
		Object[] xml_item = ((SimpleElement)pRoot).getChildByTagName("range");
		this.lowerBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		this.upperBound = Integer.valueOf(((SimpleElement)xml_item[0]).getAttribute("lowerbound"));
		Object[] xml_items = ((SimpleElement)pRoot).getChildByTagName("evaluator");
		if(xml_items.length != 0){
			String ftype = ((SimpleElement)xml_items[0]).getAttribute("ftype");
			if (ftype!=null)
				this.type = EVALFUNCTYPE.convertToType(ftype);
			// TODO: define exception.
			switch(this.type) {
			case LINEAR:
				this.fParam.put(1, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter1")));
				this.fParam.put(0, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
				break;
			case CONSTANT:
				this.fParam.put(0, Double.valueOf(((SimpleElement)xml_items[0]).getAttribute("parameter0")));
				break;
			}
		}
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
		//TODO: implement isComplete in the EvaluatorInteger
		return null;
	}
	
	
	public Double getCost(UtilitySpace uspace, Bid bid, int index) throws Exception
	{
		throw new Exception("getCost not implemented for EvaluatorInteger");
	}

	public void showStatistics() {
		// TODO Auto-generated method stub
		
	}
	public EvaluatorInteger clone()
	{
		EvaluatorInteger ed=new EvaluatorInteger();
		//ed.setType(type);
		ed.setWeight(fweight);
		ed.type = type; 
		ed.setUpperBound(upperBound);
		ed.setLowerBound(lowerBound);
		try{
			for (Entry<Integer, Double> entry:fParam.entrySet())
				ed.fParam.put(new Integer(entry.getKey()), new Double(entry.getValue()));
		}
		catch (Exception e)  { System.out.println("INTERNAL ERR. clone fails"); }

		return ed;
	}

	/* (non-Javadoc)
	 * @see negotiator.utility.Evaluator#getEvaluationNotNormalized(negotiator.utility.UtilitySpace, negotiator.Bid, int)
	 */
	public Double getEvaluationNotNormalized(UtilitySpace uspace, Bid bid,int index) throws Exception {
		throw new Exception("getEvaluationNotNormalized not implemented for EvaluatorInteger");	
	}

}
