package negotiator.gui.tree;

import javax.swing.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class ObjectiveValuePanel extends IssueValuePanel {

//Attributes
	
	//Constructors
	public ObjectiveValuePanel(NegotiatorTreeTableModel model, Objective objective) {
		super(model, objective);
		
		init(objective);
	}
	
	//Methods
	private void init(Objective objective) {
		this.add(new JLabel("This == Objective"));
	}
	
	public void displayValues(Objective node){
		this.removeAll();
		init(node);
	}
}
