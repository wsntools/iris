package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.tools.datacollector.sending.SendingConfiguration;

public class DiaSelectMessage extends JDialog {

	private Model model;	
	private DiaSelectMessage ref = this;
	private SendingConfiguration selected;
	
	private JPanel panelMain = new JPanel(new BorderLayout());
	
	private JPanel panelConfig = new JPanel();
	private JComboBox<SendingConfiguration> comboSendConfig;
	
	private JPanel panelInfo = new JPanel(new GridLayout(3, 2));
	private JLabel labelEncoder = new JLabel();
	private JLabel labelSender = new JLabel();
	private JLabel labelPort = new JLabel();
	
	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	
	private ButtonListener listenerButtons = new ButtonListener();
	
	private DiaSelectMessage(Model m, SendingConfiguration[] mess) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;

		comboSendConfig = new JComboBox<SendingConfiguration>(mess);
		panelConfig.add(comboSendConfig);
		
		panelInfo.add(new JLabel("Encoder:"));
		panelInfo.add(labelEncoder);
		panelInfo.add(new JLabel("Sender:"));
		panelInfo.add(labelSender);
		panelInfo.add(new JLabel("Port:"));
		panelInfo.add(labelPort);

		butOK.setPreferredSize(new Dimension(28,28));
		butCancel.setPreferredSize(new Dimension(28,28));
		
		panelButton.add(butOK);
		panelButton.add(butCancel);

		panelMain.add(panelConfig, BorderLayout.NORTH);
		panelMain.add(panelInfo, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		// Add actionlistener
		comboSendConfig.addActionListener(listenerButtons);
		butOK.addActionListener(listenerButtons);
		butCancel.addActionListener(listenerButtons);
		
		comboSendConfig.setSelectedIndex(0);

		// Windowsettings
		this.setTitle("IRIS - Select Message to Send");
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
	
	public SendingConfiguration getSelectedMessage() {

		return selected;
	}
	
	public static SendingConfiguration showMessageSelectionWindow(Model m, SendingConfiguration[] mess) {
		DiaSelectMessage vmap = new DiaSelectMessage(m, mess);
		return vmap.getSelectedMessage();
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// Apply mapping changes
			if (ae.getSource().equals(comboSendConfig)) {
				SendingConfiguration conf = (SendingConfiguration)comboSendConfig.getSelectedItem();
				labelEncoder.setText(conf.getEncoder().getClass().getName());
				labelSender.setText(conf.getSender().getClass().getName());
				labelPort.setText(conf.getPort().getClass().getName());
			}			
			else if (ae.getSource().equals(butOK)) {		
				selected = (SendingConfiguration)comboSendConfig.getSelectedItem();
				ref.dispose();
			}
			else if (ae.getSource().equals(butCancel)) {
				selected = null;
				ref.dispose();
			}
		}
	}
}
