/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.functions;

import java.util.ArrayList;

import com.wsntools.iris.data.FunctionBasic;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;

public class Func_Quality_LMS extends FunctionBasic implements
		IRIS_FunctionModule {

	private float[] weights = new float[20];
	private ArrayList<Float> last_values = new ArrayList<Float>(200);
	private ArrayList<Float> output_values = new ArrayList<Float>(200);

	// To check whether data is new or old
	private ArrayList<Float> checkdata = new ArrayList<Float>();

	private int windowsize = 10; // Setting 1
	private float mucorrect = (float) 0.01; // Setting 2
	private int threshold = 5; // Setting 3
	private int trainwindow = 20; // Setting 4
	private int learnonerror = 10; // Setting 5
	private int errorTolerance = 0; // Setting 6
	private int predlength = 1; // Setting 7

	private int errorsMade = 0;
	private int tolearn = 0;
	private float predict = 0;
	private boolean trainingDone = false;

	@Override
	public String getFunctionName() {

		return "Least Mean Square";
	}

	@Override
	public String getFunctionDescription() {

		return "Least Mean Square algorithm to predict the behavior of future data results";
	}

	@Override
	public float[] computeData(float[][] arr, float[] set) {

		float[] res;

		boolean newvalue = false;
		boolean reset = false;
		// Check if there is a new value or just old (redraw)
		if (trainingDone && (checkdata.size() < arr[0].length)) {
			newvalue = false;
			System.out.println(checkdata.size() + " - " + arr[0].length);
			for (int i = 0; i < checkdata.size(); i++) {

				if (arr[0][arr[0].length - checkdata.size() + i] != checkdata
						.get(i)) {
					newvalue = true;
					break;
				}
			}
			// If first value is different, decide if it is a new value or the
			// whole set is new
			if (newvalue) {
				for (int i = 0; i < checkdata.size(); i++) {

					if (arr[0][arr[0].length - checkdata.size() + i - 1] != checkdata
							.get(i)) {
						reset = true;
						break;
					}
				}
			}
			// System.out.println("Newvalues = " + newvalue);
			// System.out.println("Reset = " + reset);
		}

		// Check for changes in settings and reset function if changes made
		if (reset || (int) set[0] != windowsize || set[1] != mucorrect
				|| (int) set[2] != threshold || (int) set[3] != trainwindow
				|| (int) set[4] != learnonerror
				|| (int) set[5] != errorTolerance) {
			System.out.println("Function: LMS-Reset due to setting change");
			windowsize = (int) set[0];
			mucorrect = set[1];
			threshold = (int) set[2];
			trainwindow = (int) set[3];
			learnonerror = (int) set[4];
			errorTolerance = (int) set[5];
			predlength = (int) set[6];

			weights = new float[windowsize];
			last_values.clear();
			output_values.clear();
			checkdata.clear();
			predict = 0;
			trainingDone = false;
		}
		// Prediction-mode only if data>windowsize
		// Else wait until all required data are delivered and wait for training
		// the filter (only pass values through)
		if (arr[0].length < trainwindow) {
			return arr[0];
		}

		// If new values have been identified, compute next value
		if (newvalue || !trainingDone) {

			// If the training mode has not been done, ensure convergency [0 <=
			// mu <= 1/Ex]
			// Also train the function to the newest value
			if (!trainingDone) {
				System.out.println("LMS-Training started");
				System.out.println("Chosen Mu-Value: "
						+ Float.toString(mucorrect));

				// Compute approximation of Ex and add first values
				float ex = 0;
				for (int i = 0; i < windowsize; i++) {
					ex += Math.pow(arr[0][i], 2);
					last_values.add(arr[0][i]);
					output_values.add(arr[0][i]);
					checkdata.add(arr[0][i]);
				}
				ex = (float) (ex * (1.0 / (float) trainwindow));
				ex = (float) (1.0 / ex);

				System.out.println("1/Ex = " + Float.toString(ex));
				if (mucorrect < 0 || mucorrect > (ex)) {
					System.out.println("Warning: Mu out of convergency range");
					System.out.println("Recommended value: " + (ex / 100));
				}

				// Set training window size
				tolearn = trainwindow;

				// Compute one value after another to reconstruct prediction
				// values from former data
				for (int i = windowsize; i < arr[0].length - 1; i++) {

					computeNextValue(arr[0][i]);
				}

				trainingDone = true;
				System.out.println("LMS-Training completed");
			}

			computeNextValue(arr[0][arr[0].length - 1]);

			res = new float[output_values.size()];
		}
		// Otherwise just give all previous results back
		else {
			res = new float[output_values.size()];
		}
		// Build up result array
		for (int i = 0; i < output_values.size(); i++) {

			res[i] = output_values.get(i);
		}

		return res;
	}

	@Override
	public boolean isOneValueResult() {

		return false;
	}

	@Override
	public String[] getParameterNames() {

		String[] res = { "Apply to" };
		return res;
	}

	@Override
	public String[] getSettingNames() {

		String[] res = { "Windowsize", "Mu-Correction", "Threshold +/-",
				"Training-Window", "LearnOnErr-Window", "Error-Tolerance",
				"Prediction Count" };
		return res;
	}

	@Override
	public float[] getDefaultSettings() {

		float[] res = { 10, (float) 0.000005, 5, 20, 10, 0, 1 };
		return res;
	}

	@Override
	public boolean hasPredictionValues() {

		return true;
	}

	@Override
	// Compute prediction values for chosen range assuming predictions are
	// correct
	public float[] getPredictionValues() {
		if (!trainingDone) {
			return new float[] {};
		}
		if (predlength == 1) {
			return new float[] { predict };
		} else {
			float[] res = new float[predlength];
			res[0] = predict;
			// Work on a copy of last_values
			ArrayList<Float> cpy_val = new ArrayList<Float>(last_values.size());
			cpy_val.addAll(last_values);
			cpy_val.add(predict);
			float nextpred;
			for (int k = 0; k < predlength - 1; k++) {
				nextpred = 0;
				for (int i = 0; i < weights.length; i++) {
					nextpred += weights[i]
							* cpy_val.get(cpy_val.size() - weights.length + i);
				}
				cpy_val.add(nextpred);
				res[k + 1] = nextpred;
			}
			return res;
		}

	}

	private void learnWithNextValue(float next, float err) {
		// Add newest value to the array;
		last_values.add(next);
		last_values.remove(0);

		// Update weights
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weights[i] + mucorrect
					* last_values.get(last_values.size() - weights.length + i)
					* err;
		}
	}

	private void computeNextValue(float next) {

		// System.out.println(next + " " + errorTolerance + " " + errorsMade);

		// Remember next-value for later checks
		checkdata.add(next);
		checkdata.remove(0);

		// Check correctness of data & prediction (error) (value below
		// threshold)
		float err = next - predict;
		// Exceed tolerance limit or still in window learning mode
		boolean predictNext = false;

		if (0 < tolearn) {
			tolearn--;
			errorsMade = 0;
			learnWithNextValue(next, err);
		} else if ((Math.abs(err) > Math.abs(threshold))) {
			errorsMade++;
			if (errorsMade > errorTolerance) {
				tolearn = learnonerror - 1;
				learnWithNextValue(next, err);
			} else {
				predictNext = true;
			}
		} else {
			if (0 < errorsMade)
				errorsMade--;
			predictNext = true;
		}

		// If for next value the predicted is used
		if (predictNext) {
			// Assume prediction value as input for this step
			last_values.add(predict);
			last_values.remove(0);
		}
		// Add old predicted value to output
		output_values.add(predict);

		// Next predicted value = Sum(i=1 to N){ w(i)[k] * x[k-i] }
		predict = 0;
		for (int i = 0; i < weights.length; i++) {
			predict += weights[i]
					* last_values.get(last_values.size() - weights.length + i);
		}
	}
}
