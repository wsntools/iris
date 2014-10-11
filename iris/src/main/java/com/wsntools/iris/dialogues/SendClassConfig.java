/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.tinyos.message.Message;
import net.tinyos.message.MoteIF;

import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.extensions.MessageAttributeWrapper;
import com.wsntools.iris.extensions.MessageWrapper;
import com.wsntools.iris.extensions.typeSupport.TypeSupport;
import com.wsntools.iris.tools.ModuleLoader;

public class SendClassConfig extends JFrame implements ActionListener {

	/**
	 * classes in the folder/ name
	 */
	Class<? extends Message>[] classes;
	String[] classNames;

	/**
	 * Textfields and setter methods to create an object
	 */
	JTextField[] fields;
	
	JComboBox classeChooser;

	Measurement meas;

	/**
	 * The created Message
	 */
	MessageWrapper msg;
	String msgName;
	public Class<? extends Message> selectedClass;
	MessageAttributeWrapper msgParamWrap;

	int address;

	boolean useFields = true, useSetMethods = true, useStatics;

	JComboBox classbox; // read from the folder
	JComboBox existingMsg; // read from measurement object
	JTextField msgNametf; // message name

	Component[] loadComponents;

	boolean new_load = false;

	/**
	 * extra textfields at the end (address)
	 */
	private int extraFields = 1;

	public SendClassConfig(Measurement meas) {
		super("Configure message");
		//		super(parent, true);
		this.setTitle("Message creation");
		this.setSize(300, 200);
		this.meas = meas;
		display(meas);
	}

	private void newMessage(Class<?> cls) {
		if (cls == null) {
			this.setContentPane(createContentPane(null));
			existingMsg.setSelectedIndex(0);
		} else {
			this.setVisible(false);
			msg = null;
			this.setContentPane(createContentPane(cls));
			this.setVisible(true);
			existingMsg.setSelectedIndex(0);
			msgNametf.setText("untitled_message");
			classbox.setSelectedItem(cls.getSimpleName());
		}
		msg = null;
	}

	private JPanel createContentPane(Class<?> c) {
		JPanel panel;
		if (c == null) {
			panel = new JPanel(new GridLayout(8, 3));
			Component[] co = getLoadComponents();
			panel.add(co[0]);
			panel.add(co[1]);
			panel.add(co[2]);
			return panel;
		} else {
			//			System.out.println(fields.length + " fields");
			ArrayList<Component[]> fieldComps = new ArrayList<Component[]>();

			// create the components to set up all values
			Iterator<Object[]> iter = msgParamWrap.iterator();
			int maxNameFieldLength = 100;
			while (iter.hasNext()) {
				Object[] fo = iter.next();
				maxNameFieldLength = Math.max(((String) fo[1]).length(), maxNameFieldLength);
				fieldComps.add(getFieldComps(fo));
			}

			fieldComps.add(getFieldComps(new Object[] { Integer.TYPE, "address", null }));
			// set the address field to broadcast

			this.fields = new JTextField[fieldComps.size()];

			panel = new JPanel(new GridLayout(extraFields + fieldComps.size() + 1, 3));

			Component[] co = getLoadComponents();
			panel.add(co[0]);
			panel.add(co[1]);
			panel.add(co[2]);
			int i = 0;
			for (Component[] fico : fieldComps) {
				panel.add(fico[0]);
				panel.add(fico[1]);
				panel.add(fico[2]);
				fields[i++] = (JTextField) fico[2];
			}
			if (msg != null) {
				fields[i - 1].setText(String.valueOf(meas.getAddress()));
			} else
				fields[i - 1].setText(String.valueOf(MoteIF.TOS_BCAST_ADDR));
			// the feature for Richard. when pressing enter the cursor will jump to the next field
			for (int fi = 0; fi < fields.length - 1; fi++) {
				final int fip = fi;
				fields[fi].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fields[fip + 1].requestFocus();
					}
				});
			}

			msgNametf = new JTextField();
			panel.add(msgNametf);

			JButton done = new JButton("done");
			done.setName("done");
			done.addActionListener(this);
			this.setSize(maxNameFieldLength * 12 + 100, (fields.length + 4) * 25);
			panel.add(done);
			JButton update = new JButton("create/update");
			update.setName("update");
			update.addActionListener(this);
			panel.add(update);
			return panel;
		}
	}

	/**
	 * int, bool, float, short, long
	 * 
	 * @param cls
	 * @param text
	 * @return
	 */
	public static Object getValue(Class<?> cls, String text) {
		//		System.out.println(cls.getSimpleName() + " " + text);
		if (text.equals(""))
			return null;
		if (cls.equals(Integer.TYPE)) {
			return (int) Integer.parseInt(text);
		} else if (cls.equals(Boolean.TYPE)) {
			return Boolean.parseBoolean(text);
		} else if (cls.equals(Float.TYPE)) {
			return Float.parseFloat(text);
		} else if (cls.equals(Short.TYPE)) {
			return Short.parseShort(text);
		} else if (cls.equals(Long.TYPE)) {
			return Long.parseLong(text);
		} else if (cls.equals(Byte.TYPE)) {
			return Byte.parseByte(text);
		} else if (cls.equals(TypeSupport.INT_ARRAY)) {
			return getArray(Integer.TYPE, text);
		} else if (cls.equals(TypeSupport.BYTE_ARRAY)) {
			return getArray(Byte.TYPE, text);
		} else if (cls.equals(TypeSupport.SHORT_ARRAY)) {
			return getArray(Short.TYPE, text);
		} else if (cls.equals(TypeSupport.LONG_ARRAY)) {
			return getArray(Long.TYPE, text);
		} else {
			System.out.println("type not supported: " + cls.getSimpleName());
		}
		return null;
	}

	static Object getArray(Class<?> baseClass, String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		int i = 0;
		if (baseClass.equals(Integer.TYPE)) {
			int[] array = new int[st.countTokens()];
			while (st.hasMoreTokens())
				array[i++] = Integer.parseInt(st.nextToken());
			return array;
		} else if (baseClass.equals(Byte.TYPE)) {
			byte[] array = new byte[st.countTokens()];
			while (st.hasMoreTokens())
				array[i++] = Byte.parseByte(st.nextToken());
			return array;
		} else if (baseClass.equals(Short.TYPE)) {
			short[] array = new short[st.countTokens()];
			while (st.hasMoreTokens())
				array[i++] = Short.parseShort(st.nextToken());
			return array;
		} else if (baseClass.equals(Long.TYPE)) {
			long[] array = new long[st.countTokens()];
			while (st.hasMoreTokens())
				array[i++] = Long.parseLong(st.nextToken());
			return array;
		} else
			return null;
	}

	/**
	 * 
	 * @param attribute
	 *            0 is the attribute name, 1 the class, 2 the input field
	 * @return the components for the gui
	 */
	private Component[] getFieldComps(Object[] attribute) {
		Component[] cos = new Component[3];
		cos[0] = new JLabel((String) attribute[1]);
		String className = ((Class<?>) attribute[0]).getSimpleName();
		if (((Class<?>) attribute[0]).isArray()) {
			if (msgParamWrap.numElements.containsKey((String) attribute[1]))
				className = className.substring(0, className.length() - 1)
						+ msgParamWrap.numElements.get((String) attribute[1]) + "]";
		}
		cos[1] = new JLabel(className);
		cos[2] = new JTextField("");
		return cos;
	}

	private Component[] getLoadComponents() {
		if (loadComponents != null) {
			classbox = getClassChooser();
			loadComponents[0] = classbox;
			loadComponents[1] = existingMsg = new JComboBox(meas.getMsgNames(true));
//			System.out.println(Arrays.toString((meas.getMsgNames(true))));
			return loadComponents;
		}
		Component[] co = new Component[3];
		classbox = getClassChooser();
		classbox.setName("classbox");
		classbox.addActionListener(this);
		co[0] = classbox;
		JButton newMsg = new JButton("New/Load");
		newMsg.setName("new");
		newMsg.addActionListener(this);
		co[1] = newMsg;
		existingMsg = new JComboBox(meas.getMsgNames(true));
		existingMsg.setName("loadExisitingMessage");
		existingMsg.addActionListener(this);
		co[2] = existingMsg;
		return co;
	}

	/**
	 * reads the classes in the defines folder and creates the combobox of them
	 * 
	 * @return
	 */
	private JComboBox getClassChooser() {
		//		classes = ModuleLoader.getRadioLinkMessageClasses();
		classes = ModuleLoader.getRadioLinkMessageClassesForSending();
		//		System.out.println(new File(folder).getAbsolutePath());
		classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = classes[i].getSimpleName();
		}
		classeChooser = new JComboBox(classNames);
		return classeChooser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((Component) e.getSource()).getName();
		//		System.out.println(name);
		if (name == "done" || name == "update") {
			try {
				msgName = msgNametf.getText().replace(" ", "_");
				try {
					address = Integer.valueOf(fields[fields.length - 1].getText());
				} catch (NumberFormatException exc) {
					address = MoteIF.TOS_BCAST_ADDR;
				}
				if (msg == null)
					msg = new MessageWrapper(selectedClass, msgName, address);
				else {
					msg.name = msgName;
					msg.address = address;
				}
				Object o = null;
				for (int i = 0; i < fields.length - extraFields; i++) {
					try {
						o = getValue(msgParamWrap.classes[i], fields[i].getText());
					} catch (Exception exc) {
						System.out.println("Wrong Input for " + msgParamWrap.names[i]);
						o = null;
					}
					msg.set(i, o);
				}
				meas.addMsg(msg);
				if (name == "done")
					setVisible(false);
				else {
					existingMsg.addItem(msgName);
					existingMsg.setSelectedItem(msgName);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (name == "classbox")
			new_load = false;
		else if (name == "loadExisitingMessage")
			new_load = true;
		else if (name == "new") { // button for new and load
			if (!new_load) { // new
				selectedClass = classes[classeChooser.getSelectedIndex()];
				msgParamWrap = MessageAttributeWrapper.getMsgAttributeWrapper(selectedClass);
				newMessage(selectedClass);
			} else { // load
				msgName = (String) existingMsg.getSelectedItem();
				if (msgName == "NEW MESSAGE") {
					selectedClass = classes[classeChooser.getSelectedIndex()];
					newMessage(selectedClass);
					return;
				}
				// LOADING AN EXSISTING
				msg = meas.getMessageWrapper(msgName);
				selectedClass = msg.cls;
				msgParamWrap = MessageAttributeWrapper.getMsgAttributeWrapper(selectedClass);
				this.setVisible(false);
				this.setContentPane(createContentPane(selectedClass));
				this.setVisible(true);
				classbox.setSelectedItem(selectedClass.getSimpleName());
				existingMsg.setSelectedItem(msgName);
				msgNametf.setText(msgName);
				loadMessage(msg);
			}
		}
	}

	public void display(Measurement meas) {
		msg = meas.getActualMessageW();
		this.meas = meas;
		if (msg == null) {
			newMessage(null);
		} else {
			selectedClass = msg.cls;
			msgParamWrap = MessageAttributeWrapper.getMsgAttributeWrapper(selectedClass);
			this.setContentPane(createContentPane(selectedClass));
			classeChooser.setSelectedItem(selectedClass.getSimpleName());
			msgName = meas.getActualMessageName();
			msgNametf.setText(msgName);
			existingMsg.setSelectedItem(msgName);
			loadMessage(msg);
		}

		this.setVisible(true);
		new_load = false;
	}

	private void loadMessage(MessageWrapper msg) {
		int i = 0;
		for (; i < fields.length - 1; i++) {
			String t = null;
			Object v = msg.get(i);
			if (v == null)
				continue;
			// following bullshit is only for arrays
			Class<?> cls = msgParamWrap.classes[i];
			if (cls.equals(TypeSupport.INT_ARRAY) || cls.equals(TypeSupport.BYTE_ARRAY)
					|| cls.equals(TypeSupport.SHORT_ARRAY) || cls.equals(TypeSupport.LONG_ARRAY)) {
				t = getArrayToMyString(cls, v);
				if (t != null)
					fields[i].setText(t);
			} else
				// this is for all primites
				fields[i].setText(String.valueOf(v));
		}
	}

	public static String getArrayToMyString(Class<?> cls, Object o) {
		String t = null;
		if (cls.equals(TypeSupport.INT_ARRAY))
			t = Arrays.toString((int[]) o);
		else if (cls.equals(TypeSupport.BYTE_ARRAY))
			t = Arrays.toString((byte[]) o);
		else if (cls.equals(TypeSupport.SHORT_ARRAY))
			t = Arrays.toString((short[]) o);
		else if (cls.equals(TypeSupport.LONG_ARRAY))
			t = Arrays.toString((long[]) o);
		return t.substring(1, t.length() - 1);
	}

}
