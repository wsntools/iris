/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.extensions.SimpleScriptExecuter;
import com.wsntools.iris.extensions.UAVXMLLogParser;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.networkcomm.PanelCommunication;
import com.wsntools.iris.tools.DataCollector;
import com.wsntools.iris.tools.SaveAndLoad;

public class GUI_NetworkCommunication extends IRIS_GUIModule {

	private PanelCommunication panelCommunication;
	
	public GUI_NetworkCommunication(Model m) {
		super(m);
		panelCommunication = new PanelCommunication(m);
		getGUIPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(getModuleName()));
	}
	
	
	@Override
	public String getModuleName() {
		
		return "Network Communication";
	}

	@Override
	public String getModuleDescription() {
		
		return "This package provides tools for handling communication aspects of a connected sensor network"
				+ " and allows for bidirectional communication between IRIS and the network.";
	}	
	
	@Override
	public JPanel getGUIPanel() {
		return panelCommunication;
	}
	
	@Override
	public boolean isToolbarOnly() {
		return true;
	}

	@Override
	public IRIS_Observer getModuleObserver() {
		return null;
	}

	@Override
	public IRIS_ModuleInfo[] getRelatedModuleInfos() {
		// TODO Auto-generated method stub
		return null;
	}


	private static final String[] menuBarEntries =
		{ "Import Messages", "Import Script", "Import C-Listener Dump", "Decode Package", "Export Messages", "Export Script", "Start Quickdump", "Stop Quickdump" };
	@Override
	public String[] getRelatedMenuBarEntries() {		
		return menuBarEntries;
	}

	private final ActionListener menuBarActionListener = new ActionListener() {		

		public void actionPerformed(ActionEvent e) {
			String name = e.getActionCommand();
			//Import Messages
			if(name.equals(menuBarEntries[0])) {
				SimpleScriptExecuter.setMeasurement(model.getCurrentMeasurement());
				SimpleScriptExecuter.exec(SimpleScriptExecuter.parse(SaveAndLoad.loadScript(model, Constants.getPathSavesMessages())));
				System.out.println("Script imported");
			}
			//Import Script
			else if(name.equals(menuBarEntries[1])) {
				SimpleScriptExecuter.setMeasurement(model.getCurrentMeasurement());
				SimpleScriptExecuter.setText(SaveAndLoad.loadScript(model, Constants.getPathSavesScripts()));
				SimpleScriptExecuter.openGUI(model.getCurrentMeasurement());
			}
			//Import C-Listener Dump
			else if(name.equals(menuBarEntries[2])) {
				File f = SaveAndLoad.fileChooserOpen(model,	System.getProperty("user.dir"));
				if(f==null) return;
				UAVXMLLogParser p = new UAVXMLLogParser();
				String[] files = p.createNodeLogCSV(f.getAbsolutePath());
				System.out.println(Arrays.toString(files));
				SaveAndLoad.loadFromTrace(model, files);
			}
			//Decode Package
			else if(name.equals(menuBarEntries[3])) {
				model.openPackageDecoder();
			}
			//Export Messages
			else if(name.equals(menuBarEntries[4])) {
				SaveAndLoad.saveMessages(model);
			}
			//Export Script
			else if(name.equals(menuBarEntries[5])) {
				SaveAndLoad.saveScript(model, SimpleScriptExecuter.getScriptText());
			}
			//Start Quickdump
			else if(name.equals(menuBarEntries[6])) {
				DataCollector collector = model.getDataCollector();
				if (collector == null) {
					model.setNewDataCollector();
					collector = model.getDataCollector();
				}
				collector.setActivation(true);
				collector.setQuickDump(true);
			}
			//Stop Quickdump
			else if(name.equals(menuBarEntries[7])) {
				DataCollector collector = model.getDataCollector();
				if (collector != null) {
					collector.setActivation(true);
					collector.setQuickDump(false);
				}
			}
		}
	};
	@Override
	public ActionListener getMenuBarActionListener() {
		return menuBarActionListener;
	}
	
	@Override
	public AliasAttribute[] getRequiredAliasAttributes() {		
		return null;
	}

}
