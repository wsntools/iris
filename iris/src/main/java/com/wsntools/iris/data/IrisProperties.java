/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

public class IrisProperties {

	private final boolean logIncommingMessages;
	private final boolean logOutgoingMessages;

	private static IrisProperties instance = null;

	private IrisProperties() {
		IrisPropertyReader propertyReader = IrisPropertyReader.getInstance();
		logIncommingMessages = propertyReader
				.getBooleanValue("logIncommingMessages");
		logOutgoingMessages = propertyReader
				.getBooleanValue("logOutgoingMessages");
	}

	public synchronized static IrisProperties getInstance() {
		if (null == instance) {
			instance = new IrisProperties();
		}
		return instance;
	}

	public boolean getLogIncommingMessages() {
		return logIncommingMessages;
	}

	public boolean getLogOutgoingMessages() {
		return logOutgoingMessages;
	}

}
