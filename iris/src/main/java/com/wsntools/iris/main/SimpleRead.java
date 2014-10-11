/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.main;
import java.util.Arrays;

import com.wsntools.iris.extensions.SendMessage;

public class SimpleRead {

	/**
	 * 0: executing type ("send","listen","record"),1: messagetypeName ,1..n
	 * message parameter
	 * 
	 * @param args
	 *            includes the message type(which has to be included as a class
	 *            file in the mote folder)
	 */
	public static void main(String[] args) {
		SendMessage.send(SendMessage.createMessage(args[0], Arrays.copyOfRange(args, 1, args.length)));
	}

}
