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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

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
import negotiator.actions.Action;
import negotiator.actions.Offer;
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
public class NegotiationClient implements IOCallback {
	/**
	 * socket for connecting to the Node.js negotiation server:
	 */
	private SocketIO socket;
	
	
	/**
	 * agent for strategic negotiation:
	 */
	private Agent agent;
	
	private Domain domain;
	
	/**
	 *  @throws IOException 
	 * @throws NegotiatorException 
	 *  @see  negotiator.protocol.asyncoffers.AsyncOffersProtocol#runNegotiationSession
	 *  @see  negotiator.protocol.asyncoffers.AsyncOffersBilateralAtomicNegoSession#run
	 *  @see  negotiator.protocol.Protocol#loadWorldInformation
	 */
	private void initializeAgent(String role, String partnerRole) throws IOException, NegotiatorException {
		agent = new KBAgent();
		agent.setName(role);
		agent.setAgentID(new AgentID(role));
		agent.setActionListener(new ActionListener() {
			@Override public void actionSent(Action a) {
				socket.send("I do: "+a.toString());
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
	

	public NegotiationClient() throws Exception {
		String role = "Candidate";
		String otherRole = "Employer";
		domain = new Domain("/host/workspace/GeniusBI/etc/templates/JobCandiate/JobCanDomain.xml");
		initializeAgent(role, otherRole);
	
		socket = new SocketIO();
		socket.connect("http://localhost:4000/", this);

		socket.emit("start_session", new JSONObject()
			.put("userid", "Java "+new Date().toString())
			.put("gametype", "menus_humanvshuman")
			.put("role", "Candidate")
			);

		socket.send("Hello! I am the "+role);
	}

	@Override public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			System.out.println("Server said:" + json.toString(2));
			socket.send("You just said '"+json.toString(2)+"'");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
		socket.send("You just said '"+data+"'");
	}

	@Override public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured");
		socketIOException.printStackTrace();
	}

	@Override public void onDisconnect() {
		System.out.println("Connection terminated.");
	}

	@Override public void onConnect() {
		System.out.println("Connection established");
	}

	@Override public void on(String event, IOAcknowledge ack, Object... args) {
		System.out.println("Server triggered event '" + event + "'.");
		try {
			if (event.equals("message")) {
				JSONObject arg0 = (JSONObject)args[0];
				System.out.println("\t"+arg0.get("id") + " said: "+arg0.get("msg"));
			} else if (event.equals("offer")) {
				JSONObject arg0 = (JSONObject)args[0];
				System.out.println(arg0.toString(2));
				HashMap<Integer, Value> demandedBidValues = new HashMap<Integer, Value>();  // collect specific-issue actions
				for (Iterator<?> iIssue = arg0.keys(); iIssue.hasNext();) {
					String issueName = (String)iIssue.next();
					String valueName = arg0.getString(issueName);

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
					Action theAction = new Offer(agent.getAgentID(), theBid);
					agent.ReceiveMessage(theAction);
				}
			}
		} catch (JSONException | UnknownIssueException | UnknownValueException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new NegotiationClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
