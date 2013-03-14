package negotiator.issue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValueDiscrete extends Value {

	// Class fields
	@XmlAttribute
	public String value;
	
	// Constants
	public static final String StrNoAgreement = "-";
	
	public ValueDiscrete() {
		
	}
	
	// Constructor
	public ValueDiscrete(String s) {
		value = s;
	}
	
	// Class methods
	public final ISSUETYPE getType() {
		return ISSUETYPE.DISCRETE;
	}
	
	public String getValue() {
		return value;
	}
		
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
    	if(obj instanceof ValueDiscrete) {
			ValueDiscrete val = (ValueDiscrete)obj;
			return value.equals(val.getValue());
		} else
			if(obj instanceof String){
				String val = (String) obj;
				return (value.equals(val));
			} else				
			return false;
		
	}
	
	
}
