package model.output;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import addons.ExceptionAlert;
import model.Multiplex;
import model.Subchannel;

public class ETIZeromq extends Output {

	private Modulator mod;
	
	
	public ETIZeromq(String name) {
		super(name, "zmq+tcp", "*:9100");
		
		destination.set(getFreeDestination());
	}


	
	// if Selected Output need dabmod configuration file
	
	public Modulator getMod() {
		return mod;
	}
	
	public void setMod(Modulator mod) {
		this.mod = mod;
	}

	
	private String getFreeDestination() {
		
		boolean isPortFree = false;
		int port = 9100;
		
		while (isPortFree) {	
			
			if (getAllMultiplexPorts().contains(""+port)) {
				port++;
			}
			else {
				
				ServerSocket socket = null;
				try {
					socket = new ServerSocket(port);
				} 
				catch (IOException e) {
					new ExceptionAlert(this.getClass().getName(), e);
				} 
				finally {
					
					if (socket != null) {
	                    try { 
	                        socket.close(); 
	                        isPortFree = true;
	                        
	                    } catch (IOException e) { 
	                    	new ExceptionAlert(this.getClass().getName(), e);
	                    } 
					}
				}
			} 
		}
		
		return "*:"+port;
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
			if (out.getDestination().getValue().contains("*:")) {
				portList.add(out.getDestination().getValue().substring(2));
				
				// I/Q-Modulator
				Modulator mod = ((ETIZeromq)out).getMod();
				
				if (mod != null) {
					
					portList.add(mod.getTelnetport().getValue());
					portList.add(mod.getZmqctrlendpoint().getValue());
					
					// I/Q-ZMQ
					if (mod.getOutput().getValue().contains("zmq")) {
						portList.add(((IQZeromq)mod).getListen().getValue().substring(8));
					}
				}	
			}
		}
		return portList;
	}
}
