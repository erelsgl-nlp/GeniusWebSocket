package negotiator.gui.tournamentvars;

import java.awt.Panel;
import javax.swing.BoxLayout;
import negotiator.repository.*;
import java.util.ArrayList;
import negotiator.exceptions.Warning;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import negotiator.gui.DefaultOKCancelDialog;
/**
 * This is a UI for editing a profile variable.
 * @author wouter
 *
 */

public class ProfileVarUI extends DefaultOKCancelDialog {
	
	ArrayList<MyCheckBox> checkboxes; // copy of what's in the panel, for easy check-out
	ArrayList<ProfileRepItem> oldselection; // what was selected before?
	/**
	 * Ask user which profiles he wants to be used. 
	 * TODO copy old selection into the new checkboxes.
	 * TODO force selection of exactly ONE checkbox.
	 * 
	 * @param oldsel contain a list of the values that are aready selected
	 * @param owner is used to place the dialog properly over the owner's window.
	 * @throws if domain repository has a problem
	 */
	public ProfileVarUI(JFrame owner,ArrayList<ProfileRepItem> oldsel)  throws Exception {
		super(owner,"Profile Selector GUI"); // modal dialog.
		oldselection=oldsel;
	}
	
	public Panel getPanel() {
		Panel agentlist=new Panel();
		try {
			checkboxes=new ArrayList<MyCheckBox>(); // static initialization does NOT WORK now as getPanel is part of constructor!!
			Repository domainrep=Repository.get_domain_repos();
			agentlist.setLayout(new BoxLayout(agentlist,BoxLayout.Y_AXIS));
			for (RepItem domain :domainrep.getItems()) {
				for (ProfileRepItem profile: ((DomainRepItem)domain).getProfiles()) {
					MyCheckBox cbox=new MyCheckBox(profile,oldselection.contains(profile));
					checkboxes.add(cbox);
					agentlist.add(cbox);
				}
			}
		} catch (Exception e) {
			new Warning("creation of content panel failed: "+e); e.printStackTrace();
		}
		return agentlist;
	}

	public ArrayList<ProfileRepItem> ok() {
		ArrayList<ProfileRepItem> result=new ArrayList<ProfileRepItem>();
		for (MyCheckBox cbox: checkboxes) {
			if (cbox.isSelected()) result.add(cbox.profileRepItem);
		}
		return result;
	}


}

class MyCheckBox extends JCheckBox {
	public ProfileRepItem profileRepItem;
	public MyCheckBox(ProfileRepItem profileitem,boolean selected) { 
		super(""+profileitem.getURL(),selected); 
		profileRepItem=profileitem;
	}
}