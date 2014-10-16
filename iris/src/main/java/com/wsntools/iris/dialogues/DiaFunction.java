/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.data.FunctionAttribute;
import com.wsntools.iris.data.Model;
import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_FunctionModule;
import com.wsntools.iris.interfaces.IRIS_Observer;
import com.wsntools.iris.tools.SaveAndLoad;
/**
 * @author Sascha Jungen
 */

public class DiaFunction extends JDialog implements IRIS_Observer {

	private static final long serialVersionUID = 1L;
	
	Model model;
	DiaFunction ref = this;
	
	//--Panel--
	private JPanel panelMain = new JPanel();
	
	private JPanel panelSelection = new JPanel(new BorderLayout());
	
	private JPanel panelFunctionSelect = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JComboBox comboFunctions = new JComboBox();
	private JButton butApply = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnOk())));
	
	private JPanel panelDescription = new JPanel();
	private JTextArea textDescription = new JTextArea(3,25);
	private JScrollPane scrollDescr = new JScrollPane(textDescription, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	private JPanel panelToAttribute = new JPanel();	
	private JComboBox comboToAttribute = new JComboBox();
	private JLabel labelToAttribute = new JLabel("Scalar result - no update possible");
	
	private JPanel panelFunctionBrowser = new JPanel(new BorderLayout());
	private JPanel panelBrowserTools = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JButton butSettings = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnSettings())));
	private JButton butRename = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnRename())));
	private JButton butDelete = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnDelete())));
	private JButton butSave = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnSave())));
	private JButton butLoad = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnLoadFunc())));
	private JButton butExit = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons() + Constants.getNameBtnExit())));
	private DefaultListModel listModel = new DefaultListModel();
	private JList listUsedFunctions;
	private JScrollPane scrollFuncs;
	private JTextArea textDependencies = new JTextArea(4,25);
	private JScrollPane scrollDepend = new JScrollPane(textDependencies, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
	
	
	
	
	//--Listener--
	private ButtonListener listenerButtons = new ButtonListener();
	private ComboBoxListener listenerComboBox = new ComboBoxListener();
	private ListBoxListener listenerListBox = new ListBoxListener();
	
	
	public DiaFunction(Model m) {
		super(m.getView(), false);
		
		model = m;
		model.registerObserver(this);
		
		//Object init

		
		/*
		 * Define Layout
		 */		
		butApply.setPreferredSize(new Dimension(28,28));
		butDelete.setPreferredSize(new Dimension(28,28));
		butSave.setPreferredSize(new Dimension(28,28));
		butLoad.setPreferredSize(new Dimension(28,28));
		butSettings.setPreferredSize(new Dimension(28,28));
		butRename.setPreferredSize(new Dimension(28,28));
		butExit.setPreferredSize(new Dimension(28,28));
		
		butApply.setToolTipText("Applies the chosen settings and creates a new function-attribute");
		butDelete.setToolTipText("Removes a previously created function from the list");
		butSave.setToolTipText("Saves the selected function");
		butLoad.setToolTipText("Loads a previously created function into the application");
		butSettings.setToolTipText("Enables the later change of function settings");
		butRename.setToolTipText("Changes the current function descriptor");
		butExit.setToolTipText("Closes the function window");
		
		//Add on functions with n -> n result mapping 
		for(int i=0; i<model.getFunctionNames().length; i++) {
			//if(!model.getFunctionInstanceByName(model.getFunctionNames()[i]).isOneValueResult()) {
				comboFunctions.addItem(model.getFunctionNames()[i]);
			//}
		}
		panelFunctionSelect.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Function"));
		panelFunctionSelect.add(comboFunctions);
		panelFunctionSelect.add(butApply);
		
		textDescription.setEditable(false);
		textDescription.setBackground(this.getBackground());
		textDescription.setLineWrap(true);
		textDescription.setWrapStyleWord(true);
		scrollDescr.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		panelDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Function Information"));
		panelDescription.add(scrollDescr);
		
		comboToAttribute.addItem("--New Attribute--");
		//List only non-scalar functions to update
		List<IRIS_Attribute> funcList = model.getMeasureAttributesBySpecification(false, false, false, false, true, false);
		for(int i=0; i<funcList.size(); i++) {
			comboToAttribute.addItem(funcList.get(i).getAttributeName());
		}			
		comboToAttribute.setSelectedIndex(0);
		labelToAttribute.setVisible(false);
		panelToAttribute.setBorder(javax.swing.BorderFactory.createTitledBorder("Update existing Attribute"));
		panelToAttribute.add(comboToAttribute);
		panelToAttribute.add(labelToAttribute);
		
		panelSelection.add(panelFunctionSelect, BorderLayout.NORTH);
		panelSelection.add(panelDescription, BorderLayout.CENTER);
		panelSelection.add(panelToAttribute, BorderLayout.SOUTH);		
		
		
		//--Function Browser--
		
		//Toolbar for function browsing
		panelBrowserTools.add(butRename);
		panelBrowserTools.add(butSettings);
		panelBrowserTools.add(butSave);
		panelBrowserTools.add(butLoad);
		panelBrowserTools.add(butDelete);
		panelBrowserTools.add(butExit);
		
		
		//List of function attributes
		listUsedFunctions = new JList(listModel);
		//listUsedFunctions.setVisibleRowCount(8);
		listUsedFunctions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollFuncs = new JScrollPane(listUsedFunctions);
		scrollFuncs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollFuncs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		//Dependency-Textbox
		textDependencies.setEditable(false);
		textDependencies.setBackground(this.getBackground());
		textDependencies.setLineWrap(true);
		scrollDepend.setBorder(javax.swing.BorderFactory.createTitledBorder("Dependencies"));
		
		

		panelFunctionBrowser.add(panelBrowserTools, BorderLayout.NORTH);
		panelFunctionBrowser.add(scrollFuncs, BorderLayout.CENTER);
		panelFunctionBrowser.add(scrollDepend, BorderLayout.SOUTH);
		panelFunctionBrowser.setBorder(javax.swing.BorderFactory.createTitledBorder("Function Browser"));
		panelFunctionBrowser.setPreferredSize(new Dimension(210,0));
		
		IRIS_Attribute[] attr = model.getFunctionAttributesInMeasurement();
		for(int i=0; i<attr.length; i++) {
			listModel.addElement(attr[i].getAttributeName());
		}

		
		
		
		panelMain.setLayout(new BorderLayout());
		panelMain.add(panelSelection, BorderLayout.CENTER);
		panelMain.add(panelFunctionBrowser, BorderLayout.WEST);


		
		//Add actionlistener
		comboFunctions.addActionListener(listenerComboBox);
		
		butApply.addActionListener(listenerButtons);
		butDelete.addActionListener(listenerButtons);
		butSave.addActionListener(listenerButtons);
		butLoad.addActionListener(listenerButtons);
		butSettings.addActionListener(listenerButtons);
		butRename.addActionListener(listenerButtons);
		butExit.addActionListener(listenerButtons);
		
		listUsedFunctions.addListSelectionListener(listenerListBox);
				
		//Designsettings
		comboFunctions.setSelectedIndex(-1);
		
		//Windowsettings
		this.setTitle("RMT - Functions");
		//this.setResizable(false);
		this.setContentPane(panelMain);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		
		//Set windowposition to center
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit(); 
		this.setLocation((tk.getScreenSize().width / 2 - this.getWidth() / 2), (tk.getScreenSize().height / 2 - this.getHeight() / 2));
		this.setLocation(((this.getX() < 0) ? 0 : this.getX()), ((this.getY() < 0) ? 0 : this.getY()));
		this.setVisible(true);
		
		
	}
	
	
	
	//--Inner listenerclasses--
	class ComboBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if(ae.getSource().equals(comboFunctions)) {
				
				if(comboFunctions.getSelectedIndex() != -1) {				
					//Get selected function
					IRIS_FunctionModule func = model.getFunctionInstanceByName((String)comboFunctions.getSelectedItem());
										
					if(func.isScalarValueResult()) {
						comboToAttribute.setVisible(false);
						labelToAttribute.setVisible(true);
						//Set function description
						textDescription.setText("+++Scalar Output+++\n" + func.getFunctionDescription());
						textDescription.setCaretPosition(0);
					}
					else {
						comboToAttribute.setVisible(true);
						labelToAttribute.setVisible(false);
						//Set function description
						textDescription.setText(func.getFunctionDescription());
						textDescription.setCaretPosition(0);
					}										
					
					//Apply panel change
					ref.pack();
					
					butApply.setEnabled(true);
					
				}
				else {
					butApply.setEnabled(false);
					textDescription.setText("");					
					
				}
				
				
			}
		}
		
	}
	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
						
			//Create Attribute using selected functions
			if(ae.getSource().equals(butApply)) {					
					
				//Open input dialog for attributename
				String name = (String) JOptionPane.showInputDialog(ref, "Please enter a name for the new attribute:", "Name attribute", JOptionPane.INFORMATION_MESSAGE, null, null, (String)comboFunctions.getSelectedItem());
				//If the progress is aborted, stop creating a new function
				if(name == null) {
					return;
				}
				if(name.isEmpty()) {
					name = (String)comboFunctions.getSelectedItem();
				}
								
				//Create new Attribute if selected
				if(comboToAttribute.getSelectedIndex() == 0) {
					IRIS_FunctionModule module = model.getFunctionInstanceByName((String)comboFunctions.getSelectedItem());
					FunctionAttribute fa = new FunctionAttribute(name, !module.isScalarValueResult(), FunctionAttribute.OUTPUT_FLOAT, module);					
					
					DiaFunctionSettings.showFunctionSettingsWindow(model, fa, -1, false);
					model.addFunctionAttributeToMeasurement(fa);
					System.out.println("Attribute successfully created");
				}
				
				//Else update chosen attribute
				else {					
						FunctionAttribute fa = (FunctionAttribute)model.getMeasureAttribute((String)comboToAttribute.getSelectedItem(), false);
						IRIS_FunctionModule module = model.getFunctionInstanceByName((String)comboFunctions.getSelectedItem());
						fa.addNewFunction(name, !module.isScalarValueResult(), FunctionAttribute.OUTPUT_FLOAT, module);
						
						DiaFunctionSettings.showFunctionSettingsWindow(model, fa, fa.getUsedFunctionCount()-1, false);
						updateNewAttribute();
						System.out.println("Attribute successfully updated");
				}							
			}
			
			//Show a list to select function to delete
			if(ae.getSource().equals(butDelete)) {
				
				if(listUsedFunctions.getSelectedIndex() != -1) {
					int conf = JOptionPane.showConfirmDialog(model.getCurrentlyFocusedWindow(), "Do you really want to delete this function?",
							"Delete Function", JOptionPane.YES_NO_OPTION);
					if(conf == 0) {
						String dependencies = model.removeFunctionAttributeFromMeasurement((FunctionAttribute)model.getMeasureAttribute((String)listUsedFunctions.getSelectedValue(), true));
						
						if(dependencies != null) {
							JOptionPane.showMessageDialog(ref, "Cannot delete attribute, because the following other\nattributes use it:\n" + dependencies);	
						}
					}	
					
				}
				
			}
			
			//Opens a dialog to choose a name for the function to save
			if(ae.getSource().equals(butSave)) {
				
				if(listUsedFunctions.getSelectedIndex() != -1) {
					FunctionAttribute func = (FunctionAttribute) model.getMeasureAttribute((String)listUsedFunctions.getSelectedValue(), false);
					SaveAndLoad.saveFunctionAttribute(model, func);
				}
			}
			
			//Opens a dialog to choose a file to load
			if(ae.getSource().equals(butLoad)) {
				
				SaveAndLoad.loadFunctionAttribute(model);
			}
			
			//Opens a dialog change the settings later on
			if(ae.getSource().equals(butSettings)) {
				
				if(listUsedFunctions.getSelectedIndex() != -1) {
					IRIS_Attribute[] funcatts = model.getFunctionAttributesInMeasurement();
					for(IRIS_Attribute attr:funcatts) {
						if(attr.getAttributeName().equals(listUsedFunctions.getSelectedValue().toString())) {
							DiaFunctionSettings.showFunctionSettingsWindow(model, (FunctionAttribute)attr, -1, true);
						}
					}					
				}
				
			}
			
			//Opens a dialog to type in a new function name
			if(ae.getSource().equals(butRename)) {
				
				if(listUsedFunctions.getSelectedIndex() != -1) {
					//Open input dialog for attributename
					String name = (String) JOptionPane.showInputDialog(model.getView(), "Please enter a new name for the function:", "Rename", JOptionPane.INFORMATION_MESSAGE, null, null, (String)listUsedFunctions.getSelectedValue());
					//If the progress is aborted, stop renaming
					if(name == null || name.isEmpty()) {
						return;
					}
					else {
						if(!model.renameFunctionAttributeInMeasurement((String)listUsedFunctions.getSelectedValue(), name)) {
							JOptionPane.showMessageDialog(model.getView(), "Cannot rename function because the name is already in use.");	
						}
					}
				}
			}
			
			if(ae.getSource().equals(butExit)) {
				ref.dispose();
			}
		}		
	}
	
	//Set dependency information depending on selected function
	class ListBoxListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent lse) {
			
			if(listUsedFunctions.getSelectedIndex() != -1) {
				
				textDependencies.setText(((FunctionAttribute)model.getMeasureAttribute((String)listUsedFunctions.getSelectedValue(), true)).getParameterDependencies());
				textDependencies.setCaretPosition(0);
			}
			
		}		
	}
	
	
	//--Interface methods--
	//Do nothing
	@Override
	public void updateNewMeasure() {		
	}
	//Do nothing
	@Override
	public void updateNewPacket() {
	}
	//Update combobox and browserlist for new FunctionAttributes
	@Override
	public void updateNewAttribute() {
		
		
		comboFunctions.setSelectedIndex(0);
		
		comboToAttribute.removeAllItems();
		comboToAttribute.addItem("--New Attribute--");
		//List only non-scalar functions to update
		List<IRIS_Attribute> funcList = model.getMeasureAttributesBySpecification(false, false, false, false, true, false);
		for(int i=0; i<funcList.size(); i++) {
			comboToAttribute.addItem(funcList.get(i).getAttributeName());
		}			
		comboToAttribute.setSelectedIndex(0);
		
		//Function browser
		listModel.clear();
		IRIS_Attribute[] attr = model.getFunctionAttributesInMeasurement();
		for(int i=0; i<attr.length; i++) {
			listModel.addElement(attr[i].getAttributeName());
		}
	}
	
}
