/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.interfaces;

import java.io.Serializable;

/**
 * @author Sascha Jungen
 */
public interface IRIS_FunctionModule extends Serializable {

	
	public String getFunctionName();
	
	public String getFunctionDescription();
	
	//First dim: #Parameter	/ #Setting
	//Second dim: Values
	//Shall only give back amount of data equal to the length of parameter values except oneValue results
	public float[] computeData(float[][] val, float[] set);
	
	public int getParameterCount();
	
	public String[] getParameterNames();
	
	public int getSettingsCount();
	
	public String[] getSettingNames();
	
	public float[] getDefaultSettings();
	
	public boolean hasPredictionValues();
	
	//Only to use after computed data
	public float[] getPredictionValues();
	
	//For dynamic computation of single value results e.g. as measureinfo
	public boolean isScalarValueResult();
	
}
