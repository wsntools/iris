/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.jfreechart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.jfreechart.PanelDrawValue.ComboListener;

public class PanelDrawBarchart extends JPanel implements IRIS_Observer {

	private Model model;
	
	private JPanel panelDisplayValue = new JPanel();
	protected JComboBox comboValue = new JComboBox();
	private JPanel panelSeparateValue = new JPanel();
	protected JComboBox comboSeparate = new JComboBox();
	private JPanel panelBarFunction = new JPanel();
	protected JComboBox comboFunctions = new JComboBox();
	private JButton butDraw;
	
	private ComboListener listenerCombo = new ComboListener();
	
	public PanelDrawBarchart(Model m, JButton drawbut) {
		
		model = m;
		model.registerObserver(this);
		
		butDraw = drawbut;
		
		panelDisplayValue.add(comboValue);
		panelDisplayValue.setBorder(javax.swing.BorderFactory.createTitledBorder("Draw Value"));
		
		comboValue.setPreferredSize(new Dimension(150, 28));
		comboValue.addItem("None");
		
		panelSeparateValue.add(comboSeparate);
		panelSeparateValue.setBorder(javax.swing.BorderFactory.createTitledBorder("Separate by"));
		
		comboSeparate.setPreferredSize(new Dimension(150, 28));
		comboSeparate.addItem("None");
		
		panelBarFunction.add(comboFunctions);
		panelBarFunction.setBorder(javax.swing.BorderFactory.createTitledBorder("Use Function"));
		
		comboFunctions.setPreferredSize(new Dimension(150, 28));
		comboFunctions.addItem("None");
		
		butDraw.setPreferredSize(new Dimension(28, 28));
		
		
		this.add(panelDisplayValue);
		this.add(panelSeparateValue);
		this.add(panelBarFunction);
		this.add(butDraw);
		
		//Listener
		comboValue.addActionListener(listenerCombo);
		comboSeparate.addActionListener(listenerCombo);
		comboFunctions.addActionListener(listenerCombo);
	}
	
	public void setObserverRegisterStatus(boolean val) {
		
		if (val) {
			model.unregisterObserver(this);
		}
		else {
			model.registerObserver(this);
		}
	}
	
	@Override
	public void updateNewMeasure() {
		
		updateNewAttribute();
	}

	@Override
	public void updateNewPacket() {
	}

	@Override
	public void updateNewAttribute() {
		
		// Add all drawable attributes to the comboboxes
		comboValue.removeActionListener(listenerCombo);
		comboValue.removeAllItems();

		List<IRIS_Attribute> listattr = model.getMeasureAttributes(true);
		comboValue.addItem("None");
		for (int j = 0; j < listattr.size(); j++) {
			if (listattr.get(j).isDrawable()) {
				comboValue.addItem(listattr.get(j).getAttributeName());
			}
		}

		comboValue.setSelectedIndex(0);
		comboValue.addActionListener(listenerCombo);
		

		comboSeparate.removeActionListener(listenerCombo);
		comboSeparate.removeAllItems();

		comboSeparate.addItem("None");
		for (int j = 0; j < listattr.size(); j++) {
			//if (model.getMeasureAttribute(j).isDrawable()) {
				comboSeparate.addItem(listattr.get(j).getAttributeName());
			//}
		}

		comboSeparate.setSelectedIndex(0);
		comboSeparate.addActionListener(listenerCombo);
		
		//Get all functions which return a single value
		comboFunctions.removeActionListener(listenerCombo);
		comboFunctions.removeAllItems();

		for (int j = 0; j < model.getFunctionNames().length; j++) {
			IRIS_FunctionModule func = model.getFunctionInstanceByName(model.getFunctionNames()[j]);
			if (func.isOneValueResult() && func.getParameterCount() == 1) {
				comboFunctions.addItem(model.getFunctionNames()[j]);
			}
		}
		
		if(comboFunctions.getItemCount() == 0) {
			comboFunctions.setSelectedIndex(-1);
		}
		else {
			comboFunctions.setSelectedIndex(0);
		}

		
		comboFunctions.addActionListener(listenerCombo);

	}
	
	
	class ComboListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			
			if (ae.getSource().equals(comboValue)) {
				
			}
			if (ae.getSource().equals(comboSeparate)) {
				
			}
			if (ae.getSource().equals(comboFunctions)) {
				
			}
			
		}

	}
	
}
