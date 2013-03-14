/*
 * UIAgent.java
 *
 * Created on November 16, 2006, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agents;

import javax.swing.*;

import java.awt.*;

import negotiator.Agent;
import negotiator.Bid;
//import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import java.util.*;

/**
 *
 * @author W.Pasman, modified version of Dmytro's UIAgent
 */
public class UIAgentExtended extends Agent{
    private Action opponentAction=null;
    private EnterBidDialogExtended ui=null;
    private Bid myPreviousBid=null;
    
    //Alina added...
    private Bid oppPreviousBid=null;
    protected int bidCounter = 0;
    protected NegoRoundData roundData;
    protected ArrayList<NegoRoundData> historyOfBids = null;

    /** Creates a new instance of UIAgent */
    
    
    /**
     * One agent will be kept alive over multiple sessions.
     * Init will be called at the start of each nego session.
     */
    public static String getVersion() { return "2.0"; }
    public void init()
    {
    	
    	System.out.println("try to init UIAgent");   	
    	System.out.println("Utility Space initialized: "+utilitySpace);
    	historyOfBids = new ArrayList<NegoRoundData>();

        System.out.println("closing old dialog of ");
        if (ui!=null) { ui.dispose(); ui=null; }
        System.out.println("old  dialog closed. Trying to open new dialog. ");
        try { 
        	ui = new EnterBidDialogExtended(this, null, true,utilitySpace); 
        	//alina: dialog in the center- doesnt really work  
        	Toolkit t = Toolkit.getDefaultToolkit();
    		int x = (int)((t.getScreenSize().getWidth() - ui.getWidth()) / 2);
    		int y = (int)((t.getScreenSize().getHeight() - ui.getHeight()) / 2);
    		ui.setLocation(x, y);
        	System.out.println("finished init of UIAgent2");
        }
        catch (Exception e) {
        	System.out.println("Problem in UIAgent2.init:"+e.getMessage()); e.printStackTrace(); 
        	System.out.println("UIAgent could not be initialized");
        }
        
    }

    public void ReceiveMessage(Action opponentAction) {
        this.opponentAction = opponentAction;
        if(opponentAction instanceof Accept)
            JOptionPane.showMessageDialog(null, "Opponent accepted your last offer.");

        if(opponentAction instanceof EndNegotiation)
            JOptionPane.showMessageDialog(null, "Opponent canceled the negotiation session");

        return;
    }
    
    public Action chooseAction() {
        Action action = ui.askUserForAction(opponentAction, myPreviousBid);
        if((action != null)&&(action instanceof Offer)) {
        	myPreviousBid=((Offer)action).getBid(); 
        	if(opponentAction!=null) {
        		oppPreviousBid = ((Offer)opponentAction).getBid();
            	roundData = new NegoRoundData(oppPreviousBid,myPreviousBid);
            	historyOfBids.add(roundData);
        	}
        	//does this happen only the first time?
        	else{
        		roundData = new NegoRoundData(null,myPreviousBid);
            	historyOfBids.add(roundData);
        	}
        	bidCounter++;
        }
        
        return action;
    }
    public boolean isUIAgent() { return true; }
    
    public Bid getMyPreviousBid(){
    	return myPreviousBid;
    }
    public Bid getOppPreviousBid(){
    	return oppPreviousBid;
    }
   
}

class NegoRoundData{
	private Bid lastOppBid;
	private Bid ourLastBid;
		
	public NegoRoundData (Bid lastOppBid, Bid ourLastBid){
		this.lastOppBid = lastOppBid;
		this.ourLastBid = ourLastBid;
	}	
	
	public Bid getOppentBid (){
		return lastOppBid;
	}
	
	public Bid getOurBid (){
		return ourLastBid;
	}
	
}

