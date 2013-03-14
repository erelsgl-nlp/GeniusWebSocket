/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package agents;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import negotiator.Agent;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.actions.Reject;
import negotiator.utility.UtilitySpace;

/**
 * HistoryInfo is the class that contains a history of bids and
 *         fills a JTable  with it.
 * @author Yinon Oshrat 
 */
@SuppressWarnings("serial")
class EnterBidHistoryInfo extends AbstractTableModel {
	protected ArrayList<EnterBidMessageInfo> history = null;
	protected ArrayList<Double> utilities = null;
	protected String[] colNames;
	protected UtilitySpace utilitySpace1;
	protected Agent agent;

	EnterBidHistoryInfo(Agent agent, UtilitySpace us) throws Exception {
		this.agent = agent;
		utilitySpace1 = us;
		history=new ArrayList<EnterBidMessageInfo>();
		utilities=new ArrayList<Double>();
		colNames = new String[] {"Turn", "Agent", "Action", "utility"};
	}

	@Override public String getColumnName(int col) {
		return colNames[col];
	}

	public int getSize() {
		return history.size();
	}

	/**
	 * return the utilties of the bids stored in this object, as an array of doubles to be used for graph drawing
	 * @return - a number of bid X 2 array of doubles the first row contain the actions index the second their utility
	 * @author - Yinon Osrat
	 */
	public double[][] getUtilitiesAsArray(){
		double[][] res = new double[2][utilities.size()];
		for (int i=0;i<utilities.size();i++) {
			res[0][i] = i + 1;
			res[1][i] = utilities.get(i);
		}
		return res;
	}

	public double[][] getOppUtilsAsArray(){
		double[][] res1 = new double[2][utilities.size()];
		int i = 0, j = 0, countUtils = 0;
		while (i < history.size()) {
			if (history.get(i).getAction() instanceof BidAction) {
				if (!history.get(i).getAction().getAgent().equals(agent.worldInformation.getMediatorID())) {
					res1[0][countUtils] = countUtils + 1;
					res1[1][countUtils] = utilities.get(j);
					++countUtils;
				}
				++j;
			}
			++i;
		}

		double[][] res = new double[2][countUtils];
		System.arraycopy(res1[0], 0, res[0], 0, countUtils);
		System.arraycopy(res1[1], 0, res[1], 0, countUtils);

		return res;
	}

	public double[][] getMedUtilsAsArray(){
		double[][] res1 = new double[2][utilities.size()];
		int i = 0, j = 0, countUtils = 0;
		while (i < history.size()) {
			if (history.get(i).getAction() instanceof BidAction) {
				if (history.get(i).getAction().getAgent().equals(agent.worldInformation.getMediatorID())) {
					res1[0][countUtils] = countUtils + 1;
					res1[1][countUtils] = utilities.get(j);
					++countUtils;
				}
				++j;
			}
			++i;
		}

		double[][] res = new double[2][countUtils];
		System.arraycopy(res1[0], 0, res[0], 0, countUtils);
		System.arraycopy(res1[1], 0, res[1], 0, countUtils);

		return res;
	}

	public void addAction(Action action, int turn)  {
		history.add(new EnterBidMessageInfo(action,turn));
		if (action instanceof BidAction) {
			double util=0;
			try {
				util = utilitySpace1.getUtility(((BidAction)action).getBid());
			} catch (Exception e) {	/* ignore exception */	}
			utilities.add(util);
		}
		fireTableRowsInserted(history.size() - 1, history.size() - 1); // Notifies all listeners that a row has been appended
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return history.size(); 
	}
	
	/*
    @Override public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }*/
    
	public Object getValueAt(int row, int col) {
		return getComponentValueAt(row, col);
	}
	
	protected Component getComponentValueAt(int row, int col) {
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
			case 3:  // utility
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
	
	public void setColumnWidthsOf(TableColumnModel columnModel) {
		// setting the columns that contain numbers to a small width:
		columnModel.getColumn(0).setMaxWidth(50);
		columnModel.getColumn(0).setMinWidth(50);
		columnModel.getColumn(1).setMaxWidth(50);
		columnModel.getColumn(1).setMinWidth(50);
		columnModel.getColumn(2).setPreferredWidth(300);
		columnModel.getColumn(3).setMaxWidth(50);
		columnModel.getColumn(3).setMinWidth(50);
	}
	
	public TableCellRenderer getRenderer(String name) {
		return new MyHistoryCellRenderer(this, name);
	}


	
	class MyHistoryCellRenderer implements TableCellRenderer {
		EnterBidHistoryInfo historyinfo;
		String name; // for debug;

		public MyHistoryCellRenderer(EnterBidHistoryInfo n, String name) {
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

			if (column == 2) {   // the "Action" column - should be unlimited in height:
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
		EnterBidHistoryInfo tableModel = new EnterBidHistoryInfo(agent, uspace);
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