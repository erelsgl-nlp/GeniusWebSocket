package negotiator;
/**
 * This class stores info about a parameter of an agent.
 * You can get and set parameters using Agent.getParameter and agent.setParameter.
 *  @author W.Pasman 19aug08
 *
 * The type is changable.
 * With the function getType you can check what kind of data is in a particular AgenParam.
 * 	@author Marc Dekker
 */

public class AgentParam {
	public String agentclass; 	// the agent class for which this is a parameter.
								// we do not refer to Class because that suggests loading its definition etc
								// and that may not be possible, especially from static contexts.
	public String name;
	public double min;
	public double max;
	public boolean isEnum;
	public boolean isInt;
	public String[] EnumOptions;
	
	public AgentParam(String agentclassP, String nameP, double minP, double maxP){
		agentclass=agentclassP;
		name=nameP;
		min=minP;
		max=maxP;
		EnumOptions = null;
		isEnum = false;
		isInt = false;
	}
	
	public AgentParam(String agentclassP, String nameP, String[] tOptions){
		agentclass=agentclassP;
		name=nameP;
		min=0;
		max=0;
		EnumOptions = tOptions;
		isEnum = true;
		isInt = false;
	}
	
	public AgentParam(String agentclassP, String nameP, int minP, int maxP){
		agentclass=agentclassP;
		name=nameP;
		min=minP;
		max=maxP;
		EnumOptions = null;
		isEnum = false;
		isInt = true;
	}
	
	static final long serialVersionUID=0;
	
	public boolean equals(Object o) {
		if (!(o instanceof AgentParam)) return false;
		AgentParam ap=(AgentParam)o;
		return ap.agentclass.equals(agentclass) && ap.name.equals(name) /*&& ap.min==min && ap.max==max*/;
	}
	public String toString() {
		return agentclass+":"+name;
	}
	
	//Returns the class name of T for this AgentParam
	public String getType(){
		if(isEnum){
			return "Enumuration";
		}
		if(isInt){
			return "Integer";
		}
		return "Double";
	}
}