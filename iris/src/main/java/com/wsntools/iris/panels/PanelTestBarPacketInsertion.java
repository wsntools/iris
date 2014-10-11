/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;

public class PanelTestBarPacketInsertion extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Model model;	
	
	private JButton butNewPacket = new JButton("New Packet (complete)");
	private JButton butNewPacket2 = new JButton("New Packet (x100 single)");
	private JButton butNewPacket3 = new JButton("New Packet (x100 bundle)");
	private JButton butNewPacket4 = new JButton("New Packet (x1.000 single)");
//	private JComboBox comboMapTo = new JComboBox();

	
	private ButtonListener listenerButtons = new ButtonListener();

	public PanelTestBarPacketInsertion(Model m) {
		
		model = m;
		/*
		 * Init objects
		 */
		
		
		/*
		 * Define layout
		 */		
		
		this.add(butNewPacket);
		this.add(butNewPacket2);
		this.add(butNewPacket3);

		this.add(butNewPacket4);


		butNewPacket.addActionListener(listenerButtons);
		butNewPacket2.addActionListener(listenerButtons);
		butNewPacket3.addActionListener(listenerButtons);
		butNewPacket4.addActionListener(listenerButtons);

	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			
			
			//Testfunctions
			if(ae.getSource().equals(butNewPacket)) {
				
				String[] names = {"Noisefloor", "Packet#", "RSSI", "ReceiverID", "SenderID"};
				Float[] values = {(float)(Math.random()*20), (float)(model.getCurrentMeasurement().getNumberOfPackets()+1), (float)(Math.random()*30), (float)(Math.random()*2), (float)(Math.round((float)Math.random()*3))};
				
				model.addPacket(new Packet(names, values));
				

				String[] names1 = {"Jerr"};
				Float[] values1 = {(float)(Math.random()*20)};
				
				model.addPacket(new Packet(names1, values1));

			}
			if(ae.getSource().equals(butNewPacket2)) {
				
				for(int i=0; i<100; i++) {
					String[] names = {"Noisefloor", "Packet#", "RSSI", "ReceiverID", "SenderID"};
					Float[] values = {(float)(Math.random()*20), (float)(model.getCurrentMeasurement().getNumberOfPackets()+1), (float)(Math.random()*30), (float)(Math.random()*2), (float)(Math.round((float)Math.random()*3))};
					
					model.addPacket(new Packet(names, values));
				}
			}
			if(ae.getSource().equals(butNewPacket3)) {
				
				Packet[] p = new Packet[100];
				for(int i=0; i<100; i++) {
					String[] names = {"Noisefloor", "Packet#", "RSSI", "ReceiverID", "SenderID"};
					Float[] values = {(float)(Math.random()*20), (float)(model.getCurrentMeasurement().getNumberOfPackets()+1), (float)(Math.random()*30), (float)(Math.random()*2), (float)(Math.round((float)Math.random()*3))};
					p[i] = new Packet(names, values);					
				}
				model.addPacket(p);
			}

			if (ae.getSource().equals(butNewPacket4)) {

				for (int i = 0; i < 1000; i++) {
					String[] names = { "Noisefloor", "Packet#", "RSSI",
							"ReceiverID", "SenderID" };
					Float[] values = {
							(float) (Math.random() * 20),
							(float) (model.getCurrentMeasurement()
									.getNumberOfPackets() + 1),
							(float) (Math.random() * 30),
							(float) (Math.random() * 2),
							(float) (Math.round((float) Math.random() * 3)) };

					model.addPacket(new Packet(names, values));
				}
			}
		}
	}
}
