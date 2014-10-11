/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import net.tinyos.message.Message;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.dialogues.SendClassConfig;
import com.wsntools.iris.extensions.typeSupport.TypeSupport;

public class MessageWrapper implements Serializable {

	private Message msg;
	public String name;
	public Class<? extends Message> cls;
	public int address;
	MessageAttributeWrapper maw;

	private Object[] values;

	int size;

	/**
	 * whenever something has changed it is set to true. after creating(in
	 * getMsg()) it is set to false
	 */
	boolean changed;

	public MessageWrapper(Class<? extends Message> cls, String name, int address) {
		this.cls = cls;
		this.name = name;
		this.address = address;
		maw = MessageAttributeWrapper.getMsgAttributeWrapper(cls);
		size = maw.size;
		values = new Object[size];
	}

	/**
	 * gets the message or creates a message if not created yet or changed.
	 * 
	 * @return
	 */
	public Message getMsg() {
		if (changed || msg == null) {
			try {
				msg = cls.newInstance();
				for (int i = 0; i < size; i++)
					if (values[i] != null)
						maw.setter[i].invoke(msg, values[i]);
				changed = false;
				return msg;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		} else
			return msg;
	}

	public Object get(String attribute) {
		return values[maw.index(attribute)];
	}

	public Object get(int attributeIndex) {
		return values[attributeIndex];
	}

	public void set(String attribute, Object value) {
		values[maw.index(attribute)] = value;
		changed = true;
	}

	public void set(int i, Object value) {
		values[i] = value;
		changed = true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(name + "(" + cls.getSimpleName()
				+ ")- (Adr: " + address + ")- ");
		MessageAttributeWrapper maw = MessageAttributeWrapper
				.getMsgAttributeWrapper(cls);
		for (int i = 0; i < maw.names.length; i++)
			sb.append(maw.names[i] + ": " + get(maw.names[i])
					+ (i < maw.names.length - 1 ? ", " : ""));
		return sb.toString();
	}

	public String toPString() {
		StringBuilder sb = new StringBuilder("msg < " + name + " "
				+ cls.getSimpleName() + " " + address);
		MessageAttributeWrapper maw = MessageAttributeWrapper
				.getMsgAttributeWrapper(cls);
		for (int i = 0; i < maw.names.length; i++) {
			String t = null;
			// following bullshit is only for arrays
			Class<?> cls = maw.classes[i];
			if (values[i] == null) {
				sb.append(" -");
				continue;
			}
			if (cls.equals(TypeSupport.INT_ARRAY)
					|| cls.equals(TypeSupport.BYTE_ARRAY)
					|| cls.equals(TypeSupport.SHORT_ARRAY)
					|| cls.equals(TypeSupport.LONG_ARRAY))
				t = SendClassConfig.getArrayToMyString(cls, values[i]);
			// SendClassConfig.getArrayToMyString(cls, MessageWrapper.get(msg,
			// maw.names[i]));
			if (t != null) {
				sb.append(" " + t);
			} else
				// this is for all primites
				// sb.append(" " + String.valueOf(MessageWrapper.get(msg,
				// maw.names[i])));
				sb.append(" " + String.valueOf(values[i]));
		}
		return sb.toString();
	}

	public String toCSVString() {
		StringBuilder sb = new StringBuilder("");
		boolean first = true;
		for (Object value : values) {
			if (first) {
				first = false;
			} else
				sb.append(Constants.getTraceDataSeparator());
			if (!(value == null))
				sb.append(String.valueOf(value));
		}
		return sb.toString();
	}

	/**
	 * this is for decoding.
	 * 
	 * @param msg
	 */
	public void setMessage(Message msg) {
		this.msg = msg;
	}
}
