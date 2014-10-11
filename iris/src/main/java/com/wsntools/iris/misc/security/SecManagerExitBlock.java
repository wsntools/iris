/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.misc.security;

import java.io.FileDescriptor;

public class SecManagerExitBlock extends SecurityManager {

	public void checkExit(int status) {
		if(status != 0)
			throw new SecurityException();
	}
	
	public void checkAccept(String host, int port) {}
	public void checkConnect(String host, int port) {}
	public void checkConnect(String host, int port, Object executionContext) {}
	public void checkListen(int port) {}
	 
	public void checkAccess(Thread thread) {}
	public void checkAccess(ThreadGroup threadgroup) {}
	 
	public void checkCreateClassLoader() {}
	 
	public void checkDelete(String filename) {}
	public void checkLink(String library) {}
	public void checkRead(FileDescriptor filedescriptor) {}
	public void checkRead(String filename) {}
	public void checkRead(String filename, Object executionContext) {}
	public void checkWrite(FileDescriptor filedescriptor) {}
	public void checkWrite(String filename) {}
	 
	public void checkExec(String command) {}
	 	 
	public void checkPackageAccess(String packageName) {}
	public void checkPackageDefinition(String packageName) {}
	 
	public void checkPropertiesAccess() {}
	public void checkPropertyAccess(String key) {}
	public void checkPropertyAccess(String key, String def) {}
	 
	public void checkSetFactory() {}
	 
	public boolean checkTopLevelWindow(Object window) { return true; }

}
