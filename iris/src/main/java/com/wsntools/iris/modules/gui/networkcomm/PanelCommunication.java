/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.networkcomm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.tinyos.message.MoteIF;
import net.tinyos.util.PrintStreamMessenger;

import org.apache.log4j.Logger;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.dialogues.DiaLoggerSettings;
import com.wsntools.iris.dialogues.DiaSelectMessage;
import com.wsntools.iris.dialogues.SendClassConfig;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.tools.datacollector.sending.SendingConfiguration;

public class PanelCommunication extends JPanel implements IRIS_Observer {
	
	private Model model;
	private final static Logger logger = Logger.getLogger("Sender");
	private SendClassConfig scc;

//	private JButton butDDsRef = new JButton(new ImageIcon(
//			Constants.getResource(Constants.getPathPicsButtons()
//					+ Constants.getNameBtnRecord())));
	private JPanel panelIn = new JPanel();
	private JButton butRecord = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnRecord())));
	private JButton butConfigInput = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSetup())));
	
	private JPanel panelOut = new JPanel();
	private JButton butSendMessage = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSend())));
	private JButton butSetupMessage = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSetup())));
	
	private JPanel panelLog = new JPanel();
	private JButton butActivateLogger = new JButton("Logging");
	private JButton butConfigLogger = new JButton(new ImageIcon(
			Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnSetup())));
	
	private GUIListener listenerGUI = new GUIListener();
	
	public PanelCommunication(Model m) {
		model = m;

//		butDDsRef.setPreferredSize(new Dimension(28, 28));
		butRecord.setPreferredSize(new Dimension(28, 28));
		butConfigInput.setPreferredSize(new Dimension(28, 28));
		butSetupMessage.setPreferredSize(new Dimension(28, 28));
		butSendMessage.setPreferredSize(new Dimension(28, 28));
		//butActivateLogger.setPreferredSize(new Dimension(28, 28));
		butConfigLogger.setPreferredSize(new Dimension(28, 28));

//		butDDsRef.setToolTipText("Connects to the PLANET communication system");
		butRecord.setToolTipText("Starts/stops the measurement of received packages");
		butConfigInput.setToolTipText("Configure details for recording incoming data");
		butSendMessage.setToolTipText("Send the previously selected message");
		butSetupMessage.setToolTipText("Configure the message you want to send into the network");
		butActivateLogger.setToolTipText("Activates/stops the event logger for packet reception");
		butConfigLogger.setToolTipText("Configure the event logger for the current measurement");

		if (model.getDataCollector() == null || !model.getDataCollector().isActive()) {
			butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
					.getPathPicsButtons() + Constants.getNameBtnRecord())));
		} else {
			butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
					.getPathPicsButtons() + Constants.getNameBtnStop())));
		}
		
		panelIn.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panelIn.add(butRecord);
		panelIn.add(butConfigInput);
		panelIn.setBorder(BorderFactory.createTitledBorder("In"));
		
		panelOut.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panelOut.add(butSendMessage);
		panelOut.add(butSetupMessage);
		panelOut.setBorder(BorderFactory.createTitledBorder("Out"));
		
		panelLog.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panelLog.add(butActivateLogger);
		panelLog.add(butConfigLogger);
		panelLog.setBorder(BorderFactory.createTitledBorder("Log"));
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(panelIn);
		this.add(panelOut);
		this.add(panelLog);
		
//		butDDsRef.addActionListener(listenerGUI);
		butRecord.addActionListener(listenerGUI);
		butConfigInput.addActionListener(listenerGUI);
		butSendMessage.addActionListener(listenerGUI);
		butSetupMessage.addActionListener(listenerGUI);
		butActivateLogger.addActionListener(listenerGUI);
		butConfigLogger.addActionListener(listenerGUI);
	}	
	
	class GUIListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			
			Object o = ae.getSource();
//			if(o.equals(butDDsRef)) {
//				ImacSensorPositionDataPublisher.getInstance();
//			}
			
			if(o.equals(butRecord)) {
				if (!model.isDataCollectorActive()) {
					model.startRecording();
					butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
							.getPathPicsButtons() + Constants.getNameBtnStop())));
				}
				else  {	
					model.stopRecording();
					butRecord.setIcon(new ImageIcon(Constants.getResource(Constants
							.getPathPicsButtons() + Constants.getNameBtnRecord())));
				}
			}
			
			else if(o.equals(butConfigInput)) {
				model.getDataCollector().buildNewListener();
			}
			
			else if(o.equals(butSendMessage)) {
				SendingConfiguration[] configs = model.getSendingConfigurations();
				boolean sendSuccess;
				if(configs.length == 0) {
					sendSuccess = model.setupSendingManager();
				}
				else {
					SendingConfiguration conf = DiaSelectMessage.showMessageSelectionWindow(model, configs);
					sendSuccess = model.sendConfiguration(conf);
				}
				
				//Status report
				if(sendSuccess) {
					JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(), "Message has been sent", "IRIS - Send Success", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(), "Message has NOT been sent", "IRIS - Send Fail", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				/*
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
				*/
			}
			
			else if(o.equals(butSetupMessage)) {
				model.setupSendingManager();
				/*
				if (scc == null)
					scc = new SendClassConfig(model.getMeasurement(model
							.getCurrentMeasureIndex()));
				scc.display(model.getCurrentMeasurement());
				*/
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

	@Override
	public void updateNewMeasure() {
		butActivateLogger.setBackground(null);
	}

	@Override
	public void updateNewPacket() {}

	@Override
	public void updateNewAttribute() {}
}
