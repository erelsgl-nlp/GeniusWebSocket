package negotiator.gui.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.NoNLPDataException;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.actions.Reject;
import negotiator.exceptions.MissingIssueException;
import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.UnknownIssueException;
import negotiator.exceptions.UnknownValueException;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;

import org.grammaticalframework.Linearizer.LinearizerException;
import org.grammaticalframework.UnknownLanguageException;


/**
 * Utility functions that bridge between grammars and semantic representations to GENIUS domains and actions. 
 * to the corresponding GENIUS Action. 
 * @author Erel Segal
 * @since 2011-12-18
 */
public class GrammarToGeniusBridge {
	
	private GrammarToGeniusBridge() { /* Prevent initialization */ }
	
	/**
	 * @param domain a GENIUS domain.
	 * @param sidename one of the sides in this domain.
	 * @return the corresponding grammar rules.
	 */
	public static GFRules GFRulesFromDomain(Domain domain, String sidename) throws IOException, NoNLPDataException, UnknownLanguageException, LinearizerException {
		return new GFRules(
			domain.getPGFInputStream(), 
			domain.getConcreteGrammarName(sidename),
			"nlp_eng_genius");
	}
	
	protected static final String tokenCharacters = ":<>";
	protected static final String leftTokenCharacters = "%";
	protected static final Pattern tokenCharactersPattern = Pattern.compile("(["+tokenCharacters+leftTokenCharacters+"])");
	protected static final Pattern leadingWhitespacePattern = Pattern.compile("\\s+"+"(["+tokenCharacters+leftTokenCharacters+"])");
	protected static final Pattern trailingWhitespacePattern = Pattern.compile("(["+tokenCharacters+"])\\s+");
	protected static String untokenize(String result) {
		result = leadingWhitespacePattern.matcher(result).replaceAll("$1");
		result = trailingWhitespacePattern.matcher(result).replaceAll("$1");
		return result;
	}
	
	/**
	 * Copy a value for a specific issue, from the source bid to the target map.
	 * @param domain [INPUT]
	 * @param sourceBid [INPUT]
	 * @param issueName [INPUT]
	 * @param targetMap [OUTPUT]
	 * @throws UnknownIssueException if issueName does not exist in the given domain.
	 * @throws MissingIssueException if issueName exists in the domain but has no value in sourceBid.
	 */
	public static void copyValueForIssue(Domain domain, Bid sourceBid, String issueName, HashMap<Integer, Value> targetMap) throws UnknownIssueException, MissingIssueException {
		Issue issue = domain.issueByName(issueName);
		if (issue==null)
			throw new UnknownIssueException(issueName);
		if (!sourceBid.hasValue(issue.getNumber()))
			throw new MissingIssueException(issueName);
		Value value = sourceBid.getValue(issue.getNumber());
		targetMap.put(issue.getNumber(), value);
	}
	
	public static <Key,ValueType> void copyValuesForNonexistingKeys(Map<Key,ValueType> target, Map<Key,ValueType> source) {
		for (Entry<Key,ValueType> entry: source.entrySet()) {
			if (target.containsKey(entry.getKey()))
				continue;
			target.put(entry.getKey(), entry.getValue());
		}
	}
	
	
	/**
	 * @param issueName (INPUT)
	 * @param valueName (INPUT)
	 * @param bidValues (OUTPUT)
	 * @throws UnknownIssueException 
	 * @throws UnknownValueException 
	 */
	public static void putIssueValuePairInBid(Domain domain, String issueName, String valueName, Map<Integer, Value> bidValues) throws UnknownIssueException, UnknownValueException {
		if (issueName.isEmpty()) return;
		if (valueName.isEmpty()) return;
		Issue issue = domain.issueByName(issueName);
		if (issue==null)
			throw new UnknownIssueException(issueName);
		switch(issue.getType()) {
		case DISCRETE:
			IssueDiscrete issueDiscrete = (IssueDiscrete)issue;
			Value value = issueDiscrete.valueByName(valueName);
			if (value==null)
				throw new UnknownValueException(issueDiscrete, valueName);
			bidValues.put(issue.getNumber(), value);
			break;
		default:
			throw new UnsupportedOperationException("Only discrete types are supported currently!");
		}
	}

	/**
	 * @param semanticString a string returned by the {@link GrammarPanel}, e.g. "<action:demand:Job Description:QA>",
	 * @param bidTime if this is a bid action, it will have this time stamp (optional)
	 * @param opponentLatestBidAction If this is an accept/reject/partial-agree action, it will relate to this opponent bid (optional) 
	 * @param speakerLatestBidAction If this is an append action, it will relate to this bid (optional) 
	 * @return the corresponding GENIUS Actions, e.g. an Offer action.
	 * @throws UnknownIssueException if the semantic string contains an issue that is not in the domain.
	 * @throws UnknownValueException if the semantic string contains a value that is not in the list of values for an issue in the domain.  
	 */
	public static List<Action> semanticStringToGeniusAction(String semanticString, Domain domain, AgentID thisAgent, Integer bidTime, BidAction opponentLatestBidAction, BidAction speakerLatestBidAction) throws NegotiatorException {
		List<Action> actions = new ArrayList<Action>();

		semanticString = untokenize(semanticString);
		Bid opponentLatestBid = opponentLatestBidAction==null? null: opponentLatestBidAction.getBid();
		Bid speakerLatestBid = speakerLatestBidAction==null? null: speakerLatestBidAction.getBid();
		HashMap<Integer, Value> demandedBidValues = new HashMap<Integer, Value>();  // collect specific-issue actions
		HashMap<Integer, Value> agreedBidValues = new HashMap<Integer, Value>();    // collect general actions
		//String[] semanticStrings = semanticString.split(" AND ");
		Matcher matcher = PatternCache.matcher("<action:[^<>]*>", semanticString);
		Matcher subMatcher;

		/*
		 * If the agent partially agrees with opponentLatestBidAction, this means that
		 *    the agent agrees with all issues in opponentLatestBidAction EXCEPT the ones that he mentions explicitly.
		 */

		while (matcher.find()) {
			String semanticSubstring = matcher.group(0);
			if ((subMatcher=PatternCache.matcher("<action:demand:(.*):(.*)>", semanticSubstring)).matches()||
				(subMatcher=PatternCache.matcher("<action:agree:(.*):(.*)>", semanticSubstring)).matches()	) {
				String issueName = subMatcher.group(1);
				String valueName = subMatcher.group(2);
				putIssueValuePairInBid(domain, issueName, valueName, demandedBidValues);
			} else if ((subMatcher=PatternCache.matcher("<action:reject:(.*)>", semanticSubstring)).matches()) {
				//String issueName = subMatcher.group(1);
				actions.add(new Reject(thisAgent, opponentLatestBidAction));
			} else if ((subMatcher=PatternCache.matcher("<action:agree:general>", semanticSubstring)).matches()) {
				actions.add(new Accept(thisAgent, opponentLatestBidAction));
			} else if ((subMatcher=PatternCache.matcher("<action:partial-agree:(.*)>", semanticSubstring)).matches()) {
				if (opponentLatestBid==null) 
					throw new IllegalArgumentException("partial agreement without a base opponent action");
				String issueName = subMatcher.group(1);
				if (issueName.equals("general")) {
					copyValuesForNonexistingKeys(agreedBidValues, opponentLatestBid.getValues());
				} else {
					copyValueForIssue(domain, opponentLatestBid, issueName, demandedBidValues);
				}
			} else if ((subMatcher=PatternCache.matcher("<action:append(.*)>", semanticSubstring)).matches()) {
				if (speakerLatestBid==null) 
					throw new IllegalArgumentException("append without a base speaker action");
				copyValuesForNonexistingKeys(agreedBidValues,speakerLatestBid.getValues());
			} else if ((subMatcher=PatternCache.matcher("<action:insist:(.*)>", semanticSubstring)).matches()) {
				if (speakerLatestBid==null) 
					throw new IllegalArgumentException("insist without a base speaker action");
				String issueName = subMatcher.group(1);
				if (issueName.equals("general")) {
					copyValuesForNonexistingKeys(agreedBidValues, speakerLatestBid.getValues());
				} else {
					copyValueForIssue(domain, speakerLatestBid, issueName, demandedBidValues);
				}
			} else if ((subMatcher=PatternCache.matcher("<action:quit(.*)>", semanticSubstring)).matches()) {
				actions.add(new EndNegotiation(thisAgent));
			} else if ((subMatcher=PatternCache.matcher("<action:question:(.*)>", semanticSubstring)).matches()) {
				System.err.println("Cannot handle questions right now!");
			} else {
				System.err.println("Unknown action type '"+semanticSubstring+"'");
			}
		}
		
		copyValuesForNonexistingKeys(demandedBidValues, agreedBidValues);
		
		if (!demandedBidValues.isEmpty()) {
			Bid theBid = new Bid(domain, demandedBidValues);
			if (bidTime!=null)
				theBid.setTime(bidTime);
			Action theAction = new Offer(thisAgent, theBid);
			actions.add(theAction);
		}
		
		return actions;
	}
	
	public static List<Action> semanticStringToGeniusAction(String semanticString, Domain domain, AgentID thisAgent) throws NegotiatorException {
		return semanticStringToGeniusAction(semanticString, domain, thisAgent, null, null, null);
	}
	
	
	
	/**
	 * @param geniusAction a GENIUS Action, e.g. an Offer action.
	 * @return the corresponding semantic string, e.g. <action:demand:Salary:7,000>
	 */
	public static String geniusActionToSemanticString(Action geniusAction, Domain domain) {
		StringBuffer semantic = new StringBuffer();
		if (geniusAction instanceof BidAction) {
			for (Map.Entry<Integer, Value> entry: ((BidAction)geniusAction).getBid().getValues().entrySet()) {
				String issueName = domain.getObjective(entry.getKey()).getName();
				if (semantic.length()>0)
					semantic.append(" and ");
				semantic.append("<action:demand:"+issueName+":"+entry.getValue()+">");
			}
		} else if (geniusAction instanceof Reject) {
			BidAction rejectedAction = ((Reject)geniusAction).getAcceptedOrRejectedAction();
			if (rejectedAction==null)
				return "<action:reject:general>";
			else {
				return "<action:reject:general>"; // ignore the rejected offer - problem with partial offers
				/*
				for (Map.Entry<Integer, Value> entry: rejectedAction.getBid().getValues().entrySet()) {
					String issueName = domain.getObjective(entry.getKey()).getName();
					if (semantic.length()>0)
						semantic.append(" AND ");
					semantic.append("<action:reject:"+issueName+">");
				}*/
			}
		} else if (geniusAction instanceof Accept) {
			//BidAction acceptedAction = ((Reject)geniusAction).getRejectedAction();
			//if (acceptedAction==null)
				return "<action:agree:general>";
		} else if (geniusAction instanceof EndNegotiation) {
			return "<action:quit>";
		}
		return semantic.toString();
	}
	
	public static String geniusActionsToSemanticString(List<Action> geniusActions, Domain domain) {
		StringBuffer semantic = new StringBuffer();
		for (Action action: geniusActions) {
			if (semantic.length()>0)
				semantic.append(" AND ");
			semantic.append(geniusActionToSemanticString(action, domain));
		}
		return semantic.toString();
	}
	
	/**
	 * demo program.
	 */
	public static void main (String[] args) throws Exception {
		GrammarToGeniusBridgeTest.main(args);
	}
}
