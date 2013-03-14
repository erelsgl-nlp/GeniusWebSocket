package negotiator.logging;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.*;

@XmlRootElement
public class AgentLog extends LoggingBasis {
	
	
	private AgentID mAgent;
	private String mAgentClass;
	private String mSide;
	private String mProfile;
	private String mUserId;
	private String mRole;
	
	public AgentLog(){
		
	}
	
	public AgentLog(ActionReceiver tAgent){
		if (tAgent!=null) {
			mAgent = tAgent.getAgentID();
			mAgentClass = tAgent.getClass().getName();
			mSide = tAgent.getName();
			mUserId = tAgent.getUserID();
			
			if (tAgent instanceof Agent && ((Agent)tAgent).utilitySpace!=null)
				mProfile = ((Agent)tAgent).utilitySpace.getFileName();
		}
	}
	
	@XmlJavaTypeAdapter(value=AgentIDAdapter.class)
	@XmlAttribute(name="AgentID")
	public AgentID getAgentID() {
		return mAgent;
	}

	public void setAgentID(AgentID agent) {
		mAgent = agent;
	}
	
	@XmlAttribute(name="AgentRole")
	public String getAgentRole() {
		return mRole;
	}

	public void setAgentRole(String role) {
		mRole = role;
	}
	
	
	@XmlAttribute(name="class")
	public String getAgentClass() {
		return mAgentClass;
	}

	public void setAgentClass(String agentClass) {
		mAgentClass = agentClass;
	}
	
	@XmlAttribute
	public String getSide() {
		return mSide;
	}

	public void setSide(String side) {
		mSide = side;
	}

	@XmlAttribute
	public String getProfile() {
		return mProfile;
	}

	public void setProfile(String profile) {
		mProfile = profile;
	}

	@XmlAttribute(name="UserID")
	public String getmUserId() {
		return mUserId;
	}

	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}
	
}

class AgentIDAdapter extends XmlAdapter<String,AgentID> {

	@Override
	public String marshal(AgentID arg0) throws Exception {
		return arg0.toString();
	}

	@Override
	public AgentID unmarshal(String arg0) throws Exception {
		return new AgentID(arg0);
	}
}