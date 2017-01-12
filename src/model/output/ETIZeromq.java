package model.output;

import java.util.ArrayList;
import model.*;


public class ETIZeromq extends Output {

	private Modulator mod;
	
	
	public ETIZeromq(String name) {
		super(name, "zmq+tcp", "9100");
		
		destination.set(getFreePort());
	}


	
	// if Selected Output need dabmod configuration file
	
	public Modulator getMod() {
		return mod;
	}
	
	public void setMod(Modulator mod) {
		this.mod = mod;
	}

	
	private String getFreePort() {
		ArrayList<String> allPorts = getAllMultiplexPorts();
		
		for (int port = 9100; port < 65536; port++) {
			
			for (int i=0; i < allPorts.size(); i++) {
				
				if (allPorts.get(i).contentEquals(""+port)) {
					break;
				}
				
				// last checked port
				if (i == allPorts.size() - 1) {
					return "" + port;
				}	
			}			
		}
		return null;
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
				if (subch.getInputfile().getValue().contains("tcp://")) {
					portList.add(subch.getInputfile().getValue().substring(8));
				}
			}	
		}
		
		// Outputs
		for (Output out: Multiplex.getInstance().getOutputList()) {
			
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
