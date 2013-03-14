package negotiator.gui.tree;

import javax.swing.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class IssueIntegerValuePanel extends IssueValuePanel {

	//Attributes
		
	//Constructors
	public IssueIntegerValuePanel(NegotiatorTreeTableModel model, IssueInteger issue) {
		super(model, issue);
		
		init(issue);
	}
	
	//Methods
	private void init(IssueInteger issue) {
		this.add(new JLabel("Min: " + issue.getLowerBound() + "\tMax: " + issue.getUpperBound()));
	}
	
	public void displayValues(Objective node){
		this.removeAll();
		init(((IssueInteger)node));
	}
}
