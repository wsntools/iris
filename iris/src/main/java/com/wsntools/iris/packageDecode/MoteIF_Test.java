/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.packageDecode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.wsntools.iris.packageDecode.parser.ConvertMessageListener;
import com.wsntools.iris.packageDecode.parser.hexParser;
import com.wsntools.iris.packageDecode.testClasses.Test_ArrayValue;
import com.wsntools.iris.packageDecode.testClasses.Test_SimpleValues;
import com.wsntools.iris.packageDecode.testClasses.Test_Union;

import net.tinyos.message.Message;
import net.tinyos.message.MoteIF;



public class MoteIF_Test implements ConvertMessageListener {
private static boolean running = true;
MoteIF motel ;
Hashtable templateTbl = new Hashtable();

	public static void main(String[] args)
	{
		MoteIF_Test test = new MoteIF_Test();
	}
	
	public MoteIF_Test()
	{
		BufferedReader read  = new BufferedReader(new InputStreamReader(System.in));
		String in;
		
		hexParser pars = new hexParser();
		BlinkToRadio radioTest = new BlinkToRadio();
		pars.addConvertMessageListener(this, radioTest);
		pars.addConvertMessageListener(this, new Test_ArrayValue());
		pars.addConvertMessageListener(this, new Test_SimpleValues());
		pars.addConvertMessageListener(this, new Test_Union());;
		while(running)
		{
			in = null;
			try {
				in = read.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(in.equals("exit"))
				System.exit(1);
			
			if(in.startsWith("parse -"))
			{
				in = in.replace("parse -","");
				System.out.println(convertByteArray(parseHex(in.replaceAll(" ",""))));	
				pars.readByteArray(parseHex(in.replaceAll(" ","")));
			}
		}
	}
	
	public static String convertByteArray(byte[] b)
	{
		String t = "";
		for(byte be: b)
		{
			t = t+" "+ Byte.toString(be);
		}
		return t;
	}
	
	public static byte[] parseHex(String hex)
	{
		byte[] out = new byte[hex.length()];
		int i = 0;
		hex =hex.replaceAll(" ", "");
		while(!hex.isEmpty())
		{
			int buf = Integer.parseInt(hex.substring(0, 1).toUpperCase(),16);
			out[i] = Byte.parseByte(Integer.toString(buf));
			hex = hex.substring(1);
			i++;
		}
		return out;
	}
	

	@Override
	public void messageReceived(long timeStamp, Message m) {
				
	}
}

