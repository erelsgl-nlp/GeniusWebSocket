package negotiator.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Domain;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.NegotiationOutcome;
import negotiator.WorldInformation;
import negotiator.actions.Action;
import negotiator.events.*;
import negotiator.exceptions.Warning;
import negotiator.logging.NegotiationLog;
import negotiator.logging.NegotiationLogger;
import negotiator.repository.AgentRepItem;
import negotiator.repository.MediatorRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;
import negotiator.xml.*;

public abstract class Protocol implements Runnable {
    protected Thread negoThread = null;
    protected TournamentRunner tournamentRunner;
    /**
     * stopNegotiation indicates that the session has now ended.
     * it is checked after every call to the agent,
     * and if it happens to be true, session is immediately returned without any updates to the results list.
     * This is because killing the thread in many cases will return Agent.getAction() but with
     * a stale action. By setting stopNegotiation to true before killing, the agent will still immediately return.
     */
    public boolean stopNegotiation=false;
    
	private AgentRepItem[] agentRepItems;
    private ProfileRepItem[] profileRepItems;
    private MediatorRepItem mediatorRepItems;
    private String[] agentNames;
    private AgentID[] agentIDs;
    private AgentID mediatorID;
    private HashMap<AgentParameterVariable,AgentParamValue>[]  agentParams;
    
    
    /** -- **/
    protected Domain domain;
    private UtilitySpace[] agentUtilitySpaces;
    private WorldInformation[] agentsWorldInformation;
    
    ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();    

    private SimpleElement fRoot;
//	private String fFileName;    
	protected NegotiationLogger neglogger;

	public abstract String getName();
	
	public abstract NegotiationOutcome getNegotiationOutcome();
	
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception {
		throw new Exception("This protocol cannot be used in a tournament");
	}

    public final void startSession() {
    	Thread protocolThread = new Thread(this);
    	protocolThread.setName("ProtocolThread");
    	protocolThread.start();
    }
    
    
    public Protocol(AgentRepItem[] agentRepItems, AgentID[] agentIDs, ProfileRepItem[] profileRepItems, HashMap<AgentParameterVariable,AgentParamValue>[] agentParams,ArrayList<ProfileRepItem>[] agentsWorldProfiles,MediatorRepItem mediatorRepItem) throws Exception{
    	this.agentRepItems = agentRepItems.clone();
    	this.profileRepItems = profileRepItems.clone();
    	this.mediatorRepItems = mediatorRepItem;
    	if (agentParams!=null) 
    		this.agentParams = agentParams.clone();
    	else
    		this.agentParams = new HashMap[agentRepItems.length];
    	//TODO: read the agent names
		agentNames = new String[profileRepItems.length];
		agentNames[0] = "Agent A";
		agentNames[1] = "Agent B";
		this.agentIDs = agentIDs.clone();
		if (mediatorRepItem!=null)
			this.mediatorID = new AgentID("Mediator");
		else
			this.mediatorID = null;
		
    	neglogger = new NegotiationLogger();
    	addNegotiationEventListener(neglogger);
    	
    	loadAgentsUtilitySpaces();
    	loadWorldInformation(agentsWorldProfiles);
    }
	

	protected void loadAgentsUtilitySpaces() throws Exception
	{
		if(domain==null)
			//domain = new Domain(profileRepItems[0].getDomain().getURL().getFile());
			domain = Repository.get_domain_repos().getDomain(profileRepItems[0].getDomain());

		//load the utility space		
		agentUtilitySpaces = new UtilitySpace[profileRepItems.length]; 
		for(int i=0;i<profileRepItems.length;i++) {
			ProfileRepItem profile = profileRepItems[i];
			agentUtilitySpaces[i] =  Repository.get_domain_repos().getUtilitySpace(domain, profile);
			agentUtilitySpaces[i].setUseNormalizeUtility(false); // in this protocol we use utilities with time effect
		}
		return;

	}
	
	/**
	 * @param agentsWorldProfiles
	 * @author Yinon Oshrat
	 */
	private void loadWorldInformation(ArrayList<ProfileRepItem>[] agentsWorldProfiles) {
		
		try {
			agentsWorldInformation = new WorldInformation[agentNames.length];
			
			// Create the locales HashMap
			Locale.setDefault(Locale.ENGLISH);
			HashMap<AgentID, Locale> agentsLocale = new HashMap<AgentID, Locale>();
			for (int i = 0; i < agentRepItems.length; i++) {
				if (agentRepItems[i].getLocaleString() != null) {
					agentsLocale.put(agentIDs[i], new Locale(agentRepItems[i].getLocaleString()));
				}
				else {
					agentsLocale.put(agentIDs[i], Locale.getDefault());
				}
			}
			
			// Go over the agents
			for(int i = 0; i < agentNames.length; i++) {

				agentsWorldInformation[i] = new WorldInformation(agentIDs[(i+1)%2],mediatorID);
			
				// If the caller specified the opponent utility space
				if (agentsWorldProfiles != null && 
						agentsWorldProfiles[i] != null && 
						agentsWorldProfiles[i].size() > 0) {
					
					for(ProfileRepItem profile : agentsWorldProfiles[i]) {
						UtilitySpace us=Repository.get_domain_repos().getUtilitySpace(domain, profile);
						us.setUseNormalizeUtility(false); // in this protocol we use utiltis with time effect
						agentsWorldInformation[i].addUtilitySpace(us);
					}
				}
				else {
					// If the opponent utility space is specified in the agent profile
					if (agentUtilitySpaces[i].getNumOpponentUtiltySpaces() > 0) {
						for (int j = 0; j < agentUtilitySpaces[i].getNumOpponentUtiltySpaces(); j++) {
							agentsWorldInformation[i].addUtilitySpace(
									agentUtilitySpaces[i].getOpponentUtilitySpace(j));							
						}
					}
				}
				
				// Set the locales HashMap
				agentsWorldInformation[i].setAgentsLocale(agentsLocale);
			}
		} catch(Exception e) {
			new Warning("Problem Loading agents world information",e,true,3);
		}
		
		
	}
	
	/**
	 * @param fileName Wouter: I think this is the domain.xml file.
	 */
	private void loadFromFile(String fileName)  throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		//BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
		BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
		
		fRoot = parser.parse(file);
		/*            if (root.getAttribute("negotiation_type").equals("FDP"))this.negotiationType = FAIR_DEVISION_PROBLEM;
        else thisnegotiationType = CONVENTIONAL_NEGOTIATION;*/
		SimpleElement xml_utility_space = (SimpleElement)(fRoot.getChildByTagName("utility_space")[0]);
		domain = new Domain(xml_utility_space);
		loadAgentsUtilitySpaces();
		if (Global.analysisEnabled && !Global.batchMode)
		{
			if(fRoot.getChildByTagName("analysis").length>0) {
				//fAnalysis = new Analysis(this, (SimpleElement)(fRoot.getChildByTagName("analysis")[0]));
			} else {
				//propose to build an analysis
/*				Object[] options = {"Yes","No"};                  
				int n = JOptionPane.showOptionDialog(null,
						"You have no analysis available for this template. Do you want build it?",
						"No Analysis",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options);
				if(n==0) {*/
					//bidSpace=new BidSpace(fAgentAUtilitySpace,fAgentBUtilitySpace);
					//fAnalysis = Analysis.getInstance(this);
					//  save the analysis to the cache
					//fAnalysis.saveToCache();
				//}
			}
		}
	}

    void check() throws Exception {
    	//if (!(getProfileArep().getDomain().equals(getProfileBrep().getDomain())))
    		//throw new IllegalArgumentException("profiles "+getProfileArep()+" and "+getProfileBrep()+" have a different domain.");
    }
    
    public void addNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.add(listener);
    }
    public ArrayList<NegotiationEventListener> getNegotiationEventListeners() {
    	return (ArrayList<NegotiationEventListener>) (actionEventListener.clone());
    }

    public void removeNegotiationEventListener(NegotiationEventListener listener) {
    	if(!actionEventListener.contains(listener))
    		actionEventListener.remove(listener);
    }
	public synchronized void fireBilateralAtomicNegotiationSessionEvent(BilateralAtomicNegotiationSession session,ProfileRepItem profileA,
			ProfileRepItem profileB,
			AgentRepItem agentA,
			AgentRepItem agentB) {
		for(NegotiationEventListener listener : actionEventListener) {
			listener.handleBlateralAtomicNegotiationSessionEvent(new BilateralAtomicNegotiationSessionEvent (this, session,profileA,profileB,agentA,agentB));
		}
	}
	
    public synchronized void fireLogMessage(String source, String log) { 
    	for(NegotiationEventListener listener : actionEventListener) { 
        	listener.handleLogMessageEvent(new LogMessageEvent(this, source, log));
    	}
	}
    public void setTournamentRunner(TournamentRunner runner) {
    	tournamentRunner = runner; 
    }
    public Domain getDomain() {
    	return domain;
    }
	public AgentRepItem getAgentRepItem(int index) {
		return agentRepItems[index];
	}
    public ProfileRepItem getProfileRepItems(int index) {
    	return profileRepItems[index];
    }
    public String getAgentName(int index) {
    	return agentNames[index];
    }
    
    public AgentID getAgentIds(int index) {
    	return agentIDs[index];
    }
    public MediatorRepItem getMediatorRepItem() {
		return mediatorRepItems;
	}
    public HashMap<AgentParameterVariable,AgentParamValue> getAgentParams(int index) {
    	return agentParams[index];
    }
    public  UtilitySpace getAgentUtilitySpaces(int index) {
    	return agentUtilitySpaces[index];
    }
    public WorldInformation getAgentWorldInformation(int index) {
    	return agentsWorldInformation[index];
    }
    public int getNumberOfAgents() {
    	return agentRepItems.length;
    }

    public int getSessionNumber() {
    	return 1;
    }
    
    
    public NegotiationLog getLog() {
		return neglogger.getNegotiationLog();
	}

	public void stopNegotiation() {
    	if (negoThread!=null&&negoThread.isAlive()) {
    		try {
    			stopNegotiation=true; // see comments in sessionrunner..
    			negoThread.interrupt();
    			
                synchronized (this) {  this.notify();  }
    		} catch (Exception e) {	new Warning("problem stopping the nego",e); }
    	}
        return;
    }
    public abstract void cleanUP();

}
