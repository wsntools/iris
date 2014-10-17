/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.comm.decoder;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.wsntools.iris.modules.comm.collector.TinyOSCollector;
import com.wsntools.iris.tools.datacollector.receiving.AbstractCollector;
import com.wsntools.iris.tools.datacollector.receiving.AbstractDecoder;
import com.wsntools.iris.tools.datacollector.util.MethodWrapper;

import net.tinyos.message.Message;
public class TinyOSDecoder  extends AbstractDecoder {
	public TinyOSDecoder(AbstractCollector parent_collector) {
		super(parent_collector);
	}
	@Override
	public void decode(byte[] data) {
		Message msg;
		if (parent_collector instanceof TinyOSCollector) {
			msg = ((TinyOSCollector)parent_collector).getMessage();
		} else {
			msg = new Message(data);
		}
		MethodWrapper current_wrapper = MethodWrapper.generateMethodWrapper(msg.getClass());
		HashMap<String,Object> rec_values = new HashMap<>();
		if (current_wrapper != null) {
			for (int i=0;i<current_wrapper.getGetter().length;i++) {
				Object value;
				try {
					value = current_wrapper.getGetter()[i].invoke(msg, new Object[] {});
					String name = current_wrapper.getName()[i];
					rec_values.put(name, value);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			parent_collector.messageReceived(rec_values);
		}
	}

	
	
}
