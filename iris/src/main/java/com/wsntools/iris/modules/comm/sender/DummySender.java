package com.wsntools.iris.modules.comm.sender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.wsntools.iris.data.Constants;
import com.wsntools.iris.tools.datacollector.sending.AbstractEncoder;
import com.wsntools.iris.tools.datacollector.sending.ISender;

public class DummySender implements ISender {

	private File output = new File(Constants.getPathSavesLogger() + "Dummy.txt");
	
	@Override
	public void send(String port, AbstractEncoder encoder) {
				
		//Append output to dummyfile
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(output, true));
			writer.write(new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date()) + " " + Arrays.toString(encoder.encode()));
			writer.newLine();
			writer.flush();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(writer != null)
				try {writer.close();}
				catch (IOException e) {}
		}
	}

}
