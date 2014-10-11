/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions.typeSupport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import mote.exampleHeader.Test_ArrayMsg;


import com.wsntools.iris.data.Constants;
import com.wsntools.iris.extensions.MessageAttributeWrapper;

public class SimpleWrapperCreator {

	static File create(MessageAttributeWrapper maw) {
		String className = "SW_" + maw.cls.getSimpleName();
		File file = new File(Constants.getDirMoteMessagesSimplewrapper() + Constants.getSep() + className + ".java");
		try {
			FileWriter fw = new FileWriter(file);
			fw.append("package mote.simpleWrapper;");
			fw.append("public class " + className + "{");
			for (int i = 0; i < maw.names.length; i++) {
				fw.append(maw.classes[i].getName() + " " + maw.names[i] + ";");
			}
			fw.append("}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	public static void main(String[] args) {
//		MessageAttributeWrapper maw = new MessageAttributeWrapper(Test_ArrayMsg.class);
//		System.out.println(create(maw).getAbsolutePath());
	}
}
