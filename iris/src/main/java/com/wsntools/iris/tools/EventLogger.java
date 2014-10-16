/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.IrisProperties;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.dialogues.DiaLoggerSettings;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_Observer;

public class EventLogger implements IRIS_Observer {

	private Model model;
	private boolean isLoggingEnabled;
	
	//Configuration of logged attributes
	private Map<Measurement, List<IRIS_Attribute[]>> mapMeasurementToLoggedAttributes;
	private Map<BufferedWriter, IRIS_Attribute[]> mapWriterToLoggedAttributes;
	
	public EventLogger(Model m) {
		model = m;
		isLoggingEnabled = false;
		
		mapMeasurementToLoggedAttributes = new HashMap<Measurement, List<IRIS_Attribute[]>>();
		mapWriterToLoggedAttributes = new HashMap<BufferedWriter, IRIS_Attribute[]>();
	}
	
	//--Configuration--
	public List<IRIS_Attribute[]> getMeasurementConfiguration(Measurement measure) {
		
		if(mapMeasurementToLoggedAttributes.containsKey(measure))
			return mapMeasurementToLoggedAttributes.get(measure);
		else
			return new ArrayList<IRIS_Attribute[]>();
	}
	public void addMeasurementConfiguration(Measurement measure, List<IRIS_Attribute[]> configlist) {
		
		mapMeasurementToLoggedAttributes.put(measure, configlist);
	}
	
	//--Logging functions--
	public boolean isLoggingEnabled() {		
		return isLoggingEnabled;
	}
	
	public void setLoggingEnabled(boolean activate) {
		if(activate && !isLoggingEnabled) {
			startLogging();
		}
		else if(!activate && isLoggingEnabled) {
			stopLogging();
		}
	}	
	
	private void startLogging() {
		//Check whether a configuration file is available and if not, show the configuration input
		if(!mapMeasurementToLoggedAttributes.containsKey(model.getCurrentMeasurement())) {
			List<IRIS_Attribute[]> list = DiaLoggerSettings.showLoggerSettingsWindow(model, model.getCurrentMeasurement(), getMeasurementConfiguration(model.getCurrentMeasurement()));
			if(list != null && list.size() > 0)
				mapMeasurementToLoggedAttributes.put(model.getCurrentMeasurement(), list);
		}
		if(mapMeasurementToLoggedAttributes.containsKey(model.getCurrentMeasurement())) {
			Measurement currentMeasurement = model.getCurrentMeasurement();
			List<IRIS_Attribute[]> toLog = mapMeasurementToLoggedAttributes.get(currentMeasurement);
			//Instantiate a writer for each chosen attribute combination
			BufferedWriter writer;
			String filename, firstline;
			for(IRIS_Attribute[] attrList: toLog) {
				filename = firstline = "";
				for(int i=0; i<attrList.length; i++) {
					filename += attrList[i].getAttributeName() + "_";
					firstline += ((i!=0) ? Constants.getLoggingValueSeparator() : "") + attrList[i].getAttributeName();
				}
				filename += new SimpleDateFormat("dd-MM-yy_HH-mm-ss").format(new Date()) + ".txt";
				try {
					File f = new File(Constants.getPathSavesLogger() + Constants.getSep() + currentMeasurement.getMeasureName());
					f.mkdirs();
					f = new File(f, Constants.getSep() + filename);
					writer = new BufferedWriter(new FileWriter(f));
					writer.write(firstline);
					writer.newLine();					
					writer.flush();					
					
					mapWriterToLoggedAttributes.put(writer, attrList);
				}
				catch(Exception e) {
					System.err.println("Unable to create/open output file!");
				}				
			}
			
			model.registerObserver(this);
			isLoggingEnabled = true;
			System.out.println("Logging of " + toLog.size() + " files started");
			
			//Write all packets that have been received so far
			for(BufferedWriter bw: mapWriterToLoggedAttributes.keySet())
				writeValues(bw, model.getCurrentMeasurement().getAllPacketsInOrder());
		}
		else {
			
		}
	}
	
	private void stopLogging() {
		for(BufferedWriter writer: mapWriterToLoggedAttributes.keySet()) {
			try {
				writer.close();
			} catch (IOException e) {
				System.err.println("Error while closing a logfile!");
			}
		}
		mapWriterToLoggedAttributes.clear();
		System.out.println("Logging ended");
		
		model.unregisterObserver(this);
		isLoggingEnabled = false;
	}

	private void writeValues(BufferedWriter writer, Packet[] newPackets) {

		if (isLoggingEnabled) {
			
			IRIS_Attribute[] toLog = mapWriterToLoggedAttributes.get(writer);			
			String line;
			for(int i=0; i<newPackets.length;i++) {
				line = "";
				for(int j=0; j<toLog.length; j++) {
					line += (!line.isEmpty() ? Constants.getLoggingValueSeparator() : "") + Float.toString(newPackets[i].getValue(toLog[j]));
				}
				
				try {
					writer.write(line);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					System.err.println("Error while writing the log file");
					e.printStackTrace();
				}
				
			}
		}
	}

		
	@Override
	public void updateNewMeasure() {
		if(isLoggingEnabled) {
			setLoggingEnabled(false);
		}
	}

	@Override
	public void updateNewPacket() {
		if(isLoggingEnabled) {
			Packet[] newPackets = model.getCurrentMeasurement().getLastPackets();
			for(BufferedWriter writer: mapWriterToLoggedAttributes.keySet()) {
				writeValues(writer, newPackets);
			}
		}
	}

	@Override
	public void updateNewAttribute() {
		//Do nothing since new attributes do not have any effect on the configuration
	}

}
