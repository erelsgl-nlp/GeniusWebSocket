package negotiator.gui.tree;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

/**
*
* @author Richard Noorlandt
* 
*/


public class JLabelCellRenderer extends DefaultTableCellRenderer {

	
	
	//Methods
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return (Component)value;
	}
}
