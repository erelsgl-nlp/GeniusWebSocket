package negotiator.exceptions;

import negotiator.issue.IssueDiscrete;

/**
 * Thrown when a user tries to use a value that is not one of the legal values for a given issue.
 * @author erelsgl
 * @since 2012-01-31
 */
@SuppressWarnings("serial")
public class UnknownValueException extends NegotiatorException {
	IssueDiscrete issue;
	String value;
    public UnknownValueException(IssueDiscrete theIssue, String theValue) { 
    	super("The value '"+theValue+"' is not valid for the issue '"+theIssue+"'");  
    	issue=theIssue; 
    	value=theValue; 
    }
}
