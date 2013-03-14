/*
 * UtilitySpace.java
 *
 * Created on November 6, 2006, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.utility;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;

import negotiator.utility.EVALFUNCTYPE;
import negotiator.*;
import negotiator.issue.*;
import negotiator.xml.*;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.Warning;

/**
 * Used to calculate the utility of a bid according to specific utility function.
 * @see #getEvaluation(int, Bid) - utility of a single issue
 * @see #getUtility(Bid) - utility of all issues
 * @see #getCost(Bid) - cost of bid
 * @see #getMaxUtilityBid() - bid with the maximum utility (ignoring cost)
 * 
 * @author Dmytro Tykhonov & Koen Hindriks 
 *
 * Wouter: the utility space couples all objectives to weights and evaluators.
 * A UtilitySpace currently is not bound to one agent.
 * I can see some security issues with that...
 * 
 * Wouter: this class is final to prevent users (students) to override the getUtility function
 * with their own version of UtilitySpace
 * 
 * Wouter 15nov: un-done the final, students may hack what they want, but they work with a copy anyway.
 */
public class UtilitySpace {

	public enum CHECK_CONSTRAINTS {DO_CHECK, DO_NOT_CHECK};
	public static CHECK_CONSTRAINTS fCheckConstraints = CHECK_CONSTRAINTS.DO_CHECK;
	// Class fields
	protected Domain domain;
	//Added by Dmytro: I need the XMLRoot for the utility space to load the Similarity functions
	// in the Similarity agent
	private SimpleElement fXMLRoot;
	public SimpleElement getXMLRoot() { return fXMLRoot;}
	// Added by Yinon, needed in Async protocol, let you decide if by default the utility used is the normalized or not 
	private Double fReservationValue = null;
	private Double fOptOutValue = null;
	private Double fTimeAffectValue = null;
	private Double fWeightMultiplyer = 1.0;
	private boolean fUseNormalizedUtility=true;
	private String fName="";

	private Map<Objective, Evaluator> fEvaluators; //changed to use Objective. TODO check casts.

	private String fileName;

	private ArrayList<UtilitySpace> opponentUtiltySpaces = new ArrayList<UtilitySpace>();

	private Bid maxUtilityBidCache = null;
	private Double maxUtilityCache = null;

	public static boolean log=false; 

	/**
	 * Creates an empty utility space.
	 */
	public UtilitySpace(){
		this.domain = new Domain();
		fEvaluators = new HashMap<Objective, Evaluator>();
	}

	/**
	 * Create new default util space for a given domain.
	 * @param domain
	 * @param fileName to read util space from. 
	 * Set fileName to "" if no file available, in which case default evaluators are loaded..
	 * @throws IOException e.g. if domain does not match the util space, or file not found.
	 */
	public UtilitySpace(Domain domain, String fileName) throws IOException {
		this.domain = domain;
		this.fileName = fileName;
		fEvaluators = new HashMap<Objective, Evaluator>();
		if(!fileName.equals("")) {
			loadTreeFromFile(fileName);
		} else { // add evaluator to all objectives
			ArrayList<Objective> objectives=domain.getObjectives();        	
			for (Objective obj:objectives) {
				Evaluator eval =  DefaultEvaluator(obj);
				fEvaluators.put(obj, eval);
				if(eval instanceof EvaluatorDiscrete) {
					EvaluatorDiscrete evalDisc = (EvaluatorDiscrete)eval;
					IssueDiscrete issue = (IssueDiscrete)obj;        			

					for(Value val: issue.getValues()) {
						ValueDiscrete valDisc = (ValueDiscrete)val;
						evalDisc.setCost(valDisc, issue.getCost(valDisc));
					}
				}
			}

		}
	}

	/** @return a clone of another utility space */
	@SuppressWarnings("unchecked")
	public UtilitySpace(UtilitySpace us)
	{
		domain=us.getDomain();
		fileName = us.getFileName();
		fEvaluators = new HashMap<Objective, Evaluator>();
		fReservationValue = us.getReservationValue(); 
		fOptOutValue = us.getOptOutValue(0);
		fTimeAffectValue=us.getTimeEffectValue();
		fWeightMultiplyer=us.getWeightMultiplyer();
		// and clone the evaluators
		for (Objective obj:domain.getObjectives())
		{
			Evaluator e=us.getEvaluator(obj.getNumber());
			if (e!=null) fEvaluators.put(obj, e.clone());
			// else incomplete. But that is allowed I think.
			// especially, objectives (the non-Issues) won't generally have an evlauator.
		}
		fXMLRoot = us.getXMLRoot();
		fUseNormalizedUtility=us.fUseNormalizedUtility;
		opponentUtiltySpaces = (ArrayList<UtilitySpace>) us.opponentUtiltySpaces.clone();
	}


	/**
	 * create a default evaluator for a given Objective.
	 * This function is placed here, and not in Objective, because
	 * the Objectives should not be loaded with utility space functionality.
	 * The price we pay for that is that we now have an ugly switch inside the code,
	 * losing some modularity.
	 * @param obj the objective to create an evaluator for
	 * @return the defualt evaluator
	 * @author W.Pasman
	 */
	public Evaluator DefaultEvaluator(Objective obj)
	{
		if (obj.isObjective()) return new EvaluatorObjective();
		//if not an objective then it must be an issue.
		switch (((Issue)obj).getType())
		{
		case DISCRETE: return new EvaluatorDiscrete();
		case INTEGER: return new EvaluatorInteger();
		case REAL: return new EvaluatorReal();
		default: System.out.println("INTERNAL ERROR: issue of type "+((Issue)obj).getType()+
				"has no default evaluator");
		}
		return null;
	}



	/**
	 * Checks the normalization throughout the tree. Will eventually replace checkNormalization 
	 * @return true if the weigths are indeed normalized, false if they aren't. 
	 */
	private boolean checkTreeNormalization(){
		return checkTreeNormalizationRecursive(domain.getObjectivesRoot());
	}

	/**
	 * Private helper function to check the normalisation throughout the tree.
	 * @param currentRoot The current parent node of the subtree we are going to check
	 * @return True if the weights are indeed normalized, false if they aren't.
	 */
	private boolean checkTreeNormalizationRecursive(Objective currentRoot ){
		boolean normalised = true;
		double lSum = 0;

		Enumeration<Objective> children = currentRoot.children();

		// Wouter: there is nothing recursive here. This function seems broken
		while(children.hasMoreElements() && normalised){

			Objective tmpObj = children.nextElement();
			lSum += (fEvaluators.get(tmpObj)).getWeight();

		}
		return (normalised && lSum>.98 && lSum<1.02);
	}

	/**
	 * @author W.Pasman
	 * check if this utility space is ready for negotiation.
	 * @param dom is the domain in which nego is taking place
	 * throws if problem occurs.
	 */
	public void checkReadyForNegotiation(String agentName, Domain dom) throws Exception
	{
		// check if utility spaces are instance of the domain
		// following checks normally succeed, as the domain of the domain space is enforced in the loader.
		if (!(dom.equals(domain)))
			throw new Exception("domain of agent "+agentName+"does not match the negotiation domain");
		String err=IsComplete();
		if (err!=null) throw new Exception("utility space '"+ fileName +"' of agent "+agentName+" is incomplete\n"+err);

		// TODO 
		if (!checkTreeNormalization())  throw new Exception("utility space of agent "+agentName+" is not normalized \n(the issue weights do not sum to 1)");
	}

	/**Wouter: I think this should not be used anymore*/
	public final int getNrOfEvaluators() {
		return fEvaluators.size();
	}

	/**
	 * @param index The IDnumber of the Objective or Issue
	 * @return An Evaluator for the Objective or Issue.
	 */
	public final Evaluator getEvaluator(int index) {
		/*   	Issue issue = domain.getIssue(index);
    	return fEvaluators.get(issue);
		 */   	
		Objective obj = domain.getObjective(index); //Used to be Issue in stead of Objective
		if(obj != null){
			return fEvaluators.get(obj);
		}else return null;
	}

	// Utility space should not return domain-related information, should it?
	//    public final int getNumberOfIssues() {
	//        return domain.getNumberOfIssues();
	//    }

	/**
	 * @return whether or not the value returned by getUtility() is the normalized utility or the notNormalize with time effect
	 */
	public boolean isUsingNormalizeUtility() {
		return fUseNormalizedUtility;
	}

	/**
	 * If set to true the returned utility from ge
	 */
	public void setUseNormalizeUtility(boolean value) {
		fUseNormalizedUtility=value;
	}

	/**
	 * @param bid the bid to calculate utility for
	 * @return the utility of bid calculated in the default manner for this space (i.e. normalized/not normalized)
	 * @throws Exception
	 */
	public double getUtility(Bid bid) throws Exception
	{
		if (bid.isOptOut())
			return getOptOutValue(bid.getTime());
		else if (fUseNormalizedUtility)
			return getNormlizedUtility(bid);
		else
			return getUtilityWithTimeEffect(bid);
	}
	/**
	 * update 23oct. If a hard constraint is violated, the utility should be 0.
	 * @return the  normalize utility of bid
	 * @param bid the bid to calculate utility for
	 * @throws Exception
	 */
	public double getNormlizedUtility(Bid bid) throws Exception
	{
		EVALUATORTYPE type;
		double utility = 0, financialUtility = 0, financialRat = 0;
		if(fCheckConstraints == CHECK_CONSTRAINTS.DO_CHECK)
			if (constraintsViolated(bid)) return 0.;

		Objective root = domain.getObjectivesRoot();
		Enumeration<Objective> issueEnum = root.getPreorderIssueEnumeration();
		while(issueEnum.hasMoreElements()){
			Objective is = issueEnum.nextElement();
			Evaluator eval = fEvaluators.get(is);
			type = eval.getType();
			switch(type) {
			case DISCRETE:
			case INTEGER:
			case REAL:
				double weight = eval.getWeight();
				int number = is.getNumber();
				double evaluation = getEvaluation(number, bid);
				utility += weight*evaluation;
				if (log) System.out.println("evaluation("+number+")="+evaluation+"; utility += "+weight+"*"+evaluation+"="+utility);
				break;
			case PRICE:
				financialUtility = getEvaluation(is.getNumber(),bid);
				financialRat = ((EvaluatorPrice)eval).rationalityfactor;
				break;
			}
		}
		return financialRat*financialUtility+(1-financialRat)*utility;
	}

	/** return the unnormalized Utility with the time effect
	 * @param bid - the bid to evaluate
	 * @param time - the current time (notice that this should be in the same unit 
	 * that the time effect operate - usually this means turns)
	 * @author Yinon Oshrat
	 */
	public double getUtilityWithTimeEffect(Bid bid,double time) throws Exception
	{
		if (getTimeEffectValue()==null)
			return getUtilityNotNormalized(bid);
		else
			return getUtilityNotNormalized(bid)+time*getTimeEffectValue();
	}

	/** return the unnormalized Utility with the time effect,
	 * it uses the time stamp contained inside the bid, as this timestamp 
	 * is not always it is advised not to use this method, without a good reason
	 * @param bid - the bid to evaluate
	 * @author Yinon Oshrat
	 */
	public double getUtilityWithTimeEffect(Bid bid) throws Exception
	{
		return getUtilityWithTimeEffect(bid,bid.getTime());
	}
	/** return the unnormalized Utility
	 * the constraints aren't checked, this is a revised version on the getUtilty function 
	 * @param bid - the bid to evaluate
	 * @return the utilty of the bid with out performing normalization
	 * @author Yinon Oshrat
	 * @throws Exception 
	 */
	private double getUtilityNotNormalized(Bid bid) throws Exception {
		EVALUATORTYPE type;
		double utility = 0, financialUtility = 0, financialRat = 0;

		Enumeration<Objective> issueEnum = domain.getObjectivesRoot().getPreorderIssueEnumeration();
		while(issueEnum.hasMoreElements()){
			Objective issue = issueEnum.nextElement();
			Evaluator eval = fEvaluators.get(issue);
			switch(eval.getType()) {
			case DISCRETE:
			case INTEGER:
			case REAL:
				utility += eval.getWeight()*getEvaluationNotNormalized(issue.getNumber(),bid)*fWeightMultiplyer;
				break;
			case PRICE:
				financialUtility = getEvaluationNotNormalized(issue.getNumber(),bid);
				financialRat = ((EvaluatorPrice)eval).rationalityfactor;
				break;
			}
		}
		return financialRat*financialUtility+(1-financialRat)*utility;
	}



	/**
	 * @author W.Pasman
	 * CHeck that the constraints are not violated.
	 * This is an ad-hoc solution, we need structural support 
	 * for constraints. Soft, hard constraints, a constraint space etc.
	 * @param bid the bid to be checked
	 * @return true if the bid violates constraint, else false.
	 */
	public boolean constraintsViolated(Bid bid)
	{
		Double cost=0.;
		try { cost=getCost(bid); } catch (Exception e) 
		{ 
			System.out.println("can not compute cost:"+e.getMessage()+"- assuming constraints violated");
			return true; 
		}
		return cost>1200.;
	}

	/**
	 * gets the utility of one issue in the bid.
	 * @param pIssueIndex
	 * @param bid
	 * @return utility of the specified issue in the given bid
	 */
	public final double getEvaluation(int pIssueIndex, Bid bid) throws Exception {
		if (!bid.hasValue(pIssueIndex))
			return 0; // Y. Oshrat: we have a bid with some missing issue - changed to allow incomplete bids 

		/* hdevos: used to be this: 	
   		Issue lIssue = getDomain().getIssue(pIssueIndex);
    	Evaluator lEvaluator = fEvaluators.get(lIssue);
		 */

		Objective lObj = getDomain().getObjective(pIssueIndex);
		Evaluator lEvaluator = fEvaluators.get(lObj);
		switch(lEvaluator.getType()) {
		case DISCRETE:
			return ((EvaluatorDiscrete)lEvaluator).getEvaluation(this,bid,pIssueIndex);
		case INTEGER:
			return ((EvaluatorInteger)lEvaluator).getEvaluation(this,bid,pIssueIndex);
		case REAL:
			return ((EvaluatorReal)lEvaluator).getEvaluation(this,bid,pIssueIndex);
		case PRICE:
			return ((EvaluatorPrice)lEvaluator).getEvaluation(this,bid,pIssueIndex);
		case OBJECTIVE: 
			return ((EvaluatorObjective)lEvaluator).getEvaluation(this,bid,pIssueIndex);
		default:
			return -1;

		}
	}

	public final double getEvaluation(Objective issue, Value value) throws Exception {
		Evaluator lEvaluator = fEvaluators.get(issue);
		switch(lEvaluator.getType()) {
		case DISCRETE:
			ValueDiscrete valueDiscrete = (ValueDiscrete)value;
			return ((EvaluatorDiscrete)lEvaluator).getEvaluation(valueDiscrete);
		default:
			throw new UnsupportedOperationException("I didn't have time to implement getEvaluation for non-discrete issues");
		}
	}

	/**
	 * gets the utility of one issue in the bid, without normalization
	 * @param number
	 * @param bid
	 * @return
	 * @author
	 * @throws Exception 
	 */
	private double getEvaluationNotNormalized(int pIssueIndex, Bid bid) throws Exception {
		if (bid==null) throw new NullPointerException("bid==null");
		try {
			bid.getValue(pIssueIndex);
		} catch (Exception e) { // Y. Oshrat: we have a bid with some missing issue - changed to allow incomplete bids
			return 0;
		}

		Objective lObj = getDomain().getObjective(pIssueIndex);
		Evaluator lEvaluator = fEvaluators.get(lObj);

		switch(lEvaluator.getType()) {
		case DISCRETE:
			return ((EvaluatorDiscrete)lEvaluator).getEvaluationNotNormalized(this,bid,pIssueIndex);
		case INTEGER:
			return ((EvaluatorInteger)lEvaluator).getEvaluationNotNormalized(this,bid,pIssueIndex);
		case REAL:
			return ((EvaluatorReal)lEvaluator).getEvaluationNotNormalized(this,bid,pIssueIndex);
		case PRICE:
			return ((EvaluatorPrice)lEvaluator).getEvaluationNotNormalized(this,bid,pIssueIndex);
		case OBJECTIVE: 
			return ((EvaluatorObjective)lEvaluator).getEvaluationNotNormalized(this,bid,pIssueIndex);
		default:
			return -1;
		}
	}

	/**
	 * Totally revised, brute-force search now.
	 * @return a bid with the maximum utility value attainable in this util space
	 * @throws Exception if there is no bid at all in this util space.
	 * @author W.Pasman
	 */
	public final Bid getMaxUtilityBid() throws Exception
	{
		if (maxUtilityBidCache==null) {
			maxUtilityCache=0.;
			BidIterator bidit=new BidIterator(domain);

			if (bidit.hasNext()) maxUtilityBidCache=bidit.next();
			else throw new Exception("The domain does not contain any bids!");
			while (bidit.hasNext())
			{
				Bid thisBid=bidit.next();
				double thisutil=getUtility(thisBid);
				if (thisutil>maxUtilityCache) { maxUtilityCache=thisutil; maxUtilityBidCache=thisBid;  }
			}
		}
		return maxUtilityBidCache;
	}

	/**
	 * Totally revised, brute-force search now.
	 * @return a bid with the maximum utility value attainable in this util space
	 * @throws Exception if there is no bid at all in this util space.
	 * @author W.Pasman
	 */
	public final double getMaxUtility() throws Exception
	{
		if (maxUtilityCache==null) 
			getMaxUtilityBid();
		return maxUtilityCache;
	}

	private final SimpleElement loadTreeFromReader(Reader reader) throws IOException {
		SimpleElement root = new SimpleDOMParser().parse(reader);
		fXMLRoot = root;
		//load reservation value
		try {
			if((root.getChildByTagName("reservation")!=null)&&(root.getChildByTagName("reservation").length>0)){
				SimpleElement xml_reservation = (SimpleElement)(root.getChildByTagName("reservation")[0]);
				fReservationValue = Double.valueOf(xml_reservation.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no reservation value");
		}
		// Yinon Oshrat 27/10/09 : load opt-out value
		try {
			if((root.getChildByTagName("optout")!=null)&&(root.getChildByTagName("optout").length>0)){
				SimpleElement xml_optout = (SimpleElement)(root.getChildByTagName("optout")[0]);
				fOptOutValue = Double.valueOf(xml_optout.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no optout value");
		}
		// Yinon Oshrat 27/10/09 : load time effect value
		try {
			if((root.getChildByTagName("timeeffect")!=null)&&(root.getChildByTagName("timeeffect").length>0)){
				SimpleElement xml_timeeffect = (SimpleElement)(root.getChildByTagName("timeeffect")[0]);
				fTimeAffectValue= Double.valueOf(xml_timeeffect.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no optout value");
		}
		// Yinon Oshrat 16/11/09 : load weight multiplier
		try {
			if((root.getChildByTagName("weightmultiplyer")!=null)&&(root.getChildByTagName("weightmultiplyer").length>0)){
				SimpleElement xml_weightmultiplier = (SimpleElement)(root.getChildByTagName("weightmultiplyer")[0]);
				fWeightMultiplyer = Double.valueOf(xml_weightmultiplier.getAttribute("value"));
			}
		} catch (Exception e) {
			System.out.println("Utility space has no Weight Multiplyer");
		}	

		return root;
	}

	/**
	 * @author Herbert. Modified Wouter.
	 * @param filename The name of the xml file to parse.
	 * @throws IOException if error occurs, e.g. file not found
	 */
	private final boolean loadTreeFromFile(String filename) throws IOException 
	{        
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
		SimpleElement root = loadTreeFromReader(reader);

		// Load opponent utility spaces
		try {

			// Get the parent folder
			File parentFile = new File(filename);
			String parentFolder = parentFile.getParent();

			SimpleElement[] xmlOpponentsTag = root.getChildByTagName("opponent");

			if (xmlOpponentsTag.length > 0) {
				if (xmlOpponentsTag.length != 1)
					new Warning("Too much opponent tags");

				// Get the opponents
				SimpleElement[] xmlSpaces = xmlOpponentsTag[0].getChildByTagName("utilityspace");

				for(SimpleElement spaceElement : xmlSpaces){
					// Get the absolute path of the opponent file
					// NOTE: only the filename part of the opponent path is used.
					URL oppURL = new URL(spaceElement.getAttribute("file"));
					File oppRelativeFile = new File(oppURL.getFile());
					File oppFile = new File(parentFolder, oppRelativeFile.getName());

					// Create the opponent UtilitySpace
					UtilitySpace opp = new UtilitySpace(domain, oppFile.getAbsolutePath());
					opponentUtiltySpaces.add(opp);
				}
			}
		} catch (Exception e) {
			System.err.println("loadTreeFromFile: Error on load opponent utility: " + e);
			e.printStackTrace();
		}	

		return loadTreeRecursive(root);
	}


	/**
	 * @author hdevos
	 * Loads the weights and issues for the evaluators.
	 * @param root The current root of the XML structure.
	 */
	private final boolean loadTreeRecursive(SimpleElement currentRoot){
		//TODO hdevos:
		//We get an Objective or issue from the SimpleElement structure,
		//get it's number of children:
		int nrOfWeights = 0;
		/*		String what = currentRoot.getTagName();
		if(!what.equals("Objective") || !what.equals("utility_space")){ //are the only two tags that can have weights
			loadTreeRecursive((SimpleElement)(currentRoot.getChildElements())[0]); //It's the utility_space tag. Ignore.
		}
		 */		
		//TODO hdevos: find a way of checking the number of issues in the Domain versus the number of issues in the UtilitySpace

		int index;
		double weightsSum = 0;


		Vector<Evaluator> tmpEvaluator = new Vector<Evaluator>(); //tmp vector with all Evaluators at this level. Used to normalize weigths.
		EVALUATORTYPE evalType;
		String type, etype;
		Evaluator lEvaluator=null;
		int indexEvalPrice=-1;

		//Get the weights of the current children
		Object[] xml_weights = currentRoot.getChildByTagName("weight");
		nrOfWeights = xml_weights.length; //assuming each 
		HashMap<Integer, Double> tmpWeights = new HashMap<Integer, Double>();
		//System.out.println("nrOfWeights = " + nrOfWeights);
		for(int i = 0; i < nrOfWeights; i++){
			index = Integer.valueOf(((SimpleElement)xml_weights[i]).getAttribute("index"));
			double dval = Double.valueOf( ((SimpleElement)xml_weights[i]).getAttribute("value"));
			Integer indInt = new Integer(index);
			Double valueDouble = new Double(dval);
			tmpWeights.put(indInt, valueDouble);
			weightsSum += tmpWeights.get(index); // For normalization purposes on this level. See below.
		}

		//      Collect evaluations for each of the issue values from file.
		// Assumption: Discrete-valued issues.
		Object[] xml_issues = currentRoot.getChildByTagName("issue");
		Object[] xml_objectives = currentRoot.getChildByTagName("objective");
		/*if (xml_issues.length==0 && xml_objectives.length==0) { not an error if we are in the middle of recursion
        	throw new RuntimeException("No objectives and no issues in file "+this.fileName);
        }*/
		Object[] xml_obj_issues = new Object[xml_issues.length + xml_objectives.length];
		int i_ind;
		for(i_ind = 0; i_ind < xml_issues.length; i_ind++){
			//System.out.println("issues_index: " + i_ind + " vs length:" + xml_issues.length +" to fill something of lenght: "+ xml_obj_issues.length);
			xml_obj_issues[i_ind] = xml_issues[i_ind];
		}
		/*     for(int o_ind = i_ind; o_ind < xml_obj_issues.length; o_ind++){ 
        	System.out.println("objectives_index: " + o_ind + " vs length:" + xml_objectives.length +" to fill something of lenght: "+ xml_obj_issues.length);
        	xml_obj_issues[o_ind] = xml_objectives[o_ind];
        }
		 */     for(int o_ind = 0; (o_ind + i_ind) < xml_obj_issues.length; o_ind++){ 
			 //System.out.println("objectives_index: " + o_ind + " vs length:" + xml_objectives.length +" to fill something of lenght: "+ xml_obj_issues.length);
			 xml_obj_issues[(o_ind + i_ind) ] = xml_objectives[o_ind];
		 }  
		 //        boolean issueWithCost = false;
		 //        double[] cost;
		 for(int i=0;i<xml_obj_issues.length;i++) {
			 index = Integer.valueOf(((SimpleElement)xml_obj_issues[i]).getAttribute("index"));
			 type = ((SimpleElement)xml_obj_issues[i]).getAttribute("type");
			 etype = ((SimpleElement)xml_obj_issues[i]).getAttribute("etype");
			 if (type==null) { // No value type specified.
				 new Warning("Evaluator type not specified in utility template file.");
				 // TODO: Define exception.
				 evalType = EVALUATORTYPE.DISCRETE;
			 }
			 else if (type.equals(etype)) {
				 evalType = EVALUATORTYPE.convertToType(type);
			 } else if (etype!=null && type==null) {
				 evalType = EVALUATORTYPE.convertToType(etype);
			 } else if (type!=null && etype==null) { // Used label "type" instead of label "vtype".
				 evalType = EVALUATORTYPE.convertToType(type);
			 } else {
				 System.out.println("Conflicting value types specified for evaluators in utility template file.");
				 // TODO: Define exception.
				 // For now: use "type" label.
				 evalType = EVALUATORTYPE.convertToType(type);
			 }
			 if(tmpWeights.get(index) != null){
				 switch(evalType) {
				 case DISCRETE:
					 lEvaluator = new EvaluatorDiscrete();
					 break;
				 case INTEGER:
					 lEvaluator = new EvaluatorInteger();
					 break;
				 case REAL:
					 lEvaluator = new EvaluatorReal();
					 break;
				 case PRICE:
					 if (indexEvalPrice>-1)
						 System.out.println("Multiple price evaluators in utility template file!");
					 // TODO: Define exception.
					 indexEvalPrice = index-1;
					 lEvaluator = new EvaluatorPrice();
					 break;
				 case OBJECTIVE:
					 lEvaluator = new EvaluatorObjective();		
					 break;
				 }
				 lEvaluator.loadFromXML((SimpleElement)(xml_obj_issues[i]));
				 // TODO: put lEvaluator to an array (done?)
				 //evaluations.add(tmp_evaluations);

				 try{
					 fEvaluators.put(getDomain().getObjective(index),lEvaluator); //Here we get the Objective or Issue.
				 }catch(Exception e){
					 System.out.println("Domain-utilityspace mismatch");
					 e.printStackTrace();
					 return false;
				 }
			 }
			 try{
				 if(nrOfWeights != 0){
					 Integer indexInt = new Integer(index);
					 //System.out.println("Hashcode here is: " + indexInt.hashCode());
					 double tmpdwt = tmpWeights.get(indexInt).doubleValue();
					 Objective tmpob = getDomain().getObjective(index);
					 fEvaluators.get(tmpob).setWeight(tmpdwt);
					 //fEvaluators.get(getDomain().getObjective(index)).setWeight(tmpWeights.get(index).doubleValue());
					 //System.out.println("set weight to " + tmpdwt);
				 }
			 }catch(Exception e){
				 System.out.println("Evaluator-weight mismatch or no weight for this issue or objective.");
			 }
			 tmpEvaluator.add(lEvaluator); //for normalisation purposes.
		 }
		 //Normalize weights if sum of weights exceeds 1.
		 // Do not include weight for price evaluator! This weight represents "financial rationality factor".
		 // TODO: Always normalize weights to 1??
		 if (indexEvalPrice!=-1) {
			 weightsSum -= tmpWeights.get(indexEvalPrice); //FIXME? hdv: -1 is an invalid index. So.. what gives? Why is it -1 in the original program?
		 }
		 /* if (weightsSum>1.0) { // Only normalize if sum of weights exceeds 1.
        	for (int i=0;i<nrOfWeights;i++) {
        		if (i!=indexEvalPrice) {
        			tmpEvaluator.elementAt(i).setWeight(tmpEvaluator.elementAt(i).getWeight()/weightsSum); 
        		}
        	}
        }
		  */ 

		 //Recurse over all children:
		 boolean returnval = false;
		 Object[] objArray = currentRoot.getChildElements();
		 for(int i = 0; i < objArray.length ; i++ )
			 returnval = loadTreeRecursive((SimpleElement)objArray[i]);
		 return returnval;
	}


	/**
	 * Get the weight of specific issue
	 * @param issueID The Issue or Objective to get the weight from
	 * @return The weight, or -1 if the objective doesn't exist.
	 */
	public double getWeight(int issueID) {
		//return weights[issuesIndex]; //old
		//TODO geeft -1.0 terug als de weight of de eveluator niet bestaat.
		Objective ob = domain.getObjective(issueID);
		if(ob != null){
			//	System.out.println("Obje index "+ issueID +" != null");
			Evaluator ev = fEvaluators.get(ob);
			if(ev != null){
				//System.out.println("Weight " + issueID + " should be " + ev.getWeight());
				return ev.getWeight();
			}
		}
		else
			System.out.println("Obje "+ issueID +" == null");
		return 0.0; //fallthrough.
	}

	public double setWeightSimple(Objective tmpObj, double wt){
		try{
			Evaluator ev = fEvaluators.get(tmpObj);
			ev.setWeight(wt); //set weight
		}catch(NullPointerException npe){
			return -1;
		}
		return wt;
	}



	public double setWeight(Objective tmpObj, double wt){
		try{
			Evaluator ev = fEvaluators.get(tmpObj);
			double oldWt = ev.getWeight();
			if(!ev.weightLocked()){
				ev.setWeight(wt); //set weight
			}
			this.normalizeChildren(tmpObj.getParent());
			if(this.checkTreeNormalization()){
				return fEvaluators.get(tmpObj).getWeight();
			}else{
				ev.setWeight(oldWt); //set the old weight back.
				return fEvaluators.get(tmpObj).getWeight();
			}
		}catch(NullPointerException npe){
			return -1;
		}

	}

	/**
	 * @depricated Use getObjective
	 * 
	 * @param index The index of the issue to 
	 * @return the indexed objective or issue
	 */
	public final Objective getIssue(int index) {
		return domain.getIssue(index);
	}

	/**
	 * Returns the Objective or Issue at that index
	 * @param index The index of the Objective or Issue.
	 * @return An Objective or Issue.
	 */
	public final Objective getObjective(int index){
		return domain.getObjective(index);
	}

	public final Domain getDomain() {
		return domain;
	}

	/**
	 * Adds an evaluator to an objective or issue
	 * @param obj The Objective or Issue to attach an Evaluator to.
	 * @return The new Evaluator.
	 */
	public final Evaluator addEvaluator(Objective obj){
		Evaluator ev = null;
		ISSUETYPE etype = obj.getType();
		switch(etype){
		case INTEGER:
			ev = new EvaluatorInteger();
			break;
		case REAL:
			ev = new EvaluatorReal();
			break;
		case DISCRETE:
			ev = new EvaluatorDiscrete();
			break;
		case OBJECTIVE:
			ev = new EvaluatorObjective();
			break;
			/*	case PRICE:
    		ev = new EvaluatorPrice();
    		break;
			 */		
		}
		fEvaluators.put(obj, ev);
		return ev;
	}

	/**
	 * Sets an <Objective, evaluator> pair. Replaces old evaluator for objective
	 * @param obj The Objective to attach an Evaluator to.
	 * @param ev The Evaluator to attach.
	 * @return the given evaluator Wouter: what's the use of the return value???
	 */
	public final Evaluator addEvaluator(Objective obj, Evaluator ev){
		fEvaluators.put(obj, ev); // replaces old value for that object-key if key already existed.
		return ev;
	}

	/**
	 * @return The set with all pairs of evaluators and objectives in this utilityspace.
	 */
	public final Set<Map.Entry<Objective, Evaluator> >getEvaluators(){
		return fEvaluators.entrySet(); 
	}

	/**
	 * Place a lock on the weight of an objective or issue.
	 * @param obj The objective or issue that is about to have it's weight locked.
	 * @return <code>true</code> if succesfull, <code>false</code> If the objective doesn't have an evaluator yet.
	 */
	public final boolean lock(Objective obj){
		try{
			fEvaluators.get(obj).lockWeight();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Clear a lock on the weight of an objective or issue.
	 * @param obj The objective or issue that is having it's lock cleared.
	 * @return <code>true</code> If the lock is cleared, <code>false</code> if the objective or issue doesn't have an evaluator yet.
	 */
	public final boolean unlock(Objective obj){
		try{
			fEvaluators.get(obj).unlockWeight();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;    	
	}

	public final Set<Map.Entry<Objective,Evaluator>> normalizeChildren(Objective obj){
		Enumeration<Objective> childs = obj.children();
		double RENORMALCORR=0.05; // we add this to all weight sliders to solve the slider-stuck-at-0 problem.
		double weightSum = 0;
		double lockedWeightSum = 0;
		int freeCount = 0;
		int lockedCount = 0;
		while(childs.hasMoreElements()){
			Objective tmpObj = childs.nextElement();
			try{
				if(!fEvaluators.get(tmpObj).weightLocked()){
					weightSum += fEvaluators.get(tmpObj).getWeight();
					freeCount++;
				}else{
					lockedWeightSum += fEvaluators.get(tmpObj).getWeight();
					lockedCount++;
				}
			}catch(Exception e){

				//do nothing, we can encounter Objectives/issues without Evaluators.
			}
		}
		System.out.println("freeCount + lockedCount = " + freeCount + " + " + lockedCount);
		if(freeCount + lockedCount == 1){
			System.out.println("At least the IF works...");
			Enumeration<Objective> singleChild = obj.children();
			while(singleChild.hasMoreElements()) {
				Objective tmpObj = singleChild.nextElement();
				fEvaluators.get(tmpObj).setWeight(1.0);
			}
		}

		//Wouter: cleaned up the test...
		//if(/*weightSum + lockedWeightSum != 1.0 && */(lockedCount +1) < (freeCount + lockedCount) /*&& weightSum + lockedWeightSum != 0.0*/ ){ //that second bit to ensure that there is no problem with
		if( freeCount >1){
			Enumeration<Objective> normalChilds = obj.children();
			while(normalChilds.hasMoreElements()){
				Objective tmpObj = normalChilds.nextElement();
				double diff = (lockedWeightSum + weightSum) - 1.0 ;
				// because of RENORMALCORR, total weight will get larger.
				double correctedWeightSum=weightSum+RENORMALCORR*freeCount;
				try{

					if(!fEvaluators.get(tmpObj).weightLocked()){
						double currentWeight = fEvaluators.get(tmpObj).getWeight();
						double newWeight = currentWeight -(diff* (currentWeight+RENORMALCORR)/correctedWeightSum);
						if(newWeight < 0){
							newWeight = 0; //FIXME hdv: could this become 0? Unsure of that.
						}
						fEvaluators.get(tmpObj).setWeight(newWeight);
						System.out.println("new Weight of " + tmpObj.getName() + " is " + newWeight);
					}
				}catch(Exception e){
					// do nothing, we can encounter Objectives/issues without Evaluators.
				}

			}

		}

		return getEvaluators();
	}

	public final Set<Map.Entry<Objective,Evaluator> > modifyWeight(Objective obj, double wt)
	{
		if(fEvaluators.get(obj).weightLocked() || wt > 1.0){
			return getEvaluators();
		}else{
			fEvaluators.get(obj).setWeight(wt);
			return normalizeChildren(obj.getParent());
		}
	}

	public boolean removeEvaluator(Objective obj){
		try{
			fEvaluators.remove(obj);

		}catch(Exception e){
			return false;
		}
		return true;
	}

	/**
	 * Creates an xml representation (in the form of a SimpleElements) of the utilityspace.
	 * @return A representation of this utilityspace or <code>null</code> when there was an error.
	 */ 
	public SimpleElement toXML(){
		SimpleElement root = (domain.getObjectivesRoot()).toXML(); // convert the domain. 
		root = toXMLrecurse(root);
		SimpleElement rootWrapper = new SimpleElement("utility_space"); 
		//can't really say overhere how many issues there are inhere.
		// Wouter: huh??? Just count them??
		rootWrapper.addChildElement(root);
		// Yinon Oshrat 27/10/09 :write reservation value, opt-out value, and time effect
		if (fReservationValue!=null) {
			SimpleElement reservationElement = new SimpleElement("reservation");
			reservationElement.setAttribute("value",fReservationValue.toString());
			rootWrapper.addChildElement(reservationElement);
		}
		if (fOptOutValue!=null) {
			SimpleElement optoutElement = new SimpleElement("optout");
			optoutElement.setAttribute("value",fOptOutValue.toString());
			rootWrapper.addChildElement(optoutElement);
		}
		if (fTimeAffectValue!=null) {
			SimpleElement timeeffectElement = new SimpleElement("timeeffect");
			timeeffectElement.setAttribute("value",fTimeAffectValue.toString());
			rootWrapper.addChildElement(timeeffectElement);
		}
		if (fWeightMultiplyer!=null) {
			SimpleElement weightmultiplyerElement = new SimpleElement("weightmultiplyer");
			weightmultiplyerElement.setAttribute("value",fWeightMultiplyer.toString());
			rootWrapper.addChildElement(weightmultiplyerElement);
		}
		return rootWrapper;//but how to get the correct values in place?
	}

	/**
	 * Creates an html list representation of the utilityspace.
	 * @param withWeightedUtilities if true, write near each value its weighted utility.
	 * @return A representation of this utilityspace or <code>null</code> when there was an error.
	 * @see Issue#getPossibleValues()
	 */ 
	public String toHTML(boolean withWeightedUtilities) {
		StringBuffer html=new StringBuffer();
		for (Objective issue: objectivesOrderdByWeight()) {
			Evaluator e = fEvaluators.get(issue);
			html.append("<li>")
			.append(issue.getName())
			.append(": ")
			.append(valuesToHTML(issue,withWeightedUtilities))
			.append("</li>\n");
		}
		return html.toString();
	}

	public String valuesToHTML(Objective issue, boolean withWeightedUtilities) {
		Evaluator e = fEvaluators.get(issue);
		return 
				(e instanceof EvaluatorDiscrete? 
						((EvaluatorDiscrete)e).valuesOrderedByUtilityStrings(withWeightedUtilities, fWeightMultiplyer):
							e.toString()).toString();
	}

	public String valueToHTML(Objective issue, Value value, boolean withWeightedUtilities) {
		Evaluator e = fEvaluators.get(issue);
		return 
				(e instanceof EvaluatorDiscrete? 
						((EvaluatorDiscrete)e).valueString(value, withWeightedUtilities, fWeightMultiplyer):
							value.toString());
	}

	public String toHTML() {
		return toHTML(false);
	}

	/**
	 * Wouter: I assume this adds the utilities (weights and cost) from this utility space
	 * to a given domain. It modifies the currentLevel so the return value is superfluous.
	 * @param currentLevel is pointer to a XML tree describing the domain.
	 * @return XML tree with the weights and cost set. NOTE: currentLevel is modified anyway.
	 */
	private SimpleElement toXMLrecurse(SimpleElement currentLevel){
		//go through all tags.

		// update the objective fields.
		Object[] Objectives = currentLevel.getChildByTagName("objective");
		//Object[] childWeights = currentLevel.getChildByTagName("weight");
		// Wou;ter: again, domain has no weights.

		for(int objInd=0; objInd<Objectives.length;objInd++){
			SimpleElement currentChild = (SimpleElement)Objectives[objInd];
			int childIndex = Integer.valueOf(currentChild.getAttribute("index"));
			try{
				Evaluator ev = fEvaluators.get(domain.getObjective(childIndex));
				// Wouter: nasty, they dont check whether object actually has weight.
				// they account on an exception being thrown in dthat case....
				SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", ""+childIndex);
				currentChildWeight.setAttribute("value", ""+ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);	
			}catch(Exception e){
				//do nothing, not every node has an evaluator. 
			}	
			currentChild = toXMLrecurse(currentChild);
		}

		// update the issue fields.
		Object[] Issues = currentLevel.getChildByTagName("issue");
		//Object[] IssueWeights = currentLevel.getChildByTagName("weight"); 
		// Wouter: huh, domain has no weights!!!

		for(int issInd=0; issInd<Issues.length; issInd++){
			SimpleElement issueL = (SimpleElement) Issues[issInd];

			//set the weight
			int childIndex = Integer.valueOf(issueL.getAttribute("index"));
			Objective tmpEvObj = domain.getObjective(childIndex);
			try{

				Evaluator ev = fEvaluators.get(tmpEvObj);

				SimpleElement currentChildWeight = new SimpleElement("weight");
				currentChildWeight.setAttribute("index", ""+childIndex);
				currentChildWeight.setAttribute("value", ""+ev.getWeight());
				currentLevel.addChildElement(currentChildWeight);

				String evtype_str = issueL.getAttribute("etype");
				EVALUATORTYPE evtype = EVALUATORTYPE.convertToType(evtype_str);
				switch(evtype){
				case DISCRETE:
					//fill this issue with the relevant weights to items.
					Object[] items = issueL.getChildByTagName("item");
					for(int itemInd = 0; itemInd < items.length; itemInd++){
						//SimpleElement tmpItem = (SimpleElement) items[itemInd];
						IssueDiscrete theIssue = (IssueDiscrete)domain.getObjective(childIndex);

						EvaluatorDiscrete dev = (EvaluatorDiscrete) ev;
						Integer eval = dev.getValue(theIssue.getValue(itemInd));
						((SimpleElement)items[itemInd]).setAttribute("evaluation", ""+eval);

						Double cost = dev.getCost(theIssue.getValue(itemInd));
						if (cost!=null) ((SimpleElement)items[itemInd]).setAttribute("cost", ""+cost);

						//String desc = dev.getDesc(theIssue.getValue(itemInd));
						//if (desc!=null) tmpItem.setAttribute("description", ""+desc);
					}
					break;
				case INTEGER:
					Object[] Ranges = issueL.getChildByTagName("range");
					SimpleElement thisRange = (SimpleElement)Ranges[0];
					EvaluatorInteger iev = (EvaluatorInteger) ev;
					thisRange.setAttribute("lowerbound", ""+iev.getLowerBound());
					thisRange.setAttribute("upperbound", ""+iev.getUpperBound());
					SimpleElement thisIntEval = new SimpleElement("evaluator");
					EVALFUNCTYPE ievtype = iev.getFuncType();
					if(ievtype == EVALFUNCTYPE.LINEAR){
						thisIntEval.setAttribute("ftype", "linear");
						thisIntEval.setAttribute("parameter1", ""+iev.getLinearParam());
					}else if(ievtype == EVALFUNCTYPE.CONSTANT){
						thisIntEval.setAttribute("ftype", "constant");
						thisIntEval.setAttribute("parameter0", ""+iev.getConstantParam());
					}
					issueL.addChildElement(thisIntEval);
					//TODO hdv We need an new simpleElement here that contains the evaluator and it's ftype. 
					break;
				case REAL:
					Object[] RealRanges = issueL.getChildByTagName("range");
					EvaluatorReal rev = (EvaluatorReal) ev;
					SimpleElement thisRealEval = new SimpleElement("evaluator");
					EVALFUNCTYPE revtype = rev.getFuncType();
					if(revtype == EVALFUNCTYPE.LINEAR){
						thisRealEval.setAttribute("ftype", "linear");
						thisRealEval.setAttribute("parameter1", ""+rev.getLinearParam());
					}else if(revtype == EVALFUNCTYPE.CONSTANT){
						thisRealEval.setAttribute("ftype", "constant");
						thisRealEval.setAttribute("parameter0", ""+rev.getConstantParam());
					}
					issueL.addChildElement(thisRealEval);    				
					//TODO hdv the same thing as above vor the "evaluator" tag.
					break;
				}
			}catch(Exception e){
				//do nothing, it could be that this objective/issue doesn't have an evaluator yet.
			}	

		}

		return currentLevel;
	}

	/**
	 * Wouter: this function *should* check that the domainSubtreeP is a subtree of the utilSubtreeP, 
	 * and that all leaf nodes are complete.
	 * However currently we only check that all the leaf nodes are complete,
	 * @author W.Pasman
	 * @return null if util space is complete, else returns string containging explanation why not.
	 */
	public String IsComplete() 
	// Oh damn, problem, we don't have the domain template here anymore.
	// so how can we check domain compativility?
	// only we can check that all fields are filled.........
	{ 
		ArrayList<Issue> issues=domain.getIssues();
		if (issues==null) return "Utility space is not complete, in fact it is empty!";
		String mess;
		for (Issue issue:issues) 
		{
			Evaluator ev=getEvaluator(issue.getNumber());
			if (ev==null) return "issue "+issue.getName()+" has no evaluator";
			mess= (ev.isComplete(issue));
			if (mess!=null) return mess;
		}
		return null;
	}


	/**
	 * as we don't have the domain tree we can't do the check as we hoped to do .
	 * @param utilSubtreeP
	 * @param domainSubtreeP
	 * @return  Stringg containing explanation why not a subtree, or null.
	 * @author W.Pasman
    String IsSubtreeAndComplete(Objective utilSubtreeP,Objective domainSubtreeP)
    {
    	if (utilSubtreeP.isLeaf() && domainSubtreeP.isLeaf())
    	{
    		// check the evaluator at the utilSubtree, whether it agrees with the domain description and is complete
    		// if it is a leaf, it is an Issue and there should be an evaluator.
    		// note, the non-leaf nodes do not need an evaluator.
    		blabla
    	}
    	else
    	{
    			// check all objectives in the domain. 
    		for (Objective domSpaceObj:domainSubtreeP.getChildren())
    		{
    			 // get child from utilSubtreeP that has same ID. These should match.
    			 // we do this because order of children may differ.
    			Objective matchingUtilSpaceObj=utilSubtreeP.getChildWithID(domSpaceObj.getNumber());
    			if (matchingUtilSpaceObj==null)
    				return "The utility space has no objective matching domainspace object "+domSpaceObj.getName();
    			String checksubtrees=IsSubtreeAndComplete(matchingUtilSpaceObj,domSpaceObj);
    			if (checksubtrees!=null) return checksubtrees;
    		}
    		 // strictly it don't matter if there is more in the util space under this node, but 
    		 // lets give a warning...
    		if (utilSubtreeP.getChildren().size()<domainSubtreeP.getChildren().size())
    			//Wouter: need to make this a messagebox or so, how did that work??
    			System.out.println("WARNING: utility space has more objectives than the domain space under the node"+
    					domainSubtreeP.getName());
    	}
    	return null;
    }
	 */

	/**
	 * compute the cost of the given bid. 
	 * There is also getCost in Evaluator but it currently only works for EvaluatorDiscrete.
	 * Need more clarity on how to deal with this.
	 * For instance, one could argue that the evaluator for the root object
	 * should be able to compute cost of the entire bid.
	 * @throws if cost can not be computed for some reason.
	 * @return computed cost
	 * @author W.Pasman
	 */
	public Double getCost(Bid bid) throws Exception
	{
		Double totalCost=0.0;
		if (bid != null) {
			Double costofissue = 0.0;
			for (Issue issue: domain.getIssues())
			{
				int ID=issue.getNumber();
				if (bid.hasValue(ID)) {
					try {costofissue=getEvaluator(ID).getCost(this, bid, ID);}
					catch (Exception e) { 
						new Warning("getcost:"+e.getMessage()+". using 0",false,1);
					}
				}
				totalCost += costofissue;;
			}
		}
		return totalCost;
	}

	public void showStatistics()
	{
		for (Objective obj: fEvaluators.keySet())
		{
			System.out.print("Objective "+obj.getName()+" ");
			fEvaluators.get(obj).showStatistics();
		}
	}

	public Double getTimeEffectValue() {
		return fTimeAffectValue;
	}

	public void setTimeEffectValue(Double value) {
		fTimeAffectValue=value;
	}

	public Double getOptOutValue(double time) {
		Double timeEffect = getTimeEffectValue();
		if (timeEffect==null)
			timeEffect = 0.0;
		return fOptOutValue+time*timeEffect;
	}

	public void setOptOutValue(Double value) {
		fOptOutValue=value;
	}

	public Double getReservationValue() {
		return fReservationValue;
	}


	/**
	 * @param time
	 * @return the utility value at time-out.
	 * @author Erel Segal
	 * @since 2012-02-28
	 */
	public Double getReservationValue(double time) {  
		Double timeEffect = getTimeEffectValue();
		if (timeEffect==null)
			timeEffect = 0.0;
		return fReservationValue+time*timeEffect;
	}

	public void setReservationValue(Double value) {
		fReservationValue=value;
	}

	public Double getWeightMultiplyer() {
		return fWeightMultiplyer;
	}

	public void setWeightMultiplyer(Double value) {
		fWeightMultiplyer=value;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the side name of this utility space, e.g. "employer" or "candidate".
	 */
	public String getSideName() {
		return domain.getSideName(getFileName());
	}

	/**
	 * @return the personality of this utility space, e.g. "compromise" or "short-term".
	 */
	public String getPersonality() {
		return domain.getPersonality(getFileName());
	}


	public String getName() {
		if (fName.length()>0)
			return fName;
		else
			return fileName.substring(fileName.lastIndexOf('/')+1,fileName.lastIndexOf('.'));
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (!(obj instanceof UtilitySpace)) return false;
		UtilitySpace obj2 = (UtilitySpace)obj;
		//check domains
		if(!domain.equals(obj2.getDomain())) return false;
		//check evaluators
		for(Entry<Objective, Evaluator> entry : fEvaluators.entrySet()) {
			Evaluator eval2 = obj2.getEvaluator(entry.getKey().getNumber());
			if(!entry.getValue().equals(eval2)) return false;
		}
		return true;
	}


	/**
	 * 
	 * @return -The number of possible utility spaces of the opponent that are held by this object
	 */
	public int getNumOpponentUtiltySpaces() { 
		return opponentUtiltySpaces.size(); 

	}
	/**
	 * 
	 * @param index - the number of the utility space to return
	 * @return - possible opponent's utility space with the given index 
	 */
	public UtilitySpace getOpponentUtilitySpace(int index) { 
		return opponentUtiltySpaces.get(index); 
	}

	/**
	 * insert the given UtilitySpace into this object
	 * @param us
	 * @throws NegotiatorException - if the domain of the given UtiltySpace differ from 
	 * the domain of UtilitySpaces already in this object
	 */
	public void addOpponentUtilitySpace(UtilitySpace us) throws NegotiatorException {
		if (opponentUtiltySpaces.size()>0 && !opponentUtiltySpaces.get(0).getDomain().equals(us.getDomain()))
			throw new NegotiatorException("The UtilitySpace has different domain then the ones already in the WorldInformation"); 
		opponentUtiltySpaces.add(us);
	}

	/**
	 * @return the list of objectives, ordered from the most important (highest weight) to the least important.
	 * @author Erel Segal
	 */
	public List<Objective> objectivesOrderdByWeight() {
		List<Objective> list = new ArrayList<Objective>();
		list.addAll(this.fEvaluators.keySet());
		Collections.sort(list, new Comparator<Objective>() {
			@Override public int compare(Objective arg0, Objective arg1) {
				return -Double.compare(fEvaluators.get(arg0).getWeight(), fEvaluators.get(arg1).getWeight());
			}
		});
		return list;
	}

	/**
	 * Demo program for testing only
	 * @author Erel Segal
	 * @date 2011-02-09
	 * 
	 * @param args[0] a path to an xml file holding domain info
	 * @param args[1] a path to an xml file holding utility info
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		Domain domain = new Domain(args[0]);
		UtilitySpace uspace = new UtilitySpace(domain, args[1]);
		System.out.println("\n\nThis is the utility space: \n"+uspace.toXML());
		System.out.println("\n\nThis is it in HTML format: \n"+uspace.toHTML());
		System.out.println("\n\nThis is it in HTML format with utils: \n"+uspace.toHTML(true));

		// To calcualte utility of bid:
		//   uspace.getUtility(bid);
	}
}
