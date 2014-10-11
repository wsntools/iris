/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.tinyos.message.Message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.packageDecode.parser.PackageDecoder;
import com.wsntools.iris.packageDecode.parser.PackageDecoder.TimeMessage;
import com.wsntools.iris.tools.SaveAndLoad;

// A) Converts log files Telemetric log (xml) -> (timeStamped) CSV - (can be displayed by the RMT)
// B(1)) Node log (xml) -> timeStamped + Message HEX
// B(2)) timeStamped + Message HEX -> (timeStamped) CSV - (can be displayed by the RMT)
/**
 * The program has to be started with two parameters. The first is the file that
 * should be converted. The second gives the method/type of convertation: A) :
 * "tele", B(1+2)): "node", B(2)): "hex" "tele" for logs of the uav telemetric
 * data, "node" for logged node data on the uav and "hex" for the dumped files
 * of the C_Listener node
 * 
 * @author Ramin Soleymani
 * 
 */
public class UAVXMLLogParser {

	String fileName; // original name
	File file; // repaired file with root
	ArrayList<UAVLog> timeStamps; // all timeStamps

	/**
	 * important for both file types. makes the xml file well-formed.
	 * 
	 * @param fn
	 * @return
	 */
	private File createTempWithRoot(String fn) {
		try {
			fileName = fn;
			file = new File(fn + "-temp.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			BufferedReader br = new BufferedReader(new FileReader(fn));
			String l = null;
			String nl = System.getProperty("line.separator");
			bw.write(br.readLine() + nl);
			bw.write("<root>" + nl);
			while ((l = br.readLine()) != null) {
				bw.write(l + nl);
			}
			bw.write("</root>");
			br.close();
			bw.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * parses the uav-log and creates an arraylist of UAVLog objects 1. steo of:
	 * Converts log files Telemetric log (xml) -> (timeStamped) CSV - (can be
	 * displayed by the RMT)
	 * 
	 * @param file
	 * @return
	 */
	private ArrayList<UAVLog> parseUAVLog(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("timetag");
			ArrayList<UAVLog> list = new ArrayList<UAVXMLLogParser.UAVLog>(nodeList.getLength() + 2);
			for (int s = 0; s < nodeList.getLength(); s++) {
				Element el = (Element) nodeList.item(s);
				list.add(new UAVLog(el.getAttribute("time"), el.getAttribute("LAT"), el.getAttribute("LON"), el
						.getAttribute("HEI"), el.getAttribute("PITCH"), el.getAttribute("YAW"),
						el.getAttribute("ROLL"), el.getAttribute("VN"), el.getAttribute("VE"), el.getAttribute("VD"),
						el.getAttribute("GPSW"), el.getAttribute("GPST")));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * main function to get the logged data from the node attached to the uav
	 * 
	 * @param file
	 * @return fileName of the new file
	 */
	private String NodeLog_To_Time_And_Hex(File file) {
		try {
			FileWriter fw = new FileWriter(fileName + ".txt");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("timetag");
			String nl = System.getProperty("line.separator");
			// fw.write("time,data" + nl);
			for (int s = 0; s < nodeList.getLength(); s++) {
				Node n = nodeList.item(s);
				fw.append(String.valueOf(UAVLog.simpleDateFormat.parse(((Element) n).getAttribute("time")).getTime())
						+ " ");
				fw.append((getStrValue(Base64Coder.decodeLines(nodeList.item(s).getTextContent()))) + nl);
			}
			fw.close();
			file.delete();
			return fileName + ".txt";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ByteArray to Hex-String
	 * 
	 * @param obj
	 * @return
	 */
	public String getStrValue(byte[] arrByte) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arrByte.length; i++) {

			char b = (char) (arrByte[i] & 0xFF);
			if (b < 0x10) {
				sb.append("0");
			}
			sb.append((String) (Integer.toHexString(b)).toUpperCase() + " ");
		}
		return sb.toString();
	}

	/**
	 * used to create a cvs file
	 */
	public void toCSV() {
		File cvsFile = new File(fileName + ".csv");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(cvsFile));
			String nl = System.getProperty("line.separator");
			bw.write("time,lat,lon,hei,pitch,yaw,roll,vn,ve,vd,gpsw,gpst" + nl);
			for (UAVLog tt : timeStamps)
				bw.write(tt.time + Constants.getTraceAttributeSeparator() + tt.lat + Constants.getTraceAttributeSeparator()
						+ tt.lon + Constants.getTraceAttributeSeparator() + tt.hei + Constants.getTraceAttributeSeparator()
						+ tt.pitch + Constants.getTraceAttributeSeparator() + tt.yaw + Constants.getTraceAttributeSeparator()
						+ tt.roll + Constants.getTraceAttributeSeparator() + tt.vn + Constants.getTraceAttributeSeparator()
						+ tt.ve + Constants.getTraceAttributeSeparator() + tt.vd + Constants.getTraceAttributeSeparator()
						+ tt.gpsw + Constants.getTraceAttributeSeparator() + tt.gpst + nl);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * uses the NodeLog_To_Time_And_Hex and the package decoder to create csv
	 * files
	 * 
	 * @param fileName
	 */
	public static String[] createNodeLogCSV(String fn) {
		// String fn = NodeLog_To_Time_And_Hex(createTempWithRoot(fileName));
		PackageDecoder pdecoder = new PackageDecoder(false);
		String nl = System.getProperty("line.separator");
		// > create temp file without time
		File tmpFile = new File(fn + "-tmp.txt");
		FileWriter fw;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fn));
			fw = new FileWriter(tmpFile);
			while (br.ready()) {
				String l = br.readLine();
				l = l.substring(l.indexOf(" ") + 1);
				fw.write(l + nl);
				// System.out.println(l);
			}
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// < create temp file without time
		pdecoder.loadFile(tmpFile.getAbsolutePath());
		// pdecoder.printAllMessage();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fn));
			// maps message classes to Wrapper, AttributeWrapper and file
			HashMap<Class<? extends Message>, Object[]> map = new HashMap<Class<? extends Message>, Object[]>();
			// go through all messages and write them
			System.out.println(pdecoder.timeMessages.size() + " messages");
			for (TimeMessage tm : pdecoder.timeMessages) {
				Class<? extends Message> cls = null;
				try {
					cls = tm.mes.getClass();
					System.out.println(cls.getSimpleName());
				} catch (NullPointerException exc) {
					//					System.out.println(exc.getMessage());
					// TODO delete before/dont create TimeMessage
					System.out.println("message at missing " + tm.time);
					continue;
				}
				// get the wrappers and files from the map or create new
				if (!map.containsKey(cls)) {
					MessageAttributeWrapper maw = new MessageAttributeWrapper(cls);
					MessageWrapper mw = new MessageWrapper(maw.cls, "", 0);
					File file = new File(SaveAndLoad.killFTypeExt(fn) + "_" + cls.getSimpleName() + ".csv");
					map.put(cls, new Object[] { maw, mw, file });
					fw = new FileWriter(file);
					String line = "loggerTime, time, ";
					for (String ValueName : maw.names)
						line += ValueName + Constants.getTraceAttributeSeparator();
					fw.write(line + nl);
					fw.close();
				}
				Object[] w_f = map.get(cls);
				MessageAttributeWrapper maw = (MessageAttributeWrapper) w_f[0];
				MessageWrapper mw = (MessageWrapper) w_f[1];
				File file = (File) w_f[2];
				fw = new FileWriter(file, true);
				// end: get the wrappers and files from the map or create new
				int i = 0;
				for (Method get : maw.getter) {
					try {
						// System.out.println(get.invoke(tm.mes, new
						// Object[0]));
						mw.set(i, get.invoke(tm.mes, new Object[0]));
					} catch (Exception e) {
						// e.printStackTrace();
						mw.set(i, new Integer(0));
					}
					i++;
				}
				// System.out.println(mw.cls.getSimpleName()+
				// " "+mw.toCSVString());
				String fline = br.readLine();
				// write line for each message
				StringBuilder sLine = new StringBuilder();
				// loggertime
				sLine.append(fline.substring(0, fline.indexOf(" ")) + Constants.getTraceDataSeparator());
				// messagetime
				sLine.append(String.valueOf(tm.time) + Constants.getTraceDataSeparator());
				sLine.append(mw.toCSVString());
				// System.out.println(mw.toCSVString());
				fw.write(sLine.toString() + nl);
				System.out.println(sLine);
				fw.close();
			}
			br.close();
			String[] files = new String[map.size()];
			int i = 0;
			Iterator<Object[]> iter = map.values().iterator();
			while (iter.hasNext()) {
				files[i++] = ((File) iter.next()[2]).getAbsolutePath();
				System.out.println(files[i - 1]);
			}
			tmpFile.delete();
			return files;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		if (args.length == 0) {// test
			UAVXMLLogParser p = new UAVXMLLogParser();
			p.createNodeLogCSV("hexDump.txt");
		} else {
			UAVXMLLogParser p = new UAVXMLLogParser();
			if (args[1].equalsIgnoreCase("tele")) { // A)
				p.timeStamps = p.parseUAVLog(p.createTempWithRoot(args[0]));
				p.toCSV();
			} else if (args[1].equalsIgnoreCase("mote")) { // B(1))
				String hexFile = p.NodeLog_To_Time_And_Hex(p.createTempWithRoot(args[0]));
				//				p.createNodeLogCSV(hexFile);
			} else if (args[1].equalsIgnoreCase("hex")) { // B(2))
				p.createNodeLogCSV(args[0]);
			}
		}
	}

	public static class UAVLog {

		Date timeD;
		long time;
		double lat, lon, hei, pitch, yaw, roll, vn, ve, vd;
		long gpsw, gpst;

		static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy	HH:mm:ss:SSS");

		public UAVLog(String timeD, String lat, String lon, String hei, String pitch, String yaw, String roll,
				String vn, String ve, String vd, String gpsw, String gpst) {

			try {
				this.timeD = simpleDateFormat.parse(timeD);
				time = this.timeD.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.lat = Double.parseDouble(lat);
			this.lon = Double.parseDouble(lon);
			this.hei = Double.parseDouble(hei);
			this.pitch = Double.parseDouble(pitch);
			this.yaw = Double.parseDouble(yaw);
			this.roll = Double.parseDouble(roll);
			this.vn = Double.parseDouble(vn);
			this.ve = Double.parseDouble(ve);
			this.vd = Double.parseDouble(vd);
			this.gpsw = Long.parseLong(gpsw);
			this.gpst = Long.parseLong(gpst);
		}
	}

}
