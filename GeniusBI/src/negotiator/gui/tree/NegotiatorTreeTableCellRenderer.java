package negotiator.gui.tree;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import jtreetable.*;
import negotiator.gui.tree.*;

/**
* This class is a modification of the TreeTableCellRenderer which can be found as an inner
* class of JTreeTable in the jtreetable package. Since subclassing the inner class was not
* possible without modifying the treetable package I just copy-pasted the code from
* TreeTableCellRenderer and modified it. - Richard 
* 
* @author Richard Noorlandt
* 
* @author Philip Milne
* @author Scott Violet
* 
*/

public class NegotiatorTreeTableCellRenderer  extends JTree implements TableCellRenderer {
	
	//Attributes
	/** Last table/tree row asked to renderer. */
	protected int visibleRow;
	
	private JTreeTable treeTable;
	private DefaultTreeCellRenderer defaultRenderer;
	
	//Constructors
	public NegotiatorTreeTableCellRenderer(JTreeTable treeTable) {
		super(treeTable.getTree().getModel());
		this.treeTable = treeTable;
		defaultRenderer = new DefaultTreeCellRenderer();
	}
	
	//Methods
	
	/**
	 * updateUI is overridden to set the colors of the Tree's renderer
	 * to match that of the table.
	 */
	public void updateUI() {
	    super.updateUI();
	    // Make the tree's cell renderer use the table's cell selection
	    // colors. 
	    TreeCellRenderer tcr = getCellRenderer();
	    if (tcr instanceof DefaultTreeCellRenderer) {
		DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr); 

		dtcr.setTextSelectionColor(UIManager.getColor
						("Table.selectionForeground"));
		dtcr.setBackgroundSelectionColor(UIManager.getColor
						("Table.selectionBackground"));
	    }
	}

	/**
	 * Sets the row height of the tree, and forwards the row height to
	 * the table.
	 */
	public void setRowHeight(int rowHeight) { 
	    if (rowHeight > 0) {
	    	super.setRowHeight(rowHeight); 
			if (treeTable != null &&
				treeTable.getRowHeight() != rowHeight) {
				treeTable.setRowHeight(getRowHeight()); 
			}
	    }
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h) {
	    super.setBounds(x, 0, w, treeTable.getHeight());
	}

	/**
	 * Sublcassed to translate the graphics such that the last visible
	 * row will be drawn at 0,0.
	 */
	public void paint(Graphics g) {
	    g.translate(0, -visibleRow * getRowHeight());
	    super.paint(g);
	}

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column) {

		if(isSelected) {
			setBackground(Color.YELLOW);
			//((JTreeTable)table).getTree().setBackground(Color.YELLOW);
			if (value instanceof JTextField) {
				//((JTextField)value).setOpaque(true);
				((JTextField)value).setBackground(Color.YELLOW);
				//((JTextField)value).setForeground(Color.YELLOW);
				((JTextField)value).repaint();
				System.out.println(((JTextField)value).getText());//((JTextField)value).setText("selected");
				return (Component)value;
			}
		}
		else {
			setBackground(Color.WHITE);
			if (value instanceof JTextField)
				((JTextField)value).setBackground(Color.WHITE);
		}
		
		visibleRow = row;
		return this;
	
	}
}
