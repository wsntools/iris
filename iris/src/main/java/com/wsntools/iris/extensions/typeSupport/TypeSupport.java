/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions.typeSupport;

import java.util.Comparator;
import java.util.TreeSet;

public class TypeSupport {

	public static final Class<?> INT_ARRAY = new int[0].getClass();
	public static final Class<?> BYTE_ARRAY = new byte[0].getClass();
	public static final Class<?> SHORT_ARRAY = new short[0].getClass();
	public static final Class<?> LONG_ARRAY = new long[0].getClass();

	TreeSet<Class<?>> supportedTypes = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

		@Override
		public int compare(Class<?> o1, Class<?> o2) {
			return o1.getSimpleName().compareTo(o2.getSimpleName());
		}
	});

	public TypeSupport() {
	}

	public TypeSupport(Class<?>[] classes) {
		for (Class<?> c : classes)
			supportedTypes.add(c);
	}

	public boolean supports(Class<?> c) {
		return supportedTypes.contains(c);
	}

	static TypeSupport basicTypeSupport;

	public static TypeSupport getBasicTypeSupport() {

		if (basicTypeSupport == null)
			basicTypeSupport = new TypeSupport(new Class<?>[] { Integer.TYPE, Float.TYPE, Boolean.TYPE, Short.TYPE,
					Long.TYPE, Byte.TYPE, INT_ARRAY, BYTE_ARRAY, SHORT_ARRAY, LONG_ARRAY });
		return basicTypeSupport;
	}

	public static void main(String[] args) {
		System.out.println(new char[0].getClass());
		//		System.out.println(Arrays.);
	}
}
