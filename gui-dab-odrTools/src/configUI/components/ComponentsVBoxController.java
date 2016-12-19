package configUI.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import addons.ExceptionAlert;
import model.*;


public class ComponentsVBoxController implements Initializable {
	
	@FXML Button addButton, remButton;
	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	@FXML Accordion accordion;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> scrollPane.setPrefHeight((double)newHeight-66));
		
//		Multiplex.getInstance().getComponentList().addListener(new ListChangeListener<Component>() {
//			@Override
//			public void onChanged(Change<? extends Component> c) {
//				while (c.next()) {
//					if (c.wasAdded()) {
//						for (Component addComponent : c.getAddedSubList()) {
////							addTitledPane(addComponent);  TODO for Open-Multiplex then add all components 
//						}
//					} 
//					else if (c.wasRemoved()) {
//						for (Component remComponent: c.getRemoved()) {
////							remTitledPane(remComponent);	TODO for Open-Multiplex then remove all old components
//						}
//					}
//				}	
//			}
//		});	
	}
	
	
	@FXML
	private void addComponent() {	
		TitledPane titledPane = null;
		try {
			titledPane = (TitledPane)FXMLLoader.load(getClass().getResource("/configUI/components/ComponentTitledPane.fxml"));
		} catch (IOException e) {
			new ExceptionAlert("ComponentsVBox",e);
		}
		accordion.getPanes().add(titledPane);
		accordion.setExpandedPane(titledPane);
	}
	
	
	@FXML
	private void removeComponent() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {
			int index = accordion.getPanes().indexOf(expandedTitledPane);
			
			Multiplex.getInstance().getComponentList().remove(index);
			accordion.getPanes().remove(index);
		}	
	}

	

}
