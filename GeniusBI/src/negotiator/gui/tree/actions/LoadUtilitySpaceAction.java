package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import negotiator.Domain;
import negotiator.gui.tree.*;
import negotiator.utility.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class LoadUtilitySpaceAction extends AbstractAction {

	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Consturctors
	public LoadUtilitySpaceAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Open Utility Space");
		this.parent = parent;
		this.fileChooser = fileChooser;
		
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) 
	{
		try {
			int result = fileChooser.showOpenDialog(parent);
			if (result == JFileChooser.APPROVE_OPTION) {
				openedFile = fileChooser.getSelectedFile();
				loadUtilitySpace(openedFile);
			}
		}
		catch (Exception err) { System.out.println("Error loading utilspace:"); err.printStackTrace();}
	}
	
	private void loadUtilitySpace(File file) throws Exception
	{
		Domain domain = parent.getNegotiatorTreeTableModel().getDomain();
		parent.clearTreeTable(domain, new UtilitySpace(domain, file.getPath()));
	}
}