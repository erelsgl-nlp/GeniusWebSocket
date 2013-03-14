/*
 * NegoSessionUI2.java
 *
 * Created on September 3, 2008, 3:36 PM
 */

package negotiator.gui.negosession;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import negotiator.AgentID;
import negotiator.Global;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.NegoGUIComponent;
import negotiator.gui.progress.ProgressUI2;
import negotiator.gui.tournamentvars.ProfileVarUI;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MediatorRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;

import org.jdesktop.application.Action;

/**
 *
 * @author  dmytro
 */
public class NegoSessionUI2 extends JPanel implements NegoGUIComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean fShowProgressUI = true;
    private ArrayList<ProfileRepItem>[] agentsWorldProfiles=new ArrayList[2];
    
    private JFrame parentFrame = null;
    
    /** Creates new form NegoSessionUI2 */
    public NegoSessionUI2(JFrame parentFrame) { 
    	this.parentFrame = parentFrame;
        initComponents();
        try {
        	initValues();
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
    }

    private void initValues() throws Exception {
    	Repository protocolRep = Repository.getProtocolRepository();
		Repository agent_rep=Repository.get_agent_repository();

		cmbPrefProfileA.removeAllItems();
		cmbPrefProfileB.removeAllItems();
		for (RepItem prof: getProfiles()) {
			cmbPrefProfileA.addItem(new ProfileComboBoxItem((ProfileRepItem)prof));
			cmbPrefProfileB.addItem(new ProfileComboBoxItem((ProfileRepItem)prof));
		}
		// Select the second profile for agent B (so user won't have to change every time)
		cmbPrefProfileB.setSelectedIndex(1);
		
		cmbAgentA.removeAllItems();
		cmbAgentB.removeAllItems();
		for (RepItem agt: agent_rep.getItems()) {
			cmbAgentA.addItem(new AgentComboBoxItem((AgentRepItem)agt));
			cmbAgentB.addItem(new AgentComboBoxItem((AgentRepItem)agt));
		}
		
		cmbProtocol.removeAllItems();
		for(RepItem protocol : protocolRep.getItems()) {
			cmbProtocol.addItem(new ProtocolComboBoxItem((ProtocolRepItem)protocol));
		}
		
        Repository mediatorRep=Repository.get_mediator_repository();
        cmbMediatorName.removeAllItems();
        cmbMediatorName.addItem(new MediatorComboBoxItem(null));
        for(RepItem mediator : mediatorRep.getItems()) {
        	cmbMediatorName.addItem(new MediatorComboBoxItem((MediatorRepItem)mediator));
		}
        
        // TODO: ERAN - REMOVE!!!
        //cmbMediatorName.setSelectedIndex(2);
        
    }
    
	/** TODO use the parameters. */
	public void start() throws Exception {
		ProtocolRepItem protocol = ((ProtocolComboBoxItem)cmbProtocol.getSelectedItem()).protocol;
		if (protocol ==null) throw new NullPointerException("Please select a protocol");
		
		ProfileRepItem[] agentProfiles = new ProfileRepItem[2];
		agentProfiles[0]=((ProfileComboBoxItem)cmbPrefProfileA.getSelectedItem()).profile;
		
		
		agentProfiles[1]=((ProfileComboBoxItem)cmbPrefProfileB.getSelectedItem()).profile;
		for(ProfileRepItem item : agentProfiles)
			if (item ==null) throw new NullPointerException("Please select a profile for agent");
		
		AgentRepItem[] agents = new AgentRepItem[2];
		agents[0] =((AgentComboBoxItem)cmbAgentA.getSelectedItem()).agent;
		
		agents[1] = ((AgentComboBoxItem)cmbAgentB.getSelectedItem()).agent;
		for(AgentRepItem item : agents)
			if (item==null) throw new NullPointerException("Please select agent B");
		
		MediatorRepItem mediator=((MediatorComboBoxItem)cmbMediatorName.getSelectedItem()).mediator;
		
		 // determine the domain
		DomainRepItem domain=agentProfiles[0].getDomain();
		if (domain!=agentProfiles[1].getDomain())
			throw new IllegalArgumentException("profiles for agent A and B do not have the same domain. Please correct your profiles");

		// set the world information of the agents
		for (int i=0;i<agentsWorldProfiles.length;i++) {
			if (agentsWorldProfiles[i]==null)
				agentsWorldProfiles[i]=new ArrayList<ProfileRepItem>();
			else {
				for(ProfileRepItem profile : agentsWorldProfiles[i]) {
					if (domain!=profile.getDomain())
						throw new IllegalArgumentException("some of the profiles for agent A and B worlds do not have the same domain as the agents. Please correct your profiles");
				}
			}
		}
		AgentID[] agentIDs=new AgentID[2];
		agentIDs[0]=new AgentID("Side A");
		agentIDs[1]=new AgentID("Side B");
		ProgressUI2 graphlistener=null;
		if(fShowProgressUI) graphlistener=new ProgressUI2();
		Protocol newSession;
		try {
			newSession = Global.createProtocolInstance(protocol, agents, agentIDs, agentProfiles, null,agentsWorldProfiles, mediator, 0, 0, Global.class.getClassLoader());
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
			throw new Exception("Cannot create protocol.", ex);
		}
		if(fShowProgressUI) {
			NegoGUIApp.negoGUIView.replaceTab("Sess."+newSession.getSessionNumber()+" Prog.", this, graphlistener);		
			newSession.addNegotiationEventListener(graphlistener);
			//graphlistener.setNegotiationSession(ns);
		}
		// java.awt.EventQueue.invokeLater(ns); // this does not work... still deadlock in swing.
		 
		
		newSession.startSession();
	}
    
	public ArrayList<ProfileRepItem> getProfiles() throws Exception
	{
		Repository domainrep=Repository.get_domain_repos();
		ArrayList<ProfileRepItem> profiles=new ArrayList<ProfileRepItem>();
		for (RepItem domain:domainrep.getItems()) {
			if (!(domain instanceof DomainRepItem))
				throw new IllegalStateException("Found a non-DomainRepItem in domain repository:"+domain);
			for (ProfileRepItem profile:((DomainRepItem)domain).getProfiles())	profiles.add(profile);
		}
		return profiles;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbPrefProfileA = new javax.swing.JComboBox();
        cmbAgentA = new javax.swing.JComboBox();
        txtParamsAgentA = new javax.swing.JTextField();
        btnEditWorldAgentA = new javax.swing.JButton();
        btnParamsAgentA = new javax.swing.JButton();
        cmbProtocol = new javax.swing.JComboBox();
        btnStart = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbPrefProfileB = new javax.swing.JComboBox();
        cmbAgentB = new javax.swing.JComboBox();
        txtParamsAgentB = new javax.swing.JTextField();
        btnParamsAgentB = new javax.swing.JButton();
        btnEditWorldAgentB = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtNonGUITimeout = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtGUITimeout = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtGUITimeout1 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cmbMediatorName = new javax.swing.JComboBox();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(NegoGUIApp.class).getContext().getResourceMap(NegoSessionUI2.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        cmbPrefProfileA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPrefProfileA.setName("cmbPrefProfileA"); // NOI18N

        cmbAgentA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAgentA.setName("cmbAgentA"); // NOI18N

        txtParamsAgentA.setEditable(false);
        txtParamsAgentA.setText(resourceMap.getString("txtParamsAgentA.text")); // NOI18N
        txtParamsAgentA.setName("txtParamsAgentA"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(NegoGUIApp.class).getContext().getActionMap(NegoSessionUI2.class, this);
        btnEditWorldAgentA.setAction(actionMap.get("editWorldInformationAgentA")); // NOI18N
        btnEditWorldAgentA.setFont(btnEditWorldAgentA.getFont());
        btnEditWorldAgentA.setText(resourceMap.getString("btnEditWorldAgentA.text")); // NOI18N
        btnEditWorldAgentA.setName("btnEditWorldAgentA"); // NOI18N
        btnEditWorldAgentA.setEnabled(true); // disabled by Yoshi.

        btnParamsAgentA.setText(resourceMap.getString("btnParamsAgentA.text")); // NOI18N
        btnParamsAgentA.setName("btnParamsAgentA"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cmbAgentA, 0, 229, Short.MAX_VALUE)
                            .add(cmbPrefProfileA, 0, 229, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .add(txtParamsAgentA, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(btnParamsAgentA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnEditWorldAgentA))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cmbPrefProfileA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(cmbAgentA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(btnParamsAgentA)
                    .add(txtParamsAgentA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnEditWorldAgentA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cmbProtocol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alternating offers" }));
        cmbProtocol.setName("cmbProtocol"); // NOI18N

        btnStart.setAction(actionMap.get("startSession")); // NOI18N
        btnStart.setText(resourceMap.getString("btnStart.text")); // NOI18N
        btnStart.setName("btnStart"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        cmbPrefProfileB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPrefProfileB.setName("cmbPrefProfileB"); // NOI18N

        cmbAgentB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAgentB.setName("cmbAgentB"); // NOI18N

        txtParamsAgentB.setEditable(false);
        txtParamsAgentB.setText(resourceMap.getString("txtParamsAgentB.text")); // NOI18N
        txtParamsAgentB.setName("txtParamsAgentB"); // NOI18N

        btnParamsAgentB.setText(resourceMap.getString("btnParamsAgentB.text")); // NOI18N
        btnParamsAgentB.setName("btnParamsAgentB"); // NOI18N

        btnEditWorldAgentB.setAction(actionMap.get("editWorldInformationAgentB")); // NOI18N
        btnEditWorldAgentB.setText(resourceMap.getString("btnEditWorldAgentB.text")); // NOI18N
        btnEditWorldAgentB.setName("btnEditWorldAgentB"); // NOI18N
        btnEditWorldAgentB.setEnabled(true); // disabled by Yoshi.

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cmbAgentB, 0, 229, Short.MAX_VALUE)
                            .add(cmbPrefProfileB, 0, 229, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                                .add(txtParamsAgentB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(btnParamsAgentB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnEditWorldAgentB))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(cmbPrefProfileB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(cmbAgentB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(btnParamsAgentB)
                    .add(txtParamsAgentB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnEditWorldAgentB))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel6.border.title"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        txtNonGUITimeout.setText(resourceMap.getString("txtNonGUITimeout.text")); // NOI18N
        txtNonGUITimeout.setName("txtNonGUITimeout"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtGUITimeout.setText(resourceMap.getString("txtGUITimeout.text")); // NOI18N
        txtGUITimeout.setName("txtGUITimeout"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        txtGUITimeout1.setText(resourceMap.getString("txtTurns.text")); // NOI18N
        txtGUITimeout1.setName("txtTurns"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(jLabel15)
                    .add(jLabel16))
                .add(14, 14, 14)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(txtGUITimeout1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtGUITimeout, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtNonGUITimeout, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(txtNonGUITimeout, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(txtGUITimeout, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel16)
                    .add(txtGUITimeout1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        // Yoshi: Don't display the timeouts textboxes, since they are not currnetly used. 
        jPanel6.setVisible(false);
        
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N
        
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        cmbMediatorName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMediatorName.setName("cmbMediatorName"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .add(26, 26, 26)
                .add(cmbMediatorName, 0, 227, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel8)
                .add(cmbMediatorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cmbProtocol, 0, 234, Short.MAX_VALUE)
                        .add(29, 29, 29))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(btnStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cmbProtocol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btnStart))
        );

        jPanel4.getAccessibleContext().setAccessibleName(resourceMap.getString("jPanel4.AccessibleContext.accessibleName")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void startSession() {
    	try {
    		start();
    	} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
    		e.printStackTrace();
		}
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditWorldAgentA;
    private javax.swing.JButton btnEditWorldAgentB;
    private javax.swing.JButton btnParamsAgentA;
    private javax.swing.JButton btnParamsAgentB;
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox cmbAgentA;
    private javax.swing.JComboBox cmbAgentB;
    private javax.swing.JComboBox cmbMediatorName;
    private javax.swing.JComboBox cmbPrefProfileA;
    private javax.swing.JComboBox cmbPrefProfileB;
    private javax.swing.JComboBox cmbProtocol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField txtGUITimeout;
    private javax.swing.JTextField txtGUITimeout1;
    private javax.swing.JTextField txtNonGUITimeout;
    private javax.swing.JTextField txtParamsAgentA;
    private javax.swing.JTextField txtParamsAgentB;
    // End of variables declaration//GEN-END:variables

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
		// TODO Auto-generated method stub
		
	}

    /**
     * Open the dialog to edit agent A's world information
     * For now it opens a ProfileVarUI and get a list of Profiles
     * If the structure of WorldInformation changes this should be changed too
     * @author Yinon Oshrat
     */
    @Action
    public void editWorldInformationAgentA() {
    	agentsWorldProfiles[0]=editWorldInformation(agentsWorldProfiles[0]);

    }

    /**
     * Open the dialog to edit agent B's world information
     * For now it opens a ProfileVarUI and get a list of Profiles
     * If the structure of WorldInformation changes this should be changed too
     * @author Yinon Oshrat
     */
    @Action
    public void editWorldInformationAgentB() {
    	agentsWorldProfiles[1]=editWorldInformation(agentsWorldProfiles[1]);
    }

    /**
     * Show the dialog to decide on world information and update the results
     * @param agentWorldProfiles - the array of old selected profiles
     * @author Yinon Oshrat
     */
    private ArrayList<ProfileRepItem> editWorldInformation(ArrayList<ProfileRepItem> agentWorldProfiles) {
            ArrayList<ProfileRepItem> res=null;
            try {
                    if (agentWorldProfiles==null)
                            agentWorldProfiles=new ArrayList<ProfileRepItem>(); // so we won't crash in the next stage
                    res = (ArrayList<ProfileRepItem>)(
                    		new ProfileVarUI(parentFrame, agentWorldProfiles)
                    ).getResult();
            } catch (Exception e) {
                    e.printStackTrace();
            }
            if (res==null)
                    return new ArrayList<ProfileRepItem>();
            else
                    return res;

	

    }
    
    /**
     * demo program
     */
    public static void main(String[] args) {
    	JDialog testDialog = new JDialog((Frame)null, true);
    	testDialog.setTitle("Grammar Dialog");
    	testDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	
    	testDialog.add(new NegoSessionUI2(null));
    	testDialog.pack();
    	testDialog.setVisible(true);
   	
    	testDialog.setVisible(true);
    }
}
/** this is to override the toString of an AgentRepItem, to show only the short name. */
class AgentComboBoxItem {
	public AgentRepItem agent;
	public AgentComboBoxItem(AgentRepItem a) {agent=a; } 
	@Override
	public String toString() { return agent.getName(); }
}

/** this is to override the toString of an ProfileRepItem, to show only the short name. */
class ProfileComboBoxItem {
	public ProfileRepItem profile;
	public ProfileComboBoxItem(ProfileRepItem p) {profile=p; } 
	@Override
	public String toString() { return profile.getURL().getFile(); }
}

class ProtocolComboBoxItem {
	public ProtocolRepItem protocol;
	public ProtocolComboBoxItem(ProtocolRepItem p) {protocol=p; } 
	@Override
	public String toString() { return protocol.getName(); }
}

class MediatorComboBoxItem {
	public MediatorRepItem mediator;
	public MediatorComboBoxItem(MediatorRepItem m) {mediator=m; } 
	@Override
	public String toString() {
		if (mediator==null)
			return "None";
		return mediator.getName(); 
	}
}