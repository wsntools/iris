package com.wsntools.iris.modules.gui;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.wsntools.iris.data.AliasAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_GUIModule;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.modules.gui.tinyos_deploy.controller.Controller;

public class GUI_TinyOSDeployer extends IRIS_GUIModule {

	private Controller deployController;
	
	public GUI_TinyOSDeployer(Model m) {
		super(m);
		
	}

	@Override
	public String getModuleName() {
		return "TinyOS Deployer";
	}

	@Override
	public String getModuleDescription() {		
		return "This module allows to batch-install different TinyOS applications on multiple nodes with a few clicks.";
	}

	@Override
	public JPanel getGUIPanel() {
		if(deployController == null) {
			deployController = new Controller();
			deployController.getView().setVisible(false);
			((JPanel)deployController.getView().getContentPane()).setBorder(BorderFactory.createTitledBorder(getModuleName()));
		}
		return (JPanel) deployController.getView().getContentPane();
	}

	@Override
	public boolean isToolbarOnly() {
		return false;
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

}
