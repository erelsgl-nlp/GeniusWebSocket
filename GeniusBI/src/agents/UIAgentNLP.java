/*
 * UIAgentNLP.java
 *
 * Created on December 8, 2011

 */

package agents;

import javax.swing.JOptionPane;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;

/**
 * A negotiation agent activated by a user using a GUI.
 * 
 * @author  Inon Zuckerman
 */
public class UIAgentNLP extends Agent{
    private Action opponentAction=null;
   // private EnterBidDialog ui=null;
    private NLPDialog nlpd = null;
    private Bid myPreviousBid=null;

    public static String getVersion() { return "1.0"; }
    
    /**
     * One agent will be kept alive over multiple sessions.
     * init will be called at the start of each nego session.
     */
    public void init()
    {
    	System.out.println("init UIAgent");
        
        System.out.println("closing old dialogs");
        if (nlpd!=null) { 
        	//ui.dispose(); ui=null;
        	nlpd.dispose(); nlpd=null;
        }
        System.out.println("old  dialog closed. Trying to open new dialog. ");
        try { 
        	//ui = new EnterBidDialog(this, null, true,utilitySpace);
        	nlpd = new NLPDialog(this, null, true,utilitySpace);
        }
        catch (Exception e) {System.out.println("Problem in UIAgent2.init:"+e.getMessage()); e.printStackTrace(); }
        System.out.println("finished init of UIAgentNLP");
    }

    @Override public void ReceiveMessage(Action opponentAction) {
        this.opponentAction = opponentAction;
        if(opponentAction instanceof Accept)
            JOptionPane.showMessageDialog(null, "Opponent accepted your last offer.");

        if(opponentAction instanceof EndNegotiation)
            JOptionPane.showMessageDialog(null, "Opponent canceled the negotiation session");

        return;
    }
    
    @Override public Action chooseAction() {
        Action action = nlpd.askUserForAction(opponentAction, myPreviousBid);
        if((action != null)&&(action instanceof Offer)) myPreviousBid=((Offer)action).getBid(); 
        return action;
    }
  
    public boolean isUIAgent() { return true; }

}
