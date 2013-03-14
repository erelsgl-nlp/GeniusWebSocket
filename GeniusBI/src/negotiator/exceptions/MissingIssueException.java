package negotiator.exceptions;

import negotiator.issue.IssueDiscrete;

/**
 * Thrown when a user tries to use a value that is not one of the legal values for a given issue.
 * @author erelsgl
 * @since 2012-01-31
 */
@SuppressWarnings("serial")
public class MissingIssueException extends NegotiatorException {
	String issue;
    public MissingIssueException(int theIssueNumber) { 
    	super("Issue #"+theIssueNumber+" is not mentioned in this bid");  
    	issue=String.valueOf(theIssueNumber); 
    }
    public MissingIssueException(String theIssue) { 
    	super(theIssue+" was not mentioned in the opponent's bid");  
    	issue=theIssue; 
    }
}
