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
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;


/**
 *
 * @author W.Pasman, modified version of Dmytro's UIAgent
 */
public class UIAgentAsync extends Agent{
	protected EnterBidAsyncInterface ui=null;

    //Alina added...
    protected int bidCounter = 0;
    protected NegoRoundData roundData;


    /** Creates a new instance of UIAgent */
    
    
    /**
     * One agent will be kept alive over multiple sessions.
     * Init will be called at the start of each nego session.
     */
    public static String getVersion() { return "2.0"; }
    @Override public void init()  {
    	System.out.println("UIAgentAsync: starting init");   	
        if (ui!=null) { ui.dispose(); ui=null; }
        try { 
        	ui = new EnterBidAsyncInterface(this,utilitySpace);
        	ui.initThePanel();
        	//alina: dialog in the center- doesnt really work  
        	Toolkit t = Toolkit.getDefaultToolkit();
    		int x = (int)((t.getScreenSize().getWidth() - ui.getWidth()) / 2);
    		int y = (int)((t.getScreenSize().getHeight() - ui.getHeight()) / 2);
    		ui.setLocation(x, y);    		
        	System.out.println("UIAgentAsync: finished init");
        }
        catch (Exception e) {
        	System.err.println("Problem in UIAgentAsync.init:"+e.getMessage());
        	e.printStackTrace(); 
        	System.err.println("UIAgentAsync could not be initialized.");
        }
    }

    @Override public void ReceiveMessage(Action opponentAction) {
        ui.updateOpponentAction(opponentAction);
        if(opponentAction instanceof Accept)
            JOptionPane.showMessageDialog(ui, "Opponent accepted your last Action.");

        if(opponentAction instanceof EndNegotiation)
            JOptionPane.showMessageDialog(ui, "Opponent canceled the negotiation session.");

        return;
    }
    
    @Override public Action chooseAction() {        
        return null;
    }
    
    @Override public boolean isUIAgent() { return true; }
    
    @Override public void cleanUp() {
    	JOptionPane.showMessageDialog(ui, "Negotiation has ended.");
    	if (ui!=null) { 
    		ui.dispose(); 
    		ui=null; 
    	}
    }

}