/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.receiving;

import java.util.HashMap;

public abstract class AbstractCollector {
	public AbstractCollector(String port) {
		this.port = port;
	}
	protected String port;
	public String getPort() {
		return port;
	}
	public abstract void messageReceived(HashMap<String, Object> rec_values);
		
}
