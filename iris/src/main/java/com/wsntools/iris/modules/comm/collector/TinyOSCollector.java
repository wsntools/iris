/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.comm.collector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.wsntools.iris.tools.ModuleLoader;
import com.wsntools.iris.tools.datacollector.receiving.AbstractCollector;
import com.wsntools.iris.tools.datacollector.receiving.AbstractDecoder;
import com.wsntools.iris.tools.datacollector.receiving.MetaDataCollector;

import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;

public class TinyOSCollector extends AbstractCollector implements
		MessageListener {

	private MoteIF mote;
	private Class<Message>[] message_types;
	private com.wsntools.iris.tools.datacollector.receiving.AbstractDecoder decoder;
	private MetaDataCollector parent_collector;
	private Message message;
	public TinyOSCollector(MetaDataCollector parent_collector,String port,Class<AbstractDecoder> decoder_type) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		super(port);
		this.parent_collector = parent_collector;
		decoder = createDecoder(decoder_type);
		message_types = ModuleLoader.getRadioLinkMessageClassesForReceiving();
		startListening();
	}

	public void startListening() {
		for (Class<Message> type : message_types)
			try {
				mote = new MoteIF(BuildSource.makePhoenix(port,
						PrintStreamMessenger.err));
				Message messageType = type.cast(type.newInstance()).getClass().newInstance();
				mote.registerListener(messageType, this);

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	}
	public Message getMessage() {
		return this.message;
	}
	@Override
	public void messageReceived(int arg0, Message arg1) {
		this.message = arg1;
		decoder.decode(this.message.dataGet());
	}
	
	private AbstractDecoder createDecoder(Class<AbstractDecoder> decoder_type) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Constructor<AbstractDecoder> decoder_const = decoder_type.getConstructor(new Class<?>[]{AbstractCollector.class});
		AbstractDecoder decoder = decoder_const.newInstance(new Object[]{this});
		return decoder;
	}

	@Override
	public void messageReceived(HashMap<String, Object> rec_values) {
		parent_collector.messageReceived(rec_values);
	}

	@Override
	public void closeConnection() {
		//Unregister all messages and shutdown the connection
		for (Class<Message> type : message_types)
			try {
				mote = new MoteIF(BuildSource.makePhoenix(port,
						PrintStreamMessenger.err));
				Message messageType = type.cast(type.newInstance()).getClass().newInstance();
				mote.deregisterListener(messageType, this);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		mote.getSource().shutdown();
	}

}
