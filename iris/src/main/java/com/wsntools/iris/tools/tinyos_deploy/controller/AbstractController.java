/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.tinyos_deploy.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.wsntools.iris.tools.tinyos_deploy.model.Model;

public abstract class AbstractController implements ActionListener {

	@Override
	public abstract void actionPerformed(ActionEvent e);

	public abstract Model getModel();
}
