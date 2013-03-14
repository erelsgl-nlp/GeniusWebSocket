/*
 * NegotiatorException.java
 *
 * Created on November 17, 2006, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.exceptions;

/**
 *
 * @author dmytro
 * This is a generic class of nogotiation errors.
 */
public class NegotiatorException extends Exception{
	public NegotiatorException() { super(); }
    public NegotiatorException(String message) { super(message);  }
	public NegotiatorException(Throwable cause) { super(cause); }
    public NegotiatorException(String message, Throwable cause) { super(message, cause);  }
}
