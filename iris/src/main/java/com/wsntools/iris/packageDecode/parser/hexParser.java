/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.packageDecode.parser;

import java.util.Date;
import java.util.Hashtable;

import net.tinyos.message.Message;

public class hexParser {
	/**
	 * Stores all registered Message Typs to Templates
	 */
	Hashtable<Integer, Template> templates;

	private boolean print = false;

	/**
	 * at which position is the AmType stored in an byte array
	 */
	private static int AmTypeOffset = 7;
	/**
	 * the Amount of Bytes that are used for addressing and type configuration
	 * (before the actual Data Starts)
	 */
	private static int Offset = 8;

	public hexParser() {
		templates = new Hashtable<Integer, Template>();
	}

	/**
	 * True if the given id is free to use
	 * 
	 * @param id
	 * @return
	 */
	public boolean AMTypeFree(int id) {
		return !templates.containsKey(id);
	}

	/**
	 * reads a byteMessage and parses it into the right MessageObject (if
	 * registered by adding a listener with addConvertMessageListener).
	 * 
	 * @param byteMessage
	 *            the received hex value converted into a byte Array;
	 */
	public void readByteArray(byte[] byteMessage) {
		int id = getAmType(byteMessage);
		if (!templates.containsKey(id)) {
			println("AmType " + id + " not found. Message missing in the mote/Received folder");
			return;
		}
		Message m = FillMessage(byteMessage, templates.get(id).template);
		if (print)
			println(m.toString());
		Date d = new Date();
		templates.get(id).listener.messageReceived(d.getTime(), m);
	}

	/**
	 * reads a Hex message , converts it to a byteMessage and parses it into the
	 * right MessageObject (if registered by adding a listener with
	 * addConvertMessageListener).
	 * 
	 * @param String
	 *            Hex Message the received hex value converted into a byte
	 *            Array;
	 */
	public void readHex(String Hex) {
		readByteArray(parseHex(Hex));
	}

	public static byte[] parseHex(String hex) {
		hex = hex.replaceAll(" ", "");
		byte[] out = new byte[hex.length() / 2];
		int i = 0;

		while (!hex.isEmpty()) {
			int te = Integer.decode("0X" + hex.substring(0, 2).toUpperCase());
			// System.out.println("Conv : "+te);
			out[i] = (byte) te;
			hex = hex.substring(2);
			i++;
		}
		return out;
	}

	private Message FillMessage(byte[] byteMessage, Message mes) {

		try {
			Message received = mes.clone(byteMessage.length);
			byte[] data = new byte[byteMessage.length - Offset];
			int p = 0;

			for (int i = Offset; i < byteMessage.length; i++) {
				data[p] = byteMessage[i];
				// System.out.println("byte :" +Byte.toString(byteMessage[i]));
				p++;
			}

			received.dataSet(data);

			// received.dataSet(byteMessage,
			// Offset,0,byteMessage.length-Offset);

			/*
			 * for(byte b:received.dataGet()) {
			 * System.out.println("saved byte : " + Byte.toString(b)); }
			 */

			return received;
		} catch (ArrayIndexOutOfBoundsException e) {
			println("invalid length message received (too long)");
			return null;
		} catch (Exception e) {
			println("couldn't clone message!");
			return null;
		}

	}

	/**
	 * adds the given information to the template hashtable that is used to cast
	 * received message to the right message-child
	 * 
	 * @param listener
	 * @param Message
	 * @return true if successful, false if AmType is already in use;
	 */
	public boolean addConvertMessageListener(ConvertMessageListener listener, Message Message) {
		Template tempTemplate = new Template(Message, listener);
		int id = Message.amType();
		if (templates.containsKey(id))
			return false;
		templates.put(id, tempTemplate);
		return true;
	}

	/**
	 * adds the given information to the template hashtable that is used to cast
	 * received message to the right message-child
	 * 
	 * @param listener
	 * @param Message
	 * @return true if successful, false if AmType is already in use;
	 */
	public boolean addConvertMessageListener(ConvertMessageListener listener, Message Message, int mapOverwrite) {
		Template tempTemplate = new Template(Message, listener);
		int id = Message.amType();

		if (templates.containsKey(mapOverwrite))
			return false;

		templates.put(mapOverwrite, tempTemplate);
		return true;
	}

	/**
	 * converts the AmType from a message byte array to an integer value
	 * 
	 * @param byteMessage
	 *            the message byte Array
	 * @return the AmType given in this byte array.
	 */
	public int getAmType(byte[] byteMessage) {
		try {
			return ((int) byteMessage[AmTypeOffset]) % 128;
		} catch (ArrayIndexOutOfBoundsException exc) {
			System.out.println(byteMessage.length);
			System.out.println(exc.getMessage());
		}
		return 0;
	}

	private void println(String s) {
		System.out.println(s);
	}
}

class Template {
	Message template;
	ConvertMessageListener listener;

	Class<? extends Message> cls;

	Template(Message template, ConvertMessageListener listener) {
		this.template = template;
		this.listener = listener;
		cls = template.getClass();
	}
}
