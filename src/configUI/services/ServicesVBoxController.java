package configUI.services;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import model.*;


public class ServicesVBoxController implements Initializable {
	
	@FXML Button addButton, remButton;
	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	@FXML Accordion accordion;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> 
			scrollPane.setPrefHeight((double)newHeight - 66));
		
		Multiplex.getInstance().getServiceList().addListener(new ListChangeListener<Service>() {
			@Override
			public void onChanged(Change<? extends Service> c) {
				while (c.next()) {
					
					if (c.wasAdded()) {
						for (Service service : c.getAddedSubList()) {
							
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/configUI/services/ServiceTitledPane.fxml"));
							
							TitledPane titledPane = null;
							try {	
								titledPane = loader.load();
								loader.<ServiceTitledPaneController>getController().setService(service);
								
							} catch (IOException e) {
								e.printStackTrace();
							}
							accordion.getPanes().add(titledPane);
							accordion.setExpandedPane(titledPane);
						}
					} 
					else if (c.wasRemoved()) {
						for (Service service: c.getRemoved()) {
							
							int index = -1;
							for (TitledPane pane: accordion.getPanes()) {
								if (pane.getText().contains(service.getName().getValue())) {
									index = accordion.getPanes().indexOf(pane);
									break;
								}
							}
							if (index != -1) accordion.getPanes().remove(index);
						}
					}
				}	
			}
		});	
	}
	
	
	
	@FXML
	private void addNewService() {	
		int size = Multiplex.getInstance().getServiceList().size();
		Multiplex.getInstance().getServiceList().add(new Service("NEW_Service_" + size));
	}
	
	
	@FXML
	private void removeService() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {			
			int index = accordion.getPanes().indexOf(expandedTitledPane);		
			Multiplex.getInstance().getServiceList().remove(index);
		}	
	}
}
