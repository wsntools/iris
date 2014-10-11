/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.util;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import net.tinyos.message.Message;

public class ClassLoader {
	
	public static Class<?>[] loadClassesByPackage(String package_path) {
		String package_path_replaced = package_path.replace(".", "/");
		String maven_dir = "target/classes/";
		File f = new File(maven_dir+package_path_replaced);
		File[] class_files = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});
//		System.out.println(Arrays.toString(class_files));
		java.lang.ClassLoader xx = ClassLoader.class.getClassLoader();
		try {
			Class<?>[] classes = new Class<?>[class_files.length];
			for (int i = 0; i < class_files.length; i++) {
				classes[i] = xx.loadClass(package_path+"."+class_files[i].getName().substring(0, class_files[i].getName().length()-6));
			}
			
			return classes;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public static Class<?>[] loadClassesByFolder(String path) {
		File f = new File(path);
		File[] class_files = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});

		Class<?>[] message_types = new Class[class_files.length];
		int i = 0;
		for (File file : class_files) {

			try {
				URL url = f.toURL();
				URL[] urls = new URL[] { url };
				URLClassLoader cl = new URLClassLoader(urls);
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				Class<?> cls = cl.loadClass(className);
				message_types[i++] = cls;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return message_types;
	}
}
