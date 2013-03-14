package negotiator.gui.nlp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GrammarRules {
	public static final String SELECT_PROMPT="-- select --";

	/**
	 * Init the grammar rules using the given strings.
	 * @param theRoot the initial derivation string.
	 * @param rulesSides Each even string is a LHS, and each odd string is its RHS.
	 */
	public GrammarRules(String theRoot, String... rulesSides) {
		this.root = theRoot;
		if (rulesSides.length % 2 != 0)
			throw new IllegalArgumentException("There number of lines must be even - LHSs and RHSs");
		rules = new GrammarRule[rulesSides.length/2];
		for (int i=0; i<rulesSides.length; i+=2)
			rules[i/2] = new GrammarRule(rulesSides[i], rulesSides[i+1]);
	}

	/**
	 * Init the grammar rules using the given file[s].
	 * @param rulesFiles one or more files that contain lists of rules, in the following format:
	 * <pre>
	 * left-hand-side:
	 * * right-hand-side
	 * </pre>
	 */
	public GrammarRules(File... rulesFiles) throws IOException {
		List<GrammarRule> rulesFromFile = new ArrayList<GrammarRule>();
		for (File file: rulesFiles) {
			List<String> lines = loadFileToList(file);
			Matcher matcher;
			String currentLHS=null, currentRHS=null;
			for (String line: lines) {
				if ((matcher=PatternCache.matcher("(.*):", line)).matches()) {
					currentLHS = matcher.group(1);
					if (currentLHS.isEmpty())
						currentLHS = null;
				} else if ((matcher=PatternCache.matcher("[*]\\s*(.*)", line)).matches()) {
					currentRHS = matcher.group(1);
					if (currentLHS!=null)
						rulesFromFile.add(new GrammarRule(currentLHS, currentRHS));
				} else if ((matcher=PatternCache.matcher("ROOT\\s*=\\s*(.*)", line)).matches()) {
					root = matcher.group(1);
				}
			}
		}
		rules = rulesFromFile.toArray(new GrammarRule[0]);
	}
	
	/**
	 * @return the root of the grammar - an initial string from which all other strings can be derived.
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param lhsPattern a pattern for a rule's left-hand-side.
	 * @return all right-hand-sides that for left-hand-sides that match the given pattern.
	 */
	public List<GrammarRule> getMatchingRules(String lhsPattern) {
		List<GrammarRule> result = new ArrayList<GrammarRule>();
		String lhsPatternForInput = lhsPattern;
		for (int i=0; i<rules.length; i++) {
			if (rules[i].lhs.matches(lhsPatternForInput)) 
				result.add(rules[i]);
		}
		return result;
	}
	
	/**
	 * @param lhsPattern a pattern for a rule's left-hand-side.
	 * @return HTML for a select-box for selecting a right-hand-side for left-hand-sides that match the given pattern.
	 * @see GrammarRuleComboBox#GrammarRuleComboBox
	 */
	public String getMatchingRulesOptionsHtml(String lhsPattern) {
		StringBuilder sb = new StringBuilder();
		for (GrammarRule rule: getMatchingRules(inputRegexp(lhsPattern))) 
			sb.append("\t<option value='"+rule.lhs.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")+"'>")
			  .append(outputRegexp(rule.rhs).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
			  .append("</option>\n");
		return "<select>\n\t<option>"+SELECT_PROMPT+"</option>\n" + sb.toString() + "</select>";
	}
	
	/**
	 * Run all rules of this grammar on the given string.
	 * @param start e.g. "<action:demand:Salary:7000>"
	 * @return the final string, e.g. "I want to earn 7000 per month".
	 */
	public String generate(String start) {
		String result = null;
		for (int i=0; i<10 /* prevent infinite iteration */; i++) {
			result = start;
			for (GrammarRule rule: rules)
				result = rule.replaceLhsWithRhs(result);
			if (result.equals(start) || !NONTERMINAL.matcher(result).find()) 
				break;
			start = result;
		}
		// post processing:
		if (result!=null)
			result = result.replaceAll("(?i)[.] and", "; and");
		return result;
	}

	@Override public String toString() {
		return Arrays.asList(this.rules).toString();
	}
	
	
	
	
	
	/*
	 * PROTECTED ZONE
	 */

	/**
	 * The grammar rules. Each even string is a LHS, and each odd string is its RHS.
	 */
	protected GrammarRule[] rules;
	protected String root;           // the derivation root 

	protected static String inputRegexp(String bidirectionalRegexp) {
		String input = bidirectionalRegexp;
		input = input.replaceAll("[$]\\d+\\(", "("); // "$1(.*)" => "(.*)";
		input = input.replaceAll("[$]\\d+\\[(.*?)\\]", "([^<>:$1]*)[$1]"); // "$1[.]?" => "([^<>:]*)";
		input = input.replaceAll("[$]\\d+", "([^<>:]*)"); // "$1" => "([^<>:]*)"; 
		// NOTE: $1 without parens does not include special chars, but it DOES include commas (for numbers)
		return input;
	}

	protected static String outputRegexp(String bidirectionalRegexp) {
		String output = bidirectionalRegexp;
		output = output.replaceAll("([$]\\d+)\\(.*?\\)", "$1"); // "$1(.*)" => "$1";
		output = output.replace("(?i)","").replace("(?s)","");
		output = output.replace("[?]","__QUESTIONMARK__");
		output = output.replace("[.]","__DOT__");
		output = output.replace("[*]","__STAR__");
		output = output.replace(".?","").replace(".*","").replace("?","");
		output = output.replace("__QUESTIONMARK__","?");
		output = output.replace("__DOT__",".");
		output = output.replace("__STAR__","*");
		output = output.replaceAll("\\[(.)\\][?]?","$1");  //  [a] => a, [a]? => a
		return output;
	}

	protected static final Pattern NONTERMINAL = Pattern.compile("<[^<>]*>");
	
	/**
	 * See asyncBidding.jsp#nonterminalsInPattern
	 * @param pattern a rule-pattern
	 * @return a list of all nonterminals in the pattern. 
	 * <p>A nonterminal is denoted by angled brackets, for example, in:
	 * <p>I want &lt;number&gt; dollars
	 * <p>The nonterminal is   "&lt;number&gt;".
	 */
	protected static List<String> nonterminalsInPattern(String pattern) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = NONTERMINAL.matcher(pattern);
		while (matcher.find()) 
			result.add(matcher.group());
		return result;
	}


	/**
	 * Reads a text file into a list of strings, each containing one line of the file
	 * @param iFile
	 * @return
	 * @throws IOException
	 */
	protected static List<String> loadFileToList(File iFile) throws IOException {
		List<String> outList = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(iFile));
		String line;
		while ((line = reader.readLine()) != null) {
			outList.add(line);
		}
		return outList;
	}

	
	
	
	
	
	
	

	/**
	 * demo program
	 */
	public static void main(String[] args) throws Exception {
		GrammarRulesTest.main(args);
	}
}
