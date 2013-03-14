package negotiator.gui.tree.actions;

import java.awt.event.*;
import javax.swing.*;

import negotiator.gui.dialogs.*;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import jtreetable.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class EditAction extends AbstractAction {
	
	//Attributes
	TreeFrame treeFrame;
	//JTreeTable treeTable;
	
	//Constructors
	//public EditAction(JTreeTable treeTable) {
	//	super("Edit");
	//	this.treeTable = treeTable;
	//}
	public EditAction(TreeFrame treeFrame) {
		super("Edit");
		this.treeFrame = treeFrame;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		try {
			Objective obj = (Objective) treeFrame.getTreeTable().getTree().getLastSelectedPathComponent();
			if (obj instanceof Issue) {
				EditIssueDialog dialog = new EditIssueDialog(treeFrame, (Issue)obj);
			}
			/*
			if (obj instanceof IssueDiscrete) {
				EditIssueDialog dialog = new EditIssueDialog(treeTable, (Issue)obj);
			}
			else if (obj instanceof IssueInteger) {
				EditIssueDialog dialog = new EditIssueDialog(treeTable, (Issue)obj);
			}
			else if (obj instanceof IssueReal) {
				EditIssueDialog dialog = new EditIssueDialog(treeTable, (Issue)obj);
			}*/
			else if (obj instanceof Objective) {
				EditObjectiveDialog dialog = new EditObjectiveDialog(treeFrame, obj);
			}
		}
		catch (Exception except) {
			except.printStackTrace();
		}
	}

}
