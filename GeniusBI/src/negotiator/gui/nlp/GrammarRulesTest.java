package negotiator.gui.nlp;

import java.io.File;
import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarRulesTest {
	
	static GrammarRules rules = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rules = new GrammarRules(
				new File("etc/templates/JobCandiate/Side_B_NLP.txt"),
				new File("etc/templates/JobCandiate/Common_NLP.txt"));
		System.out.println("The rules: "+rules);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		rules = null;
	}

	public void testGenerate(String semantic, String expectedNatural) {
		String natural = rules.generate(semantic);
		System.out.println(semantic+" ==> "+natural);
		Assert.assertEquals(expectedNatural, natural);
	}

	public void testParse(String natural, String expectedSemantic) {
		/*
		String semantic = rules.parse(semantic);
		Assert.assertEquals(expectedSemantic, semantic);
		System.out.println(natural+" ==> "+semantic);
		*/
	}

	@Test public void testPartialAgree() {
		testGenerate("<action:partial-agree:Promotion Possibilities>", "I accept your promotion track offer,");
		testGenerate("<action:partial-agree:Salary>", "I accept your salary offer,");
		testGenerate("<action:partial-agree:Job Description>", "I accept your job description offer,");
		testGenerate("<action:partial-agree:Leased Car>", "I accept your leased car offer,");
		testGenerate("<action:partial-agree:Working Hours>", "I accept your working hours offer,");
		testGenerate("<action:partial-agree:Pension Fund>", "I accept your pension offer,");
	}
	
	@Test public void test() {
		testGenerate("<action:demand:Salary:7,000 NIS>", "I would like a salary of 7,000 NIS per month.");
		testGenerate("<action:agree:Leased Car:Without leased car>", "I can do without a company car.");
		testGenerate("<action:demand:Promotion Possibilities:Fast promotion track> And <action:agree:Pension Fund:10%>", "I want a Fast promotion track; and I can agree on 10% pension.");
		testGenerate("<action:reject:Leased Car> And <action:reject:Salary>", "I must have a car to get to work; and The salary you offer is too low.");
		testGenerate("<append> and <action:demand:Promotion Possibilities:Fast promotion track>", "I offer everything I offered before, and I want a Fast promotion track.");
	}

	
	public void testSelectBox(String lhs) {
		System.out.println("<select name='"+lhs+"'>\n"+rules.getMatchingRulesOptionsHtml(lhs)+"</select>");
	}
	
	@Test public void testSelectBox() {
		testSelectBox("<action:.*>");
		testSelectBox("<demand:.*>");
	}
	
	public void demo() {
		test();
		testPartialAgree();
		testSelectBox();
	}

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		new GrammarRulesTest().demo();
		tearDownAfterClass();
	}

}
