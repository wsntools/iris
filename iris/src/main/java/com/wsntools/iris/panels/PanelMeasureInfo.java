/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_Observer;
/**
 * @author Sascha Jungen
 */

public class PanelMeasureInfo extends JPanel implements IRIS_Observer {

	private static final long serialVersionUID = 1L;
	private Model model;
	
	//Entries related to GUI modules
	private Map<IRIS_GUIModule, JPanel> mapModuleToInfopanel;
	
	private GridBagConstraints c = new GridBagConstraints();
	//Measurement information	
	private JLabel[][] arrLabelValues;
	//first dim: # of information of measurement
	//second dim: 	0 -> description of information
	//				1 -> displayed value
	
	
	//Constructor
	public PanelMeasureInfo(Model m) {
		
		model = m;
		model.registerObserver(this);
		
		mapModuleToInfopanel = new HashMap<IRIS_GUIModule, JPanel>();
		
		/*
		 * Object initialization
		 */
		arrLabelValues = new JLabel[model.getMeasureInfoNames().length][2];
		//Fill labelarray with defined values
		for(int i=0; i<model.getMeasureInfoNames().length; i++) {
			for(int j=0; j<2; j++) {
				arrLabelValues[i][j] = new JLabel((j==0 ? model.getMeasureInfoNames()[i] : "-"));
			}
		}
		
		
		/*
		 * Define layout
		 */		
		//TODO Umpositionierung der Elemente, 2 pro Reihe
		this.setLayout(new GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createTitledBorder("Measurement Information"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		for(int i=0; i<model.getMeasureInfoNames().length; i++) {
			for(int j=0; j<2; j++) {
				this.add(arrLabelValues[i][j], c);
				c.gridx = (c.gridx + 1) % 2;
				c.gridy = ((c.gridx == 0) ? (c.gridy+1) : c.gridy);
				c.weightx = ((j==0) ? 0.2 : 0.8);
			}
		}
	}
	
	public void addGUIModuleInfo(IRIS_GUIModule module) {
		
	}
	
	public void removeGUIModuleInfo(IRIS_GUIModule module) {
		
	}


	//--Interface methods--
	@Override
	public void updateNewMeasure() {
		
		String[] val = model.getMeasureInfoResult();
		for(int i=0; i<val.length; i++) {
			arrLabelValues[i][1].setText(val[i]);
		}
	}
	//Same behavoir for new packets as for new measure
	@Override
	public void updateNewPacket() {
		
		updateNewMeasure();
	}

	@Override
	public void updateNewAttribute() {
		
		//Do nothing
	}
	
}
