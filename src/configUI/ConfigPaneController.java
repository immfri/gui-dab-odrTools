package configUI;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.*;
import processing.*;
import io.*;


public class ConfigPaneController implements Initializable {
	
	@FXML Tab ensembleTab, servicesTab, componentsTab, subchannelsTab, outputsTab;	
	@FXML Button runningButton, newButton, openButton, saveButton, saveAsButton;
	@FXML TextField mailTextField;
	@FXML CheckBox dablinCheckBox;
	@FXML Label cuLabel, dablinLabel;
	@FXML ProgressBar cuProgressBar;
	
	private Multiplex mux;
	private ProcessManager pm;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mux = Multiplex.getInstance();
		pm = new ProcessManager(runningButton);
		
		// Listener for running Button
		runningButton.textProperty().addListener(c -> changeButtonView());
		
		// Set up save-Button
		saveButton.setDisable(true);
		
		// Init. Tabs
		try {
			ensembleTab.setContent((VBox)FXMLLoader.load(getClass().getResource("/configUI/ensemble/EnsemblePane.fxml")));
			servicesTab.setContent((VBox)FXMLLoader.load(getClass().getResource("/configUI/services/ServicesVBox.fxml")));	
			componentsTab.setContent((VBox)FXMLLoader.load(getClass().getResource("/configUI/components/ComponentsVBox.fxml")));	
			subchannelsTab.setContent((VBox)FXMLLoader.load(getClass().getResource("/configUI/subchannels/SubchannelsVBox.fxml")));	
			outputsTab.setContent((VBox)FXMLLoader.load(getClass().getResource("/configUI/outputs/OutputsVBox.fxml")));		
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// CU
		DoubleProperty maxCU = new SimpleDoubleProperty(864);
		SimpleIntegerProperty currentCU = new SimpleIntegerProperty(0);
		currentCU.bind(Multiplex.getInstance().getTotalCU());
		currentCU.addListener(change -> setCuAlert(currentCU.get()));
		
		cuLabel.textProperty().bind(currentCU.asString());
		cuProgressBar.progressProperty().bind(currentCU.divide(maxCU));
		
		// Mail
		mailTextField.textProperty().bindBidirectional(mux.getEMail());
		
		// DABlin
		dablinCheckBox.selectedProperty().bindBidirectional(mux.getDablinActivate());
	}
	
	@FXML
	private void clickedRunningButton() {
		
		String os = System.getProperty("os.name");
		
		if ((os.contains("ix") || os.contains("nux")) && mux.getProjectFolder() != null) {
			
			if (runningButton.getText().contentEquals("Start")) {
				runningButton.setText("Stop");
				pm.start();
			}
			else {
				runningButton.setText("Start");
				pm.stop();
			}
			
		}
		else if (mux.getProjectFolder() != null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("This OS is not fully supported");
			alert.setContentText("The Running-Operation did not working on this OS. Only for UNIX!");
			alert.show();
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("No Project");
			alert.setContentText("Load or create first a Project!");
			alert.show();
		}
	}
	
	
	private void changeButtonView() {
		
		ImageView view = new ImageView();
		view.setFitHeight(70);
		view.setFitWidth(70);
		
		if (runningButton.getText().contentEquals("Start")) {
			view.setImage(new Image("icons/media-start-icon.png"));
		}
		else {
			view.setImage(new Image("icons/media-stop-icon.png"));
		}
		
		runningButton.setGraphic(view);
	}
	
	@FXML
	private void newConfig() {
		
		// Warning delete Configuration
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Remove the Configuration");
		alert.setHeaderText("The complete Configuration will be lost");

		ButtonType ok = new ButtonType("OK");
		alert.getButtonTypes().setAll(ok, new ButtonType("Cancel"));
		Optional<ButtonType> result = alert.showAndWait();

		if (result != null) {
			if (result.get() == ok) {


				mux.setProjectFolder(null);
				setNewMux(false);	

				// Set up save-Button
				saveButton.setDisable(true);

				// Remove Projekt-Name
				((Stage)openButton.getScene().getWindow()).setTitle("GUI4ODR");
			}
		}
	}
	
	@FXML
	private void openConfig() throws IOException {
		setNewMux(true); 
		
		File projectFolder = mux.getProjectFolder();
		
		// Choose Project Folder
		DirectoryChooser chooser = new DirectoryChooser(); 
		chooser.setTitle("Browse Folder");
		
		if (projectFolder != null) {
			chooser.setInitialDirectory(projectFolder.getParentFile());	
		}
		else {
			chooser.setInitialDirectory(new File("."));	
		}
		
		projectFolder = chooser.showDialog(null);
		
		if (projectFolder != null) {
			boolean validBashFileExist = false;
			mux.setProjectFolder(projectFolder);
			
			// Set up save-Button
			saveButton.setDisable(false);

			// Search valid Bash File
			for (File bashFile: projectFolder.listFiles()) {	
				if (bashFile.getName().length() > 3 && bashFile.getName().indexOf(".sh") == (bashFile.getName().length()-3)) {

					// Bash File is valid
					validBashFileExist = true;
					new Load(projectFolder, bashFile);

					// Set Projekt-Name
					((Stage)openButton.getScene().getWindow()).setTitle("Project from "+projectFolder.getAbsolutePath());
					break;
				}
			}

			// Alert/Dialog Folder not valide Project inside
			if (!validBashFileExist) {
				Alert alertBash = new Alert(AlertType.ERROR);
				alertBash.setTitle("Project can't load");
				alertBash.setContentText("Need an allowed Bash-File!");
				alertBash.showAndWait();
			} 
			
			// Project done to load
			else {
				Alert alertDone = new Alert(AlertType.INFORMATION);
				alertDone.setTitle("DONE");
				alertDone.setContentText("Project is completly loaded!");
				alertDone.show();
			}
		}
		
	}
	
	@FXML
	private void saveConfig() throws IOException {	
		new Save();		
	}
	
	@FXML
	private void saveAsConfig() throws IOException {
		
		DirectoryChooser chooser = new DirectoryChooser(); 
		chooser.setTitle("Browse Folder");
		chooser.setInitialDirectory(new File("."));
		
		mux.setProjectFolder(chooser.showDialog(null));
		
		if (mux.getProjectFolder() != null) {
			new Save();
			
			// Set up save-Button
			saveButton.setDisable(false);
			
			// Set Project-Name
			((Stage)saveButton.getScene().getWindow()).setTitle("Project from "+mux.getProjectFolder().getAbsolutePath());
		}
	}
	
	
	private void setCuAlert(int cu) {
		if (cu > 864) {
			cuLabel.setStyle("-fx-text-fill: red;");
			
			// Warning Dialog: over Limit
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("CU Size exceeded");
			alert.setContentText("Decrease Bitrate/Protection Level or reduce Services!");
			alert.showAndWait();				
		} 
		else cuLabel.setStyle("");
	}

	
	private void setNewMux(boolean removeOnlyAllLists) {
		
		// Set up General and Ensemble
		if (!removeOnlyAllLists) {
			mux.newGeneral();
			mux.newEnsemble();
		}
		
		// Clear all Lists
		mux.getComponentList().clear();
		mux.getServiceList().clear();
		mux.getSubchannelList().clear();
		mux.getOutputList().clear();	
		
		// Activate Garbage Collector, delete all un-/used Object
		System.gc();
	}
	
}
