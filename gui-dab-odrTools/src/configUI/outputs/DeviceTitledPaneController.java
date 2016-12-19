package configUI.outputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import addons.ExceptionAlert;
import addons.*;
import model.*;
import model.output.*;


public class DeviceTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML GridPane devicePane, farsyncSourcePane;
	
	@FXML Label nameLabel, deviceLabel, sourceLabel;
	@FXML TextField nameTextField;
	@FXML ChoiceBox<String> deviceChoiceBox, sourceChoiceBox;
	
	private Output output;
	private VBox uhdVBox;
	private FXMLLoader fxmlLoader;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// create new Output
		int index = Multiplex.getInstance().getOutputList().size();
		String name = "NEW Output "+index;
		
		output = new ETIDevice(name);
		Multiplex.getInstance().getOutputList().add(output);
		
		// Name & TitledPane
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		new NameValidation(nameTextField, output.getName(), Multiplex.getInstance().getOutputList());
		
		// Device
		deviceChoiceBox.setItems(FXCollections.observableArrayList("FarSync","Ettus USRP"));
		deviceChoiceBox.getSelectionModel().select(0);
		deviceChoiceBox.valueProperty().addListener(change -> changeDevice());
		
		// FarSync - Source
		sourceChoiceBox.setItems(getSourceList());
		sourceChoiceBox.getSelectionModel().select(0);
		
		// UHD init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/UhdVBox.fxml"));
			uhdVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			new ExceptionAlert(this.getClass().getName(), e);
		}
	}
	
	
	
	private void changeDevice() {
		
		int index = Multiplex.getInstance().getOutputList().indexOf(output);
		vBox.getChildren().remove(1);
		
		// Unbind
		nameTextField.textProperty().unbind();
		titledPane.textProperty().unbind();
		
		if (deviceChoiceBox.getValue().contains("FarSync")) {
			output = new ETIDevice(nameTextField.getText());
			vBox.getChildren().add(farsyncSourcePane);
		}
		else {
			output = new ETIZeromq(nameTextField.getText());
			fxmlLoader.<UhdVBoxController>getController().setUhd((ETIZeromq)output);
			vBox.getChildren().add(uhdVBox);
		}
		
		// Binding
		nameTextField.textProperty().bindBidirectional(output.getName());
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		
		Multiplex.getInstance().getOutputList().set(index, output);
	}
	
	
	private ObservableList<String> getSourceList() {
		ObservableList<String> sources = FXCollections.observableArrayList("no device");
		
		try {
            Process proc = Runtime.getRuntime().exec("ifconfig -a"); 
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                new ExceptionAlert("DeviceTitledPane.getSourceList",e);
            }
            while (read.ready()) {
               String[] sArray = read.readLine().split(":");
               
               if (sArray[0].contains("sync")) {
            	   sources.add(sArray[0]);
               }   
            }
        } catch (IOException e) { 	
        	Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Unsupported OS");
    		alert.setHeaderText("FarSync can't find on this OS");
    		alert.showAndWait();	
        }
		
		if (sources.size() > 1) {
			sources.remove(0);
		}
		return sources;
	}
}
