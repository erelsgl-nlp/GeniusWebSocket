package negotiator.protocol.alternatingoffers;

import java.io.*;
import java.net.URL;
import java.util.*;

import misc.SpaceDistance;
import negotiator.*;
import negotiator.actions.Action;
import negotiator.analysis.*;
import negotiator.events.ActionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MediatorRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.utility.UtilitySpace;
import negotiator.xml.*;


public class AlternatingOffersProtocol extends Protocol {
	public static final int ALTERNATING_OFFERS_AGENT_A_INDEX = 0;
	public static final int ALTERNATING_OFFERS_AGENT_B_INDEX = 1;


	int sessionTotalNumber;
	int sessionNumber; // the main session number: increases with different session setups
	public int sessionTestNumber; // the sub-session number: counts from 1 to sessionTotalNumber



	boolean startingWithA=true;
	ArrayList<NegotiationEventListener> actionEventListener = new ArrayList<NegotiationEventListener>();
	String startingAgent; // agentAname or agnetBname

	NegotiationOutcome outcome;


	private Integer totalTime; // will be set only AFTER running the session, because it depends on whether agent isUIAgent() or not	
	public int non_gui_nego_time = 120;
	public int gui_nego_time=60*30; 	// Nego time if a GUI is involved in the nego

	private static int tournament_gui_time=30*60, tournament_non_gui_time=120;

	private Agent agentA;
	private Agent agentB;

	/** fields copied from the NegotiationTemplate class */

	private SimpleElement fAdditional;

	AlternatingOffersBilateralAtomicNegoSession sessionrunner;
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
	/* public AlternatingOffersMetaProtocol(AgentRepItem agtA, AgentRepItem agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentArep=agtA;
    	agentBrep=agtB;

    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time, tournamentnr);
    }

    public AlternatingOffersMetaProtocol(Agent agtA, Agent agtB, ProfileRepItem profA, ProfileRepItem profB,
    		String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
    		int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time, int tournamentnr) throws Exception {
    	agentA=agtA;
    	agentB=agtB;
    	continueSetup( profA,  profB, nameA,nameB, agtApar, agtBpar, sessionnr, totalsessions, forceStartA,gui_time,non_gui_time,tournamentnr);
    }*/



	/*    private void continueSetup(ProfileRepItem profA, ProfileRepItem profB,
	String nameA, String nameB,HashMap<AgentParameterVariable,AgentParamValue> agtApar,HashMap<AgentParameterVariable,AgentParamValue> agtBpar,
	int sessionnr, int totalsessions,boolean forceStartA, int gui_time, int non_gui_time,int tournamentnr) throws Exception {

        non_gui_nego_time=non_gui_time;
    	gui_nego_time=gui_time;
    	tournamentNumber=tournamentnr;
    	setProfileArep(profA);
    	setProfileBrep(profB);
    	setAgentAname(nameA);
    	setAgentBname(nameB);
    	if (agtApar!=null) setAgentAparams(agtApar);
    	if (agtBpar!=null) setAgentBparams(agtBpar);
    	sessionNumber=sessionnr;
    	if (tournamentNumber==-1) sessionNumber=non_tournament_next_session_nr++;
    	sessionTotalNumber=totalsessions;
    	startingWithA=forceStartA;
    	//actionEventListener.add(ael);
    	startingAgent=getAgentAname();
    	if ( (!startingWithA) && new Random().nextInt(2)==1) { 
    		startingAgent=getAgentBname();
    	}
   		fFileName = getProfileArep().getDomain().getURL().getFile();
		loadFromFile(fFileName);
    	check();
    }
	 */  


	/***************** RUN A NEGO SESSION. code below comes from NegotiationManager ****************************/

	public AlternatingOffersProtocol(AgentRepItem[] agentRepItems,AgentID[] agentIDs,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			ArrayList<ProfileRepItem>[] agentsWorldProfiles,MediatorRepItem mediatorRepItems, int turnsNo, int turnLen)
	throws Exception {
		super(agentRepItems, agentIDs, profileRepItems, agentParams,agentsWorldProfiles, mediatorRepItems);
		sessionTotalNumber = 1;
	}

	public AlternatingOffersProtocol(AgentRepItem[] agentRepItems,AgentID[] agentIDs,
			ProfileRepItem[] profileRepItems,
			HashMap<AgentParameterVariable, AgentParamValue>[] agentParams,
			ArrayList<ProfileRepItem>[] agentsWorldProfiles,MediatorRepItem mediatorRepItems)
	throws Exception {
		this(agentRepItems, agentIDs, profileRepItems, agentParams,agentsWorldProfiles, mediatorRepItems, /*turnsNo=*/30, /*turnLen=*/1);
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
			// only sleep if batch mode????
			Thread.sleep(1000); // 1 second delay before next nego starts. Used to be 5, is it needed anyway?
			// Wouter: huh?? removed this           System.exit(0);
		} catch (Exception e) { new Warning("Problem starting negotiation:"+e); e.printStackTrace();}
	}

	/** this runs sessionTotalNumber of sessions with the provided settings */
	public void startNegotiation() throws Exception {
		// Main.log("Starting negotiations...");
		for(int i=0;i<sessionTotalNumber;i++) {
			//Main.log("Starting session " + String.valueOf(i+1));
			if(tournamentRunner!=null) {
				synchronized (tournamentRunner) {
					runNegotiationSession(i+1);
					tournamentRunner.notify();
				}
			} else
				runNegotiationSession(i+1);
		}
	}
	


	/** do test run of negotiation session.
	 * There may be multiple test runs of a single session, for isntance to take the average score.
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
			// for now the agent name and ID are the same
			agentA.setAgentID(getAgentIds(ALTERNATING_OFFERS_AGENT_A_INDEX));

			java.lang.ClassLoader loaderB =Global.class.getClassLoader();//ClassLoader.getSystemClassLoader();
			agentB = (Agent)(loaderB.loadClass(getAgentBRep().getClassPath()).newInstance());
			agentB.setName(getAgentBname());
			// for now the agent name and ID are the same
			agentB.setAgentID(getAgentIds(ALTERNATING_OFFERS_AGENT_B_INDEX));

			sessionTestNumber=nr;
			if(tournamentRunner!= null) tournamentRunner.fireNegotiationSessionEvent(this);
			//NegotiationSession nego = new NegotiationSession(agentA, agentB, nt, sessionNumber, sessionTotalNumber,agentAStarts,actionEventListener,this);
			//SessionRunner sessionrunner=new SessionRunner(this);
			startingAgent=getAgentAname();
			if ( (!startingWithA) && new Random().nextInt(2)==1) { 
				startingAgent=getAgentBname();
			}

			sessionrunner=new AlternatingOffersBilateralAtomicNegoSession(this, 
					agentA, 
					agentB, 
					getAgentAname(),
					getAgentBname(),
					getAgentAUtilitySpace(), 
					getAgentBUtilitySpace(), 
					getAgentAparams(),
					getAgentBparams(),
					startingAgent,
					non_gui_nego_time);
			if(agentA.isUIAgent()||agentB.isUIAgent()) totalTime = non_gui_nego_time;
			else totalTime = gui_nego_time;
			sessionrunner.setTotalTime(totalTime);
			sessionrunner.setSessionTotalNumber(sessionTotalNumber);
			sessionrunner.setStartingWithA(startingWithA);
			// moved into run method in alternatingOffersBilateralAtomicNegotiationSession so the event will be thrown after agents are initalized
			//fireBilateralAtomicNegotiationSessionEvent(sessionrunner,  getProfileArep(), getProfileBrep(),getAgentARep(), getAgentBRep());
			if(Global.fDebug) {
				sessionrunner.run();
			} else {
				
				negoThread = new Thread(sessionrunner);
				System.out.println("nego start. "+System.currentTimeMillis()/1000);
				negoThread.start();
				try {
					synchronized (this) {
						System.out.println("waiting NEGO_TIMEOUT="+totalTime*1000);
						// wait will unblock early if negotiation is finished in time.
						wait(totalTime*1000);
					}
				} catch (InterruptedException ie) { new Warning("wait cancelled:",ie); }
			}
			//System.out.println("nego finished. "+System.currentTimeMillis()/1000);
			//synchronized (this) { try { wait(1000); } catch (Exception e) { System.out.println("2nd wait gets exception:"+e);} }

			stopNegotiation();

			// add path to the analysis chart
			// TODO Wouter: I removed this, not the job of a negotiationsession. We have no nt here anyway.
			//if (nt.getBidSpace()!=null)
			//	nt.addNegotiationPaths(sessionNumber, nego.getAgentABids(), nego.getAgentBBids());
			
			if(sessionrunner.no==null) {
				sessionrunner.JudgeTimeout();
			}
			outcome=sessionrunner.no;
			//sf.addNegotiationOutcome(outcome);        // add new result to the outcome list.
			
			//calculate distance between the two spaces
			SpaceDistance dist = new SpaceDistance(getAgentAUtilitySpace(),getAgentBUtilitySpace());
			SimpleElement xmlDistance =  dist.calculateDistances();
			xmlDistance.setTagName("opposition");
			
			if(fAdditional!=null) { 
				if(outcome.additional==null) {
					outcome.additional = new SimpleElement("additional");
				}
				outcome.additional.addChildElement(fAdditional);				
			}
			if(xmlDistance!=null) { 
				if(outcome.additional==null) {
					outcome.additional = new SimpleElement("additional");
				}
				outcome.additional.addChildElement(xmlDistance);				
			}

			try {
				neglogger.write(Global.outcomesFile,true);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Global.summerizedOutcomesFile), "UTF8"));
				out.write(agentA.getName() + "," +agentA.getAgentID().toString() + "," + agentA.utilitySpace.getFileName() + ",");
				out.write(agentB.getName() + "," + agentB.getAgentID().toString()+ "," + agentB.utilitySpace.getFileName() + ",");
				out.write((outcome.agentAutility + outcome.agentAutilityPenalty) + ",");
				out.write((outcome.agentButility + outcome.agentButilityPenalty) + ",");
				out.write(outcome.ErrorRemarks + "\n");
				out.close();
			} catch (Exception e) {
				new Warning("Exception during writing s:"+e);
				e.printStackTrace();
			}

	}

	public void stopNegotiation() {
		if (negoThread!=null&&negoThread.isAlive()) {
			try {
				sessionrunner.stopNegotiation=true; // see comments in sessionrunner..
				negoThread.interrupt();
				// we call cleanup of agent from separate thread, preventing any sabotage on kill.
				//Thread cleanup=new Thread() {public void run() { sessionrunner.currentAgent.cleanUp();  } };
				//cleanup.start();
				//TODO call this from separate thread.
				//negoThread.stop(); // kill the stuff
				// Wouter: this will throw a ThreadDeath Error into the nego thread
				// The nego thread will catch this and exit immediately.
				// Maybe it should not even try to catch that.
			} catch (Exception e) {	new Warning("problem stopping the nego",e); }
		}
		return;
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


	/**
	 * 
	 * @param fileName
	 * @param mf points to the MainFrame GUI that currently also holds the application data (...)
	 * @throws Exception if there are problems reading the file.
	 */
	/*public static void loadParamsFromFile (String fileName, MainFrame mf) throws Exception
	{
		SimpleDOMParser parser = new SimpleDOMParser();
		try {
			BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));		
			SimpleElement root = parser.parse(file);

            mf.setNemberOfSessions(root.getAttribute("number_of_sessions"));
            SimpleElement xml_agentA = (SimpleElement)(root.getChildByTagName("agent")[0]);
            mf.setAgentAName(xml_agentA.getAttribute("name"));
            mf.setAgentAClassName(xml_agentA.getAttribute("class"));
            mf.setAgentAUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentA.getAttribute("utility_space"));
            SimpleElement xml_agentB = (SimpleElement)(root.getChildByTagName("agent")[1]);
            mf.setAgentBName(xml_agentB.getAttribute("name"));
            mf.setAgentBClassName(xml_agentB.getAttribute("class"));
            mf.setAgentBUtilitySpace((new File(fileName)).getParentFile().toString()+"/"+  xml_agentB.getAttribute("utility_space"));
        } catch (Exception e) {
            throw new IOException("Problem loading parameters from "+fileName+": "+e.getMessage());
        }
    }
	 */

	public Domain getDomain() {
		return domain;
	}


	public UtilitySpace getAgentAUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public UtilitySpace getAgentBUtilitySpace() {
		return getAgentUtilitySpaces(ALTERNATING_OFFERS_AGENT_B_INDEX);
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
		return getAgentName(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public String getAgentBname() {
		return getAgentName(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentAparams() {
		return getAgentParams(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public HashMap<AgentParameterVariable,AgentParamValue>  getAgentBparams() {
		return getAgentParams(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public ProfileRepItem getProfileArep() {
		return getProfileRepItems(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}

	public ProfileRepItem getProfileBrep() {
		return getProfileRepItems(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}

	public AgentRepItem getAgentARep() {
		return getAgentRepItem(ALTERNATING_OFFERS_AGENT_A_INDEX);
	}
	public AgentRepItem getAgentBRep() {
		return getAgentRepItem(ALTERNATING_OFFERS_AGENT_B_INDEX);
	}


	/*    public Agent getAgentA() {
    	if(agentA==null)
    		if(sessionrunner!=null)
    			return sessionrunner.agentA;
    		else
    			return null;
    	else
    	return agentA;
    }
    public Agent getAgentB() {
    	if(agentB==null)
    		if(sessionrunner!=null)
    			return sessionrunner.agentB;
    		else
    			return null;
    	else
    	return agentB;
    }
	 */
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

	public AlternatingOffersBilateralAtomicNegoSession getSessionRunner() {
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
	 * @throws exception if something wrong with the variables, eg not set. 
	 */
	public static ArrayList<Protocol> getTournamentSessionsOld(Tournament tournament) throws Exception {

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

		ArrayList<ProfileRepItem> profiles=tournament.getProfiles();

		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<Protocol>sessions =new ArrayList<Protocol>();
		for (ProfileRepItem profileA: profiles) {
			for (ProfileRepItem profileB: profiles) {
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
				for (TournamentValue agentAval: agentAvalues ) {
					AgentRepItem agentA=((AgentValue)agentAval).getValue();
					for (TournamentValue agentBval: agentBvalues) {
						AgentRepItem agentB=((AgentValue)agentBval).getValue();
						sessions.addAll(allParameterCombis(tournament, agentA,agentB,profileA,profileB));
					}
				}

			}
		}
		return sessions;
	}

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
		
		String path ="file:etc/templates/journal_learning/six_issues/";
		DomainRepItem domain = new DomainRepItem(new URL(path+"six_issues.xml"));
		ArrayList<ProfileRepItem> profilesA=new ArrayList<ProfileRepItem>(); //tournament.getProfiles();
		/*profilesA.add(new ProfileRepItem(new URL(path+"a_l_u_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_d_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_d_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_d_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_t_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_t_t.xml"),domain));*/
		/*profilesA.add(new ProfileRepItem(new URL(path+"a_l_t_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_t_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u2_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u3_u1.xml"),domain));*/
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u3_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u2_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u2_u2_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u3_u3_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u2_u3_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1_u2_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u2_u2_u1_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u3_u3_u3_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_l_u1_u1_u3_u2.xml"),domain));

		/*profilesA.add(new ProfileRepItem(new URL(path+"a_n_u_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_d_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_d_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_d_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_t_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_t_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_t_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_t_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_d.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_t.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1.xml"),domain));*/
	/*	profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u2_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u3_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u3_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u2_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1_u2.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u2_u2_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u3_u3_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1_u3.xml"),domain));

	profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u2_u3_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1_u2_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u2_u2_u1_u3.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u3_u3_u3_u1.xml"),domain));
		profilesA.add(new ProfileRepItem(new URL(path+"a_n_u1_u1_u3_u2.xml"),domain));*/

		ArrayList<ProfileRepItem> profilesB=new ArrayList<ProfileRepItem>();		
		profilesB.add(new ProfileRepItem(new URL(path+"b_l_d_d.xml"),domain));
/**		profilesB.add(new ProfileRepItem(new URL(path+"b_l_u_u.xml"),domain));
		profilesB.add(new ProfileRepItem(new URL(path+"b_lr_d_d.xml"),domain));
		profilesB.add(new ProfileRepItem(new URL(path+"b_lr_u_u.xml"),domain));*/
//		profilesB.add(new ProfileRepItem(new URL(path+"b_n_d_d.xml"),domain));
//		profilesB.add(new ProfileRepItem(new URL(path+"b_n_u_u.xml"),domain));
//		profilesB.add(new ProfileRepItem(new URL(path+"b_nr_d_d.xml"),domain));
//		profilesB.add(new ProfileRepItem(new URL(path+"b_nr_u_u.xml"),domain));
		// we need to exhaust the possible combinations of all variables.
		// we iterate explicitly over the profile and agents, because we need to permutate
		// only the parameters for the selected agents.
		ArrayList<Protocol>sessions =new ArrayList<Protocol>();
		for (ProfileRepItem profileA: profilesB) {
			for (ProfileRepItem profileB: profilesA) {
				if (!(profileA.getDomain().equals(profileB.getDomain())) ) continue; // domains must match. Optimizable by selecting matching profiles first...
				if (profileA.equals(profileB)) continue;
				for (TournamentValue agentAval: agentAvalues ) {
					AgentRepItem agentA=((AgentValue)agentAval).getValue();
					for (TournamentValue agentBval: agentBvalues) {
						AgentRepItem agentB=((AgentValue)agentBval).getValue();
						sessions.addAll(allParameterCombis(tournament, agentA,agentB,profileA,profileB));
					}
				}

			}
		}
		return sessions;
	}


	/** 
	 * This is a recursive function that iterates over all *parameters* and tries all values for each,
	 * recursively calling itself to iterate over the remaining parameters.
	 * This only runs over parameters, not the other variables (Agents and Profiles)
	 * because there may be many parameters and we need to filter 
	 * Not all permutations of the vars are acceptable, for instance domains have to be idnetical.
	 * One optimization: 
	 * @param sessions is the final result: all valid permutations of variables. 
	 * @param varnr is the index of the variable in the variables array.
	 * @throws exception if one of the variables contains no values (which would prevent any 
	 * running sessions to be created with that variable.
	 */
	protected static ArrayList<AlternatingOffersProtocol> allParameterCombis(Tournament tournament, AgentRepItem agentA,AgentRepItem agentB,
			ProfileRepItem profileA, ProfileRepItem profileB) throws Exception {
		ArrayList<AssignedParameterVariable> allparameters;
		allparameters=tournament.getParametersOfAgent(agentA,AGENT_A_NAME);
		allparameters.addAll(tournament.getParametersOfAgent(agentB,AGENT_B_NAME)); // are the run-time names somewhere?
		ArrayList<AlternatingOffersProtocol> sessions=new ArrayList<AlternatingOffersProtocol>();
		allParameterCombis(tournament, allparameters,sessions,profileA,profileB,agentA,agentB,new ArrayList<AssignedParamValue>());
		return sessions;
	}

	/**
	 * adds all permutations of all NegotiationSessions to the given sessions array.
	 * Note, this is not threadsafe, if called from multiple threads the session number will screw up.
	 * @param allparameters the parameters of the agents that were selected for this nego session.
	 * @param sessions
	 * @throws Exception
	 */
	protected static void allParameterCombis(Tournament tournament, ArrayList<AssignedParameterVariable> allparameters, ArrayList<AlternatingOffersProtocol> sessions,
			ProfileRepItem profileA, ProfileRepItem profileB,
			AgentRepItem agentA, AgentRepItem agentB,ArrayList<AssignedParamValue> chosenvalues) throws Exception {
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
			/*			AlternatingOffersProtocol session =new AlternatingOffersProtocol(agentA, agentB, profileA,profileB,
		    		AGENT_A_NAME, AGENT_B_NAME,paramsA,paramsB,session_number++, numberOfSessions , false,
		    		tournament_gui_time, tournament_non_gui_time,1);//TODO::TournamentNumber) ;*/
			AgentRepItem[] agents = new AgentRepItem[2];
			agents[0] = agentA;
			agents[1] = agentB;
			AgentID[] agentIDs=new AgentID[2];
			agentIDs[0]=new AgentID(agents[0].getName());
			agentIDs[1]=new AgentID(agents[1].getName());
			ProfileRepItem[] profiles = new ProfileRepItem[2];
			profiles[0] = profileA;
			profiles[1] = profileB;
			HashMap<AgentParameterVariable,AgentParamValue>[] params = new HashMap[2];
			params[0] = paramsA;
			params[1] = paramsB;

			AlternatingOffersProtocol session =new AlternatingOffersProtocol(agents, agentIDs, profiles,params,null, null); 
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
				allParameterCombis(tournament, newparameters, sessions, profileA,  profileB,agentA,  agentB,newchosenvalues);
			} 
		}	    	
	}


	@Override
	public void cleanUP() {
		agentA = null;
		agentB = null;

	}





}
