/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package agents;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import negotiator.Agent;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.actions.Reject;
import negotiator.gui.nlp.GrammarRules;
import negotiator.gui.nlp.GrammarToGeniusBridge;
import negotiator.utility.UtilitySpace;

/**
 * A table model for the NLP WOZ GUI.
 * extends {@link EnterBidHistoryInfo} with an additional column - the natural language sentence that corresponds to the action.
 * @author Erel Segal
 * @since 22/12/2011
 */
@SuppressWarnings("serial")
class EnterBidHistoryInfoNLP extends EnterBidHistoryInfo {
	protected GrammarRules rules;
	
	public EnterBidHistoryInfoNLP(Agent agent, UtilitySpace us, GrammarRules theRules) throws Exception {
		super(agent, us);
		rules = theRules;
		colNames = new String[]  {"Turn", "Agent", "Action", "Sentence", "utility"};
	}

	@Override public String getColumnName(int col) {
		return colNames[col];
	}

	@Override protected Component getComponentValueAt(int row, int col) {
		if (row>=0 && row < history.size()) {
			// get the bids for the row-th round:
			EnterBidMessageInfo message=history.get(row);
			switch (col) {
			case 0:  // roundcount
				return new JLabel(Integer.toString(message.getTurn()));
			case 1:  // Agent
				if (message.action.getAgent() == null)  
					return new JLabel("");
				else
					return new JLabel(message.action.getAgent().toString()); 
			case 2:  // opponent bid as string
				JTextArea bidText = new JTextArea(message.getAction().toString());
				bidText.setLineWrap(true);
				return bidText; 
			case 3:  // opponent bid as natural language sentence
				String semanticString = GrammarToGeniusBridge.geniusActionToSemanticString(message.getAction(), utilitySpace1.getDomain());
				String naturalString = rules.generate(rules.generate(semanticString));  // run generate twice to make 2 passes on the rules list
				bidText = new JTextArea(naturalString);
				bidText.setLineWrap(true);
				return bidText; 
			case 4:  // utility
				try {
					double util = 0.0;
					JTextArea res = new JTextArea("");
					Action action=message.getAction();
					if (action != null && action instanceof BidAction) {
						util = utilitySpace1.getUtility(((BidAction)action).getBid());
						DecimalFormat df = new DecimalFormat("0.00");
						res.setText(df.format(util));
					}

					return res;
				} catch (Exception e) { /*  ignore exception */	}		
			}
		}

		return null;
	}
	
	@Override public void setColumnWidthsOf(TableColumnModel columnModel) {
		// setting the columns that contain numbers to a small width:
		columnModel.getColumn(0).setMaxWidth(50);
		columnModel.getColumn(0).setMinWidth(50);
		columnModel.getColumn(1).setMaxWidth(50);
		columnModel.getColumn(1).setMinWidth(50);
		columnModel.getColumn(2).setPreferredWidth(200);
		columnModel.getColumn(3).setPreferredWidth(200);
		columnModel.getColumn(4).setMaxWidth(50);
		columnModel.getColumn(4).setMinWidth(50);
	}
	
	@Override public TableCellRenderer getRenderer(String name) {
		return new MyHistoryCellRenderer(this, name);
	}

	
	class MyHistoryCellRenderer implements TableCellRenderer {
		EnterBidHistoryInfoNLP historyinfo;
		String name; // for debug;

		public MyHistoryCellRenderer(EnterBidHistoryInfoNLP n, String name) {
			historyinfo = n;
			this.name = name;
		}

		// the default converts everything to string...
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			//Component c = historyinfo.getValueAt(row, column);
			Component c = (Component)value;
			//System.out.println(name+","+row+","+column+": "+value);

			if (column == 2 || column==3) {   // the "Action" columns - should be unlimited in height:
				c.setSize(table.getColumnModel().getColumn(column).getWidth(), 1000);
				int rowHeight = (int)c.getPreferredSize().getHeight();
				if (rowHeight + table.getRowMargin() != table.getRowHeight(row)) {
					table.setRowHeight(row, rowHeight + table.getRowMargin());
				}
			}

			return c;
		}

	}
	

	/**
	 * demo program
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception {
		Domain domain = new Domain("etc/templates/JobCandiate/JobCanDomain.xml");
		UtilitySpace uspace = new UtilitySpace(domain, "etc/templates/JobCandiate/Side_ACompromise.xml");
		UIAgentAsync agent = new UIAgentAsync();
		EnterBidHistoryInfoNLP tableModel = new EnterBidHistoryInfoNLP(agent, uspace, GrammarToGeniusBridge.grammarRulesFromUtilitySpace(uspace));
		tableModel.addAction(new Accept(agent.getAgentID()), 1);
		tableModel.addAction(new Reject(agent.getAgentID()), 2);
		JTable testTable = new JTable(tableModel);
		tableModel.setColumnWidthsOf(testTable.getColumnModel());
		testTable.setPreferredSize(new Dimension(400,400));

		JFrame frame = new JFrame();
		frame.add(testTable);
		frame.pack();
		frame.setVisible(true);
	}
}