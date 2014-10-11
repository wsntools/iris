/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.data;

import java.io.File;
import java.net.URL;

import com.wsntools.iris.tools.ModuleLoader;

public class Constants {
	
	private static Constants instance;
	private Constants(){
	}
	
	private static Constants getInstance(){
		if (null == instance){
			instance = new Constants();
		}
		return instance;
	}

	//System values (limits etc.)
	private static final int PACKET_ARRAY_START_SIZE = 200;
	private static final int PACKET_BUFFER_MAX_SIZE = 100;
	private static final float PACKET_DEFAULT_EMPTY_NUMBER = (float) -0.0;
	private static final int CHART_MAX_BARS = 100;	

	//Relevant constants for file structures
	private static final String TRACE_ATTRIBUTE_SEPARATOR = ", ";
	private static final String TRACE_DATA_SEPARATOR = "\t";
	
	private static final String ATTR_USER_ALIAS_PREFIX = "[USR]";
	private static final String ATTR_GUI_ALIAS_PREFIX = "[GUI]";
	
	private static final String NAMING_ALLOWED_CHARS = "[0123456789a-zA-Z _\\-]+";
	
	private static final String LOGGING_VALUE_SEPARATOR = ";";

	//All relevant constants for picture and save directories
	private static final String SEP = System.getProperty("file.separator");
//	private static final String CUR_DIR = System.getProperty("user.dir"); //TODO this does not work within a jar
//	private static final String CUR_DIR = Class.class.getResource("/").getPath();
	private static final String CUR_DIR = ModuleLoader.getSourcePath();
	private static final String JAR_SEP = "/";

//	private static final String PATH_PICS_BUTTONS = "pictures" + getSep() + "btn" + getSep();
//	private static final String PATH_PICS_MISC = "pictures" + getSep() + "misc" + getSep();
	private static final String PATH_PICS_BUTTONS = "pictures" + getJarSep() + "btn" + getJarSep();
	private static final String PATH_PICS_MISC = "pictures" + getJarSep() + "misc" + getJarSep();
	
	private static final String PATH_SAVES_FUNCTIONS = "saves" + getSep() + "functions" + getSep();
	private static final String PATH_SAVES_MEASURE = "saves" + getSep() + "measurements" + getSep();
	private static final String PATH_SAVES_TRACES = "saves" + getSep() + "traces" + getSep();
	private static final String PATH_SAVES_BACKGROUNDTRACES = "saves" + getSep() + "backgroundWriter" + getSep();
	private static final String PATH_SAVES_NOISETRACES = "saves" + getSep() + "noisetraces" + getSep();
	private static final String PATH_SAVES_MESSAGES = "saves" + getSep() + "messages" + getSep();
	private static final String PATH_SAVES_SCRIPTS = "saves" + getSep() + "scripts" + getSep();
	private static final String PATH_SAVES_LOGGER = "saves" + getSep() + "logger" + getSep();
	private static final String DIR_QUICK_DUMP = "quickDump" + getSep();
	
	
	
//	private static final String PATH_SAVES_FUNCTIONS = CUR_DIR + getSep() + "saves" + getSep() + "functions" + getSep();
//	private static final String PATH_SAVES_MEASURE = CUR_DIR + getSep() + "saves" + getSep() + "measurements" + getSep();
//	private static final String PATH_SAVES_TRACES = CUR_DIR + getSep() + "saves" + getSep() + "traces" + getSep();
//	private static final String PATH_SAVES_BACKGROUNDTRACES = CUR_DIR + getSep() + "saves" + getSep() + "backgroundWriter" + getSep();
//	private static final String PATH_SAVES_NOISETRACES = CUR_DIR + getSep() + "saves" + getSep() + "noisetraces" + getSep();
//	private static final String PATH_SAVES_MESSAGES = CUR_DIR + getSep() + "saves" + getSep() + "messages" + getSep();
//	private static final String PATH_SAVES_SCRIPTS = CUR_DIR + getSep() + "saves" + getSep() + "scripts" + getSep();
//	private static final String DIR_QUICK_DUMP = CUR_DIR + getSep() + "quickDump" + getSep();	
	
	private static final String PATH_IRIS_PACKAGE = "com" + getSep() + "wsntools" + getSep() + "iris" + getSep();
	private static final String PATH_IRIS_PACKAGE_JAR = "com" + getJarSep() + "wsntools" + getJarSep() + "iris" + getJarSep();

	private static final String DIR_MODULES_ATTRIBUTES = PATH_IRIS_PACKAGE + "modules" + getSep() + "attributes" + getSep();
	private static final String DIR_MODULES_FUNCTIONS = PATH_IRIS_PACKAGE + "modules" + getSep() + "functions" + getSep();
	private static final String DIR_MODULES_INFO = PATH_IRIS_PACKAGE + "modules" + getSep() + "info" + getSep();
	private static final String DIR_MODULES_GUI = PATH_IRIS_PACKAGE + "modules" + getSep() + "gui" + getSep();
	
	//	public static final String DIR_MOTE_MESSAGES = "mote" + SEP;
	private static final String DIR_MOTE_MESSAGES_SENDING = PATH_IRIS_PACKAGE + "mote" + getSep() + "sending" + getSep();
	private static final String DIR_MOTE_MESSAGES_RECEIVING = PATH_IRIS_PACKAGE + "mote" + getSep() + "receiving" + getSep();
	private static final String DIR_MOTE_MESSAGES_SIMPLEWRAPPER = PATH_IRIS_PACKAGE + "mote" + getSep() + "receiving" + getSep(); // for groovy shell (class: simpleWrapperCreator)
	private static final String DIR_SEND_LOG = PATH_IRIS_PACKAGE + "saves" + getSep() + "sendLog" + getSep();

	private static final String JAR_MODULES_ATTRIBUTES = PATH_IRIS_PACKAGE_JAR + "modules" + getJarSep() + "attributes" + getJarSep();
	private static final String JAR_MODULES_FUNCTIONS = PATH_IRIS_PACKAGE_JAR + "modules" + getJarSep() + "functions" + getJarSep();
	private static final String JAR_MODULES_INFO = PATH_IRIS_PACKAGE_JAR + "modules" + getJarSep() + "info" + getJarSep();
	private static final String JAR_MODULES_GUI = PATH_IRIS_PACKAGE_JAR + "modules" + getJarSep() + "gui" + getJarSep();
	//	public static final String JAR_DIR_MOTE_MESSAGES = "mote" + getJarSep();
	private static final String JAR_MOTE_MESSAGES_SENDING = PATH_IRIS_PACKAGE_JAR + "mote" + getJarSep() + "sending" + getJarSep();
	private static final String JAR_MOTE_MESSAGES_RECEIVING = PATH_IRIS_PACKAGE_JAR + "mote" + getJarSep() + "receiving" + getJarSep();
	private static final String JAR_MOTE_MESSAGES_SIMPLEWRAPPER = PATH_IRIS_PACKAGE_JAR + "mote" + getJarSep() + "receiving" + getJarSep();
	private static final String JAR_SEND_LOG = PATH_IRIS_PACKAGE_JAR + "saves" + getJarSep() + "sendLog" + getJarSep();

	
	private static final String NAME_BTN_ADD_SM = "add_small.png";
	private static final String NAME_BTN_APPLY = "apply.png";
	private static final String NAME_BTN_DELETE = "del.png";
	private static final String NAME_BTN_DELETE_ITEM = "del_item.png";
	private static final String NAME_BTN_DELETE_SM = "del_small.png";
	private static final String NAME_BTN_EXIT = "exit.png";
	private static final String NAME_BTN_HELP = "help.png";
	private static final String NAME_BTN_LOAD_FUNC = "load_fnc.png";
	private static final String NAME_BTN_LOAD_DOC = "load_doc.png";
	private static final String NAME_BTN_SETUP = "edit.png";
	private static final String NAME_BTN_SEND = "send.png";
	private static final String NAME_BTN_LOAD_STD = "load_arr.png";
	private static final String NAME_BTN_NEW = "new.png";
	private static final String NAME_BTN_OK = "ok.png";
	private static final String NAME_BTN_OK_SM = "ok_small.png";
	private static final String NAME_BTN_RECORD = "rec.png";
	private static final String NAME_BTN_REFRESH = "refresh.png";
	private static final String NAME_BTN_REFRESH_SM = "refresh_small.png";
	private static final String NAME_BTN_RENAME = "rename.png";
	private static final String NAME_BTN_SAVE = "save.png";
	private static final String NAME_BTN_SELECT_ALL = "select_all.png";
	private static final String NAME_BTN_SELECT_NONE = "select_none.png";
	private static final String NAME_BTN_SETTINGS = "settings.png";
	private static final String NAME_BTN_STOP = "stop.png";
	private static final String NAME_BTN_TRANSFER = "transfer.png";
	private static final String NAME_BTN_IMPORT_MESS = "import_mess.png";
	private static final String NAME_BTN_IMPORT_SCRIPT = "import_scr.png";
	private static final String NAME_BTN_EXPORT_MESS = "export_mess.png";
	private static final String NAME_BTN_EXPORT_SCRIPT = "export_scr.png";
	private static final String NAME_BTN_DECODE_PACKAGE = "decode_pack.png";

	private static final String NAME_MISC_ARROW = "arr.png";

	private static final String LINE_SEP = System.getProperty("line.separator");
	
	public static URL getResource(String Path) {
//		return Thread.currentThread().getContextClassLoader().getResource(Path);
		return ClassLoader.getSystemResource(Path);
	}

	public static int getPacketArrayStartSize() {
		return PACKET_ARRAY_START_SIZE;
	}

	public static int getPacketBufferMaxSize() {
		return PACKET_BUFFER_MAX_SIZE;
	}

	public static float getPacketDefaultEmptyNumber() {
		return PACKET_DEFAULT_EMPTY_NUMBER;
	}

	public static int getChartMaxBars() {
		return CHART_MAX_BARS;
	}

	public static String getTraceAttributeSeparator() {
		return TRACE_ATTRIBUTE_SEPARATOR;
	}

	public static String getTraceDataSeparator() {
		return TRACE_DATA_SEPARATOR;
	}

	public static String getAttrUserAliasPrefix() {
		return ATTR_USER_ALIAS_PREFIX;
	}

	public static String getAttrGuiAliasPrefix() {
		return ATTR_GUI_ALIAS_PREFIX;
	}

	public static String getNamingAllowedChars() {
		return NAMING_ALLOWED_CHARS;
	}

	public static String getLoggingValueSeparator() {
		return LOGGING_VALUE_SEPARATOR;
	}

	public static String getSep() {
		return SEP;
	}
	
	private static String mkdirs(String relPath){
		File temp = new File(ModuleLoader.getSourcePath()+getSep()+relPath);
		if (false == temp.exists()){
			System.out.println("creating: "+temp.getAbsolutePath());
			temp.mkdirs();
		}
		return relPath;
	}

	public static String getPathPicsButtons() {
		return PATH_PICS_BUTTONS;
	}

	public static String getPathPicsMisc() {
		return mkdirs(PATH_PICS_MISC);
	}

	public static String getPathSavesFunctions() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_FUNCTIONS);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_FUNCTIONS;
		}
	}

	public static String getPathSavesMeasure() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_MEASURE);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_MEASURE;
		}
	}

	public static String getPathSavesTraces() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_TRACES);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_TRACES;
		}
	}

	public static String getPathSavesBackgroundtraces() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_BACKGROUNDTRACES);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_BACKGROUNDTRACES;
		}
	}

	public static String getPathSavesNoisetraces() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_NOISETRACES);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_NOISETRACES;
		}
	}

	public static String getPathSavesMessages() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_MESSAGES);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_MESSAGES;
		}
	}

	public static String getPathSavesScripts() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_SCRIPTS);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_SCRIPTS;
		}
	}	
	
	public static String getPathSavesLogger() {
		if (ModuleLoader.insideJar()){
		return mkdirs(PATH_SAVES_LOGGER);
		} else {
			return CUR_DIR + getSep() +PATH_SAVES_LOGGER;
		}
	}

	public static String getJarModulesAttributes() {
		return JAR_MODULES_ATTRIBUTES;
	}

	public static String getJarModulesFunctions() {
		return JAR_MODULES_FUNCTIONS;
	}

	public static String getJarModulesInfo() {
		return JAR_MODULES_INFO;
	}
	public static String getJarModulesGUI() {
		return JAR_MODULES_GUI;
	}

	public static String getJarMoteMessagesSending() {
		return JAR_MOTE_MESSAGES_SENDING;
	}

	public static String getJarMoteMessagesReceiving() {
		return JAR_MOTE_MESSAGES_RECEIVING;
	}

	public static String getJarMoteMessagesSimplewrapper() {
		return JAR_MOTE_MESSAGES_SIMPLEWRAPPER;
	}

	public static String getJarSendLog() {
		return JAR_SEND_LOG;
	}

	public static String getDirQuickDump() {
		if (ModuleLoader.insideJar()){
		return mkdirs(DIR_QUICK_DUMP);
		} else {
			return CUR_DIR + getSep() +DIR_QUICK_DUMP;
		}
	}

	public static String getDirModulesAttributes() {
		return mkdirs(DIR_MODULES_ATTRIBUTES);
	}
	

	public static String getDirModulesFunctions() {
		return mkdirs(DIR_MODULES_FUNCTIONS);
	}

	public static String getDirModulesInfo() {
		return mkdirs(DIR_MODULES_INFO);
	}
	
	public static String getDirModulesGUI() {
		return mkdirs(DIR_MODULES_GUI);
	}

	public static String getDirMoteMessagesSending() {
		return mkdirs(DIR_MOTE_MESSAGES_SENDING);
	}

	public static String getDirMoteMessagesReceiving() {
		return mkdirs(DIR_MOTE_MESSAGES_RECEIVING);
	}

	public static String getDirMoteMessagesSimplewrapper() {
		return mkdirs(DIR_MOTE_MESSAGES_SIMPLEWRAPPER);
	}

	public static String getDirSendLog() {
		return mkdirs(DIR_SEND_LOG);
	}

	public static String getNameBtnAddSm() {
		return NAME_BTN_ADD_SM;
	}

	public static String getNameBtnApply() {
		return NAME_BTN_APPLY;
	}

	public static String getNameBtnDelete() {
		return NAME_BTN_DELETE;
	}

	public static String getNameBtnDeleteItem() {
		return NAME_BTN_DELETE_ITEM;
	}

	public static String getNameBtnDeleteSm() {
		return NAME_BTN_DELETE_SM;
	}

	public static String getNameBtnExit() {
		return NAME_BTN_EXIT;
	}

	public static String getNameBtnHelp() {
		return NAME_BTN_HELP;
	}

	public static String getNameBtnLoadFunc() {
		return NAME_BTN_LOAD_FUNC;
	}

	public static String getNameBtnLoadDoc() {
		return NAME_BTN_LOAD_DOC;
	}

	public static String getNameBtnSetup() {
		return NAME_BTN_SETUP;
	}

	public static String getNameBtnSend() {
		return NAME_BTN_SEND;
	}

	public static String getNameBtnLoadStd() {
		return NAME_BTN_LOAD_STD;
	}

	public static String getNameBtnNew() {
		return NAME_BTN_NEW;
	}

	public static String getNameBtnOk() {
		return NAME_BTN_OK;
	}

	public static String getNameBtnOkSm() {
		return NAME_BTN_OK_SM;
	}

	public static String getNameBtnRecord() {
		return NAME_BTN_RECORD;
	}

	public static String getNameBtnRefresh() {
		return NAME_BTN_REFRESH;
	}

	public static String getNameBtnRefreshSm() {
		return NAME_BTN_REFRESH_SM;
	}

	public static String getNameBtnRename() {
		return NAME_BTN_RENAME;
	}

	public static String getNameBtnSave() {
		return NAME_BTN_SAVE;
	}

	public static String getNameBtnSelectAll() {
		return NAME_BTN_SELECT_ALL;
	}

	public static String getNameBtnSelectNone() {
		return NAME_BTN_SELECT_NONE;
	}

	public static String getNameBtnSettings() {
		return NAME_BTN_SETTINGS;
	}

	public static String getNameBtnStop() {
		return NAME_BTN_STOP;
	}

	public static String getNameBtnTransfer() {
		return NAME_BTN_TRANSFER;
	}

	public static String getNameBtnImportMess() {
		return NAME_BTN_IMPORT_MESS;
	}

	public static String getNameBtnImportScript() {
		return NAME_BTN_IMPORT_SCRIPT;
	}

	public static String getNameBtnDecodePackage() {
		return NAME_BTN_DECODE_PACKAGE;
	}

	public static String getNameMiscArrow() {
		return NAME_MISC_ARROW;
	}

	public static String getLineSep() {
		return LINE_SEP;
	}
	
	public static String getJarSep() {
		return JAR_SEP;
	}
}
