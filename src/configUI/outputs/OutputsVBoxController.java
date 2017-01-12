package configUI.outputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
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
import model.*;
import model.output.*;


public class OutputsVBoxController implements Initializable {

	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	
	@FXML Button remButton;
	@FXML MenuButton addMenuButton;
	@FXML MenuItem deviceMenuItem, fileMenuItem, networkMenuItem;
	@FXML Accordion accordion;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> 
			scrollPane.setPrefHeight((double)newHeight - 66));

		Multiplex.getInstance().getOutputList().addListener(new ListChangeListener<Output>() {
			@Override
			public void onChanged(Change<? extends Output> c) {
				while (c.next()) {
					
					if (c.wasAdded() && !c.wasReplaced()) {
	
						for (Output out: c.getAddedSubList()) {
							String format = out.getFormat().getValue();
							String modOutput = "";
							
							// Modulator Output
							if (format.contains("zmq")) {
								
								if (((ETIZeromq)out).getMod() != null) {
									modOutput = ((ETIZeromq)out).getMod().getOutput().getValue();
								}
							}
							
							// Add Output
							FXMLLoader loader;
											
							if (format.contains("raw") || modOutput.contains("uhd")) {				// Device
								loader = new FXMLLoader(getClass().getResource("/configUI/outputs/DeviceTitledPane.fxml"));
							} 																		
							else if(format.contains("fifo") || modOutput.contains("file")) {		// File
								loader = new FXMLLoader(getClass().getResource("/configUI/outputs/FileTitledPane.fxml"));
							} 
							else {																	// Network
								loader = new FXMLLoader(getClass().getResource("/configUI/outputs/NetworkTitledPane.fxml"));
							}
							
							TitledPane titledPane = null;
							try {	
								titledPane = loader.load();	
								
								if (format.contains("raw") || modOutput.contains("uhd")) {			// Device
									loader.<DeviceTitledPaneController>getController().createOutput(out);
								} 																		
								else if(format.contains("fifo") || modOutput.contains("file")) {	// File
									loader.<FileTitledPaneController>getController().createOutput(out);
								} 
								else {																// Network
									loader.<NetworkTitledPaneController>getController().createOutput(out);
								}	
							} catch (IOException e) {
								e.printStackTrace();
							}		
									
							accordion.getPanes().add(titledPane);
							accordion.setExpandedPane(titledPane);
						}
					}	
					else if (c.wasRemoved() && !c.wasReplaced()) {
						for (Output out: c.getRemoved()) {
							
							int index = Multiplex.getInstance().getOutputList().indexOf(out);
							for (TitledPane pane: accordion.getPanes()) {
								if (pane.getText().contains(out.getName().getValue())) {
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
	private void addDevice() throws IOException {	
		int size = Multiplex.getInstance().getOutputList().size();
		Multiplex.getInstance().getOutputList().add(new ETIDevice("NEW_Output_"+size));
	}
	
	@FXML
	private void addFile() throws IOException {	
		int size = Multiplex.getInstance().getOutputList().size();
		Multiplex.getInstance().getOutputList().add(new ETIFile("NEW_Output_"+size));
	}
	
	@FXML
	private void addNetwork() throws IOException {	
		int size = Multiplex.getInstance().getOutputList().size();
		Multiplex.getInstance().getOutputList().add(new EDI("NEW_Output_"+size));
	}
	
	@FXML
	private void removeOutput() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {
			int index = accordion.getPanes().indexOf(expandedTitledPane);	
			Multiplex.getInstance().getOutputList().remove(index);
		}	
	}	
}
