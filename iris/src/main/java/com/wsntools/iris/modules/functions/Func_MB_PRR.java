/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import java.util.HashMap;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_MB_PRR extends FunctionBasic implements IRIS_FunctionModule {

	@Override
	public String getFunctionName() {

		return "MB_PRR_END2END";
	}

	@Override
	public String getFunctionDescription() {

		return "Berechnet end zu end PRR";
	}

	@Override
	public float[] computeData(float[][] val, float[] set) {
		float[] res = new float[val[1].length];	
		HashMap<Float, Float> hm = new HashMap<Float, Float>();
		
		float min= Float.MAX_VALUE;
		
		for (int i = 0; i < val[1].length; i++){

			if(min > val[1][i]){
				min = val[1][i];
			}
			
		}

		
		for(int i=0; i<val[1].length; i++) {
			if (val[1][i]== min){
				if (hm.get(val[0][i]) == null){
					hm.put(val[0][i], new Float(1f));
				} else{
					Float temp = hm.get(val[0][i]);
					temp++;
					hm.put(val[0][i], temp);
				}
			}
		}
		
		float[]re = new float[hm.size()];
		float[]re2 = new float[hm.size()];
		int a = 0;
		
		for (Float current: hm.keySet()){
			Float temp = hm.get(current);
			temp = temp / val[2][0];
			hm.put(current, temp);
			re[a] = temp;
			re2[a] = current;
			a++;
		}
		
		
		//Sortiere
		for (int k = 0; k < re.length; k++){
			for (int l = k; l < re.length; l++){
				if (re2[k] > re2[l]){
					float temp = re2[k];
					re2[k] = re2[l];
					re2[l] = temp;
					temp = re[k];
					re[k] = re[l];
					re[l] = temp;
				}
			}
		}
		
		for (int k = 0; k < re.length; k++)
			System.out.println(re[k]+" "+re2[k]);
		
		// verkehrt

		
		int number = hm.size();
		
		for ( int i = 0 ; i < number; i++){
			if (1== i){
				System.out.println("tut");
			}
			if (2==i){
				System.out.println("bah");
			}
			for (int k = i*res.length/number; k < (i+1)*res.length/number; k++){
				res[k] = re[i];
			}
		}	
		return res;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = { "Number Of DiscoverPackets", "DestinationID", "NumberOfRounds" };
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
