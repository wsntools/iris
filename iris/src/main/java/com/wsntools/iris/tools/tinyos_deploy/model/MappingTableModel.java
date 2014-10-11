/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;
/**
 * Table Model for the mapping table
 * @author Sascha Hevelke
 *
 */
public class MappingTableModel extends AbstractTableModel {
	
	private String[] columns = new String[]{"Hardware","ID","Application"};
	private List<String[]> rows;
	
	public MappingTableModel(List<String[]> rows) {
		this.rows = rows;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return rows.get(arg0)[arg1];
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		rows.get(rowIndex)[columnIndex] = (String)aValue;
		this.fireTableCellUpdated(rowIndex, columnIndex);
		}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
//		return false;
	}
	/**
	 * Adds a row to the TableModel
	 * @param row row to be added
	 */
	public void addRow(String[] row) {
		rows.add(row);
		this.fireTableDataChanged();
	}

	/**
	 * Getter for all rows of the model
	 * @return List of rows
	 */
	public List<String[]> getRows() {
		return rows;
	}
}
