package configUI.subchannels;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import addons.*;
import model.*;


public class SubchannelTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML ImageView titledPaneImageView;
	@FXML Button changePanesButton;
	@FXML GridPane subchannelPane, bitrateLevelPane, idPane;
	
	//Subchannel
	@FXML Label nameLabel, typeLabel, idLabel;
	@FXML TextField nameTextField, idTextField;
	@FXML ChoiceBox<String> typeChoiceBox;
	
	// ZeroMQ
	@FXML Label bufferLabel, preBufferLabel, encryptionLabel, secretKeyLabel, publicKeyLabel, encoderKeyLabel;
	@FXML TextField secretKeyTextField, publicKeyTextField, encoderKeyTextField, bufferTextField, preBufferTextField;
	@FXML Button secretKeyButton, publicKeyButton, encoderKeyButton;
	@FXML CheckBox encryptionCheckBox;
	@FXML GridPane zmqPane, zmqEncryptionPane;
	
		
	private Subchannel subchannel;
	
	private VBox audioVBox, dataVBox;
	private FXMLLoader audioFxmlLoader, dataFxmlLoader;
	
	private BooleanProperty advancedView;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		advancedView = new SimpleBooleanProperty(false);
			
		initInputPanes();
		
		// Default
		vBox.getChildren().clear();
		vBox.getChildren().addAll(subchannelPane, changePanesButton);
	}
	
	public void setSubchannel(Subchannel subch) {
		subchannel = subch;
		
		// TitledPane
		titledPane.textProperty().bind(Bindings.concat("Subchannel: ",subch.getName()));
		
		// Name
		new NameValidation(nameTextField, subch.getName(), Multiplex.getInstance().getSubchannelList());

		// Type
		typeChoiceBox.setItems(subch.getTypeNameList());	
		typeChoiceBox.getSelectionModel().select(0);
		typeChoiceBox.valueProperty().addListener(c -> changeType());


		// ZMQ only for Audio
		new NumberValidation(bufferTextField, subch.getZmqBuffer(), 0, 1000, 1, null);
		new NumberValidation(preBufferTextField, subch.getZmqPreBuffer(), 0, 500, 1, null);
		
		encryptionCheckBox.selectedProperty().bindBidirectional(subch.getEncryption());
		
		new FileValidation(secretKeyTextField, subch.getSecretKey());
		new FileValidation(publicKeyTextField, subch.getPublicKey());
		new FileValidation(encoderKeyTextField, subch.getEncoderKey());

		// ID
		new SubchannelIdValidation(idTextField, subch.getId(), Multiplex.getInstance().getSubchannelList());
		
		
		
		audioFxmlLoader.<AudioVBoxController>getController().setAudio((Audio)subchannel.getInput());
//		dataFxmlLoader.<DataVBoxController>getController().setData((Data)subchannel.getInput());
		
		vBox.getChildren().add(1, audioVBox);
	}

	private void initInputPanes() {
		
		// Audio & Data
		audioFxmlLoader = new FXMLLoader(getClass().getResource("/configUI/subchannels/AudioVBox.fxml"));
		dataFxmlLoader = new FXMLLoader(getClass().getResource("/configUI/subchannels/DataVBox.fxml"));

		try {
			audioVBox = (VBox) audioFxmlLoader.load();
			dataVBox  = (VBox) dataFxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		audioFxmlLoader.<AudioVBoxController>getController().getAdvancedView().bind(advancedView);
		dataFxmlLoader.<DataVBoxController>getController().getAdvancedView().bind(advancedView);
	}

	private void changeType() {
		vBox.getChildren().removeAll(audioVBox, dataVBox, zmqPane, zmqEncryptionPane, idPane);

		subchannel.setInput(typeChoiceBox.getValue());		
		String type = subchannel.getInput().getType().get();
		
		// Titled-Icon
		if (type.contains("audio")) {
			titledPaneImageView.setImage(new Image("icons/dab-icon.png"));
		} else {
			titledPaneImageView.setImage(new Image("icons/dab-plus-icon.png"));
		}
		
		if (type.contains("audio") || type.contains("dabplus")) {					// Audio (MP2/AAC)
			
			audioFxmlLoader.<AudioVBoxController>getController().setAudio((Audio)subchannel.getInput());
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), audioVBox);
			
			if (advancedView.get()) {
				vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), zmqPane);
				
				if (encryptionCheckBox.isSelected()) {
					vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), zmqEncryptionPane);	
				}
			}	
		} 
		else {																		// Data (Data, Packet, EPM)
			
			dataFxmlLoader.<DataVBoxController>getController().setData((Data)subchannel.getInput());
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), dataVBox);
		}
		
		if (advancedView.get()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), idPane);	
		}
		
		Multiplex.getInstance().updateCU();											// update CU on ProgessPane
	}
	
	@FXML
	private void changePanes() {
		advancedView.set(!advancedView.get());		// invert bool
		
		if (advancedView.get()) {
			changePanesButton.setText("Show basic parameters");
			
			// ZMQ
			if (vBox.getChildren().contains(audioVBox)) {
				vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), zmqPane);
				
				if (encryptionCheckBox.isSelected()) {
					vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), zmqEncryptionPane);	
				}
			}
			
			// ID
			vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), idPane);	
				
		}	
		else {	
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().removeAll(idPane, zmqPane, zmqEncryptionPane);
		}
	}
	
	
	
	@FXML
	private void updateEncryptionPane() {
		vBox.getChildren().remove(zmqEncryptionPane);
		
		if (encryptionCheckBox.isSelected()) {
			vBox.getChildren().add(vBox.getChildren().indexOf(idPane), zmqEncryptionPane);	
		}
	}

	@FXML
	private void browseSecretKeyFile() {
		
		File file = openFileChooser(secretKeyLabel.getText(), new ExtensionFilter("Secret Files","*.sec"));
		if(file != null) {
			secretKeyTextField.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	private void browsePublicKeyFile() {
		
		File file = openFileChooser(publicKeyLabel.getText(), new ExtensionFilter("Public Files","*.pub"));
		if(file != null) {
			publicKeyTextField.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	private void browseEncoderKeyFile() {
		File file = openFileChooser(encoderKeyLabel.getText(), new ExtensionFilter("Public Files","*.pub"));
		if(file != null) {
			encoderKeyTextField.setText(file.getAbsolutePath());
		}
	}
	
	private File openFileChooser(String title, ExtensionFilter filter) {
		
		FileChooser chooser = new FileChooser(); 
		chooser.setInitialDirectory(new File("."));
		chooser.setTitle("Select "+title);
		chooser.getExtensionFilters().add(filter);
		return chooser.showOpenDialog(null);
	}
}
