/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import jogamp.common.Debug;

import org.apache.log4j.Logger;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.main.RadioMonitoringTool;

/**
 * @author Sascha Jungen
 * 
 */

public class ModuleLoader {

	private <E> E createContents(Class<E> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static IRIS_Attribute[] getAttributeList() {
		System.out.println("----------\nAttributes:\n----------");
		Object[] arr = loadIFClasses(Constants.getDirModulesAttributes(),
				Constants.getJarModulesAttributes(),
				"com.wsntools.iris.interfaces.RMT_Attribute");
		IRIS_Attribute[] res = new IRIS_Attribute[arr.length];
		for (int i = 0; i < arr.length; i++) {
			res[i] = (IRIS_Attribute) arr[i];

		}
		return res;

	}

	// private static void debug(String jarPath) {
	// String pckg = jarPath.replace(Constants.getJarSep(), ".");
	//
	// ArrayList<Object> classlist = new ArrayList<Object>();
	// ArrayList<String> listFiles = getClassList(jarPath, true);
	//
	// URL urlpath = pathToFile(jarPath, true);
	// ClassLoader loader = ClassLoader.getSystemClassLoader();
	// // new URLClassLoader(new URL[] { urlpath });
	//
	// for (int i = 0; i < listFiles.size(); i++) {
	// try {
	//
	// System.out.println(pckg);
	//
	// // Class cls = loader.loadClass(pckg+listFiles.get(i));
	// Class cls = Class.forName(pckg+listFiles.get(i)) ;
	//
	// System.out.println(listFiles.get(i));
	//
	// System.out.println(cls.newInstance() instanceof IRIS_FunctionModule);
	//
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InstantiationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	public static IRIS_FunctionModule[] getFunctionList() {

		System.out.println("----------\nFunctions:\n----------");

		// debug(Constants.getDirModulesFunctions());

		Object[] arr = loadIFClasses(Constants.getDirModulesFunctions(),
				Constants.getJarModulesFunctions(),
				"com.wsntools.iris.interfaces.IRIS_FunctionModule");

		IRIS_FunctionModule[] res = new IRIS_FunctionModule[arr.length];
		for (int i = 0; i < arr.length; i++) {
			// System.out.println(arr[i]);
			// System.out.println(arr[i] instanceof IRIS_FunctionModule);
			// System.out.println(IRIS_FunctionModule.class);
			// System.out.println(arr[i].getClass());
			Class[] classes = arr[i].getClass().getClasses();
			// System.out.println("number of classes: " + classes.length);
			for (Class classInst : classes)
				System.out.println("HIER:" + classInst);
			res[i] = (IRIS_FunctionModule) arr[i];
		}
		return res;
	}

	public static IRIS_ModuleInfo[] getMeasureInfoList() {
		System.out.println("----------\nMeasurement Information:\n----------");
		Object[] arr = loadIFClasses(Constants.getDirModulesInfo(),
				Constants.getJarModulesInfo(),
				"com.wsntools.iris.interfaces.IRIS_ModuleInfo");
		IRIS_ModuleInfo[] res = new IRIS_ModuleInfo[arr.length];
		for (int i = 0; i < arr.length; i++) {
			res[i] = (IRIS_ModuleInfo) arr[i];
		}
		return res;
	}

	public static IRIS_GUIModule[] getGUIModuleList(Model m) {
		System.out.println("----------\nGUI Modules:\n----------");
		Object[] arr = loadEXTClasses(Constants.getDirModulesGUI(),
				Constants.getJarModulesGUI(),
				"com.wsntools.iris.interfaces.IRIS_GUIModule");
		IRIS_GUIModule[] res = new IRIS_GUIModule[arr.length];
		for (int i = 0; i < arr.length; i++) {
			try {
				System.out.println("CURREN: " + arr[i]);

				// System.out.println(arr[i].getClass().getConstructor(
				// Class.forName(m.getClass().getCanonicalName())));
				// System.out.println(arr[i].getClass().getConstructor(
				// Model.class));
				// System.out.println(arr[i].getClass().getConstructor(
				// m.getClass()));

				System.out.println(((Class) arr[i]).getConstructor(Class
						.forName(m.getClass().getCanonicalName())));
				System.out
						.println(((Class) arr[i]).getConstructor(Model.class));
				System.out
						.println(((Class) arr[i]).getConstructor(m.getClass()));

				// res[i] = (IRIS_GUIModule) (((Class) arr[i])
				// .getConstructor(Model.class).newInstance(m));
				//
				res[i] = (IRIS_GUIModule) (((Class) arr[i])
						.getConstructor(m.getClass()).newInstance(m));

			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				// e.printStackTrace();
				System.err.println("Error during class loading");
				e.printStackTrace();
				res[i] = null;
			}
		}
		return res;
	}

	public static Class[] getRadioLinkMessageClassesForSending() {

		System.out
				.println("----------------\nTinyOs Messages (Sending):\n----------------");
		Object[] arr = loadEXTClasses(Constants.getDirMoteMessagesSending(),
				Constants.getJarMoteMessagesSending(),
				"net.tinyos.message.Message");
		Class[] res = new Class[arr.length];
		for (int i = 0; i < arr.length; i++) {
			res[i] = (Class) arr[i];
		}
		return res;
	}

	public static Class[] getRadioLinkMessageClassesForReceiving() {

		System.out
				.println("----------------\nTinyOs Messages (Receiving):\n----------------");
		Object[] arr = loadEXTClasses(Constants.getDirMoteMessagesReceiving(),
				Constants.getJarMoteMessagesReceiving(),
				"net.tinyos.message.Message");
		Class[] res = new Class[arr.length];
		for (int i = 0; i < arr.length; i++) {
			res[i] = (Class) arr[i];
		}
		return res;
	}

	public static boolean insideJar() {
		boolean result = ModuleLoader.class.getResource("ModuleLoader.class")
				.toString().startsWith("jar");
		// System.out.println(result);
		return result;
	}

	public static String getSourcePath() {
		String result = "";

		CodeSource codeSource = ModuleLoader.class.getProtectionDomain()
				.getCodeSource();
		File jarFile;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
			File jarDir = null;
			if (insideJar()) {
				jarDir = jarFile.getParentFile();
			} else {
				jarDir = jarFile;
			}

			if (jarDir != null && jarDir.isDirectory()) {
				result = jarDir.getAbsolutePath();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static URL pathToFile(String path, boolean considerJar) {

		URL result = null;
		try {
			if (considerJar && insideJar()) {

				// TODO make it nicer!
				result = new URL(ModuleLoader.class
						.getResource("ModuleLoader.class").toString()
						.split("!/")[0]
						+ "!/");
			} else {
				result = new File(getSourcePath() + Constants.getSep()).toURI()
						.toURL();
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Find all classes in the given folder
	 * 
	 * @param path
	 *            where to search
	 * @return list of all .class files within the given folder
	 */
	private static ArrayList<String> getClassList(String path,
			boolean considerJar) {

		ArrayList<String> listFiles = new ArrayList<String>();

		if (considerJar && insideJar()) {
			CodeSource src = ModuleLoader.class.getProtectionDomain()
					.getCodeSource();

			if (src != null) {
				URL jar = src.getLocation();
				JarInputStream zip;

				try {
					zip = new JarInputStream(jar.openStream());

					JarEntry ze = null;

					while ((ze = zip.getNextJarEntry()) != null) {
						// System.out.println(ze.getClass().getSimpleName());ze.getClass().getInterfaces()

						if (ze.getName().startsWith(path)
								&& ze.getName().endsWith(".class")) {
							String temp = ze.getName().split("/")[ze.getName()
									.split("/").length - 1];
							listFiles.add(temp.split(".class")[0]);
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			// File folderToFiles = new File(getSourcePath() +
			// Constants.getSep() + path + Constants.getSep());
			File folderToFiles = new File(getSourcePath() + Constants.getSep()
					+ path);

			if (folderToFiles.exists()) {
				String[] files = folderToFiles.list();
				for (String s : files) {
					if (s.toLowerCase().endsWith(".class")) {
						listFiles.add(s.substring(0, s.length() - 6));
					}
				}
			}

		}
		return listFiles;
	}

	/**
	 * Loads all classes located in 'sysPath', implementing 'ifname'
	 * 
	 * @param sysPath
	 * @param jarPath
	 * @param ifname
	 * @return
	 */
	private static Object[] loadIFClasses(String sysPath, String jarPath,
			String ifname) {
		String pckg = jarPath.replace(Constants.getJarSep(), ".");
		try {
			ArrayList<Object> classlist = new ArrayList<Object>();
			ArrayList<String> listFiles = getClassList(jarPath, true);

			URL urlpath = pathToFile(jarPath, true);
			URLClassLoader loader = new URLClassLoader(new URL[] { urlpath });

			for (int i = 0; i < listFiles.size(); i++) {
				Class cls = Class.forName(pckg + listFiles.get(i));

				// Class cls = loader.loadClass(pckg + listFiles.get(i));

				// Check if class has interface implemented
				for (int k = 0; k < cls.getInterfaces().length; k++) {

					if (cls.getInterfaces()[k].getName().equals(ifname)) {
						classlist.add(cls.newInstance());
						System.out.println("Loaded: " + cls.getSimpleName());
						break;
					}
				}
			}

			if (insideJar()) {
				System.out.println("----");
				System.out.println("Loading external classes in:");
				System.out.println(getSourcePath() + Constants.getSep()
						+ sysPath);

				listFiles = getClassList(sysPath, false);
				urlpath = pathToFile(sysPath, false);
				loader = new URLClassLoader(new URL[] { urlpath });

				for (int i = 0; i < listFiles.size(); i++) {
					Class cls = loader.loadClass(pckg + listFiles.get(i));
					System.out.println(cls.getSimpleName());

					// Check if class has interface implemented
					for (int k = 0; k < cls.getInterfaces().length; k++) {
						if (cls.getInterfaces()[k].getName().equals(ifname)) {
							classlist.add(cls.newInstance());
							System.out
									.println("Loaded: " + cls.getSimpleName());
							break;
						}
					}
				}
				System.out.println();
			}

			loader.close();
			return classlist.toArray();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Class Loading Error!");
		}

		return null;
	}

	/**
	 * Loads all classes located in 'sysPath', extending 'extname'
	 * 
	 * @param sysPath
	 * @param jarPath
	 * @param extname
	 * @return
	 */
	private static Object[] loadEXTClasses(String sysPath, String jarPath,
			String extname) {
		String pckg = jarPath.replace(Constants.getJarSep(), ".");
		try {
			ArrayList<Object> classlist = new ArrayList<Object>();
			ArrayList<String> listFiles = getClassList(jarPath, true);
			URL urlpath = pathToFile(jarPath, true);
			URLClassLoader loader = new URLClassLoader(new URL[] { urlpath });

			for (int i = 0; i < listFiles.size(); i++) {
				// Class cls = loader.loadClass(pckg + listFiles.get(i));
				Class cls = Class.forName(pckg + listFiles.get(i));
				if (cls.getSuperclass().getName().equals(extname)) {
					classlist.add(cls);
					System.out.println("Loaded: " + cls.getSimpleName());
					// break;
				}
			}

			if (insideJar()) {

				System.out.println("Loading external classes in:");
				System.out.println(getSourcePath() + Constants.getSep()
						+ sysPath);

				listFiles = getClassList(sysPath, false);
				urlpath = pathToFile(sysPath, false);
				loader = new URLClassLoader(new URL[] { urlpath });

				for (int i = 0; i < listFiles.size(); i++) {
					// Class cls = loader.loadClass(pckg + listFiles.get(i));
					Class cls = Class.forName(pckg + listFiles.get(i));
					System.out.print("Try ");
					System.out.println(cls.getSimpleName());
					if (cls.getSuperclass().getName().equals(extname)) {
						classlist.add(cls);
						System.out.println("Loaded: " + cls.getSimpleName());
						// break;
					}
				}

			}

			loader.close();
			return classlist.toArray();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Class Loading Error!");
		}

		return null;
	}

	public static Class[] loadClasses(File path) {

		try {
			ArrayList<Class> classlist = new ArrayList<Class>();

			// Find all classes in the given folder
			ArrayList<String> listFiles = new ArrayList<String>();

			String[] files = new File(Class.class.getResource(
					Constants.getSep() + path + Constants.getSep()).getPath())
					.list();

			// String[] files = path.list();

			// System.out.println(Arrays.toString(files));
			for (String s : files) {
				if (s.toLowerCase().endsWith(".class")) {
					listFiles.add(s.substring(0, s.length() - 6));
				}
			}
			// Change replacement depending on OS
			String pckg = path.getPath().replace(Constants.getSep(), ".") + ".";

			// Classloader to access and instantiate classes
			for (int i = 0; i < listFiles.size(); i++) {
				// System.out.println("ModuleLoader.loadClasses:: " +
				// loadClass(pckg + listFiles.get(i)));
				classlist.add(loadClass(pckg + listFiles.get(i)));
				// System.out.println("Loaded: " + cls.getSimpleName());
			}
			Class<?>[] classes = new Class[classlist.size()];
			return classlist.toArray(classes);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Class Loading Error!");
		}

		return null;
	}

	/**
	 * Loads one message type from the mote package
	 * 
	 * @param file
	 *            the name of the
	 * @return
	 */
	public static Class loadClass(String file) {
		String path = Constants.getDirMoteMessagesSending();
		URL urlpath;
		try {
			System.out.println(file);
			urlpath = new File(path).toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[] { urlpath });
			System.out.println(file);
			return loader.loadClass(file);
		} catch (ClassNotFoundException e) {
			System.out.println("class does not exsist");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void exploreAvailableJarExtensions() {

	}

}
