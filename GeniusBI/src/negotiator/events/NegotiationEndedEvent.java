/**
 * 
 */
package negotiator.events;

import negotiator.AgentID;
import negotiator.Bid;

/**
 * @author User
 *
 */
public class NegotiationEndedEvent extends NegotiationEvent {
	
	public enum AgreementType {Full,Partial,StatusQuo,Optout,Error};
	
	private long negotiationMilliseconds;
	private double utilityA;
	private double utilityB;
	private AgentID lastActor;
	private AgreementType typeOfAgreement;
	private Bid finalAgreement;
	private String remarks;
	
	public NegotiationEndedEvent(Object source,long negotiationMilliseconds,
			double utilityA,double utilityB,AgentID lastActor,AgreementType typeOfAgreement,Bid finalAgreement,String remarks) {
		super(source);
		this.negotiationMilliseconds = negotiationMilliseconds;
		this.utilityA = utilityA;
		this.utilityB = utilityB;
		this.lastActor = lastActor;
		this.typeOfAgreement = typeOfAgreement;
		this.finalAgreement = finalAgreement;
		this.remarks = remarks;
	}

	public long getNegotiationMilliseconds() {
		return negotiationMilliseconds;
	}

	public double getUtilityA() {
		return utilityA;
	}

	public double getUtilityB() {
		return utilityB;
	}

	public AgentID getLastActor() {
		return lastActor;
	}

	public AgreementType getTypeOfAgreement() {
		return typeOfAgreement;
	}

	public Bid getFinalAgreement() {
		return finalAgreement;
	}

	public String getRemarks() {
		return remarks;
	}
	
	
}
