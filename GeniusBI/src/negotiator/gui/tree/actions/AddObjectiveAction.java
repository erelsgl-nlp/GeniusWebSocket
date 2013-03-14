package negotiator.gui.tree.actions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jtreetable.*;
import negotiator.issue.*;
import negotiator.gui.dialogs.*;
import negotiator.gui.tree.*;
import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class AddObjectiveAction extends AbstractAction {
	
	//Attributes
	JTreeTable treeTable;
	TreeFrame owner;
	
	//Constructors
	public AddObjectiveAction(TreeFrame dialogOwner, JTreeTable treeTable) {
		super("Add Objective");
		this.treeTable = treeTable;
		this.owner = dialogOwner;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		NewObjectiveDialog dialog = new NewObjectiveDialog(owner, true);
	}

}
