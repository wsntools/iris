/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.model;
/**
 * Represents a WSN node connected to the system.
 * @author Sascha Hevelke
 */
public class Mote {
	private String hw_Id;
	private String port;
	private String name;

	/**
	 * Creates a new Mote
	 * @param hw_Id Hardware ID of the mote
	 * @param port Port it is connected to
	 * @param name Name of the Mote (e.g. TelosB)
	 */
	public Mote(String hw_Id, String port, String name) {
		this.hw_Id = hw_Id;
		this.port = port;
		this.name = name;
	}

	public String getHw_Id() {
		return hw_Id;
	}

	public void setHw_Id(String hw_Id) {
		this.hw_Id = hw_Id;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Mote:("+hw_Id+","+port+","+name+")";
	}
}
