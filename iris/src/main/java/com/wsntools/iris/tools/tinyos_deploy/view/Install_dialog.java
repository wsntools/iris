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


public class Install_dialog extends JDialog {
	private static final long serialVersionUID = 7410684169875744254L;
	private JButton close_btn;
	private JButton run_btn;
	
	private JPanel hw_file_panel;
	private JPanel app_file_panel;
	private JPanel btn_panel;
	
	private JLabel hw_lbl;
	private JLabel app_lbl;
	
	private JTextField hw_file_txt;
	private JTextField app_file_txt;
	
	private JButton app_choosefile_btn;
	private JButton hw_choosefile_btn;
	private AbstractController cont;
	
	public Install_dialog(AbstractController cont) {
		super();
		this.cont = cont;
		this.setLayout(new GridLayout(5,1));
		hw_lbl = new JLabel("Hardware Config:");
		app_lbl = new JLabel("Application Config:");
		
		hw_file_txt = new JTextField();
		if (cont.getModel().getHw_lastpath() != null)
				hw_file_txt.setText(cont.getModel().getHw_lastpath().getAbsolutePath());
		app_file_txt = new JTextField();
		if (cont.getModel().getApp_lastpath() != null)
			app_file_txt.setText(cont.getModel().getApp_lastpath().getAbsolutePath());
		
		hw_choosefile_btn = new JButton("...");
		hw_choosefile_btn.addActionListener(cont);
		app_choosefile_btn = new JButton("...");
		app_choosefile_btn.addActionListener(cont);

		hw_file_panel = new JPanel();
		hw_file_panel.setLayout(new BorderLayout());
		hw_file_panel.add(hw_file_txt,BorderLayout.CENTER);
//		hw_file_panel.add(hw_choosefile_btn,BorderLayout.EAST);
		
		app_file_panel = new JPanel();
		app_file_panel.setLayout(new BorderLayout());
		app_file_panel.add(app_file_txt,BorderLayout.CENTER);
//		app_file_panel.add(app_choosefile_btn,BorderLayout.EAST);
		
		run_btn = new JButton("Run");
		run_btn.addActionListener(cont);
		close_btn = new JButton("Close");
		close_btn.addActionListener(cont);
		
		btn_panel = new JPanel();
		btn_panel.setLayout(new GridLayout(1,2));
		btn_panel.add(run_btn);
		btn_panel.add(close_btn);
		
		this.add(hw_lbl);
		this.add(hw_file_panel);
		this.add(app_lbl);
		this.add(app_file_panel);
		this.add(btn_panel);
		
		this.pack();
		
//		this.setModal(true);
		this.setVisible(true);
	}

	public JButton getClose_btn() {
		return close_btn;
	}

	public JButton getRun_btn() {
		return run_btn;
	}

	public JButton getApp_choosefile_btn() {
		return app_choosefile_btn;
	}

	public JButton getHw_choosefile_btn() {
		return hw_choosefile_btn;
	}

	public JTextField getHw_file_txt() {
		return hw_file_txt;
	}

	public JTextField getApp_file_txt() {
		return app_file_txt;
	}
}
