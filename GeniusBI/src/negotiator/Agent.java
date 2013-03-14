/*
 * Agent.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import negotiator.actions.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import negotiator.utility.UtilitySpace;

import java.util.Date;

import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;


/**
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman
 * 
 */


public abstract class Agent implements ActionReceiver{
	private AgentID 		agentID;
    private String          fName=null;
    private String          userID=null; 
    private Locale			myLocale=null;
    private ActionListener	actionSender=null; // event handler for action performing on asynch protocols
    
    public  UtilitySpace    utilitySpace;
    public WorldInformation worldInformation=null; // hold available information about the world
    public	Date			startTime;
    public Integer			totalTime; // total time to complete entire nego, in seconds.
    public Integer			numOfTurns; // number of turns to complete entire nego.
    public Integer			turnLength; // length of turn, in seconds.
    public Integer			sessionNumber;
    public Integer			sessionTotalNumber;
    public BilateralAtomicNegotiationSession 	fNegotiation;// can be accessed only in the experimental setup 
     // Wouter: disabled 21aug08, are not necessarily run from a negotiation session.
     // particularly we now have NegotiationSession2 replacing NegotiationSession.
    
    
    //protected Hashtable<String,Double> parametervalues = new Hashtable<String, Double>(); // values for the parameters for this agent. Key is param name

    /** Erel: It seems that these parameters are used for tournaments */
    protected HashMap<AgentParameterVariable,AgentParamValue> parametervalues;

    public Agent() {  }

    public Agent(String defaultAgentID) {  
    	agentID = new AgentID(defaultAgentID);
    }

    public static String getVersion() {return "unknown";};
    /**
     * This method is called by the environment (SessionRunner) every time before starting a new 
     * session after the internalInit method is called. User can override this method. 
     */
    public void init() {
    
    }
    
    /**
     * This method is called by the SessionRunner to initialize the agent with a new session information.
     * @param sessionNumber number of the session
     * @param sessionTotalNumber total number of sessions
     * @param startTimeP - the time the negotiation started
     * @param totalTimeP - the total time of negotiation (in sec)
     * @param turnLengthP - if time has effect on negotiation, what the length (in sec) of each time step 
     * @param us utility space of the agent for the session
     * @param params parameters of the agent
     */
    public final void internalInit(int sessionNumber, int sessionTotalNumber, Date startTimeP, 
    		Integer totalTimeP,Integer turnLengthP,
    		UtilitySpace us, HashMap<AgentParameterVariable,AgentParamValue> params,WorldInformation wi) {
        startTime=startTimeP;
        totalTime=totalTimeP;
        turnLength=turnLengthP;
        numOfTurns = totalTime/turnLength;
        this.sessionNumber = sessionNumber;
        this.sessionTotalNumber = sessionTotalNumber;
    	utilitySpace=us;
    	worldInformation=wi;
    	parametervalues=params;
        return;
    }
    
    /**
     * informs you which action the opponent did
     * @param opponentAction
     */
    public void ReceiveMessage(Action opponentAction) {
        return;
    }
    
    /**
     * this function is called after ReceiveMessage,
     * with an Offer-action.
     * @return (should return) the bid-action the agent wants to make.
     */
    public Action chooseAction() {
        return null;
    }
    
    public String getName() {
        return fName;
    }
    
    /**
     * added W.Pasman 19aug08
     * @return arraylist with all parameter names that this agent can handle
     * defaults to an empty parameter list. Override when you use parameters.
     */
    public static ArrayList<AgentParam> getParameters() { 
    	return new ArrayList<AgentParam>();
    	}
    
    public HashMap<AgentParameterVariable,AgentParamValue> getParameterValues() {
    	return parametervalues;
    }
    
    
    public final Agent setName(String pName) {
        if(this.fName==null) this.fName = pName;
        return this;
    }
    
    
    public String getUserID() {
		return userID;
	}

	public Agent setUserID(String userID) {
		this.userID = userID;
		return this;
	}

	/**
	 * @param myLocale the myLocale to set
	 */
	public Agent setMyLocale(Locale myLocale) {
		this.myLocale = myLocale;
		return this;
	}

	/**
	 * @return the myLocale
	 */
	public Locale getMyLocale() {
		return myLocale;
	}

	/**
     * @author W.Pasman
     * determine if this agent is communicating with the user about nego steps.
     * @return true if a human user is directly communicating with the agent in order
     * to steer the nego. This flag is used to determine the timeout for the
     * negotiation (larger with human users).
     */
    public boolean isUIAgent() { return false; }
    
    /**
     * This function cleans up the remainders of the agent: open windows etc.
     * This function will be called when the agent is killed,
     * typically when it was timed out in a nego session.
     * The agent will not be able to do any negotiation actions here, just clean up.
     * To ensure that the agent can not sabotage the negotiation, 
     * this function will be called from a separate thread.
     * 
     * @author W.Pasman
     */
    public void cleanUp() {  }
    
    public AgentID getAgentID() {
    	return agentID;
    }
    public Agent setAgentID(AgentID value) {
    	agentID = value;
    	return this;
    }
    
    /** 
     * Set the name and the Agent ID to the same value
     * @param newName the new name and agent id
     * @return this (for chaining)
     */
    public Agent setNameAndId(String newName) {
    	setName(newName);
    	setAgentID (new AgentID(newName));
    	return this;
    }
    
    /**
     * Used by Negotiation Session to set a listener for the asynchronous actions of this agent 
     * @param al
     * @author Yinon Oshrat
     */
    public final Agent setActionListener(ActionListener al) {
    	actionSender=al;
    	return this;
    }
    /**
     * Use this function to perform event in asynchronous protocols
     * @param a - the action to perform
     * @author Yinon Oshrat
     */
    public final void sendAction(Action a) {
		// test for cheating
    	AgentID id=getAgentID();
    	AgentID actionId=a.getAgent();
    	if (id !=null && id.equals(actionId)) 
    		if (actionSender!=null)
    			actionSender.actionSent(a);
    }

	public Agent setDebug(boolean b) {
		// TODO Auto-generated method stub
		return this;
	}
}
