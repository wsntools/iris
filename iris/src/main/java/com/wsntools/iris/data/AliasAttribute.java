/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.wsntools.iris.interfaces.IRIS_Attribute;

public class AliasAttribute implements IRIS_Attribute {

	private Model model;
	
	private String name;
	private String description;
	
	private boolean isGUIRequired;
	
	private Map<Measurement, IRIS_Attribute> mapMeasurementToAttribute;
	
	public AliasAttribute(Model m, String attname, boolean guirequired, String attdesc) {
		model = m;
		name = (guirequired ? Constants.getAttrGuiAliasPrefix() : Constants.getAttrUserAliasPrefix()) + attname;		
		description = attdesc;
		isGUIRequired = guirequired;
		mapMeasurementToAttribute = new HashMap<Measurement, IRIS_Attribute>();
	}
	
	public void setAliasAttributeName(String newName) {
		
		name = (isGUIRequired ? Constants.getAttrGuiAliasPrefix() : Constants.getAttrUserAliasPrefix()) + newName;
	}
	public String getAliasAttributeDescription() {
		
		return description;
	}
	
	public boolean isGUIRequiredAlias() {
		
		return isGUIRequired;
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
