/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.interfaces;
/**
 * @author Sascha Jungen
 */
public interface IRIS_Observer {

	public static final int EVENT_MEASURE = 1;
	public static final int EVENT_PACKET = 2;
	public static final int EVENT_ATTRIBUTE = 3;
	
	public void updateNewMeasure();
	
	public void updateNewPacket();
	
	public void updateNewAttribute();
}
