/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.util;

public class ArrayCopy {
	public static Object arraycopy2D(Object[][] source, Object[][] destination) throws IllegalArgumentException{
		if (source.length>destination.length) {
			throw new IllegalArgumentException("The destination array must have at least the same size as the source array.");
		}
		for (int i = 0; i < source.length; i++) {
			if (source[i].length>destination[i].length){
				throw new IllegalArgumentException("The destination array must have at least the same size as the source array.");
			}
			for (int j = 0; j < source[i].length; j++) {
				destination[i][j] = source[i][j];
			}
		}
		return destination;
	}
}
