/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_CC2420_LQI extends FunctionBasic implements IRIS_FunctionModule {

	@Override
	public String getFunctionName() {

		return "CC2420 LQI";
	}

	@Override
	public String getFunctionDescription() {
		
		return "Converts the delivered CORR value from a CC2420 transceiver into LQI\n" +
				"LQI = (CORR - a) * b";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {
		
		float[] res = new float[val[0].length];
		for(int i=0; i<val[0].length; i++) {
			res[i] = (float) (val[0][i] - 45.0);			
		}
		return res;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = {"CC2420 CORR"};
		return res;
	}

	@Override
	public String[] getSettingNames() {

		String[] res = {"a", "b"};
		return res;
	}

	@Override
	public float[] getDefaultSettings() {

		float[] res = {0, 1};
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
