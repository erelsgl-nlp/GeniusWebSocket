package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;

/**
 * Represents a text message.
 * 
 * @author Yoshi
 *
 */
@XmlRootElement
public class TextMessage extends Action {

	@XmlElement
	String message;
	
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public TextMessage() {
	}

	/**
	 * Creates a new instance of TextMessage.
     * @param agent - the agent creating this action
     * @param message - the text message
     */
	public TextMessage(AgentID agent, String message) {
		super(agent);
		this.message = message;
	}

    /**
	 * Creates a new instance of TextMessage.
     * @param agent - the agent creating this action
     * @param bid - the bid this action send to other parties 
     * @param message - the text message
     */
	public TextMessage(AgentID agent, AgentID destination, String message) {
		super(agent, destination);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override public String toString() {
        return "("+this.agent+" says: " + message + ")";
	}

	@Override public Object clone() throws CloneNotSupportedException {
		TextMessage result = (TextMessage)super.clone();
		result.message = message;
		return result;
	}
}
