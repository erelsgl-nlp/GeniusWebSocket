package negotiator.gui.dialogs;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import negotiator.gui.tree.*;
import jtreetable.*;

import negotiator.xml.SimpleElement;

/**
 * Maakt een dialog om een Domain op te slaan
 * 
 *
 */

public class SaveDomainDialog extends JDialog implements ActionListener {
	
	//Attributes
	private TreeFrame parent;
	private JFileChooser fileChooser;
	
	private JLabel sessionLabel;
	private JLabel agentAClassLabel;
	private JLabel agentAClassNameLabel;
	private JLabel agentAUtilitySpaceLabel;
	private JLabel agentBClassLabel;
	private JLabel agentBClassNameLabel;
	private JLabel agentBUtilitySpaceLabel;
	
	private JTextField sessionField;
	private JTextField agentAClassField;
	private JTextField agentAClassNameField;
	private JTextField agentAUtilitySpaceField;
	private JTextField agentBClassField;
	private JTextField agentBClassNameField;
	private JTextField agentBUtilitySpaceField;
	
	private JButton browseAgentAUtilitySpaceButton;
	private JButton browseAgentBUtilitySpaceButton;
	private JButton cancelButton;
	private JButton saveButton;
	
	//Constructors
	public SaveDomainDialog(TreeFrame owner, boolean modal, JTreeTable treeTable, JFileChooser fileChooser) {
		this(owner, modal, treeTable, "Save Domain", fileChooser);
	}
	
	public SaveDomainDialog(TreeFrame owner, boolean modal, JTreeTable treeTable, String name, JFileChooser fileChooser) {
		//super(owner, name, modal);
		super();
		this.fileChooser = fileChooser;
		this.parent = owner;
		init();
	}
	
	//Methods
	public void init() {
		sessionLabel = new JLabel("Number of Sessions");
		agentAClassLabel = new JLabel("Agent A Class");
		agentAClassNameLabel = new JLabel("Agent A Name");
		agentAUtilitySpaceLabel = new JLabel("Agent A UtilitySpace");
		agentBClassLabel = new JLabel("Agent B Class");
		agentBClassNameLabel = new JLabel("Agent B Name");
		agentBUtilitySpaceLabel = new JLabel("Agent B UtilitySpace");
		
		sessionField = new JTextField("");
		agentAClassField = new JTextField("");
		agentAClassNameField = new JTextField("");
		agentAUtilitySpaceField = new JTextField("");
		agentBClassField = new JTextField("");
		agentBClassNameField = new JTextField("");
		agentBUtilitySpaceField = new JTextField("");
		
		browseAgentAUtilitySpaceButton = new JButton("Browse");
		browseAgentAUtilitySpaceButton.addActionListener(this);
		browseAgentBUtilitySpaceButton = new JButton("Browse");
		browseAgentBUtilitySpaceButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		
		JPanel labels = new JPanel();
		//labels.setLayout(new BoxLayout(labels, BoxLayout.PAGE_AXIS));
		labels.setLayout(new GridLayout(7,1));
		labels.add(sessionLabel);
		labels.add(agentAClassLabel);
		labels.add(agentAClassNameLabel);
		labels.add(agentAUtilitySpaceLabel);
		labels.add(agentBClassLabel);
		labels.add(agentBClassNameLabel);
		labels.add(agentBUtilitySpaceLabel);
		
		JPanel fields = new JPanel();
		//fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
		fields.setLayout(new GridLayout(7,1));
		fields.add(sessionField);
		fields.add(agentAClassField);
		fields.add(agentAClassNameField);
		fields.add(agentAUtilitySpaceField);
		fields.add(agentBClassField);
		fields.add(agentBClassNameField);
		fields.add(agentBUtilitySpaceField);
		
		JPanel browseButtons = new JPanel();
		//browseButtons.setLayout(new BoxLayout(browseButtons, BoxLayout.PAGE_AXIS));
		browseButtons.setLayout(new GridLayout(7,1));
		browseButtons.add(new JLabel(""));
		browseButtons.add(new JLabel(""));
		browseButtons.add(new JLabel(""));
		browseButtons.add(browseAgentAUtilitySpaceButton);
		browseButtons.add(new JLabel(""));
		browseButtons.add(new JLabel(""));
		browseButtons.add(browseAgentBUtilitySpaceButton);
		
		JPanel okButtons = new JPanel();
		okButtons.add(cancelButton);
		okButtons.add(saveButton);
		
		//this.setLayout(new BorderLayout());
		this.add(labels, BorderLayout.LINE_START);
		this.add(fields, BorderLayout.CENTER);
		this.add(browseButtons, BorderLayout.LINE_END);
		this.add(okButtons, BorderLayout.PAGE_END);
		
		//this.setSize(500, 300);
		this.setResizable(false);
		
		this.pack();
		this.setSize(new Dimension(400, (int)this.getSize().getHeight()));
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == browseAgentAUtilitySpaceButton) {
			int result = fileChooser.showOpenDialog(parent);
			if (result == JFileChooser.APPROVE_OPTION) {
				agentAUtilitySpaceField.setText(fileChooser.getSelectedFile().getPath());
			}
		}
		else if (e.getSource() == browseAgentBUtilitySpaceButton) {
			int result = fileChooser.showOpenDialog(parent);
			if (result == JFileChooser.APPROVE_OPTION) {
				agentBUtilitySpaceField.setText(fileChooser.getSelectedFile().getPath());
			}
		}
		else if (e.getSource() == saveButton) {
			int result = fileChooser.showSaveDialog(parent);
			File file = null;
			if (result == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
			}
			
			SimpleElement neg_template = new SimpleElement("negotiation_template");
			neg_template.setAttribute("number_of_sessions", sessionField.getText());
			
			SimpleElement agentA_tag = new SimpleElement("agent");
			agentA_tag.setAttribute("class", agentAClassLabel.getText());
			agentA_tag.setAttribute("name", agentAClassNameLabel.getText());
			agentA_tag.setAttribute("utility_space", agentAUtilitySpaceLabel.getText());
			
			SimpleElement agentB_tag = new SimpleElement("agent");
			agentB_tag.setAttribute("class", agentBClassLabel.getText());
			agentB_tag.setAttribute("name", agentBClassNameLabel.getText());
			agentB_tag.setAttribute("utility_space", agentBUtilitySpaceLabel.getText());
			
			neg_template.addChildElement(agentA_tag);
			neg_template.addChildElement(agentB_tag);
			neg_template.addChildElement(parent.getNegotiatorTreeTableModel().getDomain().toXML());
			
			neg_template.saveToFile(file.getPath());
			
			this.dispose();
		}
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
}
