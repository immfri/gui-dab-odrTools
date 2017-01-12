package configUI.outputs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import addons.*;
import model.Multiplex;
import model.output.*;


public class FileTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML Button changePanesButton;

	@FXML Label nameLabel, typeLabel, fileLabel, formatLabel;
	@FXML TextField nameTextField, fileTextField;
	@FXML ChoiceBox<String> typeChoiceBox, formatChoiceBox;
	@FXML Button browseButton;
	@FXML GridPane filePane, formatPane;
	
	private boolean advancedView;
	private Output output;
	private VBox modulatorVBox;
	private FXMLLoader fxmlLoader;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// File
		typeChoiceBox.setItems(FXCollections.observableArrayList("ETI", "I/Q-Samples"));
		
		// Modulator Pane init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/ModulatorVBox.fxml"));
			modulatorVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// Default
		advancedView = false;
		vBox.getChildren().remove(formatPane);
	}
	
	
	public void createOutput(Output out) {
		
		if (out.getFormat().getValue().contains("fifo")) {		// ETI-File
			typeChoiceBox.getSelectionModel().select(0);
		}
		else {
			typeChoiceBox.getSelectionModel().select(1);		// I/Q File
			setModulatorVBox((ETIZeromq)out);
		}
		setModulatorPane();
		typeChoiceBox.valueProperty().addListener(change -> changeFile());
		
		setOutput(out);
	}
	

	private void setOutput(Output out) {
		output = out;
		
		// TitledPane
		titledPane.textProperty().unbind();
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		
		// Name
		new NameValidation(nameTextField, output.getName(), Multiplex.getInstance().getOutputList());

		if (out.getFormat().getValue().contains("fifo")) {	// ETI-File
			
			// Format/Type
			formatChoiceBox.setItems(((ETIFile)out).getTypeList());
			formatChoiceBox.valueProperty().unbindBidirectional(((ETIFile)out).getType());
			formatChoiceBox.valueProperty().bindBidirectional(((ETIFile)out).getType());
			
			// File
			new FileValidation(fileTextField, out.getDestination());
		} 
		else {
			IQFile iqFile = (IQFile) ((ETIZeromq)out).getMod();
			
			// Format
			formatChoiceBox.setItems(iqFile.getFormatList());		
			formatChoiceBox.valueProperty().unbindBidirectional(iqFile.getFormat());
			formatChoiceBox.valueProperty().bindBidirectional(iqFile.getFormat());
			
			// File
			new FileValidation(fileTextField, iqFile.getFile());
		}
	}
	

	private void changeFile() {
		
		int index = Multiplex.getInstance().getOutputList().indexOf(output);
		
		Output out = null;
		if (typeChoiceBox.getSelectionModel().isSelected(0)) {		// ETI-File
			out =  new ETIFile(nameTextField.getText());
		}
		else {														// I/Q-File
			ETIZeromq zmq  = new ETIZeromq(nameTextField.getText());
			zmq.setMod(new IQFile());
			out = zmq;
			
			setModulatorVBox(zmq);
		}
		setModulatorPane();
		
		setOutput(out);
		Multiplex.getInstance().getOutputList().set(index, output);	
	}
	
	private void setModulatorPane() {
		vBox.getChildren().remove(modulatorVBox);
		
		if (advancedView && typeChoiceBox.getSelectionModel().isSelected(1)) {
			vBox.getChildren().add(2, modulatorVBox);
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
			vBox.getChildren().add(1, formatPane);	
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().removeAll(formatPane, modulatorVBox);
		}
		
		setModulatorPane();		
	}

	@FXML
	private void browseFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select " + fileLabel.getText());
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			fileTextField.setText(file.toString());
		}
	}
}
