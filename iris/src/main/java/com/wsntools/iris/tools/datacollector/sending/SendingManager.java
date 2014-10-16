/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.sending;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.tools.ModuleLoader;

public class SendingManager {
	private List<ConstructorWrapper> getAnnotatedConstructors(Class<?> encoder_type) {
		Constructor<?>[] consts = encoder_type.getConstructors();
		List<ConstructorWrapper> annotated_constructors = new ArrayList<>();
		for (Constructor<?> c : consts) {
			Class<?>[] parameter_types = c.getParameterTypes();
			Annotation[][] parameter_annotations = c.getParameterAnnotations();
			if (parameter_annotations.length == parameter_types.length) {
				List<ConstructorParameter> const_params = new ArrayList<>();
				for (int i = 0; i < parameter_types.length; i++) {
					// check if one of the annotations is ParameterMapping
					for (int j = 0; j < parameter_annotations[i].length; j++) {
						if (parameter_annotations[i][j] instanceof AbstractEncoder.ParameterMapping) {
							// create new ConstructorParameter
							ConstructorParameter cp = new ConstructorParameter(
									((AbstractEncoder.ParameterMapping) parameter_annotations[i][j])
											.name(), parameter_types[i]);
							// add ConstructorParameter to List
							const_params.add(cp);
							break;
						}
					}
				}
				if (const_params.size() > 0) {
					// Create new ConstructorWrapper and add to
					// annotated_constructors
					annotated_constructors.add(new ConstructorWrapper(c,
							const_params));
				}
			}
		}
		return annotated_constructors;
	}

	public SendingConfiguration send(Class<?> encoder_type, String port)
			throws InvalidParameterException {
		AbstractEncoder enc = null;
		try {
			List<ConstructorWrapper> annotated_constructors = getAnnotatedConstructors(encoder_type);
			if (annotated_constructors.size() != 0) {
				JOptionPane.showInputDialog(null, "", "IRIS - Choose a constructor",
						JOptionPane.PLAIN_MESSAGE, null,
						annotated_constructors.toArray(), null);
			} else {
				Constructor<?>[] consts = encoder_type.getConstructors();

				boolean found_standard_constructor = false;
				for (int i = 0; i < consts.length; i++) {
					if (consts[i].getParameterTypes().length == 0) {
						found_standard_constructor = true;
					}
				}
				if (!found_standard_constructor) {
					throw new InvalidParameterException(
							"The provided encoder_type does not contain annotated constructors or an argumentless constructor");
				}
				enc = (AbstractEncoder) encoder_type.newInstance();
			}
			if (enc != null) {
				return send(enc, port);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SendingConfiguration send(AbstractEncoder encoder, String port) {
		try {
			encoder.encode();
			Class<?>[] sender_types = ModuleLoader
					.loadClassesByPackage(Constants.getNetSendingSenderPackage());

			Object selection = null;
			do {
				selection = JOptionPane.showInputDialog(null,
						"Please choose a sender type", "IRIS - Sender Selection",
						JOptionPane.PLAIN_MESSAGE, null, sender_types, null);
				if (selection == null)
					return null;
			} while (Class.class.isAssignableFrom((Class<?>) selection));

			Class<?> sender_type = (Class<?>) selection;

			ISender sender = (ISender) sender_type.newInstance();
			sender.send(port, encoder);
			return new SendingConfiguration(encoder, sender, port);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SendingConfiguration send(AbstractEncoder encoder, ISender sender,
			String port) {
		encoder.encode();
		sender.send(port, encoder);
		return new SendingConfiguration(encoder, sender, port);
	}
	
	public SendingConfiguration send(SendingConfiguration sendConfig) {
		sendConfig.getEncoder().encode();
		sendConfig.getSender().send(sendConfig.getPort(), sendConfig.getEncoder());
		return sendConfig;
	}

//	public void send(SendingConfiguration config) {
//		send(config.getEncoder(), config.getSender(), config.getPort());
//	}

	public SendingConfiguration buildSendingSetup() {		
		
		Class<?>[] encoder_types = ModuleLoader.loadClassesByPackage(Constants.getNetSendingEncoderPackage());
		JPanel dialog_panel = new JPanel(new GridLayout(2,1));
		JComboBox<Class<?>> encoder_selection = new JComboBox<Class<?>>(encoder_types);
		JTextField port_selection = new JTextField();
		{
			JPanel encoder_panel = new JPanel(new BorderLayout());
			JLabel encoder_label = new JLabel("Encoder:");
			encoder_panel.add(encoder_label,BorderLayout.WEST);
			encoder_panel.add(encoder_selection,BorderLayout.CENTER);
			dialog_panel.add(encoder_panel);
			JPanel port_panel = new JPanel(new BorderLayout());
			JLabel port_label = new JLabel("Port:");
			port_panel.add(port_label,BorderLayout.WEST);
			port_panel.add(port_selection,BorderLayout.CENTER);
			dialog_panel.add(port_panel);
		}
		int choice = JOptionPane.showConfirmDialog(null, dialog_panel, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if(choice == JOptionPane.CANCEL_OPTION) return null;
		Class<?> encoder_type = (Class<?>)encoder_selection.getSelectedItem();
		String port = port_selection.getText();
		
		
		return send(encoder_type, port);		
	}

	class ConstructorParameter {
		private String name;
		private Class<?> type;

		public ConstructorParameter(String name, Class<?> type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}
	}

	class ConstructorWrapper {
		private List<ConstructorParameter> params;
		private Constructor<?> constructor;

		public ConstructorWrapper(Constructor<?> constructor,
				List<ConstructorParameter> params) {
			this.constructor = constructor;
			this.params = params;
		}

		public List<ConstructorParameter> getParams() {
			return params;
		}

		public void setParams(List<ConstructorParameter> params) {
			this.params = params;
		}

		public Constructor<?> getConstructor() {
			return constructor;
		}

		public void setConstructor(Constructor<?> constructor) {
			this.constructor = constructor;
		}

		@Override
		public String toString() {
			String ret = constructor.getName() + "(";
			for (ConstructorParameter constructorParameter : params) {
				ret += " " + constructorParameter.name + ":"
						+ constructorParameter.getType().getCanonicalName();
			}
			ret += ")";
			return ret;
		}
	}
}
