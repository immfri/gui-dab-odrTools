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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import addons.*;
import model.Multiplex;
import model.output.*;


public class ModulatorVBoxController implements Initializable {

	@FXML VBox vBox;
	
	@FXML Label gainModeLabel, samplerateLabel, digGainLabel, firLabel, firFileLabel;
	@FXML ChoiceBox<String> gainmodeChoiceBox;
	@FXML TextField samplerateTextField, firFileTextField;
	@FXML Slider digGainSlider;
	@FXML CheckBox firCheckBox;
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
	@FXML TextField framesQueueTextField;
	@FXML GridPane inputPane;

	private Modulator modulator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		modulator = new IQFile();
		
		// FIR
		firCheckBox.selectedProperty().addListener(c -> setFirFilterPane());
		new FileValidation(firFileTextField, modulator.getFiltertapsfile(), true);
		
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
		this.modulator = modulator;
		
		// Gain Mode
		gainmodeChoiceBox.valueProperty().unbindBidirectional(modulator.getGainmode());
		gainmodeChoiceBox.setItems(modulator.getGainModeList());
		gainmodeChoiceBox.valueProperty().bindBidirectional(modulator.getGainmode());
		
		// Rate
		new NumberValidation(samplerateTextField, modulator.getRate(), 1000, 50000000, 1, null);
		
		// Dig Gain
		digGainSlider.valueProperty().bindBidirectional(modulator.getDigital_gain());
		digGainSlider.valueProperty().bindBidirectional(modulator.getDigital_gain());
		
		// Fir
		firCheckBox.selectedProperty().unbindBidirectional(modulator.getEnabled());
		firCheckBox.selectedProperty().bindBidirectional(modulator.getEnabled());
		
		firFileTextField.textProperty().unbindBidirectional(modulator.getFiltertapsfile());
		firFileTextField.textProperty().bindBidirectional(modulator.getFiltertapsfile());
		
		// Remote
		new PortValidation(telnetTextField, modulator.getTelnetport(), 0);
		new PortValidation(zmqTextField, modulator.getZmqctrlendpoint(), 0);
		
		// Syslog
		syslogCheckBox.selectedProperty().bindBidirectional(modulator.getSyslog());
		syslogCheckBox.selectedProperty().bindBidirectional(modulator.getSyslog());
		
		// Filelog
		filelogCheckBox.selectedProperty().unbindBidirectional(modulator.getFilelog());
		filelogCheckBox.selectedProperty().bindBidirectional(modulator.getFilelog());
		new FileValidation(logfileTextField, modulator.getFilename(), false);
		
		// Frames Queue
		new NumberValidation(framesQueueTextField, modulator.getMax_frames_queued(), 0, 10000, 1, null);
	}
	

	
	@FXML 
	private void browseFirFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setTitle("Select "+ firFileLabel.getText());
		
		if (Multiplex.getInstance().getProjectFolder() == null) {
			chooser.setInitialDirectory(new File("."));
		}
		else {
			chooser.setInitialDirectory(Multiplex.getInstance().getProjectFolder());
		}	
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			String path = file.getAbsolutePath();
			path = path.replace(chooser.getInitialDirectory().getAbsolutePath(), ".");
			firFileTextField.setText(path);
		}		
	}
	
	
	@FXML 
	private void browseLogFile() {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setTitle("Select "+ logfileLabel.getText());
		
		if (Multiplex.getInstance().getProjectFolder() == null) {
			chooser.setInitialDirectory(new File("."));
		}
		else {
			chooser.setInitialDirectory(Multiplex.getInstance().getProjectFolder());
		}
		File file = chooser.showOpenDialog(null);
		
		if(file != null) {
			String path = file.getAbsolutePath();
			path = path.replace(chooser.getInitialDirectory().getAbsolutePath(), ".");
			logfileTextField.setText(path);
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
