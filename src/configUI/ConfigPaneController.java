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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.*;
import io.*;


public class ConfigPaneController implements Initializable {
	
	@FXML Tab ensembleTab, servicesTab, componentsTab, subchannelsTab, outputsTab;
	
	@FXML Button newButton, openButton, saveButton;
	@FXML Label cuLabel;
	@FXML ProgressBar cuProgressBar;
	
	private File projectFolder = null;

	@Override
	public void initialize(URL location, ResourceBundle resources){
		
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
	
	
	@FXML
	private void newConfig() {

		// Warning -> all configurations lost
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Remove the Configuration");
		alert.setHeaderText("The completely Configuration will be lost");
		
		ButtonType ok = new ButtonType("OK");
		alert.getButtonTypes().setAll(ok, new ButtonType("Cancel"));
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result != null) {
			if (result.get() == ok) {
				
				// all config. removes
				Multiplex mux = Multiplex.getInstance();	
				mux.newGeneral();
				mux.newEnsemble();
				mux.getComponentList().clear();
				mux.getServiceList().clear();
				mux.getSubchannelList().clear();
				mux.getOutputList().clear();
			} 
		}	
	}
	
	@FXML
	private void saveConfig() throws IOException {
		
		DirectoryChooser chooser = new DirectoryChooser(); 
		chooser.setTitle("Browse Folder");
		chooser.setInitialDirectory(new File("."));
		
		projectFolder = chooser.showDialog(null);
		
		if (projectFolder != null) new Save(projectFolder);		
	}
	
	@FXML
	private void openConfig() throws IOException {

		// Warning delete Configuration
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Remove the Configuration");
		alert.setHeaderText("The completely Configuration will be lost");

		ButtonType ok = new ButtonType("OK");
		alert.getButtonTypes().setAll(ok, new ButtonType("Cancel"));
		Optional<ButtonType> result = alert.showAndWait();

		if (result != null) {
			if (result.get() == ok) {

				// Clear all Lists
				Multiplex mux = Multiplex.getInstance();
				mux.getComponentList().clear();
				mux.getServiceList().clear();
				mux.getSubchannelList().clear();
				mux.getOutputList().clear();

				// Choose Folder
				DirectoryChooser chooser = new DirectoryChooser(); 
				chooser.setTitle("Browse Folder");
				chooser.setInitialDirectory(new File("."));	
				projectFolder = chooser.showDialog(null);

				if (projectFolder != null) {
					boolean validBashFileExist = false;

					// Search valid Bash File
					for (File bashFile: projectFolder.listFiles()) {	
						if (bashFile.getName().length() > 3 && bashFile.getName().indexOf(".sh") == (bashFile.getName().length()-3)) {
							
							// Bash File is valid
							validBashFileExist = true;
							new Load(projectFolder, bashFile);
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
		}
	}
}
