/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.interfaces;
/**
 * @author Sascha Jungen
 *
 */
import com.wsntools.iris.data.Measurement;

public interface IRIS_ModuleInfo {

	/**
	 * Return the description of the displayed information (e.g. Package Number)
	 * @return
	 */
	public String getModuleInfoName();
	
	/**
	 * Returns the computed necessary information with the given measurement
	 * @param meas The current measurement
	 * @return
	 */
	public String getResult(Measurement meas);
}
