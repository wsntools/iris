/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class GraphFunction {
	
	private XYSeries fullSeries = null;
	private XYSeries shownSeries = null;
	private XYSeries partialSeries = null;
	
	private int displayStatus = 0;
	private int charttype = 0;
	private int barnum;
	private String name = "";
	private int lower, upper = -1;
	private int showNLastValues = -1;
	private int lastKnown = -1;
	private Model model = null;
	private float[] values;
	private IRIS_Attribute yAxis = null;
	private ArrayList<Integer> indices = null;

	public GraphFunction(String name, float[] values, int displayStatus,
			Model model, ArrayList<Integer> indices, IRIS_Attribute yAxis, int charttype, int barnum) {
		this.yAxis = yAxis;
		this.name = name;
		this.model = model;
		this.partialSeries = new XYSeries(name + " partial");
		this.indices = indices;
		this.values = values;
		this.charttype = charttype;
		this.barnum = barnum;
		readValues(values);
		setDisplayStatus(displayStatus);
		fullRedraw();
	}

	protected String getName() {
		return name;
	}

	/**
	 * recompute the series to drawn using the new status
	 * 
	 * @param displayStatus
	 * 
	 * @return true if the reference to the current set has changed
	 */
	protected boolean setDisplayStatus(int displayStatus) {
		if (this.displayStatus != displayStatus) {
			if (displayStatus == 1) {
				lower = upper = showNLastValues = -1;
			}
			this.displayStatus = displayStatus;
			return fullRedraw();
		}
		return false;
	}

	protected XYSeries getUnusedReference() {
		return (shownSeries == fullSeries ? partialSeries : fullSeries);
	}
	
	public IRIS_Attribute getYAxis(){
		return yAxis;
	}

	/**
	 * returns whether the series reference has changed
	 */
	private boolean fullRedraw() {

		boolean result = false;
		//Destinguish between line/barchart
		if(charttype == 0) {
			switch (displayStatus) {
	
			case 0: // draw everything
				if (null != yAxis) {
					shownSeries = partialSeries;
	
					// TODO this is not working...
					float[] yAxisValues = yAxis.getValues(model
							.getCurrentMeasurement().getAllPacketsInOrder());
	
					for (int i = 0; i < values.length; i++) {
	//					System.out.println(yAxisValues[i]);
						
						int index = i;
						if (null != indices && i < indices.size()){
							index = indices.get(i);
						}
	
						// TODO THIS IS BAD SINCE THE ATTRIBUTE IS NOT YET FILTERED;
						// IT JUST GIVES BACK THE FIRST X VALUES
						//THE SAME FOR ADD VALUE
						// FILTERING SHOULD BE DONE WITHIN THIS CLASS! THEN I COULD JUST USE THE INDECES
						shownSeries.add(yAxisValues[index], values[i]);
					}
	
					result = true;
	
				} else if (shownSeries != fullSeries) {
					shownSeries = fullSeries;
					result = true;
				}
				// TODO change asap:
	
				break;
			case 1: // Draw last n values - find parameter with smallest data count
				if (shownSeries == fullSeries) {
					partialSeries.clear();
					shownSeries = partialSeries;
					// TODO CHANGE THROUGH LISTENER
					result = true;
				}
				int min = (fullSeries.getItemCount() < showNLastValues ? fullSeries
						.getItemCount() : showNLastValues);
	
				for (int i = fullSeries.getItemCount() - min; i < fullSeries
						.getItemCount(); i++) {
					partialSeries.add(i - 1, fullSeries.getY(i));
				}
	
				break;
			// Display value range - find parameter with highest data count
			case 2:
				if (shownSeries == fullSeries) {
					partialSeries.clear();
					shownSeries = partialSeries;
					result = true;
				}
				for (int i = lower, i2 = 0; i < upper
						&& i < fullSeries.getItemCount(); i++, i2++) {
					partialSeries.add(i2, fullSeries.getY(i));
				}
				break;
			}
			
			
		}
		//On barchart, check for barnumber and add it to the series
		if(charttype == 1) {
			shownSeries = fullSeries;
			fullSeries.clear();
			fullSeries.add(barnum, values[0]);
		}
		shownSeries.fireSeriesChanged();
		return result;
	}

	protected void setShowLastNValues(int showNLastValues) {
		if (showNLastValues != this.showNLastValues) {
			this.showNLastValues = showNLastValues;
			if (1 == displayStatus) {
				fullRedraw();
			}
		} else {
			return;
		}
	}

	protected void setBoundaries(int lower, int upper) {
		if (this.lower != lower || this.upper != upper) {
			this.lower = lower;
			this.upper = upper;
			if (2 == displayStatus) {
				fullRedraw();
			}
		} else {
			return;
		}
	}

	protected XYSeries getSeries() {
		return shownSeries;
	}

	/**
	 * Even if this list takes a complete array as input, it will just draw new
	 * values
	 * 
	 * @param values
	 */
	protected void addValue(float[] values) {

		if (values.hashCode() != lastKnown) {
			lastKnown = values.hashCode();
		} else {
			return;
		}

		for (int i = fullSeries.getItemCount() - 1; i < values.length; i++) {
			fullSeries.add(i, values[i]);
		}
		switch (displayStatus) {
		case 0:
			// TODO this is not working since the packet will update this
			// section earlier than the attribute itself
			if (null != yAxis) {
				
				// TODO this is not working...
				// float[] yAxis = ((NormalAttribute) model
				// .getCurrentMeasurement().getYAxis())
				// .getHistoricValues();
				float[] yAxisValues = yAxis.getValues(model
						.getCurrentMeasurement().getAllPacketsInOrder());

				for (int i = 0; i < values.length; i++) {

					int index = i;
					if (null != indices && i < indices.size()){
						index = indices.get(i);
					}
//					System.out.println(yAxisValues[i]);
					shownSeries.add(yAxisValues[index], values[i]);
				}
			}
			// do nothing
			break;
		case 1:
			// add just the last one (meybe it can happen to miss some values
			// before?
			if (partialSeries.getItemCount() >= showNLastValues) {
				partialSeries.remove(0);
			}
			partialSeries.add(values.length - 1, values[values.length - 1]);
			break;
		case 2:
			if (partialSeries.getItemCount() < upper - lower) {
				partialSeries.add(shownSeries.getItemCount(),
						values[values.length - 1]);
			}
			break;
		default:
			break;
		}
		shownSeries.fireSeriesChanged();
	}

	protected void readValues(float[] values) {
		this.fullSeries = new XYSeries(name);
		for (int i = 0; i < values.length; i++) {
			fullSeries.add(i, values[i]);
		}
	}
}
