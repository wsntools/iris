/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import java.util.Arrays;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Quality_SMAW extends FunctionBasic implements
		IRIS_FunctionModule {

	float[] result = null;
	boolean initialized = false;
	int currentLength = 0;

	@Override
	public String getFunctionName() {
		return "Sliding Moving Average Window";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {

		if (val[0].length  < (int) set[0]) {
			result = new float[1];
			result[0] = 0.0f;
			currentLength = 1;
			return result;
		} else if (val[0].length == (int) set[0]) {
			float temp = 0;
			for (int i = 0; i < val[0].length; i++) {
				temp += val[0][i];
			}
			temp /= (int) set[0];
			result = new float[1];
			result[0] = temp;
			currentLength = 1;
			initialized = true;
		} else {
			if (false == initialized){
				float temp = 0;
				for (int i = 0; i < (int) set[0]; i++) {
					temp += val[0][i];
				}
				temp /= (int) set[0];
				result = new float[1];
				result[0] = temp;
				currentLength =1;
				initialized = true;
			}
			for (int i = currentLength; i <= val[0].length - set[0]; i++) {
				currentLength++;
//				result = new float[result.length + 1]; // TODO bad practice!
				result = Arrays.copyOf(result, currentLength); // TODO bad practice!
				result[i] = result[i - 1]
						+ ((val[0][i + (int) set[0]-1] - val[0][i-1]) / (int) set[0]);

			}
		}
		if (result.length < val[0].length){
			int temp = result.length;
			result =Arrays.copyOf(result, val[0].length);
			for (int i = temp; i <val[0].length; i++){
				result[i]= result[temp-1];
			}
		}		
		return result;
	}

	@Override
	public float[] getDefaultSettings() {
		float def[] = { 10f };
		return def;
	}

	@Override
	public boolean hasPredictionValues() {

		return false;
	}

	@Override
	public float[] getPredictionValues() {

		return new float[] {};
	}

	@Override
	public boolean isScalarValueResult() {
		return false;
	}

	@Override
	public String[] getParameterNames() {
		String params[] = { "Input" };
		return params;
	}

	@Override
	public String[] getSettingNames() {
		String settings[] = { "window-size" };
		return settings;
	}

}
