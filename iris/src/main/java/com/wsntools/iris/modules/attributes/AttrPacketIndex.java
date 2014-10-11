/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.attributes;

import com.wsntools.iris.data.Packet;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class AttrPacketIndex implements IRIS_Attribute {

	@Override
	public String[] getValuesString(Packet[] p) {		
		String[] res = new String[p.length];
		for(int i=0; i<p.length; i++) {
			res[i] = Integer.toString(i);
		}
		return res;
	}

	@Override
	public String getAttributeName() {
		
		return "Index";
	}

	@Override
	public boolean isDrawable() {

		return false;
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
	public float[] getValues(Packet[] p) {
		float[] res = new float[p.length];
		for(int i=0; i<p.length; i++) {
			res[i] = i;
		}
		return res;
	}

	@Override
	public void setMappingAttribute(IRIS_Attribute att) {
		
	}

	@Override
	public IRIS_Attribute getMappingAttribute() {
		return null;
	}

}
