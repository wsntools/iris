/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.wsntools.iris.interfaces.IRIS_Attribute;

/**
 * @author Sascha Jungen
 */
public class Packet implements Serializable {

	private Map<String, Float> informationmap = new HashMap<String, Float>();

	
	//Constructor with arguments of all available attributes
	public Packet(IRIS_Attribute[] attr, Float[] values) {
		
		for(int i=0; i<attr.length; i++) {
			informationmap.put(attr[i].getAttributeName(), values[i]);
		}
	}
	
	//Constructor with arguments of all available information
	public Packet(String[] name, Float[] values) {
		
		name = eliminiateDuplicates(name);
		
		for(int i=0; i<name.length; i++) {
			informationmap.put(name[i], values[i]);
		}
	}
	
	private String[] eliminiateDuplicates(String names[]){
		String result[] = new String[names.length];
		
		for (int a = 0; a < names.length; a++){
			for (int b = a+1; b < names.length; b++){
				if (names[a].equals(names[b])){
					names[b] = names[a]+"+";
				}
			}
			result[a] = names[a];
		}
		
		return result;
	}
	
	//Getter
	public float getValue(String attr_name) {
		
		if(informationmap.containsKey(attr_name)) {
			
			return informationmap.get(attr_name);
		}
		else {
			
			return Constants.getPacketDefaultEmptyNumber();
		}
	}
	public float getValue(IRIS_Attribute attr) {
		
		return getValue(attr.getAttributeName());
	}
	
	public String[] getAllValues() {
		
		String[] res = new String[informationmap.keySet().size()];
		res = informationmap.keySet().toArray(res);
		
		return res;
	}
	
	public boolean hasValue(String attr_name) {
		
		return informationmap.containsKey(attr_name);
	}
	public boolean hasValue(IRIS_Attribute attr) {
		
		return informationmap.containsKey(attr.getAttributeName());
	}
	
}
