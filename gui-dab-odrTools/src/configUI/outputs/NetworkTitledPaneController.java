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
import javafx.scene.control.Spinner;
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
	@FXML Spinner<Integer> ttlSpinner;
	@FXML CheckBox multicastCheckBox;
	@FXML GridPane sourcePane, multicastPane;
	
	// EDI
	@FXML Label pftLabel, fecLabel, chunkLabel, dumpLabel, verboseLabel, tagpacketLabel;
	@FXML CheckBox pftCheckBox, dumpCheckBox, verboseCheckBox;
	@FXML Spinner<Integer> fecSpinner, chunkSpinner, tagpacketSpinner;
	@FXML GridPane ediPane;
	
	private boolean advancedView;
	private Output output;
	private VBox modulatorVBox;
	private FXMLLoader fxmlLoader;
		
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// create new Output
		int index = Multiplex.getInstance().getOutputList().size();
		String name = "NEW Output "+index;

		output = new EDI(name);
		Multiplex.getInstance().getOutputList().add(output);

		// Name & TitledPane
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		new NameValidation(nameTextField, output.getName(), Multiplex.getInstance().getOutputList());
		
		
		// Protokoll
		protocolChoiceBox.setItems(FXCollections.observableArrayList("UDP","TCP"));
		protocolChoiceBox.getSelectionModel().select(0);
		protocolChoiceBox.valueProperty().addListener(change -> changeProtokoll());
		
	
		// Service
		serviceChoiceBox.setItems(FXCollections.observableArrayList("EDI","none"));
		serviceChoiceBox.getSelectionModel().select(0);
		serviceChoiceBox.valueProperty().addListener(change -> changeService());
		
		// Destination
		new IpValidation(destIpTextField, output.getDestination());
		destPortTextField.setTextFormatter(new TextFormatter<String>(c -> {
			
			// Spinner Text not empty
			if (c.getControlNewText().isEmpty()) c.setText("7000");

			// only decimal chars
			if (c.getControlNewText().matches("\\d*")) {

				// only ports, not "0123" , "0..."
				if (c.getControlNewText().length() > 1 && c.getControlNewText().charAt(0) == '0') return null;

				// only ports <= 65535
				if (Integer.parseInt(c.getControlNewText()) > 65535) return null;

				return c;
			}
			return null;
		}));
		
		//Multicast
		multicastCheckBox.selectedProperty().addListener(change -> setSourcePane());
		
		// Source
		new IpValidation(sourceIpTextField, ((EDI)output).getSource());
		sourcePortTextField.setTextFormatter(new TextFormatter<String>(c -> {
			
			// Spinner Text not empty
			if (c.getControlNewText().isEmpty()) c.setText("7000");

			// only decimal chars
			if (c.getControlNewText().matches("\\d*")) {

				// only ports, not "0123" , "0..."
				if (c.getControlNewText().length() > 1 && c.getControlNewText().charAt(0) == '0') return null;

				// only ports <= 65535
				if (Integer.parseInt(c.getControlNewText()) > 65535) return null;

				return c;
			}
			return null;
		}));
		
		// TTL
		new NumberValidation(ttlSpinner, ((EDI)output).getTtl(), 0, 255, 1);
		
		// FEC
		new NumberValidation(fecSpinner, ((EDI)output).getFec(), 0, 9, 1);
		
		// Chunk
		new NumberValidation(chunkSpinner, ((EDI)output).getChunk_len(), 0, 999, 1);
		
		// Tagpacket
		new NumberValidation(tagpacketSpinner, ((EDI)output).getTagpacket_alignment(), 0, 255, 1);
		
		
		// Pane for I/Q-ZeroMQ init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/ModulatorVBox.fxml"));
			modulatorVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			new ExceptionAlert(this.getClass().getName(), e);
		}
		
		// Default
		advancedView = false;
		changeService();
	}
	

	private void changeService() {	
		
		if (serviceChoiceBox.getValue() != null) {
			int index = Multiplex.getInstance().getOutputList().indexOf(output);
			vBox.getChildren().removeAll(ediPane, modulatorVBox);

			destIpTextField.setDisable(serviceChoiceBox.getValue().contains("ZeroMQ"));
			destPortTextField.setDisable(serviceChoiceBox.getValue().contains("ZeroMQ"));

			// Unbind
			destIpTextField.textProperty().unbind();
			destPortTextField.textProperty().unbind();
			multicastCheckBox.selectedProperty().unbind();
			sourceIpTextField.textProperty().unbind();
			sourcePortTextField.textProperty().unbind();
			ttlSpinner.getValueFactory().valueProperty().unbind();

			socketTypeChoiceBox.valueProperty().unbind();
			pftCheckBox.selectedProperty().unbind();
			fecSpinner.getValueFactory().valueProperty().unbind();
			chunkSpinner.getValueFactory().valueProperty().unbind();
			dumpCheckBox.selectedProperty().unbind();
			verboseCheckBox.selectedProperty().unbind();
			tagpacketSpinner.getValueFactory().valueProperty().unbind();


			if (serviceChoiceBox.getValue().contains("ZeroMQ")) {									// ETI-ZMQ and I/Q-ZMQ

				ETIZeromq zmq = new ETIZeromq(nameTextField.getText());	
				output = zmq;

				if (serviceChoiceBox.getValue().contains("I/Q")) {									 // I/Q-ZMQ
					IQZeromq iq = new IQZeromq();			
					zmq.setMod(iq);
					fxmlLoader.<ModulatorVBoxController>getController().setModulator(iq);

					socketTypeChoiceBox.setItems(iq.getSocketTypeList());
					socketTypeChoiceBox.valueProperty().bindBidirectional(iq.getSocketType());
					destPortTextField.setText(iq.getListen().getValue().substring(8));			// only Port, from Address
				} 
				else {																			// ETI-ZMQ
					destPortTextField.setText(zmq.getDestination().getValue().substring(2));	// only Port, from Address
				}

				// Set Items
				destIpTextField.setText("*");
			}	
			else {
				if (serviceChoiceBox.getValue().contains("EDI")) {								// EDI

					EDI edi = new EDI(nameTextField.getText());
					output = edi;

					// Binding
					sourcePortTextField.textProperty().bindBidirectional(edi.getSourceport());
					pftCheckBox.selectedProperty().bindBidirectional(edi.getEnable_pft());	
					fecSpinner.getValueFactory().valueProperty().bindBidirectional(edi.getFec().asObject());
					chunkSpinner.getValueFactory().valueProperty().bindBidirectional(edi.getChunk_len().asObject());
					dumpCheckBox.selectedProperty().bindBidirectional(edi.getDump());
					verboseCheckBox.selectedProperty().bindBidirectional(edi.getVerbose());
					tagpacketSpinner.getValueFactory().valueProperty().bindBidirectional(edi.getTagpacket_alignment().asObject());
				}
				else {
					Network net;

					if (protocolChoiceBox.getValue().contains("UDP")){							// UDP	
						net = new Network(nameTextField.getText(), "udp");
					} else {																	// TCP
						net = new Network(nameTextField.getText(), "tcp");
					}
					output = net;			
				}

				// Binding
				sourceIpTextField.textProperty().bindBidirectional(((Network)output).getSource());
				destIpTextField.textProperty().bindBidirectional(((Network)output).getDestination());
				destPortTextField.textProperty().bindBidirectional(((Network)output).getDestinationPort());
				multicastCheckBox.selectedProperty().bindBidirectional(((Network)output).getMulticast());
				ttlSpinner.getValueFactory().valueProperty().bindBidirectional(((Network)output).getTtl().asObject());		
			}
			Multiplex.getInstance().getOutputList().set(index, output);

			// Update Panes
			setChangeButton();
			setIqPane();
			setMulticastPane();
			setSourcePane();
			setEdiPane();
		}
	}
	

	private void changeProtokoll() {
		
		if (protocolChoiceBox.getValue().contains("UDP")) {		// UDP
			serviceChoiceBox.setItems(FXCollections.observableArrayList("EDI","none"));
			serviceChoiceBox.getSelectionModel().select(0);
		}
		else {														// TCP
			serviceChoiceBox.setItems(FXCollections.observableArrayList("ETI-ZeroMQ","I/Q-ZeroMQ","ETI-none"));
			serviceChoiceBox.getSelectionModel().select(0);	
		}
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
	
	
	private void setSourcePane() {				// Multicast Source Pane
		vBox.getChildren().remove(sourcePane);
		
		if (multicastCheckBox.isSelected()) {
			sourcePortTextField.setDisable(serviceChoiceBox.getValue().contains("none"));
			
			if (serviceChoiceBox.getValue().contains("none")) {
				sourcePortTextField.setText("none");
			}		
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
