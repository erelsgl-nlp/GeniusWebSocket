package negotiator.logging;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import negotiator.actions.*;

@XmlRootElement
public class OfferLog extends ActionLog {
	@XmlElement(name="Utilities")
	@XmlJavaTypeAdapter(OfferMapAdapter.class)
	private HashMap<String,Double> mUtility = new HashMap<String,Double>(); 
	
	public OfferLog(){
		super();
	}
	
	public OfferLog(Action tInput){
		super(tInput);
	}
	
	public void addUtility(String tID, double tInput){
		mUtility.put(tID, tInput);
	}
	
	public void setUtility(String tIndex, double tInput){
		mUtility.put(tIndex, tInput);
	}
	
	public void delUtility(String tUtility){
		mUtility.remove(tUtility);
	}
	
	public double getUtility(String tIndex){
		return mUtility.get(tIndex);
	}
	
	public HashMap<String, Double> getUtilities(){
		return mUtility;
	}
}

class OfferMapAdapter extends XmlAdapter<OfferTemp,Map<String,Double>> {

	@Override
	public OfferTemp marshal(Map<String,Double> arg0) throws Exception {
		OfferTemp temp = new OfferTemp();
		for(Entry<String,Double> entry : arg0.entrySet()){
			temp.entry.add(new OfferItem(entry.getKey(), entry.getValue()));
		}
		return temp;
	}

	@Override
	public Map<String, Double> unmarshal(OfferTemp arg0) throws Exception {
		Map<String, Double> map = new HashMap<String, Double>();
		for(OfferItem item: arg0.entry) {
			map.put(item.key, item.value);
		}
		return map;
	}

}
class OfferTemp {
  //@XmlElement(name="issue")  
  public List<OfferItem> entry ;
  
  public OfferTemp(){entry = new ArrayList<OfferItem>();}
   
}
@XmlRootElement
class OfferItem {
	  @XmlAttribute(name="Agent")
	  public String key;
	  	
	  @XmlElement(name="Utility")	  
	  public Double value;
	  
	  public OfferItem(){ }
	  public OfferItem(String key, Double val) {
		  this.key = key;
		  this.value = val;
	  }
	}
