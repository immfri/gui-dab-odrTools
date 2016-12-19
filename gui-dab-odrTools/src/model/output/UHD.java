package model.output;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public abstract class UHD extends Modulator {
	
	protected StringProperty device, type, refclk_source, pps_source, behaviour_refclk_lock_lost;
	protected IntegerProperty frequency, max_gps_holdover_time;
	protected DoubleProperty txgain; 
	protected Double maxTxGain;
	protected ObservableList<String> supportedDeviceList, refclk_sourceList, pps_sourceList, behaviour_refclk_lock_lostList;
	
	// SFN
	protected BooleanProperty synchronous, mutenotimestamps;
	protected StringProperty offset;
	
	public UHD(String type, double txGain, double max) {
		super("uhd");
		
		this.type = 			new SimpleStringProperty(type);
		txgain =				new SimpleDoubleProperty(txGain);
		maxTxGain = 			max;
		
		device = 				new SimpleStringProperty("");
		frequency =				new SimpleIntegerProperty(223936);			// Channel: 12A
		
		supportedDeviceList = 	FXCollections.observableArrayList("B100 Series","B200 Series","USRP1 Devices", "USRP2 Devices");
		refclk_sourceList = 	FXCollections.observableArrayList("internal","external","MIMO","gpsdo","gpsdo-ettus");
		pps_sourceList = 		FXCollections.observableArrayList("none","external","MIMO","gpsdo");
		
		refclk_source =			new SimpleStringProperty(refclk_sourceList.get(0));
		pps_source = 			new SimpleStringProperty(pps_sourceList.get(0));
		max_gps_holdover_time = new SimpleIntegerProperty(0);
		
		behaviour_refclk_lock_lostList = FXCollections.observableArrayList("ignore", "crash");
		behaviour_refclk_lock_lost = new SimpleStringProperty(behaviour_refclk_lock_lostList.get(0));
		
	
		// Delaymanagement
		synchronous = 		new SimpleBooleanProperty(false);
		mutenotimestamps =  new SimpleBooleanProperty(false);
		offset =			new SimpleStringProperty("0.0");
	}

	public StringProperty getDevice() {
		return device;
	}

	public StringProperty getType() {
		return type;
	}

	public StringProperty getRefclk_source() {
		return refclk_source;
	}

	public StringProperty getPps_source() {
		return pps_source;
	}

	public StringProperty getBehaviour_refclk_lock_lost() {
		return behaviour_refclk_lock_lost;
	}

	public DoubleProperty getTxgain() {
		return txgain;
	}
	
	public Double getMaxTxGain() {
		return maxTxGain;
	}

	public IntegerProperty getFrequency() {
		return frequency;
	}

	public IntegerProperty getMax_gps_holdover_time() {
		return max_gps_holdover_time;
	}
	
	public ObservableList<String> getSupportedDeviceList() {
		return supportedDeviceList;
	}

	public ObservableList<String> getRefclk_sourceList() {
		return refclk_sourceList;
	}

	public ObservableList<String> getPps_sourceList() {
		return pps_sourceList;
	}

	public ObservableList<String> getBehaviour_refclk_lock_lostList() {
		return behaviour_refclk_lock_lostList;
	}

	public BooleanProperty getSynchronous() {
		return synchronous;
	}

	public BooleanProperty getMutenotimestamps() {
		return mutenotimestamps;
	}

	public StringProperty getOffset() {
		return offset;
	}
	
}
