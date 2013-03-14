package negotiator.logging;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class RoundLog extends LoggingBasis {
	
	
	private int mRoundIndex;
	@XmlElementWrapper(name = "Actions")
	@XmlElement(name="ActionLog")
	private ArrayList<ActionLog> mActions = new ArrayList<ActionLog>();
	
	public RoundLog(){
		mRoundIndex = -1;
	}
	
	public RoundLog(int tIndex){
		mRoundIndex = tIndex;
	}

	public RoundLog(ArrayList<ActionLog> tActions){
		mActions = tActions;
	}
	
	public RoundLog(int tIndex, ArrayList<ActionLog> tActions){
		mActions = tActions;
		mRoundIndex = tIndex;
	}
		
	public ArrayList<ActionLog> getActions(){
		return mActions;
	}
	
	public ActionLog getAction(int tIndex){
		return mActions.get(tIndex);
	}
	
	public void setAction(int tIndex, ActionLog tAction){
		mActions.set(tIndex, tAction);
	}
	
	public void addAction(ActionLog tAction){
		mActions.add(tAction);
	}
	
	public void delAction(ActionLog tAction){
		mActions.remove(tAction);
	}
	
	public void delAction(int tIndex){
		mActions.remove(tIndex);
	}
	
	public int getNumberOfActions(){
		return mActions.size();
	}
	
	public void setRoundIndex(int tIndex){
		mRoundIndex = tIndex;
	}
	@XmlAttribute(name="Index")	
	public int getRoundIndex(){
		return mRoundIndex;
	}
}
