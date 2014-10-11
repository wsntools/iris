/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class IrisPropertyReader {

	private static final Properties properties = new Properties();
	private static IrisPropertyReader instance = null;

	private IrisPropertyReader() {
		BufferedInputStream stream;
		try {
			// opens a stream to read the property file (file is in the jar)
			stream =  new BufferedInputStream(Class.class.getResourceAsStream(Constants.getJarSep() + "config"
					+ Constants.getJarSep() + "imac.properties"));
			
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized IrisPropertyReader getInstance(){
		if (null == instance){
			instance = new IrisPropertyReader();
		}
		return instance;
	}

	public boolean getBooleanValue(String property) {
		String result = properties.getProperty(property, "true");
		return Boolean.parseBoolean(result);
	}

}
