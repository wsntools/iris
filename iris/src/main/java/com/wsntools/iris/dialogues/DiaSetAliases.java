/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.panels.PanelMapping;

public class DiaSetAliases extends JDialog {

	private static final long serialVersionUID = 1L;
	private Model model;
	private DiaSetAliases ref = this;
	
	private JPanel panelMain = new JPanel(new BorderLayout());
	
	private JPanel panelUpper = new JPanel();
	private JComboBox<AliasAttribute> comboAttributes = new JComboBox<AliasAttribute>();
	private JButton butAdd = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnNew())));
	private JButton butRename = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnRename())));
	private JButton butRemove = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private JScrollPane scrollMapping;
	private JPanel panelMapping = new JPanel();
	private PanelMapping[] arrPanelMappings;

	private JPanel panelLower = new JPanel();
	private JButton butApply = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnApply())));
	private JButton butClose = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnExit())));
	
	private GUIListener listenerGUI = new GUIListener();
	
	private DiaSetAliases(Model m) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		
		comboAttributes.setPreferredSize(new Dimension(200,28));
		butAdd.setPreferredSize(new Dimension(28,28));
		butRename.setPreferredSize(new Dimension(28,28));
		butRemove.setPreferredSize(new Dimension(28,28));
		
		panelUpper.add(comboAttributes);
		panelUpper.add(butAdd);
		panelUpper.add(butRename);
		panelUpper.add(butRemove);				
		
		//Assume a non changing count of measurements during the usage of this dialog
		arrPanelMappings = new PanelMapping[model.getMeasureCount()];
		panelMapping.setLayout(new BoxLayout(panelMapping, BoxLayout.Y_AXIS));
		for(int i=0; i<arrPanelMappings.length; i++) {
			arrPanelMappings[i] = new PanelMapping(model, model.getMeasurement(i));
			panelMapping.add(arrPanelMappings[i]);
		}		
		scrollMapping = new JScrollPane(panelMapping);
		scrollMapping.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		updateList();
		
		butApply.setPreferredSize(new Dimension(28,28));
		butClose.setPreferredSize(new Dimension(28,28));
		
		panelLower.add(butApply);
		panelLower.add(butClose);
		
		panelMain.add(panelUpper, BorderLayout.NORTH);
		panelMain.add(scrollMapping, BorderLayout.CENTER);
		panelMain.add(panelLower, BorderLayout.SOUTH);
		
		butAdd.addActionListener(listenerGUI);
		butRename.addActionListener(listenerGUI);
		butRemove.addActionListener(listenerGUI);
		butApply.addActionListener(listenerGUI);
		butClose.addActionListener(listenerGUI);
		
		// Windowsettings
		this.setTitle("IRIS - Configuration and Mapping of Aliases");
		// this.setResizable(false);
		this.setContentPane(panelMain);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();

		// Set windowposition to center
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
				(tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()),
				((this.getY() < 0) ? 0 : this.getY()));
		this.setVisible(true);
	}
	
	public static void showAliasSettingWindow(Model m) {
		new DiaSetAliases(m);
	}
	
	private void updateList() {
		List<AliasAttribute> listOfAttributes = model.getAliasAttributes();
		int lastPos = comboAttributes.getSelectedIndex() > -1 ? comboAttributes.getSelectedIndex() : 0;
		comboAttributes.removeActionListener(listenerGUI);
		comboAttributes.removeAllItems();
		for(AliasAttribute aa: listOfAttributes) {
			comboAttributes.addItem(aa);
		}
		comboAttributes.addActionListener(listenerGUI);
		comboAttributes.setSelectedIndex(lastPos < comboAttributes.getItemCount() ? lastPos : comboAttributes.getItemCount()-1);
	}
	
	private void updateMappings() {
		AliasAttribute attr = (AliasAttribute) comboAttributes.getSelectedItem();	
		if(attr != null) {
			for(int i=0; i<arrPanelMappings.length; i++) {
				arrPanelMappings[i].setNewAlias(attr);
			}
		}
	}
	
	
	private class GUIListener implements ActionListener {
	
		public void actionPerformed(ActionEvent ae) {
	
			if (ae.getSource().equals(comboAttributes)) {
				updateMappings();
			}			
			else if(ae.getSource().equals(butAdd)) {
				String input = JOptionPane.showInputDialog(model.getCurrentlyFocusedWindow(), "Please enter a name for the new alias:");
				if(input == null) return;
				input = input.trim();
				if(input.isEmpty() || !input.toLowerCase().matches("[0123456789a-z]+")) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "Please enter a valid aliasname (only letters and numbers allowed)");
				}
				else  {
					AliasAttribute attr = new AliasAttribute(model, input, false, "");
					if(!model.addAliasAttribute(attr)) {
						JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "The chosen aliasname is already existing");
					}
					else {
						updateList();
					}
				}
			}
			else if(ae.getSource().equals(butRename)) {
				//Only allow a change of name, if it is no gui-provided alias
				AliasAttribute selAttr = (AliasAttribute)comboAttributes.getSelectedItem();
				if(selAttr == null) return;
				if(selAttr.isGUIRequiredAlias()) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "Attributes required by GUI Modules cannot be renamed");
					return;
				}
				
				String input = JOptionPane.showInputDialog(model.getCurrentlyFocusedWindow(), "Please enter a new name for the alias:", selAttr.getAttributeName());
				input = input.trim();
				if(input.isEmpty() || !input.toLowerCase().matches("[0123456789a-z]+")) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "Please enter a valid aliasname (only letters and numbers allowed)");
				}
				else  {
					selAttr.setAliasAttributeName(input);
					updateList();
				}
			}
			else if(ae.getSource().equals(butRemove)) {
				AliasAttribute selAttr = ((AliasAttribute)comboAttributes.getSelectedItem());
				if(selAttr == null) return;
				if(selAttr.isGUIRequiredAlias()) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "Attributes required by GUI Modules cannot be removed manually");
					return;
				}
				int choice = JOptionPane.showConfirmDialog(ref, "Do you really want to delete the alias attribute " + selAttr.getAttributeName(), "IRIS - Delete Alias Attribute", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(choice == JOptionPane.YES_OPTION) {
					model.removeAliasAttribute(selAttr);
					updateList();
				}
			}
			else if(ae.getSource().equals(butApply)) {
				AliasAttribute selAttr = ((AliasAttribute)comboAttributes.getSelectedItem());
				if(selAttr == null) return;
				Measurement meas;
				for(int i=0; i<arrPanelMappings.length; i++) {
					meas = arrPanelMappings[i].getMappingMeasure();
					selAttr.setMappingAttribute(meas, meas.getAttribute(arrPanelMappings[i].getMappingSelection()));
				}				
			}
			else if(ae.getSource().equals(butClose)) {
				ref.dispose();
			}
		}
	}	
}
