/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wsntools.iris.data.Packet;
import com.wsntools.iris.interfaces.IRIS_Attribute;
/**
 * @author Sascha Jungen
 *
 */

public class FilterTool {

	/**
	 * Checks if the text input for setting filters is a valid input
	 * @param input
	 * @return
	 */
	public static boolean isValidFilterTextInput(String input) {
		
		//Allow ranges, negative values and separations with a comma
		return input.matches("((-)?[0123456789]+(.[0123456789]+)?(-(-)?[0123456789]+(.[0123456789]+)?)?)(,(-)?[0123456789]+(.[0123456789]+)?(-(-)?[0123456789]+(.[0123456789]+)?)?)*");
	}
	
	/**
	 * Parses the user specified filter input and converts it to a list of intervals (float arrays with a length of 2)
	 * Also compares already existing intervals and merges them
	 * @param input
	 * @return
	 */
	public static List<float[]> parseFilterTextInput(String input, List<float[]> listOfExistingRanges) {
		//Remove spaces
		input = input.replace(" ", "");
		
		if(!isValidFilterTextInput(input)) return listOfExistingRanges;
		
		//Sort the filter input and eliminate overlapping intervals
		ArrayList<float[]> listOfDisplayedRanges = new ArrayList<float[]>();
		float[] arrRange;
		String[] exp = input.split(",");
		try {
			for(int i=0; i<exp.length; i++) {
				//Check for number range
				if(exp[i].matches("((-)?[0123456789]+(.[0123456789]+)?-(-)?[0123456789]+(.[0123456789]+)?)")) {
					//Separate both values
					int index = exp[i].indexOf("-", 1);
					arrRange = new float[2];
					arrRange[0] = Float.parseFloat(exp[i].substring(0, index));
					arrRange[1] = Float.parseFloat(exp[i].substring(index+1));
					Arrays.sort(arrRange);
					//System.out.println("range");
					//System.out.println(Arrays.toString(arrRange));
					listOfDisplayedRanges.add(arrRange);
				}
				//Check for single number
				else if(exp[i].matches("(-)?[0123456789]+(.[0123456789]+)?")) {
					//System.out.println("single");
					//System.out.println(exp[i]);
					float parsed = Float.parseFloat(exp[i]);
					listOfDisplayedRanges.add(new float[] {parsed, parsed});
				}			
			}
		}
		catch (NumberFormatException nfe) {
			return listOfExistingRanges;
		}
		//Check for already existing filters
		if(listOfExistingRanges != null)
			listOfDisplayedRanges.addAll(listOfExistingRanges);

		//Eliminate/Combine overlapping intervals
		float[] next, compare;
		for(int i=0; i<listOfDisplayedRanges.size(); i++) {
			next = listOfDisplayedRanges.get(i);
			for(int j=i+1; j<listOfDisplayedRanges.size(); j++) {
				compare = listOfDisplayedRanges.get(j);
				//Check for inclusion
				if(next[0] < compare[0] && compare[1] < next[1]) {
					listOfDisplayedRanges.remove(j);
					j--;
					continue;
				}
				else if(compare[0] < next[0] && next[1] < compare[1]) {
					listOfDisplayedRanges.remove(i);
					i--;
					break;
				}
				//Check for intersections
				else if(next[0] < compare[0] && next[1] > compare[0] && next[1] < compare[1]) {
					next[1] = compare[1];
					listOfDisplayedRanges.remove(j);
					j--;
					continue;
				}
				else if(compare[0] < next[0] && compare[1] > next[0] && compare[1] < next[1]) {
					compare[1] = next[1];
					listOfDisplayedRanges.remove(i);
					i--;
					break;
				}
			}
		}
		
		return listOfDisplayedRanges;
	}
	
	
	
	public static ArrayList<Integer> getAllFilterPassingIndices(Map<IRIS_Attribute, List<float[]>> map, Packet[] p) {
		
		ArrayList<Integer> res = new ArrayList<Integer>();			
		
		IRIS_Attribute[] attrToCheck = new IRIS_Attribute[map.size()];
		map.keySet().toArray(attrToCheck);
		boolean packetMatch, attrMatch;
		float valueToCheck;

		//Load valuelists before to reduce computational overhead of RMT_Attribute.getValues()
		float[][] values = new float[attrToCheck.length][];
		for(int i=0; i<attrToCheck.length; i++) {
			values[i] = attrToCheck[i].getValues(p);
		}
		
		//Compare packet content to filter and sort out all non matching entries
		for(int i=0; i<p.length; i++) {
			packetMatch = true;
			for(int j=0; j<attrToCheck.length; j++) {
				attrMatch = false;
				valueToCheck = values[j][i];
				for(float[] arrFilter: map.get(attrToCheck[j])) {
					if((arrFilter[0] <= valueToCheck) && (valueToCheck <= arrFilter[1])) {
						attrMatch = true;
						break;
					}
				}
				if(!attrMatch) {
					packetMatch = false;
					break;
				}
			}
			
			if(packetMatch) {
				res.add(i);
			}			
		}
		
		return res;
	}
	
	public static ArrayList<Packet> getAllFilterPassingPackets(Map<IRIS_Attribute, List<float[]>> map, Packet[] p) {
		
		ArrayList<Packet> res = new ArrayList<Packet>();			
		
		IRIS_Attribute[] attrToCheck = new IRIS_Attribute[map.size()];
		map.keySet().toArray(attrToCheck);
		boolean packetMatch, attrMatch;
		float valueToCheck;

		//Load valuelists before to reduce computational overhead of RMT_Attribute.getValues()
		float[][] values = new float[attrToCheck.length][];
		for(int i=0; i<attrToCheck.length; i++) {
			values[i] = attrToCheck[i].getValues(p);
		}
		
		//Compare packet content to filter and sort out all non matching entries
		for(int i=0; i<p.length; i++) {
			packetMatch = true;
			for(int j=0; j<attrToCheck.length; j++) {
				attrMatch = false;
				valueToCheck = values[j][i];
				for(float[] arrFilter: map.get(attrToCheck[j])) {
					if((arrFilter[0] <= valueToCheck) && (valueToCheck <= arrFilter[1])) {
						attrMatch = true;
						break;
					}
				}
				if(!attrMatch) {
					packetMatch = false;
					break;
				}
			}
			
			if(packetMatch) {
				res.add(p[i]);
			}			
		}
		
		return res;
	}
	
	public static Object[] getAllFilterPassingPacketsAndIndices(Map <IRIS_Attribute, List<float[]>> map, Packet[] p) {
		
		ArrayList<Packet> arrPacks = new ArrayList<Packet>();
		ArrayList<Integer> arrIndex = new ArrayList<Integer>();
		IRIS_Attribute[] attrToCheck = new IRIS_Attribute[map.size()];
		map.keySet().toArray(attrToCheck);
		boolean packetMatch, attrMatch;
		float valueToCheck;

		//Load valuelists before to reduce computational overhead of RMT_Attribute.getValues()
		float[][] values = new float[attrToCheck.length][];
		for(int i=0; i<attrToCheck.length; i++) {
			values[i] = attrToCheck[i].getValues(p);
		}
		
		//Compare packet content to filter and sort out all non matching entries
		for(int i=0; i<p.length; i++) {
			packetMatch = true;
			for(int j=0; j<attrToCheck.length; j++) {
				attrMatch = false;
				valueToCheck = values[j][i];
				for(float[] arrFilter: map.get(attrToCheck[j])) {
					if((arrFilter[0] <= valueToCheck) && (valueToCheck <= arrFilter[1])) {
						attrMatch = true;
						break;
					}
				}
				if(!attrMatch) {
					packetMatch = false;
					break;
				}
			}
			
			if(packetMatch) {
				arrPacks.add(p[i]);
				arrIndex.add(i);
			}			
		}
		
		return new Object[] {arrPacks, arrIndex};
	}
	
	public static float[][] separateValuesByFilter(Packet[] p, IRIS_Attribute toSort, IRIS_Attribute filter, ArrayList<String> naming) {
		
		float[] valSort = toSort.getValues(p);
		float[] valFilter = filter.getValues(p);
		
		if(valSort.length != valFilter.length) {
			return new float[0][];
		}
		
		HashMap<Float, ArrayList<Float>> map = new HashMap<Float, ArrayList<Float>>();
		
		//Set up a list for all filter values
		for(Float f:valFilter) {
			if(!map.containsKey(f)) {
				map.put(f, new ArrayList<Float>());				
			}
		}
		//Insert all values to sort
		for(int i=0; i<valSort.length; i++) {
			map.get(valFilter[i]).add(valSort[i]);
		}
		
		//Convert lists into result-array
		float[][] res = new float[map.size()][];
		Float[] keys = map.keySet().toArray(new Float[map.size()]);
		Arrays.sort(keys);
		ArrayList<Float> arr;
		for(int i=0; i<keys.length; i++) {
			arr = map.get(keys[i]);
			naming.add(keys[i].toString());
			res[i] = new float[arr.size()];
			for(int j=0; j<arr.size(); j++) {
				res[i][j] = arr.get(j).floatValue();
			}
		}
		
		return res;
	}
}
