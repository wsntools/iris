/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions.experimentfix.mussFix;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JFrame;

public class MussFixFix {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//		String folder = "z:/Donana/";
		String folder = "C:/Users/ramin/planet/planet-intern/PLANETExperiments/DBR-Oct-2011/measurement/Donana/2011-10-12/MUSS-fixed/Afternoon-Logs-UAV/";
		//		UAVXMLLogParser
		//				.main(new String[] {
		//						"C:/Users/ramin/planet/planet-intern/PLANETExperiments/DBR-Oct-2011/measurement/Donana/2011-10-12/MUSS-fixed/Afternoon-Logs-UAV/MoteStream_2000-01-01_05-32-43_at__dev_ttyUSB0.xml",
		//						"mote" });

		//		magic7ERepair(folder + "MoteStream_2000-01-01_05-32-43_at__dev_ttyUSB0.xml.txt");

		firstCorrelationCheck(folder + "AutopilotLog_2000-01-01_05-32-43.xml.csv.txt", folder
				+ "MoteStream_2000-01-01_05-32-43_at__dev_ttyUSB0.xml.txt-fixed.txt");

		//		UAVXMLLogParser.createNodeLogCSV(folder + "MoteStream_2000-01-01_05-32-43_at__dev_ttyUSB0.xml.txt-fixed.txt");
	}

	private static void firstCorrelationCheck(String autopilot, String moteStreamFixed) throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader(autopilot));
		BufferedReader br2 = new BufferedReader(new FileReader(moteStreamFixed));

		TreeSet<Long> times1 = new TreeSet<Long>();
		TreeSet<Long> times2 = new TreeSet<Long>();
		TreeSet<TimeMatch> timeMatches = new TreeSet<TimeMatch>();

		ArrayList<Point2D.Double> coordinates = new ArrayList<Point2D.Double>();

		String line = br1.readLine();
		long lineCount = 1;
		StringTokenizer st;
		Point2D.Double p;
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = -10, minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;
		while ((line = br1.readLine()) != null) {
			st = new StringTokenizer(line, ", ");
			long time = Long.parseLong(st.nextToken());

			p = new Point2D.Double(Double.valueOf(st.nextToken()), Double.valueOf(st.nextToken()));

			double z = Double.valueOf(st.nextToken());
			System.out.println(" x : " + p.x + " ,  y : " + p.y + " , z : " + z);
			timeMatches.add(new TimeMatch(0, time, p, z));
			//			System.out.println(p);
			//			coordinates.add(p);
			if (37.11 > p.x || p.x > 37.3 || -6.44 > p.y || -6.4 < p.y || 560 < z || 50 > z)
				continue;
			if (p.x < minX)
				minX = p.x;
			else if (p.x > maxX)
				maxX = p.x;
			if (z < minZ)
				minZ = z;
			else if (z > maxZ)
				maxZ = z;
			if (p.y < minY)
				minY = p.y;
			else if (p.y > maxY) {
				maxY = p.y;

				//				System.out.println(p.y);
				//				System.out.println(lineCount);
			}
			if (!times1.add(time)) {
				//				System.out.println(line);
				//				System.out.println(Long.parseLong(line.substring(0, line.indexOf(", "))));
			}
			lineCount++;
		}
		System.out.println(minX + "," + minY + " . " + maxX + "," + maxY + " . " + minZ + "," + maxZ);
		System.out.println("autopilot:");
		System.out.println("lines: " + lineCount);
		System.out.println("entries: " + times1.size());
		br1.close();

		line = br2.readLine();
		lineCount = 1;
		while ((line = br2.readLine()) != null) {
			if (!times2.add(Long.parseLong(line.substring(0, line.indexOf(" "))))) {
				//				System.out.println(line);
				//				System.out.println(Long.parseLong(line.substring(0, line.indexOf(", "))));
			}
			lineCount++;
		}
		System.out.println("motestream:");
		System.out.println("lines: " + lineCount);
		System.out.println("entries: " + times1.size());
		br2.close();

		int matches = 0;
		ArrayList<Long> dif = new ArrayList<Long>();
		long divSum = 0;
		long maxDiv = 0;

		for (long moteTime : times2) {
			long match = times1.floor(moteTime);
			//			System.out.println(moteTime + " ." + match);
			timeMatches.floor(new TimeMatch(0, match, null, 0)).time = moteTime;

			//			if (times1.contains(moteTime)) {
			//				matches++;
			//				match = moteTime;
			//			} else {
			//				match = times1.floor(moteTime);
			//				long div = moteTime - match;
			//				if (div > maxDiv)
			//					maxDiv = div;
			//				divSum += div;
			//				dif.add(div);
			//			}

			//			timeMatches.add(new TimeMatch(moteTime, match));
		}
		System.out.println("matches: " + matches);
		System.out.println("avg div= " + ((float) divSum / (float) dif.size()) + " // " + maxDiv);

		JFrame frame = new JFrame();
		Ap ap = new Ap();
		int fx = 800, fy = 600;
		frame.setSize(fx, fy);
		frame.add(ap);
		ap.init();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		double scale = 0;
		if (maxY - minY > maxX - minX)
			scale = fy / (maxY - minY);
		else
			scale = fx / (float) (maxX - minX);
		System.out.println("SCALE " + scale);
		ap.addSensor(37.112306478, -6.43328967082);
		ap.addSensor(37.112669383, -6.43279168259);
		ap.setValues(minX, minY, minZ, maxX, maxY, maxZ, scale);
		ap.setRoute(timeMatches);

	}

	public static void magic7ERepair(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + "-fixed.txt"));
		long time;
		String line, token = null;
		StringTokenizer st;
		boolean begin, broken;
		String brokenLine = null;
		while ((line = br.readLine()) != null) {
			st = new StringTokenizer(line, " ");
			time = Long.parseLong(st.nextToken());
			if (st.nextToken().equals("7E"))
				begin = true;
			else
				begin = false;
			while (st.hasMoreTokens())
				token = st.nextToken();
			if (!token.equals("7E")) {
				broken = true;
				brokenLine = line;
			} else
				broken = false;
			//			System.out.println(token);
			//			System.out.println(line + " begin: " + begin + ",broken: " + broken);
			if (!begin && brokenLine != null) {
				System.out.println(brokenLine + ", + " + line);
				String newLine = brokenLine + line.substring(line.indexOf(" ") + 1);
				bw.append(delete7E(newLine));
				brokenLine = null;
				bw.newLine();
			} else if (!broken) {
				bw.append(delete7E(line));
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}

	private static String delete7E(String l) {
		return l.replaceAll("7E", "");
	}

	/**
	 * Calculates the distance between to GPS Coordinates and returns this
	 * distance in km (for use in small areas only)
	 * 
	 * @param lon1
	 *            Longitude first Coordinate
	 * @param lat1
	 *            Latitude first Coordinate
	 * @param lon2
	 *            Longitude second Coordinate
	 * @param lat2
	 *            Latitude second Coordinate
	 * @return distance in km
	 */
	public static double getDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
		double dx = 111.3 * Math.cos((lat1 + lat2) / 2 * 0.01745) * (lon1 - lon2);
		double dy = 111.3 * (lat1 - lat2);

		return Math.sqrt(dx * dx + dy * dy);
	}

	public static class TimeMatch implements Comparable<TimeMatch> {

		long time, bestMatch;
		Point2D.Double loc;
		double height = 0;

		@Override
		public int compareTo(TimeMatch arg0) {
			return (int) (bestMatch - arg0.bestMatch);
		}

		public TimeMatch(long time, long bestMatch, Point2D.Double p, double height) {
			super();
			this.height = height;
			this.time = time;
			this.bestMatch = bestMatch;
			loc = p;
			System.out.println(loc);
		}
	}
}
