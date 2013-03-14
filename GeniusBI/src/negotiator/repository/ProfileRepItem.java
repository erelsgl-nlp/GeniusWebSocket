package negotiator.repository;

import javax.xml.bind.annotation.*;
import java.net.URL;
import negotiator.exceptions.*;
import javax.xml.bind.Unmarshaller;

/**
 * ProfileRepItem is a profile, as an item to put in the registry.
 * The profile is not contained here, it's just a (assumed unique) filename.
 * 
 * @author wouter
 *
 */
@XmlRootElement
public class ProfileRepItem implements RepItem {
	@XmlAttribute
	URL url; 	// URL is not accepted by JAXB xml thingie. We convert in getURL().
	@XmlTransient
	DomainRepItem domain;
	
	/** This creator is not for public use, only to keep XML parser happy... */
	public ProfileRepItem() { 
		try { 
			url=new URL("file:uninstantiatedProfilerepitem"); 
		} catch (Exception e) { new Warning("failed to set filename default value"+e); }
	}
	
	public ProfileRepItem(URL file,DomainRepItem dom) {
		url=file;
		domain=dom;
	}
	
	public  URL getURL() { return url; }
	
	public DomainRepItem getDomain() { return domain; }
	
	@Override public String toString() {
		return "ProfileRepItem["+url+"]";
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.domain = (DomainRepItem)parent;
	}
	
	@Override public boolean equals(Object o) {
		if (!(o instanceof ProfileRepItem)) return false;
		return url.equals( ((ProfileRepItem)o).getURL());
	}
}