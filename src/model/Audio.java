package model;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.output.*;


public abstract class Audio extends Input {
	
	protected StringProperty samplerate, channel;
	protected BooleanProperty driftCompensation;	
	protected ObservableList<String> channelList, samplerateList;

	protected Pad pad;
	
	
	public Audio(String type, String profile) {	
		super(type, profile);
		
		sourceList = 			FXCollections.observableArrayList("Webstream","ALSA","JACK");
		samplerateList =		FXCollections.observableArrayList("32000","48000");
		channelList = 			FXCollections.observableArrayList("1","2");
		
		driftCompensation = 	new SimpleBooleanProperty(false);
		channel =				new SimpleStringProperty(channelList.get(1));			// def. stereo
		samplerate = 			new SimpleStringProperty(samplerateList.get(1));		// def. 48 kHz
		
		source.setValue(sourceList.get(0));												
		output.setValue("tcp://*:" + getFreePort());
		
		pad = new Pad();
	}

	public StringProperty getSamplerate() {
		return samplerate;
	}

	public StringProperty getChannel() {
		return channel;
	}

	public BooleanProperty getDriftCompensation() {
		return driftCompensation;
	} 
	
	public ObservableList<String> getChannelList() {
		return channelList;
	}

	public ObservableList<String> getSamplerateList() {
		return samplerateList;
	}

	public Pad getPad() {
		return pad;
	}
	
	public void setPad(Pad pad) {
		this.pad = pad;
	}


	private int getFreePort() {
		
		ArrayList<String> allPorts = getAllMultiplexPorts();
		int index;
		
		for (int freePort = 9000; freePort < 65535; freePort++) {
			
			// Check freePort if exist
			for (index=0; index < allPorts.size(); index++) {
				
				if (allPorts.get(index).contentEquals(""+freePort)) break;
			}
			
			// freePort is not exist before
			if (index == allPorts.size()) return freePort;
		}
		
		// Error
		return 0;
	}
	
	private ArrayList<String> getAllMultiplexPorts() {
		ArrayList<String> portList = new ArrayList<>();
		
		// DabMux-Remote Ports
		portList.add(Multiplex.getInstance().getManagementport().getValue());
		portList.add(Multiplex.getInstance().getTelnetPort().getValue());
		
		// Audio-Subchannels ZMQ-Ports
		for (Subchannel subch: Multiplex.getInstance().getSubchannelList()) {
			
			// Find Subchannel with inputfile contains "tcp://" -> exist Port
			if (subch.getInputfile() != null) {
				if (subch.getInputfile().getValue().contains("tcp://*:")) {
					portList.add(subch.getInputfile().getValue().substring(8));
				}
			}	
		}
		
		// Outputs
		for (Output out: Multiplex.getInstance().getOutputList()) {
			
			// EDI
			if (out.getFormat().getValue().contains("edi")) {
				portList.add(((EDI)out).getSourceport().getValue());
				portList.add(((EDI)out).getDestinationPort().getValue());
			}
			
			// ETI-ZMQ
			if (out.getFormat().getValue().contains("zmq")) {
				portList.add(out.getDestination().getValue());
				
				// I/Q-Modulator
				Modulator mod = ((ETIZeromq)out).getMod();
				
				if (mod != null) {
					
					portList.add(mod.getTelnetport().getValue());
					portList.add(mod.getZmqctrlendpoint().getValue());
					
					// I/Q-ZMQ
					if (mod.getOutput().getValue().contains("zmq")) {
						portList.add(((IQZeromq)mod).getListen().getValue());
					}
				}	
			}
		}
		return portList;
	}
	
}

