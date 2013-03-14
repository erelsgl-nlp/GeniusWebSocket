package negotiator.gui.tournamentvars;

import java.awt.Panel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;

import negotiator.AgentParam;
import negotiator.repository.*;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.exceptions.Warning;
import javax.swing.JComboBox;
/**
 * this shows a dialog where the user can enter the parameter values
 * These should be comma-separated doubles. 
 * @author wouter
 *
 */

public class ParameterValueUI extends DefaultOKCancelDialog {
	Panel panel;
	JTextField values;
	
	ParameterValueUI(Frame owner,String paraname,String initialvalue) throws Exception {
		super(owner, "Value(s) of parameter "+paraname+" editor");
		panel=new Panel(new BorderLayout());
		JLabel label=new JLabel("Please enter a comma-separated list of doubles as possible values for parameter "+paraname);
		values=new JTextField(initialvalue);
		panel.add(label,BorderLayout.NORTH);
		panel.add(values,BorderLayout.CENTER);
	}
	
	public Panel getPanel() {
		return panel;
	}
	
	public Object ok() {
		return values.getText();
	}
}