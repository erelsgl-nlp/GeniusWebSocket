package negotiator.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;

import negotiator.gui.dialogs.*;
import negotiator.gui.dialogs.NewObjectiveDialog.InvalidInputException;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.utility.*;
import jtreetable.*;

/**
 * 
 * @author Richard Noorlandt
 *
 * This launches a editissue dialog window.
 * Wouter: this is ugly. The EditIssueDialog also handles editing of evaluators.
 * it gets access to the util space via the treeFrame, the parent of this dialog.
 * 
 */

public class EditIssueDialog extends NewIssueDialog {
	
	//Attributes
	private Issue issue;
	
	//Constructors
	public EditIssueDialog(TreeFrame owner, Issue issue) {
		this(owner, false, issue);
	}
		
	public EditIssueDialog(TreeFrame owner, boolean modal, Issue issue) {
		this(owner, modal, "Edit Issue", issue);
		this.issue = issue;
	}
	
	public EditIssueDialog(TreeFrame owner, boolean modal, String name, Issue issue) {
		super(owner, modal, name);
		this.issue = issue;
		setPanelContents(issue);
	}
	
	/**
	 * Load the appropriate contents into the right panel.
	 * @param issue
	 */
	private void setPanelContents(Issue issue) {
		UtilitySpace utilSpace = treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		
		nameField.setText(issue.getName());
		numberField.setText("" + issue.getNumber());
		
		/*
		if (utilSpace == null || (utilSpace.getEvaluator(issue.getNumber()) == null))
			weightCheck.setSelected(false);
		else
			weightCheck.setSelected(true);
		*/
		
		if (issue instanceof IssueDiscrete) {
			this.issueType.setSelectedItem(DISCRETE);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, DISCRETE);
			ArrayList<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();

			String valueString = "";
			String descString="";
			for (ValueDiscrete val: values)
			{
				valueString = valueString + val.getValue() + "\n";
				String desc=((IssueDiscrete)issue).getDesc(val);
				if (desc!=null) descString=descString+desc;
				descString=descString+"\n";
			}
			discreteTextArea.setText(valueString);	
			discreteDescEvaluationArea.setText(descString);
			
			if (utilSpace != null) {
				EvaluatorDiscrete eval = (EvaluatorDiscrete)utilSpace.getEvaluator(issue.getNumber());
				if (eval!=null)
				{
					 // load the eval and cost values
					valueString = "";
					String costString="";
					
					for (ValueDiscrete val: values) 
					{
						Integer util=eval.getValue(val); // get the utility for this value
						//System.out.println("util="+util);
						if (util!=null) valueString=valueString+util;
						
						Double cost=eval.getCost(val);
						if (cost!=null) costString=costString+cost;
						
						valueString=valueString+"\n";
						costString=costString+"\n";
											}
					discreteTextEvaluationArea.setText(valueString);
					discreteCostEvaluationArea.setText(costString);
				}
			}
		}
		else if (issue instanceof IssueInteger) {
			this.issueType.setSelectedItem(INTEGER);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, INTEGER);
			integerMinField.setText("" + ((IssueInteger)issue).getLowerBound());
			integerMaxField.setText("" + ((IssueInteger)issue).getUpperBound());
			if (utilSpace != null) {
				EvaluatorInteger eval = (EvaluatorInteger)utilSpace.getEvaluator(issue.getNumber());
				if (eval != null) {
					switch (eval.getFuncType()) {
					case LINEAR:
						integerLinearField.setText("" + eval.getLinearParam());
					case CONSTANT:
						integerParameterField.setText("" + eval.getConstantParam());
					default:
						break;
						
					}
				}
			}
		}
		else if (issue instanceof IssueReal) {
			this.issueType.setSelectedItem(REAL);
			this.issueType.setEnabled(false);
			((CardLayout)issuePropertyCards.getLayout()).show(issuePropertyCards, REAL);
			realMinField.setText("" + ((IssueReal)issue).getLowerBound());
			realMaxField.setText("" + ((IssueReal)issue).getUpperBound());
			if (utilSpace != null) {
				EvaluatorReal eval = (EvaluatorReal)utilSpace.getEvaluator(issue.getNumber());
				if (eval != null) {
					switch (eval.getFuncType()) {
					case LINEAR:
						realLinearField.setText("" + eval.getLinearParam());						
					case CONSTANT:
						realParameterField.setText("" + eval.getConstantParam());
					default:
						break;
					}
					//realOtherField.setText(eval.); Herbert: what's realOtherField?
				}
			}
		}
	}
	
	/**
	 * Overrides getObjectiveNumber from NewObjectiveDialog
	 */
	protected int getObjectiveNumber() throws InvalidInputException {
		try {
			return Integer.parseInt(numberField.getText());
		}
		catch (Exception e) {
			throw new InvalidInputException("Error reading objective number from (hidden) field.");
		}
	}
	
	/**
	 * Overrides actionPerformed from NewIssueDialog.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == okButton) {
			if (issue == null) return;
			updateIssue(issue);
			
			//Notify the model that the contents of the treetable have changed
			NegotiatorTreeTableModel model = (NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel();
			
			(model.getIssueValuePanel(issue)).displayValues(issue);
			model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
			
			//if (model.getUtilitySpace() == null) {
			//	model.treeStructureChanged(this, treeFrame.getTreeTable().getTree().getSelectionPath().getPath());
			//}
			//else {
			//	treeFrame.reinitTreeTable(((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getDomain(), ((NegotiatorTreeTableModel)treeFrame.getTreeTable().getTree().getModel()).getUtilitySpace());
				//}
			
			this.dispose();
		}			
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}
}