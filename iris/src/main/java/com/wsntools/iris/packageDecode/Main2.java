/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.wsntools.iris.packageDecode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main2 {
static boolean running = true;
static BlinkToRadio radio = null;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		BufferedReader read  = new BufferedReader(new InputStreamReader(System.in));
		String in;
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
			
			if(in.equals("hi"))
				System.out.println("Hello");
			
			if(in.startsWith("t1"))
			{
				radio = new BlinkToRadio();
				for(int i = 0 ;i<20;i++)
				{
					radio.set_counter(i);
					radio.set_nodeid(i);
					System.out.println(i+" : "+radio);
				}
			}
			
			if(in.startsWith("t3"))
			{
				String hex = "00 FF FF 00 02 04 00 06 FF 02 FF 04 04 04 04 04 04 04 04 04 04 04 04 04 04 FF";
				System.out.println("orignial : "+hex);
				//System.out.println(PackageConvert.parseToSimpleValues(hex));
			}
			
			if(in.startsWith("t2"))
			{
				byte[] bo = new byte[4];
				
				radio = new BlinkToRadio();
				for(int i = 0 ;i<255;i++)
				{
					byte[] b = parseHex(Integer.toHexString(i));
					if(b.length>1)
					{
						bo[0] = b[0];
						bo[1] = b[1];
						bo[2] = b[0];
						bo[3] = b[1];
					}
					else
					{
						bo[1] = b[0];
						bo[3] = b[0];
					}
					
					radio = new BlinkToRadio(bo);
					System.out.println(i+" : "+radio);
				}
			}
			if(in.startsWith("c -"))
			{
				in = in.replace("c -", "");
				in = in.toUpperCase().replaceAll(" ", "");
				String work = in.substring(in.length()-4);
			byte[] bo = new byte[4];
				byte[] b = parseHex(work);
				
				if(b.length>1)
				{
					bo[0] = b[0];
					bo[1] = b[1];
					bo[2] = b[2];
					bo[3] = b[3];
				}
				else
				{
					bo[1] = b[0];
					bo[3] = b[1];
				}
				radio = new BlinkToRadio(bo);
				System.out.println(radio);
			}
		}	
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

	
}
