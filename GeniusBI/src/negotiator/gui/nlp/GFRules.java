package negotiator.gui.nlp;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.grammaticalframework.*;
import org.grammaticalframework.Linearizer.LinearizerException;
import org.grammaticalframework.Trees.PrettyPrinter;
import org.grammaticalframework.Trees.Absyn.Tree;
import org.grammaticalframework.reader.*;
import org.junit.Test;


/**
 * Rules for parsing and generating for a single user in a single domain.
 * @see GrammarRules
 * @author erelsgl
 * @since 2012-03-06
 */
public class GFRules {
	public GFRules(InputStream input, String concreteGrammarName, String semanticGrammarName) throws IOException, UnknownLanguageException, LinearizerException {
		pgf = PGFBuilder.fromInputStream(input);
		absGrammar = pgf.getAbstract();

		absCats = new HashMap<String,AbsCat>();
		for (AbsCat cat: absGrammar.getAbsCats())
			absCats.put(cat.name(), cat);

		absFuns = new HashMap<String,AbsFun>();
		for (AbsFun fun: absGrammar.getAbsFuns())
			absFuns.put(fun.name, fun);
		
		mapConcreteToFunctions = new HashMap<String, Map<String, CncFun>>();

		humanGrammar = pgf.concrete(concreteGrammarName);
		humanParser = new Parser(pgf, concreteGrammarName);
		humanLinearizer = new Linearizer(pgf, concreteGrammarName);
		mapConcreteToFunctions.put(concreteGrammarName, concreteFunctions(humanGrammar));

		geniusGrammar = pgf.concrete(semanticGrammarName);
		geniusParser = new Parser(pgf, semanticGrammarName);
		geniusLinearizer = new Linearizer(pgf, semanticGrammarName);
		mapConcreteToFunctions.put(semanticGrammarName, concreteFunctions(geniusGrammar));

		root = "<"+absGrammar.startcat()+">";
	}

	protected PGF pgf;
	protected Abstract absGrammar;
	protected Map<String,AbsCat> absCats;
	protected Map<String,AbsFun> absFuns;
	protected Concrete humanGrammar, geniusGrammar, biuteeGrammar;
	protected Parser humanParser, geniusParser, biuteeParser;
	protected Linearizer humanLinearizer, geniusLinearizer, biuteeLinearizer;
	
	/**
	 * Map name of concrete grammar to map of concrete functions in that grammar. 
	 */
	protected Map<String, Map<String, CncFun>> mapConcreteToFunctions;
	
	//protected HashMap<String, CncFun> humanFuns, geniusFuns;
	protected String root;
	
	protected static final String tokenCharacters = ":<>";
	protected static final String leftTokenCharacters = "%";
	protected static final Pattern tokenCharactersPattern = Pattern.compile("(["+tokenCharacters+leftTokenCharacters+"])");
	
	
	
	/*
	 * STATIC FUNCTIONS
	 */

	protected static String[] tokenize(String s) {
		s = tokenCharactersPattern.matcher(s).replaceAll(" $1 ");
		return s.replaceAll("\\s\\s+", " ").replaceAll("^ ","").replaceAll(" $","").split(" ");
	}
	
	protected static String join(String sep, Iterable<?> tokens) {
		StringBuffer buf = new StringBuffer();
		for (Object token : tokens) {
			if (buf.length()>0)
				buf.append(sep);
			buf.append(token);
		}
		return buf.toString();
	}
	
	protected static String join(String sep, Object[] tokens) {
		StringBuffer buf = new StringBuffer();
		for (Object token : tokens) {
			if (buf.length()>0)
				buf.append(sep);
			buf.append(token);
		}
		return buf.toString();
	}
	
	protected static final Pattern leadingWhitespacePattern = Pattern.compile("\\s+"+"(["+tokenCharacters+leftTokenCharacters+"])");
	protected static final Pattern trailingWhitespacePattern = Pattern.compile("(["+tokenCharacters+"])\\s+");
	protected static String untokenize(Vector<String> tokens) {
		String result = join(" ",tokens);
		result = leadingWhitespacePattern.matcher(result).replaceAll("$1");
		result = trailingWhitespacePattern.matcher(result).replaceAll("$1");
		return result;
	}
	
	public static Map<String,CncFun> concreteFunctions(Concrete concrete) {
		Map<String,CncFun> result = new HashMap<String,CncFun>();
		for (CncFun fun: concrete.getCncFuns())
			result.put(fun.name(), fun);
		return result;
	}

	
	
	public Set<String> concretes() {
		return mapConcreteToFunctions.keySet();
	}
	
	public Concrete humanGrammar() {
		return this.humanGrammar;
	}
	
	public Concrete geniusGrammar() {
		return this.geniusGrammar;
	}
	
	public Concrete biuteeGrammar() {
		return this.biuteeGrammar;
	}
	
	public String humanToGeniusSingle(String human) throws LinearizerException {
		String[] tokens = tokenize(human);
		Tree[] trees = humanParser.parseToTrees(tokens);
		if (trees.length<1)
			return null;
		Vector<String> genius = geniusLinearizer.linearizeTokens(trees[0]);
		return untokenize(genius);
	}

	public String humanToGenius(String human) throws LinearizerException {
		String[] humans = human.split("(?i)\\s+AND\\s+");
		String[] geniuss= new String[humans.length];
		int nulls = 0;
		for (int i=0; i<geniuss.length; ++i) {
			geniuss[i] = humanToGeniusSingle(humans[i]);
			if (geniuss[i]==null) nulls++;
		}
		return (nulls>0? null: join(" AND ", geniuss));
	}

	
	public String geniusToHumanSingle(String genius) throws LinearizerException {
		String[] tokens = tokenize(genius);
		Tree[] trees = geniusParser.parseToTrees(tokens);
		if (trees.length<1)
			return null;
		Vector<String> human = humanLinearizer.linearizeTokens(trees[0]);
		return untokenize(human);
	}
	
	public String geniusToHuman(String genius) throws LinearizerException {
		String[] geniuss = genius.split("(?i)\\s+and\\s+");
		String[] humans = new String[geniuss.length];
		int nulls = 0;
		for (int i=0; i<geniuss.length; ++i) {
			humans[i] = geniusToHumanSingle(geniuss[i]);
			if (humans[i]==null) nulls++;
		}
		return (nulls>0? null: join(" and ", humans));
	}

	/**
	 * Given a human sentence-start, predict the possible next tokens.
	 * @param human the sentence-start.
	 * @return the possible tokens.
	 */
	public String[] nextPossibleTokensOfHuman(String human) {
		String[] predictions = humanParser.parse(human).predict();
		Arrays.sort(predictions);
		return predictions;
	}

	
	
	/**
	 * @return the root of the grammar - the start category
	 */
	public String getRoot() {
		return root;
	}

	
	@Test public void testGenerateAllTrees() throws LinearizerException {
		GenerateTrees generator = new GenerateTrees(pgf);
		Collection<Tree> allTrees = generator.generateTrees(pgf.getAbstract().startcat(), 2);
		for (Tree t: allTrees) // Generates sentence of depth up to N
			System.out.println(PrettyPrinter.print(t)+"\t"+humanLinearizer.linearizeString(t)+"\t"+geniusLinearizer.linearizeString(t));		
	}

	
	/**
	 * demo program
	 */
	public static void main(String[] args) throws Exception {
		GFRulesTest.main(args);
	}
	
}
