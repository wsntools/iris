/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.misc.listener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.wsntools.iris.data.Model;


public class SubWindowListener implements WindowListener {
	
	private Model model;
	
	
	public SubWindowListener(Model m) {
		model = m;
	}
	
	
	public void windowClosing(WindowEvent arg0) {		
	}
	public void windowActivated(WindowEvent arg0) {
	}
	public void windowClosed(WindowEvent arg0) {
		model.getView().setEnabled(true);
		model.getView().requestFocus();	
	}
	public void windowDeactivated(WindowEvent arg0) {
		((JFrame)arg0.getSource()).requestFocus();
	}
	public void windowDeiconified(WindowEvent arg0) {
	}
	public void windowIconified(WindowEvent arg0) {
	}
	public void windowOpened(WindowEvent arg0) {
		model.getView().setEnabled(false);
	}

}
