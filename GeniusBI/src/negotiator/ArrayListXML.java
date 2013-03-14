/*
 * Agent.java
 *
 * Created on November 6, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package negotiator;

import java.util.ArrayList;
import negotiator.xml.SimpleElement;
import negotiator.XMLable;

/**
 * ArrayListXML is an arraylist with an extra toXML function.
 * 
 * @author W.Pasman
 * 
 */

public class ArrayListXML<E> extends ArrayList<E> implements XMLable
{
	ArrayListXML(ArrayList<E> l) { super(l); } // useful to cast ArrayList to ArrayListXML.
	
   public SimpleElement toXML()
   {
	   SimpleElement xmlist=new SimpleElement("ArrayList");
	   int N=this.size();
	   xmlist.setAttribute("size", ""+N);
	   for (int i=0; i<N;i++)
	   {
		   SimpleElement elt=new SimpleElement("ArrayListElement");
		   elt.setAttribute("index", ""+i); // have to do this way, XML does not enforce any order...
		   Object e=get(i);
		   if (!(e instanceof XMLable))
			   throw new ClassCastException("Element of ArrayListXML at position "+i+" is not XMLable");
		   elt.addChildElement(((XMLable)e).toXML());
		   xmlist.addChildElement(elt);
	   }
	   return xmlist;
   }
}
