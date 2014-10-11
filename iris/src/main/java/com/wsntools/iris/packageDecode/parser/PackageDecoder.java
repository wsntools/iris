/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.packageDecode.parser;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.tools.ModuleLoader;

import net.tinyos.message.Message;

public class PackageDecoder implements ConvertMessageListener {
	public JFrame frame;
	JTextArea tAInpData;
	hexParser hexPar = new hexParser();

	public LinkedList<TimeMessage> timeMessages = new LinkedList<TimeMessage>();

	public PackageDecoder(boolean visible) {
		if (visible) {
			frame = new JFrame();
			frame.setSize(400, 600);
			// frame.setDefaultCloseOperation(JFrame.);
			frame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			frame.setLayout(new FlowLayout(5));
			frame.setTitle("Hex to Message");
			try {
				frame.setIconImage(ImageIO.read(new File("src/hexToMess.png")));
			} catch (IOException e1) {
			}
		}
		tAInpData = new JTextArea();
		tAInpData.setPreferredSize(new Dimension(400, 500));
		tAInpData.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		tAInpData.setAutoscrolls(true);

		final JButton btnConvert = new JButton();
		btnConvert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parseButtonPressed();
			}
		});
		btnConvert.setPreferredSize(new Dimension(100, 40));
		btnConvert.setText("parse");

		final JButton btnOpen = new JButton();
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		btnOpen.setPreferredSize(new Dimension(100, 40));
		btnOpen.setText("open File");

		final JButton btnPrintOut = new JButton();
		btnPrintOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				printAllMessage();
			}
		});
		btnPrintOut.setPreferredSize(new Dimension(140, 40));
		btnPrintOut.setText("print to Console");
		if (visible) {
			frame.add(tAInpData);
			frame.add(btnConvert);
			frame.add(btnOpen);
			frame.add(btnPrintOut);

			frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {

				@Override
				public void ancestorResized(HierarchyEvent e) {
					tAInpData.setPreferredSize(new Dimension(frame.getSize().width, frame.getSize().height - 100));
				}

				@Override
				public void ancestorMoved(HierarchyEvent e) {
				}
			});

			frame.setVisible(true);
		}
		addStaticTestClass();
	}

	/**
	 * adds some classes to the Listener
	 */
	private void addStaticTestClass() {
		System.out.println("-------------" + Constants.getLineSep() + "Package Decoder:" + Constants.getLineSep()
				+ "-------------");
		Object[] classList = ModuleLoader.getRadioLinkMessageClassesForReceiving();
		for (Object entry : classList) {
			try {
				addMessageType((Message) (((Class<? extends Message>) entry).newInstance()));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * adds a message Template to the hexParser, if the AmType Id is already
	 * used, we ask for a new One
	 */
	public void addMessageType(Message mes) {
		if (!hexPar.addConvertMessageListener(this, mes)) {
			// Id is already Used by another AmType
			String str = JOptionPane.showInputDialog(null, "You can choose a differen AMType id for this Template : "
					+ mes.getClass().getName() + "\n enter new ID: ", "AMTYPE already used by Template", 1);
			while (!hexPar.AMTypeFree(Integer.parseInt(str))) {
				str = JOptionPane.showInputDialog(null, "You can choose a differen AMType id for this Template : "
						+ mes.getClass().getName() + "\n enter new ID: ", "AMTYPE already used by Template", 1);
			}
			hexPar.addConvertMessageListener(this, mes, Integer.parseInt(str));
		} else {
			hexPar.addConvertMessageListener(this, mes);
		}
	}

	private void parseButtonPressed() {
		String buf = tAInpData.getText();
		buf = buf + "\n";
		String work;
		while (!buf.isEmpty()) {
			work = buf.substring(0, buf.indexOf("\n"));
			buf = buf.substring(buf.indexOf("\n") + 1);
			if (work.replaceAll(" ", "").equals(""))
				continue;
			parseHex(work);
		}
	}

	private void parseHex(String hexValue) {
		//		System.out.println(hexValue);
		hexPar.readHex(hexValue.replaceAll(" ", ""));
	}

	@Override
	public void messageReceived(long timeStamp, Message m) {
		timeMessages.add(new TimeMessage(timeStamp, m));
	}

	public void printAllMessage() {
		for (TimeMessage m : timeMessages) {
			System.out.println(m.time + " : " + m.mes);
		}
	}

	public void loadFile(String fileName) {
		try {
			BufferedReader read = new BufferedReader(new FileReader(new File(fileName)));
			while (read.ready()) {
				String line = read.readLine();
				parseHex(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "File " + fileName + " could not be found", "File not Found",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFile() {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (!(returnVal == JFileChooser.APPROVE_OPTION))
			return;
		String pos = fc.getSelectedFile().getAbsolutePath();
		loadFile(pos);
	}

	public static class TimeMessage {
		public Long time;
		public Message mes;

		public TimeMessage(Long Time, Message Mes) {
			time = Time;
			mes = Mes;
		}
	}

}
