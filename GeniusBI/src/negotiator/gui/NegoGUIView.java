/*
 * NegoGUIView.java
 */

package negotiator.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import negotiator.Domain;
import negotiator.gui.agentrepository.AgentRepositoryUI;
import negotiator.gui.domainrepository.DomainRepositoryUI;
import negotiator.gui.domainrepository.MyTreeNode;
import negotiator.gui.negosession.NegoSessionUI2;
import negotiator.gui.tab.CloseListener;
import negotiator.gui.tab.CloseTabbedPane;
import negotiator.gui.tournamentvars.TournamentUI;
import negotiator.gui.tree.TreeFrame;
import negotiator.issue.Objective;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.RepItem;
import negotiator.utility.UtilitySpace;

import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;

/**
 * The application's main frame.
 */
public class NegoGUIView extends FrameView {
	private static final boolean fTournamentEnabled =true;
	private AgentRepositoryUI agentRep = null;
	private DomainRepositoryUI domainRep = null;
	private NegoGUIComponent activeComponent = null;
    public NegoGUIView(SingleFrameApplication app) {
        super(app);

        initComponents(); 

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start(); 
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        
        //custom
        try{
        	agentRep = new AgentRepositoryUI(tableAgents);
        	domainRep = new DomainRepositoryUI(treeDomains);
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        CloseListener cl = new CloseListener() {

			public void closeOperation(MouseEvent e, int overTabIndex) {
				// TODO Auto-generated method stub
				closeTabbedPane1.remove(overTabIndex);
				
			}
        };
        closeTabbedPane1.addCloseListener(cl);
    }
    
    /**
     * @author W.Pasman
     * @param filename 
     * @return part of filename following the last slash, or full filename if there is no slash.
     */
    public String GetPlainFileName(String filename) {
    	int i=filename.lastIndexOf('/');
    	if (i==-1) return filename;
    	return filename.substring(i+1);
    }
    
    /**
     * @author W.Pasman
     * @param filename
     * @return filename stripped of its extension (the part after the last dot).
     */
    public String StripExtension(String filename) {
    	int i=filename.lastIndexOf('.');
    	if (i==-1) return filename;
    	return filename.substring(0, i);
    }
    
    
    
    public void replaceTab(String title, Component oldComp, Component newComp) {
    	closeTabbedPane1.remove(oldComp);
    	addTab(title, newComp);
    }
    public void addTab(String title, Component comp) {
    	closeTabbedPane1.addTab(title, comp);    	
    	closeTabbedPane1.setSelectedComponent(comp);
    }
    public DomainRepositoryUI getDomainRepositoryUI() {
    	return domainRep;
    }
    @Action
    public void showAboutBox() {
        /*if (aboutBox == null) {
            JFrame mainFrame = NegoGUIApp.getApplication().getMainFrame();
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        NegoGUIApp.getApplication().show(aboutBox);*/
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeDomains = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableAgents = new javax.swing.JTable();
        closeTabbedPane1 = new negotiator.gui.tab.CloseTabbedPane();
        toolBar = new javax.swing.JToolBar();
        openToolBarButton1 = new javax.swing.JButton();
        saveToolBarButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        addToolBarButton = new javax.swing.JButton();
        removeToolBarButton = new javax.swing.JButton();
        editToolBarButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newMenu = new javax.swing.JMenu();
        newSessionMenuItem = new javax.swing.JMenuItem();
        newTournamentMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        newPrefProfileMenuItem = new javax.swing.JMenuItem();
        newDomainMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(300, 300));
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        treeDomains.setMinimumSize(new java.awt.Dimension(100, 100));
        treeDomains.setName("treeDomains"); // NOI18N
        treeDomains.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeDomainsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(treeDomains);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getResourceMap(NegoGUIView.class);
        jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableAgents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableAgents.setMinimumSize(new java.awt.Dimension(100, 100));
        tableAgents.setName("tableAgents"); // NOI18N
        jScrollPane1.setViewportView(tableAgents);

        jTabbedPane2.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setLeftComponent(jSplitPane2);

        closeTabbedPane1.setName("closeTabbedPane1"); // NOI18N
        jSplitPane1.setRightComponent(closeTabbedPane1);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getActionMap(NegoGUIView.class, this);
        openToolBarButton1.setAction(actionMap.get("opnFileAction")); // NOI18N
        openToolBarButton1.setFocusable(false);
        openToolBarButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openToolBarButton1.setName("openToolBarButton1"); // NOI18N
        openToolBarButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(openToolBarButton1);

        saveToolBarButton.setAction(actionMap.get("saveFileAction")); // NOI18N
        saveToolBarButton.setIcon(resourceMap.getIcon("saveToolBarButton.icon")); // NOI18N
        saveToolBarButton.setToolTipText(resourceMap.getString("saveToolBarButton.toolTipText")); // NOI18N
        saveToolBarButton.setFocusable(false);
        saveToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveToolBarButton.setName("saveToolBarButton"); // NOI18N
        saveToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(saveToolBarButton);

        jSeparator3.setName("jSeparator3"); // NOI18N
        toolBar.add(jSeparator3);

        addToolBarButton.setAction(actionMap.get("addButtonAction")); // NOI18N
        addToolBarButton.setFont(resourceMap.getFont("addToolBarButton.font")); // NOI18N
        addToolBarButton.setText(resourceMap.getString("addToolBarButton.text")); // NOI18N
        addToolBarButton.setFocusable(false);
        addToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addToolBarButton.setName("addToolBarButton"); // NOI18N
        addToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addToolBarButton);

        removeToolBarButton.setAction(actionMap.get("removeButtonAction")); // NOI18N
        removeToolBarButton.setFont(resourceMap.getFont("removeToolBarButton.font")); // NOI18N
        removeToolBarButton.setFocusable(false);
        removeToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeToolBarButton.setName("removeToolBarButton"); // NOI18N
        removeToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(removeToolBarButton);

        editToolBarButton.setAction(actionMap.get("editAction")); // NOI18N
        editToolBarButton.setFocusable(false);
        editToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editToolBarButton.setName("editToolBarButton"); // NOI18N
        editToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(editToolBarButton);

        jSeparator2.setName("jSeparator2"); // NOI18N
        toolBar.add(jSeparator2);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .add(45, 45, 45)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
            .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mainPanelLayout.createSequentialGroup()
                    .add(toolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(666, Short.MAX_VALUE)))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newMenu.setText(resourceMap.getString("newMenu.text")); // NOI18N
        newMenu.setName("newMenu"); // NOI18N

        newSessionMenuItem.setAction(actionMap.get("newNegoSession")); // NOI18N
        newSessionMenuItem.setName("newSessionMenuItem"); // NOI18N
        newMenu.add(newSessionMenuItem);

        newTournamentMenuItem.setAction(actionMap.get("newTournamentAction")); // NOI18N
        newTournamentMenuItem.setName("newTournamentMenuItem"); // NOI18N
        newMenu.add(newTournamentMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        newMenu.add(jSeparator1);

        newPrefProfileMenuItem.setAction(actionMap.get("newPrefProfile")); // NOI18N
        newPrefProfileMenuItem.setText(resourceMap.getString("newPrefProfileMenuItem.text")); // NOI18N
        newPrefProfileMenuItem.setName("newPrefProfileMenuItem"); // NOI18N
        newMenu.add(newPrefProfileMenuItem);

        newDomainMenuItem.setAction(actionMap.get("newDomain")); // NOI18N
        newDomainMenuItem.setText(resourceMap.getString("newDomainMenuItem.text")); // NOI18N
        newDomainMenuItem.setName("newDomainMenuItem"); // NOI18N
        newMenu.add(newDomainMenuItem);

        fileMenu.add(newMenu);

        openMenuItem.setText(resourceMap.getString("openMenuItem.text")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        fileMenu.add(openMenuItem);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setText("Save");
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 431, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void treeDomainsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeDomainsMouseClicked
// TODO add your handling code here:
         int selRow = treeDomains.getRowForLocation(evt.getX(), evt.getY());
         TreePath selPath = treeDomains.getPathForLocation(evt.getX(), evt.getY());
         if(selRow != -1) {
             if(evt.getClickCount() == 1) {

             }
             else if(evt.getClickCount() == 2) {
                if(selPath!=null) {
                    TreeFrame tf;
                    MyTreeNode node = (MyTreeNode)(selPath.getLastPathComponent());
                    RepItem repItem = node.getRepositoryItem();
                    if(repItem instanceof DomainRepItem) {
                        try {
                        	String filename=((DomainRepItem) repItem).getURL().getFile();
                            Domain domain = new Domain( filename);
                            tf = new TreeFrame(domain);
                            addTab(StripExtension(GetPlainFileName(filename)), tf);
                        } catch (Exception e) {
                            e.printStackTrace();
                        
                        }                        
                    } else if(repItem instanceof ProfileRepItem) {
                        try {
                        	MyTreeNode parentNode = (MyTreeNode)(node.getParent());
                        	String utilityFilename=((ProfileRepItem) repItem).getURL().getFile();
                        	
                        	DomainRepItem domainRepItem=(DomainRepItem) parentNode.getRepositoryItem();
                        	String domainFilename=domainRepItem.getURL().getFile();
                            Domain domain = new Domain( domainFilename );
                            UtilitySpace utilitySpace = new UtilitySpace(domain, utilityFilename);
                       
                            tf = new TreeFrame(domainRepItem,domain,utilitySpace);
                            addTab(StripExtension(GetPlainFileName(utilityFilename)), tf);
                        } catch (Exception e) {
                            e.printStackTrace();
                        
                        }                        
                    }                
                }
             }
         }
    
}//GEN-LAST:event_treeDomainsMouseClicked

    @Action
    public void newNegoSession() {
    	//JFrame frame = new JFrame();
    	try {  
    		NegoSessionUI2 sessionUI = new NegoSessionUI2(this.getFrame());    		
    		addTab("Sess. Editor", sessionUI);
    		setActiveComponent(sessionUI);
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		} 
    }

    @Action
    public void newTournamentAction() {
    	if(fTournamentEnabled) {
    		try {
    			TournamentUI tournamentUI = new TournamentUI();            
    			addTab("Tour."+tournamentUI.getTournament().TournamentNumber+" settings", tournamentUI);
    			setActiveComponent(tournamentUI);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		JOptionPane.showMessageDialog(this.getComponent(), "The tournament functionality is switched off in this version.");
    	}
        
    }
    public void setActiveComponent(NegoGUIComponent comp) {
    	activeComponent = comp;
    }
    
	static int profilenr=1;
    @Action
	public void newPrefProfile() {
    	TreePath selPath = treeDomains.getSelectionPath();
    	if(selPath!=null) {
    		TreeFrame tf;
    		MyTreeNode node = (MyTreeNode)(selPath.getLastPathComponent());
    		RepItem repItem = node.getRepositoryItem();
    		if(repItem instanceof DomainRepItem) {
    			try {
    				DomainRepItem domainRepItem = (DomainRepItem) repItem; 
    				Domain domain = new Domain( domainRepItem.getURL().getFile());
    				tf = new TreeFrame(domainRepItem , domain, new UtilitySpace(domain, ""));
    				addTab("Profile "+profilenr++, tf);
    				setActiveComponent(tf);
    			} catch (Exception e) {
    				e.printStackTrace();

    			}
    			
    		} else {
    			JOptionPane.showMessageDialog(this.getComponent(), "Please, select a domain in the Domains repository");
    		}
    	}else {
			JOptionPane.showMessageDialog(this.getComponent(), "Please, select a domain in the Domains repository");
		}
    }

    static int domainnr=1;
    @Action
    public void newDomain() {
		Objective newRoot = new Objective(null, "root", 0);
		Domain domain = new Domain();
		domain.setObjectivesRoot(newRoot);
    	TreeFrame tf = new TreeFrame(domain);
    	addTab("Domain "+domainnr++, tf);
    }

    @Action
    public void addButtonAction() {
        if(activeComponent!=null) activeComponent.addAction();
    }

    @Action
    public void removeButtonAction() {
        if(activeComponent!=null) activeComponent.removeAction();
    }

    @Action
    public void editAction() {
    	 if(activeComponent!=null) activeComponent.editAction();
    }

    @Action
    public void opnFileAction() {
    }

    @Action
    public void saveFileAction() {
    	 if(activeComponent!=null) activeComponent.saveAction();
    }
    @Action
    public void save() {
    	 if(activeComponent!=null) activeComponent.saveAction();
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToolBarButton;
    private negotiator.gui.tab.CloseTabbedPane closeTabbedPane1;
    private javax.swing.JButton editToolBarButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newDomainMenuItem;
    private javax.swing.JMenu newMenu;
    private javax.swing.JMenuItem newPrefProfileMenuItem;
    private javax.swing.JMenuItem newSessionMenuItem;
    private javax.swing.JMenuItem newTournamentMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JButton openToolBarButton1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton removeToolBarButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton saveToolBarButton;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable tableAgents;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTree treeDomains;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    
    public  CloseTabbedPane getMainTabbedPane() {
    	return closeTabbedPane1;
    }

}
