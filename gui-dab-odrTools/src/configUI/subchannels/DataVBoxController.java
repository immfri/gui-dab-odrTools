package configUI.subchannels;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import addons.FileValidation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.*;


public class DataVBoxController implements Initializable {
	
	@FXML VBox vBox;
	
	@FXML Label sourceLabel, fileLabel, bitrateLabel, levelLabel;
	@FXML Button fileButton;
	@FXML TextField fileTextField;
	@FXML ChoiceBox<String> sourceChoiceBox, bitrateChoiceBox, levelChoiceBox;
	@FXML GridPane sourcePane, filePane, bitrateLevelPane;

	private Data data;
	private BooleanProperty advancedView;
	private ChangeListener<String> brListener, plListener;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Default Audio
		data = new Data();
		
		advancedView = new SimpleBooleanProperty();
		
		// Listener
		brListener = (o, old, newBitrate) -> data.updatePlList(advancedView.get());
		plListener = (o, old, newPl) -> data.updateCU();
	}

	public void setData(Data data) {
		
		// Unbind
		sourceChoiceBox.valueProperty().unbind();
		fileTextField.textProperty().unbind();
		bitrateChoiceBox.valueProperty().unbind();
		levelChoiceBox.valueProperty().unbind();
		
		// Remove old Listener
		this.data.getBitrate().removeListener(brListener);
		this.data.getProtectionLevel().removeListener(plListener);
		
		// Set new Data
		this.data = data;
		
		// Source
		sourceChoiceBox.setItems(data.getSourceList());
		sourceChoiceBox.valueProperty().bindBidirectional(data.getSource());
		
		// File
		new FileValidation(fileTextField, data.getPath());

		// Bitrate
		bitrateChoiceBox.setItems(data.getBitrateList());
		bitrateChoiceBox.valueProperty().bindBidirectional(data.getBitrate());
		
		// Protection Level
		levelChoiceBox.setItems(data.getProtectionLevelList());
		levelChoiceBox.valueProperty().bindBidirectional(data.getProtectionLevel());
		
		// Add new Listener
		this.data.getBitrate().addListener(brListener);
		this.data.getProtectionLevel().addListener(plListener);
	}
	
	
	public BooleanProperty getAdvancedView() {
		return advancedView;
	}
		
	@FXML
	private void browseInputFile() {
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+ fileLabel.getText());
		
		File file = chooser.showOpenDialog(null);
		if(file != null) {
			fileTextField.setText(file.getAbsolutePath());
		}
	}
}
