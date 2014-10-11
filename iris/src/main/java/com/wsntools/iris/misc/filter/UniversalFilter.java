/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.misc.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class UniversalFilter extends FileFilter {

	public final String prefix;
	public final String description;
	
	public UniversalFilter(String pre, String desc) {
		
		prefix = "." + pre;
		description = desc;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.getName().toLowerCase().endsWith(prefix) | (f.isDirectory())) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {		
		return description;
	}

}
