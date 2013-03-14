package negotiator.gui.tree.actions;

import javax.swing.*;
import java.awt.event.*;

import negotiator.Domain;
import negotiator.gui.tree.*;
import negotiator.utility.UtilitySpace;

/**
*
* @author Richard Noorlandt
* 
*/

public class NewUtilitySpaceAction extends AbstractAction {
	
	//Attributes
	TreeFrame parent;
	
	//Constructors
	public NewUtilitySpaceAction(TreeFrame parent) {
		super("New Utility Space");
		this.parent = parent;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Domain domain = parent.getNegotiatorTreeTableModel().getDomain();
			parent.clearTreeTable(domain, new UtilitySpace(domain, ""));
		}
		catch (Exception err) { System.out.println("Error creating utility space:");err.printStackTrace();}
	}
}
