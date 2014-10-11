/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

/**
 * @author Sascha Jungen
 */
public abstract class FunctionBasic {

	public String getFunctionDescription() {
		
		return "";
	}
	
	public int getParameterCount() {
		
		return getParameterNames().length;
	}
	
	public abstract String[] getParameterNames();
	
	public int getSettingsCount() {
		
		return getSettingNames().length;
	}
	
	public abstract String[] getSettingNames();
}
