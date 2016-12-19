package configUI.outputs;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import addons.*;
import model.output.*;


public class ModulatorVBoxController implements Initializable {

	@FXML VBox vBox;
	
	@FXML Label gainModeLabel, samplerateLabel, digGainLabel, firLabel, firFileLabel;
	@FXML ChoiceBox<String> gainmodeChoiceBox;
	@FXML Spinner<Integer> samplerateSpinner;
	@FXML Slider digGainSlider;
	@FXML CheckBox firCheckBox;
	@FXML TextField firFileTextField;
	@FXML Button firFileBrowseButton;
	@FXML GridPane modPane, firFilePane;
	
	// DabMod-Remote & -Logging
	@FXML Label telnetLabel, zmqLabel, syslogLabel, filelogLabel, logfileLabel; 
	@FXML CheckBox syslogCheckBox, filelogCheckBox;
	@FXML TextField telnetTextField, zmqTextField, logfileTextField;
	@FXML Button logfileBrowseButton;
	@FXML GridPane remotePane, logPane, logFilePane;
	
	// Input
	@FXML Label framesQueueLabel;
	@FXML Spinner<Integer> framesQueueSpinner;
	@FXML GridPane inputPane;

	private Modulator modulator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		modulator = new IQFile();
		
		// FIR
		firCheckBox.selectedProperty().addListener(c -> setFirFilterPane());
		new FileValidation(firFileTextField, modulator.getFiltertapsfile());
		
		// Remote Control
		telnetTextField.textProperty().addListener(c -> {
			modulator.getTelnet().setValue(!telnetTextField.getText().contentEquals("0"));
		});
		
		zmqTextField.textProperty().addListener(c -> {
			modulator.getZmqctrl().setValue(!zmqTextField.getText().contentEquals("0"));
		});
		
		// Log
		filelogCheckBox.selectedProperty().addListener(c -> setLogfilePane());
		
		
		// init. Panes
		setFirFilterPane();
		setLogfilePane();
	}
	
	public void setModulator(Modulator modulator) {
		
		// Unbind
		gainmodeChoiceBox.valueProperty().unbind();
		digGainSlider.valueProperty().unbind();
		firCheckBox.selectedProperty().unbind();
		firFileTextField.textProperty().unbind();
		syslogCheckBox.selectedProperty().unbind();
		filelogCheckBox.selectedProperty().unbind();
		logfileTextField.textProperty().unbind();
		
		this.modulator = modulator;
		
		// set Items
		gainmodeChoiceBox.setItems(modulator.getGainModeList());
		new NumberValidation(samplerateSpinner, modulator.getRate(), 1000, 50000000, 1);
		new PortValidation(telnetTextField, modulator.getTelnetport());
		new PortValidation(zmqTextField, modulator.getZmqctrlendpoint());
		new NumberValidation(framesQueueSpinner, modulator.getMax_frames_queued(), 0, 10000, 1);
		
		// Binding	
		gainmodeChoiceBox.valueProperty().bindBidirectional(modulator.getGainmode());
		
		digGainSlider.valueProperty().bindBidirectional(modulator.getDigital_gain());
		firCheckBox.selectedProperty().bindBidirectional(modulator.getEnabled());
		firFileTextField.textProperty().bindBidirectional(modulator.getFiltertapsfile());
		syslogCheckBox.selectedProperty().bindBidirectional(modulator.getSyslog());
		filelogCheckBox.selectedProperty().bindBidirectional(modulator.getFilelog());
		new FolderValidation(logfileTextField,modulator.getFilename());
	}
	

	
	@FXML 
	private void browseFirFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+ firFileLabel.getText());
		chooser.getExtensionFilters().add(new ExtensionFilter("Text Files","*.txt"));
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			firFileTextField.setText(file.getAbsolutePath());
		}		
	}
	
	
	@FXML 
	private void browseLogFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+ logfileLabel.getText());
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			logfileTextField.setText(file.getAbsolutePath());
		}	
	}
	
	
	private void setFirFilterPane() {
		vBox.getChildren().remove(firFilePane);
		
		if (firCheckBox.isSelected()) {
			vBox.getChildren().add(1, firFilePane);
		}
	}
	
	
	private void setLogfilePane() {
		vBox.getChildren().remove(logFilePane);
		
		if (filelogCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(inputPane), logFilePane);
		}		
	}
}
