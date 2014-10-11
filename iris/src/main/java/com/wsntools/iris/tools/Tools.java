/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.wsntools.iris.data.Constants;

public class Tools {

	/**
	 * Computes the difference between two lists of the same type
	 * @param listA
	 * @param listB
	 * @return listA (diff) listB
	 */	
	public static <T> List<T> listDiff(List<T> listA, List<T> listB) {	
		ArrayList<T> listDiff = new ArrayList<T>();
		for(T next: listA) {
			if(!listDiff.contains(next) && !listB.contains(next))
				listDiff.add(next);
		}
		return listDiff;
	}
	
	/**
	 * Computes the intersection of two lists of the same type
	 * @param listA
	 * @param listB
	 * @return listA (intersect) listB
	 */	
	public static <T> List<T> listIntersect(List<T> listA, List<T> listB) {	
		ArrayList<T> listIntersect = new ArrayList<T>();
		for(T next: listA) {
			if(!listIntersect.contains(next) && listB.contains(next))
				listIntersect.add(next);
		}
		return listIntersect;
	}
	
	/**
	 * Evaluates whether the given string is valid in terms of the given restrictions
	 * @param input
	 * @return The String (trimmed) if the input is valid - null if it is invalid
	 */
	public static String checkStringForValidInput(String input) {
		input = input.trim();
		if (input.matches(Constants.getNamingAllowedChars()))
			return input;
		else
			return null;
	}
}
