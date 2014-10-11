/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.model;
/**
 * Represents a mapping HW<->AM-Address OR Application<->AM-Address
 * @author Sascha Hevelke
 *
 */
public class Mapping {
	private String Id;
	private String mapTo;
	/**
	 * Creates a new Mapping
	 */
	public Mapping() {
	}
	/**
	 * Creates a new Mapping
	 * @param Id AM-Address for the Application/Hardware
	 * @param mapTo Hardware-ID/Application Name
	 */
	public Mapping(String Id, String mapTo) {
		this.Id = Id;
		this.mapTo = mapTo;
	}
	/**
	 * Creates a new Mapping
	 * @param Id AM-Address for the Application/Hardware
	 * @param mapTo Hardware-ID/Application Name
	 */
	public Mapping(int Id, String mapTo) {
		this(String.valueOf(Id),mapTo);
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getMapTo() {
		return mapTo;
	}

	public void setMapTo(String mapTo) {
		this.mapTo = mapTo;
	}
	@Override
	public String toString() {
		return this.Id+": "+this.mapTo;
	}
}
