package negotiator.logging;

import java.util.ArrayList;
import java.io.*;
import javax.xml.bind.annotation.*;

@XmlTransient
public abstract class LoggingBasis {
	
	@XmlElementWrapper(name="Additional_Information")
	@XmlElement(name="Info")
	private ArrayList<String> mAdditional= new ArrayList<String>();
	
	public void addInformation(String tInput){
		mAdditional.add(tInput);
	}
	
	public void setInformation(int tIndex, String tInput){
		mAdditional.set(tIndex, tInput);
	}
	
	public String getInformation(int tIndex){
		if(tIndex<0 || tIndex>= mAdditional.size()){
			return null;
		}
		return mAdditional.get(tIndex);
	}
	
	public int getInformationIndex(String tObject){
		return mAdditional.indexOf(tObject);
	}
	
	public ArrayList<String> getInformation(){
		return mAdditional;
	}
	
	public void delInformation(int tIndex){
		mAdditional.remove(tIndex);
	}
	
	public void delInformation(String tObject){
		mAdditional.remove(tObject);
	}
	
	public int getNumberOfInformation(){
		return mAdditional.size();
	}
}
