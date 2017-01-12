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


public abstract class Modulator {
		
	// Remote Control & Log
	protected BooleanProperty telnet; 
	protected StringProperty telnetport;
	protected BooleanProperty zmqctrl;
	protected StringProperty zmqctrlendpoint;	
	protected BooleanProperty syslog, filelog; 
	protected StringProperty filename;
	
	// Input
	protected StringProperty transport, source, max_frames_queued;
	
	// Modulator	
	protected ObservableList<String> gainModeList;
	protected StringProperty gainmode;
	protected DoubleProperty digital_gain;
	protected StringProperty rate;
	protected IntegerProperty dac_clk_rate;
		
	// FIR
	protected BooleanProperty enabled;
	protected StringProperty filtertapsfile;

	// Modulator Output
	protected StringProperty output;
	
	
	public Modulator(String outputType) {
		
		// Remote Control & Log
		telnet = 			new SimpleBooleanProperty(false);
		telnetport = 		new SimpleStringProperty("0");
		
		zmqctrl = 			new SimpleBooleanProperty(false);
		zmqctrlendpoint = 	new SimpleStringProperty("0");	// only Port, fix later in save/load-Class
		
		syslog = 			new SimpleBooleanProperty(false);
		filelog = 			new SimpleBooleanProperty(false);
		filename =			new SimpleStringProperty("/dev/stderr");
		
		// fix. Input: ZeroMQ
		transport =			new SimpleStringProperty("zeromq");	
		source = 			new SimpleStringProperty("");	// source set up from ETIZeromq-Port
		max_frames_queued =	new SimpleStringProperty("100");
		
		// Modulator
		gainModeList = 		FXCollections.observableArrayList("0","1","2");
		gainmode = 			new SimpleStringProperty("2");	// ETSI 300 798	
		digital_gain = 		new SimpleDoubleProperty(0.8);	
		rate =				new SimpleStringProperty("2048000");	
		dac_clk_rate = 		new SimpleIntegerProperty(0);	
		enabled = 			new SimpleBooleanProperty(false);
		filtertapsfile =	new SimpleStringProperty("");
		
		// Output
		this.output =		new SimpleStringProperty(outputType);
	}



	public BooleanProperty getTelnet() {
		return telnet;
	}

	public BooleanProperty getZmqctrl() {
		return zmqctrl;
	}

	public BooleanProperty getSyslog() {
		return syslog;
	}

	public BooleanProperty getFilelog() {
		return filelog;
	}

	public StringProperty getTelnetport() {
		return telnetport;
	}

	public StringProperty getZmqctrlendpoint() {
		return zmqctrlendpoint;
	}

	public StringProperty getFilename() {
		return filename;
	}

	public StringProperty getTransport() {
		return transport;
	}

	public StringProperty getSource() {
		return source;
	}

	public StringProperty getMax_frames_queued() {
		return max_frames_queued;
	}

	public ObservableList<String> getGainModeList() {
		return gainModeList;
	}

	public StringProperty getRate() {
		return rate;
	}

	public IntegerProperty getDac_clk_rate() {
		return dac_clk_rate;
	}

	public DoubleProperty getDigital_gain() {
		return digital_gain;
	}

	public StringProperty getGainmode() {
		return gainmode;
	}

	public StringProperty getFiltertapsfile() {
		return filtertapsfile;
	}

	public BooleanProperty getEnabled() {
		return enabled;
	}

	public StringProperty getOutput() {
		return output;
	}
}
