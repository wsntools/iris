/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.ArrayList;
import java.util.Arrays;

import com.wsntools.iris.interfaces.IRIS_Attribute;
/**
 * @author Sascha Jungen
 */

public abstract class FixedAttribute {

	//Value caching
	private ArrayList<Packet> packetHistory = new ArrayList<Packet>(Constants.getPacketArrayStartSize());
	private float[] resultHistory = new float[0];
	private String[] stringHistory = new String[0];
	
	private IRIS_Attribute mapTo = null;
	
	public abstract String getAttributeName();

	public float[] getValues(Packet[] p) {
		

		checkPacketsAndComputeValues(p);
		return resultHistory;
	}

	//For output only show filtered values
	public String[] getValuesString(Packet[] p, boolean isInteger) {

		checkPacketsAndComputeValues(p);
		return stringHistory;
	}
	
	public abstract boolean isDrawable();

	public boolean isFunctionAttribute() {
		
		return false;
	}

	public boolean isNormalAttribute() {
		
		return false;
	}

	
	public void setMappingAttribute(IRIS_Attribute att) {
		
		mapTo = att;
		//ResetAllValues
		computeCompleteArrays(packetHistory.toArray(new Packet[packetHistory.size()]));
	}
	
	public IRIS_Attribute getMappingAttribute() {
		
		return mapTo;
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
			if(!equal) {
				computeCompleteArrays(p);				
			}
			return;
		}
		//Check for one new value
		if(p.length == packetHistory.size()+1) {
			boolean equal = true;
			for(int i=packetHistory.size()-1; i>=0 && i>=packetHistory.size()-10; i--) {
				if(!p[i].equals(packetHistory.get(i))) {
					equal = false;
					//break;
				}
			}
			if(equal) {
				packetHistory.add(p[p.length-1]);
				
				resultHistory = Arrays.copyOf(resultHistory, resultHistory.length+1);
				//If there is no mapping reference, try to get attribute value by name
				if(mapTo == null) {
						resultHistory[resultHistory.length-1] = (Float)p[p.length-1].getValue(getAttributeName());
				}			
				//If this attribute is mapped to another attribute, use it to get the values
				else {
					resultHistory[resultHistory.length-1] = (Float)p[p.length-1].getValue(mapTo);
				}
				
				stringHistory = Arrays.copyOf(stringHistory, stringHistory.length+1);
				if(p[p.length-1].hasValue((mapTo == null) ? getAttributeName(): mapTo.getAttributeName())) {
					stringHistory[resultHistory.length-1] = Float.toString(((float)Math.round(resultHistory[resultHistory.length-1]*100))/100);
				}
				else {
					stringHistory[resultHistory.length-1] = "-";
				}
				
				return;
			}
			else {
				computeCompleteArrays(p);
				return;
			}
		}
		computeCompleteArrays(p);
	}

	private void computeCompleteArrays(Packet[] p) {
		
		packetHistory.clear();
		for(Packet pa:p) packetHistory.add(pa);
		
		float[] res1 = new float[p.length];
		
		//If there is no mapping reference, try to get attribute value by name
		if(mapTo == null) {
			for(int i=0; i<p.length; i++) {
				res1[i] = (Float)p[i].getValue(getAttributeName());
			}
		}
		//If this attribute is mapped to another attribute, use it to get the values
		else {
			for(int i=0; i<p.length; i++) {
				res1[i] = (Float)p[i].getValue(mapTo);
			}
		}
		resultHistory = res1;
		//return res;
		
		//float[] val = getValues(p);
		String[] res2 = new String[res1.length];	
			
		for(int i=0; i<res2.length; i++) {
	
			if(p[i].hasValue((mapTo == null) ? getAttributeName(): mapTo.getAttributeName())) {
				res2[i] = Float.toString(((float)Math.round(res1[i]*100))/100);
	
			}
			else {
				res2[i] = "-";
			}
		}
		
		stringHistory = res2;
		//return res;	
	}

}
