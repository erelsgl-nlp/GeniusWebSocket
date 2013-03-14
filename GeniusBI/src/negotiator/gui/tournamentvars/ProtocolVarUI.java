package negotiator.gui.tournamentvars;


import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.Panel;
import javax.swing.JDialog;

import negotiator.repository.*;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.exceptions.Warning;
/**
 * Open a UI and negotiate with user about which agents to use in tournament.
 * @author wouter
 *
 */
public class ProtocolVarUI extends DefaultOKCancelDialog {

	ArrayList<ProtocolRadioButton> radioButtons; // copy of what's in the panel, for easy check-out. 

	public ProtocolVarUI(Frame owner) {
		super(owner, "Protocol Variable Selector");
		
	}
	
	public Panel getPanel() {
		radioButtons=new ArrayList<ProtocolRadioButton>();
		Panel protocolList=new Panel();
		protocolList.setLayout(new BoxLayout(protocolList,BoxLayout.Y_AXIS));
		ButtonGroup group = new ButtonGroup();

		Repository protocolRep=Repository.getProtocolRepository();
		for (RepItem agt: protocolRep.getItems()) 
		{
			if (!(agt instanceof ProtocolRepItem))
				new Warning("there is a non-AgentRepItem in agent repository:"+agt);
			ProtocolRadioButton cbox=new ProtocolRadioButton((ProtocolRepItem)agt);
			radioButtons.add(cbox);
			protocolList.add(cbox);
			group.add(cbox);
		}
		return protocolList;
	}
	
	public Object ok() { 		
		ArrayList<ProtocolRepItem> result=new ArrayList<ProtocolRepItem>();
		for (ProtocolRadioButton cbox: radioButtons) {
			if (cbox.isSelected()) result.add(cbox.protocolRepItem);
		}
		return result;
	}
}

class ProtocolRadioButton extends JRadioButton {
	public ProtocolRepItem protocolRepItem;
	public ProtocolRadioButton(ProtocolRepItem protocolRepItem) { 
		super(""+protocolRepItem.getName()); 
		this.protocolRepItem = protocolRepItem;
	}
}