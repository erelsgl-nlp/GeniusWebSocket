package negotiator.gui.tree;

import negotiator.*;
import negotiator.xml.*;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/

public class NegotiatorTreeLauncher {

	
	
	//Methods
	
	public static void main(String[] args) {
		
		/* Old code to bridge the strange necessity to use SimpleElement for about everything.
		
		SimpleElement rootElem = new SimpleElement("objective");
		rootElem.setAttribute("number_of_issues", "0");
		rootElem.setAttribute("index", "0");
		rootElem.setAttribute("number_of_children", "0");
		SimpleElement dummy = new SimpleElement("root");
		dummy.setAttribute("number_of_issues", "0");
		dummy.addChildElement(rootElem);
		Domain domain = new Domain(dummy);
		*/
		Objective obj = new Objective(null, "root", 0);
		Domain domain = new Domain();
		domain.setObjectivesRoot(obj);
		
		TreeFrame mainFrame = new TreeFrame(domain);

	}

}
