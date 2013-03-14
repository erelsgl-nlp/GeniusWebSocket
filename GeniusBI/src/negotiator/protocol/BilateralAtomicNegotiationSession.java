package negotiator.protocol;

import java.util.ArrayList;
import java.util.HashMap;

import negotiator.ActionReceiver;
import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Global;
import negotiator.Mediator;
import negotiator.NegotiationEventListener;
import negotiator.WorldInformation;
import negotiator.actions.Action;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.analysis.BidSpaceCash;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEndedEvent;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

public abstract class BilateralAtomicNegotiationSession implements Runnable {
	
	public class BidHolder {
		public String agent;
		public int round;
		public BidPoint bidPoint;
	}
	
    protected 	Agent   		agentA;
    protected 	Agent   		agentB;
    protected 	Mediator   		mediator = null;
    protected 	UtilitySpace 	spaceA;
    protected 	UtilitySpace 	spaceB;
    protected	String 			agentAname;
    protected	String 			agentBname;
    protected WorldInformation  worldInformationA;		// information a bout the world
    protected WorldInformation  worldInformationB;		// information a bout the world
    protected 	Bid 			lastBid = null;				// the last bid that has been done
    protected	Protocol 		protocol;
    protected 	int				sessionNumber;
    public ArrayList<BidPoint> 	fAgentABids;
    public ArrayList<BidPoint> 	fAgentBBids;  
    public ArrayList<BidHolder> fAllBids;
    
    protected 	BidSpace		bidSpace;
	protected HashMap<AgentParameterVariable,AgentParamValue> agentAparams;
	protected HashMap<AgentParameterVariable,AgentParamValue> agentBparams;

    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	private String log;
	/** tournamentNumber is the tournament.TournamentNumber, or -1 if this session is not part of a tournament*/
    int tournamentNumber=-1; 

    public SimpleElement additionalLog = new SimpleElement("additional_log");
	
    /**
     * 
     * @param protocol
     * @param agentA, agentB
     * @param agentAname, agentBname
     * @param spaceA, spaceB - utility spaces
     * @param agentAparams, agentBparams - parameters that are, most likely, used in tournaments (Erel)
     * @param worldInformationA, worldInformationB
     * @throws Exception
     */
    public BilateralAtomicNegotiationSession(Protocol protocol, 
    		Agent agentA, 
    		Agent agentB, 
    		String agentAname,
    		String agentBname,
    		UtilitySpace spaceA, 
    		UtilitySpace spaceB,
    		HashMap<AgentParameterVariable, AgentParamValue> agentAparams, 
    		HashMap<AgentParameterVariable, AgentParamValue> agentBparams,
    		WorldInformation worldInformationA,
    		WorldInformation worldInformationB) throws Exception {
    	this.protocol = protocol;
    	this.agentA = agentA;
    	this.agentB = agentB;
    	this.agentAname = agentAname;
    	this.agentBname = agentBname;
    	this.spaceA = spaceA;
    	this.spaceB = spaceB;
    	if(agentAparams!=null)
    		this.agentAparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentAparams);
    	else this.agentAparams = new HashMap<AgentParameterVariable, AgentParamValue>();
        if(agentBparams!=null)
        	this.agentBparams = new HashMap<AgentParameterVariable, AgentParamValue>(agentBparams);
        else this.agentBparams = new HashMap<AgentParameterVariable, AgentParamValue>();
        this.worldInformationA=worldInformationA;
        this.worldInformationB=worldInformationB;
        if(Global.isExperimentalSetup()) {
        	agentA.fNegotiation = this;
        	agentB.fNegotiation = this;
        }
        fAgentABids = new ArrayList<BidPoint>();
        fAgentBBids = new ArrayList<BidPoint>();
        fAllBids = new ArrayList<BidHolder>();
        if (protocol!=null)
        	actionEventListener.addAll(protocol.getNegotiationEventListeners());
    }

    public BilateralAtomicNegotiationSession(Protocol protocol, 
    		Agent agentA, 
    		Agent agentB, 
    		String agentAname,
    		String agentBname,
    		UtilitySpace spaceA, 
    		UtilitySpace spaceB,
    		HashMap<AgentParameterVariable, AgentParamValue> agentAparams, 
    		HashMap<AgentParameterVariable, AgentParamValue> agentBparams,
    		WorldInformation worldInformationA,
    		WorldInformation worldInformationB,
    		Mediator mediator) throws Exception {
    	
    	this(protocol, agentA, agentB, agentAname, agentBname, spaceA, spaceB,
    			agentAparams, agentBparams, worldInformationA, worldInformationB);
    	
    	this.mediator = mediator;
    }

    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    public void removeNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.remove(listener);
    }
	protected synchronized void fireNegotiationActionEvent(ActionReceiver sender,Action actP,long elapsed,
			double utilA,double utilB,String remarks) {
		Global.logStdout("BilateralAtomicNegoSession.fireNegotiationActionEvent", "action="+actP + " ("+remarks+")", "");
		for(NegotiationEventListener listener : actionEventListener) {
			//Global.logStdout("BilateralAtomicNegoSession.fireNegotiationActionEvent", "listener="+listener, "");
			listener.handleActionEvent(new ActionEvent(this, sender, actP, elapsed, utilA, utilB, remarks ));
		}
	}
	protected synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
	}
	protected synchronized void fireNegotiationEndedEvent(long negotiationMilliseconds,
			double utilityA,double utilityB,AgentID lastActor,NegotiationEndedEvent.AgreementType typeOfAgreement,Bid finalAgreement,String remarks) {
		Global.logStdout("BilateralAtomicNegoSession.fireNegotiationEndedEvent", "finalAgreement="+finalAgreement + " ("+remarks+")", "");
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleNegotiationEndedEvent(new NegotiationEndedEvent(this,negotiationMilliseconds,utilityA,utilityB,lastActor,typeOfAgreement,finalAgreement,remarks));
		}
	}
    public void cleanUp() {
    	agentA.cleanUp();
    	agentA = null;
    	agentB.cleanUp();
    	agentB = null;
    }
    public BidSpace getBidSpace() { 
    	if(bidSpace==null) {
    		try {    	
    			bidSpace = BidSpaceCash.getBidSpace(spaceA, spaceB);
    			if (bidSpace==null) {    				
    				bidSpace=new BidSpace(spaceA,spaceB);
    				BidSpaceCash.addBidSpaceToCash(spaceA, spaceB, bidSpace);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
			}
    	}
    	return bidSpace;     	
    }
	public int getNrOfBids(){
		return fAllBids.size();
	}

	//alinas code
	@SuppressWarnings("unchecked")
	public double[][] getNegotiationPathA(){
		// Clone the array, because the display is updated on another thread, and
		// we don't want the array to change during the process
		ArrayList<BidPoint> currentABids = (ArrayList<BidPoint>)fAgentABids.clone();
		
		double[][] lAgentAUtilities = new double[2][currentABids.size()];
		try
        {
			int i=0;
	    	for (BidPoint p:currentABids)
	    	{
	        	lAgentAUtilities [0][i] = p.utilityA;
	        	lAgentAUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
        } catch (Exception e) {
			e.printStackTrace();
        	return null;
		}
    	
		return lAgentAUtilities; 
	}
	
	public ArrayList<BidPoint> getAgentABids() {
		return fAgentABids;
	}
	
	public ArrayList<BidPoint> getAgentBBids() {
		return fAgentBBids;
	}

	@SuppressWarnings("unchecked")
	public double[][] getNegotiationPathB(){
		// Clone the array, because the display is updated on another thread, and
		// we don't want the array to change during the process
		ArrayList<BidPoint> currentBBids = (ArrayList<BidPoint>)fAgentBBids.clone();
		
		double[][] lAgentBUtilities = new double [2][currentBBids.size()];  
		try{
			int i=0;
	    	for (BidPoint p:currentBBids)
	    	{
	        	lAgentBUtilities [0][i] = p.utilityA;
	        	lAgentBUtilities [1][i] = p.utilityB;
	        	i++;
	    	}
	 	} catch (Exception e) {
		   	e.printStackTrace();
		   	return null;
		}
		return lAgentBUtilities;
	}
    public double getOpponentUtility(Agent pAgent, Bid pBid) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return spaceB.getUtility(pBid);
    	else
    		return spaceA.getUtility(pBid);
    }
    public double getOpponentWeight(Agent pAgent, int pIssueID) throws Exception{
    	if(pAgent.equals(agentA)) 
    		return spaceB.getWeight(pIssueID);
    	else
    		return spaceA.getWeight(pIssueID);
    }
    
    public void addAdditionalLog(SimpleElement pElem) {
    	if(pElem!=null)
    		additionalLog.addChildElement(pElem);
    	
    }
    
	public void setLog(String str){
		log = str;
	}
	public String getLog(){
		return log;
	}
	public String getAgentAname() {
		return agentAname;
	}
	public String getAgentBname() {
		return agentBname;
	}

    public int getTournamentNumber() { 
    	return tournamentNumber; 
    }
    public int getSessionNumber() { 
    	return sessionNumber; 
    }
    public int getTestNumber() { 
    	return 1;//TODO:protocol.getSessionTestNumber(); 
    }

	public abstract String getStartingAgent() ;

	public HashMap<AgentParameterVariable, AgentParamValue> getAgentAparams() {
		return agentAparams;
	}

	public HashMap<AgentParameterVariable, AgentParamValue> getAgentBparams() {
		return agentBparams;
	}
	public Agent getAgentA() {
		return agentA;
	}
	public Agent getAgentB() {
		return agentB;
	}
	public Mediator getMediator() {
		return mediator;
	}
	public UtilitySpace getAgentAUtilitySpace() {
		return spaceA;
	}
	public UtilitySpace getAgentBUtilitySpace() {
		return spaceB;
	}
	public WorldInformation getAgentAWorldInformation() {
		return worldInformationA;
	}
	public WorldInformation getAgentBWorldInformation() {
		return worldInformationB;
	}

	public ArrayList<BidHolder> getAllBids() {
		return fAllBids;
	}
	
	

}
