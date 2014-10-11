/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.model;

/**
 * Represents a mapping HW<->AM-Address<->Application
 * @author Sascha Hevelke
 *
 */
public class Mapping3 implements Comparable<Mapping3>{
	private String app_id;
	private String id;
	private String hw_id;
	/**
	 * Creates a new Mapping3
	 * @param hw_id Hardware Id
	 * @param id AM-Address
	 * @param app_id Application name
	 */
	public Mapping3(String hw_id, int id, String app_id) {
		this(hw_id,String.valueOf(id),app_id);
	}
	
	/**
	 * Creates a new Mapping3
	 * @param hw_id Hardware Id
	 * @param id AM-Address
	 * @param app_id Application name
	 */
	public Mapping3(String hw_id, String id, String app_id) {
		this.hw_id = hw_id;
		this.id = id;
		this.app_id = app_id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHw_id() {
		return hw_id;
	}

	public void setHw_id(String hw_id) {
		this.hw_id = hw_id;
	}
	@Override
	public String toString() {
		return this.hw_id+":"+this.id+":"+this.app_id;
	}

	@Override
	public int compareTo(Mapping3 arg0) {
		return this.getId().compareTo(arg0.getId());
	}
}
