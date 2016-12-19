package configUI;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import model.Multiplex;
import addons.ExceptionAlert;


public class ConfigPaneController implements Initializable {
	
	@FXML Tab ensembleTab, servicesTab, componentsTab, subchannelsTab, outputsTab;
	
	@FXML Button newButton, openButton, saveButton;
	@FXML Label cuLabel;
	@FXML ProgressBar cuProgressBar;
	

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
			new ExceptionAlert(this.getClass().getName(), e);
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
		//TODO
	}
	
	@FXML
	private void saveConfig() {
		//TODO
	}
	
	@FXML
	private void openConfig() {
		//TODO
	}
	
	
}
