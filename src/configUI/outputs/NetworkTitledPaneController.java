package configUI.outputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import addons.*;
import model.*;
import model.output.*;


public class NetworkTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML Button changePanesButton;
	
	@FXML Label nameLabel, protocolLabel, serviceLabel, sourceLabel, destinationLabel, typeLabel, socketTypeLabel;
	@FXML TextField nameTextField, sourceIpTextField, sourcePortTextField, destIpTextField, destPortTextField;
	@FXML ChoiceBox<String> protocolChoiceBox, serviceChoiceBox, socketTypeChoiceBox;
	@FXML GridPane networkPane, socketTypePane;
	
	// Multicast
	@FXML Label multicastLabel, ttlLabel;
	@FXML TextField ttlTextField;
	@FXML CheckBox multicastCheckBox;
	@FXML GridPane sourcePane, multicastPane;
	
	// EDI
	@FXML Label pftLabel, fecLabel, interleaverLabel, chunkLabel, dumpLabel, verboseLabel, tagpacketLabel;
	@FXML CheckBox pftCheckBox, dumpCheckBox, verboseCheckBox;
	@FXML TextField fecTextField, interleaverTextField, chunkTextField, tagpacketTextField;
	@FXML GridPane ediPane;
	
	private boolean advancedView;
	private Output output;
	private VBox modulatorVBox;
	private FXMLLoader fxmlLoader;
		
	private final String[] udpServices = {"EDI", "none"};
	private final String[] tcpServices = {"ETI-ZeroMQ", "I/Q-ZeroMQ", "ETI-none"};
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Protokoll
		protocolChoiceBox.setItems(FXCollections.observableArrayList("UDP","TCP"));
		
		// Destination Port
		destPortTextField.setTextFormatter(new TextFormatter<String>(c -> {
			
			// Text empty
			if (c.getControlNewText().isEmpty()) c.setText("12000");   

			// only decimal chars
			if (!c.getControlNewText().matches("\\d*")) return null;

			// only numbers, not "0123" , "0..."
			if (c.getControlNewText().length() > 1 && c.getControlNewText().charAt(0) == '0') return null;

			// only valide numbers < maxPort
			if (Integer.parseInt(c.getControlNewText()) > 65535) return null;

			return c;
		}));
			
		// Multicast
		multicastCheckBox.selectedProperty().addListener(change -> setSourcePane());
		
		// Pane for I/Q-ZeroMQ init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/ModulatorVBox.fxml"));
			modulatorVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// Default
		advancedView = false;
	}
	
	
	public void createOutput(Output out) {
		String format = out.getFormat().getValue();
		
		if (format.contains("udp") || format.contains("edi")) {		// UDP/EDI
			protocolChoiceBox.getSelectionModel().select(0);
			serviceChoiceBox.setItems(FXCollections.observableArrayList(udpServices));
			
			if (format.contains("edi")) {							// EDI
				serviceChoiceBox.getSelectionModel().select(0);
			}
			else {
				serviceChoiceBox.getSelectionModel().select(1);		// UDP
			}	
		}
		
		else {														// TCP/ZMQ
			protocolChoiceBox.getSelectionModel().select(1);
			serviceChoiceBox.setItems(FXCollections.observableArrayList(tcpServices));
			
			if (format.contains("zmq")) {							// ZMQ
				
				if (((ETIZeromq)out).getMod() == null) {			// ETI-Zmq
					serviceChoiceBox.getSelectionModel().select(0);
				} 
				else {												// I/Q-Zmq
					serviceChoiceBox.getSelectionModel().select(1);			
				}
			}
			else {													// TCP
				serviceChoiceBox.getSelectionModel().select(2);
			}
		}
		
		// Listener
		protocolChoiceBox.valueProperty().addListener(change -> changeProtokoll());
		serviceChoiceBox.valueProperty().addListener(change -> changeService());
		
		setOutput(out);
		
		// Panes init.
		setChangeButton();
		setIqPane();
		setMulticastPane();
		setSourcePane();
		setEdiPane();
	}


	private void setOutput(Output out) {		
		output = out;
		
		// Format
		String format = out.getFormat().getValue();
		
		// TitledPane
		titledPane.textProperty().unbind();
		titledPane.textProperty().bind(Bindings.concat("Output: ",out.getName()));
		
		// Name
		new NameValidation(nameTextField, out.getName(), Multiplex.getInstance().getOutputList());
		
		// Destination IP
		destIpTextField.setDisable(format.contains("zmq"));
		
		if (format.contains("zmq")) {
			IQZeromq iq = (IQZeromq) ((ETIZeromq)out).getMod();
			
			if (iq != null) {																// I/Q-ZMQ		
				setModulatorVBox((ETIZeromq)out);

				// Socket-Type
				socketTypeChoiceBox.setItems(iq.getSocketTypeList());
				socketTypeChoiceBox.valueProperty().unbindBidirectional(iq.getSocketType());
				System.out.println(iq.getSocketType());
				socketTypeChoiceBox.valueProperty().bindBidirectional(iq.getSocketType());
				
				destPortTextField.textProperty().unbindBidirectional(iq.getListen());
				destPortTextField.textProperty().bindBidirectional(iq.getListen());
			} 
			else {																			// ETI-ZMQ
				destPortTextField.textProperty().unbindBidirectional(out.getDestination());
				destPortTextField.textProperty().bindBidirectional(out.getDestination());
			}
			destIpTextField.setText("*");
		}
		else if (format.contains("edi")) {													// EDI
			EDI edi = (EDI)out;

			// Destination
			new IpValidation(destIpTextField, edi.getDestination());
			destPortTextField.textProperty().unbindBidirectional(edi.getDestinationPort());
			destPortTextField.textProperty().bindBidirectional(edi.getDestinationPort());
			

			// Multicast
			multicastCheckBox.selectedProperty().unbind();
			multicastCheckBox.selectedProperty().bindBidirectional(edi.getMulticast());

			// Source
			new IpValidation(sourceIpTextField, edi.getSource());	
			new PortValidation(sourcePortTextField, edi.getSourceport(), 13000);

			// TTL
			new NumberValidation(ttlTextField, edi.getTtl(), 0, 255, 1, null);

			// PFT
			pftCheckBox.selectedProperty().unbindBidirectional(edi.getEnable_pft());	
			pftCheckBox.selectedProperty().bindBidirectional(edi.getEnable_pft());

			// FEC
			new NumberValidation(fecTextField, edi.getFec(), 0, 9, 1, null);

			// Interleaver
			new NumberValidation(interleaverTextField, edi.getInterleave(), 0, 999, 1, null);

			// Chunk
			new NumberValidation(chunkTextField, edi.getChunk_len(), 0, 999, 1, null);

			// Dump
			dumpCheckBox.selectedProperty().unbindBidirectional(edi.getDump());
			dumpCheckBox.selectedProperty().bindBidirectional(edi.getDump());

			// Verbose
			verboseCheckBox.selectedProperty().unbindBidirectional(edi.getVerbose());
			verboseCheckBox.selectedProperty().bindBidirectional(edi.getVerbose());

			// Tagpacket
			new NumberValidation(tagpacketTextField, edi.getTagpacket_alignment(), 0, 255, 1, null);
			
			// advanced Parameters are the same in all EDI-Outputs
			bindEDI(edi);
		}
		else {																				// TCP/UDP	
			Network net = (Network)out;
			
			// Destination
			new IpValidation(destIpTextField, net.getDestination());
			destPortTextField.textProperty().unbindBidirectional(net.getDestinationPort());
			destPortTextField.textProperty().bindBidirectional(net.getDestinationPort());

			// Multicast
			multicastCheckBox.selectedProperty().unbind();
			multicastCheckBox.selectedProperty().bindBidirectional(net.getMulticast());

			// Source-IP
			new IpValidation(sourceIpTextField, net.getSource());	

			// TTL
			new NumberValidation(ttlTextField, ((Network)out).getTtl(), 0, 255, 1, null);
		}
	}
	

	private void changeService() {	
		
		if (serviceChoiceBox.getValue() != null) {
			int index = Multiplex.getInstance().getOutputList().indexOf(output);

			Output out = null;
			
			if (serviceChoiceBox.getValue().contains("ZeroMQ")) {									// ETI-/IQ-ZMQ
				ETIZeromq zmq = new ETIZeromq(nameTextField.getText());	
				out = zmq;
				
				if (serviceChoiceBox.getValue().contains("I/Q")) {									 // I/Q-ZMQ
					IQZeromq iq = new IQZeromq();			
					zmq.setMod(iq);
				} 
			}	
			else if (serviceChoiceBox.getValue().contains("EDI")) {								// EDI

				EDI edi = new EDI(nameTextField.getText());
				out = edi;
				
				// advanced Parameters are the same in all EDI-Outputs
				bindEDI(edi);
				
			}
			else {
				Network net;
				if (protocolChoiceBox.getValue().contains("UDP")){							// UDP	
					net = new Network(nameTextField.getText(), "udp");
				} 
				else {																	// TCP
					net = new Network(nameTextField.getText(), "tcp");
				}	
				out= net;
			}
			
			setOutput(out);
			Multiplex.getInstance().getOutputList().set(index, out);

			// Update Panes
			setChangeButton();
			setIqPane();
			setMulticastPane();
			setSourcePane();
			setEdiPane();
		}
	}
	
	private void bindEDI(EDI edi) {
		
		for (Output o: Multiplex.getInstance().getOutputList()) {					
			if (o.getFormat().getValue().contains("edi") && !o.equals(edi)) {	
				edi.getDestinationPort().bindBidirectional(((EDI)o).getDestinationPort());
				edi.getEnable_pft().bindBidirectional(((EDI)o).getEnable_pft());
				edi.getFec().bindBidirectional(((EDI)o).getFec());
				edi.getInterleave().bindBidirectional(((EDI)o).getInterleave());
				edi.getChunk_len().bindBidirectional(((EDI)o).getChunk_len());
				edi.getDump().bindBidirectional(((EDI)o).getDump());
				edi.getVerbose().bindBidirectional(((EDI)o).getVerbose());
				edi.getTagpacket_alignment().bindBidirectional(((EDI)o).getTagpacket_alignment());
				break;
			}
		}		
	}


	private void setModulatorVBox(ETIZeromq zmq) {
		
		String source = "tcp://localhost:" + zmq.getDestination().getValue();
		zmq.getMod().getSource().setValue(source);

		fxmlLoader.<ModulatorVBoxController>getController().setModulator(zmq.getMod());
	}

	@FXML
	private void changePanes() {
		advancedView = !advancedView;
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");	
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
		}			
		setIqPane();
		setMulticastPane();
		setSourcePane();
		setEdiPane();	
	}

	
	private void changeProtokoll() {
		
		if (protocolChoiceBox.getValue().contains("UDP")) {		// UDP
			serviceChoiceBox.setItems(FXCollections.observableArrayList(udpServices));
			serviceChoiceBox.getSelectionModel().select(0);
		}
		else {														// TCP
			serviceChoiceBox.setItems(FXCollections.observableArrayList(tcpServices));
			serviceChoiceBox.getSelectionModel().select(0);	
		}
	}
	
	private void setSourcePane() {				// Multicast Source Pane
		vBox.getChildren().remove(sourcePane);
		
		if (multicastCheckBox.isSelected() && !output.getFormat().getValue().contains("zmq")) {
			sourcePortTextField.setVisible(!serviceChoiceBox.getValue().contains("none"));
	
			vBox.getChildren().add(vBox.getChildren().indexOf(multicastPane)+1, sourcePane);
		}
	}
	
	private void setEdiPane() {
		vBox.getChildren().remove(ediPane);
		
		if (serviceChoiceBox.getValue().contains("EDI") && advancedView) {	
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), ediPane);
		}
	}
	
	private void setMulticastPane() {
		vBox.getChildren().remove(multicastPane);
		
		if (protocolChoiceBox.getValue().contains("UDP")) {	
			vBox.getChildren().add(vBox.getChildren().indexOf(networkPane)+1, multicastPane);
		}
	}
	
	private void setIqPane() {										// I/Q-ZMQ 
		vBox.getChildren().removeAll(socketTypePane, modulatorVBox);
		
		if (serviceChoiceBox.getValue().contains("I/Q") && advancedView) {	
			vBox.getChildren().add(vBox.getChildren().indexOf(networkPane)+1, socketTypePane);
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), modulatorVBox);
		}
	}
	
	private void setChangeButton() {
		vBox.getChildren().remove(changePanesButton);
		
		if (!serviceChoiceBox.getValue().contains("none") && !serviceChoiceBox.getValue().contains("ETI")) {	
			vBox.getChildren().add(changePanesButton);
		}
	}
}
