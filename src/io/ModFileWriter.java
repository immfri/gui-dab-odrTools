package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.Property;
import model.output.Modulator;
import model.output.UHD;

public class ModFileWriter extends FileWriter {

	private Modulator modulator;
	private ArrayList<Field> fields;
	private String outputType;

	public ModFileWriter(File file, Modulator modulator) throws IOException {
		super(file);
		
		this.modulator = modulator;
		
		// All attributes
		fields = new ArrayList<>(Arrays.asList(modulator.getClass().getDeclaredFields()));
		
		Class<?> superClass = modulator.getClass().getSuperclass();
		
		while (superClass != null) {
			fields.addAll(0, Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		
		
		// Remote control
		writeSection("remotecontrol", 	0,4);
		writeSection("log", 			4,7);
		writeSection("input", 			7,10);
		writeSection("modulator", 		11,15);
		writeSection("firfilter", 		15,17);
		writeSection("output", 			17,18);
		
		switch (outputType) {	
		case "file":
			writeSection("fileoutput", 		18,20);
			break;
			
		case "uhd":
			if (((UHD)modulator).getType().getValue().contentEquals("usrp1")) {
				writeSection("uhdoutput", 	18,36);
			}
			else {
				writeSection("uhdoutput", 	18,35);
			}	
			writeSection("delaymanagement", 32,35);
			break;
			
		default:	// ZMQ-Output
			writeSection("zmqoutput", 		18,20);
		}
	}

	
	private void writeSection(String sectionName, int fromIndex, int toIndex) throws IOException {
		
		// start section
		this.write("[" + sectionName + "]\n");
	
		// Attributes
		for (int index = fromIndex; index < toIndex; index++) {
			fields.get(index).setAccessible(true);

			Object value = null;
			try {
				value = ((Property<?>) fields.get(index).get(modulator)).getValue();
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			// No value
			if (value != null) { 

				// Value is not empty
				if (!value.toString().isEmpty()) {
					
					String name = fields.get(index).getName();

					// Writing
					
					// convert boolean
					if (value.toString().contentEquals("false") || value.toString().contentEquals("true")) {
						if ((boolean)value == true) this.write(name +"=1\n");
						else 						this.write(name +"=0\n");
					}
					else if (name.contains("zmqctrlendpoint")) {
						this.write(name +"=tcp://127.0.0.1:"+ value +"\n");	
					}
					else if (name.contains("listen")) {
						this.write(name +"=tcp://*:"+ value +"\n");	
					}
					else if (name.contentEquals("file")) {		// I/Q-File: fileoutput -> rename file to filename (filename exist in Modulator)
						this.write("filename="+ value +"\n");	
					}
					else {
						this.write(name +"="+ value +"\n");	
					}
					
					// Sub-section: Output
					if (name.contains("output")) {
						outputType = value.toString();
					}
					
					// write by UHD-output: subdevice/master_clock_rate
					if (index == 25) {
						index = 33;
					}
				}
			}
		}
		this.write("\n");
	}
}
