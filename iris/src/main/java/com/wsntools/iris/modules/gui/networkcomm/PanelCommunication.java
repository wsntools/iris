/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.networkcomm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.tinyos.message.MoteIF;
import net.tinyos.util.PrintStreamMessenger;

import org.apache.log4j.Logger;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaLoggerSettings;
import com.wsntools.iris.dialogues.SendClassConfig;

public class PanelCommunication extends JPanel {
	
	private Model model;
	private final static Logger logger = Logger.getLogger("Sender");
	private SendClassConfig scc;

//	private JButton butDDsRef = new JButton(new ImageIcon(
//			Constants.getResource(Constants.getPathPicsButtons()
//					+ Constants.getNameBtnRecord())));
	private JButton butRecord = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnRecord())));
	private JButton butSetupMessage = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSetup())));
	private JButton butSendMessage = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSend())));
	private JButton butActivateLogger = new JButton("Logger");
	private JButton butConfigLogger = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSetup())));
	
	private GUIListener listenerGUI = new GUIListener();
	
	public PanelCommunication(Model m) {
		model = m;

//		butDDsRef.setPreferredSize(new Dimension(28, 28));
		butRecord.setPreferredSize(new Dimension(28, 28));
		butSetupMessage.setPreferredSize(new Dimension(28, 28));
		butSendMessage.setPreferredSize(new Dimension(28, 28));
		//butActivateLogger.setPreferredSize(new Dimension(28, 28));
		butConfigLogger.setPreferredSize(new Dimension(28, 28));

//		butDDsRef.setToolTipText("Connects to the PLANET communication system");
		butRecord.setToolTipText("Starts/stops the measurement of received packages");
		butSetupMessage.setToolTipText("Confige the message you want to send into the network");
		butSendMessage.setToolTipText("Send the previously selected message");
		butActivateLogger.setToolTipText("Activates/stops the event logger for packet reception");
		butConfigLogger.setToolTipText("Configure the event logger for the current measurement");

		if (model.getDataCollector() == null || !model.getDataCollector().isActive()) {
			butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
					.getPathPicsButtons() + Constants.getNameBtnRecord())));
		} else {
			butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
					.getPathPicsButtons() + Constants.getNameBtnStop())));
		}
		
//		this.add(butDDsRef);
		this.add(butRecord);
		this.add(butSetupMessage);
		this.add(butSendMessage);
		this.add(butActivateLogger);
		this.add(butConfigLogger);
		butRecord.addActionListener(listenerGUI);
//		butDDsRef.addActionListener(listenerGUI);
		butSetupMessage.addActionListener(listenerGUI);
		butSendMessage.addActionListener(listenerGUI);
		butActivateLogger.addActionListener(listenerGUI);
		butConfigLogger.addActionListener(listenerGUI);
	}
	
	private void recordPressed() {
		if (model.getDataCollector() == null || !model.getDataCollector().isActive()) {
			model.startRecording();
		} else {
			model.stopRecording();
		}
	}	
	
	class GUIListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			
			Object o = ae.getSource();
//			if(o.equals(butDDsRef)) {
//				ImacSensorPositionDataPublisher.getInstance();
//			}
			
			if(o.equals(butRecord)) {
				recordPressed();
				if (model.getDataCollector() == null || !model.getDataCollector().isActive()) {
					butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
							.getPathPicsButtons() + Constants.getNameBtnStop())));
				}
				else  {
					butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
							.getPathPicsButtons() + Constants.getNameBtnRecord())));
				}
			}
			
			else if(o.equals(butSetupMessage)) {
				if (scc == null)
					scc = new SendClassConfig(model.getMeasurement(model
							.getCurrentMeasureIndex()));
				scc.display(model.getCurrentMeasurement());
			}
			
			else if(o.equals(butSendMessage)) {
				logger.info("trying to send Message: "
						+ model.getCurrentMeasurement().getActualMessage()
								.getClass());
				logger.info("\t using address"
						+ model.getCurrentMeasurement().getAddress());

				logger.debug(""
						+ model.getCurrentMeasurement().getActualMessage());

				try {
					MoteIF mote = new MoteIF(PrintStreamMessenger.err);
					mote.send(model.getCurrentMeasurement().getAddress(), model
							.getCurrentMeasurement().getActualMessage());
					model.getCurrentMeasurement().logSendMessage();
					mote.getSource().shutdown();
				} catch (IOException e1) {
					System.err.println("no success");
					e1.printStackTrace();
				}
			}
			
			else if(o.equals(butActivateLogger)) {
				if(model.getEventLogger().isLoggingEnabled()) {
					model.getEventLogger().setLoggingEnabled(false);
					butActivateLogger.setBackground(null);
				}
				else {
					model.getEventLogger().setLoggingEnabled(true);
					butActivateLogger.setBackground(Color.GREEN);
				}
			}			
			
			else if(o.equals(butConfigLogger)) {
				model.getEventLogger().addMeasurementConfiguration(model.getCurrentMeasurement(),
						DiaLoggerSettings.showLoggerSettingsWindow(model, model.getCurrentMeasurement(), model.getEventLogger().getMeasurementConfiguration(model.getCurrentMeasurement())));
			}
			
		}			
	}
}
