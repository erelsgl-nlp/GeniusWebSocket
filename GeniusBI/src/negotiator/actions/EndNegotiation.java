/*
 * BreakNegotiation.java
 *
 * Created on November 6, 2006, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator.actions;
import javax.xml.bind.annotation.XmlRootElement;


import negotiator.AgentID;
/**
 * An action that may be sent to opt-out from the negotiation.
 * Sending this action will end the negotiation with the predefined outcome that
 * depends on the protocol used 
 * @author Dmytro Tykhonov
 */
@XmlRootElement
public class EndNegotiation extends Action {
    
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public EndNegotiation() { }
	
    /** Creates a new instance of EndNegotiation */
    public EndNegotiation(AgentID agent) {
        super(agent);
    }
    public String toString() {
        return "(EndNegotiation)";
    }    
}
