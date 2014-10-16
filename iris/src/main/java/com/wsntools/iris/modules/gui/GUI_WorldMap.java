/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.worldmap.MapView;

public class GUI_WorldMap extends IRIS_GUIModule {

	private final AliasAttribute[] requiredAttributes; 
	private MapView panelWorldMap;
	
	public GUI_WorldMap(Model m) {
		super(m);
		requiredAttributes = new AliasAttribute[3];
		requiredAttributes[0] = new AliasAttribute(m, "Latitude", AliasAttribute.ALIAS_GUI, "Latitude coordinate of a node");
		requiredAttributes[1] = new AliasAttribute(m, "Longitude", AliasAttribute.ALIAS_GUI, "Longitude coordinate of a node");
		requiredAttributes[2] = new AliasAttribute(m, "NodeID", AliasAttribute.ALIAS_GUI, "The identification number of a node");		
	}

	@Override
	public String getModuleName() {
		return "World Map";
	}

	@Override
	public String getModuleDescription() {
		return "This module allows you to view a nodes position on the globe by using longitute and latitude information.";
	}

	@Override
	public JPanel getGUIPanel() {
		if(panelWorldMap == null) {
			panelWorldMap = new MapView(model, requiredAttributes[0], requiredAttributes[1], requiredAttributes[2]);
			panelWorldMap.setBorder(javax.swing.BorderFactory.createTitledBorder(getModuleName()));
		}
		return panelWorldMap;
	}

	@Override
	public boolean isToolbarOnly() {
		return false;
	}

	@Override
	public IRIS_Observer getModuleObserver() {
		return panelWorldMap;
	}

	@Override
	public IRIS_ModuleInfo[] getRelatedModuleInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRelatedMenuBarEntries() {
		return null;
	}

	@Override
	public ActionListener getMenuBarActionListener() {
		return null;
	}
	
	@Override
	public AliasAttribute[] getRequiredAliasAttributes() {		
		return requiredAttributes;
	}

}
