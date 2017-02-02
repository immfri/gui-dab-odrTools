package configUI.ensemble;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import addons.*;
import model.*;


public class EnsemblePaneController implements Initializable {
	
	@FXML VBox muxVBox;
	@FXML GridPane generalPane, advancedGeneralPane, ensemblePane, advancedEnsemblePane;
	@FXML Button changePanesButton;
	
	// General
	@FXML Label generalLabel, advancedGeneralLabel, nonstopLabel, dabModeLabel, dabModeNumberLabel, dabModeAdvancedLabel;
	@FXML Label framesLabel, telnetLabel, managementLabel, syslogLabel, timestampLabel, sccaLabel;
	@FXML ChoiceBox<String> dabModeChoiceBox;
	@FXML TextField framesTextField, telnetTextField, managementTextField;
	@FXML CheckBox syslogCheckBox, timestampCheckBox, sccaCheckBox;
	
	// Ensemble
	@FXML Label label, shortLabel, idLabel, eccLabel, intTableLabel, offsetLabel;
	@FXML TextField labelTextField, shortLabelTextField, idTextField, eccTextField;
	@FXML ChoiceBox<String> intTableChoiceBox;
	@FXML Spinner<String> offsetSpinner;
		
	private Multiplex mux;
	private Ensemble ensemble;	
	private boolean advancedView;
	
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//--------------- Multiplex ------------------
		mux = Multiplex.getInstance();
		
		// DAB-Mode
		dabModeChoiceBox.setItems(mux.getDabModeList());
		dabModeChoiceBox.valueProperty().bindBidirectional(mux.getDabMode());
		dabModeNumberLabel.textProperty().bind(mux.getDabMode());
				
		// ETI-Frames
		new NumberValidation(framesTextField, mux.getNbframes(), 0, Integer.MAX_VALUE, 1, nonstopLabel);
		
		// Telnet-Port
		new PortValidation(telnetTextField, mux.getTelnetPort(), 0);
		
		// Management-Port
		new PortValidation(managementTextField, mux.getManagementport(), 0);
		
		// Syslog
		syslogCheckBox.selectedProperty().bindBidirectional(mux.getSyslog());
		
		// Timestamp
		timestampCheckBox.selectedProperty().bindBidirectional(mux.getTist());
		
		// SCCA
		sccaCheckBox.selectedProperty().bindBidirectional(mux.getWritescca());
		
		
		//--------------- Ensemble ------------------
		ensemble = mux.getEnsemble();
			
		
		// Label
		new LabelValidation(labelTextField, ensemble.getLabel(), null);
			
		// Short-Label
		new ShortLabelValidation(shortLabelTextField, ensemble.getLabel(), ensemble.getShortLabel(), null);
								
		// ID
		new IdValidation(idTextField, ensemble.getId(), 4, null);
		
		// ECC
		new IdValidation(eccTextField, ensemble.getEcc(), 2, null);
		
		// International Table
		intTableChoiceBox.setItems(FXCollections.observableArrayList("1","2"));
		intTableChoiceBox.valueProperty().bindBidirectional(ensemble.getIntTable());
		
		// Local time offset
		offsetSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<String>(ensemble.getLocalTimeOffsetList()));
		offsetSpinner.getValueFactory().valueProperty().bindBidirectional(ensemble.getLocalTimeOffset());
		
		
		advancedView = false;
		muxVBox.getChildren().removeAll(advancedGeneralPane, advancedEnsemblePane);
	}


	@FXML
	private void changePanes() {
		advancedView = !advancedView;
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");
			
			muxVBox.getChildren().remove(generalPane);
			muxVBox.getChildren().add(0, advancedGeneralPane);
			muxVBox.getChildren().add(2, advancedEnsemblePane);
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			
			muxVBox.getChildren().removeAll(advancedGeneralPane, advancedEnsemblePane);
			muxVBox.getChildren().add(0, generalPane);
		}	
	}
	
}