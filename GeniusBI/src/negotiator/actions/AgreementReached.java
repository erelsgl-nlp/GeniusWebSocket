package negotiator.actions;

import negotiator.AgentID;

public class AgreementReached extends Action {
    
	/**
     * Default constructor. Shouldn't be used, exist to enable serialization  
     */
	public AgreementReached() { }
	
    /** Creates a new instance of AgreementReached */
    public AgreementReached(AgentID agent) {
        super(agent);
    }
    public String toString() {
        return "(AgreementReached)";
    }    
} 
