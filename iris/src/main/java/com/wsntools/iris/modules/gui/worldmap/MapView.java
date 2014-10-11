/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.worldmap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JPanel;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.worldmap.GlobeController;
import com.wsntools.iris.modules.gui.worldmap.model.ISensorNode;
import com.wsntools.iris.modules.gui.worldmap.model.ISensorSample;
import com.wsntools.iris.modules.gui.worldmap.model.SensorNodeImpl;
import com.wsntools.iris.modules.gui.worldmap.model.SensorSampleImpl;

public class MapView extends JPanel implements IRIS_Observer {
	private static final long serialVersionUID = 1L;
	private JPanel content = new JPanel();
	
	private Model model;
	private IRIS_Attribute attrLatitude, attrLongitude, attrSenderID;
	private final int gpsCorrect = 1000000;
	
	private GlobeController gc = new GlobeController(content);
	
	HashMap<Integer, Color> idToColor = new HashMap<>();

	public MapView(Model model, IRIS_Attribute latitude, IRIS_Attribute longitude, IRIS_Attribute senderID) {

		this.model = model;
		attrLatitude = latitude;
		attrLongitude = longitude;
		attrSenderID = senderID;		

		idToColor.put(0, Color.red);
		idToColor.put(5, Color.green);	

		this.add(content);
		this.setSize(600, 480);
		// this.setDefaultCloseOperation();
		//this.setVisible(true);

	}
	
	private void genericUpdate() {
		
		float[] attributesLatitude = attrLatitude.getValues(model.getCurrentMeasurement().getAllPacketsInOrder());
		float[] attributesLongitude = attrLongitude.getValues(model.getCurrentMeasurement().getAllPacketsInOrder());
		float[] attributesId = attrSenderID.getValues(model.getCurrentMeasurement().getAllPacketsInOrder());

		ArrayList<ISensorSample> samples = null;
		HashMap<Integer, ArrayList<ISensorSample>> IdToSample = new HashMap<>();

		ArrayList<ISensorNode> sensors = new ArrayList<>();

		// ArrayList<ISensorNode> sensors = null;
		for (int i = 0; i < attributesId.length; i++) {

			samples = IdToSample.get((int) attributesId[i]);
			if (null == samples) {
				samples = new ArrayList<>();
				IdToSample.put((int) attributesId[i], samples);
			}
			

			// double lon = (attributesLongitude[i] / gpsCorrect) - 0.05;
			// double lat = (attributesLatitude[i] / gpsCorrect) - 0.08;
			double lon = (attributesLongitude[i] / gpsCorrect);
			double lat = (attributesLatitude[i] / gpsCorrect);

			samples.add(new SensorSampleImpl(new Date(), lat, lon));

			int id = (int) attributesId[i];


		}

		// create new sensornode with the samples
		ISensorNode sensor = null;
		for (int i : IdToSample.keySet()) {
			Color color = idToColor.get(i);
			if (null == color) {
				color = Color.black;
			}
			//System.out.println(i);
			sensor = new SensorNodeImpl(i, IdToSample.get(i),color);
			sensors.add(sensor);
		}

		// ISensorNode sensor = new SensorNodeImpl(1,samples);

		
		gc.setExclusiveSensorDisplay(sensors);
		// gc.showScreenShotButton();
		gc.displayNodes();
	}

	@Override
	public void updateNewMeasure() {
		genericUpdate();
	}

	@Override
	public void updateNewPacket() {
		genericUpdate();		
	}

	@Override
	public void updateNewAttribute() {
		//Do nothing since there is no dependency on other attributes
	}
}