/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads and saves setting files
 * 
 * @author Sascha Hevelke
 * 
 */
public class ProjectManagement {
	private static final String APP_MAPPING_PATH_KEY = "app_mapping_path";
	private static final String HW_MAPPING_PATH_KEY = "hw_mapping_path";
	private static final String TINYOS_APPS_PATH_KEY = "tinyos_apps_path";
	private File settings_file;
	private Properties settings;

	/**
	 * Creates a new ProjectManagement
	 */
	public ProjectManagement() {
		this.settings = new Properties();
	}

	/**
	 * Creates a new ProjectManagement with a certain settings file
	 * 
	 * @param file
	 *            settings file
	 */
	public ProjectManagement(File file) {
		settings_file = file;
	}

	/**
	 * Imports settings from a file
	 * 
	 * @param settings_file
	 *            file to import
	 * @throws IOException
	 */
	public void importSettings(File settings_file) throws IOException {
		this.settings_file = settings_file;
		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(settings_file));
		settings.load(stream);
		stream.close();
	}

	/**
	 * Exports settings to a certain file
	 * 
	 * @param settings_file
	 *            file to export to
	 * @throws IOException
	 */
	public void exportSettings(File settings_file) throws IOException {
		BufferedOutputStream stream = new BufferedOutputStream(
				new FileOutputStream(settings_file));
		settings.store(stream, "");
		this.settings_file = settings_file;
		stream.close();
	}

	/**
	 * Getter for the App_mapping_path setting
	 * @return app mapping path
	 */
	public String getApp_mapping_path() {
		return settings.getProperty(APP_MAPPING_PATH_KEY);
	}
	
	/**
	 * Getter for the hw_mapping_path setting
	 * @return hw mapping path
	 */
	public String getHw_mapping_path() {
		return settings.getProperty(HW_MAPPING_PATH_KEY);
	}

	/**
	 * Setter for the App_mapping_path
	 * @param path new app_mapping_path
	 */
	public void setApp_mapping_path(String path) {
		this.settings.setProperty(APP_MAPPING_PATH_KEY, path);
	}
	
	/**
	 * Setter for the Hw_setting_path
	 * @param path new hw_setting_path
	 */
	public void setHw_mapping_path(String path) {
		this.settings.setProperty(HW_MAPPING_PATH_KEY, path);
	}
	/**
	 * Getter for the TinyOS apps path
	 * @return tinyos apps path
	 */
	public String getTinyos_apps_path() {
		return settings.getProperty(TINYOS_APPS_PATH_KEY);
	}

	/**
	 * Settter for the TinyOS apps path
	 * @param path new tinyOs apps paths
	 */
	public void setTinyos_apps_path(String path) {
		this.settings.setProperty(TINYOS_APPS_PATH_KEY, path);
	}
}
