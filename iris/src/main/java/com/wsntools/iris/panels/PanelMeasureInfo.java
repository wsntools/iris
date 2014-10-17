/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.FunctionInfo;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
/**
 * @author Sascha Jungen
 */

public class PanelMeasureInfo extends JPanel implements IRIS_Observer {

	private static final long serialVersionUID = 1L;
	private Model model;
	private PanelMeasureInfo ref = this;
	private boolean isWindow;
	
	private Timer updateTimer;
	
	//Entries related to scalar functions
	private JPanel panelFunctionInfo, panelFunctionNames, panelFunctionValues;
	private Map<IRIS_Attribute, FunctionInfo> mapFuncAttrToFuncInfo;
	private Map<FunctionInfo, JLabel> mapFuncInfoToLabel;
	
	//Entries related to GUI modules
	private Map<IRIS_GUIModule, JPanel> mapModuleToInfopanel;
	private Map<IRIS_ModuleInfo, JLabel> mapModuleInfoToLabel;
	
	//Constructor
	public PanelMeasureInfo(Model m, boolean windowed) {
		
		model = m;
		model.registerObserver(this);
		
		isWindow = windowed;
		
		panelFunctionInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panelFunctionInfo.setBorder(BorderFactory.createTitledBorder("Scalar Functions"));
		panelFunctionNames = new JPanel();
		panelFunctionValues = new JPanel();
		panelFunctionInfo.add(panelFunctionNames);
		panelFunctionInfo.add(panelFunctionValues);
		panelFunctionInfo.setVisible(false);
		
		mapFuncAttrToFuncInfo = new HashMap<IRIS_Attribute, FunctionInfo>();
		mapFuncInfoToLabel = new HashMap<FunctionInfo, JLabel>();
		
		mapModuleToInfopanel = new HashMap<IRIS_GUIModule, JPanel>();
		mapModuleInfoToLabel = new HashMap<IRIS_ModuleInfo, JLabel>();
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT,0 ,0));
		this.setBorder(BorderFactory.createTitledBorder("Module Information"));
		this.add(panelFunctionInfo);
		this.setVisible(false);
	}
	
	public void addGUIModuleInfo(IRIS_GUIModule module) {
		
		if((module.getRelatedModuleInfos() == null) || (mapModuleToInfopanel.containsKey(module)) || !module.getModuleSettings().isDisplayingInformation()) return;
		IRIS_ModuleInfo[] moduleInfos = module.getRelatedModuleInfos();
		JPanel panelModule = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		JPanel panelNames = new JPanel(new GridLayout(moduleInfos.length, 1));
		JPanel panelValues = new JPanel(new GridLayout(moduleInfos.length, 1));
		for(int i=0; i<moduleInfos.length; i++) {
			panelNames.add(new JLabel(moduleInfos[i].getModuleInfoName()));
			JLabel labelValue = new JLabel(moduleInfos[i].getResult(model.getCurrentMeasurement()));
			//labelModule.setHorizontalAlignment(SwingConstants.RIGHT);
			panelValues.add(labelValue);
			mapModuleInfoToLabel.put(moduleInfos[i], labelValue);
		}
		panelModule.add(panelNames);
		panelModule.add(panelValues);
		mapModuleToInfopanel.put(module, panelModule);
		if(!isWindow) panelModule.setBorder(BorderFactory.createTitledBorder(module.getModuleName()));
		this.add(panelModule);
		
		this.setVisible(!mapModuleToInfopanel.isEmpty());
		
		//Timer activation on first element
		if(mapModuleToInfopanel.size() == 1) {
			updateTimer = new Timer();
			updateTimer.schedule(new TimerTask() {			
				@Override
				public void run() {
					if(ref.isValid()) {
						genericUpdate();
					}
					else {
						updateTimer.cancel();
						updateTimer.purge();
						updateTimer = null;
					}
				}
			}, Constants.getModuleinfoUpdateTimerInterval(), Constants.getModuleinfoUpdateTimerInterval());
		}
	}
	
	public void removeGUIModuleInfo(IRIS_GUIModule module) {

		if((module.getRelatedModuleInfos() == null) || (!mapModuleToInfopanel.containsKey(module))) return;
		IRIS_ModuleInfo[] moduleInfos = module.getRelatedModuleInfos();		
		for(int i=0; i<moduleInfos.length; i++) {			
			mapModuleInfoToLabel.remove(moduleInfos[i]);
		}
		this.remove(mapModuleToInfopanel.get(module));
		mapModuleToInfopanel.remove(module);
		
		this.setVisible(!mapModuleToInfopanel.isEmpty());
		
		//Timer cancellation on empty list
		if(mapModuleToInfopanel.isEmpty()) {
			updateTimer.cancel();
			updateTimer.purge();
		}
	}

	private void genericUpdate() {
		for(Entry<IRIS_ModuleInfo, JLabel> e: mapModuleInfoToLabel.entrySet()) {
			e.getValue().setText(e.getKey().getResult(model.getCurrentMeasurement()));
		}
	}
	
	private void functionInfoSet() {		
		List<IRIS_Attribute> listScalars = model.getMeasureAttributesBySpecification(false, false, false, false, false, true);
		for(IRIS_Attribute attr: listScalars) {
			if(!mapFuncAttrToFuncInfo.containsKey(attr)) {
				mapFuncAttrToFuncInfo.put(attr, new FunctionInfo((FunctionAttribute)attr));
				mapFuncInfoToLabel.put(mapFuncAttrToFuncInfo.get(attr), new JLabel(attr.getAttributeName()));
			}			
		}
		
		panelFunctionNames.removeAll();
		panelFunctionNames.setLayout(new GridLayout(listScalars.size(), 1));
		panelFunctionValues.removeAll();
		panelFunctionValues.setLayout(new GridLayout(listScalars.size(), 1));
		for(IRIS_Attribute attr: listScalars) {			
			panelFunctionNames.add(new JLabel(mapFuncAttrToFuncInfo.get(attr).getModuleInfoName()));
			panelFunctionValues.add(mapFuncInfoToLabel.get(mapFuncAttrToFuncInfo.get(attr)));
		}
		
		panelFunctionInfo.setVisible(!listScalars.isEmpty());
	}
	
	private void functionInfoUpdate() {
		for(Entry<FunctionInfo, JLabel> e: mapFuncInfoToLabel.entrySet()) {
			e.getValue().setText(e.getKey().getResult(model.getCurrentMeasurement()));
		}
	}
	
	//--Interface methods--
	@Override
	public void updateNewMeasure() {		
		genericUpdate();
		functionInfoSet();
		functionInfoUpdate();
	}
	
	@Override
	public void updateNewPacket() {		
		genericUpdate();
		functionInfoUpdate();
	}

	@Override
	public void updateNewAttribute() {
		genericUpdate();		
		functionInfoSet();
		functionInfoUpdate();
	}
	
}
