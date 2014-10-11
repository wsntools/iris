/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.SendClassConfig;

import net.tinyos.message.Message;
import net.tinyos.message.MoteIF;
import net.tinyos.util.PrintStreamMessenger;

public class SendMessage {

	
	public static boolean send(MessageWrapper msg) {


		MoteIF mote = new MoteIF(PrintStreamMessenger.err);
		System.out.println(msg);
		try {
			mote.send(msg.address, msg.getMsg());
			mote.getSource().shutdown();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static MessageWrapper createMessage(String type, String[] params) {
		// load Class // TODO lookup
		Class<? extends Message> msgClass = MessageAttributeWrapper.getMsgAttributeWrapper(type).cls;
		try {
			MessageWrapper mw = new MessageWrapper(msgClass, "MSG:" + msgClass.getSimpleName(), 0);
			MessageAttributeWrapper maw = MessageAttributeWrapper.getMsgAttributeWrapper(msgClass);
			//			for (int i = 0; i < params.length; i++)
			//				System.out.println(params[i]);
			if (!(params.length - 1 == maw.classes.length)) { //  -1 because of the address
				System.out.println(Arrays.toString(params));
				throw new ArrayIndexOutOfBoundsException("Wrong number of parameters. Expected: " + maw.classes.length
						+ " .There are: " + (params.length - 1));
			}
			Object[] convertedParams = new Object[params.length - 1];
			for (int i = 0; i < params.length - 1; i++) {
				//				System.out.println(i);
				//				if (params[i + 1] instanceof String)
				if (!(params[i + 1].equals("-"))) {
					convertedParams[i] = SendClassConfig.getValue(maw.classes[i], (String) params[i + 1]);
					mw.set(maw.names[i], convertedParams[i]);
					Model.logger.debug("setting " + maw.names[i] + " to " + (String) params[i + 1]);
				} else {
					Model.logger.debug("skipping " + maw.names[i]);
				}
			}
			mw.address = (Integer) SendClassConfig.getValue(Integer.TYPE, (String) params[0]);
			return mw;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
