package negotiator.gui.tree;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.text.Format;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import jtreetable.*;
import negotiator.*;
import negotiator.repository.DomainRepItem;
import negotiator.utility.*;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.NegoGUIComponent;
import negotiator.gui.tree.actions.*;
import negotiator.gui.tree.UtilityGeneralFieldsPanel;
import negotiator.issue.*;

/**
*
* @author Richard Noorlandt
* 
*/


/**
 * Wouter: JTreeTable probably has been downloaded from
 * http://java.sun.com/products/jfc/tsc/articles/treetable1/
 * 
 * "To compile and run the example program provided with this article, 
 * you must use Swing 1.1 Beta 2 or a compatible Swing release."
 * 
 */

public class TreeFrame extends JPanel implements NegoGUIComponent{
	
	//Attributes
	private static final Color UNSELECTED = Color.WHITE;
	private static final Color HIGHLIGHT = Color.YELLOW;
	
	private JTreeTable treeTable;
	private NegotiatorTreeTableModel model;
	
	private SelectedInfoPanel infoPanel;
	
	private AddObjectiveAction addObjectiveAct;
	private AddIssueAction addIssueAct;
	//private CutAction cutAct;
	//private PasteAction pasteAct;
	private DeleteAction delAct;
	private EditAction editAct;
	private NewDomainAction newDomainAct;
	private NewUtilitySpaceAction newUtilitySpaceAct;
	private LoadDomainAction loadDomainAct;
	private LoadUtilitySpaceAction loadUtilitySpaceAct;
	private SaveDomainAction saveDomainAct;
	private SaveUtilitySpaceAction saveUtilitySpaceAct;
	private ExitAction exitAct;
	
	private static JFileChooser fileChooser;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private DomainRepItem fDomainRepItem;
	private JPopupMenu treePopupMenu;
	
	//Constructors
	public TreeFrame(Domain domain) {
		this(new NegotiatorTreeTableModel(domain));
	}
	
	public TreeFrame(Domain domain, UtilitySpace utilitySpace) {
		this(new NegotiatorTreeTableModel(domain, utilitySpace));
	}

	public TreeFrame(DomainRepItem domainRepItem, Domain domain, UtilitySpace utilitySpace) {		
		this(new NegotiatorTreeTableModel(domain, utilitySpace));
		fDomainRepItem = domainRepItem;
	}

	public TreeFrame(NegotiatorTreeTableModel treeModel) {
		super();
		
		init(treeModel, null);
		final NegoGUIComponent comp = this;
		treeTable.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				NegoGUIApp.negoGUIView.setActiveComponent(comp); 
			}

			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	
	
	public void clearTreeTable(Domain domain) {
		init(new NegotiatorTreeTableModel(domain), this.getSize());
	}
	
	
	public void clearTreeTable(Domain domain, UtilitySpace utilitySpace) {
		init(new NegotiatorTreeTableModel(domain, utilitySpace), this.getSize());
		//treeTable.repaint();
	}
	

	
	private void init(NegotiatorTreeTableModel treeModel, Dimension size) {
		model = treeModel;
		
		//JPanel contentPane = new JPanel();
		setLayout(new BorderLayout());
		//this.setContentPane(contentPane);
		
		//Initialize the table
		initTable(model);
		treeTable.addMouseListener(new TreePopupListener());
		treeTable.getSelectionModel().addListSelectionListener(new TreeSelectionListener());
		
		treeTable.getTree().addTreeWillExpandListener(new TestListener());
		
		//Initialize the FileChooser
		initFileChooser();
		
		initActions();
		
		//Initialize the Menu
		initMenus();
		initPopupMenus();
		
		JPanel controlPanel = new JPanel();
		
		//Initialise the Panel with buttons.
		JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createTitledBorder("Edit nodes"));
		BoxLayout boxLayout = new BoxLayout(controls, BoxLayout.PAGE_AXIS);
		
		controls.setLayout(boxLayout);
		//controls.add(new JButton(addAct));
		controls.add(new JButton(addObjectiveAct));
		controls.add(new JButton(addIssueAct));
		//controls.add(new JButton(cutAct));
		//controls.add(new JButton(pasteAct));
		controls.add(new JButton(delAct));
		controls.add(new JButton(editAct));
		controlPanel.add(controls);
		//this.getContentPane().add(controls, BorderLayout.PAGE_END);
		
		//Initialize the InfoPanel
		infoPanel = new SelectedInfoPanel();
		controlPanel.add(infoPanel);
		//Initialize the General Fields for the Utility Values
		if (model.getUtilitySpace()!=null) {
			UtilityGeneralFieldsPanel generalFieldsPanel=new UtilityGeneralFieldsPanel(model.getUtilitySpace());
			controlPanel.add(generalFieldsPanel);
		}
		add(controlPanel, BorderLayout.PAGE_END);
		
		
		
		//Do nothing on closing, since we might need different behaviour.
		//See negotiator.gui.tree.actions.ExitAction
		//this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); 
//		this.pack();
//		this.setVisible(true);
		
		if (size != null)
			this.setSize(size);
		
	}
	
	private void initTable(NegotiatorTreeTableModel model) {
		treeTable = new JTreeTable(model);
		treeTable.setPreferredSize(new Dimension(1024, 600));
		treeTable.setPreferredScrollableViewportSize(new Dimension(1024, 600));
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		treeTable.setRowSelectionAllowed(true);
		treeTable.setColumnSelectionAllowed(false);
		treeTable.setCellSelectionEnabled(true);
		
		TableColumnModel colModel = treeTable.getColumnModel();
		if (treeTable.getColumnCount()>3) colModel.getColumn(3).setMinWidth(220); //Wouter: make it likely that Weight column is shown completely.
		//colModel.getColumn(1).setMaxWidth(80);
		//colModel.getColumn(2).setMaxWidth(120);
		
		//NegotiatorTreeTableCellRenderer treeRenderer = new NegotiatorTreeTableCellRenderer(treeTable);
		//treeRenderer.setRowHeight(18);
		//treeTable.setDefaultRenderer(TreeTableModel.class, treeRenderer);
		//NegotiatorTreeCellRenderer treeRenderer = new NegotiatorTreeCellRenderer();
		//treeRenderer.setBackgroundSelectionColor(Color.RED);
		//treeRenderer.setBackgroundNonSelectionColor(Color.CYAN);
		//treeTable.getTree().setCellRenderer(treeRenderer);
		
		DefaultTableCellRenderer labelRenderer = new JLabelCellRenderer();
		treeTable.setDefaultRenderer(JLabel.class, labelRenderer);
		treeTable.setDefaultRenderer(JTextField.class, labelRenderer);
		
		IssueValueCellEditor valueEditor = new IssueValueCellEditor(model);
		treeTable.setDefaultRenderer(IssueValuePanel.class, valueEditor);
		treeTable.setDefaultEditor(IssueValuePanel.class, valueEditor);
		
		WeightSliderCellEditor cellEditor = new WeightSliderCellEditor(model);
		treeTable.setDefaultRenderer(WeightSlider.class, cellEditor);
		treeTable.setDefaultEditor(WeightSlider.class, cellEditor);
		//treeTable.getColumnModel().getColumn(4).setPreferredWidth(new WeightSlider(model).getPreferredSize().width);
		//treeTable.setRowHeight(new WeightSlider(model, null).getPreferredSize().height);
		treeTable.setRowHeight(24);
		//treeTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(slider));
		
		JScrollPane treePane = new JScrollPane(treeTable);
		treePane.setBackground(treeTable.getBackground());
		add(treePane, BorderLayout.CENTER);
	}
	
	/**
	 * Recreates the Actions. Note that it doesn't reinitialise the Buttons that are dependent on it!
	 * The caller is responsible for this.
	 */
	private void initActions() {
		//Create Actions
		addObjectiveAct = new AddObjectiveAction(this, treeTable);
		addIssueAct = new AddIssueAction(this, treeTable);
		//cutAct = new CutAction(this);
		//pasteAct = new PasteAction(this);
		delAct = new DeleteAction(treeTable);
		editAct = new EditAction(this);
		newDomainAct = new NewDomainAction(this);
		newUtilitySpaceAct = new NewUtilitySpaceAction(this);
		loadDomainAct = new LoadDomainAction(this, fileChooser);
		loadUtilitySpaceAct = new LoadUtilitySpaceAction(this, fileChooser);
		saveDomainAct = new SaveDomainAction(this, fileChooser);
		saveUtilitySpaceAct = new SaveUtilitySpaceAction(this, fileChooser);
		exitAct = new ExitAction(this);
		
		//Disable the actions, since no selection is made yet
		addObjectiveAct.setEnabled(false);
		addIssueAct.setEnabled(false);
		delAct.setEnabled(false);
		editAct.setEnabled(false);
	}
	
	private void initMenus() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		
		fileMenu.add(newDomainAct);
		fileMenu.add(newUtilitySpaceAct);
		fileMenu.addSeparator();
		fileMenu.add(loadDomainAct);
		fileMenu.add(loadUtilitySpaceAct);
		fileMenu.addSeparator();
		fileMenu.add(saveDomainAct);
		fileMenu.add(saveUtilitySpaceAct);
		fileMenu.addSeparator();
		fileMenu.add(exitAct);
		editMenu.add(addObjectiveAct);
		editMenu.add(addIssueAct);
		editMenu.addSeparator();
		//editMenu.add(cutAct);
		//editMenu.add(pasteAct);
		editMenu.add(editAct);
		editMenu.add(delAct);
		
		//this.setJMenuBar(menuBar);
	}
	
	private void initFileChooser() {
		//Don't reset the fileChooser if it's already there, since that would also reset the last
		//opened location.
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	
	private void initPopupMenus() {
		treePopupMenu = new JPopupMenu();
		
		treePopupMenu.add(addObjectiveAct);
		treePopupMenu.add(addIssueAct);
		treePopupMenu.addSeparator();
		//treePopupMenu.add(cutAct);
		//treePopupMenu.add(pasteAct);
		treePopupMenu.add(editAct);
		treePopupMenu.add(delAct);
	}
	
	public JTreeTable getTreeTable() {
		return treeTable;
	}
	
	public NegotiatorTreeTableModel getNegotiatorTreeTableModel() {
		return model;
	}
	
	public void selectionChanged() {
		Object selected = treeTable.getTree().getLastSelectedPathComponent();
		
		if (selected instanceof Issue) {
			addObjectiveAct.setEnabled(false);
			addIssueAct.setEnabled(false);
			//cutAct.setEnabled(true);
			//pasteAct.setEnabled(false);
			editAct.setEnabled(true);
			delAct.setEnabled(true);
			
			//TODO remove
			System.out.println("An Issue");
		}
		else if (selected instanceof Objective) {
			addObjectiveAct.setEnabled(true);
			addIssueAct.setEnabled(true);
			//cutAct.setEnabled(true);
			//pasteAct.setEnabled(false); //TODO check is something is cut first.
			editAct.setEnabled(true);
			delAct.setEnabled(true);
						
			//TODO remove
			System.out.println("An Objective");
		}
		/*else {
			addObjectiveAct.setEnabled(false);
			addIssueAct.setEnabled(false);
			//cutAct.setEnabled(false);
			//pasteAct.setEnabled(false);
			editAct.setEnabled(false);
			delAct.setEnabled(false);
			
			selected = null;
			
			//TODO remove
			System.out.println("Unknown selection");
		}*/
		
		updateHighlights((Objective)selected);
		infoPanel.displayObjective((Objective)selected);
		treeTable.repaint();
	}
	
	protected void updateHighlights(Objective selected) {
		Objective parent = null;
		if (selected != null) {
			parent = selected.getParent();
		}
		Enumeration<Objective> treeEnum = ((Objective)model.getRoot()).getPreorderEnumeration();
		while (treeEnum.hasMoreElements()) {
			Objective obj = treeEnum.nextElement();
			if (selected == null || parent == null) {
				setRowBackground(obj, UNSELECTED);
			}
			else if (parent.isParent(obj)) {
				setRowBackground(obj, HIGHLIGHT);
			}
			else {
				setRowBackground(obj, UNSELECTED);
			}
		}
	}
	
	
	protected void setRowBackground(Objective node, Color color) {
		model.getNameField(node).setBackground(color);
		model.getTypeField(node).setBackground(color);
		model.getNumberField(node).setBackground(color);
		model.getIssueValuePanel(node).setBackground(color);
		//model.getWeightSlider(node).setBackground(color);
	}
	
	class TreePopupListener extends MouseAdapter {
		
		//Methods
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		} 
		
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				Point point = new Point(e.getX(), e.getY());
				int rowIndex = treeTable.rowAtPoint(point);
				if (rowIndex != -1) {
					treeTable.setRowSelectionInterval(rowIndex, rowIndex);
				}				
				treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		} 
	}
	
	class TreeSelectionListener implements ListSelectionListener {
		
		//Methods
		public void valueChanged(ListSelectionEvent e) {
			selectionChanged();
		}
		
	}

	//TODO: RANZIGHEID, TEST!
	class TestListener implements TreeWillExpandListener {

		public TestListener() {
			
		}

        // Required by TreeExpansionListener interface.
        public void treeWillExpand(TreeExpansionEvent e) {
            System.out.println("blaat");
        }

        // Required by TreeExpansionListener interface.
        public void treeWillCollapse(TreeExpansionEvent e) {
        	System.out.println("blaat");
        }
    }

	public void addAction() {
		// TODO Auto-generated method stub
		
	}

	public void editAction() {
		// TODO Auto-generated method stub
		
	}

	public JButton[] getButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAction() {
		// TODO Auto-generated method stub
		
	}

	public void saveAction() {
		if(model.getUtilitySpace()==null)
			saveDomainAct.actionPerformed(null);
		else
			saveUtilitySpaceAct.actionPerformed(null);
	}
	public DomainRepItem getDomainRepItem() {
		return fDomainRepItem;
	}
}
