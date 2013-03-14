package negotiator.gui.nlp;

import java.util.List;

import junit.framework.Assert;

import negotiator.AgentID;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.actions.BidAction;
import negotiator.exceptions.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for {@link GrammarToGeniusBridge}
 * @author Erel Segal
 * @since 2012-01-31
 */
public class GrammarToGeniusBridgeTest {
	
	static Domain domain;
	static AgentID agent;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass public static void setUpBeforeClass() throws Exception {
		 domain = new Domain("etc/templates/JobCandiate/JobCanDomain.xml");
		 agent = new AgentID("agent");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass public static void tearDownAfterClass() throws Exception {
		domain = null;
		agent = null;
	}
	
	public void assertActions(List<Action> actualActions, String expectedSemantic) throws NegotiatorException {
		String actualSemantic = GrammarToGeniusBridge.geniusActionsToSemanticString(actualActions, domain);
		Assert.assertEquals(expectedSemantic, actualSemantic);
		//List<Action> expectedActions = GrammarToGeniusBridge.semanticStringToGeniusAction(expectedSemantic, domain, agent);
		//Assert.assertEquals(expectedActions, actualActions);
	}
	
	
	public void testConvert(String semanticString) throws NegotiatorException {
		List<Action> actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString, domain, agent);
		String recreatedSemanticString = GrammarToGeniusBridge.geniusActionsToSemanticString(actions, domain);
		boolean backToOriginal = recreatedSemanticString.equals(semanticString);
		System.out.println(semanticString + " ==> \n\t" + actions + " ==> \n"+recreatedSemanticString + "\n\t"+ (backToOriginal? "back to original!": "different than original!")+"\n");
	}
	@Test (expected=IllegalArgumentException.class) public void testPartialAcceptWithoutBaseAction() throws Exception {
		String semanticString = "<action:partial-agree:general> AND <action:demand:Job Description:QA>";
		List<Action> actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString, domain, agent);
		System.out.println(semanticString + " ==> \n\t" + actions);
	}
	
	@Test (expected=UnknownIssueException.class) public void testUnknownIssue() throws Exception {
		testConvert("<action:demand:TUVWXYZ:QA>"); // non-existing issue - should return null
	}
	
	@Test (expected=UnknownValueException.class) public void testUnknownValue() throws Exception {
		testConvert("<action:demand:Job Description:ABCDE>"); // non-existing value - should return null
	}
	
	@Test public void testPartialAccept() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager> AND <action:demand:Promotion Possibilities:Fast promotion track> AND <action:demand:Working Hours:8 hours>";
		BidAction opponentBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);
		//System.out.println(semanticString0 + " ==> \n\t" + opponentBid);
		
		String semanticString1 = "<action:partial-agree:general> AND <action:demand:Job Description:QA>";
		List<Action> actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, opponentBid, null);
		assertActions(actions,   "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:QA> and <action:demand:Promotion Possibilities:Fast promotion track> and <action:demand:Working Hours:8 hours>");
		//System.out.println(semanticString1 + " ==> \n\t" + actions);
	
		semanticString1 = "<action:partial-agree:Salary> AND <action:demand:Job Description:QA>";
		actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, opponentBid, null);
		assertActions(actions,   "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:QA>");
		//System.out.println(semanticString1 + " ==> \n\t" + actions);
		
		semanticString1 = "<action:partial-agree:Promotion Possibilities> AND <action:demand:Job Description:QA>";
		actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, opponentBid, null);
		assertActions(actions,   "<action:demand:Job Description:QA> and <action:demand:Promotion Possibilities:Fast promotion track>");
		//System.out.println(semanticString1 + " ==> \n\t" + actions);
	}

	@Test public void testInsist() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager> AND <action:demand:Promotion Possibilities:Fast promotion track> AND <action:demand:Working Hours:8 hours>";
		BidAction speakerBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);
		//System.out.println(semanticString0 + " ==> \n\t" + speakerBid);

		String semanticString1 = "<action:insist:general> AND <action:agree:Working Hours:10 hours>";
		List<Action> actions1 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, null, speakerBid);
		assertActions(actions1, "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Promotion Possibilities:Fast promotion track> and <action:demand:Working Hours:10 hours>");
		
		semanticString1 = "<action:agree:Working Hours:10 hours> AND <action:insist:general>";
		actions1 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, null, speakerBid);
		assertActions(actions1, "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Promotion Possibilities:Fast promotion track> and <action:demand:Working Hours:10 hours>");
		
		semanticString1 = "<action:insist:Salary> AND <action:agree:Working Hours:10 hours>";
		actions1 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, null, speakerBid);
		assertActions(actions1, "<action:demand:Salary:20,000 NIS> and <action:demand:Working Hours:10 hours>");
	}

	@Test public void testInsistAndPartialAgree() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager> AND <action:demand:Promotion Possibilities:Fast promotion track> AND <action:demand:Working Hours:8 hours>";
		BidAction speakerBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);
		String semanticString1 = "<action:demand:Salary:7,000 NIS> AND <action:demand:Job Description:QA> AND <action:demand:Working Hours:10 hours> AND <action:demand:Promotion Possibilities:Slow promotion track>";
		BidAction opponentBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent).get(0);
		
		String semanticString2 = "<action:insist:general> AND <action:partial-agree:Job Description>";
		List<Action> actions2 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString2, domain, agent, null, opponentBid, speakerBid);
		assertActions(actions2,  "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:QA> and <action:demand:Promotion Possibilities:Fast promotion track> and <action:demand:Working Hours:8 hours>");

		String semanticString3 = "<action:partial-agree:Job Description> AND <action:insist:general>";
		List<Action> actions3 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString3, domain, agent, null, opponentBid, speakerBid);
		Assert.assertEquals(actions2, actions3);
		
		semanticString2 = "<action:partial-agree:general> AND <action:insist:Job Description>";
		actions2 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString2, domain, agent, null, opponentBid, speakerBid);
		assertActions(actions2, "<action:demand:Salary:7,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Promotion Possibilities:Slow promotion track> and <action:demand:Working Hours:10 hours>");

		semanticString3 = "<action:insist:Job Description> AND <action:partial-agree:general>";
		actions3 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString3, domain, agent, null, opponentBid, speakerBid);
		Assert.assertEquals(actions2, actions3);
	}
	
	@Test (expected=MissingIssueException.class) public void testPartialAcceptWithMissingIssue() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager> AND <action:demand:Working Hours:8 hours>";
		BidAction opponentBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);
		System.out.println(semanticString0 + " ==> \n\t" + opponentBid);
		String semanticString1 = "<action:partial-agree:Salary> AND <action:demand:Job Description:QA> AND <action:partial-agree:Leased Car>";
		List<Action> actions = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, opponentBid, null);
		System.out.println(semanticString1 + " ==> \n\t" + actions);
	}

	@Test public void testGeneral() throws Exception {
		testConvert("<action:demand:Job Description:QA>");
		testConvert("<action:demand:Job Description:QA> AND <action:demand:Salary:7,000 NIS>"); // different order
		testConvert("<action:demand:Job Description:QA> AND <action:agree:Salary:20,000 NIS>"); // agree => demand
		testConvert("<action:reject:Salary>");  // => reject general
		testConvert("<action:agree:general>");  
		testConvert("<action:demand:Salary:7,000 NIS> AND <action:reject:general> AND <action:demand:Job Description:QA>"); 
		testConvert("<action:question:Job Description>");  // exception - Cannot handle questions right now
	}

	@Test public void testSpaces() throws Exception {
		testConvert(" <action:demand:Job Description:QA>");
		testConvert("< action:demand:Job Description:QA> AND <action:demand:Salary:7,000 NIS>"); // different order
		testConvert("<action :demand:Job Description:QA> AND <action:agree:Salary:20,000 NIS>"); // agree => demand
		testConvert("<action: reject:Salary>");  // => reject general
		testConvert("<action:agree : general>");  
		testConvert("<action:demand:Salary: 7,000 NIS > AND <action:reject:general> AND <action:demand:Job Description:QA> "); 
	}
	
	@Test public void testAppend() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager> AND <action:demand:Promotion Possibilities:Fast promotion track> AND <action:demand:Working Hours:8 hours>";
		BidAction speakerBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);

		String semanticString1 = "<action:append:general> AND <action:demand:Leased Car:With leased car>";
		List<Action> actions1 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent, null, null, speakerBid);
		assertActions(actions1,  "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Leased Car:With leased car> and <action:demand:Promotion Possibilities:Fast promotion track> and <action:demand:Working Hours:8 hours>");
		//System.out.println(semanticString1 + " ==> \n\t" + actions1);
	}
	
	@Test public void testAppendAndPartialAgree() throws Exception {
		String semanticString0 = "<action:demand:Salary:20,000 NIS> AND <action:demand:Job Description:Project Manager>";
		BidAction speakerBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString0, domain, agent).get(0);
		String semanticString1 = "<action:demand:Salary:7,000 NIS> AND <action:demand:Promotion Possibilities:Fast promotion track>";
		BidAction opponentBid = (BidAction)GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString1, domain, agent).get(0);

		String semanticString2 = "<action:append:general> AND <action:partial-agree:general>";
		List<Action> actions2 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString2, domain, agent, null, opponentBid, speakerBid);
		assertActions(actions2,  "<action:demand:Salary:20,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Promotion Possibilities:Fast promotion track>");

		String semanticString3 = "<action:partial-agree:general> AND <action:append:general>";  // first one takes precedence (?)
		List<Action> actions3 = GrammarToGeniusBridge.semanticStringToGeniusAction(semanticString3, domain, agent, null, opponentBid, speakerBid);
		assertActions(actions3,  "<action:demand:Salary:7,000 NIS> and <action:demand:Job Description:Project Manager> and <action:demand:Promotion Possibilities:Fast promotion track>");
	}

	
	public void test() throws Exception {
		//testGeneral();
		//testPartialAccept();
		testInsistAndPartialAgree();
		//testAppend();
		//testSpaces();
		//testInsist();
		//testInsistAndPartialAgree();
		testAppendAndPartialAgree();
	}

	public static void main(String[] args) throws Exception {
		GrammarToGeniusBridgeTest.setUpBeforeClass();
		new GrammarToGeniusBridgeTest().test();
		GrammarToGeniusBridgeTest.tearDownAfterClass();
	}
}
