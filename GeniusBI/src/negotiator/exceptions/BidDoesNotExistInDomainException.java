/*
 * BidDoesNotExistInDomainException.java
 *
 * Created on November 17, 2006, 4:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.exceptions;

/**
 *
 * @author dmytro
 * 
 * This is thrown if an attempt is made to create a bid with illegal values.
 */
public class BidDoesNotExistInDomainException  extends NegotiatorException
{
    
    /** Creates a new instance of BidDoesNotExistInDomainException */
    public BidDoesNotExistInDomainException(String msg) 
    {
    	super(msg);
    }
     
}
