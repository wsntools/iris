/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */package com.wsntools.iris.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.dialogues.DiaFunctionSettings;
import com.wsntools.iris.dialogues.DiaSettings;
import com.wsntools.iris.extensions.MessageWrapper;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.misc.exceptions.AttributeNotExistantException;
import com.wsntools.iris.misc.exceptions.IncompleteInformationException;
import com.wsntools.iris.misc.filter.UniversalFilter;
import com.wsntools.iris.modules.attributes.AttrSenderID;

/**
 * @author Sascha Jungen 
 * 
 */
public class SaveAndLoad {

	private static final String NODETYPE = "Crossbow Telos Rev.B";

	public static void saveToXML(Model m) {

		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesMeasure()));
		String filename = (m.getCurrentMeasurement().getMeasureName() + ".wiseml")
				.replaceAll(" ", "_");
		fc.setSelectedFile(new File(filename));
		fc.setFileFilter(new UniversalFilter("wiseml", "*.wiseml - WISEML File"));
		if ((fc.showSaveDialog(m.getView()) == 0)
				&& (fc.getSelectedFile() != null)) {
			f = fc.getSelectedFile();
			if (!f.getName().endsWith(
					((UniversalFilter) fc.getFileFilter()).prefix)) {
				f = new File(f + ((UniversalFilter) fc.getFileFilter()).prefix);
			}
		} else
			return;

		Measurement meas = m.getCurrentMeasurement();
		int gap = 0;
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(f));
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.newLine();
			out.write("<wiseml version=\"1.0\" xmlns=\"http://wisebed.eu/ns/wiseml/1.0\">");
			out.newLine();
			gap++;

			// Setups
			makeGap(gap, out);
			out.write("<setup>");
			out.newLine();
			gap++;

			makeGap(gap, out);
			out.write("<origin>");
			out.newLine();
			gap++;
			makeGap(gap, out);
			out.write("<x>0</x>");
			out.newLine();
			makeGap(gap, out);
			out.write("<y>0</y>");
			out.newLine();
			gap--;
			makeGap(gap, out);
			out.write("</origin>");
			out.newLine();

			makeGap(gap, out);
			out.write("<timeinfo>");
			out.newLine();
			gap++;
			makeGap(gap, out);
			out.write("<start>"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZZ").format(
							new Date()).replace(' ', 'T') + "</start>");
			out.newLine();
			makeGap(gap, out);
			out.write("<duration>" + meas.getNumberOfPackets() + "</duration>");
			out.newLine();
			makeGap(gap, out);
			out.write("<unit>packets</unit>");
			out.newLine();
			gap--;
			makeGap(gap, out);
			out.write("</timeinfo>");
			out.newLine();

			makeGap(gap, out);
			out.write("<description>" + "RMT - " + meas.getMeasureName()
					+ "</description>");
			out.newLine();

			makeGap(gap, out);
			out.write("<defaults>");
			out.newLine();
			gap++;
			makeGap(gap, out);
			out.write("<node>");
			out.newLine();
			gap++;
			makeGap(gap, out);
			out.write("<position>");
			out.newLine();
			gap++;
			makeGap(gap, out);
			out.write("<x>0</x>");
			out.newLine();
			makeGap(gap, out);
			out.write("<y>0</y>");
			out.newLine();
			gap--;
			makeGap(gap, out);
			out.write("</position>");
			out.newLine();
			makeGap(gap, out);
			out.write("<nodeType>" + NODETYPE + "</nodeType>");
			out.newLine();
			// For all available entries list default values
			ArrayList<String> keys = new ArrayList<String>();
			String[] values;
			for (int i = 0; i < meas.getNumberOfPackets(); i++) {
				values = meas.getPacketAt(i).getAllValues();
				for (int j = 0; j < values.length; j++) {
					if (!keys.contains(values[j])) {
						keys.add(values[j]);
					}
				}
			}
			for (int i = 0; i < keys.size(); i++) {
				makeGap(gap, out);
				out.write("<capability>");
				out.newLine();
				gap++;
				makeGap(gap, out);
				out.write("<name>" + keys.get(i) + "</name>");
				out.newLine();
				makeGap(gap, out);
				out.write("<datatype>float</datatype>");
				out.newLine();
				makeGap(gap, out);
				out.write("<unit></unit>");
				out.newLine();
				makeGap(gap, out);
				out.write("<default>0.0</default>");
				out.newLine();
				gap--;
				makeGap(gap, out);
				out.write("</capability>");
				out.newLine();

			}

			gap--;
			makeGap(gap, out);
			out.write("</node>");
			out.newLine();
			gap--;
			makeGap(gap, out);
			out.write("</defaults>");
			out.newLine();
			// List nodeID's in ascending order
			ArrayList<Integer> nodeIDs = new ArrayList<Integer>();
			int id;
			for (int i = 0; i < meas.getNumberOfPackets(); i++) {
				id = (int) meas.getPacketAt(i).getValue(new AttrSenderID());
				if (!nodeIDs.contains(id)) {
					nodeIDs.add(id);
				}
			}
			for (int i = 0; i < nodeIDs.size(); i++) {
				for (int j = i + 1; j < nodeIDs.size(); j++) {

					if (nodeIDs.get(i) > nodeIDs.get(j)) {
						id = nodeIDs.get(i);
						nodeIDs.set(i, nodeIDs.get(j));
						nodeIDs.set(j, id);
					}
				}
			}
			for (int i = 0; i < nodeIDs.size(); i++) {

				makeGap(gap, out);
				out.write("<node id=\"urn:wisebed:node:uzl:" + nodeIDs.get(i)
						+ "\"/>");
				out.newLine();
			}
			gap--;
			makeGap(gap, out);
			out.write("</setup>");
			out.newLine();
			out.flush();

			// Values of measurement
			makeGap(gap, out);
			out.write("<trace id=\"0\">");
			out.newLine();
			gap++;
			for (int i = 0; i < meas.getNumberOfPackets(); i++) {
				makeGap(gap, out);
				out.write("<timestamp>" + i + "</timestamp>");
				out.newLine();
				makeGap(gap, out);
				out.write("<node id=\"urn:wisebed:node:uzl:"
						+ (int) meas.getPacketAt(i)
								.getValue(new AttrSenderID()) + "\">");
				out.newLine();
				gap++;
				makeGap(gap, out);
				out.write("<position>");
				out.newLine();
				gap++;
				makeGap(gap, out);
				out.write("<x>0</x>");
				out.newLine();
				makeGap(gap, out);
				out.write("<y>0</y>");
				out.newLine();
				gap--;
				makeGap(gap, out);
				out.write("</position>");
				out.newLine();
				// Read out all values in a packet
				values = meas.getPacketAt(i).getAllValues();
				for (int j = 0; j < values.length; j++) {
					makeGap(gap, out);
					out.write("<data key=\"" + values[j] + "\">"
							+ meas.getPacketAt(i).getValue(values[j])
							+ "</data>");
					out.newLine();
				}
				gap--;
				makeGap(gap, out);
				out.write("</node>");
				out.newLine();
			}
			gap--;
			makeGap(gap, out);
			out.write("</trace>");
			out.newLine();
			gap--;
			makeGap(gap, out);
			out.write("</wiseml>");
			out.newLine();

			out.flush();
			System.out.println("Trace successfully created");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Cannot create file");
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}

	}

	public static void loadFromXML(Model m) {

		File[] f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesMeasure()));
		fc.setFileFilter(new UniversalFilter("wiseml", "*.wiseml - WISEML File"));
		fc.setMultiSelectionEnabled(true);
		if ((fc.showOpenDialog(m.getView()) == 0)
				&& (fc.getSelectedFiles() != null)) {

			f = fc.getSelectedFiles();
		} else
			return;

		Measurement[] res = new Measurement[fc.getSelectedFiles().length];
		String measurename;

		ArrayList<String> attrnames = new ArrayList<String>();
		ArrayList<Float> attrvalues = new ArrayList<Float>();
		ArrayList<Packet> arrPackets = new ArrayList<Packet>();
		String[] arrnames;
		Float[] arrvalues;

		for (int i = 0; i < fc.getSelectedFiles().length; i++) {

			res[i] = new Measurement(m.getNextMeasurementNumber() + i,
					"Measurement " + (m.getNextMeasurementNumber() + i));
			attrnames.clear();
			attrvalues.clear();
			arrPackets.clear();

			BufferedReader in = null;
			try {
				String line = null;

				// Open Reader and scan the document for relevant values
				in = new BufferedReader(new FileReader(f[i]));
				line = in.readLine();
				line = line.trim();

				// Check for Wiseml-Tag
				while (!line.startsWith("<wiseml")) {
					line = in.readLine();
					if (line == null) {
						in.close();
						throw new IncompleteInformationException();
					} else {
						line = line.trim();
					}
				}

				while (!line.equals("</wiseml>")) {
					// Get Measurementname
					if (line.startsWith("<description>")) {
						measurename = line.replaceFirst("<description>", "")
								.replaceFirst("</description>", "");
						res[i] = new Measurement(m.getNextMeasurementNumber()
								+ i, measurename);
					}
					// Read out measuredata
					if (line.startsWith("<trace id=\"")) {
						while (!line.startsWith("</trace>")) {

							if (line.startsWith("<node id=\"")) {
								attrnames.clear();
								attrvalues.clear();
								while (!line.startsWith("</node>")) {

									if (line.startsWith("<data")) {
										// Get Keyname
										attrnames.add(line.split("\">")[0]
												.replaceFirst("<data key=\"",
														"").replaceFirst("\">",
														""));
										// Key Values
										attrvalues.add(Float.parseFloat(line
												.split(">")[1].split("<")[0]));
									}

									line = in.readLine();
									line = line.trim();
									if (line == null) {
										in.close();
										throw new IncompleteInformationException();
									}
								}
								if (!attrnames.isEmpty()) {
									arrnames = new String[attrnames.size()];
									arrnames = attrnames.toArray(arrnames);
									arrvalues = new Float[attrvalues.size()];
									arrvalues = attrvalues.toArray(arrvalues);
									arrPackets.add(new Packet(arrnames,
											arrvalues));
								}
							}
							// Separate Information for different links
							if (line.startsWith("<link")) {
								attrnames.clear();
								attrvalues.clear();
								String[] help;

								// Get Sender and Receiver id
								attrnames.add("source");
								help = line.split("\"")[1].split(":");
								attrvalues
										.add(convertStringToFloat(help[help.length - 1]));

								attrnames.add("target");
								help = line.split("\"")[3].split(":");
								attrvalues
										.add(convertStringToFloat(help[help.length - 1]));

								while (!line.startsWith("</link>")) {

									if (line.startsWith("<data")) {
										// Get Keyname
										attrnames.add(line.split("\">")[0]
												.replaceFirst("<data key=\"",
														"").replaceFirst("\">",
														""));
										// Key Values
										attrvalues.add(Float.parseFloat(line
												.split(">")[1].split("<")[0]));
									}

									line = in.readLine();
									line = line.trim();
									if (line == null) {
										in.close();
										throw new IncompleteInformationException();
									}
								}
								arrnames = new String[attrnames.size()];
								arrnames = attrnames.toArray(arrnames);
								arrvalues = new Float[attrvalues.size()];
								arrvalues = attrvalues.toArray(arrvalues);
								arrPackets.add(new Packet(arrnames, arrvalues));
							}
							line = in.readLine();
							line = line.trim();
							if (line == null) {
								in.close();
								throw new IncompleteInformationException();
							}
						}
					}

					line = in.readLine();
					line = line.trim();
					if (line == null) {
						in.close();
						throw new IncompleteInformationException();
					}
				}
				// Finally add all packets to the measurement
				res[i].addPacket(arrPackets.toArray(new Packet[arrPackets
						.size()]));

			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to find file");
				res[i] = null;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to read file");
				res[i] = null;
			} catch (ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[i] = null;
			} catch (IncompleteInformationException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[i] = null;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		// If successful, add all measures to the model
		for (int i = 0; i < res.length; i++) {
			if (res[i] != null) {
				m.addNewMeasurement(res[i]);
				System.out.println("'" + res[i].getMeasureName()
						+ "' successfully loaded");
			}
		}

	}

	public static void saveToTrace(Model m) {

		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesTraces()));
		String filename = (m.getCurrentMeasurement().getMeasureName() + ".txt")
				.replaceAll(" ", "_");
		fc.setSelectedFile(new File(filename));
		fc.setFileFilter(new UniversalFilter("txt", "*.txt - Trace File"));
		if ((fc.showSaveDialog(m.getView()) == 0)
				&& (fc.getSelectedFile() != null)) {
			f = fc.getSelectedFile();
			if (!f.getName().endsWith(
					((UniversalFilter) fc.getFileFilter()).prefix)) {
				f = new File(f + ((UniversalFilter) fc.getFileFilter()).prefix);
			}
		} else
			return;

		Measurement meas = m.getCurrentMeasurement();
		float[][] values = new float[meas.getAttributeCount()][];
		String line = "";
		// ArrayList<RMT_Attribute> arrAttNames = new
		// ArrayList<RMT_Attribute>();
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(f));

			// Collect all used attributes and read out their values
			for (int i = 0; i < meas.getAttributeCount(); i++) {

				line = line
						+ (line.isEmpty() ? meas.getAttribute(i)
								.getAttributeName()
								: Constants.getTraceAttributeSeparator()
										+ meas.getAttribute(i)
												.getAttributeName());
				values[i] = meas.getAttributeValuesByName(meas.getAttribute(i)
						.getAttributeName(), false, false);
			}

			out.write(line);

			// Insert the values
			for (int i = 0; i < values[0].length; i++) {

				line = "";
				for (int j = 0; j < values.length; j++) {
					line = line
							+ (line.isEmpty() ? Float.toString(values[j][i])
									: Constants.getTraceDataSeparator()
											+ Float.toString(values[j][i]));

				}
				out.newLine();
				out.write(line);
			}

			out.flush();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Cannot create file");
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}

	}

	public static void loadFromTrace(Model m) {

		File[] f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesTraces()));
		fc.setFileFilter(new UniversalFilter("txt", "*.txt - Trace File"));
		fc.setMultiSelectionEnabled(true);
		if ((fc.showOpenDialog(m.getView()) == 0)
				&& (fc.getSelectedFiles() != null)) {

			f = fc.getSelectedFiles();
		} else
			return;

		Measurement[] res = new Measurement[fc.getSelectedFiles().length];
		ArrayList<Packet> arrPackets = new ArrayList<Packet>();
		String[] attrnames, valstrings;
		Float[] arrvalues;
		BufferedReader in = null;

		for (int i = 0; i < fc.getSelectedFiles().length; i++) {

			res[i] = new Measurement(m.getNextMeasurementNumber() + i,
					"Measurement " + (m.getNextMeasurementNumber() + i));
			arrPackets.clear();

			try {
				String line = null;

				// Open Reader and scan the document for relevant values
				in = new BufferedReader(new FileReader(f[i]));
				line = in.readLine();

				if (line == null) {
					throw new IncompleteInformationException();
				}
				// Create attribute array from data
				attrnames = line.split(Constants.getTraceAttributeSeparator());

				// Extract measuredata from file
				line = in.readLine();

				while (line != null) {

					valstrings = line.split(Constants.getTraceDataSeparator());
					// Check if number of attributenames and values are equal
					if (valstrings.length != attrnames.length) {
						throw new IncompleteInformationException();
					}
					arrvalues = new Float[valstrings.length];
					for (int j = 0; j < valstrings.length; j++) {
						// Try to convert value to float - if an error occurs,
						// set value null
						try {
							arrvalues[j] = Float.parseFloat(valstrings[j]);
						} catch (NumberFormatException e) {
							arrvalues[j] = null;
						}
					}
					arrPackets.add(new Packet(attrnames, arrvalues));
					line = in.readLine();
				}
				// If finished add all packets to measurement
				res[i].addPacket(arrPackets.toArray(new Packet[arrPackets
						.size()]));

			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to find file");
				res[i] = null;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to read file");
				res[i] = null;
			} catch (ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[i] = null;
			} catch (IncompleteInformationException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[i] = null;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		// If successful, add all measures to the model
		for (int i = 0; i < res.length; i++) {
			if (res[i] != null) {
				m.addNewMeasurement(res[i]);
				System.out.println("'" + f[i].getName()
						+ "' successfully loaded");
			}
		}
	}

	/**
	 * this is a cheap copy of the method above, but uses only one file instead
	 * of using the filechooser
	 * 
	 * @param m
	 * @param file
	 */
	public static void loadFromTrace(Model m, String files[]) {

		File[] f = new File[files.length];

		Measurement[] res = new Measurement[1];
		ArrayList<Packet> arrPackets = new ArrayList<Packet>();
		String[] attrnames, valstrings;
		Float[] arrvalues;
		BufferedReader in = null;

		for (int i = 0; i < f.length; i++) {

			f[i] = new File(files[i]);

			res[i] = new Measurement(m.getNextMeasurementNumber() + i,
					killFTypeExt(f[i].getName()));
			arrPackets.clear();

			try {
				String line = null;

				// Open Reader and scan the document for relevant values
				in = new BufferedReader(new FileReader(f[i]));
				line = in.readLine();
				if (line == null) {
					throw new IncompleteInformationException();
				}
				// Create attribute array from data
				attrnames = line.split(Constants.getTraceAttributeSeparator());

				// Extract measuredata from file
				line = in.readLine();

				while (line != null) {

					valstrings = line.split(Constants.getTraceDataSeparator());
					// Check if number of attributenames and values are equal
					if (valstrings.length != attrnames.length) {
						throw new IncompleteInformationException();
					}
					arrvalues = new Float[valstrings.length];
					for (int j = 0; j < valstrings.length; j++) {
						// Try to convert value to float - if an error occurs,
						// set value null
						try {
							arrvalues[j] = Float.parseFloat(valstrings[j]);
						} catch (NumberFormatException e) {
							arrvalues[j] = null;
						}
					}
					arrPackets.add(new Packet(attrnames, arrvalues));
					line = in.readLine();
				}
				// If finished add all packets to measurement
				res[i].addPacket(arrPackets.toArray(new Packet[arrPackets
						.size()]));

			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to find file");
				res[i] = null;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: Unable to read file");
				res[i] = null;
			} catch (ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[i] = null;
			} catch (IncompleteInformationException e) {
				JOptionPane.showMessageDialog(m.getView(),
						"ERROR: File contains incomplete information");
				res[0] = null;
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (IOException e) {
				}
			}
		}

		// If successful, add all measures to the model
		for (int i = 0; i < res.length; i++) {
			if (res[i] != null) {
				m.addNewMeasurement(res[i]);
				System.out.println("'" + f[i].getName()
						+ "' successfully loaded");
			}
		}
	}

	public static void saveFunctionAttribute(Model m, FunctionAttribute func) {

		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesFunctions()));
		String filename = (func.getAttributeName() + ".func").replaceAll(" ",
				"_");
		fc.setSelectedFile(new File(filename));
		fc.setFileFilter(new UniversalFilter("func",
				"*.func - Function Attributes"));
		if ((fc.showSaveDialog(m.getView()) == 0)
				&& (fc.getSelectedFile() != null)) {
			f = fc.getSelectedFile();
			if (!f.getName().endsWith(
					((UniversalFilter) fc.getFileFilter()).prefix)) {
				f = new File(f + ((UniversalFilter) fc.getFileFilter()).prefix);
			}
		} else
			return;

		// Get dependencies to other functionattributes of the selected function
		String depnames = "";
		ArrayList<FunctionAttribute> alldeps = new ArrayList<FunctionAttribute>();
		ArrayList<FunctionAttribute> sortdeps = new ArrayList<FunctionAttribute>();
		ArrayList<FunctionAttribute> tocheck = new ArrayList<FunctionAttribute>();

		// Delivers functions in order: earlier use -> earlier appearance
		FunctionAttribute[] depparam = func.getFunctionAttributeDependencies();

		// Add all dependencies to the checking array for further recursive
		// checks
		// Success of the later loading operation depends on the order
		for (int i = 0; i < depparam.length; i++) {
			tocheck.add(depparam[i]);
			alldeps.add(depparam[i]);
		}
		// First collect all dependency-attributes
		while (!tocheck.isEmpty()) {
			depparam = tocheck.get(tocheck.size() - 1)
					.getFunctionAttributeDependencies();
			for (int i = 0; i < depparam.length; i++) {
				if (!alldeps.contains(depparam[i])) {
					tocheck.add(0, depparam[i]);
					alldeps.add(0, depparam[i]);
				}
			}
			tocheck.remove(tocheck.size() - 1);
		}
		// --Dependency sorting--
		// First check for elements which have no dependencies
		for (int i = 0; i < alldeps.size(); i++) {

			if (alldeps.get(i).getFunctionAttributeDependencies().length == 0) {
				sortdeps.add(alldeps.get(i));
				alldeps.remove(i);
				i--;
			}
		}
		FunctionAttribute[] fas;
		while (!alldeps.isEmpty()) {
			for (int i = 0; i < alldeps.size(); i++) {

				fas = alldeps.get(i).getFunctionAttributeDependencies();
				tocheck.clear();
				// Transfer arrayvalues to arraylist
				for (int j = 0; j < fas.length; j++) {
					tocheck.add(fas[j]);
				}
				// Check, if the dependencies are already in the sorted
				// arraylist
				if (sortdeps.containsAll(tocheck)) {
					sortdeps.add(alldeps.get(i));
					alldeps.remove(i);
					i--;
				}
			}
		}

		for (int i = 0; i < sortdeps.size(); i++) {
			depnames = depnames
					+ (depnames.isEmpty() ? sortdeps.get(i).getAttributeName()
							: ", " + sortdeps.get(i).getAttributeName());
		}

		// Ask to save the other functions too
		if (depparam.length > 0) {
			int conf = JOptionPane
					.showConfirmDialog(
							m.getView(),
							"The function '"
									+ func.getAttributeName()
									+ "' you want to save depends on the following\nother functions:\n"
									+ depnames
									+ "\nYou need to save them too. Confirm?",
							"Save dependencies?", JOptionPane.YES_NO_OPTION);
			if (conf == 1) {
				return;
			}
		}

		// Add function to save to the dependency-array
		sortdeps.add(func);

		Object[] arr;
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new FileOutputStream(f));
			os.writeInt(sortdeps.size());

			// If there are other dependencies, save other objects first
			for (int i = 0; i < sortdeps.size(); i++) {
				// Extract information to save from the function attribute
				arr = sortdeps.get(i).getSaveOutput();
				os.writeObject(arr[0]);
				os.writeBoolean((Boolean) arr[1]);
				os.writeInt((Integer) arr[2]);
				os.writeObject(arr[3]);
				os.writeObject(arr[4]);
				os.writeObject(arr[5]);
				os.flush();
			}
			System.out.println("Function '" + func.getAttributeName()
					+ "' successfully saved");

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Unable to find file");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Unable to read/write file");
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}

	public static void loadFunctionAttribute(Model m) {

		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesFunctions()));
		fc.setFileFilter(new UniversalFilter("func",
				"*.func - Function Attributes"));
		if ((fc.showOpenDialog(m.getView()) == 0)
				&& (fc.getSelectedFiles() != null)) {
			f = fc.getSelectedFile();
		} else
			return;

		String name = "";
		boolean drawable;
		int out;
		ArrayList<String> appliance;
		ArrayList<String[]> params;
		ArrayList<float[]> settings;

		ArrayList<FunctionAttribute> reconstructed = new ArrayList<FunctionAttribute>();
		int num;

		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(f));
			num = is.readInt();

			for (int k = 0; k < num; k++) {
				name = (String) is.readObject();
				drawable = is.readBoolean();
				out = is.readInt();
				appliance = (ArrayList<String>) is.readObject();
				params = (ArrayList<String[]>) is.readObject();
				settings = (ArrayList<float[]>) is.readObject();

				// Convert Strings into RMT_Functions
				IRIS_FunctionModule fm;
				ArrayList<IRIS_FunctionModule> funcModules = new ArrayList<IRIS_FunctionModule>();
				for (int i = 0; i < appliance.size(); i++) {

					fm = m.getFunctionInstanceByName(appliance.get(i));

					if (fm == null) {
						System.err.println("Could not find required function: "
								+ appliance.get(i));
						throw new AttributeNotExistantException();
					}
					funcModules.add(fm);
				}

				// Find functions refering to other function attributes
				ArrayList<IRIS_Attribute[]> paramAttr = new ArrayList<IRIS_Attribute[]>();
				// First add arrays in loaded size
				for (int i = 0; i < params.size(); i++) {
					IRIS_Attribute[] nextattr = new IRIS_Attribute[params.get(i).length];
					paramAttr.add(nextattr);
					for (int j = 0; j < params.get(i).length; j++) {

						for (FunctionAttribute fa : reconstructed) {
							if (fa.getAttributeName().equals(params.get(i)[j])) {
								nextattr[i] = fa;
							}
						}
					}
				}

				/*
				 * //Find out, if all attribute names are existant boolean
				 * attrmissing = false; for(int i=0; i<params.size(); i++) {
				 * for(int j=0; j<params.get(i).length; j++) {
				 * if(m.getMeasureAttribute(params.get(i)[j]) == null) {
				 * attrmissing = true; break; } } }
				 */

				// Remapping only if at least one attribute name hast changed or
				// is not existant
				// if(attrmissing) {
				// Remapping

				// RMT_Attribute[][] attr =
				// DiaSettings.showFunctionMappingWindow(m,
				// paramAttr.toArray(new RMT_Attribute[paramAttr.size()][]),
				// funcModules.toArray(new
				// RMT_FunctionModule[funcModules.size()]));

				// paramAttr.clear();
				// for(int i=0; i<attr.length; i++) {
				// paramAttr.add(attr[i]);
				// }
				// }
				// Otherwise if function is complete directly insert parameter
				// attributes
				// else {
				for (int i = 0; i < params.size(); i++) {
					for (int j = 0; j < params.get(i).length; j++) {

						if (paramAttr.get(i)[j] == null) {
							paramAttr.get(i)[j] = m.getMeasureAttribute(params
									.get(i)[j], true);
						}
					}
				}
				// }
				FunctionAttribute fa = new FunctionAttribute(name, drawable,
						out, funcModules, paramAttr, settings, null);
				DiaFunctionSettings.showFunctionSettingsWindow(m, fa, -1, false);

				// Remember function attributes for recursive loading
				reconstructed.add(fa);
				m.addFunctionAttributeToMeasurement(reconstructed
						.get(reconstructed.size() - 1));
			}
			System.out.println("Function '" + name + "' successfully loaded");

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Unable to find file");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Unable to read file");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Unable to reconstruct the functionclass");
		} catch (AttributeNotExistantException e) {
			JOptionPane
					.showMessageDialog(m.getView(),
							"ERROR: A required (function-)attribute could not be found!\nLoading aborted");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}

	}

	public static void saveNoiseTrace(Model m) {

		//Dont save global values		
		List<IRIS_Attribute> listAttr = m.getMeasureAttributes(false);
		String[] attrnames = new String[listAttr.size()];
		for (int i = 0; i < listAttr.size(); i++) {
			attrnames[i] = listAttr.get(i).getAttributeName();
		}
		String res = (String) JOptionPane
				.showInputDialog(
						m.getView(),
						"Please choose the attribute to use to generate the noise trace",
						"Generate Noise Trace", JOptionPane.PLAIN_MESSAGE,
						null, attrnames, attrnames[0]);

		if (res == null) {
			return;
		}

		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Constants.getPathSavesNoisetraces()));
		String filename = (m.getCurrentMeasurement().getMeasureName() + ".txt")
				.replaceAll(" ", "_");
		fc.setSelectedFile(new File(filename));
		fc.setFileFilter(new UniversalFilter("txt", "*.txt - Noise Trace File"));
		if ((fc.showSaveDialog(m.getView()) == 0)
				&& (fc.getSelectedFile() != null)) {
			f = fc.getSelectedFile();
			if (!f.getName().endsWith(
					((UniversalFilter) fc.getFileFilter()).prefix)) {
				f = new File(f + ((UniversalFilter) fc.getFileFilter()).prefix);
			}
		} else
			return;

		String line = "";
		BufferedWriter out = null;
		float[] val = m.getMeasureAttributeValuesByName(res, false, false, false);

		try {
			out = new BufferedWriter(new FileWriter(f));

			// Write all values down
			for (int i = 0; i < val.length; i++) {

				line = Integer.toString((int) val[i]);
				out.write(line);
				out.newLine();
			}
			out.flush();
			System.out.println("Noisetrace successfully created");

		} catch (IOException e) {
			JOptionPane.showMessageDialog(m.getView(),
					"ERROR: Cannot create file");
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	private static void makeGap(int num, BufferedWriter out) throws IOException {

		for (int i = 0; i < num; i++) {
			out.write(" ");
		}
	}

	private static float convertStringToFloat(String s) {

		float res = (float) -0.0;
		// First try normal float parsing
		try {
			res = Float.parseFloat(s);
			return res;
		} catch (NumberFormatException nfe) {
		}
		// If not check for hexavalue
		try {
			res = Integer
					.valueOf(((s.contains("x")) ? s.split("x")[1] : s), 16)
					.floatValue();
			return res;
		} catch (NumberFormatException nfe) {
		}

		return res;
	}

	public static void saveMessages(Model m) {
		File f = fileChooserSave(m, Constants.getPathSavesMessages(), "_msg_");
		if(f == null) return;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			HashMap<String, MessageWrapper> map = m.getCurrentMeasurement()
					.getMsges();
			boolean first = true;
			for (String msgName : map.keySet()) {
				if (first)
					first = false;
				else
					bw.newLine();
				bw.write(map.get(msgName).toPString());
			}
			bw.close();
			System.out.println("Messagefile written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveScript(Model m, String fieldText) {
		File f = fileChooserSave(m, Constants.getPathSavesScripts(),
				"_script_.txt");
		if(f == null) return;
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(fieldText);
			fw.close();
			System.out.println("Scriptfile written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String loadScript(Model m, String folder) {
		File file = fileChooserOpen(m, folder);
		if (file == null)
			return null;
		return loadScript(file);
	}

	/**
	 * loads a Scriptfile and returns the String
	 * 
	 * @param file
	 * @return
	 */
	public static String loadScript(File file) {
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first)
					first = false;
				else
					text.append(Constants.getLineSep());
				text.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("Script imported");
		return text.toString();
	}

	public static File fileChooserSave(Model m, String path, String nameAdd) {
		File f = null;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(path));
		String filename = (m.getCurrentMeasurement().getMeasureName() + nameAdd)
				.replaceAll(" ", "_");
		fc.setSelectedFile(new File(filename));
		fc.setFileFilter(new UniversalFilter("txt", "*.txt msg or scripts"));
		if ((fc.showSaveDialog(m.getView()) == 0)
				&& (fc.getSelectedFile() != null)) {
			f = fc.getSelectedFile();
			if (!f.getName().endsWith(
					((UniversalFilter) fc.getFileFilter()).prefix)) {
				f = new File(f + ((UniversalFilter) fc.getFileFilter()).prefix);
			}
		}
		return f;
	}

	public static File fileChooserOpen(Model m, String path) {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(path));
		fc.setFileFilter(new UniversalFilter("txt", "*.txt msg or scripts"));
		if ((fc.showOpenDialog(m.getView()) == 0)
				&& (fc.getSelectedFiles() != null)) {
			return fc.getSelectedFile();
		} else
			return null;
	}

	public static String killFTypeExt(String fn) {
		return fn.substring(0, fn.indexOf("."));
	}
}
