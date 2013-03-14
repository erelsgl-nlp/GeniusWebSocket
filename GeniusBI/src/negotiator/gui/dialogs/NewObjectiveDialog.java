package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jtreetable.*;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.utility.UtilitySpace;
import negotiator.utility.*;
/**
 * Maakt een Dialog om een nieuwe Objective toe te voegen
 */

public class NewObjectiveDialog extends JDialog implements ActionListener {

	//Attributes	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected JLabel nameLabel;
	protected JLabel numberLabel;
	//protected JLabel descriptionLabel;
	protected JLabel weightLabel;
	
	protected JTextField nameField;
	protected JTextField numberField; //TODO: make this non editable
	protected JCheckBox weightCheck;
	//JTextArea descriptionArea;
	
	protected TreeFrame treeFrame;
	//protected JTreeTable treeTable;
	
	//Constructors
	
	public NewObjectiveDialog(TreeFrame owner) {
		this(owner, false);
	}
		
	/**
	 * 
	 * @param owner
	 * @param modal true if multiple dialogs can be open at once, false if not.
	 */
	public NewObjectiveDialog(TreeFrame owner, boolean modal) {
		this(owner, modal, "Create new Objective");
	}
	
	public NewObjectiveDialog(TreeFrame owner, boolean modal, String name) {
		super();
		this.treeFrame = owner;
		//this.treeTable = treeTable;

			//Wouter: set weightCheck according to utility space setting
		Objective selected = (Objective) treeFrame.getTreeTable().getTree().getLastSelectedPathComponent();
		UtilitySpace uts = treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		//if (selected!=null && uts !=null) 
		//	weightCheck=uts.getEvaluator(selected.getNumber()).???
		initPanels();
		
		this.pack();
		this.setVisible(true);
	}
	
	//Methods
	protected void initPanels() {
		this.setLayout(new BorderLayout());
		
		this.add(constructBasicPropertyPanel(), BorderLayout.NORTH);
		this.add(constructButtonPanel(), BorderLayout.SOUTH);
/*		
		if(this.weightCheck.isSelected()){
			weightCheck.setEnabled(false);
		}
*/		
	}
	
	private JPanel constructBasicPropertyPanel() {
		//Initialize the labels
		nameLabel = new JLabel("Name:");
		nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		numberLabel = new JLabel("Number:");
		numberLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		weightLabel = new JLabel("Has Weight:");
		weightLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//		descriptionLabel = new JLabel("Description:");
//		descriptionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		//Initialize the fields
		nameField = new JTextField();
		nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		numberField = new JTextField();
		numberField.setAlignmentX(Component.LEFT_ALIGNMENT);
//		numberField.setEditable(false);
//		numberField.setText("" + (((NegotiatorTreeTableModel)treeTable.getTree().getModel()).getHighestObjectiveNr() + 1));
		weightCheck = new JCheckBox();
		weightCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
//		descriptionArea = new JTextArea();
//		descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		
		labelPanel.add(new JLabel("Name:"));
//		labelPanel.add(new JLabel("Number:"));
		//labelPanel.add(new JLabel("Description:"));
		labelPanel.add(new JLabel("has Weight:"));
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
		
		fieldPanel.add(nameField);
//		fieldPanel.add(numberField);
	//	fieldPanel.add(descriptionArea);
		fieldPanel.add(weightCheck);
		
		JPanel basicPropertyPanel = new JPanel();
		basicPropertyPanel.setBorder(BorderFactory.createTitledBorder("Basic Properties"));
		basicPropertyPanel.setLayout(new BorderLayout());
		basicPropertyPanel.add(labelPanel, BorderLayout.LINE_START);
		basicPropertyPanel.add(fieldPanel, BorderLayout.CENTER);
		
		return basicPropertyPanel;
	}
	
	/**
	 * Initializes the buttons, and returns a panel containing them.
	 * @return a JPanel with the buttons.
	 */
	private JPanel constructButtonPanel() {
		//Initialize the buttons
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		return buttonPanel;
	}
	
	protected String getObjectiveName() throws InvalidInputException {
		//TODO Add side effect: check the input, and throw exception 
		return nameField.getText();
	}
	
	protected int getObjectiveNumber() throws InvalidInputException {
		//TODO Add side effect: check the input, and throw exception
		//return Integer.parseInt(numberField.getText());
		return (((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getHighestObjectiveNr() + 1);
	}
	
	protected String getObjectiveDescription() throws InvalidInputException {
		//TODO Add side effect: check the input, and throw exception
	//	return descriptionArea.getText();
		return "";
	}
	
	protected boolean getWeightCheck(){
		return weightCheck.isSelected();
		
	}
	
	protected Objective constructObjective() {
		String name;
		int number;
		//String description;
		boolean hasEvaluator;
		Objective selected; //The Objective that is seleced in the tree, which will be the new Objective's parent.
		try {
			name = getObjectiveName();
			number = treeFrame.getNegotiatorTreeTableModel().getHighestObjectiveNr() + 1;
			hasEvaluator = getWeightCheck();
		//	description = getObjectiveDescription();
		}
		catch (InvalidInputException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		try {
			selected = (Objective) treeFrame.getTreeTable().getTree().getLastSelectedPathComponent();
			if (selected == null) {
				JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
				return null;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "There is no valid parent selected for this objective.");
			return null;
		}
		Objective objective = new Objective(selected, name, number);
		
		//objective.setDescription(description);
		selected.addChild(objective);
		//Wouter: following code is new.
		UtilitySpace utilspace=treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		if (utilspace!=null)
		{
			EvaluatorObjective ev=new EvaluatorObjective();
			ev.setHasWeight(getWeightCheck());
			utilspace.addEvaluator(objective, ev);
		}
		
		return objective;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			Objective objective = constructObjective();
			if (objective == null) return;
				
			//Notify the model that the contents of the treetable have changed
			NegotiatorTreeTableModel model = treeFrame.getNegotiatorTreeTableModel();				
			model.treeStructureChanged(this, treeFrame.getTreeTable().
					getTree().getSelectionPath().getPath());
			
			this.dispose();
		
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	protected class InvalidInputException extends Exception {
		protected InvalidInputException() {
			super();
		}
		
		protected InvalidInputException(String message) {
			super(message);
		}
	}
}
