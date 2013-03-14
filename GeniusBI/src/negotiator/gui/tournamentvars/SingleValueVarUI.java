package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

import negotiator.exceptions.Warning;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.repository.AgentRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberValue;

public class SingleValueVarUI extends DefaultOKCancelDialog {
	private JTextField textField;
	
	public SingleValueVarUI(Frame frame) {
		super(frame, "Number of sessions");
	}
	
	@Override
	public Panel getPanel() {
		textField = new JTextField();
		Panel panel = new Panel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(textField);
		return panel;

	}

	@Override
	public Object ok() {
		// TODO Auto-generated method stub
		return new TotalSessionNumberValue(Integer.valueOf(textField.getText()));
	}

}
