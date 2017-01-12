package configUI.subchannels;

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


public class SubchannelsVBoxController implements Initializable {

	@FXML Button addButton, remButton;
	@FXML VBox vBox;
	@FXML ScrollPane scrollPane;
	@FXML Accordion accordion;

	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		vBox.heightProperty().addListener((o, old, newHeight) -> 
			scrollPane.setPrefHeight((double)newHeight - 66));

		Multiplex.getInstance().getSubchannelList().addListener(new ListChangeListener<Subchannel>() {
		@Override
		public void onChanged(Change<? extends Subchannel> c) {
			while (c.next()) {
				
				if (c.wasAdded()) {
					for (Subchannel subch: c.getAddedSubList()) {
						
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/configUI/subchannels/SubchannelTitledPane.fxml"));
						
						TitledPane titledPane = null;
						try {	
							titledPane = loader.load();
							loader.<SubchannelTitledPaneController>getController().setSubchannel(subch);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
						accordion.getPanes().add(titledPane);
						accordion.setExpandedPane(titledPane);
					}
				} 
				else if (c.wasRemoved()) {
					for (Subchannel subch: c.getRemoved()) {
						
						int index = -1;
						for (TitledPane pane: accordion.getPanes()) {
							if (pane.getText().contains(subch.getName().getValue())) {
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
	private void addSubchannel() throws IOException {	
		if (Multiplex.getInstance().getTotalCU().get() < 864) {
			int size = Multiplex.getInstance().getSubchannelList().size();
			Multiplex.getInstance().getSubchannelList().add(new Subchannel("NEW_Subchannel_" + size, new MP2()));
		}
	}
	
	
	@FXML
	private void removeSubchannel() {	
		TitledPane expandedTitledPane = accordion.getExpandedPane();
		
		if (expandedTitledPane != null) {
			int index = accordion.getPanes().indexOf(expandedTitledPane);		
			Multiplex.getInstance().getSubchannelList().remove(index);
		}	
	}	
}
