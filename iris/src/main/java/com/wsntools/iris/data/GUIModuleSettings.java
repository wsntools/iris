/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import com.wsntools.iris.interfaces.IRIS_GUIModule;

/**
 * Contains information about the handling of a GUI Module, specified by the user 
 *
 */
public class GUIModuleSettings {

	private IRIS_GUIModule guiModule;
	
	//This flag indicates whether a module is used within the program
	private boolean isActive;
	
	//This flag is set to true, if the module has an observer which should be registered within the model
	private boolean isRegisteredAsObserver;
	
	//This flag is used to indicate if the panel shall be placed within the ViewMain or a separate window
	private boolean isWindowed;

	
	public GUIModuleSettings(IRIS_GUIModule module) {
		
		guiModule = module;
		isActive = false;
		isRegisteredAsObserver = true;
		isWindowed = false;
		
	}
	
	
	
	
	//Getter && Setter
	public IRIS_GUIModule getGUIModule() {
		return guiModule;
	}
	
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean val) {
		isActive = val;
	}
	
	public boolean isRegisteredAsObserver() {
		return isRegisteredAsObserver;
	}
	public void setRegisteredAsObserver(boolean val) {
		isRegisteredAsObserver = val;
	}
	
	
	public boolean isWindowed() {
		return isWindowed;
	}
	public void setWindowed(boolean val) {
		isWindowed = val;
	}
}
