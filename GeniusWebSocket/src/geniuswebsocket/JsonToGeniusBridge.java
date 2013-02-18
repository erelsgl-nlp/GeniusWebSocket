package geniuswebsocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.actions.Offer;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.UnknownIssueException;
import negotiator.exceptions.UnknownValueException;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;

/**
 * Utility functions that bridge between JSON objects 
 * to the corresponding GENIUS Action.
 * 
 * @see negotiator.gui.nlp.GrammarToGeniusBridge
 * @author Erel Segal haLevi
 * @since 2013-02-18
 */
public class JsonToGeniusBridge {
	
	private JsonToGeniusBridge() { /* Prevent initialization */ }

	/**
	 * @see negotiator.gui.nlp.GrammarToGeniusBridge#semanticStringToGeniusAction
	 */
	@SuppressWarnings("unused")
	public static List<Action> jsonObjectToGeniusAction(JSONObject json, Domain domain, AgentID thisAgent, Integer bidTime, BidAction opponentLatestBidAction, BidAction speakerLatestBidAction) throws NegotiatorException, JSONException {
		List<Action> actions = new ArrayList<Action>();

		Bid opponentLatestBid = opponentLatestBidAction==null? null: opponentLatestBidAction.getBid();
		Bid speakerLatestBid = speakerLatestBidAction==null? null: speakerLatestBidAction.getBid();
		
		HashMap<Integer, Value> demandedBidValues = new HashMap<Integer, Value>();  // collect specific-issue actions
		for (Iterator<?> iIssue = json.keys(); iIssue.hasNext();) {
			String issueName = (String)iIssue.next();
			if (issueName.isEmpty()) continue;
			String valueName = json.getString(issueName);
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
			if (bidTime!=null)
				theBid.setTime(bidTime);
			Action theAction = new Offer(thisAgent, theBid);
			actions.add(theAction);
		}
		
		return actions;
	}
	
	public static List<Action> jsonObjectToGeniusAction(JSONObject json, Domain domain, AgentID thisAgent) throws NegotiatorException, JSONException {
		return jsonObjectToGeniusAction(json, domain, thisAgent, null, null, null);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
