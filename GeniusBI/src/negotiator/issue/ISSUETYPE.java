package negotiator.issue;

public enum ISSUETYPE {	UNKNOWN, DISCRETE, INTEGER, REAL, OBJECTIVE;
// KH dd070510: Removed DISCRETEWCOST, PRICE

	public static ISSUETYPE convertToType(String typeString) {
		// TODO: already checked in Domain.java. Where do we want this check?
		//Added by Dmytro on 09/05/2007 --------------------------
		//If typeString is null for some reason (i.e. not spceified in the XML template
		// then we assume that we have DISCRETE type
		if(typeString==null) return ISSUETYPE.DISCRETE;
		//End of Added by Dmytro
// TODO: Remove.
//		if (typeString.equalsIgnoreCase("price"))
//			return ISSUETYPE.PRICE;
		else if (typeString.equalsIgnoreCase("integer"))
			return ISSUETYPE.INTEGER;
		else if (typeString.equalsIgnoreCase("real"))
			return ISSUETYPE.REAL;
		else if (typeString.equalsIgnoreCase("discrete"))
			return ISSUETYPE.DISCRETE;
		else {
			// Type specified incorrectly!
			System.out.println("Type specified incorrectly.");
			// For now return DISCRETE type.
			return ISSUETYPE.DISCRETE;
			// TODO: Define corresponding exception.
		}
	}
	
}