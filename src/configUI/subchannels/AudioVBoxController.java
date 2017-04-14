package configUI.subchannels;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import addons.*;
import model.*;


public class AudioVBoxController implements Initializable {
	
	@FXML VBox vBox;
	
	// Audio
	@FXML Label encoderLabel, sourceLabel, pathLabel, bitrateLabel, levelLabel, samplerateLabel, channelLabel, driftLabel;
	@FXML TextField pathTextField;
	@FXML ChoiceBox<String> encoderChoiceBox, sourceChoiceBox, bitrateChoiceBox, levelChoiceBox, samplerateChoiceBox, channelChoiceBox;
	@FXML CheckBox driftCheckBox;
	@FXML GridPane sourcePane, samplerateChannelPane, driftPane;

	// PAD
	@FXML Label lengthLabel, dlsLabel, dlsFileLabel, charsetLabel, rawDlsLabel, removeDlsLabel, slsLabel, delayLabel, imageFolderLabel, rawSlidesLabel, eraseSlidesLabel;
	@FXML TextField lengthTextField, dlsFileTextField, imageFolderTextField, delayTextField;
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
		
		// Listener
		advancedView = new SimpleBooleanProperty();
		advancedView.addListener(c -> changePanes());
		
		srcListener = ((o, old, newSource) -> changeSourcePane());
		brListener = (o, old, newBitrate) -> audio.updatePlList(advancedView.get());
		plListener = (o, old, newPl) -> audio.updateCU();
		
		dlsCheckBox.selectedProperty().addListener(c -> setDlsConfigPane());
		slsCheckBox.selectedProperty().addListener(c -> setSlsConfigPane());
		
		
		// Pane init.
		vBox.getChildren().clear();
		vBox.getChildren().addAll(sourcePane, padPane, dlsPane, slsPane);
	}
	
	public void setAudio(Audio audio) {
		
		// Unbind
		sourceChoiceBox.valueProperty().unbind();
		bitrateChoiceBox.valueProperty().unbind();
		levelChoiceBox.valueProperty().unbind();
		channelChoiceBox.valueProperty().unbind();
		driftCheckBox.selectedProperty().unbind();
		dlsCheckBox.selectedProperty().unbind();
		charsetChoiceBox.valueProperty().unbind();
		rawDlsCheckBox.selectedProperty().unbind();
		removeDlsCheckBox.selectedProperty().unbind();
		slsCheckBox.selectedProperty().unbind();
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
		
		// Path
		pathTextField.textProperty().unbindBidirectional(audio.getPath());
		pathTextField.textProperty().bindBidirectional(audio.getPath());
		
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

		// Pad-length
		new NumberValidation(lengthTextField, audio.getPad().getLength(), 8, 196, 1, null);

		// DLS	
		dlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getDlsEnabled());	
		new FileValidation(dlsFileTextField, audio.getPad().getFileDls(), true);
		charsetChoiceBox.setItems(audio.getPad().getCharsetList());
		charsetChoiceBox.valueProperty().bindBidirectional(audio.getPad().getCharset());
		rawDlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRawDls());	
		removeDlsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRemoveDls());

		// SLS
		slsCheckBox.selectedProperty().bindBidirectional(audio.getPad().getSlsEnabled());
		new FolderValidation(imageFolderTextField, audio.getPad().getDirSlides());
		new NumberValidation(delayTextField, audio.getPad().getDelay(), 0, 320, 1, null);
		rawSlidesCheckBox.selectedProperty().bindBidirectional(audio.getPad().getRawSlides());	
		eraseSlidesCheckBox.selectedProperty().bindBidirectional(audio.getPad().getEraseSlides());
		
		// Listener
		sourceChoiceBox.valueProperty().addListener((o, old, newSource) -> changeSourcePane());  
		this.audio.getBitrate().addListener(brListener);
		this.audio.getProtectionLevel().addListener(plListener);
		
		this.audio.updateCU();
	}
	
	
	
	private void changePanes() {
		// change visible of EEP-B Protections
		if (audio.getType().get().contains("dabplus")) {
			audio.updatePlList(advancedView.get());								
		}
				
		setSampleratePane();
		setDriftPane();
		setPadPane();
		setDlsConfigPane();
		setSlsConfigPane();
	}
	
	
	private void changeSourcePane() {
		vBox.getChildren().remove(driftPane);
		
		String source = audio.getSource().getValue();

		// Drift Pane
		if (advancedView.get() && !source.contains("JACK")) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), driftPane);
		}
		
		switch (source) {
		case "Webstream":
			pathLabel.setText("URL");
			pathTextField.setPromptText("no URL");
			break;

		case "ALSA":
			pathLabel.setText("Interface");
			pathTextField.setPromptText("no Interface");
			pathTextField.setText("default");
			break;

		case "JACK":
			pathLabel.setText("Client");
			pathTextField.setPromptText("no Client");
		}
		
		pathTextField.textProperty().unbindBidirectional(audio.getPath());
		pathTextField.textProperty().bindBidirectional(audio.getPath());
	}
	
	
	private void setSampleratePane() {
		vBox.getChildren().remove(samplerateChannelPane);
		
		if (advancedView.get()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), samplerateChannelPane);
		}
	}
	
	private void setDriftPane() {
		vBox.getChildren().remove(driftPane);
		
		if (!sourceChoiceBox.getSelectionModel().isSelected(2)) {
			vBox.getChildren().add(vBox.getChildren().indexOf(padPane), driftPane);
		}
	}
	
	private void setPadPane() {
		vBox.getChildren().remove(advancedPadPane);
		
		if (advancedView.getValue()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(dlsPane), advancedPadPane);
		}
	}
	
	private void setDlsConfigPane() {
		vBox.getChildren().removeAll(dlsConfigPane, advancedDlsConfigPane);
		
		if (dlsCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(dlsPane)+1, dlsConfigPane);
			
			// Advanced DLS
			if (advancedView.getValue()) {
				vBox.getChildren().add(vBox.getChildren().indexOf(dlsPane)+2, advancedDlsConfigPane);
			}
		}
	}
	
	private void setSlsConfigPane() {
		vBox.getChildren().removeAll(slsConfigPane, advancedSlsConfigPane);
		
		if (slsCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(slsPane)+1, slsConfigPane);
			
			// Advanced SLS
			if (advancedView.get()) {
				vBox.getChildren().add(vBox.getChildren().indexOf(slsPane)+2, advancedSlsConfigPane);
			}
		}
	}
	
	
	public BooleanProperty getAdvancedView() {
		return advancedView;
	}
	

	@FXML
	private void changeDlsConfigPane() {
		setDlsConfigPane();
	}
	
	@FXML
	private void changeSlsConfigPane() {
		setSlsConfigPane();
	}
		
	@FXML
	private void browseDlsFile() {

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select DLS-File");
		if (Multiplex.getInstance().getProjectFolder() == null) {
			chooser.setInitialDirectory(new File("."));
		}
		else {
			chooser.setInitialDirectory(Multiplex.getInstance().getProjectFolder());
		}
		
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			String path = file.getAbsolutePath();
			path = path.replace(chooser.getInitialDirectory().getAbsolutePath(), ".");
			dlsFileTextField.setText(path);
		}
	}
	
	@FXML
	private void browseFolder() {
		
		DirectoryChooser chooser = new DirectoryChooser(); 
		chooser.setTitle("Select Image-Folder");
		if (Multiplex.getInstance().getProjectFolder() == null) {
			chooser.setInitialDirectory(new File("."));
		}
		else {
			chooser.setInitialDirectory(Multiplex.getInstance().getProjectFolder());
		}
		
		File folder = chooser.showDialog(null);
		if(folder != null) {
			String path = folder.getAbsolutePath();
			path = path.replace(chooser.getInitialDirectory().getAbsolutePath(), ".");
			imageFolderTextField.setText(path);
		}
	}
}
