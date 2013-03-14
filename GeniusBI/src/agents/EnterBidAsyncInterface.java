/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package agents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.AgreementReached;
import negotiator.actions.BidAction;
import negotiator.actions.Comment;
import negotiator.actions.CounterOffer;
import negotiator.actions.EndNegotiation;
import negotiator.actions.EndTurn;
import negotiator.actions.Offer;
import negotiator.actions.OfferUpgrade;
import negotiator.actions.Promise;
import negotiator.actions.Query;
import negotiator.actions.Reject;
import negotiator.actions.TextMessage;
import negotiator.actions.Threat;
import negotiator.actions.Ultimatum;
import negotiator.actions.UltimatumThreat;
import negotiator.actions.UpdateStatusAction;
import negotiator.actions.ValidateAgreement;
import negotiator.exceptions.Warning;
import negotiator.gui.chart.UtilityPlot;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Y. Oshrat
 */
@SuppressWarnings("serial")
public class EnterBidAsyncInterface extends JFrame {

	protected NegoInfo negoinfo; // the table model
	protected negotiator.actions.Action selectedAction;
	protected Agent agent;
	protected JTextArea negotiationMessages = new JTextArea("NO MESSAGES YET");
	protected JTextArea typeMessages = new JTextArea("");
	protected JTextArea negotiationTurnsMessage = new JTextArea("Negotiation has not begun yet.");
	// Wouter: we have some whitespace in the buttons,
	// that makes nicer buttons and also artificially increases the window size.
	protected JButton buttonAccept, buttonReject, buttonEnd, buttonBid, buttonComment, buttonThreat, buttonAgreementReached;
	protected JPanel firstButtonPanel = new JPanel();
	protected JPanel secondButtonPanel = new JPanel();
	protected UtilitySpace utilitySpace;
	protected JComboBox actionTypeCombo = new JComboBox(new String[] {"Offer","Validate Agreement"});
	protected JTable BidTable;

	// alinas variables
	protected ChartPanel chartPanel;
	protected JPanel defaultChartPanel;
	// private ScatterPlot plot;
	protected UtilityPlot plot;
	
	//Yinon variables:
	protected JTable ourBidHistoryTable, opponentBidHistoryTable;  // the tables
	protected EnterBidHistoryInfo ourActions, opponentActions;             // the table models
	
	protected int currentTurn;
	
	public EnterBidAsyncInterface(Agent agent, UtilitySpace us) throws Exception {
		super("Negotiation of " + agent.getName());
		this.agent = agent;
		this.utilitySpace = us;
		negoinfo = new NegoInfo(null, null, us);
		ourActions = new EnterBidHistoryInfo(agent, us);
		opponentActions = new EnterBidHistoryInfo(agent, us);
		currentTurn=0;
	}

	// quick hack.. we can't refer to the Agent's utilitySpace because
	// the field is protected and there is no getUtilitySpace function either.
	// therefore the Agent has to inform us when utilspace changes.
	public void setUtilitySpace(UtilitySpace us) {
		negoinfo.utilitySpace1 = us;
		ourActions.utilitySpace1 = us;
		opponentActions.utilitySpace1 = us;
	}

	
	protected JScrollPane init_ourBidHistoryTable() {
		// Create panel for our history of bids
		
		ourBidHistoryTable = new JTable(ourActions);
		ourBidHistoryTable.setGridColor(Color.lightGray);
		ourActions.setColumnWidthsOf(ourBidHistoryTable.getColumnModel());
		ourBidHistoryTable.setDefaultRenderer(ourBidHistoryTable.getColumnClass(0),
				ourActions.getRenderer("ourActions"));
		ourBidHistoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		ourBidHistoryTable.setRowHeight(40);
		
		JScrollPane tablepaneOurHistory = new JScrollPane(ourBidHistoryTable);
		tablepaneOurHistory.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Sent actions:"));
		return tablepaneOurHistory;
	}
	
	protected JScrollPane init_opponentBidHistoryTable() {
		// Create panel for opponent history of bids
		opponentBidHistoryTable = new JTable(opponentActions);
		opponentBidHistoryTable.setGridColor(Color.lightGray);
		opponentActions.setColumnWidthsOf(opponentBidHistoryTable.getColumnModel());
		opponentBidHistoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		opponentBidHistoryTable.setRowHeight(40);
		opponentBidHistoryTable.setDefaultRenderer(opponentBidHistoryTable.getColumnClass(0),
				opponentActions.getRenderer("opponentActions"));
		
		JScrollPane tablepaneOpponentHistory = new JScrollPane(opponentBidHistoryTable);
		tablepaneOpponentHistory.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Received actions:"));
		return tablepaneOpponentHistory;
	}
	
	protected JSplitPane init_upperPanel() {
		// Create the upper panel
		
		JSplitPane upperPanel;
		upperPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		upperPanel.setLeftComponent(init_ourBidHistoryTable());
		upperPanel.setRightComponent(init_opponentBidHistoryTable());
		upperPanel.setDividerLocation(0.5f);
		upperPanel.setBorder(BorderFactory.createEmptyBorder());
		upperPanel.setResizeWeight(0.5f);
		return upperPanel;
		
	}
	
	protected void init_defaultChart() {
		// Create the chart
		defaultChartPanel = new JPanel(new BorderLayout());
		defaultChartPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),	"Utilities of Bids per round:")));
		defaultChartPanel.setPreferredSize(new Dimension(400, 350));
	}
	
	protected JPanel init_userInputPanel() {
		// Create the user input panel
		
		JPanel userInputPanel = new JPanel();
		userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));
		userInputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),	"Please place your bid:")));
		
		negotiationTurnsMessage.setBackground(Color.lightGray);
		negotiationTurnsMessage.setEditable(false);
		userInputPanel.add(negotiationTurnsMessage);
		negotiationMessages.setBackground(Color.lightGray);
		negotiationMessages.setEditable(false);
		userInputPanel.add(negotiationMessages);
		typeMessages.setBackground(Color.lightGray);
		typeMessages.setEditable(false);
		userInputPanel.add(typeMessages);

		// create center panel: the bid table
		BidTable = new JTable(negoinfo);
		BidTable.setGridColor(Color.lightGray);
		BidTable.setRowHeight(18);
		BidTable.getColumnModel().getColumn(0).setMaxWidth(80);
		BidTable.getColumnModel().getColumn(0).setMinWidth(80);
		BidTable.getColumnModel().getColumn(1).setMaxWidth(120);
		BidTable.getColumnModel().getColumn(1).setMinWidth(120);
		JPanel tablepane = new JPanel(new BorderLayout());
		tablepane.add(BidTable.getTableHeader(), "North");
		tablepane.add(BidTable, "Center");
		userInputPanel.add(tablepane);
		BidTable.setDefaultRenderer(BidTable.getColumnClass(0),
				new MyCellRenderer(negoinfo));
        BidTable.setDefaultEditor(BidTable.getColumnClass(0),new MyCellEditor(negoinfo));

		// create the buttons:
		firstButtonPanel.setLayout(new FlowLayout());
		firstButtonPanel.add(buttonReject = new JButton("Reject"));
		firstButtonPanel.add(buttonAccept = new JButton("Accept"));
		firstButtonPanel.add(buttonBid = new JButton("Send"));
		firstButtonPanel.add(actionTypeCombo);
		userInputPanel.add(firstButtonPanel);
		
		secondButtonPanel.setLayout(new FlowLayout());
		secondButtonPanel.add(buttonComment = new JButton("Send Comment"));
		buttonComment.setEnabled(false);
		secondButtonPanel.add(buttonThreat = new JButton("Send Threat"));
		buttonThreat.setEnabled(false);
		secondButtonPanel.add(buttonEnd = new JButton("Opt out"));
		secondButtonPanel.add(buttonAgreementReached = new JButton("Agreement Reached"));
		userInputPanel.add(secondButtonPanel);
		
		// set action listeners for the buttons
		buttonBid.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonBidActionPerformed(evt);
			}
		});
		buttonReject.addActionListener(new java.awt.event.ActionListener() {
		 public void actionPerformed(java.awt.event.ActionEvent evt) {
		 buttonRejectActionPerformed(evt);
		 }
		 });
		buttonEnd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonEndActionPerformed(evt);
			}
		});
		buttonAccept.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonAcceptActionPerformed(evt);
			}
		});
		buttonComment.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonCommentActionPerformed(evt);
			}
		});
		buttonThreat.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonThreatActionPerformed(evt);
			}
		});
		buttonAgreementReached.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonAgreementReachedActionPerformed(evt);
			}
		});

		return userInputPanel;
	}
	
	protected JSplitPane init_lowerPanel() {
		// Create the lower panel
		
		JSplitPane lowerPanel;
		lowerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		lowerPanel.setLeftComponent(defaultChartPanel);
		lowerPanel.setRightComponent(init_userInputPanel());
		lowerPanel.setDividerLocation(0.5f);
		lowerPanel.setBorder(BorderFactory.createEmptyBorder());
		lowerPanel.setResizeWeight(0.5f);
		
		return lowerPanel;
	}

	public void initThePanel() {
		if (negoinfo == null)
			throw new NullPointerException("negoinfo is null");
		Container pane = getContentPane();

		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Choose action for agent " + agent.getName());
		
		pane.add(init_upperPanel());
		init_defaultChart();
		pane.add(init_lowerPanel());
		
		pack(); // pack will do complete layout, getting all cells etc.
		setVisible(true);
	}

	private Bid getBid() {
		Bid bid = null;
		try {
			bid = negoinfo.getBid();
			bid.setTime(currentTurn);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"There is a problem with your bid: " + e.getMessage());
		}
		return bid;
	}

	/**
	 * @param evt  
	 */
	void buttonBidActionPerformed(java.awt.event.ActionEvent evt) {

		Bid bid = getBid();
		if (bid != null) {
			String type=(String)actionTypeCombo.getSelectedItem();
			if (type.equals("Counter Offer"))
				selectedAction = new CounterOffer(agent.getAgentID(), bid);
			else if (type.equals("Query"))
				selectedAction = new Query(agent.getAgentID(), bid);
			else if (type.equals("Promise"))
				selectedAction = new Promise(agent.getAgentID(), bid);
			else if (type.equals("Offer"))
				selectedAction = new Offer(agent.getAgentID(), bid);
			else if (type.equals("Validate Agreement"))
				selectedAction = new ValidateAgreement(agent.getAgentID(), bid);
			agent.sendAction(selectedAction);
			ourActions.addAction(selectedAction, currentTurn);
			//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
			updateUtiltyPlot();
		}
		
	}

	/**
	 * @param evt the event. 
	 */
	void buttonRejectActionPerformed(java.awt.event.ActionEvent evt) {
		BidAction lastOpponentBidAction=negoinfo.getOpponentLatestBidAction();
		if (lastOpponentBidAction != null && lastOpponentBidAction.getBid() != null) {
			System.out.println("Reject performed");
			selectedAction = new Reject(agent.getAgentID(),lastOpponentBidAction);
			agent.sendAction(selectedAction);
			ourActions.addAction(selectedAction, currentTurn);
			//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
			if (buttonAccept!=null) buttonAccept.setEnabled(false);
			if (buttonReject!=null) buttonReject.setEnabled(false);
		}

	}

	/**
	 * @param evt  
	 */
	void buttonAcceptActionPerformed(java.awt.event.ActionEvent evt) {
		BidAction lastOpponentBidAction=negoinfo.getOpponentLatestBidAction();
		if (lastOpponentBidAction != null && lastOpponentBidAction.getBid() != null) {
			System.out.println("Accept performed");
			selectedAction = new Accept(agent.getAgentID(),lastOpponentBidAction);
			agent.sendAction(selectedAction);
			ourActions.addAction(selectedAction, currentTurn);
			//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
			if (buttonAccept!=null) buttonAccept.setEnabled(false);
			if (buttonReject!=null) buttonReject.setEnabled(false);
		}
	}

	
	/**
	 * @param evt the event. 
	 */
	void buttonEndActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("End Negotiation performed");
		selectedAction = new EndNegotiation(agent.getAgentID());
		agent.sendAction(selectedAction);
		ourActions.addAction(selectedAction, currentTurn);
		//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
	}
	
	/**
	 * @param evt  
	 */
	void buttonCommentActionPerformed(java.awt.event.ActionEvent evt) {
		CommentThreatChooser ctc=new CommentThreatChooser(this,true);
		Integer res=(Integer)ctc.getResult();
		if (res!=null) {
			selectedAction=new Comment(agent.getAgentID(),res.intValue());
			agent.sendAction(selectedAction);
			ourActions.addAction(selectedAction, currentTurn);
			//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
		}
	}
	
	/**
	 * @param evt  
	 */
	void buttonThreatActionPerformed(java.awt.event.ActionEvent evt) {
		CommentThreatChooser ctc=new CommentThreatChooser(this,false);
		Integer res=(Integer)ctc.getResult();
		if (res!=null) {
			selectedAction=new Threat(agent.getAgentID(),res.intValue());
			agent.sendAction(selectedAction);
			ourActions.addAction(selectedAction, currentTurn);
			//ourBidHistoryTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
		}
	}
	
	/**
	 * @param evt the event. 
	 */
	void buttonAgreementReachedActionPerformed(java.awt.event.ActionEvent evt) {
		selectedAction=new AgreementReached(agent.getAgentID());
		agent.sendAction(selectedAction);
		ourActions.addAction(selectedAction, currentTurn);
	}
	
	
	/**
	 * This is called by UIAgent repeatedly, to update about the action of the opponent.
	 * 
	 * @param opponentAction
	 *            is action done by opponent.
	 */
	public void updateOpponentAction(negotiator.actions.Action opponentAction) {
		if (opponentAction == null) {
			return;
		}
		else if (opponentAction instanceof EndTurn) {
    		currentTurn=((EndTurn)opponentAction).getTurn();
    		negotiationTurnsMessage.setText("We just enterd the " + currentTurn + " turn!");
    		return;
    	}
		else if (opponentAction instanceof UpdateStatusAction) {
			/* ignore */
    	}
		else if (opponentAction instanceof Accept) {
			negotiationMessages.setText("Opponent accepted your bid!");
		}
		else if (opponentAction instanceof Reject) {
			negotiationMessages.setText("Opponent rejected your bid!");
		}
		else if (opponentAction instanceof EndNegotiation) {
			negotiationMessages.setText("Opponent cancels the negotiation.");
		}
		else if (opponentAction instanceof AgreementReached) {
			negotiationMessages.setText("Opponent thinks that an agreement was reached.");
		}
		else if (opponentAction instanceof TextMessage) {
			negotiationMessages.setText("Opponent sent a text message.");
		}
		else if (opponentAction instanceof BidAction) {
			String type = null;
			if (opponentAction instanceof Offer)
				type="Offer";
			else if (opponentAction instanceof Query)
				type="Query";
			else if (opponentAction instanceof Promise)
				type="Promise";
			else if (opponentAction instanceof CounterOffer)
				type="CounterOffer";
			else if (opponentAction instanceof OfferUpgrade)
				type="OfferUpgrade";
			else if (opponentAction instanceof Ultimatum)
				type="Ultimatum";
			else if (opponentAction instanceof ValidateAgreement)
				type="ValidateAgreement";
			
			if (opponentAction instanceof Ultimatum) {
				typeMessages.setText("Mediator sent the following " + type + ":");
				negotiationMessages.setText(((BidAction) opponentAction).getAccompanyText());
			} else {
				typeMessages.setText("Opponent sent the following " + type + ":");
			}
			
			negoinfo.opponentLatestBidAction = (BidAction) opponentAction;
			if (opponentAction instanceof Ultimatum && ((BidAction) opponentAction).getBid() == null) {
				/* ignore (???) */
			} else {
				if (buttonAccept!=null) buttonAccept.setEnabled(true);
				if (buttonReject!=null) buttonReject.setEnabled(true);
			}
		} else if (opponentAction instanceof UltimatumThreat) {
			negotiationMessages.setText(((UltimatumThreat) opponentAction).getAccompanyText());
		} else if (opponentAction instanceof Threat) {
			negotiationMessages.setText(((Threat) opponentAction).getAccompanyText());
		} else {
			new Warning("unknown messsage type got to UI");
		}
		opponentActions.addAction(opponentAction, currentTurn);
		//opponentBidHistoryTable.updateUI();  // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!
		
		if (opponentAction instanceof BidAction || opponentAction instanceof EndTurn) {
			if (opponentAction instanceof Ultimatum && ((BidAction) opponentAction).getBid() == null) {
				/* ignore */
			} else {
				updateUtiltyPlot();
				//if (BidTable!=null) BidTable.updateUI(); // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!

			}
		}
		//pack();
		setVisible(true);
	}
	
	 /**
	 * create a new plot of the bid utilities for each round
	 *	if there is a chart already, remove and draw new one
	 */
	protected void updateUtiltyPlot() {
		
		if (defaultChartPanel.getComponents().length > 0)
			defaultChartPanel.remove(chartPanel);
		// plot = new ScatterPlot(myBidSeries, oppBidSeries);
		plot = new UtilityPlot(ourActions.getUtilitiesAsArray(), opponentActions.getOppUtilsAsArray(), opponentActions.getMedUtilsAsArray());
		JFreeChart chart = plot.getChart();
		chartPanel = new ChartPanel(chart);
		//chartPanel.setPreferredSize(new Dimension(350, 350));
		defaultChartPanel.add(chartPanel);
		defaultChartPanel.validate();
		//defaultChartPanel.updateUI();   // updateUI destroys the renderer, and causes the table to contain "javax" strings instead of the real components!

	}

 
	/********************************************************/

	/********************************************************/

	/**
	 * NegoInfo is the class that contains all the negotiation data, and handles
	 * the GUI, updating the JTable. This is the main interface to the actual
	 * JTable. This is usually called XXXModel but I dont like the 'model' in
	 * the name. We implement actionlistener to hear the combo box events that
	 * require re-rendering of the total cost and utility field. We are pretty
	 * hard-wired for a 3-column table, with column 0 the labels, column 1 the
	 * opponent bid and col2 our own bid.
	 */
	class NegoInfo extends AbstractTableModel implements ActionListener {
		
		public Bid ourOldBid; // Bid is hashmap <issueID,Value>. Our current bid
								// is only in the comboboxes,
		public BidAction opponentLatestBidAction;
		// use getBid().
		public UtilitySpace utilitySpace1; // WARNING: this may be null
		public ArrayList<Issue> issues = new ArrayList<Issue>();
		// the issues, in row order as in the GUI. Init to empty, to enable
		// freshly initialized NegoInfo to give useful results to the GUI.
		public ArrayList<Integer> IDs; // the IDs/numbers of the issues, ordered
										// to row number
		public ArrayList<JComboBox> comboBoxes; // the combo boxes for the
												// second column, ordered to row
												// number
		
		/**
		 * @param opponent  
		 */
		NegoInfo(Bid our, Bid opponent, UtilitySpace us) throws Exception {
			// Wouter: just discovered that assert does nothing...........
			ourOldBid = our;
			opponentLatestBidAction = null;
			utilitySpace1 = us;
			issues = utilitySpace1.getDomain().getIssues();
			IDs = new ArrayList<Integer>();
			for (int i = 0; i < issues.size(); i++)
				IDs.add(issues.get(i).getNumber());
			makeComboBoxes();
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return issues.size() + 2;
		}

		@Override public boolean isCellEditable(int row, int col) {
			return (col == 2 && row < issues.size());
		}

		private String[] colNames = { "Issue", "Last received bid",
				"Your bid" };

		@Override public String getColumnName(int col) {
			return colNames[col];
		}

		public void setOurBid(Bid bid) throws Exception {
			ourOldBid = bid;
			if (bid == null)
				try {
					ourOldBid = utilitySpace1.getMaxUtilityBid();
				} catch (Exception e) {
					System.out.println("error getting max utility first bid:"
							+ e.getMessage());
					e.printStackTrace();
				}
			makeComboBoxes(); // reset the whole shit...
			setComboBoxes(ourOldBid);
		}

		void makeComboBoxes() throws Exception {
			comboBoxes = new ArrayList<JComboBox>();
			for (Issue issue : issues) {
				if (!(issue instanceof IssueDiscrete))
					System.out.println("Problem: issue " + issue
							+ " is not IssueDiscrete. ");
				ArrayList<ValueDiscrete> values = ((IssueDiscrete) issue)
						.getValues();
				JComboBox cbox = new JComboBox();
				EvaluatorDiscrete eval = null;
				if (utilitySpace1 != null)
					eval = (EvaluatorDiscrete) utilitySpace1.getEvaluator(issue
							.getNumber());
				for (ValueDiscrete val : values) {
					String utilinfo = "";
					if (eval != null)
						try {
							// utilinfo="("+eval.getEvaluation(val)+")";
							utilinfo = "(" + eval.getValue(val) + ")";

						} catch (Exception e) {
							System.out.println("no evaluator for " + val
									+ "???");
						}

					cbox.addItem(val + utilinfo);
				}
				comboBoxes.add(cbox);
			}
			for (JComboBox b : comboBoxes)
				b.addActionListener(this);
			
		}

		/**
		 * set the initial combo box selections according to ourOldBid Note, we
		 * can only handle Discrete evaluators right now.
		 * 
		 * @throws if there is a problem with the issues and evaluators.
		 */
		void setComboBoxes(Bid bid) throws Exception {
			for (int i = 0; i < issues.size(); i++) {
				IssueDiscrete iss = (IssueDiscrete) issues.get(i);
				ValueDiscrete val = (ValueDiscrete) bid.getValue(iss
						.getNumber());
				comboBoxes.get(i).setSelectedIndex(
						iss.getValueIndex(val));
			}
		}

		/**
		 * get the currently chosen evaluation value of given row in the table.
		 * 
		 * @param bid
		 *            : which bid (the column in the table are for ourBid and
		 *            opponentBid)
		 * @return the evaluation of the given row in the bid. returns null if
		 *         the bid has no value in that row.
		 * @throws probablly
		 *             if rownr is out of range 0...issues.size()-1
		 */
		Value getCurrentEval(Bid bid, int rownr) throws Exception {
			if (bid == null)
				return null;
			Integer ID = IDs.get(rownr); // get ID of the issue in question.
			return bid.getValue(ID); // get the current value for that issue in
										// the bid
		}

		/**
		 * get a render component
		 * 
		 * @return the Component that can be rendered to show this cell.
		 */
		public Component getValueAt(int row, int col) {
			if (row == issues.size()) {
				if (col == 0)
					return new JLabel("COST (in your utilspace):");

				if (utilitySpace1 == null)
					return new JLabel("No UtilSpace");
				Bid bid;
				if (col == 1)
					
						bid = getOpponentLatestBid();
				else
					try {
						bid = getBid();
					} catch (Exception e) {
						bid = null;
						System.out.println("Internal err with getBid:"
								+ e.getMessage());
					}
				String val;
				try {
					val = utilitySpace1.getCost(bid).toString();
				} catch (Exception e) {
					new Warning("Exception during cost calculation:"
							+ e.getMessage(), false, 1);
					val = "XXX";
				}

				JTextArea result = new JTextArea(val);
				if (utilitySpace1.constraintsViolated(bid))
					result.setBackground(Color.red);
				return result;
			}
			if (row == issues.size() + 1) {
				if (col == 0)
					return new JLabel("Utility:");
				if (utilitySpace1 == null)
					return new JLabel("No UtilSpace");
				Bid bid;
				if (col == 1)
					bid = getOpponentLatestBid();
				else
					try {
						bid = getBid();
					} catch (Exception e) {
						bid = null;
						System.out.println("Internal err with getBid:"
								+ e.getMessage());
					}
				
				JProgressBar bar = new JProgressBar(0, 100);
				bar.setStringPainted(true);
				if (bid!=null)
				{
					try {
						bar.setValue((int) (0.5 + 100.0 * utilitySpace1.getNormlizedUtility(bid)));
						DecimalFormat format = new DecimalFormat("#0.00");
						bar.setString(format.format(utilitySpace1.getUtilityWithTimeEffect(bid, currentTurn)));
						bar.setIndeterminate(false);
					} catch (Exception e) {
						new Warning("Exception during utility calculation:"
								+ e.getMessage(), false, 1);
						bar.setIndeterminate(true);
					}
				}
				return bar;
			}
			switch (col) {
			case 0:
				return new JTextArea(issues.get(row).getName());
			case 1:
				Value value = null;
				try {
					value = getCurrentEval(getOpponentLatestBid(), row);
				} catch (Exception e) {
					System.out.println("Err EnterBidDialog2.getValueAt: "
							+ e.getMessage());
				}
				if (value == null)
					return new JTextArea("-");
				return new JTextArea(value.toString());
			case 2:
				return comboBoxes.get(row);
			}
			return null;
		}

		/**
		 * @return the last BidAction of the opponent
		 */
		public BidAction getOpponentLatestBidAction() {	
			return opponentLatestBidAction;
		}
		
		/**
		 * @return the bid from the last BidAction received from the opponent
		 */
		public Bid getOpponentLatestBid() {
			if (opponentLatestBidAction==null)
				return null;
			else
				return opponentLatestBidAction.getBid();
		}

		Bid getBid() throws Exception {
			HashMap<Integer, Value> values = new HashMap<Integer, Value>();

			for (int i = 0; i < issues.size(); i++)
				values.put(IDs.get(i), ((IssueDiscrete) issues.get(i))
						.getValue(comboBoxes.get(i).getSelectedIndex()));
			// values.put(IDs.get(i),
			// (Value)comboBoxes.get(i).getSelectedItem());
			return new Bid(utilitySpace1.getDomain(), values);
		}
				
		public void actionPerformed(ActionEvent e) {
			// System.out.println("event d!"+e);
			// update the cost and utility of our own bid.
			fireTableCellUpdated(issues.size(), 2);
			fireTableCellUpdated(issues.size() + 1, 2);
		}

	}

	/********************************************************************/

	class MyCellRenderer implements TableCellRenderer {
		NegoInfo negoinfo1;

		public MyCellRenderer(NegoInfo n) {
			negoinfo1 = n;
		}

		// the default converts everything to string...
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return negoinfo1.getValueAt(row, column);
		}
	}

	/********************************************************************/

	class MyCellEditor extends DefaultCellEditor {
		NegoInfo negoinfo1;

		public MyCellEditor(NegoInfo n) {
			super(new JTextField("vaag")); // Java wants us to call super class,
											// who cares...
			negoinfo1 = n;
			setClickCountToStart(1);
		}

		@Override public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return negoinfo1.getValueAt(row, column);
		}

	}
	
	
	
	
	/**
	 * demo program
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception{
		Domain domain = new Domain("etc/templates/JobCandiate/JobCanDomain.xml");
		UtilitySpace uspace = new UtilitySpace(domain, "etc/templates/JobCandiate/Side_ACompromise.xml");
		UIAgentAsync agent = new UIAgentAsync();
		EnterBidAsyncInterface frame = new EnterBidAsyncInterface(agent, uspace);
		frame.initThePanel();
	}
}