package configUI.components;

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


public class ComponentsVBoxController implements Initializable {
	
	@FXML Button addButton, remButton;
	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	@FXML Accordion accordion;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> 
			scrollPane.setPrefHeight((double)newHeight - 66));
		
		Multiplex.getInstance().getComponentList().addListener(new ListChangeListener<Component>() {
			@Override
			public void onChanged(Change<? extends Component> c) {
				while (c.next()) {
					
					if (c.wasAdded()) {
						for (Component comp: c.getAddedSubList()) {
							
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/configUI/components/ComponentTitledPane.fxml"));
							
							TitledPane titledPane = null;
							try {	
								titledPane = loader.load();
								loader.<ComponentTitledPaneController>getController().setComponent(comp);
								
							} catch (IOException e) {
								e.printStackTrace();
							}
							accordion.getPanes().add(titledPane);
							accordion.setExpandedPane(titledPane);
						}
					} 
					else if (c.wasRemoved()) {
						for (Component comp: c.getRemoved()) {
							
							int index = -1;
							for (TitledPane pane: accordion.getPanes()) {
								if (pane.getText().contains(comp.getName().getValue())) {
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
	private void addComponent() {	
		int size = Multiplex.getInstance().getComponentList().size();
		Multiplex.getInstance().getComponentList().add(new Component("NEW_Component_" +size));
	}
	
	
	@FXML
	private void removeComponent() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {
			int index = accordion.getPanes().indexOf(expandedTitledPane);
			
			Multiplex.getInstance().getComponentList().remove(index);
		}	
	}
}
