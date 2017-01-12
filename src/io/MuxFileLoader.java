package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.*;


public class MuxFileLoader {
	
	private Multiplex mux;
	private ArrayList<Component> componentList;		// need for visibility of Service-/Subchannel-ChoiceBox in ComponentTitledPane

	public MuxFileLoader(File muxFile) throws IOException {
		mux = Multiplex.getInstance();
		
		loadSection("general", muxFile, "mux");
		loadSection("remotecontrol", muxFile, "mux");
		loadSection("ensemble", muxFile, "mux");
		
		// Services
		loadSectionList("services", muxFile);
		
		// Subchannels
		loadSectionList("subchannels", muxFile);
		
		// Components
		componentList = new ArrayList<>();
		
		loadSectionList("components", muxFile);
		
		mux.getComponentList().addAll(componentList);
		componentList.clear();
		
		// Outputs
	}
	

	private void loadSectionList(String sectionName, File muxFile) throws IOException {

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
			line = reader.readLine();
			
			// Read to end of Section	
			int sectionIndex = 1;
			
			while (sectionIndex > 0) {
				String[] strings = line.split(" ");
				
				// possible Entries
				if (strings.length > 1) {
					// Subsection Name
					String name = strings[0].replaceAll("\\s","");
					
					// Not Comment Line
					if (name.indexOf(0) != ';') {
						
						if (strings[1].replaceAll("\\s","").contentEquals("{")) {
							loadSection(name, muxFile, sectionName);
							sectionIndex++;
						}		
					}
				}		
				line = reader.readLine();
						
				if (line == null) break;
				if (line.contains("}")) sectionIndex--;
			}		 
		}
		reader.close();
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
					
					// Not Comment Line
					if (!name.isEmpty()) {
						
						// switch Mux-Section
						boolean isAnError = false;
						switch (type) {
						
						case "mux":
							isAnError = writeToMux(name, value);
							break;
						
						case "services":
							isAnError = writeToService(sectionName, name, value);
							break;
							
						case "subchannels":
							isAnError = writeToSubchannel(sectionName, name, value);
							break;
							
						case "components":
							isAnError = writeToComponent(sectionName, name, value);
							break;	
						
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
	

	private boolean writeToMux(String name, String value) {
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
	
	
	private boolean writeToService(String serviceName, String name, String value) {
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
	
	
	private boolean writeToSubchannel(String subchName, String name, String value) { 	// beim erstellen von Subchannel, muss korrekter Input gesetzt werden!!!!
		Subchannel subch = null;
		
		// Subchannel init.
		for (Subchannel sub: mux.getSubchannelList()) {
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
	
			mux.getSubchannelList().add(subch);
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
	
	
	
	private boolean writeToComponent(String compName, String name, String value) {
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
		case "figtype":				comp.getFigtype().setValue(value);								return false;
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


