/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.jfreechart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.tools.FilterTool;
import com.wsntools.iris.tools.Graph;

/**
 * @author Sascha Jungen 
 * 
 */
public class PanelGraph extends JPanel implements IRIS_Observer {

	private static final long serialVersionUID = 1L;
	private Model model;
	private int charttype = 0;

	// Upper panel containing selection box for values to draw
	//---Linechart
	private JPanel panelSelectValuesLinechart = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private PanelDrawList panelDrawList;

	private JPanel panelDrawButtons = new JPanel(new GridLayout(3,1, 5,5));
	private JButton butAddToList = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnTransfer())));
	private JButton butRefreshList = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnRefresh())));
	private JButton butRemoveFromList = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDeleteItem())));

	private PanelDrawValue panelDrawValue;
	
	//---Barchart
	private PanelDrawBarchart panelSelectValuesBarchart;
	private JButton butConfirmBarchart = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	

	// Middle panel contains visualization of packageinformation
	private Graph panelGraphics = null;

	// Lower panel containing settings for graph type & scaling
	
	private JPanel panelTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	
	private JPanel panelGraphType = new JPanel(new GridLayout(2,1));
	private ButtonGroup radioGroup = new ButtonGroup();
	private JRadioButton radioLine = new JRadioButton("Linechart");
	private JRadioButton radioBar = new JRadioButton("Barchart");
	
	private JPanel panelAdjust = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,
			0));

	private JPanel panelAdjustPacketAll = new JPanel(new FlowLayout(
			FlowLayout.LEFT, 5, 0));
	private JButton butPacketAll = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOkSm())));

	private JPanel panelAdjustPacketRange = new JPanel(new FlowLayout(
			FlowLayout.LEFT, 5, 0));
	private JLabel labelPacketRange = new JLabel("to");
	private JTextField textPacketRangeFrom = new JTextField("", 3);
	private JTextField textPacketRangeTo = new JTextField("", 3);
	private JButton butPacketRange = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOkSm())));

	private JPanel panelAdjustLastPackets = new JPanel(new FlowLayout(
			FlowLayout.LEFT, 5, 0));
	private JLabel labelLastPackets = new JLabel("values");
	private JTextField textLastPackets = new JTextField("", 3);
	private JButton butLastPackets = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOkSm())));

	// Listener for user interactions
	private ButtonGraphAdjustListener listenerButtons = new ButtonGraphAdjustListener();

	// Constructor
	public PanelGraph(Model m) {

		model = m;
		model.registerObserver(this);

		panelGraphics = new Graph(model);

		// Define layout

		// Upper bar

		//Linechart
		panelDrawList = new PanelDrawList(model);
		panelDrawValue = new PanelDrawValue(model);

		panelSelectValuesLinechart.add(panelDrawList);

		butAddToList.setPreferredSize(new Dimension(28, 28));
		butRefreshList.setPreferredSize(new Dimension(28,28));
		butRemoveFromList.setPreferredSize(new Dimension(28,28));
		panelDrawButtons.add(butAddToList);
		panelDrawButtons.add(butRefreshList);
		panelDrawButtons.add(butRemoveFromList);

		panelSelectValuesLinechart.add(panelDrawButtons);

		panelSelectValuesLinechart.add(panelDrawValue);

		//Barchart		
		panelSelectValuesBarchart = new PanelDrawBarchart(model, butConfirmBarchart);

		
		// Graph-Settings
		panelGraphics.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		// Lower Bar
		
		//Graph-Type

		radioGroup.add(radioLine);
		radioGroup.add(radioBar);		
		radioLine.setSelected(true);
		
		panelGraphType.add(radioLine);
		panelGraphType.add(radioBar);
		panelGraphType.setBorder(javax.swing.BorderFactory.createTitledBorder("Graph"));
		
		panelTools.add(panelGraphType);	
		
		//Adjustment
		butPacketAll.setPreferredSize(new Dimension(19, 19));

		panelAdjustPacketAll.add(butPacketAll);
		panelAdjustPacketAll.setBorder(javax.swing.BorderFactory
				.createTitledBorder("All"));

		butPacketRange.setPreferredSize(new Dimension(19, 19));

		panelAdjustPacketRange.add(textPacketRangeFrom);
		panelAdjustPacketRange.add(labelPacketRange);
		panelAdjustPacketRange.add(textPacketRangeTo);
		panelAdjustPacketRange.add(butPacketRange);
		panelAdjustPacketRange.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Show values from"));

		butLastPackets.setPreferredSize(new Dimension(19, 19));

		panelAdjustLastPackets.add(textLastPackets);
		panelAdjustLastPackets.add(labelLastPackets);
		panelAdjustLastPackets.add(butLastPackets);
		panelAdjustLastPackets.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Show last"));

		panelAdjust.add(panelAdjustLastPackets);
		panelAdjust.add(panelAdjustPacketRange);
		panelAdjust.add(panelAdjustPacketAll);
		panelAdjust.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Adjustment"));
		
		panelTools.add(panelAdjust);
		
		
		//Whole Panel
		this.setLayout(new BorderLayout());
		this.add(panelSelectValuesLinechart, BorderLayout.NORTH);
		this.add(panelGraphics, BorderLayout.CENTER);
		this.add(panelTools, BorderLayout.SOUTH);
		this.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Visualization"));

		// Add listener
		radioBar.addActionListener(listenerButtons);
		radioLine.addActionListener(listenerButtons);
		butConfirmBarchart.addActionListener(listenerButtons);
		butAddToList.addActionListener(listenerButtons);
		butRefreshList.addActionListener(listenerButtons);
		butRemoveFromList.addActionListener(listenerButtons);
		butPacketAll.addActionListener(listenerButtons);
		butLastPackets.addActionListener(listenerButtons);
		butPacketRange.addActionListener(listenerButtons);

		updateNewMeasure();
	}

	// --Panel arranging
	private void switchGraphType(int type) {
		
		charttype = type;
		//Remove all panels for other charts
		this.remove(panelSelectValuesLinechart);
		
		this.remove(panelSelectValuesBarchart);
		panelSelectValuesBarchart.setObserverRegisterStatus(false);
		
		switch (type) {
		case 0:			
			this.add(panelSelectValuesLinechart, BorderLayout.NORTH);
			this.repaint();
			this.validate();
			panelAdjust.setVisible(true);
			panelGraphics.switchGraphType(0);
			break;
		case 1:
			this.add(panelSelectValuesBarchart, BorderLayout.NORTH);
			panelSelectValuesBarchart.setObserverRegisterStatus(true);
			panelAdjust.setVisible(false);
			this.repaint();
			this.validate();
			panelGraphics.switchGraphType(1);
			break;
		}
	}
	
	// --Interface methods--
	@Override
	public void updateNewMeasure() {

		// Reset graph values if draw list is empty
		panelDrawList.updateNewAttribute();
		if (panelDrawList.getListItemCount() == 0) {
			panelGraphics.drawNewValues(new float[][] {}, new String[] {});
		} else {
			updateNewPacket(true);
		}
	}

	// Redraw graph on new packet
	@Override
	public void updateNewPacket() {
		updateNewPacket(false);
	}

	public void updateNewPacket(boolean fullredraw) {

		if(charttype == 0) {
			float[][] arr = new float[panelDrawList.getListItemCount()][];
			String[] name = new String[panelDrawList.getListItemCount()];
			IRIS_Attribute yAxis = null;
			ArrayList<Integer> indices = null;
			
			//Depending on selected item and type of attribute create parameter array
			for(int i=0; i<panelDrawList.getListItemCount(); i++) {
							
				name[i] = panelDrawList.getListItemName(i);
				IRIS_Attribute attr = panelDrawList.getListItem(i);
				// TODO THIS IS TH ONLY IMPORTANT SETTING OF YAXIS
				yAxis = panelDrawList.getYAxis();
				
				//Build up filter if necessary or domain range for a function
				if(panelDrawList.getListItem(i).isFunctionAttribute()) {
					int[] range = (int[])panelDrawList.getListItemSetting(i);
					
					if(range == null) {
						arr[i] = model.getMeasureAttributeValuesByName(attr.getAttributeName(), false, false, true);
						
					}
					else {						
						Packet[] allpacks = model.getCurrentMeasurement().getAllPacketsInOrder();
						Packet[] packs = Arrays.copyOfRange(allpacks, range[0], (range[1]>allpacks.length)?allpacks.length:range[1]);
						arr[i] = attr.getValues(packs);
					}
					//Add prediction values if available
					if(((FunctionAttribute)attr).getPredictionValueCount() > 0) {
	
						int oldlength = arr[i].length, ind = 0;
						arr[i] = Arrays.copyOf(arr[i], oldlength + ((FunctionAttribute)attr).getPredictionValueCount());
						float[] pred = ((FunctionAttribute)attr).getPredictionValues();
						for(int j=oldlength; j<arr[i].length; j++) {
							arr[i][j] = pred[ind++];
						}
					}
				}
				else {
					
					arr[i] = model.getMeasureAttributeValuesByName(attr.getAttributeName(), false, false, true);
					
					if(!((Map<IRIS_Attribute, Float>)panelDrawList.getListItemSetting(i)).isEmpty()) {
						Packet[] allpacks = model.getCurrentMeasurement().getAllPacketsInOrder();
						float[] allvals = arr[i];
						int ind = 0;
						indices = FilterTool.getAllFilterPassingIndices((Map<IRIS_Attribute, List<float[]>>)panelDrawList.getListItemSetting(i), allpacks);
						float[] filtervals = new float[indices.size()];
						for(int j=0; j<indices.size(); j++) {
							filtervals[ind++] = allvals[indices.get(j)];
						}
						arr[i] = filtervals;
					}
				}
				
				
			}				
	
			// 
			if (true == fullredraw){
				panelGraphics.forceRedraw(arr, name, null, panelDrawList.getYAxis());
			} else {
				//TODO wenn kein anderer Graph existiert, wird diese Methode aufgerufen und yAxis ist leer!
				panelGraphics.drawNewValues(arr, name, indices, panelDrawList.getYAxis());
			}
		}
		
		//For barchart graphics
		if (charttype == 1) {
			//Value != 'None' selected
			if(panelSelectValuesBarchart.comboValue.getSelectedIndex() > 0) {
				
				float[][] arr;
				String[] name;				
				
				//Separation value != 'None' selected
				if(panelSelectValuesBarchart.comboSeparate.getSelectedIndex() > 0)  {
					ArrayList<String> names = new ArrayList<String>();
					//Split values with specified filter
					float[][] res = FilterTool.separateValuesByFilter(model.getCurrentMeasurement().getAllPacketsInOrder(), model.getMeasureAttribute(panelSelectValuesBarchart.comboValue.getSelectedItem().toString(), true), model.getMeasureAttribute(panelSelectValuesBarchart.comboSeparate.getSelectedItem().toString(), true), names);
					
					//Abort if there are more than CHART_MAX_BARS Values
					if(res.length > Constants.getChartMaxBars()) {
						arr = new float[][] {{0}};
						name = new String[] {"Too many different values to separate"};
						panelGraphics.forceRedraw(arr, name, null, null);
						return;
					}
					
					float[][] vals = new float[1][];
					arr = new float[res.length][1];
					IRIS_FunctionModule func = model.getFunctionInstanceByName(panelSelectValuesBarchart.comboFunctions.getSelectedItem().toString());
					for(int i=0; i<res.length; i++) {
						vals[0] = res[i];
						arr[i] = func.computeData(vals, null);
					}					
					//Build names for chartlabels
					name = new String[names.size()];
					
					name[name.length-1] = names.get(names.size()-1)+"["+panelSelectValuesBarchart.comboFunctions.getSelectedItem().toString() + ": " + panelSelectValuesBarchart.comboValue.getSelectedItem().toString() + " -> " + panelSelectValuesBarchart.comboSeparate.getSelectedItem().toString()+"]";
					for(int i=0; i<names.size()-1; i++) {
						name[i] = names.get(i);
					}
				}
				//No separation selected -> apply function to all values
				else {
					//Apply function to all values
					arr = new float[1][];
					name = new String[] {panelSelectValuesBarchart.comboFunctions.getSelectedItem().toString() + ": " + panelSelectValuesBarchart.comboValue.getSelectedItem().toString()};				
					arr[0] = model.getMeasureAttributeValuesByName(panelSelectValuesBarchart.comboValue.getSelectedItem().toString(), false, false, true);
					arr[0] = model.getFunctionInstanceByName(panelSelectValuesBarchart.comboFunctions.getSelectedItem().toString()).computeData(arr, null);
				}
				
				panelGraphics.forceRedraw(arr, name, null, null);
				panelGraphics.setName("TEEEEEEEEEEEEEST");
			}
			//No Value selected -> delete graph
			else {
				float[][] arr = new float[0][];
				String[] name = new String[0];
				panelGraphics.forceRedraw(arr, name, null, null);
			}
				
		}
		
	}

	// Same behaviour as for new measures
	@Override
	public void updateNewAttribute() {

		updateNewMeasure();
	}

	// --Inner listener classes--

	// Applies settings to the graph
	class ButtonGraphAdjustListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if (ae.getSource().equals(butConfirmBarchart)) {

				panelGraphics.displayAllPackets();
			}
			
			if (ae.getSource().equals(butAddToList)) {
				if (panelDrawValue.getSelectedIndex() > 0) {

					IRIS_Attribute attr = model
							.getMeasureAttribute(panelDrawValue
									.getSelectedItem(), true);
					if (panelDrawValue.isSelectedFunctionAttribute()) {
						panelDrawList.addListItem(attr,
								panelDrawValue.getSelectedValueRange());
					} else {
						panelDrawList.addListItem(attr,
								panelDrawValue.getSelectedFilter(), panelDrawValue.getYAxis());
//						RMT_Attribute test = panelDrawValue.getYAxis();
					}
				}
			}
			if(ae.getSource().equals(butRefreshList)) {
				//Reset all function attributes before redrawing
				for(int i=0; i<panelDrawList.getListItemCount(); i++) {
					if (panelDrawList.getListItem(i).isFunctionAttribute()) {
						((FunctionAttribute)panelDrawList.getListItem(i)).resetPacketCache();
					}
				}
			}
			if (ae.getSource().equals(butRemoveFromList)) {
				panelDrawList.removeSelectedItem();
			}
			
			if (ae.getSource().equals(radioLine) | ae.getSource().equals(radioBar)) {
				//Switch to barchart
				if(radioBar.isSelected() && charttype != 1) {
					switchGraphType(1);
				}
				//Switch to linechart
				else {
					if (radioLine.isSelected() && charttype != 0) {
						switchGraphType(0);
					}
				}
			}

			if (ae.getSource().equals(butPacketAll)) {
				panelGraphics.displayAllPackets();
			}
			if (ae.getSource().equals(butLastPackets)) {

				try {
					int val = Integer.parseInt(textLastPackets.getText());
					val = (val <= 0) ? -1 : val;
					panelGraphics.displayNumberOfLastValues(val);
				} catch (NumberFormatException nfe) {
					JOptionPane
							.showMessageDialog(model.getCurrentlyFocusedWindow(),
									"This is not a valid input. Please enter an integer.");
				}
			}

			if (ae.getSource().equals(butPacketRange)) {

				try {
					int from = Integer.parseInt(textPacketRangeFrom.getText());
					int to = Integer.parseInt(textPacketRangeTo.getText());
					panelGraphics.displayRangeOfValues(from, to);
				} catch (NumberFormatException nfe) {
					JOptionPane
							.showMessageDialog(model.getCurrentlyFocusedWindow(),
									"This is not a valid input. Please enter an integer.");
				}
			}

			// Update text information
			updateNewPacket();
			textLastPackets
					.setText((panelGraphics.getNumberOfLastValues() == -1) ? ""
							: Integer.toString(panelGraphics
									.getNumberOfLastValues()));
			textPacketRangeFrom
					.setText((panelGraphics.getRangeOfValues()[0] == -1) ? ""
							: Integer.toString(panelGraphics.getRangeOfValues()[0]));
			textPacketRangeTo
					.setText((panelGraphics.getRangeOfValues()[1] == -1) ? ""
							: Integer.toString(panelGraphics.getRangeOfValues()[1]));
		}
	}

	class ButtonDrawListListener implements ActionListener {

		// If triggered, get relevant information from panelDrawValue and add
		// them to the list
		public void actionPerformed(ActionEvent ae) {

		}
	}

}
