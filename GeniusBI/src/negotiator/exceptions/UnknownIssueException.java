package negotiator.exceptions;

/**
 * Thrown when a user tries to use an issue that does not exist in the domain.
 * @author erelsgl
 * @since 2012-01-31
 */
@SuppressWarnings("serial")
public class UnknownIssueException extends NegotiatorException {
	String issue;
    public UnknownIssueException(String theIssue) { 
    	super("The issue '"+theIssue+"' does not exist in the domain");  
    	issue=theIssue; 
    }
}
