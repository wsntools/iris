/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wsntools.iris.modules.gui.worldmap.model;


import java.awt.Color;
import java.util.ArrayList;

/**
 *Basic interface for a SensorNode, which contains a LinkedList<ISensorSample>
 * @author Marvin Baudewig, Sascha Hevelke
 */
public interface ISensorNode {
    
	/**
	 * Returns the sensor ID of this node
	 * @return sensor ID of the node
	 */
    public int getSensorID();
    
    /**
     * Sets the sensor ID
     * @param sensorID new sensor id
     */
    public void setSensorID(int sensorID);
//    boolean isVisible();
//    void setVisible(boolean visible);
    
    /**
     * NOT IMPLEMENTED YET
     * @return copy of this object
     */
    public ISensorNode copy();
    
//    public int getFirstToDisplay();
//    public int getLastToDisplay();
//    
//    public void setFirstToDisplay(int firstToDisplay);
//    public void setLastToDisplay(int lastToDisplay);
    
	/**
	 * @param data Adds an ISensorSample Object to the list of gps coordinates for this node
	 */
    public void addData(ISensorSample data);
    
	/**Returns a list with the gps coordinate objects of this sensor node
	 * @return list of gps coordinates of this node
	 * @see ISensorSample
	 */
    public ArrayList<ISensorSample> getGPSDataList();
//    public LinkedList<IHorseGPSDataInterface> getGPSDataListTimed();
    
    /**
     * Getter for the color of the path on the globe
     * @return the color for the path
     */
    public Color getColor();
    
    /**
     * Setter for the color of the path on the globe
     * @param c The new color object
     */
    public void setColor(Color c);
}
