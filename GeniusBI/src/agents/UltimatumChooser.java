/**
 * 
 */
package agents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Ultimatum;
import negotiator.gui.DefaultOKCancelDialog;

/**
 * @author Eran
 *
 */
public class UltimatumChooser extends DefaultOKCancelDialog{
	
	JSpinner spActivationNumberOfTurns;
	JSlider slActivationPenalty;
	JTextArea txtUltimatum;
	
	int currentTurn;
	Ultimatum result;
	
	/**
	 * @param owner
	 */
	public UltimatumChooser(Frame owner, AgentID agent, AgentID destination, Bid bid, int currentTurn) {
		super(owner, "");
		setTitle("Choose Ultimatum details");
		this.currentTurn = currentTurn;
		result = new Ultimatum(agent, destination, bid);
	}

	@Override
	public Panel getPanel() {
		// Spinner for activation number of turn
		SpinnerModel spActivationNumberOfTurnsModel = new SpinnerNumberModel(3, //initial value
	                               0, //min
	                               10, //max
	                               1); //step
		spActivationNumberOfTurns = new JSpinner(spActivationNumberOfTurnsModel);
		spActivationNumberOfTurns.setBorder(BorderFactory.createTitledBorder("Activation Number Of Turns:"));
		Dimension d = spActivationNumberOfTurns.getPreferredSize();
		d.width = 200;
		spActivationNumberOfTurns.setPreferredSize(d);
//		Panel spinnerPanel = new Panel();
//		spinnerPanel.setLayout(new FlowLayout());
//		spinnerPanel.add(spActivationNumberOfTurns);

		// Slider for activation penalty
		slActivationPenalty = new JSlider(JSlider.HORIZONTAL, -300, 0, -50);
		slActivationPenalty.setBorder(BorderFactory.createTitledBorder("Penalty:"));
		slActivationPenalty.setMajorTickSpacing(50);
		slActivationPenalty.setMinorTickSpacing(10);
		slActivationPenalty.setSnapToTicks(true);
		slActivationPenalty.setPaintTicks(true);
		slActivationPenalty.setPaintLabels(true);
		
		// TextArea for ultimatum text
		txtUltimatum = new JTextArea();
		txtUltimatum.setBorder(BorderFactory.createTitledBorder("Ultimatum Text:"));
		
		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(600, 300));
		panel.add(spActivationNumberOfTurns, BorderLayout.NORTH);
//		panel.add(spinnerPanel, BorderLayout.NORTH);
		panel.add(txtUltimatum, BorderLayout.CENTER);
		panel.add(slActivationPenalty, BorderLayout.SOUTH);
		
		return panel;
	}

	@Override
	public Object ok() {
		result.setAccompanyText(txtUltimatum.getText());
		result.setSpeechText(txtUltimatum.getText());
		result.setUltimatumSentTurn(currentTurn);
		result.setUltimatumActivationTurn(currentTurn + ((SpinnerNumberModel) spActivationNumberOfTurns.getModel()).getNumber().intValue());
		result.setUltimatumPenalty(slActivationPenalty.getValue());
		return result;
	}
}