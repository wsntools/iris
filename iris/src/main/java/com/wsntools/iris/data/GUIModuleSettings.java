/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.io.Serializable;
import java.util.Arrays;

import com.wsntools.iris.interfaces.IRIS_GUIModule;

/**
 * Contains information about the handling of a GUI Module, specified by the user 
 *
 */
public class GUIModuleSettings implements Serializable {

	private Class<IRIS_GUIModule> guiModule;
	
	//This flag indicates whether a module is used within the program
	private boolean isActive;
	
	//This flag is set to true, if the module has an observer which should be registered within the model
	private boolean isRegisteredAsObserver;
	
	//This flag is used to indicate if the panel shall be placed within the ViewMain or a separate window
	private boolean isWindowed;
	
	//This flag indicates whether available module information are shown within the infopanel
	private boolean isDisplayingInformation;
	//Array for single toggling of module information
	private boolean[] arrDisplaySingleInformation;
	
	
	
	public GUIModuleSettings(IRIS_GUIModule module) {
		
		guiModule = (Class<IRIS_GUIModule>) module.getClass();
		isActive = false;
		isRegisteredAsObserver = true;
		isWindowed = false;
		isDisplayingInformation = true;
		arrDisplaySingleInformation = new boolean[module.getRelatedModuleInfos() != null ? module.getRelatedModuleInfos().length : 0];
		Arrays.fill(arrDisplaySingleInformation, true);
	}
	
	
	//Getter && Setter
	public void adaptModuleSettings(GUIModuleSettings settings) {
		this.isActive = settings.isActive;
		this.isRegisteredAsObserver = settings.isRegisteredAsObserver;
		this.isWindowed = settings.isWindowed;
		this.isDisplayingInformation = settings.isDisplayingInformation;
		if(this.arrDisplaySingleInformation.length == settings.arrDisplaySingleInformation.length)
			this.arrDisplaySingleInformation = settings.arrDisplaySingleInformation;
	}
	
	public Class<IRIS_GUIModule> getGUIModuleClass() {
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
	
	public boolean isDisplayingInformation() {
		return isDisplayingInformation;
	}
	public void setDisplayingInformation(boolean val) {
		isDisplayingInformation = val;
	}
	
	public boolean[] getDisplaySingleInformationArray() {
		return arrDisplaySingleInformation;
	}
	public void setDisplaySingleInformationArray(boolean[] newArr) {
		if(arrDisplaySingleInformation.length == newArr.length)
			arrDisplaySingleInformation = newArr;
	}
}
