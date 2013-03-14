package negotiator.gui.tournamentvars;

import java.awt.Panel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.BorderLayout;

import negotiator.AgentParam;
import negotiator.repository.*;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.exceptions.Warning;
import javax.swing.JComboBox;
/**
 * this shows a dialog where the user can select the parameter name 
 * @author wouter
 *
 */

public class ParameterVarUI extends DefaultOKCancelDialog {
	Panel panel;
	JComboBox combobox;
	
	ParameterVarUI(Frame owner,ArrayList<AgentParam> selectableparameters) throws Exception {
		super(owner, "Agent Parameter Selector");
		if (selectableparameters==null || selectableparameters.size()==0)
			throw new IllegalArgumentException("There are no selectable parameters because there are no selectable agents with parameters");
		panel=new Panel(new BorderLayout());
		combobox=new JComboBox(new Vector<AgentParam>(selectableparameters));
		panel.add(combobox,BorderLayout.CENTER);
	}
	
	public Panel getPanel() {
		return panel;
	}
	
	public Object ok() {
		return combobox.getSelectedItem();
	}
}