/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.interfaces;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.GUIModuleSettings;
import com.wsntools.iris.data.Model;

public abstract class IRIS_GUIModule {
	
	protected Model model;
	protected GUIModuleSettings settings;
	
	public IRIS_GUIModule(Model m) {
		model = m;
		settings = new GUIModuleSettings(this);
	}

	public GUIModuleSettings getModuleSettings() {
		return settings;
	}
	public void setModuleSettings(GUIModuleSettings set) {
		settings = set;
	}
	
	//--Abstract methods--
	
	/**
	 * The name of this module
	 * @return
	 */
	public abstract String getModuleName();
	
	/**
	 * The description of the functions provided by this module (shown in the GUI selection)
	 * @return
	 */
	public abstract String getModuleDescription();
	
	/**
	 * The panel which will be used to be placed in the view
	 * @return
	 */
	public abstract JPanel getGUIPanel();
	
	/**
	 * Indicates whether the panel can be placed within the upper bar or if it requires more space
	 * @return
	 */
	public abstract boolean isToolbarOnly();
	
	/**
	 * The observer class which will be registered if the module is chosen
	 * @return
	 */
	public abstract IRIS_Observer getModuleObserver();
	
	/**
	 * List of all module related information that can be displayed
	 * @return
	 */
	public abstract IRIS_ModuleInfo[] getRelatedModuleInfos();
	
	/**
	 * List of all module related menubar entries that are linked to the module
	 * @return
	 */
	public abstract String[] getRelatedMenuBarEntries();
	
	/**
	 * The listener that handles actions related to the given menu entries
	 * @return
	 */
	public abstract ActionListener getMenuBarActionListener();
	
	/**
	 * List of all Attributes, required for this module to operate
	 * @return
	 */
	public abstract AliasAttribute[] getRequiredAliasAttributes();
}
