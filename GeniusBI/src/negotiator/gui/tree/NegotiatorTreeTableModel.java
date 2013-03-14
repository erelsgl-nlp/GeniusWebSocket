package negotiator.gui.tree;

import jtreetable.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import negotiator.*;
import negotiator.issue.*;
import negotiator.utility.*;
import negotiator.gui.dialogs.NewObjectiveDialog;
/**
*
* @author Richard Noorlandt
* 
*/

//TODO: replace instances of root by Domain.getRoot (or something similar)

public class NegotiatorTreeTableModel extends AbstractTreeTableModel implements TreeTableModel {

	//Attributes
	private static final String NAME = "Name";
	private static final String TYPE = "Type";
	private static final String NUMBER = "Number";
	private static final String VALUE = "Value";
	private static final String WEIGHT = "Weight";
	
	private Objective root;
	private Domain domain;
	private String[] colNames;
	private Class[] colTypes;
	private UtilitySpace utilitySpace;
	private boolean containsUtilitySpace;
	private Map<Objective, JTextField> names;
	private Map<Objective, JTextField> types;
	private Map<Objective, JTextField> numbers;
	private Map<Objective, WeightSlider> sliders;
	private Map<Objective, IssueValuePanel> issueValues; //Contains objects representing the possible values of an issue
	
	private static final String[] domainColNames = {NAME, TYPE, /*NUMBER,*/ VALUE};
	private static final Class[] domainColTypes = {TreeTableModel.class, JTextField.class, /*JTextField.class,*/ IssueValuePanel.class};
	private static final String[] domainAndUtilityColNames = {NAME, TYPE, /*NUMBER,*/ VALUE, WEIGHT};
	private static final Class[] domainAndUtilityColTypes = {TreeTableModel.class, JTextField.class, /*JTextField.class,*/ IssueValuePanel.class, WeightSlider.class};
	
	
	//Constructors
	public NegotiatorTreeTableModel(Domain domain) {
		this.domain = domain;
		this.root = domain.getObjectivesRoot();
		this.containsUtilitySpace = false;
		this.colNames = domainColNames;
		this.colTypes = domainColTypes;
		names = new HashMap<Objective, JTextField>();
		types = new HashMap<Objective, JTextField>();
		numbers = new HashMap<Objective, JTextField>();
		issueValues = new HashMap<Objective, IssueValuePanel>();
	}
	
	public NegotiatorTreeTableModel(Domain domain, UtilitySpace utilitySpace) {
		this.domain = domain;
		this.root = domain.getObjectivesRoot();
		this.utilitySpace = utilitySpace;
		this.containsUtilitySpace = true;
		this.colNames = domainAndUtilityColNames;
		this.colTypes = domainAndUtilityColTypes;
		names = new HashMap<Objective, JTextField>();
		types = new HashMap<Objective, JTextField>();
		numbers = new HashMap<Objective, JTextField>();
		issueValues = new HashMap<Objective, IssueValuePanel>();
		sliders = new HashMap<Objective, WeightSlider>();
		
		
	}
	
	//Methods
	
	/**
	 * @return the root Object of the tree.
	 */
	public Object getRoot() {
		return root;
	}
	
	/**
	 * @return true if and only if node is an Issue.
	 */
	public boolean isLeaf(Object node) {
		return (node instanceof Issue);
	}
	
	/**
	 * 
	 * @param row the row number of the cell.
	 * @param col the column number of the cell.
	 * @return if the given cell is editable.
	 */
	public boolean isCellEditable(int row, int col) {
		if (col >= colTypes.length || col < 0)
			return false;
		else if (colTypes[col] == TreeTableModel.class)
			return true;
		else if (colTypes[col] == WeightSlider.class)
			return true;
		else
			return false;
	}
	
	public boolean isCellEditable(Object node, int column) {
		return isCellEditable(-1, column);
   }

	
	/**
	 * Method is empty at the moment. Default implementation from AbstractTreeTableModel.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {}
			
	/**
	 * @return the number of columns for the TreeTable. 
	 */
	public int getColumnCount() {
		return colNames.length;
	}

	/**
	 * @return the name of column. If column >= getColumnCount, an empty String is returned.
	 */
	public String getColumnName(int column) {
		if (column < getColumnCount())
			return colNames[column];
		else
			return "";
	}
	
	public Class getColumnClass(int column) {
		return colTypes[column];
	}

	/**
	 * When node is an Objective, this method returns the object beloging in the given column.
	 * If node is no Objective, or column has an invalid value, null is returned.
	 * 
	 * @return the contents of column, for the given node.
	 */
	public Object getValueAt(Object node, int column) {
		Objective objective;
		if (!(node instanceof Objective) || getColumnCount() <= column || column < 0)
			return null;
		else
			objective = (Objective)node;
		
		
		
		//TODO Maybe also instanceof Issue.
		//do the rest
		//Also, when only editing Objectives, don't show anything after the objective columns. <-- already happens automatically due to getColumnCount()
		
		/*switch(column) {
		case 0: 	return objective.getName();
		case 1: 	return objective.getType();
		case 2:		return objective.getNumber();
		case 3:		return utilitySpace.getEvaluator(objective.getNumber());//Is this going to work in all cases? Answer: no
		case 4:		return getWeightSlider(objective); 
		}*/
		
		//if (getColumnName(column).equals(arg0))
		if (getColumnName(column) == NAME)
			return getNameField(objective);
		else if (getColumnName(column) == TYPE)
			return getTypeField(objective);
		else if (getColumnName(column) == NUMBER)
			return getNumberField(objective);
		else if (getColumnName(column) == VALUE)
			return getIssueValuePanel(objective);
		else if (getColumnName(column) == WEIGHT)
			return getWeightSlider(objective);
		
		return null;
	}

	/**
	 * Returns parent's child at the given index. If parent is not of type Objective, or index is invalid, null is returned.
	 */
	public Object getChild(Object parent, int index) {
		if (!(parent instanceof Objective) || ((Objective)parent).getChildCount() <= index || index < 0)
			return null;
		else
			return ((Objective)parent).getChildAt(index);
	}

	/**
	 * If parent is instanceof Objective, returns the number of children. Otherwise, 0 is returned.
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof Objective)
			return ((Objective)parent).getChildCount();
		else
			return 0;
	}
	
	/**
	 * Recursively calculates the highest Objective / Issue number in the tree.
	 * @return the highest Objective / Issue  number in the tree, or -1.
	 */
	public int getHighestObjectiveNr() {
		if (root != null)
			return root.getHighestObjectiveNr(-1);
		else
			return -1;
	}
	
	/**
	 * 
	 * @return the Domain.
	 */
	public Domain getDomain() {
		return domain;
	}
	
	/**
	 * 
	 * @return the UtilitySpace.
	 */
	public UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}
	
	/**
	 * Sets this model's UtilitySpace. A UtilitySpace is required to map utilities to treeNodes.
	 * @param space a UtilitySpace object.
	 */
	public void setUtilitySpace(UtilitySpace space) {
		utilitySpace = space;
		
		if (space != null) {
			containsUtilitySpace = true;
			colNames = domainAndUtilityColNames;
			colTypes = domainAndUtilityColTypes;
		}
		else {
			containsUtilitySpace = false;
			colNames = domainColNames;
			colTypes = domainColTypes;
		}
	}
	
	public void updateWeights(WeightSlider caller, double newWeight) {
		//Calculate the new weights for the tree, and return to caller with the caller's new weight. This new weight can be 
		//different from the requested weight, for instance if that modification is impossible for some reason.
		
		//Root may not be null!
		Enumeration<Objective> objectives = root.getPreorderEnumeration();
		while (objectives.hasMoreElements()) {
			Objective obj = objectives.nextElement();
			double updatedWeight = utilitySpace.getWeight(obj.getNumber());
			getWeightSlider(obj).setWeight(updatedWeight);
		}
	}
	
	public void removeObjective(Objective obj) {
		obj.removeFromParent();
		names.remove(obj);
		types.remove(obj);
		numbers.remove(obj);
		
		//In case we're editing a UtilitySpace
		if (containsUtilitySpace) {
			getUtilitySpace().removeEvaluator(obj);
			sliders.remove(obj);
		}
	}
	
	protected JTextField getNameField(Objective node) {
		JTextField field = names.get(node);
		if (field == null) {
			field = new JTextField(node.getName());
			field.setBorder(BorderFactory.createEmptyBorder());
			setNameField(node, field);
		}
		return field;
	}
	
	protected JTextField getTypeField(Objective node) {
		JTextField field = types.get(node);
		if (field == null) {
			field = new JTextField("" + node.getType());
			field.setBorder(BorderFactory.createEmptyBorder());
			setTypeField(node, field);
		}
		return field;
	}
	
	protected JTextField getNumberField(Objective node) {
		JTextField field = numbers.get(node);
		if (field == null) {
			field = new JTextField("" + node.getNumber());
			field.setBorder(BorderFactory.createEmptyBorder());
			setNumberField(node, field);
		}
		return field;
	}
	
	/**
	 * Returns the WeightSlider belonging to the given Objective. If there is no WeightSlider attached to the given Objective,
	 * a new one is created and added using setWeightSlider(Objective, WeightSlider).
	 * @param node an Objective.
	 * @return the slider associated with node.
	 */
	public WeightSlider getWeightSlider(Objective node) {
		WeightSlider slider = sliders.get(node);
		if (slider == null) {
			slider = new WeightSlider(this, node);
			setWeightSlider(node, slider);
			
			if (utilitySpace != null) {
				System.out.println("Our newest debug info says node  " + node.getNumber() + " has weight " + utilitySpace.getWeight(node.getNumber()));
				slider.setWeight(utilitySpace.getWeight(node.getNumber()));
			}
			else {
				System.out.println("Here we come in the else part of getWeightSlider. Set weight to 0.5");
				slider.setWeight(0.5);
			}
		}
		return slider;
	}
	
	/**
	 * Sets the WeightSlider object for the given Objective.
	 * @param node Objective to attach the slider to.
	 * @param slider the WeightSlider to be attached to node.
	 */
	protected void setWeightSlider(Objective node, WeightSlider slider) {
		sliders.put(node, slider);
	}
	
	protected void setNameField(Objective node, JTextField field) {
		names.put(node, field);
	}
	
	protected void setTypeField(Objective node, JTextField field) {
		types.put(node, field);
	}
	
	protected void setNumberField(Objective node, JTextField field) {
		numbers.put(node, field);
	}
	
	public IssueValuePanel getIssueValuePanel(Objective node) {
		//TODO Finish!
		IssueValuePanel value = issueValues.get(node);
		if (value == null) {
			if (node.getType() == ISSUETYPE.DISCRETE) {
				value = new IssueDiscreteValuePanel(this, (IssueDiscrete)node);
			}
			else if (node.getType() == ISSUETYPE.INTEGER) {
				value = new IssueIntegerValuePanel(this, (IssueInteger)node);
			}
			else if (node.getType() == ISSUETYPE.REAL) {
				value = new IssueRealValuePanel(this, (IssueReal)node);
			}
			else if (node.getType() == ISSUETYPE.OBJECTIVE) {
				value = new ObjectiveValuePanel(this, node);
			}
			setIssueValuePanel(node, value);
		}
		return value;
	}
	
	protected void setIssueValuePanel(Objective node, IssueValuePanel panel) {
		issueValues.put(node, panel);
	}
	
	/**
	 * Notifies the listeners that the structure of the tree has changed. In it's current
	 * implementation, this method is just a wrapper for the protected method
	 * fireTreeStructureChanged where the child index array and the children array
	 * are left empty.
	 * Wouter: be careful with calling this, 
	 * The GUI below the source point will be collapsed
	 * 
	 * @param source the source that triggered the change.
	 * @param path a TreePath object that identifies the path to the parent of the modified item(s)
	 */
	public void treeStructureChanged(Object source, Object[] path) {
		fireTreeStructureChanged(source, path, new int[0], new Object[0]);
	}
	
	
	/**
	 * Wouter: added to handle change of values without change of tree structure.
	 * @param source the source that triggered the change.
	 * @param path path a TreePath object that identifies the path to the parent of the modified item(s)
	 */
	public void treeNodesChanged(Object source, Object[] path) {
		fireTreeNodesChanged(source, path, new int[0], new Object[0]);
	}
}
