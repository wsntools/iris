/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;

/**
 * @author Sascha Jungen
 * M#2242754
 * This class uses the jfreechart-library (GNU Lesser General Public Licence) to draw the graphics
 */

public class Graph extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int panelSizeX = 600, panelSizeY = 400;
	private ChartPanel chartPanel = null;
	//0-Linechart	1-Barchart
	private int charttype = 0;
	
	private XYPlot plot;
	private HashMap<String, GraphFunction> functionMap = new HashMap<String, GraphFunction>();

	// Settings for plot-drawing

	// 0 - Draw all values, 1 - Draw last n values, 2 - Draw range of values
	private int displayStatus = 0;

	private int lastvaluesonly = -1;

	private int rangefrom = -1, rangeto = -1;
	private Model model = null;

	public Graph(Model model) {

		this.model = model;

		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,
				null, PlotOrientation.VERTICAL, true, true, false);		

		chartPanel = new ChartPanel(chart);
		setupChartType(chart);
		

		this.setLayout(new BorderLayout());
		this.add(chartPanel, BorderLayout.CENTER);
	}
	
	
	// Setupfunction for adjusting graph parameters on new instanciation
	private void setupChartType(JFreeChart chart) {
		
		chart.setBackgroundPaint(Color.white);

		// plot = chart.getCategoryPlot();
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();

		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setAutoRange(true);
		domainAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setAutoRange(true);
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		chartPanel.setChart(chart);
	}

	// Display new values given as params
	// Dim 1 - # of dataset (normally divided by sender id)
	XYSeries series;
	XYSeriesCollection xysc = new XYSeriesCollection();

	public void drawNewValues(float[][] arr, String[] names) {
		drawNewValues(arr, names, null, null);
	}

	private void insertNewGraph(float[] arr, String name, ArrayList<Integer> indices, IRIS_Attribute yAxis, int barnr) {
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(2, Color.black);
		functionMap.put(name, new GraphFunction(name, arr, displayStatus, model, indices, yAxis, charttype, barnr));
		xysc.addSeries(functionMap.get(name).getSeries());
		plot.setDataset(xysc);
	}

	public void drawNewValues(float[][] arr, String[] names, ArrayList<Integer> indices, IRIS_Attribute yAxis) {
		for (int i = 0; i < names.length; i++) {
			if (false == functionMap.containsKey(names[i])) {
				insertNewGraph(arr[i], names[i], indices, yAxis, i);
			} else {
				if (yAxis != null && functionMap.get(names[i]).getYAxis() != yAxis) {
					forceRedraw(arr, names, indices, yAxis);
				} else {
					functionMap.get(names[i]).addValue(arr[i]);
				}
			}
		}
		cleanGraph(names);
	}

	public void forceRedraw(float[][] arr, String[] names, ArrayList<Integer> indices, IRIS_Attribute yAxis) {
		// TODO Auto-generated method stub
		for (int i = 0; i < names.length; i++) {
			if (false == functionMap.containsKey(names[i])) {
				insertNewGraph(arr[i], names[i], indices, yAxis, i);
			} else {
				xysc.removeSeries(functionMap.get(names[i]).getSeries());
				functionMap.get(names[i]).readValues(arr[i]);
				xysc.addSeries(functionMap.get(names[i]).getSeries());
			}
		}
		cleanGraph(names);
	}

	// Visual adjustment
	// -----------------

	//0-Linechart	1-Barchart
	public void switchGraphType(int type) {
		
		if(type != charttype) {
						
			switch (type) {
			case 0:
				setupChartType(ChartFactory.createXYLineChart(null, null, null,	null, PlotOrientation.VERTICAL, true, true, false));
				break;
			case 1:
				setupChartType(ChartFactory.createXYBarChart(null, null, false, null, null, PlotOrientation.VERTICAL, true, true, false));
				break;
			}
			
			charttype = type;
		}
		
	}
	
	// Show all values

	private void changeDisplayStatus(int displayStatus) {
		if (this.displayStatus != displayStatus) {
			this.displayStatus = displayStatus;

		}
	}

	public void displayAllPackets() {

		// TODO reference
		for (GraphFunction temp : functionMap.values()) {

			setDisplayStatus(0);

			// temp.setDisplayStatus(1);
		}

	}

	// Show last n values
	public void displayNumberOfLastValues(int val) {

		// Adjusts the drawing of the last 'val' packets
		if (val > 0) {
			lastvaluesonly = val;
			setDisplayStatus(1);

			// TODO reference
			for (GraphFunction temp : functionMap.values()) {
				temp.setShowLastNValues(val);
			}
		}
	}

	public int getNumberOfLastValues() {

		return lastvaluesonly;
	}

	// Range of values to show
	public void displayRangeOfValues(int from, int to) {

		if ((from < to)) {
			rangefrom = (from >= 0) ? from : 0;
			rangeto = to;

			setDisplayStatus(2);

			// TODO reference
			for (GraphFunction temp : functionMap.values()) {
				temp.setBoundaries(from, to);
				// temp.setDisplayStatus(2);
			}
		}
	}

	public int[] getRangeOfValues() {

		return new int[] { rangefrom, rangeto };
	}

	// To get the upper bound of packets
	public int getHighestDataCount() {

		int max = 0, val;
		for (int i = 0; i < plot.getSeriesCount(); i++) {

			val = plot.getDataset().getItemCount(i);
			max = (val > max) ? val : max;
		}

		return max;
	}

	// Private function to adjust other values when status is set
	private void setDisplayStatus(int status) {

		displayStatus = status;
		switch (status) {

		case 0:
			lastvaluesonly = rangefrom = rangeto = -1;
			for (GraphFunction temp : functionMap.values()) {
				if (true == temp.setDisplayStatus(0)) {
					xysc.removeSeries(temp.getUnusedReference());
					xysc.addSeries(temp.getSeries());
				}
			}
			break;
		case 1:
			rangefrom = rangeto = -1;
			for (GraphFunction temp : functionMap.values()) {
				if (true == temp.setDisplayStatus(1)) {
					xysc.removeSeries(temp.getUnusedReference());
					xysc.addSeries(temp.getSeries());
				}
			}
			break;
		case 2:
			lastvaluesonly = -1;
			for (GraphFunction temp : functionMap.values()) {
				if (true == temp.setDisplayStatus(2)) {
					xysc.removeSeries(temp.getUnusedReference());
					xysc.addSeries(temp.getSeries());
				}
			}
			break;
		}
		plot.getRangeAxis().setAutoRange(true);
		plot.getDomainAxis().setAutoRange(true);
	}

	// Panelspecific functions
	@Override
	public Dimension getPreferredSize() {
		return this.getMaximumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(panelSizeX, panelSizeY);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(panelSizeX, panelSizeY);
	}

	/**
	 * This function removes all references on deleted graphs
	 * 
	 * @param names
	 *            the current valid list of graphs
	 */
	private void cleanGraph(String[] names) {
		// TODO CLUDE better write an event for this!
		ArrayList<GraphFunction> toDelete = findUnecessaryGraphs(names);
		removeListOfGraphs(toDelete);
	}

	private ArrayList<GraphFunction> findUnecessaryGraphs(String[] names) {
		ArrayList<GraphFunction> result = new ArrayList<GraphFunction>();

		for (GraphFunction temp : functionMap.values()) {
			boolean found = false;
			findNext: for (int i = 0; i < names.length; i++) {
				if (names[i].equals(temp.getName())) {
					found = true;
					break findNext;
				}
			}
			if (false == found) {
				result.add(temp);
			}
		}
		return result;
	}

	private void removeListOfGraphs(ArrayList<GraphFunction> toDelete) {
		for (GraphFunction temp : toDelete) {
			xysc.removeSeries(temp.getSeries());
			functionMap.remove(temp.getName());
		}
	}
}
