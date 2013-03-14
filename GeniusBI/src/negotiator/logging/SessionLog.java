package negotiator.logging;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.*;

import negotiator.AgentID;

@XmlType(name = "SessionLogType", propOrder = {"mAgents", "mRounds", "results","mAdditional"})
@XmlRootElement
public class SessionLog extends LoggingBasis {

	
	private int mSessionNumber;
	@XmlElementWrapper(name = "Agents")
	@XmlElement(name="Agent")
	private ArrayList<AgentLog> mAgents = new ArrayList<AgentLog>();
	@XmlElementWrapper(name = "Rounds")
	@XmlElement(name="Rounds")
	private ArrayList<RoundLog> mRounds = new ArrayList<RoundLog>();
	
	private Date mStart = null;
	private Date mEnd = null;
	
	private ResultLog mResult;
	
	public SessionLog(){
		mSessionNumber = -1;
		mResult = new ResultLog();
	}
	
	public SessionLog(int tNumber){
		mSessionNumber = tNumber;
		mResult = new ResultLog();
	}
	
	public SessionLog(int tNumber, ArrayList<AgentLog> tAgents, ArrayList<RoundLog> tRounds, ResultLog tResult){
		mSessionNumber = tNumber;
		mAgents = tAgents;
		mRounds = tRounds;
		mResult = tResult;
	}
	@XmlElement(name="Result")
	public ResultLog getResults(){
		return mResult;
	}
	
	public void setResults(ResultLog tResult){
		mResult = tResult;
	}
	
	public int getRoundIndex(RoundLog tRound){
		return mRounds.indexOf(tRound);
	}
	
	public ArrayList<RoundLog> getRounds(){
		return mRounds;
	}
	
	public RoundLog getRound(int tIndex){
		return mRounds.get(tIndex);
	}
	
	public void setRound(int tIndex, RoundLog tRound){
		mRounds.set(tIndex, tRound);
	}
	
	public void addRound(RoundLog tRound){
		mRounds.add(tRound);
	}
	
	public void delRound(RoundLog tRound){
		mRounds.remove(tRound);
	}
	
	public void delRound(int tIndex){
		mRounds.remove(tIndex);
	}
	
	public void delAllRounds(){
		mRounds.clear();
	}

	public int getNumberOfRounds(){
		return mRounds.size();
	}
	@XmlAttribute(name="Number")
	public int getNumber(){
		return mSessionNumber;
	}
	
	public void setSessionNumber(int tNumber){
		mSessionNumber=tNumber;
	}
	
	public int getAgentIndex(AgentID tAgent){
		return mAgents.indexOf(tAgent);
	}
	
	public ArrayList<AgentLog> getAgents(){
		return mAgents;
	}
	
	public AgentLog getAgent(int tIndex){
		return mAgents.get(tIndex);
	}
	
	public void setAgent(int tIndex, AgentLog tAgent){
		mAgents.set(tIndex, tAgent);
	}
	
	public void addAgent(AgentLog tAgent){
		mAgents.add(tAgent);
	}
	
	public void delAgent(AgentID tAgent){
		mAgents.remove(tAgent);
	}
	
	public void delAgent(int tIndex){
		mAgents.remove(tIndex);
	}
		
	public int getNumberOfAgents(){
		return mAgents.size();
	}
	
	public void setStart(Date time) {
		mStart = time;
	}

	@XmlAttribute(name = "Start")
	public Date getStart() {
		return mStart;
	}

	public void setEnd(Date time) {
		mEnd = time;
	}

	@XmlAttribute(name = "End")
	public Date getEnd() {
		return mEnd;
	}
}
