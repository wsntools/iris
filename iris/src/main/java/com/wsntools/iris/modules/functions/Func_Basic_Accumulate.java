/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Basic_Accumulate extends FunctionBasic implements
		IRIS_FunctionModule {

	float[] result = new float[0];

	@Override
	public String getFunctionName() {

		return "Accumulate";
	}

	@Override
	public String getFunctionDescription() {

		return "Accumulates all values if they are within a certain range";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {

		if (val[0].length > result.length) {
			// copy array
			float[] newResult = new float[val[0].length];
			for (int i = 0; i < result.length; i++) {
				newResult[i] = result[i];
			}
			result = newResult;
			// fill remaining entries
			for (int i = 0; i < val[0].length; i++) {
				if (val[0][i] > set[0] && val[0][i] < set[1]) {
					if (i > 1) {
						result[i] = result[i - 1] + val[0][i];
					} else {
						result[i] = val[0][i];
					}
				}
			}
		}
		return result;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = { "Value" };
		return res;
	}

	@Override
	public String[] getSettingNames() {

		String[] res = { "MinValue", "MaxValue" };
		return res;
	}

	@Override
	public float[] getDefaultSettings() {

		float[] res = { 0, 1000 };
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
