/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.interfaces;

import com.wsntools.iris.data.Packet;
/**
 * @author Sascha Jungen
 */
public interface IRIS_Attribute {

	
	public String getAttributeName();
		
	//Shall return values of the packets in p, either by using package information or functions 
	public float[] getValues(Packet[] p);
	
	//Shall output the values of 'getValues' as string to decide which kind of number it is (Integer, Float...)
	public String[] getValuesString(Packet[] p);
	
	public boolean isDrawable();
	
	public boolean isFunctionAttribute();
	
	public boolean isNormalAttribute();
	
	//Only for fixed classes
	public void setMappingAttribute(IRIS_Attribute att);
	
	public IRIS_Attribute getMappingAttribute();
}
