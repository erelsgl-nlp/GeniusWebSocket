/**
 * 
 */
package negotiator.logging;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Global;
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
import negotiator.events.ActionEvent;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEndedEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;

/**
 * @author User
 * 
 */
public class NegotiationLogger implements NegotiationEventListener {
	public static final SimpleDateFormat FILENAME_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");

	private NegotiationLog negLog = new NegotiationLog();;
	private SessionLog currentSessionLog;
	private RoundLog currentRoundLog;

	public NegotiationLogger() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * negotiator.NegotiationEventListener#handeNegotiationSessionEvent(negotiator
	 * .events.NegotiationSessionEvent)
	 */
	public void handleNegotiationSessionEvent(NegotiationSessionEvent evt) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * negotiator.NegotiationEventListener#handleActionEvent(negotiator.events
	 * .ActionEvent)
	 */
	public void handleActionEvent(ActionEvent evt) {
		// Todo: let this handle also alternating offers format
		if (!(evt.getAction() instanceof EndTurn)) {
			if (currentRoundLog == null || evt.getRound() != currentRoundLog.getRoundIndex()) {
				currentRoundLog = new RoundLog(evt.getRound());
				currentSessionLog.addRound(currentRoundLog);
			}
			ActionLog al = new ActionLog(evt.getAction());
			if ((evt.getAction() instanceof BidAction)
					|| (evt.getAction() instanceof Accept)
					|| (evt.getAction() instanceof UpdateStatusAction)) {
				al.addUtility(currentSessionLog.getAgent(0).getAgentID(), evt.getNormalizedUtilityA());
				al.addUtility(currentSessionLog.getAgent(1).getAgentID(), evt.getNormalizedUtilityB());
			}
			if (evt.getErrorRemarks() != null && evt.getErrorRemarks().length() > 0)
				al.addInformation(evt.getErrorRemarks());

			al.setTimestamp(new Date());
			currentRoundLog.addAction(al);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenegotiator.NegotiationEventListener#
	 * handleBlateralAtomicNegotiationSessionEvent
	 * (negotiator.events.BilateralAtomicNegotiationSessionEvent)
	 */
	public void handleBlateralAtomicNegotiationSessionEvent(BilateralAtomicNegotiationSessionEvent evt) {
		// create a new session
		currentSessionLog = new SessionLog(evt.getSession().getSessionNumber());
		negLog.addSession(currentSessionLog);
		AgentLog agentLog1 = new AgentLog(evt.getSession().getAgentA());
		agentLog1.setProfile(evt.getProfileA().getURL().toString()); 
			// we have to set that manually as at this stage usually the utilityspace isnt initalized yet
		currentSessionLog.addAgent(agentLog1);
		AgentLog agentLog2 = new AgentLog(evt.getSession().getAgentB());
		agentLog2.setProfile(evt.getProfileB().getURL().toString());
		currentSessionLog.addAgent(agentLog2);

		AgentLog agentLogMed = null;

		if (evt.getSession().getMediator() != null) {
			agentLogMed = new AgentLog(evt.getSession().getMediator());
			currentSessionLog.addAgent(agentLogMed);
		}

		currentSessionLog.setStart(new Date());

		if (negLog.getNumberOfAgents() == 0) { // on the first seesion we have to update the Negotiaiton too
			negLog.addAgent(agentLog1);
			negLog.addAgent(agentLog2);
			negLog.addAgent(agentLogMed);
		}
		currentRoundLog = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * negotiator.NegotiationEventListener#handleLogMessageEvent(negotiator.
	 * events.LogMessageEvent)
	 */
	public void handleLogMessageEvent(LogMessageEvent evt) {
		 currentSessionLog.addInformation(evt.getMessage());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * negotiator.NegotiationEventListener#handleNegotiationEndedEvent(negotiator
	 * .events.NegotiationEndedEvent)
	 */
	public void handleNegotiationEndedEvent(NegotiationEndedEvent evt) {
		// ToDo: create a special method called handleEndOfNegotiation move that
		// to there

		// currentSessionLog might be null if there was an exception in the agents initialization
		if (currentSessionLog != null) {
			ResultLog resLog = new ResultLog(evt.getFinalAgreement());
			resLog.addUtility(currentSessionLog.getAgent(0).getAgentID(), evt
					.getUtilityA());
			resLog.addUtility(currentSessionLog.getAgent(1).getAgentID(), evt
					.getUtilityB());
			if (evt.getRemarks() != null && evt.getRemarks().length() > 0)
				resLog.addInformation(evt.getRemarks());
			resLog.setAgreementType(evt.getTypeOfAgreement());
			currentSessionLog.setResults(resLog);
			currentSessionLog.setEnd(new Date());
		}
	}
	
	public void write(String filename, boolean append) {
		JAXBContext context;
		try {
			Date now = negLog.getLogonTime();

			// Setting the file name according to the user running the game and
			// the timestamp
			if (negLog != null && negLog.getAgent(0) != null) {
				filename = filename.replaceAll("\\$", 
					negLog.getAgent(0).getAgentID().toString()+"-"+FILENAME_DATE_FORMATTER.format(now));
			}
			else {
				filename = filename.replaceAll("\\$", "-" + FILENAME_DATE_FORMATTER.format(now));
			}

			context = JAXBContext.newInstance(SessionLog.class, Action.class,
					BidAction.class, Offer.class, Query.class, Promise.class,
					CounterOffer.class, Accept.class, Reject.class,
					Comment.class, Threat.class, EndTurn.class,
					EndNegotiation.class, IllegalAction.class,
					OfferUpgrade.class, UltimatumThreat.class, Ultimatum.class, UpdateStatusAction.class, Bid.class,
					AgreementReached.class, ValidateAgreement.class,
					TextMessage.class,
					AgentID.class, ValueDiscrete.class, ValueReal.class,
					ValueInteger.class, NegotiationLog.class, SessionLog.class,
					RoundLog.class, ActionLog.class, ResultLog.class);

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			// Writing the xml version at the head of the file
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			//BufferedWriter out = new BufferedWriter(new FileWriter(filename, append));
			Global.logStdout("NegotiationLogger.write", "writing to log "+filename, "");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
			
			marshaller.marshal(new JAXBElement<NegotiationLog>(new QName("negotiationLog"),
					NegotiationLog.class, negLog), out);
			out.write("\n");
			out.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public NegotiationLog getNegotiationLog() {
		return negLog;
	}

	public void setCurrentSessionLog(SessionLog currentSessionLog) {
		this.currentSessionLog = currentSessionLog;
	}

}
