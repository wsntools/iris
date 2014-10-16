package com.wsntools.iris.data;

import com.wsntools.iris.interfaces.IRIS_Attribute;
import com.wsntools.iris.interfaces.IRIS_ModuleInfo;

/**
 * For displaying the result of functions with scalar output
 * @author Sascha Jungen
 *
 */
public class FunctionInfo implements IRIS_ModuleInfo {
	
	private FunctionAttribute funcAttribute;
	
	public FunctionInfo(FunctionAttribute fa) {
		funcAttribute = fa;
	}
	
	public FunctionAttribute getFunctionAttribute() {
		return funcAttribute;
	}

	@Override
	public String getModuleInfoName() {
		IRIS_Attribute[] params = funcAttribute.getParameter(funcAttribute.getUsedFunctionCount()-1);
		String paramString = "( ";
		for(IRIS_Attribute attr: params) paramString += attr.getAttributeName() + " ";
		paramString += ")";
		return funcAttribute.getAttributeName() + paramString;
	}

	@Override
	public String getResult(Measurement meas) {
		String[] res = funcAttribute.getValuesString(meas.getAllPacketsInOrder());
		if(res.length > 0)
			return res[0];
		else
			return "ERR";
	}

}
