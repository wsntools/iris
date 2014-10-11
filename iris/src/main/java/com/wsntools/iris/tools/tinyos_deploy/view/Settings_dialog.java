/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.tools.tinyos_deploy.controller.AbstractController;


public class Settings_dialog extends JDialog {
	private static final long serialVersionUID = 7410684169875744254L;
	private JButton close_btn;
	private JButton save_btn;
	
	private JPanel btn_panel;
	
	private JPanel app_path_panel;
	private JLabel app_path_label;
	private JTextField app_path_txtfield;
	private JButton app_path_btn;
	
	private AbstractController cont;
	
	public Settings_dialog(AbstractController cont) {
		super();
		this.cont = cont;
		this.setLayout(new GridLayout(5,1));
		
		app_path_label = new JLabel("Application directory");
		app_path_txtfield = new JTextField();
		app_path_txtfield.setText(cont.getModel().getTinyos_apps_path()==null?"":cont.getModel().getTinyos_apps_path());
		
		app_path_btn = new JButton("...");
		app_path_btn.addActionListener(cont);
		app_path_panel = new JPanel(new BorderLayout());
		app_path_panel.add(app_path_txtfield,BorderLayout.CENTER);
		app_path_panel.add(app_path_btn,BorderLayout.EAST);
		
		save_btn = new JButton("Save");
		save_btn.addActionListener(cont);
		close_btn = new JButton("Close");
		close_btn.addActionListener(cont);
		
		btn_panel = new JPanel();
		btn_panel.setLayout(new GridLayout(1,2));
		btn_panel.add(save_btn);
		btn_panel.add(close_btn);
		
		this.add(app_path_label);
		this.add(app_path_panel);
		this.add(btn_panel);
		
		this.pack();		
		this.setVisible(true);
	}

	public JButton getClose_btn() {
		return close_btn;
	}

	public JButton getSave_btn() {
		return save_btn;
	}

	public JTextField getApp_path_txtfield() {
		return app_path_txtfield;
	}

	public JButton getApp_path_btn() {
		return app_path_btn;
	}


}
