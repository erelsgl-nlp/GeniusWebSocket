package negotiator.gui.dialogs;

import java.awt.*;

import javax.swing.JOptionPane;
import negotiator.gui.tree.*;
import negotiator.issue.*;
import negotiator.utility.EvaluatorObjective;
import negotiator.utility.UtilitySpace;

import jtreetable.JTreeTable;

/**
 * Maakt dialog om objectives mee te editen
 * 
 *
 */
public class EditObjectiveDialog extends NewObjectiveDialog {
	
	//Attributes
	Objective objective;
	
	//Constructor
	public EditObjectiveDialog(TreeFrame owner, Objective objective) {
		this(owner, false, objective);
	}
		
	public EditObjectiveDialog(TreeFrame owner, boolean modal, Objective objective) {
		this(owner, modal, "Edit Objective", objective);
	}
	
	public EditObjectiveDialog(TreeFrame owner, boolean modal, String name, Objective objective) {
		super(owner, modal, name);
		this.objective = objective;
		setFieldValues();
	}
	
	//Methods
	private void setFieldValues() {
		nameField.setText(objective.getName());
		numberField.setText("" + objective.getNumber());
		
		boolean weighted=true;
		UtilitySpace utilspace=treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		if (utilspace!=null)
		{
			EvaluatorObjective ev=(EvaluatorObjective)utilspace.getEvaluator(objective.getNumber());
			weighted=ev.getHasWeight();
		}
		weightCheck.setSelected(weighted);

		//descriptionArea.setText(objective.getDescription());
	}
	
	/**Wouter:  override from NewObjectiveDialog is weird.
	 * In fact, the 'constructObjective' now is an 'editObjective'.
	 * This works because constructObjective inserted the new node into the tree anyway,
	 * and now nothing has to inserted.
	 * 
	 */
	protected Objective constructObjective() {
		String name="";
		int number=0;
		String description="";
		try {
			name = getObjectiveName();
			number = (getObjectiveNumber());
			description = (getObjectiveDescription());
		}
		catch (InvalidInputException e) {
			
		}
		objective.setName(name);
		objective.setNumber(number);
		objective.setDescription(description);
		
		UtilitySpace us = treeFrame.getNegotiatorTreeTableModel().getUtilitySpace();
		if (us!=null)
		{
			EvaluatorObjective ev=(EvaluatorObjective)us.getEvaluator(objective.getNumber());
			if (ev!=null) ev.setHasWeight(getWeightCheck());
		}
		
		return objective;
	}
}
