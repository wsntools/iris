/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.worldmap.model;

import java.util.Date;
/**
 * Basic interface for a sensor sample
  * @author Marvin Baudewig, Sascha Hevelke
 */
public interface ISensorSample extends Comparable<ISensorSample> {

	/**
	 * Getter for the timstamp
	 * @returns timestamp of this sample
	 */
    public Date getTimestamp();
        
    /** Getter for the latidue
     * @return Latitude of the sample
     */
    public double getLatitude();
    
    /** Getter for the longitude
     * @return Longitude of the sample
     */
    public double getLongitude();
    
    /**
     * Setter for the timestamp
     * @param d Timestamp of the sample
     */
    public void setTimestamp(Date d);
    
    /** Setter for the latitude
     * @param lat Latitude of the sample
     */
    public void setLatitude(double lat);
    
    /** Setter for the longitude
     * @param lon Longitude of the sample
     */
    public void setLongitude(double lon);
    
}
