/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.tools.datacollector.sending;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * GOOD Konstruktor mit params, eine Instanz pro Nachricht
 *	 public AbstractEncoder(@ParameterMapping(name = "a") int a ,@ParameterMapping(name = "b") String b)
 *	 {
 *	 }
 *
 * BAD (TINYOS) Eine Instanz für alle Nachrichtentypen
 *	 public AbstractEncoder() 
 *   {
 *	 }
 * 
 * @author Sascha Hevelke M#: 2257285
 *
 */
public abstract class AbstractEncoder {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ParameterMapping {
		public String name();
	}

	public abstract byte[] encode();

	public abstract byte[] getBytes();

}
