package negotiator.issue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class ValueReal extends Value {
	
	// Class fields
	@XmlAttribute
	double value;
	
	public ValueReal() {
		// TODO Auto-generated constructor stub
	}
	
	// Constructor
	public ValueReal(double r) {
		value = r;
	}
	
	// Class methods
	public ISSUETYPE getType() {
		return ISSUETYPE.REAL;
	}
	
	public double getValue() {
		return value;
	}
	
	public String toString() {
		return Double.toString(value);
	}
	
	public boolean equals(Object pObject) {
		if(pObject instanceof ValueReal) {
			ValueReal val = (ValueReal)pObject;
			return  value==val.getValue();
		} else 
			if(pObject instanceof Double){
				double val = (Double) pObject;
				return value == val;
			} else
			return false;
	}

}
