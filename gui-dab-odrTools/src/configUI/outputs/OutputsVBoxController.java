package configUI.outputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import addons.ExceptionAlert;
import model.Multiplex;


public class OutputsVBoxController implements Initializable {

	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	
	@FXML Button remButton;
	@FXML MenuButton addMenuButton;
	@FXML MenuItem deviceMenuItem, fileMenuItem, networkMenuItem;
	@FXML Accordion accordion;

	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> scrollPane.setPrefHeight((double)newHeight-66));
		
//		Multiplex.getInstance().getComponentList().addListener(new ListChangeListener<Component>() {
//		@Override
//		public void onChanged(Change<? extends Component> c) {
//			while (c.next()) {
//				if (c.wasAdded()) {
//					for (Component addComponent : c.getAddedSubList()) {
////						addTitledPane(addComponent);  TODO for Open-Multiplex then add all components 
//					}
//				} 
//				else if (c.wasRemoved()) {
//					for (Component remComponent: c.getRemoved()) {
////						remTitledPane(remComponent);	TODO for Open-Multiplex then remove all old components
//					}
//				}
//			}	
//		}
//	});
		
	}
	
	
	private void addOutput(TitledPane pane) {
		accordion.getPanes().add(pane);
		accordion.setExpandedPane(pane);
	}
	
	@FXML
	private void addDevice() {	
		try {
			addOutput((TitledPane)FXMLLoader.load(getClass().getResource("/configUI/outputs/DeviceTitledPane.fxml")));
		} catch (IOException e) {
			new ExceptionAlert("OutputsVBox.addDevice",e);
		}	
	}
	
	@FXML
	private void addFile() {	
		try {
			addOutput((TitledPane)FXMLLoader.load(getClass().getResource("/configUI/outputs/FileTitledPane.fxml")));
		} catch (IOException e) {
			new ExceptionAlert("OutputsVBox.addFile",e);
		}	
	}
	
	@FXML
	private void addNetwork() {	
		try {
			addOutput((TitledPane)FXMLLoader.load(getClass().getResource("/configUI/outputs/NetworkTitledPane.fxml")));
		} catch (IOException e) {
			new ExceptionAlert("OutputsVBox.addNetwork",e);
		}	
	}
	
	@FXML
	private void removeOutput() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {
			int index = accordion.getPanes().indexOf(expandedTitledPane);
			
			Multiplex.getInstance().getOutputList().remove(index);
			accordion.getPanes().remove(index);
		}	
	}

	
}
