package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Bid;
import negotiator.events.NegotiationEndedEvent.AgreementType;

@XmlRootElement
/**
 * Notifies the agents of a change in the negotiation status: the turn, the phase, or the
 * agreement reached so far.
 * NOTE: currently the EndTurn action is used to notify of turn ends, and not UpdateStatusAction.
 * 
 * @author Yoshi
 *
 */
public class UpdateStatusAction extends Action {

	public enum Phase { Negotiation, UpgradeAgreement, Ended, ContinueNegotiation };

	@XmlElement
	private int turn;

	@XmlElement
	private Phase phase = null;

	@XmlElement
	private Bid currentAgreement = null;

	@XmlElement
	private AgreementType agreementType = null;

	private Double yourUtility = null;

	public UpdateStatusAction() {}
	
    public UpdateStatusAction(int turn, Phase _phase, Bid _currentAgreement, AgreementType _agreementType) {
        super(null);
        this.turn = turn;
        this.phase = _phase;
        this.agreementType = _agreementType;
        if (_currentAgreement != null)
        	this.currentAgreement = _currentAgreement.clone();
    }

    public int getTurn() {
    	return turn;
    }

    public Phase getPhase() {
    	return phase;
    }

    public Bid getCurrentAgreement() {
    	return currentAgreement;
    }
    
    public AgreementType getAgreementType() {
		return agreementType;
	}

    public void setYourUtility(Double yourUtility) {
		this.yourUtility = yourUtility;
	}
    
	@XmlElement
	public Double getYourUtility() {
		return yourUtility;
	}
    
    @Override
    public String toString() {
        return "(UpdateStatusAction: turn=" + turn + ", phase=" + phase +
        	", currentAgreement=" + currentAgreement + 
        	", agreementType=" + agreementType + 
        	", yourUtility=" + yourUtility + ")";
    }    

	public Object clone() throws CloneNotSupportedException {
		UpdateStatusAction result = (UpdateStatusAction)super.clone();
		result.turn = turn;
		result.phase = phase;
		result.currentAgreement = currentAgreement.clone();
		result.agreementType = agreementType;
		result.yourUtility = yourUtility;
		return result;
	}
}
