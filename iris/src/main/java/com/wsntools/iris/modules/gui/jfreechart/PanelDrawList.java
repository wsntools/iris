/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.jfreechart;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_Observer;
/**
 * @author Sascha Jungen
 */
public class PanelDrawList extends JScrollPane implements IRIS_Observer {

	private static final long serialVersionUID = 1L;
	
	private Model model;
	private int lastindex = 0;
	
	private static DefaultListModel listModel = new DefaultListModel();
	private static JList listDrawnFuncs = new JList(listModel);
	
	private ArrayList<IRIS_Attribute> listAttr = new ArrayList<IRIS_Attribute>();
	private ArrayList<Object> listSets = new ArrayList<Object>();
	
	private ListListener listenerList = new ListListener();
	
	private IRIS_Attribute yAxis;
	
	
	public PanelDrawList(Model m) {
		super(listDrawnFuncs);
		model = m;
		
		m.registerObserver(this);
		
		listDrawnFuncs.setVisibleRowCount(3);
		listDrawnFuncs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		

		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setBorder(BorderFactory.createTitledBorder("Drawn values"));
		this.setPreferredSize(new Dimension(250,100));		
		
		setListListener(listenerList);
	}
	
	public IRIS_Attribute getYAxis(){
		return yAxis;
	}
	
	public int getListItemCount() {
		
		return listModel.getSize();
	}
	public String getListItemName(int i) {
		
		return listModel.get(i).toString();
	}
	public IRIS_Attribute getListItem(int i) {
		
		return listAttr.get(i);
	}
	public Object getListItemSetting(int i) {
		
		return listSets.get(i);
	}
	public void addListItem(IRIS_Attribute attr, int[] range) {
		
		listAttr.add(attr);
		listSets.add(range);
		String name = attr.getAttributeName() + (range != null ? (" ( P#" + range[0] + "-" + range[1] + " )" ) : "" );
		listModel.addElement(name);
	}
	public void addListItem(IRIS_Attribute attr, Map<IRIS_Attribute, List<float[]>> filter, IRIS_Attribute yAxis) {
		
		listAttr.add(attr);
		listSets.add(filter);
		this.yAxis = yAxis;
		
		String name = attr.getAttributeName();
		if(filter.size() > 0) {
			name += " ( ";
			IRIS_Attribute[] attrs = filter.keySet().toArray(new IRIS_Attribute[filter.size()]);
			String filterValues;
			for(int i=0; i<attrs.length; i++) {
				name += attrs[i].getAttributeName() + "= ";
				filterValues = "";
				for(float[] arr: filter.get(attrs[i])) {
					name += Arrays.toString(arr);
				}
			}
			name += " )";
		}
		listModel.addElement(name);
	}

	public void removeSelectedItem() {
		
		if(!listModel.isEmpty() && lastindex != -1) {			
			listSets.remove(lastindex);
			listAttr.remove(lastindex);
			listModel.removeElementAt(lastindex);
			lastindex = -1;
		}
	}
	
	public static void setListListener(ListListener l) {
		listDrawnFuncs.addListSelectionListener(l);
	}

	
	@Override
	public void updateNewMeasure() {


	}

	@Override
	public void updateNewPacket() {		
		
	}

	@Override
	public void updateNewAttribute() {		
		//Check if all listed attributes still exist
		for(int i=0; i<listAttr.size(); i++) {
			if(model.getMeasureAttribute(listAttr.get(i).getAttributeName(), true) == null) {
				listModel.removeElementAt(i);
				listSets.remove(i);
				listAttr.remove(i--);
			}
		}
	}
	
	
	
	public class ListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			
			if(listDrawnFuncs.getSelectedIndex() != -1)
				lastindex = listDrawnFuncs.getSelectedIndex();
		}
		
		
	}
}
