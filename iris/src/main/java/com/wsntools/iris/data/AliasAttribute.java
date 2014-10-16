/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.wsntools.iris.interfaces.IRIS_Attribute;

public class AliasAttribute implements IRIS_Attribute {

	public static final int ALIAS_USER = 1;
	public static final int ALIAS_GUI = 2;
	public static final int ALIAS_FIXED = 3;
	
	private Model model;
	
	private String name;
	private String description;
	
	private int aliasType;
	
	private Map<Measurement, IRIS_Attribute> mapMeasurementToAttribute;
	
	public AliasAttribute(Model m, String attname, int aliastype, String attdesc) {
		model = m;
		aliasType = aliastype;
		setAliasAttributeName(attname);	
		description = attdesc;
		mapMeasurementToAttribute = new HashMap<Measurement, IRIS_Attribute>();
	}
	
	public void setAliasAttributeName(String newName) {
		
		switch(aliasType) {
		case(ALIAS_USER):
			name = Constants.getAttrUserAliasPrefix() + newName;
			break;
		case(ALIAS_GUI):
			name = Constants.getAttrGuiAliasPrefix() + newName;
			break;
		case(ALIAS_FIXED):
			name = Constants.getAttrFixedAliasPrefix() + newName;
			break;
		}		
	}
	public String getAliasAttributeDescription() {
		
		return description;
	}
	
	public int getAliasType() {
		
		return aliasType;
	}
	
	public boolean isUserAlias() {
		
		return (aliasType == ALIAS_USER);
	}
	
	public boolean isGUIRequiredAlias() {
		
		return (aliasType == ALIAS_GUI);
	}
	
	public boolean isFixedAlias() {
		
		return (aliasType == ALIAS_FIXED);
	}
	
	//Extended Mapping Functions
	public IRIS_Attribute getMappingAttribute(Measurement meas) {
		
		return mapMeasurementToAttribute.get(meas);
	}
	public void setMappingAttribute(Measurement meas, IRIS_Attribute att) {
		
		mapMeasurementToAttribute.put(meas, att);
	}
	public void removeMappingAttribute(Measurement meas) {
		
		mapMeasurementToAttribute.remove(meas);
	}
	
	
	@Override
	public String getAttributeName() {

		return name;
	}

	@Override
	public float[] getValues(Packet[] p) {
		
		IRIS_Attribute mapsTo = mapMeasurementToAttribute.get(model.getCurrentMeasurement());
		if(mapsTo != null) {
			return mapsTo.getValues(p);
		}
		else {
			float[] res = new float[p.length];
			Arrays.fill(res, Constants.getPacketDefaultEmptyNumber());
			return res;
		}
	}

	@Override
	public String[] getValuesString(Packet[] p) {

		IRIS_Attribute mapsTo = mapMeasurementToAttribute.get(model.getCurrentMeasurement());
		if(mapsTo != null) {
			return mapsTo.getValuesString(p);
		}
		else {
			String[] res = new String[p.length];
			Arrays.fill(res, "-");
			return res;
		}
	}

	@Override
	public boolean isDrawable() {

		IRIS_Attribute att = mapMeasurementToAttribute.get(model.getCurrentMeasurement());
		if(att != null) return att.isDrawable();
		else return false;
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
		
		mapMeasurementToAttribute.put(model.getCurrentMeasurement(), att);
	}


	@Override
	public IRIS_Attribute getMappingAttribute() {

		return mapMeasurementToAttribute.get(model.getCurrentMeasurement());
	}
	
	public String toString() {
		return name;
	}
}
