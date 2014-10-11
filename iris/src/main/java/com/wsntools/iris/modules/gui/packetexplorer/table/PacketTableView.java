/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.packetexplorer.table;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

/**
 * @author Sascha Hevelke
 */
public class PacketTableView extends JPanel{

	private static final long serialVersionUID = 3579788388034244779L;
	private JScrollPane scrollPane;
	private JTable dataTable;
	private PacketTableModel model;

	/**
	 * Create a new PacketTableView
	 * @param drawpanel JPanel to draw the table onto
	 * @param dataModel data to be displayed by the table
	 */
	public PacketTableView(PacketTableModel dataModel) {
		super();
		this.model = dataModel;
		dataTable = new JTable(model);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dataTable.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(dataTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setLayout(new GridLayout(1,1));
		this.add(scrollPane);
		
	}
	
	/**
	 * Set the new data for the table 
	 * @param m new tablemodel
	 */
	public void updateData(PacketTableModel m) {
		dataTable.setRowSorter(null);
		this.model = m;
		this.dataTable.setModel(model);
		dataTable.setAutoCreateRowSorter(true);
	}
	
	public void setCornerElement(Component c) {
		scrollPane.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, c);
	}
	
}
