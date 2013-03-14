/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package agents;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import negotiator.Agent;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.actions.TextMessage;
import negotiator.exceptions.NegotiatorException;
import negotiator.gui.nlp.GrammarPanel;
import negotiator.gui.nlp.GrammarRules;
import negotiator.gui.nlp.GrammarToGeniusBridge;
import negotiator.utility.UtilitySpace;

/**
 * An NLP version for {@link EnterBidAsyncInterface} - for WOZ experiments
 * @author Erel Segal
 * @since 20/12/2011
 */
@SuppressWarnings("serial")
public class EnterBidAsyncNLP extends EnterBidAsyncInterface {
	
	/**
	 * The rules of the grammar by which the WOZ selects actions. 
	 */
	protected GrammarRules ourGrammarRules;
	
	/**
	 * The GUI panel in which the WOZ selects actions. 
	 */
	protected GrammarPanel grammarPanel;

	public EnterBidAsyncNLP(Agent agent, UtilitySpace us, GrammarRules ourGrammarRules, GrammarRules opponentGrammarRules) throws Exception {
		super(agent, us);
		this.ourGrammarRules = ourGrammarRules;
		ourActions = new EnterBidHistoryInfoNLP(agent, us, ourGrammarRules);
		opponentActions = new EnterBidHistoryInfoNLP(agent, us, opponentGrammarRules);
	}

	@Override protected JPanel init_userInputPanel() {
		JPanel userInputPanel = new JPanel();
		userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));
		userInputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),	"Please place your bid:")));

		userInputPanel.add(grammarPanel = new GrammarPanel(ourGrammarRules));

		firstButtonPanel.setLayout(new FlowLayout());
		firstButtonPanel.add(buttonBid = new JButton("Send"));
		userInputPanel.add(firstButtonPanel);

		buttonBid.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String semanticAction = grammarPanel.getGeneratedAction();
				List<Action> actions;
				try {
					actions = GrammarToGeniusBridge.semanticStringToGeniusAction(
							semanticAction, utilitySpace.getDomain(), agent.getAgentID(), currentTurn, negoinfo.getOpponentLatestBidAction(), null /* TODO: get speaker's latest bid action! */);
				} catch (NegotiatorException e) {
					actions = Arrays.asList( (Action)new TextMessage(agent.getAgentID(), "action '"+semanticAction+"' generated an exception: "+e));
					e.printStackTrace();
				}
				for (Action action: actions) {
					selectedAction = action;
					agent.sendAction(selectedAction);
					ourActions.addAction(selectedAction, currentTurn);
					updateUtiltyPlot();
				}
			}
		});
		return userInputPanel;
	}
	
	

	/**
	 * demo program
	 */
	public static void main (String[] args) throws Exception{
		Domain domain = new Domain("etc/templates/JobCandiate/JobCanDomain.xml");
		UtilitySpace uspace = new UtilitySpace(domain, "etc/templates/JobCandiate/Side_BCompromise.xml");
		UIAgentAsync agent = new UIAgentAsync();
		EnterBidAsyncNLP frame = new EnterBidAsyncNLP(agent, uspace,
				new GrammarRules(new File("etc/templates/JobCandiate/Side_A_NLP.txt"), new File("etc/templates/JobCandiate/Common_NLP.txt")),
				new GrammarRules(new File("etc/templates/JobCandiate/Side_B_NLP.txt"), new File("etc/templates/JobCandiate/Common_NLP.txt")));
		frame.initThePanel();
	}
}