/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.GUIModuleSettings;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class DiaSelectGUI extends JDialog {

	private Model model;
	private DiaSelectGUI ref = this;
	
	private JPanel panelMain = new JPanel(new BorderLayout());
	
	private GUISelectionTile[] arrGUISelectionTiles;
	private JScrollPane scrollGUIModules;
	private JPanel panelGUIModules = new JPanel();
	
	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	private DiaSelectGUI(Model m) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		
		panelGUIModules.setLayout(new BoxLayout(panelGUIModules, BoxLayout.Y_AXIS));
		//Get all available GUI elements and list them for selection
		IRIS_GUIModule[] modules = model.getGUIModules();
		arrGUISelectionTiles = new GUISelectionTile[modules.length];
		for(int i=0; i<modules.length; i++) {
			arrGUISelectionTiles[i] = new GUISelectionTile(modules[i].getModuleSettings());
			panelGUIModules.add(arrGUISelectionTiles[i]);
		}
		scrollGUIModules = new JScrollPane(panelGUIModules);
		scrollGUIModules.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollGUIModules.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollGUIModules.getVerticalScrollBar().setValue(0);
		
		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);
		panelButton.add(butCancel);
		
		panelMain.add(scrollGUIModules, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);
		
		butCancel.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);
		
		// Windowsettings
		this.setTitle("IRIS - GUI Module Selection");
		// this.setResizable(false);
		this.setContentPane(panelMain);
		this.setPreferredSize(new Dimension(400, 400));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();

		// Set windowposition to center
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2),
				(tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()),
				((this.getY() < 0) ? 0 : this.getY()));
		this.setVisible(true);
	}
	
	public static void showGUISelectionWindow(Model m) {
		new DiaSelectGUI(m);
	}
	
	private class GUISelectionTile extends JPanel {
		
		private IRIS_GUIModule guiModule;
		private JCheckBox checkGUIModuleSelected;
		
		private JPanel panelCheck;
		private JCheckBox checkRegisterObserver;
		private JCheckBox checkWindowed;
		
		public GUISelectionTile(GUIModuleSettings settings) {
			
			guiModule = settings.getGUIModule();			
			JTextArea textGUIModuleDesc = new JTextArea(guiModule.getModuleDescription());
			textGUIModuleDesc.setWrapStyleWord(true);
			textGUIModuleDesc.setLineWrap(true);
			textGUIModuleDesc.setEditable(false);
			
			checkGUIModuleSelected = new JCheckBox(guiModule.getModuleName(), settings.isActive());
			checkRegisterObserver = new JCheckBox("Auto-Update", settings.isRegisteredAsObserver());
			checkWindowed = new JCheckBox("Separate Window", settings.isWindowed());
			
			checkGUIModuleSelected.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					panelCheck.setVisible(checkGUIModuleSelected.isSelected());			
				}
			});
			
			this.setLayout(new BorderLayout());
			this.add(checkGUIModuleSelected, BorderLayout.NORTH);
			this.add(textGUIModuleDesc, BorderLayout.CENTER);
			
			panelCheck = new JPanel(new java.awt.GridLayout(2 - ((guiModule.getModuleObserver() == null) ? 1 : 0),1));
			if(guiModule.getModuleObserver() != null) {
				
				checkRegisterObserver.setHorizontalTextPosition(SwingConstants.LEFT);
				checkRegisterObserver.setHorizontalAlignment(SwingConstants.RIGHT);
				panelCheck.add(checkRegisterObserver);				
				
			}
			checkWindowed.setHorizontalTextPosition(SwingConstants.LEFT);
			checkWindowed.setHorizontalAlignment(SwingConstants.RIGHT);
			panelCheck.add(checkWindowed);
			panelCheck.setVisible(checkGUIModuleSelected.isSelected());
			
			this.add(panelCheck, BorderLayout.SOUTH);
			
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		public IRIS_GUIModule getGUIModule() {
			return guiModule;
		}
		
		public boolean isModuleSelected() {
			return checkGUIModuleSelected.isSelected();
		}
		
		public boolean isModuleRegisteredAsObserver() {
			return checkRegisterObserver.isSelected();
		}
		
		public boolean isWindowed() {
			return checkWindowed.isSelected();
		}
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if(ae.getSource().equals(butOK)) {
				//Apply made settings		
				for(GUISelectionTile gst: arrGUISelectionTiles) {
					GUIModuleSettings settings = gst.guiModule.getModuleSettings();
					settings.setActive(gst.isModuleSelected());
					settings.setRegisteredAsObserver(gst.isModuleRegisteredAsObserver());
					settings.setWindowed(gst.isWindowed());
				}
				model.applyGUIModuleSettings();
				ref.dispose();
			}
			else if(ae.getSource().equals(butCancel)) {
				ref.dispose();
			}
		}
	}
}
