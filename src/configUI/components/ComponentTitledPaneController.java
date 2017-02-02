package configUI.components;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import addons.*;
import model.*;


public class ComponentTitledPaneController implements Initializable {
	
	@FXML TitledPane titledPane;
	@FXML VBox vBox;
	@FXML Button changePanesButton;
	@FXML GridPane componentPane, advancedComponentPane, advancedPacketPane;
	
	@FXML Label name, serviceLabel, subchannelLabel, figtypeLabel, typeLabel, idLabel, addressLabel, datagroupLabel;
	@FXML TextField nameTextField, figtypeTextField, typeTextField, addressTextField, idTextField;
	@FXML ChoiceBox<Service> serviceChoiceBox;
	@FXML ChoiceBox<Subchannel> subchannelChoiceBox;
	@FXML CheckBox datagroupCheckBox;
	
	private Component component;
	private boolean advancedView;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
			
		// Service init.
		serviceChoiceBox.setItems(Multiplex.getInstance().getServiceList());
		updateServiceNames(null);

		// Subchannel init.
		subchannelChoiceBox.setItems(Multiplex.getInstance().getSubchannelList());	
		updateSubchannelNames(null);
				
		
		advancedView = false;
		vBox.getChildren().clear();
		vBox.getChildren().addAll(componentPane, changePanesButton);
	}
	
	
	public void setComponent(Component comp) {
		component = comp;
			
		// TitledPane Text
		titledPane.textProperty().bind(Bindings.concat("Component: ",comp.getName()));
			
		// Name
		new NameValidation(nameTextField, comp.getName(), Multiplex.getInstance().getComponentList());
		
		// Fig-Type
		new IdValidation(figtypeTextField, comp.getFigtype(), 3, FXCollections.observableArrayList());
		
		// Type
		new NumberValidation(typeTextField, comp.getType(), 0, 99, 1, null);
		
		// ID
		new IdValidation(idTextField, comp.getId(), 3, Multiplex.getInstance().getComponentList());	
		
		// Address
		new NumberValidation(addressTextField, comp.getAddress(), 0, 1023, 1, null);
			
		// Datagroup
		datagroupCheckBox.selectedProperty().bindBidirectional(comp.getDatagroup());
		
		// Service
		serviceChoiceBox.setValue(comp.getService());
		serviceChoiceBox.valueProperty().addListener(c -> 
			component.setService(serviceChoiceBox.getValue()));
		
		// Subchannel
		subchannelChoiceBox.setValue(comp.getSubchannel());
		subchannelChoiceBox.valueProperty().addListener(c -> {
			component.setSubchannel(subchannelChoiceBox.getValue());
			changeSubchannel();
		});
		
	}	
	
	
	private void changeSubchannel() {
	
		if (component.getSubchannel() != null) {
			component.getDatagroup().setValue(false);
			component.getType().setValue("0");
			
			String inputType = component.getSubchannel().getInput().getType().getValue();
			
			if (inputType.contains("data") || inputType.contains("packet")) {	// Data
				component.getAddress().setValue("0");
			}
			else {																// Audio
				component.getAddress().setValue("");
			}
			
			setPacketPane();
		}
	}
	
	
	private void setPacketPane() {
		vBox.getChildren().remove(advancedPacketPane);
		
		if (component.getSubchannel() != null) {
			String inputType = component.getSubchannel().getInput().getType().getValue();
			
			if ((inputType.contains("data") || inputType.contains("packet")) && advancedView) {	
				vBox.getChildren().add(1, advancedPacketPane);
			} 
		}
	}


	@FXML
	private void changePanes() {
		advancedView = !advancedView;
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");
			vBox.getChildren().add(1, advancedComponentPane);
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().remove(advancedComponentPane);
		}
		setPacketPane();	
	}
	
	
	private void updateSubchannelNames(Subchannel s) {
		
		if (s == null) subchannelChoiceBox.setValue(null);	
		else if (subchannelChoiceBox.getValue() != null) {
			if (subchannelChoiceBox.getValue().equals(s)) subchannelChoiceBox.setValue(null);	
		}
		
		subchannelChoiceBox.setConverter(new StringConverter<Subchannel>() {
			
			@Override
			public String toString(Subchannel s) {
				s.getName().removeListener(c -> updateSubchannelNames(s));
				s.getName().addListener(c -> updateSubchannelNames(s));	
				return s.getName().get();
			}

			@Override
			public Subchannel fromString(String string) {
				return null;
			}
		});
	}

	private void updateServiceNames(Service s) {
		
		if (s == null) serviceChoiceBox.setValue(null);
		else if (serviceChoiceBox.getValue() != null) {
			 if (serviceChoiceBox.getValue().equals(s)) serviceChoiceBox.setValue(null);
		}
		
		serviceChoiceBox.setConverter(new StringConverter<Service>() {	
			
			@Override
			public String toString(Service s) {
				s.getName().removeListener(c -> updateServiceNames(s));
				s.getName().addListener(c -> updateServiceNames(s));	
				return s.getName().get();
			}

			@Override
			public Service fromString(String string) {
				return null;
			}
		});
	}
}
