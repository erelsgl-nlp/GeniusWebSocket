package negotiator.gui.tournamentvars;


import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import negotiator.repository.*;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.exceptions.Warning;
/**
 * Open a UI and negotiate with user about which agents to use in tournament.
 * @author wouter
 *
 */
public class AgentVarUI extends DefaultOKCancelDialog {

	JButton allButton=new JButton("Select All");
	JButton noneButton=new JButton("Select None");

	ArrayList<AgentCheckBox> checkboxes; // copy of what's in the panel, for easy check-out. 

	public AgentVarUI(Frame owner) {
		super(owner, "Agent Variable Selector");
	}
	
	public Panel getPanel() {
		checkboxes=new ArrayList<AgentCheckBox>();
		Panel agentlist=new Panel();
		agentlist.setLayout(new BoxLayout(agentlist,BoxLayout.Y_AXIS));

		allButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("OK pressed");
				for (AgentCheckBox cb : checkboxes) {
					cb.setSelected(true);
				}
			}
		});

		noneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("OK pressed");
				for (AgentCheckBox cb : checkboxes) {
					cb.setSelected(false);
				}
			}
		});

		agentlist.add(allButton);
		agentlist.add(noneButton);
		
		Repository agentrep=Repository.get_agent_repository();
		for (RepItem agt: agentrep.getItems()) 
		{
			if (!(agt instanceof AgentRepItem))
				new Warning("there is a non-AgentRepItem in agent repository:"+agt);
			AgentCheckBox cbox=new AgentCheckBox((AgentRepItem)agt);
			checkboxes.add(cbox);
			agentlist.add(cbox);
		}
		return agentlist;
	}
	
	public Object ok() { 		
		ArrayList<AgentRepItem> result=new ArrayList<AgentRepItem>();
		for (AgentCheckBox cbox: checkboxes) {
			if (cbox.isSelected()) result.add(cbox.agentRepItem);
		}
		return result;
	}
}

class AgentCheckBox extends JCheckBox {
	public AgentRepItem agentRepItem;
	public AgentCheckBox(AgentRepItem agtRepItem) { 
		super(""+agtRepItem.getName()); 
		agentRepItem=agtRepItem;
	}
}