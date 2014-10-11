/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.IrisProperties;
import com.wsntools.iris.data.IrisPropertyReader;

/**
 * @author Sascha Jungen M#2242754
 * 
 *         Holds a constant connection to write measurement data into a
 *         backupfile
 */
public class BackgroundWriter {

	BufferedWriter output;
	boolean firstline = false;
	private final boolean loggingEnabled;

	public BackgroundWriter(String measname) {

		loggingEnabled =  IrisProperties.getInstance().getLogIncommingMessages();

		if (true == loggingEnabled) {

			File f = new File(
					Constants.getPathSavesBackgroundtraces()
							+ new SimpleDateFormat("dd-MM-yy_HH-mm-ss")
									.format(new Date()) + "-"
							+ measname.replace(' ', '_') + "-Backup.txt");

			try {
				output = new BufferedWriter(new FileWriter(f));
				System.out.println("Background Writer started - writing '"
						+ f.getName() + "'");
			} catch (IOException e) {
				System.err.println("ERROR: Cannot create backupfile");
				e.printStackTrace();
			}
		}
	}

	public void writeFirstLine(String[] parameter) {

		if (true == loggingEnabled) {

			if (!firstline) {
				String line = "";
				try {

					for (int i = 0; i < parameter.length; i++) {
						line = line
								+ (line.isEmpty() ? parameter[i]
										: (Constants.getTraceAttributeSeparator() + parameter[i]));
					}
					output.write(line);
					output.flush();
					firstline = true;
				} catch (IOException e) {
					System.err
							.println("ERROR: Cannot continue writing the backupfile");
					e.printStackTrace();
				}
			}
		}
	}

	public void writeNextLine(ArrayList<Float> values) {

		if (true == loggingEnabled) {

			String line = "";
			try {

				for (int i = 0; i < values.size(); i++) {
					line = line
							+ (line.isEmpty() ? values.get(i)
									: (Constants.getTraceDataSeparator() + values
											.get(i)));
				}
				output.newLine();
				output.write(line);
				output.flush();

			} catch (IOException e) {
				System.err
						.println("ERROR: Cannot continue writing the backupfile");
				e.printStackTrace();
			}
		}
	}

	public void finishWriting() {

		if (true == loggingEnabled) {

			try {
				output.close();
				this.finalize();
			} catch (IOException e) {
				System.err
						.println("ERROR: Backupfile writing process is already finished");
				e.printStackTrace();
			} catch (Throwable e) {
				System.err.println("ERROR: Class has already been destroyed");
				e.printStackTrace();
			}
		}
	}
}
