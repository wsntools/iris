/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.panels;

import javax.swing.JComboBox;
import javax.swing.JPanel;
/**
 * @author Sascha Jungen
 *
 */
public class PanelParameter extends JPanel {

private static final long serialVersionUID = 1L;
	
	private JComboBox comboParameter;
	private boolean hasitem;
	
	
	public PanelParameter(String name, String[] defval, boolean itemforfunc) {
		
		hasitem = itemforfunc;
		if(itemforfunc) {
			String[] newval = new String[defval.length+1];
			newval[0] = "Prev.Function";
			for(int i=0; i<defval.length; i++) {
				newval[i+1] = defval [i];
			}
			comboParameter = new JComboBox(newval);
		}
		else {
			comboParameter = new JComboBox(defval);
		}
		
		
		this.add(comboParameter);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(name));
	}
	
	public String getSelectedParameter() {
		
		return comboParameter.getSelectedItem().toString();
	}
	public int getSelectedIndex() {
		
		return comboParameter.getSelectedIndex();
	}
	public void setSelectedParameter(String name) {
		
		comboParameter.setSelectedItem(name);
	}
	public boolean hasItemForUsingPreviousFunction() {
		
		return hasitem;
	}
}
