package negotiator.tournament;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.NegotiationEventListener;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.AgreementReached;
import negotiator.actions.BidAction;
import negotiator.actions.Comment;
import negotiator.actions.CounterOffer;
import negotiator.actions.EndNegotiation;
import negotiator.actions.EndTurn;
import negotiator.actions.IllegalAction;
import negotiator.actions.Offer;
import negotiator.actions.OfferUpgrade;
import negotiator.actions.Promise;
import negotiator.actions.Query;
import negotiator.actions.Reject;
import negotiator.actions.TextMessage;
import negotiator.actions.Threat;
import negotiator.actions.Ultimatum;
import negotiator.actions.UltimatumThreat;
import negotiator.actions.UpdateStatusAction;
import negotiator.actions.ValidateAgreement;
import negotiator.exceptions.Warning;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.logging.ActionLog;
import negotiator.logging.AgentLog;
import negotiator.logging.NegotiationLog;
import negotiator.logging.ResultLog;
import negotiator.logging.RoundLog;
import negotiator.logging.SessionLog;
import negotiator.logging.TournamentLog;
import negotiator.protocol.Protocol;
import negotiator.repository.SetupRepItem;

/**
 * TournamentExecutor is a class that runs all the sessions of a tournament.
 * Use with new Thread(new TournamentExecutor(tournament,ael)).start();
 * You can use a null action event listener if you want to.
 */
 public class TournamentExecutor implements Runnable {
	
	public static String logFilePattern = "Tournamentoutcomes_$.xml";
	public static String errFilePattern = "TournamentErrors_$.txt";
	private final int numThreads = 64;
	private final boolean shouldWriteRounds = false;

	Tournament tournament;
    ArrayList<NegotiationEventListener> negotiationEventListeners = new ArrayList<NegotiationEventListener>();
	String logFilename;
	
	ExecutorService executor;
	
	/** 
     * 
     * @param t the tournament to be run
     * @param ael the action event listener to use. If not null, the existing listener for each
     * 	session will be overridden with this listener.
     * @throws Exception
     */
    public TournamentExecutor(Tournament t,NegotiationEventListener ael) throws Exception {
    	tournament=t;
    	
    	// ******** Yoshi: suppress the GUI results
    	//negotiationEventListeners.add(ael);
    }
    
    /**
     * Warning. You can call run() directly (instead of using Thread.start() )
     * but be aware that run() will not return until the tournament
     * has completed. That means that your interface will lock up until the tournament is complete.
     * And if any negosession uses modal interfaces, this will lock up swing, because modal
     * interfaces will not launch until the other swing interfaces have handled their events.
     * (at least this is my current understanding, Wouter, 22aug08).
     * See "java dialog deadlock" on the web...
     */
    public void run() {
    	PrintStream currErr = System.err;
    	try {
   			SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
			Date now = new Date();
    		logFilename = logFilePattern.replaceAll("\\$", s.format(now));
    		String errFilename = errFilePattern.replaceAll("\\$", s.format(now));

     		System.out.println("TournamentExecutor: log filename is " + logFilename);
     		System.out.println("TournamentExecutor: err filename is " + errFilename);
     		System.out.println("TournamentExecutor: starting tournament with " + numThreads + " threads.");

    		// Write the header of the log file
			//BufferedWriter writer1 = new BufferedWriter(new FileWriter(logFilename, true));
     		BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename), "UTF8"));
			writer1.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
			writer1.write("<tournamentLog>\n");
			writer1.write("<Negotiations>\n");
			writer1.close();
			
    		// Write the error stream to a file
			FileOutputStream errStr = new FileOutputStream(errFilename, true);
			PrintStream errPrintStream = new PrintStream(errStr);
			System.setErr(errPrintStream);
			
    		// Create a thread pool
    		executor = Executors.newFixedThreadPool(numThreads);

			ArrayList<Protocol> sessions = tournament.getSessions();
			int numTasks = 0;
			
			// Go over the tasks (sessions)
    		while (sessions.size()>0) {
    			Protocol session = sessions.remove(0);
				synchronized(this) { 
					// Submit the task to the thread pool
					executor.submit(new TournamentRunner(this, session));
					++numTasks;
				}
			}
			
			// Notify the thread pool that no further tasks will be submitted
    		executor.shutdown();
    		
    		System.out.println("TournamentExecutor: all " + numTasks + " tasks were submitted to the thread pool manager.");

    		// Wait until all the tasks finish
    		executor.awaitTermination(300, TimeUnit.MINUTES);
    		System.out.println("TournamentExecutor: all the tasks were finished.");
    		executor.shutdownNow();
    		
    		// Write the lower header of the log file
			//BufferedWriter writer2 = new BufferedWriter(new FileWriter(logFilename, true));
    		BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilename), "UTF8"));
			writer2.write("</Negotiations>\n");
			writer2.write("</tournamentLog>\n");
			writer2.close();
			
    		System.out.println("TournamentExecutor: finished writing log.");
    		
    	} catch (Exception e) {
    		new Warning("Fatal error in TournamentExecutor:" + e);
    		e.printStackTrace(); 
    	}
    	
    	// Restore the old error stream
    	System.setErr(currErr);
    }
    
	/**
	 * @return the negotiationEventListeners
	 */
	public ArrayList<NegotiationEventListener> getNegotiationEventListeners() {
		return negotiationEventListeners;
	}

	/**
	 * @return the logFilename
	 */
	public String getLogFilename() {
		return logFilename;
	}

	/**
	 * Writes the log of a specific session.
	 * 
	 * @param negotiationLog - the NegotiationLog object to write
	 */
	public void writeSessionLog(NegotiationLog negotiationLog) {
		try {
			// If we don't need to write the rounds data, delete it
			if (!shouldWriteRounds) {
				negotiationLog.delAllSessionsRounds();
			}
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SessionLog.class,
					Action.class,BidAction.class,Offer.class,Query.class,Promise.class,CounterOffer.class,
					Accept.class,Reject.class,Comment.class,Threat.class, EndTurn.class, EndNegotiation.class,
					IllegalAction.class, OfferUpgrade.class, UltimatumThreat.class, Ultimatum.class, UpdateStatusAction.class, TextMessage.class,
					Bid.class, AgreementReached.class, ValidateAgreement.class,
	                AgentID.class, ValueDiscrete.class, ValueReal.class,ValueInteger.class,
	                NegotiationLog.class,SessionLog.class,RoundLog.class,ActionLog.class,ResultLog.class,AgentLog.class,
	                SetupRepItem.class,TournamentLog.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			
			synchronized (this) {
				//BufferedWriter writer = new BufferedWriter(new FileWriter(getLogFilename(),true));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getLogFilename()), "UTF8"));
				
				marshaller.marshal(new JAXBElement<NegotiationLog>(new QName("negotiationLog"), NegotiationLog.class, negotiationLog), writer);		
				writer.write("\n");
				writer.close();
			}
		} catch (Exception e) {
			new Warning("TournamentExecutor: error writing log: " + e);
			e.printStackTrace(); 
		}
		
	}
	
}
