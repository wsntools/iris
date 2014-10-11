/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.receiving;

import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.tools.datacollector.util.ClassLoader;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;

public class MetaDataCollector {

	List<AbstractCollector> listeners = new ArrayList<>();
	private Model model;

	public MetaDataCollector(Model model) {
		this.model = model;
	}

	public void addNewListener(Class<?> listener_type, Class<?> decoder_type,
			String port) {
		try {
			Constructor<?> constr = listener_type
					.getConstructor(new Class<?>[] { MetaDataCollector.class,
							String.class, Class.class });
			AbstractCollector adcl = (AbstractCollector) constr
					.newInstance(new Object[] { this, port, decoder_type });
			listeners.add(adcl);
		} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void messageReceived(HashMap<String, Object> rec_values) {
		System.out.println("Received message");
		System.out.println(rec_values);
		Set<String> keys = rec_values.keySet();
		String[] key_array;
		key_array = keys.toArray(new String[rec_values.size()]);
		Float[] float_values = new Float[key_array.length];

		for (int i = 0; i < key_array.length; i++) {
			float x;
			try {
				x = Float.parseFloat(rec_values.get(key_array[i]).toString());
			} catch (NumberFormatException e) {
				try {
					x = (float) rec_values.get(key_array[i]);
				} catch (ClassCastException e1) {
					x = -0.0f;
				}
			}
			float_values[i] = x;
		}

		Packet p = new Packet(key_array, float_values);
		model.addPacket(p);
	}

	public void buildNewListener() {
		Class<?>[] collector_types = ClassLoader
				.loadClassesByPackage("com.wsntools.iris.tools.datacollector.receiving.collector");
		Class<?>[] decoder_types = ClassLoader
				.loadClassesByPackage("com.wsntools.iris.tools.datacollector.receiving.decoder");
		JComboBox<Class<?>> collector_selection = new JComboBox<>(
				collector_types);
		JComboBox<Class<?>> decoder_selection = new JComboBox<>(decoder_types);
		JTextField port_selection = new JTextField();
		JPanel dialog_panel = new JPanel();
		{
			dialog_panel.setLayout(new GridLayout(3, 1));
			dialog_panel.add(collector_selection);
			dialog_panel.add(decoder_selection);
			dialog_panel.add(port_selection);
		}
		if (JOptionPane.showConfirmDialog(null, dialog_panel, "",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null) == JOptionPane.OK_OPTION) {
			Class<?> collector;
			Class<?> decoder;

			if (!(collector_selection.getSelectedItem() instanceof Class<?>)
					|| !(decoder_selection.getSelectedItem() instanceof Class<?>)
					|| port_selection.getText().equals("")) {
				return;
			} else {
				collector = (Class<?>) collector_selection.getSelectedItem();
				decoder = (Class<?>) decoder_selection.getSelectedItem();
			}
			addNewListener(collector, decoder, port_selection.getText());
		}
	}
}
