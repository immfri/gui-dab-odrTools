package configUI.services;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import addons.*;
import model.*;


public class ServiceTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML Button changePanesButton;
	@FXML GridPane servicePane, advancedServicePane;

	@FXML Label name, label, shortLabel, idLabel, languageLabel, ptyLabel;
	@FXML TextField nameTextField, labelTextField, shortLabelTextField, idTextField, ptyTextField, languageTextField;
	
	private boolean advancedView;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		advancedView = false;
		vBox.getChildren().remove(advancedServicePane);
	}
	
	
	@FXML
	private void changePanes() {
		advancedView = !advancedView;
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");
			vBox.getChildren().add(1, advancedServicePane);	
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().remove(advancedServicePane);
		}
	}


	public void setService(Service service) {
		
		// TitledPane Text
		titledPane.textProperty().bind(Bindings.concat("Service: ", service.getName()));
		
		// Name
		new NameValidation(nameTextField, service.getName(), Multiplex.getInstance().getServiceList());

		// Label
		new LabelValidation(labelTextField, service.getLabel(), Multiplex.getInstance().getServiceList());

		// Short-Label
		new ShortLabelValidation(shortLabelTextField, service.getLabel(), service.getShortLabel(), Multiplex.getInstance().getServiceList());

		// Programme Type
		new NumberValidation(ptyTextField, service.getPty(), 0, 31, 1, null);

		// Language
		new IdValidation(languageTextField, service.getLanguage(), 2, null);		

		// ID
		new IdValidation(idTextField, service.getId(), 4, Multiplex.getInstance().getServiceList());
	}
}
