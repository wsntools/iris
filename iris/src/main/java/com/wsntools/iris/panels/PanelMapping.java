/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;

/**
 * @author Sascha Jungen
 *
 * This panel holds information about the mapping of hard coded attributes
 * to the measurement-specific attributes
 */
public class PanelMapping extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Measurement measure;
	
	private JLabel labelAttName = new JLabel();
	private JLabel labelArrow = new JLabel(new ImageIcon(Constants.getResource(
			Constants.getPathPicsMisc() + Constants.getNameMiscArrow())));
	private JComboBox<String> comboMapTo = new JComboBox<String>();
	
	public PanelMapping(Model m, AliasAttribute toMap, Measurement meas) {
		measure = meas;
		
		labelAttName.setText(toMap.getAttributeName());
		IRIS_Attribute[] normatt = m.getMeasureNormalAttributes();
		for(int i=0; i<normatt.length; i++) {
			comboMapTo.addItem(normatt[i].getAttributeName());
		}
		
		//Get current mapping index
		if(toMap.getMappingAttribute(meas) != null) {
			comboMapTo.setSelectedItem(toMap.getMappingAttribute(meas).getAttributeName());
		}
		else {
			comboMapTo.setSelectedIndex(-1);
		}
		
		//this.setPreferredSize(new Dimension(300, 40));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		this.add(labelAttName);
		this.add(labelArrow);
		this.add(comboMapTo);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(meas.getMeasureName()));
	}
	
	public PanelMapping(Model m, Measurement meas) {
		measure = meas;		
		
		labelAttName.setText("<Name of the Alias>");
		IRIS_Attribute[] normatt = meas.getNormalAttributes();
		for(int i=0; i<normatt.length; i++) {
			comboMapTo.addItem(normatt[i].getAttributeName());
		}
		
		//Get current mapping index
		comboMapTo.setSelectedIndex(-1);
		
		//this.setPreferredSize(new Dimension(300, 40));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		this.add(labelAttName);
		this.add(labelArrow);
		this.add(comboMapTo);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(meas.getMeasureName()));
	}
	
	public void setNewAlias(AliasAttribute toMap) {
		
		labelAttName.setText(toMap.getAttributeName());
		
		//Get current mapping index
		if(toMap.getMappingAttribute(measure) != null) {
			comboMapTo.setSelectedItem(toMap.getMappingAttribute(measure).getAttributeName());
		}
		else {
			comboMapTo.setSelectedIndex(-1);
		}
	}
	
	/*
	//For function attribute mapping
	public PanelMapping(Model m, String toMap, boolean isFirst) {
		
		labelAttName.setText(toMap);
		RMT_Attribute[] nonfuncs = m.getCurrentMeasurement().getNonFunctionalAttributes();
		//Selfmapping only allowed for later elements than the first
		if(!isFirst) {
			comboMapTo.addItem("Itself");
		}
		for(int i=0; i<nonfuncs.length; i++) {

			comboMapTo.addItem(nonfuncs[i].getAttributeName());
		}
		
		//this.setPreferredSize(new Dimension(300, 40));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		this.add(labelAttName);
		this.add(labelArrow);
		this.add(comboMapTo);
	}
	
	//For display only
	public PanelMapping(String toMap, String mapTarget) {
		
		labelAttName.setText(toMap);		
		
		//this.setPreferredSize(new Dimension(300, 40));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		this.add(labelAttName);
		this.add(labelArrow);
		this.add(new JLabel(mapTarget));
	}*/
	
	public String getMappingSelection() {
		
		if(comboMapTo.getItemCount() > 0)
			return (String)comboMapTo.getSelectedItem();
		else
			return null;
	}
	public int getMappingIndex() {
		
		return comboMapTo.getSelectedIndex();
	}
	public String getMappingObject() {
		
		return labelAttName.getText();
	}
	public Measurement getMappingMeasure() {
		
		return measure;
	}
}
