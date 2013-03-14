package negotiator.gui;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import java.awt.Panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import negotiator.repository.ProfileRepItem;


/**
 * open a modal OK/Cancel dialog. 
 * you must implement ok() and getPanel() to make this working.
 * Panel opens, gets result and returns result only  when you call getResult()
 * Typical use: new YourOKCancelDialog(yourframe).getResult();
 * 
 * @author wouter 19aug08
 *
 */
public abstract class DefaultOKCancelDialog extends JDialog {
	JButton okbutton=new JButton("OK");
	JButton cancelbutton=new JButton("Cancel");
	Object the_result=null; // will be set after ok button is pressed. null in other cases (eg cancel)
	
	/** 
	 * 
	 * @param owner is the parent frame, used only to center the dialog properly. Probably can be null
	 * @param title the title of the dialog
	 */
	public DefaultOKCancelDialog(Frame owner, String title) {
		super(owner,title,true); // modal dialog.		
		getContentPane().setLayout(new BorderLayout());
	}
	

	/** this function computes the result of the dialog.
	 * You may return null if the user entered illegal choices or somehow cancelled the dialog.
	 * This function will only be called when user presses OK button, 
	 * which also finishes the dialog and closes the dialog window.
	 */
	public abstract Object ok();
	
	/**
	 * this fucnction returns the actual contents for the dialog panel
	 * I implemented this as a function, because we need it before opening the window.
	 * @return a Panel containing the actual dialog contents.
	 * 
	 */
	public abstract Panel getPanel();
	
	/** call this to get the result. Do not override, instead override ok(). */
	public Object getResult() { 
		 // actionlisteners MUST be added before putting buttons in panel!
		okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("OK pressed");
				the_result=ok();
				setVisible(false);
			}
		});
		
		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("cancel pressed");
				setVisible(false);
			}
		});
		
		Panel buttonrow=new Panel(new BorderLayout());
		buttonrow.add(okbutton,BorderLayout.WEST);
		buttonrow.add(cancelbutton,BorderLayout.EAST);

		add(buttonrow,BorderLayout.SOUTH);
		Panel panel=getPanel();
		JScrollPane scrollPane=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Dimension size=scrollPane.getPreferredSize();
		if (size.getHeight()>400)
			size.setSize(size.getWidth(), 400);
		scrollPane.setPreferredSize(size);
		add(scrollPane,BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true); // block until closing window.
		return the_result; 
		}
}