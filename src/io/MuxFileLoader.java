package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.*;
import model.output.*;


public class MuxFileLoader {
	
	private final String dabmod = "odr-dabmod";
	
	private Multiplex mux;
	private ArrayList<Component> componentList;		// need for visibility of Service-/Subchannel-ChoiceBox in ComponentTitledPane
	private ArrayList<Output> outputList;
	private ArrayList<Subchannel> subchannelList;
	
	
	public MuxFileLoader(File muxFile, File bashFile) throws IOException {
		mux = Multiplex.getInstance();
		
		loadSection("general", muxFile, "mux");
		loadSection("remotecontrol", muxFile, "mux");
		loadSection("ensemble", muxFile, "mux");
		
		// Services
		loadSectionList("services", muxFile);
		
		// Subchannels
		subchannelList = new ArrayList<>();
		loadSectionList("subchannels", muxFile);
		mux.getSubchannelList().addAll(subchannelList);
		subchannelList.clear();
		
		// Components
		componentList = new ArrayList<>();
		
		loadSectionList("components", muxFile);
		
		mux.getComponentList().addAll(componentList);
		componentList.clear();
		
		// Outputs
		outputList = new ArrayList<>();
		
		loadOutputSection(muxFile);
		
		//------------ load advanced Output Parameters from DabMod ------------------
		for (File modFile: getDabModFiles(bashFile)) {
			new ModFileLoader(modFile, outputList);
		}
		
		mux.getOutputList().addAll(outputList);
		outputList.clear();
	}
	

	private void loadSectionList(String sectionName, File muxFile) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(muxFile));
		String line = reader.readLine();
		
		// Find Section
		while (line != null) {
			if (line.contains(sectionName)) break;
			line = reader.readLine(); 	
		}
			
		// Read to end of Section	
		int sectionIndex = 1;
			
		while (sectionIndex > 0) {		
			line = reader.readLine();
				
			if (line == null) break;
			else if (line.contains("}")) sectionIndex--;
				
			else {		
				String[] strings = line.split(" ");
				
				// possible Entries
				if (strings.length > 1) {
					
					String name = strings[0].replaceAll("\\s","");
					String value = strings[1].replaceAll("\\s","");
					if (value.indexOf("\"") == 0) value = value.replaceAll("\"","");		// EDI, ... "destination..."  -> remove ""
					
					// Not Comment Line
					if (name.indexOf(0) != ';') {		
						if (value.contentEquals("{")) {
							loadSection(name, muxFile, sectionName);
							sectionIndex++;
						}		
					}
				}				
			}		 
		}
		reader.close();
	}


	private void loadOutputSection(File muxFile) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(muxFile));
		String line;
		
		// Find Section
		do {
			line = reader.readLine(); 
			
			if (line.contains("outputs")) {
				line = reader.readLine(); 
				break;
			}
		}
		while (line != null);
			
		// Read Section
		while (line != null) {		
			String[] strings = line.split(" ");
			
			// Output valide
			if (strings.length > 1) {
				
				// Check if edi output		
				if (strings[0].replaceAll("\\s","").contentEquals("edi")) {
					loadSectionList("destinations", muxFile);
				} 
				// advanced EDI Parameters
				else if (!strings[1].contains("\"")) {
					loadEdi(reader, line);
				}
				// another Output
				else if (strings[1].contains("\"") && !strings[1].contains("simul://")) {		
					loadOutput(strings[0].replaceAll("\\s",""), strings[1].replaceAll("\\s",""));
				}
			}
			line = reader.readLine();
		}
		reader.close();
	}


	// not EDI Output
	private void loadOutput(String name, String parameter) {
		
		String format = parameter.substring(1, parameter.indexOf(":"));
		String destination = parameter.substring(parameter.indexOf("//")+2, parameter.length()-1);
		int portIndex = destination.indexOf(":");
		
		Output out = null;
		
		switch (format) {
		case "fifo":					// EtiFile		
			out = new ETIFile(name);			
			int typeIndex = destination.indexOf("?");
			
			if (typeIndex != -1) {
				out.getDestination().setValue(destination.substring(0, typeIndex));
				((ETIFile)out).getType().setValue(destination.substring(typeIndex+6));
			}
			break;
			
		case "zmq+tcp":					// all Zmq-Outputs and Outputs with Modulator
			out = new ETIZeromq(name);
			
			// Destination Port
			if (portIndex != -1) {
				out.getDestination().setValue(destination.substring(portIndex+1));
			}
			break;
		
		case "tcp":						// TCP
		case "udp":						// UDP
			out = new Network(name, format);		
			int multicastIndex = destination.indexOf("?");
			
			// Destination IP
			if (portIndex != -1) {
				out.getDestination().setValue(destination.substring(0, portIndex));
				
				// Destination Port and Multicast
				if (multicastIndex != -1 && format.contentEquals("udp")) {
					String[] multi = destination.substring(multicastIndex+1).split(",");
					((Network)out).getDestinationPort().setValue(destination.substring(portIndex+1, multicastIndex));
					
					// Multicast
					if (multi.length > 1) {
						((Network)out).getMulticast().setValue(true);
						((Network)out).getSource().setValue(multi[0].substring(4));
						((Network)out).getTtl().setValue(multi[1].substring(4));
					}
				}
				// only Destination Port
				else {
					((Network)out).getDestinationPort().setValue(destination.substring(portIndex+1));
				}
			}
			break;
			
		case "raw":						// FarSync
			out = new ETIDevice(name);
			out.getDestination().setValue(destination);
			break;
		}	
		
		if (out != null) outputList.add(out);
	}


	private void loadEdi(BufferedReader reader, String line) throws IOException {
		EDI edi = null;
		
		// select first EDI Output
		for (Output out: outputList) {
			if (out.getFormat().getValue().contentEquals("edi")) {
				edi = (EDI)out;
				break;
			}
		}
			
		while (!line.replaceAll("\\s","").contains("}") && edi != null) {		
			String[] strings = line.split(" ");	
			
			if (strings.length > 1) {
				String name = strings[0].replaceAll("\\s","");
				String value = strings[1].replaceAll("\\s","");	
				
				switch (name) {	
				case "port": 					edi.getDestinationPort().setValue(value); 		break;
				case "enable_pft":				edi.getEnable_pft().setValue(getBool(value));	break;
				case "fec": 					edi.getFec().setValue(value);					break;
				case "interleave":				edi.getInterleave().setValue(value);			break;
				case "chunk_len":				edi.getChunk_len().setValue(value);				break;
				case "dump":					edi.getDump().setValue(getBool(value));			break;
				case "verbose":					edi.getVerbose().setValue(getBool(value));		break;
				case "tagpacket_alignment":		edi.getTagpacket_alignment().setValue(value);	break;
				
				}
			}
			line = reader.readLine();
		}
	}


	private void loadSection(String sectionName, File muxFile, String type) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(muxFile));
		String line;
		
		// Find Section
		do {
			line = reader.readLine(); 
			if (line.contains(sectionName)) break;
		}
		while (line != null);
			
		// Read Section
		if (line != null) {
			
			while (!line.contains("}")) {
				line = reader.readLine();
				
				String[] strings = line.split(" ");
				if (strings.length > 1) {
					
					String name = strings[0].replaceAll("\\s","");
					String value = strings[1].replaceAll("\\s","");	
					
					// Label/ShortLabel with " "
					int index = 2;
					while (value.charAt(value.length()-1) != 34 && strings.length > index) {
						value = value + " " + strings[index++];
					}
					
					// Not Comment Line
					if (!name.isEmpty() && name.indexOf(";") == -1) {
						
						// switch Mux-Section
						boolean isAnError = false;
						switch (type) {
						
						case "mux":
							isAnError = loadToMux(name, value);
							break;
						
						case "services":
							isAnError = loadToService(sectionName, name, value);
							break;
							
						case "subchannels":
							isAnError = loadToSubchannel(sectionName, name, value);
							break;
							
						case "components":
							isAnError = loadToComponent(sectionName, name, value);
							break;	
							
						case "destinations":	// EDI Destinations
							isAnError = loadToEdi(sectionName, name, value);
						}
						
						if (isAnError) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("Load - Warning");
							alert.setHeaderText("Value don't load!");
							alert.setContentText("In Section <"+sectionName+">\nthe Parameter <"+name+">\nwith Value <"+value+">\ncouldn't be loaded.");
							alert.show();
						}
					}
				}
			}
		}
				
		reader.close();	
	}
	

	private boolean loadToEdi(String ediName, String name, String value) {
		EDI edi = null;
		
		// EDI init.
		for (Output out: outputList) {
			
			if (out.getFormat().getValue().contentEquals("edi") && ediName.contains(out.getName().getValue())) {
				edi = (EDI)out;
				break;
			}
		}
		
		// EDI is not exist before
		if (edi == null) {
			edi = new EDI(ediName);
			outputList.add(edi);
		}
		
		value = value.replaceAll("\"","");
		
		// Set EDI Attributes
		switch (name) {	
		
		case "destination":			edi.getDestination().setValue(value);					return false;				
		case "source": 				edi.getMulticast().setValue(true);
									edi.getSource().setValue(value);						return false;						
		case "sourceport":			edi.getSourceport().setValue(value);					return false;
		case "ttl":					edi.getTtl().setValue(value);							return false;	
		}	
		
		return true;
	}


	private boolean loadToMux(String name, String value) {
		Ensemble ensemble = mux.getEnsemble();
		
		switch (name) {

		// General
		case "nbframes":				mux.getNbframes().setValue(value);					return false;				
		case "syslog":					mux.getSyslog().setValue(getBool(value));			return false;
		case "writescca":				mux.getWritescca().setValue(getBool(value));		return false;
		case "tist":					mux.getTist().setValue(getBool(value));				return false;
		case "managementport":			mux.getManagementport().setValue(value);			return false;

		case "dabmode":					
			if (!mux.getDabModeList().contains(value)) 										break;

			mux.getDabMode().setValue(value);												return false;

		// Remote Control
		case "telnetport":				mux.getTelnetPort().setValue(value);				return false;
		
		// Ensemble
		case "id":						ensemble.getId().setValue(value);												return false;
		case "ecc":						ensemble.getEcc().setValue(value);												return false;
		case "label":					ensemble.getLabel().setValue(value.substring(1, value.length() - 1));			return false;
		case "shortlabel":				ensemble.getShortLabel().setValue(value.substring(1, value.length() - 1));		return false;

		case "international-table":										
			if (getInt(value) < 1 || getInt(value) > 2) 																break;
			ensemble.getIntTable().setValue(value);																		return false;
			
		case "local-time-offset":		
			if (!ensemble.getLocalTimeOffsetList().contains(value)) 													break;
			ensemble.getLocalTimeOffset().setValue(value);																return false;	
		}

		return true;
	}
	
	
	private boolean loadToService(String serviceName, String name, String value) {
		Service service = null;
		
		// Service init.
		for (Service serv: mux.getServiceList()) {
			if (serv.getName().getValue().contentEquals(serviceName)) {
				service = serv;
				break;
			}
		}
		
		// Service is not exist before
		if (service == null) {
			service = new Service(serviceName);
			mux.getServiceList().add(service);
		}
		
		// Set Attributes
		switch (name) {
		
		case "id":					service.getId().setValue(value);												return false;
		case "label":				service.getLabel().setValue(value.substring(1, value.length() - 1));			return false;
		case "shortlabel":			service.getShortLabel().setValue(value.substring(1, value.length() - 1));		return false;
		case "pty":					service.getPty().setValue(value);												return false;
		case "language":			service.getLanguage().setValue(value);											return false;	
		}
		
		return true;
	}
	
	
	private boolean loadToSubchannel(String subchName, String name, String value) { 	// beim erstellen von Subchannel, muss korrekter Input gesetzt werden!!!!
		Subchannel subch = null;
		
		// Subchannel init.
		for (Subchannel sub: subchannelList) {
			if (sub.getName().getValue().contentEquals(subchName)) {
				subch = sub;
				break;
			}
		}
		
		// Subchannel is not exist before
		if (subch == null) {
			
			if (!name.contains("type")) return true;
			
			switch (value) {
			case "audio":			subch = new Subchannel(subchName, new MP2());							break;
			case "dabplus": 		subch = new Subchannel(subchName, new AAC());							break;
			case "data": 			subch = new Subchannel(subchName, new Data());							break;
			case "packet": 			subch = new Subchannel(subchName, new Packet(value));					break;
			case "enhancedpacket": 	subch = new Subchannel(subchName, new Packet(value));					break;
			default: 																						return true;
			}
	
			subchannelList.add(subch);
		}

		switch (name) {
	
		case "id":					subch.getId().setValue(value);												return false;
		case "inputfile":			subch.getInputfile().setValue(value.substring(1, value.length() - 1));		return false;
		case "zmq-buffer":			subch.getZmqBuffer().setValue(value);										return false;
		case "zmq-prebuffering":	subch.getZmqPreBuffer().setValue(value);									return false;
		case "encryption":			subch.getEncryption().setValue(getBool(value));								return false;
		case "secret-key":			subch.getSecretKey().setValue(value);										return false;
		case "public-key":			subch.getPublicKey().setValue(value);										return false;
		case "encoder-key":			subch.getEncoderKey().setValue(value);										return false;
		
		case "nonblock":			break;			// old -> now not supported!
		case "type":				return false;	// -> set at the beginning			
			
		case "protection-profile":					// -> set at the beginning						
			if (value.contentEquals("EEP_B")) {			
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Protection-Profile EEP_B can't be load");
				alert.setHeaderText("Set up manually in the Subchannels-Tab");
			}
			else if (!subch.getProtectionProfile().getValue().contentEquals(value))	break;						
			return false;	
	
		case "protection":
			for (String pl: subch.getInput().getProtectionLevelList()) {
				
				// Value Found in List
				if (pl.contains(value)) {
					subch.getInput().getProtectionLevel().setValue(pl);
					return false;
				}		
			}
			break;
			
		case "bitrate":				
			if (!subch.getInput().getBitrateList().contains(value)) break;
			
			subch.getInput().getBitrate().setValue(value);
			return false;		
		}
		
		return true;
	}
	
	
	
	private boolean loadToComponent(String compName, String name, String value) {
		Component comp = null;
		
		// Component init.
		for (Component c: componentList) {
			if (c.getName().getValue().contentEquals(compName)) {
				comp = c;
				break;
			}
		}
		
		// Component is not exist before
		if (comp == null) {
			comp = new Component(compName);
			componentList.add(comp);
		}
		
		switch (name) {
		
		case "id":					comp.getId().setValue(value);									return false;
		case "type":				comp.getType().setValue(value);									return false;
		case "figtype":																				return false;
		case "address":				comp.getAddress().setValue(value);								return false;
		case "datagroup":			comp.getDatagroup().setValue(getBool(value));					return false;
		
		case "service":			
			Service service = getServiceByName(value);
			if (service == null) break;
			comp.setService(service);						
			return false;
			
		case "subchannel":	
			Subchannel subch = getSubchannelByName(value);
			if (subch == null) break;
			comp.setSubchannel(subch);	
			return false;	
		}
		
		return true;
	}
	
	private ArrayList<File> getDabModFiles(File bashFile) throws IOException {
		
		ArrayList<File> modFiles = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(bashFile));
		
		String line = reader.readLine();
		while (line != null) {
			
			if (line.contains(dabmod)) {
				String fileName = line.substring(line.indexOf("-C") + 3);

				// fileName is valide
				if (!fileName.contains(" ")) {
					modFiles.add(new File(bashFile.getParentFile() +"/"+ fileName));
				}
			}
			line = reader.readLine();
		}
		reader.close();	
		
		return modFiles;
	}
		
	
	private Subchannel getSubchannelByName(String name) {
		
		for (Subchannel subch: mux.getSubchannelList()) {
			if (subch.getName().getValue().contentEquals(name)) return subch;
		}	
		return null;
	}



	private Service getServiceByName(String name) {
		
		for (Service service: mux.getServiceList()) {
			if (service.getName().getValue().contentEquals(name)) return service;
		}	
		return null;
	}



	private boolean getBool(String value) {
		if (value.contentEquals("true") || value.contentEquals("1")) return true;
		
		return false;
	}
	
	
	private int getInt(String value) {
		if (value.matches("\\d*")) return Integer.parseInt(value);
	
		return 0;
	}
	
}


