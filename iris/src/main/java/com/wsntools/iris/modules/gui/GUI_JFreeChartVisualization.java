/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.jfreechart.PanelGraph;

public class GUI_JFreeChartVisualization extends IRIS_GUIModule {

	private PanelGraph panelGraph;
	
	public GUI_JFreeChartVisualization(Model m) {
		super(m);
		panelGraph = new PanelGraph(m);
		getGUIPanel().setBorder(BorderFactory.createTitledBorder(getModuleName()));
	}

	@Override
	public String getModuleName() {
		
		return "JFreeChart Content Visualization";
	}

	@Override
	public String getModuleDescription() {
		
		return "This module contains visualization tools for displaying packet data in form of"
				+ " a line and a bar chart, as well as different filter tools to restrict the content to be drawn.";
	}

	@Override
	public JPanel getGUIPanel() {
		return panelGraph;
	}
	
	@Override
	public boolean isToolbarOnly() {
		return false;
	}

	@Override
	public IRIS_Observer getModuleObserver() {
		return panelGraph;
	}

	@Override
	public IRIS_ModuleInfo[] getRelatedModuleInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRelatedMenuBarEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionListener getMenuBarActionListener() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AliasAttribute[] getRequiredAliasAttributes() {		
		return null;
	}

}
