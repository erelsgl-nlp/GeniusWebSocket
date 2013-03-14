package negotiator.gui.tournamentvars;


import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import java.awt.Panel;

import negotiator.repository.*;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.exceptions.Warning;

/**
 * Open a UI and negotiate with user about which mediators to use in tournament.
 * @author wouter
 *
 */
public class MediatorVarUI extends DefaultOKCancelDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<MediatorCheckBox> checkboxes; // copy of what's in the panel, for easy check-out. 

	public MediatorVarUI(Frame owner) {
		super(owner, "Mediator Variable Selector");
	}
	
	public Panel getPanel() {
		checkboxes=new ArrayList<MediatorCheckBox>();
		Panel mediatorlist=new Panel();
		mediatorlist.setLayout(new BoxLayout(mediatorlist,BoxLayout.Y_AXIS));

		Repository mediatorrep=Repository.get_mediator_repository();
		
		MediatorCheckBox none_cb=new MediatorCheckBox(null);
		none_cb.setSelected(true);
		checkboxes.add(none_cb);	
		mediatorlist.add(none_cb);
		
		for (RepItem med: mediatorrep.getItems()) 
		{
			if (!(med instanceof MediatorRepItem))
				new Warning("there is a non-MediatorRepItem in mediator repository:"+med);
			MediatorCheckBox cbox=new MediatorCheckBox((MediatorRepItem)med);
			checkboxes.add(cbox);
			mediatorlist.add(cbox);
		}
		return mediatorlist;
	}
	
	public Object ok() { 		
		ArrayList<MediatorRepItem> result=new ArrayList<MediatorRepItem>();
		for (MediatorCheckBox cbox: checkboxes) {
			if (cbox.isSelected()) result.add(cbox.mediatorRepItem);
		}
		return result;
	}
}

class MediatorCheckBox extends JCheckBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MediatorRepItem mediatorRepItem;
	public MediatorCheckBox(MediatorRepItem medRepItem) {
		super(medRepItem == null ? "None" : "" + medRepItem.getName()); 
		mediatorRepItem=medRepItem;
	}
}