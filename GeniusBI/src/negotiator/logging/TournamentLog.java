package negotiator.logging;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

import negotiator.repository.*;

@XmlRootElement
public class TournamentLog extends LoggingBasis {

	
	private int mTournamentNumber;
	
	@XmlElementWrapper(name = "Negotiations")
	@XmlElement(name="negotiationLog")
	private ArrayList<NegotiationLog> mNegotiation = new ArrayList<NegotiationLog>();
	
	private SetupRepItem mSetup = new SetupRepItem();
	
	public TournamentLog(){
		super();
	}
	
	public TournamentLog(int tNumber){
		super();
		mTournamentNumber=tNumber;
	}
	@XmlElement(name="TournamentSetup")
	public SetupRepItem getTournamentSetup() {
		return mSetup;
	}
	public void setTournamentSetup(SetupRepItem tSetup) {
		mSetup = tSetup;
	}
	@XmlAttribute(name="Number")
	public int getNumber() {
		return mTournamentNumber;
	}
	public void setNumber(int tNumber) {
		mTournamentNumber = tNumber;
	}
	
	public int getNegotiationIndex(NegotiationLog tNegotiation){
		return mNegotiation.indexOf(tNegotiation);
	}

	public ArrayList<NegotiationLog> getNegotiation(){
		return mNegotiation;
	}
	
	public NegotiationLog getNegotiation(int tIndex){
		return mNegotiation.get(tIndex);
	}
	
	public void setNegotiation(int tIndex, NegotiationLog tNegotiation){
		mNegotiation.set(tIndex, tNegotiation);
	}
	
	public void addNegotiation(NegotiationLog tNegotiation){
		mNegotiation.add(tNegotiation);
	}
	
	public void delNegotiation(NegotiationLog tNegotiation){
		mNegotiation.remove(tNegotiation);
	}
	
	public void delNegotiation(int tIndex){
		mNegotiation.remove(tIndex);
	}
	
	public int getNumberOfNegotiations(){
		return mNegotiation.size();
	}
}
