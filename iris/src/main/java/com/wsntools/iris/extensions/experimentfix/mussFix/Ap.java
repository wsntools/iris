/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions.experimentfix.mussFix;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.wsntools.iris.extensions.experimentfix.mussFix.MussFixFix.TimeMatch;

import processing.core.PApplet;

public class Ap extends PApplet {

	TreeSet<TimeMatch> timeMatches;
	Iterator<TimeMatch> iter;

	double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

	private boolean fade = false;

	private static int drawPerFrame = 100;
	private static int fadeTime = 100;
	private static double fadePercent = 0.03;
	private static int grayAlpha = 20;
	double scale;
	/**
	 * offset in pixel
	 */
	int offset = 20;
	long count = 0;

	public void setup() {
		background(0);
		noStroke();
		frameRate(20);
	}

	int i = 0;
	private double minZ;
	private double maxZ;

	ArrayList<Point.Double> sensors = new ArrayList<Point2D.Double>();

	public void draw() {

		if (fade) {
			fill(color(0, 0, 0, (int) (255 * fadePercent)));
			rect(0, 0, 10000, 10000);

			double distanceLon = MussFixFix.getDistance((minX), minY, (minX), maxY);
			double distanceLat = MussFixFix.getDistance((minX), minY, (maxX), minY);

			// test Value System.out.println(MussFixFix.getDistance(
			// 8.41321,49.9917, 8.42182,50.0049));
			//			System.out.println("Distance lon : " + distanceLon + " Distance lat " + distanceLat);

			Dimension size = this.getSize();
			int sizeX = size.width;
			int sizeY = size.height;
			//			System.out.println(sizeX + " x " + sizeY);

			double rasterSacleX = sizeX / distanceLat;
			double rasterSacleY = sizeY / distanceLon;

			//			System.out.println("Rasterscale X " + rasterSacleX + " rasterscale Y " + rasterSacleY);

			fill(color(133, 240, 130, 255));

			int add = (int) ((Math.max(distanceLon, distanceLat) * scale) / 1000);
			for (int x = offset; x < sizeX; x = x + add) {
				for (int y = offset; y < sizeY; y = y + add) {
					stroke(133, 240, 130, (int) (255 * fadePercent));
					this.strokeWeight(2);
					if (y == offset)
						this.line(x, 0, x, 10000);

					if (x == offset)
						this.line(0, y, 10000, y);
					noStroke();
					ellipse(x, y, 2, 2);
				}
			}
			fade = false;
			for (Point2D.Double s : sensors) {
				pushStyle();
				stroke(255);
				noFill();
				ellipse((int) (((s.x - minX)) * scale), (int) (((s.y - minY)) * scale), 6, 6);
				//				System.out.println((int) (((s.x - minX)) * scale) + " ," + (int) (((s.y - minY)) * scale));
				popStyle();
			}
		}
		if (i == 0 && timeMatches != null)
			iter = timeMatches.iterator();
		while (iter != null && iter.hasNext()) {
			TimeMatch tm = iter.next();
			if (tm.time == 0)
				fill(100, 100, 100, grayAlpha);
			else {
				double h = tm.height / maxZ;

				fill(color((int) (255 * h), (int) (255 * (1 - h)), 0, 237));
			}
			// System.out.println((tm.loc.x - minX) * scale);

			i++;

			if (i % fadeTime == 0) {
				fade = true;
			}

			if (i % drawPerFrame == 0) {
				return;
			}

			if (i == timeMatches.size()) {
				i = 0;
				fill(color(0, 0, 0));
				rect(0, 0, 10000, 10000);
			}

			// System.out.println("x: "+ tm.loc.x + " y: " +tm.loc.y+" "+ i);
			Point p = new Point((int) (((tm.loc.x - minX)) * scale), (int) (((tm.loc.y - minY)) * scale));
			//			PVector pv = new PVector((float) ((tm.loc.x - minX)), (float) ((tm.loc.y - minY)));
			ellipse(p.x + offset, p.y + offset, 9, 9);
			// point(pv.x, pv.y);
			// println((count++) + " " + pv);
		}
		//		System.out.println(sensors.size());
		// ellipse(200, 200, 50, 50);
	}

	public void setRoute(TreeSet<TimeMatch> timeMatches) {
		this.timeMatches = timeMatches;
		iter = timeMatches.iterator();
	}

	public void setValues(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double scale) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.scale = scale;
	}

	public void addSensor(double d, double e) {
		sensors.add(new Point2D.Double(d, e));
	}

	int imgCounter = 0;

	public void keyPressed() {
		if (keyCode == KeyEvent.VK_SPACE)
			save("UAV-" + (imgCounter++) + ".jpg");
		System.out.println(new File("UAV.jpg").getAbsolutePath());
	}

	public void mousePressed() {
		save("UAV-" + (imgCounter++) + ".jpg");
	}

}
