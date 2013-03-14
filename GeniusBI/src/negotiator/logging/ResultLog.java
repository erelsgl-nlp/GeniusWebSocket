package negotiator.logging;

import java.util.HashMap;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.events.NegotiationEndedEvent.AgreementType;

@XmlType(name = "ResultLogType", propOrder = {"finalAgreement","mUtilities","agreementType","mAdditional"})
@XmlRootElement
public class ResultLog extends LoggingBasis {

	@XmlJavaTypeAdapter(value=UtilitesAdapter.class)   
	@XmlElement(name = "utilities")
    private HashMap<String,Double> mUtilities=new HashMap<String, Double>();
	private Bid finalAgreement=null;
	private AgreementType agreementType=null;
	public ResultLog(){
		
	}
	
	/**
	 * @param finalAgreement
	 */
	public ResultLog(Bid finalAgreement) {
		this.finalAgreement = finalAgreement;
	}

	public void addUtility(AgentID agent, double utility){
		mUtilities.put(agent.toString(), utility);
	}
	
	public HashMap<String, Double> getUtilities(){
		return mUtilities;
	}

	public Bid getFinalAgreement() {
		return finalAgreement;
	}

	public void setFinalAgreement(Bid finalAgreement) {
		this.finalAgreement = finalAgreement;
	}

	public AgreementType getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(AgreementType agreementType) {
		this.agreementType = agreementType;
	}
	
	
	
}
