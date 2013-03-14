package negotiator.issue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueInteger extends Value {
	
	//	 Class fields
	@XmlAttribute
	int value;
	
	public ValueInteger() {
		// TODO Auto-generated constructor stub
	}
	
	// Constructor
	public ValueInteger(int i) {
		value = i;
	}
	
	// Class methods
	public ISSUETYPE getType() {
		return ISSUETYPE.INTEGER;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
	
	public boolean equals(Object pObject) {
		if(pObject instanceof ValueInteger) {
			ValueInteger val = (ValueInteger)pObject;
			return  value==val.getValue();
		} else 
			if(pObject instanceof Integer){
				int val = (Integer) pObject;
				return value == val;
			} else				
			return false;
	}

}
