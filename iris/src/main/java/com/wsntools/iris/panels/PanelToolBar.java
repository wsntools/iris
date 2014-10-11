/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaFunction;
import com.wsntools.iris.dialogues.DiaSetAliases;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.tools.SaveAndLoad;
import com.wsntools.iris.tools.Tools;


/**
 * @author Sascha Jungen
 * 
 * Class for adding a toolbar to IRIS
 */
public class PanelToolBar extends JPanel implements IRIS_Observer {

	private Model model;
	
	private JComboBox<String> comboMeasure = new JComboBox<String>();

	private JButton butMeasureNew = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnNew())));
	private JButton butMeasureRename = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnRename())));
	private JButton butMeasureClose = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnDelete())));
	private JButton butMeasureSave = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSave())));
	private JButton butMeasureLoad = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnLoadDoc())));
	private JButton butFunctions = new JButton("Functions");
	private JButton butMapping = new JButton("Global Mapping");
	
	private GUIListener listenerGUI = new GUIListener();
	
	public PanelToolBar(Model m) {
		model = m;
		model.registerObserver(this);
		
		/*
		 * Define layout
		 */
		// Combobox
		for (int i = 0; i < model.getMeasureCount(); i++) {
			comboMeasure.addItem(model.getMeasurement(i).getMeasureName());
		}
		comboMeasure.setPreferredSize(new Dimension(200, 28));

		butMeasureRename.setPreferredSize(new Dimension(28, 28));
		butMeasureClose.setPreferredSize(new Dimension(28, 28));
		butMeasureNew.setPreferredSize(new Dimension(28, 28));
		butMeasureSave.setPreferredSize(new Dimension(28, 28));
		butMeasureLoad.setPreferredSize(new Dimension(28, 28));

		butMeasureRename.setToolTipText("Renames the selected measurement");
		butMeasureClose
				.setToolTipText("Closes and discards current measurement");
		butMeasureNew.setToolTipText("Creates a new measurement");
		butMeasureSave
				.setToolTipText("Opens a filedialog to choose a path to save measurement data in");
		butMeasureLoad
				.setToolTipText("Opens a dialog to choose a measurement-file to load");
		butFunctions.setToolTipText("Configure the appliance of functions to the packet data");
		butMapping.setToolTipText("Define/Edit new aliases for existing attributes");

		this.add(comboMeasure);
		this.add(butMeasureNew);
		this.add(butMeasureRename);
		this.add(butMeasureClose);
		this.add(butMeasureLoad);
		this.add(butMeasureSave);
		this.add(butFunctions);
		this.add(butMapping);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder("Measurement"));
		
		// Add listener
		comboMeasure.addActionListener(listenerGUI);
		butMeasureRename.addActionListener(listenerGUI);
		butMeasureClose.addActionListener(listenerGUI);
		butMeasureNew.addActionListener(listenerGUI);
		butMeasureSave.addActionListener(listenerGUI);
		butMeasureLoad.addActionListener(listenerGUI);
		butFunctions.addActionListener(listenerGUI);
		butMapping.addActionListener(listenerGUI);
	}
	
	class GUIListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			
			Object o = ae.getSource();
			if(o.equals(comboMeasure)) {
				if (comboMeasure.getSelectedIndex() != -1) {

					model.setCurrentMeasureIndex(comboMeasure
							.getSelectedIndex());
				}
			}
			
			else if(o.equals(butMeasureRename)) {
				// Open input dialog for attributename
				String name = (String) JOptionPane.showInputDialog(
						model.getCurrentlyFocusedWindow(),
						"Please enter a new name for the measurement:",
						"Rename", JOptionPane.INFORMATION_MESSAGE, null, null,
						model.getCurrentMeasurement().getMeasureName());
				// If the progress is aborted, stop renaming				
				if (name == null) return;
				name = Tools.checkStringForValidInput(name);
				if (name == null) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(),
							"Invalid name - allowed characters are: A-Z a-z 0-9 (space) _");
					return;
				}
				else if (!model.setMeasureName(model.getCurrentMeasureIndex(), name)) {
					JOptionPane
							.showMessageDialog(model.getCurrentlyFocusedWindow(),
									"Cannot rename measurement because the name is already in use.");
				}				
			}
			
			else if(o.equals(butMeasureClose)) {
				int conf = JOptionPane.showConfirmDialog(model.getView(),
						"Do you really want to close this measurement?",
						"Close Measurement", JOptionPane.YES_NO_OPTION);
				if (conf == 0) {
					model.removeMeasurement(model.getCurrentMeasureIndex());
				}
			}
			
			else if(o.equals(butMeasureNew)) {
				// Open input dialog for attributename
				String name = (String) JOptionPane.showInputDialog(
						model.getView(),
						"Please enter a name for the new measurement:",
						"New Measurement", JOptionPane.INFORMATION_MESSAGE,
						null, null, ("Measurement " + model.getMeasureCount()));
				// If the progress is aborted, stop creating a new function
				name=name.trim();
				if (name == null) return;
				if (name.isEmpty()) {
					name = "Measurement " + model.getNextMeasurementNumber();
				}
				name = Tools.checkStringForValidInput(name);
				if (name == null) {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(),
							"Invalid name - allowed characters are: A-Z a-z 0-9 (space) _");
					return;
				}
				else if (!model.addNewMeasurement(name)) {
					JOptionPane
							.showMessageDialog(model.getView(),
									"Cannot create measurement because the name is already in use.");
				}
			}
			
			else if(o.equals(butMeasureSave)) {
				SaveAndLoad.saveToXML(model);
			}
			
			else if(o.equals(butMeasureLoad)) {
				SaveAndLoad.loadFromXML(model);
			}
			
			else if(o.equals(butFunctions)) {
				new DiaFunction(model);
			}
			
			else if(o.equals(butMapping)) {
				DiaSetAliases.showAliasSettingWindow(model);
			}
		}			
	}
	
	// --Interface methods--
	// Update measurecombobox
	@Override
	public void updateNewMeasure() {
		comboMeasure.removeActionListener(listenerGUI);
		comboMeasure.removeAllItems();
		for (int i = 0; i < model.getMeasureCount(); i++) {
			comboMeasure.addItem(model.getMeasurement(i).getMeasureName());
		}
		comboMeasure.setSelectedIndex(model.getCurrentMeasureIndex());
		comboMeasure.addActionListener(listenerGUI);
		
		System.out.println(model.getCurrentMeasurement().getMeasureName());
	}

	// Do nothing
	@Override
	public void updateNewPacket() {}
	@Override
	public void updateNewAttribute() {}
}
