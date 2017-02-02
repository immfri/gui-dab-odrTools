package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.output.*;


public class ModFileLoader {

	private Modulator mod;
	
	
	public ModFileLoader(File modFile, ArrayList<Output> outputList) throws IOException {
		
		// Set Modulator
		if (modFile.exists()) {
			setModulator(modFile);
			
			// configure Mudulator
			if (mod != null) {
				
				loadSection("remotecontrol", modFile);
				loadSection("log", modFile);
				loadSection("input", modFile);
				loadSection("modulator", modFile);
				loadSection("firfilter", modFile);
				
				// -> correct output
				loadSection(mod.getOutput().getValue() + "output", modFile);
				
				if (mod.getOutput().getValue().contentEquals("uhd")) {
					loadSection("delaymanagement", modFile);
				}
				else if (mod.getOutput().getValue().contentEquals("zmq")) {
					loadSection("zmqoutput", modFile);
				}
		
				// Set Modulator to right ETIZeromq-Output
				for (Output out: outputList) {
					
					// find ETI-Zeromq
					if (out.getFormat().getValue().contentEquals("zmq+tcp")) {
						
						// ETIZeromq-Name == Modfile-Name (without <.ini>)
						if (out.getName().getValue().contentEquals(modFile.getName().substring(0, modFile.getName().indexOf(".ini")))) {
							((ETIZeromq)out).setMod(mod);	
							break;
						}
					}
				}
			}
			else {
				// DabMod: Output invalid
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error: DabMod-Load");
				alert.setHeaderText("In File <"+modFile.getAbsolutePath()+"> the Output-Parameter is invalid!");
				alert.show();
			}
		}
		else {
			// Modfile not exist
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error: DabMod-File");
			alert.setHeaderText("File <"+modFile.getAbsolutePath()+"> not exist!");
			alert.show();
		}	
	}


	private void setModulator(File modFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(modFile));
		String line = reader.readLine();
		
		while (line != null) {
			
			if (line.indexOf("output=") == 0) {
				line = line.replaceAll("\\s","");

				switch (line.substring(7)) {
				
				case "uhd":
					mod = getUhdDevice(modFile);
					break;
					
				case "file":
					mod = new IQFile();
					break;
				
				case "zmq":
					mod = new IQZeromq();
				}
			}
			line = reader.readLine();
		}
		
		reader.close();
	}

	
	private Modulator getUhdDevice(File modFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(modFile));
		String line;
		
		do {
			line = reader.readLine();
			
			if (line.indexOf("type=") == 0) {
				line = line.replaceAll("\\s","");
				reader.close();
				
				switch (line.substring(5)) {
				case "b100": 	return new B100();
				case "b200": 	return new B200();
				case "usrp1": 	return new USRP1();
				case "ursp2":	return new USRP2();
				default:		return null;
				}
			}
		} while (line != null);
		
		reader.close();
		return null;
	}
	
	
	private void loadSection(String sectionName, File modFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(modFile));
		String line;
		
		// Find Section
		do {
			line = reader.readLine(); 
			if (line.contains("[" + sectionName + "]")) break;
		}
		while (line != null);
				
		// Read all Sections
		line = reader.readLine(); 
		
		while (line != null) {
			String[] strings = line.split("=");	
			
			// Section end
			if (line.contains("[") && line.contains("]") && !line.contains(";")) break;
			
			// Line valid
			if (strings.length > 1) {

				String name = strings[0].replaceAll("\\s","");
				String value = strings[1].replaceAll("\\s","");	

				// Not Comment Line
				if (!name.isEmpty() && name.indexOf(";") == -1) {

					switch (name) {				
					
					// remotecontrol
					case "telnet":			mod.getTelnet().setValue(getBool(value));											break;
					case "telnetport":		mod.getTelnetport().setValue(value);												break;
					case "zmqctrl":			mod.getZmqctrl().setValue(getBool(value));											break;
					case "zmqctrlendpoint":	mod.getZmqctrlendpoint().setValue(value.substring(value.lastIndexOf(":")+1));		break;
					
					// log (fileoutput)
					case "syslog":			mod.getSyslog().setValue(getBool(value));											break;
					case "filelog":			mod.getFilelog().setValue(getBool(value));											break;
					case "filename": 		if (sectionName.contentEquals("log")) {				// log-Filename
												mod.getFilename().setValue(value);	
											}
											else if (sectionName.contentEquals("fileoutput")) {	// fileoutput
												((IQFile)mod).getFile().setValue(value);
											}
											break;
					
					// input -> only ZMQ
					// transport -> is set up (fix)
					// source -> set up later in Output-Pane
					case "max_frames_queued": mod.getMax_frames_queued().setValue(value);										break;
					
					// modulator
					case "gainmode":		if (mod.getGainModeList().contains(value)) mod.getGainmode().setValue(value);		break;
					// mode -> ignored, used same to ensemble
					case "digital_gain": 	mod.getDigital_gain().setValue(getDouble(value));									break;
					case "rate":			mod.getRate().setValue(""+getInt(value));											break;
					case "dac_clk_rate":	mod.getDac_clk_rate().setValue(getInt(value));										break;
					
					// fir filter
					case "enabled":			mod.getEnabled().setValue(getBool(value));											break;
					case "filtertapsfile":	mod.getFiltertapsfile().setValue(value);											break;
					
					// File-Output
					// filename -> section:log
					case "format":			if (((IQFile)mod).getFormatList().contains(value)) ((IQFile)mod).getFormat().setValue(value);	break;
					
					// UHD-Output
					// device -> only blank
					case "subdevice":			((USRP1)mod).getSubdevice().setValue(value);									break;
					case "master_clock_rate": 	((UHD)mod).getMaster_clock_rate().setValue(""+getInt(value));					break;
					// type -> set up before
					case "txgain":				((UHD)mod).getTxgain().setValue(getDouble(value));								break;
					case "frequency":			((UHD)mod).getFrequency().setValue(""+getInt(value));							break;
					// channel -> work only with frequency
					case "refclk_source":		if (((UHD)mod).getRefclk_sourceList().contains(value)) {
													((UHD)mod).getRefclk_source().setValue(value);
												}																				break;
					case "pps_source":			if (((UHD)mod).getPps_sourceList().contains(value)) {
													((UHD)mod).getPps_source().setValue(value);
												}																				break;
					case "behaviour_refclk_lock_lost": 	if (((UHD)mod).getBehaviour_refclk_lock_lostList().contains(value)) {
															((UHD)mod).getBehaviour_refclk_lock_lost().setValue(value);
														}																		break;
					case "max_gps_holdover_time":		((UHD)mod).getMax_gps_holdover_time().setValue(""+getInt(value));		break;
					
					// Zmq Output
					case "listen": 		((IQZeromq)mod).getListen().setValue(value.substring(value.indexOf("*:")+2));			break;
					case "socket_type":	if (((IQZeromq)mod).getSocketTypeList().contains(value)) {
											((IQZeromq)mod).getSocketType().setValue(value);
										}																						break;
					// delaymanagement -> only for UHD
					case "synchronous":			((UHD)mod).getSynchronous().setValue(getBool(value));							break;
					case "mutenotimestamps": 	((UHD)mod).getMutenotimestamps().setValue(getBool(value));						break;
					case "offset":				((UHD)mod).getOffset().setValue(""+getDouble(value));							break;
					}
					
					// tii -> not supported
				}
			}
			line = reader.readLine();
		} 
		reader.close();
	}


	private Double getDouble(String value) {
		String[] numbers = value.split("\\.");
		
		if (numbers.length == 2) {
			
			if (numbers[0].matches("\\d+") && numbers[1].matches("\\d+")) {
				return Double.parseDouble(value);
			}
		}	
		return 0.8;
	}


	private boolean getBool(String value) {
		if (value.contentEquals("1")) return true;
		
		return false;
	}
	
	
	private int getInt(String value) {
		if (value.matches("\\d*")) return Integer.parseInt(value);
	
		return 0;
	}

}
