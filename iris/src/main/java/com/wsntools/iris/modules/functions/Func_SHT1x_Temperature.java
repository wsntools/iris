/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_SHT1x_Temperature extends FunctionBasic implements IRIS_FunctionModule {

	@Override
	public String getFunctionName() {

		return "SHT1x Temp(C)";
	}

	@Override
	public String getFunctionDescription() {
		
		return "Converts the delivered temperature value from a SHT1x sensor into C";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {
		
		float[] res = new float[val[0].length];
		for(int i=0; i<val[0].length; i++) {
			res[i] = (float) (-39.60 + 0.01*val[0][i]);
		}
		return res;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = {"TelosB Temp"};
		return res;
	}

	@Override
	public String[] getSettingNames() {

		String[] res = {};
		return res;
	}

	@Override
	public float[] getDefaultSettings() {

		float[] res = {};
		return res;
	}

	@Override
	public boolean isOneValueResult() {

		return false;
	}
	
	@Override
	public boolean hasPredictionValues() {

		return false;
	}

	@Override
	public float[] getPredictionValues() {

		float[] res = {};
		return res;
	}

}
