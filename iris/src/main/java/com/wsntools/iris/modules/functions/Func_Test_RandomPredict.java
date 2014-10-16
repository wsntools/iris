/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Test_RandomPredict extends FunctionBasic implements IRIS_FunctionModule {

	private int predicts = 2;
	
	@Override
	public String getFunctionName() {

		return "Predictor";
	}
	
	@Override
	public String getFunctionDescription() {
		
		return "Randomly creates n values";
	}

	@Override
	public float[] computeData(float[][] arr, float[] set) {
		
		predicts = (int)set[0];
		
		float[] res = new float[arr[0].length];
		for(int i=0; i<arr[0].length; i++) {
			res[i] = arr[0][i];
		}
		for(int i=arr[0].length; i<res.length; i++) {
			res[i] = (float)Math.random()*25;
		}
		return res;
	}

	@Override
	public boolean isScalarValueResult() {

		return false;
	}
	
	@Override
	public String[] getParameterNames() {
		
		String[] res = {"Input"};
		return res;
	}

	@Override
	public String[] getSettingNames() {
		
		String[] res = {"Prediction #"};
		return res;
	}

	@Override
	public float[] getDefaultSettings() {

		float[] res = {2};
		return res;
	}
	
	@Override
	public boolean hasPredictionValues() {

		return true;
	}

	@Override
	public float[] getPredictionValues() {

		float[] res = new float[predicts];
		for(int i=0; i<res.length; i++) {
			res[i] = (float) (Math.random() * 25);
		}
		return res;
	}


}