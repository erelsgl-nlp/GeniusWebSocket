package negotiator.gui.tournamentvars;


import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.Dimension;

import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEndedEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.repository.*;

import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentExecutor;
import negotiator.tournament.VariablesAndValues.*;
import negotiator.NegotiationEventListener;
import negotiator.AgentParam;

public class TournamentVarsUI extends JFrame {
	
	Tournament tournament; // this contains the variables and their possible values.
	AbstractTableModel dataModel;
	final JTable table;
	Repository domainrepository; // contains all available domains and profiles to pick from.
	Repository agentrepository; // contains all available  agents to pick from.
	
	JButton addvarbutton=new JButton("Add Parameter");
	JButton removevarbutton=new JButton("Remove Parameter");
	JButton editvarbutton=new JButton("Edit Variable");
	JButton upbutton=new JButton("Up");
	JButton downbutton=new JButton("Down");
	JButton startbutton=new JButton("Start");
	JCheckBox enableLoggingBox=new JCheckBox("Logging&Graph",true);
	
	public TournamentVarsUI() throws Exception {
		Tournament t=new Tournament(); // bit stupid to correct an empty one, but will be useful later.
		correct_tournament(t);
		
		tournament=t;
		domainrepository=Repository.get_domain_repos();
		agentrepository=Repository.get_agent_repository();
		setTitle("Tournament Editor");

		
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
			  	case 0:return var.varToString();
			  	case 1:return var.getValues().toString();
			  	default: new Warning("Illegal column in table "+col);
			  	}
			  	return col;
			}
			public String getColumnName(int column) {
			  	  return columnnames[column];
			}
		};
		table = new JTable(dataModel);
		table.setShowGrid(true);
		JScrollPane scrollpane = new JScrollPane(table);
	 	
	      // CREATE THE BUTTONS
		
		upbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try {up();}
				catch (Exception err) { new Warning("up failed:"+err); }
			}
		});
		downbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try {down();}
				catch (Exception err) { new Warning("down failed:"+err); }
			}
		});
		addvarbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try {addrow();}
				catch (Exception err) { new Warning("add failed:"+err); }
			}
		});
		removevarbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removerow();}
				catch (Exception err) { new Warning("remove failed: "+err); }
				}
		});		
		editvarbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { 
					int row=table.getSelectedRow();
					editVariable(tournament.getVariables().get(row)); 
					dataModel.fireTableRowsUpdated(row,row);
				}
				catch (Exception err) { new Warning("edit failed: "+err); }
			}
		});
		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { start();}
				catch (Exception err) { new Warning("start failed: "+err); }
				}
		});	

		//setLayout(new BorderLayout());
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));

		JPanel buttons=new JPanel();
		buttons.setLayout(new BorderLayout());
		
		JPanel updownpanel=new JPanel(new BorderLayout());
		updownpanel.add(upbutton,BorderLayout.NORTH);
		updownpanel.add(downbutton,BorderLayout.SOUTH);
		
		JPanel addremovepanel=new JPanel(new BorderLayout());
		addremovepanel.add(addvarbutton,BorderLayout.NORTH);
		addremovepanel.add(removevarbutton,BorderLayout.SOUTH);
		
		buttons.add(updownpanel,BorderLayout.WEST);
		buttons.add(editvarbutton,BorderLayout.CENTER);
		buttons.add(addremovepanel,BorderLayout.EAST);
		buttons.add(enableLoggingBox,BorderLayout.SOUTH);

		add(scrollpane); add(buttons); add(startbutton);
		//setPreferredSize(new Dimension(300,300));
		pack();
		setVisible(true);
	}
	
	/**********************button functionality************************/

	void editVariable(TournamentVariable v) throws Exception {
		 // numerous classes here result in highly duplicate code and pretty unreadable code as well.....
		 // IMHO the strong typechecking gives maybe even more problems than it resolves...
		if (v instanceof ProfileVariable) { 
			ArrayList<ProfileRepItem> oldv=new ArrayList<ProfileRepItem>();
			for (TournamentValue tv:v.getValues()) oldv.add( ((ProfileValue)tv).getProfile() );
			ArrayList<ProfileRepItem> newv=(ArrayList<ProfileRepItem>)new ProfileVarUI(this,oldv).getResult();
			System.out.println("result new vars="+newv);
			if (newv==null) return; // cancel pressed.
			 // make profilevalues for each selected profile and add to the set.
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
			for (ProfileRepItem profitem: newv) newtvs.add(new ProfileValue(profitem));
			v.setValues(newtvs);
		} else if(v instanceof ProtocolVariable) {
			
		}
		else if (v instanceof AgentVariable) {
			ArrayList<AgentRepItem> newv=(ArrayList<AgentRepItem>)new AgentVarUI(this).getResult();//(AgentVariable)v);
			System.out.println("result new vars="+newv);
			if (newv==null) return; // cancel pressed.
			// make agentvalues for each selected agent and add to the agentvariable
			ArrayList<TournamentValue> newtvs=new ArrayList<TournamentValue>(); 
			for (AgentRepItem agtitem: newv) newtvs.add(new AgentValue(agtitem));
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
					newvaluestr=(String)new ParameterValueUI(this,""+v,newvaluestr).getResult();
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
		System.out.println("add row "+table.getSelectedRow());
		// get all available parameters of all available agents
		HashSet<AgentParam> params=new HashSet<AgentParam>();
		 // Assumption: 1 and 2 in the list are the AgentVars. This is checked in allparams()
		params.addAll(allparams(tournament.getVariables().get(1).getValues()));
		params.addAll(allparams(tournament.getVariables().get(2).getValues()));
		//System.out.println("available parameters:"+params);
		 // launch editor for a variable.
		ArrayList<AgentParam> paramsAsArray=new ArrayList<AgentParam>();
		paramsAsArray.addAll(params);
		AgentParam result=(AgentParam)new ParameterVarUI(this,paramsAsArray).getResult();
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
		int row=table.getSelectedRow();
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
		
		NegotiationEventListener ael=new NegotiationEventListener() {
			public void handleActionEvent(negotiator.events.ActionEvent evt) {
				System.out.println("Caught event "+evt);
			}

			public void handleLogMessageEvent(LogMessageEvent evt) {
				// TODO Auto-generated method stub
				
			}

			public void handeNegotiationSessionEvent(NegotiationSessionEvent evt) {
				// TODO Auto-generated method stub
				
			}

			public void handleBlateralAtomicNegotiationSessionEvent(
					BilateralAtomicNegotiationSessionEvent evt) {
				// TODO Auto-generated method stub
				
			}


			public void handleNegotiationEndedEvent(NegotiationEndedEvent evt) {
				// TODO Auto-generated method stub
				
			}

			public void handleNegotiationSessionEvent(NegotiationSessionEvent evt) {
				// TODO Auto-generated method stub
				
			}

		};
		
		Thread t = new Thread(new TournamentExecutor(tournament, ael));
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
		correctposition(vars,Tournament.VARIABLE_AGENT_A,new AgentVariable());
		correctposition(vars,Tournament.VARIABLE_AGENT_B,new AgentVariable());
		vars.add(new TotalSessionNumberVariable());
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
				new Warning("tournament has no "+stub.getClass()+" variable. adding a stub");
				vars.add(expectedpos,stub);
			} else {
				new Warning("tournament has "+stub.getClass()+" variable not on expected place. Moving it to correct position.");
				vars.remove(pos);
				vars.add(expectedpos, v);
			}
		}
	}
	
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try {
			new TournamentVarsUI(); 
		}
		catch (Exception e) { new Warning("TournamentVarsUI failed to launch: "+e); }
	}
}