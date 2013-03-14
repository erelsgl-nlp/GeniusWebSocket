/**
 * 
 */
package negotiator.repository;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.exceptions.Warning;

/**
 * This repository item contains all info about a mediator that can be loaded.

 * 
 * @author Yinon Oshrat
 */
@XmlRootElement
public class MediatorRepItem implements RepItem
{
	@XmlAttribute
	String mediatorName; /**  the key: short but unique name of the mediator as it will be known in the nego system.
	 						* This is an arbitrary but unique label for this TYPE of agent.
	 						* Note that there may still be multiple actual agents of this type during a negotiation. */
	@XmlAttribute
	String classPath; /** file path including the class name */
	@XmlAttribute
	String description; /** description of this agent */
	@XmlAttribute
	boolean ncActivated;
	@XmlAttribute
	int ncTurns;
	@XmlAttribute
	int ncReject;
	@XmlAttribute
	int ncOffer;
	@XmlAttribute
	double ncRank;

	public MediatorRepItem(){
	}
	
	/**
	 * @returns true if agentName and classPath equal. Note that agentName alone is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof MediatorRepItem)) return false;
		return mediatorName.equals( ((MediatorRepItem)o).mediatorName) && classPath.equals( ((MediatorRepItem)o).classPath);
	}
	
	public MediatorRepItem(String aName, String cPath, String desc, boolean ncActivated, int ncTurns, int ncReject, int ncOffer, double ncRank) {
		mediatorName=aName; 
		classPath=cPath;
		description=desc;
		this.ncActivated = ncActivated;
		this.ncTurns = ncTurns;
		this.ncReject = ncReject;
		this.ncOffer = ncOffer;
		this.ncRank = ncRank;
	}
	
	public boolean getNC_ACTIVATED() {
		return ncActivated;
	}
	
	public int getNC_TURNS() {
		return ncTurns;
	}

	public int getNC_REJECT() {
		return ncReject;
	}

	public int getNC_OFFER() {
		return ncOffer;
	}

	public double getNC_RANK() {
		return ncRank;
	}

	public String getName() { return mediatorName; }
	
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
	           new Warning("can't get version for "+mediatorName+" :",e); //e.printStackTrace();
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
	
	public String toString() { return "AgentRepositoryItem["+mediatorName+","+classPath+","+description
															+","+ncActivated+","+ncTurns+","+ncReject+","+ncOffer+","+ncRank+"]"; }
}