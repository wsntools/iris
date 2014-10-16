package com.wsntools.iris.modules.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.data.Packet;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;

public class GUI_TestPacketInsertion extends IRIS_GUIModule {

	private PanelTestBarPacketInsertion panelTestbar;
	
	public GUI_TestPacketInsertion(Model m) {
		super(m);
		panelTestbar = new PanelTestBarPacketInsertion(m);
	}

	@Override
	public String getModuleName() {
		return "(Test) Packet Insertion";
	}

	@Override
	public String getModuleDescription() {
		return "Allows to insert packets with random content into the system for testing purposes";
	}

	@Override
	public JPanel getGUIPanel() {
		return panelTestbar;
	}

	@Override
	public boolean isToolbarOnly() {
		return true;
	}

	@Override
	public IRIS_Observer getModuleObserver() {
		return null;
	}

	@Override
	public IRIS_ModuleInfo[] getRelatedModuleInfos() {
		return null;
	}

	@Override
	public String[] getRelatedMenuBarEntries() {
		return null;
	}

	@Override
	public ActionListener getMenuBarActionListener() {
		return null;
	}

	@Override
	public AliasAttribute[] getRequiredAliasAttributes() {
		return null;
	}
	
	
	private class PanelTestBarPacketInsertion extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private Model model;	
		
		private JButton butNewPacket = new JButton("New Packet (x2 diff cnt.)");
		private JButton butNewPacket2 = new JButton("New Packet (x100 single)");
		private JButton butNewPacket3 = new JButton("New Packet (x100 bundle)");
		private JButton butNewPacket4 = new JButton("New Packet (x1.000 single)");
		
		private ButtonListener listenerButtons = new ButtonListener();

		public PanelTestBarPacketInsertion(Model m) {			
			model = m;
	
			this.setLayout(new GridLayout(2, 2));
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
					

					String[] names1 = {"Even More"};
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

}
