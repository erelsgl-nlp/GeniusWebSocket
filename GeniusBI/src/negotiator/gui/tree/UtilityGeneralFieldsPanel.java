/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UtilityGeneralFieldsPanel.java
 *
 * Created on Oct 28, 2009, 12:03:09 PM
 */

package negotiator.gui.tree;

import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import negotiator.utility.UtilitySpace;

/**
 * dispaly and edit General values of the utility function. it update the utility space after each change
 * @author Yinon Oshrat
 */
public class UtilityGeneralFieldsPanel extends javax.swing.JPanel {

    
    private UtilitySpace utilitySpace;

    /** Creates new form UtilityGeneralFieldsPanel */
    public UtilityGeneralFieldsPanel(UtilitySpace us) {
        utilitySpace=us;
        

        initComponents();
        InputVerifier verifier = new InputVerifier() {
             public boolean verify(JComponent comp) {
               boolean returnValue;
               JTextField textField = (JTextField)comp;
               if (textField.getText().length()<=0) 
            	   return true;
               try {
                 Double.parseDouble(textField.getText());
                 returnValue = true;
               } catch (NumberFormatException e) {
                 Toolkit.getDefaultToolkit().beep();
                 returnValue = false;
               }
            return returnValue;
             }
        };
        optOutText.setInputVerifier(verifier);
        statusQouText.setInputVerifier(verifier);
        timeEffectText.setInputVerifier(verifier);
        weightMultiplyerText.setInputVerifier(verifier);
        optOutText.setDocument(new NumericDocument());
        statusQouText.setDocument(new NumericDocument());
        timeEffectText.setDocument(new NumericDocument());
        weightMultiplyerText.setDocument(new NumericDocument());
        if (utilitySpace.getOptOutValue(0)!=null)
        	optOutText.setText(utilitySpace.getOptOutValue(0).toString());
        if (utilitySpace.getReservationValue()!=null)
        	statusQouText.setText(utilitySpace.getReservationValue().toString());
        if (utilitySpace.getTimeEffectValue()!=null)
        	timeEffectText.setText(utilitySpace.getTimeEffectValue().toString());
        if (utilitySpace.getWeightMultiplyer()!=null)
        	weightMultiplyerText.setText(utilitySpace.getWeightMultiplyer().toString());
        optOutText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            	if (optOutText.getText().length()>0)
            		utilitySpace.setOptOutValue(new Double(optOutText.getText()));
            	else 
            		utilitySpace.setOptOutValue(null);
            }

            public void insertUpdate(DocumentEvent e) {
            	if (optOutText.getText().length()>0)
            		utilitySpace.setOptOutValue(new Double(optOutText.getText()));
            	else 
            		utilitySpace.setOptOutValue(null);
            }

            public void removeUpdate(DocumentEvent e) {
            	if (optOutText.getText().length()>0)
            		utilitySpace.setOptOutValue(new Double(optOutText.getText()));
            	else 
            		utilitySpace.setOptOutValue(null);
            }
        });
        statusQouText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            	if (statusQouText.getText().length()>0)
            		utilitySpace.setReservationValue(new Double(statusQouText.getText()));
            	else 
            		utilitySpace.setReservationValue(null);
            }

            public void insertUpdate(DocumentEvent e) {
            	if (statusQouText.getText().length()>0)
            		utilitySpace.setReservationValue(new Double(statusQouText.getText()));
            	else 
            		utilitySpace.setReservationValue(null);
            }

            public void removeUpdate(DocumentEvent e) {
            	if (statusQouText.getText().length()>0)
            		utilitySpace.setReservationValue(new Double(statusQouText.getText()));
            	else 
            		utilitySpace.setReservationValue(null);
            }
        });
        timeEffectText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            	if (timeEffectText.getText().length()>0)
            		utilitySpace.setTimeEffectValue(new Double(timeEffectText.getText()));
            	else 
            		utilitySpace.setTimeEffectValue(null);
            }

            public void insertUpdate(DocumentEvent e) {
            	if (timeEffectText.getText().length()>0)
            		utilitySpace.setTimeEffectValue(new Double(timeEffectText.getText()));
            	else 
            		utilitySpace.setTimeEffectValue(null);
            }

            public void removeUpdate(DocumentEvent e) {
            	if (timeEffectText.getText().length()>0)
            		utilitySpace.setTimeEffectValue(new Double(timeEffectText.getText()));
            	else 
            		utilitySpace.setTimeEffectValue(null);
            }
        });
        weightMultiplyerText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            	if (weightMultiplyerText.getText().length()>0)
            		utilitySpace.setWeightMultiplyer(new Double(weightMultiplyerText.getText()));
            	else 
            		utilitySpace.setWeightMultiplyer(1.0);
            }

            public void insertUpdate(DocumentEvent e) {
            	if (weightMultiplyerText.getText().length()>0)
            		utilitySpace.setWeightMultiplyer(new Double(weightMultiplyerText.getText()));
            	else 
            		utilitySpace.setWeightMultiplyer(1.0);
            }

            public void removeUpdate(DocumentEvent e) {
            	if (weightMultiplyerText.getText().length()>0)
            		utilitySpace.setWeightMultiplyer(new Double(weightMultiplyerText.getText()));
            	else 
            		utilitySpace.setWeightMultiplyer(1.0);
            }
        });       

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        optOutText = new javax.swing.JTextField();
        statusQouText = new javax.swing.JTextField();
        timeEffectText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        weightMultiplyerText = new javax.swing.JTextField();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(negotiator.gui.NegoGUIApp.class).getContext().getResourceMap(UtilityGeneralFieldsPanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        optOutText.setColumns(6);
        optOutText.setText(resourceMap.getString("optOutText.text")); // NOI18N
        optOutText.setInheritsPopupMenu(true);
        optOutText.setName("optOutText"); // NOI18N
        optOutText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optOutTextActionPerformed(evt);
            }
        });

        statusQouText.setColumns(6);
        statusQouText.setText(resourceMap.getString("statusQouText.text")); // NOI18N
        statusQouText.setName("statusQouText"); // NOI18N

        timeEffectText.setColumns(6);
        timeEffectText.setText(resourceMap.getString("timeEffectText.text")); // NOI18N
        timeEffectText.setName("timeEffectText"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        weightMultiplyerText.setColumns(6);
        weightMultiplyerText.setName("weightMultiplyerText"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(optOutText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 13, Short.MAX_VALUE)
                        .add(statusQouText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 13, Short.MAX_VALUE)
                        .add(timeEffectText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
                        .add(weightMultiplyerText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(optOutText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusQouText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(timeEffectText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(weightMultiplyerText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optOutTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optOutTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optOutTextActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField optOutText;
    private javax.swing.JTextField statusQouText;
    private javax.swing.JTextField timeEffectText;
    private javax.swing.JTextField weightMultiplyerText;
    // End of variables declaration//GEN-END:variables

    

}

class NumericDocument extends PlainDocument {

    double currentValue = 0;

    public NumericDocument() {
    }

    public double getValue() {
      return currentValue;
    }

    public void insertString(int offset, String string,
        AttributeSet attributes) throws BadLocationException {

      if (string == null) {
        return;
      } else {
        String newValue;
        int length = getLength();
        if (length == 0) {
          newValue = string;
        } else {
          String currentContent = getText(0, length);
          StringBuffer currentBuffer = 
               new StringBuffer(currentContent);
          currentBuffer.insert(offset, string);
          newValue = currentBuffer.toString();
        }
        currentValue = checkInput(newValue, offset);
        super.insertString(offset, string, attributes);
      }
    }
    public void remove(int offset, int length)
        throws BadLocationException {
      int currentLength = getLength();
      String currentContent = getText(0, currentLength);
      String before = currentContent.substring(0, offset);
      String after = currentContent.substring(length+offset,
        currentLength);
      String newValue = before + after;
      currentValue = checkInput(newValue, offset);
      super.remove(offset, length);
    }
    public double checkInput(String proposedValue, int offset)
        throws BadLocationException {
      if (proposedValue.length() > 0) {
        try {
          double newValue = Double.parseDouble(proposedValue);
          return newValue;
        } catch (NumberFormatException e) {
          throw new BadLocationException(proposedValue, offset);
        }
      } else {
        return 0;
      }
    }
  }

