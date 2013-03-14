package negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;

public class AgentParamsMapAdapter extends XmlAdapter<APTemp,Map<AgentParameterVariable,AgentParamValue>> {

	@Override
	public APTemp marshal(Map<AgentParameterVariable,AgentParamValue> arg0) throws Exception {
		APTemp temp = new APTemp();
		if(arg0==null) {
			return temp;
		}
		for(Entry<AgentParameterVariable,AgentParamValue> entry : arg0.entrySet()){
			temp.entry.add(new APItem(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<AgentParameterVariable,AgentParamValue> unmarshal(APTemp arg0) throws Exception {
		Map<AgentParameterVariable,AgentParamValue> map = new HashMap<AgentParameterVariable,AgentParamValue>();
		for(APItem item: arg0.entry) {
			map.put(item.key, item.value);
		}
		return map;
	}

}

class APTemp {
  @XmlElement(name="issue")  
  public List<APItem> entry ;
  
  public APTemp(){entry = new ArrayList<APItem>();}
   
}

@XmlRootElement
class APItem {
	  @XmlElement(name="index")
	  public AgentParameterVariable key;
	  	
	  @XmlElement	  
	  public AgentParamValue value;
	  
	  public APItem(){
	  }
	  
	  public APItem(AgentParameterVariable key, AgentParamValue val) {
		  this.key = key;
		  this.value = val;
	  }
}
