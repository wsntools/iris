/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FilterSettings;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaSetFilter;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.tools.FilterTool;

/**
 * @author Sascha Jungen
 */
public class PanelFilter extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Model model;
	private PanelFilter ref = this;
	
	private Map<IRIS_Attribute, List<float[]>> newMapFilter, oldMapFilter;
	private ArrayList<IRIS_Attribute> listAttributeOrder;	
	
	private JPanel panelList = new JPanel();
	private JList<String> listFilters;
	private DefaultListModel<String> listModel;
	private JButton butDel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDeleteSm())));
	
	private JPanel panelUpper = new JPanel(new BorderLayout());
	private JPanel panelCombo = new JPanel();
	private JComboBox<String> comboAttributes = new JComboBox<String>();
	private JPanel panelTextFilter = new JPanel();
	private JLabel labelFilter = new JLabel("Display: ");
	private JTextField textFilter = new JTextField(10);
	private JButton butAdd = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnAddSm())));	
	private JButton butHelp = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnHelp())));
	
	private JPanel panelRight = new JPanel();
	private JButton butLoad = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnLoadStd())));	
	private JButton butSave = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnSave())));
	private JButton butDelFilter = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDeleteItem())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	public PanelFilter(Model m, Map<IRIS_Attribute, List<float[]>> filter) {
		model = m;
		
		oldMapFilter = filter;
		newMapFilter = new HashMap<IRIS_Attribute, List<float[]>>();
		listAttributeOrder = new ArrayList<IRIS_Attribute>();
		for(IRIS_Attribute key:filter.keySet()) {
			newMapFilter.put(key, filter.get(key));
			listAttributeOrder.add(key);
		}
		
		butHelp.setPreferredSize(new Dimension(28, 28));
		comboAttributes.setPreferredSize(new Dimension(200, 28));
		List<IRIS_Attribute> nonFuncAttr = model.getMeasureAttributesBySpecification(false, false, false, true, false, false);
		for (int i = 0; i < nonFuncAttr.size(); i++) {
			comboAttributes.addItem(nonFuncAttr.get(i).getAttributeName());
		}
		butAdd.setPreferredSize(new Dimension(18, 18));
		
		panelCombo.add(butHelp);
		panelCombo.add(comboAttributes);
		panelTextFilter.add(labelFilter);
		panelTextFilter.add(textFilter);
		panelTextFilter.add(butAdd);
		
		panelUpper.add(panelCombo, BorderLayout.NORTH);
		panelUpper.add(panelTextFilter, BorderLayout.CENTER);
		
		
		listModel = new DefaultListModel<String>();
		listFilters = new JList<String>(listModel);
		listFilters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		updateList();
		butDel.setPreferredSize(new Dimension(18, 18));
		panelList.add(listFilters);
		panelList.add(butDel);
		panelList.setBorder(BorderFactory.createTitledBorder("Applied Filters"));
		
		butSave.setToolTipText("Saves the current filter configuration");
		butSave.setPreferredSize(new Dimension(28,28));
		butLoad.setToolTipText("Loads a previously saved filter configuration");
		butLoad.setPreferredSize(new Dimension(28,28));
		butDelFilter.setToolTipText("Deletes one of the previously created filters");
		butDelFilter.setPreferredSize(new Dimension(28,28));
		
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		panelRight.add(butSave);
		panelRight.add(butLoad);
		panelRight.add(butDelFilter);
		
		
		this.setLayout(new BorderLayout());
		this.add(panelUpper, BorderLayout.NORTH);
		this.add(panelList, BorderLayout.CENTER);
		this.add(panelRight, BorderLayout.EAST);
		
		butAdd.addActionListener(listenerButtons);
		butDel.addActionListener(listenerButtons);
		butHelp.addActionListener(listenerButtons);
		butSave.addActionListener(listenerButtons);
		butLoad.addActionListener(listenerButtons);
		butDelFilter.addActionListener(listenerButtons);
	}
	
	private void updateList() {
		
		listModel.clear();
		for(IRIS_Attribute attr: listAttributeOrder) {
			
			List<float[]> listDisplay = newMapFilter.get(attr);
			String displayed = Arrays.toString(listDisplay.get(0));
			for(int i=1; i<listDisplay.size(); i++) {
				displayed += ", " + Arrays.toString(listDisplay.get(i));
			}
			listModel.addElement(attr.getAttributeName() + " - " + displayed);
		}
		this.repaint();
		this.revalidate();
	}
	
	public Map<IRIS_Attribute, List<float[]>> getFilterSettings() {
		return newMapFilter;
	}
	
	
	private class ButtonListener implements ActionListener {
	
		public void actionPerformed(ActionEvent ae) {
	
			if(ae.getSource().equals(butAdd)) {
				if(comboAttributes.getSelectedIndex() == -1) return;
				if(FilterTool.isValidFilterTextInput(textFilter.getText())) {
					IRIS_Attribute attr = model.getMeasureAttribute((String)comboAttributes.getSelectedItem(), true);
					newMapFilter.put(attr, FilterTool.parseFilterTextInput(textFilter.getText(), newMapFilter.get(attr)));
					if(!listAttributeOrder.contains(attr)) {
						listAttributeOrder.add(attr);
					}
					updateList();
					textFilter.setText("");
				}
				else {
					JOptionPane.showMessageDialog(ref, "Please enter a valid filter range", "IRIS - Invalid Filter", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else if(ae.getSource().equals(butDel)) {
				int index;
				if((index = listFilters.getSelectedIndex()) != -1) {
					newMapFilter.remove(listAttributeOrder.get(index));
					listAttributeOrder.remove(index);
					listModel.remove(index);
				}
			}
			else if(ae.getSource().equals(butHelp)) {
				JOptionPane.showMessageDialog(ref, "Allowed expressions are:\n-numbers and ranges separated by a comma (e.g. 2, 7-9, 10)\n-positive and negative numbers (e.g. -2--6)\n-floats (e.g. 4.6, 5.7-8.2)\n-no brackets allowed", "IRIS - Filter Definition", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(ae.getSource().equals(butSave)) {
				String input = JOptionPane.showInputDialog(model.getCurrentlyFocusedWindow(), "Please enter a name for the current filter configuration:");
				if(input == null) return;
				input = input.trim();
				if(input.isEmpty()) JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "Please enter a valid filtername");
				else  {
					model.addUserDefinedFilter(new FilterSettings(input, new HashMap<IRIS_Attribute, List<float[]>>(newMapFilter)));
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), input + " has been successfully saved");
				}
			}
			else if (ae.getSource().equals(butLoad)) {
				List<FilterSettings> filter = model.getUserDefinedFilterSets();
				if(filter.size() > 0) {
					String[] filterNames = new String[filter.size()];
					for(int i=0; i<filter.size(); i++) {
						filterNames[i] = filter.get(i).getFilterName();
					}
					String choice = (String) JOptionPane.showInputDialog(model.getCurrentlyFocusedWindow(), "Choose the filter to load", "IRIS - Load Filter", JOptionPane.PLAIN_MESSAGE, null, filterNames, filterNames[0]);
					if(choice != null) {
						newMapFilter.clear();
						for(int i=0; i< filterNames.length; i++) {
							if(filterNames[i].equals(choice)) {
								newMapFilter.putAll(filter.get(i).getFilterMap());
								break;
							}
						}
						listAttributeOrder.clear();
						listAttributeOrder.addAll(newMapFilter.keySet());
						updateList();
					}
				}
				else {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "There are no saved filters available");
				}
			}
			else if (ae.getSource().equals(butDelFilter)) {
				List<FilterSettings> filter = model.getUserDefinedFilterSets();
				if(filter.size() > 0) {
					String[] filterNames = new String[filter.size()];
					for(int i=0; i<filter.size(); i++) {
						filterNames[i] = filter.get(i).getFilterName();
					}
					String choice = (String) JOptionPane.showInputDialog(model.getCurrentlyFocusedWindow(), "Choose the filter to delete", "IRIS - Load Filter", JOptionPane.PLAIN_MESSAGE, null, filterNames, filterNames[0]);
					if(choice != null) {
						for(int i=0; i< filterNames.length; i++) {
							if(filterNames[i].equals(choice)) {
								model.removeUserDefinedFilter(filter.get(i));
								break;
							}
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(model.getCurrentlyFocusedWindow(), "There are no saved filters available");
				}
			}
		}
	}	
}
