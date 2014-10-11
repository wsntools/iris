/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.attributes;

import com.wsntools.iris.data.FixedAttribute;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class AttrSenderID extends FixedAttribute implements IRIS_Attribute {

	
	@Override
	public String getAttributeName() {

		return "SenderID";
	}

	@Override
	public float[] getValues(Packet[] p) {

		return super.getValues(p);
	}

	@Override
	public String[] getValuesString(Packet[] p) {
		
		return super.getValuesString(p, true);
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
		
		return false;
	}

}
