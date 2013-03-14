package negotiator.gui.tree.actions;

import java.awt.event.*;
import javax.swing.*;

import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.issue.Objective;

import jtreetable.JTreeTable;

/**
*
* @author Richard Noorlandt
* 
*/

public class DeleteAction extends AbstractAction {
	
	//Attributes
	JTreeTable treeTable;
	
	//Constructors
	public DeleteAction(JTreeTable treeTable) {
		super("Delete");
		this.treeTable = treeTable;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		Objective selected = (Objective) treeTable.getTree().getLastSelectedPathComponent();
		if (selected == null)
			return;
		NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeTable.getTree().getModel();
		model.removeObjective(selected);
		Object[] path = treeTable.getTree().getSelectionPath().getPath();
		Object[] rootPath = new Object[1];
		rootPath[0] = path[0];
		model.treeStructureChanged(this, rootPath);//treeTable.getTree().getSelectionPath().getPath());
	}

}
