/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.worldmap;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.view.orbit.OrbitView;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;

import com.wsntools.iris.modules.gui.worldmap.model.ISensorNode;
import com.wsntools.iris.modules.gui.worldmap.model.ISensorSample;
import com.wsntools.iris.modules.gui.worldmap.model.PathColor;
import com.wsntools.iris.modules.gui.worldmap.util.ScreenShotAction;

/**
 * Creates and controls a WorldWind globe. Provides methods to display Sensor
 * data on the globe
 * 
 * @author Marvin Baudewig, Sascha Hevelke
 */
public class GlobeController implements SelectListener, ComponentListener {
	BasicModel m;
	private WorldWindowGLCanvas wwd;
	Container d;
	
	private ArrayList<ISensorNode> display_sensornodes;
	
	private ScreenAnnotation ssButton_Annotation = null;
	private RenderableLayer ssButton_Layer;
	
	private RenderableLayer surfaceImage_Layer;

	public GlobeController(Container draw) {
		d = draw;

		wwd = new WorldWindowGLCanvas();
		wwd.setPreferredSize(new Dimension(1000, 1000));
		d.add(wwd);
		d.setLayout(new GridLayout(1, 1));

		m = new BasicModel();
		wwd.setModel(m);
		wwd.addSelectListener(this);
		wwd.addComponentListener(this);
		display_sensornodes = new ArrayList<>();
		surfaceImage_Layer = new RenderableLayer();
		m.getLayers().add(surfaceImage_Layer);
	}

	/**
	 * Removes all Sensornodes from the model and adds {@link sensorNodes} to the model
	 * @param sensorNodes
	 *            List of sensor nodes to be displayed on the globe
	 */
	public void setExclusiveSensorDisplay(ArrayList<ISensorNode> sensorNodes) {
		this.display_sensornodes = sensorNodes;
	}
	
	/**
	 * Adds a list of sensorNodes to the model
	 * @param sensorNodes Sensor nodes to be added
	 */
	public void addSensorsToDisplay(ArrayList<ISensorNode> sensorNodes) {
		display_sensornodes.addAll(sensorNodes);
	}
	
	/**
	 * Adds a sensor node to the model
	 * @param sensorNode Sensor Node to be added
	 */
	public void addSensorToDisplay(ISensorNode sensorNode) {
		display_sensornodes.add(sensorNode);
	}
	
	/**
	 * removes the specified node from the model
	 * @param sensorNode sensor node to be removed
	 */
	public void removeFromDisplay(ISensorNode sensorNode) {
		display_sensornodes.remove(sensorNode);
	}
	
	/**
	 * Displays all nodes which are currently present in the model
	 */
	public void displayNodes() {
		ArrayList<Layer> remove_list = new ArrayList<Layer>();
		for (int i = 0; i < m.getLayers().size(); i++) {
			if (m.getLayers().get(i).getName().startsWith("SENSORDATA_")) {
				remove_list.add(m.getLayers().get(i));
			}
		}

		for (Layer i : remove_list) {
			m.getLayers().remove(i);
		}

		/*
		 * Add a new layer for each sensor node, create a path from the
		 * sensornodes' gps coordinates, add the path to the layer
		 */
		for (ISensorNode node : display_sensornodes) {

			RenderableLayer current;

			current = new RenderableLayer();
			m.getLayers().add(current);
			current.setName("SENSORDATA_" + node.getSensorID());

			// Set a color for the path by specifying the color (in hue, full
			// saturation and value are given)
			// for the latest sensor sample. All earlier samples of this path
			// will have a lighter color determined
			// by their position in the path
			//PathColor color = new PathColor(node.getGPSDataList().size(),
			//		(float) Math.random());
			PathColor color = new PathColor(node.getGPSDataList().size(),node.getColor());

			ArrayList<Position> points = new ArrayList<Position>();
			for (ISensorSample point : node.getGPSDataList()) {
				points.add(Position.fromDegrees(point.getLatitude(),
						point.getLongitude(), 90));
			}
			Path p = new Path(points);
			p.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
			p.setFollowTerrain(true);
			p.setPathType(AVKey.LINEAR);
			p.setShowPositions(true);
			p.setPositionColors(color);

			current.removeAllRenderables();
			current.addRenderable(p);
		}
		wwd.redrawNow();
	}
	/**
	 * Sets the view position to the provided location
	 * @param lat Latitude of the position
	 * @param lon Londitude of the position
	 * @param height View height (in meters)
	 */
	public void setViewToPosition(double lat, double lon, int height) {
		View wwView = wwd.getView();
		if (wwView instanceof OrbitView) {
			((OrbitView)wwView).setCenterPosition(Position.fromDegrees(lat,lon));
			((OrbitView)wwView).setZoom(height*10);
		}
	}
	/**
	 * Adds a screenshot button to the panel which prompts the user for a location
	 * to save the screenshot.
	 */
	public void showScreenShotButton() {
		if (ssButton_Annotation==null && ssButton_Layer==null) {
			ssButton_Annotation = new ScreenAnnotation(
					"Screenshot", new Point(wwd.getWidth() / 2, 30));
			ssButton_Annotation.getAttributes().setInsets(new Insets(10, 10, 10, 10));
			ssButton_Annotation.getAttributes().setCornerRadius(2);
			ssButton_Layer = new RenderableLayer();
			ssButton_Layer.setName("LAYER_ScreenshotButton");
			ssButton_Layer.addRenderable(ssButton_Annotation);
			m.getLayers().add(ssButton_Layer);
		}
	}

	/**
	 * Prompts the user for a location to save a screenshot.
	 */
	public void takeScreenshot() {
		ScreenShotAction sa = new ScreenShotAction(this.wwd);
		ssButton_Layer.setEnabled(false);
		sa.takeScreenshot();
		ssButton_Layer.setEnabled(true);
	}

	/**
	 * Updates the positions of all additional visible components on the panel
	 */
	private void updateAnnotationPos() {
		if (ssButton_Annotation!=null && ssButton_Layer!=null) {
			ssButton_Annotation.setScreenPoint(new Point(wwd.getWidth()/2,20));
		}
	}
	
	/**
	 * Add a image to the globe at a specific location.
	 * @param path The path of the image (.png and .jpg tested thus far)
	 * @param corners ArrayList of LatLon object for the corners, CCW, starting bottom left
	 */
	public void addSurfaceImage(String path, ArrayList<LatLon> corners) {
		SurfaceImage si1 = new SurfaceImage(path,corners);
		surfaceImage_Layer.addRenderable(si1);
	}
	
	@Override
	public void selected(SelectEvent eve) {
		if (eve.getEventAction().equals(SelectEvent.LEFT_CLICK)
				&& eve.getTopObject() instanceof ScreenAnnotation) {
			takeScreenshot();
		}

	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		updateAnnotationPos();
	}
	
	
	/*
	 *Unused interface implementations 
	 */
	
	@Override
	public void componentHidden(ComponentEvent arg0) {}
	@Override
	public void componentMoved(ComponentEvent arg0) {}
	@Override
	public void componentShown(ComponentEvent arg0) {}
}
