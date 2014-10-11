/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.tools.FilterTool;
/**
 * @author Sascha Jungen
 */

public class FunctionAttribute implements IRIS_Attribute {

	public static final int OUTPUT_FLOAT = 1;
	public static final int OUTPUT_INTEGER = 2;
	
	//Value caching
	private ArrayList<Packet> packetHistory = new ArrayList<Packet>(Constants.getPacketArrayStartSize());
	private float[] resultHistory = new float[0];
	private String[] stringHistory = new String[0];
	private boolean historySync = false;
	
	private String name;
	private boolean drawable;
	private int output;
	private Map<IRIS_Attribute, List<float[]>> funcFilter = new HashMap<IRIS_Attribute, List<float[]>>();
	private ArrayList<Integer> funcFilterPassingIndices = new ArrayList<Integer>();
	
	//Store the appliance of functions in a given order
	private ArrayList<IRIS_FunctionModule> functionAppliance = new ArrayList<IRIS_FunctionModule>();
	
	//Store the parameter-list in a given order
	private ArrayList<IRIS_Attribute[]> parameters = new ArrayList<IRIS_Attribute[]>();
	
	//Store the settings-list in a given order
	private ArrayList<float[]> settings = new ArrayList<float[]>();
	
	
	//--Constructor for default function attribute with no values--
	public FunctionAttribute(String attname, boolean draw, int out, IRIS_FunctionModule func) {
		
		name = attname;
		drawable = draw;
		output = out;
		
		functionAppliance.add(func);
		IRIS_Attribute[] param = new IRIS_Attribute[func.getParameterCount()];
		for(int i=0; i<func.getParameterCount(); i++) {
			param[i] = this;
			
		}
		parameters.add(param);
		
		float[] sets = new float[func.getSettingsCount()];
		for(int i=0; i<func.getSettingsCount(); i++) {
			sets[i] = func.getDefaultSettings()[i];
		}
		settings.add(sets);
	}
	
	//--Constructor for loading a saved attribute--
	public FunctionAttribute(String attname, boolean draw, int out, ArrayList<IRIS_FunctionModule> arrAppliance, ArrayList<IRIS_Attribute[]> arrParam, ArrayList<float[]> arrSets, Map<IRIS_Attribute, List<float[]>> map) {
		
		name = attname;
		drawable = draw;
		output = out;
		if(map != null) funcFilter = map;

		functionAppliance = arrAppliance;
		//Before saving, search for null references in parameters(= this-reference)
		for(int i=0; i<arrParam.size(); i++) {
			for(int j=0; j<arrParam.get(i).length; j++) {
				
				if(arrParam.get(i)[j] == null) {
					arrParam.get(i)[j] = this;
				}
			}			
		}
		
		parameters = arrParam;
		settings = arrSets;

	}
	
	//--Function adding--
	public void addNewFunction(String attname, boolean draw, IRIS_FunctionModule func, IRIS_Attribute[] param, float[] sets, int out) {
		name = attname;
		output = out;
		drawable = draw;
		
		functionAppliance.add(func);
		parameters.add(param);
		settings.add(sets);
		
		//Clear packet history
		packetHistory.clear();
	}
	//New Function with not defined values and settings
	public void addNewFunction(String attname, boolean draw, int out, IRIS_FunctionModule func) {
		
		name = attname;
		drawable = draw;
		output = out;
		
		functionAppliance.add(func);
		IRIS_Attribute[] param = new IRIS_Attribute[func.getParameterCount()];
		for(int i=0; i<func.getParameterCount(); i++) {
			param[i] = this;
			
		}
		parameters.add(param);
		
		float[] sets = new float[func.getSettingsCount()];
		for(int i=0; i<func.getSettingsCount(); i++) {
			sets[i] = func.getDefaultSettings()[i];
		}
		settings.add(sets);
		
		//Clear packet history
		packetHistory.clear();
	}
	
	public Object[] getFunctionInformation(int index) {
		
		Object[] res = new Object[2];
		res[0] = parameters.get(index);
		res[1] = settings.get(index); 
		
		return res;
	}
	//Returns prediction value count of the last used function
	public int getPredictionValueCount() {
		
		return functionAppliance.get(functionAppliance.size()-1).getPredictionValues().length;
	}
	public float[] getPredictionValues() {
		
		return functionAppliance.get(functionAppliance.size()-1).getPredictionValues();
	}
	
	public void setFunctionInformation(int index, IRIS_Attribute[] newAttr, float[] newSets) {
	
		//Before saving, search for null references in parameters(= this-reference)
		for(int i=0; i<newAttr.length; i++) {				
			if(newAttr[i] == null) {
				newAttr[i] = this;
			}			
		}
		
		parameters.set(index, newAttr);
		settings.set(index, newSets);
		
		//Clear packet history
		packetHistory.clear();
	}
	
	public void renameFunctionAttribute(String newname) {
		
		name = newname;
	}
	
	public void resetPacketCache() {
		
		packetHistory.clear();
	}
	
	public String[] getAllUsedFunctions() {
		
		String[] res = new String[functionAppliance.size()];
		for(int i=0; i<functionAppliance.size(); i++) {
			res[i] = functionAppliance.get(i).getFunctionName();
		}
		return res;
	}
	public int getUsedFunctionCount() {
		
		return functionAppliance.size();
	}
	//Returns all used Parameter (once per occurence)
	public IRIS_Attribute[] getAllParameter() {
		
		IRIS_Attribute[] res;
		ArrayList<IRIS_Attribute> usedattr = new ArrayList<IRIS_Attribute>();
		for(int i=0; i<parameters.size(); i++) {
			
			res = parameters.get(i);
			for(int k=0; k<res.length; k++) {
				if(!usedattr.contains(res[k])) {
					usedattr.add(res[k]);
				}
			}
		}
		res = new IRIS_Attribute[usedattr.size()];
		return usedattr.toArray(res);
		
	}
	
	//Builds a String of dependencies this attribute has
	public String getParameterDependencies() {
		
		String res = "";
		for(int i=0; i<functionAppliance.size(); i++) {
			
			res = res + (i+1) + ") " + functionAppliance.get(i).getFunctionName() + "\nP: ";
			for(int j=0; j<parameters.get(i).length; j++) {
				res = res + parameters.get(i)[j].getAttributeName() + " ";
			}
			res = res + "\n\n";
		}
		
		return res;
	}
	
	//Builds an array of depencies to other function attributes
	public FunctionAttribute[] getFunctionAttributeDependencies() {
		
		ArrayList<FunctionAttribute> depparam = new ArrayList<FunctionAttribute>();
		IRIS_Attribute[] allparam = getAllParameter();
		for (int i=0; i<allparam.length; i++) {
			if (allparam[i].isFunctionAttribute() & !allparam[i].equals(this)) {
				depparam.add((FunctionAttribute)allparam[i]);
			}
		}
		return depparam.toArray(new FunctionAttribute[depparam.size()]);
	}
	
	//Generates an array of output information
	public Object[] getSaveOutput() {
		
		//Convert RMT_Attributes into Strings
		String[] arr;
		ArrayList<String[]> paramStrings = new ArrayList<String[]>();
		for(int i=0; i<parameters.size(); i++) {
			
			arr = new String[parameters.get(i).length];
			for(int j=0; j<parameters.get(i).length; j++) {
				arr[j] = parameters.get(i)[j].getAttributeName();
			}
			paramStrings.add(arr);
		}
		//Convert RMT_Functions into Strings
		ArrayList<String> functionStrings = new ArrayList<String>();
		for(int i=0; i<functionAppliance.size(); i++) {
			
			functionStrings.add(functionAppliance.get(i).getFunctionName());
		}
		
		Object[] res = {name, drawable, output, functionStrings, paramStrings, settings};
		return res;
	}
	
	private void convertStringsToOutput(String[] arr) {
		
		switch(output) {
			case OUTPUT_FLOAT:
				//Round to two digits
				for(int i=0; i<arr.length; i++) {
					
					try {
						arr[i] = Float.toString(((float)Math.round(Float.parseFloat(arr[i])*100))/100);
					}
					catch (NumberFormatException nfe) {
						arr[i] = "-";
					}
				}
				break;
				
			case OUTPUT_INTEGER:
				for(int i=0; i<arr.length; i++) {
					try {
						arr[i] = Integer.toString((int)Float.parseFloat(arr[i]));
					}
					catch (NumberFormatException nfe) {
						arr[i] = "-";
					}
				}
				break;
		}

		
	}
	
	//--Filter methods--
	//Only set when a function attribute is created
	public void setFunctionFilter(Map<IRIS_Attribute, List<float[]>> fil) {
		
		if(fil!=null) {
			funcFilter = fil;
		}
		else {
			funcFilter.clear();
		}
		//Clear packet history
		packetHistory.clear();
	}

	public Map<IRIS_Attribute, List<float[]>> getFunctionFilter() {
		
		return funcFilter;
	}
	
	public List<Integer> getFilterPassingPacketIndices() {
		
		return funcFilterPassingIndices;
	}
	
	
	//--Interface methods--
	@Override
	public String getAttributeName() {

		return name;
	}

	@Override
	public float[] getValues(Packet[] p) {

		checkPacketsAndComputeValues(p);
		return resultHistory;
	}

	@Override
	public String[] getValuesString(Packet[] p) {

		checkPacketsAndComputeValues(p);
		if(!historySync) {
			computeStringArrays(p);
		}
		return stringHistory;
	}

	@Override
	public boolean isDrawable() {

		return drawable;
	}

	@Override
	public boolean isFunctionAttribute() {

		return true;
	}

	@Override
	public boolean isNormalAttribute() {

		return false;
	}

	@Override
	public void setMappingAttribute(IRIS_Attribute att) {}

	@Override
	public IRIS_Attribute getMappingAttribute() {

		return null;
	}
	
	
	
	//Function to check status of new packet array
	private void checkPacketsAndComputeValues(Packet[] p) {

		//First check equality of last 10 packages
		if(p.length == packetHistory.size()) {
			boolean equal = true;
			for(int i=p.length-1; i>=0 && i>=p.length-10; i--) {
				if(!p[i].equals(packetHistory.get(i))) {
					equal = false;
					break;
				}
			}
			if(equal) {
				return;
			}			
		}
		computeValueArrays(p);
	}
	
	//At function attribute distinguish between Values and Strings - do not compute everything
	private void computeValueArrays(Packet[] p) {

		packetHistory.clear();
		for(Packet pa:p) packetHistory.add(pa);
		
		IRIS_Attribute[] attr;
		float[][] args;
		Packet[] pold = p;
		//Prepare packets matching functionfilter criteria
		if(funcFilter.size() > 0) {
			Object[] arr = FilterTool.getAllFilterPassingPacketsAndIndices(funcFilter, p);
			
			p = new Packet[((ArrayList<Packet>)arr[0]).size()];
			((ArrayList<Packet>)arr[0]).toArray(p);
			funcFilterPassingIndices = ((ArrayList<Integer>)arr[1]);
		}
		else {
			funcFilterPassingIndices.clear();
			for(int i=0; i<p.length; i++) {
				funcFilterPassingIndices.add(i);
			}
		}

		float[] res1 = new float[p.length];
		
		//Try to apply all functions in the given order
		for(int i=0; i<functionAppliance.size(); i++) {			
					
			//Get number of parameter and build up the array needed by the function
			attr = parameters.get(i);
			args = new float[attr.length][];
			for (int j=0; j<attr.length; j++) {
						
				//To prevent endless recursion if this function has itself as parameter
				if(attr[j].equals(this)) {
					args[j] = res1;
				}
				else {
					args[j] = attr[j].getValues(p);
				}				
			}
			res1 = functionAppliance.get(i).computeData(args, settings.get(i));

		}
		resultHistory = res1;
		historySync = false;
		//return res;	
	}
	
	private void computeStringArrays(Packet[] p ) {
		
		float[] val = resultHistory;
		String[] res2;

		//To remember prediction values
		float[] preds = functionAppliance.get(functionAppliance.size()-1).getPredictionValues();
		int ind = 0;
		
		res2 = new String[p.length + ((preds.length > 0) ? (preds.length +1) : 0)];
		for(int i=0; i<p.length; i++) {
							
			if (funcFilter == null | funcFilterPassingIndices.contains(i)) {					
				res2[i] = Float.toString(val[funcFilterPassingIndices.indexOf(i)]);
			}
			//If not, use '-' instead
			else {
				res2[i] = "-";
			}
			//System.out.println(res2[i]);
		}
		
		//Finally add prediction values if available
		if(preds.length > 0) {
			for(int i=res2.length - preds.length; i<res2.length; i++) {
				res2[i] = Float.toString(preds[ind++]); 
			}
			res2[res2.length - preds.length - 1] = "-";
			
			convertStringsToOutput(res2);
			
			res2[res2.length - preds.length - 1] = "+Predict+";
		}
		else {
			convertStringsToOutput(res2);
		}
		
		stringHistory = res2;
		historySync = true;
		//return res;
	}

}
