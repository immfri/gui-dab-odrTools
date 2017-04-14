package configUI.outputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.output.*;
import addons.*;


public class UhdVBoxController implements Initializable {

	@FXML VBox vBox;
	@FXML Button changePanesButton;
	
	@FXML Label typeLabel, subdeviceLabel, clockRateLabel, equaliserLabel, txGainLabel;
	@FXML Label frequenzLabel, channelLabel, refClockLabel, behRefClockLabel, ppsLabel, holdoverTimeLabel;
	@FXML ChoiceBox<String> typeChoiceBox, channelChoiceBox, refClockChoiceBox, behRefClockChoiceBox, ppsChoiceBox;
	@FXML TextField subdeviceTextField, clockrateTextField, frequenzTextField, holdoverTimeTextField;
	@FXML CheckBox equaliserCheckBox;
	@FXML Slider txGainSlider;
	@FXML GridPane typePane, subdevicePane, clockRatePane, equaliserPane, channelPane, configPane, behavPane, ppsPane, gpsPane;
	
	// Delaymanagement
	@FXML Label synchLabel, muteLabel, offsetLabel;
	@FXML CheckBox synchCheckBox, muteCheckBox;
	@FXML TextField offsetTextField;
	@FXML GridPane delayPane;
	
	private ETIZeromq zmq;
	private UHD uhd;
	private FXMLLoader fxmlLoader;
	private VBox modulatorVBox;
	private boolean advancedView;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
			
		uhd = new B100();
		
		// Type
		typeChoiceBox.setItems(uhd.getSupportedDeviceList());
		
		// Cic Equaliser
		equaliserCheckBox.selectedProperty().addListener(c -> changeEqualiser());
		
		// Channel
		channelChoiceBox.setItems(getDabChannelList());
		channelChoiceBox.valueProperty().addListener(c -> changeChannel());
		
		// Ref. Clock Source
		refClockChoiceBox.valueProperty().addListener(c -> setBehavPane());
		
		// PPS Source
		ppsChoiceBox.valueProperty().addListener(c -> setGpsPane());
		
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/configUI/outputs/ModulatorVBox.fxml"));
			modulatorVBox = fxmlLoader.load();
			fxmlLoader.<ModulatorVBoxController>getController().setModulator(uhd);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		vBox.getChildren().clear();
		vBox.getChildren().addAll(typePane, channelPane, changePanesButton);
	}


	public void setZmq(ETIZeromq zmq) {
		this.zmq = zmq;
		this.uhd = (UHD)zmq.getMod();
		
		// Type
		switch (uhd.getType().getValue()) {
		case "b100":
			typeChoiceBox.getSelectionModel().select(0);
			break;
			
		case "b200":
			typeChoiceBox.getSelectionModel().select(1);
			break;
			
		case "usrp1":
			typeChoiceBox.getSelectionModel().select(2);
			subdeviceTextField.textProperty().bindBidirectional(((USRP1)uhd).getSubdevice());
			break;
			
		case "usrp2":
			typeChoiceBox.getSelectionModel().select(3);
			break;
		}
		
		// Type Listener
		typeChoiceBox.valueProperty().addListener(c -> changeType());
		
		// Master Clockrate
		new NumberValidation(clockrateTextField, uhd.getMaster_clock_rate(), 1000000, 100000000, 1, null);
		
		// set Source from zmq to Modulator-Input
		String source = "tcp://localhost:" + this.zmq.getDestination().getValue();
		uhd.getSource().setValue(source);
		
		setValues();
	}
	
	
	private void changeType() {
		
		// Unbind
		subdeviceTextField.textProperty().unbind();
		clockrateTextField.textProperty().unbind();
		txGainSlider.valueProperty().unbind();
		frequenzTextField.textProperty().unbind();
		frequenzTextField.textProperty().removeListener(c -> changeFrequenz());
		refClockChoiceBox.valueProperty().unbind();
		behRefClockChoiceBox.valueProperty().unbind();
		ppsChoiceBox.valueProperty().unbind();
		holdoverTimeTextField.textProperty().unbind();
		synchCheckBox.selectedProperty().unbind();
		muteCheckBox.selectedProperty().unbind();
		offsetTextField.textProperty().unbind();
		
		
		if (typeChoiceBox.getValue().contentEquals(uhd.getSupportedDeviceList().get(0))) {		// B100 Serie
			uhd = new B100();
			new NumberValidation(clockrateTextField, ((B100)uhd).getMaster_clock_rate(), 1000000, 100000000, 1, null);
		}
		else if (typeChoiceBox.getValue().contentEquals(uhd.getSupportedDeviceList().get(1))) {	// B200 Serie
			uhd = new B200();
			new NumberValidation(clockrateTextField, ((B200)uhd).getMaster_clock_rate(), 1000000, 100000000, 1, null);
		} 
		else if (typeChoiceBox.getValue().contentEquals(uhd.getSupportedDeviceList().get(3))) {	// USRP2-devices
			uhd = new USRP2();		
		} 
		else {																					// USRP1-devices
			uhd = new USRP1();
			
			// Subdevices
			subdeviceTextField.textProperty().bindBidirectional(((USRP1)uhd).getSubdevice());
		}	
		// set up Modulator/UHD
		zmq.setMod(uhd);
		
		// set Source from zmq to Modulator-Input
		String source = "tcp://localhost:" + zmq.getDestination().getValue();
		uhd.getSource().setValue(source);
		
		// Master Clock Rate
		clockrateTextField.setDisable(false);
				
		setValues();
		fxmlLoader.<ModulatorVBoxController>getController().setModulator(uhd);
	}
	
	
	@FXML
	private void changePanes() {	
		
		advancedView = !advancedView;
		vBox.getChildren().clear();
		
		if (advancedView) {
			changePanesButton.setText("Show basic parameters");
			vBox.getChildren().addAll(typePane, channelPane, configPane, ppsPane, delayPane, modulatorVBox, changePanesButton);
			
			setSubdevicePane();
			setClockRatePane();
			setEqualiserPane();
			setBehavPane();
			setGpsPane();
		} 
		else {
			changePanesButton.setText("Show advanced parameters");
			vBox.getChildren().addAll(typePane, channelPane, changePanesButton);
		}		
	}
	
	private void setValues() {
		
		// set Items
		txGainSlider.setMax(uhd.getMaxTxGain());
		refClockChoiceBox.setItems(uhd.getRefclk_sourceList());
		behRefClockChoiceBox.setItems(uhd.getBehaviour_refclk_lock_lostList());
		ppsChoiceBox.setItems(uhd.getPps_sourceList());
		
		// Binding
		txGainSlider.valueProperty().bindBidirectional(uhd.getTxgain());
//		new NumberValidation(frequenzTextField, uhd.getFrequency(), 1, 6000000, 1, null);
		frequenzTextField.textProperty().unbindBidirectional(uhd.getFrequency());
		frequenzTextField.textProperty().bindBidirectional(uhd.getFrequency());
		frequenzTextField.textProperty().addListener(c -> changeFrequenz());
		changeFrequenz();
		refClockChoiceBox.valueProperty().bindBidirectional(uhd.getRefclk_source());
		behRefClockChoiceBox.valueProperty().bindBidirectional(uhd.getBehaviour_refclk_lock_lost());
		ppsChoiceBox.valueProperty().bindBidirectional(uhd.getPps_source());
		new NumberValidation(holdoverTimeTextField, uhd.getMax_gps_holdover_time(), 0, 10000, 1, null);
		
		synchCheckBox.selectedProperty().bindBidirectional(uhd.getSynchronous());
		muteCheckBox.selectedProperty().bindBidirectional(uhd.getMutenotimestamps());
		new OffsetValidation(offsetTextField, uhd.getOffset(), 0, 1);
		
		setSubdevicePane();
		if (advancedView) {
			setClockRatePane();
			setEqualiserPane();
			setBehavPane();
			setGpsPane();
		}
	}


	private void setSubdevicePane() {
		vBox.getChildren().remove(subdevicePane);	
		
		if (uhd.getType().getValue().contentEquals("usrp1")) {
			vBox.getChildren().add(1, subdevicePane);
		}
	}
	
	private void setClockRatePane() {
		vBox.getChildren().remove(clockRatePane);
		
		if (!uhd.getType().getValue().contains("usrp")) {					// Master Clock Rate not for USRP1/2
			vBox.getChildren().add(vBox.getChildren().indexOf(delayPane), clockRatePane);
		} 
	}
	
	
	private void setEqualiserPane() {
		vBox.getChildren().remove(equaliserPane);
		
		if (uhd.getType().getValue().contains("usrp")) {
			vBox.getChildren().add(vBox.getChildren().indexOf(channelPane), equaliserPane);
		}
	}
	
	private void setBehavPane() {
		vBox.getChildren().remove(behavPane);
		
		if (refClockChoiceBox.getValue() != null) {
			if (refClockChoiceBox.getValue().contentEquals("external")) {	
				vBox.getChildren().add(vBox.getChildren().indexOf(ppsPane), behavPane);
			}
		}
		setGpsPane();
	}

	private void setGpsPane() {
		vBox.getChildren().remove(gpsPane);
		
		if (refClockChoiceBox.getValue() != null && ppsChoiceBox.getValue() != null) {
			if (refClockChoiceBox.getValue().contains("gps") && ppsChoiceBox.getValue().contains("gps")) {	
				vBox.getChildren().add(vBox.getChildren().indexOf(delayPane), gpsPane);
			}
		}	
	}
	
	private void changeEqualiser() {
		boolean selected = equaliserCheckBox.isSelected();

		if (uhd.getType().getValue().contentEquals("usrp1")) {		// init. Equaliser for USRP1
			
			if (selected) {
				uhd.getDac_clk_rate().set(128000000);
			} 
			else {
				uhd.getDac_clk_rate().set(0);
			}
			
			uhd.getRate().setValue("3200000");
			clockrateTextField.setDisable(selected);
		} 
		else if (uhd.getType().getValue().contentEquals("usrp2")) {	// init. Equaliser for USRP2
			
			if (selected) {
				uhd.getDac_clk_rate().set(400000000);
			} 
			else {
				uhd.getDac_clk_rate().set(0);
			}
		}
	}
	
	private void changeFrequenz() {
		long[] freqArray = getDabFrequenz();
		int index;
		
		// Search Freq. in Array
		for (index=0; index<61; index++) {
			if (Long.parseLong(frequenzTextField.getText()) == freqArray[index]) break;
		}
		
		channelChoiceBox.getSelectionModel().select(index);;
	}

	private void changeChannel() {
		int index = channelChoiceBox.getSelectionModel().getSelectedIndex();
		
		if (index < 61) {
			frequenzTextField.setText("" + getDabFrequenz()[index]);
		}
	}

	private ObservableList<String> getDabChannelList() {
		String[] channels = new String[62];
		int x = 0;
	
		for (int i=5; i<14; i++) {
			for (String s: new String[]{"A","B","C","D"}) {	// 5A - 13D
				channels[x++] = i+s;
			}
		}
		for (String s: new String[]{"13E","13F"}) {			// 13E - 13F
			channels[x++] = s;
		}
		for (int i=65; i<88; i++) {							// LA - LW
			channels[x++] = "L"+(char)i;
		}
		channels[x++] = "none";
		return FXCollections.observableArrayList(channels);
	}
	
	private long[] getDabFrequenz() {
		long[] freqArray = new long[61];
		long freq;
		int x = 0;
		
		// Generate Frequenz-Array
		for (freq = 174928000; freq < 216930000; freq += 14000000) {		// 5A, 7A, 9A, 11A
			freqArray[x] = freq;
			x += 8;
		}
		x = 4;
		for (freq = 181936000; freq < 223940000; freq += 14000000) {		// 6A, 8A, 10A, 12A
			freqArray[x] = freq;
			x += 8;
		}
		for (x=1; x<32; x+=4) {									// x = 5...12
			freq = freqArray[x-1];
			for (int y=0; y<3; y++) {							// xB, xC, xD
				freq += 1712000;
				freqArray[x+y] = freq;
			}
		}
		x--;
		freqArray[x++] = 230748000;								// 13A
		freqArray[x++] = 232496000;
		freqArray[x++] = 234208000;
		freqArray[x++] = 235776000;
		freqArray[x++] = 237448000;
		freqArray[x++] = 239200000;								// 13F
		
		for (freq = 1452960000; freq <= 1490624000; freq += 1712000) {	// LA - LW
			freqArray[x++] = freq;
		}
		return freqArray;
	}
	
}
