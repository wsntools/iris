package com.wsntools.iris.tools.datacollector.sending.encoder;

import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.tools.ModuleLoader;
import com.wsntools.iris.tools.datacollector.sending.AbstractEncoder;
import com.wsntools.iris.tools.datacollector.util.ClassLoader;
import com.wsntools.iris.tools.datacollector.util.MethodWrapper;

import net.tinyos.message.Message;

public class TinyOSEncoder extends AbstractEncoder {

	public static final String MESSAGES_PATH = "/home/sasa/Arbeit/message_classes/";
	Message message = null;
	private byte[] data = null;

	public TinyOSEncoder() {

	}

	@Override
	public byte[] encode() {
//		Class<?>[] message_list = ClassLoader.loadClassesByFolder(MESSAGES_PATH);
		Class<?>[] message_list = ModuleLoader.getRadioLinkMessageClassesForReceiving();
		
		Object selection = null;
		do {
			selection = JOptionPane.showInputDialog(null,
					"Please choose a Message type", "",
					JOptionPane.PLAIN_MESSAGE, null, message_list, null);
			if (selection == null)
				return null;
		} while (!(Message.class.isAssignableFrom((Class<?>) selection)));
		Class<Message> send_type = (Class<Message>) selection;
		MethodWrapper wrapper = MethodWrapper.generateMethodWrapper(send_type);

		ArrayList<JLabel> labels = new ArrayList<>();
		ArrayList<JTextField> inputs = new ArrayList<>();
		JPanel dialog_panel = new JPanel(new GridLayout(
				wrapper.getName().length, 2));
		for (int i = 0; i < wrapper.getName().length; i++) {
			JPanel row_panel = new JPanel(new GridLayout(1, 2));
			JLabel label = new JLabel(wrapper.getName()[i] + " type:"
					+ wrapper.getTypes()[i].getSimpleName());
			JTextField txt = new JTextField();
			labels.add(label);
			inputs.add(txt);
			row_panel.add(label);
			row_panel.add(txt);
			dialog_panel.add(row_panel);
		}
		if (JOptionPane.showConfirmDialog(null, dialog_panel, "",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			try {
				message = send_type.newInstance();
				for (int i = 0; i < wrapper.getName().length; i++) {
					Class<?> parameter_type = wrapper.getTypes()[i];
					Object parameter = null;
					if (parameter_type.isAssignableFrom(int.class)) {
						parameter = Integer.parseInt(inputs.get(i).getText());
					} else if (parameter_type.isAssignableFrom(short.class)) {
						parameter = Short.parseShort(inputs.get(i).getText());
					} else if (parameter_type.isAssignableFrom(long.class)) {
						parameter = Long.parseLong(inputs.get(i).getText());
					} else if (parameter_type.isAssignableFrom(float.class)) {
						parameter = Float.parseFloat(inputs.get(i).getText());
					} else if (parameter_type.isAssignableFrom(double.class)) {
						parameter = Double.parseDouble(inputs.get(i).getText());
					}else {
						parameter = parameter_type
								.cast(inputs.get(i).getText());
					}
					Object[] args = new Object[] { parameter };

					wrapper.getSetter()[i].invoke(message, args);
					
				}
				data = message.dataGet();
				return data;
			} catch (IllegalArgumentException | InvocationTargetException
					| InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			return null;
		}
	}

	public Message getMessage() {
		return message;
	}

	public void setMes(Message mes) {
		this.message = mes;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return data;
	}
	
}
