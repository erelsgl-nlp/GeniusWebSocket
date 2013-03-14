package negotiator.exceptions;

/**
 * 
 * @author dmytro
 * if you want to make bid with the wrong type, 
 * eg if you give a discrete evaluation value to something of type real.
 */
public class ValueTypeError extends NegotiatorException {

	// Constructor
    public ValueTypeError(String msg) {
    	super(msg);
    }
    
}
