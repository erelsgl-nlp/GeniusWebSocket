package negotiator.tournament;

import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.NegotiationEventListener;

/**
 * TournamentRunner is a class that runs a tournament.
 * Use with new Thread(new TournamentRunner(tournament,ael)).start();
 * You can use a null action event listener if you want to.
 */
public class TournamentRunner implements Runnable {

	TournamentExecutor tournamentExecutor;
	Protocol session;

	/** 
     * 
     * @param t the tournament to be run
     * @param ael the action event listener to use. If not null, the existing listener for each
     * 	session will be overridden with this listener.
     * @throws Exception
     */
    public TournamentRunner(TournamentExecutor tournamentExecutor, Protocol session) {
    	this.tournamentExecutor = tournamentExecutor;
    	this.session = session;
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
    	try { 
    		synchronized (this) {
				for (NegotiationEventListener list: tournamentExecutor.getNegotiationEventListeners()) {
					session.addNegotiationEventListener(list);
				}

				// Start the negotiations
				session.setTournamentRunner(this);
				session.startSession(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				
				// Wait for the negotiations to finish (until the session sends a notify)
				wait();
				
				// Write the log
				tournamentExecutor.writeSessionLog(session.getLog());
    		}
    	} catch (Exception e) {
    		new Warning("Fatal error in TournamentRunner:" + e);
    		e.printStackTrace(); 
    	}
    }
    
    public void fireNegotiationSessionEvent(Protocol session) {
    	for(NegotiationEventListener listener : tournamentExecutor.getNegotiationEventListeners()) 
    		if(listener!=null)listener.handleNegotiationSessionEvent(new NegotiationSessionEvent(this,session));
    }

}
