package configUI.subchannels;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import addons.*;
import model.*;



public class AudioVBoxController implements Initializable {
	
	@FXML VBox vBox;
	
	// Audio
	@FXML Label encoderLabel, sourceLabel, fileLabel, urlLabel, alsaJackLabel, bitrateLabel, levelLabel, samplerateLabel, channelLabel, driftLabel;
	@FXML Button fileButton;
	@FXML TextField fileTextField, urlTextField;
	@FXML ChoiceBox<String> encoderChoiceBox, sourceChoiceBox, alsaJackChoiceBox, bitrateChoiceBox, levelChoiceBox, samplerateChoiceBox, channelChoiceBox;
	@FXML CheckBox driftCheckBox;
	@FXML GridPane sourcePane, filePane, urlPane, alsaJackPane, bitrateLevelPane, samplerateChannelPane, driftPane;

	// PAD
	@FXML Label lengthLabel, dlsLabel, dlsFileLabel, charsetLabel, rawDlsLabel, removeDlsLabel, slsLabel, delayLabel, imageFolderLabel, rawSlidesLabel, eraseSlidesLabel;
	@FXML Spinner<Integer> lengthSpinner, delaySpinner;
	@FXML TextField dlsFileTextField, imageFolderTextField;
	@FXML ChoiceBox<String> charsetChoiceBox;
	@FXML CheckBox dlsCheckBox, rawDlsCheckBox, removeDlsCheckBox, slsCheckBox, rawSlidesCheckBox, eraseSlidesCheckBox;
	@FXML Button dlsFileButton, imageFolderButton;
	@FXML GridPane padPane, advancedPadPane, dlsPane, dlsConfigPane, advancedDlsConfigPane, slsPane, slsConfigPane, advancedSlsConfigPane;

	
	private BooleanProperty advancedView;
	private Audio audio;
	
	private ChangeListener<String> srcListener, brListener, plListener;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Default Audio
		audio = new AAC();
		
		// Pad Spinner init.
		lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8,196));
		
		// SLS Delay init.
		delaySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,320,10,10));
		
		
		
		advancedView = new SimpleBooleanProperty();
		advancedView.addListener(c -> changePanes());
		
		// Listener
		srcListener = ((o, old, newSource) -> changeSourcePane());
		brListener = (o, old, newBitrate) -> audio.updatePlList(advancedView.get());
		plListener = (o, old, newPl) -> audio.updateCU();
		
		vBox.getChildren().clear();
		vBox.getChildren().addAll(sourcePane, filePane, bitrateLevelPane, padPane, dlsPane, slsPane);
	}
	
	public void setAudio(Audio audio) {
		
		// Unbind
		sourceChoiceBox.valueProperty().unbind();
		fileTextField.textProperty().unbind();
		alsaJackChoiceBox.valueProperty().unbind();
		urlTextField.textProperty().unbind();
		bitrateChoiceBox.valueProperty().unbind();
		levelChoiceBox.valueProperty().unbind();
		channelChoiceBox.valueProperty().unbind();
		driftCheckBox.selectedProperty().unbind();
		lengthSpinner.getValueFactory().valueProperty().unbind();
		dlsCheckBox.selectedProperty().unbind();
		dlsFileTextField.textProperty().unbind();
		charsetChoiceBox.valueProperty().unbind();
		rawDlsCheckBox.selectedProperty().unbind();
		removeDlsCheckBox.selectedProperty().unbind();
		slsCheckBox.selectedProperty().unbind();
		delaySpinner.getValueFactory().valueProperty().unbind();
		imageFolderTextField.textProperty().unbind();
		rawSlidesCheckBox.selectedProperty().unbind();
		eraseSlidesCheckBox.selectedProperty().unbind();
		
		// Remove old Listener
		sourceChoiceBox.valueProperty().removeListener(srcListener);  
		this.audio.getBitrate().removeListener(brListener);
		this.audio.getProtectionLevel().removeListener(plListener);
		
		
		// Set new Audio
		this.audio = audio;
		
		// Source
		sourceChoiceBox.setItems(audio.getSourceList());
		sourceChoiceBox.valueProperty().bindBidirectional(audio.getSource());
		new FileValidation(fileTextField, audio.getPath());
		alsaJackChoiceBox.valueProperty().bindBidirectional(audio.getPath());
		new UrlValidation(urlTextField, audio.getPath());

		// Bitrate
		bitrateChoiceBox.setItems(audio.getBitrateList());
		bitrateChoiceBox.valueProperty().bindBidirectional(audio.getBitrate());
		
		// Protection Level
		levelChoiceBox.setItems(audio.getProtectionLevelList());
		levelChoiceBox.valueProperty().bindBidirectional(audio.getProtectionLevel());
		
		// Samplerate
		samplerateChoiceBox.setItems(audio.getSamplerateList());
		samplerateChoiceBox.valueProperty().bindBidirectional(audio.getSamplerate());

		//Channel
		channelChoiceBox.setItems(audio.getChannelList());
		channelChoiceBox.valueProperty().bindBidirectional(audio.getChannel());

		// Drift
		driftCheckBox.selectedProperty().bindBidirectional(audio.getDriftCompensation());

		// Pad
		new NumberValidation(lengthSpinner, audio.getPad().getLength(), 8, 196, 1);

		// DLS
		dlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getDlsEnabled());
		new FileValidation(dlsFileTextField, audio.getPad().getFileDls());
		charsetChoiceBox.setItems(audio.getPad().getCharsetList());
		charsetChoiceBox.valueProperty().bindBidirectional(audio.getPad().getCharset());
		rawDlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRawDls());	
		removeDlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRemoveDls());

		// SLS
		slsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getSlsEnabled());
		new FileValidation(imageFolderTextField, audio.getPad().getDirSlides());
		new NumberValidation(delaySpinner, audio.getPad().getDelay(), 0, 320, 10);
		rawSlidesCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRawSlides());	
		eraseSlidesCheckBox.selectedProperty().bindBidirectional(audio.getPad().getEraseSlides());
		
		// Listener
		sourceChoiceBox.valueProperty().addListener((o, old, newSource) -> changeSourcePane());  
		this.audio.getBitrate().addListener(brListener);
		this.audio.getProtectionLevel().addListener(plListener);
		
	}
	
	public BooleanProperty getAdvancedView() {
		return advancedView;
	}
	
	
	public void changePanes() {
		
		if (advancedView.get()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), samplerateChannelPane);
			
			setDriftPane();
			setPadPane();
			setDlsPane();
			setSlsPane();
		}
		else {
			vBox.getChildren().removeAll(samplerateChannelPane, driftPane, advancedPadPane, advancedDlsConfigPane, advancedSlsConfigPane);
		}
		
		if (audio.getType().get().contains("dabplus")) {
			audio.updatePlList(advancedView.get());								// change visible of EEP-B Protections
		}
	}
	
		
	
	
	
	private void changeSourcePane() {
		String source = audio.getSource().get();
		audio.getPath().set("");					// if source change, then clear Input-Path

		vBox.getChildren().removeAll(filePane, alsaJackPane, urlPane, driftPane);

		if (advancedView.get() && !source.contains("JACK")) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), driftPane);
		}

		switch (source) {

		case "File":
			vBox.getChildren().add(1, filePane);
			break;

		case "Stream":
			vBox.getChildren().add(1, urlPane);
			break;

		case "ALSA":
			vBox.getChildren().add(1, alsaJackPane);
			alsaJackLabel.setText("Interface");
			alsaJackChoiceBox.setItems(FXCollections.observableArrayList("default:CARD=XXX","front:CARD=XXX", "null", "default:CARD=default"));	// TODO change sep. class
			alsaJackChoiceBox.setValue("default:CARD=XXX");
			break;

		case "JACK":
			vBox.getChildren().add(1, alsaJackPane);
			alsaJackLabel.setText("Client");
			alsaJackChoiceBox.setItems(FXCollections.observableArrayList("system:capture12","system:capture34","system:capture56")); //TODO test
			alsaJackChoiceBox.setValue("system:capture12");
			break;
		}
	}
	
	private void setDriftPane() {
		vBox.getChildren().remove(driftPane);
		
		if (!sourceChoiceBox.getValue().contains("JACK")) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), driftPane);
		}
	}
	
	private void setPadPane() {
		vBox.getChildren().remove(advancedPadPane);
		
		if (advancedView.get()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(dlsPane), advancedPadPane);
		}
	}
	
	private void setDlsPane() {
		vBox.getChildren().remove(advancedDlsConfigPane);
		
		if (dlsCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(slsPane), advancedDlsConfigPane);
		}
	}
	
	private void setSlsPane() {
		vBox.getChildren().remove(advancedSlsConfigPane);
		
		if (slsCheckBox.isSelected()) {
			vBox.getChildren().add(advancedSlsConfigPane);
		}
	}

	@FXML
	private void changeDlsConfigPane() {
		
		if (dlsCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(slsPane), dlsConfigPane);
			if (advancedView.get()) {
				vBox.getChildren().add(vBox.getChildren().indexOf(slsPane), advancedDlsConfigPane);
			}
		}
		else {
			vBox.getChildren().removeAll(dlsConfigPane, advancedDlsConfigPane);
		}
	}
	
	@FXML
	private void changeSlsConfigPane() {
		
		if (slsCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(slsPane)+1, slsConfigPane);
			if (advancedView.get()) {
				vBox.getChildren().add(vBox.getChildren().indexOf(slsConfigPane)+1, advancedSlsConfigPane);
			}
		}
		else {
			vBox.getChildren().removeAll(slsConfigPane, advancedSlsConfigPane);
		}
	}
		
	@FXML
	private void browseDlsFile() {
		File file = openFileChooser(dlsFileLabel.getText(), new ExtensionFilter("Text Files","*.txt"));
		if(file != null) {
			dlsFileTextField.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	private void browseAudioFile() {
		
		File file = openFileChooser(fileLabel.getText(), new ExtensionFilter("Audio Files",/*
				*/"*.mp3","*.aac","*.wav","*.ogg","*.m4a","*.aaf","*.wma","*.mp2","*.raw","*.m3u"));
		
		if(file != null) {
			fileTextField.setText(file.getAbsolutePath());
		}
	}
	
	private File openFileChooser(String title, ExtensionFilter filter) {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+title);
		chooser.getExtensionFilters().add(filter);
		return chooser.showOpenDialog(null);
	}
	
	@FXML
	private void browseFolder() {
		
		DirectoryChooser chooser = new DirectoryChooser(); 
		chooser.setTitle("Select Image-Folder");
		chooser.setInitialDirectory(new File("."));
		
		File folder = chooser.showDialog(null);
		if(folder != null) {
			imageFolderTextField.setText(folder.getAbsolutePath());
		}
	}

}
