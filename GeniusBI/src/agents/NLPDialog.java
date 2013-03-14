/*
 * NLPDialog 
 * version 1 that propose several NLP sentences that are explicitly mapped into bids
 * Created on December 8, 2011
 */

package agents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.exceptions.Warning;
import negotiator.gui.nlp.GrammarPanel;
import negotiator.gui.nlp.GrammarRules;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This version proposes several NLP sentences that are explicitly mapped into bids
 * @author  I. Zuckerman
 */
//@SuppressWarnings("serial")
public class NLPDialog extends JDialog implements ListSelectionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NegoInfo negoinfo; // the table model	
    private negotiator.actions.Action selectedAction;
    private Agent agent;    
    private JTextArea negotiationMessages=new JTextArea("NO MESSAGES YET");  
    // Wouter: we have some whitespace in the buttons,
    // that makes nicer buttons and also artificially increases the window size.
    private JButton buttonAccept=new JButton(" Accept Opponent Bid ");
    //private JButton buttonSkip=new JButton("Skip Turn");
    private JButton buttonEnd=new JButton("End Negotiation");
    private JButton buttonBid=new JButton("       Do Bid       ");
    private JPanel buttonPanel=new JPanel();    
    private JTable BidTable ;
    private JList list;
    private DefaultListModel listModel;
    private ArrayList<String> sentences = new ArrayList<String>();
    private ArrayList<String> sentencesValues = new ArrayList<String>();
    private Bid originalBid;
    
    public NLPDialog(Agent agent, java.awt.Frame parent, boolean modal, UtilitySpace us)  throws Exception
    {
        super(parent, modal);
        this.agent = agent;
        negoinfo=new NegoInfo(null,null,us); 
        initThePanel();
    }
    
    
    // quick hack.. we can't refer to the Agent's utilitySpace because
    // the field is protected and there is no getUtilitySpace function either.
    // therefore the Agent has to inform us when utilspace changes.
    public void setUtilitySpace(UtilitySpace us)
    { negoinfo.utilitySpace=us; }
    
    private void readXML(){
    	try {
    		 
    		File fXmlFile = new File("NLPIssues.xml");
    		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    		Document doc = dBuilder.parse(fXmlFile);
    		doc.getDocumentElement().normalize();
      		
    		NodeList nList = doc.getElementsByTagName("value");

    		for (int temp = 0; temp < nList.getLength(); temp++) {
    			Node nNode = nList.item(temp);
    		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
    		      sentences.add(((Element) nNode).getAttribute("text"));
    		      sentencesValues.add(((Element) nNode).getAttribute("meaning"));
    		   }
    		}
    	  } catch (Exception e) {
    		e.printStackTrace();
    	  }
    }
    
    private void initThePanel() throws IOException {
    	if (negoinfo==null) throw new NullPointerException("negoinfo is null");
    	
    	Container pane=getContentPane();
        pane.setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose action for agent "+agent.getName());
        //setSize(new java.awt.Dimension(600, 400));
        //setBounds(0,0,640,480);

        // create north field: the message field
        pane.add(negotiationMessages,"North");
        
        
        // create center panel: the bid table
        BidTable = new  JTable(negoinfo);
        //BidTable.setModel(negoinfo); // need a model for column size etc...
       	 // Why doesn't this work???
        BidTable.setGridColor(Color.lightGray);
        //String[] values = new String[]{"item1", "item2", "item3"};
        JPanel tablepane=new JPanel(new BorderLayout());
        tablepane.add(BidTable.getTableHeader(), "North");
        tablepane.add(BidTable,"Center");
        pane.add(tablepane,"Center");

        	// create south panel: the buttons:
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonEnd); 
        buttonPanel.add(buttonAccept);
        //buttonPanel.add(buttonSkip);
        buttonPanel.add(buttonBid);
        pane.add(buttonPanel,"South");
        buttonBid.setSelected(true);

       /*
        // INON >>> creating east panel: 
        try{
    		originalBid = negoinfo.getBid();
            if (originalBid == null){
            	originalBid = negoinfo.utilitySpace.getMaxUtilityBid();
            }
    	}catch(Exception e){
    		System.out.println("cannot get Bid from negoinfo");
    	}
        readXML();
        listModel = new DefaultListModel();
        
        for (int i=0;i<sentences.size();i++){
        	listModel.addElement(sentences.get(i));
        }
        */
        
/*        // Questions:
        listModel.addElement("What do you offer?");
        listModel.addElement("How much salary do you offer?");
        listModel.addElement("What is your policy regarding a company car?");
        listModel.addElement("What promotion track do you offer?");
        listModel.addElement("How many hours would I work each day?");
        listModel.addElement("What do you offer regarding pension fund?");
        listModel.addElement("Do we agree?");
        listModel.addElement("Is there anything else we should discuss?");
        // Demands: 
        listModel.addElement("I would like a salary of {7000} per month");
        listModel.addElement("I want to work as a {QA}.");
        listModel.addElement("I need a company car.");
        listModel.addElement("I want a {fast} promotion track.");
        listModel.addElement("I want a daily schedule of {9} hours.");
        listModel.addElement("I want a {7%} pension.");
        // Agreements: 
        listModel.addElement("I can agree to work for a salary of {7000} USD per month.");
        listModel.addElement("I agree to work in a {Programmer} position.");
        listModel.addElement("I can do without a company car.");
        listModel.addElement("I can agree to a {slow} promotion track.");
        listModel.addElement("I can agree on a work day of {10} hours.");
        listModel.addElement("I can agree of {7%} pension.");
        listModel.addElement("I accept your offer.");
        // Rejections: 
        listModel.addElement("This is too little.");
        listModel.addElement("This job description is not good enough for me.");
        listModel.addElement("I must have a car to get to work.");
        listModel.addElement("This is too {slow} for me.");
        listModel.addElement("This is too much for me.");
        listModel.addElement("This is too low.");
        listModel.addElement("I cannot accept your offer.");*/
        
        /*
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        //list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        pane.add(listScrollPane,"East");
        JScrollPane listScrollPane = new JScrollPane(list);
        */
       
        // EREL >>> creating east panel: 
        GrammarPanel grammarPanel = new GrammarPanel(
        		new GrammarRules(
        			new File("etc/templates/JobCandiate/Side_A_NLP.txt"),
        			new File("etc/templates/JobCandiate/Common_NLP.txt")));
        pane.add(grammarPanel,"East");


        // set action listeners for the buttons
        buttonBid.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBidActionPerformed(evt);
            }
        });
        //buttonSkip.addActionListener(new java.awt.event.ActionListener() {
        //    public void actionPerformed(java.awt.event.ActionEvent evt) {
        //        buttonSkipActionPerformed(evt);
        //    }
        //});
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
        pack(); // pack will do complete layout, getting all cells etc.
    }
    
    
    private Bid getBid()
    {
        Bid bid=null;
        try {
            bid =  negoinfo.getBid();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There is a problem with your bid: "+e.getMessage());
        }
        return bid;    	
    }

    
    private void buttonBidActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	
        Bid bid=getBid();
        if (bid!=null) { 
        	selectedAction = new Offer(agent.getAgentID(),bid);         
        	setVisible(false);
        }
    }

    
    private void buttonSkipActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("cancel performed!");
        selectedAction = null;
        setVisible(false);
    }

    private void buttonAcceptActionPerformed(java.awt.event.ActionEvent evt) {
        Bid bid=getBid();
        if (bid!=null) {
        	System.out.println("Accept performed");
        	selectedAction=new Accept(agent.getAgentID());
        	setVisible(false);
        }
    }
    
    private void buttonEndActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("End Negotiation performed");
        selectedAction=new EndNegotiation(agent.getAgentID());
        setVisible(false);
    }
      
    /** 
     * This is called by UIAgent repeatedly, to ask for next action. 
     * @param opponentAction is action done by opponent
     * @param myPreviousBid 
     * @return our next negotiation action.
     */
    public negotiator.actions.Action askUserForAction(negotiator.actions.Action opponentAction, Bid myPreviousBid) 
    {
        negoinfo.opponentOldBid=null;
        if(opponentAction==null) {
        	negotiationMessages.setText("Opponent did not send any action.");            
        }
        if(opponentAction instanceof Accept) {
        	negotiationMessages.setText("Opponent accepted your last bid!");
        	negoinfo.opponentOldBid = myPreviousBid;
        }
        if(opponentAction instanceof EndNegotiation) {
        	negotiationMessages.setText("Opponent cancels the negotiation.");
        }
        if(opponentAction instanceof Offer) {
        	negotiationMessages.setText("Opponent proposes the following bid:");
        	negoinfo.opponentOldBid = ((Offer)opponentAction).getBid();
        }
        try { 
        	negoinfo.setOurBid(myPreviousBid);
        	originalBid = negoinfo.getBid();
        	if (originalBid == null){
            	originalBid = negoinfo.utilitySpace.getMaxUtilityBid();
            }
        } catch (Exception e) {
        	new Warning("error in askUserForAction:",e,true,2);
        }
        
        BidTable.setDefaultRenderer(BidTable.getColumnClass(0),
        		new MyCellRenderer1(negoinfo));
        BidTable.setDefaultEditor(BidTable.getColumnClass(0),new MyCellEditor(negoinfo));

        pack();
        setVisible(true); // this returns only after the panel closes.
         // Wouter: this WILL return normally if Thread is killed, and the ThreadDeath exception will disappear.
        return selectedAction;
    }


	@Override
	/**
	 * This function changes the values in the bid selection combo box as the user
	 * picks different sentences from the dialog box. Note that the (val != 9) expression
	 * limits the number of values per issue to 9 (numbered 0..8).
	 * @author I. Zuckerman
	 */
	public void valueChanged(ListSelectionEvent e) {
		int index = list.getSelectedIndex();
		try{
			Bid temp = originalBid.clone();
			//System.out.println("*** original***" + temp);
			String str = sentencesValues.get(index);
			for (int i=0;i<str.length();i++){
				char nextChar = (str.charAt(i));
				int val = Character.getNumericValue(nextChar);
				if (val != 9){
					IssueDiscrete iss=(IssueDiscrete)negoinfo.issues.get(i);
					ValueDiscrete value=(ValueDiscrete)iss.getValue(val);
					temp.setValue(iss.getNumber(),value);
		        	negoinfo.setOurBid(temp);
		        	//System.out.println("*** new***" + temp);
		        	this.repaint();
				}
			}        
		}catch(Exception exc){
			System.out.println("exception when getting Bid from negoInfo");
		}
	
		
//		try{
//			Bid temp = negoinfo.getBid();
//	        if (index == 8){ //I would like a salary of {7000} per month
//	        	System.out.println(temp.toString());
//				IssueDiscrete iss=(IssueDiscrete)negoinfo.issues.get(0);
//				ValueDiscrete val=(ValueDiscrete)iss.getValue(1);
//	        	temp.setValue(iss.getNumber(),val);
//	        	negoinfo.setOurBid(temp);
//	        	this.repaint();
//	        }
//	        
//		}catch(Exception e1){
//			System.out.println("exception when getting Bid from negoInfo");
//		}
	}
}   
    





