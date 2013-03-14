package negotiator.gui.tree;

import javax.swing.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class IssueDiscreteValuePanel extends IssueValuePanel {

	//Attributes
	
	
	//Constructors
	public IssueDiscreteValuePanel(NegotiatorTreeTableModel model, IssueDiscrete issue) {
		super(model, issue);
		
		init(issue);
	}
	
	//Methods
	private void init(IssueDiscrete issue) {
		String values = "";
		for (int i = 0; i < issue.getNumberOfValues(); i++) {
			values = values + issue.getStringValue(i) + ", ";
		}
		this.add(new JLabel(values));
		this.setToolTipText(values);
	}
	

	
	public void displayValues(Objective node){
		this.removeAll();
		init((IssueDiscrete) node);
	}
}
