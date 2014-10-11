/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.worldmap.model;

import java.util.Date;

public class SensorSampleImpl implements ISensorSample{
	private Date timestamp;
	private double latitude;
	private double longitude;
	
	public SensorSampleImpl() {
		timestamp = null;
		latitude = 0;
		longitude = 0;
	}
	
	public SensorSampleImpl(Date timestamp) {
		this();
		this.timestamp = timestamp;
	}

	public SensorSampleImpl(double latitude, double longitude) {
		this();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public SensorSampleImpl(Date timestamp, double latitude, double longitude) {
		this(timestamp);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}
	
	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public void setLatitude(double lat) {
		this.latitude = lat;
	}

	@Override
	public void setLongitude(double lon) {
		this.longitude = lon;
	}
	
	@Override
	public int compareTo(ISensorSample samp) {
		return samp.getTimestamp().compareTo(timestamp) * -1;
	}
}
