/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.modules.gui.tinyos_deploy.controller.AbstractController;

public class Open_project_dialog extends JDialog {
	private static final long serialVersionUID = 7410684169875744254L;
	private JButton close_btn;
	private JButton open_btn;

	private JPanel project_file_panel;
	private JPanel btn_panel;

	private JLabel project_lbl;

	private JTextField project_file_txt;

	private JButton project_choosefile_btn;
	private AbstractController cont;

	public Open_project_dialog(AbstractController cont) {
		super();
		this.setTitle("Open..");
		this.cont = cont;
		this.setLayout(new GridLayout(5, 1));
		project_lbl = new JLabel("Hardware Config:");

		project_file_txt = new JTextField();
		// if (cont.getModel().getHw_lastpath() != null)
		// project_file_txt.setText(cont.getModel().getHw_lastpath().getAbsolutePath());
		project_choosefile_btn = new JButton("...");
		project_choosefile_btn.addActionListener(cont);

		project_file_panel = new JPanel();
		project_file_panel.setLayout(new BorderLayout());
		project_file_panel.add(project_file_txt, BorderLayout.CENTER);
		project_file_panel.add(project_choosefile_btn,BorderLayout.EAST);

		open_btn = new JButton("Open");
		open_btn.addActionListener(cont);
		close_btn = new JButton("Close");
		close_btn.addActionListener(cont);

		btn_panel = new JPanel();
		btn_panel.setLayout(new GridLayout(1, 2));
		btn_panel.add(open_btn);
		btn_panel.add(close_btn);

		this.add(project_lbl);
		this.add(project_file_panel);
		this.add(btn_panel);

		this.pack();

		// this.setModal(true);
		this.setVisible(true);
	}

	public JButton getClose_btn() {
		return close_btn;
	}

	public JButton getOpen_btn() {
		return open_btn;
	}

	public JButton getProject_choosefile_btn() {
		return project_choosefile_btn;
	}

	public JTextField getProject_file_txt() {
		return project_file_txt;
	}

	public void setProject_file_txt(JTextField hw_file_txt) {
		this.project_file_txt = hw_file_txt;
	}

}
