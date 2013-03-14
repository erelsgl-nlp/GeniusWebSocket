package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jtreetable.JTreeTable;

import java.util.Enumeration;
import java.util.ArrayList;

import negotiator.gui.tree.*;
import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.gui.tree.NegotiatorTreeTableModel;
import negotiator.issue.*;
import negotiator.utility.*;

/**
 * Maakt een dialog om een nieuwe Issue aan te maken
 * 
 *
 */
public class NewIssueDialog extends NewObjectiveDialog implements ItemListener {
	//Variables
	protected static final String DISCRETE = "Discrete";
	protected static final String INTEGER = "Integer";
	protected static final String REAL = "Real";
	
	protected JComboBox issueType;
	protected String[] issueTypes;// = {DISCRETE, INTEGER, REAL}; <- for some weird reason this doesn't work
	protected JPanel issuePropertyCards;
	protected JPanel issuePropertyPanel;
	protected JPanel discretePanel;
	protected JPanel integerPanel;
	protected JPanel realPanel;
	
	protected JTextArea discreteTextArea;
	protected JTextArea discreteTextEvaluationArea;
	protected JTextArea discreteCostEvaluationArea;
	protected JTextArea discreteDescEvaluationArea;
	
	protected JTextField integerMinField;
	protected JTextField integerOtherField;
	protected JTextField integerLinearField;
	protected JTextField integerParameterField;
	protected JTextField integerMaxField;
	
	protected JTextField realMinField;
	protected JTextField realOtherField;
	protected JTextField realLinearField;
	protected JTextField realParameterField;
	protected JTextField realMaxField;
	
	//Constructors
	public NewIssueDialog(TreeFrame owner) {
		this(owner, false);
	}
		
	public NewIssueDialog(TreeFrame owner, boolean modal) {
		this(owner, modal, "Create new Issue");
	}
	
	public NewIssueDialog(TreeFrame owner, boolean modal, String name) {
		super(owner, modal, name); // This returns only after user filled in the form and pressed OK 
	}
	
	//Methods
	protected void initPanels() {
		super.initPanels();
		weightCheck.setEnabled(false);
		weightCheck.setSelected(true);		
		JPanel tmpIssPropP = constructIssuePropertyPanel();

		this.add(tmpIssPropP, BorderLayout.CENTER);
		
	}
	
	private JPanel constructIssuePropertyPanel() {
		String[] issueTypesTmp = {DISCRETE, INTEGER, REAL};
		issueTypes = issueTypesTmp;
		
		//Initialize the comboBox.
		issueType = new JComboBox(issueTypes);
		issueType.setSelectedIndex(0);
		issueType.addItemListener(this);
		
		//Initialize the input components
		discreteTextArea = new JTextArea(20, 10);
		discreteTextEvaluationArea = new JTextArea(20,4);
		discreteCostEvaluationArea = new JTextArea(20,4);
		discreteCostEvaluationArea.setEditable(false);
		discreteDescEvaluationArea = new JTextArea(20,40);
		discreteDescEvaluationArea.setEditable(false);


		integerMinField = new JTextField(15);
		integerOtherField = new JTextField(15);
		integerLinearField = new JTextField(15);
		integerParameterField = new JTextField(15);
		
		integerMaxField = new JTextField(15);
		realMinField = new JTextField(15);
		realOtherField = new JTextField(15);
		realLinearField = new JTextField(15);
		realParameterField = new JTextField(15);
		realMaxField = new JTextField(15);
		
		//Initialize the panels.
		discretePanel = constructDiscretePanel();
		integerPanel = constructIntegerPanel();
		realPanel = constructRealPanel();
		
		issuePropertyCards = new JPanel();
		issuePropertyCards.setLayout(new CardLayout());
		issuePropertyCards.add(discretePanel, DISCRETE);
		issuePropertyCards.add(integerPanel, INTEGER);
		issuePropertyCards.add(realPanel, REAL);
		
		issuePropertyPanel = new JPanel();
		issuePropertyPanel.setBorder(BorderFactory.createTitledBorder("Issue Properties"));
		issuePropertyPanel.setLayout(new BorderLayout());
		issuePropertyPanel.add(issueType, BorderLayout.PAGE_START);
		issuePropertyPanel.add(issuePropertyCards, BorderLayout.CENTER);
		/*
		if(this.weightCheck.isSelected()){
			weightCheck.setEnabled(false);
		}
		*/
		return issuePropertyPanel;
	}
	
	private JPanel constructDiscretePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		JPanel textPanel = new JPanel();
		JPanel evalPanel = new JPanel();
		JPanel costPanel = new JPanel();
		JPanel descPanel = new JPanel();
		
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		JLabel textLabel = new JLabel("Edit the discrete values below.");
		textPanel.add(textLabel);
		textPanel.add(new JScrollPane(discreteTextArea));
		panel.add(textPanel);
		
		
		
		evalPanel.setLayout(new BoxLayout(evalPanel, BoxLayout.PAGE_AXIS));
		JLabel evalLabel = new JLabel("Evaluation values.");
		evalPanel.add(evalLabel);
		evalPanel.add(new JScrollPane(discreteTextEvaluationArea));
		panel.add(evalPanel);

		costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.PAGE_AXIS));
		JLabel costLabel = new JLabel("Cost of value");
		costPanel.add(costLabel);
		costPanel.add(new JScrollPane(discreteCostEvaluationArea));
		panel.add(costPanel);
		
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.PAGE_AXIS));
		JLabel descLabel = new JLabel("Description");
		descPanel.add(descLabel);
		descPanel.add(new JScrollPane(discreteDescEvaluationArea));
		panel.add(descPanel);
		
		if (treeFrame.getNegotiatorTreeTableModel().getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			discreteTextEvaluationArea.setEnabled(false);
			discreteTextEvaluationArea.setToolTipText("Disabled until there is a Utility Space.");
		}
		

		

		return panel;
	}
	
	private JPanel constructIntegerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Give the bounds of the Integer values:");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
		
		JPanel min = new JPanel();
		min.setAlignmentX(Component.LEFT_ALIGNMENT);
		min.add(new JLabel("Min: "));
		min.add(integerMinField);
		panel.add(min);

		JPanel lin = new JPanel();
		lin.setAlignmentX(Component.LEFT_ALIGNMENT);
		lin.add(new JLabel("Linear: "));
		lin.add(integerLinearField);
		panel.add(lin);		

		JPanel par = new JPanel();
		par.setAlignmentX(Component.LEFT_ALIGNMENT);
		par.add(new JLabel("Constant: "));
		par.add(integerParameterField);
		panel.add(par);	
		
		JPanel max = new JPanel();
		max.setAlignmentX(Component.LEFT_ALIGNMENT);
		max.add(new JLabel("Max: "));
		max.add(integerMaxField);
		panel.add(max);

		if(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			integerLinearField.setEnabled(false);
			integerLinearField.setToolTipText("Disabled until there is a Utility Space.");
			integerParameterField.setEnabled(false);
			integerParameterField.setToolTipText("Disabled until there is a Utility Space.");
		}
		
		return panel;
	}
	
	private JPanel constructRealPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Give the bounds of the Real values:");
		panel.add(label);
		
		JPanel min = new JPanel();
		min.add(new JLabel("Min: "));
		min.add(realMinField);
		panel.add(min);

		JPanel lin = new JPanel();
		lin.setAlignmentX(Component.LEFT_ALIGNMENT);
		lin.add(new JLabel("Linear: "));
		lin.add(realLinearField);
		panel.add(lin);		

		JPanel par = new JPanel();
		par.setAlignmentX(Component.LEFT_ALIGNMENT);
		par.add(new JLabel("Constant: "));
		par.add(realParameterField);
		panel.add(par);	
		
		JPanel max = new JPanel();
		max.add(new JLabel("Max: "));
		max.add(realMaxField);
		panel.add(max);

		if(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace()==null){
			weightCheck.setEnabled(false);
			weightCheck.setToolTipText("Disabled until there is a Utility Space.");
			realLinearField.setEnabled(false);
			realLinearField.setToolTipText("Disabled until there is a Utility Space.");
			realParameterField.setEnabled(false);
			realParameterField.setToolTipText("Disabled until there is a Utility Space.");
		}
		
		return panel;
	}
	
	/*
	 * 
	 * get values from the input thingy
	 * empty lines are not aloowed and just ignored..
	 */
	protected String[] getDiscreteValues() throws InvalidInputException {
		/*int index;
		int lastIndex = 0;
		//String
		while ((index = source.indexOf("\n")) != -1) {
			String value = source.subString(lastIndex, index);
			System.out.println(value);
		}*/
		String[] values = discreteTextArea.getText().split("\n");
		for (int i = 0; i < values.length; i++)
			System.out.println(values[i]);
		return values;
	}
	
	

	
	/**
	 * Gets the evaluations for the discrete issue from the input field in this dialog. 
	 * @return An  arrayList with the evaluations. 
	 * Now returns elements with value 0 to indicate non-entered (empty field) values.
	 * Cost can not be edited anyway so not returned now.
	 * @throws InvalidInputException 
	 */
	protected ArrayList<Integer> getDiscreteEvalutions() throws InvalidInputException, ClassCastException {
		String[] evalueStrings = discreteTextEvaluationArea.getText().split("\n",-1);
		for (String i:evalueStrings) System.out.println(">"+i);

		ArrayList<Integer> evalues=new ArrayList<Integer>();
		//Yinon Oshrat 1/11/09 - evalueStrings.length-1 because there was always an empty string at the end
		for(int i = 0; i<evalueStrings.length-1; i++)
		{
			Integer value=0;
			if(!evalueStrings[i].equals(""))
			{
				value=Integer.valueOf(evalueStrings[i]);
				//if (value<=0) throw new InvalidInputException("Encountered "+value+". Zero or negative numbers are not allowed here");
			}
			evalues.add(value); 
		}
		return evalues;
	}
	
	/**
	 * Gets the costs for the discrete issue from the input field in this dialog. 
	 * @return An  arrayList with the costs. 
	 * Now returns elements with value 0 to indicate non-entered (empty field) values.
	 * Cost can not be edited anyway so not returned now.
	 * @throws InvalidInputException 
	 */
	protected ArrayList<Double> getDiscreteCosts() throws InvalidInputException, ClassCastException {
		String[] evalueStrings = discreteCostEvaluationArea.getText().split("\n",-1);
		for (String i:evalueStrings) System.out.println(">"+i);

		ArrayList<Double> costs=new ArrayList<Double>();
		for(int i = 0; i<evalueStrings.length; i++)
		{
			Double value=0.;
			if(!evalueStrings[i].equals(""))
			{
				value=Double.valueOf(evalueStrings[i]);
				if (value<0) throw new InvalidInputException("Encountered "+value+". Negative cost are not allowed");
			}
			costs.add(value); 
		}
		return costs;
	}
	
	
	protected int getIntegerMin() throws InvalidInputException {
		if(!integerMinField.getText().equals(""))
		return Integer.parseInt(integerMinField.getText());
		else return 0;
	}
	
	protected int getIntegerOther() throws InvalidInputException{
		if(!integerOtherField.getText().equals(""))
			return Integer.parseInt(integerOtherField.getText());
		else return 0;
	}

	protected int getIntegerLinear() throws InvalidInputException {
		if(!integerLinearField.getText().equals(""))
			return Integer.parseInt(integerLinearField.getText());
		else return 0;
	}
	
	protected int getIntegerParameter() throws InvalidInputException {
		if(!integerParameterField.getText().equals(""))
		return Integer.parseInt(integerParameterField.getText());
		else return 0;
	}	
	
	protected int getIntegerMax() throws InvalidInputException {
		if(!integerMaxField.getText().equals(""))
			return Integer.parseInt(integerMaxField.getText());
		else return 0;
	}
	
	protected double getRealMin() throws InvalidInputException {
		if(!realMinField.getText().equals(""))
			return Double.parseDouble(realMinField.getText());
		else return 0.0;
	}
	
	protected double getRealOther()throws InvalidInputException {
		if(!realOtherField.getText().equals(""))
			return Double.parseDouble(realOtherField.getText());
		else return 0.0;
	}
	
	protected double getRealLinear() throws InvalidInputException {
		if(!realLinearField.getText().equals(""))
			return Double.parseDouble(realLinearField.getText());
		else return 0.0;
	}
	
	protected double getRealParameter() throws InvalidInputException {
		if(!realParameterField.getText().equals(""))
			return Double.parseDouble(realParameterField.getText());
		else return 0.0;
	}
	
	protected double getRealMax() throws InvalidInputException {
		if(!realMaxField.getText().equals(""))
			return Double.parseDouble(realMaxField.getText());
		else return 0.0;
	}
	
	protected Issue constructIssue() {
		return updateIssue(null);
	}
	
	/**
	 * Wouter: This updates the data structures after the issue dialog was completed and user pressed OK.
	 * Not clear to me how it can return only an issue, so where are the values that were set as well?
	 * (the values should be put into a utility space)?
	 * Wouter: answer: Yes this is ugly. the utility space is updated under water, and the dialog can access it via the 
	 * parent node (treeFrame) that has access to the utility space....
	 * @param issue
	 * @return
	 * @throws exception if issues can not be accepted. e.g. negative evaluation values 
	 * or if no evaluator available for issue while there is a utiliyt space.
	 */
	protected Issue updateIssue(Issue issue)
	{
		String name;
		int number;
		String description;
		Objective selected = null; //The Objective that is selected in the tree, which will be the new Issue's parent.
		boolean newIssue = (issue == null); //Defines if a new Issue is added, or if an existing Issue is being edited.
	
		// Wouter: added: they threw away the old evaluator... bad because you loose the weight settings of the evaluator.
		// Wouter; code is ugly. They create a NEW evaluator anyway. 
		// And at  the end they check whethere there is a util space
		// anyway, and if not they throw away the new evaluator.....
		// Also we are paying here for the mix between domain and utility space editor-in-one
		UtilitySpace uts = treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		Evaluator evaluator=null;
		if (uts!=null && issue!=null) evaluator=uts.getEvaluator(issue.getNumber());
		
		try {
			name = getObjectiveName();
			number = getObjectiveNumber();
			description = getObjectiveDescription();
		}
		catch (InvalidInputException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		//If no issue is given to be modified, 
		//construct a new one that is the child of the selected Objective.
		if (newIssue) {
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
		}
		
		String selectedType = (String)issueType.getSelectedItem();
		//Issue issue = null;
		if (selectedType == DISCRETE) {
			//EvaluatorDiscrete evDis = null;
			String[] values;
			ArrayList<Integer> evalues = null;
			ArrayList<Double> costs=null;
			try {
				values = getDiscreteValues(); 
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			try{
				evalues = getDiscreteEvalutions();
				if (evalues==null) System.out.println("No evalues");
				costs=getDiscreteCosts();
			}catch (Exception f){ //Can also be a casting exception.
					JOptionPane.showMessageDialog(this, "Problem reading evaluation values:"+f.getMessage());
			}

			if (newIssue) {
				issue = new IssueDiscrete(name, number, values);
			}
			else if (issue instanceof IssueDiscrete) {
				issue.setName(name);
				issue.setNumber(number);
				((IssueDiscrete)issue).clear();
				((IssueDiscrete)issue).addValues(values);
			}
			ArrayList<ValueDiscrete> v_enum = ((IssueDiscrete)issue).getValues();

			 // load values into discrete evaluator
			if(evaluator!=null && evalues!=null)
			{
				try
				{
					((EvaluatorDiscrete) evaluator).clear(); 
				
					for (int i=0; i<v_enum.size(); i++) 
					{
						// changed by Yinon Oshrat at 1/11/09
						if (i < evalues.size() /*&& evalues.get(i)!=0*/) // evalues field is 0 if error occured at that field.
						{
							((EvaluatorDiscrete) evaluator).setEvaluation(((Value)v_enum.get(i)), evalues.get(i));
							((EvaluatorDiscrete) evaluator).setCost(((ValueDiscrete)v_enum.get(i)), costs.get(i));
						}
					}
				}
				catch (Exception e) {JOptionPane.showMessageDialog(this, e.getMessage()); }
			
				 // Wouter: I don't like the way this works now but notime to correct it. 
	
				if(uts != null) uts.addEvaluator(issue, evaluator);
			}
			else System.out.println("WARNING. no update of values!! evDis="+evaluator);
		}
		else if (selectedType == INTEGER) {
			int min;
			int linear;
			int parameter;
			int max;
			
			//Evaluator evInt = null;
			try {
				min = getIntegerMin();
				max = getIntegerMax();
				
				if(! integerLinearField.getText().equals("")){
					//evInt = new EvaluatorInteger();
					//evInt.setWeight(0.0);
					((EvaluatorInteger)evaluator).setLowerBound(min);
					((EvaluatorInteger)evaluator).setUpperBound(max);
					((EvaluatorInteger)evaluator).setLinearParam(getIntegerLinear());
				}else if(! integerParameterField.getText().equals("")){
					//evInt = new EvaluatorInteger();
					//evInt.setWeight(0.0);
					((EvaluatorInteger)evaluator).setLowerBound(min);
					((EvaluatorInteger)evaluator).setUpperBound(max);
					((EvaluatorInteger)evaluator).setConstantParam(getIntegerParameter());
				}
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			if (newIssue) {
				issue = new IssueInteger(name, number, min, max);
			}
			else if (issue instanceof IssueInteger){
				issue.setName(name);
				issue.setNumber(number);
				((IssueInteger)issue).setLowerBound(min);
				((IssueInteger)issue).setUpperBound(max);
			}
			if(uts != null) uts.addEvaluator(issue, evaluator);
		}
		else if (selectedType == REAL) {
			double min;
			double other;
			double max;
			//Evaluator evReal = null;
			try {
				min = getRealMin();
				//other = getRealOther();
				max = getRealMax();
				if(! realLinearField.getText().equals("")){
					//evReal = new EvaluatorReal();
					//evReal.setWeight(0.0);
					((EvaluatorReal)evaluator).setLowerBound(min);
					((EvaluatorReal)evaluator).setUpperBound(max);
					((EvaluatorReal)evaluator).setLinearParam(getRealLinear());
				}else if(! realParameterField.getText().equals("")){
					//evReal = new EvaluatorReal();
					//evReal.setWeight(0.0);
					((EvaluatorReal)evaluator).setLowerBound(min);
					((EvaluatorReal)evaluator).setUpperBound(max);
					((EvaluatorReal)evaluator).setConstantParam(getRealParameter());
				}
			}
			catch (InvalidInputException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return null;
			}
			if (newIssue) {
				issue = new IssueReal(name, number, min, max);
			}
			else if (issue instanceof IssueReal){
				issue.setName(name);
				issue.setNumber(number);
				((IssueReal)issue).setLowerBound(min);
				((IssueReal)issue).setUpperBound(max);
			}
			if(uts != null) uts.addEvaluator(issue, evaluator);
			
		}
		else {
			JOptionPane.showMessageDialog(this, "Please select an issue type!");
			return null;
		}
		
		issue.setDescription(description);
		if (newIssue) {
			selected.addChild(issue);
		}
		
		return issue;	
	}
	
	/**
	 * Overrides actionPerformed from Objective.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			Issue issue = constructIssue();
			if (issue == null) 
				return;
			/*else {
				//Notify the model that the contents of the treetable have changed.
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel();
				model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				this.dispose();
			}*/
			else {
				//Notify the model that the contents of the treetable have changed
				NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getNegotiatorTreeTableModel();
				model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				
				//if (model.getUtilitySpace() == null) {
				//	model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
				//}
				//else {
				//	treeFrame.reinitTreeTable(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getDomain(), ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace());
				//}
				
				this.dispose();
			}
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, (String)e.getItem());
	}

}
