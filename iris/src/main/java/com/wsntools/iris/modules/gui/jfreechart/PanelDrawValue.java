/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.jfreechart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaSetFilter;
import com.wsntools.iris.dialogues.DiaSettings;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_Observer;
/**
 * @author Sascha Jungen
 */
public class PanelDrawValue extends JPanel implements IRIS_Observer {

	private static final long serialVersionUID = 1L;

	private Model model;
	private Map<IRIS_Attribute, List<float[]>> mapfilter = new HashMap<IRIS_Attribute, List<float[]>>();
	private int[] range = null;

	private JComboBox comboValue = new JComboBox();
	private JButton butFilterDomain = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnSettings())));

	private ButtonListener listenerButtons = new ButtonListener();
	private ComboListener listenerCombo = new ComboListener();

	private IRIS_Attribute yAxis;

	public PanelDrawValue(Model m) {

		model = m;
		model.registerObserver(this);

		butFilterDomain.setPreferredSize(new Dimension(28, 28));
		comboValue.setPreferredSize(new Dimension(200, 28));
		comboValue.addItem("None");

		this.add(comboValue);
		this.add(butFilterDomain);
		this.setBorder(BorderFactory.createTitledBorder("Add value to draw"));

		butFilterDomain.addActionListener(listenerButtons);
		comboValue.addActionListener(listenerCombo);
	}

	public Map<IRIS_Attribute, List<float[]>> getSelectedFilter() {

		return mapfilter;
	}

	public IRIS_Attribute getYAxis() {
		return yAxis;
	}

	public int[] getSelectedValueRange() {

		return range;
	}

	public boolean isSelectedFunctionAttribute() {

		return model.getMeasureAttribute(
				comboValue.getSelectedItem().toString(), true).isFunctionAttribute();
	}

	public int getSelectedIndex() {

		return comboValue.getSelectedIndex();
	}

	public String getSelectedItem() {

		return comboValue.getSelectedItem().toString();
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

		comboValue.addItem("None");
		List<IRIS_Attribute> listAttr = model.getMeasureAttributes(true);
		for (int j = 0; j < listAttr.size(); j++) {
			if (listAttr.get(j).isDrawable()) {
				comboValue.addItem(listAttr.get(j).getAttributeName());
			}
		}
		mapfilter = new HashMap<IRIS_Attribute, List<float[]>>();
		range = null;

		comboValue.setSelectedIndex(0);
		comboValue.addActionListener(listenerCombo);
	}

	class ComboListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			mapfilter = new HashMap<IRIS_Attribute, List<float[]>>();
			range = null;
		}

	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if (comboValue.getSelectedIndex() > 0) {
				if (model.getMeasureAttribute(
						comboValue.getSelectedItem().toString(), true)
						.isFunctionAttribute()) {
					range = DiaSettings.showFunctionDomainDrawWindow(model,
							(FunctionAttribute) model
									.getMeasureAttribute(comboValue
											.getSelectedItem().toString(), true));
				} else {
					yAxis = null;
					mapfilter = DiaSetFilter.showFilterSettingWindow(model, mapfilter);

					//TODO Richard
					for (Entry<IRIS_Attribute, List<float[]>> temp : mapfilter
							.entrySet()) {
						if (temp.getValue().equals(Float.NaN)) {
							yAxis = temp.getKey();
							mapfilter.remove(temp.getKey());
							break;
						}
					}
				}
			}
		}
	}
}
