/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;

import org.apache.log4j.Logger;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;

/**
 * @author Sascha Jungen M#2242754
 * 
 */
public class DataCollector implements MessageListener {

	private final static Logger logger = Logger.getLogger("Receiver");

	private Model model;
	private static boolean listenModeActivated = false;
	// Connection object
	private MoteIF mote;

	private Class<Message>[] messages = new Class[0];

	private MethodWrapper methodWraper[] = null;

	private Packet[] buffer;
	private int bufpointer;

	private boolean isActive = false;

	private File quickDumpFolder = null;
	private File quickDumpMoteFolder = null;
	private boolean quickDump;
	private boolean ddsDump;
	// private ArrayList<File> quickDumpFiles;
	private HashMap<Integer, BufferedWriter> quickDumpWriter;

	public DataCollector(Model m) {
		model = m;
//		this.ddsDump = m.getDDSMode();
//		if (this.ddsDump) {
////			this.sensorPositionDataPublisher = new ImacSensorPositionDataPublisher();
//			this.sensorPositionDataPublisher = ImacSensorPositionDataPublisher.getInstance();
//			
//		}
	}

	public void messageReceived(int to, Message mess) {
		// message are passed, if dds flag is set
//		if (ddsDump) {
//			messageDDSDump(to, mess);
//			return;
//		}
		if (quickDump) {
			messageQuickDump(to, mess);
			return;
		}
		int amType = mess.amType();
		int amSource = mess.getSerialPacket().get_header_src();
		long time = System.currentTimeMillis();

		logger.info("received a message: " + mess.toString().getClass());
		logger.debug("" + mess);
		logger.info("received from: " + amSource + " to: "
				+ mess.getSerialPacket().get_header_dest());

		ArrayList<Float> values = new ArrayList<Float>();
		ArrayList<String> value_names = new ArrayList<String>();

		MethodWrapper currentWrapper = null;

		for (MethodWrapper mw : methodWraper) {
			if (mw.message.getName().equals(mess.getClass().getName())) {
				currentWrapper = mw;
				break;
			}
		}

		Method m_dataLength = currentWrapper.dataLength;

		for (int i = 0; i < currentWrapper.getter.length; i++) {

			Method getter = currentWrapper.getter[i];
			Method isArray = currentWrapper.isArray[i];
			String name = currentWrapper.name[i];
			Method offset = currentWrapper.getOffset[i];

			Method elementSize = currentWrapper.elementSize[i];// non array ->
																// null
			Method getElement = currentWrapper.getElement[i];// non array ->
																// null
			Method arrayLength = currentWrapper.arrayLength[i]; // non array ->
																// null

			int numberOfValues = 1;
			Object[] args = new Object[0];

			logger.debug("trying to read " + getter);

			try {

				logger.debug("\tusing: " + isArray.getName());

				if (true == ((Boolean) isArray.invoke(mess, args))
						.booleanValue()) {

					logger.debug("\tfound array ");

					if (null == arrayLength) {

						logger.debug("\tfound dynamic array/structure");

						int dataLength = ((Integer) m_dataLength.invoke(mess,
								args));
						int numStructElements = currentWrapper.getter.length
								- (i);

						int elementSizes[] = new int[numStructElements];

						int sizeOfStruct = 0;

						// i is the position of the current element and getter
						// has the totla number of elements
						int highestOffset = Integer.MIN_VALUE;
						int lowestOfset = Integer.MAX_VALUE;

						ArrayList<Method> tempMethods_Offset = new ArrayList<Method>();
						ArrayList<Method> tempMethods_GetElement = new ArrayList<Method>();

						tempMethods_Offset.clear();
						tempMethods_GetElement.clear();

						for (int a = i; a < currentWrapper.elementSize.length; a++) {
							Method tempMethod;
							int temp = (Integer) currentWrapper.elementSize[a]
									.invoke(mess, args);
							sizeOfStruct += temp;
							elementSizes[(a - i)] = temp;

							tempMethod = currentWrapper.getOffset[a];
							tempMethods_Offset.add(tempMethod);

							// tempMethod = currentWrapper.message.getMethod(
							// "getElement_" + currentWrapper.name[a],
							// int.class);
							tempMethods_GetElement.add(tempMethod);

							int temp2 = (Integer) tempMethod.invoke(mess,
									(Integer) 0);

							// int temp2 = (Integer)
							// currentWrapper.getOffset[a-i+arrayIndex-1].invoke(mess,
							// (Integer) 0);
							highestOffset = highestOffset > temp2 ? highestOffset
									: temp2;
							lowestOfset = lowestOfset < temp2 ? lowestOfset
									: temp2;
						}

						Method m_Offset[] = tempMethods_Offset
								.toArray(new Method[tempMethods_Offset.size()]);
						Method m_GetElement[] = tempMethods_GetElement
								.toArray(new Method[tempMethods_GetElement
										.size()]);

						// TODO change static -2
						for (int a = 0; a < m_Offset.length; a++) {
							if (dataLength - 2 >= (Integer) m_Offset[a].invoke(
									mess, (Integer) a)) {
								int tempvalue = (Integer) m_GetElement[a]
										.invoke(mess, a);
								values.add((float) tempvalue);
								value_names.add((String) m_Offset[a].getName());
							}
						}
						System.out.println("");

						// values.add(null);
						// value_names.add(null);

						System.out.println("");

						System.out.println("dataLength: " + dataLength);
						System.out.println("lowest: " + lowestOfset);
						System.out.println("highest: " + highestOffset);
						System.out.println("size: " + sizeOfStruct);

						int remainingFields = dataLength - highestOffset;

						break;

					} else {

						// numberOfValues = ((Integer) arrayLength.invoke(mess,
						// args))
						// .intValue();
						byte byteArrayContent[] = null;
						short shortArrayContent[] = null;
						int intArrayContent[] = null;
						long longArrayContent[] = null;

						float floatArrayResult[] = null;

						if (true == getter.getReturnType().equals(
								new int[0].getClass())) {

							intArrayContent = ((int[]) getter
									.invoke(mess, args));
							floatArrayResult = new float[intArrayContent.length];
							for (int a = 0; a < intArrayContent.length; a++) {
								floatArrayResult[a] = (float) intArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new short[0].getClass())) {
							shortArrayContent = ((short[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[shortArrayContent.length];
							for (int a = 0; a < shortArrayContent.length; a++) {
								floatArrayResult[a] = (float) shortArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new byte[0].getClass())) {
							byteArrayContent = ((byte[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[byteArrayContent.length];
							for (int a = 0; a < byteArrayContent.length; a++) {
								floatArrayResult[a] = (float) byteArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new long[0].getClass())) {
							longArrayContent = ((long[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[longArrayContent.length];
							for (int a = 0; a < longArrayContent.length; a++) {
								floatArrayResult[a] = (float) longArrayContent[a];
							}
						} else

						{
							System.err
									.println("only integer arrays are supported yet");
							System.err.println("failed to read: " + getter);
						}

						for (int a = 0; a < floatArrayResult.length; a++) {
							values.add(floatArrayResult[a]);
							value_names.add(name + "[" + a + "]");
							// TODO put into values
						}
					}
				} else {
					logger.debug("\tfound primitive ");
					// Object[] args = new Object[0];
					if (getter.getReturnType().equals(short.class)) {
						values.add(((Short) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(int.class)) {
						values.add(((Integer) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(byte.class)) {
						values.add(((Byte) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(long.class)) {
						values.add(((Long) getter.invoke(mess, args))
								.floatValue());
					}
					value_names.add(name);
				}
			} catch (Exception e) {
				System.err.println("ERROR: Could not read out value'"
						+ getter.getName() + "'");
				e.printStackTrace();
			}
		}

		values.add((float) amType);
		value_names.add("AM-Type");

		values.add((float) amSource);
		value_names.add("AM-Source");

		values.add((float) time);
		value_names.add("Time");

		Float[] arrval = new Float[values.size()];
		String[] arrvalname = new String[value_names.size()];

		currentWrapper.backup.writeFirstLine(value_names.toArray(arrvalname));
		currentWrapper.backup.writeNextLine(values);

		model.getCurrentMeasurement().addNeighbour(amSource, time);

		if (buffer == null)
			model.addPacket(new Packet(value_names.toArray(arrvalname), values
					.toArray(arrval)));
		else {
			addToBuffer(new Packet(value_names.toArray(arrvalname),
					values.toArray(arrval)));
		}
	}

//	/**
//	 * Publishes a TinyOS Message as DDS Message
//	 * Currently only amType 40 is supported.
//	 * @param to
//	 * @param mess
//	 */
//	public void messageDDSDump(int to, Message mess) {
//		int amType = mess.amType();
//		if (amType == dummySerialSender.AM_TYPE) {
//			this.sensorPositionDataPublisher.publish(mess);
//		}
//	}

	/**
	 * copy of message received but only writes into a file and does not add it
	 * into the table
	 * 
	 * @author ramin
	 * @param to
	 * @param mess
	 */
	public void messageQuickDump(int to, Message mess) {

		int amType = mess.amType();
		int amSource = mess.getSerialPacket().get_header_src();
		long time = System.currentTimeMillis();

		logger.info("received a message: " + mess.toString().getClass());
		logger.debug("" + mess);
		logger.info("received from: " + amSource + " to: "
				+ mess.getSerialPacket().get_header_dest());

		ArrayList<Float> values = new ArrayList<Float>();
		ArrayList<String> value_names = new ArrayList<String>();

		MethodWrapper currentWrapper = null;

		for (MethodWrapper mw : methodWraper) {
			if (mw.message.getName().equals(mess.getClass().getName())) {
				currentWrapper = mw;
				break;
			}
		}

		Method m_dataLength = currentWrapper.dataLength;

		for (int i = 0; i < currentWrapper.getter.length; i++) {

			Method getter = currentWrapper.getter[i];
			Method isArray = currentWrapper.isArray[i];
			String name = currentWrapper.name[i];
			Method offset = currentWrapper.getOffset[i];

			Method elementSize = currentWrapper.elementSize[i];// non array ->
																// null
			Method getElement = currentWrapper.getElement[i];// non array ->
																// null
			Method arrayLength = currentWrapper.arrayLength[i]; // non array ->
																// null

			int numberOfValues = 1;
			Object[] args = new Object[0];

			logger.debug("trying to read " + getter);

			try {

				logger.debug("\tusing: " + isArray.getName());

				if (true == ((Boolean) isArray.invoke(mess, args))
						.booleanValue()) {

					logger.debug("\tfound array ");

					if (null == arrayLength) {

						logger.debug("\tfound dynamic array/structure");

						int dataLength = ((Integer) m_dataLength.invoke(mess,
								args));
						int numStructElements = currentWrapper.getter.length
								- (i);

						int elementSizes[] = new int[numStructElements];

						int sizeOfStruct = 0;

						// i is the position of the current element and getter
						// has the totla number of elements
						int highestOffset = Integer.MIN_VALUE;
						int lowestOfset = Integer.MAX_VALUE;

						ArrayList<Method> tempMethods_Offset = new ArrayList<Method>();
						ArrayList<Method> tempMethods_GetElement = new ArrayList<Method>();

						tempMethods_Offset.clear();
						tempMethods_GetElement.clear();

						for (int a = i; a < currentWrapper.elementSize.length; a++) {
							Method tempMethod;
							int temp = (Integer) currentWrapper.elementSize[a]
									.invoke(mess, args);
							sizeOfStruct += temp;
							elementSizes[(a - i)] = temp;

							tempMethod = currentWrapper.getOffset[a];
							tempMethods_Offset.add(tempMethod);

							// tempMethod = currentWrapper.message.getMethod(
							// "getElement_" + currentWrapper.name[a],
							// int.class);
							tempMethods_GetElement.add(tempMethod);

							int temp2 = (Integer) tempMethod.invoke(mess,
									(Integer) 0);

							// int temp2 = (Integer)
							// currentWrapper.getOffset[a-i+arrayIndex-1].invoke(mess,
							// (Integer) 0);
							highestOffset = highestOffset > temp2 ? highestOffset
									: temp2;
							lowestOfset = lowestOfset < temp2 ? lowestOfset
									: temp2;
						}

						Method m_Offset[] = tempMethods_Offset
								.toArray(new Method[tempMethods_Offset.size()]);
						Method m_GetElement[] = tempMethods_GetElement
								.toArray(new Method[tempMethods_GetElement
										.size()]);

						// TODO change static -2
						for (int a = 0; a < m_Offset.length; a++) {
							if (dataLength - 2 >= (Integer) m_Offset[a].invoke(
									mess, (Integer) a)) {
								int tempvalue = (Integer) m_GetElement[a]
										.invoke(mess, a);
								values.add((float) tempvalue);
								value_names.add((String) m_Offset[a].getName());
							}
						}
						// System.out.println("");
						//
						// // values.add(null);
						// // value_names.add(null);
						//
						// System.out.println("");
						//
						// System.out.println("dataLength: " + dataLength);
						// System.out.println("lowest: " + lowestOfset);
						// System.out.println("highest: " + highestOffset);
						// System.out.println("size: " + sizeOfStruct);

						int remainingFields = dataLength - highestOffset;

						break;

					} else {

						// numberOfValues = ((Integer) arrayLength.invoke(mess,
						// args))
						// .intValue();
						byte byteArrayContent[] = null;
						short shortArrayContent[] = null;
						int intArrayContent[] = null;
						long longArrayContent[] = null;

						float floatArrayResult[] = null;

						if (true == getter.getReturnType().equals(
								new int[0].getClass())) {

							intArrayContent = ((int[]) getter
									.invoke(mess, args));
							floatArrayResult = new float[intArrayContent.length];
							for (int a = 0; a < intArrayContent.length; a++) {
								floatArrayResult[a] = (float) intArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new short[0].getClass())) {
							shortArrayContent = ((short[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[shortArrayContent.length];
							for (int a = 0; a < shortArrayContent.length; a++) {
								floatArrayResult[a] = (float) shortArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new byte[0].getClass())) {
							byteArrayContent = ((byte[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[byteArrayContent.length];
							for (int a = 0; a < byteArrayContent.length; a++) {
								floatArrayResult[a] = (float) byteArrayContent[a];
							}
						} else if (true == getter.getReturnType().equals(
								new long[0].getClass())) {
							longArrayContent = ((long[]) getter.invoke(mess,
									args));
							floatArrayResult = new float[longArrayContent.length];
							for (int a = 0; a < longArrayContent.length; a++) {
								floatArrayResult[a] = (float) longArrayContent[a];
							}
						} else

						{
							System.err
									.println("only integer arrays are supported yet");
							System.err.println("failed to read: " + getter);
						}

						for (int a = 0; a < floatArrayResult.length; a++) {
							values.add(floatArrayResult[a]);
							value_names.add(name + "[" + a + "]");
							// TODO put into values
						}
					}
				} else {
					logger.debug("\tfound primitive ");
					// Object[] args = new Object[0];
					if (getter.getReturnType().equals(short.class)) {
						values.add(((Short) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(int.class)) {
						values.add(((Integer) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(byte.class)) {
						values.add(((Byte) getter.invoke(mess, args))
								.floatValue());
					} else if (getter.getReturnType().equals(long.class)) {
						values.add(((Long) getter.invoke(mess, args))
								.floatValue());
					}
					value_names.add(name);
				}
			} catch (Exception e) {
				System.err.println("ERROR: Could not read out value'"
						+ getter.getName() + "'");
				e.printStackTrace();
			}
		}

		values.add((float) amType);
		value_names.add("AM-Type");

		values.add((float) amSource);
		value_names.add("AM-Source");

		values.add((float) time);
		value_names.add("Time");

		Float[] arrval = new Float[values.size()];
		String[] arrvalname = new String[value_names.size()];

		if (!quickDumpWriter.containsKey(amType)) {
			try {
				quickDumpWriter.put(amType, new BufferedWriter(new FileWriter(
						new File(quickDumpMoteFolder + Constants.getSep()
								+ currentWrapper.message.getSimpleName()
								+ ".txt"))));
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < arrvalname.length; i++) {
					if (i > 0)
						sb.append(Constants.getTraceAttributeSeparator()
								+ arrvalname[i]);
					else
						sb.append(arrvalname[i]);
				}
				try {
					quickDumpWriter.get(amType).write(sb.toString());
					quickDumpWriter.get(amType).newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0)
				sb.append(Constants.getTraceDataSeparator() + values.get(i));
			else
				sb.append(values.get(i));
		}
		try {
			quickDumpWriter.get(amType).write(sb.toString());
			quickDumpWriter.get(amType).newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// model.getCurrentMeasurement().addNeighbour(amSource, time);
		//
		// if (buffer == null)
		// model.addPacket(new Packet(value_names.toArray(arrvalname), values
		// .toArray(arrval)));
		// else {
		// addToBuffer(new Packet(value_names.toArray(arrvalname),
		// values.toArray(arrval)));
		// }
	}

	/**
	 * This function generates an Array of Methodwrappers that inherits all
	 * public functions with the followin substrings: - get_ as "m_getter" -
	 * *postfix after get_ as "m_name" - number of elements as "m_arrayLength" -
	 * isArray as "m_isArray"
	 */
	private void generateMethodWrapper() {
		// Collect all used message objects
		messages = ModuleLoader.getRadioLinkMessageClassesForReceiving();

		// Find all getter-methods of all message classes
		ArrayList<Method> m_getter = new ArrayList<Method>();
		ArrayList<String> m_name = new ArrayList<String>();

		methodWraper = new MethodWrapper[messages.length];

		// go through every message class
		for (int i = 0; i < messages.length; i++) {
			m_getter.clear();
			m_name.clear();
			Method m_dataLength = null;

			// go through every method of the current message class
			Method[] meth = messages[i].getDeclaredMethods();
			for (Method m : meth) {
				if (m.getParameterTypes().length == 0) {

					if (m.getName().startsWith("get_")) {
						m_getter.add(m);
						m_name.add(m.getName().replaceFirst("get_", "")
								.replaceAll("()", ""));
					}

				}
			}
			try {
				m_dataLength = messages[i].getSuperclass().getMethod(
						"dataLength", null);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Method m_getter_array[] = m_getter.toArray(new Method[m_getter
					.size()]);
			String m_name_array[] = m_name.toArray(new String[m_name.size()]);

			Method m_isArray_array[] = m_getter.toArray(new Method[m_getter
					.size()]);
			Method m_arrayLength_array[] = m_getter.toArray(new Method[m_getter
					.size()]);
			Method m_elementSize_array[] = m_getter.toArray(new Method[m_getter
					.size()]);
			Method m_getElement_array[] = m_getter.toArray(new Method[m_getter
					.size()]);
			Method m_getOffset_array[] = m_getter.toArray(new Method[m_getter
					.size()]);

			for (int a = 0; a < m_name_array.length; a++) {
				m_isArray_array[a] = getMethod("isArray_" + m_name_array[a],
						messages[i]);
				m_arrayLength_array[a] = getMethod("numElements_"
						+ m_name_array[a], messages[i]);
				m_elementSize_array[a] = getMethod("elementSize_"
						+ m_name_array[a], messages[i]);

				try {
					m_getElement_array[a] = messages[i].getMethod("getElement_"
							+ m_name_array[a], int.class);
				} catch (SecurityException e1) {
					m_getElement_array[a] = null;
				} catch (NoSuchMethodException e1) {
					m_getElement_array[a] = null;
				}
				try {
					m_getOffset_array[a] = messages[i].getMethod("offset_"
							+ m_name_array[a], int.class);
				} catch (SecurityException e) {
					m_getOffset_array[a] = null;
				} catch (NoSuchMethodException e) {
					m_getOffset_array[a] = null;
				}
			}

			// private MethodWrapper(Method[] getter, Method[] isArray,
			// Method[] arrayLength, String[] name, Class<Message> message,
			// BackgroundWriter backup, Method dataLength,
			// Method[] elementSize, Method[] getElement, Method[] getOffset) {
			//

			methodWraper[i] = new MethodWrapper(m_getter_array,
					m_isArray_array, m_arrayLength_array, m_name_array,
					messages[i], new BackgroundWriter(model
							.getCurrentMeasurement().getMeasureName()
							+ "-"
							+ messages[i].getSimpleName()), m_dataLength,
					m_elementSize_array, m_getElement_array, m_getOffset_array);

		}
	}

	private Method getMethod(String name, Class<Message> message) {
		Method result = null;
		try {
			result = message.getMethod(name, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			result = null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			result = null;
		}
		return result;
	}

	/**
	 * check whether buffer is enabled
	 */
	private void configureBuffer() {
		if (model.getRecordBufferSize() != -1) {
			buffer = new Packet[model.getRecordBufferSize()];
			logger.info("Using a buffer to store packets (Size: "
					+ buffer.length + ")");
		} else {
			buffer = null;
		}
	}

	/**
	 * Establish connection to SerialForwarder
	 */
	private void registerSerialForwarderListeners() {
		try {
			mote = new MoteIF();
			for (Class<Message> c : messages) {
				mote.registerListener(c.cast(c.newInstance()).getClass()
						.newInstance(), this);
			}
		} catch (SecurityException e) {
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void deregisterSerialForwarderListener() {
		for (Class<Message> c : messages) {
			try {
				mote.registerListener(c.cast(c.newInstance()).getClass()
						.newInstance(), this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mote.getSource().shutdown();
		mote = null;
	}

	public void startListening() {
		if (false == listenModeActivated) {
			generateMethodWrapper();
			configureBuffer();
			registerSerialForwarderListeners();

			isActive = true;
			logger.info("Packet recording started");
			listenModeActivated = true;
		}
	}

	public void stopListening() {
		if (true == listenModeActivated) {
			deregisterSerialForwarderListener();
			isActive = false;

			// for (BackgroundWriter bw : backup) {
			for (MethodWrapper mw : methodWraper) {
				mw.backup.finishWriting();
			}
			flushBuffer();

			logger.info("Packet recording stopped");
			listenModeActivated = false;
		}
	}

	public void setActivation(boolean active) {

		// Build up connection
		if (active) {
			startListening();
		}

		// Close connection and stop recording
		else {
			stopListening();
		}
	}

	public boolean isActive() {
		return isActive;
	}

	private void addToBuffer(Packet p) {

		buffer[bufpointer++] = p;

		// If bufferlength is exceed
		if (bufpointer == buffer.length) {
			model.addPacket(buffer);
			bufpointer = 0;
		}
	}

	private void flushBuffer() {

		if (bufpointer != 0)
			model.addPacket(Arrays.copyOf(buffer, bufpointer));
	}

	private class MethodWrapper {
		private Method[] getter;
		private Method[] isArray;
		private Method[] arrayLength; // only arrays
		private String[] name;
		private Class<Message> message;
		private BackgroundWriter backup;
		private Method dataLength;
		private Method[] elementSize; // only arrays
		private Method[] getElement; // only arrays
		private Method[] getOffset;

		private MethodWrapper(Method[] getter, Method[] isArray,
				Method[] arrayLength, String[] name, Class<Message> message,
				BackgroundWriter backup, Method dataLength,
				Method[] elementSize, Method[] getElement, Method[] getOffset) {
			this.getter = getter;
			this.isArray = isArray;
			this.arrayLength = arrayLength;
			this.name = name;
			this.message = message;
			this.backup = backup;
			this.dataLength = dataLength;
			this.elementSize = elementSize;
			this.getElement = getElement;
			this.getOffset = getOffset;
		}
	}

	public void setDDSDump(boolean b) {
		this.ddsDump = b;
	}

	public void setQuickDump(boolean b) {
		quickDump = b;
		if (quickDump) {
			if (quickDumpFolder == null) {
				quickDumpFolder = new File(
						Constants.getDirQuickDump()
								+ "qd"
								+ new SimpleDateFormat("dd-MM-yyyy__HH-mm")
										.format(new Date()));
				quickDumpFolder.mkdir();
			}
			int i = 1;
			do {
				quickDumpMoteFolder = new File(quickDumpFolder
						+ Constants.getSep() + "m" + i);
				i++;
			} while (quickDumpMoteFolder.exists());
			quickDumpMoteFolder.mkdir();
		} else {
			Iterator<BufferedWriter> iter = quickDumpWriter.values().iterator();
			while (iter.hasNext())
				try {
					iter.next().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			quickDumpWriter.clear();
		}
	}
}
