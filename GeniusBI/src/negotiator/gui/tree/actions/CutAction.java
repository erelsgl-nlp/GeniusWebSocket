package negotiator.gui.tree.actions;

import javax.swing.*;
import java.awt.event.*;

import negotiator.gui.tree.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class CutAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	
	//Constructors
	public CutAction(TreeFrame parent) {
		super("Cut");
		this.parent = parent;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		
	}
}
