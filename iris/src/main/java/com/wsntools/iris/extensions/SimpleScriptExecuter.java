/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.extensions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.TypeConstraintException;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.IrisProperties;
import com.wsntools.iris.data.Measurement;
import com.wsntools.iris.dialogues.SendClassConfig;
import com.wsntools.iris.tools.SaveAndLoad;

public class SimpleScriptExecuter {

	public static int defaultWait = 500;

	private static StringTokenizer st;

	// /**
	// * commands parsed from the last run
	// */
	// private static ArrayList<Command> commands = new ArrayList<Command>();

	/**
	 * if some temporary commands are stored. (when creating a batch with
	 * ALLKNOWN)
	 */
	private static ArrayList<Command> tempCommands = new ArrayList<Command>();

	public static int WAIT = 0, SEND = 1, MSG = 2, IMPORT = 3;

	private static JFrame gui;
	private static JTextArea scriptArea = new JTextArea("");

	private static Measurement meas;

	private static HashMap<String, MessageWrapper> myMessages = new HashMap<String, MessageWrapper>();

	/**
	 * 
	 * @param string
	 */
	public static ArrayList<Command> parse(String string) {
		ArrayList<Command> commands = new ArrayList<Command>();
		BufferedReader br = new BufferedReader(new StringReader(string));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				Command c = checkAndCreateCmds(line);
				if (c != null) // c is null, when its a batch of commands
					commands.add(c);
				else {
					commands.addAll(SimpleScriptExecuter.tempCommands);
					tempCommands.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return commands;
	}

	public static Command checkAndCreateCmds(String line) {
		// Object[] Command = null;
		st = new StringTokenizer(line, " ");
		if (line.isEmpty())
			return null;
		String command = st.nextToken();
		if (command.equals("wait")) { // CMD WAIT: 0
			if (st.countTokens() > 2)
				throw new TypeConstraintException(
						"this is no wait. give me only one number");
			int wait;
			if (st.countTokens() == 1)
				wait = (Integer) SendClassConfig.getValue(Integer.TYPE,
						st.nextToken());
			else
				wait = defaultWait;
			return new Command(WAIT, new Object[] { wait });
		} else if (command.equals("send")) { // CMD SEND
			String type = st.nextToken();
			int numP = st.countTokens();
			String[] params = new String[numP];
			int i = 0;
			if (numP == 0) { // send known msg, type holds the name
				MessageWrapper msg = null;
				if (myMessages.containsKey(type))
					msg = myMessages.get(type);
				else if (meas.getMsges().containsKey(type))
					msg = meas.getMsges().get(type);
				else
					throw new NullPointerException("message name not known "
							+ numP);
				return new Command(SEND, new Object[] { msg });
			}
			while (st.hasMoreTokens())
				params[i++] = st.nextToken();
			if (params[0].equals("ALLKNOWN")) {
				tempCommands.clear();
				tempCommands.addAll(addSendtoAllKnown(type, params));
			} else {
				return new Command(
						SEND,
						new Object[] { SendMessage.createMessage(type, params) });
			}
		} else if (command.equals("msg")) { // CMD MSG
			boolean storeToMeas = false;
			String name = st.nextToken();
			if (name.equals("<")) {
				storeToMeas = true;
				name = st.nextToken();
			}
			String type = st.nextToken();
			int numP = st.countTokens();
			String[] params = new String[numP];
			int i = 0;
			while (st.hasMoreTokens())
				params[i++] = st.nextToken();
			// System.out.println(type + " :" + Arrays.toString(params));
			MessageWrapper msg = SendMessage.createMessage(type, params);
			msg.name = name;
			Command cmd = new Command(MSG, new Object[] { msg, storeToMeas });
			exec(cmd);
			return null;
		} else if (command.equals("import")) { // CMD IMPORT
			String fileName = st.nextToken();
			File file = getExecutableFileFromString(fileName);
			tempCommands = parse(SaveAndLoad.loadScript(file));
			return null;
		} else
			throw new IllegalAccessError("Unknown command");
		return null;
	}

	public static File getExecutableFileFromString(String fileName) {
		File file = new File(Constants.getPathSavesMessages() + fileName);
		if (!file.exists())
			file = new File(Constants.getPathSavesScripts() + fileName);
		if (!file.exists())
			throw new NullPointerException("No file: " + fileName
					+ ". Maybe your current path is not the path of the files");
		return file;
	}

	private static ArrayList<Command> addSendtoAllKnown(String type,
			String[] params) {
		ArrayList<Command> cmds = new ArrayList<Command>();
		int wait = defaultWait;
		if (params[params.length - 2].equals("wait")) {
			wait = (Integer) SendClassConfig.getValue(Integer.TYPE,
					params[params.length - 1]);
			params = Arrays.copyOfRange(params, 0, params.length - 2);
		}
		ArrayList<Integer> nbs = meas.getNeighbourlist(-1);
		for (int nb : nbs) {
			params[0] = String.valueOf(nb);
			cmds.add(new Command(SEND, new Object[] { SendMessage
					.createMessage(type, params) }));
			cmds.add(new Command(WAIT, new Object[] { wait }));
		}
		return cmds;
	}

	public static void exec(Command cmd) {
		ArrayList<Command> al = new ArrayList<Command>();
		al.add(cmd);
		exec(al);
	}

	public static void exec(ArrayList<Command> commands) {
		for (Command cmd : commands) {
			if (cmd.type == WAIT) {
				try {
					Thread.sleep((Integer) cmd.params[0]);
					System.out.println("waiting " + (Integer) cmd.params[0]
							+ " ms");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (cmd.type == SEND) {
				SendMessage.send((MessageWrapper) cmd.params[0]);
				if (true == IrisProperties.getInstance()
						.getLogOutgoingMessages()) {
					meas.logSendMessage((MessageWrapper) cmd.params[0]);
				}
				System.out.println("sending " + (MessageWrapper) cmd.params[0]);
			} else if (cmd.type == MSG) {
				// will be executed during parsing
				MessageWrapper msg = (MessageWrapper) cmd.params[0];
				myMessages.put(msg.name, msg);
				if ((Boolean) cmd.params[1])
					meas.addMsg(msg);
				System.out.println("created " + msg);
			} else if (cmd.type == IMPORT) {
				// list of commands in the file will be added to the command
				// list
			}
		}
	}

	public static void openGUI(Measurement m) {
		if (gui == null) {
			gui = new JFrame("Scripting Frame");
			gui.setSize(400, 200);
			gui.setLayout(new BorderLayout());
			scriptArea.addKeyListener(new Key());
			gui.getContentPane().add(new JScrollPane(scriptArea),
					BorderLayout.CENTER);
			JButton send = new JButton("Send");
			// send.setName("send");
			gui.getContentPane().add(send, BorderLayout.LINE_END);
			send.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ArrayList<Command> commands = parse(scriptArea.getText());
					exec(commands);
					gui.setVisible(false);
					System.out.println("script executed");
				}
			});
		}
		meas = m;
		gui.setVisible(true);
	}

	public static void setMeasurement(Measurement m) {
		meas = m;
	}

	public static class Command {

		public int type;
		public Object[] params;

		public Command(int type, Object[] params) {
			super();
			this.type = type;
			this.params = params;
		}

	}

	private static class Key implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F1) {
				System.out.println();
				int i = scriptArea.getCaretPosition() - 1;
				String text = scriptArea.getText();
				StringBuilder sb = new StringBuilder();
				while (i >= 0 && !(text.charAt(i) == ' ')) {
					sb.append(text.charAt(i--));
				}
				String msgType = new StringBuffer(sb).reverse().toString();
				MessageAttributeWrapper maw = MessageAttributeWrapper
						.getMsgAttributeWrapper(msgType);
				if (maw == null)
					return;
				sb = new StringBuilder(text);
				sb.append(" <address:int>");
				int ic = 0;
				for (String s : maw.names) {
					sb.append(" <" + s + ":" + maw.classes[ic].getSimpleName()
							+ ">");
				}
				scriptArea.setText(sb.toString());
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}

	public static String getScriptText() {
		return scriptArea.getText();
	}

	public static void setText(String script) {
		scriptArea.setText(script);
	}

	public static ArrayList<Command> getTempCmds() {
		return tempCommands;
	}

}
