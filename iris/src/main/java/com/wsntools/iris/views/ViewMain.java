/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaSelectGUI;
import com.wsntools.iris.extensions.IrisMenuBar;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.panels.PanelMeasureInfo;
import com.wsntools.iris.panels.PanelToolBar;
import com.wsntools.iris.tools.Tools;

/**
 * @author Sascha Jungen
 * 
 */
public class ViewMain extends JFrame {

	private static final long serialVersionUID = 1L;

	private Model model;
	private ViewMain ref = this;

	private List<IRIS_GUIModule> displayedModules;
	
	//--GUI-Subframes--
	private Map<IRIS_GUIModule, ViewModule> guiSubframes;
	//TODO Add also Function Window
	
	//--Menubar--
	private IrisMenuBar menuBar;

	//--Panel--
	private JPanel panelMain = new JPanel(new BorderLayout());

	private JPanel panelUpper = new JPanel();
	private PanelToolBar panelTool;
	
	private JPanel panelGUIElements = new JPanel();
	
	// Panel displaying information about selected packages
	private PanelMeasureInfo panelMeasureInfo;

	//--Windowlistener--
	private ViewWindowListener listenerWindow = new ViewWindowListener();
	
	//--Constructor--
	public ViewMain(Model m) {
		model = m;

		//Object init
		displayedModules = new ArrayList<IRIS_GUIModule>();
		guiSubframes = new HashMap<IRIS_GUIModule, ViewModule>();
		
		panelTool = new PanelToolBar(model);
		panelMeasureInfo = new PanelMeasureInfo(model, false);

		//Menubar
		menuBar = new IrisMenuBar(model);
		this.setJMenuBar(menuBar);

		//Paneldesign
		panelUpper.add(panelTool);
		
		panelGUIElements.setLayout(new BoxLayout(panelGUIElements, BoxLayout.X_AXIS));
		
		panelMain.add(panelUpper, BorderLayout.NORTH);
		panelMain.add(panelGUIElements, BorderLayout.CENTER);
		panelMain.add(panelMeasureInfo, BorderLayout.SOUTH);
		
		updateGUIModules();


		//Windowsettings
		this.setTitle("IRIS");
		//this.setResizable(false);
		this.setContentPane(panelMain);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(listenerWindow);
		this.pack();

		//Set windowposition to center
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
				(tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()), ((this.getY() < 0) ? 0 : this.getY()));
		this.setVisible(true);

		//Register window to model
		model.setView(this);
		
		//Start with GUI Module choice
		DiaSelectGUI.showGUISelectionWindow(model);

	}
	
	public void SafeCloseOperation(boolean force) {
		int conf = 1;
		if (false == force){
		conf = JOptionPane.showConfirmDialog(model.getView(), "Do you really want to close this application?",
				"Close IRIS", JOptionPane.YES_NO_OPTION);
		}
		if (force == true || conf == 0) {
			//Close data connection if open
			model.stopRecording();

			//Close all subframes
			for(JFrame frame: guiSubframes.values()) frame.dispose();
			
			ref.dispose();
			//TODO HACK
			System.exit(0);
		}
	}

	public void SafeCloseOperation() {
		SafeCloseOperation(false);
	}
	
	public void updateGUIModules() {
		
		//Diff to find out new and obsolete Modules
		List<IRIS_GUIModule> obsoleteModules = Tools.listDiff(displayedModules, model.getDisplayedGUIModules());
		List<IRIS_GUIModule> newModules = Tools.listDiff(model.getDisplayedGUIModules(), displayedModules);
		List<IRIS_GUIModule> unchangedModules = Tools.listIntersect(model.getDisplayedGUIModules(), displayedModules);
		
		//Remove old modules
		for(IRIS_GUIModule gm: obsoleteModules) {
			if(gm.getModuleSettings().isWindowed()) {
				guiSubframes.get(gm).dispose();
				guiSubframes.remove(gm);
			}
			else {
				if(gm.isToolbarOnly()) {
					panelUpper.remove(gm.getGUIPanel());
				}
				else {
					panelGUIElements.remove(gm.getGUIPanel());
				}			
				menuBar.removeGUIModuleMenuBar(gm);
				panelMeasureInfo.removeGUIModuleInfo(gm);
			}
		}
		
		//Add new modules
		for(IRIS_GUIModule gm: newModules) {
			if(gm.getModuleSettings().isWindowed()) {
				guiSubframes.put(gm, new ViewModule(model, gm));
			}
			else {
				if(gm.isToolbarOnly()) {
					panelUpper.add(gm.getGUIPanel());
				}
				else {
					panelGUIElements.add(gm.getGUIPanel());
				}
				menuBar.addGUIModuleMenuBar(gm);				
				panelMeasureInfo.addGUIModuleInfo(gm);
			}
		}
		
		//Check unchanged modules
		for(IRIS_GUIModule gm: unchangedModules) {
			//Switch from window to integration or the other way around
			if(gm.getModuleSettings().isWindowed() && (guiSubframes.get(gm) == null)) {
				guiSubframes.put(gm, new ViewModule(model, gm));
				panelUpper.remove(gm.getGUIPanel());
				panelGUIElements.remove(gm.getGUIPanel());
				menuBar.removeGUIModuleMenuBar(gm);
				panelMeasureInfo.removeGUIModuleInfo(gm);
			}
			else if(!gm.getModuleSettings().isWindowed() && (guiSubframes.get(gm) != null)) {
				guiSubframes.get(gm).dispose();
				guiSubframes.remove(gm);
				if(gm.isToolbarOnly()) {
					panelUpper.add(gm.getGUIPanel());
				}
				else {
					panelGUIElements.add(gm.getGUIPanel());
				}
				menuBar.addGUIModuleMenuBar(gm);
				panelMeasureInfo.addGUIModuleInfo(gm);
			}
			
			//Also adjust measure info
			if(gm.getModuleSettings().isWindowed()) {
				guiSubframes.get(gm).setMeasureInfo(gm.getModuleSettings().isDisplayingInformation());
			}
			else {
				if(gm.getModuleSettings().isDisplayingInformation()) panelMeasureInfo.addGUIModuleInfo(gm);
				else panelMeasureInfo.removeGUIModuleInfo(gm);
			}
		}
		displayedModules.clear();
		displayedModules.addAll(model.getDisplayedGUIModules());
		
		//Set windowposition to center
		this.pack();
		
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
				(tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()), ((this.getY() < 0) ? 0 : this.getY()));
			
	}
	
	public JFrame getCurrentlyFocusedWindow() {
		
		if(this.isFocusOwner()) return this;
		else {
			for(JFrame frame: guiSubframes.values()) {
				if(frame.isFocusOwner()) return frame;
			}
		}
		
		//If nothing has the focus, use this window as default
		return this;
	}

	//Inner class to handle window operations
	class ViewWindowListener implements WindowListener {

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowClosing(WindowEvent arg0) {
			SafeCloseOperation();
		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowOpened(WindowEvent arg0) {
		}

	}

}
