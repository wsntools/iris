/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.GUIModuleSettings;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class DiaLoggerSettings extends JDialog {

	private Model model;
	private DiaLoggerSettings ref = this;
	
	private Map<String, IRIS_Attribute[]> mapOfLoggingFiles;
	private List<IRIS_Attribute[]> initialLoggingFiles;
	
	private List<IRIS_Attribute> attributes;
	
	private int number;
	private boolean abort;
	
	private JPanel panelMain = new JPanel(new BorderLayout());
	
	private JPanel panelUpper = new JPanel();
	private JComboBox<String> comboLoggingFiles;
	private JButton butAdd = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnAddSm())));
	private JButton butDel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDeleteSm())));
	
	private JCheckBox[] arrCheckBoxes;
	private JScrollPane scrollChecklists;
	private JPanel panelChecklists;
	
	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	private DiaLoggerSettings(Model m, Measurement measure, List<IRIS_Attribute[]> currentConfig) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		abort = false;
				
		mapOfLoggingFiles = new HashMap<String, IRIS_Attribute[]>();
		initialLoggingFiles = currentConfig;
		
		String[] entries = new String[currentConfig.size()];
		for(int i=0; i<currentConfig.size(); i++) {
			entries[i] = "Logging " + (i+1) + " ( ";
			for(IRIS_Attribute attr:currentConfig.get(i)) entries[i] += attr.getAttributeName() + " ";
			entries[i] += ")";
			mapOfLoggingFiles.put(entries[i], currentConfig.get(i));
		}
		number = currentConfig.size() + 1;
		comboLoggingFiles = new JComboBox<String>(entries);		
		butAdd.setPreferredSize(new Dimension(16, 16));
		butDel.setPreferredSize(new Dimension(16, 16));
		
		panelUpper.add(comboLoggingFiles);
		panelUpper.add(butAdd);
		panelUpper.add(butDel);
		
		arrCheckBoxes = new JCheckBox[measure.getAttributeCount()];
		panelChecklists = new JPanel(new GridLayout(measure.getAttributeCount(), 1));
		attributes = measure.getAttributes();
		for(int i=0; i<attributes.size(); i++) {
			arrCheckBoxes[i] = new JCheckBox(attributes.get(i).getAttributeName());
			panelChecklists.add(arrCheckBoxes[i]);
		}
		scrollChecklists = new JScrollPane(panelChecklists);
		
		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);
		panelButton.add(butCancel);
		
		panelMain.add(panelUpper, BorderLayout.NORTH);
		panelMain.add(scrollChecklists, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);
		
		comboLoggingFiles.addActionListener(listenerButtons);
		butAdd.addActionListener(listenerButtons);
		butDel.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);
		butCancel.addActionListener(listenerButtons);
		
		// Windowsettings
		this.setTitle("IRIS - Event Logger Configuration");
		// this.setResizable(false);
		this.setContentPane(panelMain);
		this.setPreferredSize(new Dimension(400, 400));
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
	
	public static List<IRIS_Attribute[]> showLoggerSettingsWindow(Model m, Measurement measure, List<IRIS_Attribute[]> currentConfig) {
		DiaLoggerSettings dia = new DiaLoggerSettings(m, measure, currentConfig);
		return dia.getListOfCreatedLoggingAttributes();
	}
	
	private List<IRIS_Attribute[]> getListOfCreatedLoggingAttributes() {
		if(!abort)
			return new ArrayList<IRIS_Attribute[]>(mapOfLoggingFiles.values());
		else
			return initialLoggingFiles;
	}
	
	private boolean isAtLeastOneBexChecked() {
		for(JCheckBox box: arrCheckBoxes) {
			if (box.isSelected()) return true;
		}
		return false;
	}

	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if(ae.getSource().equals(butOK)) {				
				ref.dispose();
			}
			else if(ae.getSource().equals(butCancel)) {
				abort = true;
				ref.dispose();
			}
			else if(ae.getSource().equals(butAdd)) {
				if(!isAtLeastOneBexChecked()) return;
				ArrayList<IRIS_Attribute> listOfLogging = new ArrayList<IRIS_Attribute>();
				String entry, attribute = "";
				for(int i=0; i<arrCheckBoxes.length; i++) {
					if(arrCheckBoxes[i].isSelected()) {
						listOfLogging.add(attributes.get(i));
						attribute += attributes.get(i).getAttributeName() + " ";
					}
				}
				entry = "Logging " + (number++) + " ( " + attribute + " )";
				IRIS_Attribute[] arrAttr = new IRIS_Attribute[listOfLogging.size()];
				arrAttr = listOfLogging.toArray(arrAttr);
				mapOfLoggingFiles.put(entry, arrAttr);
				comboLoggingFiles.addItem(entry);
			}
			else if(ae.getSource().equals(butDel)) {
				if(comboLoggingFiles.getSelectedIndex() == -1) return;
				mapOfLoggingFiles.remove((String)comboLoggingFiles.getSelectedItem());
				comboLoggingFiles.removeItemAt(comboLoggingFiles.getSelectedIndex());
			}
			
			else if(ae.getSource().equals(comboLoggingFiles)) {
				
			}
		}
	}
}
