package negotiator.protocol.asyncoffers;

import java.util.*;

import negotiator.*;
import negotiator.analysis.*;
import negotiator.events.NegotiationEndedEvent.AgreementType;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.MediatorRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.xml.*;


public class AsyncOffersProtocol extends Protocol {
	public static final int ASYNC_OFFERS_AGENT_A_INDEX = 0;
	public static final int ASYNC_OFFERS_AGENT_B_INDEX = 1;

	ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	int 		sessionTotalNumber;
	int 		sessionNumber; // the main session number: increases with different session setups
	public int  sessionTestNumber; // the sub-session number: counts from 1 to sessionTotalNumber
	boolean 	startingWithA=true;
	
	String startingAgent; // agentAname or agnetBname
	NegotiationOutcome outcome;
	String additionalError;
	
	// TODO: ERAN - RESTORE!!! (orig = 7)
	public int num_of_turns = 28; //7; 
	public int non_gui_turn_time = 20; // in seconds
	// TODO: ERAN - RESTORE!!! (orig = 60*4)
	public int gui_turn_time= 60; //60*4; 	// turn length time if a GUI is involved in the nego
	private int turn_time;
	private Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not
	
	private Agent agentA;
	private Agent agentB;
	private Mediator mediator;
	/** fields copied from the NegotiationTemplate class */

	private SimpleElement fAdditional;

	AsyncOffersBilateralAtomicNegoSession sessionrunner;
	/** END OF fields copied from the NegotiationTemplate class */


	/** non_tournament_next_session_nr is used to auto-number non-tournament sessions */
	static int non_tournament_next_session_nr=1;


	/** shared counter */
	static int session_number;

	/** fields copied from the NegotiationTemplate class */


	//private Analysis fAnalysis;
	private BidSpace bidSpace=null;
	//private int totalTime; // total available time for nego, in seconds.


	/** END OF fields copied from the NegotiationTemplate class */

	/** 
	 * Creates a new instance of Negotiation 
	 * @param agtA AgentRepItem (agent descriptor) for agent A.
	 * @param agtB idem agent B.
	 * @param profA ProfileRep Item (the profile descriptor) for agent A.
	 * @param profB idem agent B.
	 * @param nameA the run-name for agent A. This is not the class name!
	 * @param nameB idem agent B.
	 * @param agtApar parameters for Agent A. null is equivalent to empty parameters list.
	 * @param agtBpar idem for agent B.
	 * @param sessionnr
	 * @param totalsessions
	 * @param forceStartA true to force start with agent A. with false, start agent is chosen randomly.
	 * @param ael is the callback point for bidding events. null means you won't be given call backs.
	 * @param gui_time is the time (ms) available for normal GUI agents
	 * @param non_gui_time is the time(ms) available for agents that are agents involving user interaction 
	 * 		which is indicated by Agent.isUIAgent().
	 * @param tournamentnr is the number of the tournament of which this session is a part, or -1 if this session is no part of a tournament.
	 * @throws Exception
	 */



	/***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/

	public AsyncOffersProtocol(AgentRepItem[] agentRepItems,AgentID[] agentIDs,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			ArrayList<ProfileRepItem>[] agentsWorldProfiles,MediatorRepItem mediatorRepItems, int turnsNo, int turnLen)
	throws Exception {
		super(agentRepItems, agentIDs, profileRepItems, agentParams,agentsWorldProfiles, mediatorRepItems);
		sessionTotalNumber = 1;
		
		if (turnsNo != 0) {
			num_of_turns = turnsNo;
		}
		
		if (turnLen != 0) {
			gui_turn_time = turnLen;
		}
	}


	/**
	 * Warning. You can call run() directly (instead of using Thread.start() )
	 * but be aware that run() will not return until the nego session
	 * has completed. That means that your interface will lock up until the session is complete.
	 * And if the negosession uses modal interfaces, this will lock up swing, because modal
	 * interfaces will not launch until the other swing interfaces have handled their events.
	 * (at least this is my current understanding, Wouter, 22aug08).
	 * See "java dialog deadlock" on the web...
	 */
	public void run() {
		try { 
			startNegotiation();
		} catch (Exception e) { new Warning("Problem running negotiation:"+e); e.printStackTrace();}
	}

	/** this runs sessionTotalNumber of sessions with the provided settings */
	public void startNegotiation() throws Exception {
		// Main.log("Starting negotiations...");
		for(int i=0;i<sessionTotalNumber;i++) {
			//Main.log("Starting session " + String.valueOf(i+1));
			if(tournamentRunner!=null) {
				synchronized (tournamentRunner) {
					try {
						runNegotiationSession(i+1);
					} catch (Exception e) { new Warning("Problem during negotiation:"+e); e.printStackTrace();}
					
					tournamentRunner.notify();
				}
			} else
				runNegotiationSession(i+1);
		}
	}
	


	/** do test run of negotiation session.
	 * There may be multiple test runs of a single session, for instance to take the average score.
	 * returns the result in the global field "outcome"
	 * @param nr is the sessionTestNumber
	 * @throws Exception
	 * 
	 */
	protected void runNegotiationSession(int nr)  throws Exception
	{
		java.lang.ClassLoader loaderA = Global.class.getClassLoader();// .getSystemClassLoader()/*new java.net.URLClassLoader(new URL[]{agentAclass})*/;
		agentA = (Agent)(loaderA.loadClass(getAgentARep().getClassPath()).newInstance());
		agentA.setName(getAgentAname());
		agentA.setAgentID(getAgentAID());
		
		java.lang.ClassLoader loaderB =Global.class.getClassLoader();//ClassLoader.getSystemClassLoader();
		agentB = (Agent)(loaderB.loadClass(getAgentBRep().getClassPath()).newInstance());
		agentB.setName(getAgentBname());
		agentB.setAgentID(getAgentBID());
		
		if (getMediatorRepItem()==null)
			mediator=null;
		else {
			java.lang.ClassLoader loaderM =Global.class.getClassLoader();//ClassLoader.getSystemClassLoader();
			mediator = (Mediator)(loaderM.loadClass(getMediatorRepItem().getClassPath()).newInstance());
			mediator.setName("Mediator");
			mediator.setAgentID(new AgentID("Mediator"));
			
			mediator.setNC_ACTIVATED(getMediatorRepItem().getNC_ACTIVATED());
			mediator.setNC_OFFER(getMediatorRepItem().getNC_OFFER());
			mediator.setNC_REJECT(getMediatorRepItem().getNC_REJECT());
			mediator.setNC_TURNS(getMediatorRepItem().getNC_TURNS());
			mediator.setNC_RANK(getMediatorRepItem().getNC_RANK());
		}
		additionalError = "";
		sessionTestNumber=nr;
		if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
		//NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
		//SessionRunner sessionrunner=new SessionRunner(this);
		startingAgent=getAgentAname();
		if ( (!startingWithA) && new Random().nextInt(2)==1) { 
			startingAgent=getAgentBname();
		}
		
		if(agentA.isUIAgent()||agentB.isUIAgent() || (mediator!=null && mediator.isUIMeidator()) ) 
			turn_time = gui_turn_time;
		else 
			turn_time = non_gui_turn_time;
		
		totalTime = num_of_turns * turn_time;
		
		sessionrunner=new AsyncOffersBilateralAtomicNegoSession(this, 
				agentA, 
				agentB, 
				mediator,
				getAgentAname(),
				getAgentBname(),
				getAgentAUtilitySpace(), 
				getAgentBUtilitySpace(), 
				getAgentAparams(),
				getAgentBparams(),
				startingAgent,
				getAgentAWorldInformation(),
				getAgentBWorldInformation(),
				num_of_turns,
				turn_time);
		
		sessionrunner.setTotalTime(totalTime);
		sessionrunner.setSessionTotalNumber(sessionTotalNumber);
		sessionrunner.setStartingWithA(startingWithA);
		
		// moved into run method in asyncOffersBilateralAtomicNegotiationSession so the event will be thrown after agents are initalized
		//fireBilateralAtomicNegotiationSessionEvent(sessionrunner,  getProfileArep(), getProfileBrep(),getAgentARep(), getAgentBRep());
		if(Global.fDebug) {
			sessionrunner.run();
		} else {
			
			negoThread = new Thread(sessionrunner);
			negoThread.setName("NegoSessionThread");
			Global.logStdout("AsyncOffersProtocol", "Starting at time "+System.currentTimeMillis()/1000, null);
			negoThread.start();
			Global.logStdout("AsyncOffersProtocol", "Waiting until negotiation finish.", null);
			try {
				synchronized (this) {
					// wait until the negotiation ends
					// Note: wait will unblock early if negotiation is finished in time.
					// Note: wait must reside within a loop as explained in the documentation.
					while (!sessionrunner.isFinished && !sessionrunner.isTimeout()) {
						wait(10000);
					}
					
					// wait for the protocol to handle the timeout (maximum 120 seconds)
					long startWaitTimeout = System.currentTimeMillis();
					while (!sessionrunner.isFinished && System.currentTimeMillis() <= startWaitTimeout + 120000) {
						wait(1000);
					}
				}
			} catch (InterruptedException ie) {
				new Warning("runNegotiationSession: wait cancelled",ie);
			}
		}
		
		Global.logStdout("AsyncOffersProtocol.runNegotiationSession", "Finished waiting.", null);

		stopSessionThread();

		// If the outcome is not set, there was a problem in the protocol
		if (sessionrunner.no==null) {
	        System.out.println("Warning: AsyncOffersProtocol: Session ended without a result. "+additionalError);
        	sessionrunner.newOutcome( -1,-1,null,AgreementType.Error, "Error in session." + additionalError);
		}
		
		outcome=sessionrunner.no;

		if(fAdditional!=null) { 
			if(outcome.additional==null) {
				outcome.additional = new SimpleElement("additional");
			}
			outcome.additional.addChildElement(fAdditional);				
		}

		// Only write the log if this negotiation is not part of a tournament,
		// in which case the log will be written by the tournament manager.
		if (tournamentRunner == null) {
			try {
				neglogger.write(Global.outcomesFile,true);
			} catch (Exception e) {
				new Warning("Exception during writing s:"+e);
				e.printStackTrace();
			}
		}	

		Global.logStdout("AsyncOffersProtocol", "Finished negotiation.", null);
	}

	public void stopNegotiation() {	
		if (sessionrunner != null)
			sessionrunner.stopNegotiation = true;
		additionalError += " The session was aborted.";
	}

	@SuppressWarnings("deprecation")
	public synchronized void stopSessionThread() {
		
		if (sessionrunner != null)
			sessionrunner.stopNegotiation = true;
		
		if (negoThread != null && negoThread.isAlive()) {
			try {
        		// up to 130 seconds delay to wait for the cleanup, before kill
        		try {
        			int w = 1;
        			while(negoThread.isAlive() && w <= 65536) {
        				// try to kill the thread nicely
        				// Note: if the InterruptedException is caught or the thread is in infinite loop,
        				// the thread will not die
        				negoThread.interrupt();
        				wait(w);
        				w *= 2;
        			}
        			
        			// If the thread remains alive (probably infinite loop), save the stack
        			if (negoThread.isAlive()) {
        				StackTraceElement[] stack = negoThread.getStackTrace();
        				additionalError += " Thread stuck. Active stack trace before kill: ";
        				for (StackTraceElement trace : stack) {
        					additionalError += "\n " + trace.toString();
        				}
        				
	        			// Brutally kill the thread
	        			Global.logStdout("AsyncOffersProtocol", "Brutally killing thread.", null);
	        			negoThread.stop();
        			}

        		}
        		catch (InterruptedException ie) {
        			new Warning("stopNegotiation: wait cancelled",ie);
        		}
            }
			catch (Exception e) {
				new Warning("Problem stopping the thread",e);
				e.printStackTrace();
			}
		}
		
		if (agentA != null)
			agentA.cleanUp();
		
		if (agentB != null)
			agentB.cleanUp();
	}

	public String toString() {
		return "NegotiationSession["+getAgentAStrategyName()+" versus "+getAgentBStrategyName()+"]";
	}


	/* methods copied from the NegotiationTemplate class */

	/**
	 * 
	 * Call this method to draw the negotiation paths on the chart with analysis.
	 * Wouter: moved to here from Analysis. 
	 * @param pAgentABids
	 * @param pAgentBBids
	 */
	public void addNegotiationPaths(int sessionNumber, ArrayList<BidPoint> pAgentABids, ArrayList<BidPoint> pAgentBBids) 
	{
		double[][] lAgentAUtilities = new double[pAgentABids.size()][2];
		double[][] lAgentBUtilities = new double[pAgentBBids.size()][2];        
		try
		{
			int i=0;
			for (BidPoint p:pAgentABids)
			{
				lAgentAUtilities [i][0] = p.utilityA;
				lAgentAUtilities [i][1] = p.utilityB;
				i++;
			}
			i=0;
			for (BidPoint p:pAgentBBids)
			{
				lAgentBUtilities [i][0] = p.utilityA;
				lAgentBUtilities [i][1] = p.utilityB;
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}


	public Domain getDomain() {
		return domain;
	}


	public UtilitySpace getAgentAUtilitySpace() {
		return getAgentUtilitySpaces(ASYNC_OFFERS_AGENT_A_INDEX);
	}

	public UtilitySpace getAgentBUtilitySpace() {
		return getAgentUtilitySpaces(ASYNC_OFFERS_AGENT_B_INDEX);
	}

	public SimpleElement domainToXML(){
		return domain.toXML(); 		
	}

	public BidSpace getBidSpace() { 
		if(bidSpace==null) {
			try {    	
				bidSpace=new BidSpace(getAgentAUtilitySpace(),getAgentBUtilitySpace());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bidSpace;     	
	}


	public String getAgentAname() {
		return getAgentName(ASYNC_OFFERS_AGENT_A_INDEX);
	}

	public String getAgentBname() {
		return getAgentName(ASYNC_OFFERS_AGENT_B_INDEX);
	}
	
	public AgentID getAgentAID() {
		return getAgentIds(ASYNC_OFFERS_AGENT_A_INDEX);
	}

	public AgentID getAgentBID() {
		return getAgentIds(ASYNC_OFFERS_AGENT_B_INDEX);
	}
	
	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentAparams() {
		return getAgentParams(ASYNC_OFFERS_AGENT_A_INDEX);
	}

	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentBparams() {
		return getAgentParams(ASYNC_OFFERS_AGENT_B_INDEX);
	}

	public ProfileRepItem getProfileArep() {
		return getProfileRepItems(ASYNC_OFFERS_AGENT_A_INDEX);
	}

	public ProfileRepItem getProfileBrep() {
		return getProfileRepItems(ASYNC_OFFERS_AGENT_B_INDEX);
	}

	public AgentRepItem getAgentARep() {
		return getAgentRepItem(ASYNC_OFFERS_AGENT_A_INDEX);
	}
	public AgentRepItem getAgentBRep() {
		return getAgentRepItem(ASYNC_OFFERS_AGENT_B_INDEX);
	}

	public WorldInformation getAgentAWorldInformation() {
		return getAgentWorldInformation(ASYNC_OFFERS_AGENT_A_INDEX);
	}
	public WorldInformation getAgentBWorldInformation() {
		return getAgentWorldInformation(ASYNC_OFFERS_AGENT_B_INDEX);
	}
	
	public String getAgentAStrategyName() {
		return getAgentARep().getName();

	}
	public String getAgentBStrategyName() {
		return getAgentBRep().getName();

	}
	public void setBidSpace(BidSpace pBidSpace) {
		bidSpace = pBidSpace;
	}
	public void setAdditional(SimpleElement e) {
		fAdditional = e;
	}


	/**
	 * @return total available time for entire nego, in seconds.
	 */
	public Integer getTotalTime() { return totalTime; }

	public String getStartingAgent(){
		return startingAgent;
	}

	public void setAgentA(Agent agent) {
		agentA=agent;
	}
	public void setAgentB(Agent agent) {
		agentB=agent;
	}

	public Agent getAgentA() {
		return agentA;
	}
	public Agent getAgentB() {
		return agentB;
	}

	public AsyncOffersBilateralAtomicNegoSession getSessionRunner() {
		return sessionrunner;    
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NegotiationOutcome getNegotiationOutcome() {
		return outcome;
	}

	public Agent getAgent(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/*-------------------------------------- TOURNAMENT BUILDING -------------------------*/

	static final String AGENT_A_NAME="Agent A";
	static final String AGENT_B_NAME="Agent B";

	/** called when you press start button in Tournament window.
	 * This builds the sessions array from given Tournament vars 
	 * The procedure skips sessions where both sides use the same preference profiles.
	 * THe world information include all other profile in the same domain that has the String Side_A or Side_B in them
	 * @throws exception if something wrong with the variables, eg not set. 
	 */
	public static ArrayList<Protocol> getTournamentSessions(Tournament tournament) throws Exception {

		session_number=1;
		// get agent A and B value(s)
		ArrayList<AgentVariable> agents = tournament.getAgentVars();
		if (agents.size()!=2) throw new IllegalStateException("Tournament does not contain 2 agent variables");
		
		ArrayList<TournamentValue> agentAvalues=agents.get(0).getValues();
		if (agentAvalues.isEmpty()) 
			throw new IllegalStateException("Agent A does not contain any values!");
		
		ArrayList<TournamentValue> agentBvalues=agents.get(1).getValues();
		if (agentBvalues.isEmpty()) 
			throw new IllegalStateException("Agent B does not contain any values!");
		
		ArrayList<TournamentValue> mediatorvalues=tournament.getMediatorVars().getValues();
		if (mediatorvalues.isEmpty()) 
			throw new IllegalStateException("Mediators does not contain any values!");
	
		ArrayList<ProfileRepItem> profiles=tournament.getProfiles();
				

		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<Protocol>sessions =new ArrayList<Protocol>();
		for (TournamentValue medval : mediatorvalues) {
			MediatorRepItem med=((MediatorValue)medval).getValue();			
			for (ProfileRepItem profileA: profiles) {
				for (ProfileRepItem profileB: profiles) {
					if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
					if (profileA.equals(profileB)) continue;
					// create the world informations from all possible profile in the domain the contain the side name
					ArrayList<ProfileRepItem> worldProfilesA=new ArrayList<ProfileRepItem>();
					ArrayList<ProfileRepItem> worldProfilesB=new ArrayList<ProfileRepItem>();
					for (ProfileRepItem profile :profileA.getDomain().getProfiles() ) {
						if (profile.getURL().toString().indexOf("Side_A")>0)
							worldProfilesA.add(profile);
						else if (profile.getURL().toString().indexOf("Side_B")>0)
							worldProfilesB.add(profile);
					}
					for (TournamentValue agentAval: agentAvalues ) {
						AgentRepItem agentA=((AgentValue)agentAval).getValue();
						for (TournamentValue agentBval: agentBvalues) {
							AgentRepItem agentB=((AgentValue)agentBval).getValue();
							sessions.addAll(allParameterCombis(tournament, agentA,agentB,med,profileA,profileB,worldProfilesA,worldProfilesB));
						}
					}
				} // profile B
			} // profileA
		} // med
		return sessions;
	}


	/** 
	 * This is a recursive function that iterates over all *parameters* and tries all values for each,
	 * recursively calling itself to iterate over the remaining parameters.
	 * This only runs over parameters, not the other variables (Agents and Profiles)
	 * because there may be many parameters and we need to filter 
	 * Not all permutations of the vars are acceptable, for instance domains have to be idnetical.
	 * One optimization: 
	 * @param worldProfilesB - ArrayList containing the optional profile of side A. will be given to Agent_B as world information
	 * @param worldProfilesA - AarrayList containing the optional profile of side A. will be given to Agent_B as world information
	 * @param sessions is the final result: all valid permutations of variables. 
	 * @param varnr is the index of the variable in the variables array.
	 * @throws exception if one of the variables contains no values (which would prevent any 
	 * running sessions to be created with that variable.
	 */
	protected static ArrayList<AsyncOffersProtocol> allParameterCombis(Tournament tournament, AgentRepItem agentA,AgentRepItem agentB,MediatorRepItem med,
			ProfileRepItem profileA, ProfileRepItem profileB, ArrayList<ProfileRepItem> worldProfilesA, ArrayList<ProfileRepItem> worldProfilesB) throws Exception {
		ArrayList<AssignedParameterVariable> allparameters;
		allparameters=tournament.getParametersOfAgent(agentA,AGENT_A_NAME);
		allparameters.addAll(tournament.getParametersOfAgent(agentB,AGENT_B_NAME)); // are the run-time names somewhere?
		ArrayList<AsyncOffersProtocol> sessions=new ArrayList<AsyncOffersProtocol>();
		allParameterCombis(tournament, allparameters,sessions,profileA,profileB,worldProfilesA,worldProfilesB,agentA,agentB,med,new ArrayList<AssignedParamValue>());
		return sessions;
	}

	/**
	 * adds all permutations of all NegotiationSessions to the given sessions array.
	 * Note, this is not threadsafe, if called from multiple threads the session number will screw up.
	 * @param allparameters the parameters of the agents that were selected for this nego session.
	 * @param sessions
	 * @param worldProfilesB 
	 * @param worldProfilesA 
	 * @throws Exception
	 */
	protected static void allParameterCombis(Tournament tournament, ArrayList<AssignedParameterVariable> allparameters, ArrayList<AsyncOffersProtocol> sessions,
			ProfileRepItem profileA, ProfileRepItem profileB,
			ArrayList<ProfileRepItem> worldProfilesA, ArrayList<ProfileRepItem> worldProfilesB, 
			AgentRepItem agentA, AgentRepItem agentB, MediatorRepItem med,ArrayList<AssignedParamValue> chosenvalues) throws Exception {
		if (allparameters.isEmpty()) {
			// separate the parameters into those for agent A and B.
			HashMap<AgentParameterVariable,AgentParamValue> paramsA = new HashMap<AgentParameterVariable,AgentParamValue>();
			HashMap<AgentParameterVariable,AgentParamValue> paramsB = new HashMap<AgentParameterVariable,AgentParamValue>();
			int i=0;
			for (AssignedParamValue v: chosenvalues) {
				if (v.agentname==AGENT_A_NAME) paramsA.put(allparameters.get(i).parameter, v.value); 
				else paramsB.put(allparameters.get(i).parameter,v.value);
				i++;
			}
			// TODO compute total #sessions. Now fixed to 9999
			int numberOfSessions = 1;
			if(tournament.getVariables().get(Tournament.VARIABLE_NUMBER_OF_RUNS ).getValues().size()>0)
				numberOfSessions = ((TotalSessionNumberValue)( tournament.getVariables().get(Tournament.VARIABLE_NUMBER_OF_RUNS).getValues().get(0))).getValue();
			
			// Repeat the session according to the parameter 
			for (int run = 0; run < numberOfSessions; ++run) {
				AgentRepItem[] agents = new AgentRepItem[2];
				agents[0] = agentA;
				agents[1] = agentB;
				AgentID[] agentIDs=new AgentID[2];
				
				// Add the side to the AgentIDs, so when the same agent is on both sides,
				// it will have different AgentID for each side
				agentIDs[0]=new AgentID("SideA_" + agents[0].getName());
				agentIDs[1]=new AgentID("SideB_" + agents[1].getName());
				ProfileRepItem[] profiles = new ProfileRepItem[2];
				profiles[0] = profileA;
				profiles[1] = profileB;
				HashMap<AgentParameterVariable,AgentParamValue>[] params = new HashMap[2];
				params[0] = paramsA;
				params[1] = paramsB;
				ArrayList<ProfileRepItem>[] agentsWorldProfiles = new ArrayList[2];
				agentsWorldProfiles[0] = worldProfilesA;
				agentsWorldProfiles[1] = worldProfilesB;
				
				AsyncOffersProtocol session =new AsyncOffersProtocol(agents, agentIDs, profiles,params,agentsWorldProfiles, med, 0,0); 
				sessions.add(session);
				//check if the analysis is already made for the prefs. profiles
				BidSpace bidSpace = BidSpaceCash.getBidSpace(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace());
				if(bidSpace!=null) {
					session.setBidSpace(bidSpace);
				} else {
					bidSpace = new BidSpace(session.getAgentAUtilitySpace(),session.getAgentBUtilitySpace());
					BidSpaceCash.addBidSpaceToCash(session.getAgentAUtilitySpace(), session.getAgentBUtilitySpace(), bidSpace);
					session.setBidSpace(bidSpace);
				}
			}
		} else {
			// pick next variable, and compute all permutations.
			AssignedParameterVariable v=allparameters.get(0);
			// remove that variable from the list... using clone to avoid damaging the original being used higher up
			ArrayList<AssignedParameterVariable> newparameters=(ArrayList<AssignedParameterVariable>)allparameters.clone();
			newparameters.remove(0);
			ArrayList<TournamentValue> tvalues=v.parameter.getValues();
			if (tvalues.isEmpty()) throw new IllegalArgumentException("tournament parameter "+v.parameter+" has no values!");
			// recursively do all permutations for the remaining vars.
			for (TournamentValue tv: tvalues) {
				ArrayList<AssignedParamValue> newchosenvalues=(ArrayList<AssignedParamValue>) chosenvalues.clone();
				newchosenvalues.add(new AssignedParamValue((AgentParamValue)tv,v.agentname));
				allParameterCombis(tournament, newparameters, sessions, profileA,  profileB,worldProfilesA,worldProfilesB,agentA, agentB, med, newchosenvalues);
			} 
		}	    	
	}


	@Override
	public void cleanUP() {
		agentA.cleanUp();
		agentA = null;
		agentB.cleanUp();
		agentB = null;

	}





}
