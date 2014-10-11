/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.packetexplorer.info;

import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;

public class InfoAttributesDisplayTotal implements IRIS_ModuleInfo {

	@Override
	public String getMeasureInfoName() {
		
		return "Attributes Displayed / Total:";
	}

	@Override
	public String getResult(Measurement meas) {

		return ("TODO new model structure");
	}

}
