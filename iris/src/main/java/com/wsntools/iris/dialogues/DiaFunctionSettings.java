/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.dialogues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

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
import com.wsntools.iris.panels.PanelFilter;
import com.wsntools.iris.panels.PanelMapping;
import com.wsntools.iris.panels.PanelParameter;

public class DiaFunctionSettings extends JDialog {

	private Model model;
	private DiaFunctionSettings ref = this;

	private JPanel panelMain = new JPanel(new BorderLayout());
	private JPanel panelMap;

	// Measure Mapping
	private PanelMapping[] arrMapping;

	// Filter Settings
	private PanelFilter panelInputFilter;
	private Map<IRIS_Attribute, List<float[]>> resultFilterSets;

	// Function Settings
	private JPanel panelCombo = new JPanel();
	private JComboBox comboSubfunction;
	private JButton butApply;

	private JPanel[] panelSettings;
	private PanelSettings[][] arrPanelSettings;
	private JPanel[] panelParameter;
	private PanelParameter[][] arrPanelParameter;
	private FunctionAttribute resultFuncSets;	

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
	private ComboBoxListener listenerCombo = new ComboBoxListener();
	
	// Constructor for function settings dialog
	private DiaFunctionSettings(Model m, FunctionAttribute fa, int index,
			boolean tocancel) {
		super(m.getCurrentlyFocusedWindow(), true);
		model = m;

		resultFuncSets = fa;
		// First fill combobox with all used subfunctions
		comboSubfunction = new JComboBox();
		for (int i = 0; i < fa.getUsedFunctionCount(); i++) {
			comboSubfunction.addItem((i + 1) + ". "
					+ fa.getAllUsedFunctions()[i]);
		}

		panelCombo.add(new JLabel("Subfunction in order of appliance:"));
		panelCombo.add(comboSubfunction);

		panelParameter = new JPanel[fa.getUsedFunctionCount()];
		arrPanelParameter = new PanelParameter[fa.getUsedFunctionCount()][];
		panelSettings = new JPanel[fa.getUsedFunctionCount()];
		arrPanelSettings = new PanelSettings[fa.getUsedFunctionCount()][];

		// Build all subpanels with parameters and settings

		// Create attributelist for comboboxes
		// Filter out own attribute in the list
		boolean attrexists = model.getMeasureAttribute(fa.getAttributeName(), false) != null;
		int ind = 0;		
		List<IRIS_Attribute> listattr = model.getMeasureAttributes(true);
		String[] attrnames = new String[listattr.size() + (attrexists ? -1 : 0)];
		for (int i = 0; i < listattr.size(); i++) {
			String attrname = listattr.get(i).getAttributeName();
			if (!attrname.equals(fa.getAttributeName())) {
				attrnames[ind++] = attrname;
			}
		}
		// Parameter
		for (int i = 0; i < panelParameter.length; i++) {
			IRIS_FunctionModule func = model.getFunctionInstanceByName(fa
					.getAllUsedFunctions()[i]);
			panelParameter[i] = new JPanel(new GridLayout(
					func.getParameterCount(), 1));
			panelParameter[i].setBorder(javax.swing.BorderFactory
					.createTitledBorder("Parameter"));
			panelParameter[i].setPreferredSize(new Dimension(200, 400));
			arrPanelParameter[i] = new PanelParameter[func.getParameterCount()];
			for (int j = 0; j < func.getParameterCount(); j++) {
				arrPanelParameter[i][j] = new PanelParameter(
						func.getParameterNames()[j], attrnames, (i != 0));
				arrPanelParameter[i][j]
						.setSelectedParameter(((IRIS_Attribute[]) fa
								.getFunctionInformation(i)[0])[j]
								.getAttributeName());
				panelParameter[i].add(arrPanelParameter[i][j]);
			}
		}
		// Settings
		for (int i = 0; i < panelSettings.length; i++) {
			IRIS_FunctionModule func = model.getFunctionInstanceByName(fa
					.getAllUsedFunctions()[i]);
			panelSettings[i] = new JPanel(new GridLayout(
					func.getSettingsCount(), 1));
			panelSettings[i].setBorder(javax.swing.BorderFactory
					.createTitledBorder("Settings"));
			panelSettings[i].setPreferredSize(new Dimension(140, 400));
			arrPanelSettings[i] = new PanelSettings[func.getSettingsCount()];
			for (int j = 0; j < func.getSettingsCount(); j++) {
				arrPanelSettings[i][j] = new PanelSettings(
						func.getSettingNames()[j], Float.toString(func
								.getDefaultSettings()[j]));
				arrPanelSettings[i][j]
						.setSettingsText(Float.toString(((float[]) fa
								.getFunctionInformation(i)[1])[j]));
				panelSettings[i].add(arrPanelSettings[i][j]);
			}
		}

		// Create filter-panelarray and add to filterlist
		panelInputFilter = new PanelFilter(model, fa.getFunctionFilter());

		panelInputFilter.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Domain"));
		panelInputFilter.setPreferredSize(new Dimension(250, 400));

		panelMap = new JPanel(new BorderLayout());

		butOK.setPreferredSize(new Dimension(28, 28));
		panelButton.add(butOK);

		if (tocancel) {
			butApply = new JButton(new ImageIcon(Constants.getResource(Constants.getPathPicsButtons()
					+ Constants.getNameBtnApply())));
			butApply.setPreferredSize(new Dimension(28, 28));
			panelButton.add(butApply);
			butApply.addActionListener(listenerButtons);

			butCancel.setPreferredSize(new Dimension(28, 28));
			panelButton.add(butCancel);
			butCancel.addActionListener(listenerButtons);

		}

		panelMain.add(panelMap, BorderLayout.CENTER);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		// Add actionlistener
		butOK.addActionListener(listenerButtons);
		comboSubfunction.addActionListener(listenerCombo);

		// Set
		comboSubfunction.setSelectedIndex((index > -1) ? index : 0);

		// Windowsettings
		this.setTitle("IRIS - Function Settings: " + fa.getAttributeName());
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
	
	public static FunctionAttribute showFunctionSettingsWindow(Model m,
			FunctionAttribute fa, int funcindex, boolean tocancel) {
		DiaFunctionSettings vmap = new DiaFunctionSettings(m, fa, funcindex, tocancel);
		return vmap.getFuncSetsResults();
	}
	
	private FunctionAttribute getFuncSetsResults() {

		return resultFuncSets;
	}
	
	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// Apply mapping changes
			if (ae.getSource().equals(butOK)) {
				
				// Apply all changes to the function attribute

				// First check if at least one parameter of all used
				// functions after the first refers to the previous result
				boolean ispart, crossref = false;
				for (int i = 1; i < arrPanelParameter.length; i++) {
					ispart = false;
					for (int j = 0; j < arrPanelParameter[i].length; j++) {

						if (arrPanelParameter[i][j].getSelectedIndex() == 0) {
							ispart = true;
							break;
						}
					}

					if (!ispart) {
						JOptionPane
								.showMessageDialog(
										model.getCurrentlyFocusedWindow(),
										(resultFuncSets
												.getAllUsedFunctions()[i] + ": One attribute must refer to the previous function."));
						return;
					}
				}
				//Also check if there is a cross reference between two attributes
				String crossrefd = "";
				for(int i=0; i<arrPanelParameter.length; i++) {
					for(int j=0; j<arrPanelParameter[i].length; j++) {

						if(0 != arrPanelParameter[i][j].getSelectedIndex() && model.getMeasureAttribute(arrPanelParameter[i][j].getSelectedParameter(), true).isFunctionAttribute()) {
							
							for (FunctionAttribute tocheck:((FunctionAttribute)model.getMeasureAttribute(arrPanelParameter[i][j].getSelectedParameter(), true)).getFunctionAttributeDependencies()) {
								if (tocheck.equals(resultFuncSets)) {
									crossref = true;
									crossrefd = arrPanelParameter[i][j].getSelectedParameter();
									break;
								}
							}
							if (crossref) break;
						}
					}
					
					if(crossref) {
						JOptionPane.showMessageDialog(model.getView(), (resultFuncSets.getAllUsedFunctions()[i] + ": There is a cross-reference between " + crossrefd + " and this attribute."));
						return;
					}
				}	
				// Update all attributes
				IRIS_Attribute[] newAttr;
				float[] newSets;
				for (int i = 0; i < resultFuncSets.getUsedFunctionCount(); i++) {

					// Build parameter list
					newAttr = new IRIS_Attribute[arrPanelParameter[i].length];
					for (int j = 0; j < newAttr.length; j++) {
						// If reference to function itself is set, fill in a
						// null
						if ((i != 0)
								&& (arrPanelParameter[i][j]
										.getSelectedIndex() == 0)) {
							newAttr[j] = null;
						} else {
							newAttr[j] = model
									.getMeasureAttribute(arrPanelParameter[i][j]
											.getSelectedParameter(), true);
						}
					}

					// Build settings list
					newSets = new float[arrPanelSettings[i].length];
					for (int j = 0; j < newSets.length; j++) {
						// Try to convert input to float number - if fails,
						// use default values
						try {
							newSets[j] = Float
									.parseFloat(arrPanelSettings[i][j]
											.getSettingsText());
						} catch (NumberFormatException nfe) {
							newSets[j] = model
									.getFunctionInstanceByName(
											resultFuncSets
													.getAllUsedFunctions()[comboSubfunction
													.getSelectedIndex()])
									.getDefaultSettings()[i];
						}
					}

					resultFuncSets.setFunctionInformation(i, newAttr,
							newSets);
				}

				// Build filter based on chosen values
				Map<IRIS_Attribute, List<float[]>> filter = panelInputFilter.getFilterSettings();

				if (filter.size() > 0) {
					resultFuncSets.setFunctionFilter(filter);
				} else {
					resultFuncSets.setFunctionFilter(null);
				}

				model.setCurrentMeasureIndex(model.getCurrentMeasureIndex());
				ref.dispose();
			}

			if (ae.getSource().equals(butCancel)) {

				ref.dispose();
			}

			if (ae.getSource().equals(butApply)) {

				// Apply all changes to the function attribute

				// First check if at least one parameter of all used functions
				// after the first refers to the previous result
				boolean ispart, crossref = false;
				for (int i = 1; i < arrPanelParameter.length; i++) {
					ispart = false;
					for (int j = 0; j < arrPanelParameter[i].length; j++) {

						if (arrPanelParameter[i][j].getSelectedIndex() == 0) {
							ispart = true;
							break;
						}
					}

					if (!ispart) {
						JOptionPane
								.showMessageDialog(
										model.getView(),
										(resultFuncSets.getAllUsedFunctions()[i] + ": One attribute must refer to the previous function."));
						return;
					}
				}
				//Also check if there is a cross reference between two attributes
				String crossrefd = "";
				for(int i=0; i<arrPanelParameter.length; i++) {
					for(int j=0; j<arrPanelParameter[i].length; j++) {

						if(model.getMeasureAttribute(arrPanelParameter[i][j].getSelectedParameter(), true).isFunctionAttribute()) {
							
							for (FunctionAttribute tocheck:((FunctionAttribute)model.getMeasureAttribute(arrPanelParameter[i][j].getSelectedParameter(), true)).getFunctionAttributeDependencies()) {
								if (tocheck.equals(resultFuncSets)) {
									crossref = true;
									crossrefd = arrPanelParameter[i][j].getSelectedParameter();
									break;
								}
							}
							if (crossref) break;
						}
					}
					
					if(crossref) {
						JOptionPane.showMessageDialog(model.getView(), (resultFuncSets.getAllUsedFunctions()[i] + ": There is a cross-reference between " + crossrefd + " and this attribute."));
						return;
					}
				}

				// Update all attributes
				IRIS_Attribute[] newAttr;
				float[] newSets;
				for (int i = 0; i < resultFuncSets.getUsedFunctionCount(); i++) {

					// Build parameter list
					newAttr = new IRIS_Attribute[arrPanelParameter[i].length];
					for (int j = 0; j < newAttr.length; j++) {
						// If reference to function itself is set, fill in a
						// null
						if ((i != 0)
								&& (arrPanelParameter[i][j].getSelectedIndex() == 0)) {
							newAttr[j] = null;
						} else {
							newAttr[j] = model
									.getMeasureAttribute(arrPanelParameter[i][j]
											.getSelectedParameter(), true);
						}
					}

					// Build settings list
					newSets = new float[arrPanelSettings[i].length];
					for (int j = 0; j < newSets.length; j++) {
						// Try to convert input to float number - if fails, use
						// default values
						try {
							newSets[j] = Float
									.parseFloat(arrPanelSettings[i][j]
											.getSettingsText());
						} catch (NumberFormatException nfe) {
							newSets[j] = model
									.getFunctionInstanceByName(
											resultFuncSets
													.getAllUsedFunctions()[comboSubfunction
													.getSelectedIndex()])
									.getDefaultSettings()[i];
						}
					}

					resultFuncSets.setFunctionInformation(i, newAttr, newSets);
				}

				// Build filter based on chosen values
				Map<IRIS_Attribute, List<float[]>> filter = panelInputFilter.getFilterSettings();

				if (filter.size() > 0) {
					resultFuncSets.setFunctionFilter(filter);
				} else {
					resultFuncSets.setFunctionFilter(null);
				}

				model.setCurrentMeasureIndex(model.getCurrentMeasureIndex());

			}
		}
	}
	
	private class PanelSettings extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private JTextField textSets = new JTextField(5);
		
		
		public PanelSettings(String name, String defval) {
			
			textSets.setText(defval);
			
			this.add(textSets);
			this.setBorder(javax.swing.BorderFactory.createTitledBorder(name));
		}
		
		public String getSettingsText() {
			
			return textSets.getText();
		}
		public void setSettingsText(String txt) {
			
			textSets.setText(txt);
		}
	}
	
	class ComboBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			if (ae.getSource().equals(comboSubfunction)
					&& comboSubfunction.getSelectedIndex() != -1) {
				panelMap.removeAll();
				panelMap.add(panelCombo, BorderLayout.NORTH);
				panelMap.add(
						panelParameter[comboSubfunction.getSelectedIndex()],
						BorderLayout.WEST);
				panelMap.add(
						panelSettings[comboSubfunction.getSelectedIndex()],
						BorderLayout.CENTER);
				panelMap.add(panelInputFilter, BorderLayout.EAST);
				panelMap.repaint();
				// Apply panel change
				ref.pack();
			}
		}
	}
}
