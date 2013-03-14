/*
 * ProgressUI2.java
 *
 * Created on September 8, 2008, 3:24 PM
 */

package negotiator.gui.progress;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import negotiator.NegotiationEventListener;
import negotiator.actions.Accept;
import negotiator.actions.AcceptOrReject;
import negotiator.actions.BidAction;
import negotiator.actions.CounterOffer;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEndedEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.gui.chart.BidChart;
import negotiator.protocol.Protocol;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.protocol.BilateralAtomicNegotiationSession.BidHolder;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author  dmytro
 */
public class ProgressUI2 extends javax.swing.JPanel implements NegotiationEventListener {
	
	private ProgressInfo progressinfo; // the table model
	protected int row = 0;
	private BidChart bidChart;
	protected BilateralAtomicNegotiationSession session;
	private TextArea logText;
	private JPanel chart;

	BlockingQueue<negotiator.events.ActionEvent> actionsQueue = new LinkedBlockingQueue<negotiator.events.ActionEvent>();
		
    /** Creates new form ProgressUI2 */
    public ProgressUI2() {
        initComponents();
		bidChart = new BidChart();
		progressinfo = new ProgressInfo();
		//biddingTable = new  JTable(progressinfo);
		biddingTable.setModel(progressinfo);
		biddingTable.setGridColor(Color.lightGray);
		ProgressUI1("initialized...",bidChart,biddingTable);
    }
    
    public void fillGUI(BilateralAtomicNegotiationSession ng){
    	setNegotiationSession(ng);
    	setLogText(session.getLog());
    	addGraph();
    	addTableData();
    }
    
	public void ProgressUI1 (String logging,BidChart bidChart, JTable bidTable){
		Container pane = pnlChart;
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		//the chart panel 
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
        JFreeChart plot = bidChart.getChart();
        chart = new ChartPanel(plot);
        chart.setMinimumSize(new Dimension(350, 350)); 
        chart.setBorder(loweredetched);
        c.insets = new Insets(10, 0, 0, 10);
        c.ipadx = 10;
		c.ipady = 10;
        pane.add(chart,c);

        pnlChart.add(chart);
		logText = new TextArea();
		
		logText.setText("");
		
	}
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        pnlChart = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        biddingTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextArea();

        jSplitPane2.setName("jSplitPane2"); // NOI18N

        setName("Form"); // NOI18N

        jSplitPane1.setDividerSize(3);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane3.setDividerSize(3);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        pnlChart.setBorder(javax.swing.BorderFactory.createTitledBorder("Negotiation dynamics chart"));
        pnlChart.setName("pnlChart"); // NOI18N

        org.jdesktop.layout.GroupLayout pnlChartLayout = new org.jdesktop.layout.GroupLayout(pnlChart);
        pnlChart.setLayout(pnlChartLayout);
        pnlChartLayout.setHorizontalGroup(
            pnlChartLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 375, Short.MAX_VALUE)
        );
        pnlChartLayout.setVerticalGroup(
            pnlChartLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 74, Short.MAX_VALUE)
        );

        jSplitPane3.setTopComponent(pnlChart);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Exchanged offers"));
        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        biddingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        biddingTable.setName("biddingTable"); // NOI18N
        jScrollPane2.setViewportView(biddingTable);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
        );

        jSplitPane3.setRightComponent(jPanel3);

        jSplitPane1.setRightComponent(jSplitPane3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Negotiation log"));
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textOutput.setColumns(20);
        textOutput.setRows(5);
        textOutput.setName("textOutput"); // NOI18N
        jScrollPane1.setViewportView(textOutput);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable biddingTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JPanel pnlChart;
    private javax.swing.JTextArea textOutput;
    // End of variables declaration//GEN-END:variables

    private double[][] getPareto(){
		double [][] pareto=null;
		ArrayList <BidPoint>paretoBids = null;
		BidSpace bs = session.getBidSpace();
		if(bs==null)System.out.println("bidspace == null");
		else{
			try {
				paretoBids = bs.getParetoFrontier();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(paretoBids!=null){
				pareto = new double [2][paretoBids.size()];
				for(int i=0; i<paretoBids.size();i++) 
				  {
					pareto[0][i]= paretoBids.get(i).utilityA;
					pareto[1][i]= paretoBids.get(i).utilityB;
				  }
			}
		}
		return pareto;
	}
	
	private double[][] getAllBidsInBidSpace(){
		//save the possible bids in double [][] and display in graph 
		double [][] possibleBids=null;
		BidSpace bs = session.getBidSpace();
		if(bs.bidPoints.size()>300000) return possibleBids;
		if(bs==null)System.out.println("bidspace == null");
		else{
			ArrayList<BidPoint> allBids = bs.bidPoints;// always gives a nullpointer
			if(allBids!=null){
				possibleBids = new double [2][allBids.size()];
				int i=0;
				for(BidPoint p: bs.bidPoints) 
				  {possibleBids[0][i]= p.utilityA; possibleBids[1][i]= p.utilityB; i++;}
				//bidChart.setPossibleBids(possibleBids);
			}else{
				System.out.println("possibleBids is null");
			}
		}
		return possibleBids;
	}
	
	public void addLoggingText(String t){
		textOutput.append(t+"\n");
		session.setLog(textOutput.getText());
	}
	
	/** run this for a demo of ProgressnUI */
/*	public static void main(String[] args) 
	{
		//create sample data:
		double [][] possibleBids = new double [2][1000];
		for(int i=0;i<1000;i++){
			possibleBids [0][i]= Math.random();
			possibleBids [1][i]= Math.random();
		}
		double[][] pareto = new double [2][4];
		double [][] bidSeriesA = new double [2][4];
		double [][] bidSeriesB = new double [2][4];
		
		for(int i=0;i<4;i++){
			double paretox = Math.random();
			if (paretox<0.5)paretox+=0.5;
			double paretoy = Math.random();
			if (paretoy<0.5)paretoy+=0.5;
			if (i==0)
				pareto [1][0]=1;
			if (i==3)
				pareto [0][3]=1;
													
			pareto [0][i]= paretox;
			pareto [1][i]= paretoy;
			bidSeriesA [0][i]= Math.random();
			bidSeriesA [1][i]= Math.random();
			bidSeriesB [0][i]= Math.random();
			bidSeriesB [1][i]= Math.random();
		}
		
		BidChart myChart = new BidChart();
		JTable myTable = new JTable(5,5);
		try {
			new ProgressUI("Logging started...",myChart,myTable); 
		} catch (Exception e) { new Warning("ProgressUI failed to launch: ",e); }
		
		//when the dataset is changes the chart is automatically updated
		myChart.setPossibleBids(possibleBids);
		//myChart.setPareto(pareto);
		myChart.setBidSeriesA(bidSeriesA);
		myChart.setBidSeriesB(bidSeriesB);
	}*/
	public void setLogText(String str){
		textOutput.setText(str);
	}
	
	public void setNegotiationSession(BilateralAtomicNegotiationSession nego){
		nego.addNegotiationEventListener(this);
		session = nego;
		bidChart.setAgentBName("Agent B:"+nego.getAgentBname());
		bidChart.setAgentAName("Agent A:"+nego.getAgentAname());
		BidSpace bs = session.getBidSpace();
		double [][] pb = null;//getAllBidsInBidSpace();
		double [][] nash = new double [2][1];
		double [][] kalai = new double [2][1];
		try {
			synchronized (this) {
				if(pb!=null)
					bidChart.setPossibleBids(pb);
				double [][] paretoB = getPareto();
				if(paretoB!=null)
					bidChart.setPareto(paretoB);
	
				//nash
				BidPoint bp1= bs.getNash();
				nash[0][0]= bp1.utilityA;
				nash[1][0]= bp1.utilityB;
				if(nash!=null)
					bidChart.setNash(nash);	
				//kalai
				BidPoint bp2 = bs.getKalaiSmorodinsky();
				kalai[0][0]= bp2.utilityA;
				kalai[1][0]= bp2.utilityB;
				if(kalai!=null)
					bidChart.setKalai(kalai);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/** TODO Wouter: this should be taken out of the agent's CPU time. Currently
	 * this does not follow my warning/prescribed way to do this (see the handleActionEvent docuemntation)
	 */
	public void handleActionEvent(negotiator.events.ActionEvent evt) {
		// the weird structure of the following conditions is for readability
		if (evt.getAction() == null) return;
		else if (!(evt.getAction() instanceof BidAction) && !(evt.getAction() instanceof Accept)) return;
		if (evt.getAction() instanceof Accept) {
			BidAction ac=((AcceptOrReject)evt.getAction()).getAcceptedOrRejectedAction();
			if (!(ac instanceof Offer) && !(ac instanceof CounterOffer))
				return;
		}
		
		actionsQueue.add(evt);
		
		SwingUtilities.invokeLater(processActionUpdates); 
	}
	

	public void addGraph(){
		//adding graph data:
		double [][] curveA = session.getNegotiationPathA();
		double [][] curveB = session.getNegotiationPathB();
		if(curveA!=null)
			bidChart.setBidSeriesA(curveA);
		if(curveB!=null)
			bidChart.setBidSeriesB(curveB);	
		System.out.println("curveA length "+(curveA.length-1));
		/*
		double [][]ap = new double [2][1];
		ap[0][0]= curveA[0][curveA.length-1];
		ap[1][0]= curveA[1][curveA.length-1];
		bidChart.setAgreementPoint(ap);
		*/
	}
	
	public void addTableData(){
		/*
		//System.out.println("updating the table...");
		double [][] curveA = session.getNegotiationPathA();
		double [][] curveB = session.getNegotiationPathB();
		System.out.println(curveA.length);
		System.out.println(curveA[0].length);
		System.out.println(curveB.length);
		System.out.println(curveB[0].length);
		
		// Wouter:due to a bug both paths contained all values twice.
		double[][] curve0=curveA;
		double[][] curve1=curveB;
		
		String starter = session.getStartingAgent();
		String second="";
		if(starter.equals("Agent A")){
			second = "Agent B";
		}else{
			second = "Agent A";
			curve0=curveB; // Agent B started, then we need curveB at the even points.
			curve1=curveA;
		}
		System.out.println("nr of bids in session "+session.getNrOfBids());
		for(int i=0;i<session.getNrOfBids();i++){
			if(i>=biddingTable.getModel().getRowCount()){
				//System.out.println("i bigger than row count "+i);
				progressinfo.addRow();
			}
			//round = evt.getRound();
			biddingTable.getModel().setValueAt(i+1,i,0);
			if (i%2 == 0){
				biddingTable.getModel().setValueAt(starter,i,1);
				 // round 0 and 1 both need point 0 in a curve, round 2 and 3 point 1 in a curve, etc.
				biddingTable.getModel().setValueAt(curve0[0][i/2],i,2);
				biddingTable.getModel().setValueAt(curve0[1][i/2],i,3);
			}else{
				biddingTable.getModel().setValueAt(second,i,1);
				biddingTable.getModel().setValueAt(curve1[0][i/2],i,2);
				biddingTable.getModel().setValueAt(curve1[1][i/2],i,3);

			}
			
			//try{
			//	biddingTable.getModel().setValueAt(curveA[0][i / 2],i,2);
			//	biddingTable.getModel().setValueAt(curveB[0][i/2],i,3);
			//}catch(ArrayIndexOutOfBoundsException e){
			//	System.out.println("out of bounds "+i);
			//}
			
		}*/
		int i=0;
		for (BidHolder holder:session.getAllBids()) {
			if(i>=progressinfo.getRowCount())
				progressinfo.addRow();
			progressinfo.setValueAt(holder.round,i,0);
			progressinfo.setValueAt(holder.agent,i,1);
			progressinfo.setValueAt(holder.bidPoint.utilityA,i,2);
			progressinfo.setValueAt(holder.bidPoint.utilityB,i,3);
		}
	
	}
	
	public void resetGUI(){
		//clear TextArea:
		//logText.setText("");
		textOutput.setText("");
		//clear graph
		bidChart.removeAllPlots();
		//clear table
		progressinfo.reset();
		row = 0;
	}
	
	public void handleLogMessageEvent(LogMessageEvent evt) {
		addLoggingText(evt.getMessage());	
	}

	public void handleNegotiationSessionEvent(NegotiationSessionEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void handleBlateralAtomicNegotiationSessionEvent(
			BilateralAtomicNegotiationSessionEvent evt) {
		// TODO Auto-generated method stub
		setNegotiationSession(evt.getSession());
	}

	/* (non-Javadoc)
	 * @see negotiator.NegotiationEventListener#handleNegotiationEndedEvent(negotiator.events.NegotiationEndedEvent)
	 */
	public void handleNegotiationEndedEvent(NegotiationEndedEvent evt) {
					
		synchronized (this) {
			//adding graph data:
			double [][] curveA = session.getNegotiationPathA();
			double [][] curveB = session.getNegotiationPathB();
			if(curveA!=null)
				bidChart.setBidSeriesA(curveA);
			if(curveB!=null)
				bidChart.setBidSeriesB(curveB);	
			
			
			double [][]ap = new double [2][1];
			ap[0][0]= evt.getUtilityA();
			ap[1][0]= evt.getUtilityB();
			bidChart.setAgreementPoint(ap);
		}

		
	}	

	// Runnable that processes all enqueued events.  Much more efficient than: 
	// a) Creating a new Runnable each time. 
	// b) Processing one Message per call to run(). 
	private final Runnable processActionUpdates = new Runnable() { 
		public synchronized void run() { 
			negotiator.events.ActionEvent  evt; 
		 
		    while ((evt = actionsQueue.poll()) != null) { 
				row+=1;
				if(row>progressinfo.getRowCount()){
					progressinfo.addRow();
				}
				progressinfo.setValueAt(evt.getRound(),row-1,0);
				progressinfo.setValueAt(evt.getAgentAsString(),row-1,1);
				progressinfo.setValueAt(evt.getNormalizedUtilityA(),row-1,2);
				progressinfo.setValueAt(evt.getNormalizedUtilityB(),row-1,3);

				//adding graph data:
				double [][] curveA = session.getNegotiationPathA();
				double [][] curveB = session.getNegotiationPathB();
				if(curveA!=null)
					bidChart.setBidSeriesA(curveA);
				if(curveB!=null)
					bidChart.setBidSeriesB(curveB);	
				
				if ((evt.getAction()instanceof Accept)){
					double [][]ap = new double [2][1];
					ap[0][0]= evt.getNormalizedUtilityA();
					ap[1][0]= evt.getNormalizedUtilityB();
					bidChart.setAgreementPoint(ap);
				}
		    } 
		} 
	};

}


