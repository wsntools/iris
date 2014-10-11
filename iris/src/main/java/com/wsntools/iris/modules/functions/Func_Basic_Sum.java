/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Basic_Sum extends FunctionBasic implements IRIS_FunctionModule {
	float old[] = null;

	@Override
	public String getFunctionName() {

		return "Accumulate-Packet";
	}

	@Override
	public String getFunctionDescription() {

		return "Accumulate the number of packets under certain conditions";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {
		
		int temp = 0;;
		float[] result = new float[val[0].length];

		result[0] = 0;
		
		for (int i = 1; i < result.length;i++){
			if (val[0][i-1] == set[0] && val[1][i-1] == set[1]){
				result[i] = temp+1;
				temp = temp+1;
			} else {
				result[i] = result[i-1];
			}
		}
		
		return result;
	}

	@Override
	public String[] getParameterNames() {

		return new String[] { "Value 1", "Value 2" };
	}

	@Override
	public String[] getSettingNames() {

		return new String[] { "Cond 1", "Cond 2" };
	}

	@Override
	public float[] getDefaultSettings() {

		return new float[] { 1, 1 };
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

		return new float[] {};
	}

}
