/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.sending.sender;

import java.io.IOException;

import com.wsntools.iris.tools.datacollector.sending.AbstractEncoder;
import com.wsntools.iris.tools.datacollector.sending.ISender;
import com.wsntools.iris.tools.datacollector.sending.encoder.TinyOSEncoder;

import net.tinyos.message.Message;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.PrintStreamMessenger;

public class TinyOSSender implements ISender {
	private AbstractEncoder encoder;

	public void send(String port, AbstractEncoder encoder) {
		
		this.encoder = encoder;
		byte[] data = encoder.getBytes();
		//create connection to source
		PhoenixSource phoenix = BuildSource.makePhoenix(port,
				PrintStreamMessenger.err);
		MoteIF mif = new MoteIF(phoenix);
		
		//create Message Object from byte array
		Message mes = new Message(data);
		
		if (encoder instanceof TinyOSEncoder) {
			TinyOSEncoder tosenc = (TinyOSEncoder)encoder;
			mes = tosenc.getMessage();
		}
		
		
		
		
		try {
			//send Message
			mif.send(MoteIF.TOS_BCAST_ADDR, mes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//shutdown connection
			mif = null;
			phoenix.shutdown();
			phoenix = null;
		}

	}

}
