/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.extensions.typeSupport.TypeSupport;
import com.wsntools.iris.tools.ModuleLoader;

import net.tinyos.message.Message;

public class MessageAttributeWrapper implements Iterable<Object[]>, Iterator<Object[]> {

	public Class<? extends Message> cls;

	public Class<?>[] classes;
	public String[] names;
	public AttributeType[] attributeType;
	public Method[] getter;
	public Method[] setter;

	public TypeSupport support = TypeSupport.getBasicTypeSupport();

	private static HashMap<String, MessageAttributeWrapper> allAttributeWrapper = new HashMap<String, MessageAttributeWrapper>();

	public int size;

	/**
	 * stores the limitations of array sizes
	 */
	public HashMap<String, Integer> numElements = new HashMap<String, Integer>(1);

	public MessageAttributeWrapper(Class<? extends Message> cls) {
		this.cls = cls;
		getParameterInfos();
	}

	public Message cloneMessage(Message m) {
		if (!checkMsgType(m))
			return null;
		try {
			Message copy = (Message) cls.newInstance();
			for (int i = 0; i < size; i++)
				setter[i].invoke(copy, getter[i].invoke(m, new Object[0]));
			//			for (String name : names) {
			//				MessageWrapper.set(copy, name, MessageWrapper.get(m, name));
			//			}
			return copy;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkMsgType(Message m) {
		if (!(m.getClass().equals(cls))) {
			System.out.println("wrong message type");
			return false;
		}
		return true;
	}

	public int index(String attribute) {
		for (int i = 0; i < names.length; i++)
			if (names[i].equals(attribute))
				return i;
		System.out.println("Attribut does not exsist in the message type");
		return -1;
	}

	int counter = 0;

	@Override
	public boolean hasNext() {
		return counter < classes.length;
	}

	@Override
	public Object[] next() {
		return new Object[] { classes[counter], names[counter], attributeType[counter++] };
	}

	@Override
	public void remove() {
	}

	@Override
	public Iterator<Object[]> iterator() {
		counter = 0;
		return this;
	}

	public static enum AttributeType {
		field, method, staticF
	}

	/**
	 * 
	 * 
	 * @param cls
	 * @param fields
	 * @param fromSetter
	 * @param statics
	 * @param support
	 * @param infos
	 *            a list with "classes","names" and/or "attributeTypes"
	 * @return
	 */
	public void getParameterInfos() {

		ArrayList<Class<?>> classesList = new ArrayList<Class<?>>();
		ArrayList<Method> setterList = new ArrayList<Method>();
		ArrayList<Method> getterList = new ArrayList<Method>();
		ArrayList<String> namesList = new ArrayList<String>();
		ArrayList<AttributeType> attributeTypeList = new ArrayList<AttributeType>();

		// fields
		Field[] fieldV = cls.getFields();
		for (Field f : fieldV)
			if (support.supports(f.getClass())) {
				classesList.add(f.getClass());
				namesList.add(f.getName());
				attributeTypeList.add(AttributeType.field);
			}

		// setter/getter
		Method[] methods = cls.getMethods();
		
		// manual_sort
		for (int a = 0; a < methods.length; a++){
			for (int b = a; b < methods.length; b++){
				if (0 < methods[a].getName().compareTo(methods[b].getName())){
					Method temp = methods[a];
					methods[a] = methods[b];
					methods[b] = temp;
				}
			}
		}
		
		
		// save all variables that have getter
		HashMap<String, Method> getterMap = new HashMap<String, Method>();
		for (Method m : methods)
			if (m.getName().startsWith("get_"))
				getterMap.put(m.getName().substring(4), m);

		for (Method m : methods) {
			// keep only setter and setter, where a getter also exsists
			if (!m.getName().startsWith("set_") || !getterMap.containsKey(m.getName().substring(4)))
				continue;
			if (support.supports(m.getParameterTypes()[0])) {
				classesList.add(m.getParameterTypes()[0]);
				setterList.add(m);
				getterList.add(getterMap.get(m.getName().substring(4)));
				namesList.add(m.getName().substring(4));
				attributeTypeList.add(AttributeType.method);
			}
		}
		// statics 
		fieldV = cls.getDeclaredFields();
		for (Field f : fieldV)
			if (support.supports(f.getClass())) {
				classesList.add(f.getClass());
				namesList.add(f.getName());
				attributeTypeList.add(AttributeType.staticF);
			}

		int i = 0;
		for (String n : namesList) {
			if (classesList.get(i++).isArray())
				checkArrayLimit(n);
		}

		classes = new Class<?>[classesList.size()];
		classes = classesList.toArray(classes);
		size = classes.length;
		getter = new Method[size];
		getter = getterList.toArray(getter);
		setter = new Method[size];
		setter = setterList.toArray(setter);
		names = new String[size];
		names = namesList.toArray(names);
		attributeType = new AttributeType[size];
		attributeType = attributeTypeList.toArray(attributeType);
	}

	/**
	 * CALL THIS BEFORE CREATING A MSGATTRIBUTEWRAPPER
	 * 
	 * @param cls
	 * @return
	 */
	public static MessageAttributeWrapper getMsgAttributeWrapper(Class<? extends Message> cls) {
		if (!allAttributeWrapper.containsKey(cls.getSimpleName()))
			allAttributeWrapper.put(cls.getSimpleName(), new MessageAttributeWrapper(cls));
		return allAttributeWrapper.get(cls.getSimpleName());
	}

	/**
	 * checks for a given field name, if there is a "numElements_<name>" method
	 * 
	 * @param name
	 *            the field name
	 */
	public void checkArrayLimit(String name) {
		try {
			Method m = cls.getMethod("numElements_" + name, new Class[0]);
			try {
				if (m.getParameterTypes().length == 0) {
					int i = (Integer) m.invoke(null, new Object[0]);
					numElements.put(name, i);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException exc) {
			// this is ok. the method is just not there. no limitation
			return;
		}
	}

	/**
	 * loads and return the Attribute wrapper for a given class name
	 * 
	 * @param msgType
	 * @return
	 */
	public static MessageAttributeWrapper getMsgAttributeWrapper(String msgType) {
		if (allAttributeWrapper.containsKey(msgType))
			return allAttributeWrapper.get(msgType);
		String fs = Constants.getDirMoteMessagesSending() + msgType;
		fs = fs.replace(System.getProperty("file.separator"), ".");
		Class<? extends Message> msgClass = ModuleLoader.loadClass(fs);
		if (msgClass == null)
			return null;
		return getMsgAttributeWrapper(msgClass);
	}
}
