package negotiator.gui.tree.actions;

import javax.swing.*;

import negotiator.gui.tree.TreeFrame;

import java.awt.event.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class ExitAction extends AbstractAction {
	
	//Attributes
	private TreeFrame frame;
	
	//Constructors
	public ExitAction(TreeFrame frame) {
		super("Exit");
		this.frame = frame;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
//		frame.dispose();
	}
}
