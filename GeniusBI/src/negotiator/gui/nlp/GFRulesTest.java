package negotiator.gui.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.grammaticalframework.GenerateTrees;
import org.grammaticalframework.Linearizer;
import org.grammaticalframework.PGFBuilder;
import org.grammaticalframework.UnknownLanguageException;
import org.grammaticalframework.Linearizer.LinearizerException;
import org.grammaticalframework.Trees.PrettyPrinter;
import org.grammaticalframework.Trees.Absyn.Tree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GFRulesTest {
	static GFRules employerRules = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		employerRules = new GFRules(
				new FileInputStream("etc/templates/JobCandiate/nlp_abs.pgf"), 
				"nlp_eng_employer", 
				"nlp_eng_genius");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		employerRules = null;
	}
	
	public void testHumanToGenius(String human, String genius) throws LinearizerException {
		String actualGenius = employerRules.humanToGenius(human);
		if (!genius.equals(actualGenius)) {
			System.err.println("'"+human + "' should translate to '" + genius+"', but got '"+actualGenius+"'! '"+genius+"' translates to '"+employerRules.geniusToHuman(genius)+"'");
			Assert.fail();
		}
	}
	
	public void testGeniusToHuman(String human, String genius) throws LinearizerException {
		String actualHuman = employerRules.geniusToHuman(genius);
		if (!human.equals(actualHuman)) {
			System.err.println("'"+genius + "' should translate to '" + human+"', but got '"+actualHuman+"'! '"+human+"' translates to '"+employerRules.humanToGenius(human)+"'");
			Assert.fail();
		}
	}
	
	public void testBoth(String human, String genius) throws LinearizerException {
		testHumanToGenius(human,genius);
		testGeniusToHuman(human,genius);
	}
	
	public void testPredictions(String human, String[] expected) {
		String[] predictions = employerRules.nextPossibleTokensOfHuman(human);
		System.out.println(human + " => " + Arrays.asList(predictions));		
	}
	
	@Test public void testSpaces() throws LinearizerException {
		testBoth("I want you to work for 7,000 NIS per month", "<action:demand:Salary:7,000 NIS>");
		testGeniusToHuman("I want you to work for 7,000 NIS per month", " <action:demand: Salary:7,000 NIS > ");
		testHumanToGenius(" I want        you to work for 7,000    NIS per month", "<action:demand:Salary:7,000 NIS>");
	}
	
	@Test public void testIssues() throws LinearizerException {
		testBoth("I can agree on 20% pension", "<action:agree:Pension Fund:20%>");
		testBoth("I want you to work for 7,000 NIS per month", "<action:demand:Salary:7,000 NIS>");
	}

	@Test public void testAnd() throws LinearizerException {
		testBoth("I can agree on 0% pension and I want you to work for 12,000 NIS per month", "<action:agree:Pension Fund:0%> AND <action:demand:Salary:12,000 NIS>");
		testHumanToGenius("I can agree on 0% pension    AND   I want you to work for 12,000 NIS per month", "<action:agree:Pension Fund:0%> AND <action:demand:Salary:12,000 NIS>");
		testGeniusToHuman("I can agree on 0% pension and I want you to work for 12,000 NIS per month", "<action:agree:Pension Fund:0%>          and       <action:demand:Salary:12,000 NIS>");
	}	
	
	@Test public void testPredictions() {
		testPredictions("", null);
		testPredictions("leased car", null);
		testPredictions("I", null);
		testPredictions("I can", null);
		testPredictions("I want", null);
		testPredictions("What", null);
	}
	
	@Test public void testAcceptReject() throws LinearizerException {
		testBoth("I accept your offer", "<action:agree:general>");
		testHumanToGenius("I agree to your offer", "<action:agree:general>");
	}

	
	@Test public void testGenerateAllTrees() throws LinearizerException {
		employerRules.testGenerateAllTrees();
	}
	
	public void demo() throws Exception {
		System.out.println("concretes: "+employerRules.concretes());
		//testGenerateAllTrees();
		testAcceptReject();
		//testSpaces();
		//testIssues();
		//testPredictions();
		//testAnd();
		//System.out.println("\n\nJavascript:\n"+employerRules.getRulesJavascript());
	}

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		new GFRulesTest().demo();
		tearDownAfterClass();
	}

}
