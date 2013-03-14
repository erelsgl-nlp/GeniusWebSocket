package negotiator.repository;

import negotiator.AgentParamsMapAdapter;
import negotiator.NegotiationEventListener;
import negotiator.exceptions.Warning;
import negotiator.protocol.Protocol;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This repository item contains all info about a Tournament Setup that can be loaded.

 * 
 * @author Marc
 *
 */
@XmlRootElement
public class SetupRepItem implements RepItem
{
	@XmlAttribute
	String TournamentName; /**  the key: short but unique name of the agent as it will be known in the nego system.
	 						* This is an arbitrary but unique label for this TYPE of agent.
	 						* Note that there may still be multiple actual agents of this type during a negotiation. */
	@XmlAttribute
	String classPath; /** file path including the class name */
	@XmlAttribute
	String description; /** description of this agent */
	
	@XmlAttribute
	String[] agentNames; /** Names of the Agents in use**/
	@XmlElement(name="agents")
	AgentRepItem[] agentRepItems;
	@XmlElement(name="profiles")
	ProfileRepItem[] profileRepItems; 
	@XmlJavaTypeAdapter(AgentParamsMapAdapter.class)
	HashMap<AgentParameterVariable,AgentParamValue>[]  agentParams;
	@XmlElement(name="domain")
	DomainRepItem domain;
	
	UtilitySpace[] agentUtilitySpaces;
	//the listerner are not part of the XML specification and should be intialized after load of the setup
	ArrayList<NegotiationEventListener> actionEventListener;
	
	public SetupRepItem(){
	}
	
	/**
	 * @returns true if agentName and classPath equal. Note that agentName alone is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if(!(o instanceof SetupRepItem)){
			return false;
		}
		return  TournamentName.equals(((SetupRepItem)o).TournamentName) && classPath.equals(((SetupRepItem)o).classPath);
	}
	
	public SetupRepItem(String tName, String tPath, String desc, String[] tAgentNames, AgentRepItem[] tAgentRep, ProfileRepItem[] tProfileRep, HashMap<AgentParameterVariable,AgentParamValue>[] tAgentParam, DomainRepItem tDomain, UtilitySpace[] tSpace, ArrayList<NegotiationEventListener> tActionEvents ) {
		TournamentName=tName; 
		classPath=tPath;
		description=desc;
		agentNames = tAgentNames;
		agentRepItems = tAgentRep;
		profileRepItems = tProfileRep;
		agentParams = tAgentParam;
		domain = tDomain;
		agentUtilitySpaces = tSpace;
		actionEventListener = tActionEvents;
	}
/*	
	public SetupRepItem(String tName, String tPath, String desc, Protocol tSource) {
		TournamentName=tName; 
		classPath=tPath;
		description=desc;
		agentNames = tSource.getAgentName();
		agentRepItems = tSource.getAgentRepItem();
		profileRepItems = tSource.getProfileRepItems();
		agentParams = tSource.getAgentParams();
		domain = tSource.getProfileRepItems(0).getDomain();
		agentUtilitySpaces = tSource.getAgentUtilitySpaces();
		actionEventListener = tSource.getNegotiationEventListeners();
	}
*/	
	public SetupRepItem(String aName, String cPath, String desc) {
		TournamentName=aName; 
		classPath=cPath;
		description=desc;
	}
	
	public String getTournamentName(){
		return TournamentName;
	}	
	public String getName(){
		return TournamentName;
	}
	
	public String getClassPath(){
		return classPath;
	}
	
	/** getVersion is bit involved, need to call the agent getVersion() to get it */
	@SuppressWarnings("unused")
	private static final Class[] parameters = new Class[]{URL.class};
	public String getVersion() { 
	       try{
	    	   /*
	    	   // following code somewhere from the net, see ClassPathHacker 
	   		URLClassLoader sysloader=(URLClassLoader)ClassLoader.getSystemClassLoader();
	   		Class sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			URL urloffile=new File(classPath).toURL();
			method.invoke(sysloader,new Object[]{ urloffile }); // load the new class.
			*/
			return ""+callStaticAgentFunction( "getVersion",new Object[0]);
			//Class agentClass=classLoader.loadClass(classPath);
	        //  return ""+agentClass.getMethod("getVersion").invoke(null, new Object[0]);
			
	       } catch(Exception e){
	           new Warning("can't get version for "+TournamentName+" :",e); //e.printStackTrace();
	       }  		
	       return "ERR";
		}
	
	/** callAgentFunction can call a Static agent function without instantiating the agent. 
	 * This is used to get the version and parameters from the agent class in general.
	 * @return the object returned by that function
	 * @throws any exception that the function can throw, or failures
	 * by not finding the class, failure to load the description, etc.
	 * @param methodname contains the name of the method, eg "getVersion"
	 * @param params contains an array of parameters to the call, eg Object[0] for no parameters.
	 */
	@SuppressWarnings("unchecked")
	public Object callStaticAgentFunction(String methodname, Object[] params) throws Exception {
		Class c=Class.forName(classPath);
		return c.getMethod(methodname).invoke(null, params);
	}
	
	public String getDescription() { return description; }
	
	public String toString() {
		String results = "SetupRepositoryItem["+TournamentName+","+classPath+","+description;
		for(String a : agentNames){
			results+=", ";
			results+=a;
		}
		for(AgentRepItem b : agentRepItems){
			results+=", ";
			results+=b.toString();
		}
		for(ProfileRepItem c : profileRepItems){
			results+=", ";
			results+=c.toString();
		}
		results+=", ";
		results+=agentParams.toString();
		results+=", ";
		results+=domain.toString();
		results+=", ";
		results+=agentUtilitySpaces.toString();
		for(NegotiationEventListener f : actionEventListener){
			results+=", ";
			results+=f.toString();
		}
		results+="]";
		return results;
	}
}
/*
ArrayList<NegotiationEventListener> actionEventListener;*/
