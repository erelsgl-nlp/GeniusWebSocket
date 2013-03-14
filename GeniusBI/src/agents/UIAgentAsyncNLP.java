/*
 * UIAgent.java
 *
 * Created on November 16, 2006, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agents;

import java.awt.Toolkit;

import negotiator.gui.nlp.GrammarRules;
import negotiator.gui.nlp.GrammarToGeniusBridge;


/**
 * NLP version of {@link UIAgentAsyncNLP}
 * @author Erel Segal
 * @since 20/12/2011
 */
public class UIAgentAsyncNLP extends UIAgentAsync {
    public static String getVersion() { return "2.0"; }
    
    /**
     * One agent will be kept alive over multiple sessions.
     * Init will be called at the start of each nego session.
     */
   @Override public void init()   {
    	System.out.println("UIAgentAsyncNLP: starting init");   	
        if (ui!=null) { ui.dispose(); ui=null; }
        try { 
        	GrammarRules ourGrammarRules = GrammarToGeniusBridge.grammarRulesFromUtilitySpace(utilitySpace);
        	GrammarRules opponentGrammarRules = GrammarToGeniusBridge.grammarRulesFromUtilitySpace(this.worldInformation.getUtilitySpace(0));
        	ui = new EnterBidAsyncNLP(this, utilitySpace, ourGrammarRules, opponentGrammarRules); 
        	ui.initThePanel();
        	//alina: dialog in the center- doesnt really work  
        	Toolkit t = Toolkit.getDefaultToolkit();
    		int x = (int)((t.getScreenSize().getWidth() - ui.getWidth()) / 2);
    		int y = (int)((t.getScreenSize().getHeight() - ui.getHeight()) / 2);
    		ui.setLocation(x, y);    		
        	System.out.println("UIAgentAsyncNLP: finished init");
        }
        catch (Exception e) {
        	System.err.println("Problem in UIAgentAsyncNLP:"+e.getMessage());
        	e.printStackTrace(); 
        	System.err.println("UIAgentAsyncNLP could not be initialized.");
        }
    }
}