package negotiator.gui;

import javax.swing.JButton;

public interface NegoGUIComponent {
	public JButton[] getButtons() ;
	
	public void addAction();
	
	public void removeAction();
	
	public void editAction();
	
	public void saveAction();
}
