/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.receiving;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.tools.ModuleLoader;

public class MetaDataCollector {

	private List<AbstractCollector> listeners = new ArrayList<>();
	private Model model;
	private boolean isActive;

	public MetaDataCollector(Model model) {
		this.model = model;
		isActive = false;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActivation(boolean active) {
		isActive = active;
	}
	
	public List<AbstractCollector> getListener() {
		return listeners;
	}
	
	public void removeAllListener() {		
		for(AbstractCollector ac: listeners) {			
			ac.closeConnection();
		}		
		listeners.clear();
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
		if(!isActive) return;
		//Only record message, if the collection is active
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
					x = Constants.getPacketDefaultEmptyNumber();
				}
			}
			float_values[i] = x;
		}

		Packet p = new Packet(key_array, float_values);
		model.addPacket(p);
	}

	public void buildNewListener() {
		Class<?>[] collector_types = ModuleLoader
				.loadClassesByPackage(Constants.getNetReceivingCollectorPackage());
		Class<?>[] decoder_types = ModuleLoader
				.loadClassesByPackage(Constants.getNetReceivingDecoderPackage());
		JComboBox<Class<?>> collector_selection = new JComboBox<>(
				collector_types);
		JComboBox<Class<?>> decoder_selection = new JComboBox<>(decoder_types);
		JTextField port_selection = new JTextField(10);
		
		JPanel panel_collector = new JPanel(new BorderLayout());
		panel_collector.add(new JLabel("Collector:"), BorderLayout.WEST);
		panel_collector.add(collector_selection, BorderLayout.CENTER);
		JPanel panel_decoder = new JPanel(new BorderLayout());
		panel_decoder.add(new JLabel("Decoder:"), BorderLayout.WEST);
		panel_decoder.add(decoder_selection, BorderLayout.CENTER);
		JPanel panel_port = new JPanel(new BorderLayout());
		panel_port.add(new JLabel("Port:"), BorderLayout.WEST);
		panel_port.add(port_selection, BorderLayout.CENTER);
		JPanel dialog_panel = new JPanel();
		{
			dialog_panel.setLayout(new GridLayout(3, 1));
			dialog_panel.add(panel_collector);
			dialog_panel.add(panel_decoder);
			dialog_panel.add(panel_port);
		}
		if (JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(), dialog_panel, "IRIS - Receiving Listener Setup",
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
