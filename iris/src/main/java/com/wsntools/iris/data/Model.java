/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.packageDecode.parser.PackageDecoder;
import com.wsntools.iris.tools.DataCollector;
import com.wsntools.iris.tools.EventLogger;
import com.wsntools.iris.tools.ModuleLoader;
import com.wsntools.iris.tools.datacollector.receiving.MetaDataCollector;
import com.wsntools.iris.tools.datacollector.sending.SendingConfiguration;
import com.wsntools.iris.tools.datacollector.sending.SendingManager;
import com.wsntools.iris.views.ViewMain;

/**
 * @author Sascha Jungen
 * 
 */
public class Model {

	//Arraylist of measurements
	private ArrayList<Measurement> measurements = new ArrayList<Measurement>();
	//Index of currently viewed measurement
	private int measure_index = -1;
	private boolean startInListenMode = false;
	private boolean startInDDSMode = false;

	//Info about created measurements
	private int measure_created_next = 0;
	
	//Network communication and logging
	private MetaDataCollector dataCollector;
	private SendingManager sendingMananger;
	private List<SendingConfiguration> lastConfigurations;
	private EventLogger eventLogger;
	
	//Information about recording buffer
	private int buffersize = -1;

	//List of user-created attributes	
	private ArrayList<AliasAttribute> listOfAliases;

	//List of all realized functions
	private IRIS_FunctionModule[] functions;

	//List of hardcoded displayed measure information
	private IRIS_ModuleInfo[] measureInfos;
	
	//List of available GUI Modules
	private IRIS_GUIModule[] guiModules;	
	
	//Reference to the view
	//List of updateable components which should get informed
	private ArrayList<IRIS_Observer> observer = new ArrayList<IRIS_Observer>();
	private ViewMain view;
	
	public final static Logger logger = Logger.getLogger("RMTLogger");

	//Constructor
	public Model(boolean startInListenMode, boolean startInDDSMode) {
		//Get all module classes for attributes, functions and infos
		this.startInListenMode = startInListenMode;
		this.startInDDSMode = startInDDSMode;
		lastConfigurations = new ArrayList<SendingConfiguration>();
		//Init Aliaslist and add all defined starting attributes
		listOfAliases = new ArrayList<AliasAttribute>();
		for(String alias: Constants.getAttrFixedAliases()) listOfAliases.add(new AliasAttribute(this, alias, AliasAttribute.ALIAS_FIXED, ""));
		functions = ModuleLoader.getFunctionList();
		measureInfos = ModuleLoader.getMeasureInfoList();		
		guiModules = ModuleLoader.getGUIModuleList(this);
		addNewMeasurement(new Measurement(0, "New Measurement"));
		applyGUIModuleSettings();
		
		eventLogger = new EventLogger(this);
		
		if (startInListenMode) {
			startRecording();
		}
	}
	
	/*
	 * Im/Export functions
	 */
	
	public List<Object> getExperimentData() {
		
		List<Object> toSave = new ArrayList<Object>();
		
		//Measurements (incl. function attributes)
		toSave.add(measurements);
		
		//TODO Messages/Configurations from Sender/Collector
		toSave.add(buffersize);
		
		//Save only user created aliases
		List<AliasAttribute> userAliases = new ArrayList<AliasAttribute>();
		for(AliasAttribute aa: listOfAliases) if(aa.isUserAlias()) userAliases.add(aa);
		toSave.add(userAliases);
		
		//GUI Configurations
		List<GUIModuleSettings> guiSettings = new ArrayList<GUIModuleSettings>();
		for(IRIS_GUIModule gm: guiModules) guiSettings.add(gm.getModuleSettings());
		toSave.add(guiSettings);
		
		return toSave;
	}
	
	public void setExperimentData(List<Object> params) {		
		
		int index = 0;
		//TODO Check for existence of function attributes
		measurements = (ArrayList<Measurement>) params.get(index++);
		for(Measurement meas: measurements) measure_created_next = Math.max(measure_created_next, meas.getMeasureNumber()+1);
		
		//TODO Messages/Configurations from Sender/Collector
		
		buffersize = (int)params.get(index++);
		
		//Clear previous user aliases data list
		for(int i=0; i<listOfAliases.size(); i++) if(listOfAliases.get(i).isUserAlias()) listOfAliases.remove(i);
		List<AliasAttribute> userAliases = (List<AliasAttribute>) params.get(index++);
		for(AliasAttribute aa: userAliases) listOfAliases.add(new AliasAttribute(this, aa));
		
		//Match settings to correspondent GUI Modules (if available)
		List<GUIModuleSettings> guiSettings = (List<GUIModuleSettings>) params.get(index++);
		for(GUIModuleSettings setting: guiSettings) {
			for(IRIS_GUIModule gm: guiModules) {
				if(setting.getGUIModuleClass().equals(gm.getClass())) gm.setModuleSettings(setting);
			}
		}
		
		//Set measure index
		measure_index = (measurements.size()-1);
		//Configure GUI conform to saved experiment
		applyGUIModuleSettings();
		
		//Inform all modules about the changes
		updateObserver(IRIS_Observer.EVENT_MEASURE);
	}

	/*
	 * Measurement management
	 */

	//Adds a new measurement (with optional name) and updates the view
	public boolean addNewMeasurement(String name) {

		if (!checkMeasureName(name)) {
			//Add measurement
			measurements.add(new Measurement(measure_created_next++, name));
			measure_index = measurements.size() - 1;

			//Update panels
			updateObserver(IRIS_Observer.EVENT_MEASURE);
			return true;
		} else {
			return false;
		}

	}

	//Adds a previously built measurement (from loading)
	public void addNewMeasurement(Measurement meas) {

		//First check if measurename is not duplicated
		if (checkMeasureName(meas.getMeasureName())) {
			int count = 1;
			while (checkMeasureName(meas.getMeasureName() + " (" + count + ")")) {
				count++;
			}
			meas.setMeasureName(meas.getMeasureName() + " (" + count + ")");
		}
		//Add measurement
		measurements.add(meas);
		measure_index = measurements.size() - 1;
		measure_created_next++;

		//Update panels
		updateObserver(IRIS_Observer.EVENT_MEASURE);

	}

	public boolean getListenOnStartup() {
		return startInListenMode;
	}
	
	public boolean getDDSMode() {
		return this.startInDDSMode;
	}

	//Adds a recieved packet to the current measurement and updates the view
	public void addPacket(Packet pack) {

		//Add Packet and inform observer if true is returned (new normal attribute)
		if (measurements.get(measure_index).addPacket(pack)) {
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
		}

		//Update panels
		updateObserver(IRIS_Observer.EVENT_PACKET);
	}

	//Adds a recieved packet to the current measurement and updates the view
	public void addPacket(Packet[] pack) {

		//Add Packet and inform observer if true is returned (new normal attribute)
		if (measurements.get(measure_index).addPacket(pack)) {
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
		}

		//Update panels
		updateObserver(IRIS_Observer.EVENT_PACKET);
	}

	public int getCurrentMeasureIndex() {

		return measure_index;
	}

	public Measurement getCurrentMeasurement() {

		return measurements.get(measure_index);
	}

	public Measurement getMeasurement(int num) {

		return measurements.get(num);
	}

	public int getMeasureCount() {

		return measurements.size();
	}

	public String[] getMeasureInfoNames() {

		String[] res = new String[measureInfos.length];
		for (int i = 0; i < measureInfos.length; i++) {
			res[i] = measureInfos[i].getModuleInfoName();
		}
		return res;
	}

	public String[] getMeasureInfoResult() {

		String[] res = new String[measureInfos.length];
		for (int i = 0; i < measureInfos.length; i++) {
			res[i] = measureInfos[i].getResult(measurements.get(measure_index));
		}
		return res;
	}
	
	public IRIS_GUIModule[] getGUIModules() {
		return guiModules;
	}
	public List<IRIS_GUIModule> getDisplayedGUIModules() {
		ArrayList<IRIS_GUIModule> listOfDisplayed = new ArrayList<IRIS_GUIModule>();
		for(int i=0; i<guiModules.length; i++) {
			if(guiModules[i].getModuleSettings().isActive()) listOfDisplayed.add(guiModules[i]);
		}
		return listOfDisplayed;
	}
	public void applyGUIModuleSettings() {
		
		for(int i=0; i<guiModules.length; i++) {
			GUIModuleSettings setting = guiModules[i].getModuleSettings();
			
			//Register only active and auto-update selected modules
			if(!setting.isActive() || !setting.isRegisteredAsObserver()) {
				unregisterObserver(guiModules[i].getModuleObserver());
			}
			else {				
				registerObserver(guiModules[i].getModuleObserver());
			}
			
			//Include Attributes in the list
			if(guiModules[i].getRequiredAliasAttributes() != null) {
				//TODO Mehrfache abhaengigkeiten oder Kuerzel fuer Module
				if(setting.isActive()) {
					AliasAttribute[] aliases = guiModules[i].getRequiredAliasAttributes();
					for(AliasAttribute alias: aliases) {
						addAliasAttribute(alias);
					}
				}
				else {
					AliasAttribute[] aliases = guiModules[i].getRequiredAliasAttributes();
					for(AliasAttribute alias: aliases) {
						removeAliasAttribute(alias);
					}
				}
			}
		}		
		
		//Finally update View with corresponding panels
		if(view != null)
			view.updateGUIModules();
	}

	//Returns true, if operation was successful
	public boolean setMeasureName(int index, String name) {

		if (!checkMeasureName(name)) {
			measurements.get(index).setMeasureName(name);
			updateObserver(IRIS_Observer.EVENT_MEASURE);
			return true;
		} else {
			return false;
		}
	}

	public int getNextMeasurementNumber() {

		return measure_created_next;
	}

	//Removes the measurement on the position of the parameter
	public void removeMeasurement(int pos) {

		Measurement meas = measurements.get(pos);
		//Clear all references to the measure
		for(AliasAttribute aa: listOfAliases) {
			aa.removeMappingAttribute(meas);
		}
		
		measurements.remove(meas);
		measure_index = measurements.size() - 1;
		if (measurements.size() == 0) {
			measure_created_next = 1;
			addNewMeasurement(new Measurement(0, "New Measurement"));
		}
		updateObserver(IRIS_Observer.EVENT_MEASURE);
	}

	public void setCurrentMeasureIndex(int index) {

		measure_index = index;
		updateObserver(IRIS_Observer.EVENT_MEASURE);
	}

	/*
	 * Function management
	 */
	public IRIS_FunctionModule getFunctionInstanceByName(String name) {

		for (IRIS_FunctionModule func : functions) {
			if (func.getFunctionName().equals(name)) {
				try {
					return func.getClass().newInstance();
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}

	public String[] getFunctionNames() {

		String[] res = new String[functions.length];
		for (int i = 0; i < functions.length; i++) {
			res[i] = functions[i].getFunctionName();
		}
		return res;
	}

	/*
	 * Attribute management
	 */

	public void addFunctionAttributeToMeasurement(FunctionAttribute attr) {

		if (checkAttributeName(attr.getAttributeName())) {
			int count = 1;
			while (checkAttributeName(attr.getAttributeName() + " (" + count + ")")) {
				count++;
			}
			attr.renameFunctionAttribute(attr.getAttributeName() + " (" + count + ")");
		}

		measurements.get(measure_index).addAttribute(attr);
		updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
	}
	
	
	public List<IRIS_Attribute> getMeasureAttributesBySpecification(boolean aliasUser, boolean aliasGUI, boolean aliasFixed, boolean measNormal, boolean measFuncNonScalar, boolean measFuncScalar) {
		ArrayList<IRIS_Attribute> attrList = new ArrayList<IRIS_Attribute>();
		if(aliasUser && aliasGUI && aliasFixed)
			attrList.addAll(listOfAliases);
		else {
			if(aliasUser) attrList.addAll(getAliasAttributesByType(AliasAttribute.ALIAS_USER));
			if(aliasGUI) attrList.addAll(getAliasAttributesByType(AliasAttribute.ALIAS_GUI));
			if(aliasFixed) attrList.addAll(getAliasAttributesByType(AliasAttribute.ALIAS_FIXED));
		}
		if(measNormal && measFuncNonScalar && measFuncScalar)
			attrList.addAll(measurements.get(measure_index).getAttributes());
		else {
			if(measNormal) {
				for(IRIS_Attribute attr: measurements.get(measure_index).getNormalAttributes())
					attrList.add(attr);
			}
			if(measFuncNonScalar && measFuncScalar) {
				for(IRIS_Attribute attr: measurements.get(measure_index).getFunctionAttributes(true, true))
					attrList.add(attr);
			}
			else {
				if(measFuncNonScalar) {
					for(IRIS_Attribute attr: measurements.get(measure_index).getFunctionAttributes(true, false))
						attrList.add(attr);
				}
				if(measFuncScalar) {
					for(IRIS_Attribute attr: measurements.get(measure_index).getFunctionAttributes(false, true))
						attrList.add(attr);
				}
			}
		}
		return attrList;
	}

	//Returns all attributes using functions
	public IRIS_Attribute[] getFunctionAttributesInMeasurement() {

		return measurements.get(measure_index).getFunctionAttributes(true, true);
	}
	
	public List<IRIS_Attribute> getMeasureAttributes(boolean includeGlobalAttributes) {
		
		if(includeGlobalAttributes) {
			ArrayList<IRIS_Attribute> inclList = new ArrayList<IRIS_Attribute>();
			inclList.addAll(listOfAliases);
			inclList.addAll(measurements.get(measure_index).getAttributes());
			return inclList;
		}
		return measurements.get(measure_index).getAttributes();
	}

	public IRIS_Attribute getMeasureAttribute(String atname, boolean includeGlobalAttributes) {

		if(includeGlobalAttributes) {
			//Check for AliasAttribute Matching first
			for(AliasAttribute alias: listOfAliases) {
				if(alias.getAttributeName().equals(atname)) {
					return alias;
				}
			}			
		}
		return measurements.get(measure_index).getAttribute(atname);
	}

	public int getMeasureAttributeCount(boolean includeGlobalAttributes) {

		if(includeGlobalAttributes) {
			return measurements.get(measure_index).getAttributeCount() + listOfAliases.size();
		}
		else
			return measurements.get(measure_index).getAttributeCount();
	}

	//Returns all values of the given attribute of the current measurement (for graph drawing)
	public float[] getMeasureAttributeValuesByName(String atname, boolean refresh, boolean fillUncoveredValues, boolean includeGlobalAttributes) {

		if(includeGlobalAttributes) {
			//Check for AliasAttribute Matching first
			for(AliasAttribute alias: listOfAliases) {
				if(alias.getAttributeName().equals(atname)) {
					return alias.getValues(measurements.get(measure_index).getAllPacketsInOrder());
				}
			}
			return measurements.get(measure_index).getAttributeValuesByName(atname, refresh, fillUncoveredValues);
		}			
		else {
			return measurements.get(measure_index).getAttributeValuesByName(atname, refresh, fillUncoveredValues);
		}
	}

	public String[] getMeasureAttributeValueStringsByName(String atname, boolean includeGlobalAttributes) {

		if(includeGlobalAttributes) {
			//Check for AliasAttribute Matching first
			for(AliasAttribute alias: listOfAliases) {
				if(alias.getAttributeName().equals(atname)) {
					return alias.getValuesString(measurements.get(measure_index).getAllPacketsInOrder());
				}
			}
			return measurements.get(measure_index).getAttributeValueStringsByName(atname);
		}			
		else {
			return measurements.get(measure_index).getAttributeValueStringsByName(atname);
		}
	}

	public String[] getMeasureAttributeValueStringsByNameNoDuplicates(String atname, boolean includeGlobalAttributes) {

		if(includeGlobalAttributes) {
			//Check for AliasAttribute Matching first
			for(AliasAttribute alias: listOfAliases) {
				if(alias.getAttributeName().equals(atname)) {
					return alias.getValuesString(measurements.get(measure_index).getAllPacketsInOrder());
				}
			}
			return measurements.get(measure_index).getAttributeValueStringsByNameNoDuplicates(atname);
		}			
		else {
			return measurements.get(measure_index).getAttributeValueStringsByNameNoDuplicates(atname);
		}
	}

	//If function has been deleted, inform observer
	public String removeFunctionAttributeFromMeasurement(FunctionAttribute attr) {

		String res = measurements.get(measure_index).removeFunctionAttribute(attr);
		if (res == null) {
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
		}
		return res;
	}

	public boolean renameFunctionAttributeInMeasurement(String toupdate, String newname) {

		if (!checkAttributeName(newname)) {
			((FunctionAttribute) measurements.get(measure_index).getAttribute(toupdate))
					.renameFunctionAttribute(newname);
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
			return true;
		} else {
			return false;
		}
	}

	public void updateFunctionAttributeInMeasurement(String toupdate, String newname, boolean draw, int out,
			IRIS_FunctionModule func) {

		((FunctionAttribute) getMeasureAttribute(toupdate, false)).addNewFunction(newname, draw, out, func);

		updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
	}
	
	/*
	 * AttributeAlias functions
	 */
	public List<AliasAttribute> getAliasAttributes() {
		
		return listOfAliases;
	}
	public List<AliasAttribute> getAliasAttributesByType(int type) {
		ArrayList<AliasAttribute> attrList = new ArrayList<AliasAttribute>();
		for(AliasAttribute alias: listOfAliases) if(alias.getAliasType()==type) attrList.add(alias);
		return attrList;
	}
	public boolean addAliasAttribute(AliasAttribute attr) {
		String attrName = attr.getAttributeName();
		//Check for already existing names
		boolean match = false;
		for(AliasAttribute existAttr: listOfAliases) {
			if(existAttr.getAttributeName().equals(attrName)) {
				match = true;
				break;
			}
		}
		if(!match && !listOfAliases.contains(attr)) {
			listOfAliases.add(attr);
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
			return true;
		}
		return false;
	}
	public void removeAliasAttribute(AliasAttribute attr) {
		if(!listOfAliases.contains(attr)) return;
		//Check for depencies in all measurements
		boolean hasDependency = false;
		String dependencies = "";
		for(Measurement me: measurements) {
			IRIS_Attribute[] funcs = me.getFunctionAttributes(true, true);
			for(IRIS_Attribute func: funcs) {
				IRIS_Attribute[] params = ((FunctionAttribute)func).getAllParameter();
				for(IRIS_Attribute param: params) {
					if(param.equals(attr)) {
						hasDependency = true;
						dependencies += "\nMeasurement: '" + me.getMeasureName() + "', Function: '" + func.getAttributeName() + "'";
					}
				}
			}
		}
		if(!hasDependency) {
			listOfAliases.remove(attr);
			updateObserver(IRIS_Observer.EVENT_ATTRIBUTE);
		}
		else {
			JOptionPane.showMessageDialog(getCurrentlyFocusedWindow(),
					"Cannot remove alias attribute because of the following parameter dependencies:" + dependencies, "IRIS", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Custom Filter functions
	 */
	public List<FilterSettings> getUserDefinedFilterSets() {
		return measurements.get(measure_index).getUserDefinedFilterSets();
	}
	public void addUserDefinedFilter(FilterSettings setting) {
		measurements.get(measure_index).addUserDefinedFilter(setting);
	}
	public void removeUserDefinedFilter(FilterSettings setting) {
		measurements.get(measure_index).removeUserDefinedFilter(setting);
	}

	/*
	 * Integrity functions
	 */

	public boolean checkAttributeName(String name) {

		Measurement meas = measurements.get(measure_index);
		for (int i = 0; i < meas.getAttributeCount(); i++) {
			if (meas.getAttribute(i).getAttributeName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	//Returns true if the name already exists
	public boolean checkMeasureName(String name) {

		for (int i = 0; i < measurements.size(); i++) {
			if (measurements.get(i).getMeasureName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Recording methods
	 */
	public int getRecordBufferSize() {

		return buffersize;
	}

	public void setRecordBufferSize(int val) {

		buffersize = val;
	}
	
	/*
	 * Network input
	 */
	public MetaDataCollector getDataCollector() {
		if(dataCollector == null) dataCollector = new MetaDataCollector(this);
		return dataCollector;
	}
	
	public boolean isDataCollectorActive() {
		return (dataCollector != null && dataCollector.isActive()); 
	}
	
	public void startRecording() {
		if (dataCollector == null) {
			dataCollector = new MetaDataCollector(this);
		}
		if(!dataCollector.isActive()) {
			dataCollector.setActivation(true);
		}
	}
	
	public void stopRecording() {
		if (isDataCollectorActive()) {
			dataCollector.setActivation(false);
		}
		if (eventLogger.isLoggingEnabled()) {
			eventLogger.setLoggingEnabled(false);
		}
	}	
	
	/*
	 * Network output
	 */
	
	private SendingManager getSendingManager() {
		if(sendingMananger == null) sendingMananger = new SendingManager();
		return sendingMananger;
	}
	
	public SendingConfiguration[] getSendingConfigurations() {
		SendingConfiguration[] configs = new SendingConfiguration[lastConfigurations.size()];
		return lastConfigurations.toArray(configs);
	}
	
	public boolean setupSendingManager() {		
		SendingConfiguration config = getSendingManager().buildSendingSetup();
		if(config != null) {
			lastConfigurations.add(0, config);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean sendConfiguration(SendingConfiguration conf) {		
		if(lastConfigurations != null) {
			getSendingManager().send(conf);			
			//Put it into the list of recently sent messages
			if(lastConfigurations.contains(conf)) {
				lastConfigurations.remove(conf);				
			}
			lastConfigurations.add(0, conf);
				
			return true;
		}
		return false;
	}
		
	public EventLogger getEventLogger() {
		return eventLogger;
	}
	

	/*
	 * View & update methods
	 */
	public ViewMain getView() {

		return view;
	}
	public JFrame getCurrentlyFocusedWindow() {
		
		return view.getCurrentlyFocusedWindow();
	}

	public void setView(ViewMain v) {

		view = v;
	}

	public void repackView() {

		view.pack();
	}

	public void registerObserver(IRIS_Observer obs) {

		if(obs != null && !observer.contains(obs))
			observer.add(obs);
	}

	public void unregisterObserver(IRIS_Observer obs) {

		observer.remove(obs);
	}

	public void updateObserver(int event) {

		for (int i = 0; i < observer.size(); i++) {
			switch (event) {

			case IRIS_Observer.EVENT_ATTRIBUTE:
				observer.get(i).updateNewAttribute();
				break;
			case IRIS_Observer.EVENT_MEASURE:
				observer.get(i).updateNewMeasure();
				break;
			case IRIS_Observer.EVENT_PACKET:
				observer.get(i).updateNewPacket();
				break;
			}
		}
	}

	public void openPackageDecoder() {
		new PackageDecoder(true);
	}
}
