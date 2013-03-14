package negotiator.protocol.alternatingoffers;

import java.util.Date;
import java.util.HashMap;

import agents.*;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Domain;
import negotiator.Global;
import negotiator.NegotiationOutcome;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.IllegalAction;
import negotiator.actions.Offer;
import negotiator.analysis.BidPoint;
import negotiator.events.NegotiationEndedEvent.AgreementType;
import negotiator.exceptions.Warning;
import negotiator.protocol.BilateralAtomicNegotiationSession;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

public class AlternatingOffersBilateralAtomicNegoSession extends BilateralAtomicNegotiationSession {

	//AlternatingOffersNegotiationSession session;
    /**
     * stopNegotiation indicates that the session has now ended.
     * it is checked after every call to the agent,
     * and if it happens to be true, session is immediately returned without any updates to the results list.
     * This is because killing the thread in many cases will return Agent.getAction() but with
     * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
     */
    public boolean stopNegotiation=false;
	
    

    public NegotiationOutcome no;

    protected boolean agentAtookAction = false;
    protected boolean agentBtookAction = false;
    protected String startingAgent;

	boolean startingWithA=true;    
    /* time/deadline */
    Date startTime; 
    long startTimeMillies; //idem.
	private Integer totalTime = 180000;
    Integer totTime; // total time, seconds, of this negotiation session.
    private int sessionTotalNumber = 1;
    private AlternatingOffersProtocol protocol;
    
	public Agent currentAgent=null; // agent currently bidding.

	/**
	 * <p>If true, the negotiation will terminate when one of the agents chooses an action that is not known to the protocol (not Offer, Accept or EndNegotiation).
	 * <p>If false, the negotiation will continue and nothing will happen. 
	 */
	public static boolean breakOnUnknownActionTypes = true;
	
     /** load the runtime objects to start negotiation */
    public AlternatingOffersBilateralAtomicNegoSession(AlternatingOffersProtocol protocol,
    		Agent agentA,
			Agent agentB, 
			String agentAname, 
			String agentBname,
			UtilitySpace spaceA, 
			UtilitySpace spaceB, 
			HashMap<AgentParameterVariable,AgentParamValue> agentAparams,
			HashMap<AgentParameterVariable,AgentParamValue> agentBparams,
			String startingAgent,
			int totalTime) throws Exception {
    	
		super(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB, agentAparams, agentBparams,null,null);
		this.protocol = protocol;
		this.startingAgent = startingAgent;
        this.totTime = totalTime;
	}
    
    /**
     * a parent thread will call this via the Thread.run() function.
     * Then it will start a timer to handle the time-out of the negotiation.
     * At the end of this run, we will notify the parent so that he does not keep waiting for the time-out.
     */
    public void run() {
		startTime=new Date(); startTimeMillies=System.currentTimeMillis();
        try {
            double agentAUtility,agentBUtility;

            // note, we clone the utility spaces for security reasons, so that the agent
        	 // can not damage them.
            agentA.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,totalTime,
            		new UtilitySpace(spaceA),agentAparams,null);
            agentA.init();
            agentB.internalInit(sessionNumber, sessionTotalNumber,startTime,totalTime,totalTime,
            		new UtilitySpace(spaceB),agentBparams,null);
            agentB.init();
            stopNegotiation = false;
            Action action = null;
            
            if (startingAgent.equals(agentAname)) currentAgent=agentA;
           	else currentAgent=agentB;
            
            // this is done here so the event is thrown only after the agents were initalized
            if (protocol!=null)
	            protocol.fireBilateralAtomicNegotiationSessionEvent(this, 
	            		protocol.getProfileArep(), protocol.getProfileBrep(),
	            		protocol.getAgentARep(), protocol.getAgentBRep());

        	//System.out.println("starting with agent "+currentAgent);
        	fireLogMessage("Nego","Agent " + currentAgent.getName() + " begins");
        	int round=0;
            while(!stopNegotiation) {
                try {
                   //inform agent about last action of his opponent
                   currentAgent.ReceiveMessage(action);
                   if (stopNegotiation) return;
                   //get next action of the agent that has its turn now
                   action = currentAgent.chooseAction();
                   if (stopNegotiation) return;
                   round++;
                   action.setRound(round);
                   if(action instanceof EndNegotiation) 
                   {
                       stopNegotiation=true;
                       //double utilA=spaceA.getUtility(spaceA.getMaxUtilityBid()); // normalized utility
                       //double utilB=spaceB.getUtility(spaceB.getMaxUtilityBid());
                       newOutcome(currentAgent,0.,0., action, "Agent "+currentAgent.getName()+" ended the negotiation without agreement");
                       checkAgentActivity(currentAgent) ;
                   }
                   else if (action instanceof Offer) {
                       //Main.log("Agent " + currentAgent.getName() + " sent the following offer:");
                       fireLogMessage("Nego","Agent " + currentAgent.getName() + " sent the following offer:");
                       lastBid  = ((Offer)action).getBid();                       
                       //Main.log(action.toString());
                       fireLogMessage("Nego",action.toString());
                       double utilA=agentA.utilitySpace.getUtility(lastBid);
                       double utilB=agentB.utilitySpace.getUtility(lastBid);
                       //Main.log("Utility of " + agentA.getName() +": " + utilA);
                       fireLogMessage("Nego","Utility of " + agentA.getName() +": " + utilA);
                       //Main.log("Utility of " + agentB.getName() +": " + utilB);
                       fireLogMessage("Nego","Utility of " + agentB.getName() +": " + utilB);
                       //save last results 
                       BidPoint p=null;
               		   p=new BidPoint(lastBid,
               				   spaceA.getUtility(lastBid),
               				   spaceB.getUtility(lastBid));
               		   BidHolder holder=new BidHolder();
               		   holder.bidPoint=p;
               		   holder.round=round;
                       if(currentAgent.equals(agentA))                    {
                    	   fAgentABids.add(p);
                    	   holder.agent=agentA.getName();
                       } else{
                    	   fAgentBBids.add(p);
                    	   holder.agent=agentB.getName();
                       }
                       fAllBids.add(holder);
	                   fireNegotiationActionEvent(currentAgent,action, System.currentTimeMillis()-startTimeMillies,utilA,utilB,"bid by "+currentAgent.getName());
	                	
                       checkAgentActivity(currentAgent) ;
                   }                   
                   else if (action instanceof Accept) {
                       stopNegotiation = true;
                       //Accept accept = (Accept)action;
                       if(lastBid==null)
                    	   throw new Exception("Accept was done by "+
                    			   currentAgent.getName()+" but no bid was done yet.");
                        Global.logStdout("AlternatingOffersBilateralAtomicNegoSession:Accept", "action="+action, null);
                        Global.logStdout("AlternatingOffersBilateralAtomicNegoSession:Accept", "lastBid="+action, null);
                        lastBid.setTime(action.getRound());
                        agentAUtility = spaceA.getUtility(lastBid);
                        agentBUtility = spaceB.getUtility(lastBid);
                        Global.logStdout("AlternatingOffersBilateralAtomicNegoSession", "utilities="+agentAUtility+" "+agentBUtility, null);
                        newOutcome(currentAgent, agentAUtility,agentBUtility,action, null);
                        checkAgentActivity(currentAgent) ;
                        otherAgent(currentAgent).ReceiveMessage(action);                      
                   } else {  // action instanceof unknown action, e.g. null.
                	   if (breakOnUnknownActionTypes)
                		   throw new Exception("unknown action by agent "+currentAgent.getName());
                   }
                } catch(Exception e) {
                   new Warning("Caught exception:",e,true,2);
                   stopNegotiation=true;
                   new Warning("Protocol error by Agent"+currentAgent.getName(),e,true,3);
             	   //Global.log("Protocol error by Agent " + currentAgent.getName() +":"+e.getMessage());
                   if (lastBid==null) agentAUtility=agentBUtility=1.;
                   else {
                	   agentAUtility=agentBUtility=0.;
                	   // handle both getUtility calls apart, if one crashes
                	   // the other should not be affected.
                	   try {
                		   agentAUtility = spaceA.getUtility(lastBid);
                	   }  catch (Exception e1) {}
                	   try {
                    	   agentBUtility = spaceB.getUtility(lastBid);
                	   }  catch (Exception e1) {}
                   }
                   if (currentAgent==agentA) agentAUtility=0.; else agentBUtility=0.;
                   try {
                	   newOutcome(currentAgent, agentAUtility,agentBUtility,action, "Agent " + currentAgent.getName() +":"+e.getMessage());
                   }
                   catch (Exception err) { err.printStackTrace(); new Warning("exception raised during exception handling: "+err); }
                   // don't compute the max utility, we're in exception which is already bad enough.
                }
                // swap to other agent
                if(currentAgent.equals(agentA))     currentAgent = agentB; 
                else   currentAgent = agentA;
            }
            
            // nego finished by Accept or illegal action.
            // notify parent that we're ready.
            if (protocol!=null)
            	synchronized (protocol) {  protocol.notify();  }
           
            /*
             * Wouter: WE CAN NOT DO MORE PROCESSING HERE!!!!!
             * Maybe even catching the ThreadDeath error is wrong. 
             * If we do more processing, we risk getting a ThreadDeath exception
             * causing Eclipse to pop up a dialog bringing us into the debugger.
             */            
            
        } catch (Error e) {
            if(e instanceof ThreadDeath) {
            	System.out.println("Nego was timed out");
                // Main.logger.add("Negotiation was timed out. Both parties get util=0");
            	// if this happens, the caller will adjust utilities.
           }     
             
        }

    }

	
    
    /** This is the running method of the negotiation thread.
     * It contains the work flow of the negotiation. 
     */
    void checkAgentActivity(Agent agent) {
        if(agent.equals(agentA)) agentAtookAction = true;
        else agentBtookAction = true;
        
    }

  
    public Agent otherAgent(Agent ag)
    {
    	if (ag==agentA) return agentB;
    	return agentA;    	
    }
  
    
    public void newOutcome(Agent currentAgent, double utilA, double utilB, Action action, String message) throws Exception {
        
    	no=new NegotiationOutcome(sessionNumber, 
			   agentA.getName(),  agentB.getName(),
            agentA.getClass().getCanonicalName(), agentB.getClass().getCanonicalName(),
            utilA,utilB,
            0.0,0.0,
            message,
            fAgentABids,fAgentBBids,
            spaceA.getUtility(spaceA.getMaxUtilityBid()),
            spaceB.getUtility(spaceB.getMaxUtilityBid()),
            startingWithA, 
            spaceA.getFileName(),
            spaceB.getFileName(),
            AgreementType.Full,
            additionalLog            
            );
    	action.setRound(sessionNumber);
    	fireNegotiationActionEvent(currentAgent,action, System.currentTimeMillis()-startTimeMillies,utilA,utilB,message);
    	
    	fireNegotiationEndedEvent(System.currentTimeMillis()-startTimeMillies, utilA, utilB, currentAgent.getAgentID(), AgreementType.Full, null, message);
    	
    }
    
    /**
     * This is called whenever the protocol is timed-out. 
     * What happens in case of a time-out is 
     * (1) the sessionrunner is killed with a Thread.interrupt() call  from the NegotiationSession2.
     * (2) judgeTimeout() is called.
     * @author W.Pasman
     */
    public void JudgeTimeout() {
		try {
    		newOutcome(currentAgent, 0, 0, new IllegalAction(currentAgent.getAgentID(),"negotiation was timed out"),"negotiation was timed out");
    		} catch (Exception err) { new Warning("error during creation of new outcome:",err,true,2); }
    		// don't bother about max utility, both have zero anyway.

    }
    public NegotiationOutcome getNegotiationOutcome() {
    	return no;
    }
	public String getStartingAgent() {
		return startingAgent;
	}
	public void setStartingWithA(boolean val) { 
		startingWithA = val;
	}
	public void setTotalTime(int val) {
		totalTime = val;
	}
	public void setSessionTotalNumber(int val) {
		sessionTotalNumber = val;
	}
	
	
	/**
	 * Main program for testing only
	 * 
	 * @author Erel Segal
	 * @date 2011-02-09
	 * @param args[0] path to xml file defining the domain.
	 * 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length<3) {
			System.out.println("SYNTAX: java Alt...ion <path-to-domain>.xml <path-to-profile-a>.xml <path-to-profile-b>.xml");
			return;
		}
		Domain domain = new Domain(args[0]);
		String ProfileA = args[1];
		String ProfileB = args[2];
		AlternatingOffersBilateralAtomicNegoSession session = new AlternatingOffersBilateralAtomicNegoSession(
				null /*protocol*/,
	    		new UIAgent().setAgentID(new AgentID("Mr. You")).setName("UI Agent"),
	    		new DecUtilAgent().setDebug(true).setAgentID(new AgentID("Dr. Bayes")).setName("Bayesian Agent"),
				"a" /*agentAname*/, 
				"b" /*agentBname*/,
				new UtilitySpace(domain,ProfileA) /*spaceA*/, 
				new UtilitySpace(domain,ProfileB) /*spaceB*/, 
				new HashMap<AgentParameterVariable,AgentParamValue>() /* agentAparams */,
				new HashMap<AgentParameterVariable,AgentParamValue>() /* agentBparams */,
				"A"/*startingAgent*/,
				180 /*totalTime*/
		);
		
		session.run();
	}
}

