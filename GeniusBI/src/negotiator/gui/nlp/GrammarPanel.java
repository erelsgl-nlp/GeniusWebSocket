package negotiator.gui.nlp;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.*;


@SuppressWarnings("serial")
public class GrammarPanel extends JPanel {

    public GrammarPanel(GrammarRules theRules) {
    	this(theRules, theRules.getRoot());
    }

    public GrammarPanel(GrammarRules theRules, final String theRootPattern) {
    	super(new BorderLayout());
        rules = theRules;
        rootPattern = theRootPattern;

        add(ruleSelectionBoxScrollPane = new JScrollPane(
        		ruleSelectionBox = new Box(BoxLayout.PAGE_AXIS), 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), 
        		BorderLayout.NORTH);
        ruleSelectionBoxScrollPane.setPreferredSize(new Dimension(500, 300));

        Box sentenceDescriptionBox; 
        add(sentenceDescriptionScrollPane = new JScrollPane(
        		sentenceDescriptionBox = new Box(BoxLayout.PAGE_AXIS),
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
        		BorderLayout.SOUTH);
        sentenceDescriptionBox.add(sentenceLabel = new JLabel());
        sentenceDescriptionBox.add(Box.createRigidArea(new Dimension(5,5)));
        sentenceDescriptionBox.add(semanticLabel = new JLabel());
        sentenceDescriptionBox.add(Box.createRigidArea(new Dimension(5,5)));
        sentenceDescriptionBox.add(new ClearButton());

        addComboBoxForSelectingRhs(theRootPattern);
        setPreferredSize(new Dimension(600,400));
    }

    public String getGeneratedSentence() {
    	return sentenceLabel.getText();
    }

    public String getGeneratedAction() {
    	return semanticLabel.getText();
    }

    
    
    
    
    /*
     * PROTECTED ZONE
     */
    
    protected GrammarRules rules;

    protected JPanel pane;
    protected Box ruleSelectionBox;
    protected JScrollPane ruleSelectionBoxScrollPane, sentenceDescriptionScrollPane;
    protected String sentence, semantic;
    protected Action action;
    protected JLabel sentenceLabel, semanticLabel, actionLabel;

    protected String rootPattern;
    protected List<GrammarRuleComboBox> patterns = new ArrayList<GrammarRuleComboBox>();
	
    
    /**
     * Add to the "ruleSelectionBox" and to "patterns" a new GrammarRuleComboBox, for selecting one of the possible right-hand-sides of a given left-hand-side. 
     * @param lhsPattern a pattern for the left-hand-side of the rule.
     */
	protected void addComboBoxForSelectingRhs(String lhsPattern) {
		ruleSelectionBox.add(Box.createRigidArea(new Dimension(10,10)));
		GrammarRuleComboBox newComboBox = new GrammarRuleComboBox(this, lhsPattern, ruleSelectionBox.getComponentCount()+1, patterns.size()+1);
        ruleSelectionBox.add(newComboBox);
        patterns.add(newComboBox);
        ruleSelectionBox.revalidate();
	}

	protected void addAndButton() {
		ruleSelectionBox.add(Box.createRigidArea(new Dimension(10,10)));
        ruleSelectionBox.add(new AndButton(ruleSelectionBox.getComponentCount()+1));
        ruleSelectionBox.revalidate();
	}

	protected void setGeneratedSentence() {
		sentence = "";
		for (GrammarRuleComboBox pattern: patterns) {
			GrammarRule rule = pattern.getSelectedRule();
			if (rule==null) continue;
			if (pattern.lhsPattern.equals(rootPattern)) { // ADD the root pattern RHS
				if (!sentence.isEmpty())
					sentence += " AND ";
				sentence += GrammarRules.outputRegexp(rule.rhs);
			}
			else {                                       // REPLACE the pattern with the RHS
				sentence = sentence.replace(GrammarRules.outputRegexp(pattern.lhsPattern), GrammarRules.outputRegexp(rule.rhs)); // a string-replace, NOT a regexp replace
				//generatedSentence = rule.replaceLhsWithRhs(generatedSentence);
			}
		}
		sentenceLabel.setText(sentence);

		semantic = sentence;
		for (ListIterator<GrammarRuleComboBox> iter = patterns.listIterator(patterns.size()); iter.hasPrevious();)  {
			GrammarRule rule = iter.previous().getSelectedRule();
			//System.out.println(semantic+": "+rule);
			if (rule==null) continue;
			semantic = rule.replaceRhsWithLhs(semantic);
		}
		semanticLabel.setText(semantic);
	}
	
	/**
	 * A button for adding "and" clauses.
	 * @author Erel Segal
	 * @since 18/12/2011
	 */
	class AndButton extends JButton implements ActionListener {
		AndButton(final int theIndexInVerticalBox) {
			super("AND");
			indexInVerticalBox = theIndexInVerticalBox;
	        addActionListener(this);
		}

		@Override public void actionPerformed(ActionEvent event) {
			setEnabled(false);
			addComboBoxForSelectingRhs(rootPattern);
			ruleSelectionBox.revalidate();
		}

		int indexInVerticalBox;
	}
    
	
	/**
	 * A button for clearing everything.
	 * @author Erel Segal
	 * @since 18/12/2011
	 */
	class ClearButton extends JButton implements ActionListener {
		ClearButton() {
			super("CLEAR");
	        addActionListener(this);
		}

		@Override public void actionPerformed(ActionEvent event) {
			patterns.clear();
			ruleSelectionBox.removeAll();
			addComboBoxForSelectingRhs(rootPattern);
			ruleSelectionBox.revalidate();
		}
	}

	protected static void removeAllComponentsAfter(JComponent verticalBox, int firstIndex) {
		for (; verticalBox.getComponentCount()>firstIndex; )
			verticalBox.remove(verticalBox.getComponentCount()-1);
	}

	protected static <T> void removeAllItemsAfter(List<T> list, int firstIndex) {
		for (; list.size()>firstIndex; )
			list.remove(list.size()-1);
	}
 
    
    
    
    
    /**
     * demo program
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	JDialog testDialog = new JDialog((Frame)null, true);
    	testDialog.setTitle("Grammar Dialog");
    	testDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	testDialog.add(new GrammarPanel(
        		new GrammarRules(
        			new File("etc/templates/JobCandiate/Side_A_NLP.txt"),
        			new File("etc/templates/JobCandiate/Common_NLP.txt"))));
    	testDialog.pack();
    	testDialog.setVisible(true);
    }
}
