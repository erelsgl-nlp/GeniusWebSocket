/*
 * TournamentUI.java
 *
 * Created on September 5, 2008, 10:05 AM
 */

package negotiator.gui.tournamentvars;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import negotiator.AgentParam;

import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.NegoGUIComponent;
import negotiator.gui.progress.ProgressUI2;
import negotiator.gui.progress.TournamentProgressUI2;
import negotiator.repository.AgentRepItem;
import negotiator.repository.MediatorRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.Repository;
import negotiator.tournament.*;
import negotiator.tournament.VariablesAndValues.*;

import org.jdesktop.application.Action;

/**
 *
 * @author  dmytro
 */
public class TournamentUI extends javax.swing.JPanel implements NegoGUIComponent {

	
	Tournament tournament; // this contains the variables and their possible values.
	AbstractTableModel dataModel;

	Repository domainrepository; // contains all available domains and profiles to pick from.
	Repository agentrepository; // contains all available  agents to pick from.
	Repository mediatorrepository; // contains all available  agents to pick from.

    /** Creates new form TournamentUI */
    public TournamentUI() {
        initComponents();
		//Tournament t=new TournamentTwoPhaseAuction(); // bit stupid to correct an empty one, but will be useful later.
        Tournament t=new Tournament(); 
		correct_tournament(t);
		
		tournament=t;
		try {
			domainrepository=Repository.get_domain_repos();
			agentrepository=Repository.get_agent_repository();
			mediatorrepository=Repository.get_mediator_repository();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dataModel = new AbstractTableModel() {
			final String columnnames[] = {"Variable","Values"};
			
			public int getColumnCount() { 
				return columnnames.length; 
			}
			
			public int getRowCount() { 
				return tournament.getVariables().size();
			}
			
			public Object getValueAt(int row, int col) {
				TournamentVariable var=tournament.getVariables().get(row);
			  	switch(col)
			  	{
			  	case 0: {
			  		String res =var.varToString();
			  		return res;			  	
			  	}
			  	case 1:return var.getValues().toString();
			  	default: new Warning("Illegal column in table "+col);
			  	}
			  	return col;
			}
			public String getColumnName(int column) {
			  	  return columnnames[column];
			}
		};
		jTable1.setModel(dataModel);
		jTable1.getColumnModel().getColumn(0).setMaxWidth(150);
        //jTable1.getColumnModel().getColumn(0).setWidth(150);
		jTable1.getColumnModel().getColumn(0).setMinWidth(140);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(150);
        
        
    }

	/**********************button functionality************************/

	void editVariable(TournamentVariable v) throws Exception {
		 // numerous classes here result in highly duplicate code and pretty unreadable code as well.....
		 // IMHO the strong typechecking gives maybe even more problems than it resolves...
		if (v instanceof ProfileVariable) { 
			ArrayList<ProfileRepItem> oldv=new ArrayList<ProfileRepItem>();
			for (TournamentValue tv:v.getValues()) oldv.add( ((ProfileValue)tv).getProfile() );
			ArrayList<ProfileRepItem> newv=(ArrayList<ProfileRepItem>)new ProfileVarUI(NegoGUIApp.negoGUIView.getFrame(),oldv).getResult();
			System.out.println("result new vars="+newv);
			if (newv==null) return; // cancel pressed.
			 // make profilevalues for each selected profile and add to the set.
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
			for (ProfileRepItem profitem: newv) newtvs.add(new ProfileValue(profitem));
			v.setValues(newtvs);
		}else if(v instanceof ProtocolVariable) {
			ArrayList<ProtocolRepItem> newv=(ArrayList<ProtocolRepItem>)new ProtocolVarUI(NegoGUIApp.negoGUIView.getFrame()).getResult();//(AgentVariable)v);
			System.out.println("result new vars="+newv);
			if (newv==null) return; // cancel pressed.
			// make agentvalues for each selected agent and add to the agentvariable
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
			for (ProtocolRepItem protocolItem: newv) newtvs.add(new ProtocolValue(protocolItem));
			v.setValues(newtvs);			
		}
		else if (v instanceof AgentVariable) {
			ArrayList<AgentRepItem> newv=(ArrayList<AgentRepItem>)new AgentVarUI(NegoGUIApp.negoGUIView.getFrame()).getResult();//(AgentVariable)v);
			System.out.println("result new vars="+newv);
			if (newv==null) return; // cancel pressed.
			// make agentvalues for each selected agent and add to the agentvariable
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
			for (AgentRepItem agtitem: newv) newtvs.add(new AgentValue(agtitem));
			v.setValues(newtvs);
		}
		else if (v instanceof MediatorVariable) {
				ArrayList<MediatorRepItem> newv=(ArrayList<MediatorRepItem>)new MediatorVarUI(NegoGUIApp.negoGUIView.getFrame()).getResult();
				System.out.println("result new vars="+newv);
				if (newv==null) return; // cancel pressed.
				// make mediatorvalues for each selected mediator and add to the mediatorvariable
				ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
				for (MediatorRepItem meditem: newv) newtvs.add(new MediatorValue(meditem));
				v.setValues(newtvs);
		} else if(v instanceof TotalSessionNumberVariable) {
			TotalSessionNumberValue value =	(TotalSessionNumberValue)(new SingleValueVarUI(NegoGUIApp.negoGUIView.getFrame())).getResult();
			if(value==null) return;
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>();
			newtvs.add(value);
			v.setValues(newtvs);
		}		
		else if (v instanceof AgentParameterVariable) {			
			ArrayList<TournamentValue> newvalues=null;
			String newvaluestr=new String(""+v.getValues()); // get old list, using ArrayList.toString.
			 // remove the [ and ] that ArrayList will add
			newvaluestr=newvaluestr.substring(1, newvaluestr.length()-1);
			double minimum=((AgentParameterVariable)v).getAgentParam().min;
			double maximum=((AgentParameterVariable)v).getAgentParam().max;
			
			// repeat asking the numbers until cancel or correct list was entered.
			boolean error_occured;
			do {
				error_occured=false;
				try {
					newvaluestr=(String)new ParameterValueUI(NegoGUIApp.negoGUIView.getFrame(),""+v,newvaluestr).getResult();
					if (newvaluestr==null) break;
					//System.out.println("new value="+newvaluestr);
					String[] newstrings=newvaluestr.split(",");
					newvalues=new ArrayList<TournamentValue>();
					for (int i=0; i<newstrings.length; i++) {
						Double val=Double.valueOf(newstrings[i]);
						if ( val < minimum)
							throw new IllegalArgumentException("value "+val+" is smaller than minimum "+minimum);
						if ( val > maximum)
							throw new IllegalArgumentException("value "+val+" is larger than maximum "+maximum);
						newvalues.add(new AgentParamValue(Double.valueOf(newstrings[i])));
					}
					v.setValues(newvalues);
				} catch (Exception err) { error_occured=true; new Warning("your numbers are not accepted: "+err); }
			}
			while (error_occured);
		}
		else throw new IllegalArgumentException("Unknown tournament variable "+v);		
	}
	

	
	
	/** remove selected row from table */
	void removerow() throws Exception {
		int row=checkParameterSelected("You can not remove the Profile and Agent vars.");
		tournament.getVariables().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
	}
	
	void addrow() throws Exception {
		System.out.println("add row "+jTable1.getSelectedRow());
		// get all available parameters of all available agents
		HashSet<AgentParam> params=new HashSet<AgentParam>();
		 // Assumption: 2 and 3 in the list are the AgentVars. This is checked in allparams()
		params.addAll(allparams(tournament.getVariables().get(2).getValues())); //TODO : define constants for the AgentVar
		params.addAll(allparams(tournament.getVariables().get(3).getValues()));
		//System.out.println("available parameters:"+params);
		 // launch editor for a variable.
		ArrayList<AgentParam> paramsAsArray=new ArrayList<AgentParam>();
		paramsAsArray.addAll(params);
		AgentParam result=(AgentParam)new ParameterVarUI(NegoGUIApp.negoGUIView.getFrame(),paramsAsArray).getResult();
		//System.out.println("result="+result);
		if (result==null) return; // cancel, error, whatever.
		tournament.getVariables().add(new AgentParameterVariable(result));
		int row=tournament.getVariables().size();
		dataModel.fireTableRowsInserted(row,row);
	}
	
	void up() throws Exception {
		int row=checkParameterSelected("You can not move Profile and Agent vars");
		if (row==3) throw new IllegalArgumentException("You can not move Profile and Agent vars"); // you can not move the highest one up.
		 // swap row with row-1
		ArrayList<TournamentVariable> vars=tournament.getVariables();
		TournamentVariable tmp=vars.get(row);
		vars.set(row, vars.get(row-1));
		vars.set(row-1,tmp);
		dataModel.fireTableRowsUpdated(row-1, row);
	}
	
	void down() throws Exception {
		int row=checkParameterSelected("You can not move Profile and Agent vars");
		ArrayList<TournamentVariable> vars=tournament.getVariables();
		if (row==vars.size()-1) return; // you can not move the last one down.
		 // swap row with row+1
		TournamentVariable tmp=vars.get(row);
		vars.set(row, vars.get(row+1));
		vars.set(row+1,tmp);
		dataModel.fireTableRowsUpdated(row, row+1);
	}
	
	
	/** returns selected parameter row number, or throws if not.
	 * The throw error message is "Please select a Parameter to be moved."+detailerrormessage. */
	int checkParameterSelected(String detailerrormessage) throws Exception {
		int row=jTable1.getSelectedRow();
		if (row<=2 || row>tournament.getVariables().size())
			throw new IllegalArgumentException("Please select a Parameter to be moved. "+detailerrormessage);
		return row;
	}
	
	 /** returns all parameters of given agent. 
	  * agent is referred to via the values of the agentA and agentB parameters set in the tournament
	  * @param v an ArrayList of AgentValues.
	  * @return
	  */
	ArrayList<AgentParam> allparams(ArrayList<TournamentValue> values) throws Exception {
		ArrayList<AgentParam> params=new ArrayList<AgentParam>();
		for (TournamentValue v: values) {
			if (!(v instanceof AgentValue)) 
				throw new IllegalArgumentException("Expected AgentValue but found "+v);
			AgentRepItem agentinrep=((AgentRepItem)((AgentValue)v).getValue());
			Object result=agentinrep.callStaticAgentFunction("getParameters", new Object[0]);
			if (!(result instanceof ArrayList ))
				throw new Exception("Agent "+agentinrep+" did not return an ArrayList as result to calling getParameters!");
			params.addAll((ArrayList<AgentParam>) result);
		}
		return params;
	}
	
	/** start negotiation.
	 * Run it in different thread, so that we can return control to AWT/Swing
	 * That is important to avoid deadlocks in case any negosession wants to open a frame.
	 */
	void start() throws Exception {
		

		ProgressUI2 progressUI = new ProgressUI2();
		TournamentProgressUI2 tournamentProgressUI=new TournamentProgressUI2(progressUI );
		NegoGUIApp.negoGUIView.replaceTab("Tour."+tournament.TournamentNumber+" Progress", this, tournamentProgressUI);
		 
		//new Thread(new TournamentRunnerTwoPhaseAutction (tournament,tournamentProgressUI)).start();
		Thread t = new Thread(new TournamentExecutor(tournament,tournamentProgressUI));
		t.setName("TournamentExecutor");
		t.start();
		
	}
	
	
	
	/***************************CODE FOR RUNNING DEMO AND LOADING & CORRECTING EXAMPLE ********************/
	/** make sure first three rows are Profile, AgentA, AgentB */
	static void correct_tournament(Tournament t)
	{
		ArrayList<TournamentVariable> vars=t.getVariables();
		correctposition(vars,Tournament.VARIABLE_PROTOCOL,new ProtocolVariable());		
		correctposition(vars,Tournament.VARIABLE_PROFILE,new ProfileVariable());
		AgentVariable agentVar = new AgentVariable();
		agentVar.setSide("A");
		correctposition(vars,Tournament.VARIABLE_AGENT_A,agentVar);
		agentVar = new AgentVariable();
		agentVar.setSide("B");
		correctposition(vars,Tournament.VARIABLE_AGENT_B,agentVar);
		correctposition(vars,Tournament.VARIABLE_NUMBER_OF_RUNS, new TotalSessionNumberVariable());
		correctposition(vars,Tournament.VARIABLE_MEDIATOR, new MediatorVariable());
	}

	/** check that variable of type given in stub is at expected position.
	 * If not, move the first occurence after *that position* to the given position
	 * Or create new instance of that type if there is none.
	 * @param vars is array of TournamentVariables.
	 * @param pos expected position
	 * @param stub: TournamentVariable of the expected type, which is substituted if no object of required type is in the array at all.
	 */
	static void correctposition(ArrayList<TournamentVariable> vars, int expectedpos, TournamentVariable stub) {
		// find the profile variable(s) and its position. Remove multiple occurences.
		TournamentVariable v=null;
		int pos=-1;
		for (int i=expectedpos; i<vars.size(); i++) {
			if (vars.get(i).getClass().equals(stub.getClass())) {
				if (v==null) {
					pos=i; v=vars.get(i);
				} else {
					new Warning("tournament contains multiple "+stub.getClass()+" variables. Removing all but the first one.");
					vars.remove(i);
					i--; // re-check this index
				}
			}
		}
	
		if (pos!=expectedpos) {
			// incorrect profile
			if (v==null) {
				vars.add(expectedpos,stub);
			} else {
				new Warning("tournament has "+stub.getClass()+" variable not on expected place. Moving it to correct position.");
				vars.remove(pos);
				vars.add(expectedpos, v);
			}
		}
	}
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnStart = new javax.swing.JButton();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getResourceMap(TournamentUI.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getActionMap(TournamentUI.class, this);
        btnStart.setAction(actionMap.get("startTournament")); // NOI18N
        btnStart.setText(resourceMap.getString("btnStart.text")); // NOI18N
        btnStart.setName("btnStart"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .add(btnStart))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(btnStart)
                .addContainerGap())
        );

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

private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
// TODO add your handling code here:
    if(evt.getClickCount()>1) {
    	 TournamentVariable tv = tournament.getVariables().get(jTable1.getSelectedRow());
    	 try {
			editVariable(tv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}//GEN-LAST:event_jTable1MouseClicked

    @Action
    public void startTournament() {
    	try {
    		start();
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
    		
		}
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

	public void addAction() {
		// TODO Auto-generated method stub
		try {
			addrow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		try {
			removerow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveAction() {
		// TODO Auto-generated method stub
		
	}
	
	public Tournament getTournament() { 
		return tournament;
	}

}
