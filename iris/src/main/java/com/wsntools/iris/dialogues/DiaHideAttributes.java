/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;

public class DiaHideAttributes extends JDialog {
	
	private Model model;
	private DiaHideAttributes ref = this;
	
	// Hidewindow Settings
	private JPanel panelMain = new JPanel(new BorderLayout());
	private JPanel panelMap;
	private PanelCheckbox[] arrPanelCheckbox;
	private boolean[] resultHideAttr;
	
	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butSelectAll = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons()
			+ Constants.getNameBtnSelectAll())));
	private JButton butSelectNone  = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons()
			+ Constants.getNameBtnSelectNone())));
	
	private ButtonListener listenerButtons = new ButtonListener();

	private DiaHideAttributes(Model m, IRIS_Attribute[] attr, boolean[] checked) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;

		arrPanelCheckbox = new PanelCheckbox[attr.length];
		for (int i = 0; i < attr.length; i++) {
			arrPanelCheckbox[i] = new PanelCheckbox(attr[i].getAttributeName(),
					checked[i]);
		}

		// Add all subpanels to the complete panel
		panelMap = new JPanel(new GridLayout(5, 1));
		for (int i = 0; i < arrPanelCheckbox.length; i++) {
			panelMap.add(arrPanelCheckbox[i]);
		}

		butSelectAll.setPreferredSize(new Dimension(28, 28));
		butSelectNone.setPreferredSize(new Dimension(28, 28));
		butOK.setPreferredSize(new Dimension(28, 28));

		panelButton.add(butSelectAll);
		panelButton.add(butSelectNone);
		panelButton.add(butOK);

		panelMain.add(panelMap, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		// Add actionlistener
		butSelectAll.addActionListener(listenerButtons);
		butSelectNone.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);

		// Windowsettings
		this.setTitle("IRIS - Show Attributes");
		// this.setResizable(false);
		this.setContentPane(panelMain);
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
	
	public boolean[] getHideAttributeResults() {

		return resultHideAttr;
	}
	
	public static boolean[] showHideAttibuteWindow(Model m, IRIS_Attribute[] attr, boolean[] checked) {
		DiaHideAttributes vmap = new DiaHideAttributes(m, attr, checked);
		return vmap.getHideAttributeResults();
	}
	
	
	private class PanelCheckbox extends JPanel {

		private static final long serialVersionUID = 1L;

		private JCheckBox check;
		
		
		public PanelCheckbox(String name, boolean checked) {
			
			check = new JCheckBox(name);
			check.setSelected(checked);
			
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.add(check);		
		}
		
		public void setValueChecked(boolean val) {
			
			check.setSelected(val);
		}
		public boolean isValueChecked() {
			
			return check.isSelected();
		}
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// Apply mapping changes
			if (ae.getSource().equals(butOK)) {			
				// Get all checkbox values
				resultHideAttr = new boolean[arrPanelCheckbox.length];
				for (int i = 0; i < arrPanelCheckbox.length; i++) {
					resultHideAttr[i] = arrPanelCheckbox[i]
							.isValueChecked();
				}
				ref.dispose();
			}
			else if (ae.getSource().equals(butSelectAll)) {
				for (int i = 0; i < arrPanelCheckbox.length; i++) {
					arrPanelCheckbox[i].setValueChecked(true);
				}
			}
			else if (ae.getSource().equals(butSelectNone)) {
				for (int i = 0; i < arrPanelCheckbox.length; i++) {
					arrPanelCheckbox[i].setValueChecked(false);
				}
			}
		}
	}
}
