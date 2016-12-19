package model.output;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import addons.ExceptionAlert;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Multiplex;
import model.Subchannel;


public class IQZeromq extends Modulator {

	
	private StringProperty listen, socketType;
	private ObservableList<String> socketTypeList;
	
	
	public IQZeromq() {
		super("zeromq");
		
		socketTypeList = FXCollections.observableArrayList("pub","rep");
		
		listen =		new SimpleStringProperty("tcp://*:"+getFreePort());	
		socketType = 	new SimpleStringProperty(socketTypeList.get(0));	
	}


	
	
	public StringProperty getListen() {
		return listen;
	}

	public StringProperty getSocketType() {
		return socketType;
	}

	public ObservableList<String> getSocketTypeList() {
		return socketTypeList;
	}
	
	
	
	private String getFreePort() {
		
		boolean isPortFree = false;
		int port = 54000;
		
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
		
		return ""+port;
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
