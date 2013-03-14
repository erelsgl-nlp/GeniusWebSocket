/**
 * 
 */
package agents;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import negotiator.actions.Comment;
import negotiator.actions.Threat;
import negotiator.gui.DefaultOKCancelDialog;
/**
 * @author User
 *
 */
public class CommentThreatChooser extends DefaultOKCancelDialog{
	
	boolean isCommentChooser;
	ButtonGroup bg;
	Integer tmp_result;
	
	/**
	 * @param owner
	 */
	public CommentThreatChooser(Frame owner, boolean isCommentChooser) {
		super(owner,"");
		this.isCommentChooser=isCommentChooser;
		if (isCommentChooser)
			setTitle("Choose a comment");
		else
			setTitle("Choose a threat");
	}

	@Override
	public Panel getPanel() {
		bg=new ButtonGroup();
		Panel panel=new Panel();
		panel.setLayout(new GridLayout(0,1));
		int length;
		if (isCommentChooser)
			length=Comment.getNumberOfPossibleComments();
		else
			length=Threat.getNumberOfPossibleThreats();
		
		for (int i=0;i<length;i++) {
			JRadioButton newRB=new JRadioButton();
			if (isCommentChooser)
				newRB.setText(Comment.getPossibleComment(i));
			else
				newRB.setText(Threat.getPossibleThreat(i));
			newRB.setActionCommand("" + i);

			bg.add(newRB);
			panel.add(newRB);
		}
		return panel;
	}

	@Override
	public Object ok() {
		if (bg.getSelection()!=null)
			return new Integer(bg.getSelection().getActionCommand());
		else
			return null;
	}

}
