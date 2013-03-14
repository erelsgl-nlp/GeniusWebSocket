package negotiator.utility;

import negotiator.Bid;
import negotiator.xml.SimpleElement;
import negotiator.issue.Objective;
/**
 * 
 * @author Dmytro?
 * 
 * Evaluator is an object that translates discrete values into an evaluation value.
 * The UtilitySpace attaches it to an issue.
 * It is saved if you save the utility space, using the setXML functions.
 *  
 */

public interface Evaluator {
	
	// Interface methods
	/**
	 * @return the weight associated with this
	 */
	public double getWeight();
	
	/**
	 * Sets the weight with which an Objective or Issue is evaluated.
	 * @param wt The new weight.
	 */
	public void setWeight(double wt);
	
	/**Wouter: lockWeight does not actually lock setWeight or so. It merely is a flag
	 * affecting the behavior of the normalize function in the utility space.
	 */
	public void lockWeight();
	
	public void unlockWeight();
	
	public boolean weightLocked();
	
	/** The getEvaluation method returns a scalar evaluation for a value in a bid. Normalize to the rang [0,1].
	* Providing the complete bid as a parameter to the method allows for issue dependencies.
	* @throws exception if problem, for instance illegal evaluation values.
	*/
	public Double getEvaluation(UtilitySpace uspace, Bid bid, int index) throws Exception;
	
	/** The getEvaluation method returns a scalar evaluation for a value in a bid 
	* Providing the complete bid as a parameter to the method allows for issue dependencies.
	* @throws exception if problem, for instance illegal evaluation values.
	*/
	public Double getEvaluationNotNormalized(UtilitySpace uspace, Bid bid, int ID) throws Exception;
	
	public EVALUATORTYPE getType();
	
	public void loadFromXML(SimpleElement pRoot);
	
	public SimpleElement setXML(SimpleElement evalObj);
	
	/** 
	 * Check whether the evaluator has enough information to make an evaluation.
	 * 
	 * @param whichObjective is the objective/issue to which this evaluator is attached.
	 * @return String describing lacking component, or null if the evaluator is complete. 
	 */
	public String isComplete(Objective whichObjective);
	
	/**
	 * Wouter: added 19oct. Returns cost associated to 
	 * @param uspace is the entire utiltyspace. This is because in future scenarios
	 * the cost of someissue can depend on other issues and their values.
	 * For example, you may get reduction depending if you also buy something else.
	 * @param index is the issue that you want the cost of
	 * @return the cost
	 */
	public Double getCost(UtilitySpace uspace, Bid bid, int index) throws Exception;
	
	public Evaluator clone();
	
	// print statistics of this evaluator. For analysis 
	public void showStatistics();
}
