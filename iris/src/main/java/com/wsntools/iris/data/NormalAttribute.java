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

public class NormalAttribute implements IRIS_Attribute {

	private String name;
	private boolean drawable;
	
	//Value caching
	private ArrayList<Packet> packetHistory = new ArrayList<Packet>(Constants.getPacketArrayStartSize());
	private float[] resultHistory = new float[0];
	private String[] stringHistory = new String[0];
	
	public NormalAttribute(String attname, boolean draw) {
		
		name = attname;
		drawable = draw;
	}
	
	
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
		return stringHistory;
	}

	@Override
	public boolean isDrawable() {

		return drawable;
	}

	@Override
	public boolean isFunctionAttribute() {

		return false;
	}


	@Override
	public boolean isNormalAttribute() {
		
		return true;
	}


	@Override
	public void setMappingAttribute(IRIS_Attribute att) {
		
	}


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
			if(!equal) {
				computeCompleteArrays(p);				
			}
			else {
				//System.out.println("Same");
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
				resultHistory[resultHistory.length-1] = (Float)p[p.length-1].getValue(getAttributeName());
								
				stringHistory = Arrays.copyOf(stringHistory, stringHistory.length+1);
				if(p[p.length-1].hasValue(getAttributeName())) {
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
		
		for(int i=0; i<p.length; i++) {
			res1[i] = (Float)p[i].getValue(getAttributeName());
		}
		resultHistory = res1;
		//return res;
		
		//float[] val = getValues(p);
		String[] res2 = new String[res1.length];	
			
		for(int i=0; i<res2.length; i++) {

			if(p[i].hasValue(getAttributeName())) {
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
