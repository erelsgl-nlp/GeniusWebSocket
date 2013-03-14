package negotiator.gui.tree.actions;

import javax.swing.*;
import java.awt.event.*;

import negotiator.Domain;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewDomainAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	
	//Constructors
	public NewDomainAction(TreeFrame parent) {
		super("New Domain");
		this.parent = parent;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		Objective newRoot = new Objective(null, "root", 0);
		Domain domain = new Domain();
		domain.setObjectivesRoot(newRoot);
		parent.clearTreeTable(domain);
	}
}
