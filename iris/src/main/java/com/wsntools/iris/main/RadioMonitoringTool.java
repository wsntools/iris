/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.main;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.wsntools.iris.data.Model;
import com.wsntools.iris.extensions.SimpleScriptExecuter;
import com.wsntools.iris.tools.ModuleLoader;
import com.wsntools.iris.tools.SaveAndLoad;
import com.wsntools.iris.views.ViewMain;

/**
 * @author Sascha Jungen  
 *         Main function of the RMT
 */
public class RadioMonitoringTool {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(ModuleLoader.getSourcePath());
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (ClassNotFoundException e) {

		} catch (InstantiationException e) {

		} catch (IllegalAccessException e) {

		} catch (UnsupportedLookAndFeelException e) {

		}
		
		boolean listen = false;
		boolean console = false;
		boolean view = true;
		boolean import_ = false;
		boolean dds = false;
		String script = null;
		for (String cmd : args)
			if (cmd.equalsIgnoreCase("-listen"))
				listen = true;
			else if (cmd.equalsIgnoreCase("-console"))
				console = true;
			else if (cmd.equalsIgnoreCase("-noView"))
				view = false;
			else if (cmd.startsWith("-import:")) {
				import_ = true;
				script = cmd.substring(8);
			} else if(cmd.equalsIgnoreCase("-dds"))
				dds = true;
		Model m = new Model(listen, dds);
		if (view)
			new ViewMain(m).toFront();
		if (console)
			ConsoleExecutor.startConsoleThread(m);
		if (import_) {
			File file = SimpleScriptExecuter.getExecutableFileFromString(script);
			SimpleScriptExecuter.exec(SimpleScriptExecuter.parse(SaveAndLoad.loadScript(file)));
		}
	}
}
