package negotiator.utility;

public enum EVALUATORTYPE {	DISCRETE, INTEGER, REAL, PRICE, OBJECTIVE;

	public static EVALUATORTYPE convertToType(String typeString) {
		if (typeString.equalsIgnoreCase("price"))
        	return EVALUATORTYPE.PRICE;
        else if (typeString.equalsIgnoreCase("integer"))
        	return EVALUATORTYPE.INTEGER;
        else if (typeString.equalsIgnoreCase("real"))
        	return EVALUATORTYPE.REAL;
        else if (typeString.equalsIgnoreCase("discrete"))
        	return EVALUATORTYPE.DISCRETE;
        else if (typeString.equalsIgnoreCase("objective"))
        	return EVALUATORTYPE.OBJECTIVE;
        else {
        	// Type specified incorrectly!
        	System.out.println("Evaluator type specified incorrectly.");
        	// For now return DISCRETE type.
        	return EVALUATORTYPE.DISCRETE;
        	// TODO: Define corresponding exception.
        }
	}

}