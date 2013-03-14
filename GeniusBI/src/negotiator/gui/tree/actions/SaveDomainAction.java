package negotiator.gui.tree.actions;

import javax.swing.*;
import java.io.*;
import java.awt.event.*;

import negotiator.gui.NegoGUIApp;
import negotiator.gui.tree.*;
import negotiator.gui.dialogs.*;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.xml.SimpleElement;

/**
*
* @author Richard Noorlandt
* 
*/

public class SaveDomainAction extends AbstractAction {
	
	//Attributes
	private TreeFrame parent;
	private File openedFile;
	private final JFileChooser fileChooser;
	
	//Constructors
	public SaveDomainAction (TreeFrame parent, JFileChooser fileChooser) {
		super("Save Domain");
		this.parent = parent;
		this.fileChooser = fileChooser;
	}
	
	//Methods
	public void actionPerformed(ActionEvent e) {
		int result = fileChooser.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			saveDomain(openedFile);
		}
		//new SaveDomainDialog(parent, true, parent.getTreeTable(), fileChooser);
	}
	
	private void saveDomain(File file) {
		SimpleElement neg_template = new SimpleElement("negotiation_template");
		neg_template.setAttribute("number_of_sessions", "1");
		
		SimpleElement agentA_tag = new SimpleElement("agent");
		agentA_tag.setAttribute("class", "");
		agentA_tag.setAttribute("name", "");
		agentA_tag.setAttribute("utility_space", "");
		
		SimpleElement agentB_tag = new SimpleElement("agent");
		agentB_tag.setAttribute("class", "");
		agentB_tag.setAttribute("name", "");
		agentB_tag.setAttribute("utility_space", "");
		
		neg_template.addChildElement(agentA_tag);
		neg_template.addChildElement(agentB_tag);
		neg_template.addChildElement(parent.getNegotiatorTreeTableModel().getDomain().toXML());
		
		neg_template.saveToFile(file.getPath());
		try {
			DomainRepositoryUI domainRep = NegoGUIApp.negoGUIView.getDomainRepositoryUI();
			domainRep.adddomain(openedFile.toURL());
		} catch (Exception exc) {
			// TODO: handle exception
			exc.printStackTrace();
		}
		
	}
}