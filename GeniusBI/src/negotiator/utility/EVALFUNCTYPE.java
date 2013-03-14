package negotiator.utility;

public enum EVALFUNCTYPE { CONSTANT, LINEAR, FARATIN, TRIANGULAR, TRIANGULAR_VARIABLE_TOP ;

	public static EVALFUNCTYPE convertToType(String type) {
		if (type.equalsIgnoreCase("linear"))
			return EVALFUNCTYPE.LINEAR;
		else if (type.equalsIgnoreCase("constant"))
			return EVALFUNCTYPE.CONSTANT;
		if (type.equalsIgnoreCase("faratin"))
			return EVALFUNCTYPE.FARATIN;
		if (type.equalsIgnoreCase("triangular"))
			return EVALFUNCTYPE.TRIANGULAR;
		else return null;
	}

	public static double evalLinear(double x, double coef1, double coef0) {
		return coef1*x+coef0;
	}
	public static double evalLinearRev(double y, double coef1, double coef0) {
		return (y-coef0)/coef1;
	}

	public static double evalFaratin(double x, double max, double min, double alpha, double epsilon) {
		return 1/Math.PI*Math.atan(((2*Math.abs(x-min)/(max-min)*Math.pow((x-min)/(max-min), alpha)-1)*Math.tan(Math.PI*(1/2-epsilon))))+Math.PI/2;
		
	}
	
	public static double evalTriangular(double x, double lowerBound, double upperBound, double top) {
		if(x<lowerBound) return 0;
		else
			if(x<top) return (x-lowerBound)/(top-lowerBound);
			else 
				if(x<upperBound) return (1-(x-top)/(upperBound -  top));
				else return 0;
	}
	public static double evalTriangularVariableTop(double x, double lowerBound, double upperBound, double top, double topValue) {
		if(x<lowerBound) return 0;
		else
			if(x<top) return topValue*(x-lowerBound)/(top-lowerBound);
			else 
				if(x<upperBound) return topValue*(1-(x-top)/(upperBound -  top));
				else return 0;
	}
	
}
