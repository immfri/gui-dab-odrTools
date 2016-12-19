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

	@FXML Label nameLabel, fileLabel, sourceLabel, formatLabel;
	@FXML TextField nameTextField, sourceTextField;
	@FXML ChoiceBox<String> fileChoiceBox, formatChoiceBox;
	@FXML Button browseButton;
	@FXML GridPane filePane, formatPane;
	
	private boolean advancedView;
	private Output output;
	private VBox modulatorVBox;
	private FXMLLoader fxmlLoader;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// create new Output
		int index = Multiplex.getInstance().getOutputList().size();
		String name = "NEW Output "+index;

		output = new ETIFile(name);
		Multiplex.getInstance().getOutputList().add(output);
		
		// Name & TitledPane
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		new NameValidation(nameTextField, output.getName(), Multiplex.getInstance().getOutputList());
		
		// File
		fileChoiceBox.setItems(FXCollections.observableArrayList("ETI","I/Q-Samples"));
		fileChoiceBox.getSelectionModel().select(0);
		fileChoiceBox.valueProperty().addListener(chnage -> changeFile());
		
		// Format/Type
		formatChoiceBox.setItems(((ETIFile)output).getTypeList());
		formatChoiceBox.valueProperty().bindBidirectional(((ETIFile)output).getType());
		
		// Source
		new FolderValidation(sourceTextField, output.getDestination());
		
		// Modulator Pane init.
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/ModulatorVBox.fxml"));
			modulatorVBox = fxmlLoader.load();
		} 
		catch (IOException e) {
			new ExceptionAlert(this.getClass().getName(), e);
		}
		
		// Default
		advancedView = false;
		vBox.getChildren().remove(formatPane);
	}


	private void changeFile() {
		int index = Multiplex.getInstance().getOutputList().indexOf(output);
		
		// Unbind
		nameTextField.textProperty().unbind();
		titledPane.textProperty().unbind();
		formatChoiceBox.valueProperty().unbind();
		
		
		if (fileChoiceBox.getValue().contains("ETI")) {
			ETIFile etiFile =  new ETIFile(nameTextField.getText());
			output = etiFile;
			
			// Source
			new FileValidation(sourceTextField, etiFile.getDestination());
			
			// Format/Type
			formatChoiceBox.setItems(etiFile.getTypeList());
			formatChoiceBox.valueProperty().bindBidirectional(etiFile.getType());
		}
		else {
			output = new ETIZeromq(nameTextField.getText());
			IQFile iqFile = new IQFile();
			((ETIZeromq)output).setMod(iqFile);
			fxmlLoader.<ModulatorVBoxController>getController().setModulator(iqFile);
			
			// Source
			new FileValidation(sourceTextField, iqFile.getFilename());
			
			// Format
			formatChoiceBox.setItems(iqFile.getFormatList());
			formatChoiceBox.valueProperty().bindBidirectional(iqFile.getFormat());
		}
		
		// Binding
		nameTextField.textProperty().bindBidirectional(output.getName());
		titledPane.textProperty().bind(Bindings.concat("Output: ",output.getName()));
		
		Multiplex.getInstance().getOutputList().set(index, output);
		
		setModulatorPane();
	}
	

	@FXML
	private void changePanes() {
		advancedView = !advancedView;
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");
			vBox.getChildren().add(1, formatPane);
			
			setModulatorPane();		
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().removeAll(formatPane, modulatorVBox);
		}	
	}
	

	private void setModulatorPane() {
		vBox.getChildren().remove(modulatorVBox);
		
		if (advancedView && fileChoiceBox.getValue().contains("I/Q")) {
			vBox.getChildren().add(2, modulatorVBox);
		}
	}


	@FXML
	private void browseFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+sourceLabel.getText());
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			sourceTextField.setText(file.toString());
		}
	}

}
