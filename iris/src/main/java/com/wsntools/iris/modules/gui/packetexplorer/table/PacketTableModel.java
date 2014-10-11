/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.packetexplorer.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Provides a model for the packageview
 * @author Sascha Hevelke
 *
 */
	public class PacketTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 8642625925142880300L;
		
		private int columnCount;
		private List<String> columnNames;
		private List<List<Object>> measurementdata;
		
		/**
		 * Creates new MeasurementModel
		 * @param columnCount Number of columns in the model
		 */
		public PacketTableModel(int columnCount) {
			this.columnNames = new ArrayList<String>(2);
			this.columnCount = columnCount;
			this.measurementdata = new ArrayList<>();
		} 
		
		/**
		 * Sets the names for the columns of the table. Checks if the provided array has enough entries 
		 * @param names Labels for the columns
		 */
		public void setColumnNames(List<String> names) {
			if (names.size()!=columnCount) {
				throw new IllegalArgumentException("The provided array has "+names.size()+" values, but the table contains "+columnCount+" columns!");
			}
			this.columnNames = names;
		}
		
		/**
		 * Adds a new row to the table
		 * @param sample List<Object> of all data of one row
		 */
		public void addMeasurement(List<Object> sample) {
			measurementdata.add(sample);
		}

		/**
		 * Returns the number of columns of the table.
		 * @return count of columns
		 */
	    public int getColumnCount() {
	        return columnNames.size();
	    }

	    /**
	     * returns the current number of rows in this table
	     */
	    public int getRowCount() {
	        return measurementdata.size();
	    }

	    /**
	     * returns the label of the column identified by "col"
	     * @param col number of the column
	     * @return columnname
	     */
	    public String getColumnName(int col) {
	        return columnNames.get(col);
	    }

	    /**
	     * Returns the Object at the specified location in the table
	     * @param row row of the location
	     * @param col column of the location
	     * @return Object at the location
	     */
	    public Object getValueAt(int row, int col) {
//	    	if (row<measurementdata.size() && col<measurementdata.get(0).size())
	    		return measurementdata.get(row).get(col);
//	    	return null;
	    }
	    /**
	     * returns the class of the object at the given column
	     * @param c index of the column
	     * @return class of the column
	     */
	    public Class<?> getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }
	    
	    /**
	     * Getter for the String array containing all columnnames
	     * @return String-array of columnnames
	     */
	    public List<String> getColumnNames() {
	    	return columnNames;
	    }
	    
	    
	    
	}