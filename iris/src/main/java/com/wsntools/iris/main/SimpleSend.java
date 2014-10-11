/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.main;
import java.io.File;
import java.util.Arrays;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.extensions.MessageWrapper;
import com.wsntools.iris.extensions.SendMessage;

public class SimpleSend {

	static {
	final File sendLog = new File(Constants.getDirSendLog()+"log.csv");
	}
	
	
	
	/**
	 * 0: executing type ("send","listen","record"),1: messagetypeName ,1..n
	 * message parameter
	 * 
	 * @param args
	 *            includes the message type(which has to be included as a class
	 *            file in the mote folder)
	 */
	public static void main(String[] args) {
		MessageWrapper maw = (SendMessage.createMessage(args[0], Arrays.copyOfRange(args, 1, args.length)));
		SendMessage.send(maw);
	}

}
