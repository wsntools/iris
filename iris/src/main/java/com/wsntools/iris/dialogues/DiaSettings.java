/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.panels.PanelMapping;
/**
 * @author Sascha Jungen
 */

public class DiaSettings extends JDialog {

	private static final long serialVersionUID = 1L;

	private Model model;
	private DiaSettings ref = this;
	// 0->Measurement dialog
	// 1->Measure Filter dialog
	// 2->Function Set dialog
	// 3->Attribute hiding
	// 4->Function Domain dialog (drawing)
	// 5->Draw Attribute Filter dialog
	// 6->Record Setting dialog
	private int diaType;

	private JPanel panelMain = new JPanel(new BorderLayout());
	private JPanel panelMap;

	// Measure Mapping
	private PanelMapping[] arrMapping;

	// Function Domain Draw Settings
	private JTextField textDrawFrom, textDrawTo;
	private int[] resultFuncDraw;

	// Record Settings
	private JCheckBox checkEnableBuffer;
	private JTextField textBuffersize;

	private JPanel panelButton = new JPanel();
	private JButton butOK = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	private JButton butCancel = new JButton(new ImageIcon(Constants.getResource(
			Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));

	private JComboBox comboMapForYAxis = new JComboBox();

	private IRIS_Attribute yAxis;
	// --Listener--
	private ButtonListener listenerButtons = new ButtonListener();

	
	// Constructor for function domain drawing dialog
	private DiaSettings(Model m, FunctionAttribute fa) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		diaType = 4;

		resultFuncDraw = null;

		textDrawFrom = new JTextField(5);
		textDrawTo = new JTextField(5);

		panelMap = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelMap.add(new JLabel("Values"));
		panelMap.add(textDrawFrom);
		panelMap.add(new JLabel("to"));
		panelMap.add(textDrawTo);
		panelMap.setBorder(BorderFactory.createTitledBorder(fa
				.getAttributeName()));

		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);
		panelButton.add(butCancel);

		panelMain.add(panelMap, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		// Add actionlistener
		butOK.addActionListener(listenerButtons);
		butCancel.addActionListener(listenerButtons);

		// Windowsettings
		this.setTitle("IRIS - Function Domain Drawing: " + fa.getAttributeName());
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

	// Constructor for record setting dialog
	private DiaSettings(Model m) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;
		diaType = 6;

		int recbuf = model.getRecordBufferSize();

		checkEnableBuffer = new JCheckBox("Enable Buffer");
		checkEnableBuffer.setSelected(recbuf != -1);

		textBuffersize = new JTextField(5);
		textBuffersize.setText((recbuf != -1) ? Integer.toString(recbuf) : "");

		JPanel panelText = new JPanel();
		panelText.add(new JLabel("Size (Max "
				+ Constants.getPacketBufferMaxSize() + "): "));
		panelText.add(textBuffersize);

		panelMap = new JPanel(new BorderLayout());
		panelMap.add(checkEnableBuffer, BorderLayout.NORTH);
		panelMap.add(panelText, BorderLayout.SOUTH);
		panelMap.setBorder(BorderFactory.createTitledBorder("Packet Buffering"));

		butOK.setPreferredSize(new Dimension(28, 28));
		butCancel.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);
		panelButton.add(butCancel);

		panelMain.add(panelMap, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		// Add actionlistener
		butOK.addActionListener(listenerButtons);
		butCancel.addActionListener(listenerButtons);

		// Windowsettings
		this.setTitle("IRIS - Record Setting");
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

	public int[] getFuncDrawResults() {

		return resultFuncDraw;
	}

	public IRIS_Attribute getYAxis() {
		return yAxis;
	}

	// External methods to generate dialog windows
	public static int[] showFunctionDomainDrawWindow(Model m,
			FunctionAttribute fa) {
		DiaSettings vmap = new DiaSettings(m, fa);
		return vmap.getFuncDrawResults();
	}

	public static void showRecordSettingWindow(Model m) {
		new DiaSettings(m);
	}

	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// Apply mapping changes
			if (ae.getSource().equals(butOK)) {

				switch (diaType) {

				case 4:
					int from,
					to;
					try {
						// If values are left blank, do not set a value
						if (textDrawFrom.getText().isEmpty()
								&& textDrawTo.getText().isEmpty()) {
							ref.dispose();
							return;
						}
						from = Integer.parseInt(textDrawFrom.getText());
						to = Integer.parseInt(textDrawTo.getText());
						if (from > to | from < 0 | to < 0) {
							throw new Exception();
						}
						if (to >= model.getCurrentMeasurement()
								.getNumberOfPackets())
							to = model.getCurrentMeasurement()
									.getNumberOfPackets() - 1;
					} catch (Exception e) {
						JOptionPane.showMessageDialog(model.getView(),
								("Please enter valid numbers"));
						return;
					}
					resultFuncDraw = new int[] { from, to };
					break;

				case 6:
					if (checkEnableBuffer.isSelected()) {
						try {
							int val = Integer
									.parseInt(textBuffersize.getText());

							if (val < 1)
								throw new NumberFormatException();
							if (val > Constants.getPacketBufferMaxSize())
								val = Constants.getPacketBufferMaxSize();

							model.setRecordBufferSize(val);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(model.getView(),
									("Please enter a valid buffersize"));
							return;
						}
					} else {
						model.setRecordBufferSize(-1);
					}
					break;
				}

				ref.dispose();
			}

			if (ae.getSource().equals(butCancel)) {

				ref.dispose();
			}			
		}
	}
}
