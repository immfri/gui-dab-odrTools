package addons;

import java.util.ArrayList;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.*;
import model.output.*;


public class PortValidation extends Validation {
	
	private int maxPort = 65535;

	
	public PortValidation(TextField textField, StringProperty port, int defaultPort) {
		
		// Text Formatter
		textField.setTextFormatter(new TextFormatter<String>(c -> {
			
			// Spinner Text not empty
			if (c.getControlNewText().isEmpty()) {
				c.setText(""+defaultPort);   
			}

			// only decimal chars
			if (c.getControlNewText().matches("\\d*")) {
				checkPort(textField, defaultPort);

				// only numbers, not "0123" , "0..."
				if (c.getControlNewText().length() > 1 && c.getControlNewText().charAt(0) == '0') {
					return null;
				}

				// only numbers < maxNumber
				if (Integer.parseInt(c.getControlNewText()) > maxPort) {
					return null;
				}

				return c;
			}
			return null;
		}));
		
		// Binding
		textField.textProperty().unbindBidirectional(port);
		textField.textProperty().bindBidirectional(port);
		
		checkPort(textField, defaultPort);
	}

	private void checkPort(TextField textField, int defaultPort) {
		textField.setStyle(nok);
		
		if (!textField.getText().contentEquals("0")) {
			
			int count = 0;
			for (String port: getAllMultiplexPorts()) {
				
				if (textField.getText().contentEquals(port)) count++;
				if (count > 1) break;
			}
			
			// Port exist first time and not any more 
			if (count < 2 && !textField.getText().isEmpty()) {
				// Check Port is valide
				if (Integer.parseInt(textField.getText()) > 1023) textField.setStyle(ok);		
			}
		} 
		else if (defaultPort == 0) {
			textField.setStyle(ok);
		}
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
