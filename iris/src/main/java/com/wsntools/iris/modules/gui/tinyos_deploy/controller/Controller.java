/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.wsntools.iris.modules.gui.tinyos_deploy.model.Constants;
import com.wsntools.iris.modules.gui.tinyos_deploy.model.Mapping3;
import com.wsntools.iris.modules.gui.tinyos_deploy.model.Model;
import com.wsntools.iris.modules.gui.tinyos_deploy.view.Install_dialog;
import com.wsntools.iris.modules.gui.tinyos_deploy.view.Main_View;
import com.wsntools.iris.modules.gui.tinyos_deploy.view.Open_files_dialog;
import com.wsntools.iris.modules.gui.tinyos_deploy.view.Open_project_dialog;
import com.wsntools.iris.modules.gui.tinyos_deploy.view.Settings_dialog;

/**
 * Controller for the TinyOS deploy GUI
 * 
 * @author Sascha Hevelke
 *
 */
public class Controller extends AbstractController implements ActionListener,
		TableModelListener {

	Model m;
	Main_View mp;
	Install_dialog install_dia;
	Open_files_dialog open_files_dia;
	Open_project_dialog open_proj_dia;
	private Settings_dialog settings_dia;
	private static Logger logger = Logger.getRootLogger();

	/**
	 * Creates a new Controller
	 */
	public Controller() {
		try {
			initLogger();
		} catch (IOException e) {
			e.printStackTrace();
		}
		m = new Model();
		mp = new Main_View(this);
		m.addObserver(mp);
	}

	/**
	 * Initializes the logger
	 * 
	 * @throws IOException
	 */
	private static void initLogger() throws IOException {

		File f = new File(Constants.LOG_DIR_PATH);
		if (!f.exists()) {
			f.mkdirs();
		}
		System.setProperty(
				"logfile_path",
				Constants.LOG_DIR_PATH
						+ "/tinyos-deploy-"
						+ new SimpleDateFormat(Constants.LOG_FILE_DATE_FORMAT)
								.format(new Date()));
		PropertyConfigurator.configure("log4j.properties");

		logger.info(new Date());
		logger.info("Log4j successfully set up.");
		logger.info("--------------------------");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(mp.getFile_exit_item())) {
			mp.setVisible(false);
			mp.dispose();
		} else if (source.equals(mp.getDeploy_item())) {
			deploy_action();
			
		} else if (install_dia != null
				&& source.equals(install_dia.getClose_btn())) {
			closeDialog(install_dia);
			install_dia = null;
		} else if (install_dia != null
				&& source.equals(install_dia.getHw_choosefile_btn())) {
			// TODO: implement
		} else if (install_dia != null
				&& source.equals(install_dia.getApp_choosefile_btn())) {
			// TODO: implement
		} else if (install_dia != null
				&& source.equals(install_dia.getRun_btn())) {
			try {
				File hw_list = new File(install_dia.getHw_file_txt().getText());
				File app_list = new File(install_dia.getApp_file_txt()
						.getText());
				if (!hw_list.exists() || !hw_list.canRead()) {
					throw new IllegalArgumentException("Cannot find/read "
							+ hw_list.getAbsolutePath());
				}
				if (!app_list.exists() || !app_list.canRead()) {
					throw new IllegalArgumentException("Cannot find/read "
							+ app_list.getAbsolutePath());
				}
				runDeployScript(hw_list, app_list);
			} catch (IOException | InterruptedException | IllegalStateException
					| IllegalArgumentException e1) {
				e1.printStackTrace();
			}
		} else if (source.equals(mp.getFile_open_files_item())
				&& open_files_dia == null) {
			open_files_dia = new Open_files_dialog(this);
			open_files_dia.setModal(true);
		} else if (open_files_dia != null
				&& source.equals(open_files_dia.getHw_choosefile_btn())) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(mp) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				open_files_dia.getHw_file_txt().setText(file.getPath());
			}
		} else if (open_files_dia != null
				&& source.equals(open_files_dia.getApp_choosefile_btn())) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(mp) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				open_files_dia.getApp_file_txt().setText(file.getPath());
			}
		} else if (open_files_dia != null
				&& source.equals(open_files_dia.getClose_btn())) {
			closeDialog(open_files_dia);
			open_files_dia = null;
		} else if (open_files_dia != null
				&& source.equals(open_files_dia.getOpen_btn())) {
			File hw_list = new File(open_files_dia.getHw_file_txt().getText());
			File app_list = new File(open_files_dia.getApp_file_txt().getText());
			if (!hw_list.exists() || !hw_list.canRead()) {
				throw new IllegalArgumentException("Cannot find/read "
						+ hw_list.getAbsolutePath());
			}
			if (!app_list.exists() || !app_list.canRead()) {
				throw new IllegalArgumentException("Cannot find/read "
						+ app_list.getAbsolutePath());
			}
			try {
				m.importMappings(hw_list, app_list);
				m.setHw_mapping_path(hw_list.getPath());
				m.setApp_mapping_path(app_list.getPath());
				closeDialog(open_files_dia);
				open_files_dia = null;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

		} else if (open_proj_dia != null
				&& source.equals(open_proj_dia.getOpen_btn())) {
			File proj_file = new File(open_proj_dia.getProject_file_txt()
					.getText());
			if (!proj_file.exists() || !proj_file.canRead()) {
				throw new IllegalArgumentException("Cannot find/read "
						+ proj_file.getAbsolutePath());
			}
			try {
				m.importSettings(proj_file);
				m.importMappings(
						new File(proj_file.getParentFile() + "/"
								+ m.getHw_mapping_path()),
						new File(proj_file.getParentFile() + "/"
								+ m.getApp_mapping_path()));
				closeDialog(open_proj_dia);
				open_proj_dia = null;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else if (source.equals(mp.getFile_save_project_item())) {
			saveProject();
		} else if (source.equals(mp.getMapping_open_btn()) && open_proj_dia == null) {
			open_proj_dia = new Open_project_dialog(this);
			open_proj_dia.setModal(true);
		} else if (open_proj_dia != null
				&& source.equals(open_proj_dia.getClose_btn())) {
			closeDialog(open_proj_dia);
			open_proj_dia = null;
		} else if (open_proj_dia != null
				&& source.equals(open_proj_dia.getProject_choosefile_btn())) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(mp) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				open_proj_dia.getProject_file_txt().setText(file.getPath());
			}
		} else if (source.equals(mp.getMapping_new_btn())) {
			m.createNewMapping();
		} else if (source.equals(mp.getMapping_addrow_btn())) {
			if (m.getHw_app_mapping() != null) {
				int new_id = m.getHighestMappingId() + 1;
				m.addRowToTableModel(new Mapping3("", new_id, ""));
			}
		} else if (source.equals(mp.getMapping_rmrow_btn())) {
			if (mp.getMapping_tbl().getSelectedRow() != -1) {
				m.removeRowFromTableModel(mp.getMapping_tbl().getSelectedRow());
			}
		} else if (source.equals(mp.getMapping_importhw_btn())) {
			m.mergeConnectedMotes();
		} else if (source.equals(mp.getSettings_button())
				&& settings_dia == null) {
			this.settings_dia = new Settings_dialog(this);
		} else if (settings_dia != null
				&& source.equals(settings_dia.getApp_path_btn())) {
			JFileChooser app_path_chooser = new JFileChooser();
			app_path_chooser
					.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (app_path_chooser.showOpenDialog(settings_dia) == JFileChooser.APPROVE_OPTION) {
				File f = app_path_chooser.getSelectedFile();
				settings_dia.getApp_path_txtfield()
						.setText(f.getAbsolutePath());
			}
		} else if (settings_dia != null
				&& source.equals(settings_dia.getSave_btn())) {
			File f = new File(settings_dia.getApp_path_txtfield().getText());
			if (!f.exists()) {
				JOptionPane.showMessageDialog(settings_dia,
						"The provided App directory does not seem to exist",
						"Warning.", JOptionPane.WARNING_MESSAGE);
			} else {
				this.m.setTinyos_apps_path(f.getAbsolutePath());
				closeDialog(settings_dia);
				settings_dia = null;
			}
		} else if (settings_dia != null
				&& source.equals(settings_dia.getClose_btn())) {
			closeDialog(settings_dia);
			settings_dia = null;
		} else if (source.equals(mp.getMapping_saveas_btn())) {
			saveProject();
		} else if (source.equals(mp.getDeploy_button())) {
			deploy_action();
		} else {
			System.err.println("No action assigned to " + e.getSource());
		}
	}

	private void deploy_action() {
		try {
			m.prepareToInstall(m.getHw_app_mapping());
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getSource().equals(mp.getMapping_tbl().getModel())) {
			int column = e.getColumn();
			for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
				switch (column) {
				case 0:
					m.getHw_app_mapping()
							.get(i)
							.setHw_id(
									(String) mp.getMapping_tbl().getModel()
											.getValueAt(i, column));
					break;
				case 1:
					m.getHw_app_mapping()
							.get(i)
							.setId((String) mp.getMapping_tbl().getModel()
									.getValueAt(i, column));
					break;
				case 2:
					m.getHw_app_mapping()
							.get(i)
							.setApp_id(
									(String) mp.getMapping_tbl().getModel()
											.getValueAt(i, column));
					break;
				default:
					break;
				}
			}
		}
	}

	public Model getModel() {
		return m;
	}
	
	public JFrame getView() {
		return mp;
	}

	public void closeDialog(JDialog dia) {
		dia.setVisible(false);
		dia.dispose();
	}

	private void saveProject() {
		JFileChooser fc = new JFileChooser();
		File file;
		if (fc.showSaveDialog(mp) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			m.setApp_mapping_path("app.lst");
			m.setHw_mapping_path("hw.lst");
			File mapping_file = file;
			if (!file.isDirectory()) {
				mapping_file = file.getParentFile();
			}
			m.exportAppMapping(new File(mapping_file + "/"
					+ m.getApp_mapping_path()));
			m.exportHwMapping(new File(mapping_file + "/"
					+ m.getHw_mapping_path()));

			try {
				m.exportSettings(file);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Uses hw and app file to run a deploy script
	 * 
	 * @param hw_file
	 *            hardware mapping HW-id->AM-Address
	 * @param app_file
	 *            application mapping App->AM-Address
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 * @throws FileNotFoundException
	 */
	@Deprecated
	public void runDeployScript(File hw_file, File app_file)
			throws IOException, InterruptedException, IllegalStateException,
			FileNotFoundException {

		File f = new File("scripts/run.sh");
		if (!f.exists()) {
			throw new FileNotFoundException(f.getAbsolutePath()
					+ " could not be found.");
		}
		if (!f.canExecute()) {
			throw new IllegalStateException(f.getAbsolutePath()
					+ " can not be executed! Are the permissions correct?");
		}
		List<String> cmd = new ArrayList<>();
		cmd.add("scripts/run.sh");
		cmd.add(hw_file.getAbsolutePath());
		cmd.add(app_file.getAbsolutePath());
		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process pr = builder.start();
		pr.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
		}
	}

}
