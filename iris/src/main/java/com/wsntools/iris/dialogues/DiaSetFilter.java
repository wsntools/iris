/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.panels.PanelFilter;

public class DiaSetFilter extends JDialog {

	private Model model;
	private DiaSetFilter ref = this;
	
	private Map<IRIS_Attribute, List<float[]>> newMapFilter, oldMapFilter;
	private ArrayList<IRIS_Attribute> listAttributeOrder;
	
	private JPanel panelMain = new JPanel(new BorderLayout());

	private PanelFilter panelFilter; 
	
	private JPanel panelButtons = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	//private JButton butHelp = new JButton(new ImageIcon(Constants.getResource(
	//		Constants.getPathPicsButtons() + Constants.getNameBtnHelp())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	private DiaSetFilter(Model m, Map<IRIS_Attribute, List<float[]>> filter) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		
		oldMapFilter = filter;
		newMapFilter = new HashMap<IRIS_Attribute, List<float[]>>();
		listAttributeOrder = new ArrayList<IRIS_Attribute>();
		for(IRIS_Attribute key:filter.keySet()) {
			newMapFilter.put(key, filter.get(key));
			listAttributeOrder.add(key);
		}
		
		panelFilter = new PanelFilter(model, filter);
		
		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButtons.add(butOK);
		panelButtons.add(butCancel);
		
				
		panelMain.add(panelFilter, BorderLayout.CENTER);
		panelMain.add(panelButtons, BorderLayout.SOUTH);
		
		butCancel.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);
		
		// Windowsettings
		this.setTitle("IRIS - Filter Settings");
		// this.setResizable(false);
		this.setContentPane(panelMain);
		//this.setPreferredSize(new Dimension(450, 300));
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
	
	public Map<IRIS_Attribute, List<float[]>> getFilterSettings() {
		return newMapFilter;
	}
	
	public static Map<IRIS_Attribute, List<float[]>> showFilterSettingWindow(Model m, Map<IRIS_Attribute, List<float[]>> filter) {
		DiaSetFilter diaFilter = new DiaSetFilter(m, filter);
		return diaFilter.getFilterSettings();
	}
	
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if(ae.getSource().equals(butOK)) {
				newMapFilter = panelFilter.getFilterSettings();
				ref.dispose();
			}
			else if(ae.getSource().equals(butCancel)) {
				newMapFilter = oldMapFilter;
				ref.dispose();
			}
			/*else if(ae.getSource().equals(butHelp)) {
				JOptionPane.showMessageDialog(ref, "Allowed expressions are:\n-numbers and ranges separated by a comma (e.g. 2, 7-9, 10)\n-positive and negative numbers (e.g. -2--6)\n-floats (e.g. 4.6, 5.7-8.2)\n-no brackets allowed", "IRIS - Filter Definition", JOptionPane.INFORMATION_MESSAGE);
			}*/
		}
	}	
}
