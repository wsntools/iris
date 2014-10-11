/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaSelectGUI;
import com.wsntools.iris.dialogues.DiaSettings;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.tools.DataCollector;
import com.wsntools.iris.tools.SaveAndLoad;

/**
 * @author Sascha Jungen M#2242754
 * 
 *         Contains all objects of the menubar including an inner actionlistener
 *         class
 */
public class RMT_MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private Model model;

	private JMenu fileMenu, importMenu, exportMenu, settingMenu;
	private JMenuItem fileNew, fileOpen, fileSave, fileQuit, writeScript,
			importFromWise, importFromTrace,
			exportToWise, exportToTrace, // exportToModel;
			settingGUI, settingBuffer;

	private MenuListener listenerMenu = new MenuListener();
	
	//Entries related to GUI modules
	private Map<IRIS_GUIModule, JMenu> mapModuleToMenu;
	private Map<IRIS_GUIModule, JMenuItem[]> mapModuleToMenuItems;

	public DataCollector collector;

	public RMT_MenuBar(Model m) {
		model = m;
		
		mapModuleToMenu = new HashMap<IRIS_GUIModule, JMenu>();
		mapModuleToMenuItems = new HashMap<IRIS_GUIModule, JMenuItem[]>();

		// --File--
		// --------
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		this.add(fileMenu);

		fileNew = new JMenuItem("New");
		fileNew.setAccelerator(KeyStroke
				.getKeyStroke('N', InputEvent.CTRL_MASK));
		fileOpen = new JMenuItem("Open");
		fileOpen.setAccelerator(KeyStroke.getKeyStroke('O',
				InputEvent.CTRL_MASK));
		fileSave = new JMenuItem("Save");
		fileSave.setAccelerator(KeyStroke.getKeyStroke('S',
				InputEvent.CTRL_MASK));
		fileQuit = new JMenuItem("Quit");
		fileQuit.setAccelerator(KeyStroke.getKeyStroke('Q',
				InputEvent.CTRL_MASK));
		writeScript = new JMenuItem("Script");
		writeScript.setAccelerator(KeyStroke.getKeyStroke('S',
				InputEvent.CTRL_MASK));

		fileMenu.add(fileNew);
		fileMenu.add(writeScript);
		// fileMenu.add(fileSave);
		// fileMenu.addSeparator();
		fileMenu.add(fileQuit);

		// --Import--
		// ----------
		importMenu = new JMenu("Import");
		importMenu.setMnemonic(KeyEvent.VK_I);
		this.add(importMenu);

		importFromWise = new JMenuItem("From WISEML");
		importFromTrace = new JMenuItem("From Trace");

		importMenu.add(importFromWise);
		importMenu.add(importFromTrace);

		// --Export--
		// ----------
		exportMenu = new JMenu("Export");
		exportMenu.setMnemonic(KeyEvent.VK_E);
		this.add(exportMenu);

		exportToWise = new JMenuItem("To WISEML");
		exportToTrace = new JMenuItem("To Trace");

		exportMenu.add(exportToWise);
		exportMenu.add(exportToTrace);

		// exportToModel = new JMenuItem("As Simulatormodel");
		// exportMenu.add(exportToModel);

		// --Settings--
		// ------------
		settingMenu = new JMenu("Settings");
		settingMenu.setMnemonic(KeyEvent.VK_S);
		this.add(settingMenu);

		settingGUI = new JMenuItem("Displayed GUI Elements");
		settingBuffer = new JMenuItem("Packet Record");

		settingMenu.add(settingGUI);
		settingMenu.add(settingBuffer);

		// --Actionlistener registration--
		// -------------------------------
		fileNew.addActionListener(listenerMenu);
		fileOpen.addActionListener(listenerMenu);
		fileSave.addActionListener(listenerMenu);
		fileQuit.addActionListener(listenerMenu);
		writeScript.addActionListener(listenerMenu);
		importFromWise.addActionListener(listenerMenu);
		importFromTrace.addActionListener(listenerMenu);
		exportToWise.addActionListener(listenerMenu);
		exportToTrace.addActionListener(listenerMenu);
		settingGUI.addActionListener(listenerMenu);
		settingBuffer.addActionListener(listenerMenu);
		// exportToModel.addActionListener(listenerMenu);

	}
	
	//External method to add/remove entries from the menu
	public void addGUIModuleMenuBar(IRIS_GUIModule module) {
		if(module.getRelatedMenuBarEntries() == null || module.getRelatedMenuBarEntries().length == 0) return;
		JMenu moduleMenu = new JMenu(module.getModuleName());
		this.add(moduleMenu);
		
		String[] moduleEntries = module.getRelatedMenuBarEntries();
		JMenuItem[] moduleMenuItems = new JMenuItem[moduleEntries.length];
		ActionListener moduleListener = module.getMenuBarActionListener();
		
		for(int i=0; i<moduleEntries.length; i++) {
			moduleMenuItems[i] = new JMenuItem(moduleEntries[i]);
			moduleMenu.add(moduleMenuItems[i]);
			moduleMenuItems[i].addActionListener(moduleListener);			
		}		
		
		mapModuleToMenu.put(module, moduleMenu);
		mapModuleToMenuItems.put(module, moduleMenuItems);
	}
	
	public void removeGUIModuleMenuBar(IRIS_GUIModule module) {
		if(!mapModuleToMenu.containsKey(module)) return;
		
		JMenu moduleMenu = mapModuleToMenu.get(module);		
		JMenuItem[] moduleMenuItems = mapModuleToMenuItems.get(module);
		ActionListener moduleListener = module.getMenuBarActionListener();

		for(JMenuItem item: moduleMenuItems) {
			item.removeActionListener(moduleListener);			
		}
		
		moduleMenu.removeAll();
		this.remove(moduleMenu);
		
		mapModuleToMenu.remove(module);
		mapModuleToMenuItems.remove(module);
	}

	// Inner class to handle menu selection process
	class MenuListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// --File--
			// --------
			if (ae.getSource().equals(fileNew)) {

				int conf = JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(),
						"Do you really want to reset the application?",
						"New Measure Series", JOptionPane.YES_NO_OPTION);
				if (conf == 0) {
					int todel = model.getMeasureCount();
					while (todel > 0) {
						model.removeMeasurement(0);
						todel--;
					}
				}			
			} else if (ae.getSource().equals(fileQuit)) {
				model.getView().SafeCloseOperation();
			}

			else if (ae.getSource().equals(writeScript)) {
				SimpleScriptExecuter.openGUI(model.getCurrentMeasurement());
			}

			// --Import--
			// ----------
			else if (ae.getSource().equals(importFromWise)) {
				SaveAndLoad.loadFromXML(model);
			} else if (ae.getSource().equals(importFromTrace)) {
				SaveAndLoad.loadFromTrace(model);
			}

			// --Export--
			// ----------
			else if (ae.getSource().equals(exportToWise)) {
				SaveAndLoad.saveToXML(model);
			} else if (ae.getSource().equals(exportToTrace)) {
				SaveAndLoad.saveToTrace(model);
			}
			// --Setting--
			// -----------
			else if (ae.getSource().equals(settingGUI)) {
				DiaSelectGUI.showGUISelectionWindow(model);
			}
			else if (ae.getSource().equals(settingBuffer)) {
				DiaSettings.showRecordSettingWindow(model);
			}
		}
	}

}
