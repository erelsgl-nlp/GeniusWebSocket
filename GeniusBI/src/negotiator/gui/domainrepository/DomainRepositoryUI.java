package negotiator.gui.domainrepository;

import javax.swing.JFrame;
import java.net.URL;
import javax.swing.JTree;
import javax.swing.tree.*;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JButton;

import negotiator.Domain;
import negotiator.repository.*;
import negotiator.utility.UtilitySpace;

import javax.swing.JFileChooser;
import java.io.FileFilter;

import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.NegoGUIComponent;
import negotiator.gui.tree.TreeFrame;


/**
 * A user interface to the agent repository 
 * @author wouter
 *
 */
public class DomainRepositoryUI implements NegoGUIComponent 
{
	JButton	adddomainbutton=new JButton("Add Domain");
	JButton	removedomainbutton=new JButton("Remove Domain");
	JButton addprofilebutton=new JButton("Add Profile");
	JButton removeprofilebutton=new JButton("Remove Profile");
	JButton editbutton=new JButton("Edit");
	

	Repository domainrepository; // TODO locate this somewhere better
	JFrame frame;
	MyTreeNode root=new MyTreeNode(null);
	JTree tree;
	DefaultTreeModel treemodel;
	public DomainRepositoryUI(JTree pTree) throws Exception
	{
		this.tree = pTree;
		domainrepository=Repository.get_domain_repos();
		initTree();
		tree.setModel(treemodel);
		final NegoGUIComponent comp = this;
		tree.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				NegoGUIApp.negoGUIView.setActiveComponent(comp); 
			}

			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}	
	public DomainRepositoryUI() throws Exception
	{
		domainrepository=Repository.get_domain_repos();
		frame = new JFrame();
		frame.setTitle("Negotiation Domains and Preference Profile Repository");
		frame.setLayout(new BorderLayout());
	
		 // CREATE THE BUTTONS
		JPanel buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		adddomainbutton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				try { adddomain(); } 
				catch (Exception err)  { new Warning("add domain failed:"+err);}
			}
		});
		removedomainbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removedomain(); }
				catch (Exception err)  { new Warning("remove domain failed:"+err);}
			}
		});
		addprofilebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { addprofile(); }
				catch (Exception err)  { new Warning("remove failed:"+err);}
			}
		});
		removeprofilebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { removeprofile(); }
				catch (Exception err)  { new Warning("remove failed:"+err);}
			}
		});
		editbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { edit(); }
				catch (Exception err)  { new Warning("remove failed:"+err); err.printStackTrace();}
			}
		});
		buttons.add(adddomainbutton);
		buttons.add(removedomainbutton);
		buttons.add(addprofilebutton);
		buttons.add(removeprofilebutton);
		buttons.add(editbutton);
		tree=new JTree();
		initTree();
		JScrollPane scrollpane = new JScrollPane(tree);

		frame.add(buttons,BorderLayout.EAST);
		frame.add(scrollpane,BorderLayout.CENTER);
		frame.pack();
		frame.show();
	}
	private void initTree(){
		// create the tree
		for (RepItem repitem: domainrepository.getItems()) {
			DomainRepItem dri=(DomainRepItem)repitem;
			MyTreeNode newchild=new MyTreeNode(dri);
			for (ProfileRepItem profileitem: dri.getProfiles())
			{
				newchild.add(new MyTreeNode(profileitem));
			}
			root.add(newchild);
		}
			
		treemodel=new DefaultTreeModel(root);
		tree.setModel(treemodel);
		
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true) ;
	
	}
	void adddomain() throws Exception { 
		//System.out.println("Add domain to " +((MyTreeNode)(tree.getLastSelectedPathComponent())).getRepositoryItem());
		JFileChooser fd=new JFileChooser(); 
	    //ExampleFileFilter filter = new ExampleFileFilter();
	    //filter.addExtension("xml");
	    //filter.setDescription("domain xml file");
	    //fd.setFileFilter(filter);
		//fd.setFileFilter(filter);
	    int returnVal = fd.showOpenDialog(frame);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        System.out.println("You chose to open this file: " +
	             fd.getSelectedFile().toURL());
	        DomainRepItem newnode=new DomainRepItem(fd.getSelectedFile().toURL());
	        domainrepository.getItems().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), root, root.getChildCount());
			domainrepository.save();
	     }	
	}
	public void adddomain(URL fileName) throws Exception { 
	        DomainRepItem newnode=new DomainRepItem(fileName);
	        
	        domainrepository.getItems().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), root, root.getChildCount());
			domainrepository.save();
	
	}
	
	void removedomain() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to remove first");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		System.out.println("remove domain " +item);
		domainrepository.getItems().remove(item);
		treemodel.removeNodeFromParent(selection);
		domainrepository.save();
	}
	
	
	public void addprofile(URL fileName) throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to add the profile to");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		
        // TODO check that selected profile indeed belongs to our domain.
        ProfileRepItem newnode=new ProfileRepItem(fileName,(DomainRepItem)item);
        ((DomainRepItem)item).getProfiles().add(newnode);		        
		treemodel.insertNodeInto(new MyTreeNode(newnode), selection, selection.getChildCount());
		domainrepository.save();
	    
	}

	
	void addprofile() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a domain to add the profile to");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem))
			throw new Exception("please select a domain node");
		
		JFileChooser fd=new JFileChooser(); 
	    int returnVal = fd.showOpenDialog(frame);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        System.out.println("You chose to open this file: " +
	             fd.getSelectedFile().toURL());
	        // TODO check that selected profile indeed belongs to our domain.
	        ProfileRepItem newnode=new ProfileRepItem(fd.getSelectedFile().toURL(),(DomainRepItem)item);
	        ((DomainRepItem)item).getProfiles().add(newnode);		        
			treemodel.insertNodeInto(new MyTreeNode(newnode), selection, selection.getChildCount());
			domainrepository.save();
	     }	
	}
	
	void removeprofile() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) throw new Exception("please select a profile to remove first");
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof ProfileRepItem))
			throw new Exception("please select a profile node");
		System.out.println("remove profile " +item);
		
		DomainRepItem domain=((ProfileRepItem)item).getDomain();
		domain.getProfiles().remove(item);
		treemodel.removeNodeFromParent(selection);
		domainrepository.save();
	}

	void edit() throws Exception {
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null ) 
			throw new Exception("please first select an item to be edited");
		RepItem item=selection.getRepositoryItem();
		if (item instanceof DomainRepItem) {
			URL filename=((DomainRepItem)item).getURL();
	    	Domain domain=new Domain(filename.getFile());
	    	TreeFrame treeFrame = new TreeFrame(domain);
		}
		else if (item instanceof ProfileRepItem) {
			URL filename=((ProfileRepItem)item).getURL();
			URL domainfilename=((ProfileRepItem)item).getDomain().getURL();

	    	Domain domain=new Domain(domainfilename.getFile());
	    	UtilitySpace us=new UtilitySpace(domain,filename.getFile());
	    	TreeFrame treeFrame=new TreeFrame(domain, us);
		}
		else
			throw new IllegalStateException("found unknown node in tree: "+item);
		
	}
	
	
	
	/******************DEMO CODE************************/

	
	
	/** run this for a demo of AgentReposUI */
	public static void main(String[] args) 
	{
		try {
			new DomainRepositoryUI(); 
			}
		catch (Exception e) { new Warning("DomainRepositoryUI failed to launch: "+e); }
	}
	public void addAction() {
		// TODO Auto-generated method stub
		MyTreeNode selection=(MyTreeNode)(tree.getLastSelectedPathComponent());
		if (selection==null) {
			try {
				adddomain();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		RepItem item=selection.getRepositoryItem();
		if (!(item instanceof DomainRepItem)) {
			try {
				adddomain();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { 
			try {
				addprofile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void editAction() {
		// TODO Auto-generated method stub
		try {
			edit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
}




