/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_CC2420_OutputPower extends FunctionBasic implements IRIS_FunctionModule {

	@Override
	public String getFunctionName() {

		return "CC2420 Output Power";
	}

	@Override
	public String getFunctionDescription() {
		
		return "Converts the delivered TxPower value from a CC2420 transceiver into Output Power [dBm]";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {
		
		float[] res = new float[val[0].length];
		for(int i=0; i<val[0].length; i++) {
			switch((int)val[0][i]) {
			
			case 31:
				res[i] = (float) 0;
				break;
			case 27:
				res[i] = (float) -1;
				break;
			case 23:
				res[i] = (float) -3;
				break;
			case 19:
				res[i] = (float) -5;
				break;
			case 15:
				res[i] = (float) -7;
				break;
			case 11:
				res[i] = (float) -10;
				break;
			case 7:
				res[i] = (float) -15;
				break;
			case 3:
				res[i] = (float) -25;
				break;
			default:
				res[i] = (float) -0.0;
			}			
		}
		return res;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = {"CC2420 RSSI"};
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
	public boolean isScalarValueResult() {

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
