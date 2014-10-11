/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Quality_SNR extends FunctionBasic implements IRIS_FunctionModule {

	@Override
	public String getFunctionName() {

		return "Signal Noise Ratio";
	}
	
	@Override
	public String getFunctionDescription() {
		
		return "Computes Signal [dBm]/Noise [dBm]";
	}

	@Override
	public float[] computeData(float[][] arr, float[] set) {

		float[] res = new float[arr[0].length];
		for(int i=0; i<arr[0].length; i++) {
			if(arr[1][i] != 0.0) {
				res[i] = arr[0][i]/arr[1][i];
			}
			else {
				res[i] = (float) -0.0;
			}
		}
		return res;
	}

	@Override
	public boolean isOneValueResult() {

		return false;
	}

	@Override
	public String[] getParameterNames() {
		
		String[] res = {"Signal", "Noise"};
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
	public boolean hasPredictionValues() {

		return false;
	}

	@Override
	public float[] getPredictionValues() {

		float[] res = {};
		return res;
	}

}
