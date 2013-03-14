/*
 * Main.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;


import javax.swing.JFileChooser;
import javax.swing.JPanel;

import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;


/**
 *
 * @author W.Pasman nov2007
 * Checks all selected files for conformity with the Party domain. 
 * Checked is that the selected file is ready to run (has all evaluators set)
 * and that all costs correspond to those in party_empty_utility.xml
 * You select them one by one, and if one fails the check the application stops with explanation.
 */
public class CheckPartyUtilityXML extends JPanel
{
	Domain partyDomain=null;
	UtilitySpace costSpace=null; // utili space holding correct costs.
	
	public CheckPartyUtilityXML() throws Exception
	{	
    	boolean finished=false;
    	partyDomain=new Domain("etc/templates/partydomain/party_domain.xml");
    	costSpace=new UtilitySpace(partyDomain,"etc/templates/partydomain/party_utility_empty1.xml");
    	
    	
    	while (!finished)
    	{
    		JFileChooser fileChooser = new JFileChooser();
    		if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
    		{
    			checkFile(fileChooser.getSelectedFile().toString() );
    		}
    		else finished=true;
    	}
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	try {     	new CheckPartyUtilityXML(); }
    	catch (Exception e) { 
    			System.out.println("Error checking party utilities:"+e.getMessage()); 
    			//e.printStackTrace();
    	}
    }
    
    /**
     * @author W.Pasman
     * @param filename is the filename  of utility space to be checked
     * @throws Exception if file does not meet domain requirements
     */
    public void checkFile(String filename) throws Exception
    {
    	System.out.println("Checking "+filename);
    	UtilitySpace us=new UtilitySpace(partyDomain,filename);
    	us.checkReadyForNegotiation(filename, partyDomain);
    	System.out.println("Checking cost fields...");
    	checkCostFields(us);
    	System.out.println("Check succesfull!\n\n");
    }
    
    
    /**
     * check cost against costSpace
     * @author W.Pasman
     * @param us the utilspace to be checked.
     */
    public void checkCostFields(UtilitySpace us) throws Exception
    {
    	for (Issue issue: partyDomain.getIssues())
    	{
    		if (!(issue instanceof IssueDiscrete))
    			throw new Exception("issue in domain not Discrete??!");
    		IssueDiscrete id=(IssueDiscrete)issue;
    		
    		Evaluator eva=costSpace.getEvaluator(issue.getNumber());
    		if (!(eva instanceof EvaluatorDiscrete))
    			throw new Exception("evaluator in costSpace not Discrete??!");
    		EvaluatorDiscrete ed=(EvaluatorDiscrete)eva;
    		
    		Evaluator eva1=us.getEvaluator(issue.getNumber());
    		if (!(eva1 instanceof EvaluatorDiscrete))
    			throw new Exception("evaluator in utilSpace not Discrete!");
    		EvaluatorDiscrete ed1=(EvaluatorDiscrete)eva1;

    		
    		for (ValueDiscrete val: id.getValues())
    		{
    			// check cost of every value in every issue in the domain
    			if (!(ed.getCost(val).equals(ed1.getCost(val))))
    				throw new Exception("Cost not matching for issue "+issue+
    						", value "+val+": should be "+
    						ed.getCost(val)+ " but found "+ ed1.getCost(val)
    						);
    		}
    	}
    }
}

