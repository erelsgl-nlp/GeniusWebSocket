package negotiator.gui.agentrepository;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.table.*;
import javax.swing.JButton;
import negotiator.repository.*;
import java.util.ArrayList;
import negotiator.repository.*;
import negotiator.exceptions.Warning;


class AddAgentUI extends JDialog
{
	JLabel agtnamelabel=new JLabel("Agent Name");
	JTextField agentname=new JTextField();
	
	JLabel agtclasslabel=new JLabel("Agent Class");
	JTextField agentclass=new JTextField();
	
	JLabel agtdesclabel=new JLabel("Description");
	JTextField agentdescription=new JTextField();

	JButton okbutton=new JButton("OK");
	JButton cancelbutton=new JButton("Cancel");
	
	AgentRepItem result=null;	// resulting item is stored here when user presses OK.
					// stays null when user presses cancel.

	
	public AddAgentUI(JFrame owner) {
		super(owner,"New Agent Input Dialog",true); // modal dialog.
		
		 // actionlisteners MUST be added before putting buttons in panel!
		okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				System.out.println("OK pressed");
				result=new AgentRepItem(agentname.getText(),agentclass.getText(),agentdescription.getText());	
				dispose();
			}
		});
		
		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				System.out.println("cancel pressed");
				dispose();
			}
		});

		
		JPanel row1=new JPanel(new BorderLayout());
		row1.add(agtnamelabel,BorderLayout.WEST);
		row1.add(agentname,BorderLayout.CENTER);
		
		JPanel row2=new JPanel(new BorderLayout());
		row2.add(agtclasslabel,BorderLayout.WEST);
		row2.add(agentclass,BorderLayout.CENTER);

		JPanel row3=new JPanel(new BorderLayout());
		row3.add(agtdesclabel,BorderLayout.WEST);
		row3.add(agentdescription,BorderLayout.CENTER);

		JPanel buttonrow=new JPanel(new BorderLayout());
		buttonrow.add(okbutton,BorderLayout.WEST);
		buttonrow.add(cancelbutton,BorderLayout.CENTER);

		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		add(row1);
		add(row2);
		add(row3);
		add(buttonrow);
		
		
		pack();
		setVisible(true);
		
	}

	
	public AgentRepItem getAgentRepItem() { return result; }
}