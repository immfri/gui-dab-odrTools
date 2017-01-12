package configUI.outputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import addons.*;
import model.*;
import model.output.*;


public class DeviceTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML GridPane devicePane, farsyncSourcePane;
	
	@FXML Label nameLabel, deviceLabel, sourceLabel;
	@FXML TextField nameTextField, sourceTextField;
	@FXML ChoiceBox<String> deviceChoiceBox;
	
	private Output output;
	private VBox uhdVBox;
	private FXMLLoader fxmlLoader;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Device
		deviceChoiceBox.setItems(FXCollections.observableArrayList("FarSync", "Ettus USRP"));		
			
		// UHD init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/UhdVBox.fxml"));
			uhdVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void createOutput(Output out) {
		
		if (!out.getFormat().getValue().contains("zmq")) {		// FarSync
			deviceChoiceBox.getSelectionModel().select(0);
		}
		else {
			deviceChoiceBox.getSelectionModel().select(1);		// USRP device
		}
		setPane();
		deviceChoiceBox.valueProperty().addListener(change -> changeDevice());
		
		setOutput(out);
	}
	
	
	private void setOutput(Output out) {
		output = out;
		
		// TitledPane
		titledPane.textProperty().unbind();
		titledPane.textProperty().bind(Bindings.concat("Output: ",out.getName()));
		
		// Name
		new NameValidation(nameTextField, out.getName(), Multiplex.getInstance().getOutputList());
	
		// Source
		sourceTextField.textProperty().unbindBidirectional(out.getDestination());
		sourceTextField.textProperty().bindBidirectional(out.getDestination());	
	}

	
	private void changeDevice() {
		int index = Multiplex.getInstance().getOutputList().indexOf(output);
			
		Output out = null;
		if (deviceChoiceBox.getSelectionModel().isSelected(0)) {	// FarSync
			out = new ETIDevice(nameTextField.getText());			
		}
		else {														// UHD
			out = new ETIZeromq(nameTextField.getText());	
			fxmlLoader.<UhdVBoxController>getController().setZmq((ETIZeromq)out);			
		}
		setPane();
		
		setOutput(out);
		Multiplex.getInstance().getOutputList().set(index, out);	// change Output -> replace output-object from index		
	}
	
	
	private void setPane() {
		vBox.getChildren().remove(1);
		
		if (deviceChoiceBox.getSelectionModel().isSelected(0)) {
			vBox.getChildren().add(farsyncSourcePane);
		}
		else {
			vBox.getChildren().add(uhdVBox);
		}
	}
}
