package negotiator.gui.tree;


import javax.swing.*;
import javax.swing.border.*;

import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class SelectedInfoPanel extends JPanel {

	//Attributes
	private TitledBorder mainBorder;
	private JLabel name;
	private JLabel nameLabel;
	private JLabel nr;
	private JLabel nrLabel;
	private JLabel description;
	private JLabel descriptionLabel;
	
	//Constructors
	public SelectedInfoPanel() {
		super();
		init();
	}
	
	//Methods
	private void init() {
		mainBorder = BorderFactory.createTitledBorder("");
		this.setBorder(mainBorder);
		
		//Create the labels
		nameLabel = new JLabel("Name: ");
		nrLabel = new JLabel("Nr" );
		descriptionLabel = new JLabel("Description: ");
		
		//Create the labels containing contents
		name = new JLabel();
		nr = new JLabel();
		description = new JLabel();
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		labelPanel.add(nameLabel);
		labelPanel.add(nrLabel);
		labelPanel.add(descriptionLabel);
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
		fieldPanel.add(name);
		fieldPanel.add(nr);
		fieldPanel.add(description);
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.add(labelPanel);
		this.add(fieldPanel);
	}
	
	public void displayObjective(Objective objective) {
		
		if (objective == null) {
			mainBorder.setTitle("");
			name.setText("");
			nr.setText("");
			description.setText("");
		}
		else {
			name.setText(objective.getName());
			nr.setText("" + objective.getNumber());
			String d=objective.getDescription();
			if (d.equals("")) d="-";
			description.setText(d);
			if (objective instanceof Issue) {
				mainBorder.setTitle("Issue");
			}
			else {
				mainBorder.setTitle("Objective");
			}
		}
	}
}
