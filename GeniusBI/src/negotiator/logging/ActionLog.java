package negotiator.logging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import negotiator.actions.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.*;


@XmlType(name = "ActionLogType", propOrder = {"source", "action", "mUtilities","mAdditional"})
@XmlRootElement
public class ActionLog extends LoggingBasis {
	
	private AgentID mSource;
	private Action mAction;
	
	private Date mTimestamp = null;
	
	@XmlJavaTypeAdapter(value=UtilitesAdapter.class)   
    @XmlElement(name = "utilities")
    private HashMap<String,Double> mUtilities=new HashMap<String, Double>();
	
	public ActionLog(){		
	}
	
	public ActionLog(Action tAction){
		this.mSource = tAction.getAgent();
		this.mAction = tAction;
		mUtilities = new HashMap<String, Double>();
	}
	
	public AgentID getSource(){
		return mSource;
	}
	
	public void setSource(AgentID tAgent){
		mSource = tAgent;
	}
	
	public void setAction(Action tAction){
		mAction = tAction;
	}
	
	public void addUtility(AgentID agent, double utility){
		mUtilities.put(agent.toString(), utility);
	}
	
	public HashMap<String, Double> getUtilities(){
		return mUtilities;
	}
	
	public Action getAction(){
		return mAction;
	}
	
	public void setTimestamp(Date time) {
		mTimestamp = time;
	}

	@XmlAttribute(name = "Timestamp")
	public Date getTimestamp() {
		return mTimestamp;
	}

}

class UtilitesAdapter extends XmlAdapter<UtilityList,Map<String,Double>> {

	@Override
	public UtilityList marshal(Map<String, Double> arg0) throws Exception {
		if (arg0.isEmpty())
			return null;
		UtilityList temp = new UtilityList();
		for(Entry<String, Double> entry : arg0.entrySet()){
			temp.entry.add(new UtilityItem(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<String, Double> unmarshal(UtilityList arg0) throws Exception {
		Map<String, Double> map = new HashMap<String, Double>();
		if (arg0 !=null) {
			for(UtilityItem item: arg0.entry) {
				map.put(item.key, item.value);
			}
		}
		return map;
	}

}
class UtilityList {
  @XmlElement(name="utility")  
  public List<UtilityItem> entry ;
  
  public UtilityList(){entry = new ArrayList<UtilityItem>();}
   
}

@XmlType(name = "UtilityItemType", propOrder = {"value", "key"})
@XmlRootElement
class UtilityItem {
	  @XmlAttribute(name="agent")
	  public String key;
	  	
	  @XmlAttribute(name="value")  
	  public Double value;
	  
	  public UtilityItem(){ }
	  public UtilityItem(String key, Double val) {
		  this.key = key;
		  this.value = val;
	  }
	}