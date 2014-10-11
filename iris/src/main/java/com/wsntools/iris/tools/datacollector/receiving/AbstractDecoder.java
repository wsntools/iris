/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.receiving;

public abstract class AbstractDecoder {
	protected AbstractCollector parent_collector;
	public AbstractDecoder(AbstractCollector parent_collector) {
		this.parent_collector = parent_collector;
	}
	public abstract void decode(byte[] msg);
}
