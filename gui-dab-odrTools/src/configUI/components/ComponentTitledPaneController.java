package configUI.components;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
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
	
	@FXML Label name, serviceLabel, subchannelLabel, typeLabel, idLabel, addressLabel, datagroupLabel;
	@FXML TextField nameTextField, idTextField;
	@FXML Spinner<Integer> addressSpinner;
	@FXML ChoiceBox<String> typeChoiceBox;
	@FXML ChoiceBox<Service> serviceChoiceBox;
	@FXML ChoiceBox<Subchannel> subchannelChoiceBox;
	@FXML CheckBox datagroupCheckBox;
	
	private Component component;
	private boolean advancedView;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// create new Component
		String name = "NEW Component "+Multiplex.getInstance().getComponentList().size();
		component = new Component(name);
		Multiplex.getInstance().getComponentList().add(component);
			
		// TitledPane Text
		titledPane.textProperty().bind(Bindings.concat("Component: ",component.getName()));
			
		// Name
		new NameValidation(nameTextField, component.getName(), Multiplex.getInstance().getComponentList());
		
		
		// Service
		serviceChoiceBox.setItems(Multiplex.getInstance().getServiceList());
		serviceChoiceBox.valueProperty().addListener(c -> 
			component.setService(serviceChoiceBox.getValue()));
		updateServiceNames();
		
		
		// Subchannel
		subchannelChoiceBox.setItems(Multiplex.getInstance().getSubchannelList());
		subchannelChoiceBox.valueProperty().addListener(c -> {
			component.setSubchannel(subchannelChoiceBox.getValue());
			
			typeChoiceBox.setItems(component.getTypeList());
			typeChoiceBox.getSelectionModel().select(0);
		});
		updateSubchannelNames();
		
		
		// Type
		typeChoiceBox.setItems(component.getTypeList());
		typeChoiceBox.valueProperty().bindBidirectional(component.getType());
		typeChoiceBox.valueProperty().addListener((o, old, newType) -> changeType());
		
		
		// ID
		new IdValidation(idTextField, component.getId(), 3, Multiplex.getInstance().getComponentList());
		
		
		// Address
		new NumberValidation(addressSpinner, component.getAddress(), 0, 1023, 1);
		
		
		// Datagroup
		datagroupCheckBox.selectedProperty().bindBidirectional(component.getDatagroup());
		
		
		advancedView = false;
		vBox.getChildren().clear();
		vBox.getChildren().addAll(componentPane, changePanesButton);
	}
	
	
	
	private void changeType() {
		
		if (component.getSubchannel() != null) {
			setPacketPane();		
		}
	}
	
	private void setPacketPane() {
		vBox.getChildren().remove(advancedPacketPane);
		
		if (component.getSubchannel() != null) {
			String inputType = component.getSubchannel().getInput().getType().getValue();
			
			if ((inputType.contains("data") || inputType.contains("packet")) && advancedView) {			
				vBox.getChildren().add(vBox.getChildren().indexOf(changePanesButton), advancedPacketPane);
			}
		}
	}

	@FXML
	private void updateSubchannelNames() {
		
		subchannelChoiceBox.setValue(null);
		
		subchannelChoiceBox.setConverter(new StringConverter<Subchannel>() {
			
			@Override
			public String toString(Subchannel s) {
				s.getName().removeListener(c -> updateSubchannelNames());
				s.getName().addListener(c -> updateSubchannelNames());	
				return s.getName().get();
			}

			@Override
			public Subchannel fromString(String string) {
				return null;
			}
		});
	}

	
	private void updateServiceNames() {
		
		serviceChoiceBox.setValue(null);		// if the selected service is change name, then deselect service
		
		serviceChoiceBox.setConverter(new StringConverter<Service>() {	
			
			@Override
			public String toString(Service s) {
				s.getName().removeListener(c -> updateServiceNames());
				s.getName().addListener(c -> updateServiceNames());	
				return s.getName().get();
			}

			@Override
			public Service fromString(String string) {
				return null;
			}
		});
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
	
}
