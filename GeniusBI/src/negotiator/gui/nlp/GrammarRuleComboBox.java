package negotiator.gui.nlp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

/**
 * A combo-box for selecting the right-hand-side of grammar rules.
 * Depends upon the rules of the containing class.
 * @author Erel Segal
 * @since 18/12/2011
 */
@SuppressWarnings("serial")
class GrammarRuleComboBox extends JComboBox {
	private final GrammarPanel grammarPanel;
	private final GrammarRules rules;
	String lhsPattern;
	List<GrammarRule> matchingRules;
	int indexInVerticalBox, indexInPatternList;
	
	GrammarRuleComboBox(GrammarPanel grammarPanel, String theLhsPattern, final int theIndexInVerticalBox, final int theIndexInPatternList) {
		super();
		this.grammarPanel = grammarPanel;
		this.rules = grammarPanel.rules;
		indexInVerticalBox = theIndexInVerticalBox;
		indexInPatternList = theIndexInPatternList;
		lhsPattern = theLhsPattern;
		System.out.println("new GrammarRuleComboBox("+theLhsPattern+","+indexInVerticalBox+"). ");

		this.matchingRules = this.rules.getMatchingRules(GrammarRules.inputRegexp(theLhsPattern));

		/// @see GrammarRules#getMatchingRulesSelectBoxHtml
		this.addItem(" -- select --");
		for (GrammarRule rule: matchingRules)
			this.addItem(GrammarRules.outputRegexp(rule.rhs));

        setEditable(true);
        setBorder(BorderFactory.createLineBorder(Color.black));
        addActionListener(this);
	}

	@Override public void actionPerformed(ActionEvent event) {
		GrammarPanel.removeAllComponentsAfter(this.grammarPanel.ruleSelectionBox, indexInVerticalBox);
		GrammarPanel.removeAllItemsAfter(this.grammarPanel.patterns, indexInPatternList);
		GrammarRule selectedRule = getSelectedRule();
		if (selectedRule==null) return;
		List<String> nontermnials = GrammarRules.nonterminalsInPattern(selectedRule.rhs);
		//System.out.println("nonterminals: "+nontermnials);
		if (!nontermnials.isEmpty())
			for (String nonterminal: nontermnials)
				this.grammarPanel.addComboBoxForSelectingRhs(nonterminal);
		else
			this.grammarPanel.addAndButton();
		this.grammarPanel.setGeneratedSentence();
		this.grammarPanel.ruleSelectionBox.revalidate();
	}
	
	@Override public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	
	GrammarRule getSelectedRule() {
		int selectedIndex = getSelectedIndex();
		return selectedIndex>0? matchingRules.get(selectedIndex-1): null;
	}
}