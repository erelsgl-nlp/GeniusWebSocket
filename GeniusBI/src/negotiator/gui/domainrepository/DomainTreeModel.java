package negotiator.gui.domainrepository;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import java.util.ArrayList;
import negotiator.repository.*;
import javax.swing.tree.TreeModel;
import negotiator.exceptions.*;

/**
 * Tree is not so simple. The DomainRepItem and ProfileRepItem are not so easy to navigate through,.
 * ProfileRepItem has no pointer to "parent". We need to have access to root item in order to find back that parent.
 * And we do not want to scatter around all this kind of info.
 * @author wouter
 *
 */
class DomainTreeModel implements TreeModel {
  
	/** either item or rootitem is set, the other is null */
	RepItem item;
	Repository rootitem; // link to the domain repository
  
  public DomainTreeModel(RepItem it) {
	  item=it;
  }
  
  public DomainTreeModel(Repository r) {
	  rootitem=r;
  }
  
  /** returns null if no children */
  ArrayList<RepItem> getChildren()
  {
	  if (item!=null) {
		  if (item instanceof ProfileRepItem) {
			  return null;
		  }
		  if (item instanceof DomainRepItem) {
			   // zhut, we have to do totally unnecesary cast
			   // on every member in the list...
				  return new ArrayList<RepItem>(((DomainRepItem)item).getProfiles());
					  
		  }
		  throw new IllegalStateException("Encountered node "+item+" of class "+item.getClass());
	  }
	  //item==null, then we are at root node. Return nodes in repository.
	  return rootitem.getItems();
		  
	  
  }
  public int getIndexOfChild(Object parent, Object child) 
  {
	 // if (parent)
		  return 0;
  }
  
  public Object getChild(Object parent, int index)
  {
	  if (parent==null)  { // at root
		  return rootitem.getItems().get(index);
	  } else {
		  if (!(parent instanceof DomainTreeModel))
			  throw new IllegalStateException("Encountered parent node "+item+" of class "+parent.getClass());
		//  	parent
	  }
	  return null;
  }
  
  public boolean isLeaf() {
    return(false);
  }

  public int getChildCount() {
  /*  if (!areChildrenDefined)
      defineChildNodes();
    return(super.getChildCount());*/
	  return 0;
  }

public void addTreeModelListener(TreeModelListener arg0) {
	// TODO Auto-generated method stub
	
}

public int getChildCount(Object arg0) {
	// TODO Auto-generated method stub
	return 0;
}

public Object getRoot() {
	// TODO Auto-generated method stub
	return null;
}

public boolean isLeaf(Object arg0) {
	// TODO Auto-generated method stub
	return false;
}

public void removeTreeModelListener(TreeModelListener arg0) {
	// TODO Auto-generated method stub
	
}

public void valueForPathChanged(TreePath arg0, Object arg1) {
	// TODO Auto-generated method stub
	
}


 
}