/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaFunction;
import com.wsntools.iris.dialogues.DiaHideAttributes;
import com.wsntools.iris.dialogues.DiaSetFilter;
import com.wsntools.iris.dialogues.DiaSettings;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.packetexplorer.table.PacketTableModel;
import com.wsntools.iris.modules.gui.packetexplorer.table.PacketTableView;

public class GUI_PacketExplorer extends IRIS_GUIModule implements IRIS_Observer {

	private static final float EMPTY_NUMBER = Float.NaN;
	
	private ArrayList<IRIS_Attribute> attributeHideList;
	private Map<IRIS_Attribute, List<float[]>> attributeFilterList;
	
	private JPanel panelPacketExplorer;
	private PacketTableModel measureModel;
	private PacketTableView ppm;
	
	private JPanel panelRefresh = new JPanel(new FlowLayout(FlowLayout.TRAILING,0,0));
	private JButton butRefresh = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnRefreshSm())));
	
	private JPanel panelButtons = new JPanel();
	private JButton butFilter = new JButton("Filter");
	private JButton butHide = new JButton("Hide");
	private JButton butRemoveSettings = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	public GUI_PacketExplorer(Model m) {		
		this(m, null);
		getGUIPanel().setBorder(javax.swing.BorderFactory.createTitledBorder(getModuleName()));
	}
	
	public GUI_PacketExplorer(Model m, PacketTableModel mm) {
		super(m);
		this.measureModel = mm;
		this.ppm = new PacketTableView(measureModel);		
		
		attributeHideList = new ArrayList<IRIS_Attribute>();
		attributeFilterList = new HashMap<IRIS_Attribute, List<float[]>>();
		
		butRefresh.setPreferredSize(new Dimension(18,18));
		panelRefresh.add(butRefresh);
		ppm.setCornerElement(butRefresh);
		
		butRemoveSettings.setPreferredSize(new Dimension(28,28));
		butRemoveSettings.setVisible(false);
		
		panelButtons.add(butFilter);
		panelButtons.add(butHide);
		panelButtons.add(butRemoveSettings);
		
		panelPacketExplorer = new JPanel(new BorderLayout());
		panelPacketExplorer.add(ppm, BorderLayout.CENTER);
		panelPacketExplorer.add(panelButtons, BorderLayout.SOUTH);
		

		butRefresh.addActionListener(listenerButtons);
		butFilter.addActionListener(listenerButtons);
		butHide.addActionListener(listenerButtons);
		butRemoveSettings.addActionListener(listenerButtons);
	}

	@Override
	public String getModuleName() {
		return "Packet Explorer";
	}

	@Override
	public String getModuleDescription() {
		return "This module contains a table to display all received packets and their delivered content."
				+ " It also features operations to sort and filter the information.";
	}

	@Override
	public JPanel getGUIPanel() {
		return panelPacketExplorer;
	}
	
	@Override
	public boolean isToolbarOnly() {
		return false;
	}

	@Override
	public IRIS_Observer getModuleObserver() {
		return this;
	}

	@Override
	public IRIS_ModuleInfo[] getRelatedModuleInfos() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String[] getRelatedMenuBarEntries() {
		return null;
	}

	@Override
	public ActionListener getMenuBarActionListener() {
		return null;
	}
	
	@Override
	public AliasAttribute[] getRequiredAliasAttributes() {
		return null;
	}
		
	//Own methods
	private void genericUpdate() {
		//TODO Caching for performance
		
		PacketTableModel new_measuremodel = new PacketTableModel(model.getMeasureAttributeCount(true) - attributeHideList.size());
		//System.out.println("attribute count: "+ model.getCurrentMeasurement().getAttributeCount());
		List<IRIS_Attribute> attributes = model.getMeasureAttributes(true);
		
		List<String> attr_names = new ArrayList<>();

		for (IRIS_Attribute rmt_Attribute : attributes) {
			if(!attributeHideList.contains(rmt_Attribute)) {
				attr_names.add(rmt_Attribute.getAttributeName());
			}
		}
		
		new_measuremodel.setColumnNames(attr_names);
				
		List<float[]> columns = new ArrayList<>();
		int maxSize = 0;
		for (int i = 0; i < attr_names.size(); i++) {
			float[] attrValues = model.getMeasureAttributeValuesByName(attr_names.get(i), true, true, true);
			maxSize = Math.max(attrValues.length, maxSize);
			columns.add(attrValues);
		}
		
		//Filter for values
		ArrayList<Integer> rowsToFilterOut = new ArrayList<Integer>();
		if(!attributeFilterList.isEmpty()) {
			List<float[]> allowedRange;
			for(IRIS_Attribute attr: attributeFilterList.keySet()) {
				//Identify the correct column
				for(int i=0; i<attr_names.size(); i++) {
					if(attr_names.get(i).equals(attr.getAttributeName())) {	
						allowedRange = attributeFilterList.get(attr);
						float[] col = columns.get(i);
						//Check all values of the column if it passes the filter
						for(int j=0; j<col.length; j++) {
							if(!rowsToFilterOut.contains(j) && !passesFilter(allowedRange, col[j])) rowsToFilterOut.add(j);
						}
					}
				}
			}
		}
		//Only add relevant values to the model
		for (int i = 0; (columns.size()>0) && (i<maxSize); i++) {
			//Leave out filtered rows
			if(!rowsToFilterOut.contains(i)) {
				List<Object> column = new ArrayList<>();			
				for (int j = 0; j < columns.size(); j++) {
					//Remove all rows which have been filtered out before but fill up empty columns with "empty" numbers
					if(columns.get(j).length > i)
						column.add(columns.get(j)[i]);
					else
						column.add(EMPTY_NUMBER);
				}
				new_measuremodel.addMeasurement(column);
			}
		}		
		this.measureModel = new_measuremodel;
		ppm.updateData(measureModel);
	}
	
	//Assumes a clean and not overlapping range of values within allowedRange
	private boolean passesFilter(List<float[]> allowedRange, float toCheck ) {
		for(float[] arrRange:allowedRange) {
			//Instantly return true, if one of the filter criteria is met, otherwise continue
			if ((arrRange[0] <= toCheck) && (toCheck <= arrRange[1])) return true;
		}
		return false;
	}
	
	private boolean passesFilter(List<float[]> allowedRange, String toCheck ) {
		float toCheck2;
		try { toCheck2 = Float.parseFloat(toCheck); }
		catch (NumberFormatException nfe) { return true; }
				
		for(float[] arrRange:allowedRange) {
			//Instantly return true, if one of the filter criteria is met, otherwise continue
			if ((arrRange.length == 2) && (arrRange[0] <= toCheck2) && (toCheck2 <= arrRange[1])) return true;
		}
		return false;
	}
	
	
	@Override
	public void updateNewMeasure() {
		genericUpdate();
	}

	@Override
	public void updateNewPacket() {
		genericUpdate();
	}

	@Override
	public void updateNewAttribute() {
		genericUpdate();
	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			Object o = ae.getSource();
			if (o.equals(butFilter)) {
				attributeFilterList = DiaSetFilter.showFilterSettingWindow(model, attributeFilterList);
				butFilter.setBackground(attributeFilterList.isEmpty() ? null : Color.GREEN);
				butRemoveSettings.setVisible(!(attributeFilterList.isEmpty() && attributeHideList.isEmpty()));
				genericUpdate();
			}
			
			else if (o.equals(butHide)) {
				IRIS_Attribute[] attributes = new IRIS_Attribute[model.getMeasureAttributes(true).size()];
				attributes = model.getMeasureAttributes(true).toArray(attributes);
				boolean[] checked = new boolean[attributes.length];
				for(int i=0; i<attributes.length; i++) checked[i] = !attributeHideList.contains(attributes[i]);
				checked = DiaHideAttributes.showHideAttibuteWindow(model, attributes, checked);
				attributeHideList.clear();
				for(int i=0; i<checked.length; i++) if(!checked[i]) attributeHideList.add(attributes[i]);
				butHide.setBackground(attributeHideList.isEmpty() ? null : Color.GREEN);
				butRemoveSettings.setVisible(!(attributeFilterList.isEmpty() && attributeHideList.isEmpty()));
				genericUpdate();
			}
			
			else if (o.equals(butRefresh)) {
				genericUpdate();
			}
			
			else if (o.equals(butRemoveSettings)) {
				int conf = JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(), "Do you really want to reset all settings?",
						"Reset " + getModuleName(), JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.NO_OPTION) return;
				attributeFilterList.clear();
				butFilter.setBackground(null);
				attributeHideList.clear();
				butHide.setBackground(null);
				butRemoveSettings.setVisible(false);
				genericUpdate();
			}
		}
	}
}
