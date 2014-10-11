/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.views;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.extensions.RMT_MenuBar;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.panels.PanelMeasureInfo;
import com.wsntools.iris.panels.PanelTestBarPacketInsertion;
import com.wsntools.iris.panels.PanelToolBar;
import com.wsntools.iris.tools.Tools;
import com.wsntools.iris.views.ViewMain.ViewWindowListener;

public class ViewModule extends JFrame {

		private static final long serialVersionUID = 1L;

		private Model model;
		private ViewModule ref = this;

		private IRIS_GUIModule guiModule;		
		
		//--Windowlistener--
		private ViewWindowListener listenerWindow = new ViewWindowListener();
		
		//--Constructor--
		public ViewModule(Model m, IRIS_GUIModule module) {

			model = m;
			guiModule = module;
			
			//Menubar
			if(module.getRelatedMenuBarEntries() != null && module.getRelatedMenuBarEntries().length > 0) {
				JMenuBar menuBar = new JMenuBar();
				JMenu moduleMenu = new JMenu(module.getModuleName());
				menuBar.add(moduleMenu);
				
				String[] moduleEntries = module.getRelatedMenuBarEntries();
				JMenuItem[] moduleMenuItems = new JMenuItem[moduleEntries.length];
				ActionListener moduleListener = module.getMenuBarActionListener();
				
				for(int i=0; i<moduleEntries.length; i++) {
					moduleMenuItems[i] = new JMenuItem(moduleEntries[i]);
					moduleMenu.add(moduleMenuItems[i]);
					moduleMenuItems[i].addActionListener(moduleListener);			
				}
				
				this.setJMenuBar(menuBar);
			}		
			
			//Windowsettings
			this.setTitle("IRIS - " + module.getModuleName());
			//this.setResizable(false);
			this.setContentPane(module.getGUIPanel());
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(listenerWindow);
			this.pack();

			//Set windowposition to center
			Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
			this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
					(tk.getScreenSize().height / 2 - this.getHeight() / 2));
			this.setLocation(((this.getX() < 0) ? 0 : this.getX()), ((this.getY() < 0) ? 0 : this.getY()));
			this.setVisible(true);
		}
		
		//Inner class to handle window operations
		class ViewWindowListener implements WindowListener {

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowClosing(WindowEvent arg0) {
				guiModule.getModuleSettings().setActive(false);
				model.applyGUIModuleSettings();
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
