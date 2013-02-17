package geniuswebsocket;
/*
 * socket.io-java-client Test.java
 *
 * Copyright (c) 2012, Enno Boland
 * socket.io-java-client is a implementation of the socket.io protocol in Java.
 * 
 * See LICENSE file for more information
 */
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import negotiator.ActionListener;
import negotiator.Agent;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.WorldInformation;
import negotiator.actions.*;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.UnknownIssueException;
import negotiator.exceptions.UnknownValueException;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

import org.json.JSONException;
import org.json.JSONObject;

import agents.biu.KBAgent;

/**
 * A socket.io client that negotiates with humans. 
 *
 * @author Erel Segal Halevi
 * @since 2013-02
 */
public class NegotiationClient implements IOCallback, Cloneable {
	protected String roleOfThisAgent, roleOfOtherPlayer;
	protected AgentID agentIdOfThisAgent, agentIdOfOtherPlayer;
	
	/**
	 * socket for connecting to the Node.js negotiation server:
	 */
	protected SocketIO negotiationSocket;
	
	/**
	 * agent for strategic negotiation
	 */
	protected Agent agent;
	
	/**
	 * Genius negotiation domain 
	 */
	protected Domain domain;
	
	/**
	 * full URL (http://host:port) of the socket.io server that handles the negotiation. 
	 */
	protected String serverUrl;
	
	/**
	 * name of the game-class to join - from the games available on the game-server
	 */
	protected String gameType;


	/**
	 * @param domainFile full path to the Genius XML file with the domain data. 
	 * @param serverUrl full URL (http://host:port) of the socket.io game-server that handles the negotiation.
	 * @param gameType name of the game-class to join - from the games available on the game-server.
	 * @throws Exception
	 */
	public NegotiationClient(Domain domain, String serverUrl, String gameType) {
		this.domain = domain;
		this.serverUrl = serverUrl;
		this.gameType = gameType;
		
		roleOfThisAgent = "Candidate";
		roleOfOtherPlayer = "Employer";
	}
	
	@Override public NegotiationClient clone() {
		return new NegotiationClient(domain, serverUrl, gameType);
	}
	
	public void start() throws JSONException, IOException, NegotiatorException {
		initializeAgent(roleOfThisAgent, roleOfOtherPlayer);

		negotiationSocket = new SocketIO();
		negotiationSocket.connect(serverUrl, this);

		negotiationSocket.emit("start_session", new JSONObject()
			.put("userid", "Java "+new Date().toString())
			.put("gametype", gameType)
			.put("role", roleOfThisAgent)
			);
	}
	
	/**
	 *  @throws IOException 
	 * @throws NegotiatorException 
	 *  @see  negotiator.protocol.asyncoffers.AsyncOffersProtocol#runNegotiationSession
	 *  @see  negotiator.protocol.asyncoffers.AsyncOffersBilateralAtomicNegoSession#run
	 *  @see  negotiator.protocol.Protocol#loadWorldInformation
	 */
	protected void initializeAgent(String role, String partnerRole) throws IOException, NegotiatorException {
		agentIdOfThisAgent = new AgentID(role);
		agentIdOfOtherPlayer = new AgentID(partnerRole);
		
		agent = new KBAgent();
		agent.setName(role);
		agent.setAgentID(agentIdOfThisAgent);
		
		/* handle actions from our agent to the partner */
		agent.setActionListener(new ActionListener() {
			@Override public void actionSent(Action a) {
				if (a instanceof BidAction) {
					negotiationSocket.emit("offer", a);
				} else if (a instanceof Accept) {
					negotiationSocket.emit("accept", ((AcceptOrReject)a).getAcceptedOrRejectedAction());
				} else if (a instanceof Reject) {
					negotiationSocket.emit("reject", ((AcceptOrReject)a).getAcceptedOrRejectedAction());
				}
			}
		});

		
		int sessionNumber = 1;
		int sessionTotalNumber = 1;
		Date startTime = new Date();
		Integer totalTimeSeconds = 30*60;
		Integer turnLengthSeconds = 2*60;
		HashMap<AgentParameterVariable, AgentParamValue> agentParams = new HashMap<AgentParameterVariable, AgentParamValue>();
		
		UtilitySpace agentUtilitySpace = domain.getUtilitySpace(role.toLowerCase(), "short-term");
		WorldInformation agentWorldInformation = domain.getAllUtilitySpaces(partnerRole.toLowerCase());
		
		agent.internalInit(
			sessionNumber, sessionTotalNumber, startTime, totalTimeSeconds, turnLengthSeconds,
			agentUtilitySpace, agentParams, agentWorldInformation);
		agent.init();
	}

	@Override public void onConnect() {
		System.out.println("NegotiationClient Connection established.");
	}

	@Override public void onMessage(JSONObject arg0, IOAcknowledge ack) {
		try {
			System.out.println("NegotiationClient receives an object: " + arg0.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("NegotiationClient receives a message: " + data);
	}

	@Override public void onError(SocketIOException socketIOException) {
		System.out.println("NegotiationClient receives an error:");
		socketIOException.printStackTrace();
	}

	@Override public void onDisconnect() {
		System.out.println("NegotiationClient Connection terminated.");
	}
	
	
	/* Handle actions from the partner or from the server to our agent */
	@Override public void on(String event, IOAcknowledge ack, Object... args) {
		System.out.println("NegotiationClient receives event '" + event + "' arg0="+args[0]);
		try {
			if (event.equals("status")) {  // status sent by the server:
				JSONObject arg0 = (JSONObject)args[0];
				if (arg0.get("key").equals("phase") && arg0.get("value").equals("")) { 
					System.out.println("  Game starts - launching a new client!");
					final NegotiationClient newClient = clone();
					new Thread() {
						public void run() {
							try {
								newClient.start();
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (NegotiatorException e) {
								e.printStackTrace();
							}
						}
					}.start();
				}
			} else if (event.equals("EndTurn")) {
				int turnsFromStart = (Integer)args[0];
				Action theAction = new EndTurn(turnsFromStart);
				agent.ReceiveMessage(theAction);
			} else if (event.equals("offer")) {  // offer created by the other partner:
				onPartnerOffer((JSONObject)args[0]);
			} else if (event.equals("accept")) {
				onPartnerAccept();
			} else if (event.equals("reject")) {
				onPartnerReject();
			} else if (event.equals("message")) {
				if (args[0] instanceof JSONObject) {
					JSONObject arg0 = (JSONObject)args[0];
					String speaker = (String)arg0.get("id");
					String action = (String)arg0.get("action");
					String msg = (String)arg0.get("msg");
					boolean you = (Boolean)arg0.get("you");
					System.out.println("  "+speaker + (you? " (you)": "")+": "+action+" "+msg);
					if (!you) {
						if (action.equals("Message"))
							onNaturalLanguageMessage(msg);
						else if (action.equals("Connect"))
							onPartnerConnect();
						else if (action.equals("Disconnect"))
							onPartnerDisconnect();
					}
				} else {
					String arg0 = (String)args[0];
					onMessage(arg0, ack);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * Negotiation-specific event handlers:
	 */

	public void onPartnerConnect()  {
		sayToNegotiationServer("Hello! I am the "+agentIdOfThisAgent);
	}
	
	/**
	 * @param jsonBid a JSON object that represents a bid - {issue1:value1, issue2:value2, ...}
	 */
	public void onPartnerOffer(JSONObject jsonBid) throws JSONException, UnknownIssueException, UnknownValueException {
		//System.out.println(arg0.toString(2));
		HashMap<Integer, Value> demandedBidValues = new HashMap<Integer, Value>();  // collect specific-issue actions
		for (Iterator<?> iIssue = jsonBid.keys(); iIssue.hasNext();) {
			String issueName = (String)iIssue.next();
			if (issueName.isEmpty()) continue;
			String valueName = jsonBid.getString(issueName);
			if (valueName.isEmpty()) continue;

			Issue issue = domain.issueByName(issueName);
			if (issue==null)
				throw new UnknownIssueException(issueName);
			switch(issue.getType()) {
				case DISCRETE:
					IssueDiscrete issueDiscrete = (IssueDiscrete)issue;
					Value value = issueDiscrete.valueByName(valueName);
					if (value==null)
						throw new UnknownValueException(issueDiscrete, valueName);
					demandedBidValues.put(issue.getNumber(), value);
					break;
				default:
					throw new UnsupportedOperationException("Only discrete types are supported currently!");
			}
		}

		if (!demandedBidValues.isEmpty()) {
			Bid theBid = new Bid(domain, demandedBidValues);
			//if (bidTime!=null)
			//	theBid.setTime(bidTime);
			Action theAction = new Offer(agentIdOfOtherPlayer, theBid);
			agent.ReceiveMessage(theAction);
		}
	}

	public void onPartnerAccept() {
		agent.ReceiveMessage(new Accept());
	}

	public void onPartnerReject() {
		agent.ReceiveMessage(new Reject());
	}

	public void onNaturalLanguageMessage(String message) {
		sayToNegotiationServer("I didn't understsand your message '"+message+"'. Please say it in other words.");
	}

	public void onPartnerQuit()  {
		agent.ReceiveMessage(new EndNegotiation());
	}

	public void onPartnerDisconnect()  {
		sayToNegotiationServer("Bye!");
	}
	
	public void sayToNegotiationServer(String message) {
		negotiationSocket.emit("message",message);
	}
	
	/*
	 * Main program:
	 */
	
	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	
	public static void main(String[] args) throws Exception {
		if (args.length<3) {
			System.err.println("SYNTAX: "+thisClassName+" <path-to-domain-file> <url-of-negotiation-server> <game-type>");
			System.exit(1);
		}
		java.util.logging.Logger.getLogger("io.socket").setLevel(Level.WARNING);
		new NegotiationClient(new Domain(args[0]), args[1], args[2]).start();  // Start the first client. It will launch new clients as the need arises.
	}
}
