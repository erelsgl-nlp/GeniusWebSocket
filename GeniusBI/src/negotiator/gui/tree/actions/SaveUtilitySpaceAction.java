package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;

import negotiator.gui.NegoGUIApp;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.gui.tree.*;
import negotiator.repository.Repository;
import negotiator.utility.UtilitySpace;

/**
*
* @author Richard Noorlandt
* 
*/

public class SaveUtilitySpaceAction extends AbstractAction {
	
	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Constructors
	public SaveUtilitySpaceAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Save UtilitySpace");
		this.parent = parent;
		this.fileChooser = fileChooser;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		
		// check first for problems.
		UtilitySpace us=parent.getNegotiatorTreeTableModel().getUtilitySpace();
		String warning=us.IsComplete();
		if (warning!=null){
				int choice=JOptionPane.showConfirmDialog(null, "Utility space is not complete: "+warning+". Save anyway?");
				if (choice!=JOptionPane.OK_OPTION) return;
		}
		//get the folder of the domain 
		String folder =  System.getProperties().getProperty("user.dir")  +"/"+parent.getDomainRepItem().getURL().getPath();	
		fileChooser.setSelectedFile(new File(folder));
		int result = fileChooser.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			saveUtilitySpace(openedFile);
			try {
				DomainRepositoryUI domainRep = NegoGUIApp.negoGUIView.getDomainRepositoryUI();
				domainRep. addprofile(openedFile.toURL());
			} catch (Exception exc) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(parent, "Error while saving:\r\n" + exc.getMessage());
				exc.printStackTrace();
			}
		}
		
	}
	
	private void saveUtilitySpace(File file) {
		parent.getNegotiatorTreeTableModel().getUtilitySpace().toXML().saveToFile(file.getPath());
	}
}