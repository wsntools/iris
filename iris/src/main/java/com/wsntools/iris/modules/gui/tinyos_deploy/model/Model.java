/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

/**
 * Model for the TinyOS Deploy GUI
 * 
 * @author Sascha Hevelke
 * 
 */
public class Model extends Observable {

	private static Logger logger = Logger.getRootLogger();
	private List<Mapping3> hw_app_mapping;
	private File hw_lastpath;
	private File app_lastpath;
	private ProjectManagement pm;

	/**
	 * Creates a new Model
	 */
	public Model() {
		pm = new ProjectManagement();
		pm.setTinyos_apps_path(Constants.DEFAULT_TINYOS_APP_PATH);
	}

	/**
	 * Creates a new empty Mapping list
	 */
	public void createNewMapping() {
		List<Mapping3> l = new ArrayList<>();
		this.createNewMapping(l);
		this.hw_lastpath = null;
	}

	/**
	 * Creates a new mapping already containing data
	 * 
	 * @param data
	 *            Initial entrys in the new mapping
	 */
	public void createNewMapping(List<Mapping3> data) {
		hw_app_mapping = data;
		this.setChanged();
		this.notifyObservers(hw_app_mapping);
	}

	/**
	 * Imports mappings based on a hardware and application mapping file
	 * 
	 * @param hw_mapping
	 *            File pointing to the hw-mapping
	 * @param app_mapping
	 *            File pointing to the app-mapping
	 * @throws FileNotFoundException
	 */
	public void importMappings(File hw_mapping, File app_mapping)
			throws FileNotFoundException {
		hw_app_mapping = importMappingFromFiles(hw_mapping, app_mapping);
		this.setChanged();
		this.notifyObservers(hw_app_mapping);
	}

	/**
	 * Exports all hw mappings of the current mapping list
	 * 
	 * @param file
	 *            The destination of the export
	 */
	public void exportHwMapping(File file) {
		List<Mapping> hw_mapping = new ArrayList<>();
		for (Mapping3 mapping : hw_app_mapping) {
			if (!mapping.getHw_id().equals("") && !mapping.getId().equals("")) {
				hw_mapping
						.add(new Mapping(mapping.getId(), mapping.getHw_id()));
			}
		}
		try {
			exportMapping(file, hw_mapping);
			hw_lastpath = file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Exports all app mappings of the current mapping list
	 * 
	 * @param file
	 *            The destination of the export
	 */
	public void exportAppMapping(File file) {
		List<Mapping> app_mapping = new ArrayList<>();
		for (Mapping3 mapping : hw_app_mapping) {
			if (!mapping.getApp_id().equals("") && !mapping.getId().equals("")) {
				app_mapping.add(new Mapping(mapping.getId(), mapping
						.getApp_id()));
			}
		}
		try {
			exportMapping(file, app_mapping);
			app_lastpath = file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Searches for connected motes and imports them into the current mapping.
	 * Creates a new mapping if needed
	 */
	public void mergeConnectedMotes() {
		List<String> connMotes = null;
		try {
			connMotes = getConnectedMoteIDs();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if (connMotes != null) {
			if (hw_app_mapping != null) {
				int maxid = getHighestMappingId();
				List<String> hardware_idents = getHwIds();
				for (String string : connMotes) {
					if (!hardware_idents.contains(string)) {
						addRowToTableModel(new Mapping3(string, ++maxid, ""));
					}
				}
			} else {
				int id = 0;
				List<Mapping3> new_motes = new ArrayList<>();
				for (String mote : connMotes) {
					new_motes.add(new Mapping3(mote, id++, ""));
				}
				this.createNewMapping(new_motes);
			}
		}
	}

	/**
	 * Searches for all available Applications
	 */
	public void mergeAvailableApplications() {
		if (hw_app_mapping == null) {
			createNewMapping();
		}
		List<String> avail_apps = getAvailableApplications();
		for (String string : avail_apps) {
			List<String> curr_app_names = getAppIds();
			if (!curr_app_names.contains(string)) {
				addRowToTableModel(new Mapping3("", "", string));
			}
		}
	}

	/**
	 * Adds a new row to the table model and notifys observers
	 * 
	 * @param row
	 *            the row to be added
	 */
	public void addRowToTableModel(Mapping3 row) {
		hw_app_mapping.add(row);
		this.setChanged();
		this.notifyObservers(hw_app_mapping);
	}

	/**
	 * Removes a row from the table model
	 * 
	 * @param row
	 *            index of the row to be removed
	 */
	public void removeRowFromTableModel(int row) {
		hw_app_mapping.remove(row);
		this.setChanged();
		this.notifyObservers(hw_app_mapping);
	}

	/**
	 * Getter for the highest used Id in the mapping list
	 * 
	 * @return highest mapping id
	 */
	public int getHighestMappingId() {
		int x = 0;
		for (Mapping3 mapping : hw_app_mapping) {
			try {
				if (Integer.parseInt(mapping.getId()) > x) {
					x = Integer.parseInt(mapping.getId());
				}
			} catch (NumberFormatException e) {
			}
		}
		return x;
	}

	/**
	 * Getter for all HW-ids in the mapping list
	 * 
	 * @return List of Hw-ids
	 */
	private List<String> getHwIds() {
		List<String> hardware_list = new ArrayList<>();
		for (Mapping3 mapping : hw_app_mapping) {
			if (!mapping.getHw_id().equals("")) {
				hardware_list.add(mapping.getHw_id());
			}
		}
		return hardware_list;
	}

	/**
	 * Getter for all App-ids in the mapping list
	 * 
	 * @return List of App-ids
	 */
	private List<String> getAppIds() {
		List<String> app_list = new ArrayList<>();
		for (Mapping3 mapping : hw_app_mapping) {
			if (!mapping.getApp_id().equals("")) {
				app_list.add(mapping.getApp_id());
			}
		}
		return app_list;
	}

	/**
	 * searches for applications in the path set in the settings
	 * 
	 * @return List of Applications (String)
	 */
	public List<String> getAvailableApplications() {
		String app_path = pm.getTinyos_apps_path();
		List<String> app_list = new ArrayList<String>();
		if (app_path != null) {
			app_list = searchFolderForApplications(new File(app_path), 2);
		}
		return app_list;
	}

	/**
	 * Searches for folders containing a file called "makefile"
	 * (caseinsensitive) up to a given depth
	 * 
	 * @param folder
	 *            folder to be searched
	 * @param depth
	 *            maximum depth for searching
	 * @return List of all folders found
	 * @throws IllegalArgumentException
	 */
	public List<String> searchFolderForApplications(File folder, int depth)
			throws IllegalArgumentException {
		return searchFolderForApplications(folder, folder, depth);
	}

	/**
	 * Searches for folders containing a file called "makefile"
	 * (caseinsensitive) up to a given depth
	 * 
	 * @param folder
	 *            folder to be searched
	 * @param root
	 *            this folder is used as a relative root for all matches.
	 * @param depth
	 *            maximum depth for searching
	 * @return List of all folders found, path relativ to root
	 * @throws IllegalArgumentException
	 */
	public List<String> searchFolderForApplications(File folder, File root,
			int depth) throws IllegalArgumentException {
		if (folder == null || root == null) {
			return null;
		}
		if (depth < 1) {
			throw new IllegalArgumentException("Depth must be greater than 0.");
		}
		if (!folder.getAbsolutePath().startsWith(root.getAbsolutePath())) {
			throw new IllegalArgumentException(
					"'folder' must be the same/a subdirectory of 'root'");
		}
		if (!folder.exists()) {
			throw new IllegalArgumentException("'folder' does not exist");
		}
		if (!root.exists()) {
			throw new IllegalArgumentException("'root' does not exist");
		}

		List<String> returnList = new ArrayList<>();
		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});
		for (File file : files) {
			File[] subfiles = file.listFiles();
			for (File subfile : subfiles) {
				if (subfile.getName().equalsIgnoreCase("makefile")) {
					returnList.add(file.getAbsolutePath().substring(
							root.getAbsolutePath().length() + 1));
				}
			}
			if (depth > 1) {
				returnList.addAll(searchFolderForApplications(file, root,
						depth - 1));
			}
		}
		return returnList;
	}

	/**
	 * Uses the tinyos tool 'motelist' to get a list of all connected motes
	 * 
	 * @return List of all connected motes
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Mote> getConnectedMotes() throws IOException,
			InterruptedException {
		List<String> cmd = new ArrayList<>();
		cmd.add("motelist");
		cmd.add("-c");
		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process pr = builder.start();
		pr.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line;
		List<Mote> availableMotes = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("No devices found")) {
				String[] parts = line.split(",");
				availableMotes.add(new Mote(parts[0], parts[1], parts[2]));
			}
		}
		return availableMotes;
	}

	/**
	 * Gets the hardware-ids of all connected motes
	 * 
	 * @return List of hardware-ids
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<String> getConnectedMoteIDs() throws IOException,
			InterruptedException {
		List<Mote> connMotes = getConnectedMotes();
		List<String> moteIDs = new ArrayList<>();
		for (Mote mote : connMotes) {
			moteIDs.add(mote.getHw_Id());
		}
		return moteIDs;
	}

	/**
	 * Writes the provided mapping data to a file
	 * 
	 * @param file
	 *            destination of the export
	 * @param data
	 *            content of the export
	 * @throws FileNotFoundException
	 */
	public static void exportMapping(File file, List<Mapping> data)
			throws FileNotFoundException {
		if (file.exists() && !file.canRead()) {
			throw new FileNotFoundException("Cannot read file.");
		}
		StringBuilder sb = new StringBuilder();
		for (Mapping mapping : data) {
			sb.append(mapping.getId() + " " + mapping.getMapTo() + "\n");
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(new String(sb));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Uses a hardware and an application mapping file to generate a mapping
	 * list
	 * 
	 * @param hw_mapping
	 *            hardware mapping file
	 * @param app_mapping
	 *            application mapping file
	 * @return List of mappings
	 * @throws FileNotFoundException
	 */
	public List<Mapping3> importMappingFromFiles(File hw_mapping,
			File app_mapping) throws FileNotFoundException {
		if (!hw_mapping.exists()) {
			throw new FileNotFoundException(
					"This file does not seem to exist. "
							+ hw_mapping.getAbsolutePath());
		} else if (!hw_mapping.canRead()) {
			throw new FileNotFoundException("Cannot read file. "
					+ hw_mapping.getAbsolutePath());
		}
		if (!app_mapping.exists()) {
			throw new FileNotFoundException(
					"This file does not seem to exist. "
							+ app_mapping.getAbsolutePath());
		} else if (!app_mapping.canRead()) {
			throw new FileNotFoundException("Cannot read file. "
					+ app_mapping.getAbsolutePath());
		}

		FileReader fr = new FileReader(hw_mapping);
		BufferedReader br = new BufferedReader(fr);
		String x;
		List<Mapping> hw_mappings_list = new ArrayList<>();
		try {
			while ((x = br.readLine()) != null) {
				String[] parts = x.split(" ");
				Mapping map = new Mapping(parts[0], parts[1]);
				hw_mappings_list.add(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fr = new FileReader(app_mapping);
		br = new BufferedReader(fr);
		x = null;
		List<Mapping> app_mappings_list = new ArrayList<>();
		try {
			while ((x = br.readLine()) != null) {
				String[] parts = x.split(" ");
				Mapping map = new Mapping(parts[0], parts[1]);
				app_mappings_list.add(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<Mapping3> fullMappings = new ArrayList<>();

		for (Mapping hw : hw_mappings_list) {
			boolean added = false;
			for (Mapping app : app_mappings_list) {
				if (hw.getId().equals(app.getId())) {
					fullMappings.add(new Mapping3(hw.getMapTo(), hw.getId(),
							app.getMapTo()));
					added = true;
				}
			}
			if (!added) {
				fullMappings.add(new Mapping3(hw.getMapTo(), hw.getId(), ""));
			}
		}

		for (Mapping app : app_mappings_list) {
			boolean added = false;
			for (Mapping hw : hw_mappings_list) {
				if (hw.getId().equals(app.getId())) {
					added = true;
				}
			}
			if (!added) {
				fullMappings.add(new Mapping3("", app.getId(), app.getMapTo()));
			}
		}

		return fullMappings;
	}

	/**
	 * Creates a List of Mappings from a given filepath
	 * 
	 * @param filepath
	 *            Path to read file
	 * @return List of mappings
	 * @throws FileNotFoundException
	 */
	public ArrayList<Mapping> parseList(String filepath)
			throws FileNotFoundException {
		return parseList(new File(filepath));
	}

	/**
	 * Creates a List of Mappings from file
	 * 
	 * @param file
	 *            file to be read
	 * @return List of mappings
	 * @throws FileNotFoundException
	 */
	public ArrayList<Mapping> parseList(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(
					"This file does not seem to exist. "
							+ file.getAbsolutePath());
		} else if (!file.canRead()) {
			throw new FileNotFoundException("Cannot read file.");
		}
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String x;
		ArrayList<Mapping> mappings = new ArrayList<>();
		try {
			while ((x = br.readLine()) != null) {
				String[] parts = x.split(" ");
				Mapping map = new Mapping(parts[0], parts[1]);
				mappings.add(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mappings;
	}

	/**
	 * Deploys given mappings onto all connected devices
	 * 
	 * @param hw_mapping
	 *            Hardware mapping to be used
	 * @param app_mapping
	 *            Application mapping to be used
	 * @throws IOException
	 * @throws InterruptedException
	 */
	// TODO: Rename this, it's not really a prepare since it calls install later
	// on
	public void prepareToInstall(List<Mapping> hw_mapping,
			List<Mapping> app_mapping) throws IOException, InterruptedException {
		List<Mapping3> mappings = new ArrayList<>();
		for (Mapping hws : hw_mapping) {
			if (!hws.getId().equals("")) {
				int count = 0;
				for (Mapping apps : app_mapping) {
					if (apps.getId().equals(hws.getId())) {
						mappings.add(new Mapping3(hws.getMapTo(), hws.getId(),
								apps.getMapTo()));
						++count;
					}
				}
				if (count == 0) {
					mappings.add(new Mapping3(hws.getMapTo(), hws.getId(), ""));
				}
			}
		}
		for (Mapping app : app_mapping) {
			if (!app.getId().equals("")) {
				int count = 0;
				for (Mapping hws : hw_mapping) {
					if (app.getId().equals(hws.getId())) {
						++count;
					}
				}
				if (count == 0) {
					mappings.add(new Mapping3("", app.getId(), app.getMapTo()));
				}
			}
		}
		prepareToInstall(mappings);
	}

	/**
	 * Deploys given mapping onto all connected devices
	 * 
	 * @param hw_app_mapping
	 *            hardware and app mapping to be used
	 * @throws IOException
	 * @throws InterruptedException
	 */
	// TODO: Rename this, it's not really a prepare since it calls install later
	// on
	public void prepareToInstall(List<Mapping3> hw_app_mapping)
			throws IOException, InterruptedException {
		if (hw_app_mapping==null) {
			return;
		}
		Collections.sort(hw_app_mapping);
		List<Mote> availableMotes = getConnectedMotes();
		List<Mapping3> fullRows = new ArrayList<>();
		String default_app = null;
		for (Mapping3 mapping3 : hw_app_mapping) {
			if (!mapping3.getApp_id().equals("")
					&& !mapping3.getHw_id().equals("")
					&& !mapping3.getId().equals("")) {
				fullRows.add(mapping3);
			}
			if (mapping3.getId().equals("default")) {
				default_app = mapping3.getApp_id();
			}
		}
		for (Mote mote : availableMotes) {
			boolean installed = false;
			for (Mapping3 mapping : fullRows) {
				if (mote.getHw_Id().equals(mapping.getHw_id())) {
					install(mapping.getId(), mapping.getApp_id(),
							mote.getPort());
					installed = true;
				}
			}
			if (!installed && default_app != null) {
				for (Mapping3 mapping3 : hw_app_mapping) {
					if (mapping3.getHw_id().equals(mote.getHw_Id())
							&& mapping3.getApp_id().equals("")) {
						install(mapping3.getId(), default_app, mote.getPort());
					}
				}
			}
		}

	}

	/**
	 * Installs a application onto a mote which is connected to a port using a
	 * id
	 * 
	 * @param id
	 *            AM-Address the mote should use
	 * @param app
	 *            Application to be installed on the mote
	 * @param port
	 *            The port the mote is connected to
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void install(String id, String app, String port)
			throws IOException, InterruptedException {

		logger.info("Installing " + app + " on port " + port + ".");
		List<String> cmd = new ArrayList<>();
		cmd.add("scripts/install.sh");
		cmd.add(new File(pm.getTinyos_apps_path()).getAbsolutePath() + "/"
				+ app);
		cmd.add(id);
		cmd.add(port);

		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process pr = builder.start();
		pr.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
		pr.destroy();
	}

	/**
	 * Sets the path to the application folder and notifys observers.
	 * 
	 * @param absolutePath
	 *            path to the application folder.
	 */
	public void setTinyos_apps_path(String absolutePath) {
		this.pm.setTinyos_apps_path(absolutePath);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Getter for the Tinyos_apps_path
	 * @return tinyos apps path
	 */
	public String getTinyos_apps_path() {
		return this.pm.getTinyos_apps_path();
	}
	
	/**
	 * Exports the settings to a given file
	 * @param file Destination of the export
	 * @throws IOException
	 */
	public void exportSettings(File file) throws IOException {
		pm.exportSettings(file);
	}
	
	public String getHw_mapping_path() {
		return pm.getHw_mapping_path();
	}

	public String getApp_mapping_path() {
		return pm.getApp_mapping_path();
	}

	public void setHw_mapping_path(String path) {
		pm.setHw_mapping_path(path);
	}

	public void setApp_mapping_path(String path) {
		pm.setApp_mapping_path(path);
	}

	public void importSettings(File proj_file) throws IOException {
		pm.importSettings(proj_file);
	}

	public File getHw_lastpath() {
		return hw_lastpath;
	}

	public List<Mapping3> getHw_app_mapping() {
		return hw_app_mapping;
	}

	public File getApp_lastpath() {
		return app_lastpath;
	}

}
