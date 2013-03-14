package negotiator.repository;

import negotiator.exceptions.Warning;
import java.net.URL;
import javax.xml.bind.annotation.*;

/**
 * This repository item contains all info about an agent that can be loaded.

 * 
 * @author wouter
 *
 */
@XmlRootElement
public class AgentRepItem implements RepItem
{
	@XmlAttribute
	String agentName; /**  the key: short but unique name of the agent as it will be known in the nego system.
	 						* This is an arbitrary but unique label for this TYPE of agent.
	 						* Note that there may still be multiple actual agents of this type during a negotiation. */
	@XmlAttribute
	String classPath; /** file path including the class name */
	@XmlAttribute
	String description; /** description of this agent */

	@XmlTransient
	String localeString = null;
	
	public AgentRepItem(){
	}
	
	/**
	 * @returns true if agentName and classPath equal. Note that agentName alone is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof AgentRepItem)) return false;
		return agentName.equals( ((AgentRepItem)o).agentName) && classPath.equals( ((AgentRepItem)o).classPath);
	}
	
	public AgentRepItem(String aName, String cPath, String desc) {
		agentName=aName; 
		classPath=cPath;
		description=desc;
	}
	
	public String getName() { return agentName; }
	
	public String getClassPath() { return classPath; }
	
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
	           new Warning("can't get version for "+agentName+" :",e); //e.printStackTrace();
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
	public Object callStaticAgentFunction(String methodname, Object[] params) throws Exception {
		Class c=Class.forName(classPath);
		return c.getMethod(methodname).invoke(null, params);
	}
	
	public String getDescription() { return description; }
	
	/**
	 * @param locale the locale to set
	 */
	public void setLocaleString(String localeString) {
		this.localeString = localeString;
	}

	/**
	 * @return the locale
	 */
	public String getLocaleString() {
		return localeString;
	}

	public String toString() { return "AgentRepositoryItem["+agentName+","+classPath+","+description+"]"; }
}