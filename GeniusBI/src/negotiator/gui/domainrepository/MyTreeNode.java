package negotiator.gui.domainrepository;

import javax.swing.tree.DefaultMutableTreeNode;

import negotiator.exceptions.Warning;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;

public class MyTreeNode extends DefaultMutableTreeNode {
	RepItem repository_item;
	
	public MyTreeNode(RepItem item)
	{
		super(item);
		repository_item=item;
	}
	
	public String toString() {
		if (repository_item==null) return "";
		if (repository_item instanceof DomainRepItem)
			return shortfilename(((DomainRepItem)repository_item).getURL().getFile());
		if (repository_item instanceof ProfileRepItem)
			return shortfilename( ((ProfileRepItem)repository_item).getURL().getFile());
		new Warning("encountered item "+repository_item+" of type "+repository_item.getClass());
		return "ERR";
	}
	/** returns only the filename given a full path with separating '/' */
	public String shortfilename(String filename) {
		int lastslash=filename.lastIndexOf('/');
		if (lastslash==-1) return filename;
		return filename.substring(lastslash+1); 
	}
	
	public RepItem getRepositoryItem() { return repository_item; }
}