package geniuswebsocket;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.actions.Offer;
import negotiator.exceptions.*;
import negotiator.gui.nlp.GrammarToGeniusBridge;
import negotiator.issue.*;

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
	 * @param json an object such as {"issue1": "value1", "issue2": "value2", ...}
	 * @param domain
	 * @param thisAgent
	 * @param bidTime
	 * @param opponentLatestBidAction
	 * @param speakerLatestBidAction
	 * @return a hash-map from issues to values (must be a HashMap because of dependencies in genius.Bid...)
	 */
	public static HashMap<Integer, Value> jsonObjectToGeniusBidValues(JSONObject json, Domain domain) throws NegotiatorException, JSONException {
		HashMap<Integer, Value> bidValues = new HashMap<Integer, Value>();  // collect specific-issue actions
		for (Iterator<?> iIssue = json.keys(); iIssue.hasNext();) {
			String issueName = (String)iIssue.next();
			String valueName = json.getString(issueName);
			GrammarToGeniusBridge.putIssueValuePairInBid(domain, issueName, valueName, bidValues);
		}
		return bidValues;
	}
	
	/**
	 * Go over all objects in the array, and merge fields with equal names.
	 * @param objects
	 * @return the merged object.
	 * @throws JSONException 
	 */
	public static JSONObject deepMergeJsonObjects(JSONObject[] objects) throws JSONException {
		JSONObject whole = new JSONObject();
		for (JSONObject part: objects) {
			for (Iterator<?> iKey = part.keys(); iKey.hasNext();) {
				String key = (String)iKey.next();
				Object value = part.get(key);
				
				if (!whole.has(key)) {
					// new value for "key":
					whole.put(key, value);
				} else {
					// existing value for "key" - deep merge:
					
				}
			}
		}
		return whole;
	}
	

	
	/**
	 * @see negotiator.gui.nlp.GrammarToGeniusBridge#semanticStringToGeniusAction
	 */
	@SuppressWarnings("unused")
	public static List<Action> jsonObjectToGeniusAction(JSONObject json, Domain domain, AgentID thisAgent, Integer bidTime, BidAction opponentLatestBidAction, BidAction speakerLatestBidAction) throws NegotiatorException, JSONException {
		List<Action> actions = new ArrayList<Action>();

		Bid opponentLatestBid = opponentLatestBidAction==null? null: opponentLatestBidAction.getBid();
		Bid speakerLatestBid = speakerLatestBidAction==null? null: speakerLatestBidAction.getBid();
		
		if (json.has("offer")) {
			JSONObject jsonBid = json.getJSONObject("offer");
			HashMap<Integer, Value> demandedBidValues = jsonObjectToGeniusBidValues(jsonBid, domain);
			if (!demandedBidValues.isEmpty()) {
				Bid theBid = new Bid(domain, demandedBidValues);
				if (bidTime!=null)
					theBid.setTime(bidTime);
				Action theAction = new Offer(thisAgent, theBid);
				actions.add(theAction);
			}
		}
		
		return actions;
	}

	public static List<Action> jsonObjectToGeniusAction(JSONObject json, Domain domain, AgentID thisAgent) throws NegotiatorException, JSONException {
		return jsonObjectToGeniusAction(json, domain, thisAgent, null, null, null);
	}
	
	/**
	 * demo program
	 */
	public static void main(String[] args) throws JSONException {
	}
}
