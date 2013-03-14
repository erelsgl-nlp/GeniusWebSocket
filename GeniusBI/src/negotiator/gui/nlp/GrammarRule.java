package negotiator.gui.nlp;

/**
 * @author Erel Segal
 * @since 18/12/2011
 */
public class GrammarRule {
	final String lhs, rhs;
	private String lhsInput, rhsInput, lhsOutput, rhsOutput;
	
	public GrammarRule(String newLhs, String newRhs) {
		lhs = newLhs;
		rhs = newRhs;
		
		lhsInput = GrammarRules.inputRegexp(lhs);
		rhsInput = GrammarRules.inputRegexp(rhs);
		lhsOutput = GrammarRules.outputRegexp(lhs);
		rhsOutput = GrammarRules.outputRegexp(rhs);
	}

	public String replaceRhsWithLhs(String original) {
		return original.replaceAll(rhsInput, lhsOutput);
	}

	public String replaceLhsWithRhs(String original) {
		return original.replaceAll(lhsInput, rhsOutput);
	}
	
	@Override public String toString() {
		return lhs+" => "+rhs;
	}
}
