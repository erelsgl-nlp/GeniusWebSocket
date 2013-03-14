package negotiator.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Global;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 * This class calculates the following characteristics of the negotiation space:
 * - Pareto-efficient frontier;
 * - Nash product optimal outcome;
 * - Kalai-Smorodinsky optimal outcome.
 * 
 * 
 * @author Dmytro Tykhonov
 *   FIXME Write analysis to a separete file!!!
 */
public class Analysis {
	private Bid fNashProduct;
	private ArrayList<Bid> fPareto;
	private Bid fKalaiSmorodinsky;
	private SimpleElement fRoot;  
	private NegotiationTemplate fNegotiationTemplate;
	private ArrayList<Bid> fCompleteSpace=null;
	private long fHashCode;
	
	/**
	 * Create Analysis object for the negotiation template and calculate Pareto, Nash, Kalai
	 * 
	 * @param pTemplate - the template with domain and utilities
	 * 
	 */
	public Analysis(NegotiationTemplate pTemplate) {		
		fNegotiationTemplate = pTemplate;		
		fPareto = new ArrayList<Bid>();		
		buildParetoFrontier();
		try {
			calculateKalaiSmorodinsky();
			calculateNash();
		} catch (AnalysisException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		if(getTotalNumberOfBids()<100000) buildCompleteOutcomeSpace();
		fRoot.setAttribute("hash_code", String.valueOf(getHashCodeFromTemplate(pTemplate)));
		
	}
	/**
	 * Load the analysis data from pRoot file (from cache). 
	 * 
	 * @param pTemplate
	 * @param pRoot
	 */
	public Analysis(NegotiationTemplate pTemplate, SimpleElement pRoot) {
		fNegotiationTemplate = pTemplate;
		fRoot = pRoot;
		fPareto = new ArrayList<Bid>();
		loadFromXML(fRoot);
		if(getTotalNumberOfBids()<100000) buildCompleteOutcomeSpace();		
	}
	/**
	 * Use this method to get an instance of Analysis. The method check the cache for existing analysis.
	 * Otherwise, calculate all optimality criteria.
	 * 
	 * @param pTemplate - the template for which the analysis has to be made/loaded
	 * @return
	 */
	public static Analysis getInstance(NegotiationTemplate pTemplate) {
		Analysis lAnalysis;
		//check if we have the analysis in the cache
		String lCacheFileName = pTemplate.getAgentAUtilitySpaceFileName()+"_" + pTemplate.getAgentAUtilitySpaceFileName()+".xml";
		if((new File(lCacheFileName)).exists()) {
			//check if the hash code of the utility space is the same as in the cache
			SimpleDOMParser parser = new SimpleDOMParser();
			SimpleElement lRoot =null;
			try {
				BufferedReader file = new BufferedReader(new FileReader(new File(lCacheFileName)));
				lRoot = (new SimpleDOMParser()).parse(file);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			lAnalysis = new Analysis(pTemplate, lRoot);			
			if(lAnalysis.getHashCode()!=getHashCodeFromTemplate(pTemplate))
				lAnalysis = new Analysis(pTemplate);
		} else { 		
			lAnalysis = new Analysis(pTemplate);
		}
		return lAnalysis;
	}
	/**
	 * 
	 * Calculate the hash code of the negotiation template.
	 * 
	 * @param pTemplate
	 * @return
	 */
	private static long getHashCodeFromTemplate(NegotiationTemplate pTemplate) {
		long lCode=0;
		pTemplate.getAgentAUtilitySpace().hashCode();
		return lCode;
	}
	/**
	 * TODO: Check if this method is still needed.
	 * 
	 * @return
	 */
	public int  getTotalNumberOfBids() {
		int lTotalNumberofBids=1;
		for(int i=0;i<fNegotiationTemplate.getDomain().getIssues().size();i++) {
			switch(fNegotiationTemplate.getDomain().getIssue(i).getType()) {
			case DISCRETE:
				lTotalNumberofBids = lTotalNumberofBids*((IssueDiscrete)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfValues();
				break;
			case INTEGER:
				int lTmp = ((IssueInteger)(fNegotiationTemplate.getDomain().getIssue(i))).getUpperBound()-
				((IssueInteger)(fNegotiationTemplate.getDomain().getIssue(i))).getLowerBound()+1;
				lTotalNumberofBids = lTotalNumberofBids*lTmp;
				break;
			case REAL:
				lTotalNumberofBids = lTotalNumberofBids*((IssueReal)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfDiscretizationSteps();
				break;
				/* Removed by DT because KH removed PRICE
				 * 
				
			case PRICE:
				lTotalNumberofBids = lTotalNumberofBids*((IssuePrice)(fNegotiationTemplate.getDomain().getIssue(i))).getNumberOfDiscretizationSteps();
				break;*/				
			}//swith
		}//for
		return lTotalNumberofBids;
	}
	/**
	 * Builds all possible bid and saves in the fCompleteSpace.
	 * 
	 */
	public void buildCompleteOutcomeSpace() {
		//calculate total number of bids
		fCompleteSpace = new ArrayList<Bid>();
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
			fCompleteSpace.add(lBid);
		}//for

	}
	/** 
	 * Loads analysis object from an XML file.
	 * 
	 * @param pXMLAnalysis - <analysis> node in the XML file.
	 */
	protected void loadFromXML(SimpleElement pXMLAnalysis) {
		SimpleElement lXMLAnalysis = pXMLAnalysis;
		fHashCode = Long.valueOf(lXMLAnalysis.getAttribute("hashCode"));
		//read Pareto
		if(lXMLAnalysis.getChildByTagName("pareto").length>0) {
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("pareto")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));            	
			for(int i=0;i<lXMLParetoBids.length;i++) {
				//TODO: COMPLETED DT fix the loading bid from XML in Analysis 
				try {
					Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[i]));            	
					fPareto.add(lBid);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		sortParetoFrontier();		
		//	read Nash
		if(lXMLAnalysis.getChildByTagName("nash").length>0) {
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("nash")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));
			try {
				Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[0]));            	
				fNashProduct = lBid;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}		
		//read Kalai-Smorodinsky
		if(lXMLAnalysis.getChildByTagName("kalai_smorodinsky").length>0) {										   
			SimpleElement lXMLPareto = (SimpleElement)(lXMLAnalysis.getChildByTagName("kalai_smorodinsky")[0]);
			Object[] lXMLParetoBids = (lXMLPareto.getChildByTagName("bid"));  
			try {
				Bid lBid = new Bid(getDomain(), (SimpleElement)(lXMLParetoBids[0]));            	
				fKalaiSmorodinsky = lBid;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}		
	}

	protected Domain getDomain() {
		return fNegotiationTemplate.getDomain();
	}
	protected UtilitySpace getAgentAUtilitySpace() {
		return fNegotiationTemplate.getAgentAUtilitySpace();
	}
	protected UtilitySpace getAgentBUtilitySpace() {
		return fNegotiationTemplate.getAgentBUtilitySpace();
	}
	/**
	 * Checks the bid against current Pareto set
	 * 
	 * @param pBid
	 * @return true if bid is located to the North-East from the current Pareto frontier.
	 * @throws Exception
	 */
	private boolean checkSolutionVSParetoFrontier(Bid pBid) throws Exception {
		boolean lIsStillASolution = true;
		for (Iterator<Bid> lBidIter = fPareto.iterator(); lBidIter.hasNext();) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());    	      
			if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
					(getAgentBUtilitySpace().getUtility(pBid)<getAgentBUtilitySpace().getUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
	}
	/**
	 * Checks the bid against all other bids in the space.
	 * 
	 * @param pBid
	 * @return true if bid is Pareto efficient
	 * @throws Exception
	 */
	private boolean checkSolutionVSOtherBids(Bid pBid) throws Exception {
		boolean lIsStillASolution = true;
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
//			System.out.println("checking bid "+lBid.indexesToString() +" vs " + pBid.indexesToString());
			if((getAgentAUtilitySpace().getUtility(pBid)<getAgentAUtilitySpace().getUtility(lBid))&&
					(getAgentBUtilitySpace().getUtility(pBid)<getAgentBUtilitySpace().getUtility(lBid)))
				return false;
		}
		return lIsStillASolution;
	}
	/**
	 * Builds Pareto frontier for the negotiation template
	 * 
	 */
	public void buildParetoFrontier() {
		//Global.log("Building Pareto Frontier...");
		//loadAgentsUtilitySpaces();
		BidIterator lBidIter = new BidIterator(getDomain());
		while(lBidIter.hasNext()) {
			Bid lBid = lBidIter.next();
			//System.out.println("checking bid "+lBid.toString());
			try {
				if(!checkSolutionVSParetoFrontier(lBid)) continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(checkSolutionVSOtherBids(lBid)) fPareto.add(lBid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		sortParetoFrontier();    	
		Global.log("Finished building Pareto Frontier.");
		//make an XML representation of the analysis

		fRoot = new SimpleElement("analysis");
		SimpleElement lXMLPareto = new SimpleElement("pareto");
		fRoot.addChildElement(lXMLPareto);
		for(int i=0;i<fPareto.size();i++) {
			//TODO: COMPLETED DT fix saving bids to XML in Analysis (Pareto)
			SimpleElement lXMLBid = fPareto.get(i).toXML();
			lXMLPareto.addChildElement(lXMLBid);    		
		}
		try{
			System.out.println("fPareto=");
			for (Bid b: fPareto) 
				System.out.println("("+
						getAgentAUtilitySpace().getUtility(b)+","+
						getAgentBUtilitySpace().getUtility(b));
			} catch (Exception e) { System.out.println("gaaaaaaaaap"); }
		return;
	}
	/**
	 *  Calculate Nash product. Assumes that Pareto frontier is already built.
	 *  CHECK this assumption
	 * @throws AnalysisException
	 */
	public void calculateNash() throws AnalysisException, Exception {
		//FIXME Nash for the car example seems to be wrong.
		if(fPareto.size()<1) 
			throw new AnalysisException("Nash product: Pareto frontier is unavailable.");
		else {
			Bid fMaxBid = fPareto.get(0);
			double fMaxUtility = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(fMaxBid)*
			fNegotiationTemplate.getAgentBUtilitySpace().getUtility(fMaxBid);    			

			for(int i =1;i<fPareto.size();i++) {
				Bid fTempBid = fPareto.get(i);
				double fTempUtility = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(fTempBid)*
				fNegotiationTemplate.getAgentBUtilitySpace().getUtility(fTempBid);
				if(fTempUtility>fMaxUtility) {
					fMaxBid = fTempBid;
					fMaxUtility = fTempUtility;
				}
			}
			fNashProduct = fMaxBid;
		}
		SimpleElement lXMLNash= new SimpleElement("nash");
		fRoot.addChildElement(lXMLNash);
		SimpleElement lXMLBid = fNashProduct.toXML();
		lXMLNash.addChildElement(lXMLBid);    		
		return;
	}
	/**
	 * Calculates Kalai-Smorodinsky optimal outcome. Assumes that Pareto frontier is already built.
	 * 
	 * @throws AnalysisException
	 */
	public void calculateKalaiSmorodinsky() throws AnalysisException, Exception {
		if(fPareto.size()<1) 
			throw new AnalysisException("Nash product: Pareto frontier is unavailable.");
		else {
			Bid lMinAssymetryBid = fPareto.get(0);
			double lMinAssymetryUtility = Math.abs(fNegotiationTemplate.getAgentAUtilitySpace().getUtility(lMinAssymetryBid)-
					fNegotiationTemplate.getAgentBUtilitySpace().getUtility(lMinAssymetryBid));    			
			for(int i =1;i<fPareto.size();i++) {
				Bid lTempBid = fPareto.get(i);
				double lTempUtility = Math.abs(fNegotiationTemplate.getAgentAUtilitySpace().getUtility(lTempBid)-
				fNegotiationTemplate.getAgentBUtilitySpace().getUtility(lTempBid));
				if(lTempUtility<lMinAssymetryUtility) {
					lMinAssymetryBid = lTempBid;
					lMinAssymetryUtility = lTempUtility;
				}
			}
			fKalaiSmorodinsky= lMinAssymetryBid;
		}
		SimpleElement lXMLKalai = new SimpleElement("kalai_smorodinsky");
		fRoot.addChildElement(lXMLKalai);
		SimpleElement lXMLBid = fKalaiSmorodinsky.toXML();
		lXMLKalai.addChildElement(lXMLBid);    		
		return;
	}
	/**
	 * 
	 * Call this method to draw the negotiation paths on the chart with analysis.
	 * 
	 * Wouter: This seems at the wrong place. NegoTemplate is managing the chart!
	 * @param pAgentABids
	 * @param pAgentBBids
	 */
	/*
	public void addNegotiationPaths(int sessionNumber, ArrayList<Bid> pAgentABids, ArrayList<Bid> pAgentBBids) {
        double[][] lAgentAUtilities = new double[pAgentABids.size()][2];
        double[][] lAgentBUtilities = new double[pAgentBBids.size()][2];        
        try
        {
	        for(int i=0;i< pAgentABids.size();i++) {
	        	lAgentAUtilities [i][0] = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(pAgentABids.get(i));
	        	lAgentAUtilities [i][1] = fNegotiationTemplate.getAgentBUtilitySpace().getUtility(pAgentABids.get(i));
	        }
	        for(int i=0;i< pAgentBBids.size();i++) {
	        	lAgentBUtilities [i][0] = fNegotiationTemplate.getAgentAUtilitySpace().getUtility(pAgentBBids.get(i));
	        	lAgentBUtilities [i][1] = fNegotiationTemplate.getAgentBUtilitySpace().getUtility(pAgentBBids.get(i));
	        }
	        
	        if (Main.fChart==null) throw new Exception("fChart=null, can not add curve.");
	        Main.fChart.addCurve("Negotiation path of Agent A ("+String.valueOf(sessionNumber)+")", lAgentAUtilities);
	        Main.fChart.addCurve("Negotiation path of Agent B ("+String.valueOf(sessionNumber)+")", lAgentBUtilities);
	        Main.fChart.show();
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
		
	}
	*/
	public void saveToCache() {
		String lCacheFileName = fNegotiationTemplate.getAgentAUtilitySpaceFileName()+"_" + fNegotiationTemplate.getAgentAUtilitySpaceFileName()+".xml";		
		fRoot.saveToFile(lCacheFileName );
	}
	/**
	 * @return the fKalaiSmorodinsky
	 */
	public Bid getKalaiSmorodinsky() {
		return fKalaiSmorodinsky;
	}
	/**
	 * @return the fNashProduct
	 */
	public Bid getNashProduct() {
		return fNashProduct;
	}
	public int getParetoCount() {
		return fPareto.size();
	}
	public Bid getParetoBid(int pIndex) {
		return fPareto.get(pIndex);    	
	}
	public boolean isCompleteSpaceBuilt() {
		if(fCompleteSpace!=null) return true;
		else return false;
	}
	public Bid getBidFromCompleteSpace(int pIndex) {
		return fCompleteSpace.get(pIndex);
	}
	public SimpleElement getXMLRoot() {
		return fRoot;
	}
	private void sortParetoFrontier() {
		Collections.sort(fPareto, new BidComparator());
	}
	public long getHashCode() {
		return fHashCode;
	}
	public ArrayList<Bid> getPareto() {
		return fPareto;
	}
	/**
	 * Use this class to compare two bids to sort them for the Pareto chart.
	 * 
	 * @author dmytro
	 *
	 */
	protected  class BidComparator implements java.util.Comparator
	{
		public int compare(Object o1,Object o2) throws ClassCastException
		{
			if(!(o1 instanceof Bid)) {
				throw new ClassCastException();
			}
			if(!(o2 instanceof Bid)) {
				throw new ClassCastException();
			}
			double d1=0 , d2=0;
			try {
				d1 = fNegotiationTemplate.getAgentAUtilitySpace().getUtility((Bid)o1);
				d2 = fNegotiationTemplate.getAgentAUtilitySpace().getUtility((Bid)o2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (d1 < d2) {
				return -1; 
			}
			else if (d1 > d2) {
				return 1; 
			}
			else {
				return 0;
			}
		}
	}
	
}
