/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.util;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tinyos.message.Message;

public class MethodWrapper {
	private Method[] getter;
	private Method[] setter;
	private Class<?>[] types;
	private Method[] isArray;
	private Method[] arrayLength; // only arrays
	private String[] name;
	private Class<?> message;
	private Method dataLength;
	private Method[] elementSize; // only arrays
	private Method[] getElement; // only arrays
	private Method[] getOffset;

	public MethodWrapper(Method[] getter, Method[] setter, Class<?>[] types, Method[] isArray,
			Method[] arrayLength, String[] name, Class<?> message,
			Method dataLength, Method[] elementSize, Method[] getElement,
			Method[] getOffset) {
		this.getter = getter;
		this.setter = setter;
		this.types = types;
		this.isArray = isArray;
		this.arrayLength = arrayLength;
		this.name = name;
		this.message = message;
		this.dataLength = dataLength;
		this.elementSize = elementSize;
		this.getElement = getElement;
		this.getOffset = getOffset;
	}

	public static MethodWrapper generateMethodWrapper(
			Class<?> class1) {

		ArrayList<Method> m_getter = new ArrayList<Method>();
		ArrayList<String> m_name = new ArrayList<String>();
		ArrayList<Method> m_setter = new ArrayList<Method>();
		ArrayList<Class<?>> t_type = new ArrayList<Class<?>>();
		Method m_dataLength = null;

		// go through every method of the current message class
		Method[] meth = class1.getDeclaredMethods();
		for (Method m : meth) {
			if (m.getParameterTypes().length == 0) {
				if (m.getName().startsWith("get_")) {
					m_getter.add(m);
					m_name.add(m.getName().replaceFirst("get_", "")
							.replaceAll("()", ""));
				}

			} else {
				if (m.getName().startsWith("set_")) {
					if (m.getParameterTypes().length == 1) {
						m_setter.add(m);
						t_type.add(m.getParameterTypes()[0]);
					} else if (m.getParameterAnnotations().length == 2) {
						m_setter.add(m);
						t_type.add(m.getParameterTypes()[1]);
					}
				}
			}
		}
		try {
			m_dataLength = class1.getSuperclass().getMethod("dataLength",
					null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Method m_getter_array[] = m_getter.toArray(new Method[m_getter.size()]);
		String m_name_array[] = m_name.toArray(new String[m_name.size()]);
		Class<?> t_type_array[] = t_type.toArray(new Class[t_type.size()]);
		Method m_setter_array[] = m_setter.toArray(new Method[m_setter.size()]);
		Method m_isArray_array[] = m_getter
				.toArray(new Method[m_getter.size()]);
		Method m_arrayLength_array[] = m_getter.toArray(new Method[m_getter
				.size()]);
		Method m_elementSize_array[] = m_getter.toArray(new Method[m_getter
				.size()]);
		Method m_getElement_array[] = m_getter.toArray(new Method[m_getter
				.size()]);
		Method m_getOffset_array[] = m_getter.toArray(new Method[m_getter
				.size()]);

		for (int a = 0; a < m_name_array.length; a++) {
			m_isArray_array[a] = getMethod("isArray_" + m_name_array[a],
					class1);
			m_arrayLength_array[a] = getMethod(
					"numElements_" + m_name_array[a], class1);
			m_elementSize_array[a] = getMethod(
					"elementSize_" + m_name_array[a], class1);

			try {
				m_getElement_array[a] = class1.getMethod("getElement_"
						+ m_name_array[a], int.class);
			} catch (SecurityException e1) {
				m_getElement_array[a] = null;
			} catch (NoSuchMethodException e1) {
				m_getElement_array[a] = null;
			}
			try {
				m_getOffset_array[a] = class1.getMethod("offset_"
						+ m_name_array[a], int.class);
			} catch (SecurityException e) {
				m_getOffset_array[a] = null;
			} catch (NoSuchMethodException e) {
				m_getOffset_array[a] = null;
			}
		}

		// private MethodWrapper(Method[] getter, Method[] isArray,
		// Method[] arrayLength, String[] name, Class<Message> message,
		// BackgroundWriter backup, Method dataLength,
		// Method[] elementSize, Method[] getElement, Method[] getOffset) {
		//

		MethodWrapper methodWrapper = new MethodWrapper(m_getter_array,
				m_setter_array,t_type_array, m_isArray_array, m_arrayLength_array,
				m_name_array, class1, m_dataLength, m_elementSize_array,
				m_getElement_array, m_getOffset_array);

		return methodWrapper;
	}

	public static MethodWrapper[] generateMethodWrappers(Class<?>[] message_types) {
		MethodWrapper[] methodWrapper = new MethodWrapper[message_types.length];
		for (int i = 0; i < message_types.length; i++) {
			methodWrapper[i] = generateMethodWrapper(message_types[i]);
		}
		return methodWrapper;
	}

	public static Method getMethod(String name, Class<?> message) {
		Method result = null;
		try {
			result = message.getMethod(name, null);
		} catch (SecurityException e) {
			result = null;
		} catch (NoSuchMethodException e) {
			result = null;
		}
		return result;
	}

	public Method[] getGetter() {
		return getter;
	}

	public void setGetter(Method[] getter) {
		this.getter = getter;
	}

	public Method[] getSetter() {
		return setter;
	}

	public void setSetter(Method[] setter) {
		this.setter = setter;
	}

	public Class<?>[] getTypes() {
		return types;
	}

	public void setTypes(Class<?>[] types) {
		this.types = types;
	}

	public Method[] getIsArray() {
		return isArray;
	}

	public void setIsArray(Method[] isArray) {
		this.isArray = isArray;
	}

	public Method[] getArrayLength() {
		return arrayLength;
	}

	public void setArrayLength(Method[] arrayLength) {
		this.arrayLength = arrayLength;
	}

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public Class<?> getMessage() {
		return message;
	}

	public void setMessage(Class<Message> message) {
		this.message = message;
	}

	public Method getDataLength() {
		return dataLength;
	}

	public void setDataLength(Method dataLength) {
		this.dataLength = dataLength;
	}

	public Method[] getElementSize() {
		return elementSize;
	}

	public void setElementSize(Method[] elementSize) {
		this.elementSize = elementSize;
	}

	public Method[] getGetElement() {
		return getElement;
	}

	public void setGetElement(Method[] getElement) {
		this.getElement = getElement;
	}

	public Method[] getGetOffset() {
		return getOffset;
	}

	public void setGetOffset(Method[] getOffset) {
		this.getOffset = getOffset;
	}
}