/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.modules.gui.tinyos_deploy.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.wsntools.iris.modules.gui.tinyos_deploy.controller.Controller;
import com.wsntools.iris.modules.gui.tinyos_deploy.model.Mapping3;
import com.wsntools.iris.modules.gui.tinyos_deploy.model.MappingTableModel;

public class Main_View extends JFrame implements Observer {
	private static final long serialVersionUID = 1133005594522085171L;

	private Controller cont;

	/* panel over the whole width and for each mapping respectively */
	private JPanel mapping_panel;

	/* panels for save buttons etc. for both tables */
	private JPanel mapping_upper_functions_panel;
	private JPanel mapping_lower_functions_panel;

	/* the buttons (see above) */
	private JButton mapping_new_btn;
//	private JButton mapping_save_btn;
	private JButton mapping_saveas_btn;
	private JButton mapping_open_btn;
	private JButton mapping_addrow_btn;
	private JButton mapping_rmrow_btn;
	private JButton mapping_importhw_btn;
	private JButton settings_button;
	private JButton deploy_button;

	/* scroll panes which contain the tables */
	private JScrollPane mapping_tbl_panel;

	/* jtables for the mappings */
	private JTable mapping_tbl;

	/* Menu stuff */
	private JMenuBar main_menu;

	private JMenu file_menu;
	private JMenuItem file_exit_item;
	private JMenuItem file_hw_open_item;
	private JMenuItem file_app_open_item;
	private JMenuItem file_hw_save_item;
	private JMenuItem file_app_save_item;
	private JMenuItem file_hw_saveas_item;
	private JMenuItem file_app_saveas_item;
	private JMenuItem file_open_files_item;
	private JMenuItem file_open_project_item;
	private JMenuItem file_save_project_item;

	private JMenu deploy_menu;
	private JMenuItem deploy_item;

	public Main_View(Controller c) {
		this.cont = c;
		initComponents();
		this.pack();
		this.setVisible(true);
		this.setSize(500, 500);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initComponents() {
//		initMenu();
		mapping_tbl = new JTable();
		mapping_tbl.setBorder(BorderFactory.createLineBorder(Color.gray));
		mapping_tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		mapping_tbl.setFillsViewportHeight(true);
		mapping_tbl.setModel(new MappingTableModel(new ArrayList<String[]>()));
		mapping_tbl.getModel().addTableModelListener(cont);

		mapping_tbl_panel = new JScrollPane(mapping_tbl);
		mapping_tbl_panel
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mapping_tbl_panel
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		mapping_new_btn = new JButton("New");
		mapping_new_btn.addActionListener(cont);
//		mapping_save_btn = new JButton("Save");
//		mapping_save_btn.setEnabled(false);
//		mapping_save_btn.addActionListener(cont);
		mapping_open_btn = new JButton("Open");
		mapping_open_btn.addActionListener(cont);
		mapping_saveas_btn = new JButton("Save as..");
		mapping_saveas_btn.setEnabled(false);
		mapping_saveas_btn.addActionListener(cont);
		mapping_addrow_btn = new JButton("Add row");
		mapping_addrow_btn.setEnabled(false);
		mapping_addrow_btn.addActionListener(cont);
		mapping_rmrow_btn = new JButton("Remove row");
		mapping_rmrow_btn.setEnabled(false);
		mapping_rmrow_btn.addActionListener(cont);
		mapping_importhw_btn = new JButton("Import connected motes");
		mapping_importhw_btn.addActionListener(cont);
		settings_button = new JButton("Edit settings..");
		settings_button.addActionListener(cont);
		deploy_button = new JButton("Deploy");
		deploy_button.addActionListener(cont);
		
		
		mapping_upper_functions_panel = new JPanel();
		mapping_upper_functions_panel.setLayout(new FlowLayout());
		mapping_upper_functions_panel.add(mapping_new_btn);
		mapping_upper_functions_panel.add(mapping_open_btn);
//		mapping_upper_functions_panel.add(mapping_save_btn);
		mapping_upper_functions_panel.add(mapping_saveas_btn);
		mapping_upper_functions_panel.add(mapping_addrow_btn);
		mapping_upper_functions_panel.add(deploy_button);

		mapping_lower_functions_panel = new JPanel();
		mapping_lower_functions_panel.setLayout(new FlowLayout());
		mapping_lower_functions_panel.add(mapping_addrow_btn);
		mapping_lower_functions_panel.add(mapping_rmrow_btn);
		mapping_lower_functions_panel.add(mapping_importhw_btn);
		mapping_lower_functions_panel.add(settings_button);

		mapping_panel = new JPanel();
		mapping_panel.setLayout(new BorderLayout());
//		mapping_panel.setBorder(BorderFactory.createTitledBorder("Mapping"));
		mapping_panel.add(mapping_upper_functions_panel, BorderLayout.NORTH);
		mapping_panel.add(mapping_tbl_panel, BorderLayout.CENTER);
		mapping_panel.add(mapping_lower_functions_panel, BorderLayout.SOUTH);

		this.mapping_lower_functions_panel.setEnabled(false);
		this.setLayout(new BorderLayout());
		this.add(mapping_panel, BorderLayout.CENTER);

	}

	public JTable getMapping_tbl() {
		return mapping_tbl;
	}

	private void initMenu() {
		main_menu = new JMenuBar();
		file_menu = new JMenu("File");

		file_hw_open_item = new JMenuItem("Open hw-mapping..");
		file_hw_open_item.addActionListener(cont);
		file_app_open_item = new JMenuItem("Open app-mapping..");
		file_app_open_item.addActionListener(cont);
		file_hw_save_item = new JMenuItem("Save hw-mapping");
		file_hw_save_item.addActionListener(cont);
		file_app_save_item = new JMenuItem("Save app-mapping");
		file_app_save_item.addActionListener(cont);
		file_hw_saveas_item = new JMenuItem("Save hw-mapping as..");
		file_hw_saveas_item.addActionListener(cont);
		file_app_saveas_item = new JMenuItem("Save app-mapping as..");
		file_app_saveas_item.addActionListener(cont);
		file_exit_item = new JMenuItem("Exit");
		file_exit_item.addActionListener(cont);
		file_open_files_item = new JMenuItem("Open files..");
		file_open_files_item.addActionListener(cont);
		file_open_project_item = new JMenuItem("Open project..");
		file_open_project_item.addActionListener(cont);
		file_save_project_item = new JMenuItem("Save project..");
		file_save_project_item.addActionListener(cont);

		file_menu.add(file_open_files_item);
		file_menu.add(file_open_project_item);
		// file_menu.add(file_hw_open_item);
		// file_menu.add(file_app_open_item);
		file_menu.addSeparator();
		file_menu.add(file_save_project_item);
		// file_menu.add(file_hw_save_item);
		// file_menu.add(file_app_save_item);
		file_menu.addSeparator();
		file_menu.add(file_hw_saveas_item);
		file_menu.add(file_app_saveas_item);
		file_menu.addSeparator();
		file_menu.add(file_exit_item);

		deploy_menu = new JMenu("Deploy");
		deploy_item = new JMenuItem("Deploy..");
		deploy_item.addActionListener(cont);
		deploy_menu.add(deploy_item);

		main_menu.add(file_menu);
		main_menu.add(deploy_menu);
		this.setJMenuBar(main_menu);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1!=null && (arg1.equals(cont.getModel().getHw_app_mapping())
				&& (cont.getModel().getHw_app_mapping() != null ))) {
			List<Mapping3> hw_app_mapping = cont.getModel().getHw_app_mapping();
			List<String[]> ret = new ArrayList<String[]>();
			for (Mapping3 mapping : hw_app_mapping) {
				ret.add(new String[]{mapping.getHw_id(),mapping.getId(),mapping.getApp_id()});
			}
			
			MappingTableModel ctm = new MappingTableModel(ret);
			ctm.addTableModelListener(cont);
			mapping_tbl.setModel(ctm);
			
		}
		setupApplicationColumn();

		mapping_addrow_btn.setEnabled(cont.getModel().getHw_app_mapping() != null);
		mapping_rmrow_btn.setEnabled(cont.getModel().getHw_app_mapping() != null);
		mapping_saveas_btn.setEnabled(cont.getModel().getHw_app_mapping() != null);
//		mapping_save_btn.setEnabled(cont.getModel().getHw_app_lastpath() != null);

		this.repaint();
	}
	
	/**
	 * creates a JComboBox to store all available applications. 
	 * Combobox is then added to the application column of the table
	 */
	private void setupApplicationColumn() {
		List<String> avail_apps = cont.getModel().getAvailableApplications();
		Collections.sort(avail_apps);
		JComboBox<String> box = new JComboBox<String>();
		box.setEditable(true);
		for (String app : avail_apps) {
			box.addItem(app);
		}
		TableColumn tc = mapping_tbl.getColumnModel().getColumn(2);
		tc.setCellEditor(new DefaultCellEditor(box));
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tc.setCellRenderer(tcr);
	}
	
	public Controller getController() {
		return cont;
	}

	public JMenuItem getFile_exit_item() {
		return file_exit_item;
	}

	public JMenuItem getDeploy_item() {
		return deploy_item;
	}

	public JButton getMapping_new_btn() {
		return mapping_new_btn;
	}

	public JPanel getMapping_panel() {
		return mapping_panel;
	}

	public JPanel getMapping_upper_functions_panel() {
		return mapping_upper_functions_panel;
	}

	public JPanel getMapping_lower_functions_panel() {
		return mapping_lower_functions_panel;
	}

//	public JButton getMapping_save_btn() {
//		return mapping_save_btn;
//	}

	public JButton getMapping_saveas_btn() {
		return mapping_saveas_btn;
	}

	public JButton getMapping_open_btn() {
		return mapping_open_btn;
	}

	public JButton getMapping_addrow_btn() {
		return mapping_addrow_btn;
	}

	public JButton getMapping_rmrow_btn() {
		return mapping_rmrow_btn;
	}

	public JButton getMapping_importhw_btn() {
		return mapping_importhw_btn;
	}

	public JScrollPane getMapping_tbl_panel() {
		return mapping_tbl_panel;
	}

	public JMenuItem getFile_open_files_item() {
		return file_open_files_item;
	}

	public JMenuItem getFile_open_project_item() {
		return file_open_project_item;
	}

	public JMenuItem getFile_save_project_item() {
		return file_save_project_item;
	}

	public JButton getSettings_button() {
		return settings_button;
	}

	public JButton getDeploy_button() {
		return deploy_button;
	}

}