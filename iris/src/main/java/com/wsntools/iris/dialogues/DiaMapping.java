/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class DiaMapping extends JDialog {

	private Model model;
	private DiaMapping ref = this;
	
	private Map<String, IRIS_Attribute> mapNameToAttribute;
	
	private JPanel panelMain = new JPanel(new BorderLayout());
	
	private MappingTile[] arrMappingTiles;
	private JScrollPane scrollMappingTiles;
	private JPanel panelMappingTiles = new JPanel();
	
	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	private DiaMapping(Model m, Map<String, IRIS_Attribute> map) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		
		mapNameToAttribute = map;
		
		panelMappingTiles.setLayout(new BoxLayout(panelMappingTiles, BoxLayout.Y_AXIS));
		//Get all available GUI elements and list them for selection
		arrMappingTiles = new MappingTile[mapNameToAttribute.size()];
		String[] keys = new String[mapNameToAttribute.size()];
		keys = mapNameToAttribute.keySet().toArray(keys);
		for(int i=0; i<keys.length; i++) {
			arrMappingTiles[i] = new MappingTile(model, keys[i], mapNameToAttribute.get(keys[i]), i+1);
			panelMappingTiles.add(arrMappingTiles[i]);
		}
		scrollMappingTiles = new JScrollPane(panelMappingTiles);
		scrollMappingTiles.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollMappingTiles.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);
		panelButton.add(butCancel);
		
		panelMain.add(scrollMappingTiles, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);
		
		butCancel.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);
		
		// Windowsettings
		this.setTitle("IRIS - Mapping");
		// this.setResizable(false);
		this.setContentPane(panelMain);
		this.setPreferredSize(new Dimension(400, 400));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();

		// Set windowposition to center
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
				(tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()),
				((this.getY() < 0) ? 0 : this.getY()));
		this.setVisible(true);
	}
	
	
	private Map<String, IRIS_Attribute> getMapping() {
		return mapNameToAttribute;
	}
	
	/**
	 * Creates a dialog to perform a mapping of the given names to available attributes
	 * @param m The model
	 * @param map The map containing the requested mappings as keys (may also map to null if no mapping has been done before)
	 * @return Implicit change of the mappings contained in map (+reference)
	 */
	public static Map<String, IRIS_Attribute> showMappingWindow(Model m, Map<String, IRIS_Attribute> map) {
		DiaMapping diaMap = new DiaMapping(m, map);
		return diaMap.getMapping();
	}
	
	
	
	/**
	 * @author Sascha Jungen
	 * M#2242754
	 *
	 * This panel holds information about the mapping of hard coded attributes
	 * to the measurement-specific attributes
	 */
	private class MappingTile extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private JLabel labelAttName = new JLabel();
		private JLabel labelArrow = new JLabel(new ImageIcon(Constants.getPathPicsMisc() + Constants.getNameMiscArrow()));
		private JComboBox<String> comboMapTo = new JComboBox<String>();
		
		public MappingTile(Model m, String toMap, IRIS_Attribute mapsTo, int number) {
			
			labelAttName.setText(toMap);
			IRIS_Attribute[] normatt = m.getMeasureNormalAttributes();
			comboMapTo.addItem("(None)");
			for(int i=0; i<normatt.length; i++) {
				comboMapTo.addItem(normatt[i].getAttributeName());
			}
			
			//Get current mapping index
			if(mapsTo != null) {
				comboMapTo.setSelectedItem(mapsTo.getAttributeName());
			}
			
			//this.setPreferredSize(new Dimension(300, 40));
			this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
			this.add(labelAttName);
			this.add(labelArrow);
			this.add(comboMapTo);
			this.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapping " + number));
		}		
		
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
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if(ae.getSource().equals(butOK)) {
				//Override existing mappings with new ones
				for(MappingTile mt: arrMappingTiles) {
					mapNameToAttribute.put(mt.getMappingObject(), model.getMeasureAttribute(mt.getMappingSelection(), false));
				}
				ref.dispose();
			}
			else if(ae.getSource().equals(butCancel)) {
				ref.dispose();
			}
		}
	}
}
