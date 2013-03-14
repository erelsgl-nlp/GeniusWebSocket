package negotiator.logging;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.*;

import negotiator.AgentID;

@XmlType(name = "NegLogType", propOrder = {"logonTime", "number","mAgents", "mSessions", "result","mAdditional"})
@XmlRootElement
public class NegotiationLog extends LoggingBasis {

	
	private int mNegotiationNumber;
	
	@XmlElementWrapper(name = "Agents")
	@XmlElement(name="Agent")
	private ArrayList<AgentLog> mAgents = new ArrayList<AgentLog>();
	
	@XmlElementWrapper(name = "Sessions")
	@XmlElement(name="Session")
	private ArrayList<SessionLog> mSessions = new ArrayList<SessionLog>();

	private Date logonTime;
	
	private ResultLog mResult;

	
	public NegotiationLog(){
		mNegotiationNumber = -1;
		mResult = new ResultLog();
	}
	
	public NegotiationLog(int tNumber){
		mNegotiationNumber = tNumber;
		mResult = new ResultLog();
	}
	
	public NegotiationLog(int tNumber, ArrayList<AgentLog> tAgents, ArrayList<SessionLog> tSessions, ResultLog tResult){
		mNegotiationNumber = tNumber;
		mAgents = tAgents;
		mSessions = tSessions;
		mResult = tResult;
	}
	
	public int getSessionIndex(SessionLog tSession){
		return mSessions.indexOf(tSession);
	}
	
	public ArrayList<SessionLog> getSessions(){
		return mSessions;
	}
	
	public SessionLog getSession(int tIndex){
		return mSessions.get(tIndex);
	}
	
	public void setSession(int tIndex, SessionLog tSession){
		mSessions.set(tIndex, tSession);
	}
	
	public void addSession(SessionLog tSession){
		mSessions.add(tSession);
	}
	
	public void delSession(SessionLog tSession){
		mSessions.remove(tSession);
	}
	
	public void delSession(int tIndex){
		mSessions.remove(tIndex);
	}
	
	public void delAllSessionsRounds(){
		for (SessionLog ses : mSessions) {
			ses.delAllRounds();
		}
	}	
	
	public int getNumberOfSessions(){
		return mSessions.size();
	}

	@XmlAttribute(name="Number")
	public int getNumber(){
		return mNegotiationNumber;
	}
	
	public void setNumber(int tNumber){
		mNegotiationNumber=tNumber;
	}
	
	public AgentLog getAgentById(AgentID tAgent){
		
		for (AgentLog agnt : mAgents) {
			if (agnt.getAgentID().equals(tAgent)) {
				return agnt;
			}
		}
		return null;
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
	
	public void delAgent(AgentLog tAgent){
		mAgents.remove(tAgent);
	}
	
	public void delAgent(int tIndex){
		mAgents.remove(tIndex);
	}
	
	public int getNumberOfAgents(){
		return mAgents.size();
	}

	@XmlElement(name="Result")
	public ResultLog getResult(){
		return mResult;
	}
	
	public void setResult(ResultLog tResult){
		mResult = tResult;
	}

	@XmlElement(name="logonTime")
	public Date getLogonTime() {
		return logonTime;
	}

	public void setLogonTime(Date logonTime) {
		this.logonTime = logonTime;
	}
	
}
