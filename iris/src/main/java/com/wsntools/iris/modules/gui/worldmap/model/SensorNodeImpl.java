/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.worldmap.model;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Basic implementation of the interface ISensorNode
 * @author Sascha Hevelke
 *
 */
public class SensorNodeImpl implements ISensorNode{
	
	private int sensorID;
	private ArrayList<ISensorSample> gpsDataList;
	private Color color = Color.WHITE;
	
	public SensorNodeImpl() {
		this.gpsDataList = new ArrayList<>();
	}
	
	public SensorNodeImpl(int sensorID) {
		this();
		this.sensorID = sensorID;
	}
	
	public SensorNodeImpl(int sensorID, Color pathColor) {
		this(sensorID);
		this.color = pathColor;
	}
	
	public SensorNodeImpl(int sensorID, ArrayList<ISensorSample> gpsSamples) {
		this(sensorID);
		this.gpsDataList = gpsSamples;
	}
	
	public SensorNodeImpl(int sensorID, ArrayList<ISensorSample> gpsSamples, Color pathColor) {
		this(sensorID,gpsSamples);
		this.color = pathColor;
	}
	
	@Override
	public int getSensorID() {
		return sensorID;
	}

	@Override
	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
		
	}

	@Override
	public ISensorNode copy() {
		//TODO: Not implemented yet
		throw new RuntimeException("This operation is not implementet yet.");
	}

	@Override
	public void addData(ISensorSample data) {
		gpsDataList.add(data);		
	}
	
	@Override
	public ArrayList<ISensorSample> getGPSDataList() {
		return gpsDataList;
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void setColor(Color c) {
		this.color = c;
	}


}
