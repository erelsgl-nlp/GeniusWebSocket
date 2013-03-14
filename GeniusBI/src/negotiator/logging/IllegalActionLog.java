package negotiator.logging;

import negotiator.actions.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement
public class IllegalActionLog extends ActionLog {
	
	@XmlAttribute(name="Details")
	private String mDetails="";
	
	public IllegalActionLog(){
		super();
	}
	
	public IllegalActionLog(Action tInput){
		super(tInput);
		if(tInput.getClass().equals(IllegalAction.class)){
			mDetails = ((IllegalAction)tInput).getDetails();
		}
	}
	
	public String getDetails(){
		return mDetails;
	}
	
	public void setDetails(String tDetails){
		mDetails = tDetails;
	}
}
