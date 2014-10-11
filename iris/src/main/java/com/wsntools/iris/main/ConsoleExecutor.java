/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.main;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.extensions.SimpleScriptExecuter;
import com.wsntools.iris.tools.SaveAndLoad;

public class ConsoleExecutor implements Runnable {

	private final Model m;

	public ConsoleExecutor(Model m) {
		this.m = m;
	}

	public static Thread startConsoleThread(Model m) {
		Thread c = new Thread(new ConsoleExecutor(m));
		c.start();

		// TODO check!
		//		ConsoleExecutor.m = m;
		return c;
	}

	@Override
	public void run() {
		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			//			m = ConsoleExecutor.m;
			System.out.println(">");
			String line = null;
			try {
				line = br.readLine();
				if (line != null && line.length() > 0) {
					StringTokenizer st = new StringTokenizer(line, " ");
					String cmd = st.nextToken();
					if (cmd.equals("send")) {
						SimpleScriptExecuter.exec(SimpleScriptExecuter.parse(line));
					} else if (cmd.equals("import")) {
						String fileName = st.nextToken();
						File file = SimpleScriptExecuter.getExecutableFileFromString(fileName);
						SimpleScriptExecuter.exec(SimpleScriptExecuter.parse(SaveAndLoad.loadScript(file)));
					} else if (cmd.equals("listen-on")) {
						m.startRecording();
					} else if (cmd.equals("listen-off")) {
						m.stopRecording();
					} else if (cmd.equals("quit")) {
						m.getView().SafeCloseOperation();
					} else if (cmd.equals("quit-force")) {
						m.getView().SafeCloseOperation(true);
					}
				}
				System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		startConsoleThread(new Model(false, false));
	}
}
