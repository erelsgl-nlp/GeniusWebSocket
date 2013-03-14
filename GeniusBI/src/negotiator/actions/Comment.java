/*
 * Comment.java
 *
 */

package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Bid;
/**
 * An Action used to send a textual comment to other parties in negotiation.
 * Opposed to Threat this class used to send positive or neutral coments.
 * The comment is chosen from a predefined set of possible comments 
 * @author Yinon Oshrat
 */

@XmlRootElement
public class Comment extends Action {
    @XmlElement
	protected int commentIndex;
  
	/**
	 * Contains the set of all possible comments
	 */
	public static final String[] possibleComments;
	static {
		possibleComments = new String[] {
				new String(
						"I feel that we are on the right track for reaching an agreement"),
				new String(
						"I don't believe that you are just in your intentions"),
				new String(
						"If you will try to compromise I will also try to go towards you") };
	}
	
	 /**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public Comment() { }
	/**
	 * Creates a new instance of Comment
	 * @param agent
	 *            - the agent id of the agent creating this comment
	 * @param commentIndex
	 *            - the index of the wanted comment from the possible comments
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public Comment(AgentID agent, int commentIndex) {
		super(agent);
		if (commentIndex < 0 || commentIndex >= possibleComments.length)
			throw new IllegalArgumentException(
					"Can't create comment with index " + commentIndex);
		this.commentIndex = commentIndex;
	}
	/**
	 * Creates a new instance of Comment, that will be send only to specific destination
	 * @param agent
	 *            - the agent id of the agent creating this comment
	 * @param commentIndex
	 *            - the index of the wanted comment from the possible comments
	 * @param destination - represent the destination for this action, null mean everybody else.
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public Comment(AgentID agent,AgentID destination, int commentIndex) {
		super(agent,destination);
		if (commentIndex < 0 || commentIndex >= possibleComments.length)
			throw new IllegalArgumentException(
					"Can't create comment with index " + commentIndex);
		this.commentIndex = commentIndex;
	}
	
	/**
	 * @return the comment this action contain as String
	 */
	@XmlElement(name="content")
	public String getContent() {
		return possibleComments[commentIndex];
	}
	
	/**
	 * @return the index of the comment this action contain from the array of all possible comments
	 */
	public int getIndex() {
		return commentIndex;
	}
	
	public String toString() {
		return "(Comment: " + getContent() + ")";
	}

	/**
	 * @return the number of possible comments
	 */
	public static int getNumberOfPossibleComments() {
		return possibleComments.length;
	}

	/**
	 * Get a specific possible comment as String
	 * @param index
	 *            - the index of the wanted comment
	 * @return the wanted comment as String
	 * @throws IllegalArgumentException
	 *             - if illegal index is given
	 */
	public static String getPossibleComment(int index) {
		if (index < 0 || index >= possibleComments.length)
			throw new IllegalArgumentException(
					"Can't return comment with index " + index);
		return possibleComments[index];
	}
    
	public Object clone() throws CloneNotSupportedException {
		Comment result = (Comment)super.clone();
		result.commentIndex = commentIndex;
		return result;
	}
}
