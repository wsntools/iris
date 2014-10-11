/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.List;
import java.util.Map;

import com.wsntools.iris.interfaces.IRIS_Attribute;

/**
 * General class for storing measurement-wide filter settings - once instantiated, fields
 * are read-only
 * @author Sascha Jungen
 *
 */
public class FilterSettings {

	private final String filterName;
	private final Map<IRIS_Attribute, List<float[]>> filterMap;
	
	public FilterSettings(String name, Map<IRIS_Attribute, List<float[]>> map) {
		filterName = name;
		filterMap = map;
	}
	
	public String getFilterName() {
		return filterName;
	}
	
	public Map<IRIS_Attribute, List<float[]>> getFilterMap() {
		return filterMap;
	}
}
