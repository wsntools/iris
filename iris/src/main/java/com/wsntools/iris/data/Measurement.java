/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.tinyos.message.Message;

import com.wsntools.iris.extensions.MessageWrapper;
import com.wsntools.iris.interfaces.IRIS_Attribute;

/**
 * @author Sascha Jungen, Ramin Soleamyni
 * 
 */
public class Measurement {

	private IRIS_Attribute yAxis = null;
	private int measure_nr;
	private String measure_name;

	private ArrayList<Packet> packets = new ArrayList<Packet>(
			Constants.getPacketArrayStartSize());

	private ArrayList<IRIS_Attribute> attributes = new ArrayList<IRIS_Attribute>();

	// Array for quick reply of data
	private Packet[] arrPackets = new Packet[0];
	private Packet[] arrLastPackets = new Packet[0];
	
	//List of user defined filters for this measurement
	private ArrayList<FilterSettings> userDefinedFilterSets = new ArrayList<FilterSettings>();
	
	//Network Communication Related
	private MessageWrapper actualMessage;
	private HashMap<String, MessageWrapper> msges = new HashMap<String, MessageWrapper>();

	private HashMap<Integer, Long> NeighboutToLastSeenTime = new HashMap<Integer, Long>();


	// Constructor
	public Measurement(int num, String name, IRIS_Attribute[] fixedattr) {

		measure_nr = num;
		measure_name = name;

		for (int i = 0; i < fixedattr.length; i++) {
			try {
				attributes.add(fixedattr[i].getClass().newInstance());

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Cannot create a copy of attribute: "
						+ fixedattr[i].getAttributeName());
			}
		}

	}

	public void addNeighbour(int neighbourId, long time) {
		NeighboutToLastSeenTime.put(neighbourId, time);
	}

	public ArrayList<Integer> getNeighbourlist(long time) {

		ArrayList<Integer> result = new ArrayList<Integer>();
		long currentTime = System.currentTimeMillis();

		if (-1 == time) {
			for (Integer neighbour : NeighboutToLastSeenTime.keySet()) {
				result.add(neighbour);

			}
		} else {
			for (Integer neighbour : NeighboutToLastSeenTime.keySet()) {
				if (time <= currentTime
						- NeighboutToLastSeenTime.get(neighbour)) {
					result.add(neighbour);
				}
			}
		}
		return result;
	}

	public void setYAxis(IRIS_Attribute yAxis) {
		// System.out.println("set y-axis to: "+yAxis.getAttributeName());

		this.yAxis = yAxis;
	}

	public IRIS_Attribute getYAxis() {
		return yAxis;
	}

	// Getter
	public int getMeasureNumber() {

		return measure_nr;
	}

	public String getMeasureName() {

		return measure_name;
	}

	/*
	 * Attribute management
	 */
	public ArrayList<IRIS_Attribute> getAttributes() {
		return attributes;
	}
	
	public IRIS_Attribute getAttribute(int index) {

		return attributes.get(index);
	}

	public IRIS_Attribute getAttribute(String name) {

		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getAttributeName().equals(name)) {
				return attributes.get(i);
			}
		}
		return null;
	}

	// Returns all attributes using functions
	public IRIS_Attribute[] getFunctionAttributes() {

		IRIS_Attribute[] res;
		int num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).isFunctionAttribute()) {
				num++;
			}
		}
		res = new IRIS_Attribute[num];
		num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).isFunctionAttribute()) {
				res[num++] = attributes.get(i);
			}
		}
		return res;
	}

	// Returns all non-hard-coded normal attributes
	public IRIS_Attribute[] getNormalAttributes() {

		IRIS_Attribute[] res;
		int num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).isNormalAttribute()) {
				num++;
			}
		}
		res = new IRIS_Attribute[num];
		num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).isNormalAttribute()) {
				res[num++] = attributes.get(i);
			}
		}
		return res;
	}

	// Returns all non-function attributes
	public IRIS_Attribute[] getNonFunctionalAttributes() {

		IRIS_Attribute[] res;
		int num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (!attributes.get(i).isFunctionAttribute()) {
				num++;
			}
		}
		res = new IRIS_Attribute[num];
		num = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (!attributes.get(i).isFunctionAttribute()) {
				res[num++] = attributes.get(i);
			}
		}
		return res;
	}

	// Before deleting, check if there are dependencies of other attributes
	public String removeFunctionAttribute(FunctionAttribute attr) {

		// Get all attributes used by the attribute which to delete
		IRIS_Attribute[] funcattr = getFunctionAttributes();
		IRIS_Attribute[] usedattr;
		String deps = "";

		for (int i = 0; i < funcattr.length; i++) {

			usedattr = ((FunctionAttribute) funcattr[i]).getAllParameter();
			for (int k = 0; k < usedattr.length; k++) {

				// If other functionattributes use 'attr' add them to a string
				if (attr.equals(usedattr[k]) && !funcattr[i].equals(attr)) {

					deps = deps
							+ (deps.isEmpty() ? funcattr[i].getAttributeName()
									: (", " + funcattr[i].getAttributeName()));
					break;
				}
			}
		}

		if (deps.isEmpty()) {
			// If there are no other dependencies, delete und update observer
			attributes.remove(attr);
			return null;
		} else {
			return deps;
		}
	}

	public int getAttributeCount() {

		return attributes.size();
	}

	public void addAttribute(IRIS_Attribute attr) {

		if(!attributes.contains(attr))
			attributes.add(attr);
	}

	public float[] getAttributeValuesByName(String atname, boolean refresh, boolean fillUncoveredValues) {

		IRIS_Attribute att = null;
		float[] res = new float[getNumberOfPackets()];

		// Search for matching attribute name
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getAttributeName().equals(atname)) {
				att = attributes.get(i);
				break;
			}
		}
		// Take all packets, pass it through the attribute function and store
		// the result in res
		if (att.isFunctionAttribute()) {
			// If function is called within refresh operation, clear packet
			// cache before
			if (refresh)
				((FunctionAttribute) att).resetPacketCache();

			res = att.getValues(getAllPacketsInOrder());
			
			//For output, fill the not used range of values with default values
			if(fillUncoveredValues) {
				List<Integer> listToAdd = ((FunctionAttribute) att).getFilterPassingPacketIndices();
				float[] newRes = new float[arrPackets.length + ((FunctionAttribute) att).getPredictionValueCount()];
				int next = 0;
				for(int i=0; i<arrPackets.length; i++) {
					if(!listToAdd.contains(i)) newRes[i] = Constants.getPacketDefaultEmptyNumber();
					else newRes[i] = res[next++];
				}
				float[] pred = ((FunctionAttribute) att).getPredictionValues();
				next = 0;
				for(int i=arrPackets.length; i<newRes.length; i++) {
					newRes[i] = pred[next++];
				}
				res = newRes;
			}
		} else {
			res = att.getValues(getAllPacketsInOrder());
		}

		return res;
	}

	public String[] getAttributeValueStringsByName(String atname) {

		IRIS_Attribute att = null;
		String[] res;

		// Search for matching attribute name
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getAttributeName().equals(atname)) {
				att = attributes.get(i);
				break;
			}
		}
		// Take all packets, pass it through the attribute function and store
		// the result in res
		res = att.getValuesString(getAllPacketsInOrder());

		return res;
	}

	public String[] getAttributeValueStringsByNameNoDuplicates(String name) {

		ArrayList<Float> arrNewEntry = new ArrayList<Float>();
		float[] arrValues = getAttributeValuesByName(name, false, false);
		for (int i = 0; i < arrValues.length; i++) {
			if (!arrNewEntry.contains(arrValues[i])) {
				arrNewEntry.add(arrValues[i]);
			}
		}
		Collections.sort(arrNewEntry);

		String[] res = new String[arrNewEntry.size()];
		for (int i = 0; i < arrNewEntry.size(); i++) {
			res[i] = Float.toString(arrNewEntry.get(i));
		}

		return res;
	}

	/*
	 * Packet handling
	 */

	// Packet adding (true if a new attribute is created)
	public boolean addPacket(Packet pack) {
		packets.add(pack);
		arrPackets = new Packet[packets.size()];
		packets.toArray(arrPackets);
		arrLastPackets = new Packet[] {pack};

		return checkAndAddNewAttributes(pack);
	}

	// For multiple packets (e.g. loading progresses
	public boolean addPacket(Packet[] arr) {
		boolean res = false;
		for (Packet p : arr) {
			packets.add(p);
			if (checkAndAddNewAttributes(p)) {
				res = true;
			}
		}
		arrPackets = new Packet[packets.size()];
		packets.toArray(arrPackets);
		arrLastPackets = arr;

		return res;
	}

	private boolean checkAndAddNewAttributes(Packet pack) {

		String[] val = pack.getAllValues();
		boolean exists, newattr = false;
		for (int i = 0; i < val.length; i++) {

			exists = false;
			for (int j = 0; j < attributes.size(); j++) {

				if (val[i].equals(attributes.get(j).getAttributeName())) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				attributes.add(new NormalAttribute(val[i], true));
				newattr = true;
			}
		}
		return newattr;
	}

	public int getNumberOfPackets() {

		return packets.size();
	}

	// Setter
	public void setMeasureName(String name) {

		if (name != null && !name.equals("")) {
			measure_name = name;
		} else {
			measure_name = "Measure " + measure_nr;
		}
	}

	/*
	 * --Packet getter--
	 */
	public Packet getPacketAt(int index) {

		return packets.get(index);
	}

	// Returns packets in normal order (order of arrival)
	public Packet[] getAllPacketsInOrder() {

		return arrPackets;
	}
	
	public Packet[] getLastPackets() {
		
		return arrLastPackets;
	}
	
	/*
	 * Filter related methods
	 */
	public ArrayList<FilterSettings> getUserDefinedFilterSets() {
		return userDefinedFilterSets;
	}
	public void addUserDefinedFilter(FilterSettings setting) {
		userDefinedFilterSets.add(setting);
	}
	public void removeUserDefinedFilter(FilterSettings setting) {
		userDefinedFilterSets.remove(setting);
	}

	
	/*
	 * --Message related methods TODO Maybe move to Module
	 */
	public Message getMessage(String name) {
		actualMessage = msges.get(name);
		return msges.get(name).getMsg();
	}

	public MessageWrapper getMessageWrapper(String name) {
		actualMessage = msges.get(name);
		return actualMessage;
	}

	public void addMsg(MessageWrapper mw) {
		this.msges.put(mw.name, mw);
		actualMessage = mw;
	}

	public Message getActualMessage() {
		if (actualMessage == null)
			return null;
		return actualMessage.getMsg();
	}

	public MessageWrapper getActualMessageW() {
		if (actualMessage == null)
			return null;
		return actualMessage;
	}

	public String getActualMessageName() {
		return actualMessage.name;
	}

	public String[] getMsgNames(boolean addNew) {
		String[] msgNames = new String[msges.size() + (addNew ? 1 : 0)];
		int i = 0;
		if (addNew) {
			msgNames[0] = "NEW MESSAGE";
			i++;
		}
		for (String name : msges.keySet()) {
			msgNames[i] = name;
			i++;
		}
		return msgNames;
	}

	public int getAddress() {
		return actualMessage.address;
	}

	public HashMap<String, MessageWrapper> getMsges() {
		return msges;
	}

	public void logSendMessage(MessageWrapper msg) {
		if (true == IrisProperties.getInstance().getLogOutgoingMessages()) {
			try {
				File sendLog = new File(Constants.getDirSendLog() + measure_name + ".csv");

				FileWriter fw = new FileWriter(sendLog, true);
				Date date = new Date();
				fw.append(String.valueOf(date.getTime()) + ", " + msg.name
						+ ", " + msg.toCSVString() + Constants.getLineSep());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void logSendMessage() {
		logSendMessage(getActualMessageW());
	}

}