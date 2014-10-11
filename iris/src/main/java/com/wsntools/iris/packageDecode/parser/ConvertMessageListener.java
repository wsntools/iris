/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.packageDecode.parser;

import net.tinyos.message.Message;

/**
 * recieves a new Converted Message from an hexParser
 * @author Marvin Baudewig
 *
 */
public interface ConvertMessageListener {

	/**
	 * called when this Type of Message was parsed by a hexParser
	 * @param timeStamp
	 * The timeStamp when this message was received
	 * @param m
	 * the Message that was received
	 */
	 public void messageReceived(long timeStamp, Message m);
}
